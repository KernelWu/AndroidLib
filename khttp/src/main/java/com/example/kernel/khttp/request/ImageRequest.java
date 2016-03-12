package com.example.kernel.khttp.request;

import android.graphics.Bitmap;

import com.example.kernel.khttp.HttpHeader;
import com.example.kernel.khttp.HttpParams;
import com.example.kernel.khttp.response.Response;


public class ImageRequest extends Request<Bitmap> {

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

}
