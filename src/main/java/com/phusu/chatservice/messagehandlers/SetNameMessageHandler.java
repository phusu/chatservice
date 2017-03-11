package com.phusu.chatservice.messagehandlers;

import com.phusu.chatservice.ChatServer;
import com.phusu.chatservice.ChatServerResponse;
import com.phusu.chatservice.ChatUser;
import com.phusu.chatservice.SimpleChatUser;
import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.MessageType;
import com.phusu.chatservice.messages.SetNameMessage;

public class SetNameMessageHandler implements IChatMessageHandler {
	private ChatServer server;
	
	public SetNameMessageHandler(ChatServer server) {
		this.server = server;
	}
	
	@Override
	public ChatServerResponse handleMessage(ChatMessage message) {
		if (message instanceof SetNameMessage) {
			SetNameMessage setNameMessage = (SetNameMessage) message;
			if (setNameMessage.getAuthor() != null) {
				String response = MessageType.RESPONSE_NOT_IMPLEMENTED.getMessageTypeAsString();
				return new ChatServerResponse(response);
			}
			else {
				ChatUser user = new SimpleChatUser(message.getClientConnection(), setNameMessage.getUserName());
				boolean isUnique = server.addUserIfUnique(user);
				
				if (isUnique) {
					message.getClientConnection().setUser(user);
					message.setAuthor(user);
					String response = MessageType.RESPONSE_SETNAME_OK.getMessageTypeAsString().replace("<name>", 
							setNameMessage.getUserName());
					return new ChatServerResponse(response);
				}
				else {
					String response = MessageType.RESPONSE_SETNAME_NOT_VALID.getMessageTypeAsString().replace("<message>", 
							setNameMessage.getUserName());
					return new ChatServerResponse(response);
				}
			}
		}
		return new ChatServerResponse();
	}
}
