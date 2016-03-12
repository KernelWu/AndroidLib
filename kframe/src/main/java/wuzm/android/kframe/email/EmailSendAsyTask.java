package wuzm.android.kframe.email;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

/**
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 *
 */
public class EmailSendAsyTask extends AsyncTask<Void, Void, Boolean>{
	private EmailSendCallback mSendCallback;
	
	public void setEmailSendCallback(EmailSendCallback callback) {
		mSendCallback = callback;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(mSendCallback != null) {
			mSendCallback.startSend();
		}
	}
	
	public void start() {
		this.execute();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			if(mSendCallback != null) {
				mSendCallback.onSend();
			}
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if(mSendCallback != null) {
			if(result) {
				mSendCallback.onSendSuccess();
			}else {
				mSendCallback.onSendFaild();
			}
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCancelled(Boolean result) {
		super.onCancelled(result);
		if(mSendCallback != null) {
			mSendCallback.onSendCancel();
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if(mSendCallback != null) {
			mSendCallback.onSendCancel();
		}
	}

}
