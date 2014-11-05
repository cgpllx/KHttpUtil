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
 * 重试请求政策
 */
public interface RetryPolicy {

    /**
     * 返回当前的超时（用于测井）。 (used for logging).
     */
    public int getCurrentTimeout();

    /**
     * 返回当前的重试次数 (used for logging).
     */
    public int getCurrentRetryCount();

    /**
     * 准备下一个重试采用退避到超时。
     * @param 错误的最后一次尝试错误代码。
     * @throws VolleyError In the event that the retry could not be performed (for example if we
     * ran out of attempts), the passed in error is thrown.
     */
    public void retry(VolleyError error) throws VolleyError;
}
