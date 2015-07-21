//2014-8-8
package com.kubeiwu.commontool.khttp;

import android.content.Context;

import com.kubeiwu.commontool.khttp.cookiemassage.CookieStore;
import com.kubeiwu.commontool.khttp.cookiemassage.CookieStoreManager;
import com.kubeiwu.commontool.khttp.cookiemassage.CookieUtils;
import com.kubeiwu.commontool.khttp.toolbox.KHttpUtil;

/**
 * @author cgpllx1@qq.com (www.kubeiwu.com)
 * @date 2014-8-8
 */
public class KRequestQueueManager {
	private static RequestQueue mQueue = null;
	private static KRequestQueueManager mKRequestQueueManager;
	private static ImageLoaderManager mImageLoaderManager;

	private KRequestQueueManager() {
		// not use
	}

	public static KRequestQueueManager getInstance() {
		if (mKRequestQueueManager == null) {
			mKRequestQueueManager = new KRequestQueueManager();
		}
		return mKRequestQueueManager;
	}

	public void init(Context context) {
		mQueue = KHttpUtil.newRequestQueue(context);
		mImageLoaderManager = new ImageLoaderManager(context, mQueue);

		CookieStoreManager.getInstance().init(context);// 这里耦合比较高，要修改
	}

	public static RequestQueue getRequestQueue() {
		if (mQueue == null) {
			throw new IllegalArgumentException("You must initialize KRequestQueueManager");
		}
		return mQueue;
	}

	public static ImageLoaderManager getImageLoaderManager() {
		if (mImageLoaderManager == null) {
			throw new IllegalArgumentException("You must initialize KRequestQueueManager");
		}
		return mImageLoaderManager;
	}

}
