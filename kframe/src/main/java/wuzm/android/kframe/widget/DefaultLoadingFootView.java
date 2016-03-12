package wuzm.android.kframe.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/***
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2015/1/16
 */
public class DefaultLoadingFootView extends AbstractLoadingFootView {
	private ProgressBar footProgressBar;
	private TextView footContentTv;
	private float footTextSize = DEFAULT_FOOT_TEXTSIZE;
	private static final float DEFAULT_FOOT_TEXTSIZE = 20f;

	public DefaultLoadingFootView(Context context) {
		super(context);
	}

	@Override
	public void init(Context context) {
		footProgressBar = new ProgressBar(context);
		LayoutParams rlp = new LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(footProgressBar, rlp);
		
		footContentTv = new TextView(context);
		footContentTv.setGravity(Gravity.CENTER);
		footContentTv.setTextColor(Color.BLACK);
		footContentTv.setTextSize(footTextSize);
		footContentTv.setText("点击加载更多");
		
		rlp = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(footContentTv, rlp);
		setBackgroundColor(Color.WHITE);
		setPadding(30, 20, 30, 20);
		
//		footContentTv.setVisibility(View.GONE);
//		footProgressBar.setVisibility(View.GONE);
	}

	@Override
	public void onLoadingStart() {
		super.onLoadingStart();
		footProgressBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void onLoadingStop() {
		super.onLoadingStop();
		footProgressBar.setVisibility(View.GONE);
	}

	@Override
	public void onAutoLoading() {
		super.onAutoLoading();
		footContentTv.setVisibility(View.GONE);
		footProgressBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClickLoading(boolean hasMore, boolean enable) {
		super.onClickLoading(hasMore, enable);
		footProgressBar.setVisibility(View.GONE);
		if (enable) {
			footContentTv.setVisibility(View.VISIBLE);
		} else {
			footContentTv.setVisibility(View.GONE);
		}
	}
	
}
