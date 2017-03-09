package com.phusu.chatservice.messagehandlers;

import com.phusu.chatservice.ChatServerAction;
import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.ListRoomsMessage;

public class ListRoomsMessageHandler implements IChatMessageHandler {
	
	@Override
	public ChatServerAction handleMessage(ChatMessage message) {
		if (message instanceof ListRoomsMessage) {
			return ChatServerAction.LIST_ROOMS;
		}
		return ChatServerAction.NOT_MY_MESSAGE;
	}
}
