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

package com.kubeiwu.commontool.khttp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.net.TrafficStats;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.kubeiwu.commontool.khttp.VolleyLog.MarkerLog;
import com.kubeiwu.commontool.khttp.cache.Cache;
import com.kubeiwu.commontool.khttp.exception.AuthFailureError;
import com.kubeiwu.commontool.khttp.exception.TimeoutError;
import com.kubeiwu.commontool.khttp.exception.VolleyError;

/**
 * Base class for all network requests.
 * 
 * @param <T>
 *            The type of parsed response this request expects.
 */
public abstract class Request<T> implements Comparable<Request<T>> {

	/**
	 * Default encoding for POST or PUT parameters. See {@link #getParamsEncoding()}.
	 */
	private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

	/**
	 * 支持的请求方法
	 */
	public interface Method {
		int DEPRECATED_GET_OR_POST = -1;
		int GET = 0;
		int POST = 1;
		int PUT = 2;
		int DELETE = 3;
	}

	/**
	 * 请求策越
	 * 
	 * @author Administrator
	 *
	 */
	public interface RequestMode {
		int LOAD_DEFAULT = 0;// 默认不处理
		int LOAD_NETWORK_ONLY = 1;// 只从网络获取
		int LOAD_NETWORK_ELSE_CACHE = 2;// 先从网络获取，网络没有取本地
		int LOAD_CACHE_ELSE_NETWORK = 3;// 先从本地获取，本地没有取网络
	}

	private int mRequestMode = RequestMode.LOAD_DEFAULT;
	/** An event log tracing the lifetime of this request; for debugging. */
	private final MarkerLog mEventLog = MarkerLog.ENABLED ? new MarkerLog() : null;

	/** Request method of this request. Currently supports GET, POST, PUT, and DELETE. */
	private final int mMethod;

	/** URL of this request. */
	private final String mUrl;

	/** Default tag for {@link TrafficStats}. */
	private final int mDefaultTrafficStatsTag;// 流量统计的标记 ，默认是主机的hashcode

	/** Listener interface for errors. */
	private final Response.ErrorListener mErrorListener;

	/** 这个请求序列号，用于执行FIFO。. */
	private Integer mSequence;

	/** The request queue this request is associated with. */
	private RequestQueue mRequestQueue;

	/** Whether or not responses to this request should be cached. */
	private boolean mShouldCache = true;// 是否需要缓存
	/**
	 * 强制从网络中获取数据
	 */
	private boolean mForceDataFromNetwork = false;// 强制从网络中获取数据
	/** Whether or not this request has been canceled. */
	private boolean mCanceled = false;

	/** Whether or not a response has been delivered for this request yet. */
	private boolean mResponseDelivered = false;

	// A cheap variant of request tracing used to dump slow requests.
	private long mRequestBirthTime = 0;

	/** Threshold at which we should log the request (even when debug logging is not enabled). */
	private static final long SLOW_REQUEST_THRESHOLD_MS = 3000;

	/** 重试这个请求策略. */
	private RetryPolicy mRetryPolicy;

	/**
	 * When a request can be retrieved from cache but must be refreshed from the network, the cache entry will be stored here so that in the event of a "Not Modified" response, we can be sure it hasn't been evicted from cache.
	 */
	private Cache.Entry mCacheEntry = null;

	/** An opaque token tagging this request; used for bulk cancellation. */
	private Object mTag;

	/**
	 * Creates a new request with the given method (one of the values from {@link Method}), URL, and error listener. Note that the normal response listener is not provided here as delivery of responses is provided by subclasses, who have a better idea of how to deliver an already-parsed response.
	 */
	public Request(int method, String url, Response.ErrorListener listener) {
		mMethod = method;
		mUrl = url;
		mErrorListener = listener;
		setRetryPolicy(new DefaultRetryPolicy());

		mDefaultTrafficStatsTag = TextUtils.isEmpty(url) ? 0 : Uri.parse(url).getHost().hashCode();
	}

	public void setRequestMode(int requestMode) {
		this.mRequestMode = requestMode;
	}

	public int getRequestMode() {
		return this.mRequestMode;
	}

	/**
	 * Return the method for this request. Can be one of the values in {@link Method}.
	 */
	public int getMethod() {
		return mMethod;
	}

	/**
	 * 请求的标识，可以用于取消这个请求
	 */
	public void setTag(Object tag) {
		mTag = tag;
	}

	/**
	 * Returns this request's tag.
	 * 
	 * @see Request#setTag(Object)
	 */
	public Object getTag() {
		return mTag;
	}

	/**
	 * @return A tag for use with {@link TrafficStats#setThreadStatsTag(int)}
	 */
	public int getTrafficStatsTag() {
		return mDefaultTrafficStatsTag;
	}

