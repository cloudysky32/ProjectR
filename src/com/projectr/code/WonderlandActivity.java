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

import android.content.*;
import android.content.res.*;
import android.database.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.maps.*;
import com.projectr.code.http.*;
import com.projectr.code.utils.*;
import com.projectr.code.vars.*;

public class WonderlandActivity extends MapActivity implements OnClickListener {

	private MapView mapView;
	private MyLocationOverlay myLocationOverlay;

	private ImageButton questTypeButton;
	private ImageButton friendButton;
	private Button historyButton;
	private Button clueButton;
	private Button rankingButton;
	private Button everyQuestButton;
	private Button mainQuestButton;
	private Button eventQuestButton;
	private Button dailyQuestButton;
	private Button goCenterButton;
	private Button mylocButton;
	private Button aboutButton;
	private Button logoutButton;
	private ListView friendListView;
	private ListView notifyListView;
	private TextSwitcher notifySwitcher;
	private NotifyBarSwitcher notifyBar;

	private ArrayList<String> clueList = new ArrayList<String>();
	private final GeoPoint UOS = new GeoPoint((int) (37.583290 * 1E6), (int) (127.059031 * 1E6));

	@Override
	public void onCreate(Bundle savedIntanced) {
		super.onCreate(savedIntanced);
		setContentView(R.layout.activity_wonderland);

		mapView = (MapView) findViewById(R.id.map);

		myLocationOverlay = new MyLocationOverlay(this, mapView);
		
		friendListView = (ListView) findViewById(R.id.friendList);
		notifyListView = (ListView) findViewById(R.id.notifyList);
		questTypeButton = (ImageButton) findViewById(R.id.questTypeButton);
		friendButton = (ImageButton) findViewById(R.id.friendButton);
		historyButton = (Button) findViewById(R.id.historyButton);
		clueButton = (Button) findViewById(R.id.clueButton);
		rankingButton = (Button) findViewById(R.id.rankingButton);
		everyQuestButton = (Button) findViewById(R.id.everyQuestButton);
		mainQuestButton = (Button) findViewById(R.id.mainQuestButton);
		eventQuestButton = (Button) findViewById(R.id.eventQuestButton);
		dailyQuestButton = (Button) findViewById(R.id.dailyQuestButton);
		goCenterButton = (Button) findViewById(R.id.goCenterButton);
		mylocButton = (Button) findViewById(R.id.mylocButton);
		aboutButton = (Button) findViewById(R.id.menuAbout);
		logoutButton = (Button) findViewById(R.id.menuLogout);
		notifySwitcher = (TextSwitcher) findViewById(R.id.txtSwitcher);
		notifyBar = new NotifyBarSwitcher(notifySwitcher, this);

		notifyBar.setNotify();
		friendListView.setAdapter(new ArrayAdapter<String>(this, R.layout.layout_friendlist, clueList));

		notifySwitcher.setOnClickListener(this);
		questTypeButton.setOnClickListener(this);
		friendButton.setOnClickListener(this);
		historyButton.setOnClickListener(this);
		clueButton.setOnClickListener(this);
		rankingButton.setOnClickListener(this);
		everyQuestButton.setOnClickListener(this);
		mainQuestButton.setOnClickListener(this);
		eventQuestButton.setOnClickListener(this);
		dailyQuestButton.setOnClickListener(this);
		goCenterButton.setOnClickListener(this);
		mylocButton.setOnClickListener(this);
		aboutButton.setOnClickListener(this);
		logoutButton.setOnClickListener(this);

		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mapView.getController().setZoom(18);
				mapView.getController().animateTo(UOS);			
			}
		});

		GetQuest.getMainQuests(mapView);
		GetQuest.getEventQuests(mapView);
		GetQuest.getDailyQuests(mapView);
	}

	@Override
	public void onClick(View v) {
		if (v == questTypeButton) {
			MultiDirectionSlidingDrawer questTypeDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.questTypeDrawer);
			if (questTypeDrawer.isOpened()) {
				questTypeDrawer.animateClose();
				questTypeButton.setBackgroundResource(R.drawable.btn_rounding);
			} else {
				questTypeDrawer.animateOpen();
				questTypeButton.setBackgroundResource(R.drawable.btn_rounding_on);
			}
		} else if (v == historyButton) {
			startActivity(new Intent(this, HistoryActivity.class));
		} else if (v == clueButton) {
			MultiDirectionSlidingDrawer clueListDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.clueListDrawer);
			if (clueListDrawer.isOpened()) {
				clueListDrawer.animateClose();
				clueButton.setBackgroundResource(R.drawable.btn_gradient);
			} else {
				new GalleryTask().execute();
				clueListDrawer.animateOpen();
				clueButton.setBackgroundResource(R.drawable.btn_gradient_on);
			}
		} else if (v == rankingButton) {
			startActivity(new Intent(this, RankingActivity.class));
		} else if (v == friendButton) {
			RelativeLayout menu = (RelativeLayout) findViewById(R.id.menu);
			if(menu.getVisibility() == View.GONE){
				menu.setVisibility(View.VISIBLE);
				friendButton.setBackgroundResource(R.drawable.btn_rounding_on);
			} else {
				menu.setVisibility(View.GONE);
				friendButton.setBackgroundResource(R.drawable.btn_rounding);
			}
			/*
			 * TODO 친구리스트 버튼 
			 * 
			 * MultiDirectionSlidingDrawer friendListDrawer =
			 * (MultiDirectionSlidingDrawer)
			 * findViewById(R.id.friendListDrawer); if
			 * (friendListDrawer.isOpened()) { friendListDrawer.animateClose();
			 * friendButton.setBackgroundResource(R.drawable.btn_rounding); }
			 * else { friendListDrawer.animateOpen();
			 * friendButton.setBackgroundResource(R.drawable.btn_rounding_on); }
			 */
		} else if (v == notifySwitcher) {
			final MultiDirectionSlidingDrawer notifyListDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.notifyListDrawer);
			notifyListView.setAdapter(new ArrayAdapter<String>(this, R.layout.layout_notifylist, notifyBar.getNotifyList()));

			notifyListView.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						if (notifyListDrawer.isOpened()) {
							notifyListDrawer.animateClose();
						}
					}
					return true;
				}
			});

			if (notifyListDrawer.isOpened()) {
				notifyListDrawer.animateClose();
			} else {
				notifyListDrawer.animateOpen();
			}
		} else if (v == everyQuestButton) {
			mapView.getOverlays().clear();
			GetQuest.getMainQuests(mapView);
			GetQuest.getEventQuests(mapView);
			GetQuest.getDailyQuests(mapView);
		} else if (v == mainQuestButton) {
			mapView.getOverlays().clear();
			GetQuest.getMainQuests(mapView);
		} else if (v == eventQuestButton) {
			mapView.getOverlays().clear();
			GetQuest.getEventQuests(mapView);
		} else if (v == dailyQuestButton) {
			mapView.getOverlays().clear();
			GetQuest.getDailyQuests(mapView);
		} else if (v == goCenterButton) {
			mapView.getController().setZoom(18);
			mapView.getController().animateTo(UOS);
		} else if (v == mylocButton) {
			if (myLocationOverlay.isMyLocationEnabled()) {
				if (!mapView.getOverlays().contains(myLocationOverlay)) {
					mapView.getOverlays().add(myLocationOverlay);
					mapView.getController().animateTo(myLocationOverlay.getMyLocation());
				} else {
					myLocationOverlay.disableMyLocation();
				}
			} else {
				myLocationOverlay.enableMyLocation();
				mapView.getOverlays().add(myLocationOverlay);
				mapView.getController().animateTo(myLocationOverlay.getMyLocation());
			}
		} else if (v == aboutButton) {
			startActivity(new Intent(this, ProjectRActivity.class));
		} else if (v == logoutButton) {
			this.deleteFile("projectr_u");
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		}
	}

	// GalleryTask
	private class GalleryTask extends AsyncTask<Void, Void, Void> {

		private String URL = "http://cloudysky.iptime.org/projectr_getclue.php";
		private ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		private ArrayList<String> clueNameList = new ArrayList<String>();
		private ArrayList<String> clueUrlList = new ArrayList<String>();
		private boolean state;

		@Override
		protected void onPreExecute() {
			state = true;
			nameValuePairs.add(new BasicNameValuePair("id", GlobalVariables.getInstance().getUserId()));

			LoadingDialog.getInstance().showLoading(WonderlandActivity.this);
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
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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
						clueNameList.add(json_data.getString("clue_name"));
						clueUrlList.add(json_data.getString("clue_url"));
						
						if(!GetImage.downloadImage(json_data.getString("clue_name") + ".jpg", json_data.getString("clue_url"), WonderlandActivity.this)){
							state = false;
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
			if (state) {
				Gallery gallery = (Gallery) findViewById(R.id.clueGallery);
				final ImageAdapter ia = new ImageAdapter(WonderlandActivity.this);

				gallery.setAdapter(ia);
				gallery.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
						ia.callImageViewer(position);
					}
				});
			} else {
				Toast.makeText(WonderlandActivity.this, "서버와의 연결이 원활하지 않습니다", Toast.LENGTH_SHORT).show();
			}

			LoadingDialog.getInstance().hideLoading();
		}
	}

	public class ImageAdapter extends BaseAdapter {

		private Context context;
		private String imgData;
		private String geoData;
		private ArrayList<String> thumbsDataList;
		private ArrayList<String> thumbsIDList;
		private int itemBackground;

		ImageAdapter(Context context) {
			this.context = context;
			thumbsDataList = new ArrayList<String>();
			thumbsIDList = new ArrayList<String>();
			getThumbInfo(thumbsIDList, thumbsDataList);
			TypedArray a = obtainStyledAttributes(R.styleable.ClueGallery);
			itemBackground = a.getResourceId(R.styleable.ClueGallery_android_galleryItemBackground, 0);
		}

		public final void callImageViewer(int selectedIndex) {
			Intent i = new Intent(context, ClueImageActivity.class);
			String imgPath = getImageInfo(imgData, geoData,	thumbsIDList.get(selectedIndex));
			i.putExtra("filename", imgPath);
			startActivityForResult(i, 1);
		}

		public boolean deleteSelected(int sIndex) {
			return true;
		}

		public int getCount() {
			return thumbsIDList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(context);
				imageView.setLayoutParams(new Gallery.LayoutParams(200, 200));
				imageView.setAdjustViewBounds(false);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(1, 1, 1, 1);
			} else {
				imageView = (ImageView) convertView;
			}
			BitmapFactory.Options bo = new BitmapFactory.Options();
			bo.inSampleSize = 8;
			Bitmap bmp = BitmapFactory.decodeFile(thumbsDataList.get(position),	bo);
			Bitmap resized = Bitmap.createScaledBitmap(bmp, 70, 70, true);
			imageView.setImageBitmap(resized);
			imageView.setBackgroundResource(itemBackground);

			return imageView;
		}

		private void getThumbInfo(ArrayList<String> thumbsIDs, ArrayList<String> thumbsDatas) {
			String[] proj = { MediaStore.Images.Media._ID,
					MediaStore.Images.Media.DATA,
					MediaStore.Images.Media.DISPLAY_NAME,
					MediaStore.Images.Media.SIZE };
			
			Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,  MediaStore.Images.Media.DATA + " like ? ", new String[] {"%com.projectr.mokoz%"}, null);
