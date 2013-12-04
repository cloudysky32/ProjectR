package com.projectr.code.http;

import java.io.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.message.*;
import org.apache.http.protocol.*;
import org.json.*;

import android.util.*;

public class GetData {
	private String URL;
	private boolean state = true;
	private ArrayList<String> msgs = new ArrayList<String>();
	private ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	public GetData(String URL) {
		this.URL = URL;

		getDataFromHttp();
	}

	public GetData(String userId, String URL) {
		this.URL = URL;

		// the data to send
		nameValuePairs.add(new BasicNameValuePair("id", userId));

		getDataFromHttp();
	}

	public GetData(String userId, String password, String URL) {
		this.URL = URL;

		// the data to send
		nameValuePairs.add(new BasicNameValuePair("id", userId));
		nameValuePairs.add(new BasicNameValuePair("pwd", password));

		getDataFromHttp();
	}
	
	public GetData(String questId, String questAnswer, String userId, String URL) {
		this.URL = URL;

		// the data to send
		nameValuePairs.add(new BasicNameValuePair("id", questId));
		nameValuePairs.add(new BasicNameValuePair("pwd", questAnswer));
		nameValuePairs.add(new BasicNameValuePair("user_id", userId));

		getDataFromHttp();
	}

	public GetData(String userId, String password, String userUniv, String userMajor, String userGender, String URL) {
		this.URL = URL;

		// the data to send
		nameValuePairs.add(new BasicNameValuePair("id", userId));
		nameValuePairs.add(new BasicNameValuePair("pwd", password));
		nameValuePairs.add(new BasicNameValuePair("univ", userUniv));
		nameValuePairs.add(new BasicNameValuePair("major", userMajor));
		nameValuePairs.add(new BasicNameValuePair("gender", userGender));
		
		getDataFromHttp();
	}

	private void getDataFromHttp() {
		
		String result = null;
		InputStream is = null;
		StringBuffer sb = null;
		
		// http post
		try {
			HttpClient httpclient = HttpClientFactory.getThreadSafeClient();

			HttpPost httppost = new HttpPost(URL);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,	HTTP.UTF_8));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			
			// convert response to string
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
				sb = new StringBuffer();
				sb.append(reader.readLine() + "\n");
				String line = "0";
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
			} catch (Exception e) {
				Log.e("log_tag", "Error converting result " + e.toString());
			}

			// parse json data
			JSONArray jArray;
			try {
				jArray = new JSONArray(result);
				JSONObject json_data = null;

				for (int i = 0; i < jArray.length(); i++) {
					json_data = jArray.getJSONObject(i);
					msgs.add(json_data.getString("msg"));
				}
			} catch (JSONException e) {
				Log.e("log_tag", "Error parsing data! " + e.toString());
			}

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
			state = false;
		}
	}

	public ArrayList<String> getMsgs() {
		return msgs;
	}
	
	public boolean getState() {
		return state;
	}
}
