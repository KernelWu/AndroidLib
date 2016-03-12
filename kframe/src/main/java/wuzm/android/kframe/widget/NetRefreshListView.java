package wuzm.android.kframe.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.Toast;

import wuzm.android.kframe.widget.callback.OnListViewDellDataListener;


/**
 * 封装了网络业务模块接口的{@link BaseRefreshListView}
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 */
public class NetRefreshListView extends BaseRefreshListView{
	/** 默认的初始页*/
	private int initPage = 0;
	/** 当前页*/
	private int curPage;
	/** 目标页*/
	private int targetPage;

	private OnListViewDellDataListener dellDataListener;

	public NetRefreshListView(Context context) {
		this(context,null,0);
	}

	public NetRefreshListView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public NetRefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOnLoadDataListener(mInnerLoadDataListener);
	}
	
	private void setCurPage(int pageNo) {
		this.curPage = pageNo;
	}
	
	private int getCurPage() {
		return this.curPage;
	}

    /** 获取数据成功
	 * 
	 * @param what
	 * @param obj  获取的数据
	 */
	public void onGetDataSuccess(int what, Object obj) {
		if(dellDataListener != null) {
			if( !dellDataListener.onFillData(targetPage, obj)) {
				// 使用默认的填充数据方案
				defaultFillData(targetPage, obj);
			}
		}else {
			defaultFillData(targetPage, obj);
		}
		onGetDataComplete(true);
		setCurPage(targetPage);
		if ( getLoadmoreMode() == CLICK_BOTTOM_LOADMORE_MODE) {
			changeToAutoLoadMode();
		}
	}
	
	/** 默认的填充数据方案
	 * 
	 * @param page
	 * @param obj  获取的数据
 	 */
	private void defaultFillData(int page, Object obj) {
		LoadListAdapter adapter = getLoadAdapter();
		if(adapter != null) {
			if(page == initPage) {
				adapter.refresh(obj);
			}else {
				adapter.addMore(obj);
			}
			adapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 服务器返回数据异常
	 * @param what
	 */
	public void onGetDataDataAbnormaly(int what) {
		onGetDataComplete(false);
		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getContext(), "服务器异常", Toast.LENGTH_SHORT)
				.show();
			}
		});
		if( getLoadmoreMode() == AUTO_LOADMORE_MODE) {
			changeToClickBottomLoadMode();
		}
	}
	
	/**获取数据时网络异常*/
	public void onGetDataNetError(int what) {
		onGetDataComplete(false);
		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getContext(), "网络异常", Toast.LENGTH_SHORT)
				.show();
			}
		});
		if( getLoadmoreMode() == AUTO_LOADMORE_MODE) {
			changeToClickBottomLoadMode();
		}
	}
	
	/**获取数据超时*/
	public void onGetDataTimeout(int what) {
		onGetDataComplete(false);
		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getContext(), "超时", Toast.LENGTH_SHORT)
				.show();
			}
		});
		if( getLoadmoreMode() == AUTO_LOADMORE_MODE) {
			changeToClickBottomLoadMode();
		}
	}
	
	/**完成获取数据
	 * 
	 * @param success 获取数据是否成功
	 */
	private void onGetDataComplete(boolean success) {
		if (targetPage == initPage) {
			if(success) {
				if(getLoadAdapter() != null) {
					refreshSuccess(getLoadAdapter().hasMore());
				}else {
					refreshSuccess(false);
				}
			}else {
				refreshFaild(0);
			}
			
		} else {
			if(success) {
				if(getLoadAdapter() != null) {
					loadMoreSuccess(getLoadAdapter().hasMore());
				}else {
					loadMoreSuccess(false);
				}
			}else {
				loadMoreFaild(0);
			}
		}
	}
	
	private final Handler uiHandler = new Handler(Looper.getMainLooper()) {
	};
	
	@Override
	public void setLoadAdapter(LoadListAdapter adapter) {
		super.setLoadAdapter(adapter);
		setCurPage(initPage);
	}
	
	public void setInitPage(int page) {
		this.initPage = page;
	}
	
	/** 推荐使用这个获取与填充数据,使用这个的时候不要使用 {@link #setOnLoadDataListener(OnLoadDataListener)}*/
	public void setOnDellDataListener(OnListViewDellDataListener listener) {
		dellDataListener = listener;
	}
	
	private OnLoadDataListener mInnerLoadDataListener = new OnLoadDataListener() {
		
		@Override
		public void onRefresh() {
			targetPage = initPage;
			if(dellDataListener != null) {
				dellDataListener.onGetData(initPage);
			}
		}
		
		@Override
		public void onLoadmore() {
			targetPage = getCurPage() + 1;
			if(dellDataListener != null) {
				dellDataListener.onGetData(targetPage);
			}
		}
	};
	
}
