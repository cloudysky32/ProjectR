package com.projectr.code.utils.balloon;

import java.util.*;

import android.content.*;
import android.graphics.drawable.*;

import com.google.android.maps.*;
import com.projectr.code.*;
import com.projectr.code.utils.*;
import com.projectr.code.vars.*;

public class QuestOverlay extends BalloonItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> questOverlays = new ArrayList<OverlayItem>();
	private Context context;
	private Quest quest;

	public QuestOverlay(Quest quest, MapView mapView) {
		super(checkQuestType(quest, mapView), mapView);
		this.quest = quest;
		context = mapView.getContext();
	}

	private static Drawable checkQuestType(Quest quest, MapView mapView){		
		Drawable questMarker = null;

		switch(quest.getQuestType()) {
		case Quest.MAIN:
			switch(quest.getQuestState()) {
			case 0:
				questMarker = mapView.getResources().getDrawable(R.drawable.mark_m_exclamation);
				break;
			case 1:
				questMarker = mapView.getResources().getDrawable(R.drawable.mark_m_question);
				break;
			case 2:
				questMarker = null;
				break;
			}
			break;
		case Quest.EVENT:
			switch(quest.getQuestState()) {
			case 0:
				questMarker = mapView.getResources().getDrawable(R.drawable.mark_e_exclamation);
				break;
			case 1:
				questMarker = mapView.getResources().getDrawable(R.drawable.mark_e_question);
				break;
			case 2:
				questMarker = null;
				break;
			}
			break;
		case Quest.DAILY:
			switch(quest.getQuestState()) {
			case 0:
				questMarker = mapView.getResources().getDrawable(R.drawable.mark_d_exclamation);
				break;
			case 1:
				questMarker = mapView.getResources().getDrawable(R.drawable.mark_d_question);
				break;
			case 2:
				questMarker = null;
				break;
			}
			break;
		}
		int w = questMarker.getIntrinsicWidth(); 
		int h = questMarker.getIntrinsicHeight(); 
		questMarker.setBounds(-w / 2, -h, w / 2, 0); 
		
		return questMarker;
	}
	
	public void addOverlay(OverlayItem overlay) {
		questOverlays.add(overlay);
		populate();
	}
	
	public void removeOverlay(OverlayItem overlay){
		questOverlays.remove(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return questOverlays.get(i);
	}

	@Override
	public int size() {
		return questOverlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index) {

		if(quest.getQuestState() == 0){
			context.startActivity(new Intent(context, QuestInfoActivity.class));
		} else if(quest.getQuestState() == 1){
			QuestAnswerDialog questAnswerDialog = new QuestAnswerDialog(context, quest);
			questAnswerDialog.show();
		}
		quest.hideBalloon();
		
		return true;
	}
	
	@Override
	protected boolean onTap(int index){
		super.onTap(index);
		GlobalVariables.getInstance().setQuest(quest);
		
		return true;
	}
}