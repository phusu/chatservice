package com.phusu.chatservice;

/**
 * ChatUser is an abstract class for different types of users.
 * For example, there can be a simple user with no authentication and
 * a user which is authenticated against Facebook.
 */
public abstract class ChatUser {
	public abstract String getName();
	
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
