package wuzm.android.kframe.utils;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

public class IntentUtils {

	/**
	 * 以文本形式发送来分享内容，支持文本和图片以及链接
	 * **/
	public static void doShare(Context mContext, String info, String picPath,
			String linkUrl) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		intent.putExtra(Intent.EXTRA_TEXT, info + linkUrl);
		if (!TextUtils.isEmpty(picPath) && FileUtils.isFileExist(picPath)) {
			Uri uri = Uri.parse("file:///" + picPath);
			intent.putExtra(Intent.EXTRA_STREAM, uri);
		}
		intent.putExtra("sms_body", info + linkUrl);

		intent.setType("image/*");
		intent.setType("text/plain");
		try {
			mContext.startActivity(Intent.createChooser(intent, "分享"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查看链接
	 */
	public static void ViewUrl(Context context, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setData(Uri.parse(url));
		context.startActivity(intent);
	}

	/**
	 * 拨打电话，弹出对话框确认
	 */
	public static void CallWithShowDialog(final Context context,
			final String number) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("拨打电话 " + number + " ？");
		DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setData(Uri.parse("tel:" + number));
				context.startActivity(intent);
			}
		};
		builder.setPositiveButton("确定", l);
		builder.setNegativeButton("取消", null);
		builder.create().show();
	}

	/**
	 * 发送短信
	 * 
	 * @param msg
	 *            短信内容
	 */
	public static void sendSMS(Context context, String number, String body) {
		try {
			String head = "smsto:";
			if (number.contains(";")) {
				head = "smsto:";
			}
			Uri smsToUri = Uri.parse(head + number);
			Intent mIntent = new Intent(Intent.ACTION_SENDTO,
					smsToUri);
			mIntent.putExtra("sms_body", body);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// Intent mIntent = new Intent(Intent.ACTION_VIEW);
			// mIntent.putExtra("address", number);
			// mIntent.setType("vnd.android-dir/mms-sms");
			context.startActivity(mIntent);
		} catch (Exception e) {
//			BaseApplication.showToast("");
		}
	}

	/**
	 * 拨号
	 */
	public static void call(Context context, String number) {
		try {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.CALL");
			intent.setData(Uri.parse("tel:" + number));
			context.startActivity(intent);
		} catch (Exception e) {
//			BaseApplication.showToast("拨号失败");
		}
	}

	/**
	 * 发送邮件
	 */
	public static void sendEmial(Context context, String emial) {
		Intent mEmailIntent = new Intent(Intent.ACTION_SEND);
		mEmailIntent.setType("plain/text");
		String[] strEmailReceive = new String[] { emial };
		mEmailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mEmailIntent.putExtra(Intent.EXTRA_EMAIL,
				strEmailReceive);
		mEmailIntent.putExtra(Intent.EXTRA_CC, "");
		mEmailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
		mEmailIntent.putExtra(Intent.EXTRA_TEXT, "");
		context.startActivity(Intent.createChooser(mEmailIntent, "Send Email"));
	}

