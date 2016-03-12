package com.example.kernel.khttp.stack;


import com.example.kernel.khttp.entity.Boundary;
import com.example.kernel.khttp.entity.FilePart;
import com.example.kernel.khttp.entity.MultiPartEntity;
import com.example.kernel.khttp.entity.StringPart;
import com.example.kernel.khttp.request.Request;
import com.example.kernel.khttp.request.UploadRequest;
import com.example.kernel.khttp.utils.MultityEntityUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by kernel on 15/2/19.
 */
public class HttpClientStack implements  HttpStack{
	private HttpClient mClient;
	private HttpUriRequest mRequest;
	/** 连接超时时间*/
	private int connectTimeout = 6000;
	/** socket超时时间*/
	private int socketTimeout = 6000;
	
	private boolean canceled;
	/** 客户端是否支持GZIP压缩的数据*/
    private boolean clientSupportGzip = true;
	
	public HttpClientStack() {
		super();
		mClient = new DefaultHttpClient();
	}
	
    @Override
    public HttpResponse excute(Request request) throws IOException{
        configHttpClient(mClient, request);
        mRequest = createHttpRequest(request);
        return mClient.execute(mRequest);
    }


    private void configHttpClient(HttpClient client, Request request) {
        HttpParams mParams = client.getParams();
        HttpConnectionParams.setConnectionTimeout(mParams, connectTimeout);
        HttpConnectionParams.setSoTimeout(mParams, socketTimeout);
        HttpConnectionParams.setTcpNoDelay(mParams, false);
    }

    /** 创建一个Http请求*/
    private HttpUriRequest createHttpRequest(Request request) 
    		throws UnsupportedEncodingException {
        HttpUriRequest mUriRequest = null;
        switch (request.getMethod()) {
            case Request.Method.GET:
                mUriRequest = createHttpGet(request);
                break;
            case Request.Method.POST:
                mUriRequest = createHttpPost(request);
                mUriRequest.addHeader(MultityEntityUtil.HEADER_CONTENT_TYPE,
                        String.format(MultityEntityUtil.CONTENT_TYPE_MULTITY_FORMAT,
                                new Boundary().getBoundary()));
                mUriRequest.addHeader("Connection", "Keep_Alive");
                break;
            case Request.Method.UPLOAD:
                mUriRequest = createHttpPost(request);
                mUriRequest.addHeader(MultityEntityUtil.HEADER_CONTENT_TYPE,
                        String.format(MultityEntityUtil.CONTENT_TYPE_MULTITY_FORMAT,
                                new Boundary().getBoundary()));
                mUriRequest.addHeader("Connection", "Keep_Alive");
                break;
            case Request.Method.DOWNLOAD:
                mUriRequest = createHttpGet(request);
                break;
        }
        if(mUriRequest != null) {
            configHeaderEncoding(mUriRequest, request);
        }
        return mUriRequest;
    }

