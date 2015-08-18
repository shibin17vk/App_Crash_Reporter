# App_Crash_Reporter
The library helps to store the crash reports  in  the precreated Google sheets without user intimation. In the offline mode, the app store the exception details locally and sends back to the server whenever the device connects to the internet. The crash log includes exception details, time of exception, device details etc.

#### The best way to log the app crashes during the unit test:

The following code snippet gives the idea to implement the crash logger  for your android apps.  You can handle the uncaught exception in your thread using the method  setDefaultUncaughtExceptionHandler().

Create one generic class for handle all uncaught exception in your project and pass the current thread reference through constructor from the accessing context.

####### CrashReporter.java

import java.lang.Thread.UncaughtExceptionHandler;
import android.content.Context;

public class CrashReporter {
 
	private Context context;
	private UncaughtExceptionHandler defaultUEH;
	
	private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
		
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			
			/*
			 *   Handle it  here....
			 *   store it locally some where in the sd card...
			 */
			
		      defaultUEH.uncaughtException(thread, ex);
			
		}
	};
	
	public CrashReporter(Context context){
		this.context=context;
	}
	
	public void reportCrash(Thread thread){
		
		this.defaultUEH = thread.getDefaultUncaughtExceptionHandler();
		thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);
		
	}
 
}

####### MainActivity.class
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity {

	private CrashReporter crashReporter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		crashReporter=new CrashReporter(this);
		crashReporter.reportCrash(Thread.currentThread());			
		
	}

	@Override
	protected void onDestroy() {	
		super.onDestroy();
		crashReporter=null;
	}
	
}



####### Library for CrashReporter:

		The library helps to store the error report  in the sd card and and aslo automatically send the crash reportes to the GoogleDoc form without user intimation. The crash log includes exception details, OS vesrion, internet status, device detail ( model,  disaply resolution, RAM etc.) .

 

####### 1. Import this library to your project

####### 2. Manifest Permission:

 For use this feature, the app must request for the permission  INTERNET, WRITE_EXTERNAL_STORAGE . Add the following element as a child element of the <manifest> element in your app manifest:

<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

####### 3.Create object for CrashReporter in your context and call the method reportCrash()
 
	CrashReporter crashReporter=new CrashReporter(this);
      crashReporter.reportCrash(Thread.currentThread());



