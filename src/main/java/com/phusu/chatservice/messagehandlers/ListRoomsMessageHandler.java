package com.phusu.chatservice.messagehandlers;

import java.util.Set;

import com.phusu.chatservice.ChatServer;
import com.phusu.chatservice.ChatServerResponse;
import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.ListRoomsMessage;
import com.phusu.chatservice.messages.MessageType;

public class ListRoomsMessageHandler implements IChatMessageHandler {
	
	private ChatServer server;
	
	public ListRoomsMessageHandler(ChatServer server) {
		this.server = server;
	}
	
	@Override
	public ChatServerResponse handleMessage(ChatMessage message) {
		if (message instanceof ListRoomsMessage) {
			Set<String> publicRoomNames = server.listPublicRoomNames();
			StringBuilder listOfRooms = new StringBuilder();
			for (String roomName : publicRoomNames) {
				listOfRooms.append(roomName);
				listOfRooms.append(" ");
			}
			String listOfRoomsResponse = MessageType.RESPONSE_LISTROOMS.getMessageTypeAsString().replace("<rooms>", listOfRooms);
			return new ChatServerResponse(listOfRoomsResponse.toString().trim());
		}
		return new ChatServerResponse();
	}
}