	public static boolean checkActivityRunning(Context context, Class<?> name) {
		Intent mainIntent = new Intent(context, name);
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> appTask = am.getRunningTasks(1);
		if (appTask.size() > 0
				&& appTask.get(0).baseActivity
						.equals(mainIntent.getComponent())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据包名检测app是否安装
	 */
	public static boolean checkAppInstalled(String packagename, Context mContext) {
		PackageInfo packageInfo;
		try {
			packageInfo = mContext.getPackageManager().getPackageInfo(
					packagename, 0);
			Log.v("CheckAppInstalled","packageInfo------>" + packageInfo);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) {
			Log.e("isInstalledApp", "not installed");
			return false;
		} else {
			Log.e("isInstalledApp", "is installed");
			return true;
		}
	}

	/**
	 * 根据包名得到app版本号
	 */
	public static int getAppInstalledVersionCode(String packagename,
			Context mContext) {
		PackageInfo packageInfo;
		try {
			packageInfo = mContext.getPackageManager().getPackageInfo(
					packagename, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) {
			Log.e("isInstalledApp", "not installed");
		} else {
			Log.e("isInstalledApp", "is installed ,versionCode is  "
					+ packageInfo.versionCode);
			return packageInfo.versionCode;
		}
		return -1;
	}

	/**
	 * 根据app包名打开对应程序
	 */
	public static void openAppByPacagename(Context mContext, String packagename) {
		PackageManager packageManager = mContext.getPackageManager();
		Intent intent = new Intent();
		intent = packageManager.getLaunchIntentForPackage(packagename);
		if (intent != null) {
			mContext.startActivity(intent);
			return;
		}

		intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setPackage(packagename);
		PackageManager pManager = mContext.getPackageManager();
		List<ResolveInfo> apps = pManager.queryIntentActivities(intent, 0);
		try {
			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				packagename = ri.activityInfo.packageName;
				String className = ri.activityInfo.name;
				intent = new Intent(Intent.ACTION_MAIN);
				ComponentName cn = new ComponentName(packagename, className);
				intent.setComponent(cn);
				mContext.startActivity(intent);
			}else{
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void goSysSettings(Context context) {
		Intent settingIntent = new Intent(Settings.ACTION_SETTINGS);
		settingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(settingIntent);
	}
	
	/**
	 * pick file by system file picker 
	 * @return if system file picker exist return true,or false
	 */
	public static boolean pickFile(Context context) {
		Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
		pickIntent.setType("*/*");
		pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			context.startActivity(pickIntent);
		}catch(android.content.ActivityNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean openUrlWithBrowser(Context context,String url) {
		try {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
			browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(browserIntent);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	/**
	 * 
	 * @param activity     
	 * @param targetUri           uri that photo storage
	 * @param requestCode
	 */
	public static void capturePhoto(Activity activity,Uri targetUri,int requestCode) {
		Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri);
		activity.startActivityForResult(captureIntent, requestCode);
	}
	
	/**
	 * you can get the uri that photo by the third parament in OnActivityResult.
	 * <ul>
	 * <li><strong>For Example</strong></li>
	 * the third parament is name data , then invake data.getData.
	 * </ul>
	 * @param activity
	 * @param requestCode             
	 */
	public static void openPhotoAlbum(Activity activity,int requestCode) {
		Intent getAlbum = new Intent(Intent.ACTION_PICK);
		getAlbum.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		activity.startActivityForResult(getAlbum, requestCode);
	}
	
	
	/**
	 * crop photo , the photo that crop will by storage in targetUri
	 * @param activity
	 * @param sourceUri
	 * @param targetUri         the uri that storage the photo that crop 
	 * @param requestCode
	 */
	public static void cropPhoto(Activity activity,Uri sourceUri,Uri targetUri,int requestCode) {
		Intent cropIntent = new Intent("com.android.camera.action.CROP");  
		cropIntent.setDataAndType(sourceUri, "image/*");
		cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri);
//		cropIntent.putExtra("crop", "true");  
//		cropIntent.putExtra("circleCrop", "true");
		cropIntent.putExtra("aspectX", 1);  
		cropIntent.putExtra("aspectY", 1);  
//		cropIntent.putExtra("outputX", width);  
//		cropIntent.putExtra("outputY", height);  
		cropIntent.putExtra("noFaceDetection", true);  //不支持人脸识别
		cropIntent.putExtra("return-data", false);                                  
		activity.startActivityForResult(cropIntent, requestCode); 
	}
	
	
	/**
	 * crop photo with a certain aspect radio, the photo that crop will by storage in targetUri
	 * <ul>
	 * <strong>Note: </strong> the aspect radio is w / h
	 * </ul> 
	 * @param activity
	 * @param sourceUri
	 * @param targetUri
	 * @param w                 
	 * @param h
	 * @param requestCode
	 */
	public static void cropPhoto(Activity activity,Uri sourceUri,Uri targetUri,int w,int h,int requestCode) {
		Intent cropIntent = new Intent("com.android.camera.action.CROP");  
		cropIntent.setDataAndType(sourceUri, "image/*");
		cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri);
		cropIntent.putExtra("aspectX", w);  
		cropIntent.putExtra("aspectY", h);  
		cropIntent.putExtra("noFaceDetection", true);  //不支持人脸识别
		cropIntent.putExtra("return-data", false);                                  
		activity.startActivityForResult(cropIntent, requestCode); 
	}
	
}