	/**
	 * Sets the retry policy for this request.
	 */
	public Request<T> setRetryPolicy(RetryPolicy retryPolicy) {
		mRetryPolicy = retryPolicy;
		return this;
	}

	/**
	 * Adds an event to this request's event log; for debugging.
	 */
	public void addMarker(String tag) {
		if (MarkerLog.ENABLED) {
			mEventLog.add(tag, Thread.currentThread().getId());
		} else if (mRequestBirthTime == 0) {
			mRequestBirthTime = SystemClock.elapsedRealtime();
		}
	}

	/**
	 * Notifies the request queue that this request has finished (successfully or with error).
	 * 
	 * <p>
	 * Also dumps all events from this request's event log; for debugging.
	 * </p>
	 */
	void finish(final String tag) {
		if (mRequestQueue != null) {
			mRequestQueue.finish(this);
		}
		if (MarkerLog.ENABLED) {
			final long threadId = Thread.currentThread().getId();
			if (Looper.myLooper() != Looper.getMainLooper()) {
				// If we finish marking off of the main thread, we need to
				// actually do it on the main thread to ensure correct ordering.
				Handler mainThread = new Handler(Looper.getMainLooper());
				mainThread.post(new Runnable() {
					@Override
					public void run() {
						mEventLog.add(tag, threadId);
						mEventLog.finish(this.toString());
					}
				});
				return;
			}

			mEventLog.add(tag, threadId);
			mEventLog.finish(this.toString());
		} else {
			long requestTime = SystemClock.elapsedRealtime() - mRequestBirthTime;
			if (requestTime >= SLOW_REQUEST_THRESHOLD_MS) {
				VolleyLog.d("%d ms: %s", requestTime, this.toString());
			}
		}
	}

	/**
	 * Associates this request with the given queue. The request queue will be notified when this request has finished.
	 */
	public void setRequestQueue(RequestQueue requestQueue) {
		mRequestQueue = requestQueue;
	}

	/**
	 * Sets the sequence number of this request. Used by {@link RequestQueue}. 设置请求序号
	 */
	public final void setSequence(int sequence) {
		mSequence = sequence;
	}

	/**
	 * Returns the sequence number of this request.
	 */
	public final int getSequence() {
		if (mSequence == null) {
			throw new IllegalStateException("getSequence called before setSequence");
		}
		return mSequence;
	}

	/**
	 * Returns the URL of this request.
	 */
	public String getUrl() {
		return mUrl;
	}

	/**
	 * Returns the cache key for this request. By default, this is the URL.
	 */
	public String getCacheKey() {
		return getUrl();
	}

	/**
	 * Annotates this request with an entry retrieved for it from cache. Used for cache coherency support.
	 */
	public void setCacheEntry(Cache.Entry entry) {
		mCacheEntry = entry;
	}

	/**
	 * Returns the annotated cache entry, or null if there isn't one.
	 */
	public Cache.Entry getCacheEntry() {
		return mCacheEntry;
	}

	/**
	 * Mark this request as canceled. No callback will be delivered.
	 */
	public void cancel() {
		mCanceled = true;
	}

	/**
	 * Returns true if this request has been canceled.
	 */
	public boolean isCanceled() {
		return mCanceled;
	}

	/**
	 * Returns a list of extra HTTP headers to go along with this request. Can throw {@link AuthFailureError} as authentication may be required to provide these values.
	 * 
	 * @throws AuthFailureError
	 *             In the event of auth failure
	 */
	public Map<String, String> getHeaders() throws AuthFailureError {
		return Collections.emptyMap();
	}

	/**
	 * Returns a Map of parameters to be used for a POST or PUT request. Can throw {@link AuthFailureError} as authentication may be required to provide these values.
	 * 
	 * <p>
	 * Note that you can directly override {@link #getBody()} for custom data.
	 * </p>
	 * 
	 * @throws AuthFailureError
	 *             in the event of auth failure
	 */
	protected Map<String, String> getParams() throws AuthFailureError {
		return null;
	}

	/**
	 * Returns which encoding should be used when converting POST or PUT parameters returned by {@link #getParams()} into a raw POST or PUT body.
	 * 
	 * <p>
	 * This controls both encodings:
	 * <ol>
	 * <li>The string encoding used when converting parameter names and values into bytes prior to URL encoding them.</li>
	 * <li>The string encoding used when converting the URL encoded parameters into a raw byte array.</li>
	 * </ol>
	 */
	protected String getParamsEncoding() {
		return DEFAULT_PARAMS_ENCODING;
	}

	public String getBodyContentType() {
		return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
	}

