package com.kubeiwu.commontool.khttp.cookiemassage;

import android.content.Context;

public class CookieStoreManager {
	private static CookieStoreManager mKVolleyManager;
	private static CookieStore cookieStore;

	private CookieStoreManager() {
		// not use
	}

	public static CookieStoreManager getInstance() {
		if (mKVolleyManager == null) {
			mKVolleyManager = new CookieStoreManager();
		}
		return mKVolleyManager;
	}

	public void init(Context context) {
		cookieStore = new CookieUtils(context);
	}

	// TODO 这里耦合比较高，要修改
	public static CookieStore getCookieStore() {
		if (cookieStore == null) {
			throw new IllegalArgumentException("You must initialize KVolleyManager");
		}
		return cookieStore;
	}
}
