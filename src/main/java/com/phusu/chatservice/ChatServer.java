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

import com.phusu.chatservice.messagehandlers.*;
import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.TextMessage;

/**
 * ChatServer represents our server. It holds all the different chat rooms
 * and users. It contains the application main method, which will create
 * the server and start listening the specified port for incoming client connections.
 * 
 * Supported commands from client->server	|	Responses from server->client
 * -----------------------------------------|-----------------------------------------
 * MESSAGE TO roomname message				| <MESSAGE FROM user TO roomname message>
 * COMMAND SETNAME name						| <RESPONSE OK SETNAME> or <RESPONSE NOT VALID SETNAME>
 * COMMAND JOIN roomname					| <RESPONSE OK JOIN roomname> or <RESPONSE NOT EXIST JOIN roomname>
 * COMMAND LEAVE roomname					| <RESPONSE OK LEAVE roomname> or <RESPONSE NOT EXIST LEAVE roomname>
 * COMMAND LISTROOMS						| <RESPONSE LISTROOMS room1 room2 room3 ... roomn>
 * COMMAND QUIT								| no response
 *  
 */
public class ChatServer implements Runnable {
	private Map<String, ChatRoom> chatRooms;
	private Set<ChatUser> users;
	private Collection<ClientConnection> connections;
	private Set<String> publicRoomNames;
	private Collection<IChatMessageHandler> messageHandlers;
	
	private static final Logger logger = LogManager.getLogger(ChatServer.class);
	
	private final static int PORT = 9001;
	private ServerSocket serverSocket;
	private boolean serverIsRunning = false;
	
	public ChatServer() {
		logger.trace("ChatServer created");
		this.chatRooms = new HashMap<String, ChatRoom>();
		this.users = new HashSet<ChatUser>();
		this.connections = new ArrayList<ClientConnection>();
		this.publicRoomNames = new HashSet<String>();
		this.messageHandlers = new ArrayList<IChatMessageHandler>();
		this.messageHandlers.add(new TextMessageHandler(this));
		this.messageHandlers.add(new QuitMessageHandler(this));
		this.messageHandlers.add(new SetNameMessageHandler(this));
		this.messageHandlers.add(new JoinRoomMessageHandler(this));
		this.messageHandlers.add(new LeaveRoomMessageHandler(this));
		this.messageHandlers.add(new ListRoomsMessageHandler(this));
		this.messageHandlers.add(new UnknownMessageHandler());
	}
	
	public Set<String> listPublicRoomNames() {
		return Collections.unmodifiableSet(publicRoomNames);
	}

	public void createRoomIfUnique(String roomName, ChatRoomType roomType) {
		if (roomName == null)
			throw new NullPointerException("Room name was null.");
		if (roomType == null)
			throw new NullPointerException("Room type was null.");
		
		synchronized (chatRooms) {
			boolean roomExists = chatRooms.containsKey(roomName);
			if (!roomExists) {
				chatRooms.put(roomName, new ChatRoom(roomName, roomType));
			
				if (roomType == ChatRoomType.PUBLIC) {
					synchronized (publicRoomNames) {
						publicRoomNames.add(roomName);	
					}
				}
				
				logger.trace("Room " + roomName + " added.");
				logger.trace(chatRooms);
				logger.trace(publicRoomNames);
			}
		}
	}

	public boolean deleteRoomIfEmpty(String roomName) {
		if (roomName == null)
			throw new NullPointerException("Room was null.");
		
		synchronized (chatRooms) {
			boolean roomExists = chatRooms.containsKey(roomName);
			if (roomExists) {
				ChatRoom room = chatRooms.get(roomName);
				
				if (room.getUsers().isEmpty()) {
					room = chatRooms.remove(roomName);
					if (room.getType() == ChatRoomType.PUBLIC) {
						synchronized (publicRoomNames) {
							publicRoomNames.remove(roomName);
						}
					}
					room = null;

					logger.trace("Room " + roomName + " removed.");
					logger.trace(chatRooms);
					logger.trace(publicRoomNames);
					
					return true;
				}
			}
			return false;
		}
	}
	
	public Set<ChatUser> listUsers() {
		return Collections.unmodifiableSet(users);
	}
	
	public Set<ChatUser> listUsersInRoom(String roomName) {
		if (roomName == null)
			throw new NullPointerException("Room name was null.");
		
		synchronized (chatRooms) {
			boolean roomExists = chatRooms.containsKey(roomName);
			if (roomExists) {
				ChatRoom room = chatRooms.get(roomName);
				return room.getUsers();
			}
		}
		return null;
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
				Set<String> roomNames = new HashSet<>();
				synchronized (chatRooms) {
					for (ChatRoom chatRoom : chatRooms.values()) {
						boolean removed = chatRoom.removeUserIfExists(user);
						if (removed && chatRoom.getUsers().isEmpty()) {
							roomNames.add(chatRoom.getName());
						}
					}
					for (String roomName : roomNames) {
						chatRooms.remove(roomName);
						logger.trace("Room " + roomName + " removed.");
						logger.trace(chatRooms);
					}
				}
				logger.trace("User " + user.getName() + " removed.");
				logger.trace(users);
			}
		}
	}
	
	private void addConnection(ClientConnection connection) {
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
	
	public String handleMessage(ClientConnection connection, ChatMessage message) {
		for (IChatMessageHandler handler : messageHandlers) {
			ChatServerResponse response = handler.handleMessage(message);
			if (response.isResponseHandled()) {
				return response.getResponse();
			}
			else {
				continue;
			}
		}
		
		return "";
	}
	
	public boolean deliverMessageToRoom(TextMessage message) {
		String chatRoomName = message.getRoomName();
		String chatMessage = message.getMessage();
		if (chatRooms.containsKey(chatRoomName)) {
			Set<ChatUser> users = chatRooms.get(chatRoomName).getUsers();
			for (ChatUser chatUser : users) {
				chatUser.getClientConnection().deliverMessage(chatMessage);
			}
			
			logger.trace("Delivered to: " + chatRoomName + ", message: " + chatMessage);
			
			return true;
		}
		return false;
	}

	public boolean addUserToRoom(ChatUser user, String roomName) {
		synchronized (chatRooms) {
			if (chatRooms.containsKey(roomName)) {
				ChatRoom room = chatRooms.get(roomName);
				boolean userAdded = room.addUserIfUnique(user);
				
				return userAdded;
			}
			return false;
		}
	}

	public boolean removeUserFromRoom(ChatUser user, String roomName) {
		synchronized (chatRooms) {
			if (chatRooms.containsKey(roomName)) {
				ChatRoom room = chatRooms.get(roomName);
				boolean userRemoved = room.removeUserIfExists(user);
				
				return userRemoved;
			}
			return false;
		}
	}
	
	public void run() {
		logger.trace("ChatServer started");
		serverIsRunning = true;
		
		try {
			serverSocket = new ServerSocket(PORT);
			while (serverIsRunning) {
				Socket socket = serverSocket.accept();
				ClientConnection connection = new ClientConnection(socket, this);
				this.addConnection(connection);
				connection.start();
			}
			serverSocket.close();
		} 
		catch (IOException e) {
			logger.catching(e);
		}
		finally {
			logger.trace("ChatServer stopped");
		}
	}
	
	public void stop() {
		logger.trace("ChatServer stopping");
		serverIsRunning = false;
	}
	
	public static void main(String[] args) {
		ChatServer server = new ChatServer();
		Thread serverThread = new Thread(server, "server");
		serverThread.start();
	}
}
