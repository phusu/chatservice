package com.phusu.chatservice.messagehandlers;

import com.phusu.chatservice.ChatServerAction;
import com.phusu.chatservice.messages.ChatMessage;

/**
 * IChatMessageHandler, interface for all different message handlers.
 */
public interface IChatMessageHandler {
	public ChatServerAction handleMessage(ChatMessage message);
}
