package com.phusu.chatservice;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.phusu.chatservice.messages.JoinRoomMessage;
import com.phusu.chatservice.messages.LeaveRoomMessage;
import com.phusu.chatservice.messages.ListRoomsMessage;
import com.phusu.chatservice.messages.MessageType;
import com.phusu.chatservice.messages.QuitMessage;
import com.phusu.chatservice.messages.SetNameMessage;
import com.phusu.chatservice.messages.TextMessage;
import com.phusu.chatservice.messages.UnknownMessage;

/**
 * Unit tests for ChatServer.
 */
public class ChatServerTest {

	private static final String USER_NAME_FOO = "foo";
	private static final String ROOM_NAME = "general";
	private static final String TEXT_MESSAGE = "test message";
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void ChatServerListRoomsTest() {
		ChatServer server = new ChatServer();
		assertTrue(server.listPublicRoomNames().size() == 0);
		server.createRoomIfUnique(ROOM_NAME, ChatRoomType.PUBLIC);
		assertTrue(server.listPublicRoomNames().size() == 1);
	}
	
	@Test
	public void ChatServerAddRoomTest() {
		ChatServer server = new ChatServer();
		server.createRoomIfUnique(ROOM_NAME, ChatRoomType.PUBLIC);
		assertTrue(server.listPublicRoomNames().size() == 1);
	}
	
	@Test
	public void ChatServerAddRoomDuplicateTest() {
		ChatServer server = new ChatServer();
		server.createRoomIfUnique(ROOM_NAME, ChatRoomType.PUBLIC);
		assertTrue(server.listPublicRoomNames().size() == 1);
		server.createRoomIfUnique(ROOM_NAME, ChatRoomType.PUBLIC);
		assertTrue(server.listPublicRoomNames().size() == 1);
	}
	
	@Test
	public void ChatServerDeleteRoomTest() {
		ChatServer server = new ChatServer();
		server.createRoomIfUnique(ROOM_NAME, ChatRoomType.PUBLIC);
		assertTrue(server.listPublicRoomNames().size() == 1);
		assertTrue(server.deleteRoomIfEmpty(ROOM_NAME));
		assertTrue(server.listPublicRoomNames().size() == 0);
	}

	@Test
	public void ChatServerDeleteRoomNotExistTest() {
		ChatServer server = new ChatServer();

		assertFalse(server.deleteRoomIfEmpty(ROOM_NAME));
	}

	@Test
	public void ChatServerAddRoomNullNameTest() {
		ChatServer server = new ChatServer();
		exception.expect(NullPointerException.class);
		exception.expectMessage("Room name was null.");
		server.createRoomIfUnique(null, ChatRoomType.PUBLIC);
	}

	@Test
	public void ChatServerAddRoomNullTypeTest() {
		ChatServer server = new ChatServer();
		exception.expect(NullPointerException.class);
		exception.expectMessage("Room type was null.");
		server.createRoomIfUnique(ROOM_NAME, null);
	}

	@Test
	public void ChatServerDeleteRoomNullTest() {
		ChatServer server = new ChatServer();
		exception.expect(NullPointerException.class);
		exception.expectMessage("Room was null.");
		server.deleteRoomIfEmpty(null);
	}
	
	@Test
	public void ChatServerListUsersTest() {
		ChatServer server = new ChatServer();
		assertTrue(server.listUsers().size() == 0);
	}
	
	@Test
	public void ChatServerAddUserTest() {
		ChatServer server = new ChatServer();
		ChatUser user = mock(ChatUser.class);
		assertTrue(server.addUserIfUnique(user));
		assertTrue(server.listUsers().size() == 1);
	}
	
	@Test
	public void ChatServerAddUserDuplicateTest() {
		ChatServer server = new ChatServer();
		ChatUser user = new SimpleChatUser(null, USER_NAME_FOO);
		ChatUser user2 = new SimpleChatUser(null, USER_NAME_FOO);
		assertTrue(user.equals(user2));
		assertTrue(server.addUserIfUnique(user));
		assertTrue(server.listUsers().size() == 1);
		assertFalse(server.addUserIfUnique(user2));
		assertTrue(server.listUsers().size() == 1);
	}

	@Test
	public void ChatServerAddUserNullTest() {
		ChatServer server = new ChatServer();
		exception.expect(NullPointerException.class);
		exception.expectMessage("User was null.");
		server.addUserIfUnique(null);
	}
	
