package wuzm.android.kframe.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 具有顶部下拉刷新与底部上拉加载更多的ListView
 *
 * 注意： 缓慢上拉加载更多会有点问题，没到达最底部的时候尾部会突兀的显示出来，可能覆盖住底部的部分位置
 *
 * Created by kernel on 15/4/11.
 * Email: 372786297@qq.com
 * version: 0.1beta
 */
public class RefreshListView extends AbsRefreshAdapterView<ListView>{
    private static final String TAG = RefreshListView.class.getSimpleName();
    private ListView mView;

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshListView(Context context) {
        super(context);
    }

    @Override
    public void setupContentView(ListView contentView) {
        super.setupContentView(contentView);
        contentView.setOnScrollListener(this);
        mView = contentView;
    }

    @Override
    public ListView createContentView(Context context, AttributeSet attrs) {
        return new ListView(context, attrs);
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
