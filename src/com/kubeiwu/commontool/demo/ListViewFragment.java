package com.kubeiwu.commontool.demo;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kubeiwu.commontool.R;
import com.kubeiwu.commontool.khttp.DisplayImageOptions;
import com.kubeiwu.commontool.khttp.DisplayImageOptions.ImageScaleType;
import com.kubeiwu.commontool.khttp.KRequestQueueManager;

public class ListViewFragment extends ListFragment {
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		MyBaseAdapter adapter = new MyBaseAdapter();
		// adapter.setList(list);
//		ImageLoaderManager(getActivity());
		setListAdapter(adapter);
	}

	String[] data = { "http://www.sinaimg.cn/dy/slidenews/1_img/2014_46/2841_511731_909979.jpg",//
			"http://www.sinaimg.cn/dy/slidenews/1_img/2014_46/2841_511719_838317.jpg",//
			"http://www.sinaimg.cn/dy/slidenews/1_img/2014_46/2841_511725_496052.jpg",//
			"http://www.sinaimg.cn/dy/slidenews/1_img/2014_46/2841_511726_304157.jpg",//
			"http://www.sinaimg.cn/dy/slidenews/1_img/2014_46/2841_511721_385816.jpg",//
			"http://www.sinaimg.cn/dy/slidenews/1_img/2014_46/2841_511722_546215.jpg",//
			"http://www.sinaimg.cn/dy/slidenews/1_img/2014_46/2841_511723_382289.jpg",//
			"http://www.sinaimg.cn/dy/slidenews/1_img/2014_46/2841_511727_125400.jpg",//
			"http://www.sinaimg.cn/dy/slidenews/1_img/2014_46/2841_511730_111067.jpg",//
			"http://www.sinaimg.cn/dy/slidenews/1_img/2014_46/2841_511720_753857.jpg",//
			"http://www.sinaimg.cn/dy/slidenews/1_img/2014_46/2841_511728_400876.jpg",//
			"http://www.sinaimg.cn/dy/slidenews/1_img/2014_46/2841_511729_654797.jpg"//
	};

	class MyBaseAdapter extends BaseAdapter {
		List<String> list = new ArrayList<String>();

		public void setList(List<String> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return data.length*100;
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Hoder hoder;
			if (convertView == null||convertView.getTag()==null) {
				ImageView imageView=new ImageView(getActivity());
				convertView=imageView;
				hoder=new Hoder();
				hoder.imageView= imageView;
				convertView.setTag(hoder);
			}else{
				hoder=(Hoder) convertView.getTag();
			}
//			 hoder.imageView.setImageResource(R.drawable.aa);
			DisplayImageOptions options=new DisplayImageOptions.Builder().setImageScaleType(ImageScaleType.NONE).build();
//			KRequestQueueManager.getImageLoaderManager().displayImage(data[position%data.length], hoder.imageView  );
			KRequestQueueManager.getImageLoaderManager().displayImage(data[position%data.length], hoder.imageView,options);
//			mImageLoader.get(data[position%data.length], ImageLoader.getImageListener(hoder.imageView, R.drawable.ic_launcher, 0), 1800, 1800);

			return convertView;
		}
		class Hoder{
			ImageView imageView;
		}

	}

//	private RequestQueue mQueue;
//	private BitmapCache mBitmapCache;
//	private ImageLoader mImageLoader;
//
//	public void ImageLoaderManager(Context context) {
//		mQueue = Volley.newRequestQueue(context);
//		mBitmapCache = new BitmapCache(context);
//		mImageLoader = new ImageLoader(mQueue, mBitmapCache);
//
//	}
	// public void displayImage(String uri, ImageView imageView ) {
	// ImageListener listener = ImageLoader.getImageListener(imageView, option.getImageResForEmptyUri(), option.getImageResOnFail());
	// switch (option.getImageScaleType()) {
	// case ImageScaleType.NONE:
	// mImageLoader.get(uri, listener, 0, 0);
	// break;
	// default:
	// mImageLoader.get(uri, listener, imageView.getWidth(), imageView.getHeight());
	// break;
	// }
	// }

}
