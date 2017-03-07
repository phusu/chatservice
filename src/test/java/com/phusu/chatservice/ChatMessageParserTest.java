package com.phusu.chatservice;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit tests for ChatMessageParser.
 */
public class ChatMessageParserTest {
	
	private final static String DESTINATION = "testroom";
	private final static String CONTENTS = "Simple message.";
	private final static String INVALID_MESSAGE = "PICK this UP";
	private final static String COMMAND_SETNAME_ARGUMENTS = "foo";

	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void ChatMessageParserParseSimpleMessageTest() {
		ChatMessage message = ChatMessageParser.parseLine(MessageType.MESSAGE_TO.getMessageTypeAsString() 
				+ " " + DESTINATION + " " + CONTENTS);
		assertTrue(message instanceof TextMessage);
		TextMessage msg = (TextMessage) message;
		assertTrue("Expected " + DESTINATION + ", was " + msg.getChatRoomName(), 
				msg.getChatRoomName().equals(DESTINATION));
		assertTrue("Expected " + CONTENTS + ", was " + msg.getMessage(), 
				msg.getMessage().equals(CONTENTS));
	}
	
	@Test
	public void ChatMessageParserParseNullMessageTest() {
		exception.expect(NullPointerException.class);
		exception.expectMessage("Line was null.");
		ChatMessageParser.parseLine(null);
	}


	@Test
	public void ChatMessageParserParseInvalidMessageTest() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Unknown command.");
		ChatMessageParser.parseLine(INVALID_MESSAGE);
	}

	@Test
	public void ChatMessageParserParseEmptyMessageTest() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Empty message.");
		ChatMessageParser.parseLine(MessageType.MESSAGE_TO.getMessageTypeAsString()  + " " + DESTINATION + " ");
	}

	@Test
	public void ChatMessageParserParseEmptyMessage2Test() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Empty message.");
		ChatMessageParser.parseLine(MessageType.MESSAGE_TO.getMessageTypeAsString()  + " " + DESTINATION);
	}
	
	@Test
	public void ChatMessageParserParseMissingDestinationTest() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Missing destination.");
		ChatMessageParser.parseLine(MessageType.MESSAGE_TO.getMessageTypeAsString()  + " ");
	}

	@Test
	public void ChatMessageParserParseSetNameTest() {
		ChatMessage message = ChatMessageParser.parseLine(MessageType.COMMAND_SETNAME.getMessageTypeAsString()  
				+ " " + COMMAND_SETNAME_ARGUMENTS);
		assertTrue(message instanceof CommandMessage);
		CommandMessage msg = (CommandMessage) message;
		assertTrue("Expected " + MessageType.COMMAND_SETNAME + ", was " + msg.getMessageType(), 
				msg.getMessageType().equals(MessageType.COMMAND_SETNAME));
		assertTrue("Expected " + COMMAND_SETNAME_ARGUMENTS + ", was " + msg.getArguments(), 
				msg.getArguments().equals(COMMAND_SETNAME_ARGUMENTS));
	}

	@Test
	public void ChatMessageParserParseSetNameEmptyTest() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Missing arguments.");
		ChatMessageParser.parseLine(MessageType.COMMAND_SETNAME.getMessageTypeAsString()  + " ");
	}

	@Test
	public void ChatMessageParserParseJoinRoomTest() {
		ChatMessage message = ChatMessageParser.parseLine(MessageType.COMMAND_JOIN.getMessageTypeAsString()  + " " + DESTINATION);
		assertTrue(message instanceof CommandMessage);
		CommandMessage msg = (CommandMessage) message;
		assertTrue("Expected " + MessageType.COMMAND_JOIN + ", was " + msg.getMessageType(), 
				msg.getMessageType().equals(MessageType.COMMAND_JOIN));
		assertTrue("Expected " + DESTINATION + ", was " + msg.getArguments(), 
				msg.getArguments().equals(DESTINATION));
	}

	@Test
	public void ChatMessageParserParseJoinRoomEmptyTest() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Missing arguments.");
		ChatMessageParser.parseLine(MessageType.COMMAND_JOIN.getMessageTypeAsString()  + " ");
	}

	@Test
	public void ChatMessageParserParseLeaveRoomTest() {
		ChatMessage message = ChatMessageParser.parseLine(MessageType.COMMAND_LEAVE.getMessageTypeAsString()  + " " + DESTINATION);
		assertTrue(message instanceof CommandMessage);
		CommandMessage msg = (CommandMessage) message;
		assertTrue("Expected " + MessageType.COMMAND_LEAVE + ", was " + msg.getMessageType(), 
				msg.getMessageType().equals(MessageType.COMMAND_LEAVE));
		assertTrue("Expected " + DESTINATION + ", was " + msg.getArguments(), 
				msg.getArguments().equals(DESTINATION));
	}

	@Test
	public void ChatMessageParserParseLeaveRoomEmptyTest() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Missing arguments.");
		ChatMessageParser.parseLine(MessageType.COMMAND_LEAVE.getMessageTypeAsString()  + " ");
	}

	@Test
	public void ChatMessageParserParseListRoomsTest() {
		ChatMessage message = ChatMessageParser.parseLine(MessageType.COMMAND_LISTROOMS.getMessageTypeAsString());
		assertTrue(message instanceof CommandMessage);
		CommandMessage msg = (CommandMessage) message;
		assertTrue("Expected " + MessageType.COMMAND_LISTROOMS + ", was " + msg.getMessageType(), 
				msg.getMessageType().equals(MessageType.COMMAND_LISTROOMS));
	}
}
