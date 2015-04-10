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

import com.kubeiwu.commontool.khttp.exception.VolleyError;

/**
 * 响应传递
 * 
 * @author Administrator
 * 
 */
public interface ResponseDelivery {
	/**
	 * Parses a response from the network or cache and delivers it.
	 */
	public void postResponse(Request<?> request, Response<?> response);

	/**
	 * Parses a response from the network or cache and delivers it. The provided Runnable will be executed after delivery.
	 */
	public void postResponse(Request<?> request, Response<?> response, Runnable runnable);

	/**
	 * Posts an error for the given request.
	 */
	public void postError(Request<?> request, VolleyError error);

	/**
	 * 当前线程执行
	 * 
	 * @param request
	 * @param error
	 */
	public void postCurrentError(Request<?> request, VolleyError error);

	/**
	 * 当前线程执行
	 * 
	 * @param request
	 * @param response
	 * @param runnable
	 */
	public void postCurrentResponse(Request<?> request, Response<?> response, Runnable runnable);
}
