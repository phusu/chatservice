package com.phusu.chatservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ConnectionHandler class is a Thread-based implementation of handling
 * a single client-server connection.
 */
public class ConnectionHandler extends Thread {

	private static final String SUBMITNAME = "SUBMITNAME";
	private static final Logger logger = LogManager.getLogger(ConnectionHandler.class); 
	private Socket socket;
	private ChatServer server;
	private BufferedReader in;
	private PrintWriter out;
	private ChatUser user;

	public ConnectionHandler(Socket socket, ChatServer server) {
		this.socket = socket;
		this.server = server;
	}

	@Override
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			getValidUserNameAndCreateUser();
			
		}
		catch (IOException e) {
			logger.catching(e);
		}
		finally {
			if (user != null) {
				server.deleteUser(user);
				logger.trace("User removed.");
			}

			logger.trace("Closing connection.");
			
			try {
				socket.close();
			} 
			catch (IOException e) {
				logger.catching(e);
			}
		}
	}

	private void getValidUserNameAndCreateUser() throws IOException {
		while (true) {
			out.println(SUBMITNAME);
			String line = in.readLine();
			logger.trace("Read line: " + line);
			ChatMessage msg = null;
			try {
				msg = ChatMessageParser.parseLine(line);
			}
			catch (IllegalArgumentException e) {
				logger.catching(e);
				continue;
			}
			
			if (msg instanceof CommandMessage) {
				CommandMessage commandMessage = (CommandMessage) msg;
				String userName = commandMessage.getArguments();
				logger.trace("Parsed userName: " + userName);
				ChatUser user = new SimpleChatUser(userName);
				if (server.addUserIfUnique(user)) {
					this.user = user;
					logger.trace("Username accepted.");
					break;
				}
			}
		}
	}
}
