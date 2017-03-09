package com.phusu.chatservice;

public enum ChatServerAction {
	HANDLED_NO_RESPONSE,
	NOT_MY_MESSAGE,
	SUBMITNAME,
	SUBMITNAME_OK,
	CLOSE_CONNECTION,
	ROOM_DOESNT_EXIST,
	LIST_ROOMS,
	UNKNOWN_COMMAND
}
