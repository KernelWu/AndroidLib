package wuzm.android.kframe.utils;

import android.util.Log;

/**
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 *
 */
public class LogUtil {
	
	private static boolean debug = true;
	
	public static void setDebug(boolean debug) {
		LogUtil.debug = debug;
	}
	
	public static void v(String tag,String msg) {
		if(debug) {
			Log.v(tag,msg);
		}
	}
	
	public static void v(String tag,String msg,Throwable tb) {
		if(debug) {
			Log.v(tag,msg,tb);
		}
	}
	
	public static void d(String tag,String msg) {
		if(debug) {
			Log.d(tag,msg);
		}
	}
	
	public static void d(String tag,String msg,Throwable tb) {
		if(debug) {
			Log.d(tag,msg,tb);
		}
	}
	
	public static void i(String tag,String msg) {
		if(debug) {
			Log.i(tag,msg);
		}
	}
	
	public static void i(String tag,String msg,Throwable tb) {
		if(debug) {
			Log.i(tag,msg,tb);
		}
	}
	 
	public static void w(String tag,String msg) {
		if(debug) {
			Log.w(tag,msg);
		}
	}
	
	public static void w(String tag,String msg,Throwable tb) {
		if(debug) {
			Log.w(tag,msg,tb);
		}
	}
	
	public static void e(String tag,String msg) {
		if(debug) {
			Log.e(tag,msg);
		}
	}
	
	public static void e(String tag,String msg,Throwable tb) {
		if(debug) {
			Log.e(tag,msg,tb);
		}
	}

}
