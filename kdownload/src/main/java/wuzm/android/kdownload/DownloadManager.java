package wuzm.android.kdownload;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import wuzm.android.kdownload.bean.TaskBean;
import wuzm.android.kdownload.bean.factory.TaskBeanFactory;
import wuzm.android.kdownload.callback.DownloadProgressListener;
import wuzm.android.kdownload.callback.DownloadStateListener;
import wuzm.android.kdownload.dao.ContinueDAO;
import wuzm.android.kdownload.debugUtils.LogUtil;
import wuzm.android.kdownload.task.DownloadAsyncTask;

public class DownloadManager implements Observer{
	
	private static final String TAG = DownloadManager.class.getSimpleName();
	private static int MAX_TASK_COUNT = 2;
	private static int MAX_THREAD_COUNT_PER_TASK = 2;
	private static long SIZE_MUL_THREAD_CONDITION = 4 * 1024 * 1024; //bytes
	private ArrayList<TaskBean> blockingTaskBeans;
	private ArrayList<DownloadAsyncTask> downloadingTasks;
	private ContinueDAO dao;
	private String fileSaveDir;
	
	private DownloadProgressListener mProgressListener;
	private DownloadStateListener mDownloadStateListener;
	
	private volatile static DownloadManager instance;
	
	private DownloadManager(Context context) {
		init(context);
	}
	
	public static DownloadManager getInstance(Context context) {
		if(instance == null) {
			
			synchronized (DownloadManager.class) {
				if(instance == null) {
					instance = new DownloadManager(context);
				}
			}
		}
		return instance;
	}
	
	public boolean isTaskExist(String url) {
		return dao.isExistTask(url);
	}
	
