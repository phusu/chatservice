package com.phusu.chatservice;

import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.JoinRoomMessage;
import com.phusu.chatservice.messages.LeaveRoomMessage;
import com.phusu.chatservice.messages.ListRoomsMessage;
import com.phusu.chatservice.messages.MessageType;
import com.phusu.chatservice.messages.SetNameMessage;
import com.phusu.chatservice.messages.TextMessage;
import com.phusu.chatservice.messages.UnknownMessage;

/**
 * ChatMessageParser parses incoming messages from client.
 */
public class ChatMessageParser {
	public static ChatMessage parseLine(String line) throws ChatMessageParseException {
		if (line == null)
			throw new NullPointerException("Line was null.");
		
		if (line.startsWith(MessageType.MESSAGE_TO.getMessageTypeAsString())) {
			return parseTextMessage(line);
		}
		else if (line.startsWith(MessageType.COMMAND_SETNAME.getMessageTypeAsString())) {
			return new SetNameMessage(parseArguments(MessageType.COMMAND_SETNAME, line));
		}
		else if (line.startsWith(MessageType.COMMAND_JOIN.getMessageTypeAsString())) {
			return new JoinRoomMessage(parseArguments(MessageType.COMMAND_JOIN, line));
		}
		else if (line.startsWith(MessageType.COMMAND_LEAVE.getMessageTypeAsString())) {
			return new LeaveRoomMessage(parseArguments(MessageType.COMMAND_LEAVE, line));
		}
		else if (line.startsWith(MessageType.COMMAND_LISTROOMS.getMessageTypeAsString())) {
			return new ListRoomsMessage();
		}
		
		return new UnknownMessage();
	}

	private static TextMessage parseTextMessage(String line) throws ChatMessageParseException {
		String room;
		String message;
		
		int roomStartIndex = line.indexOf(MessageType.MESSAGE_TO.getMessageTypeAsString()) 
				+ MessageType.MESSAGE_TO.getMessageTypeAsString().length() + 1;
		
		if (roomStartIndex == line.length())
			throw new ChatMessageParseException(MessageType.MESSAGE_TO, "Missing destination.");
		
		int roomEndIndex = line.indexOf(" ", roomStartIndex);
		
		if (roomEndIndex == -1) 
			throw new ChatMessageParseException(MessageType.MESSAGE_TO, "Empty message.");
		
		room = line.substring(roomStartIndex, roomEndIndex);
		int messageStartIndex = roomEndIndex + 1;
		message = line.substring(messageStartIndex);
		
		if (message.length() == 0)
			throw new ChatMessageParseException(MessageType.MESSAGE_TO, "Empty message.");
		
		return new TextMessage(room, message);
	}

	private static String parseArguments(MessageType type, String line) throws ChatMessageParseException  {
		String arguments;
		int argumentsStart = line.indexOf(type.getMessageTypeAsString()) 
				+ type.getMessageTypeAsString().length() + 1;
		
		if (argumentsStart >= line.length())
			throw new ChatMessageParseException(type, "Missing arguments.");
		
		int argumentsEnd = line.indexOf(" ", argumentsStart);
		if (argumentsEnd == -1) {
			argumentsEnd = line.length();
		}
		
		arguments = line.substring(argumentsStart, argumentsEnd);
		return arguments;
	}
}
