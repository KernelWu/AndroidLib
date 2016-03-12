package wuzm.android.kframe.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.ListAdapter;

/**
 * 提供上拉刷新，底部上拉加载更多功能的GridView
 *
 * 注意： 缓慢上拉加载更多会有点问题，没到达最底部的时候尾部会突兀的显示出来，可能覆盖住底部的部分位置
 *
 * Created by kernel on 15/4/13.
 * Email: 372786297@qq.com
 * version: 0.1beta
 */
public class RefreshGridView extends AbsRefreshAdapterView<GridView>{
    private static final String TAG = RefreshListView.class.getSimpleName();
    private GridView mView;

    public RefreshGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshGridView(Context context) {
        super(context);
    }

    @Override
    public GridView createContentView(Context context, AttributeSet attrs) {
        mView = new GridView(context, attrs);
        mView.setVerticalSpacing(10);
        mView.setHorizontalSpacing(10);
        return mView;
    }

    @Override
    public void setupContentView(GridView contentView) {
        super.setupContentView(contentView);
        contentView.setOnScrollListener(this);
    }

    @Override
    public boolean enableLoadMore() {
        return true;
    }

    @Override
    public boolean enableRefresh() {
        return true;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if(mView != null) {
            mView.setAdapter(adapter);
        }
    }

    @Override
    public ListAdapter getAdapter() {
        if(mView != null) {
            return mView.getAdapter();
        }
        return null;
    }
}
