package wuzm.android.kdownload.callback;

public interface DownloadProgressListener {
	
	/**
	 * 
	 * @param id the download's task block id
	 * @param url the download's task url
	 * @param state @see class BlockImfBean state
	 * @param downloadedSize 
	 * @param taskSize
	 */
	public void onDownloadProgress(String id, String url, int state, long downloadedSize, long taskSize);
	
	/**
	 * 
	 * @param id the download's task block id
	 * @param url the download's task url
	 * @param state @see class BlockImfBean state
	 * @param progress from 0 to 100 percent
	 */
	public void onDownloadProgress(String id, String url, int state, float progress);
}
