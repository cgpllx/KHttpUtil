package com.kubeiwu.commontool.khttp.krequestimpl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.JsonSyntaxException;
import com.kubeiwu.commontool.khttp.NetworkResponse;
import com.kubeiwu.commontool.khttp.Response;
import com.kubeiwu.commontool.khttp.Response.ErrorListener;
import com.kubeiwu.commontool.khttp.Response.Listener;
import com.kubeiwu.commontool.khttp.exception.ParseError;
import com.kubeiwu.commontool.khttp.toolbox.HttpHeaderParser;

public class KGsonArrayRequest<T> extends KGsonRequest<List<T>> {

	public KGsonArrayRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Listener<List<T>> listener, ErrorListener errorListener, Class<T> clazz) {
		super(method, url, headers, params, listener, errorListener, clazz);
	}

	/**
	 * 这里有一个泛型擦除问题，不能直接返回list
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Response<List<T>> parseNetworkResponse(NetworkResponse response) {
		List<T> list = new ArrayList<T>();
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				list.add((T) gson.fromJson(jsonArray.getString(i), clazz));
			}
			return Response.success(list, HttpHeaderParser.parseCacheHeaders(response));
		} catch (JSONException e) {
			return Response.error(new ParseError(e));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

}