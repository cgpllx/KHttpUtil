package com.kubeiwu.commontool.khttp.krequestimpl;

import java.util.Map;

import com.kubeiwu.commontool.khttp.Request;
import com.kubeiwu.commontool.khttp.Response.ErrorListener;
import com.kubeiwu.commontool.khttp.Response.Listener;
import com.kubeiwu.commontool.khttp.exception.AuthFailureError;

/**
 * @author cgpllx1@qq.com (www.kubeiwu.com)
 * @param <T>
 * @date 2014-8-13
 * @param <T>
 */
public abstract class KRequest<T> extends Request<T> {
	private final Map<String, String> headers;
	private final Map<String, String> params;
	private final Listener<T> listener;

	/**
	 * 构造
	 * 
	 * @param method
	 *            请求方式
	 * @param url
	 *            地址
	 * @param headers
	 *            请求头信息
	 * @param params
	 *            请求参数
	 * @param listener
	 *            正确响应监听
	 * @param errorListener
	 *            错误响应监听
	 */
	public KRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.headers = headers;
		this.params = params;
		this.listener = listener;
	}

	// get----------------------------------------------------
	public KRequest(String url, Map<String, String> headers,//
			Map<String, String> params, Listener<T> listener, ErrorListener errorListener) {
		this(Method.GET, url, headers, params, listener, errorListener);
	}

	public KRequest(String url, Map<String, String> params, Listener<T> listener,//
			ErrorListener errorListener) {
		this(url, null, params, listener, errorListener);
	}

	public KRequest(String url, Map<String, String> headers) {
		this(Method.GET, url, headers, null, null, null);
	}

	public KRequest(String url, Listener<T> listener, ErrorListener errorListener) {
		this(url, null, null, listener, errorListener);
	}

	public KRequest(String url, Listener<T> listener) {
		this(url, null, null, listener, null);
	}

	public KRequest(String url) {
		this(url, null, null, null, null);
	}

	// get----------------------------------------------------

	// post----------------------------------------------------

	public KRequest(int method, String url, Map<String, String> params,//
			Listener<T> listener, ErrorListener errorListener) {
		this(method, url, null, params, listener, errorListener);
	}

	public KRequest(int method, String url, Map<String, String> params, Listener<T> listener) {
		this(method, url, null, params, listener, null);
	}

	public KRequest(int method, String url, Listener<T> listener) {
		this(method, url, null, null, listener, null);
	}

	public KRequest(int method, String url, Map<String, String> params) {
		this(method, url, null, params, null, null);
	}

	public KRequest(int method, String url, Map<String, String> headers, Map<String, String> params) {
		this(method, url, headers, params, null, null);
	}

	// post----------------------------------------------------
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return headers != null ? headers : super.getHeaders();
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return params != null ? params : super.getParams();
	}

	@Override
	protected void deliverResponse(T response) {
		if (null != listener) {
			listener.onResponse(response);
		}
	}
}
