package com.phusu.chatservice;

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
		
		this.setAuthor(author);
	}
	
	public MessageType getMessageType() {
		return messageType;
	}
}
