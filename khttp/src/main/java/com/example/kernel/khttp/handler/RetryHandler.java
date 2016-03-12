package com.example.kernel.khttp.handler;

public class RetryHandler {
    // 最大重试次数
    private int maxRetryCount = 3;
    // 当前已重试次数
    private int retriedCount = 0;

    public RetryHandler(int maxRetryCount) {
    	if(maxRetryCount < 0) {
    		throw new IllegalArgumentException("parament must not less than 0");
    	}
        this.maxRetryCount = maxRetryCount;
        this.retriedCount = 0;
    }

    public RetryHandler() {
        this.retriedCount = 0;
    }

    /**
     * 获取最大可重试次数
     */
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    /**
     * 设置最大可重试次数
     */
    public void setMaxRetryCount(int maxRetryCount) {
    	if(maxRetryCount < 0) {
    		throw new IllegalArgumentException("parament must not less than 0");
    	}
        this.maxRetryCount = maxRetryCount;
    }

    /**
     * 重试的标志，每次重试需要调用
     */
    public void markRetry() {
        retriedCount ++;
    }

    /**
     * 是否还可以重试
     */
    public boolean canRetry() {
        return retriedCount < maxRetryCount;
    }

}
