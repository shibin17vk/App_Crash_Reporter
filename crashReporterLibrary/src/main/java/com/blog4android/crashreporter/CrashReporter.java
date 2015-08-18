package com.blog4android.crashreporter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CrashReporter {

	private Context context;
	private UncaughtExceptionHandler defaultUEH; 
	 
	public CrashReporter(Context context){
		this.context=context;
	}
	
	public void reportCrash(Thread thread){ 
		
		this.defaultUEH = thread.getDefaultUncaughtExceptionHandler(); 
		thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);
		
	}
	
	public void setGoogleFormUrl(String formUrl){
		Persistent.setGoogleFormUrl(context, formUrl);
	}
	
	public void setGoogleFormColoumnEntries(String[] entries){
		Persistent.setFormColoumnEntities(context, entries);
	}
	
	private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
		
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			 
			Intent intent = new Intent(context, ServiceReportCrash.class);
			intent.putExtra(Utilities.INTENT_KEY.EXCEPTION,ex);
			intent.putExtra(Utilities.INTENT_KEY.EXCEPTION_TIME,Utilities.getCurrentDeviceDateTime());
			intent.putExtra(Utilities.INTENT_KEY.SRC,Utilities.INTENT_KEY.SRC_CRASH_REPORTER);			
			PendingIntent pintent = PendingIntent.getService(context,0, intent, 0);
			AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);		
			alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5000, pintent);		
			
			defaultUEH.uncaughtException(thread, ex);
			 
		}
	};

}
