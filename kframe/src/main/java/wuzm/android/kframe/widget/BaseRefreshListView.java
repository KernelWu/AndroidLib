package wuzm.android.kframe.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.Serializable;

import wuzm.android.kframe.R;
import wuzm.android.kframe.widget.callback.OnCacheListener;


/**
 * listview supports drag down refresh and drag to bottom load more
 * @author wuzm
 * @version 0.1beta
 * @since 2015/1/18
 */
public class BaseRefreshListView extends ListView implements OnScrollListener{
	private Context mContext;
	private LoadListAdapter mAdapter;

    /** 加载数据前后是否运用控件本身的动画*/
    private boolean showAnimation;
	
	/** 头部刷新*/
	private AbstractRefreshHeadView mHeadView;
	// 刷新头部的高度
	private int mRefreshHeaderHeight;
    // 刷新头部的最大高度,下拉拉伸后的最大高度
    private int mRefreshHeaderMaxHeight;
	// 头部是否正在滚动
	private boolean isScrollHeader;
	private static final int HEAD_SCROLL_DELAY = 10; // head scroll once delay time 
	private static final int HEAD_SCROLL_DS_PER_DELAY = 20; // head scroll distance per delay time 
	
	/*refresh about*/
	private boolean isRefreshing = false;
	private boolean enableRefresh = true;
	private static final int REFRESH_IDLE = 0x11;
	private static final int REFRESH_PREPARE = 0x21;
	private static final int RELEASE_TO_REFRESH = 0x31;
	private static final int REFRESHING = 0x41;
	private int refreshState = REFRESH_IDLE;
	private int distanceEnableRefresh;  //finger drag  distance could refresh
	
	/** 底部加载更多*/
	private AbstractLoadingFootView mFootView;
	
	/*loadmore about*/
	public static final int AUTO_LOADMORE_MODE = 0x10;
	public static final int CLICK_BOTTOM_LOADMORE_MODE = 0x12;
	private int loadmoreMode = AUTO_LOADMORE_MODE;
	private boolean isLoadingmore = false;
	private boolean hasMore = true;
	private boolean enableLoadmore = true;
	private boolean enableClickLoadmore = false;
    private boolean hasLoadFootView = false;
	
	/*touch event about*/
	private float downY;
	
	private OnLoadDataListener mLoadDataListener;
	private OnScrollListener mScrollListener; 
	private OnCacheListener mCacheListener;
	
	private AutoScrollHeadRunner mScrollHeadRunner;

	public BaseRefreshListView(Context context) {
		this(context,null,0);
	}

	public BaseRefreshListView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public BaseRefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		if(attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RefreshViewStyle);
			loadmoreMode = ta.getInt(R.styleable.RefreshViewStyle_loadmoreMode,
					AUTO_LOADMORE_MODE);
			ta.recycle();
		}
		init();
	}
	
	private void init() {
		initHeadView();
		initFootView();
		super.setOnScrollListener(this);
	}

    /**
     * 可以在这里自定义刷新头部
     * @param context
     * @return
     */
    public AbstractRefreshHeadView createRefreshHeadView(Context context) {
        return new DefaultRefreshHeadView(context);
    }
	
	/*refresh about*/
	private void initHeadView() {
		mHeadView = createRefreshHeadView(mContext);
		measureView(mHeadView);
		mRefreshHeaderHeight = mHeadView.getMeasuredHeight();
        mRefreshHeaderMaxHeight = mRefreshHeaderHeight * 2;
		distanceEnableRefresh = mRefreshHeaderHeight;
        mHeadView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                mRefreshHeaderMaxHeight));
        // 设置头部可拉伸的高度
//        DebugUtil.printDebug("refresh", "header heigh:" + mRefreshHeaderHeight);
//        DebugUtil.printDebug("refresh","header pading top before:" + mHeadView.getPaddingTop());
        mHeadView.setPadding(mHeadView.getPaddingLeft(),
                mRefreshHeaderMaxHeight - mRefreshHeaderHeight + mHeadView.getPaddingTop(),
                mHeadView.getPaddingBottom(), mHeadView.getPaddingRight());
