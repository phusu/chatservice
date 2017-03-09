package com.phusu.chatservice;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;

import com.phusu.chatservice.messages.ChatMessage;
import com.phusu.chatservice.messages.SetNameMessage;

public class ProtocolHandlerTest {

	@Test
	public void ProtocolHandlerSendLineTest() {
		StringWriter sw = new StringWriter();
		IOHandler handler = new IOHandler(null, new PrintWriter(sw));
		handler.sendLine("test line");
		assertTrue(sw.toString().compareTo("test line\r\n") == 0);
	}
	
	@Test
	public void ProtocolHandlerGetMessageTest() {
		String line = "COMMAND SETNAME foo\r\n";
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(line.getBytes("UTF-8"))));
			IOHandler handler = new IOHandler(input, null);
			ChatMessage message = handler.getMessage();
			assertTrue(message instanceof SetNameMessage);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
