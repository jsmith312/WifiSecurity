package com.example.wifisecurity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class HTTPThread extends AsyncTask<HTTPHelper, Void, Boolean> {
	private HTTPHelper httpHelper; 
	private String DEBUG = "DEBUG";
	private boolean hasDefaultPW = false;
	private String Company;
	
	@SuppressLint("NewApi")
	protected Boolean doInBackground(HTTPHelper... params) {
		// TODO Auto-generated method stub
		httpHelper = params[0];
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://www.macvendorlookup.com/api/v2/"+httpHelper.getMac());
		try {
		    HttpResponse response = httpClient.execute(post);
		    HttpEntity entity = response.getEntity();
		    String responseString = EntityUtils.toString(entity, "UTF-8");
		    String jsonObj = responseString.substring(1,responseString.length()-1);
		    JSONObject result = new JSONObject(jsonObj);
		    Company =result.getString("company");
		    Log.d(DEBUG, responseString.toString());
		    Log.d(DEBUG, Company);
		} catch (ClientProtocolException e) {
		    // Log exception
		    e.printStackTrace();
		} catch (IOException e) {
		    // Log exception
		    e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// replace the first 2 parameters with the API call that will 
		// send back the username and password. Once server is setup.
		try {
			HttpClient httpClient2 = new DefaultHttpClient();
			HttpPost post2 = new HttpPost("http://52.89.45.40/routepass.py");
			List<NameValuePair> arguments = new ArrayList<NameValuePair>();
	        arguments.add(new BasicNameValuePair("manufacturer", Company));
	        post2.setEntity(new UrlEncodedFormEntity(arguments));
			
	        HttpResponse response2 = httpClient2.execute(post2);
		    HttpEntity entity2 = response2.getEntity();
		    String responseString2 = EntityUtils.toString(entity2, "UTF-8");
		    Log.d(DEBUG, responseString2.toString());
			JSONObject dataresult = new JSONObject(responseString2);
			JSONArray arr = dataresult.getJSONArray("test");
			for (int i = 0; i < arr.length(); i++) {
				JSONObject up = arr.getJSONObject(i);
				String user = up.getString("user");
				String pass = up.getString("pass");
				httpHelper.setPassword(pass);
				httpHelper.setUsername(user);
				int resp = httpHelper.getResponse();
				Log.d(DEBUG, "USER:" + httpHelper.getUsername() + " PASS: " + httpHelper.getPassword());
				Log.d(DEBUG, "USER:" + user + " PASS: " + pass + " RESPONSE "+resp);
				if (resp != 401) {
					return true; // has default pw
				} 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	protected void onPostExecute(Boolean result) {
        // This is executed in main Thread, use the result
		if(result == true) {
			Toast.makeText(httpHelper.getContext(), "Using default password",
				   Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(httpHelper.getContext(), "Not using default password",
					   Toast.LENGTH_LONG).show();	
		}
    }
}
