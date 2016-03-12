package com.example.kernel.khttp.task;

import android.os.AsyncTask;

import com.example.kernel.khttp.handler.RetryHandler;
import com.example.kernel.khttp.request.Request;
import com.example.kernel.khttp.response.Response;
import com.example.kernel.khttp.stack.HttpStack;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;

/**
 * Created by kernel on 15/2/19.
 */
public class AsyncNetTask extends AsyncTask<Void, Void, Response>
    implements NetTask{
    private Request<Object> mRequest;
    private RetryHandler mRetryHandler;
    private HttpStack mHttpStack;
    
    private boolean canceled;

    public AsyncNetTask(Request<Object> mRequest) {
        this.mRequest = mRequest;
        this.mRetryHandler = mRequest.getRetryHandler();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(mRequest != null) {
        	mRequest.markStatus(Request.Status.WORKING);
        	mRequest.setStartExcuteTime(System.currentTimeMillis());
            mRequest.preExcute();
        }
    }

    @Override
    protected Response doInBackground(Void... params) {
        return work();
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        if(mRequest != null && response != null) {
        	mRequest.markStatus(Request.Status.FINISH);
        	mRequest.setFinishExcuteTime(System.currentTimeMillis());
            if(response.getResultCode() == Response.ResultCode.RESULT_OK) {
                // 得到正确的结果
                mRequest.postResult(response.getResult());
            }else {
                mRequest.postError(response.getResultCode());
            }
        }
    }

    @Override
    public Response work() {
        Response mResponse = new NetTaskResponse();
        if(mRequest != null) {
        	mHttpStack = mRequest.getStack();
            if(mHttpStack != null) {
            	while(true) {
            		if(mRequest.canceled()) {
            			// 请求被取消了
            			mResponse.setResultCode(Response.ResultCode.RESTUL_CANCEL);
            			return mResponse;
            		}
                    try {
                        HttpResponse response = mHttpStack.excute(mRequest);
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        mResponse.setHttpStatusCode(httpStatusCode);
                        switch (httpStatusCode) {
    					case HttpStatus.SC_OK:
    						// 获取数据正常
    						if( responseIsGzip(response) ) {
    							mResponse.setServerSupportGzip(true);
    						}else {
    							mResponse.setServerSupportGzip(false);
    						}
    						mResponse.setEntity(response.getEntity());
                            Object result = mRequest.parseResponse(mResponse);
                            if(result == null) {
                                // 数据解析异常
                                mResponse.setResultCode(Response.ResultCode.RESULT_PARSE_ERROR);
                                mResponse.setResult(null);
                            }else {
                                // 数据解析正常
                                mResponse.setResultCode(Response.ResultCode.RESULT_OK);
                                mResponse.setResult(result);
                            }
    						return mResponse;
    					case HttpStatus.SC_NOT_MODIFIED:
    						// 本次请求与上次同样的请求的数据没有改变，如果有缓存，则可以直接从缓存获取
    						
    						break;
    					case HttpStatus.SC_MOVED_PERMANENTLY:
    					case HttpStatus.SC_MOVED_TEMPORARILY:
    						// 本次请求的数据的地址已发生改变，需要重定位
    						String redirectUrl = response.getHeaders("Location")[0].getValue();
    						mRequest.setRedirectUrl(redirectUrl);
    						break;
    					default:
    						// 本次请求获取数据失败
    						mResponse.setResultCode(Response.ResultCode.RESULT_FAILD);
    						break;
    					}
                        if(httpStatusCode < 200 || httpStatusCode > 299) {
                        	throw new IOException();
                        }
                    } catch (ConnectTimeoutException e) {
                        e.printStackTrace();
                        mResponse.setResultCode(Response.ResultCode.RESULT_TIMEOUT);
    					if( !needRetry()) {
    						// 不能重试
    						break;
    					}
                    } catch (SocketTimeoutException e) {
                    	e.printStackTrace();
                        mResponse.setResultCode(Response.ResultCode.RESULT_TIMEOUT);
    					if( !needRetry()) {
    						// 不能重试
    						break;
    					}
                    } catch (InterruptedIOException e) {
                    	// 用户取消了请求
                    	e.printStackTrace();
                    	mRequest.setCancel(true);
                    	mResponse.setResultCode(Response.ResultCode.RESTUL_CANCEL);
                    	return mResponse;
                    } catch (IOException e) {
    					e.printStackTrace();
    					switch (mResponse.getHttpStatusCode()) {
						case HttpStatus.SC_MOVED_PERMANENTLY:
						case HttpStatus.SC_MOVED_TEMPORARILY:
	    					if( !needRetry()) {
	    						// 不能重试
	    						break;
	    					}
							break;
						default:
							return mResponse;
						}
    				}
            	}
            }else {
                throw new NullPointerException("");
            }
        }else {
            mResponse.setResultCode(Response.ResultCode.RESULT_FAILD);
        }
        return mResponse;
    }
    
    /** 服务器是否是返回采用Gzip压缩过的数据*/
    private boolean responseIsGzip(HttpResponse response) {
    	for(Header header : response.getHeaders("Content-Encoding")) {
    		if(header.getValue().toLowerCase().indexOf("gzip") > -1) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean needRetry() {
    	if(mRetryHandler == null) {
    		return false;
    	}
    	boolean needRetry = mRetryHandler.canRetry();
    	mRetryHandler.markRetry();
    	return needRetry;
    }

	@Override
	public void cancel() {
		canceled = true;
		mHttpStack.cancel();
	}
    
}
