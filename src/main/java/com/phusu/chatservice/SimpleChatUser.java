package com.phusu.chatservice;

public class SimpleChatUser extends ChatUser {
	
	private String userName;
	
	public SimpleChatUser(String userName) {
		this.userName = userName;
	}

	@Override
	public String getName() {
		return userName;
	}

}
