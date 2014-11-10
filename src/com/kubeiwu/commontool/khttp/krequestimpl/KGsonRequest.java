package com.kubeiwu.commontool.khttp.krequestimpl;

import java.io.UnsupportedEncodingException;
import java.util.List;
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

public abstract class KGsonRequest<T> extends KRequest<T> {

	public KGsonRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Listener<T> listener, ErrorListener errorListener) {
		super(method, url, headers, params, listener, errorListener);
	}

	protected final Gson gson = new Gson();

//	@Override
//	protected Response<T> parseNetworkResponse(NetworkResponse response) {
//		try {
//			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//			T t = gson.fromJson(json, new TypeToken<T>() {
//			}.getType());
//			return Response.success(t, HttpHeaderParser.parseCacheHeaders(response));
//		} catch (UnsupportedEncodingException e) {
//			return Response.error(new ParseError(e));
//		} catch (JsonSyntaxException e) {
//			return Response.error(new ParseError(e));
//		}
//	}
//	protected abstract T parseT(String josn);
}