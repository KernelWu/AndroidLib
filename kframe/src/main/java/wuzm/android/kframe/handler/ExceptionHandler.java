package wuzm.android.kframe.handler;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import wuzm.android.kframe.email.EmailSendAsyTask;
import wuzm.android.kframe.email.EmailSendCallback;
import wuzm.android.kframe.email.EmailSender;
import wuzm.android.kframe.email.EmailSenderConfig;
import wuzm.android.kframe.utils.MySharePreferences;


/**
 * 提供捕捉异常的处理机制
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 *
 */
public class ExceptionHandler implements UncaughtExceptionHandler{
	private static final String TAG = ExceptionHandler.class.getSimpleName();
	private Context mContext;
	private String errorLogDir;
	
	private long durationOutOfDate = 1000 * 60 * 60 * 24 * 30; //默认每个log文件最长存放时间为30天
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:MM:ss");
	
	private boolean autoSendErrorLog = false;
	
	public static final int SEND_LOG_BY_EMAIL = 0x55;
	public static final int SEND_LOG_BY_NET = 0x56;
	
	private int sendErrorLogWay = SEND_LOG_BY_EMAIL;
	
	private static final UncaughtExceptionHandler defaultUncanghtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
	
	public ExceptionHandler(Context context,String errorLogDir) {
		mContext = context;
		this.errorLogDir = errorLogDir;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				if(autoSendErrorLog) {
					
					MySharePreferences mSp = new MySharePreferences(mContext);
					
					try {
						File logF = new File(ExceptionHandler.this.errorLogDir);
						if( ! logF.exists()) {
							return;
						}
						switch (sendErrorLogWay) {
						
						case SEND_LOG_BY_EMAIL:
							senErrorLogByEmail(logF);
							break;
						case SEND_LOG_BY_NET:
							sendErrorLogToServer(logF);
							break;

						default:
							break;
						}
						
					}catch(NullPointerException e) {
						e.printStackTrace();
						return;
					}
				}
				
				deleteLogOutOfDate();
			}
		}).start();
	}
	

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Date curDate = new Date();
		File logDir = new File(errorLogDir);
		if( !logDir.isDirectory()) {
			logDir.mkdirs();
		}
		File logF = new File(errorLogDir + "/" + curDate.getTime() + ".log");
		
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		ex.printStackTrace(pw);
		
		StringBuffer sb = new StringBuffer();
		sb.append("*****************" + sdf.format(curDate) + "*****************" + "\n");
		sb.append(getPhoneImf());
		sb.append(getApkImf());
		sb.append("*****************UnCaughtException*****************************" + "\n");
		sb.append(writer.toString());
		sb.append("*************************************************************" + "\n");
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(logF));
			bw.write(sb.toString());
			bw.flush();
			bw.close();
			
			saveNextSendLogFileName(logF);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		defaultUncanghtExceptionHandler.uncaughtException(thread, ex);
	}
	
	private StringBuffer getPhoneImf() {
		StringBuffer sb = new StringBuffer();
		sb.append("************Phone imformation**************" + "\n");
		sb.append("android system version:" + android.os.Build.VERSION.RELEASE);
		sb.append("\n");
		sb.append("phone model:" + android.os.Build.MODEL);
		sb.append("\n");
		return sb;
	}
	
	private StringBuffer getApkImf() {
		StringBuffer sb = new StringBuffer();
		sb.append("**************Apk imformation************************" + "\n");
		try {
			sb.append("version name:" + mContext.getPackageManager()
					.getPackageInfo(mContext.getPackageName(), 0).versionName );
			sb.append("\n");
			sb.append("version code:" + mContext.getPackageManager()
					.getPackageInfo(mContext.getPackageName(), 0).versionCode );
			sb.append("\n");
			sb.append("package name:" + mContext.getPackageName());
			sb.append("\n");
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return sb;
	}
	
	public void setDurationOutOfDate(long duration) {
		durationOutOfDate = duration;
	}
	
	private void deleteLogOutOfDate() {
		File logDir = new File(errorLogDir);
		if( !logDir.exists()) {
			return;
		}
		
		final long curDateL = new Date().getTime();
		
		logDir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				
				if(pathname.getName().endsWith(".log")) {
					return curDateL - pathname.lastModified() > durationOutOfDate && pathname.delete();
				}
				return false;
			}
		});
	}
	
	public void setAutoSendErrorLog(boolean enable) {
		autoSendErrorLog = enable;
	}
	
	public void setSendErrorLogWay(int way) {
		if(way != SEND_LOG_BY_EMAIL && way != SEND_LOG_BY_NET) {
			Log.e(TAG,"setSendErrorLogWay the argument must be SEND_LOG_BY_EMAIL or SEND_LOG_BY_NET");
		}else {
			sendErrorLogWay = way;
		}
	}
	
	private void saveNextSendLogFileName(File logF) {
		MySharePreferences mSp = new MySharePreferences(mContext);
		mSp.putString("next_send_error_log_path", logF.getAbsolutePath());
	}
	
	/*auto send error log to admin's email by email*/
	private String emailHost;
	private String emailUsername;
	private String emailPassword;
	private String emailTitle;
	private String managerEmail;
	
	public ExceptionHandler setEmailHost(String host) {
		this.emailHost = host;
		return this;
	}
	
	public ExceptionHandler setEmailUsername(String username) {
		this.emailUsername = username;
		return this;
	}
	
	public ExceptionHandler setEmailPassword(String password) {
		this.emailPassword = password;
		return this;
	}
	
	public ExceptionHandler setEmailTitle(String title) {
		this.emailTitle = title;
		return this;
	}
	
	public ExceptionHandler setManagerEmail(String email) {
		this.managerEmail = email;
		return this;
	}
	
	public void senErrorLogByEmail(File logF) {
		if(TextUtils.isEmpty(emailHost) || TextUtils.isEmpty(emailUsername) 
				|| TextUtils.isEmpty(emailPassword)
				|| TextUtils.isEmpty(managerEmail)) {
			Log.d(TAG,"please set the email config paraments");
			return ;
		}
		
		if(TextUtils.isEmpty(emailTitle)) {
			try {
				emailTitle = mContext.getResources().getString(mContext.getApplicationInfo().labelRes)
						+ mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		EmailSenderConfig config = new EmailSenderConfig();
		config.setServerHost(emailHost)
		      .setUserName(emailUsername)
		      .setPassword(emailPassword);
		final EmailSender sender = EmailSender.create(config);
		sender.init();
		sender.setFromAddress(emailUsername);
		final ArrayList<File> attacheFiles = new ArrayList<File>();
		attacheFiles.add(logF);
		
		EmailSendAsyTask task = new EmailSendAsyTask();
		task.setEmailSendCallback(new EmailSendCallback() {
			
			@Override
			public void startSend() {
				
			}
			
			@Override
			public void onSendSuccess() {
				MySharePreferences mSp = new MySharePreferences(mContext);
				mSp.putString("next_send_error_log_path", null);;
			}
			
			@Override
			public void onSendFaild() {
				
			}
			
			@Override
			public void onSendCancel() {
				
			}
			
			@Override
			public void onSend() {
				sender.sendAttachment(managerEmail, emailTitle, attacheFiles);
			}
		});
		task.start();
	}
	
	/*auto send error log to server by net*/
	public void sendErrorLogToServer(File logF) {
		
	}

}
