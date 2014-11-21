package com.kubeiwu.commontool.khttp.krequestimpl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import android.text.TextUtils;

import com.kubeiwu.commontool.khttp.NetworkResponse;
import com.kubeiwu.commontool.khttp.Response;
import com.kubeiwu.commontool.khttp.Response.ErrorListener;
import com.kubeiwu.commontool.khttp.Response.Listener;
import com.kubeiwu.commontool.khttp.exception.ParseError;
import com.kubeiwu.commontool.khttp.toolbox.HttpHeaderParser;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class KDownloadRequest extends KRequest<String> {
	private final String mDownloadPath;

	/**
	 * 下载请求
	 * 
	 * @param url
	 *            地址
	 * @param download_path
	 *            文件保存地址
	 * @param listener
	 * @param errorListener
	 */
	public KDownloadRequest(int method, String url, String download_path, Map<String, String> headers, Map<String, String> params, Listener<String> listener, ErrorListener errorListener) {
		super(method, url, headers, params, listener, errorListener);
		mDownloadPath = download_path;
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String parsed = null;
		try {
			byte[] data = response.data;
			// convert array of bytes into file
			FileOutputStream fileOuputStream = new FileOutputStream(mDownloadPath);
			fileOuputStream.write(data);
			fileOuputStream.close();
			parsed = mDownloadPath;
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (FileNotFoundException e) {
			return Response.error(new ParseError(e));
		} catch (IOException e) {
			return Response.error(new ParseError(e));
		} finally {
			if (TextUtils.isEmpty(parsed)) {
				parsed = "";
			}
		}
		// 返回的是文件路径
		return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
	}
}