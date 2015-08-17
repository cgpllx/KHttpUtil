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

import android.annotation.SuppressLint;
import android.net.TrafficStats;
import android.os.Build;

import com.kubeiwu.commontool.khttp.Request.RequestMode;
import com.kubeiwu.commontool.khttp.cache.Cache;
import com.kubeiwu.commontool.khttp.exception.VolleyError;
import com.kubeiwu.commontool.khttp.superinterface.Network;

/**
 * Provides a thread for performing network dispatch from a queue of requests.
 * 
 * Requests added to the specified queue are processed from the network via a specified {@link Network} interface. Responses are committed to cache, if eligible, using a specified {@link Cache} interface. Valid responses and errors are posted back to the caller via a {@link ResponseDelivery}.
 */
public class CurrentNetworkDispatcher extends CurrentDispatcher {
	/** The queue of requests to service. */
	// private final BlockingQueue<Request> mQueue;
	/** The network interface for processing requests. */
	private final Network mNetwork;
	/** The cache to write to. */
	private final Cache mCache;
	/** For posting responses and errors. */
	// private final ResponseDelivery mDelivery;
	/** Used for telling us to die. */
	private volatile boolean mQuit = false;

	/**
	 * Creates a new network dispatcher thread. You must call {@link #start()} in order to begin processing.
	 * 
	 * @param queue
	 *            Queue of incoming requests for triage
	 * @param network
	 *            Network interface to use for performing requests
	 * @param cache
	 *            Cache interface to use for writing responses to cache
	 * @param delivery
	 *            Delivery interface to use for posting responses
	 */
	public CurrentNetworkDispatcher(Network network, Cache cache) {
		// mQueue = queue;
		mNetwork = network;
		mCache = cache;
		// mDelivery = delivery;
	}

	/**
	 * Forces this dispatcher to quit immediately. If any requests are still in the queue, they are not guaranteed to be processed.
	 */

	@Override
	public void quit() {
		mQuit = true;
		// interrupt();
	}

	// @SuppressLint("NewApi")
	@Override
	@SuppressLint("NewApi")
	public <T> T execute(Request<T> mRequest) {
		Request<T> request;
		request = mRequest;
		try {
			request.addMarker("current_network-queue-take");

			if (mQuit) {
				return null;
			}
			// If the request was cancelled already, do not perform the
			// network request.
			if (request.isCanceled()) {
				request.finish("network-discard-cancelled");
				return null;
			}

			// Tag the request (if API >= 14)
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				TrafficStats.setThreadStatsTag(request.getTrafficStatsTag());// 这个是流量统计用的
			}
			// Perform the network request.
			NetworkResponse networkResponse = mNetwork.performRequest(request);
			request.addMarker("network-http-complete");

			// If the server returned 304 AND we delivered a response already,
			// we're done -- don't deliver a second identical response.
			if (networkResponse.notModified && request.hasHadResponseDelivered()) {
				request.finish("not-modified");
				return null;
			}

			// Parse the response here on the worker thread.
			Response<T> response = request.parseNetworkResponse(networkResponse);
			
			request.addMarker("network-parse-complete");
			// Write to cache if applicable.
			// TODO: Only update cache metadata instead of entire record for 304s.
			if (request.shouldCache() && response.cacheEntry != null) {
				mCache.put(request.getCacheKey(), response.cacheEntry);// 网络请求的缓存的key是url
				request.addMarker("network-cache-written");
			}

			// Post the response back.
			request.markDelivered();
			// mDelivery.postCurrentResponse(request, response, null);
			return completion(request, response);
		} catch (VolleyError volleyError) {
			volleyError.printStackTrace();
			System.out.println("http-VolleyError--错误");
			// VolleyLog.e(volleyError, null);
			// parseAndDeliverNetworkError(request, volleyError);
		} catch (Exception e) {
			VolleyLog.e(e, "Unhandled exception %s", e.toString());
			// mDelivery.postError(request, new VolleyError(e));
		}
		return null;
	}
}
