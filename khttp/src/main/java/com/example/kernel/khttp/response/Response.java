package com.example.kernel.khttp.response;

import org.apache.http.HttpEntity;

public interface Response {

    public void setHttpStatusCode(int code);

    public int getHttpStatusCode();

    public void setEntity(HttpEntity entity);

    public HttpEntity getEntity();

    public void setResult(Object object);

    public Object getResult();

    public void setResultCode(int code);

    public int getResultCode();
    
    public void setServerSupportGzip(boolean support);
    
    public boolean isServerSupportGzip();

    /**
     * 请求的结果状态码
     */
    public static class ResultCode {
        /**请求结果正常*/
        public static final int RESULT_OK = 1;
        /**网络错误*/
        public static final int RESULT_NET_ERROR = -1;
        /**请求超时*/
        public static final int RESULT_TIMEOUT = -2;
        /** 解析数据错误*/
        public static final int RESULT_PARSE_ERROR = -3;
        /** 请求结果失败*/
        public static final int RESULT_FAILD = -4;
        /** 请求被取消了*/
        public static final int RESTUL_CANCEL = -5;
    }

    /**
     * 进度改变接口
     */
    public interface ProgressListener {
        public void onProgress(long completeSize, long totalSize);
    }
}
