package com.kubeiwu.commontool.khttp.cookiemassage;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class CopyOfCookieUtil {
	private static final String SET_COOKIE_KEY = "Set-Cookie";
	private static final String COOKIE_KEY = "Cookie";
	private static final String SESSION_COOKIE = "sessionid";

	private SharedPreferences _preferences;

	public CopyOfCookieUtil(Context context) {
		_preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * Checks the response headers for session cookie and saves it if it finds it.
	 * 
	 * @param headers
	 *            Response Headers.
	 */
	public final void checkSessionCookie(Map<String, String> headers) {
		// String cookie = headers.get("Set-Cookie");
		// UserManager.getInstance().setCookie(cookie);
		
		if (headers.containsKey(SET_COOKIE_KEY) && headers.get(SET_COOKIE_KEY)//
				.startsWith(SESSION_COOKIE)) {
			String cookie = headers.get(SET_COOKIE_KEY);
			if (cookie.length() > 0) {
				String[] splitCookie = cookie.split(";");
				String[] splitSessionId = splitCookie[0].split("=");
				cookie = splitSessionId[1];
				Editor prefEditor = _preferences.edit();
				prefEditor.putString(SESSION_COOKIE, cookie);
				prefEditor.commit();
			}
		}
	}

	/**
	 * Adds session cookie to headers if exists.
	 * 
	 * @param headers
	 */
	public final void addSessionCookie(Map<String, String> headers) {
		String sessionId = _preferences.getString(SESSION_COOKIE, "");
		if (sessionId.length() > 0) {
			StringBuilder builder = new StringBuilder();
			builder.append(SESSION_COOKIE);
			builder.append("=");
			builder.append(sessionId);
			if (headers.containsKey(COOKIE_KEY)) {
				builder.append("; ");
				builder.append(headers.get(COOKIE_KEY));
			}
			headers.put(COOKIE_KEY, builder.toString());
		}
	}
}
