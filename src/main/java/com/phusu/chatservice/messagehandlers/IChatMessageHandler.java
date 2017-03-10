package com.phusu.chatservice.messagehandlers;

import com.phusu.chatservice.ChatServerResponse;
import com.phusu.chatservice.messages.ChatMessage;

/**
 * IChatMessageHandler, interface for all different message handlers.
 */
public interface IChatMessageHandler {
	public ChatServerResponse handleMessage(ChatMessage message);
}
