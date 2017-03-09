package com.phusu.chatservice.messages;

public class JoinRoomMessage extends ChatMessage {
	private String roomName;
	
	public JoinRoomMessage(String roomName) {
		super(MessageType.COMMAND_JOIN);
		this.roomName = roomName;
	}
	
	public String getRoomName() {
		return roomName;
	}
}
