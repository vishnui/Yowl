package vishnu.Indukuri.TextLater;

import java.io.File;
import java.io.FileInputStream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Recordings extends ListActivity implements MediaPlayer.OnCompletionListener {
	
		String Title = "" ;
		String[] OPTIONS = fileList() ;
		MediaPlayer player ;
		
		@Override
		public void onCreate(Bundle icicle){
			super.onCreate(icicle) ;
			setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item_1, OPTIONS));
			ListView lv = getListView();
			lv.setTextFilterEnabled(true);
		    lv.setBackgroundColor(Color.WHITE) ;
			lv.setDividerHeight(10) ;

			lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) { 
					TextView tv = (TextView) view ;
					Title = (String) tv.getText() ;
					showDialog(0) ;
			    }
			 });
			
			
		}
 		
		@Override
		public Dialog onCreateDialog(int id){
			if(id == 0){
				AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
				builder.setTitle(Title+".amr") ;
				// Postive Button
				//----------------
				builder.setPositiveButton("Play", new DialogInterface.OnClickListener(){				
					@Override
					public void onClick (DialogInterface dialog, int which){
						if(player == null)player = new MediaPlayer() ;  else player.stop() ;
						String path = getFilesDir().getAbsolutePath()+"/"+Title+".amr" ;
						 try{	player.setDataSource(new FileInputStream(new File(path)).getFD()) ;
						 player.setOnCompletionListener(Recordings.this) ;
						 player.prepare() ;
					 } catch(Exception e){e.printStackTrace() ;}
					 player.start() ;
					}
				}) ;			
				// Negative button
				//------------------
				builder.setNegativeButton("Choose", new DialogInterface.OnClickListener() {			
					@Override
					public void onClick(DialogInterface dialog, int which){
						if(player != null){
							player.stop() ;
							player.release() ;
						}
						Intent intent = new Intent() ;
						intent.putExtra("FileName", Title) ;
						setResult(0,intent ) ;
						finish() ;
					}			
				}) ;
				return builder.create() ;
			}
			return super.onCreateDialog(id) ;
		}

		@Override
		public void onCompletion(MediaPlayer mp) {
			player.stop() ;
			player.release();
		}
}