//			Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,  "bucket_display_name='com.projectr.mokoz'", null, null);

			if (imageCursor != null && imageCursor.moveToFirst()) {
				String title;
				String thumbsID;
				String thumbsImageID;
				String thumbsData;
				String data;
				String imgSize;

				int thumbsIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
				int thumbsDataCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
				int thumbsImageIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
				int thumbsSizeCol = imageCursor.getColumnIndex(MediaStore.Images.Media.SIZE);
				int num = 0;
				do {
					thumbsID = imageCursor.getString(thumbsIDCol);
					thumbsData = imageCursor.getString(thumbsDataCol);
					thumbsImageID = imageCursor.getString(thumbsImageIDCol);
					imgSize = imageCursor.getString(thumbsSizeCol);
					num++;
					if (thumbsImageID != null) {
						thumbsIDs.add(thumbsID);
						thumbsDatas.add(thumbsData);
					}
				} while (imageCursor.moveToNext());
			}
			imageCursor.close();
			return;
		}

		private String getImageInfo(String ImageData, String Location, String thumbID) {
			String imageDataPath = null;
			String[] proj = { MediaStore.Images.Media._ID,
					MediaStore.Images.Media.DATA,
					MediaStore.Images.Media.DISPLAY_NAME,
					MediaStore.Images.Media.SIZE };

			Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, "_ID='" + thumbID + "'", null, null);

			if (imageCursor != null && imageCursor.moveToFirst()) {
				if (imageCursor.getCount() > 0) {
					int imgData = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
					imageDataPath = imageCursor.getString(imgData);
				}
			}
			imageCursor.close();
			return imageDataPath;
		}
	}

	private Boolean isFinish = false;
	private Timer timer = new Timer();

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		final Toast finishToast = Toast.makeText(this, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다", Toast.LENGTH_LONG);

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (GlobalVariables.getInstance().getQuest() == null) {
				if (!isFinish) {
					finishToast.show();
					isFinish = true;
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							finishToast.cancel();
							isFinish = false;
						}
					}, 2000);
				} else {
					finishToast.cancel();
					finish();
				}
			} else if (GlobalVariables.getInstance().getQuest().getVisivility() == View.VISIBLE) {
				GlobalVariables.getInstance().getQuest().hideBalloon();
			} else if (!isFinish) {
				finishToast.show();
				isFinish = true;
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						finishToast.cancel();
						isFinish = false;
					}
				}, 2000);
			} else {
				finishToast.cancel();
				finish();
			}
		} else if (keyCode == KeyEvent.KEYCODE_MENU){
			RelativeLayout menu = (RelativeLayout) findViewById(R.id.menu);
			if(menu.getVisibility() == View.GONE){
				menu.setVisibility(View.VISIBLE);
				friendButton.setBackgroundResource(R.drawable.btn_rounding_on);
			} else {
				menu.setVisibility(View.GONE);
				friendButton.setBackgroundResource(R.drawable.btn_rounding);
			}
		}

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		mapView.getOverlays().clear();
		GetQuest.getMainQuests(mapView);
		GetQuest.getEventQuests(mapView);
		GetQuest.getDailyQuests(mapView);
	
		if (!myLocationOverlay.isMyLocationEnabled()) {
			myLocationOverlay.enableMyLocation();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		if (myLocationOverlay.isMyLocationEnabled()) {
			myLocationOverlay.disableMyLocation();
		}
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
