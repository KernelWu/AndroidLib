package wuzm.android.kframe.widget;

import android.content.Context;
import android.widget.RelativeLayout;

public abstract class AbstractStatusView extends RelativeLayout {

	public AbstractStatusView(Context context) {
		super(context);
		init();
		onNormal();
	}
	
	public abstract void init();
	
	/**
	 * 正常状态下
	 */
	public abstract void onNormal();
	
	/** 
	 * 没有数据的状态
	 */
	public abstract void onNoData();
	/**
	 * 网络错误的状态
	 */
	public abstract void onNetError();
	/**
	 * 超时的状态
	 */
	public abstract void onTimeout();
	/**
	 * 数据异常的状态
	 */
	public abstract void onDataAbnormaly();

}
