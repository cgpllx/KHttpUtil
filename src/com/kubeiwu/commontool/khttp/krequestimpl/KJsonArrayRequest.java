package com.kubeiwu.commontool.khttp.krequestimpl;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import com.kubeiwu.commontool.khttp.NetworkResponse;
import com.kubeiwu.commontool.khttp.Response;
import com.kubeiwu.commontool.khttp.Response.ErrorListener;
import com.kubeiwu.commontool.khttp.Response.Listener;
import com.kubeiwu.commontool.khttp.exception.ParseError;
import com.kubeiwu.commontool.khttp.toolbox.HttpHeaderParser;

public class KJsonArrayRequest extends KRequest<JSONArray> {

	public KJsonArrayRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Listener<JSONArray> listener, ErrorListener errorListener) {
		super(method, url, headers, params, listener, errorListener);
	}

	public KJsonArrayRequest(int method, String url, Listener<JSONArray> listener) {
		super(method, url, listener);

	}

	public KJsonArrayRequest(int method, String url, Map<String, String> params, Listener<JSONArray> listener, ErrorListener errorListener) {
		super(method, url, params, listener, errorListener);
	}

	public KJsonArrayRequest(int method, String url, Map<String, String> params, Listener<JSONArray> listener) {
		super(method, url, params, listener);
	}

	public KJsonArrayRequest(int method, String url, Map<String, String> params) {
		super(method, url, params);
	}

	public KJsonArrayRequest(String url, Listener<JSONArray> listener, ErrorListener errorListener) {
		super(url, listener, errorListener);
	}

	public KJsonArrayRequest(String url, Listener<JSONArray> listener) {
		super(url, listener);
	}

	public KJsonArrayRequest(String url, Map<String, String> params, Listener<JSONArray> listener, ErrorListener errorListener) {
		super(url, params, listener, errorListener);
	}

	public KJsonArrayRequest(String url, Map<String, String> headers, Map<String, String> params, Listener<JSONArray> listener, ErrorListener errorListener) {
		super(url, headers, params, listener, errorListener);
	}

	public KJsonArrayRequest(String url) {
		super(url);
	}

	@Override
	protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(new JSONArray(jsonString), HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}
}
