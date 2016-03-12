package wuzm.android.kframe.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;


public class MySharePreferences {
	
	private static SharedPreferences instance;
	
	public MySharePreferences(Context context) {
		if(instance == null) {
			instance = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		}
	}
	
	/*****************Command Method********************************/
	
	public void putBoolean(String key,boolean value) {
		instance.edit().putBoolean(key, value).commit();
	}
	
	public boolean getBoolean(String key) {
		return instance.getBoolean(key, false);
	}
	
	public void putInt(String key,int value) {
		instance.edit().putInt(key, value).commit();
	}
	
	public int getInt(String key) {
		return instance.getInt(key, -1);
	}
	
	public void putLong(String key,long value) {
		instance.edit().putLong(key, value).commit();
	}
	
	public long getLong(String key) {
		return instance.getLong(key, -1);
	}
	
	public void putFloat(String key,float value) {
		instance.edit().putFloat(key, value).commit();
	}
	
	public float getFloat(String key) {
		return instance.getFloat(key, -1);
	}
	
	public void putString(String key,String value) {
		instance.edit().putString(key, value).commit();
	}
	
	public String getString(String key) {
		return instance.getString(key, null);
	}
	
	public void putStringSet(String key,Set<String> set) {
		instance.edit().putStringSet(key, set).commit();
	}
	
	public Set<String> getStringSet(String key) {
		return instance.getStringSet(key, null);
	}

}
