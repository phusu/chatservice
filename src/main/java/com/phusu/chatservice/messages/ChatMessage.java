package com.phusu.chatservice.messages;

import com.phusu.chatservice.ChatUser;
import com.phusu.chatservice.ClientConnection;

/**
 * ChatMessage is an abstract base class for different types of chat messages.
 */
public abstract class ChatMessage {
	
	private ChatUser author;
	private MessageType messageType;
	private ClientConnection clientConnection;
	
	public ChatMessage(MessageType messageType) {
		this.messageType = messageType;
	}
	
	public ChatUser getAuthor() {
		return author;
	}
	
	public void setAuthor(ChatUser author){
		this.author = author;
	}
	
	public MessageType getMessageType() {
		return messageType;
	}

	public ClientConnection getClientConnection() {
		return clientConnection;
	}

	public void setClientConnection(ClientConnection clientConnection) {
		this.clientConnection = clientConnection;
	}
}
