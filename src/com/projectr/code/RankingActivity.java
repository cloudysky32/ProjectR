package com.projectr.code;

import java.io.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.message.*;
import org.apache.http.protocol.*;
import org.json.*;

import android.app.*;
import android.os.*;
import android.util.*;
import android.widget.*;

import com.projectr.code.http.*;
import com.projectr.code.utils.*;
import com.projectr.code.vars.*;

public class RankingActivity extends Activity {

	private ListView rankingListView;
	private TextView rankingUserId;
	private TextView rankingUserRank;
	
	@Override
	public void onCreate(Bundle savedIntanced) {
		super.onCreate(savedIntanced);
		setContentView(R.layout.activity_ranking);
		
		rankingUserId = (TextView) findViewById (R.id.rankingUserId);
		rankingUserId.setText(GlobalVariables.getInstance().getUserId());
		
		new GetHistoryTask().execute();
	}
	
	// GetRanking Task
	private class GetHistoryTask extends AsyncTask<Void, Void, Void> {
		
		private String URL = "http://cloudysky.iptime.org/projectr_getranking.php";		
		private ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		private boolean state = true;
		
		private ArrayList<String> userRank = new ArrayList<String>();
		private int myRank;
		
		@Override
		protected void onPreExecute() {
			LoadingDialog.getInstance().showLoading(RankingActivity.this);
			super.onPreExecute();
			
			nameValuePairs.add(new BasicNameValuePair("id", GlobalVariables.getInstance().getUserId()));
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
						
						sb.setLength(0);
						sb.append(json_data.getString("user_id"));
						userRank.add(json_data.getInt("rank") + "위\t" + "(" + json_data.getInt("score") + ")\t" + sb.replace(7, sb.length(), "*******"));
						if(json_data.getString("user_id").equals(GlobalVariables.getInstance().getUserId())) {
							myRank = json_data.getInt("rank");
						}
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
		protected void onPostExecute(Void unused) {
			rankingListView = (ListView) findViewById(R.id.rankingList);
			rankingUserRank = (TextView) findViewById(R.id.rankingUserRank);
			
			if(state) {
				rankingListView.setAdapter(new ArrayAdapter<String>(RankingActivity.this, R.layout.layout_rankinglist, userRank));
				rankingUserRank.setText(myRank + "위");
			} else {
				Toast.makeText(RankingActivity.this, "서버와의 연결이 원활하지 않습니다", Toast.LENGTH_SHORT).show();
			}
			
			LoadingDialog.getInstance().hideLoading();
		}
	}
}
