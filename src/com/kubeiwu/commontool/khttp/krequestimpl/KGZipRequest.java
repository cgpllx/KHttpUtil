package com.kubeiwu.commontool.khttp.krequestimpl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import com.kubeiwu.commontool.khttp.NetworkResponse;
import com.kubeiwu.commontool.khttp.Response;
import com.kubeiwu.commontool.khttp.exception.AuthFailureError;
import com.kubeiwu.commontool.khttp.exception.ParseError;
import com.kubeiwu.commontool.khttp.toolbox.HttpHeaderParser;

public class KGZipRequest extends KRequest<String> {

	public KGZipRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		super(method, url, null, null, listener, errorListener);
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("Accept-Encoding", "gzip");
		return map;
	}

	// Content-Encoding gzip
	// parse the gzip response using a GZIPInputStream
	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		StringBuffer output = new StringBuffer();
		try {
			GZIPInputStream gStream = new GZIPInputStream(new ByteArrayInputStream(response.data));
			InputStreamReader reader = new InputStreamReader(gStream);
			BufferedReader in = new BufferedReader(reader);
			String read;
			while ((read = in.readLine()) != null) {
				output.append(read);
			}
			reader.close();
			in.close();
			gStream.close();
		} catch (IOException e) {
			return Response.error(new ParseError(e));
		}
		return Response.success(output.toString(), HttpHeaderParser.parseCacheHeaders(response));
	}

	/**
	 * gzip解压方法
	 * 
	 * @param bContent
	 * @return
	 */
	public static byte[] unGZip(byte[] bContent) {

		byte[] data = new byte[1024];
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(bContent);
			GZIPInputStream pIn = new GZIPInputStream(in);
			DataInputStream objIn = new DataInputStream(pIn);

			int len = 0;
			int count = 0;
			while ((count = objIn.read(data, len, len + 1024)) != -1) {
				len = len + count;
			}
			byte[] trueData = new byte[len];
			System.arraycopy(data, 0, trueData, 0, len);

			objIn.close();
			pIn.close();
			in.close();
			return trueData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * zip解压方法
	 * 
	 * @param bContent
	 * @return
	 */
	public static byte[] unZip(byte[] bContent) {
		byte[] b = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bContent);
			ZipInputStream zip = new ZipInputStream(bis);
			while (zip.getNextEntry() != null) {
				byte[] buf = new byte[1024];
				int num = -1;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				while ((num = zip.read(buf, 0, buf.length)) != -1) {
					baos.write(buf, 0, num);
				}
				b = baos.toByteArray();
				baos.flush();
				baos.close();
			}
			zip.close();
			bis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
}