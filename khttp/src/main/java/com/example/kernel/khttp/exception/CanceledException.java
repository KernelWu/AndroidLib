package com.example.kernel.khttp.exception;

/** 用户取消请求操作后抛出的异常*/
public class CanceledException extends Exception {
	
	public CanceledException() {
		super();
	}

	public CanceledException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public CanceledException(String detailMessage) {
		super(detailMessage);
	}

	public CanceledException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3485223918681711684L;
	
	

}
