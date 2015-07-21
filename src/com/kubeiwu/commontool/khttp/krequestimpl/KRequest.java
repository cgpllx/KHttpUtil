package com.kubeiwu.commontool.khttp.krequestimpl;

import java.util.HashMap;
import java.util.Map;

import com.kubeiwu.commontool.khttp.NetworkResponse;
import com.kubeiwu.commontool.khttp.Request;
import com.kubeiwu.commontool.khttp.Response;
import com.kubeiwu.commontool.khttp.Response.ErrorListener;
import com.kubeiwu.commontool.khttp.Response.Listener;
import com.kubeiwu.commontool.khttp.cache.Cache;
import com.kubeiwu.commontool.khttp.cookiemassage.CookieStoreManager;
import com.kubeiwu.commontool.khttp.exception.AuthFailureError;

/**
 * @author cgpllx1@qq.com (www.kubeiwu.com)
 * @param <T>
 * @date 2014-8-13
 * @param <T>
 */
public abstract class KRequest<T> extends Request<T> {
	private final Map<String, String> mHeaders;
	private final Map<String, String> mParams;
	private final Listener<T> listener;

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
	public KRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.mHeaders = headers;
		this.mParams = params;
		this.listener = listener;
	}

	// get----------------------------------------------------
	public KRequest(String url, Map<String, String> headers, Listener<T> listener,//
			ErrorListener errorListener) {
		this(Method.GET, url, headers, null, listener, errorListener);
	}

	public KRequest(String url, Map<String, String> headers) {
		this(Method.GET, url, headers, null, null, null);
	}

	public KRequest(String url, Listener<T> listener, ErrorListener errorListener) {
		this(url, null, listener, errorListener);
	}

	public KRequest(String url, Listener<T> listener) {
		this(url, null, listener, null);
	}

	public KRequest(String url) {
		this(url, null, null, null);
	}

	// get----------------------------------------------------

	// post----------------------------------------------------

	public KRequest(int method, String url, Map<String, String> params,//
			Listener<T> listener, ErrorListener errorListener) {
		this(method, url, null, params, listener, errorListener);
	}

	public KRequest(int method, String url, Map<String, String> params, Listener<T> listener) {
		this(method, url, null, params, listener, null);
	}

	public KRequest(int method, String url, Listener<T> listener) {
		this(method, url, null, null, listener, null);
	}

	public KRequest(int method, String url, Map<String, String> params) {
		this(method, url, null, params, null, null);
	}

	public KRequest(int method, String url, Map<String, String> headers, Map<String, String> params) {
		this(method, url, headers, params, null, null);
	}

	// post----------------------------------------------------
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = null;
		if (mShouldAddCookiesToRequest) {
			headers = mHeaders != null ? mHeaders : new HashMap<String, String>();// Collections.emptyMap()//这个map不能添加数据的 所以这里缓存hashmap
			CookieStoreManager.getCookieStore().addCookiesToHeaders(headers);
		} else {
			headers = mHeaders != null ? mHeaders : super.getHeaders();
		}
		return headers;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mParams != null ? mParams : super.getParams();
	}

	@Override
	protected void deliverResponse(T response) {
		if (null != listener) {
			listener.onResponse(response);
		}
	}

	/**
	 * 重写这个方法获取响应头信息 通过可以为 Set-Cookie 可以获取cookie信息
	 * 
	 * @param headers
	 */
	protected void deliverHeaders(Map<String, String> headers) {
		if (mShouldSaveCookies) {
			CookieStoreManager.getCookieStore().saveCookiesFromHeaders(headers);
		}
	}

	private boolean mShouldSaveCookies = true;// 是否需要保存cookies
	private boolean mShouldAddCookiesToRequest = true;// 请求中添加cookies

	/**
	 * 是否保存当前请求的cookies
	 * 
	 * @param shouldSaveCookies
	 */
	public void setShouldSaveCookies(boolean shouldSaveCookies) {
		mShouldSaveCookies = shouldSaveCookies;
	}

	/**
	 * 请求时候是否附带cookies
	 * 
	 * @param shouldAddCookiesToRequest
	 */

	public void setShouldAddCookiesToRequest(boolean shouldAddCookiesToRequest) {
		mShouldAddCookiesToRequest = shouldAddCookiesToRequest;
	}

	/**
	 * 设置缓存到期时间
	 * 
	 * @param duration
	 */
	public void setCache_Duration(long duration) {
		this.mCache_Duration = duration;
	}

	private long mCache_Duration = 0;

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		deliverHeaders(response.headers);
		if (mCache_Duration != 0) {
			response.headers.put(Cache.CACHE_DURATION, mCache_Duration + "");
		}
		return null;
	}
}
