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

package com.kubeiwu.commontool.khttp.requestimpl;

import java.io.UnsupportedEncodingException;

import com.kubeiwu.commontool.khttp.NetworkResponse;
import com.kubeiwu.commontool.khttp.Request;
import com.kubeiwu.commontool.khttp.Response;
import com.kubeiwu.commontool.khttp.Response.ErrorListener;
import com.kubeiwu.commontool.khttp.Response.Listener;
import com.kubeiwu.commontool.khttp.toolbox.HttpHeaderParser;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class StringRequest extends Request<String> {
	private final Listener<String> mListener;

 
	public StringRequest(int method, String url, Listener<String> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		mListener = listener;
	}
 
	public StringRequest(String url, Listener<String> listener, ErrorListener errorListener) {
		this(Method.GET, url, listener, errorListener);
	}

	public StringRequest(String url, Listener<String> listener) {
		this(Method.GET, url, listener, null);
	}

	public StringRequest(String url) {
		this(Method.GET, url, null, null);
	}

	@Override
	protected void deliverResponse(String response) {
		if (mListener != null) {
			mListener.onResponse(response);
		}
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
