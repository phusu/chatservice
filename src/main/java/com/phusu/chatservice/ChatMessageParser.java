package com.phusu.chatservice;

/**
 * ChatMessageParser parses incoming messages from client.
 * 
 * Supported commands:
 * MESSAGE TO roomname message
 * COMMAND SETNAME name
 * COMMAND JOIN roomname
 * COMMAND LEAVE roomname
 * COMMAND LISTROOMS
 * COMMAND QUIT
 */
public class ChatMessageParser {
	public static ChatMessage parseLine(String line) {
		if (line == null)
			throw new NullPointerException("Line was null.");
		
		if (line.startsWith(MessageType.MESSAGE_TO.getMessageTypeAsString())) {
			return parseMessage(line);
		}
		else if (line.startsWith(MessageType.COMMAND_SETNAME.getMessageTypeAsString())) {
			return parseCommandMessage(MessageType.COMMAND_SETNAME, line);
		}
		else if (line.startsWith(MessageType.COMMAND_JOIN.getMessageTypeAsString())) {
			return parseCommandMessage(MessageType.COMMAND_JOIN, line);
		}
		else if (line.startsWith(MessageType.COMMAND_LEAVE.getMessageTypeAsString())) {
			return parseCommandMessage(MessageType.COMMAND_LEAVE, line);
		}
		else if (line.startsWith(MessageType.COMMAND_LISTROOMS.getMessageTypeAsString())) {
			return parseCommandMessage(MessageType.COMMAND_LISTROOMS, line);
		}
		else if (line.startsWith(MessageType.COMMAND_QUIT.getMessageTypeAsString())) {
			return parseCommandMessage(MessageType.COMMAND_QUIT, line);
		}
		
		throw new IllegalArgumentException("Unknown command.");
	}

	private static TextMessage parseMessage(String line) {
		String room;
		String message;
		
		int roomStartIndex = line.indexOf(MessageType.MESSAGE_TO.getMessageTypeAsString()) 
				+ MessageType.MESSAGE_TO.getMessageTypeAsString().length() + 1;
		
		if (roomStartIndex == line.length())
			throw new IllegalArgumentException("Missing destination.");
		
		int roomEndIndex = line.indexOf(" ", roomStartIndex);
		
		if (roomEndIndex == -1) 
			throw new IllegalArgumentException("Empty message.");
		
		room = line.substring(roomStartIndex, roomEndIndex);
		int messageStartIndex = roomEndIndex + 1;
		message = line.substring(messageStartIndex);
		
		if (message.length() == 0)
			throw new IllegalArgumentException("Empty message.");
		
		return new TextMessage(room, message);
	}

	private static ChatMessage parseCommandMessage(MessageType type, String line) {
		if (type == MessageType.COMMAND_LISTROOMS) {
			return new CommandMessage(MessageType.COMMAND_LISTROOMS);
		}
		else if (type == MessageType.COMMAND_QUIT) {
			return new CommandMessage(MessageType.COMMAND_QUIT);
		}
		else {
			return new CommandMessage(type, parseArguments(type, line));
		}
	}

	private static String parseArguments(MessageType type, String line) {
		String arguments;
		int argumentsStart = line.indexOf(type.getMessageTypeAsString()) 
				+ type.getMessageTypeAsString().length() + 1;
		
		if (argumentsStart >= line.length())
			throw new IllegalArgumentException("Missing arguments.");
		
		arguments = line.substring(argumentsStart);
		return arguments;
	}
}
