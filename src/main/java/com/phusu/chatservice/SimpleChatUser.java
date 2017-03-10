package com.phusu.chatservice;

/**
 * SimpleChatUser is a simple user implementation, containing only username.
 */
public class SimpleChatUser extends ChatUser {
	
	private String userName;
	
	public SimpleChatUser(ClientConnection clientConnection, String userName) {
		super(clientConnection);

		if (userName == null)
			throw new NullPointerException("User name was null.");
		if (userName.isEmpty())
			throw new IllegalArgumentException("User name cannot be empty.");
		
		this.userName = userName;
	}

	@Override
	public String getName() {
		return userName;
	}

}
