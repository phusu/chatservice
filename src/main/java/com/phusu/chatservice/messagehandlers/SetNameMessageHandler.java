package com.phusu.chatservice.messagehandlers;

import com.phusu.chatservice.ChatServer;
import com.phusu.chatservice.ChatServerAction;
import com.phusu.chatservice.ChatUser;
import com.phusu.chatservice.SimpleChatUser;
import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.SetNameMessage;

public class SetNameMessageHandler implements IChatMessageHandler {
	private ChatServer server;
	
	public SetNameMessageHandler(ChatServer server) {
		this.server = server;
	}
	
	@Override
	public ChatServerAction handleMessage(ChatMessage message) {
		if (message instanceof SetNameMessage) {
			SetNameMessage setNameMessage = (SetNameMessage) message;
			ChatUser user = new SimpleChatUser(message.getClientConnection(), setNameMessage.getUserName());
			boolean isUnique = server.addUserIfUnique(user);
			
			if (isUnique) {
				message.getClientConnection().setUser(user);
				return ChatServerAction.SUBMITNAME_OK;
			}
			else {
				return ChatServerAction.SUBMITNAME;
			}
		}
		return ChatServerAction.NOT_MY_MESSAGE;
	}
}
