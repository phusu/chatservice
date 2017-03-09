package com.phusu.chatservice.messages;

public class ListRoomsMessage extends ChatMessage {
	public ListRoomsMessage() {
		super(MessageType.COMMAND_LISTROOMS);
	}
}
