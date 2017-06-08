package com.phusu.chatservice;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
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
		server.start();

		try {
			TestClient client = new TestClient("User", TestCase.USER_NAME_TESTS);
			
			Thread thread = new Thread(client, "Client");
			thread.start();
		
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();	
		} finally {
			try {
				server.stop();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
		server.start();
		
		try {
			TestClient client = new TestClient("User", TestCase.GROUP_HANDLING_TESTS);
			
			Thread thread = new Thread(client, "Client");
			thread.start();
		
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();	
		} finally {
			try {
				server.stop();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	@Test
	public void TestSingleClientMessagingTest() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		ChatServer server = new ChatServer();
		server.start();
		
		TestClient client = new TestClient("User", TestCase.MESSAGING_TESTS);
		
		Thread thread = new Thread(client, "Client");
		thread.start();
		
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestMultipleClientsMessagingTest() {
		ChatServer server = new ChatServer();
		try {
			Thread.sleep(1000);
			server.start();
			
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
	}
	*/
	
	enum TestCase {
		USER_NAME_TESTS,
		GROUP_HANDLING_TESTS,
		MESSAGING_TESTS
	}
	

	enum ClientState {
		TEST_USER_WAIT_SETNAME,
		TEST_USER_GARBAGE_SENT,
		TEST_USER_INVALID_CMD_SENT,
		TEST_USER_VALID_NAME_SENT,
		TEST_USER_NAME_ACCEPTED,
		TEST_USER_TEST_COMPLETED,
		TEST_GROUP_WAIT_SETNAME,
		TEST_GROUP_VALID_NAME_SENT,
		TEST_GROUP_LISTROOMS_SENT,
		TEST_GROUP_JOIN_NO_ARGUMENTS_SENT,
		TEST_GROUP_JOIN_GENERAL_SENT,
		TEST_GROUP_JOIN_RANDOM_SENT,
		TEST_GROUP_MESSAGE_GENERAL_SENT,
		TEST_GROUP_MESSAGE_RANDOM_SENT,
		TEST_GROUP_MESSAGE_INVALID_CHANNEL_SENT,
		TEST_GROUP_LEAVE_RANDOM_SENT,
		TEST_GROUP_LEAVE_GENERAL_SENT,
		TEST_GROUP_LEAVE_INVALID_CHANNEL_SENT,
		TEST_GROUP_TEST_COMPLETED
	}
	
	private class TestClient implements Runnable {
		private final Logger logger = LogManager.getLogger(TestClient.class);
		
		private String clientName;
		private TestCase testCase;
		private WebSocketClient cc;
		private ClientState state;
		
		public TestClient(String clientName, TestCase testCase) throws URISyntaxException {
			this.clientName = clientName;
			this.testCase = testCase;
			if (testCase == TestCase.USER_NAME_TESTS)
				this.state = ClientState.TEST_USER_WAIT_SETNAME;
			else if (testCase == TestCase.GROUP_HANDLING_TESTS)
				this.state = ClientState.TEST_GROUP_WAIT_SETNAME;
			
			this.cc = new WebSocketClient(new URI("http://localhost:9001"), new Draft_6455()) {
				@Override
				public void onMessage(String message) {
					logger.info("Message received: " + message + ", current state: " + state);
					
					switch (state) {
						case TEST_USER_WAIT_SETNAME: {
							assertTrue(message.equals("SETNAME"));
							cc.send("aervblekmrgj");
							state = ClientState.TEST_USER_GARBAGE_SENT;
							break;
						}
						case TEST_USER_GARBAGE_SENT: {
							assertTrue(message.equals("SETNAME"));
							cc.send("MESSAGE TO general Testing one two three");
							state = ClientState.TEST_USER_INVALID_CMD_SENT;
							break;
						}
						case TEST_USER_INVALID_CMD_SENT: {
							assertTrue(message.equals("SETNAME"));
							cc.send("COMMAND SETNAME " + clientName);
							state = ClientState.TEST_USER_VALID_NAME_SENT;
							break;
						}
						case TEST_USER_VALID_NAME_SENT: {
							assertTrue(message.equals("RESPONSE OK SETNAME " + clientName));
							cc.send("COMMAND SETNAME bar");
							state = ClientState.TEST_USER_NAME_ACCEPTED;
							break;
						}
						case TEST_USER_NAME_ACCEPTED: {
							assertTrue(message.equals("NOT IMPLEMENTED"));
							state = ClientState.TEST_USER_TEST_COMPLETED;
							cc.close();
							break;
						}
						case TEST_GROUP_WAIT_SETNAME: {
							assertTrue(message.equals("SETNAME"));
							cc.send("COMMAND SETNAME " + clientName);
							state = ClientState.TEST_GROUP_VALID_NAME_SENT;
							break;
						}
						case TEST_GROUP_VALID_NAME_SENT: {
							assertTrue(message.equals("RESPONSE OK SETNAME " + clientName));
							cc.send("COMMAND LISTROOMS");
							state = ClientState.TEST_GROUP_LISTROOMS_SENT;
							break;
						}
						case TEST_GROUP_LISTROOMS_SENT: {
							assertTrue(message.equals("RESPONSE LISTROOMS"));
							cc.send("COMMAND JOIN");
							state = ClientState.TEST_GROUP_JOIN_NO_ARGUMENTS_SENT;
							break;
						}
						case TEST_GROUP_JOIN_NO_ARGUMENTS_SENT: {
							assertTrue(message.equals("RESPONSE NOT VALID JOIN Missing arguments."));
							cc.send("COMMAND JOIN general");
							state = ClientState.TEST_GROUP_JOIN_GENERAL_SENT;
							break;
						}
						case TEST_GROUP_JOIN_GENERAL_SENT: {
							assertTrue(message.equals("MESSAGE FROM " + clientName 
									+ " TO general " + clientName + " joined!"));
							cc.send("COMMAND JOIN random");
							state = ClientState.TEST_GROUP_JOIN_RANDOM_SENT;
							break;
						}
						case TEST_GROUP_JOIN_RANDOM_SENT: {
							assertTrue(message.equals("MESSAGE FROM " + clientName 
									+ " TO random " + clientName + " joined!"));
							cc.send("MESSAGE TO general test message to test channel");
							state = ClientState.TEST_GROUP_MESSAGE_GENERAL_SENT;
							break;
						}
						case TEST_GROUP_MESSAGE_GENERAL_SENT: {
							cc.send("MESSAGE TO random test message to random channel");
							state = ClientState.TEST_GROUP_MESSAGE_RANDOM_SENT;
							break;
						}
						case TEST_GROUP_MESSAGE_RANDOM_SENT: {
							cc.send("MESSAGE TO generalchat test message to invalid channel");
							state = ClientState.TEST_GROUP_MESSAGE_INVALID_CHANNEL_SENT;
							break;
						}
						case TEST_GROUP_MESSAGE_INVALID_CHANNEL_SENT: {
							assertTrue(message.equals("MESSAGE NOT VALID Room generalchat not found"));
							cc.send("COMMAND LEAVE random");
							state = ClientState.TEST_GROUP_LEAVE_RANDOM_SENT;
							break;
						}
						case TEST_GROUP_LEAVE_RANDOM_SENT: {
							assertTrue(message.equals("RESPONSE OK LEAVE random"));
							cc.send("COMMAND LEAVE generalchat");
							state = ClientState.TEST_GROUP_LEAVE_INVALID_CHANNEL_SENT;
							break;
						}
						case TEST_GROUP_LEAVE_INVALID_CHANNEL_SENT: {
							assertTrue(message.equals("RESPONSE NOT VALID LEAVE generalchat"));
							cc.send("COMMAND LEAVE general");
							state = ClientState.TEST_GROUP_LEAVE_GENERAL_SENT;
							break;
						}
						case TEST_GROUP_LEAVE_GENERAL_SENT: {
							assertTrue(message.equals("RESPONSE OK LEAVE general"));
							state = ClientState.TEST_GROUP_TEST_COMPLETED;
							cc.close();
							break;
						}
					default:
						break;
					}
				}
				
				@Override
				public void onClose(int code, String reason, boolean remote) {
					logger.info("onClose called");
				}
				
				@Override
				public void onError(Exception ex) {
					logger.catching(ex);
				}
				
				@Override
				public void onOpen(ServerHandshake handshake) {
					logger.info("Connection opened");
				}
			};
			
		}

		@Override
		public void run() {
				if (testCase == TestCase.USER_NAME_TESTS) {
					testSettingUserName(clientName);
				}
				else if (testCase == TestCase.GROUP_HANDLING_TESTS) {
					testCreatingGroupsAndMessaging(clientName);
				}
				/*
				else if (testCase == TestCase.MESSAGING_TESTS) {
					testMessagingToMultipleGroups(clientName);
				}
				*/
		}

		private void testSettingUserName(String clientName) {
			cc.connect();
			logger.debug("Socket created");
			
			while (state != ClientState.TEST_USER_TEST_COMPLETED) {
				try {
					logger.debug("State: " + state);
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			logger.debug("Test ready.");
		}
		
		private void testCreatingGroupsAndMessaging(String userName) {
			cc.connect();
			logger.debug("Socket created");
			
			while (state != ClientState.TEST_GROUP_TEST_COMPLETED) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			logger.debug("Test ready.");
		}
		
		/*
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
		*/
	}
}
