package com.kubeiwu.commontool.khttp.cookiemassage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public class CookieUtils implements CookieStore {
	private static final String SET_COOKIE_KEY = "Set-Cookie";
	private static final String COOKIE_KEY = "Cookie";
	private static final String COOKIE_PREFS = "CookiePrefsFile";
	private static final String SESSION_COOKIE = "sessionid";
	private SharedPreferences _preferences;

	public CookieUtils(Context context) {
		_preferences = context.getSharedPreferences(COOKIE_PREFS, 0);
	}

	/**
	 * 检测headers中有没有cookies,有就保存
	 * 
	 * @param headers
	 *            Response Headers.
	 */

	public final void saveCookiesFromHeaders(Map<String, String> headers) {
		if (headers.containsKey(SET_COOKIE_KEY) && headers.get(SET_COOKIE_KEY).startsWith(SESSION_COOKIE)) {
			String cookie = headers.get(SET_COOKIE_KEY);
			if (!TextUtils.isEmpty(cookie)) {
				Editor prefEditor = _preferences.edit();
				prefEditor.putString(COOKIE_KEY, cookie);
				prefEditor.commit();
			}
		}
	}

	/**
	 * 将cookies添加到已经存在的headers中
	 * 
	 * @param headers
	 */

	public final void addCookiesToHeaders(Map<String, String> headers) {
		String sessionId = _preferences.getString(COOKIE_KEY, "");
		if (!TextUtils.isEmpty(sessionId)) {
			headers.put(COOKIE_KEY, sessionId);
		}
	}

	public Map<String, String> getCookiesWithHeaders() {
		Map<String, String> headers = new HashMap<>();
		String sessionId = _preferences.getString(COOKIE_KEY, "");
		if (!TextUtils.isEmpty(sessionId)) {
			headers.put(COOKIE_KEY, sessionId);
		}
		return headers;
	}

	@Override
	public void addCookie(String cookies) {
		if (!TextUtils.isEmpty(cookies)) {
			Editor prefEditor = _preferences.edit();
			prefEditor.putString(COOKIE_KEY, cookies);
			prefEditor.commit();
		}
	}

	@Override
	public void clear() {
		_preferences.edit().remove(COOKIE_KEY).commit();
	}

	@Override
	public void clearExpired(Date d) {
		// not user
	}

	@Override
	public String getCookies() {
		return _preferences.getString(COOKIE_KEY, "");
	}
}