    private HttpGet createHttpGet(Request request) {
        String url = request.getUrl();
        com.example.kernel.khttp.HttpParams params = request.getHttpParams();
        if(params == null) {
            return new HttpGet(url);
        }
        Map<String, Object> mPairs = params.getParams();
        if(mPairs != null) {
            try {
                int i = 0;
                for(String key : mPairs.keySet()) {
                    if(mPairs.get(key) instanceof File) {
                        throw new IllegalStateException("the get can't transport file");
                    }
                    if( i != 0) {
                        url += "&" + URLEncoder.encode(key, request.getEncoding())
                                + "=" + URLEncoder.encode(String.valueOf(mPairs.get(key)),
                                request.getEncoding());
                    }else {
                        url += "?" + URLEncoder.encode(key, request.getEncoding())
                                + "=" + URLEncoder.encode(String.valueOf(mPairs.get(key)),
                                request.getEncoding());
                    }
                    i ++;
                }
//                for(int i = 0 , len = mPairs.size() ; i < len ; i ++) {
//                    if(i != 0) {
//                        url += "&" + URLEncoder.encode(mPairs.get(i).getName(), request.getEncoding())
//                                + "=" + URLEncoder.encode(mPairs.get(i).getValue(),
//                                request.getEncoding());
//                    }else {
//                        url += "?" + URLEncoder.encode(mPairs.get(i).getName(), request.getEncoding())
//                                + "=" + URLEncoder.encode(params.get(mPairs.get(i).getValue()),
//                                request.getEncoding());
//                    }
//                }
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return new HttpGet(url);
    }

    private HttpPost createHttpPost(Request request) throws UnsupportedEncodingException {
        HttpPost post = new HttpPost(request.getUrl());
        Map<String, Object> mParams = request.getHttpParams().getParams();
        if(mParams == null || mParams.keySet().size() == 0) {
            return post;
        }
        Boundary boundary = new Boundary();
        MultiPartEntity mMultiPartEntity = new MultiPartEntity(boundary);

        /** 只有是上传文件的请求才有监听文件上传进度的功能*/
        if(request instanceof UploadRequest) {
            mMultiPartEntity.setProgressListener(((UploadRequest)request).getProgressListener() );
        }

        /** add StringPart*/
        for(String key : mParams.keySet()) {
            if( !(mParams.get(key) instanceof File)) {
                StringPart mStringPart = new StringPart(boundary, key,
                        String.valueOf(mParams.get(key)), request.getEncoding());
                mMultiPartEntity.addPart(mStringPart);
            }
        }

        /** add FilePart*/
        for(String key : mParams.keySet()) {
            if(mParams.get(key) instanceof File) {
                File f = (File)mParams.get(key);
                FilePart mFilePart = new FilePart(boundary, key, f.getName(), f);
                mMultiPartEntity.addPart(mFilePart);
            }
        }
        post.setEntity(mMultiPartEntity);
        return post;
    }

    /** 配置编码过的http头部*/
    private void configHeaderEncoding(HttpUriRequest uriRequest, Request request) 
        throws UnsupportedEncodingException {
        ArrayList<BasicNameValuePair> header = request.getHttpHeader().getHeader();
        if(header != null) {
            BasicHeader[] headers = new BasicHeader[header.size()];
            int i = 0;
			for (BasicNameValuePair pair : header) {
				headers[i] = new BasicHeader(URLEncoder.encode(pair.getName(),
						request.getEncoding()), URLEncoder.encode(
						pair.getValue(), request.getEncoding()));
			}
            uriRequest.setHeaders(headers);
        }
        
        /** 配置是否支持接受服务器发送GZIP压缩的数据*/
        if(isClientSupportGzip()) {
        	 //　支持
        	 uriRequest.addHeader("Accep-Encoding", "gzip");
        }else {
        	 // 禁止
        	 uriRequest.addHeader("Accep-Encoding", "identity");
        }
    }
    
    public HttpClient getClient() {
    	return this.mClient;
    }

	@Override
	public void setConnectTimeout(int time) {
		if(time <= 0) {
			throw new IllegalArgumentException("参数必须大于0");
		}
		this.connectTimeout = time;
	}

	@Override
	public int getConnectTimeout() {
		return this.connectTimeout;
	}

	@Override
	public void setSocketTimeout(int time) {
		if(time <= 0) {
			throw new IllegalArgumentException("参数必须大于0");
		}
		this.socketTimeout = time;
	}

	@Override
	public int getSocketTimeout() {
		return this.socketTimeout;
	}

	@Override
	public void cancel() {
		this.canceled = true;
		if(mRequest != null) {
			mRequest.abort();
		}
	}

	@Override
	public boolean isClientSupportGzip() {
		return clientSupportGzip;
	}

	@Override
	public void setClientSupportGzip(boolean support) {
		this.clientSupportGzip = support;
	}
}
