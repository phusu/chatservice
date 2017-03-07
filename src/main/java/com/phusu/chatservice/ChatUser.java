package com.phusu.chatservice;

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
