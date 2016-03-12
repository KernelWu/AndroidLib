package com.example.kernel.khttp.stack;

import com.example.kernel.khttp.request.Request;

import org.apache.http.HttpResponse;

import java.io.IOException;

public interface HttpStack {
	public HttpResponse excute(Request request) throws IOException;

	public void setConnectTimeout(int time);

	public int getConnectTimeout();

	public void setSocketTimeout(int time);

	public int getSocketTimeout();
	
	public void cancel();
	
	/** 客户端是否支持GZIP压缩的数据*/
	public boolean isClientSupportGzip();
	public void setClientSupportGzip(boolean support);
}
