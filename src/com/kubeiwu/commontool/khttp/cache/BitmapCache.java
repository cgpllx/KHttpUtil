package com.kubeiwu.commontool.khttp.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.kubeiwu.commontool.khttp.cache.disk.DiskLruImageCache;
import com.kubeiwu.commontool.khttp.cache.memory.LruMemoryCache;
import com.kubeiwu.commontool.khttp.toolbox.ImageLoader.ImageCache;

/**
 * @author  cgpllx1@qq.com (www.kubeiwu.com)
 * @date    2014-8-7
 */
public class BitmapCache implements ImageCache {
	public Context context;
	public String uniqueName = "kubeiwu";
	public int diskCacheSize = 1024 * 1024 * 1024;   

	public BitmapCache(Context context) {
		this.context = context;
		mDiskLruImageCache = new DiskLruImageCache(context, uniqueName, diskCacheSize);
		mLruMemoryCachem = new LruMemoryCache();
	}

	LruMemoryCache mLruMemoryCachem;
	DiskLruImageCache mDiskLruImageCache;

	@Override
	public Bitmap getBitmap(String urlkey) {
		urlkey = Md5.generate(urlkey);
		if (TextUtils.isEmpty(urlkey))
			throw new IllegalStateException("image url is not right");
		Bitmap bitmap = mLruMemoryCachem.getBitmap(urlkey);
		if (bitmap == null) {
			bitmap = mDiskLruImageCache.getBitmap(urlkey);
			System.out.println("来自SD卡");
			if (bitmap != null && !bitmap.isRecycled()) {
				mLruMemoryCachem.putBitmap(urlkey, bitmap);
			}
		}else{
			System.out.println("来自内存");
		}
		return bitmap;
	}

	@Override
	public void putBitmap(String urlkey, Bitmap bitmap) {
		urlkey = Md5.generate(urlkey);
		if (TextUtils.isEmpty(urlkey))
			return;
		mLruMemoryCachem.putBitmap(urlkey, bitmap);
		if (!mDiskLruImageCache.containsKey(urlkey))
			mDiskLruImageCache.putBitmap(urlkey, bitmap);
	}

}
