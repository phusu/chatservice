package com.phusu.chatservice.messagehandlers;

import com.phusu.chatservice.ChatServer;
import com.phusu.chatservice.ChatServerAction;
import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.QuitMessage;

public class QuitMessageHandler implements IChatMessageHandler {

	@Override
	public ChatServerAction handleMessage(ChatMessage message) {
		if (message instanceof QuitMessage) {
			return ChatServerAction.CLOSE_CONNECTION;
		}
		else {
			return ChatServerAction.NOT_MY_MESSAGE;
		}
	}
}
