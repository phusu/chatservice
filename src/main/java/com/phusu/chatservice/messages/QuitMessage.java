package com.phusu.chatservice.messages;

public class QuitMessage extends ChatMessage {
	public QuitMessage() {
		super(MessageType.COMMAND_QUIT);
	}
}
