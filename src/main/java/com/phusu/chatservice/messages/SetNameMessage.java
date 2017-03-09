package com.phusu.chatservice.messages;

public class SetNameMessage extends ChatMessage {
	private String userName;
	
	public SetNameMessage(String userName) {
		super(MessageType.COMMAND_SETNAME);
		this.userName = userName;
	}
	
	public String getUserName() {
		return this.userName;
	}
}
