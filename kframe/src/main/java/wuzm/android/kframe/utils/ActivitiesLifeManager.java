package wuzm.android.kframe.utils;

import android.app.Activity;

import java.util.Stack;

/**
 * 管理activity的退出工具
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 * @see 
 * <ul>
 * <strong>使用帮助:</strong>
 * </ul>
 * <ul>
 * <li>在每个Activity的onCreate中调用{@link #push(Activity)},在每个Activity的finish()调用{@link #remove(Activity)}</li>
 * <li>在退出整个应用的时候调用{@link #exitApp()}</li>
 * </ul>
 */
public class ActivitiesLifeManager {

	private static ActivitiesLifeManager mInstance;
	private static Stack<Activity> activitiesStack = new Stack<Activity>();
	
	private ActivitiesLifeManager() {
	}
	
	public static ActivitiesLifeManager getInstance() {
		
		if (mInstance == null) {
			mInstance = new ActivitiesLifeManager();
		}
		return mInstance;
	}
	
	public void push(Activity activity) {
		for(int i = 0 , len = activitiesStack.size() ; i < len ; i ++ ) {
			if(activitiesStack.get(i) == activity) {
				removeAll();
				break;
			}
		}
		activitiesStack.push(activity);
	}
	
	public boolean remove(Activity activity) {
		for(int i = 0 , len = activitiesStack.size() ; i < len ; i ++ ) {
			if(activitiesStack.get(i) == activity) {
				activitiesStack.remove(activity);
				return true;
			}
		}
		return false;
	}
	
	public void removeAll() {
		while(activitiesStack.size() > 0) {
			activitiesStack.pop().finish();
		}
	}
	
	public void exitApp() {
		removeAll();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
