package com.phusu.chatservice;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
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
		this.chatRooms = new HashMap<String, ChatRoom>();
		this.users = new HashSet<ChatUser>();
		this.connections = new ArrayList<ClientConnection>();
		this.publicRoomNames = new HashSet<String>();
	}
	
	public Set<String> listPublicRoomNames() {
		return publicRoomNames;
	}

	public synchronized boolean addRoomIfUnique(ChatRoom room) {
		if (room == null)
			throw new NullPointerException("Room was null.");
		
		boolean roomExists = chatRooms.containsKey(room.getName());
		if (!roomExists) {
			chatRooms.put(room.getName(), room);
		
			if (room.getType() == ChatRoomType.PUBLIC) {
				publicRoomNames.add(room.getName());
			}
		}
		
		return roomExists;
	}

	public synchronized boolean deleteRoomIfExists(ChatRoom room) {
		if (room == null)
			throw new NullPointerException("Room was null.");
		
		boolean roomExists = chatRooms.containsKey(room.getName());
		if (roomExists)
		{
			chatRooms.remove(room);
			if (room.getType() == ChatRoomType.PUBLIC) {
				publicRoomNames.remove(room.getName());
			}	
		}
		
		return roomExists;
	}
	
	public Set<ChatUser> listUsers() {
		return users;
	}
	
	public synchronized boolean addUserIfUnique(ChatUser user) {
		if (user == null)
			throw new NullPointerException("User was null.");
		
		return users.add(user);	
	}

	public synchronized void removeUser(ChatUser user) {
		if (user == null)
			throw new NullPointerException("User was null.");
		
		boolean isInTheSet = users.remove(user);
		
		if (!isInTheSet) {
			throw new IllegalArgumentException("User doesn't exist.");
		}
		else {
			for (ChatRoom chatRoom : chatRooms.values()) {
				chatRoom.removeUserIfExists(user);
			}
		}
	}
	
	public void addConnection(ClientConnection connection) {
		if (connection == null)
			throw new NullPointerException("Connection was null.");
		
		connections.add(connection);
	}
	
	public void removeConnection(ClientConnection connection) {
		if (connection == null)
			throw new NullPointerException("Connection was null.");		
		
		connections.remove(connection);
	}
	
	public void deliverMessage(String chatRoomName, String message) {
		if (chatRooms.containsKey(chatRoomName)) {
			Set<ChatUser> users = chatRooms.get(chatRoomName).getUsers();
			for (ChatUser chatUser : users) {
				chatUser.getClientConnection().deliverMessage(message);
			}
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
