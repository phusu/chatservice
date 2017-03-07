package com.phusu.chatservice;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ChatServerTest {

	private static final String USER_NAME_FOO = "foo";
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void ChatServerListRoomsTest() {
		ChatServer server = new ChatServer();
		assertTrue(server.listRooms().size() == 0);
	}
	
	@Test
	public void ChatServerAddRoomTest() {
		ChatServer server = new ChatServer();
		server.addRoom(createNiceMock(ChatRoom.class));
		assertTrue(server.listRooms().size() == 1);
	}
	
	@Test
	public void ChatServerDeleteRoomTest() {
		ChatServer server = new ChatServer();
		ChatRoom room = createNiceMock(ChatRoom.class);
		server.addRoom(room);
		assertTrue(server.listRooms().size() == 1);
		server.deleteRoom(room);
		assertTrue(server.listRooms().size() == 0);
	}

	@Test
	public void ChatServerAddRoomNullTest() {
		ChatServer server = new ChatServer();
		exception.expect(NullPointerException.class);
		exception.expectMessage("Room was null.");
		server.addRoom(null);
	}

	@Test
	public void ChatServerDeleteRoomNullTest() {
		ChatServer server = new ChatServer();
		exception.expect(NullPointerException.class);
		exception.expectMessage("Room was null.");
		server.deleteRoom(null);
	}
	
	@Test
	public void ChatServerListUsersTest() {
		ChatServer server = new ChatServer();
		assertTrue(server.listUsers().size() == 0);
	}
	
	@Test
	public void ChatServerAddUserTest() {
		ChatServer server = new ChatServer();
		server.addUserIfUnique(createNiceMock(ChatUser.class));
		assertTrue(server.listUsers().size() == 1);
	}
	
	@Test
	public void ChatServerAddUserDuplicateTest() {
		ChatServer server = new ChatServer();
		ChatUser user = new SimpleChatUser(USER_NAME_FOO);
		ChatUser user2 = new SimpleChatUser(USER_NAME_FOO);
		assertTrue(user.equals(user2));
		assertTrue(server.addUserIfUnique(user));
		assertTrue(server.listUsers().size() == 1);
		assertFalse(server.addUserIfUnique(user2));
		assertTrue(server.listUsers().size() == 1);
	}
	
	@Test
	public void ChatServerDeleteUserTest() {
		ChatServer server = new ChatServer();
		ChatUser user = createNiceMock(ChatUser.class);
		server.addUserIfUnique(user);
		assertTrue(server.listUsers().size() == 1);
		server.deleteUser(user);
		assertTrue(server.listUsers().size() == 0);
	}

	@Test
	public void ChatServerAddUserNullTest() {
		ChatServer server = new ChatServer();
		exception.expect(NullPointerException.class);
		exception.expectMessage("User was null.");
		server.addUserIfUnique(null);
	}

	@Test
	public void ChatServerDeleteUserNullTest() {
		ChatServer server = new ChatServer();
		exception.expect(NullPointerException.class);
		exception.expectMessage("User was null.");
		server.deleteUser(null);
	}
	
}
