package com.projectr.code.vars;


// Singleton design pattern
public class GlobalVariables {
	private String userId;
	private Quest quest;
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setQuest(Quest quest) {
		this.quest = quest;
	}

	public Quest getQuest(){
		return quest;
	}

	private static GlobalVariables instance = null;

	public static synchronized GlobalVariables getInstance() {
		if (null == instance) {
			instance = new GlobalVariables();
		}
		return instance;
	}
}