	@Test
	public void ChatServerRemoveUserTest() {
		ChatServer server = new ChatServer();
		ChatUser user = mock(ChatUser.class);
		assertTrue(server.addUserIfUnique(user));
		assertTrue(server.listUsers().size() == 1);
		server.removeUser(user);
		assertTrue(server.listUsers().size() == 0);
	}
	
	@Test
	public void ChatServerRemoveUserNotExistsTest() {
		ChatServer server = new ChatServer();
		ChatUser user = mock(ChatUser.class);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("User doesn't exist.");
		server.removeUser(user);
	}

	@Test
	public void ChatServerRemoveUserNullTest() {
		ChatServer server = new ChatServer();
		exception.expect(NullPointerException.class);
		exception.expectMessage("User was null.");
		server.removeUser(null);
	}
	
	@Test
	public void ChatServerAddUserToRoomTest() {
		ChatServer server = new ChatServer();
		ChatUser user = new SimpleChatUser(null, USER_NAME_FOO);
		server.createRoomIfUnique(ROOM_NAME + " 1", ChatRoomType.PUBLIC);
		
		assertFalse(server.addUserToRoom(user, ROOM_NAME));
		assertTrue(server.addUserToRoom(user, ROOM_NAME + " 1"));
	}
	
	@Test
	public void ChatServerRemoveUserFromRoomTest() {
		ChatServer server = new ChatServer();
		ChatUser user = new SimpleChatUser(null, USER_NAME_FOO);
		
		server.createRoomIfUnique(ROOM_NAME + " 1", ChatRoomType.PUBLIC);
		server.createRoomIfUnique(ROOM_NAME + " 2", ChatRoomType.PUBLIC);
		assertTrue(server.addUserIfUnique(user));
		assertTrue(server.addUserToRoom(user, ROOM_NAME + " 2"));
		
		assertTrue(server.listUsers().size() == 1);
		server.removeUser(user);
		assertTrue(server.listUsers().size() == 0);
		assertTrue(server.listUsersInRoom(ROOM_NAME + " 2") == null);
		server.createRoomIfUnique(ROOM_NAME + " 2", ChatRoomType.PUBLIC);
		
		assertTrue(server.addUserIfUnique(user));
		assertTrue(server.addUserToRoom(user, ROOM_NAME + " 1"));
		assertTrue(server.addUserToRoom(user, ROOM_NAME + " 2"));
		assertTrue(server.listUsers().size() == 1);
		assertTrue(server.listUsersInRoom(ROOM_NAME + " 1").size() == 1);
		assertTrue(server.listUsersInRoom(ROOM_NAME + " 2").size() == 1);
		assertFalse(server.removeUserFromRoom(user, ROOM_NAME));
		assertTrue(server.removeUserFromRoom(user, ROOM_NAME + " 1"));
		assertTrue(server.listUsersInRoom(ROOM_NAME + " 1").size() == 0);
		assertTrue(server.listUsersInRoom(ROOM_NAME + " 2").size() == 1);
	}
	
	@Test
	public void ChatServerDeliverMessageTest() {
		ChatServer server = new ChatServer();
		server.createRoomIfUnique(ROOM_NAME, ChatRoomType.PUBLIC);
		TextMessage message = mock(TextMessage.class);
		when(message.getRoomName()).thenReturn(ROOM_NAME);
		when(message.getMessage()).thenReturn("test message");
		assertTrue(server.deliverMessageToRoom(message));
		when(message.getRoomName()).thenReturn(ROOM_NAME + " 1");
		assertFalse(server.deliverMessageToRoom(message));
	}
	
