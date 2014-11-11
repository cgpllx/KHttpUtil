package com.kubeiwu.commontool.khttp.krequestimpl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import com.kubeiwu.commontool.khttp.NetworkResponse;
import com.kubeiwu.commontool.khttp.Response;
import com.kubeiwu.commontool.khttp.exception.ParseError;
import com.kubeiwu.commontool.khttp.toolbox.HttpHeaderParser;

public class KGZipRequest extends KRequest<String> {

	public KGZipRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		super(method, url, null, null, listener, errorListener);
	}

 

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
			return Response.error(new ParseError());
		}
		return Response.success(output.toString(), HttpHeaderParser.parseCacheHeaders(response));
	}
}