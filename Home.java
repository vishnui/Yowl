package vishnu.Indukuri.TextLater;

import greendroid.app.GDActivity;
import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBar.Type;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;
import greendroid.widget.PagedView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


public class Home extends GDActivity implements Runnable{
	
    public static int TOKENS  ; // NEED TO KEEP THIS SAFE. MAKE SURE NOBODY CAN MESS WITH THIS. 
    public static String packageName ;
    static Context context ;
    Intent inte ;                                                                                    // Intent for Billing Service
    PendingIntent text ;
    PagedView pagedView ;
    EditText message ;
    boolean one  = true ;
	EditText phoneNumber ;
	TimePicker TP ;
	DatePicker DP ;
	String x ;          
	ActionBar mActionBar ;
	AlarmManager manager ;
//	ArrayList<Call> calls ;
//	ProgressDialog m_ProgressDialog ;
//	ListView currentData ;
    
    @Override
    public void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState) ;
    	requestWindowFeature(Window.FEATURE_NO_TITLE) ;     // Just looks nicer
    	
    	setActionBarContentView(R.layout.scheduler) ;
    	
       context = this ;
  	   packageName = getPackageName() ;
  	   
  		TP = (TimePicker) findViewById(R.id.TimePicker01) ;
 	   phoneNumber = (EditText) findViewById(R.id.EditText01) ;
 	   DP = (DatePicker) findViewById(R.id.DatePicker01) ;
 	   message = (EditText) findViewById(R.id.message) ;
  	  
  	   // setting up UI
  	   SharedPreferences settings = getSharedPreferences("TOKENS", 0);
       TOKENS = settings.getInt("tokens", -5);
       if(TOKENS == -5){                                                                                                // the first time the user uses the application, EULA shows up
    	   showDialog(0) ;
    	   TOKENS = 3 ;
      }
      // Setting up Scheduled
// 	  calls = new ArrayList<Call>() ;
      
	  // Setting up the PagedView Home page
	  mActionBar = getActionBar() ;
	  mActionBar.setType(Type.Empty) ;
	  mActionBar.setTitle("Your Tokens: "+TOKENS) ;
	  mActionBar.setBackgroundColor(0x00000000) ;
	  addActionBarItem(mActionBar.newActionBarItem(NormalActionBarItem.class).setDrawable(new ActionBarDrawable(this,R.drawable.cart))) ;
	  
//	  pagedView = (PagedView) findViewById(R.id.paged_view);
//      pagedView.setOnPageChangeListener(mOnPagedViewChangedListener);
//      pagedView.setAdapter(new PhotoSwipeAdapter());
//      
//      pagedView.setVerticalFadingEdgeEnabled(true) ;
      
    }
    
	@Override
	public void onResume() {
	  super.onResume();
	  inte = new Intent(this, BillingService.class) ;
  	  startService(inte) ;
	}
	
	@Override
	public void onPause(){
		super.onPause() ;
		SharedPreferences settings = getSharedPreferences("TOKENS", 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putInt("tokens", TOKENS);
        editor.commit();
	}
	
	@Override
	public void onStop(){
		super.onStop() ;
		stopService(inte) ;
	}
	
	@Override
	protected Dialog onCreateDialog(int i){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Agreement").setCancelable(false).setNeutralButton("Ok", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismissDialog(0) ;
			}
		}).setMessage("By using this application, you agree to the following: I am not responsible for any indirect, special, " +
				"incidental, or consequential damages of any character " +
				"including, without limitation to, damages for loss of goodwill, work stoppage, computer failure or malfunction, " +
				"or any and all other commercial damages or losses that your use of this application may cause.  This app is for " +
				"recreational and entertainment use.  Do not " +
				"depend upon it to perform any life saving tasks.  The user is solely responsible for his or her use of this application." +
				"this agreement is in addition to any other agreements the user may have agreed to while using the android market, " +
				"not in replacement.  All sales are final. No refunds.") ;
		return builder.create() ;
	}	
	
	public static void showText(){
		Toast.makeText(context, "Tokens delivered. Refresh to see them.", 200).show() ;
	}
	
	// onClick methods
	//-------------------------
	
	@Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
			try {
				Bundle resp = BillingService.purchase() ;
				if((resp.getInt("RESPONSE_CODE")) != 0){
					Toast.makeText(Home.this, "Cannot reach Android Market.  Sure you have the latest version?", 200).show() ;
					return true;
				}
				 ((PendingIntent) resp.get("PURCHASE_INTENT" )).send()  ;
			} catch (RemoteException e) { e.printStackTrace() ;
				Toast.makeText(Home.this, "I'm sorry but the purchase could not be started at this time.", 200).show() ;
			} catch (CanceledException e) { e.printStackTrace() ; Toast.makeText(Home.this, "Fatal Error", 200).show() ; // should never happen. 
			} 
		return true ;
	}
	
