package com.phusu.chatservice.messagehandlers;

import com.phusu.chatservice.ChatRoomType;
import com.phusu.chatservice.ChatServer;
import com.phusu.chatservice.ChatServerAction;
import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.JoinRoomMessage;
import com.phusu.chatservice.messages.TextMessage;

public class JoinRoomMessageHandler implements IChatMessageHandler {
	
	private ChatServer server;

	public JoinRoomMessageHandler(ChatServer server) {
		this.server = server;
	}
	
	@Override
	public ChatServerAction handleMessage(ChatMessage message) {
		if (message instanceof JoinRoomMessage) {
			JoinRoomMessage joinRoomMessage = (JoinRoomMessage) message;
			
			server.createRoomIfUnique(joinRoomMessage.getRoomName(), ChatRoomType.PUBLIC);
			boolean succeeded = server.addUserToRoom(joinRoomMessage.getAuthor(), joinRoomMessage.getRoomName());
			
			if (succeeded) {
				// Notify also others
				TextMessage userJoinedMessage = new TextMessage(joinRoomMessage.getRoomName(), joinRoomMessage.getAuthor().getName() + " joined!");
				server.deliverMessage(userJoinedMessage);
				
				return ChatServerAction.HANDLED_NO_RESPONSE;
			}
			else {
				return ChatServerAction.ROOM_DOESNT_EXIST;
			}
		}
		else {
			return ChatServerAction.NOT_MY_MESSAGE;
		}
	}
}
