package com.phusu.chatservice;

public final class CommandMessage extends ChatMessage {
	private String arguments;
	
	public CommandMessage(MessageType messageType, String arguments) {
		super(messageType);
		this.arguments = arguments;
	}
	
	public String getArguments() {
		return arguments;
	}
}
