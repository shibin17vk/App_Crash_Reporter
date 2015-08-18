package com.blog4android.crashreporter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.text.format.Time;
import android.util.Log;

public class Utilities {

	public static class INTENT_KEY{
		
		public static final String EXCEPTION="exception";
		public static final String EXCEPTION_TIME="time";
		public static final String SRC="src";
		public static final int SRC_CRASH_REPORTER=1;
		public static final int SRC_RECEIVER=2;
		  
	}
	
	
	public static String getDeviceManufacture(){
		return  android.os.Build.MANUFACTURER;
	}
	
	public static String getDeviceModelName(){
		return android.os.Build.MODEL;
	}
	
	public static String getCurrentDeviceDateTime(){

		Time todaytoday = new Time(Time.getCurrentTimezone());;
		todaytoday.setToNow();
		String date=todaytoday.year+"-"+(todaytoday.month+1)+"-"+todaytoday.monthDay+" "+todaytoday.hour+":"+todaytoday.minute;		
		return date;
	}
	
	// Checking for all possible internet providers
	public static boolean isConnectingToInternet(Context context) {

		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}
	
	public static String getVersionCodeVersionName(Context context) throws NameNotFoundException{
		
		PackageManager manager = context.getPackageManager();
		PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
		StringBuilder details=new StringBuilder();
		details.append("package: "+info.packageName);
		details.append("\nversion code: "+ info.versionCode);
		details.append("\nversion name:"+ info.versionName);
		details.append("\nRepuesting permissions"+ info.permissions);
		
		return details.toString();
		
	}
	
	// to get the current battery percentage!
	public static float getBatteryLevel(Context context) {
		
		    Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		    int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		    int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    if(level == -1 || scale == -1) {
		        return 50.0f;
		    }
            Log.i("MESSAGE", "Chcked battery percentage:"+((float)level / (float)scale) * 100.0f);
		    return ((float)level / (float)scale) * 100.0f;
            
	}
	
	
	// to check battery is in charging state!
	public static boolean isChargingState(Context context){
		
		Intent batteryIntent =context .registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                            status == BatteryManager.BATTERY_STATUS_FULL;       
        return isCharging;       
	}
	
	

	public static String getStringFromStackTrace(Throwable ex) {
		if (ex == null) {
			return "Null Excepection...!";
		}
		StringWriter str = new StringWriter();
		PrintWriter writer = new PrintWriter(str);
		try {
			ex.printStackTrace(writer);
			return str.getBuffer().toString();
		} finally {
			try {
				str.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
