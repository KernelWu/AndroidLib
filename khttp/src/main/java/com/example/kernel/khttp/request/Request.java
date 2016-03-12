package com.example.kernel.khttp.request;

import com.example.kernel.khttp.HttpEncoding;
import com.example.kernel.khttp.HttpHeader;
import com.example.kernel.khttp.HttpParams;
import com.example.kernel.khttp.RequestQueue;
import com.example.kernel.khttp.callback.HttpHandListener;
import com.example.kernel.khttp.handler.RetryHandler;
import com.example.kernel.khttp.response.Response;
import com.example.kernel.khttp.stack.HttpStack;

public abstract class Request<T extends Object> implements Comparable<Request<T>>{
	private String url;
	private HttpHeader mHttpHeader;
	private HttpParams mHttpParams;
	// 编码格式
	private String mEncoding = HttpEncoding.UTF8;
	
	private boolean canceled;
	/** 是否允许服务器使用Gzip压缩的数据*/
	private boolean clientSupportGzip;

    // 执行http请求的客户端
    private HttpStack mStack;
	// 请求创建的时间
	private long createTime;
	// 请求开始执行的时间
	private long startExcuteTime;
	// 请求执行完毕的时间
	private long finishExcuteTime;
	// 优先级
	private int mPriority = RequestPriority.NORMAL;
    // 分配的请求ID
    private int mId;

    // 提供给用户的自定义处理的方法
    private HttpHandListener mHandListener;
    // 重试处理器
    private RetryHandler mRetryHandler;
    // 所属的请求队列
    private RequestQueue mQueue;
	
    /** Http请求的方法*/
	public class Method {
		public static final int GET = 1;
		public static final int POST = 2;
		public static final int UPLOAD = 3;
		public static final int DOWNLOAD = 4;
	}
	//　http请求的方法
	private int mMethod = Method.POST;
	
	/** Http请求当前的状态*/
	public class Status {
		/** 等待执行中*/
		public static final int WAITTING = 0;
		/** 执行中*/
		public static final int WORKING = 1;
		/** 已完成*/
		public static final int FINISH = -1;
	}
	// http请求的状态
	private int mStatus = Status.WAITTING;
	
	// url
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
	
	// 重定向url
	public void setRedirectUrl(String url) {
		this.url = url;
	}
	public String getRedirectUrl() {
		return this.url;
	}
	
	// 头部
	public void setHttpHeader(HttpHeader header) {
		this.mHttpHeader = header;
	}
	public HttpHeader getHttpHeader() {
		return this.mHttpHeader;
	}
	
	// 参数
	public void setHttpParams(HttpParams params) {
		this.mHttpParams = params;
	}
	public HttpParams getHttpParams() {
		return this.mHttpParams;
	}
	
	// 编码格式
	public void setEncoding(String encoding) {
		if( !encoding.equals(HttpEncoding.UTF8)
				|| encoding.equals(HttpEncoding.GBK)) {
			throw new IllegalArgumentException("the parament must be member of HttpEncoding");
		}
		mEncoding = encoding;
	}
	public String getEncoding() {
		return mEncoding;
	}
	
	public boolean isClientSupportGzip() {
		return clientSupportGzip;
	}
	
	public void setClientSupportGzip(boolean clientSupportGzip) {
		this.clientSupportGzip = clientSupportGzip;
	}
	
	public void setMethod(int method) {
		switch (method) {
		case Method.GET:
		case Method.POST:
		case Method.UPLOAD:
		case Method.DOWNLOAD:
			this.mMethod = method;
			break;
		default:
			throw new IllegalArgumentException("the parament must be member of Method"); 
		}
	}
	
	public int getMethod() {
		return this.mMethod;
	}

    public HttpStack getStack() {
        return mStack;
    }

    public void setStack(HttpStack mStack) {
        this.mStack = mStack;
    }

    // 请求的创建时间
	public void setCreateTime(long time) {
		this.createTime = time;
	}
	public long getCreateTime() {
		return createTime;
	}
	
	// 请求开始执行的时间
	public long getStartExcuteTime() {
		return startExcuteTime;
	}
	public void setStartExcuteTime(long startExcuteTime) {
		this.startExcuteTime = startExcuteTime;
	}
	
	// 请求执行完毕的时间
	public long getFinishExcuteTime() {
		return finishExcuteTime;
	}
	public void setFinishExcuteTime(long finishExcuteTime) {
		this.finishExcuteTime = finishExcuteTime;
	}
	
	//　请求优先级
	public void setPriority(int priority) {
		switch (priority) {
		case RequestPriority.LOW:
		case RequestPriority.NORMAL:
		case RequestPriority.HIGHT:
            this.mPriority = priority;
			break;
		default:
			throw new IllegalArgumentException("the parament must be the member of RequestPriority");
		}
	}
	public int getPriority() {
		return mPriority;
	}

	// 请求的Id
    public void setId(int id) {
        this.mId = id;
    }
    public int getId() {
        return this.mId;
    }

    public void setHttpHandListener(HttpHandListener listener) {
        this.mHandListener = listener;
    }

    public HttpHandListener getHttpHandListener() {
        return this.mHandListener;
    }

    public void setRetryHandler(RetryHandler handler) {
        this.mRetryHandler = handler;
    }

    public RetryHandler getRetryHandler() {
        return this.mRetryHandler;
    }
    
    public void setRequestQueue(RequestQueue queue) {
    	this.mQueue = queue;
    }
    
    public RequestQueue getRequestQueue() {
    	return this.mQueue;
    }
    
    /** 标识当前请求的状态*/
    public void markStatus(int status) {
    	switch (status) {
		case Status.WAITTING:
		case Status.WORKING:
		case Status.FINISH:
			break;
		default:
			throw new IllegalArgumentException("parament must be the element of Request.Status");
		}
    	this.mStatus = status;
    }
    
    /** 获取当前请求的状态*/
    public int getStatus() {
    	return mStatus;
    }

    public void setCancel(boolean cancel) {
        this.canceled = cancel;
    }
    
    public boolean canceled() {
        return canceled;
    }
	
	@Override
	public int compareTo(Request<T> another) {
		return mPriority > another.getPriority() ? 1 : -1;
	}
	
    // 请求前的操作（UI线程）
    public void preExcute() {
        if(mHandListener != null) {
            mHandListener.onHttpHandStart(mId);
        }
    }

	// 解析response（非UI线程）
	public abstract Object parseResponse(Response response);

    // 反馈结果（UI线程）
    public void postResult(T result) {
        if(mHandListener != null) {
            mHandListener.onHttpHandStop(mId);
            mHandListener.onHttpHandSuccess(mId, result);
        }
    }
    
    // 反馈错误（UI线程）
    public void postError(int resultCode) {
        if(mHandListener != null) {
            mHandListener.onHttpHandStop(mId);
            switch (resultCode) {
                case Response.ResultCode.RESULT_NET_ERROR:
                    mHandListener.onHttpHandNetError(mId);
                    break;
                case Response.ResultCode.RESULT_TIMEOUT:
                    mHandListener.onHttpHandTimeout(mId);
                    break;
                case Response.ResultCode.RESULT_PARSE_ERROR:
                    mHandListener.onHttpHandFaild(mId);
                    break;
                case Response.ResultCode.RESTUL_CANCEL:
                	mHandListener.onHttpHandCancel(mId);
                	break;
            }
        }
    }
}
