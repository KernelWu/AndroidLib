package wuzm.android.kframe.widget;

import android.view.View;
import android.view.ViewGroup.LayoutParams;

/***
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2015/1/16
 */
public interface AbstractLoadingView {

	public void showLoading();
	public void hideLoading();
	
	public boolean onLoadingStart();
	public boolean onLoadingStop();
	public boolean onLoadingCancel();
	public void onLoadingShow();
	public void onLoadingHide();
	public void onLoadingDismiss();
	
	public void setLoadingView(int layoutResID);
	public void setLoadingView(View view);
	public void setLoadingView(View view, LayoutParams params);
}
