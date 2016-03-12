package wuzm.android.kframe.widget;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

/**
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 *
 */
@TargetApi(VERSION_CODES.HONEYCOMB)
public class MyCalendarDialog extends DatePickerDialog{
	private long minDate = -1;
	private long maxDate = -1;
	
	public MyCalendarDialog(Context context, OnDateSetListener callBack,
			int year, int monthOfYear, int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
	}
	
	/**
	 * sdk版本3.0以上该方法才有效
	 * @param date
	 */
	public void setMinDate(long date) {
		if(date < 0) {
			throw new IllegalArgumentException("date must bigger than 0");
		}
		this.minDate = date;
		if(VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			getDatePicker().setMinDate(date);
		}
	}
	
	/**
	 * sdk版本3.0以上该方法才有效
	 * @param date
	 */
	public void setMaxDate(long date) {
		if(date < 0) {
			throw new IllegalArgumentException("date must bigger than 0");
		}
		this.maxDate = date;
		if(VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			getDatePicker().setMaxDate(date);
		}
	}
	
	/**
	 * 
	 * @return 返回-1表示没有设置
	 */
	public long getMinDate() {
		return this.minDate;
	}
	
	/**
	 * 
	 * @return 返回-1表示没有设置
	 */
	public long getMaxDate() {
		return this.maxDate;
	}

}
