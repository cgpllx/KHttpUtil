package com.kubeiwu.commontool.khttp.krequestimpl;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.google.gson.JsonSyntaxException;
import com.kubeiwu.commontool.khttp.NetworkResponse;
import com.kubeiwu.commontool.khttp.Response;
import com.kubeiwu.commontool.khttp.Response.ErrorListener;
import com.kubeiwu.commontool.khttp.Response.Listener;
import com.kubeiwu.commontool.khttp.exception.ParseError;
import com.kubeiwu.commontool.khttp.toolbox.HttpHeaderParser;

public class KGsonObjectRequest<T> extends KGsonRequest<T> {

	public KGsonObjectRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Listener<T> listener, ErrorListener errorListener, Class<? extends T> clazz) {
		super(method, url, headers, params, listener, errorListener, clazz);
	}
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			@SuppressWarnings("unchecked")
			T t = (T) gson.fromJson(json, clazz);
			return Response.success(t, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

}