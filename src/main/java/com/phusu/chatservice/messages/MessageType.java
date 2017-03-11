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
	RESPONSE_NOT_IMPLEMENTED("NOT IMPLEMENTED"),
	RESPONSE_MESSAGE_FROM("MESSAGE FROM <from> TO <to> <message>"),
	RESPONSE_MESSAGE_NOT_VALID("MESSAGE NOT VALID <message>"),
	RESPONSE_SETNAME_OK("RESPONSE OK SETNAME <name>"),
	RESPONSE_SETNAME_NOT_VALID("RESPONSE NOT VALID SETNAME <message>"),
	RESPONSE_JOIN_OK("RESPONSE OK JOIN <message>"),
	RESPONSE_JOIN_NOT_VALID("RESPONSE NOT VALID JOIN <message>"),
	RESPONSE_LEAVE_OK("RESPONSE OK LEAVE <message>"),
	RESPONSE_LEAVE_NOT_VALID("RESPONSE NOT VALID LEAVE <message>"),
	RESPONSE_LISTROOMS("RESPONSE LISTROOMS <rooms>"),
	UNKNOWN("UNKNOWN COMMAND");
	
	private final String messageTypeAsString;
	
	private MessageType(String messageTypeAsString) {
		this.messageTypeAsString = messageTypeAsString;
	}
	
	public String getMessageTypeAsString() {
		return messageTypeAsString;
	}
}