package com.phusu.chatservice.messagehandlers;

import com.phusu.chatservice.ChatServer;
import com.phusu.chatservice.ChatServerAction;
import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.TextMessage;

public class TextMessageHandler implements IChatMessageHandler {
	
	private ChatServer server;
	
	public TextMessageHandler(ChatServer server) {
		this.server = server;
	}

	@Override
	public ChatServerAction handleMessage(ChatMessage message) {
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			server.deliverMessage(textMessage);
			return ChatServerAction.HANDLED_NO_RESPONSE;
		}
		else {
			return ChatServerAction.NOT_MY_MESSAGE;
		}
	}

}
