# Chat service

Simple multi-threaded chat server written in Java. Supports multiple users (well of course) and multiple chat rooms. 

Motivation for the project was to try to follow Clean Code principles after I read the [book](https://www.amazon.co.uk/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882/ref=sr_1_1?s=books&ie=UTF8&qid=1492443990&sr=1-1&keywords=clean+code). In addition, I got interested about the test driven development methodology. I tried to write good unit tests with good test coverage. In the process, I learned the basics of unit testing, mocking, JUnit, Mockito and Maven. 

Client implementation is not done, it will follow up later. That might also incur some changes and fixes to server code base.

Updated on June 2017: Implementation modified to use WebSockets. This makes client implementation a lot easier and client can be written with HTML & Javascript.

## Implementation details
Chat server listens incoming WebSocket connections on port 9001. For each new connection (user), a separate connection thread is spawned which handles communication with remote user. Commands are just plain text.

## Supported commands / API
Documented in more detail in ChatServer.java.
- COMMAND SETNAME name
- COMMAND LISTROOMS
- COMMAND JOIN roomname
- COMMAND LEAVE roomname
- MESSAGE TO roomname message

## Goals of the project
- Refresh Java skills
- Try to follow the Clean Code principles of Mr. Martin
- Learn TDD and write good unit tests
- Learn WebSockets

## Things to do
- Client!
- Add more functionality, such as private channels, user-to-user direct messages, Facebook login
- Improve integration tests