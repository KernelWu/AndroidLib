package com.example.kernel.khttp.request;


import com.example.kernel.khttp.response.Response;

public class JsonRequest extends StringRequest {
	/** 用来解析json数据的类*/
	private Class<?> parseClass;

	public Class<?> getParseClass() {
		return parseClass;
	}

	public void setParseClass(Class<?> parseClass) {
		this.parseClass = parseClass;
	}

	@Override
	public Object parseResponse(Response response) {
		Object resultTmp = super.parseResponse(response);
		if(resultTmp != null) {
			String result = (String) super.parseResponse(response);
			System.out.println("result->" + result);
			return result;
		}
		return null;
	}
}
