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
		Thread serverThread = new Thread(server, "Server");
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
				runClient();
			}
			catch (Exception e) {
				
			}
		}

		private void runClient() throws IOException {
			Socket socket = new Socket("localhost", 9001);
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
			
			writeLine("COMMAND QUIT");
			socket.close();
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