	@Test
	public void ChatServerHandleJoinRoomMessageTest() {
		ChatServer server = new ChatServer();
		ClientConnection connection = mock(ClientConnection.class);
		
		SimpleChatUser user = mock(SimpleChatUser.class);
		when(user.getName()).thenReturn(USER_NAME_FOO);
		when(user.getClientConnection()).thenReturn(connection);
		
		JoinRoomMessage message = mock(JoinRoomMessage.class);
		when(message.getRoomName()).thenReturn(ROOM_NAME);
		when(message.getAuthor()).thenReturn(user);
		
		assertTrue(message.getAuthor() == user);

		assertTrue(server.listUsersInRoom(ROOM_NAME) == null);

		String expected = "";
		String response = server.handleMessage(connection, message);
		assertTrue("Expected " + expected + ", got: " + response, response.compareTo(expected) == 0);
		assertFalse(server.listUsersInRoom(ROOM_NAME).isEmpty());
		
		expected = MessageType.RESPONSE_JOIN_NOT_VALID.getMessageTypeAsString().replace("<message>", ROOM_NAME);
		response = server.handleMessage(connection, message);
		assertTrue("Expected " + expected + ", got: " + response, response.compareTo(expected) == 0);
		assertTrue(server.listUsersInRoom(ROOM_NAME).size() == 1);
	}
	
	@Test
	public void ChatServerHandleLeaveRoomMessageTest() {
		ChatServer server = new ChatServer();
		ClientConnection connection = mock(ClientConnection.class);
		
		SimpleChatUser user = mock(SimpleChatUser.class);
		when(user.getName()).thenReturn(USER_NAME_FOO);
		when(user.getClientConnection()).thenReturn(connection);
		
		LeaveRoomMessage message = mock(LeaveRoomMessage.class);
		when(message.getRoomName()).thenReturn(ROOM_NAME);
		when(message.getAuthor()).thenReturn(user);
		
		assertTrue(message.getAuthor() == user);
		
		String expected = MessageType.RESPONSE_LEAVE_NOT_VALID.getMessageTypeAsString().replace("<message>", ROOM_NAME);
		String response = server.handleMessage(connection, message);
		assertTrue("Expected " + expected + ", got: " + response, response.compareTo(expected) == 0);
		assertTrue(server.listUsersInRoom(ROOM_NAME) == null);

		assertTrue(server.addUserIfUnique(user));
		server.createRoomIfUnique(ROOM_NAME, ChatRoomType.PUBLIC);
		assertTrue(server.addUserToRoom(user, ROOM_NAME));
		assertFalse(server.listUsersInRoom(ROOM_NAME).isEmpty());
		
		expected = MessageType.RESPONSE_LEAVE_OK.getMessageTypeAsString().replace("<message>", ROOM_NAME);
		response = server.handleMessage(connection, message);
		assertTrue("Expected " + expected + ", got: " + response, response.compareTo(expected) == 0);
		assertTrue(server.listUsersInRoom(ROOM_NAME) == null);
	}

	@Test
	public void ChatServerHandleListRoomsMessageTest() {
		ChatServer server = new ChatServer();
		ClientConnection connection = mock(ClientConnection.class);
		
		SimpleChatUser user = mock(SimpleChatUser.class);
		when(user.getName()).thenReturn(USER_NAME_FOO);
		when(user.getClientConnection()).thenReturn(connection);
		
		ListRoomsMessage message = mock(ListRoomsMessage.class);
		when(message.getAuthor()).thenReturn(user);
		
		assertTrue(message.getAuthor() == user);
		
		server.createRoomIfUnique("room1", ChatRoomType.PUBLIC);
		server.createRoomIfUnique("room2", ChatRoomType.PUBLIC);
		
		String expected = MessageType.RESPONSE_LISTROOMS.getMessageTypeAsString().replace("<rooms>", "room1 room2");
		String response = server.handleMessage(connection, message);
		assertTrue("Expected " + expected + ", got: " + response, response.compareTo(expected) == 0);
	}

	@Test
	public void ChatServerHandleSetNameMessageTest() {
		ChatServer server = new ChatServer();
		ClientConnection connection = mock(ClientConnection.class);
		
		SetNameMessage message = mock(SetNameMessage.class);
		when(message.getUserName()).thenReturn(USER_NAME_FOO);
		when(message.getClientConnection()).thenReturn(connection);
		
		String expected = MessageType.RESPONSE_SETNAME_OK.getMessageTypeAsString().replace("<name>", message.getUserName());
		String response = server.handleMessage(connection, message);
		assertTrue("Expected " + expected + ", got: " + response, response.compareTo(expected) == 0);
	}

