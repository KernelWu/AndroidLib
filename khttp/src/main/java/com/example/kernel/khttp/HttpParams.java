package com.example.kernel.khttp;

import java.util.HashMap;
import java.util.Map;

/***
 * http 参数
 */
public class HttpParams {
    private Map<String, Object> params;

	public HttpParams() {
		params = new HashMap<String, Object>();
	}
	
	public void put(String key, Object value)
    {
        params.put(key, value);
	}
	
	public Object get(String key) {
        for(String k : params.keySet()) {
            if(k.equals(key)) {
                return params.get(key);
            }
        }
        return null;
	}

    public Map<String, Object> getParams() {
        return params;
    }

}
