package com.phusu.chatservice.messagehandlers;

import com.phusu.chatservice.ChatServer;
import com.phusu.chatservice.ChatServerResponse;
import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.QuitMessage;

public class QuitMessageHandler implements IChatMessageHandler {
	
	private ChatServer server;
	
	public QuitMessageHandler(ChatServer server) {
		this.server = server;
	}

	@Override
	public ChatServerResponse handleMessage(ChatMessage message) {
		if (message instanceof QuitMessage) {
			server.removeConnection(message.getClientConnection());
			message.getClientConnection().closeConnection();
			return new ChatServerResponse();
		}
		else {
			return new ChatServerResponse();
		}
	}
}
