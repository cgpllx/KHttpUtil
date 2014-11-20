/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kubeiwu.commontool.khttp.krequestimpl;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.kubeiwu.commontool.khttp.NetworkResponse;
import com.kubeiwu.commontool.khttp.Response;
import com.kubeiwu.commontool.khttp.Response.ErrorListener;
import com.kubeiwu.commontool.khttp.Response.Listener;
import com.kubeiwu.commontool.khttp.toolbox.HttpHeaderParser;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class KStringRequest extends KRequest<String> {
	/**
	 * 
	 * @param method
	 *            请求方式
	 * @param url
	 *            请求地址
	 * @param headers
	 *            请求头
	 * @param params
	 *            请求参数
	 * @param listener
	 *            请求正确响应监听
	 * @param errorListener
	 *            请求错误响应监听
	 */
	public KStringRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Listener<String> listener, ErrorListener errorListener) {
		super(method, url, headers, params, listener, errorListener);
	}

	public KStringRequest(String url, Listener<String> listener) {
		this(Method.GET, url, null, null, listener, null);
	}

	public KStringRequest(int method, String url, Listener<String> listener) {
		this(method, url, null, null, listener, null);
	}

	public KStringRequest(int method, String url, Listener<String> listener, ErrorListener errorListener) {
		this(method, url, null, null, listener, errorListener);
	}

	public KStringRequest(int method, String url, Map<String, String> params, Listener<String> listener, ErrorListener errorListener) {
		this(method, url, null, params, listener, errorListener);
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
	}
}
