package com.phusu.chatservice;

/**
 * TextMessage represents a simple text-based message from a single client
 * to a chat room.
 */
public final class TextMessage extends ChatMessage {
	
	private String message;
	private String chatRoomName;

	public TextMessage(String chatRoomName, String message) {
		super(MessageType.MESSAGE_TO);
		this.chatRoomName = chatRoomName;
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getChatRoomName() {
		return chatRoomName;
	}
}
