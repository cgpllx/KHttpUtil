package com.kubeiwu.commontool.khttp.krequestimpl;

import java.util.Map;

import com.kubeiwu.commontool.khttp.Request;
import com.kubeiwu.commontool.khttp.Response.ErrorListener;
import com.kubeiwu.commontool.khttp.Response.Listener;
import com.kubeiwu.commontool.khttp.exception.AuthFailureError;

/**
 * @author  cgpllx1@qq.com (www.kubeiwu.com)
 * @param <T>
 * @date    2014-8-13
 * @param <T>
 */
public abstract class KRequest<T>  extends Request<T>{
	private final Map<String, String> headers;
	private final Map<String, String> params;
	private final Listener<T> listener;

	public KRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.headers = headers;
		this.params = params;
		this.listener = listener;
	}

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
