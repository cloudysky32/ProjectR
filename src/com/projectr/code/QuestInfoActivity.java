package com.projectr.code;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.projectr.code.utils.*;
import com.projectr.code.vars.*;

public class QuestInfoActivity extends Activity {
		
	@Override
	public void onCreate(Bundle savedIntanced) {
		super.onCreate(savedIntanced);
		setContentView(R.layout.activity_questinfo);
		
		Quest quest = GlobalVariables.getInstance().getQuest();
		
		TextView npcName = (TextView) findViewById(R.id.npcName);
		ImageView NPC = (ImageView) findViewById(R.id.npcImg);
		TextSwitcher textSwitcher = (TextSwitcher) findViewById(R.id.questInfoSwitcher);
		final Button preInfoButton = (Button) findViewById(R.id.preInfoButton);
		final Button nextInfoButton = (Button) findViewById(R.id.nextInfoButton);
		final QuestInfoSwitcher questInfoSwitcher = new QuestInfoSwitcher(quest, textSwitcher, this);

		// TODO 퀘스트 NPC에 따라 다른 이미지가 뜨도록
		if(quest.getQuestNPC().equals("이휘재")){
			NPC.setImageResource(R.drawable.img_jones_lee);
		} else if(quest.getQuestNPC().equals("독백")) {
			NPC.setImageResource(R.drawable.img_clear);
		}
		
		npcName.setText(quest.getQuestNPC());	
		questInfoSwitcher.setQuestInfo();
				
		textSwitcher.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				questInfoSwitcher.nextText();
				if (questInfoSwitcher.getTextState() == 0) {
					preInfoButton.setText("");
					nextInfoButton.setText("\u003e");
				} else if (questInfoSwitcher.getTextState() == 1) {
					preInfoButton.setText("\u003c");
					nextInfoButton.setText("");
				} else {
					preInfoButton.setText("\u003c");
					nextInfoButton.setText("\u003e");
				}
			}
		});

		preInfoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				questInfoSwitcher.preText();
				if (questInfoSwitcher.getTextState() == 0) {
					preInfoButton.setText("");
					nextInfoButton.setText("\u003e");
				} else if (questInfoSwitcher.getTextState() == 1) {
					preInfoButton.setText("\u003c");
					nextInfoButton.setText("");
				} else {
					preInfoButton.setText("\u003c");
					nextInfoButton.setText("\u003e");
				}
			}
		});
		
		nextInfoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				questInfoSwitcher.nextText();
				if (questInfoSwitcher.getTextState() == 0) {
					preInfoButton.setText("");
					nextInfoButton.setText("\u003e");
				} else if (questInfoSwitcher.getTextState() == 1) {
					preInfoButton.setText("\u003c");
					nextInfoButton.setText("");
				} else {
					preInfoButton.setText("\u003c");
					nextInfoButton.setText("\u003e");
				}
			}
		});
	}
	
	public void finishQuestInfo(){
		finish();
	}
}