	/**
	 * Returns the raw POST or PUT body to be sent.
	 * 
	 * @throws AuthFailureError
	 *             in the event of auth failure
	 */
	public byte[] getBody() throws AuthFailureError {
		Map<String, String> params = getParams();
		if (params != null && params.size() > 0) {
			return encodeParameters(params, getParamsEncoding());
		}
		return null;
	}

	/**
	 * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
	 */
	private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
		StringBuilder encodedParams = new StringBuilder();
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
				encodedParams.append('=');
				encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
				encodedParams.append('&');
			}
			return encodedParams.toString().getBytes(paramsEncoding);
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
		}
	}

	public String encodeParameter(String param, String paramsEncoding) {
		try {
			return URLEncoder.encode(param, paramsEncoding);
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
		}
	}

	/**
	 * Set whether or not responses to this request should be cached.
	 */
	public final Request<T> setShouldCache(boolean shouldCache) {
		mShouldCache = shouldCache;
		return this;
	}

	/**
	 * Set whether or not responses to this request should be cached.
	 */
	public final Request<T> setForceDataFromNetwork(boolean forceDataFromNetwork) {
		mForceDataFromNetwork = forceDataFromNetwork;
		return this;
	}

	/**
	 * Returns true if responses to this request should be cached.
	 */
	public final boolean shouldCache() {
		return mShouldCache;
	}

	/**
	 * Returns true if responses to this request should be cached.
	 */
	public final boolean forceDataFromNetwork() {
		return mForceDataFromNetwork;
	}

	/**
	 * Priority values. Requests will be processed from higher priorities to lower priorities, in FIFO order.
	 */
	public enum Priority {
		LOW, NORMAL, HIGH, IMMEDIATE
	}

	/**
	 * Returns the {@link Priority} of this request; {@link Priority#NORMAL} by default.
	 */
	public Priority getPriority() {
		return Priority.NORMAL;
	}

	/**
	 * Returns the socket timeout in milliseconds per retry attempt. (This value can be changed per retry attempt if a backoff is specified via backoffTimeout()). If there are no retry attempts remaining, this will cause delivery of a {@link TimeoutError} error.
	 */
	public final int getTimeoutMs() {
		return mRetryPolicy.getCurrentTimeout();
	}

	/**
	 * Returns the retry policy that should be used for this request.
	 */
	public RetryPolicy getRetryPolicy() {
		return mRetryPolicy;
	}

	/**
	 * Mark this request as having a response delivered on it. This can be used later in the request's lifetime for suppressing identical responses.
	 */
	public void markDelivered() {
		mResponseDelivered = true;
	}

	/**
	 * Returns true if this request has had a response delivered for it.
	 */
	public boolean hasHadResponseDelivered() {
		return mResponseDelivered;
	}

	/**
	 * Subclasses must implement this to parse the raw network response and return an appropriate response type. This method will be called from a worker thread. The response will not be delivered if you return null.
	 * 
	 * @param response
	 *            Response from the network
	 * @return The parsed response, or null in the case of an error
	 */
	abstract protected Response<T> parseNetworkResponse(NetworkResponse response);

	/**
	 * Subclasses can override this method to parse 'networkError' and return a more specific error.
	 * 
	 * <p>
	 * The default implementation just returns the passed 'networkError'.
	 * </p>
	 * 
	 * @param volleyError
	 *            the error retrieved from the network
	 * @return an NetworkError augmented with additional information
	 */
	protected VolleyError parseNetworkError(VolleyError volleyError) {
		return volleyError;
	}

	/**
	 * Subclasses must implement this to perform delivery of the parsed response to their listeners. The given response is guaranteed to be non-null; responses that fail to parse are not delivered.
	 * 
	 * @param response
	 *            The parsed response returned by {@link #parseNetworkResponse(NetworkResponse)}
	 */
	abstract protected void deliverResponse(T response);

	/**
	 * Delivers error message to the ErrorListener that the Request was initialized with.
	 * 
	 * @param error
	 *            Error details
	 */
	public void deliverError(VolleyError error) {
		if (mErrorListener != null) {
			mErrorListener.onErrorResponse(error);
		}
	}

	/**
	 * Our comparator sorts from high to low priority, and secondarily by sequence number to provide FIFO ordering.
	 */
	@Override
	public int compareTo(Request<T> other) {
		Priority left = this.getPriority();
		Priority right = other.getPriority();
		// 根据优先级排序，高优先级的排前面 FIFO ordering.
		// Equal priorities are sorted by sequence number to provide FIFO ordering.
		return left == right ? this.mSequence - other.mSequence : right.ordinal() - left.ordinal();
	}

	@Override
	public String toString() {
		String trafficStatsTag = "0x" + Integer.toHexString(getTrafficStatsTag());
		return (mCanceled ? "[X] " : "[ ] ") + getUrl() + " " + trafficStatsTag + " " + getPriority() + " " + mSequence;
	}
}
