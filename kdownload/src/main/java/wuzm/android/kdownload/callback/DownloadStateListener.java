package wuzm.android.kdownload.callback;

public interface DownloadStateListener {

	public void onStart(String url);
	public void onResume(String url);
	public void onPause(String url);
	public void onStop(String url);
	
	public void onWaitting(String url);
	
	public void onComplete(String url);
	public void onFaild(String url);
}