	public void resumeTasks() {
		if(downloadingTasks.size() == 0 && blockingTaskBeans.size() == 0) {
			//read task from database and continue download
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					ArrayList<TaskBean> taskBeans = dao.readTasks();
					LogUtil.d(TAG, "get task record from database");
					if(taskBeans != null && taskBeans.size() != 0) {
						blockingTaskBeans.addAll(taskBeans);
					}
					while(downloadingTasks.size() < MAX_TASK_COUNT && blockingTaskBeans.size() > 0) {
						addDownloadTask();
					}
					for(int i = 0 ,len = blockingTaskBeans.size() ; i < len ; i ++ ) {
						if(mDownloadStateListener != null) {
							mDownloadStateListener.onWaitting(blockingTaskBeans.get(i).getUrl());
						}
					}
				}
			}).start();
		}else {
			//resume all the pause task
			for(DownloadAsyncTask task : downloadingTasks) {
				if(task.isPause()) {
					task.resume();
				}
			}
		}
	}
	
	public void pauseTasks() {
		for(DownloadAsyncTask task : downloadingTasks) {
			task.pause();
		}
	}
	
	public void stopTasks() {
		blockingTaskBeans.clear();
		for(DownloadAsyncTask task : downloadingTasks) {
			task.remove();
		}
		downloadingTasks.clear();
	}
	
	public boolean addTask(final String url,final String fileName,final String fileSuffix) {
		
		if(TextUtils.isEmpty(fileSaveDir)) {
			Log.e(TAG,"before start must set files saving dir");
			return false;
		}
		File dir = new File(fileSaveDir);
		if( ! dir.exists()) {
			dir.mkdirs();
		}
		
		if( ! dao.isExistTask(url)) {
			TaskBean bean = TaskBeanFactory.create(url, fileSaveDir,
					fileName, fileSuffix);
			bean.setMaxBlockCount(MAX_TASK_COUNT);
			bean.setSizeMulThreadCondition(SIZE_MUL_THREAD_CONDITION);
			blockingTaskBeans.add(bean);
			if(downloadingTasks.size() < MAX_TASK_COUNT) {
				addDownloadTask();
			}else {
				//进入等待下载队列
				if(mDownloadStateListener != null) {
					mDownloadStateListener.onWaitting(url);
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean addTask(final String url,final String fileName,final String fileSuffix,final long taskSize) {
		
		if(TextUtils.isEmpty(fileSaveDir)) {
			Log.e(TAG,"before start must set files saving dir");
			return false;
		}
		if(taskSize <= 0) {
			Log.e(TAG,"the task size must set");
			return false;
		}
		File dir = new File(fileSaveDir);
		if( ! dir.exists()) {
			dir.mkdirs();
		}
		
		if( ! dao.isExistTask(url)) {
			TaskBean bean = TaskBeanFactory.create(url, fileSaveDir,
					fileName, fileSuffix, taskSize);
			bean.setMaxBlockCount(MAX_TASK_COUNT);
			bean.setSizeMulThreadCondition(SIZE_MUL_THREAD_CONDITION);
			blockingTaskBeans.add(bean);
			if(downloadingTasks.size() < MAX_TASK_COUNT) {
				addDownloadTask();
			}else {
				//进入等待下载队列
				if(mDownloadStateListener != null) {
					mDownloadStateListener.onWaitting(url);
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean removeTask(String url) {
		for(int i = 0, len = downloadingTasks.size() ; i < len ; i ++) {
			if(url.equals(downloadingTasks.get(i).getTaskBean().getUrl())) {
				downloadingTasks.get(i).remove();
				LogUtil.d(TAG, "remove task from downloading task queue->" + url);
				if(blockingTaskBeans.size() > 0) {
					addDownloadTask();
				}
				return true;
			}
		}
		for(int i = 0,len = blockingTaskBeans.size() ; i < len ; i ++ ) {
			if(url.equals(blockingTaskBeans.get(i).getUrl())) {
				blockingTaskBeans.remove(i);
				LogUtil.d(TAG, "remove task from blocking task queue->" + url);
				return true;
			}
		}
		return false;
	}
	
	public void resumeTask(String url) {
		
		if(dao.isExistTask(url)) {
			for(DownloadAsyncTask task : downloadingTasks) {
				// task in the downloading queue before pause
				if(task.getTaskBean().getUrl().equals(url)) {
					LogUtil.d(TAG, "task resume->" + url);
					task.resume();
					return;
				}
			}
			if(downloadingTasks.size() < MAX_TASK_COUNT) {
				// task in the blocking queue and downloading queue size smaller MAX_TASK_COUNT before pause
				for (TaskBean bean : blockingTaskBeans) {
					if (bean.getUrl().equals(url)) {
						addDownloadTask(bean);
						blockingTaskBeans.remove(bean);
						return;
					}
				}
			}
		}
	}
	
	public void pauseTask(String url) {
		if(dao.isExistTask(url)) {
			for(DownloadAsyncTask task : downloadingTasks) {
				if(task.getTaskBean().getUrl().equals(url)) {
					//pause the task only when the task in the downloading queue 
					task.pause();
					LogUtil.d(TAG, "task pause->" + url);
					break;
				}
			}
		}
	}
	
	/**
	 * 重新开始下载任务
	 * @param url
	 * @param fileName
	 * @param fileSuffix
	 * @return
	 */
	public boolean reStartTask(String url, String fileName, String fileSuffix) {
		
		if(removeTask(url)) {
			return addTask(url, fileName, fileSuffix);
		}
		return false;
	}
	
	/**
	 * 重新开始下载任务
	 * @param url
	 * @param fileName
	 * @param fileSuffix
	 * @param taskSize
	 * @return
	 */
	public boolean reStartTask(String url, String fileName, String fileSuffix, long taskSize) {
		
		if(removeTask(url)) {
			return addTask(url, fileName, fileSuffix, taskSize);
		}
		return false;
	}
	
	private void init(Context context) {
		blockingTaskBeans = new ArrayList<TaskBean>();
		downloadingTasks = new ArrayList<DownloadAsyncTask>();
		dao = new ContinueDAO(context, 1);
	}
	
	//push a blocking task into downloading tasks queue and start download
	private void addDownloadTask() {
		DownloadAsyncTask task = new DownloadAsyncTask(blockingTaskBeans.get(0), dao);
		task.setCoreThreadPoolSize(MAX_THREAD_COUNT_PER_TASK);
		if(mDownloadStateListener != null) {
			mDownloadStateListener.onStart(task.getTaskBean().getUrl());
			task.setDownloadStateListener(mDownloadStateListener);
		}
		if(mProgressListener != null) {
			task.setDownloadProgressListener(mProgressListener);
		}
		task.addObserver(this);
		downloadingTasks.add(task);
		task.start();
		
		blockingTaskBeans.remove(0);
		LogUtil.d(TAG,"add task to downloading task queue->" + task.getTaskBean().getUrl());
	}
	
	private void addDownloadTask(TaskBean bean) {
		DownloadAsyncTask task = new DownloadAsyncTask(bean, dao);
		task.setCoreThreadPoolSize(MAX_THREAD_COUNT_PER_TASK);
		if(mProgressListener != null) {
			task.setDownloadProgressListener(mProgressListener);
		}
		task.addObserver(this);
		downloadingTasks.add(task);
		task.start();
	}
	
	@Override
	public void update(Observable observable, Object data) {
		//receive a notify that has a task that url is data complete the download,
		//so remove the task from the downloading queue
		removeTask((String)data);
		LogUtil.d(TAG, "remove task->" + (String)data );
	}
	
	public DownloadManager setFilesSaveDir(String dir) {
		this.fileSaveDir = dir;
		return this;
	}
	
	public void setDebug(boolean debug) {
		LogUtil.setDebug(debug);
	}
	
	public void setMaxDownloadingTaskCount(int count) {
		if(count <= 0) {
			throw new IllegalArgumentException("count must bigger than 0");
		}
		MAX_TASK_COUNT = count;
	}
	
	public void setMaxThreadCountPerTask(int count) {
		if(count <= 0) {
			throw new IllegalArgumentException("count must bigger than 0");
		}
		MAX_THREAD_COUNT_PER_TASK = count;
	}
	
    /**
     * 设置采用多线程下载的文件大小界限值,只有待下载的文件的大小大于设定的值的时候才会采用多线程
     * @param size
     */
    public static void setSizeMulThreadCondition(long size) {
 	   if(size <= 0) {
 		   throw new IllegalArgumentException("size must bigger than 0");
 	   }
 	  SIZE_MUL_THREAD_CONDITION = size;
    }
	
	
	public DownloadManager setDownloadProgressListener(DownloadProgressListener l) {
		this.mProgressListener = l;
		return this;
	}
	
	public DownloadManager setDownloadStateListener(DownloadStateListener l) {
		this.mDownloadStateListener = l;
		return this;
	}

}
