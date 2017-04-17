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
	public void TestSingleClientUserNameTest() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		ChatServer server = new ChatServer();
		Thread serverThread = new Thread(server);
		serverThread.start();
		
		TestClient client = new TestClient("User", TestCase.USER_NAME_TESTS);
		
		Thread thread = new Thread(client, "Client");
		thread.start();
		
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			server.stop();
		}
	}
	
	@Test
	public void TestSingleClientGroupHandlingTest() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		ChatServer server = new ChatServer();
		Thread serverThread = new Thread(server);
		serverThread.start();
		
		TestClient client = new TestClient("User", TestCase.GROUP_HANDLING_TESTS);
		
		Thread thread = new Thread(client, "Client");
		thread.start();
		
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			server.stop();	
		}
	}
	
	@Test
	public void TestSingleClientMessagingTest() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		ChatServer server = new ChatServer();
		Thread serverThread = new Thread(server);
		serverThread.start();
		
		TestClient client = new TestClient("User", TestCase.MESSAGING_TESTS);
		
		Thread thread = new Thread(client, "Client");
		thread.start();
		
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			server.stop();	
		}
	}
	
	@Test
	public void TestMultipleClientsMessagingTest() {
		ChatServer server = new ChatServer();
		try {
			Thread.sleep(1000);
			Thread serverThread = new Thread(server, "Server");
			serverThread.start();
			
			TestClient client1 = new TestClient("User1", TestCase.MESSAGING_TESTS);
			TestClient client2 = new TestClient("User2", TestCase.MESSAGING_TESTS);
			TestClient client3 = new TestClient("User3", TestCase.MESSAGING_TESTS);
			
			Thread thread1 = new Thread(client1, "Client1");
			Thread thread2 = new Thread(client2, "Client2");
			Thread thread3 = new Thread(client3, "Client3");
			
			thread1.start();
			Thread.sleep((int) Math.random() * 200);
			thread2.start();
			Thread.sleep((int) Math.random() * 200);
			thread3.start();
		
			thread1.join();
			thread2.join();
			thread3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			server.stop();	
		}
	}
	
	enum TestCase {
		USER_NAME_TESTS,
		GROUP_HANDLING_TESTS,
		MESSAGING_TESTS
	}
	
	private class TestClient implements Runnable {
		private BufferedReader in;
		private PrintWriter out;
		
		private final Logger logger = LogManager.getLogger(TestClient.class);
		
		private String clientName;
		private TestCase testCase;
		
		public TestClient(String clientName, TestCase testCase) {
			this.clientName = clientName;
			this.testCase = testCase;
		}

		@Override
		public void run() {
			try {
				if (testCase == TestCase.USER_NAME_TESTS) {
					testSettingUserName(clientName);
				}
				else if (testCase == TestCase.GROUP_HANDLING_TESTS) {
					testCreatingGroupsAndMessaging(clientName);
				}
				else if (testCase == TestCase.MESSAGING_TESTS) {
					testMessagingToMultipleGroups(clientName);
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private void testSettingUserName(String clientName) {
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
				writeLine("COMMAND SETNAME " + clientName);
				
				line = readLine();
				assertTrue(line.equals("RESPONSE OK SETNAME " + clientName));
				
				writeLine("COMMAND SETNAME bar");
				line = readLine();
				assertTrue(line.equals("NOT IMPLEMENTED"));
				
				writeLine("COMMAND QUIT");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void testCreatingGroupsAndMessaging(String userName) {
			try (Socket socket = new Socket("localhost", 9001)) {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				
				logger.debug("Socket created");
				
				String line = readLine();
				assertTrue(line.equals("SETNAME"));
				writeLine("COMMAND SETNAME " + userName);
				
				line = readLine();
				assertTrue(line.equals("RESPONSE OK SETNAME " + userName));
				
				writeLine("COMMAND LISTROOMS");
				line = readLine();
				assertTrue(line.equals("RESPONSE LISTROOMS"));
				
				writeLine("COMMAND JOIN");
				line = readLine();
				assertTrue(line.equals("RESPONSE NOT VALID JOIN Missing arguments."));
				
				writeLine("COMMAND JOIN general");
				line = readLine();
				assertTrue(line.equals("MESSAGE FROM " + userName + " TO general " + userName + " joined!"));
				
				writeLine("COMMAND JOIN random");
				line = readLine();
				assertTrue(line.equals("MESSAGE FROM " + userName + " TO random " + userName + " joined!"));
				
				messageTo("general", "test message to test channel", userName);
				
				messageTo("random", "test message to random channel", userName);

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
		
		private void testMessagingToMultipleGroups(String userName) throws InterruptedException {
			try (Socket socket = new Socket("localhost", 9001)) {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				
				logger.debug("Socket created");
				
				readLine();
				writeLine("COMMAND SETNAME " + userName);
				
				readLine();
				
				writeLine("COMMAND LISTROOMS");
				readLine();
				
				writeLine("COMMAND JOIN general");
				readLine();
				
				writeLine("COMMAND JOIN random");
				readLine();
				
				for (int i = 0; i < 100; ++i) {
					if (i % 2 == 0) {
						messageTo("general", "test message " + i, userName);
					}
					else {
						messageTo("random", "test message " + i, userName);
					}
					Thread.sleep((int) (Math.random()*500));
				}
				
				writeLine("COMMAND LEAVE random");
				readLine();

				writeLine("COMMAND LEAVE generalchat");
				readLine();

				writeLine("COMMAND LEAVE general");
				readLine();

				writeLine("COMMAND LISTROOMS");
				readLine();
				
				writeLine("COMMAND QUIT");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void messageTo(String destination, String message, String userName) throws IOException {
			writeLine("MESSAGE TO " + destination + " " + message);
			readLine();
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
