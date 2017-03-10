package com.phusu.chatservice.messages;

/**
 * TextMessage represents a simple text-based message from a single client
 * to a chat room.
 */
public class TextMessage extends ChatMessage {
	
	private String message;
	private String roomName;

	public TextMessage(String roomName, String message) {
		super(MessageType.MESSAGE_TO);
		this.roomName = roomName;
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getRoomName() {
		return roomName;
	}
}
