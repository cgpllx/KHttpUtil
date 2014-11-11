package com.kubeiwu.commontool.khttp.krequestimpl;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.kubeiwu.commontool.khttp.NetworkResponse;
import com.kubeiwu.commontool.khttp.Response;
import com.kubeiwu.commontool.khttp.Response.ErrorListener;
import com.kubeiwu.commontool.khttp.Response.Listener;
import com.kubeiwu.commontool.khttp.exception.ParseError;
import com.kubeiwu.commontool.khttp.krequestimpl.core.MultipartRequestParams;
import com.kubeiwu.commontool.khttp.toolbox.HttpHeaderParser;

/**
 * @author  cgpllx1@qq.com (www.kubeiwu.com)
 * @date    2014-8-13 
 */
public class KSimpleMultiPartRequest extends KMultiPartRequest<String> {

	public KSimpleMultiPartRequest(int method, String url, Map<String, String> headers, MultipartRequestParams params, ErrorListener errorListener,
			Listener<String> listener) {
		super(method, url, headers, params, errorListener, listener);
	}

	public KSimpleMultiPartRequest(String url, MultipartRequestParams params) {
		this(Method.POST, url, null, params, null, null);
	}
     
	public KSimpleMultiPartRequest(String url, MultipartRequestParams params, ErrorListener errorListener, Listener<String> listene) {
		this(Method.POST, url, null, params, errorListener, listene);
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(response));
		} catch (Exception je) {
			return Response.error(new ParseError(response));
		}
	}

}