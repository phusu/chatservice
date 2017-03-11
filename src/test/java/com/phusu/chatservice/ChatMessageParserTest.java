package com.phusu.chatservice;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.JoinRoomMessage;
import com.phusu.chatservice.messages.LeaveRoomMessage;
import com.phusu.chatservice.messages.ListRoomsMessage;
import com.phusu.chatservice.messages.MessageType;
import com.phusu.chatservice.messages.QuitMessage;
import com.phusu.chatservice.messages.SetNameMessage;
import com.phusu.chatservice.messages.TextMessage;
import com.phusu.chatservice.messages.UnknownMessage;

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
		assertTrue("Expected " + DESTINATION + ", was " + msg.getRoomName(), 
				msg.getRoomName().equals(DESTINATION));
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
	public void ChatMessageParserParseUnknownMessageTest() {
		ChatMessage message = ChatMessageParser.parseLine(MessageType.UNKNOWN.getMessageTypeAsString());
		assertTrue(message instanceof UnknownMessage);
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
		assertTrue(message instanceof SetNameMessage);
		SetNameMessage msg = (SetNameMessage) message;
		assertTrue("Expected " + MessageType.COMMAND_SETNAME + ", was " + msg.getMessageType(), 
				msg.getMessageType().equals(MessageType.COMMAND_SETNAME));
		assertTrue("Expected " + COMMAND_SETNAME_ARGUMENTS + ", was " + msg.getUserName(), 
				msg.getUserName().equals(COMMAND_SETNAME_ARGUMENTS));
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
		assertTrue(message instanceof JoinRoomMessage);
		JoinRoomMessage msg = (JoinRoomMessage) message;
		assertTrue("Expected " + MessageType.COMMAND_JOIN + ", was " + msg.getMessageType(), 
				msg.getMessageType().equals(MessageType.COMMAND_JOIN));
		assertTrue("Expected " + DESTINATION + ", was " + msg.getRoomName(), 
				msg.getRoomName().equals(DESTINATION));
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
		assertTrue(message instanceof LeaveRoomMessage);
		LeaveRoomMessage msg = (LeaveRoomMessage) message;
		assertTrue("Expected " + MessageType.COMMAND_LEAVE + ", was " + msg.getMessageType(), 
				msg.getMessageType().equals(MessageType.COMMAND_LEAVE));
		assertTrue("Expected " + DESTINATION + ", was " + msg.getRoomName(), 
				msg.getRoomName().equals(DESTINATION));
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
		assertTrue(message instanceof ListRoomsMessage);
		ListRoomsMessage msg = (ListRoomsMessage) message;
		assertTrue("Expected " + MessageType.COMMAND_LISTROOMS + ", was " + msg.getMessageType(), 
				msg.getMessageType().equals(MessageType.COMMAND_LISTROOMS));
	}
	

	@Test
	public void ChatMessageParserParseQuitTest() {
		ChatMessage message = ChatMessageParser.parseLine(MessageType.COMMAND_QUIT.getMessageTypeAsString());
		assertTrue(message instanceof QuitMessage);
		QuitMessage msg = (QuitMessage) message;
		assertTrue("Expected " + MessageType.COMMAND_QUIT + ", was " + msg.getMessageType(), 
				msg.getMessageType().equals(MessageType.COMMAND_QUIT));
	}
	
	@Test
	public void ChatMessageParseMessageAuthorConnectionTest() {
		ChatMessage message = ChatMessageParser.parseLine(MessageType.COMMAND_LEAVE.getMessageTypeAsString()  + " " + DESTINATION);
		ChatUser user = mock(SimpleChatUser.class);
		ClientConnection connection = mock(ClientConnection.class);
		message.setAuthor(user);
		assertTrue(message.getAuthor().equals(user));
		message.setClientConnection(connection);
		assertTrue(message.getClientConnection() == connection);
	}
}
