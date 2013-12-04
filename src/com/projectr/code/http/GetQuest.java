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

import android.os.*;
import android.util.*;
import android.widget.*;

import com.google.android.maps.*;
import com.projectr.code.*;
import com.projectr.code.utils.*;
import com.projectr.code.vars.*;

public class GetQuest {

	private MapView mapView;
	private String URL;
	private int questType;
	private boolean state = true;
	private ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	public GetQuest(int questType, MapView mapView) {
		
		this.questType = questType;
		this.mapView = mapView;
		
		switch(questType) {
		case Quest.MAIN:
			URL = "http://cloudysky.iptime.org/projectr_getquests_main.php";
			break;
		case Quest.EVENT:
			URL = "http://cloudysky.iptime.org/projectr_getquests_event.php";
			break;
		case Quest.DAILY:
			URL = "http://cloudysky.iptime.org/projectr_getquests_daily.php";
			break;
		}
		
		// the data to send
		nameValuePairs.add(new BasicNameValuePair("id", GlobalVariables.getInstance().getUserId()));
		
		new GetDataFromHttpTask().execute();
	}

	private class GetDataFromHttpTask extends AsyncTask<Void, Void, Void> {
		
		private ArrayList<Quest> questList = new ArrayList<Quest>();
		
		@Override
		protected void onPreExecute(){
			LoadingDialog.getInstance().showLoading(mapView.getContext());
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
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
						questList.add(new Quest(json_data.getString("quest_id"),
								json_data.getString("quest_name"), json_data.getString("quest_info"), json_data.getString("npc"),
								new GeoPoint(json_data.getInt("lat"), json_data.getInt("lng")), json_data.getInt("state"), questType));
					}
				} catch (JSONException e) {
					Log.e("log_tag", "Error parsing data! " + e.toString());
				}

			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection " + e.toString());
				state = false;
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void unused){		
			if(state) {		
				for(int i = 0; i < questList.size(); i++){
					questList.get(i).setIcon(mapView);
				}
			} else {
				Toast.makeText(mapView.getContext(), "서버와의 연결이 원활하지 않습니다", Toast.LENGTH_LONG).show();
			}
			mapView.getController().scrollBy(1, 1);
			mapView.getController().scrollBy(-1, -1);
			questList.clear();
			
			LoadingDialog.getInstance().hideLoading();
		}
	}
	
	public static void getMainQuests(MapView mapView) {
		new GetQuest(Quest.MAIN, mapView);
	}
	
	public static void getEventQuests(MapView mapView) {
		new GetQuest(Quest.EVENT, mapView);
	}
	
	public static void getDailyQuests(MapView mapView) {
		new GetQuest(Quest.DAILY, mapView);
	}
}
