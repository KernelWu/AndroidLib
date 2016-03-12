package wuzm.android.kframe.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 监听APK包的变化,包括安装,卸载,替换
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 */
public abstract class PackageChangeBrocastReceiver extends BroadcastReceiver{
	

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(Intent.ACTION_PACKAGE_ADDED.equals(action)) {
			onInstall(intent.getDataString().substring(intent.getDataString().indexOf(":")));
		}else if(Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
			onUnInstall(intent.getDataString().substring(intent.getDataString().indexOf(":")));
		}else if(Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
			onReplace(intent.getDataString().substring(intent.getDataString().indexOf(":")));
		}
	}
	
	
	public abstract void onInstall(String packageName);
	
	public abstract void onUnInstall(String packageName);
	
	//版本更新,替换新的安装包
	public abstract void onReplace(String packageName);
	

}
