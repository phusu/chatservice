package com.phusu.chatservice.messagehandlers;

import com.phusu.chatservice.ChatServer;
import com.phusu.chatservice.ChatServerResponse;
import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.MessageType;
import com.phusu.chatservice.messages.TextMessage;

public class TextMessageHandler implements IChatMessageHandler {
	
	private ChatServer server;
	
	public TextMessageHandler(ChatServer server) {
		this.server = server;
	}

	@Override
	public ChatServerResponse handleMessage(ChatMessage message) {
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			String response = MessageType.RESPONSE_MESSAGE_FROM.getMessageTypeAsString();
			response = response.replace("<from>", textMessage.getAuthor().getName());
			response = response.replace("<to>", textMessage.getRoomName());
			response = response.replace("<message>", textMessage.getMessage());
			server.deliverMessageToRoom(new TextMessage(textMessage.getRoomName(), response));
			return new ChatServerResponse("");
		}
		else {
			return new ChatServerResponse();
		}
	}

}
