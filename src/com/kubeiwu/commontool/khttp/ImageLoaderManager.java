package com.kubeiwu.commontool.khttp;

import android.content.Context;
import android.widget.ImageView;

import com.kubeiwu.commontool.khttp.DisplayImageOptions.ImageScaleType;
import com.kubeiwu.commontool.khttp.cache.BitmapCache;
import com.kubeiwu.commontool.khttp.toolbox.ImageLoader;
import com.kubeiwu.commontool.khttp.toolbox.ImageLoader.ImageListener;

/**
 * @author  cgpllx1@qq.com (www.kubeiwu.com)
 * @date    2014-8-7
 */
public class ImageLoaderManager {
	private BitmapCache mBitmapCache;
	private ImageLoader mImageLoader;

	public ImageLoaderManager(Context context,RequestQueue mQueue) {
		mBitmapCache = new BitmapCache(context);
		mImageLoader = new ImageLoader(mQueue, mBitmapCache);
	}

	public void displayImage(String uri, ImageView imageView) {
		this.displayImage(uri, imageView, DisplayImageOptions.createSimple());
	}

	public void displayImage(String uri, ImageView imageView, DisplayImageOptions option) {
		ImageListener listener = ImageLoader.getImageListener(imageView, option.getImageResForEmptyUri(), option.getImageResOnFail());
		switch (option.getImageScaleType()) {
		case ImageScaleType.NONE:
			mImageLoader.get(uri, listener, 0, 0);
			break;
		default:
			mImageLoader.get(uri, listener, imageView.getWidth(), imageView.getHeight());
			break;
		}
	}

	public void loadImage(String uri, ImageListener listener) {
		this.loadImage(uri, listener, 0, 0);
	}

	public void loadImage(String uri, ImageListener listener, int maxWidth, int maxHeight) {
		mImageLoader.get(uri, listener, maxWidth, maxHeight);
	}
}
