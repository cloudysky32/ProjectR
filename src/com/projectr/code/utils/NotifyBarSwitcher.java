package com.projectr.code.utils;

import java.util.*;

import android.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import android.widget.ViewSwitcher.ViewFactory;

import com.projectr.code.http.*;

public class NotifyBarSwitcher implements ViewFactory {
	
	private TextSwitcher notifySwitcher;
	private Context context;
	
	private int notifyCount = 0;
	private ArrayList<String> notifyList = new ArrayList<String>();
	
	public NotifyBarSwitcher (TextSwitcher notifySwitcher, Context context){
		this.notifySwitcher = notifySwitcher;
		this.context = context;
	}
	
	public void setNotify(){		
		Animation in = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
		Animation out = AnimationUtils.loadAnimation(context, R.anim.slide_out_right);
				
		notifySwitcher.setFactory(this);
		notifySwitcher.setInAnimation(in);
		notifySwitcher.setOutAnimation(out);

		getNotifyHander.sendEmptyMessage(0);
		nextNotifyHandler.sendEmptyMessageDelayed(0, 5000);
	}
	
	// getNotify()
	private class GetNotifyTask extends AsyncTask<Void, Void, Void> {
		
		String URL = "http://cloudysky.iptime.org/projectr_getnotifybar.php";
		GetData getNotify;
		
		@Override
		protected void onPreExecute(){
			notifyList.clear();
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			getNotify = new GetData(URL);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused){
			
			for(int i = 0; i < getNotify.getMsgs().size(); i++){
				notifyList.add(getNotify.getMsgs().get(i));
			}
			
			if(!notifyList.isEmpty()) {
				notifySwitcher.setText(notifyList.get(0).toString());
			}
		}
	}

	private Handler getNotifyHander = new Handler() {
		public void handleMessage(Message msg) {
			new GetNotifyTask().execute();
			this.sendEmptyMessageDelayed(0, 300000);
		}
	};
	
	private Handler nextNotifyHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(!notifyList.isEmpty()) {
				if (notifyCount == notifyList.size() - 1) {
					notifyCount = -1;
				}
				notifyCount++;
				notifySwitcher.setText(notifyList.get(notifyCount).toString());
			}
			this.sendEmptyMessageDelayed(0, 5000);
		}
	};

	@Override
	public View makeView() {
		TextView t = new TextView(context);
		t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
		t.setTextSize(13);
		t.setLines(1);
		t.setTextColor(ColorStateList.valueOf(0xFFDDDDDD));
		return t;
	}
	
	public ArrayList<String> getNotifyList(){
		return notifyList;
	}
}
