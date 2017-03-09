package com.phusu.chatservice;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.phusu.chatservice.messages.TextMessage;

/**
 * Unit tests for ChatServer.
 */
public class ChatServerTest {

	private static final String USER_NAME_FOO = "foo";
	private static final String ROOM_NAME = "general";
	
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
		assertTrue(server.listUsersInRoom(ROOM_NAME + " 2").size() == 0);
	}
	
	@Test
	public void ChatServerDeliverMessageTest() {
		ChatServer server = new ChatServer();
		server.createRoomIfUnique(ROOM_NAME, ChatRoomType.PUBLIC);
		TextMessage message = mock(TextMessage.class);
		when(message.getChatRoomName()).thenReturn("general");
		when(message.getMessage()).thenReturn("test message");
		assertTrue(server.deliverMessage(message));
	}
}
