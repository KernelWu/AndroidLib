package com.example.kernel.khttp.callback;

public interface HttpHandListener {
	
    public void onHttpHandStart(int what);
    /** 服务器返回的数据经过处理后的结果*/
    public void onHttpResult(int what, Object obj, int status);
    public void onHttpHandStop(int what);
    
    
    public void onHttpHandSuccess(int what, Object obj);
    public void onHttpHandNetError(int what);
    public void onHttpHandTimeout(int what);
    public void onHttpHandFaild(int what);
    public void onHttpHandCancel(int what);
}
