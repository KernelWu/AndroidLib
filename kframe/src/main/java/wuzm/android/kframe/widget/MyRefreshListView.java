package wuzm.android.kframe.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.io.Serializable;

import wuzm.android.kframe.widget.BaseRefreshListView.OnLoadDataListener;
import wuzm.android.kframe.widget.callback.OnCacheListener;
import wuzm.android.kframe.widget.callback.OnListViewDellDataListener;


public class MyRefreshListView extends FrameLayout {
	private NetRefreshListView mListView;
	private AbstractStatusView mStatusView;

	public MyRefreshListView(Context context) {
		this(context, null, 0);
	}

	public MyRefreshListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyRefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}
	
	private void init(Context context, AttributeSet attrs, int defStyle) {
		mListView = new NetRefreshListView(context, attrs, defStyle);
        setupListView(mListView);
		mStatusView = createStatusView(context);
		mStatusView.setOnClickListener(mStatusViewClickListener);
		
		addView(mStatusView, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(mListView, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		
	}

    /**
     * 可以在这里自定义状态View
     * @param context
     * @return
     */
    public AbstractStatusView createStatusView(Context context) {
        return new DefaultStatusView(context);
    }
	
	public void setCustomerStatusView(AbstractStatusView view) {
		removeAllViews();
		mStatusView = view;
		mStatusView.setOnClickListener(mStatusViewClickListener);
		addView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(mListView, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

    /**
     * 可以在这里对listview的属性进行配置
     * @param listview
     */
    public void setupListView(ListView listview) {

    }

    /**
     * 开始获取数据
     * @param what
     * @return
     */
    public boolean onGetDataStart(int what) {
        if(mListView != null) {
            return mListView.onGetDataStart(what);
        }
        return false;
    }

    /**
     * 可以在获取数据操作前调用
     * @param what 用户可以传入本次获取数据操作的唯一标识
     * @return     true 表示本次获取数据操作前的动画采用控件本身的那一套动画
     */
    public boolean onGetDataStop(int what) {
        if(mListView != null) {
            return mListView.onGetDataStop(what);
        }
        return false;
    }

    /**
     * 可以在获取数据结束后调用
     * @param what 用户可以传入本次获取数据操作的唯一标识
     * @return     true 表示本次获取数据操作结束后的动画采用控件本身的那一套动画
     */
	public void onGetDataSuccess(int what, Object obj) {
		if(mListView != null) {
			mListView.onGetDataSuccess(what, obj);
		}
		if(mStatusView != null && mListView != null
				&& mListView.getLoadAdapter() != null) {
			if(mListView.getLoadAdapter().getCount() > 0) {
				mListView.setVisibility(View.VISIBLE);
				mStatusView.onNormal();
			}else {
				mListView.setVisibility(View.GONE);
				mStatusView.onNoData();
			}
		}
	}
	
	/**
	 * 服务器返回数据异常
	 * @param what
	 */
	public void onGetDataDataAbnormaly(int what) {
		if(mListView != null) {
			mListView.onGetDataDataAbnormaly(what);
		}
		if(mStatusView != null && mListView != null
				&& mListView.getLoadAdapter() != null) {
			if(mListView.getLoadAdapter().getCount() > 0) {
				mListView.setVisibility(View.VISIBLE);
				mStatusView.onNormal();
			}else {
				mListView.setVisibility(View.GONE);
				mStatusView.onDataAbnormaly();
			}
		}
	}
	
	/**获取数据时网络异常*/
	public void onGetDataNetError(int what) {
		if(mListView != null) {
			mListView.onGetDataNetError(what);
		}
		if(mStatusView != null && mListView != null
				&& mListView.getLoadAdapter() != null) {
			if(mListView.getLoadAdapter().getCount() > 0) {
				mListView.setVisibility(View.VISIBLE);
				mStatusView.onNormal();
			}else {
				mListView.setVisibility(View.GONE);
				mStatusView.onNetError();
			}
		}
	}
	
	/**获取数据超时*/
	public void onGetDataTimeout(int what) {
		if(mListView != null) {
			mListView.onGetDataTimeout(what);
		}
		if(mStatusView != null && mListView != null
				&& mListView.getLoadAdapter() != null) {
			if(mListView.getLoadAdapter().getCount() > 0) {
				mListView.setVisibility(View.VISIBLE);
				mStatusView.onNormal();
			}else {
				mListView.setVisibility(View.GONE);
				mStatusView.onTimeout();
			}
		}
	}
	
	public void setLoadAdapter(LoadListAdapter adapter) {
		if(mListView != null) {
			mListView.setLoadAdapter(adapter);
		}
	}

	public LoadListAdapter getLoadAdapter() {
		if(mListView != null) {
			return mListView.getLoadAdapter();
		}
		return null;
	}
	
	public void setInitPage(int page) {
		if(mListView != null) {
			mListView.setInitPage(page);
		}
	}

	public void setOnDellDataListener(OnListViewDellDataListener listener) {
		if(mListView != null) {
			mListView.setOnDellDataListener(listener);
		}
	}
	
	/** 自定义刷新头部View*/
	public final void setHeadView(AbstractRefreshHeadView headView) {
		if(mListView != null) {
			mListView.setHeadView(headView);
		}
	}
	
	/** 自定义加载更多底部view*/
	public void setFootView(AbstractLoadingFootView footView) {
		if(mListView != null) {
			mListView.setFootView(footView);
		}
	}
	
	public final void setRefreshEnable(boolean enable) {
		if(mListView != null) {
			mListView.setRefreshEnable(enable);
		}
	}
	
	public final void setLoadmoreEnable(boolean enable) {
		if(mListView != null) {
			mListView.setLoadmoreEnable(enable);
		}
	}
	
	public final void setOnLoadDataListener (OnLoadDataListener l) {
		if(mListView != null) {
			mListView.setOnLoadDataListener(l);
		}
	}
	
	//because we used the AbsListview.setOnScrollListener and implements it, 
    //we must support another replace method to avoid the crash,and let users use the method as normally
	public final void setOnScrollListener(OnScrollListener l) {
		if(mListView != null) {
			mListView.setOnScrollListener(l);
		}
	}

	/** 刷新成功(数据)*/
	public final void refreshSuccess(boolean hasMore) {
		if(mListView != null) {
			mListView.refreshSuccess(hasMore);
		}
	}
	
	/** 刷新失败(数据)*/
	public final void refreshFaild(int errorCode) {
		if(mListView != null) {
			mListView.refreshFaild(errorCode);
		}
	}
	
	/** 加载更多成功(数据)*/
	public final void loadMoreSuccess(boolean hasMore) {
		if(mListView != null) {
			mListView.loadMoreSuccess(hasMore);
		}
	}
	
	/** 加载更多失败(数据)*/
	public final void loadMoreFaild(int errorCode) {
		if(mListView != null) {
			mListView.loadMoreFaild(errorCode);
		}
	}
	
	/** 刷新(数据)*/
	public final void refresh() {
		if(mListView != null) {
			mListView.refresh();
		}
	}
	
	/**
	 * 预加载数据
	 * @return cache if has cache,or null
	 */
	public Serializable preLoadCache() {
		if(mListView != null) {
			return mListView.preLoadCache();
		}
		return null;
	}
	
	/**
	 * 存储数据
	 * @param cache
	 * @return true if cache success, or false
	 */
	public boolean cacheData(Serializable cache) {
		if(mListView != null) {
			return mListView.cacheData(cache);
		}
		return false;
	}
	
	public void setOnCacheListener(OnCacheListener l) {
		if(mListView != null) {
			mListView.setOnCacheListener(l);
		}
	}
	
	private OnClickListener mStatusViewClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mStatusView != null) {
				mStatusView.onNormal();
			}
			if(mListView != null) {
				mListView.setVisibility(View.VISIBLE);
				mListView.refresh();
			}
		}
	};

}
