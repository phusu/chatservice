package com.phusu.chatservice;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SimpleChatUserTest {
	
	private final static String USER_NAME_FOO = "foo";
	private final static String USER_NAME_BAR = "bar";
	private final static String USER_NAME_EMPTY = "";

	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void SimpleChatUserConstructorTest() {
		ClientConnection clientConnection = mock(ClientConnection.class);
		SimpleChatUser user = new SimpleChatUser(clientConnection, USER_NAME_FOO);
		assertTrue(clientConnection == user.getClientConnection());
		assertTrue(user.getName().compareTo(USER_NAME_FOO) == 0);
	}
	
	@Test
	public void SimpleChatUserEmptyNameTest() {
		ClientConnection clientConnection = mock(ClientConnection.class);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("User name cannot be empty.");
		new SimpleChatUser(clientConnection, USER_NAME_EMPTY);
	}

	@Test
	public void SimpleChatUserNullNameTest() {
		ClientConnection clientConnection = mock(ClientConnection.class);
		exception.expect(NullPointerException.class);
		exception.expectMessage("User name was null.");
		new SimpleChatUser(clientConnection, null);
	}
	
	@Test
	public void SimpleChatUserEqualsAndHashcodeTest() {
		ClientConnection clientConnection = mock(ClientConnection.class);
		SimpleChatUser user1 = new SimpleChatUser(clientConnection, USER_NAME_FOO);
		SimpleChatUser user2 = new SimpleChatUser(clientConnection, USER_NAME_FOO);
		SimpleChatUser user3 = new SimpleChatUser(clientConnection, USER_NAME_BAR);
		assertTrue(user1.equals(user2));
		assertFalse(user2.equals(user3));
		assertFalse(user3.equals(clientConnection));
		assertTrue(user1.hashCode() == user2.hashCode());
		assertFalse(user2.hashCode() == user3.hashCode());
	}
}
