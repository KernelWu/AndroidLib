package wuzm.android.kframe.widget.refresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListAdapter;

/**
 * Created by kernel on 15/4/13.
 * Email: 372786297@qq.com
 * version: 0.1beta
 */
public abstract class AbsRefreshAdapterView<T extends AbsListView> extends AbsRefreshLayout<T>{
    private static final String TAG = AbsRefreshAdapterView.class.getSimpleName();

    protected AbsRefreshAdapterView(Context context) {
        super(context);
    }

    protected AbsRefreshAdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setupContentView(T contentView) {
        contentView.setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    public boolean isBottom() {
        if(getContentView() == null || getContentView().getAdapter() == null) {
            return false;
        }
        return getContentView().getLastVisiblePosition() == getContentView().getAdapter().getCount() - 1;
    }

    @Override
    public boolean isTop() {
        if(getContentView() == null) {
            return false;
        }
        boolean isTop = getContentView().getFirstVisiblePosition() == 0;
        if(getContentView().getChildCount() != 0) {
            isTop = isTop && getContentView().getChildAt(0).getTop() == 0;
        }
        return isTop;
    }

    @TargetApi(11)
    public void setAdapter(ListAdapter adapter) {
        getContentView().setAdapter(adapter);
    }

    public ListAdapter getAdapter() {
        return getContentView().getAdapter();
    }
}
