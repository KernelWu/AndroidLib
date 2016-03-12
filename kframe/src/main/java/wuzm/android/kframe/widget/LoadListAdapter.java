package wuzm.android.kframe.widget;

import android.widget.BaseAdapter;

/**
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 *
 */
public abstract class LoadListAdapter<T> extends BaseAdapter{
	
	abstract public boolean hasMore();
	/** 刷新成功的时候调用*/
	abstract public void refresh(T bean);
	/** 加载更多成功的时候调用*/
	abstract public void addMore(T bean);
}
