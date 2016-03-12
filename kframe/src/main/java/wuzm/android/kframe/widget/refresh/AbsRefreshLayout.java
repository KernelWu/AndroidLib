package wuzm.android.kframe.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import wuzm.android.kframe.widget.AbstractLoadingFootView;
import wuzm.android.kframe.widget.AbstractRefreshHeadView;
import wuzm.android.kframe.widget.DefaultLoadingFootView;
import wuzm.android.kframe.widget.DefaultRefreshHeadView;

/**
 * 提供下拉刷新和上拉加载更多功能的基类
 *
 * 注意：上拉加载更多的时候，尾部不会跟着手指的移动速率滚动出来，而是会像动画一样直接滚动出全部尾部
 *
 * Created by kernel on 15/4/9.
 * Email: 372786297@qq.com
 * version: 0.1beta
 */
public abstract class AbsRefreshLayout<T extends View> extends ViewGroup implements
        AbsListView.OnScrollListener{
    private static final String TAG = AbsRefreshLayout.class.getSimpleName();
    private Context mContext;

    /**
     * About refresh variable
     */
    private AbstractRefreshHeadView mHeadView;
    private int mHeadContentHeight;
    private int mHeadHeight;
    private boolean isTop;
    private boolean enableRefresh;
    private OnRefreshListener mRefreshListener;

    /**
     * Abount content
     */
    private T mContentView;

    /**
     * About load more variable
     */
    private AbstractLoadingFootView mFootView;
    private int mFootHeight;
    private boolean enableLoadMore;
    private boolean hasMore;
    private OnLoadMoreListener mLoadMoreListener;

    // 当前的状态
    private int mStatus = STATUS_IDLE;
    // 空闲状态
    public static final int STATUS_IDLE = 0;
    // 下拉刷新状态
    public static final int STATUS_PREPARE_REFRESH = 1;
    // 松手刷新状态
    public static final int STATUS_RELEASE_REFRESH = 2;
    // 刷新状态
    public static final int STATUS_REFRESHING = 3;
    // 加载更多状态
    public static final int STATUS_LOADING_MORE = 4;

    /**
     * Abount scroll variable and Contans
     */

    private int mDownX;
    private int mDownY;
    private int mCurrentY;
    private int mLastY;

    /**
     * 手指Y轴的移动方向
     */
    private int yScrollOrientation = Y_SCROLL_NONE;
    private static final int Y_SCROLL_NONE = 20;
    private static final int Y_SCROLL_DOWN = 21;
    private static final int Y_SCROLL_UPPER = 22;

    /**
     * 移动头部相关
     */
    private static float DRAGGING_DOWN_RADIO = 0.5f;
    private static float DRAGGING_UPPER_RADIO = 1f;


    /**
     * 控制显示与隐藏头部、尾部的滚动操作
     */
    private Scroller mScroller;

    public AbsRefreshLayout(Context context) {
        this(context, null);
    }

    public AbsRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        enableRefresh = enableRefresh();
        enableLoadMore = enableLoadMore();
        DRAGGING_DOWN_RADIO = getDragHeadDownRadio();
        DRAGGING_UPPER_RADIO = getDragHeadUpperRadio();
        initHeadView();
        initContentView(attrs);
        initFootView();
        mScroller = new Scroller(context);
    }

    /**
     * About refresh head
     */

    public AbstractRefreshHeadView createHeadView(Context context) {
        return new DefaultRefreshHeadView(context);
    }

    private void initHeadView() {
        mHeadView = createHeadView(mContext);
        measureView(mHeadView);
        mHeadContentHeight = mHeadView.getMeasuredHeight();
        mHeadHeight = mHeadContentHeight * 3 / 2;
        mHeadView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                mHeadHeight));
        mHeadView.setPadding(mHeadView.getPaddingLeft(),
                mHeadView.getPaddingTop() + mHeadHeight - mHeadContentHeight,
                mHeadView.getPaddingRight(), mHeadView.getPaddingBottom());
        addView(mHeadView);
    }

    private void changeHeadView() {
        switch (mStatus) {
            case STATUS_IDLE:
                mHeadView.noneRefresh();
                break;
            case STATUS_PREPARE_REFRESH:
                mHeadView.prepareRefresh();
                break;
            case STATUS_RELEASE_REFRESH:
                mHeadView.relaseToRefresh();
                break;
            case STATUS_REFRESHING:
                mHeadView.refreshing();
                break;
        }
    }

    private void scrollHead(int deltaY) {
        scrollTo(0, getScrollY() + deltaY);
        if(mHeadView != null) {
            if(deltaY > 0) {
                mHeadView.onScrollingBack(-deltaY);
            }else {
                mHeadView.onDraggingOut(deltaY);
            }
        }
    }

    /***
     * Abount content
     */

    public abstract T createContentView(Context context, AttributeSet attrs);

    private void initContentView(AttributeSet attrs) {
        mContentView = createContentView(mContext, attrs);
        setupContentView(mContentView);
        addView(mContentView);
    }

    public AbstractRefreshHeadView getHeadView() {
        return mHeadView;
    }

    public void setupContentView(T contentView) {
    }

    public T getContentView() {
        return mContentView;
    }

    /**
     * About load more foot
     */

    public AbstractLoadingFootView createFootView(Context context) {
        return new DefaultLoadingFootView(context);
    }

    private void initFootView() {
        mFootView = createFootView(mContext);
        measureView(mFootView);
        mFootHeight = mFootView.getMeasuredHeight();
        mFootView.setLayoutParams(new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, mFootHeight));
        addView(mFootView);
    }

    public AbstractLoadingFootView getFootView() {
        return mFootView;
    }

    private void changeFootView() {
        if(hasMore && mStatus == STATUS_LOADING_MORE) {
            showFootView();
        }else if( !hasMore || mStatus == STATUS_IDLE) {
            hideFootView();
        }
    }

    private void showFootView() {
        autoScroll(mHeadHeight + mFootHeight);
    }

    private void hideFootView() {
        autoScroll(mHeadHeight);
    }

    public void measureView(View view) {
        if(view == null) {
            return;
        }
        int mMeasureWidth = 0;
        int mMeasureHeight = 0;
        LayoutParams LP = view.getLayoutParams();
        if(LP != null) {
            if(LP.width != 0) {
                mMeasureWidth = MeasureSpec.makeMeasureSpec(LP.width, MeasureSpec.AT_MOST);
            }else {
                mMeasureWidth = LP.width;
            }
            if(LP.height != 0) {
                mMeasureHeight = MeasureSpec.makeMeasureSpec(LP.height, MeasureSpec.UNSPECIFIED);
            }else {
                mMeasureHeight = LP.height;
            }
        }else {
            mMeasureWidth = MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST);
            mMeasureHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(mMeasureWidth, mMeasureHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure");
        int height = 0;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        for(int i =0, childCount = getChildCount() ; i < childCount ; i ++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            height += child.getMeasuredHeight();
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed) {
            View child = null;
            int mTop = getTop();
            for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
                child = getChildAt(i);
                child.layout(0, mTop, child.getMeasuredWidth(),
                        mTop + child.getMeasuredHeight());
                mTop += child.getMeasuredHeight();
            }
            scrollTo(0, mHeadHeight);
        }
    }


    /**
     * 判断是否满足下拉刷新的条件(垂直下拉)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            return false;
        }
        int y = (int)ev.getRawY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int)ev.getRawX();
                mDownY = (int)ev.getRawY();
                mLastY = y;
                mCurrentY = y;
                isTop = isTop();
                break;
            case MotionEvent.ACTION_MOVE:
                if(y > mLastY) {
                    // 下拉
                    yScrollOrientation = Y_SCROLL_DOWN;
                }else {
                    // 上拉
                    yScrollOrientation = Y_SCROLL_UPPER;
                }
                mLastY = mCurrentY;
                mCurrentY = y;
                if( !isTop) {
                    return false;
                }
                if (!enableRefresh) {
                    return false;
                }
                // 确认滚动的方向
                if (isScrollVertical(ev, mDownX, mDownY)) {
                    return y - mDownY > 0;
                } else if (isScrollHorizontal(ev, mDownX, mDownY)) {
                    return false;
                } else {
                    return true;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public abstract boolean isTop();

    public boolean isScrollHorizontal(MotionEvent ev, int downX, int downY) {
        return Math.abs(ev.getRawY() - downY) <
                Math.abs(ev.getRawX() - downX);
    }

    public boolean isScrollVertical(MotionEvent ev, int downX, int downY) {
        return Math.abs(ev.getRawY() - downY) >=
                Math.abs(ev.getRawX() - downX);
    }

    /**
     * 处理下拉刷新和上拉加载更多
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int)event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return  true;
            case MotionEvent.ACTION_MOVE:
                if(y > mLastY) {
                    // 下拉
                    yScrollOrientation = Y_SCROLL_DOWN;
                }else {
                    // 上拉
                    yScrollOrientation = Y_SCROLL_UPPER;
                }
                mLastY = mCurrentY;
                mCurrentY = y;
                int deltaY = 0; //本次滚动的位移
                if (isTop) {
                    // 处理下拉刷新的滑动触屏事件
                    int curScrollY = getScrollY();
                    switch (yScrollOrientation) {
                        case Y_SCROLL_UPPER:
                            deltaY = (int)((mLastY - mCurrentY) * DRAGGING_UPPER_RADIO);
                            break;
                        case Y_SCROLL_DOWN:
                            deltaY = (int)((mLastY - mCurrentY) * DRAGGING_DOWN_RADIO);
                            break;
                    }
                    if(curScrollY + deltaY >= mHeadHeight) {
                        // 继续上拉的距离超过了头部未隐藏的高度，则让头部移动到刚好完全隐藏的位置
                        deltaY = mHeadHeight - curScrollY;
                        if(curScrollY == mHeadHeight) {
                            // 已经完全隐藏了头部了还继续上拉
                            return true;
                        }
                    }else if(curScrollY + deltaY <= 0) {
                        // 继续下拉的距离超过了头部未显示的高度，则让头部移动到刚好完全显示的位置
                        deltaY = - curScrollY;
                        if(curScrollY == 0) {
                            // 已经完全显示头部了还继续下拉
                            return true;
                        }
                    }
                    switch (mStatus) {
                        case STATUS_IDLE:
                            mStatus = STATUS_PREPARE_REFRESH;
                            changeHeadView();
                            break;
                        case STATUS_PREPARE_REFRESH:
                            if (curScrollY == 0) {
                                mStatus = STATUS_IDLE;
                                changeHeadView();
                            } else if (curScrollY <= (mHeadHeight - mHeadContentHeight)
                                    && curScrollY > 0) {
                                mStatus = STATUS_RELEASE_REFRESH;
                                changeHeadView();
                            }
                            break;
                        case STATUS_RELEASE_REFRESH:
                            if (curScrollY > (mHeadHeight - mHeadContentHeight)) {
                                mStatus = STATUS_PREPARE_REFRESH;
                                changeHeadView();
                            }
                            break;
                    }
                    scrollHead(deltaY);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                switch (mStatus) {
                    case STATUS_PREPARE_REFRESH:
                        autoScroll(mHeadHeight);
                        mStatus = STATUS_IDLE;
                        return true;
                    case STATUS_RELEASE_REFRESH:
                        autoScroll(mHeadHeight - mHeadContentHeight);
                        mStatus = STATUS_REFRESHING;
                        onRefreshStart();
                        onRefreshing();
                        return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 自定义手指下拉刷新头部的头部滚动系数(手指下移一像素与对应的刷新头部下移的像素的比率)
     * @return  头部滚动系数
     */
    public float getDragHeadDownRadio() {
        return 0.5f;
    }

    /**
     * 自定义手指上拉刷新头部的头部滚动系数(手指上移一像素与对应的刷新头部上移的像素的比率)
     * @return  头部滚动系数
     */
    public float getDragHeadUpperRadio() {
        return 1f;
    }

    /**
     * 不用手指操作的滚动操作
     * @param targetScrollY
     */
    public void autoScroll(int targetScrollY) {
        mScroller.startScroll(0, getScrollY(), 0, targetScrollY - getScrollY());
        invalidate();
    }


    /**
     * 从这里判断是否要进行加载更多操作
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(enableLoadMore && hasMore && mCurrentY - mDownY < 0 &&
                mStatus == STATUS_IDLE && isBottom()) {
            onLoadMoreStart();
            changeFootView();
            onLoadingMore();
        }
    }

    public abstract boolean isBottom();

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()) {
            int scrollY = getScrollY();
            if(scrollY < mHeadHeight) {
                if(mHeadView != null) {
                    mHeadView.onScrolling(0, scrollY, mHeadHeight);
                }
            }else if(scrollY > mHeadHeight &&
                    scrollY < mHeadHeight + mFootHeight) {
                if(mFootView != null) {
                    mFootView.onScrolling(mHeadHeight, scrollY,
                            mHeadHeight + mFootHeight);
                }
            }
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     * About refresh methods
     */

    public boolean enableRefresh() {
        return true;
    }

    private void onRefreshStart() {
        mStatus = STATUS_REFRESHING;
        changeHeadView();
    }

    private void onRefreshing() {
        if(mRefreshListener != null) {
            mRefreshListener.onRefresh();
        }
    }

    private void onRefreshComplete() {
        autoScroll(mHeadHeight);
        mStatus = STATUS_IDLE;
        changeHeadView();
    }

    public final void completeRefresh(boolean hasMore) {
        this.hasMore = hasMore;
        onRefreshComplete();
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    public interface OnRefreshListener {
        public void onRefresh();
    }

    /**
     * About load more methods
     */

    public boolean enableLoadMore() {
        return false;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    private void onLoadMoreStart() {
        mStatus = STATUS_LOADING_MORE;
    }

    private void onLoadingMore() {
        if(mLoadMoreListener != null) {
            mLoadMoreListener.onLoadMore();
        }
    }

    private void onLoadMoreComplete() {
        mStatus = STATUS_IDLE;
        changeFootView();
    }

    public final void completeLoadMore(boolean hasMore) {
        this.hasMore = hasMore;
        onLoadMoreComplete();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    public interface OnLoadMoreListener {
        public void onLoadMore();
    }

}
