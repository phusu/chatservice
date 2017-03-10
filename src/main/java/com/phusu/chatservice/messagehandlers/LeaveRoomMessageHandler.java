package com.phusu.chatservice.messagehandlers;

import com.phusu.chatservice.ChatServer;
import com.phusu.chatservice.ChatServerResponse;
import com.phusu.chatservice.ChatUser;
import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.LeaveRoomMessage;
import com.phusu.chatservice.messages.MessageType;
import com.phusu.chatservice.messages.TextMessage;

public class LeaveRoomMessageHandler implements IChatMessageHandler {

	private ChatServer server;

	public LeaveRoomMessageHandler(ChatServer server) {
		this.server = server;
	}

	@Override
	public ChatServerResponse handleMessage(ChatMessage message) {
		if (message instanceof LeaveRoomMessage) {
			LeaveRoomMessage leaveRoomMessage = (LeaveRoomMessage) message;
			String roomName = leaveRoomMessage.getRoomName();
			ChatUser author = leaveRoomMessage.getAuthor();
			boolean succeeded = server.removeUserFromRoom(author, roomName);
			server.deleteRoomIfEmpty(roomName);
			
			if (succeeded) {
				// Notify also others
				String userLeftMessage = MessageType.RESPONSE_MESSAGE_FROM.getMessageTypeAsString();
				userLeftMessage = userLeftMessage.replace("<from>", author.getName());
				userLeftMessage = userLeftMessage.replace("<to>", roomName);
				userLeftMessage = userLeftMessage.replace("<message>", author.getName() + " left!");
				TextMessage textMessage = new TextMessage(roomName, userLeftMessage);
				textMessage.setAuthor(author);
				textMessage.setClientConnection(author.getClientConnection());
				server.deliverMessageToRoom(textMessage);
				
				String response = MessageType.RESPONSE_LEAVE_OK.getMessageTypeAsString().replace("<room>", roomName);
				return new ChatServerResponse(response);	
			}
			else {
				String response = MessageType.RESPONSE_LEAVE_NOT_VALID.getMessageTypeAsString().replace("<room>", roomName);
				return new ChatServerResponse(response);	
			}
		}
		return new ChatServerResponse();
	}
}
