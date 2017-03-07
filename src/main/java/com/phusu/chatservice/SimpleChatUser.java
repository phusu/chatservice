package com.phusu.chatservice;

/**
 * SimpleChatUser is a simple user implementation, containing only username.
 */
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