//	// PagedView 
//	//-----------------------------
//	private OnPagedViewChangeListener mOnPagedViewChangedListener = new OnPagedViewChangeListener() {
//        @Override
//        public void onStopTracking(PagedView pagedView) {        }
//        @Override
//        public void onStartTracking(PagedView pagedView) {        }
//        @Override
//        public void onPageChanged(PagedView pagedView, int previousPage, int newPage) {        }
//    };
//    
//    private class PhotoSwipeAdapter extends PagedAdapter {
//        @Override
//        public int getCount() {
//            return 2;
//        }
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//        
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (position == 0) {
//        		Toast.makeText(context, "Phone numbers must only have numbers. No spaces, dashes etc.", 500).show() ;
//                return getLayoutInflater().inflate(R.layout.scheduler, parent, false);
//            }
//            else if(position == 1){
//            	m_ProgressDialog = ProgressDialog.show(Home.this,  "Please wait...", "Retrieving data ...", true);
//            	calls = new ArrayList<Call>() ;
//
//            	Thread mThread = new Thread(new DataCheckThread());
//            	mThread.start();
//            	
//            	return getLayoutInflater().inflate(R.layout.scheduled, parent, false) ;
//    		}
//            return convertView;
//        }
//    }
    
    // Scheduler stuff
    //---------------------
	public void scheduleCall(View view)  { 
		if(checkForMistakes()){
			Toast.makeText(this, "Connecting to server...", 1000).show() ;
			Thread thread = new Thread(this) ;
			thread.start();
		}
		else{
			Toast.makeText(this, x, 200).show() ;
		}
	}
	
	public void scheduleText(View view){
		if(checkForTMistakes()){
			Intent textintent = new Intent(this, BillingReceiver.class) ;
			textintent.putExtra("vishnu.Indukuri.TextLater.message", message.getText().toString()) ;
			textintent.putExtra("vishnu.Indukuri.TextLater.number", phoneNumber.getText().toString()) ;
			textintent.putExtra("vishnu.Indukuri.TextLater.secureNo", 133459874602763999L) ;
			text = PendingIntent.getBroadcast(this, 001, textintent, 0) ;
			
			manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE) ;
			manager.set(AlarmManager.RTC_WAKEUP, new GregorianCalendar(DP.getYear(),DP.getMonth(),DP.getDayOfMonth(),
					TP.getCurrentHour(),TP.getCurrentMinute()).getTimeInMillis(), text);
			
			Toast.makeText(context, "Message Scheduled", 200).show() ;
		}
		else{
			Toast.makeText(this, x, 200).show() ;
		}
	}
	
	private boolean checkForMistakes() {
		if(Home.TOKENS <= 0){
			x = "Not Enough tokens" ;
			return false ;
		}
		if(message.length() > 500 || message.length() < 1){
			x = "Message must not empty or longer than 500 characters" ;
			return false ;
		}
		return checkForTMistakes() ;
	}
	
	private boolean checkForTMistakes(){
		GregorianCalendar cal = new GregorianCalendar() ;
		
		if(DP.getMonth() != cal.get(Calendar.MONTH)){
			x = "Call must be this month" ;
			return false ;
		}
		if(DP.getYear() != cal.get(Calendar.YEAR)) {
			x = "Call must be this year" ;
			return false ;
		}
		return true ;
	}
	
	  ////////////////////////
	 //// DATBASE OPERATIONS NO TOUCHING!!!!
	 /////////////////////////
	public void contacts(View view){
		Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
		startActivityForResult(intent, 100) ;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 100 && data != null){ // check that i am in fact the one who called the activity picker 
			loadContactInfo(data.getData()) ;
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
		 Cursor cursor = contentResolver.query(contactUri,new String[]{BaseColumns._ID, Contacts.DISPLAY_NAME}, null, null, null);
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
	
	// Contact server and schedule call
	//-------------------------
	 public void run()  {
		 
			String id = ((TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId() ;
			Call call = new Call() ;
			Serializer serializer = new Persister();
			
			call.setDate(new GregorianCalendar(DP.getYear(),DP.getMonth(),DP.getDayOfMonth(),TP.getCurrentHour(),TP.getCurrentMinute()).getTimeInMillis()) ;
		    call.setNumber(phoneNumber.getText().toString(), id) ;
	    	call.setActId(id+call.getDate()+call.getNumber()) ;
	    	
	    	
	    	String tel = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number() ;  
	    	Log.e("Yowl",tel);
    		char[] array = tel.toCharArray() ;
    		String arrayspaces = "" ;
    		for(int i=0;i <  array.length;i++){
    			arrayspaces += array[i] ;
    			arrayspaces += " " ;
    		}
    		call.setRecording("Yowl brings you this message  from     "+arrayspaces+", , , , , , , , , , , , , , , , , , , ,"+message.getText().toString()) ; 

		    File xml = new File(getFilesDir().getAbsoluteFile()+"/example.xml");
	    	try {
	    		serializer.write(call, xml);	
	    	} catch (Exception e) {e.printStackTrace();
	    	}
			try{
	      	  HttpClient httpclient = new DefaultHttpClient();
	      	  HttpPost httppost = new HttpPost("http://vishnui.appspot.com/texthimlater");
	      	  FileEntity fe = new FileEntity(xml, "text/xml") ;
	      	  httppost.setEntity(fe);
	      	  
	      	  x = "Talking to server..." ;
	     	  mHandler.sendEmptyMessage(0) ;
	     	  
	      	  HttpResponse response = httpclient.execute(httppost);
	      	  HttpEntity ent = response.getEntity() ;
	      	  BufferedReader br = new BufferedReader(new InputStreamReader(ent.getContent())) ;
	      	  
	      	   x = br.readLine() ;
	      	  mHandler.sendEmptyMessage(0) ;
	      	  
	      	  br.close() ;
	      	  if(x.equals("SUCCESS")){ TOKENS--  ;  }
	        }catch (SocketException e) {e.printStackTrace();  x ="Network error.  Please, try again." ; mHandler.sendEmptyMessage(0) ;
	        }catch(IOException e) { e.printStackTrace() ; x ="Malformed HTTP response.  Please, try again." ; mHandler.sendEmptyMessage(0) ;
	        }catch(Exception e){Log.e("YOWL",e.toString()) ;}
		}
	
	// A handler to give messages to the UI thread
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			Toast.makeText(context, x, 200).show() ;
			mActionBar.setTitle("Your Tokens: "+TOKENS) ;
		}
	} ;
	
//	// Setting Scheduled
//	//-------------------------
//	private class DataCheckThread implements Runnable{
//		@Override
//		public void run() {
//			try{
//				HttpClient httpclient = new DefaultHttpClient();
//				HttpPost httpost = new HttpPost("http://vishnui.appspot.com/datacheck") ;
//				StringEntity se = new StringEntity(((TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId()) ;
//				httpost.setEntity(se) ;
//				HttpResponse response = httpclient.execute(httpost);
//				HttpEntity ent = response.getEntity() ;
//				BufferedReader br = new BufferedReader(new InputStreamReader(ent.getContent())) ;
//				String x ;
//				while((x = br.readLine()) != null){
//					Call call = new Call() ;
//					StringTokenizer st = new StringTokenizer(x) ;
//					String overQ = st.nextToken() ;
//					if(overQ.equals("Overquota"))  break ;
//					call.setDate(Long.parseLong(overQ)) ;
//					call.setRecording(st.nextToken()) ;
//					call.setStatusCode(st.nextToken()) ;
//					call.setNumber(st.nextToken()) ;
//					try{
//						call.setNumber(st.nextToken()) ;
//					} catch (NoSuchElementException e) {x = br. readLine() ;  call.setNumber(st.nextToken()) ;}
//					calls.add(call) ;
//				}
//				br.close() ;
//	 	    }catch(SocketException e) {e.printStackTrace() ; x = "Cannot connect to the internet" ; showText() ; 
//			} catch (UnsupportedEncodingException e) {e.printStackTrace();  
//			} catch(ConnectTimeoutException e){ e.printStackTrace() ; x = "Connection timed out.  Please try again" ; showText() ; 
//			} catch(IOException e){e.printStackTrace() ; 
//			}
////			runOnUiThread(runs);
//		}
//    }
}