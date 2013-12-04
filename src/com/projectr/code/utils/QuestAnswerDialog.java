package com.projectr.code.utils;

import android.app.*;
import android.content.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.projectr.code.*;
import com.projectr.code.http.*;
import com.projectr.code.vars.*;

public class QuestAnswerDialog extends Dialog implements OnClickListener {
    private EditText questAnswer;
    private TextView quenstAnswerDialogName;
    private Button answerSendButton;
    private Button answerCancelButton;
    private Button reviewButton;
    private Context context;
    private Quest quest;
    private String URL;
   
    public QuestAnswerDialog(Context context, Quest quest) {
        super(context);
        this.context = context;
        this.quest = quest;
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.dialog_questanswer);
       
        questAnswer = (EditText)findViewById(R.id.questAnswer);
        quenstAnswerDialogName = (TextView)findViewById(R.id.quenstAnswerDialogName);
        answerSendButton = (Button)findViewById(R.id.answerSendButton);
        answerCancelButton = (Button)findViewById(R.id.answerCancelButton);
        reviewButton = (Button)findViewById(R.id.reviewButton);
        
        answerSendButton.setOnClickListener(this);
        answerCancelButton.setOnClickListener(this);
        reviewButton.setOnClickListener(this);
        quenstAnswerDialogName.setText(quest.getQuestName());
        
        switch(quest.getQuestType()){
        case Quest.MAIN:
        	URL = "http://cloudysky.iptime.org/projectr_checkanswer_main.php";
        	break;
        case Quest.EVENT:
        	URL = "http://cloudysky.iptime.org/projectr_checkanswer_event.php";
        	break;
        case Quest.DAILY:
        	URL = "http://cloudysky.iptime.org/projectr_checkanswer_daily.php";
        	break;
        }
    }
 
	@Override
	public void onClick(View v) {
		if (v == answerSendButton) {
			new AnswerCheckTask().execute();
		} else if (v == reviewButton) {
			context.startActivity(new Intent(context, QuestInfoActivity.class));
		} else if (v == answerCancelButton) {
			dismiss();
		}
	}
	
	// answerSendButton onClick Task
	private class AnswerCheckTask extends AsyncTask<Void, Void, Void> {
		
		GetData checkAnswer;
		
		@Override
		protected void onPreExecute(){
			
			if (TextUtils.isEmpty(questAnswer.getText())) {
				Toast.makeText(context, "답을 입력하지 않았습니다", Toast.LENGTH_SHORT).show();
			}
			
			LoadingDialog.getInstance().showLoading(context);
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {

			checkAnswer = new GetData(quest.getQuestId().toString(), questAnswer.getText().toString(), GlobalVariables.getInstance().getUserId(), URL);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused){

			if (checkAnswer.getState()){
				if (!checkAnswer.getMsgs().isEmpty()){
					quest.setAnswerInfo(checkAnswer.getMsgs().get(0));
					quest.setQuestState(2);
		
					dismiss();
					context.startActivity(new Intent(context, QuestInfoActivity.class));
				} else {
					Toast.makeText(context, "오답입니다", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(context, "서버와의 연결이 원할하지 않습니다", Toast.LENGTH_SHORT).show();
			}
			
			LoadingDialog.getInstance().hideLoading();
		}
	}
}
