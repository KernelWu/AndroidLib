package wuzm.android.kframe.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import wuzm.android.kframe.R;


/***
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2015/1/16
 */
public class DefaultLoadingDialog extends Dialog
    implements AbstractLoadingView {

	public DefaultLoadingDialog(Context context, int theme) {
		super(context, theme);
        setLoadingView(R.layout.dlg_loading_default);
        setCanceledOnTouchOutside(false);
	}

	public DefaultLoadingDialog(Context context) {
		this(context, R.style.float_dialog_style);
	}

	@Override
	public void showLoading() {
		show();
	}

	@Override
	public void hideLoading() {
		dismiss();
	}

	@Override
	public boolean onLoadingStart() {
		return false;
	}

	@Override
	public boolean onLoadingStop() {
		return false;
	}

	@Override
	public boolean onLoadingCancel() {
		return false;
	}

	@Override
	public void onLoadingShow() {
		
	}

	@Override
	public void onLoadingHide() {
		
	}

	@Override
	public void onLoadingDismiss() {
	}

	@Override
	protected void onStart() {
		super.onStart();
		onLoadingStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		onLoadingStop();
	}

	@Override
	public void cancel() {
		super.cancel();
		onLoadingCancel();
	}
	
	@Override
	public void hide() {
		super.hide();
		onLoadingHide();
	}

	@Override
	public void dismiss() {
		super.dismiss();
		onLoadingDismiss();
	}

	@Override
	public void setLoadingView(int layoutResID) {
        setContentView(layoutResID);
	}

	@Override
	public void setLoadingView(View view) {
        setContentView(view);
	}

	@Override
	public void setLoadingView(View view, LayoutParams params) {
        setContentView(view, params);
	}

	@Deprecated
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
	}

	@Deprecated
	@Override
	public void setContentView(View view) {
		super.setContentView(view);
	}

	@Deprecated
	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
	}
	
}
