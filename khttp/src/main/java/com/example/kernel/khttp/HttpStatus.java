package com.example.kernel.khttp;

/** 发起的http请求的结果状态*/
public class HttpStatus {
	/** 正常*/
	public static final int STATUS_OK = 1;
	/** 超时*/
	public static final int STATUS_TIMEOUT = 2;
	/** 网络异常*/
	public static final int STATUS_NET_ERROR = 3;
	/** 服务器异常*/
	public static final int STATUS_SERVER_ERROR = 4;
	/** 解析异常*/
	public static final int STATUS_PARSE_ERROR = 5;

}
