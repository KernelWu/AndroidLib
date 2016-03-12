package wuzm.android.kframe.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import wuzm.android.kframe.widget.AbstractLoadingView;
import wuzm.android.kframe.widget.DefaultLoadingDialog;


/***
 * 增强型Activity
 * @author wuzm
 * @version 1.0beta
 */
public class AdvancedActivity extends Activity {
	/** 加载数据的进度条*/
	private AbstractLoadingView mLoadingView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLoadingView = new DefaultLoadingDialog(this);
		initCreate(savedInstanceState);
		initConfig(savedInstanceState);
		initViews();
		initData(savedInstanceState);
	}
	
	public void initCreate(Bundle savedInstanceState) {
	}
	
	public void initConfig(Bundle savedInstanceState) {
	}
	
	public void initViews() {
	}
	
	public void initData(Bundle savedInstanceState) {
	}
	
	public void setLoadingView(int layoutResID) {
		mLoadingView.setLoadingView(layoutResID);
	}
	
	public void setLoadingView(View view) {
		mLoadingView.setLoadingView(view);
	}
	
	public void setLoadingView(View view, LayoutParams lps) {
		mLoadingView.setLoadingView(view, lps);
	}
	
	public void showLoading() {
		mLoadingView.showLoading();
	}
	
	public void hideLoading() {
		mLoadingView.hideLoading();
	}
	
 }
