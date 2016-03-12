package wuzm.android.kframe.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.support.v4.app.Fragment;

import wuzm.android.kframe.widget.AbstractLoadingView;
import wuzm.android.kframe.widget.DefaultLoadingDialog;


/***
 * 增强型Fragment
 * @author wuzm
 * @version 1.0beta
 */
public class AdvancedFragment extends Fragment {
	AdvancedFragmentActivity mActivity;
	View mView;
	LayoutInflater mInflater;
	int mLayoutResID;
	/** 加载数据的进度条*/
	private AbstractLoadingView mLoadingView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (AdvancedFragmentActivity) activity;
		mLoadingView = new DefaultLoadingDialog(mActivity);
	}

	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initCreate(savedInstanceState);
		initConfig(savedInstanceState);
	}
	
	public void initCreate(Bundle savedInstanceState) {
	}
	
	public void initConfig(Bundle savedInstanceState) {
	}
	

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		initViews();
		initData();
		return mView;
	}
	
	public void initViews() {
	}
	
	public void setContentView(int layoutResID) {
		mLayoutResID = layoutResID;
		mView = mInflater.inflate(layoutResID, null);
	}
	
	public View findViewById(int resId) {
		return mView.findViewById(resId);
	}
	
    public void initData() {
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
    
    /***
     * 替换 {@link #getActivity()}
     * @return
     */
    public AdvancedFragmentActivity getFragmentActivity() {
    	return mActivity;
    }
    
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
    	getActivity().startActivityForResult(intent, requestCode);
    }

	/**
     * 
     * @return 返回true代表有自己控制返回事件，返回false交给activity控制返回事件,默认返回false
     */
    public boolean onBackPressed() {
    	return false;
    }
    
}
