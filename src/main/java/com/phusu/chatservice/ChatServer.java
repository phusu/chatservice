package com.phusu.chatservice;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

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
 *  
 */
public class ChatServer extends WebSocketServer {
	private Map<String, ChatRoom> chatRooms;
	private Set<ChatUser> users;
	private Map<InetSocketAddress, ClientConnection> connections;
	private Set<String> publicRoomNames;
	private Collection<IChatMessageHandler> messageHandlers;
	
	private static final Logger logger = LogManager.getLogger(ChatServer.class);
	
	private final static int PORT = 9001;
	
	public ChatServer() {
		super(new InetSocketAddress(PORT));
		logger.trace("ChatServer created");
		this.chatRooms = new HashMap<String, ChatRoom>();
		this.users = new HashSet<ChatUser>();
		this.connections = new HashMap<>();
		this.publicRoomNames = new HashSet<String>();
		this.messageHandlers = new ArrayList<IChatMessageHandler>();
		this.messageHandlers.add(new TextMessageHandler(this));
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
				
				if (room.isEmpty()) {
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
	
	public boolean containsRoom(String roomName) {
		if (roomName == null)
			throw new NullPointerException("Room name was null.");
		
		boolean roomExists = false;
		synchronized (chatRooms) {
			roomExists = chatRooms.containsKey(roomName);
		}
		
		return roomExists;
	}
	
	public int roomSize(String roomName) {
		if (roomName == null)
			throw new NullPointerException("Room was null.");
		
		int size = -1;
		if (!containsRoom(roomName))
			return size;
		
		synchronized (chatRooms) {
			size = chatRooms.get(roomName).size();
		}
		
		return size;
	}
	
	public Set<ChatUser> listUsersInServer() {
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
				Set<String> roomNames = new HashSet<>();
				synchronized (chatRooms) {
					for (ChatRoom chatRoom : chatRooms.values()) {
						boolean removed = chatRoom.removeUserIfExists(user);
						if (removed && chatRoom.isEmpty()) {
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
			chatRooms.get(chatRoomName).deliverMessage(chatMessage);
			
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
	
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		logger.info("Closing connection: " + conn.getRemoteSocketAddress().toString());
		ClientConnection clientConnection = null;
		synchronized (connections) {
			clientConnection = connections.get(conn.getRemoteSocketAddress());
			if (clientConnection != null) {
				removeUser(clientConnection.getUser());
				connections.remove(conn.getRemoteSocketAddress());
			}
		}
	}
	
	@Override
	public void onError(WebSocket conn, Exception ex) {
		logger.catching(ex);
	}
	
	@Override
	public void onMessage(WebSocket conn, String message) {
		ClientConnection clientConnection = null;
		synchronized (connections) {
			clientConnection = connections.get(conn.getRemoteSocketAddress());	
		}
		if (clientConnection != null) {
			clientConnection.handleMessage(message);
		}
	}
	
	@Override
	public void onStart() {
		logger.info("Server started");
	}
	
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		logger.info("New connection from: " + conn.getRemoteSocketAddress().toString());
		ClientConnection clientConnection = new ClientConnection(conn, this);
		synchronized (connections) {
			connections.put(conn.getRemoteSocketAddress(), clientConnection);
		}
		// Request username for each new connection
		conn.send("SETNAME");
	}
		
	public static void main(String[] args) {
		ChatServer server = new ChatServer();
		server.start();
	}
}
