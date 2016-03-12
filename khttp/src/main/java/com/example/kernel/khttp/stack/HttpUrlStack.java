package com.example.kernel.khttp.stack;

import android.os.Build;

import com.example.kernel.khttp.entity.Boundary;
import com.example.kernel.khttp.entity.FilePart;
import com.example.kernel.khttp.entity.StringPart;
import com.example.kernel.khttp.request.Request;
import com.example.kernel.khttp.request.UploadRequest;
import com.example.kernel.khttp.response.Response;
import com.example.kernel.khttp.utils.MultityEntityUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicStatusLine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;


/**
 * Created by kernel on 15/2/21.
 */
public class HttpUrlStack implements HttpStack {
	private HttpURLConnection mConnection;
	/** 连接超时时间*/
	private int connectTimeout = 6000;
	/** socket超时时间*/
	private int socketTimeout = 6000;
	
	// 文件上传进度接口
	Response.ProgressListener uploadFilesProgressListener;
    // 文件已上传的大小
    long filesUploadCompleteSize = 0;
    // 要上传的文件的总大小
    long filesTotalSize = 0;
    
    private boolean canceled;
    /** 客户端是否支持GZIP压缩的数据*/
    private boolean clientSupportGzip = true;
	
    @Override
    public HttpResponse excute(Request request) throws IOException {
    	mConnection = createHttpUrlConnection(request);

        if(request.getMethod() == Request.Method.POST
                || request.getMethod() == Request.Method.UPLOAD) {
            setMultiEntityToConnection(mConnection, request);
        }

        if(mConnection.getResponseCode() == -1) {
        	// 无法处理的responseCode，直接抛出异常
        	throw new IOException("Could not retrieve response code from HttpUrlConnection");
        }
        ProtocolVersion mProtocolVersion = new ProtocolVersion("HTTP", 1, 1);
        StatusLine mStatusLine = new BasicStatusLine(mProtocolVersion,
        		mConnection.getResponseCode(), mConnection.getResponseMessage());
        HttpResponse mResponse = new BasicHttpResponse(mStatusLine);
        for(String key : mConnection.getHeaderFields().keySet()) {
        	mResponse.addHeader(key, mConnection.getHeaderField(key));
        }
        mResponse.setEntity(getEntityFromConnection(mConnection));
        return mResponse;
    }
    
    private HttpEntity getEntityFromConnection(HttpURLConnection connection) {
    	BasicHttpEntity mEntity = new BasicHttpEntity();
    	mEntity.setContentEncoding(connection.getContentEncoding());
    	mEntity.setContentType(connection.getContentType());
    	mEntity.setContentLength(connection.getContentLength());
    	InputStream is = null;
    	try {
    		is = connection.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			is = connection.getErrorStream();
		}
    	mEntity.setContent(is);
    	return mEntity;
    }

    /** 写入表单数据*/
    private void setMultiEntityToConnection(HttpURLConnection connection,
                                            Request request) throws IOException{
        OutputStream os = connection.getOutputStream();

        Map<String, Object> mParams = request.getHttpParams().getParams();
        Boundary mBoundary = new Boundary();

        /** 写入字符串部分*/
        for(String key : mParams.keySet()) {
            if( !(mParams.get(key) instanceof File)) {
                StringPart part = new StringPart(mBoundary, key,
                        String.valueOf(mParams.get(key)), request.getEncoding());
                part.writeTo(os);
            }
        }
        
        if(request.getMethod() == Request.Method.UPLOAD) {
        	/** 文件上传*/
        	filesTotalSize = getUploadFilesSize(mParams);
        	filesUploadCompleteSize = 0;
        	uploadFilesProgressListener = ((UploadRequest)request).getProgressListener();
        	
        	Response.ProgressListener innerFilesPublicProgressListener
        	= new Response.ProgressListener() {
				
				@Override
				public void onProgress(long completeSize, long totalSize) {
					filesUploadCompleteSize += completeSize;
					if(uploadFilesProgressListener != null) {
						uploadFilesProgressListener.onProgress(filesUploadCompleteSize, filesTotalSize);
					}
				}
			};
			
	        /** 写入文件部分*/
	        for(String key : mParams.keySet()) {
	            if( mParams.get(key) instanceof File) {
	                File f = (File)mParams.get(key);
	                FilePart part = new FilePart(mBoundary, key,
	                        f.getName(), f);
	                part.setProgressListener(innerFilesPublicProgressListener);
	                part.writeTo(os);
	            }
	        }
        }
		os.write(mBoundary.getEndBoundaryByte());
		os.close();
    }
    
    /** 获取上传文件的总大小*/
    private long getUploadFilesSize(Map<String, Object> mParams) {
    	long size = 0;
    	for(String key : mParams.keySet()) {
    		File f = (File) mParams.get(key);
    		size += f.length();
    	}
     	return size;
    }


    /**
     * 配置
     */
    private HttpURLConnection createHttpUrlConnection(Request request)
        throws IOException{
		URL mUrl = new URL(createUrlReal(request));
		HttpURLConnection mConnection = null;
		mConnection = (HttpURLConnection) mUrl.openConnection();
		configHeader(mConnection, request);
		mConnection.setConnectTimeout(connectTimeout);
		mConnection.setReadTimeout(socketTimeout);
		mConnection.setUseCaches(false);
		mConnection.setInstanceFollowRedirects(false);
		mConnection.addRequestProperty("Charset", request.getEncoding());
		switch (request.getMethod()) {
		case Request.Method.GET:
			httpGet(mConnection);
			break;
		case Request.Method.POST:
			httpPost(mConnection);
			break;
		case Request.Method.UPLOAD:
			httpUpload(mConnection);
			break;
		case Request.Method.DOWNLOAD:
			httpDownload(mConnection);
			break;
		}
		return mConnection;
    }

