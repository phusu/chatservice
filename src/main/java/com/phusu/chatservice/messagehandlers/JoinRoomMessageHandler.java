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
				TextMessage userJoinedMessage = new TextMessage(roomName, author.getName() + " joined!");
				server.deliverMessageToRoom(userJoinedMessage);
				
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
