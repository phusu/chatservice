package com.phusu.chatservice.messagehandlers;

import com.phusu.chatservice.ChatRoomType;
import com.phusu.chatservice.ChatServer;
import com.phusu.chatservice.ChatServerResponse;
import com.phusu.chatservice.ChatUser;
import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.JoinRoomMessage;
import com.phusu.chatservice.messages.MessageType;
import com.phusu.chatservice.messages.TextMessage;

public class JoinRoomMessageHandler implements IChatMessageHandler {
	
	private ChatServer server;

	public JoinRoomMessageHandler(ChatServer server) {
		this.server = server;
	}
	
	@Override
	public ChatServerResponse handleMessage(ChatMessage message) {
		if (message instanceof JoinRoomMessage) {
			JoinRoomMessage joinRoomMessage = (JoinRoomMessage) message;
			String roomName = joinRoomMessage.getRoomName();
			ChatUser author = joinRoomMessage.getAuthor();
			server.createRoomIfUnique(roomName, ChatRoomType.PUBLIC);
			boolean succeeded = server.addUserToRoom(author, roomName);
			
			if (succeeded) {
				// Notify also others
				String userJoinedMessage = MessageType.RESPONSE_MESSAGE_FROM.getMessageTypeAsString();
				userJoinedMessage = userJoinedMessage.replace("<from>", author.getName());
				userJoinedMessage = userJoinedMessage.replace("<to>", roomName);
				userJoinedMessage = userJoinedMessage.replace("<message>", author.getName() + " joined!");
				TextMessage textMessage = new TextMessage(roomName, userJoinedMessage);
				textMessage.setAuthor(author);
				textMessage.setClientConnection(author.getClientConnection());
				server.deliverMessageToRoom(textMessage);
				
				String response = MessageType.RESPONSE_JOIN_OK.getMessageTypeAsString().replace("<room>", roomName);
				return new ChatServerResponse(response);
			}
			else {
				String response = MessageType.RESPONSE_JOIN_NOT_VALID.getMessageTypeAsString().replace("<room>", roomName);
				return new ChatServerResponse(response);
			}
		}
		else {
			return new ChatServerResponse();
		}
	}
}
