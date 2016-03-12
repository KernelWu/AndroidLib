package wuzm.android.kframe.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/***
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2015/1/18
 */
public abstract class AbstractRefreshHeadView extends RelativeLayout {
	private OnRefreshHeadScrollListener mScrollListener;

	public AbstractRefreshHeadView(Context context) {
		this(context, null, 0);
	}

    public AbstractRefreshHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractRefreshHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public abstract void init(Context context);
	public abstract void setTextSize(float size);
	public abstract void setTextSize(int resId);
	/** 没有刷新时*/
	public abstract void noneRefresh();
	/** 需要继续下拉才能达到刷新的条件时*/
	public abstract void prepareRefresh();
	/** 松手就能刷新时*/
	public abstract void relaseToRefresh();
	/** 正在刷新时*/
	public abstract void refreshing();
	
	/** 回滚时*/
	public void onScrollingBack(float distance) {
		if(mScrollListener != null) {
			mScrollListener.onRefreshHeadScrolled(distance);
		}
	}
	
	/** 下拉时*/
	public void onDraggingOut(float distance) {
		if(mScrollListener != null) {
			mScrollListener.onRefreshHeadScrolled(distance);
		}
	}
	
	/** 开始刷新*/
	public void onRefreshStart() {
		refreshing();
	}
	
	/** 结束刷新*/
	public void onRefreshStop() {
		noneRefresh();
	}
	
	/** 刷新数据成功*/
	public void onRefreshSuccess() {
		
	}
	
	/** 刷新数据失败*/
	public void onRefreshFaild(int errorCode) {
		
	}
	
	/** 滚动监听器*/
	public interface OnRefreshHeadScrollListener {
		/**
		 * 
		 * @param scrolledDistance 大于0表示下拉，小于0表示回滚
		 */
		public void onRefreshHeadScrolled(float scrolledDistance);
	}
	
	public void setOnRefreshHeadScrollListener(OnRefreshHeadScrollListener listener) {
		mScrollListener = listener;
	}

    public void onScrolling(int minScrollY, int curScrollY, int maxScrollY) {

    }

}