    private String createUrlReal(Request request) throws UnsupportedEncodingException{
		String url = request.getUrl();
		switch (request.getMethod()) {
		case Request.Method.GET:
		case Request.Method.DOWNLOAD:
			if (request.getHttpParams() != null) {
				Map<String, Object> mPair = request.getHttpParams()
						.getParams();
                int i = 0;
                for(String key : mPair.keySet()) {
                    if (i != 0) {
                        url += "&"
                                + URLEncoder.encode(key,
                                request.getEncoding())
                                + "="
                                + URLEncoder.encode(String.valueOf(mPair.get(key)),
                                request.getEncoding());
                    } else {
                        url += "?"
                                + URLEncoder.encode(key,
                                request.getEncoding())
                                + "="
                                + URLEncoder.encode(String.valueOf(mPair.get(key)),
                                request.getEncoding());
                    }
                    i ++;
                }
			}
			break;
		}
        System.out.print(url);
		return url;
    }

    /**
     * 配置Http头部公用部分
     * @throws java.io.UnsupportedEncodingException
     */
    private void configHeader(HttpURLConnection connection, Request request)
    		throws UnsupportedEncodingException{
    	
    	/** 配置是否支持接受服务器发送GZIP压缩的数据*/
    	if(isClientSupportGzip()) {
    		// 支持
        	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
        		// HttpURLConnection在Android的GINGERBREAD版本之后会自动在头部增加支持gzip压缩的数据
        		connection.addRequestProperty("Accept-Encoding", "gzip");
        	}
    	}else {
    		// 禁止
    		connection.addRequestProperty("Accept-Encoding", "identity");
    	}
    	
		if (request.getHttpHeader() == null) {
			return;
		}
		for (BasicNameValuePair pair : request.getHttpHeader().getHeader()) {
			connection.addRequestProperty(
					URLEncoder.encode(pair.getName(), request.getEncoding()),
					URLEncoder.encode(pair.getValue(), request.getEncoding()));
		}
    }

//    /**
//     * 配置Http参数
//     */
//    private void configParams(HttpURLConnection connection, Request request)
//    		throws UnsupportedEncodingException, IOException{
//		if (request.getHttpParams() != null) {
//			DataOutputStream os = new DataOutputStream(
//					connection.getOutputStream());
//			StringBuilder sb = new StringBuilder();
//			for (BasicNameValuePair pair : request.getHttpParams().getParams()) {
//				sb.append(" ");
//				sb.append(URLEncoder.encode(pair.getName(),
//						request.getEncoding()));
//				sb.append("=");
//				sb.append(" ");
//				sb.append(" ");
//				sb.append(URLEncoder.encode(pair.getValue(),
//						request.getEncoding()));
//				sb.append(" ");
//			}
//			os.writeBytes(sb.toString());
//			os.flush();
//			os.close();
//		}
////		if (request.getMethod() == Request.Method.UPLOAD) {
////			HttpEntity mEntity = request.getHttpEntity();
////			if (mEntity != null && mEntity instanceof MultiPartEntity) {
////				OutputStream os = connection.getOutputStream();
////				mEntity.writeTo(os);
////				os.flush();
////				os.close();
////			}
////		}
//    }
    
    private void httpGet(HttpURLConnection connection) throws ProtocolException{
    	connection.addRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
    	connection.setRequestMethod("GET");
    	connection.setDoInput(false);
    	connection.setDoOutput(false);
    }
    
    private void httpPost(HttpURLConnection connection) throws ProtocolException{
        connection.addRequestProperty(MultityEntityUtil.HEADER_CONTENT_TYPE,
                String.format(MultityEntityUtil.CONTENT_TYPE_MULTITY_FORMAT,
                        new Boundary().getBoundary()));
        connection.addRequestProperty("Connection", "Keep-Alive");
    	connection.setRequestMethod("POST");
    	connection.setDoInput(false);
    	connection.setDoOutput(true);
    }
    
    private void httpUpload(HttpURLConnection connection) throws ProtocolException{
        connection.addRequestProperty(MultityEntityUtil.HEADER_CONTENT_TYPE,
                String.format(MultityEntityUtil.CONTENT_TYPE_MULTITY_FORMAT,
                        new Boundary().getBoundary()));
    	connection.addRequestProperty("Connection", "Keep-Alive");
    	connection.setRequestMethod("POST");
    	connection.setDoOutput(true);
    	connection.setDoInput(false);
    }
    
    private void httpDownload(HttpURLConnection connection) throws ProtocolException{
    	connection.addRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
    	connection.addRequestProperty("Connection", "Keep-Alive");
    	connection.setRequestMethod("GET");
    	connection.setDoInput(true);
    	connection.setDoOutput(false);
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

	/** Android SDK 2.2版本及以下版本不能正常关闭，这是官方的一个BUG*/
	@Override
	public void cancel() {
		this.canceled = true;
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			// 在Android 2.2版本之前，HttpURLConnection一直存在着一些令人厌烦的bug。
			// 比如说对一个可读的InputStream调用close()方法时，就有可能会导致连接池失效了。
			// 那么我们通常的解决办法就是直接禁用掉连接池的功能
			System.setProperty("http.keepAlive", "false");
		}
		try {
			mConnection.getOutputStream().close();
			mConnection.getInputStream().close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		mConnection.disconnect();
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