//        DebugUtil.printDebug("refresh","header pading top after:" + mHeadView.getPaddingTop());
		this.addHeaderView(mHeadView, null, false);
//        DebugUtil.printDebug("refresh","list pading top before:" + getPaddingTop());
		setPadding(getPaddingLeft(), -mRefreshHeaderMaxHeight + getPaddingTop(),
                getPaddingRight(), getPaddingBottom());
//        DebugUtil.printDebug("refresh","list padding top after:" + getPaddingTop());
	}

	/** 自定义刷新头部View*/
	public final void setHeadView(AbstractRefreshHeadView headView) {
		if(this.mHeadView != null) {
			this.removeHeaderView(this.mHeadView);
		}
		this.mHeadView = headView;
		measureView(headView);
		mRefreshHeaderHeight = headView.getMeasuredHeight();
        mRefreshHeaderMaxHeight = mRefreshHeaderHeight * 2;
		distanceEnableRefresh = mRefreshHeaderHeight;
        mHeadView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                mRefreshHeaderMaxHeight));
        mHeadView.setPadding(mHeadView.getPaddingLeft(),
                mRefreshHeaderMaxHeight - mRefreshHeaderHeight + mHeadView.getPaddingTop(),
                mHeadView.getPaddingBottom(), mHeadView.getPaddingRight());
		this.addHeaderView(headView,null,false);
        setPadding(getPaddingLeft(), - mRefreshHeaderMaxHeight + getPaddingTop(),
                getPaddingRight(), getPaddingBottom());
	}


	private void measureView(View v) {
		int widthMeasureSpec = 0;
		int heightMeasureSpec = 0;
		android.view.ViewGroup.LayoutParams lp = v.getLayoutParams();
		if(lp != null) {
			if(lp.width != 0) {
				widthMeasureSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
			}else {
				widthMeasureSpec = lp.width;
			}
			if(lp.height != 0) {
				heightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
			}else {
				heightMeasureSpec = lp.height;
			}
		}else {
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST);
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		v.measure(widthMeasureSpec, heightMeasureSpec);
	}
	
	private void changeHeadContent() {
		switch (refreshState) {
		case REFRESH_IDLE:
			mHeadView.noneRefresh();
			break;
		case REFRESH_PREPARE:
			mHeadView.prepareRefresh();
			break;
		case RELEASE_TO_REFRESH:
			mHeadView.relaseToRefresh();
			break;
		case REFRESHING:
			mHeadView.refreshing();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 头部滚动中
	 * @param deltaY   必须大于等于0
	 */
	private void scrollHeader(float deltaY) {
		if(deltaY < 0) {
			return;
		}
		if(deltaY < getScrollY() ) {
			// 向上滚动
			mHeadView.onScrollingBack(deltaY);
		}else {
			// 向上滚动
			mHeadView.onDraggingOut(deltaY);
		}
		scrollTo(0, - (int)(deltaY) );
	}
	
	private void onRefreshing() {
		mHeadView.onRefreshStart();
		if(mLoadDataListener != null) {
			mLoadDataListener.onRefresh();
		}
	}
	
	/** 自动滚动头部View类*/
	private final class AutoScrollHeadRunner implements Runnable {
		int mDeltaY;
		int mFinalScrollY;
		
		/**
		 * 
		 * @param deltaY
		 * @param finalScrollY
		 */
	    public void autoScroll(int deltaY,int finalScrollY) {
	    	this.mDeltaY = deltaY;
	    	this.mFinalScrollY = finalScrollY;
	    	post(this);
	    }

		@Override
		public void run() {
			int curScrollY = getScrollY();
			if(mDeltaY == 0) {
				isScrollHeader = false;
				return;
			}
			if(mDeltaY > 0) {
				// 向下滚动
				
				//本次需要滚动的位移
				int realScrollY = Math.min(curScrollY - mFinalScrollY, mDeltaY);
				scrollTo(0, curScrollY - realScrollY);
				mHeadView.onDraggingOut(realScrollY);
				if(mDeltaY == realScrollY) {
					// 还没有滚动到目的地，继续滚动
					postDelayed(this, HEAD_SCROLL_DELAY);	
				}else {
					isScrollHeader = false;
				}
			}else if(mDeltaY < 0) {
				// 向上滚动
				
				//本次需要滚动的位移
				int realScrollY = Math.max(curScrollY - mFinalScrollY, mDeltaY);
				scrollTo(0, curScrollY - realScrollY);
				mHeadView.onScrollingBack(realScrollY);
				if(mDeltaY == realScrollY) {
					// 还没有滚动到目的地，继续滚动
					postDelayed(this, HEAD_SCROLL_DELAY);
				}else {
					isScrollHeader = false;
				}
			}
		}
	}
	
	/**
	 * 
	 * @param deltaY 
	 * @param finalScrollY  最终状态下scrollY
	 */
	private void autoScrollHeadView(int deltaY, int finalScrollY) {
		if(mScrollHeadRunner == null) {
			mScrollHeadRunner = new AutoScrollHeadRunner();
		}
		if(isScrollHeader) {
			return;
		}
		isScrollHeader = true;
		mScrollHeadRunner.autoScroll(deltaY, finalScrollY);
		
	}

    /**
     * 可以在这里自定义加载更多的底部view
     * @param context
     * @return
     */
    public AbstractLoadingFootView createLoadingFootView(Context context) {
        return new DefaultLoadingFootView(mContext);
    }
	
	/*loadmore about*/
	private void initFootView() {
		mFootView = createLoadingFootView(mContext);
		mFootView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(enableClickLoadmore) {
					isLoadingmore = true;
					enableClickLoadmore = false;
					changeFootView();
					onLoadmore();
				}
			}
		});
