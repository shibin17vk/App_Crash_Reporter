package com.blog4android.crashreporter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Persistent {

	public static final String SHARED_PREF_NAME="shared_pref_uncaught_exception_log";
	
	public static final String PREF_KEY_ID="id";
	public static final String PREF_KEY_EXCEPTION_STRING_="exception_string";
	public static final String PREF_KEY_EXCEPTION_ID_="exception_id";
	public static final String PREF_KEY_EXCEPTION_TIME_="exception_time";
	
	public static final String PREF_KEY_GOOGLE_FORM_URL="google_form_url";
	public static final String PREF_KEY_COLOUMN_ENTITIES="google_form_column_entities";
	
	public static final  String INVALID_STRING="invalid_string";
	
	public static void setException(Context context,long id, String exception, String dateTime){
	
		Editor editor=context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();		
		editor.putLong(PREF_KEY_EXCEPTION_ID_+id, id);
		editor.putString(PREF_KEY_EXCEPTION_STRING_+id, exception);
		editor.putString(PREF_KEY_EXCEPTION_TIME_+id, dateTime);		
		editor.commit();
		
		pushIntoIdList(context, id);
		
	}
	
	
	public static String getException(Context context, long id){
		return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).
		              getString(PREF_KEY_EXCEPTION_STRING_+id, INVALID_STRING);
	}
	
	public static  String getExceptionTime(Context context, long id){
		return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).
		              getString(PREF_KEY_EXCEPTION_TIME_+id, INVALID_STRING);
	}
	
	
	public static void deleteException(Context context, long id){

		Editor editor=context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();		
		editor.remove(PREF_KEY_EXCEPTION_ID_+id);
		editor.remove(PREF_KEY_EXCEPTION_STRING_+id);
		editor.remove(PREF_KEY_EXCEPTION_TIME_+id);	
		editor.commit();
		
		deleteFromIdList(context,id);
	}
	
	public static void deleteFromIdList(Context context, long id){
	
		Set<String> idsList=context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).getStringSet(PREF_KEY_ID, null);;
		
		if(idsList==null){
			return;
		}
		
		idsList.remove(String.valueOf(id));
		Editor editor=context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();		
		editor.putStringSet(PREF_KEY_ID,idsList);
		editor.commit();		
		
	}
	
	public static void pushIntoIdList(Context context,long id){
		
		Set<String> idsList=context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).getStringSet(PREF_KEY_ID, null);;
		
		if(idsList==null){
			idsList=new HashSet<String>();
		}
		idsList.add(String.valueOf(id));
		
		Editor editor=context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();		
		editor.putStringSet(PREF_KEY_ID,idsList);
		editor.commit();
	}
	
	public static String[] getAllExceptionIds(Context context){
		
		Set<String> idsList=context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).getStringSet(PREF_KEY_ID, null);
		return  idsList.toArray(new String[idsList.size()]);
	}
	
	public static int getPendinRepotrsCount(Context context){
		
		Set<String> idsList=context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).getStringSet(PREF_KEY_ID, null);
		return idsList.size();
	}
	
	public static void setGoogleFormUrl(Context context, String formURL){
		
		Editor editor=context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(PREF_KEY_GOOGLE_FORM_URL, formURL);
		editor.commit();		
		
	}
	
	public static String getGoogleFormURL(Context context){
		return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).
				getString(PREF_KEY_GOOGLE_FORM_URL, INVALID_STRING);
	}
	
	public static void setFormColoumnEntities(Context context, String[] coloumnIds){
		
		try{
			JSONArray jsonArray=new JSONArray();
			for(int i=0;i<coloumnIds.length; i++){
				jsonArray.put(coloumnIds[i]);
			}

			Editor editor=context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
			editor.putString(PREF_KEY_COLOUMN_ENTITIES, jsonArray.toString());
			editor.commit();
			
		}catch(Exception ex){
			
		} 
		
	}
	
	public static String[] getColoumnEntities(Context context){

		String idsList=context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).
				            getString(PREF_KEY_COLOUMN_ENTITIES, null);
		try{
			
			JSONArray jsonArray=new JSONArray(idsList);
			String resultArray[]=new String[jsonArray.length()];
			
			for(int index=0; index<jsonArray.length(); index++){
				resultArray[index]=jsonArray.getString(index);
			} 
			return  resultArray;
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;		
		
	}
	
}
