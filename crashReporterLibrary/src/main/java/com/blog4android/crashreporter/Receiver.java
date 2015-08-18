package com.blog4android.crashreporter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Receiver extends BroadcastReceiver {

	private ReceiverSuport receiveSuport;
	
	@Override
	public void onReceive(Context context, Intent intent) {
	   			
		Intent dataIntent=new Intent(context, ServiceReportCrash.class);
		dataIntent.putExtra(Utilities.INTENT_KEY.SRC,Utilities.INTENT_KEY.SRC_RECEIVER);			
		context.startService(dataIntent);
				
	}

	private class ReceiverSuport extends AsyncTask{

		private Context context;
		
		public ReceiverSuport(Context context){
			this.context=context;
		}
		
		@Override
		protected Object doInBackground(Object... params) {
			
			int pendingRepotrs=Persistent.getPendinRepotrsCount(context);
			 
			if(pendingRepotrs>0){
			
				Intent dataIntent=new Intent(context, ServiceReportCrash.class);
				dataIntent.putExtra(Utilities.INTENT_KEY.SRC,Utilities.INTENT_KEY.SRC_RECEIVER);			
				context.startService(dataIntent);
				
			}
			return null;
		}
		
		
	}
	
}
