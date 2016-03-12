package com.example.kernel.khttp.request;


import com.example.kernel.khttp.HttpHeader;
import com.example.kernel.khttp.HttpParams;
import com.example.kernel.khttp.response.Response;

import java.io.File;

public class DownloadRequest extends Request<File> {

    private Response.ProgressListener mProgressListener;

	@Override
	public void setUrl(String url) {
		
	}

	@Override
	public String getUrl() {
		return null;
	}

	@Override
	public void setRedirectUrl(String url) {
		
	}

	@Override
	public String getRedirectUrl() {
		return null;
	}

	@Override
	public void setHttpParams(HttpParams params) {
		
	}

	@Override
	public HttpParams getHttpParams() {
		return null;
	}

	@Override
	public void setHttpHeader(HttpHeader header) {
		
	}

	@Override
	public HttpHeader getHttpHeader() {
		return null;
	}

    @Override
    public Object parseResponse(Response response) {
        return null;
    }

    /** 设置文件传输进度监听接口*/
    public void setProgressListener(Response.ProgressListener listener) {
        this.mProgressListener = listener;
    }

    public Response.ProgressListener getProgressListener() {
        return mProgressListener;
    }

}