//		addFooterView(mFootView, null, false);
        hasLoadFootView = false;
	}
	
	/** 自定义加载更多底部view*/
	public void setFootView(AbstractLoadingFootView footView) {
        if(mFootView != null) {
            removeFooterView(mFootView);
        }
		mFootView = footView;
        mFootView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(enableClickLoadmore) {
                    isLoadingmore = true;
                    enableClickLoadmore = false;
                    changeFootView();
                    onLoadmore();
                }
            }
        });
//        addFooterView(mFootView, null, false);
        hasLoadFootView = false;
	}
	
	private void changeFootView() {
		if( !hasMore && mFootView != null && hasLoadFootView) {
			removeFooterView(mFootView);
            hasLoadFootView = false;
//            DebugUtil.printDebug("refresh"," changeFootView Gone");
			return;
		}

        if( hasMore && mFootView != null && !hasLoadFootView) {
            addFooterView(mFootView, null, false);
            hasLoadFootView = true;
//            DebugUtil.printDebug("refresh"," changeFootView Visible");
        }

//        else if(hasMore && mFootView != null && getFooterViewsCount() == 0) {
//			addFooterView(mFootView, null, false);
//		}
//
//        if(hasMore && mFootView != null &&
//                mFootView.getVisibility() == View.GONE) {
//            mFootView.setVisibility(View.VISIBLE);
//        }
		
		switch (loadmoreMode) {
		case AUTO_LOADMORE_MODE:
			mFootView.onAutoLoading();
			break;
		case CLICK_BOTTOM_LOADMORE_MODE:
			mFootView.onClickLoading(hasMore, enableClickLoadmore);
			break;
		default:
			break;
		}
		if(isLoadingmore) {
			mFootView.onLoadingStart();
		}else {
			mFootView.onLoadingStop();
		}
	}
	
	protected final void changeToClickBottomLoadMode() {
		loadmoreMode = CLICK_BOTTOM_LOADMORE_MODE;
		changeFootView();
	}
	
	protected final void changeToAutoLoadMode() {
		loadmoreMode = AUTO_LOADMORE_MODE;
		changeFootView();
	}
	
	public int getLoadmoreMode() {
		return loadmoreMode;
	}
	
	private void onLoadmore() {
		mFootView.onLoadingStart();
		if(mLoadDataListener != null) {
			mLoadDataListener.onLoadmore();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if(mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
		if( mAdapter == null || mAdapter.getCount() == 0) {
			return;
		}
		if(totalItemCount - 2 == mAdapter.getCount() && firstVisibleItem + visibleItemCount >= totalItemCount -1
				&& enableLoadmore && !isLoadingmore && !isRefreshing && hasMore
				&& refreshState == REFRESH_IDLE) {
			switch (loadmoreMode) {
			case AUTO_LOADMORE_MODE:
				isLoadingmore = true;
				changeFootView();
				onLoadmore();
				break;
			case CLICK_BOTTOM_LOADMORE_MODE:
				enableClickLoadmore = true;
				changeFootView();
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(isRefreshing ) {
			return true;
		}
		if(isScrollHeader) {
			return true;
		}
		
		float y = ev.getY();
		switch (ev.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			refreshState = REFRESH_IDLE;
			downY = y;
			changeHeadContent();
			break;
		case MotionEvent.ACTION_MOVE:
//            DebugUtil.printDebug("refresh", "scroll Y:" + getScrollY());
			if(isLoadingmore) {
				break;
			}
			if( y - downY <= 0 && refreshState == REFRESH_IDLE) {
				//nomally move up
				break;
			}
//			int curScrollY = getScrollY();
            int curScrollY = (int)(y - downY);
//            DebugUtil.printDebug("refresh","scroll y:" + curScrollY);
			if(getFirstVisiblePosition() == 0 && enableRefresh) {
				if(refreshState == REFRESH_IDLE) {
					refreshState = REFRESH_PREPARE;
					changeHeadContent();
				}else if(refreshState == REFRESH_PREPARE) {
					if(curScrollY == 0) {
						refreshState = REFRESH_IDLE;
						changeHeadContent();
					}else if(Math.abs(curScrollY) >= distanceEnableRefresh) {
						refreshState = RELEASE_TO_REFRESH;
						changeHeadContent();
					}
					
				}else if(refreshState == RELEASE_TO_REFRESH) {
					if( Math.abs(curScrollY) >= mRefreshHeaderMaxHeight) {
						//the drag head view distance not more than mRefreshHeaderMaxHeight
						return true;
					}
					if( Math.abs(curScrollY) < distanceEnableRefresh) {
						refreshState = REFRESH_PREPARE;
						changeHeadContent();
					}
				}
				scrollHeader(y - downY);
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if(isLoadingmore) {
				return true;
			}
			switch (refreshState) {
			case REFRESH_IDLE:
				break;
			case REFRESH_PREPARE:
				autoScrollHeadView( -HEAD_SCROLL_DS_PER_DELAY, 0);
				return true;
			case RELEASE_TO_REFRESH:
				isRefreshing = true;
				refreshState = REFRESHING;
				changeHeadContent();
				autoScrollHeadView( -HEAD_SCROLL_DS_PER_DELAY, -distanceEnableRefresh);
				onRefreshing();
				return true;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}
	
	public interface OnLoadDataListener {
		public void onRefresh();
		public void onLoadmore();
	}
	
	public final void setRefreshEnable(boolean enable) {
		this.enableRefresh = enable;
	}
	
	public final void setLoadmoreEnable(boolean enable) {
		this.enableLoadmore = enable;
	}
	
	/**
	 * @deprecated use {@link #setLoadAdapter(LoadListAdapter)}
     * 请勿使用，否则会造成BUG
	 */
	@Deprecated 
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
	}
	
	/**
	 * replace {@link #setAdapter(ListAdapter)}
	 * @param adapter
	 */
	public void setLoadAdapter(LoadListAdapter adapter) {
		super.setAdapter(adapter);
        if(adapter != null) {
            mAdapter = adapter;
            hasMore = adapter.hasMore();
        }
        changeFootView();
	}
	
    /**
     * @deprecated use {@link #getLoadAdapter()}
     * 请勿使用，否则会造成BUG
     */
	@Deprecated
	@Override
	public ListAdapter getAdapter() {
		return super.getAdapter();
	}
	
	/**
	 * replace {@link #getAdapter()}
	 */
	public LoadListAdapter getLoadAdapter() {
		return mAdapter;
	}
	
	
	public final void setOnLoadDataListener (OnLoadDataListener l) {
		mLoadDataListener = l;
	}
	
	//because we used the AbsListview.setOnScrollListener and implements it, 
    //we must support another replace method to avoid the crash,and let users use the method as normally
	public final void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

    /**
     * 可以在获取数据操作前调用
     * @param what 用户可以传入本次获取数据操作的唯一标识
     * @return     true 表示本次获取数据操作前的动画采用控件本身的那一套动画
     */
    public boolean onGetDataStart(int what) {
        return showAnimation;
    }

    /**
     * 可以在获取数据结束后调用
     * @param what 用户可以传入本次获取数据操作的唯一标识
     * @return     true 表示本次获取数据操作结束后的动画采用控件本身的那一套动画
     */
    public boolean onGetDataStop(int what) {
        return showAnimation;  }

	/** 完成刷新(失败与成功都会调动)*/
	private void completeRefresh() {
        this.isRefreshing = false;
        refreshState = REFRESH_IDLE;
        if(showAnimation) {
            autoScrollHeadView( -HEAD_SCROLL_DS_PER_DELAY, 0);
        }
        changeFootView();
        showAnimation = true;
	}
	
	/** 完成加载更多(失败与成功都会调动)*/
	private void completeLoadmore() {
        this.isLoadingmore = false;
        this.enableClickLoadmore = false;
//        DebugUtil.printDebug("refresh"," completeLoadmore showAnimation:" + showAnimation);
//        if(showAnimation) {
            changeFootView();
//        }
        showAnimation = true;
	}
	
	/** 刷新成功(数据)*/
	public final void refreshSuccess(boolean hasMore) {
		this.hasMore = hasMore;
		completeRefresh();
		mHeadView.onRefreshSuccess();
	}
	
	/** 刷新失败(数据)*/
	public final void refreshFaild(int errorCode) {
		completeRefresh();
		mHeadView.onRefreshFaild(errorCode);
	}
	
	/** 加载更多成功(数据)*/
	public final void loadMoreSuccess(boolean hasMore) {
		this.hasMore = hasMore;
		completeLoadmore();
		mFootView.onLoadingSuccess();
	}
	
	/** 加载更多失败(数据)*/
	public final void loadMoreFaild(int errorCode) {
		completeLoadmore();
		mFootView.onLoadingFaild(errorCode);
	}
	
	/** 刷新(数据),当已经填充有数据的时候有下拉的动画*/
	public final void refresh() {
		if(isLoadingmore || isRefreshing) {
			return;
		}
		isRefreshing = true;
        if(getLoadAdapter() != null && getLoadAdapter().getCount() != 0) {
            showAnimation = true;
            refreshState = REFRESHING;
            changeHeadContent();
            autoScrollHeadView( -HEAD_SCROLL_DS_PER_DELAY, -distanceEnableRefresh);
        }else {
            showAnimation = false;
        }
		onRefreshing();
	}
	
	/**
	 * 预加载数据
	 * @return cache if has cache,or null
	 */
	public Serializable preLoadCache() {
		if(mCacheListener != null) {
			return mCacheListener.readCache();
		}
		return null;
	}
	
	/**
	 * 存储数据
	 * @param cache
	 * @return true if cache success, or false
	 */
	public boolean cacheData(Serializable cache) {
		if(mCacheListener != null) {
			return mCacheListener.cache(cache);
		}
		return false;
	}
	
	public void setOnCacheListener(OnCacheListener l) {
		mCacheListener = l;
	}

}
