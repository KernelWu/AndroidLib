package com.example.kernel.khttp.task;


import com.example.kernel.khttp.response.Response;

import org.apache.http.HttpEntity;

public class NetTaskResponse implements Response {
	private boolean serverSupportGzip = false;
	
    @Override
    public void setHttpStatusCode(int code) {

    }

    @Override
    public int getHttpStatusCode() {
        return 0;
    }

    @Override
    public void setEntity(HttpEntity entity) {

    }

    @Override
    public HttpEntity getEntity() {
        return null;
    }

    @Override
    public void setResult(Object object) {

    }

    @Override
    public Object getResult() {
        return null;
    }

    @Override
    public void setResultCode(int code) {

    }

    @Override
    public int getResultCode() {
        return 0;
    }

	@Override
	public void setServerSupportGzip(boolean support) {
		this.serverSupportGzip = support;
	}

	@Override
	public boolean isServerSupportGzip() {
		return serverSupportGzip;
	}
    
}
