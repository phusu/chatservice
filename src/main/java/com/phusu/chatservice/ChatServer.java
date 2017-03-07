package com.phusu.chatservice;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ChatServer represents our server. It holds all the different chat rooms
 * and users. It contains the application main method, which will create
 * the server and start listening the specified port for incoming client connections.
 */
public class ChatServer {
	private Set<ChatRoom> chatRooms;
	private Set<ChatUser> users;
	
	private static final Logger logger = LogManager.getLogger(ChatServer.class);
	
	private final static int PORT = 9001;
	
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
	
	public boolean addUserIfUnique(ChatUser user) {
		if (user == null)
			throw new NullPointerException("User was null.");
		
		synchronized (users) {
			return users.add(user);	
		}
	}

	public void deleteUser(ChatUser user) {
		if (user == null)
			throw new NullPointerException("User was null.");
		
		boolean isInTheSet = false;
		
		synchronized (users) {
			isInTheSet = users.remove(user);
		}
		
		if (!isInTheSet)
			throw new IllegalArgumentException("User doesn't exist.");
	}
	
	public static void main(String[] args) {
		ChatServer server = new ChatServer();
		logger.trace("ChatServer started");
		
		try (ServerSocket listener = new ServerSocket(PORT)) {
			while (true) {
				new ConnectionHandler(listener.accept(), server).start();
			}
		} 
		catch (IOException e) {
			logger.catching(e);
		}
		finally {
			logger.trace("ChatServer stopping");
		}
	}
	
}
