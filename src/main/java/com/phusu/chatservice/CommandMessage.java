package com.phusu.chatservice;

/**
 * CommandMessage represents a command message from client to server, i.e. not a chat message.
 */
public class CommandMessage extends ChatMessage {
	private String arguments;
	
	public CommandMessage(MessageType messageType, String arguments) {
		super(messageType);
		this.arguments = arguments;
	}
	
	public CommandMessage(MessageType messageType) {
		super(messageType);
	}

	public String getArguments() {
		return arguments;
	}
}
