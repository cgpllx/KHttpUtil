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

	public KGsonRequest(String url) {
		super(url);
	}
	//-----------------显示添加类型
	public KGsonRequest(int method, String url, Map<String, String> headers,//
			Map<String, String> params, Listener<T> listener, ErrorListener errorListener,Class<T> clazz) {
		super(method, url, headers, params, listener, errorListener);
		setResponseType(clazz);
	}
	
	public KGsonRequest(int method, String url, Listener<T> listener,Class<T> clazz) {
		super(method, url, listener);
		setResponseType(clazz);
	}
	
	public KGsonRequest(int method, String url, Map<String, String> headers, Map<String, String> params,Class<T> clazz) {
		super(method, url, headers, params);
		setResponseType(clazz);
	}
	
	public KGsonRequest(String url, Map<String, String> headers,Class<T> clazz) {
		super(url, headers);
		setResponseType(clazz);
	}
	
	public KGsonRequest(int method, String url, Map<String, String> params, Listener<T> listener, ErrorListener errorListener,Class<T> clazz) {
		super(method, url, params, listener, errorListener);
		setResponseType(clazz);
	}
	
	public KGsonRequest(int method, String url, Map<String, String> params, Listener<T> listener,Class<T> clazz) {
		super(method, url, params, listener);
		setResponseType(clazz);
	}
	
	public KGsonRequest(int method, String url, Map<String, String> params,Class<T> clazz) {
		super(method, url, params);
		setResponseType(clazz);
	}
	
	public KGsonRequest(String url, Listener<T> listener, ErrorListener errorListener,Class<T> clazz) {
		super(url, listener, errorListener);
		setResponseType(clazz);
	}
	
	public KGsonRequest(String url, Listener<T> listener,Class<T> clazz) {
		super(url, listener);
		setResponseType(clazz);
	}
	
	public KGsonRequest(String url, Map<String, String> params, Listener<T> listener, ErrorListener errorListener,Class<T> clazz) {
		super(url, params, listener, errorListener);
		setResponseType(clazz);
	}
	
	public KGsonRequest(String url,Class<T> clazz) {
		super(url);
		setResponseType(clazz);
	}

	/**
	 * 注意：如果T的类型比较复杂（比如T中有泛型），请设置此方法名称类型，防止泛型被擦除，找不到对应的类型，导致解析出错
	 * 
	 * @param clazz
	 */
	public void setResponseType(Class<T> clazz) {
		this.mClazz = clazz;
	}

	protected final static Gson mGson = new Gson();
	private Class<T> mClazz = null;

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			T t = null;
			if (mClazz == null) {
				t = mGson.fromJson(json, new TypeToken<T>() {
				}.getType());
			} else {
				t = mGson.fromJson(json, mClazz);
			}
			return Response.success(t, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}catch (Exception e) {
			return Response.error(new ParseError(e));
		}
	}

}
