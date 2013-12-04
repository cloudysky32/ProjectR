package com.projectr.code.utils;

import java.util.*;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import android.widget.ViewSwitcher.ViewFactory;

import com.projectr.code.*;
import com.projectr.code.http.*;
import com.projectr.code.vars.*;

public class QuestInfoSwitcher implements ViewFactory{
	
	private TextSwitcher questInfoSwitcher;
	private AlertDialog questConfirm = null;
	private Context context;
	
	private Quest quest;
	private int textCount = 0;
	private ArrayList<String> questInfo;
	private int INIT_LENGTH;
	
	public QuestInfoSwitcher (Quest quest, TextSwitcher questInfoSwitcher, Context context){
		this.quest = quest;
		this.questInfoSwitcher = questInfoSwitcher;
		this.context = context;
		
		if(quest.getQuestState() == 2) {
			this.questInfo = splitString(quest.getAnswerInfo());
		} else {
			this.questInfo = splitString(quest.getQuestInfo());
		}
	}
	
	public void setQuestInfo(){		
		questInfoSwitcher.setFactory(this);
		questInfoSwitcher.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_right));
		questInfoSwitcher.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_left));
		questInfoSwitcher.setText(questInfo.get(0).toString());
	}
	
	public void preText() {		
		questInfoSwitcher.setInAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left));
		questInfoSwitcher.setOutAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right));
		if (textCount > 0) {
			textCount--;
			questInfoSwitcher.setText(questInfo.get(textCount).toString());
		}
	}
	
	public void nextText() {
		questInfoSwitcher.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_right));
		questInfoSwitcher.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_left));
		if (textCount < questInfo.size()-1) {
			textCount++;
			questInfoSwitcher.setText(questInfo.get(textCount).toString());
		} else {
			if(quest.getQuestState() == 0){		
				questConfirm = createDialog();
				questConfirm.show();
			} else if (quest.getQuestState() == 1) {
				((Activity) context).finish();
			} else if (quest.getQuestState() == 2) {
				((Activity) context).finish();
			}
		}
	}
	
	public int getTextState(){
		if(textCount == 0) {
			return 0;
		} else if (textCount == questInfo.size()-1) {
			return 1;
		}
		return 2;
	}
	
	private ArrayList<String> splitString(String str){
		ArrayList<String> strList = new ArrayList<String>();
		StringBuffer sb = new StringBuffer(str);
		
		DisplayMetrics metrics = new DisplayMetrics(); 
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics); 
		
		int displayWidth = metrics.widthPixels - (95 * ((int)metrics.density));
		INIT_LENGTH = (displayWidth / 20) * 3;
		
		if(str.length() % INIT_LENGTH != 0){
			sb.setLength(str.length() - str.length() % INIT_LENGTH + INIT_LENGTH);
		}
		
		for(int i = 0; i < sb.length(); i += INIT_LENGTH) {
			strList.add(sb.substring(i, i + INIT_LENGTH).toString());
		}
		
		return strList;
	}
	
	private class AcceptQuestTask extends AsyncTask<Void, Void, Void> {
		
		String URL = null;
		GetData updateQuest;
		
		@Override
		protected void onPreExecute(){
			
			switch(quest.getQuestType()){
			case Quest.MAIN:
				URL = "http://cloudysky.iptime.org/projectr_updatequest_main.php";
				break;
			case Quest.EVENT:
				URL = "http://cloudysky.iptime.org/projectr_updatequest_event.php";
				break;
			case Quest.DAILY:
				URL = "http://cloudysky.iptime.org/projectr_updatequest_daily.php";
				break;
			}
			
			LoadingDialog.getInstance().showLoading(context);
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
	
        	updateQuest = new GetData(GlobalVariables.getInstance().getUserId(), quest.getQuestId(), URL);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused){
			
        	if(updateQuest.getState()) {
        		if(updateQuest.getMsgs().get(0).equals("1")) {   			
            		setDismiss(questConfirm);
            		((Activity) context).finish();
        		} else {
        			Toast.makeText(context, "서버와의 연결이 원할하지 않습니다", Toast.LENGTH_SHORT).show();
        		}
        	} else {
        		Toast.makeText(context, "서버와의 연결이 원할하지 않습니다", Toast.LENGTH_SHORT).show();
        	}
        	
			LoadingDialog.getInstance().hideLoading();
		}
	}
	
    private AlertDialog createDialog() {
   	
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setMessage("퀘스트를 수락하시겠습니까?");
        dialogBuilder.setCancelable(false);
          
        dialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            	new AcceptQuestTask().execute();
            }
        });
          
        dialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                setDismiss(questConfirm);
            }
        });
          
        return dialogBuilder.create();
    }
	
    private void setDismiss(Dialog dialog){
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
    
	@Override
	public View makeView() {
		AutoResizeTextView at = new AutoResizeTextView(context);
		at.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.LEFT);
		at.setTextColor(ColorStateList.valueOf(0xFFFFFFFF));

		return at;
	}
}
