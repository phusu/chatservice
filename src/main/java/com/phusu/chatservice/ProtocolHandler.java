package com.phusu.chatservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ProtocolHandler class is a 
 */
public class ProtocolHandler {
	
	private static final Logger logger = LogManager.getLogger(ProtocolHandler.class);
	
	private BufferedReader input;
	private PrintWriter output;

	public ProtocolHandler(BufferedReader input, PrintWriter output) {
		this.input = input;
		this.output = output;
	}
	
	public ChatMessage getMessage() throws IOException {
		String line = input.readLine();
		logger.trace("Read line: " + line);
		return ChatMessageParser.parseLine(line);
	}
	
	public void sendLine(String line) {
		output.println(line);
		logger.trace("Sent line: " + line);
	}
}
