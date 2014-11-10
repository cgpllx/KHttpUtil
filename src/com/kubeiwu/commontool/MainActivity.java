package com.kubeiwu.commontool;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.kubeiwu.commontool.khttp.DefaultRetryPolicy;
import com.kubeiwu.commontool.khttp.Request.Method;
import com.kubeiwu.commontool.khttp.RequestQueue;
import com.kubeiwu.commontool.khttp.Response.ErrorListener;
import com.kubeiwu.commontool.khttp.Response.Listener;
import com.kubeiwu.commontool.khttp.exception.AuthFailureError;
import com.kubeiwu.commontool.khttp.exception.VolleyError;
import com.kubeiwu.commontool.khttp.krequestimpl.KGsonObjectRequest;
import com.kubeiwu.commontool.khttp.krequestimpl.KJsonObjectRequest;
import com.kubeiwu.commontool.khttp.requestimpl.JsonObjectRequest;
import com.kubeiwu.commontool.khttp.requestimpl.StringRequest;
import com.kubeiwu.commontool.khttp.toolbox.Volley;
import com.kubeiwu.commontool.khttp.toolbox.notused.RequestFuture;

public class MainActivity extends Activity {
	public static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		text4();
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
		String url = "url";
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
		// String url="http://video.konka2cloud.cn/client/GetTV";
		String url = "http://www.google.com";
		RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
		mQueue.add(new StringRequest(url, new Listener<String>() {
			@Override
			public void onResponse(String response) {

			}
		}).setRetryPolicy(new DefaultRetryPolicy()));
		mQueue.start();
	}

	public void text3() {
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("AccountLogin[username]", "echo");
		map.put("AccountLogin[password]", "123456");
		// String url="http://video.konka2cloud.cn/client/GetTV";
		String url = "http://video.konkacloud.cn/account/account/login";
	
		RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
		ErrorListener lin = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				System.out.println("结果:" + error.toString());
			}

		};
		mQueue.add(new JsonObjectRequest(Method.POST, url, null, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				System.out.println("结果:" + response.toString());
			}
		}, lin) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return map;
			}
		});
		mQueue.start();
	}

	public void text4() {
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("AccountLogin[username]", "echo");
		map.put("AccountLogin[password]", "123456");
		// String url="http://video.konka2cloud.cn/client/GetTV";
		JSONObject jsonObject=new JSONObject(map);
		
		String url = "http://video.konkacloud.cn/account/account/login";
		RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
		ErrorListener lin = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				System.out.println("结果:" + error.toString());
			}

		};
		mQueue.add(new JsonObjectRequest(Method.POST, url, jsonObject, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				System.out.println("结果:" + response.toString());
			}
		}, lin));
		mQueue.start();
	}
	public void text5() {
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("AccountLogin[username]", "echo");
		map.put("AccountLogin[password]", "123456");
		// String url="http://video.konka2cloud.cn/client/GetTV";
		JSONObject jsonObject=new JSONObject(map);
		RequestFuture<JSONObject> requestFuture=RequestFuture.newFuture();
		String url = "http://video.konkacloud.cn/account/account/login";
		RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
		ErrorListener lin = new ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				System.out.println("结果:" + error.toString());
			}
			
		};
		mQueue.add(new KGsonObjectRequest<MainActivity>(Method.GET, url, null, null, new Listener<MainActivity>() {

			@Override
			public void onResponse(MainActivity response) {
				// TODO Auto-generated method stub
				
			}
		}, null));
		mQueue.start();
	}
	public void text6() {
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("AccountLogin[username]", "echo");
		map.put("AccountLogin[password]", "123456");
		// String url="http://video.konka2cloud.cn/client/GetTV";
		JSONObject jsonObject=new JSONObject(map);
		
		String url = "http://video.konkacloud.cn/account/account/login";
		RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
		ErrorListener lin = new ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				System.out.println("结果:" + error.toString());
			}
			
		};
		mQueue.add(new KGsonObjectRequest<MainActivity>(Method.GET, url, null, null, new Listener<MainActivity>() {
			
			@Override
			public void onResponse(MainActivity response) {
				// TODO Auto-generated method stub
				
			}
		}, null));
		mQueue.start();
	}
}
