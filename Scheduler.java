package vishnu.Indukuri.TextLater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

/*
 * This class is the call scheduler activity.  It is going to be where people schedule their calls ef
 */
public class Scheduler extends Activity implements MediaPlayer.OnCompletionListener, Runnable{
	EditText message ;
    boolean one  = true ;
	EditText phoneNumber ;
	TimePicker TP ;
	DatePicker DP ;
	EditText ET;
	String  PHONE_NUMBER  ;
	
	FileOutputStream fos ;
	
	MediaRecorder recorder = new MediaRecorder() ;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState) ;
		setContentView(R.layout.scheduler) ;
	    
	    TP = (TimePicker) findViewById(R.id.TimePicker01) ;
	    phoneNumber = (EditText) findViewById(R.id.EditText01) ;
	    DP = (DatePicker) findViewById(R.id.DatePicker01) ;
	    ET = (EditText) findViewById(R.id.EditText02) ;
	    
	    PHONE_NUMBER = ((TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	    
	}
	
	// Sending data to server.  WIll finish this later.
	//--------------------------------
	public void schedule(View view) { 
          Thread thread = new Thread(this) ;
          thread.start() ;
	}
	
	public byte[] getBytes(int[] array){
		ByteBuffer byteBuffer = ByteBuffer.allocate(array.length * 4);        
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(array);
        return byteBuffer.array() ;
	}
	  /////////////////////
	 //// DATBASE OPERATIONS NO TOUCHING!!!!
	 /////////////////////////
	
	
	public void contacts(View view){
		Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
		startActivityForResult(intent, 100) ;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 100){ // check that i am in fact the one who called the activity picker 
			loadContactInfo(data.getData()) ;
		}
		if(requestCode == 9){
			 ET.setText(data.getStringExtra("FileName")) ;
		}
	}
	
	 private void loadContactInfo(Uri contactUri) {
		AsyncTask<Uri, Void,String> task = new AsyncTask<Uri, Void, String>() { // a new thread for database operations
        @Override
        protected String doInBackground(Uri... uris) {
            return loadContact(getContentResolver(),uris[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            phoneNumber.setText(result) ;
        }};
		task.execute(contactUri);
	 }
	 
	 public String loadContact(ContentResolver contentResolver, Uri contactUri) {
		 long contactId = -1 ;
		 Cursor cursor = contentResolver.query(contactUri,new String[]{Contacts._ID, Contacts.DISPLAY_NAME}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                contactId = cursor.getLong(0);
            }
        } finally {
            cursor.close();
        }
		 cursor = contentResolver.query(Phone.CONTENT_URI,new String[]{Phone.NUMBER},Phone.CONTACT_ID + "=" + contactId, null, Phone.IS_SUPER_PRIMARY + " DESC");
		 String[] number = new String[10] ;
		 try {
	        if (cursor.moveToFirst()) {
	        	number = cursor.getString(0).split("-") ;
	        	for(int i=1; number[i] != null; i++){
	        		 Log.d("THL",""+i) ;
	        		 number[0] += number[i] ;
	        	} // getting rid of any "-" in the phone number 
	        }
	        } catch(Exception e){} finally {
	            cursor.close();
	        }
         return number[0];
	 }
	 
	 
	 ///////////////////////////////
	 /// END OF DATABASE OPERATIONS
	 ////////////////////////////////	
	 
	 //////////////////////////////
	 /// RECORDING STUFF
	 ////////////////////////////////
	 public void record(View view){
		 if(ET.getText().toString() == "File name" || ET.getText().toString() == ""){ Toast.makeText(this, "Please pick a file name", 200) ; return ; }
		 String[] array = fileList() ;
		 for(int i=0; i < array.length;i++){
			 if(ET.getText().toString() == array[i]){
				 Toast.makeText(this, "This file name already exists.  Press again to replace.", 200) ; 
				 if(one){ one = false ; return ; }
				 else one = true ;
			 }
		 } // make sure ET has a valid  value
		 if(one)  showDialog(0) ;
	 }
	 
	 @Override
	 protected Dialog onCreateDialog(int id){
		 if(id == 0){
			try {	 fos = openFileOutput(ET.getText()+".amr", Context.MODE_PRIVATE);} catch (FileNotFoundException e1) {	e1.printStackTrace();}
			 recorder.setAudioSource(MediaRecorder.AudioSource.MIC) ;
			 recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			 recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)   ;
			 try {recorder.setOutputFile(     
					 fos.getFD()  ); 
			 recorder.prepare() ;} catch (IllegalStateException e)
			 {e.printStackTrace();} catch (IOException e) 
			 {e.printStackTrace();}
			 ProgressDialog dialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER) ;
			 dialog.setTitle("Recording... ") ;
			 dialog.setCancelable(false) ;
			 recorder.start() ;
			 dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Stop Recording", new DialogInterface.OnClickListener(){
				 	@Override
				 	public void onClick(DialogInterface dialog, int which) {
				 		recorder.stop() ;
				 		recorder.reset() ;
				 		dialog.dismiss() ;
				 	}
			 }) ;
			 return dialog ;
		 }
		 if(id == 1){
			 ProgressDialog dialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER) ;
			 dialog.setTitle("Playing...") ;
			 dialog.setCancelable(false) ;
			 return dialog ;
		 }
		 return super.onCreateDialog(id) ;
	 }
	 
	 // playing the file back to the user
	 //----------------------------------
	 public void play(View view) {
		 String path = getFilesDir().getAbsolutePath()+"/"+ET.getText()+".amr" ;
		 MediaPlayer player = new MediaPlayer() ;
		 try{	player.setDataSource(new FileInputStream(new File(path)).getFD()) ;
			 player.setOnCompletionListener(this) ;
			 player.prepare() ;
		 } catch(Exception e){e.printStackTrace() ;}
		 showDialog(1) ;
		 player.start() ; 
	 }

	 // once the file is finished playing... it is time to stop
	 //-------------------
	@Override
	public void onCompletion(MediaPlayer player) {
		player.stop() ;
		player.release() ;
		dismissDialog(1) ; 
	}
	
	// picking a recording
	//---------------------------
	public void recordingPicker(View view){
		Intent intent = new Intent( Scheduler.this, Recordings.class) ;
		startActivityForResult(intent, 9) ;
	}
	
	//////////////////////////
	/// End recording/Playing stuff
	/////////////////////////
	
	// LifeCycle methods
	//-------------------------
	@Override
	public void onStop(){
		super.onStop() ;
		recorder.release() ;
	}

	@Override
	public void run() {
		Call call = new Call(TP.getCurrentMinute(), TP.getCurrentHour(), DP.getDayOfMonth(), DP.getMonth(), DP.getYear()) ;
	    call.setNumber(phoneNumber.getText().toString(), ((TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId()) ;
    	call.setFile(new File(getFilesDir().getAbsolutePath()+"/"+ET.getText()+".amr")) ;
	    Serializer serializer = new Persister();
    	File file = new File("example.xml");
		try{
      	  serializer.write(call, file);
      	  Socket socket = new Socket("127.0.0.1", 8888) ;
          BufferedInputStream in = new BufferedInputStream(new FileInputStream(file)) ;
          BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream()) ;
          byte[] bytes = null ;
          in.read(bytes, 0,(int) file.length()) ;
          in.close() ;
          out.write(bytes) ;
          out.flush() ;
          out.close() ;
          socket.close() ;
        }catch (Exception e) {e.printStackTrace();}  // because serializer throws out a generic exception...
	}
}