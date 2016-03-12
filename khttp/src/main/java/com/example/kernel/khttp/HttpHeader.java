package com.example.kernel.khttp;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/***
 * Http 请求头部
 */
public class HttpHeader {
	private ArrayList<BasicNameValuePair> mHeader;

	public HttpHeader() {
        mHeader = new ArrayList<BasicNameValuePair>();
	}

	public void put(String key, Object value) {
		BasicNameValuePair pair = new BasicNameValuePair(key, String.valueOf(value));
        mHeader.add(pair);
	}

	public String get(String key) {
        if(mHeader == null) {
            return null;
        }
		for(int i = 0, len = mHeader.size() ; i < len ; i++) {
            if(mHeader.get(i).getName().equals(key)) {
                return mHeader.get(i).getValue();
            }
        }
        return null;
	}

    public ArrayList<BasicNameValuePair> getHeader() {
        return mHeader;
    }


}
