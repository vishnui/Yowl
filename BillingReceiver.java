package vishnu.Indukuri.TextLater;

import java.security.SecureRandom;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.telephony.SmsManager;

public class BillingReceiver extends BroadcastReceiver {

	Bundle bundle = new Bundle() ;
	SmsManager sm ;
	ArrayList<String> messageparts ;
	NotificationManager nm ;
	String lnumber ;
	
	@Override
	public void onReceive(Context con, Intent intent) {
		if(intent.getLongExtra("vishnu.Indukuri.TextLater.secureNo", 0) == 133459874602763999L){
			sm = SmsManager.getDefault() ;
			messageparts = sm.divideMessage(intent.getStringExtra("vishnu.Indukuri.TextLater.message")) ;
			ArrayList<PendingIntent>  pi = new ArrayList<PendingIntent>() ;
			Intent ill = new Intent(con, BillingReceiver.class);
			ill.putExtra("vishnu.Indukuri.TextLater.random", 1554) ;
			pi.add(PendingIntent.getBroadcast(con,001, ill, 0)) ;
			lnumber = intent.getStringExtra("vishnu.Indukuri.TextLater.number") ;
			sm.sendMultipartTextMessage(lnumber, null, messageparts, null, pi) ;
		}
		else if(intent.getDoubleExtra("vishnu.Indukuri.TextLater.random", 0) == 1554){
			nm = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE) ;
			
			Notification not = new Notification() ;
			not.setLatestEventInfo(con, "Message delivered", "Your text message to "+lnumber+" has been delivered", 
					PendingIntent.getActivity(con, 001, new Intent(con, Home.class), 0));
			
			nm.notify(10990, not) ;
		}
		else if(intent.getAction().equals("com.android.vending.billing.IN_APP_NOTIFY")){
			bundle.putString("BILLING_REQUEST", "GET_PURCHASE_INFORMATION");
			bundle.putInt("API_VERSION", 1) ;
			bundle.putLong("NONCE", new SecureRandom().nextLong()) ;
			bundle.putString("PACKAGE_NAME", Home.packageName) ;
		    bundle.putStringArray("NOTIFY_IDS", new String[] { intent.getStringExtra("notification_id")});
			try{
				BillingService.biller.sendBillingRequest(bundle) ;
			} catch(Exception e) {e.printStackTrace() ;} // should never occur
		}
		else if(intent.getAction().equals("com.android.vending.billing.PURCHASE_STATE_CHANGED")){
			bundle.putString("BILLING_REQUEST", "CONFIRM_NOTIFICATIONS");
			bundle.putInt("API_VERSION", 1) ;
			bundle.putString("PACKAGE_NAME", Home.packageName) ;
			try{
				 BillingService.biller.sendBillingRequest(bundle) ;
			} catch(RemoteException e) {e.printStackTrace() ;} // should never occur
			
			try {
				JSONObject data = new JSONObject(intent.getStringExtra("inapp_signed_data")) ;
				JSONArray array = data.getJSONArray("orders") ;
				if(array.getJSONObject(0).getInt("purchaseState") == 0){
					Home.TOKENS += 15 ;
					Home.showText() ;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
