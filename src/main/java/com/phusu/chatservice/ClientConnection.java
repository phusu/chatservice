package com.phusu.chatservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ClientConnection 
 */
public class ClientConnection extends Thread {
	
	private Socket socket;
	private ChatServer server;
	private ChatUser user;
	private boolean closed = false;

	private static final Logger logger = LogManager.getLogger(ClientConnection.class);
	private ProtocolHandler handler;
	
	public ClientConnection(Socket socket, ChatServer server) {
		this.socket = socket;
		this.server = server;
	}
	
	@Override
	public void run() {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter output = new PrintWriter(socket.getOutputStream());
			
			handler = new ProtocolHandler(input, output);
			
			boolean userNameIsUnique = false;
			while (!userNameIsUnique) {
				// Get username from client
				handler.sendLine("SUBMITNAME");
				ChatMessage message = handler.getMessage();
				if (message.getMessageType() == MessageType.COMMAND_SETNAME) {
					// Check from server if it already exists
					CommandMessage commandMessage = (CommandMessage) message;
					user = new SimpleChatUser(this, commandMessage.getArguments());
					userNameIsUnique = server.addUserIfUnique(user);	
				}
			}
			
			handler.sendLine("SUBMITNAME OK");
			
			while (true) {
				ChatMessage message = handler.getMessage();
				
				if (message.getMessageType() == MessageType.COMMAND_JOIN) {
					handler.sendLine("NOT IMPLEMENTED");
				}
				else if (message.getMessageType() == MessageType.COMMAND_LEAVE) {
					handler.sendLine("NOT IMPLEMENTED");
				}
				else if (message.getMessageType() == MessageType.COMMAND_LISTROOMS) {
					handler.sendLine("NOT IMPLEMENTED");
				}
				else if (message.getMessageType() == MessageType.COMMAND_QUIT) {
					server.removeUser(user);
					closeConnection();
					break;
				}
				else if (message.getMessageType() == MessageType.MESSAGE_TO) {
					TextMessage textMessage = (TextMessage) message;
					server.deliverMessage(textMessage.getChatRoomName(), textMessage.getMessage());
				}
			}
			
		}
		catch (IOException e) {
			logger.catching(e);
		}
		catch (NullPointerException e) {
			logger.catching(e);
		}
		finally {
			closeConnection();
		}
	}
	
	public void deliverMessage(String message) {
		handler.sendLine(message);
	}

	private void closeConnection() {
		if (!closed) {
			logger.trace("Closing connection");
			server.removeConnection(this);
			
			try {
				socket.close();
				closed = true;
			}
			catch (IOException e) {
				logger.catching(e);
			}	
		}
	}
}
