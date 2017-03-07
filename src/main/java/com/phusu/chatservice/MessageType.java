package com.phusu.chatservice;

enum MessageType {
	MESSAGE_TO("MESSAGE TO"),
	COMMAND_SETNAME("COMMAND SETNAME"),
	COMMAND_JOIN("COMMAND JOIN"),
	COMMAND_LEAVE("COMMAND LEAVE");
	
	private final String messageTypeAsString;
	
	private MessageType(String messageTypeAsString) {
		this.messageTypeAsString = messageTypeAsString;
	}
	
	public String getMessageTypeAsString() {
		return messageTypeAsString;
	}
}