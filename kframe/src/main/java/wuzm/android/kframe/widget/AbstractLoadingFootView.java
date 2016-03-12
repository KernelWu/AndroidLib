package wuzm.android.kframe.widget;

import android.content.Context;
import android.widget.RelativeLayout;

/***
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2015/1/16
 */
public abstract class AbstractLoadingFootView extends RelativeLayout {
	
	public AbstractLoadingFootView(Context context) {
		super(context);
		init(context);
	}
	
	public abstract void init(Context context);
	
	/** 开始加载更多*/
	public void onLoadingStart() {
		
	}
	
	/** 结束加载更多*/
	public void onLoadingStop() {
		
	}
	
	/** 自动加载模式*/
	public void onAutoLoading() {
	}
	
	/**
	 * 点击加载模式
	 * @param hasMore 是否还有更多数据可以加载
	 * @param enable  是否可以点击加载
	 */
	public void onClickLoading(boolean hasMore, boolean enable) {
		
	}
	
	/** 加载更多成功*/
	public void onLoadingSuccess() {
	}
	
	/** 加载更多失败*/
	public void onLoadingFaild(int errorCode) {
	}

    public void onScrolling(int minScrollY, int curScrollY, int maxScrollY) {

    }
	
}
