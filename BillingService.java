package vishnu.Indukuri.TextLater;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.vending.billing.IMarketBillingService;

public class BillingService extends Service implements ServiceConnection {
	
	static IMarketBillingService biller ;
	Context mContext ;
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId){
		super.onCreate() ;
		try {
			   getApplicationContext().bindService( 
					  new Intent("com.android.vending.billing.MarketBillingService.BIND"), 
					  this,  Context.BIND_AUTO_CREATE);
			} catch (SecurityException e) { e.printStackTrace() ;
			}
			return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	} 

	@Override
	public void onServiceConnected(ComponentName CN, IBinder service) {
		biller = IMarketBillingService.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) { 
		
	}
	
	public static Bundle purchase() throws RemoteException{
		Bundle bundle = new Bundle() ;
		bundle.putString("BILLING_REQUEST", "REQUEST_PURCHASE"); 
		bundle.putInt("API_VERSION", 1) ;
		bundle.putString("PACKAGE_NAME", Home.packageName) ;
		bundle.putString("ITEM_ID", "15tokens") ;
		return biller.sendBillingRequest(bundle) ;
	}
}
