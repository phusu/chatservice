package com.phusu.chatservice;

import java.util.HashSet;
import java.util.Set;

/**
 * ChatRoom implements a chat room. 
 */
public class ChatRoom {
	private String name;
	private ChatRoomType type;
	private String topic;
	private Set<ChatUser> users;
	
	public ChatRoom(String name, ChatRoomType type) {
		if (name == null || type == null)
			throw new NullPointerException("Constructor argument was null.");
		
		this.name = name;
		this.type = type;
		this.users = new HashSet<ChatUser>();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ChatRoom) {
			ChatRoom other = (ChatRoom) obj;
			return this.name.equals(other.getName());
		}
		return false;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public ChatRoomType getType() {
		return type;
	}

	public void setTopic(String topic) {
		if (topic == null)
			throw new NullPointerException("Topic was null.");
		this.topic = topic;
	}
	
	public String getTopic() {
		return topic;
	}

	public Set<ChatUser> getUsers() {
		return users;
	}
	
	public void addUser(ChatUser user) {
		if (user == null)
			throw new NullPointerException("User was null.");
		
		boolean isUnique = users.add(user);
		if (!isUnique) {
			throw new IllegalArgumentException("User already exists.");
		}
	}
	
	public void deleteUser(ChatUser user) {
		if (user == null)
			throw new NullPointerException("User was null.");
		
		boolean isInTheSet = users.remove(user);
		if (!isInTheSet) {
			throw new IllegalArgumentException("User doesn't exist.");
		}
	}
}
