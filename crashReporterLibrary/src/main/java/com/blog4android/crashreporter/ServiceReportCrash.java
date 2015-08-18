package com.blog4android.crashreporter;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class ServiceReportCrash extends Service {
 
	private int sheduledThreadCount;
	
	@Override
	public void onCreate() {	
		super.onCreate();				
	}
	 
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {		
		super.onStartCommand(intent, flags, startId);		
		
		Bundle bundle=intent.getExtras();
		
		if(Persistent.getGoogleFormURL(this).equals(Persistent.INVALID_STRING) || Persistent.getColoumnEntities(this)==null){
			
			Log.e("Crash Reporter...","Google form url or coloumn fields are missing...!");
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
			
		}
		
		
		int source=bundle.getInt(Utilities.INTENT_KEY.SRC);
		 			
		if(Utilities.getBatteryLevel(this)<=15 && Utilities.isChargingState(this)){		
					
			if(source==Utilities.INTENT_KEY.SRC_CRASH_REPORTER){
				
				Throwable exception=(Throwable) bundle.get(Utilities.INTENT_KEY.EXCEPTION);				
				String exceptionString=Utilities.getStringFromStackTrace(exception);
				long exceptionId=System.currentTimeMillis();
				String exceptionDateTime=Utilities.getCurrentDeviceDateTime();
				boolean isInternetAvailable=Utilities.isConnectingToInternet(this);				
				
				Persistent.setException(this, exceptionId, exceptionString, exceptionDateTime);				
			}
			Log.e("Crash Reporter...","Battery is in critical now, can not be proccess");
			stopSelf();
		}
		else{
			
			if(source==Utilities.INTENT_KEY.SRC_CRASH_REPORTER){
				
				Throwable exception=(Throwable) bundle.get(Utilities.INTENT_KEY.EXCEPTION);				
				String exceptionString=Utilities.getStringFromStackTrace(exception);
				String exceptionTime=bundle.getString(Utilities.INTENT_KEY.EXCEPTION_TIME);
				
				SendToGoogleDrive threadSendToGoogleDrive=new SendToGoogleDrive(source,exceptionString, exceptionTime); 	
				threadSendToGoogleDrive.start();
				
			}
			else if(source==Utilities.INTENT_KEY.SRC_RECEIVER){
				
				ExecutorService executor = Executors.newFixedThreadPool(1);
				String[] reportPendingIds=Persistent.getAllExceptionIds(ServiceReportCrash.this);
				sheduledThreadCount=reportPendingIds.length;
				
				for(int count=0; count<sheduledThreadCount; count++){
					
					long exceptionId=Long.parseLong(reportPendingIds[count]);
					SendToGoogleDrive thread=new SendToGoogleDrive(Utilities.INTENT_KEY.SRC_RECEIVER, exceptionId);
					thread.setName(String.valueOf(count));
					thread.setPriority(Thread.MAX_PRIORITY); 
					executor.execute(thread);					
					
				}
				
			}
			
		} 
		
		return super.onStartCommand(intent, flags, startId);					
	}

	@Override
	public IBinder onBind(Intent intent) {		
		return null;
	}
	

	private class SendToGoogleDrive extends Thread{
		
		private String exception;
		private long exceptionID;
		private String exceptionTime;
		private int source;
		
		public SendToGoogleDrive(int source, String exception, String exceptionTime){
			
			this.exception=exception;
			this.exceptionTime=exceptionTime;
			this.source=source;
			
		}
		
		public SendToGoogleDrive(int source,long exceptionID){
			
			this.exceptionID=exceptionID;
			this.exception=Persistent.getException(ServiceReportCrash.this, exceptionID);
			this.exceptionTime=Persistent.getExceptionTime(ServiceReportCrash.this,exceptionID);
			this.source=source;
			
		}
		
		@Override
		public void run() {
			
			HttpURLConnection connection = null;  
			String urlString=Persistent.getGoogleFormURL(ServiceReportCrash.this);			
			URL url;
			AndroidHttpClient client = null;
			 
			try{
				
				url = new URL(urlString);
				connection= (HttpsURLConnection) url.openConnection();
				connection.setReadTimeout(100000);
				connection.setConnectTimeout(150000);
				connection.setRequestMethod("POST");
				connection.setDoInput(true);
				connection.setDoOutput(true);
				
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				 
				String formFieldArray[]=Persistent.getColoumnEntities(ServiceReportCrash.this);
				
				if(formFieldArray!=null && formFieldArray.length>0){
				
					String formData[]={
							this.exception,
							this.exceptionTime,
							Utilities.getVersionCodeVersionName(getApplicationContext()),
							Utilities.getDeviceManufacture(),
							Utilities.getDeviceModelName()							
							
					};
					
					for (int index=0; index<formFieldArray.length; index++) {
						
						params.add(new BasicNameValuePair(formFieldArray[index],formData[index]));						
						Log.i("Crash logger","Updating to server [ "+formFieldArray[index]+" : "+formData[index]+"]");
						
					}
					
				}
				  
				OutputStream os = connection.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
				writer.write(getQuery(params));
				writer.flush();
				writer.close();
				os.close();

				connection.connect();
				 
				int responceCode=connection.getResponseCode();
				String responseMsg=connection.getResponseMessage();
				
				Log.i("Server Respons: ","Response Code: "+responceCode+"\tResponse Message: "+responseMsg);
				
				if(!(responceCode==HttpURLConnection.HTTP_OK)){
							
					if(source==Utilities.INTENT_KEY.SRC_CRASH_REPORTER){
						
						long exceptionId=System.currentTimeMillis();
						Persistent.setException(getApplicationContext(), exceptionId, this.exception, this.exceptionTime);						
					}
					
				}else{
					
					if(source==Utilities.INTENT_KEY.SRC_RECEIVER){
						Persistent.deleteException(ServiceReportCrash.this,this.exceptionID);
					}					
					
				}
				
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			 
			
			if(source==Utilities.INTENT_KEY.SRC_CRASH_REPORTER){
				ServiceReportCrash.this.stopSelf();
			}
			
			else if(source==Utilities.INTENT_KEY.SRC_RECEIVER){
				
				if(String.valueOf(sheduledThreadCount).equals(Thread.currentThread().getName())){					
					ServiceReportCrash.this.stopSelf();
				}
			}
			
			//
		}

		private String getQuery(List<NameValuePair> params)
				throws UnsupportedEncodingException {
			StringBuilder result = new StringBuilder();
			boolean first = true;

			for (NameValuePair pair : params) {
				if (first)
					first = false;
				else
					result.append("&");

				result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
				result.append("=");
				result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
			}

			return result.toString();
		}
	}
}
