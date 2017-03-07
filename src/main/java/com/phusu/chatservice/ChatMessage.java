package com.phusu.chatservice;

/**
 * ChatMessage is an abstract base class for different types of chat messages.
 */
public abstract class ChatMessage {
	
	private ChatUser author;
	private MessageType messageType;
	
	public ChatMessage(MessageType messageType) {
		this.messageType = messageType;
	}
	
	public ChatUser getAuthor() {
		return author;
	}
	
	public void setAuthor(ChatUser author){
		if (author == null)
			throw new NullPointerException("Author was null.");
		
		this.author = author;
	}
	
	public MessageType getMessageType() {
		return messageType;
	}
}
