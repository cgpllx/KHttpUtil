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

import android.os.Process;

import java.util.concurrent.BlockingQueue;

import com.kubeiwu.commontool.khttp.Request.RequestMode;
import com.kubeiwu.commontool.khttp.cache.Cache;
import com.kubeiwu.commontool.khttp.exception.VolleyError;

/**
 * 提供一个线程在队列中的请求进行缓存的分类。
 * 
 * 请添加到指定的缓存队列解决高速缓存。 Any deliverable response is posted back to the caller via a {@link ResponseDelivery}. Cache misses and responses that require refresh are enqueued on the specified network queue for processing by a {@link NetworkDispatcher}.
 */
@SuppressWarnings("rawtypes")
public class CacheDispatcher extends Thread {

	private static final boolean DEBUG = VolleyLog.DEBUG;

	/** The queue of requests coming in for triage. */
	private final BlockingQueue<Request> mCacheQueue;

	/** The queue of requests going out to the network. */
	private final BlockingQueue<Request> mNetworkQueue;

	/** The cache to read from. */
	private final Cache mCache;

	/** For posting responses. */
	private final ResponseDelivery mDelivery;

	/** Used for telling us to die. */
	private volatile boolean mQuit = false;

	/**
	 * Creates a new cache triage dispatcher thread. You must call {@link #start()} in order to begin processing.
	 * 
	 * @param cacheQueue
	 *            Queue of incoming requests for triage
	 * @param networkQueue
	 *            Queue to post requests that require network to
	 * @param cache
	 *            Cache interface to use for resolution
	 * @param delivery
	 *            Delivery interface to use for posting responses
	 */
	public CacheDispatcher(BlockingQueue<Request> cacheQueue, BlockingQueue<Request> networkQueue, Cache cache, ResponseDelivery delivery) {
		mCacheQueue = cacheQueue;
		mNetworkQueue = networkQueue;
		mCache = cache;
		mDelivery = delivery;
	}

	/**
	 * Forces this dispatcher to quit immediately. If any requests are still in the queue, they are not guaranteed to be processed.
	 */
	public void quit() {
		mQuit = true;
		interrupt();
	}

	@Override
	public void run() {
		if (DEBUG)
			VolleyLog.v("start new dispatcher");
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

		// Make a blocking call to initialize the cache.
		mCache.initialize();

		while (true) {
			try {
				// Get a request from the cache triage queue, blocking until
				// at least one is available.
				final Request request = mCacheQueue.take();
				request.addMarker("cache-queue-take");

				// If the request has been canceled, don't bother dispatching it.
				if (request.isCanceled()) {
					request.finish("cache-discard-canceled");
					continue;
				}

				// Attempt to retrieve this item from cache.
				Cache.Entry entry = mCache.get(request.getCacheKey());
				// --------------------cgp 2015 -08-13 添加先从网络请求数据，网络没有再充本地缓存中取
				if (request.getRequestMode() == RequestMode.LOAD_NETWORK_ELSE_CACHE) {// 先网络，然后缓存
					if (entry != null) {
						Response<?> response = request.parseNetworkResponse(new NetworkResponse(entry.data, entry.responseHeaders));
						mDelivery.postResponse(request, response);
					} else {
						mDelivery.postError(request, new VolleyError("缓存中没有数据"));
					}
					continue;
				}
				// --------------------cgp 2015 -08-13

				if (entry == null) {// 缓存中如果没有，就加到网络请求中，不执行后面的了
					request.addMarker("cache-miss");
					// Cache miss; send off to the network dispatcher.
					mNetworkQueue.put(request);
					continue;
				}

				// If it is completely expired, just send it to the network.
				if (entry.isExpired()) {// 如果缓存的时间过期了，同上处理
					request.addMarker("cache-hit-expired");
					System.out.println("缓存过期");
					request.setCacheEntry(entry);// entry中可能有 请求头等信息，所以要放进去给request去使用
					mNetworkQueue.put(request);
					continue;
				}

				// We have a cache hit; parse its data for delivery back to the request.
				request.addMarker("cache-hit");
				Response<?> response = request.parseNetworkResponse(// 有缓存，将他传回去
						new NetworkResponse(entry.data, entry.responseHeaders));
				request.addMarker("cache-hit-parsed");

				if (!entry.refreshNeeded()) {// 软引用没有到期（ 不明白）
					// Completely unexpired cache hit. Just deliver the response.
					mDelivery.postResponse(request, response);
				} else {
					System.out.println("缓存过期");
					// Soft-expired cache hit. We can deliver the cached response,
					// but we need to also send the request to the network for
					// refreshing.
					request.addMarker("cache-hit-refresh-needed");
					request.setCacheEntry(entry);

					// Mark the response as intermediate.
					response.intermediate = true;

					// Post the intermediate response back to the user and have
					// the delivery then forward the request along to the network.
					mDelivery.postResponse(request, response, new Runnable() {
						@Override
						public void run() {
							try {
								mNetworkQueue.put(request);
							} catch (InterruptedException e) {
								// Not much we can do about this.
							}
						}
					});
				}
			} catch (InterruptedException e) {
				// We may have been interrupted because it was time to quit.
				if (mQuit) {
					return;
				}
				continue;
			}
		}
	}
}
