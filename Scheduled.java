package vishnu.Indukuri.TextLater;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Scheduled extends ListActivity {
	public void onCreate(Bundle savedInstanceState){
//		MediaPlayer player ;
		
		super.onCreate(savedInstanceState) ;
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item_1, OPTIONS));

		  ListView lv = getListView();
		  lv.setTextFilterEnabled(true);
		  lv.setBackgroundColor(Color.WHITE) ;
		  lv.setDividerHeight(10) ;

		  lv.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) { 
//		    		TextView tv = (TextView) view ;
		    	}
		    });
		}
	String[] OPTIONS = fileList() ;
}
