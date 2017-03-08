package com.phusu.chatservice;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;

public class ProtocolHandlerTest {

	@Test
	public void ProtocolHandlerSendLineTest() {
		StringWriter sw = new StringWriter();
		ProtocolHandler handler = new ProtocolHandler(null, new PrintWriter(sw));
		handler.sendLine("test line");
		assertTrue(sw.toString().compareTo("test line\r\n") == 0);
	}
	
	@Test
	public void ProtocolHandlerGetMessageTest() {
		String line = "COMMAND SETNAME foo\r\n";
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(line.getBytes("UTF-8"))));
			ProtocolHandler handler = new ProtocolHandler(input, null);
			ChatMessage message = handler.getMessage();
			assertTrue(message.getMessageType() == MessageType.COMMAND_SETNAME);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
