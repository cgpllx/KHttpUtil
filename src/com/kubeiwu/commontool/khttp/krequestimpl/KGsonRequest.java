package com.kubeiwu.commontool.khttp.krequestimpl;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kubeiwu.commontool.khttp.NetworkResponse;
import com.kubeiwu.commontool.khttp.Response;
import com.kubeiwu.commontool.khttp.Response.ErrorListener;
import com.kubeiwu.commontool.khttp.Response.Listener;
import com.kubeiwu.commontool.khttp.exception.ParseError;
import com.kubeiwu.commontool.khttp.toolbox.HttpHeaderParser;

public class KGsonRequest<T> extends KRequest<T> {

	public KGsonRequest(int method, String url, Map<String, String> headers,//
			Map<String, String> params, Listener<T> listener, ErrorListener errorListener) {
		super(method, url, headers, params, listener, errorListener);
	}

	 
	public KGsonRequest(int method, String url, Listener<T> listener) {
		super(method, url, listener);
	}

	public KGsonRequest(int method, String url, Map<String, String> headers, Map<String, String> params) {
		super(method, url, headers, params);
	}


	public KGsonRequest(String url, Map<String, String> headers) {
		super(url, headers);
	}


	public KGsonRequest(int method, String url, Map<String, String> params, Listener<T> listener, ErrorListener errorListener) {
		super(method, url, params, listener, errorListener);
	}

	public KGsonRequest(int method, String url, Map<String, String> params, Listener<T> listener) {
		super(method, url, params, listener);
	}

	public KGsonRequest(int method, String url, Map<String, String> params) {
		super(method, url, params);
	}

	public KGsonRequest(String url, Listener<T> listener, ErrorListener errorListener) {
		super(url, listener, errorListener);
	}

	public KGsonRequest(String url, Listener<T> listener) {
		super(url, listener);
	}

	public KGsonRequest(String url, Map<String, String> params, Listener<T> listener, ErrorListener errorListener) {
		super(url, params, listener, errorListener);
	}

	public KGsonRequest(String url, Map<String, String> headers, Map<String, String> params, Listener<T> listener, ErrorListener errorListener) {
		super(url, headers, params, listener, errorListener);
	}

	public KGsonRequest(String url) {
		super(url);
	}


	protected final static Gson gson = new Gson();

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
				System.out.println("正真返回数据的字符串="+json);
			T t = gson.fromJson(json, new TypeToken<T>() {
			}.getType());
			System.out.println("正真返回数据的字符串TTT==="+t);
			return Response.success(t, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}
}
