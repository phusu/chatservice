package com.phusu.chatservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;

import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.SetNameMessage;

/**
 * ClientConnection 
 */
public class ClientConnection {
	private static final Logger logger = LogManager.getLogger(ClientConnection.class);
	
	private WebSocket socket;
	private ChatServer server;
	private ChatUser user;
	
	public ClientConnection(WebSocket socket, ChatServer server) {
		this.socket = socket;
		this.server = server;
	}
	
	public void deliverMessage(String message) {
		sendLine(message);
	}
	
	public synchronized void closeConnection() {
		logger.trace("Closing connection");
		socket.close();
	}
	
	public void setUser(ChatUser user) {
		this.user = user;
	}
	
	public ChatUser getUser() {
		return user;
	}
	
	private void sendLine(String line) {
		if (socket.isOpen()) {
			socket.send(line);
			logger.info(line);
		}
	}

	private ChatMessage getMessage(String line) throws ChatMessageParseException {
		logger.info(line);
		ChatMessage message = ChatMessageParser.parseLine(line); 
		message.setClientConnection(this);
		message.setAuthor(user);
		return message;
	}
	
	public void handleMessage(String msg) {
		try {
			ChatMessage message = getMessage(msg);
			if (this.user != null) {
				String response = server.handleMessage(this, message);
				if (!response.isEmpty()) {
					sendLine(response);
				}
			}
			else {
				if (message instanceof SetNameMessage) {
					String response = server.handleMessage(this, message);
					if (!response.isEmpty()) {
						sendLine(response);
					}
				}
				else {
					sendLine("SETNAME");
				}
			}
		}
		catch (ChatMessageParseException e) {
			String response = e.getMessageType().getMessageTypeAsString().replace("<message>", e.getMessage());
			sendLine(response);
		}
	}
}
