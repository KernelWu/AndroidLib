package wuzm.android.kframe.widget;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wuzm.android.kframe.R;


/***
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2015/1/18
 */
public class DefaultRefreshHeadView extends AbstractRefreshHeadView {
	private Context mContext;
	
	private float headTextSize = DEFAULT_HEAD_TEXTSIZE;
	private static final float DEFAULT_HEAD_TEXTSIZE = 20f;
	
	private ImageView headArrowIv;
	private ImageView headRefreshIv;
	private TextView headContentTv;
	
	/*head arrow animations*/
	private RotateAnimation rotateAnimation;
	private RotateAnimation rotateResetAnimation;
	
	/*head refresh animation*/
	private RotateAnimation recycleRotateAnimation;

	public DefaultRefreshHeadView(Context context) {
		super(context);
	}

    public DefaultRefreshHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultRefreshHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
	public void init(Context context) {
		mContext = context;
		initAnim();
		initView();
	}
	
	private void initView() {
        RelativeLayout container = new RelativeLayout(mContext);

		headArrowIv = new ImageView(mContext);
		headArrowIv.setImageResource(R.drawable.down_arrow);
		LayoutParams rlp = new LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		rlp.addRule(RelativeLayout.CENTER_VERTICAL);
		container.addView(headArrowIv, rlp);
		
		headRefreshIv = new ImageView(mContext);
		headRefreshIv.setImageResource(R.drawable.refresh);
		headRefreshIv.setVisibility(View.INVISIBLE);
		container.addView(headRefreshIv, rlp);
		
		headContentTv = new TextView(mContext);
		headContentTv.setGravity(Gravity.CENTER);
		headContentTv.setTextColor(Color.BLACK);
		headContentTv.setTextSize(DEFAULT_HEAD_TEXTSIZE);
		headContentTv.setText("下拉刷新");
		rlp = new LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
		container.addView(headContentTv, rlp);
		container.setPadding(50, 50, 50, 50);

        RelativeLayout.LayoutParams containerRLP = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        containerRLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(container, containerRLP);
		this.setBackgroundColor(Color.WHITE);
	}
	
	private void initAnim() {
		rotateAnimation = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF,
				0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(150);
		rotateAnimation.setFillAfter(true);
		
		rotateResetAnimation = new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF,
				0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotateResetAnimation.setDuration(150);
		rotateResetAnimation.setFillAfter(true);
		
		recycleRotateAnimation =new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF,
				0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		recycleRotateAnimation.setDuration(1200);
		recycleRotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
		recycleRotateAnimation.setRepeatMode(RotateAnimation.RESTART);
	}
	
	@Override
	public void setTextSize(float size) {
		headTextSize = size;
		headContentTv.setTextSize(size);
	}

	@Override
	public void setTextSize(int resId) {
		headTextSize = mContext.getResources().getDimension(resId);
		headContentTv.setTextSize(headTextSize);
	}

	@Override
	public void noneRefresh() {
		headRefreshIv.clearAnimation();
		headRefreshIv.setVisibility(View.INVISIBLE);
		headArrowIv.setVisibility(View.VISIBLE);
		headContentTv.setText("下拉刷新");
	}

	@Override
	public void prepareRefresh() {
		headArrowIv.startAnimation(rotateResetAnimation);
		headContentTv.setText("下拉刷新");
	}

	@Override
	public void relaseToRefresh() {
		headArrowIv.startAnimation(rotateAnimation);
		headContentTv.setText("释放刷新");
	}

	@Override
	public void refreshing() {
		headContentTv.setText("刷新中...");
		headArrowIv.clearAnimation();
		headArrowIv.setVisibility(View.INVISIBLE);
		headRefreshIv.setVisibility(View.VISIBLE);
		headRefreshIv.startAnimation(recycleRotateAnimation);
	}
	
}
