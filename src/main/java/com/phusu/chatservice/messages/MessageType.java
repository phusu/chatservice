package com.phusu.chatservice.messages;

/**
 * Enumeration of different recognized message types from client to server.
 */
public enum MessageType {
	MESSAGE_TO("MESSAGE TO"),
	COMMAND_SETNAME("COMMAND SETNAME"),
	COMMAND_JOIN("COMMAND JOIN"),
	COMMAND_LEAVE("COMMAND LEAVE"),
	COMMAND_LISTROOMS("COMMAND LISTROOMS"),
	COMMAND_QUIT("COMMAND QUIT");
	
	private final String messageTypeAsString;
	
	private MessageType(String messageTypeAsString) {
		this.messageTypeAsString = messageTypeAsString;
	}
	
	public String getMessageTypeAsString() {
		return messageTypeAsString;
	}
}