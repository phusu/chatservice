package com.phusu.chatservice.messages;

public class LeaveRoomMessage extends ChatMessage {
	
	private String roomName;

	public LeaveRoomMessage(String roomName) {
		super(MessageType.COMMAND_LEAVE);
		this.roomName = roomName;
	}
	
	public String getRoomName() {
		return roomName;
	}
}
