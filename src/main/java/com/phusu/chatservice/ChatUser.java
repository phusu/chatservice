package com.phusu.chatservice;

/**
 * ChatUser is an abstract class for different types of users.
 * For example, there can be a simple user with no authentication and
 * a user which is authenticated against Facebook.
 */
public abstract class ChatUser {
	
	private ClientConnection clientConnection;
	
	public ChatUser(ClientConnection clientConnection) {
		this.clientConnection = clientConnection;
	}
	
	public abstract String getName();
	
	public ClientConnection getClientConnection() {
		return this.clientConnection;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ChatUser) {
			ChatUser other = (ChatUser) obj;
			return this.getName().equals(other.getName());	
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
}
