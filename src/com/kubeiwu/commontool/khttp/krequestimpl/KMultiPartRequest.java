package com.kubeiwu.commontool.khttp.krequestimpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpEntity;

import com.kubeiwu.commontool.khttp.Response.ErrorListener;
import com.kubeiwu.commontool.khttp.Response.Listener;
import com.kubeiwu.commontool.khttp.exception.AuthFailureError;
import com.kubeiwu.commontool.khttp.krequestimpl.core.MultipartRequestParams;

/**
 * A request for making a Multi Part request 小文件上传效果比较好
 * 
 * @param <T>
 * 
 * @param <T>
 *            Response expected
 */
public abstract class KMultiPartRequest<T> extends KRequest<T> {

	private MultipartRequestParams params = null;
	private HttpEntity httpEntity = null;

	public KMultiPartRequest(int method, String url, Map<String, String> headers, MultipartRequestParams params, ErrorListener errorListener, Listener<T> listener) {
		super(method, url, headers, null, listener, errorListener);
		this.params = params;
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (params != null) {
			httpEntity = params.getEntity();
			try {
				httpEntity.writeTo(baos);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return baos.toByteArray();
	}

	@Override
	public String getBodyContentType() {
		return httpEntity.getContentType().getValue();
	}

}
