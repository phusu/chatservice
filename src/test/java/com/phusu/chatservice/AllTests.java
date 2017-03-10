package com.phusu.chatservice;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ChatMessageParserTest.class, ChatRoomTest.class, ChatServerTest.class, SimpleChatUserTest.class })
public class AllTests {

}
