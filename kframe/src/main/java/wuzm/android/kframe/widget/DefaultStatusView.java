package wuzm.android.kframe.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DefaultStatusView extends AbstractStatusView {
	TextView noticeTv;

	public DefaultStatusView(Context context) {
		super(context);
	}

	@Override
	public void init() {
		noticeTv = new TextView(getContext());
        noticeTv.setTextColor(Color.BLACK);
        noticeTv.setTextSize(20);
        noticeTv.setGravity(Gravity.CENTER);
        noticeTv.setBackgroundColor(Color.WHITE);
        
        RelativeLayout.LayoutParams rl = new LayoutParams(LayoutParams.WRAP_CONTENT,
        		LayoutParams.WRAP_CONTENT);
        rl.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(noticeTv, rl);
        setBackgroundColor(Color.WHITE);
	}

	@Override
	public void onNormal() {
		setVisibility(View.GONE);
		noticeTv.setText("");
	}

	@Override
	public void onNoData() {
		setVisibility(View.VISIBLE);
		noticeTv.setText("没有数据");
	}

	@Override
	public void onNetError() {
		setVisibility(View.VISIBLE);
		noticeTv.setText("网络异常");
	}

	@Override
	public void onTimeout() {
		setVisibility(View.VISIBLE);
		noticeTv.setText("请求超时");
	}

	@Override
	public void onDataAbnormaly() {
		setVisibility(View.VISIBLE);
		noticeTv.setText("数据异常");
	}

}
