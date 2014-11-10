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
	public KJsonObjectRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Listener<JSONObject> listener, ErrorListener errorListener) {
		super(method, url, headers, params, listener, errorListener);
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
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
