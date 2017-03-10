package com.phusu.chatservice;

public class ChatServerResponse {
	private String response;
	
	public ChatServerResponse() {
		this.response = null;
	}
	
	public ChatServerResponse(String response) {
		this.response = response;
	}
	
	public String getResponse() {
		return response;
	}
	
	public boolean isResponseHandled() {
		return response != null;
	}
}
