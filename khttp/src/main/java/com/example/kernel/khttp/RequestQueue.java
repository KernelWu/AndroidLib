package com.example.kernel.khttp;

import android.os.Build;

import com.example.kernel.khttp.handler.RetryHandler;
import com.example.kernel.khttp.request.Request;
import com.example.kernel.khttp.stack.HttpClientStack;
import com.example.kernel.khttp.stack.HttpUrlStack;
import com.example.kernel.khttp.task.AsyncNetTask;

import java.util.PriorityQueue;


public class RequestQueue {
	private PriorityQueue<Request<Object>> mRequestQueue;
	
	public RequestQueue() {
		mRequestQueue = new PriorityQueue<Request<Object>>();
	}
	
	public void addRequest(Request<Object> request) {
		synchronized (mRequestQueue) {
			for(Request<Object> r : mRequestQueue) {
				if(r.getId() == request.getId()) {
					throw new IllegalArgumentException("the request id equal with exist request in request queue");
				}
			}
			request.setCreateTime(System.currentTimeMillis());
			request.markStatus(Request.Status.WAITTING);
			mRequestQueue.add(request);
		}
	}
	
	public boolean removeRequest(Request<Object> request) {
		synchronized (mRequestQueue) {
			return mRequestQueue.remove(request);
		}
	}
	
	public boolean removeRequest(int requestId) {
		synchronized (mRequestQueue) {
			for(Request<Object> r : mRequestQueue) {
				if(r.getId() == requestId) {
					mRequestQueue.remove(r);
					return true;
				}
			}
		}
		return false;
	}
	
	/** 开始执行请求队列里面的网络请求*/
	public void start() {
		synchronized (mRequestQueue) {
			for(Request<Object> request : mRequestQueue) {
				if(request.getStatus() != Request.Status.WAITTING) {
					// 正在执行或者已经执行完毕的请求不重复执行它
					continue;
				}
				if(request.getStack() == null) {
					if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
						// 2.3以前的android SDK版本使用HttpClient,之后的采用HttpUrlConnection
						request.setStack(new HttpClientStack());
					}else 
						request.setStack(new HttpUrlStack());
				}
				if(request.getRetryHandler() == null) {
					request.setRetryHandler(new RetryHandler());
				}
				request.setRequestQueue(this);
				AsyncNetTask netTask = new AsyncNetTask(request);
				netTask.execute();
			}
		}
	}
	
	/** 网络请求完毕后回调接口，从请求队列中移除自己*/
	public synchronized void RequestFinish(Request<?> request) {
		synchronized (mRequestQueue) {
			mRequestQueue.remove(request);
		}

	}
	
}
