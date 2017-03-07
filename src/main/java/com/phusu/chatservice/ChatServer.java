package com.phusu.chatservice;

import java.util.HashSet;
import java.util.Set;

public class ChatServer {
	private Set<ChatRoom> chatRooms;
	private Set<ChatUser> users;
	
	public ChatServer() {
		this.chatRooms = new HashSet<ChatRoom>();
		this.users = new HashSet<ChatUser>();
	}
	
	public Set<ChatRoom> listRooms() {
		return chatRooms;
	}

	public void addRoom(ChatRoom room) {
		if (room == null)
			throw new NullPointerException("Room was null.");
		
		boolean isUnique = chatRooms.add(room);
		if (!isUnique)
			throw new IllegalArgumentException("Room already exists.");
	}

	public void deleteRoom(ChatRoom room) {
		if (room == null)
			throw new NullPointerException("Room was null.");
		
		boolean isInTheSet = chatRooms.remove(room);
		if (!isInTheSet)
			throw new IllegalArgumentException("Room doesn't exist.");
	}
	
	public Set<ChatUser> listUsers() {
		return users;
	}

	public void addUser(ChatUser user) {
		if (user == null)
			throw new NullPointerException("User was null.");
		
		boolean isUnique = users.add(user);
		if (!isUnique)
			throw new IllegalArgumentException("User already exists.");
	}

	public void deleteUser(ChatUser user) {
		if (user == null)
			throw new NullPointerException("User was null.");
		
		boolean isInTheSet = users.remove(user);
		if (!isInTheSet)
			throw new IllegalArgumentException("User doesn't exist.");
	}
	
	public static void main(String[] args) {
		
	}
	
}
