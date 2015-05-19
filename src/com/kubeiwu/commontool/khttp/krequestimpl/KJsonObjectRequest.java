package com.kubeiwu.commontool.khttp.krequestimpl;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.kubeiwu.commontool.khttp.NetworkResponse;
import com.kubeiwu.commontool.khttp.Response;
import com.kubeiwu.commontool.khttp.Response.ErrorListener;
import com.kubeiwu.commontool.khttp.Response.Listener;
import com.kubeiwu.commontool.khttp.exception.ParseError;
import com.kubeiwu.commontool.khttp.toolbox.HttpHeaderParser;

public class KJsonObjectRequest extends KRequest<JSONObject> {

	public KJsonObjectRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Listener<JSONObject> listener, ErrorListener errorListener) {
		super(method, url, headers, params, listener, errorListener);
	}

	public KJsonObjectRequest(int method, String url, Listener<JSONObject> listener) {
		super(method, url, listener);
	}

	public KJsonObjectRequest(int method, String url, Map<String, String> params, Listener<JSONObject> listener, ErrorListener errorListener) {
		super(method, url, params, listener, errorListener);
	}

	public KJsonObjectRequest(int method, String url, Map<String, String> params, Listener<JSONObject> listener) {
		super(method, url, params, listener);
	}

	public KJsonObjectRequest(int method, String url, Map<String, String> headers, Map<String, String> params) {
		super(method, url, headers, params);
	}

	public KJsonObjectRequest(String url, Map<String, String> headers) {
		super(url, headers);
	}

	public KJsonObjectRequest(int method, String url, Map<String, String> params) {
		super(method, url, params);
	}

	public KJsonObjectRequest(String url, Listener<JSONObject> listener, ErrorListener errorListener) {
		super(url, listener, errorListener);
	}

	public KJsonObjectRequest(String url, Listener<JSONObject> listener) {
		super(url, listener);
	}

	public KJsonObjectRequest(String url, Map<String, String> params, Listener<JSONObject> listener, ErrorListener errorListener) {
		super(url, params, listener, errorListener);
	}

	public KJsonObjectRequest(String url) {
		super(url);
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		super.parseNetworkResponse(response);
		try {
			String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

}
