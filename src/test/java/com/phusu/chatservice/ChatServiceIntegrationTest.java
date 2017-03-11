package com.phusu.chatservice;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class ChatServiceIntegrationTest {

	@Test
	public void test() {
		ChatServer server = new ChatServer();
		Thread serverThread = new Thread(server);
		serverThread.start();
		
		TestClient client1 = new TestClient();
		Thread thread1 = new Thread(client1, "Client 1");
		thread1.start();
		try {
			thread1.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			server.stop();	
		}
	}
	
	private class TestClient implements Runnable {
		private BufferedReader in;
		private PrintWriter out;
		
		private final Logger logger = LogManager.getLogger(TestClient.class);

		@Override
		public void run() {
			try {
				testSettingUserName();
				Thread.sleep(100);
				testCreatingGroupsAndMessaging();
			}
			catch (InterruptedException e) {
				
			}
		}

		private void testSettingUserName() {
			try (Socket socket = new Socket("localhost", 9001)) {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				
				logger.debug("Socket created");
				
				String line = readLine();
				assertTrue(line.equals("SETNAME"));
				writeLine("aervblekmrgj");
				
				line = readLine();
				assertTrue(line.equals("SETNAME"));
				writeLine("MESSAGE TO general Testing one two three");
	
				line = readLine();
				assertTrue(line.equals("SETNAME"));
				writeLine("COMMAND SETNAME foo");
				
				line = readLine();
				assertTrue(line.equals("RESPONSE OK SETNAME foo"));
				
				writeLine("COMMAND SETNAME bar");
				line = readLine();
				assertTrue(line.equals("NOT IMPLEMENTED"));
				
				writeLine("COMMAND QUIT");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void testCreatingGroupsAndMessaging() {
			try (Socket socket = new Socket("localhost", 9001)) {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				
				logger.debug("Socket created");
				
				String line = readLine();
				assertTrue(line.equals("SETNAME"));
				writeLine("COMMAND SETNAME foo");
				
				line = readLine();
				assertTrue(line.equals("RESPONSE OK SETNAME foo"));
				
				writeLine("COMMAND LISTROOMS");
				line = readLine();
				assertTrue(line.equals("RESPONSE LISTROOMS"));
				
				writeLine("COMMAND JOIN");
				line = readLine();
				assertTrue(line.equals("RESPONSE NOT VALID JOIN Missing arguments."));
				
				writeLine("COMMAND JOIN general");
				line = readLine();
				assertTrue(line.equals("MESSAGE FROM foo TO general foo joined!"));
				
				writeLine("COMMAND JOIN random");
				line = readLine();
				assertTrue(line.equals("MESSAGE FROM foo TO random foo joined!"));
				
				writeLine("MESSAGE TO general test message to general channel");
				line = readLine();
				assertTrue(line.equals("MESSAGE FROM foo TO general test message to general channel"));
				
				writeLine("MESSAGE TO random test message to random channel");
				line = readLine();
				assertTrue(line.equals("MESSAGE FROM foo TO random test message to random channel"));

				writeLine("MESSAGE TO generalchat test message to generalchat channel");
				line = readLine();
				assertTrue(line.equals("MESSAGE NOT VALID Room generalchat not found"));
				
				writeLine("COMMAND LEAVE random");
				line = readLine();
				assertTrue(line.equals("RESPONSE OK LEAVE random"));

				writeLine("COMMAND LEAVE generalchat");
				line = readLine();
				assertTrue(line.equals("RESPONSE NOT VALID LEAVE generalchat"));

				writeLine("COMMAND LEAVE general");
				line = readLine();
				assertTrue(line.equals("RESPONSE OK LEAVE general"));

				writeLine("COMMAND LISTROOMS");
				line = readLine();
				assertTrue(line.equals("RESPONSE LISTROOMS"));
				
				writeLine("COMMAND QUIT");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private String readLine() throws IOException {
			String line = in.readLine();
			logger.debug("--> " + line);
			return line;
		}
		
		private void writeLine(String line) {
			logger.debug("<-- " + line);
			out.println(line);
		}
	}
}
