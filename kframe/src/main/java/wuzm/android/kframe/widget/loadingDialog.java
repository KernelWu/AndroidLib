package wuzm.android.kframe.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;

import wuzm.android.kframe.R;


/**
 * a simple loadingprogress dialog 
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 * @see
 * <ul>
 * <strong>construction</strong>
 * <li><{@link #loadingDialog(Context)} default construction </li>
 * <li><{@link #loadingDialog(Context, int)} custom construction, the second parament set the loading animation_list</li>
 * </ul>
 */
public class loadingDialog extends Dialog {
	private Context mContext;
	LinearLayout ll;
	ProgressBar pb;
	private OnCanceLoadinglLister cancelListener;

	public loadingDialog(Context context) {
		super(context, R.style.float_dialog_style);
		mContext = context;
		initView(-1); 
		setCanceledOnTouchOutside(false);
	}
	
	public loadingDialog(Context context,int drawableResId) {
		super(context,R.style.float_dialog_style);
		mContext = context;
		initView(drawableResId); 
		setCanceledOnTouchOutside(false);
	}
	
	private void initView(int resId) {
		ll = new LinearLayout(mContext);
		ll.setBackgroundResource(R.drawable.loading_bg);
		pb = new ProgressBar(mContext);
//		pb.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.loading_gray));
		LayoutParams llp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		llp.gravity = Gravity.CENTER;
		ll.addView(pb, llp);
		setContentView(ll);
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
	}
	
	@Override
	public void cancel() {
		super.cancel();
		if(cancelListener != null) {
			cancelListener.onCancelLoading();
		}
	}

	public void setBackGround(int resId) {
		ll.setBackgroundResource(resId);
	}
	
	public void setLoadingAnimaList(int resId) {
		pb.setIndeterminateDrawable(mContext.getResources().getDrawable(resId));
	}
	
	
	public interface OnCanceLoadinglLister {
		public void onCancelLoading();
	}
	
	public void setOnCancelListener(OnCanceLoadinglLister l) {
		cancelListener = l;
	}
}
