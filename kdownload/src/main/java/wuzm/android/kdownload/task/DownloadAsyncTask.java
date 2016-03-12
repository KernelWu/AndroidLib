package wuzm.android.kdownload.task;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import wuzm.android.kdownload.bean.BlockImfBean;
import wuzm.android.kdownload.bean.DownloadBlockBean;
import wuzm.android.kdownload.bean.TaskBean;
import wuzm.android.kdownload.callback.DownloadProgressListener;
import wuzm.android.kdownload.callback.DownloadStateListener;
import wuzm.android.kdownload.dao.ContinueDAO;
import wuzm.android.kdownload.dao.RecordValue;
import wuzm.android.kdownload.debugUtils.LogUtil;
import wuzm.android.kdownload.downloader.ContinueDownloader;

public class DownloadAsyncTask extends Observable implements Observer{
	
	private static final String TAG = DownloadAsyncTask.class.getSimpleName();

	private static  int CORE_POOL_SIZE = 4;
	private static  int MAXIMUN_POOL_SIZE = 8;
	private static final int KEEP_ALIVE_TIME = 1;  //second
	private ContinueDAO dao;
	private TaskBean bean;
	private ArrayList<ContinueDownloader> downloaders;
	private static BlockingQueue<Runnable> poolWorkQueue = new LinkedBlockingQueue<Runnable>();
	private static ThreadFactory threadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);
		
		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "DownloadThread #"+ mCount.getAndIncrement());
		}
	};
	private static Executor THREAD_POOL_EXECUTOR = 
			new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUN_POOL_SIZE, KEEP_ALIVE_TIME,
					TimeUnit.SECONDS,poolWorkQueue, threadFactory);
	
	private DownloadProgressListener mProgressListener;
	private DownloadStateListener mDownloadStateListener;
	
	public static final int STATE_DOWNLOADING = 1;
	public static final int STATE_PAUSE = 2;
	public static final int STATE_DONE = 0;
	public static final int STATE_ERROR = -1;
	private int state;
	
	public DownloadAsyncTask(TaskBean bean,ContinueDAO dao) {
		this.bean = bean;
		this.dao = dao;
	}
	
	public TaskBean getTaskBean() {
		return this.bean;
	}
	
	public ContinueDAO getDao() {
		return this.dao;
	}
	
	public boolean isPause() {
		return state == STATE_PAUSE;
	}
	
	public boolean isDownloading() {
		return state == STATE_DOWNLOADING;
	}
	
	public boolean isError() {
		return state == STATE_ERROR;
	}
	
	public synchronized void start() {
		if(bean.getTaskSize() <= 0) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					long contentSize = 0;
					if( (contentSize = getContentSize()) > 0) {
						DownloadAsyncTask.this.bean.setTaskSize(contentSize);
						DownloadAsyncTask.this.bean.resolveBlock(contentSize);
						initBlock();
						startDownload();
					}
				}
			}).start();
		}else {
			initBlock();
			startDownload();
		}
	}
	
	public synchronized void remove() {
		
		for(int i = 0 , len = downloaders.size() ; i < len ;i++ ) {
			ContinueDownloader downloader = downloaders.get(i);
			downloader.pause();
			downloader.deleteObserver(this);
			synchronized (poolWorkQueue) {
				poolWorkQueue.remove(downloader);
			}
		}
		dellDbRecords();
		if(mDownloadStateListener != null) {
			mDownloadStateListener.onStop(bean.getUrl());
		}
		LogUtil.d(TAG, "task reomve success->" + bean.getUrl());
	}
	
	public synchronized void resume() {
		if( !isPause()) {
			LogUtil.i(TAG,"don't resume task again,task was resume->" + bean.getUrl());
			return;
		}
		state = STATE_DOWNLOADING;
		start();
		if(mDownloadStateListener != null) {
			mDownloadStateListener.onResume(bean.getUrl());
		}
		LogUtil.d(TAG, "task resume success->" + bean.getUrl());
	}
	
	public synchronized void pause() {
		if(isPause()) {
			LogUtil.i(TAG,"don't pause task again,task was pause->" + bean.getUrl());
			return;
		}
		state = STATE_PAUSE;
		for(int i = 0 , len = downloaders.size() ; i < len ; i ++ ) {
			ContinueDownloader downloader = downloaders.get(i);
			downloader.pause();
			synchronized (poolWorkQueue) {
				poolWorkQueue.remove(downloader);
			}
		}
		if(mDownloadStateListener != null) {
			mDownloadStateListener.onPause(bean.getUrl());
		}
		LogUtil.d(TAG, "task pause success->" + bean.getUrl());
	}
	
	/**
	 * 获取待下载文件大小
	 * @return 
	 */
	private long getContentSize() {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost();
			post.setURI(new URI(bean.getUrl()));
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			long contentLenth = entity.getContentLength();
			LogUtil.d(TAG, "task->" + bean.getUrl());
			LogUtil.d(TAG,"contentLenth->" + contentLenth + "") ;
			client.getConnectionManager().shutdown();
			return contentLenth;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private void initBlock() {
		
		downloaders = new ArrayList<ContinueDownloader>();
		ArrayList<RecordValue> recordValues = new ArrayList<RecordValue>();
		RecordValue recordValue = null;
		DownloadBlockBean block = null;
		ContinueDownloader downLoader = null;
		for(int i = 0 , len = bean.getBlockCount(); i < len ; i ++ ) {
			block = bean.getBlockBean(i);
			if(block.getDownloadedSize() == block.getBlockSize()) {
				continue;
			}
			downLoader = new ContinueDownloader(block);
			downLoader.addObserver(this);
			downloaders.add(downLoader);
			synchronized (poolWorkQueue) {
				poolWorkQueue.add(downloaders.get(i));
			}
			
			//database records 
			recordValue = new RecordValue();
			recordValue.id = block.getId();
			recordValue.url = block.getUrl();
			recordValue.fileSuffix = block.getFileSuffix();
			recordValue.filePath = block.getFilePath();
			recordValue.startPos = block.getStartPos();
			recordValue.curPos = block.getCurPos();
			recordValue.blockSize = block.getBlockSize();
			recordValue.downloadedSize = block.getDownloadedSize();
			recordValue.taskSize = block.getTaskSize();
			recordValues.add(recordValue);
		}
		if( ! dao.isExistTask(bean.getUrl())) {
			dao.addRecords(recordValues);
		}
	}
	
	private void startDownload() {
		for(int i = 0 , len = downloaders.size() ; i < len ;i++ ) {
			
			synchronized (poolWorkQueue) {
				poolWorkQueue.remove(downloaders.get(i));
			}
			THREAD_POOL_EXECUTOR.execute(downloaders.get(i));
			LogUtil.d(TAG,"exe downloader->" + downloaders.get(i).getId());
		}
	}
	
	@Override
	public void update(Observable observable, Object data) {
		try {
			BlockImfBean imf = (BlockImfBean) data;
			switch (imf.state) {
			case BlockImfBean.STATE_CONNECT_FAILD:
			case BlockImfBean.STATE_OTHER_ERROR:
			case BlockImfBean.STATE_CREATE_TMP_FILE_FAILD:
				state = STATE_ERROR;
				for(ContinueDownloader d : downloaders) {
					d.pause();
				}
				if (mProgressListener != null) {
					mProgressListener.onDownloadProgress(imf.id, bean.getUrl(),
							imf.state, bean.getDownloadedSize(),
							bean.getTaskSize());
				}
				if(mDownloadStateListener != null) {
					mDownloadStateListener.onFaild(bean.getUrl());
				}
				LogUtil.e(TAG, "downloading task error->" + bean.getUrl());
				return;
				
			case BlockImfBean.STATE_DOWNLOAD_OVER:
			case BlockImfBean.STATE_DOWNLOADING:
			default:
				break;
			}
			if(isPause()) {
				return;
			}
			if(isError()) {
				return;
			}
			if( imf.state == BlockImfBean.STATE_DOWNLOADING &&state != STATE_DOWNLOADING) {
				state = STATE_DOWNLOADING;
			}
			if (imf.increaceSize < 0) {
				return;
			}
			
			DownloadBlockBean blockBean = bean.getBlockBean(imf.id);
			blockBean.setCurPos(blockBean.getCurPos() + imf.increaceSize);
			blockBean.setDownloadedSize(blockBean.getDownloadedSize() + imf.increaceSize);
			bean.setDownloadedSize(bean.getDownloadedSize() + imf.increaceSize);
			
			if (mProgressListener != null) {
				mProgressListener
						.onDownloadProgress(imf.id, bean.getUrl(), imf.state,
								bean.getDownloadedSize(), bean.getTaskSize());
				mProgressListener
				        .onDownloadProgress(imf.id, bean.getUrl(), imf.state,
				        		100 * bean.getDownloadedSize() / bean.getTaskSize());
			}
			dao.writeDownloadedBytes(imf.id,imf.offset,imf.downloadSize);
			if(bean.getDownloadedSize() == bean.getTaskSize()) {
				state = STATE_DONE;
				if(packageFile()) {
					deleteTmpFiles();
					dellDbRecords();
				}
				if(mDownloadStateListener != null) {
					mDownloadStateListener.onComplete(bean.getUrl());
				}
				setChanged();
				notifyObservers(bean.getUrl()); //notify downloadmanager remove this task from downloading queue
				clearChanged();
				LogUtil.d(TAG, "complete downloading task->" + bean.getUrl());
			}else if(bean.getDownloadedSize() > bean.getTaskSize()) {
				LogUtil.e(TAG,"task url->" + bean.getUrl());
				LogUtil.e(TAG,"downloaded size is bigger than task size");
				LogUtil.e(TAG,"downloaded size->" + bean.getDownloadedSize());
				LogUtil.e(TAG,"task size->" + bean.getTaskSize());
				for(DownloadBlockBean b : bean.getBlockBeans()) {
					LogUtil.e(TAG,"block->" + b.getId());
					LogUtil.e(TAG,"block download->" + b.getDownloadedSize());
					LogUtil.e(TAG,"block size->" + b.getBlockSize());
				}
				if(mDownloadStateListener != null) {
					mDownloadStateListener.onFaild(bean.getUrl());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean packageFile() {
		
		LogUtil.d(TAG, "start package file->" + bean.getUrl());
		File f = new File(bean.getSavePath());
		try {
			f.createNewFile();
			long totalSize = 0;
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			for(int i = 0 , len = bean.getBlockCount(); i < len ; i ++ ) {
				raf.seek(totalSize);
				byte[] buffer = new byte[ 4 * 1024 * 1024];
				int readLenth = 0;
				InputStream is = new FileInputStream(new File(bean.getBlockBean(i).getFilePath()));
				while( (readLenth = is.read(buffer)) > 0) {
					raf.write(buffer, 0, readLenth);
					totalSize += readLenth;
				}
				is.close();
			}
			raf.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void deleteTmpFiles() {
		for(int i = 0 ,len = bean.getBlockCount() ; i < len ; i ++ ) {
			File tmpF = new File(bean.getBlockBean(i).getFilePath());
			if(tmpF.exists()) {
				tmpF.delete();
			}
		}
		LogUtil.d(TAG, "delete temp files");
	}
	
	private void dellDbRecords() {
		dao.deleteTask(bean.getUrl());
		LogUtil.d(TAG, "delete task record from database");
	}
	
	
	
	public  void setCoreThreadPoolSize(int count) {
		if(count <= 0) {
			throw new IllegalArgumentException("count must bigger than 0");
		}
		CORE_POOL_SIZE = count;
		MAXIMUN_POOL_SIZE  = count * 2;
	}
	
	public void setDownloadProgressListener(DownloadProgressListener l) {
		this.mProgressListener = l;
	}

	public void setDownloadStateListener(DownloadStateListener l) {
		this.mDownloadStateListener = l;
	}

}
