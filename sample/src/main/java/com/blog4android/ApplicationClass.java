package com.blog4android;

import com.blog4android.crashreporter.CrashReporter;

import android.app.Application;

public class ApplicationClass extends Application {

	private CrashReporter crashReporter; 
	@Override
	public void onCreate() { 
		super.onCreate();
		
		String[] googleFormEntities={
				
				"entry_83514160" , //Exception String
				"entry_243233283", // date time
				"entry_1839554973", // app details
				"entry_1052208750", //device manufacture
				"entry_570776229"  //device_model
		};
		
		crashReporter=new CrashReporter(this);
		crashReporter.reportCrash(Thread.currentThread());
		crashReporter.setGoogleFormUrl("https://docs.google.com/forms/d/1uTaxBy7TFzx1EREdnnFzSYfLKvz_kscX33McqFn7I4Q/formResponse");		
		crashReporter.setGoogleFormColoumnEntries(googleFormEntities);
		
	}
	
	
}
