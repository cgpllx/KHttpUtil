package com.kubeiwu.commontool.khttp.cookiemassage;

import java.util.Date;
import java.util.Map;

public interface CookieStore {
	/**
	 * 添加cookies
	 * 
	 * @param cookies
	 */
	void addCookie(String cookies);

	/**
	 * 删除cookies
	 */

	void clear();

	void clearExpired(Date d);

	/**
	 * 获取cookies
	 * 
	 * @return
	 */

	String getCookies();

	/**
	 * 检测headers中有没有cookies,有就保存
	 * 
	 * @param headers
	 *            Response Headers.
	 */

	void saveCookiesFromHeaders(Map<String, String> headers);

	/**
	 * 将cookies添加到已经存在的headers中
	 * 
	 * @param headers
	 */

	void addCookiesToHeaders(Map<String, String> headers);
}
