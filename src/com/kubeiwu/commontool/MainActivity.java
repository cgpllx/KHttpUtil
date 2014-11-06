package com.kubeiwu.commontool;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.kubeiwu.commontool.khttp.DefaultRetryPolicy;
import com.kubeiwu.commontool.khttp.Request.Method;
import com.kubeiwu.commontool.khttp.RequestQueue;
import com.kubeiwu.commontool.khttp.Response.Listener;
import com.kubeiwu.commontool.khttp.requestimpl.JsonObjectRequest;
import com.kubeiwu.commontool.khttp.requestimpl.StringRequest;
import com.kubeiwu.commontool.khttp.toolbox.Volley;

public class MainActivity extends Activity {
	public static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		text2();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void text1() {
		String url="url";
		RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
		mQueue.add(new JsonObjectRequest(Method.GET, url, null, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				Log.d(TAG, "response : " + response.toString());
			}
		}, null).setShouldCache(true));
		mQueue.start();
	}
	public void text2() {
//		String url="http://video.konka2cloud.cn/client/GetTV";
		String url="http://www.google.com";
		RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
		mQueue.add(new StringRequest(url, null, null).setRetryPolicy(new DefaultRetryPolicy()));
		mQueue.start();
	}
}
