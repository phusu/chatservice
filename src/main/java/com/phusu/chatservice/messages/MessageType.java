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
	COMMAND_QUIT("COMMAND QUIT"),
	RESPONSE_MESSAGE_FROM("MESSAGE FROM <from> TO <to> <message>"),
	RESPONSE_SETNAME_OK("RESPONSE OK SETNAME <name>"),
	RESPONSE_SETNAME_NOT_VALID("RESPONSE NOT VALID SETNAME <name>"),
	RESPONSE_JOIN_OK("RESPONSE OK JOIN <room>"),
	RESPONSE_JOIN_NOT_VALID("RESPONSE NOT VALID JOIN <room>"),
	RESPONSE_LEAVE_OK("RESPONSE OK LEAVE <room>"),
	RESPONSE_LEAVE_NOT_VALID("RESPONSE NOT VALID LEAVE <room>"),
	RESPONSE_LISTROOMS("RESPONSE LISTROOMS <rooms>");
	
	private final String messageTypeAsString;
	
	private MessageType(String messageTypeAsString) {
		this.messageTypeAsString = messageTypeAsString;
	}
	
	public String getMessageTypeAsString() {
		return messageTypeAsString;
	}
}