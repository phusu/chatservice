package com.phusu.chatservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.SetNameMessage;

/**
 * ClientConnection 
 */
public class ClientConnection extends Thread {
	private static final Logger logger = LogManager.getLogger(ClientConnection.class);
	
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	private ChatServer server;
	private ChatUser user;
	private boolean isConnectionClosed = false;
	
	public ClientConnection(Socket socket, ChatServer server) {
		this.socket = socket;
		this.server = server;
	}
	
	public void deliverMessage(String message) {
		sendLine(message);
	}
	
	public synchronized void closeConnection() {
		logger.trace("Closing connection");
		isConnectionClosed = true;	
		
		try {
			socket.close();
		} catch (IOException e) {
			logger.catching(e);
		}
	}
	
	private synchronized boolean isConnectionClosed() {
		return isConnectionClosed;
	}

	public void setUser(ChatUser user) {
		this.user = user;
	}
	
	@Override
	public void run() {
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);
			
			getValidUserName();
			processMessageLoop();
			
		}
		catch (IOException e) {
			logger.catching(e);
		}
		catch (NullPointerException e) {
			logger.catching(e);
		}
	}
	
	private void getValidUserName() throws IOException {
		boolean userNameIsValid = false;
		while (!userNameIsValid) {
			sendLine("SETNAME");
			ChatMessage message;
			try {
				message = getMessage();
			}
			catch (ChatMessageParseException e) {
				continue;
			}
			if (message instanceof SetNameMessage) {
				message.setClientConnection(this);
				String response = server.handleMessage(this, message);
				sendLine(response);
				if (this.user != null) {
					userNameIsValid = true;	
				}
			}
		}
	}
	
	private void sendLine(String line) {
		output.println(line);
		logger.info(line);
	}

	private void processMessageLoop() throws IOException {
		while (!isConnectionClosed()) {
			try {
				ChatMessage message = getMessage();
				String response = server.handleMessage(this, message);
				if (!response.isEmpty()) {
					sendLine(response);
				}
			}
			catch (ChatMessageParseException e) {
				String response = e.getMessageType().getMessageTypeAsString().replace("<message>", e.getMessage());
				sendLine(response);
			}
		}
	}

	private ChatMessage getMessage() throws IOException, ChatMessageParseException {
		String line = input.readLine();
		logger.info(line);
		ChatMessage message = ChatMessageParser.parseLine(line); 
		message.setClientConnection(this);
		message.setAuthor(user);
		return message;
	}
}
