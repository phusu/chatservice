package com.phusu.chatservice;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit tests for ChatServer.
 */
public class ChatServerTest {

	private static final String USER_NAME_FOO = "foo";
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void ChatServerListRoomsTest() {
		ChatServer server = new ChatServer();
		assertTrue(server.listPublicRoomNames().size() == 0);
		ChatRoom room = mock(ChatRoom.class);
		when(room.getType()).thenReturn(ChatRoomType.PUBLIC);
		when(room.getName()).thenReturn("general");
		assertTrue(server.addRoomIfUnique(room));
		assertTrue(server.listPublicRoomNames().size() == 1);
	}
	
	@Test
	public void ChatServerAddRoomTest() {
		ChatServer server = new ChatServer();
		ChatRoom room = mock(ChatRoom.class);
		when(room.getType()).thenReturn(ChatRoomType.PUBLIC);
		when(room.getName()).thenReturn("general");
		assertTrue(server.addRoomIfUnique(room));
		assertTrue(server.listPublicRoomNames().size() == 1);
	}
	
	@Test
	public void ChatServerAddRoomDuplicateTest() {
		ChatServer server = new ChatServer();
		ChatRoom room = mock(ChatRoom.class);
		when(room.getType()).thenReturn(ChatRoomType.PUBLIC);
		when(room.getName()).thenReturn("general");

		ChatRoom room2 = mock(ChatRoom.class);
		when(room2.getType()).thenReturn(ChatRoomType.PUBLIC);
		when(room2.getName()).thenReturn("general");
		
		assertTrue(server.addRoomIfUnique(room));
		assertTrue(server.listPublicRoomNames().size() == 1);
		assertFalse(server.addRoomIfUnique(room2));
		assertTrue(server.listPublicRoomNames().size() == 1);
	}
	
	@Test
	public void ChatServerDeleteRoomTest() {
		ChatServer server = new ChatServer();
		ChatRoom room = mock(ChatRoom.class);
		when(room.getType()).thenReturn(ChatRoomType.PUBLIC);
		when(room.getName()).thenReturn("general");

		assertTrue(server.addRoomIfUnique(room));
		assertTrue(server.listPublicRoomNames().size() == 1);
		assertTrue(server.deleteRoomIfExists(room));
		assertTrue(server.listPublicRoomNames().size() == 0);
	}

	@Test
	public void ChatServerDeleteRoomNotExistTest() {
		ChatServer server = new ChatServer();
		ChatRoom room = mock(ChatRoom.class);
		when(room.getType()).thenReturn(ChatRoomType.PUBLIC);
		when(room.getName()).thenReturn("general");

		assertFalse(server.deleteRoomIfExists(room));
	}

	@Test
	public void ChatServerAddRoomNullTest() {
		ChatServer server = new ChatServer();
		exception.expect(NullPointerException.class);
		exception.expectMessage("Room was null.");
		server.addRoomIfUnique(null);
	}

	@Test
	public void ChatServerDeleteRoomNullTest() {
		ChatServer server = new ChatServer();
		exception.expect(NullPointerException.class);
		exception.expectMessage("Room was null.");
		server.deleteRoomIfExists(null);
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
	public void ChatServerDeleteUserTest() {
		ChatServer server = new ChatServer();
		ChatUser user = mock(ChatUser.class);
		assertTrue(server.addUserIfUnique(user));
		assertTrue(server.listUsers().size() == 1);
		server.removeUser(user);
		assertTrue(server.listUsers().size() == 0);
	}
	
	@Test
	public void ChatServerDeleteUserNotExistsTest() {
		ChatServer server = new ChatServer();
		ChatUser user = mock(ChatUser.class);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("User doesn't exist.");
		server.removeUser(user);
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
		server.removeUser(null);
	}
	
	@Test
	public void ChatServerAddConnectionNullTest() {
		ChatServer server = new ChatServer();
		exception.expect(NullPointerException.class);
		exception.expectMessage("Connection was null.");
		server.addConnection(null);
	}
	
	@Test
	public void ChatServerRemoveConnectionNullTest() {
		ChatServer server = new ChatServer();
		exception.expect(NullPointerException.class);
		exception.expectMessage("Connection was null.");
		server.removeConnection(null);
	}
	
	@Test
	public void ChatServerDeliverMessageTest() {
		ChatServer server = new ChatServer();
		ChatRoom room = mock(ChatRoom.class);
		when(room.getName()).thenReturn("general");
		when(room.getType()).thenReturn(ChatRoomType.PUBLIC);
		server.addRoomIfUnique(room);
		TextMessage message = mock(TextMessage.class);
		when(message.getChatRoomName()).thenReturn("general");
		when(message.getMessage()).thenReturn("test message");
		assertTrue(server.deliverMessage(message));
	}
}
