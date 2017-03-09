package com.phusu.chatservice.messagehandlers;

import com.phusu.chatservice.ChatServer;
import com.phusu.chatservice.ChatServerAction;
import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.LeaveRoomMessage;
import com.phusu.chatservice.messages.TextMessage;

public class LeaveRoomMessageHandler implements IChatMessageHandler {

	private ChatServer server;

	public LeaveRoomMessageHandler(ChatServer server) {
		this.server = server;
	}

	@Override
	public ChatServerAction handleMessage(ChatMessage message) {
		if (message instanceof LeaveRoomMessage) {
			LeaveRoomMessage leaveRoomMessage = (LeaveRoomMessage) message;
			boolean succeeded = server.removeUserFromRoom(leaveRoomMessage.getAuthor(), leaveRoomMessage.getRoomName());
			server.deleteRoomIfEmpty(leaveRoomMessage.getRoomName());
			
			if (succeeded) {
				// Notify also others
				TextMessage userJoinedMessage = new TextMessage(leaveRoomMessage.getRoomName(), leaveRoomMessage.getAuthor().getName() + " left!");
				server.deliverMessage(userJoinedMessage);
				
				return ChatServerAction.HANDLED_NO_RESPONSE;
			}
			return ChatServerAction.ROOM_DOESNT_EXIST;
		}
		return ChatServerAction.NOT_MY_MESSAGE;
	}
}
