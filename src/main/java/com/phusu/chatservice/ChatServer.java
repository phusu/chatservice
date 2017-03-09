package com.phusu.chatservice;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ChatServer represents our server. It holds all the different chat rooms
 * and users. It contains the application main method, which will create
 * the server and start listening the specified port for incoming client connections.
 */
public class ChatServer {
	private Map<String, ChatRoom> chatRooms;
	private Set<ChatUser> users;
	private Collection<ClientConnection> connections;
	private Set<String> publicRoomNames;
	
	private static final Logger logger = LogManager.getLogger(ChatServer.class);
	
	private final static int PORT = 9001;
	
	public ChatServer() {
		logger.trace("ChatServer created");
		this.chatRooms = new HashMap<String, ChatRoom>();
		this.users = new HashSet<ChatUser>();
		this.connections = new ArrayList<ClientConnection>();
		this.publicRoomNames = new HashSet<String>();
	}
	
	public Set<String> listPublicRoomNames() {
		return Collections.unmodifiableSet(publicRoomNames);
	}

	public boolean addRoomIfUnique(ChatRoom room) {
		if (room == null)
			throw new NullPointerException("Room was null.");
		
		synchronized (chatRooms) {
			String roomName = room.getName();
			ChatRoomType roomType = room.getType();
			boolean roomExists = chatRooms.containsKey(roomName);
			if (!roomExists) {
				chatRooms.put(roomName, room);
			
				if (roomType == ChatRoomType.PUBLIC) {
					synchronized (publicRoomNames) {
						publicRoomNames.add(roomName);	
					}
				}
				
				logger.trace("Room " + roomName + " added.");
				logger.trace(chatRooms);
				logger.trace(publicRoomNames);
				
				return true;
			}
			
			return false;	
		}
	}

	public boolean deleteRoomIfExists(ChatRoom room) {
		if (room == null)
			throw new NullPointerException("Room was null.");
		
		synchronized (chatRooms) {
			String roomName = room.getName();
			ChatRoomType roomType = room.getType();
			boolean roomExists = chatRooms.containsKey(roomName);
			if (roomExists) {
				chatRooms.remove(room);
				if (roomType == ChatRoomType.PUBLIC) {
					synchronized (publicRoomNames) {
						publicRoomNames.remove(roomName);
					}
				}

				logger.trace("Room " + roomName + " removed.");
				logger.trace(chatRooms);
				logger.trace(publicRoomNames);
				
				return true;
			}
			return false;
		}
	}
	
	public Set<ChatUser> listUsers() {
		return Collections.unmodifiableSet(users);
	}
	
	public boolean addUserIfUnique(ChatUser user) {
		if (user == null)
			throw new NullPointerException("User was null.");
		
		synchronized (users) {
			boolean result = users.add(user);
			if (result) {
				logger.trace("User " + user.getName() + " added.");
				logger.trace(users);
			}
			return result;
		}	
	}

	public void removeUser(ChatUser user) {
		if (user == null)
			throw new NullPointerException("User was null.");
		
		synchronized (users) {
			boolean isInTheSet = users.remove(user);
			if (!isInTheSet) {
				throw new IllegalArgumentException("User doesn't exist.");
			} else {
				for (ChatRoom chatRoom : chatRooms.values()) {
					chatRoom.removeUserIfExists(user);
				}
				
				logger.trace("User " + user.getName() + " removed.");
				logger.trace(users);
			}
		}
	}
	
	public void addConnection(ClientConnection connection) {
		if (connection == null)
			throw new NullPointerException("Connection was null.");
		
		synchronized (connections) {
			connections.add(connection);
			logger.trace("Connection added.");
		}
	}
	
	public void removeConnection(ClientConnection connection) {
		if (connection == null)
			throw new NullPointerException("Connection was null.");		
		
		synchronized (connections) {
			connections.remove(connection);
			logger.trace("Connection removed.");
		}
	}
	
	public void deliverMessage(String chatRoomName, String message) {
		if (chatRooms.containsKey(chatRoomName)) {
			Set<ChatUser> users = chatRooms.get(chatRoomName).getUsers();
			for (ChatUser chatUser : users) {
				chatUser.getClientConnection().deliverMessage(message);
			}
			
			logger.trace("Delivered to: " + chatRoomName + ", message: " + message);
		}
	}
	
	public void start() {

		logger.trace("ChatServer started");
		
		try (ServerSocket listener = new ServerSocket(PORT)) {
			while (true) {
				Socket socket = listener.accept();
				ClientConnection connection = new ClientConnection(socket, this);
				this.addConnection(connection);
				connection.start();
			}
		} 
		catch (IOException e) {
			logger.catching(e);
		}
		finally {
			logger.trace("ChatServer stopping");
		}
	}
	
	public static void main(String[] args) {
		ChatServer server = new ChatServer();
		server.start();
	}
}
