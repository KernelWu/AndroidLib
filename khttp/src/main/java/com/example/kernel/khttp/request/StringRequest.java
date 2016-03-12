package com.example.kernel.khttp.request;

import com.example.kernel.khttp.HttpHeader;
import com.example.kernel.khttp.HttpParams;
import com.example.kernel.khttp.response.Response;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;


public class StringRequest extends Request {
	
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
		StringBuilder result;
		if(response.isServerSupportGzip() && isClientSupportGzip()) {
			try {
				GZIPInputStream gzipIS = new GZIPInputStream(response.getEntity().getContent());
				result = new StringBuilder();
				String bufferStr = null;
				byte[] bufferByte = new byte[2048];
				while( gzipIS.read(bufferByte) > 0 ) {
					bufferStr = new String(bufferByte, getEncoding());
					result.append(bufferStr);
				}
				return result.toString();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			try {
				InputStreamReader ISR = new InputStreamReader(response.getEntity().getContent(),
						getEncoding());
				result = new StringBuilder();
				char[] buffer = new char[256];
				while(ISR.read(buffer) > 0) {
					result.append(buffer);
				}
				return result.toString();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return null;
    }

}
