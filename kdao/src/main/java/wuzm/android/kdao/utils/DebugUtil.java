package wuzm.android.kdao.utils;

import android.util.Log;

public class DebugUtil {

	private static boolean debug = false;
	
	public static void setDebug(boolean debug) {
		DebugUtil.debug = debug;
	}
	
	public static void v(String tag, String msg, Throwable throwable) {
		if(debug) {
			Log.v(tag, msg, throwable);
		}
	}
	
	public static void v(String tag, String msg) {
		if(debug) {
			Log.v(tag, msg);
		}
	}
	
	public static void d(String tag, String msg, Throwable throwable) {
		if(debug) {
			Log.d(tag, msg, throwable);
		}
	}
	
	public static void d(String tag, String msg) {
		if(debug) {
			Log.d(tag, msg);
		}
	}
	
	public static void e(String tag, String msg, Throwable throwable) {
		if(debug) {
			Log.e(tag, msg, throwable);
		}
	}
	
	public static void e(String tag, String msg) {
		if(debug) {
			Log.e(tag, msg);
		}
	}
	
	public static void i(String tag, String msg, Throwable throwable) {
		if(debug) {
			Log.i(tag, msg);
		}
	}
	
	public static void i(String tag, String msg) {
		if(debug) {
			Log.i(tag, msg);
		}
	}
}
