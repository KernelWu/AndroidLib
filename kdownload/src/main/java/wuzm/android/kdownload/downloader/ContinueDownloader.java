package wuzm.android.kdownload.downloader;

import android.os.Debug;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.util.Observable;

import wuzm.android.kdownload.bean.BlockImfBean;
import wuzm.android.kdownload.bean.DownloadBlockBean;
import wuzm.android.kdownload.debugUtils.LogUtil;


public class ContinueDownloader extends Observable implements Runnable{
	
	private static final String TAG = ContinueDownloader.class.getSimpleName();
	private DownloadBlockBean bean;
	private boolean alive = false;
	
	private boolean downloading = false;
	
	public ContinueDownloader(DownloadBlockBean bean) {
		this.bean = bean;
	}
	
	@Override
	public void run() {
		setAlive(true);
		downloading();
	}
	
	public boolean isDownloading() {
		return downloading;
	}
	
	private void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	public String getId() {
		return bean.getId();
	}
	
	public String getTaskUrl() {
		return bean.getUrl();
	}
	
	public long getBlockSize() {
		return bean.getBlockSize();
	}
	
	public long getTaskSize() {
		return bean.getTaskSize();
	}
	
	public long getOffsetInBlock() {
		return bean.getCurPos();
	}
	
	public long getDownloadedSize() {
		return bean.getDownloadedSize();
	}

	public long getStartPosInTask() {
		return bean.getStartPos();
	}
	
	public String getFilePath() {
		return bean.getFilePath();
	}
	
	public void resume() {
		setAlive(true);
	}
	
	public void pause() {
		setAlive(false);
	}
	
	private void downloading() {
		
		long starTime = Debug.threadCpuTimeNanos();
		
		BlockImfBean imf = new BlockImfBean();
		imf.id = bean.getId();
		imf.offset = bean.getCurPos();
		imf.downloadSize = bean.getDownloadedSize();
		
		HttpClient client = null;
		try {
			client = new DefaultHttpClient();
			HttpGet get = new HttpGet();
			get.setURI(new URI(bean.getUrl()));
			get.addHeader("Connection", "Keep-Alive");
			get.addHeader("Charset", "utf-8");
			get.addHeader("Content-Type", "application/ocet-stream");
			get.addHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			get.addHeader("Range", "bytes=" + (bean.getStartPos() + bean.getCurPos()) + "-" +
			                        (bean.getStartPos() + bean.getBlockSize() - 1));
			HttpResponse response = client.execute(get);
			if(response.getStatusLine().getStatusCode() != 206 ) {
				//connect failed
				Log.e("ContinueDownloader","http response code->" + response.getStatusLine().getStatusCode());
				imf.state = BlockImfBean.STATE_CONNECT_FAILD;
				notifyObservers(imf);
				return;
			}
			 
			File tmpFile = new File(bean.getFilePath());
			if( !tmpFile.exists() && ! tmpFile.createNewFile()) {
				//create file failed
				imf.state = BlockImfBean.STATE_CREATE_TMP_FILE_FAILD;
				notifyObservers(imf);
				Log.e("ContinueDownloader","create file faild");
				return;
			}
			imf.state = BlockImfBean.STATE_DOWNLOADING;
			RandomAccessFile accessFile = new RandomAccessFile(tmpFile, "rw");
			accessFile.seek(bean.getCurPos());
			InputStream is = response.getEntity().getContent();
			byte[] buffer = new byte[ 10 * 1024];
			int readSize = 0; 
			
			downloading = true;
			final int cycleTime = 10;  //每个读取周期的次数
			int i = 0;   //当前周期读取网络流的次数,每cycleTime次为一个周期
			int tmpIncrease = 0;
			while(alive && (readSize = is.read(buffer)) > 0  ) {
				
				accessFile.write(buffer, 0, readSize);
				tmpIncrease += readSize;
				i ++;
				if( i == cycleTime) {
					imf.increaceSize = tmpIncrease;
					imf.offset += tmpIncrease;
					imf.downloadSize += tmpIncrease;
					this.setChanged();
					notifyObservers(imf);
					this.clearChanged();
					i = 0;
					tmpIncrease = 0;
				}
			}
			if(alive && i != 0) {
				//读取周期没有读取完整周期次数
				LogUtil.d(TAG, "block->" + bean.getId());
				LogUtil.d(TAG, "block downloaded size->" + (bean.getDownloadedSize() + tmpIncrease) );
				LogUtil.d(TAG, "block size->" + bean.getBlockSize());
				//notifyProgressChanged
				imf.increaceSize = tmpIncrease;
				imf.offset += tmpIncrease;
				imf.downloadSize += tmpIncrease;
				if(imf.downloadSize == bean.getBlockSize()) {
					imf.state = BlockImfBean.STATE_DOWNLOAD_OVER;
				}
				this.setChanged();
				notifyObservers(imf);
				this.clearChanged();
				i = 0;
				tmpIncrease = 0;
			}
			downloading = false;
			
			is.close();
			accessFile.close();
			
			LogUtil.d(TAG,"download->" + bean.getId() + "\n" + "consume time->" + (Debug.threadCpuTimeNanos() - starTime) );
		} catch (Exception e) {
			
			e.printStackTrace();
			downloading = false;
			imf.state = BlockImfBean.STATE_OTHER_ERROR;
			setChanged();
			notifyObservers(imf);
			clearChanged();
		}
	}
}
