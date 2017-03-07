package com.phusu.chatservice;

import static org.easymock.EasyMock.createNiceMock;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ChatRoomTest {
	
	public static final String NAME = "general";
	public static final String TOPIC = "topic for this chat room";
	public static final String USER_NAME = "John Doe";
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void ChatRoomNullNameTest() {
		exception.expect(NullPointerException.class);
		exception.expectMessage("Constructor argument was null.");
		new ChatRoom(null, ChatRoomType.PUBLIC);
	}

	@Test
	public void ChatRoomNullTypeTest() {
		exception.expect(NullPointerException.class);
		exception.expectMessage("Constructor argument was null.");
		new ChatRoom(NAME, null);
	}
	
	@Test
	public void ChatRoomGetNameTypeTest() {
		ChatRoom room = new ChatRoom(NAME, ChatRoomType.PUBLIC);
		assertTrue(room.getName().equals(NAME));
		assertTrue(room.getType().equals(ChatRoomType.PUBLIC));
	}
	
	@Test
	public void ChatRoomSetNameTest() {
		ChatRoom room = new ChatRoom(NAME, ChatRoomType.PUBLIC);
		room.setName("random");
		assertTrue(room.getName().equals("random"));
	}
	
	@Test
	public void ChatRoomGetUsersTest() {
		ChatRoom room = new ChatRoom(NAME, ChatRoomType.PUBLIC);
		assertTrue(room.getUsers().size() == 0);
	}
	
	@Test
	public void ChatRoomAddUserTest() {
		ChatRoom room = new ChatRoom(NAME, ChatRoomType.PUBLIC);
		ChatUser user = createNiceMock(ChatUser.class);
		room.addUser(user);
		assertTrue(room.getUsers().size() == 1);
	}
	
	@Test
	public void ChatRoomAddUserNullTest() {
		ChatRoom room = new ChatRoom(NAME, ChatRoomType.PUBLIC);
		exception.expect(NullPointerException.class);
		exception.expectMessage("User was null.");
		room.addUser(null);
	}
	

	@Test
	public void ChatRoomAddUserDuplicateTest() {
		ChatRoom room = new ChatRoom(NAME, ChatRoomType.PUBLIC);
		ChatUser user = createNiceMock(ChatUser.class);
		room.addUser(user);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("User already exists.");
		room.addUser(user);
	}
	
	@Test
	public void ChatRoomDeleteUserTest() {
		ChatRoom room = new ChatRoom(NAME, ChatRoomType.PUBLIC);
		ChatUser user = createNiceMock(ChatUser.class);
		room.addUser(user);
		assertTrue(room.getUsers().size() == 1);
		room.deleteUser(user);
		assertTrue(room.getUsers().size() == 0);
	}

	@Test
	public void ChatRoomDeleteUserNullTest() {
		ChatRoom room = new ChatRoom(NAME, ChatRoomType.PUBLIC);
		exception.expect(NullPointerException.class);
		exception.expectMessage("User was null.");
		room.deleteUser(null);
	}

	@Test
	public void ChatRoomDeleteUserNotExistsTest() {
		ChatRoom room = new ChatRoom(NAME, ChatRoomType.PUBLIC);
		ChatUser user = createNiceMock(ChatUser.class);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("User doesn't exist.");
		room.deleteUser(user);
	}
	
	@Test
	public void ChatRoomSetGetTopicTest() {
		ChatRoom room = new ChatRoom(NAME, ChatRoomType.PUBLIC);
		room.setTopic(TOPIC);
		assertTrue(room.getTopic().equals(TOPIC));
	}
	
	@Test
	public void ChatRoomSetTopicNullTest() {
		ChatRoom room = new ChatRoom(NAME, ChatRoomType.PUBLIC);
		exception.expect(NullPointerException.class);
		exception.expectMessage("Topic was null.");
		room.setTopic(null);
		
	}
}
