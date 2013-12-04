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

public class HistoryActivity extends Activity{

	private ExpandableListView historyListView;
	private TextView historyUserId;
	private TextView historyTotalScore;
	private int totalScore = 0;
	
	@Override
	public void onCreate(Bundle savedIntanced) {
		super.onCreate(savedIntanced);
		setContentView(R.layout.activity_history);
		
		historyUserId = (TextView) findViewById (R.id.historyUserId);
		historyUserId.setText(GlobalVariables.getInstance().getUserId());

		new GetHistoryTask().execute();
	}
	
	// GetHistory Task
	private class GetHistoryTask extends AsyncTask<Void, Void, Void> {
		
		private String URL = "http://cloudysky.iptime.org/projectr_gethistory.php";		
		private ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		private boolean state = true;
		
		private ArrayList<String> questName = new ArrayList<String>();
		private ArrayList<String> questInfo = new ArrayList<String>();
		private ArrayList<String> answerInfo = new ArrayList<String>();
		
		@Override
		protected void onPreExecute() {
			LoadingDialog.getInstance().showLoading(HistoryActivity.this);
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
						questName.add(json_data.getString("quest_name") + " (" + json_data.getInt("score") + "점)");
						questInfo.add(json_data.getString("quest_info"));
						answerInfo.add(json_data.getString("answer_info"));
						totalScore += json_data.getInt("score");
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
			historyListView = (ExpandableListView) findViewById (R.id.historyList);
			historyTotalScore = (TextView) findViewById (R.id.historyTotalScore);
			
			if(state) {
				historyTotalScore.setText(totalScore + " 점");
				
		        SimpleExpandableListAdapter adapter= new SimpleExpandableListAdapter(
		        		HistoryActivity.this,
		        		getGroupList(),
		        		R.layout.layout_historylist,
		        		new String[]{"historyQuestName"},
		        		new int[]{R.id.historyQuestName},
		        		getChildList(),
		        		R.layout.layout_historylist,
		        		new String[]{"historyQuestInfo"},
		        		new int[]{R.id.historyQuestInfo}
		        ); 
	        
		        historyListView.setAdapter(adapter);
		        historyListView.setGroupIndicator(HistoryActivity.this.getResources().getDrawable(R.drawable.icon_expandable_listview));
			} else {
				Toast.makeText(HistoryActivity.this, "서버와의 연결이 원활하지 않습니다", Toast.LENGTH_SHORT).show();
			}
			
			LoadingDialog.getInstance().hideLoading();
		}
		
	    public List getGroupList(){
	    	String[] group = questName.toArray(new String[questName.size()]);
	    	List groupList = new ArrayList<HashMap>();
	    	
	    	for(int i=0;i<group.length;i++){
	    		HashMap map = new HashMap();
	    		map.put("historyQuestName", group[i]);
	    		groupList.add(map);
	    	}
	    	return groupList;
	    }
	    public List getChildList(){
	    	String[] childQ = questInfo.toArray(new String[questInfo.size()]);
	    	String[] childA = answerInfo.toArray(new String[answerInfo.size()]);
	    	List childList = new ArrayList<List>();

	    	for(int i = 0; i < childQ.length; i++) {
	    		List grandChildList = new ArrayList<HashMap>();
	    	
		    	HashMap mapQ = new HashMap();
		    	mapQ.put("historyQuestInfo", childQ[i]);
		    	grandChildList.add(mapQ);
		    
		    	HashMap mapA = new HashMap();
		    	mapA.put("historyQuestInfo", childA[i]);
		    	grandChildList.add(mapA);
		    	
	    		childList.add(grandChildList);
	    	}
	    	
	    	return childList;
	    }	    
	}
}