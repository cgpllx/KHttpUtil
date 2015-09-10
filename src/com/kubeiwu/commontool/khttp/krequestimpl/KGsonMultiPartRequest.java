package com.kubeiwu.commontool.khttp.krequestimpl;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kubeiwu.commontool.khttp.NetworkResponse;
import com.kubeiwu.commontool.khttp.Response;
import com.kubeiwu.commontool.khttp.Response.ErrorListener;
import com.kubeiwu.commontool.khttp.Response.Listener;
import com.kubeiwu.commontool.khttp.exception.ParseError;
import com.kubeiwu.commontool.khttp.krequestimpl.core.MultipartRequestParams;
import com.kubeiwu.commontool.khttp.toolbox.HttpHeaderParser;

/**
 * @author  cgpllx1@qq.com (www.kubeiwu.com)
 * @param <T>
 * @date    2014-8-13 
 */
public class KGsonMultiPartRequest<T> extends KMultiPartRequest<T> {

	public KGsonMultiPartRequest(int method, String url, Map<String, String> headers, MultipartRequestParams params, ErrorListener errorListener,
			Listener<T> listener) {
		super(method, url, headers, params, errorListener, listener);
	}

	public KGsonMultiPartRequest(String url, MultipartRequestParams params) {
		this(Method.POST, url, null, params, null, null);
	}
     
	public KGsonMultiPartRequest(String url, MultipartRequestParams params, ErrorListener errorListener, Listener<T> listene) {
		this(Method.POST, url, null, params, errorListener, listene);
	}
 
	public void setResponseType(Type type) {
		this.mType = type;
	}

	protected final static Gson mGson = new Gson();
	
	private Type mType=null;
	
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		super.parseNetworkResponse(response);
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			T t = null;
			if (mType == null) {
				t = mGson.fromJson(json, new TypeToken<T>() {
				}.getType());
			} else {
				t = mGson.fromJson(json, mType);
			}
			return Response.success(t, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}catch (Exception e) {
			return Response.error(new ParseError(e));
		}
	}
}