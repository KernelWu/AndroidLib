package wuzm.android.kframe.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import wuzm.android.kframe.widget.AbstractLoadingView;
import wuzm.android.kframe.widget.DefaultLoadingDialog;


/***
 * 增强型FragmentActivity
 * @author wuzm
 * @version 1.0beta
 */
public class AdvancedFragmentActivity extends FragmentActivity {
	private FragmentManager mFm;
	private int mFragmentContentId;
	private AdvancedFragment visibleFragment;
	
	/** 自定义的统一的fragment跳转动画*/
	private int customerEnterAnim;
	private int customerExitAnim;
	private int customerPopEnterAnim;
	private int customerPopExitAnim;
	/** 加载数据的进度条*/
	private AbstractLoadingView mLoadingView;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		mLoadingView = new DefaultLoadingDialog(this);
		mFm = getSupportFragmentManager();
		initCreate(bundle);
		initConfig(bundle);
		initViews();
		initData();
	}
	
	public void initCreate(Bundle bundle) {
	}
	
	public void initConfig(Bundle bundle) {
	}
	
	public void initViews() {
	}
	
	public final void setFragmentContentId(int fragmentContentId) {
		mFragmentContentId = fragmentContentId;
	}
	
	public void initData() {
	}
	
	/**
	 * 设置统一的fragment切换动画
	 * @param enter
	 * @param exit
	 * @param popEnter
	 * @param popExit
	 */
	public final void setCustomerAnim(int enter, int exit, int popEnter, int popExit) {
		this.customerEnterAnim = enter;
		this.customerExitAnim = exit;
		this.customerPopEnterAnim = popEnter;
		this.customerPopExitAnim = popExit;
	}
	
	public final synchronized void changeFragment(AdvancedFragment fragment) {
		changeFragment(fragment, fragment.getClass().getSimpleName());
	}
	
	/** 切换fragment*/
	public final synchronized void changeFragment(AdvancedFragment fragment, String tag) {
		if(fragment == null) {
			throw new NullPointerException("java.lang.NullPointerException");
		}
		if( !(fragment instanceof AdvancedFragment) ) {
			throw new IllegalArgumentException("java.lang.IllegalArgumentException");
		}
		FragmentTransaction mFt = mFm.beginTransaction();
		AdvancedFragment enterFragment = fragment;
		AdvancedFragment exitFragment = null;
		for(Fragment preF : mFm.getFragments()) {
			if(preF != null && preF.getId() == mFragmentContentId && preF.isVisible()) {
				exitFragment = (AdvancedFragment) preF;
				break;
			}
		}
		if(exitFragment != null && enterFragment == exitFragment) {
			return;
		}
		if( !customerFragmentChangeAnim(enterFragment, exitFragment, mFt)) {
			mFt.setCustomAnimations(customerEnterAnim, customerExitAnim,
					customerPopEnterAnim, customerPopExitAnim);
		}
		if( tag == null || tag.length() == 0) {
			tag = enterFragment.getClass().getSimpleName();
		}
		if(exitFragment != null) {
			mFt.hide(exitFragment);
		}
		if( mFm.findFragmentByTag(tag) != null) {
			mFt.show(enterFragment);
		}else {
			mFt.add(mFragmentContentId, enterFragment, tag);
		}
		visibleFragment = enterFragment;
		mFt.commitAllowingStateLoss();
	}
	
	/**
	 * 自定义正要切换的Fragment的切换动画
	 * @return  返回true,则使用在本方法中设置的切换动画,返回false则使用统一的切换动画（系统默认或者用户定义的）
	 */
	public boolean customerFragmentChangeAnim(AdvancedFragment enterFragment,
			AdvancedFragment exitFragment, FragmentTransaction ft) {
		return false;
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if(visibleFragment != null && visibleFragment.onBackPressed()) {
				return true;
			}else {
				return super.onKeyDown(keyCode, event);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
