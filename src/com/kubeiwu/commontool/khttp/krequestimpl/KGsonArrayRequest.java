package com.kubeiwu.commontool.khttp.krequestimpl;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kubeiwu.commontool.khttp.NetworkResponse;
import com.kubeiwu.commontool.khttp.Response;
import com.kubeiwu.commontool.khttp.Response.ErrorListener;
import com.kubeiwu.commontool.khttp.Response.Listener;
import com.kubeiwu.commontool.khttp.exception.ParseError;
import com.kubeiwu.commontool.khttp.toolbox.HttpHeaderParser;

public class KGsonArrayRequest<T> extends KGsonRequest<List<T>> {

	public KGsonArrayRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Listener<List<T>> listener, ErrorListener errorListener) {
		super(method, url, headers, params, listener, errorListener);
	}

	@Override
	protected Response<List<T>> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			List<T> list = gson.fromJson(json, new TypeToken<List<T>>() {
			}.getType());
			return Response.success(list, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

}