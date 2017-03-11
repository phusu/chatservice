package com.phusu.chatservice;

import com.phusu.chatservice.messages.MessageType;

public class ChatMessageParseException extends Exception {
	
	private static final long serialVersionUID = -526107924999718731L;
	private MessageType messageType;
	
	public ChatMessageParseException(MessageType messageType, String message) {
		super(message);
		switch (messageType) {
		case MESSAGE_TO:
			this.messageType = MessageType.RESPONSE_MESSAGE_NOT_VALID;
			break;
		case COMMAND_JOIN:
			this.messageType = MessageType.RESPONSE_JOIN_NOT_VALID;
			break;
		case COMMAND_LEAVE:
			this.messageType = MessageType.RESPONSE_LEAVE_NOT_VALID;
			break;
		case COMMAND_SETNAME:
			this.messageType = MessageType.RESPONSE_SETNAME_NOT_VALID;
		default:
			this.messageType = MessageType.UNKNOWN;
		}
	}
	
	public MessageType getMessageType() {
		return messageType;
	}
}
