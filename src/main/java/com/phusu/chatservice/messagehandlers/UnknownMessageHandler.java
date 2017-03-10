package com.phusu.chatservice.messagehandlers;

import com.phusu.chatservice.ChatServerResponse;
import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.MessageType;
import com.phusu.chatservice.messages.UnknownMessage;

public class UnknownMessageHandler implements IChatMessageHandler {
	@Override
	public ChatServerResponse handleMessage(ChatMessage message) {
		if (message instanceof UnknownMessage) {
			return new ChatServerResponse(MessageType.UNKNOWN.getMessageTypeAsString());
		}
		else {
			return new ChatServerResponse();
		}
	}
}
