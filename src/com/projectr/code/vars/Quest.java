package com.projectr.code.vars;

import com.google.android.maps.*;
import com.projectr.code.utils.balloon.*;

public class Quest {
	public static final int MAIN = 0;
	public static final int EVENT = 1;
	public static final int DAILY = 2;

	private int questType;
	private int questState;
	private String questId;
	private String questName;
	private String questInfo;
	private String answerInfo;
	private String questNPC;
	private GeoPoint questLoc;
	
	private QuestOverlay questOverlay;
	private OverlayItem overlayItem; 
	
	public Quest(String questId, String questName, String questInfo, String questNPC, GeoPoint questLoc, int questState, int questType) {
		this.questState = questState;
		this.questId = questId;
		this.questName = questName;
		this.questInfo = questInfo;
		this.questNPC = questNPC;
		this.questLoc = questLoc;
		this.questType = questType;
	}
	
	public void setIcon(MapView mapView) {
		if(questState != 2){
			StringBuffer sb = new StringBuffer(questInfo);
			sb.setLength(32);
			
			questOverlay = new QuestOverlay(this, mapView);
			overlayItem = new OverlayItem(questLoc, questName, sb.substring(0, 20) + "...");
			
			questOverlay.addOverlay(overlayItem);
			mapView.getOverlays().add(questOverlay);
		}
	}
	
	public void refreshIcon(MapView mapView) {
		questOverlay.removeOverlay(overlayItem);
		mapView.getOverlays().remove(questOverlay);
		setIcon(mapView);
		
		mapView.getController().scrollBy(1, 1);
		mapView.getController().scrollBy(-1, -1);
	}
	
	public int getVisivility(){
		return questOverlay.getVisivility();
	}
	
	public void hideBalloon(){
		questOverlay.hideBalloon();
	}
	
	public String getQuestId(){
		return this.questId;
	}
	
	public String getQuestName(){
		return this.questName;
	}
	
	public String getQuestInfo(){
		return this.questInfo;
	}
	
	public String getQuestNPC(){
		return this.questNPC;
	}
	
	public GeoPoint getQuestLoc(){
		return this.questLoc;
	}
	
	public int getQuestState(){
		return this.questState;
	}
	
	public int getQuestType(){
		return this.questType;
	}
	
	public String getAnswerInfo(){
		return this.answerInfo;
	}
	
	public void setQuestState(int state) {
		this.questState = state;
	}
	
	public void setAnswerInfo(String answerInfo) {
		this.answerInfo = answerInfo;
	}
}
