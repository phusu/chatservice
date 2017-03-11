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
			String roomName = textMessage.getRoomName();
			String userName = textMessage.getAuthor().getName();
			String messageString = textMessage.getMessage();
			
			String response = MessageType.RESPONSE_MESSAGE_FROM.getMessageTypeAsString();
			response = response.replace("<from>", userName);
			response = response.replace("<to>", roomName);
			response = response.replace("<message>", messageString);
			
			textMessage = new TextMessage(roomName, response);
			boolean succeeded = server.deliverMessageToRoom(textMessage);
			if (succeeded) {
				return new ChatServerResponse("");
			}
			else {
				response = MessageType.RESPONSE_MESSAGE_NOT_VALID.getMessageTypeAsString();
				response = response.replace("<message>", "Room " + roomName + " not found");
				return new ChatServerResponse(response);
			}
		}
		else {
			return new ChatServerResponse();
		}
	}

}
