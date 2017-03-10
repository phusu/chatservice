package com.phusu.chatservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.phusu.chatservice.messages.ChatMessage;

/**
 * ClientConnection 
 */
public class ClientConnection extends Thread {
	private static final Logger logger = LogManager.getLogger(ClientConnection.class);
	
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	private ChatServer server;
	private boolean connectionClosed = false;
	private ChatUser user;
	
	public ClientConnection(Socket socket, ChatServer server) {
		this.socket = socket;
		this.server = server;
	}
	
	public void deliverMessage(String message) {
		sendLine(message);
	}

	public void closeConnection() {
		if (!connectionClosed) {
			logger.trace("Closing connection");
			
			try {
				socket.close();
				connectionClosed = true;
			}
			catch (IOException e) {
				logger.catching(e);
			}	
		}
	}

	public void setUser(ChatUser user) {
		this.user = user;
	}
	
	@Override
	public void run() {
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream());
			
			sendLine("SUBMITNAME");			
			processMessages();
			
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
	
	private void sendLine(String line) {
		output.println(line);
		logger.info(line);
	}

	private void processMessages() throws IOException {
		while (!connectionClosed) {
			ChatMessage message = getMessage();
			message.setClientConnection(this);
			message.setAuthor(user);
			String response = server.handleMessage(this, message);
			if (!response.isEmpty()) {
				sendLine(response);
			}
		}
	}

	private ChatMessage getMessage() throws IOException {
		String line = input.readLine();
		logger.info(line);
		return ChatMessageParser.parseLine(line);
	}
}