	@Test
	public void ChatServerHandleSetNameDuplicateMessageTest() {
		ChatServer server = new ChatServer();
		ClientConnection connection = mock(ClientConnection.class);
		ClientConnection connection2 = mock(ClientConnection.class);
		
		SetNameMessage message = mock(SetNameMessage.class);
		when(message.getUserName()).thenReturn(USER_NAME_FOO);
		when(message.getClientConnection()).thenReturn(connection);
		
		SetNameMessage message2 = mock(SetNameMessage.class);
		when(message2.getUserName()).thenReturn(USER_NAME_FOO);
		when(message2.getClientConnection()).thenReturn(connection2);
		
		String expected = MessageType.RESPONSE_SETNAME_OK.getMessageTypeAsString().replace("<name>", message.getUserName());
		String response = server.handleMessage(connection, message);
		assertTrue("Expected " + expected + ", got: " + response, response.compareTo(expected) == 0);

		expected = MessageType.RESPONSE_SETNAME_NOT_VALID.getMessageTypeAsString().replace("<message>", message2.getUserName());
		response = server.handleMessage(connection2, message2);
		assertTrue("Expected " + expected + ", got: " + response, response.compareTo(expected) == 0);
	}
	
	@Test
	public void ChatServerHandleTextMessageTest() {
		ChatServer server = new ChatServer();
		ClientConnection connection = mock(ClientConnection.class);
		
		SimpleChatUser user = mock(SimpleChatUser.class);
		when(user.getName()).thenReturn(USER_NAME_FOO);
		when(user.getClientConnection()).thenReturn(connection);
		
		TextMessage message = mock(TextMessage.class);
		when(message.getAuthor()).thenReturn(user);
		when(message.getMessage()).thenReturn(TEXT_MESSAGE);
		when(message.getRoomName()).thenReturn(ROOM_NAME);

		server.createRoomIfUnique(ROOM_NAME, ChatRoomType.PUBLIC);
		assertTrue(server.addUserToRoom(user, ROOM_NAME));	
		
		assertTrue(message.getAuthor() == user);
		
		String expected = MessageType.RESPONSE_MESSAGE_FROM.getMessageTypeAsString().replace("<from>", 
				message.getAuthor().getName()).replace("<to>", message.getRoomName()).replace("<message>", 
				message.getMessage());
		String response = server.handleMessage(connection, message);
		assertTrue(response.isEmpty());
		
		// TODO Would need a way to test what is written to ClientConnection's socket's output stream
	}
	
	@Test
	public void ChatServerHandleUnknownMessageTest() {
		ChatServer server = new ChatServer();
		ClientConnection connection = mock(ClientConnection.class);
		
		UnknownMessage message = mock(UnknownMessage.class);
		when(message.getClientConnection()).thenReturn(connection);
		
		String expected = MessageType.UNKNOWN.getMessageTypeAsString();
		String response = server.handleMessage(connection, message);
		assertTrue("Expected " + expected + ", got: " + response, response.compareTo(expected) == 0);
	}
	
	@Test
	public void ChatServerHandleQuitMessageTest() {
		ChatServer server = new ChatServer();
		ClientConnection connection = mock(ClientConnection.class);
		
		SetNameMessage message = new SetNameMessage(USER_NAME_FOO);
		message.setClientConnection(connection);
		
		String expected = MessageType.RESPONSE_SETNAME_OK.getMessageTypeAsString().replace("<name>", message.getUserName());
		String response = server.handleMessage(connection, message);
		assertTrue("Expected " + expected + ", got: " + response, response.compareTo(expected) == 0);
		
		assertFalse(server.listUsers().isEmpty());
		
		QuitMessage quitMessage = mock(QuitMessage.class);
		when(quitMessage.getClientConnection()).thenReturn(connection);
		when(quitMessage.getAuthor()).thenReturn(message.getAuthor());
		
		response = server.handleMessage(connection, quitMessage);
		assertTrue(server.listUsers().isEmpty());
		assertTrue(response.isEmpty());
	}
	
	@Test
	public void ChatServerListUsersInRoomTest() {
		ChatServer server = new ChatServer();
		assertTrue(server.listUsersInRoom(ROOM_NAME) == null);
		server.createRoomIfUnique(ROOM_NAME, ChatRoomType.PUBLIC);
		assertTrue(server.listUsersInRoom(ROOM_NAME).isEmpty());
		SimpleChatUser user = mock(SimpleChatUser.class);
		assertTrue(server.addUserToRoom(user, ROOM_NAME));

		exception.expect(NullPointerException.class);
		exception.expectMessage("Room name was null.");
		server.listUsersInRoom(null);
	}
}
