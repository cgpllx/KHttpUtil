package com.kubeiwu.commontool.khttp.krequestimpl;

import java.util.Map;

import com.google.gson.Gson;
import com.kubeiwu.commontool.khttp.Response.ErrorListener;
import com.kubeiwu.commontool.khttp.Response.Listener;

public abstract class KGsonRequest<T> extends KRequest<T> {

	public KGsonRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Listener<T> listener, ErrorListener errorListener, Class<?> clazz) {
		super(method, url, headers, params, listener, errorListener);
		this.clazz = clazz;
	}

	protected final static Gson gson = new Gson();
	protected final Class<?> clazz;
}
