package com.kubeiwu.commontool.khttp;


public abstract class CurrentDispatcher {

	public <T> T completion(Request<T> request, Response<T> response) {
		// If this request has canceled, finish it and don't deliver.
		if (request.isCanceled()) {
			request.finish("canceled-at-delivery");
			return null;
		}

		// If this is an intermediate response, add a marker, otherwise we're done
		// and the request can be finished.
		if (response.intermediate) {
			request.addMarker("intermediate-response");
		} else {
			request.finish("done");
		}
		if (response.isSuccess()) {
			T t = response.result;
			return t;
		}
		return null;
	}
	public abstract <T> T execute(Request<T> mRequest) ;
	public abstract void quit();
}
