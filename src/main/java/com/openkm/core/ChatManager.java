/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017  Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.core;

import com.openkm.api.OKMAuth;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.util.FormatUtil;

import java.util.*;

public class ChatManager {
	private static final int ACTION_LOGIN = 0;
	private static final int ACTION_LOGOUT = 1;
	private static final int ACTION_ADD_ROOM_TO_USER = 2;
	private static final int ACTION_REMOVE_USER_ROOM = 3;
	private static final int ACTION_ADD_PENDING_ROOM_TO_USER = 4;
	private static final int ACTION_GET_PENDING_USER_FROM_ROOM = 5;
	private static final int ACTION_GET_PENDING_USER_ROOM_MESSAGE = 6;
	private static final int ACTION_ADD_USER_MESSAGE_TO_ROOM = 7;
	private static final int ACTION_CREATE_MESSAGE_ROOM = 8;
	private static final int ACTION_CREATE_MESSAGE_USER_ROOM = 9;
	private static final int ACTION_REMOVE_USER_MESSAGE_ROOM = 10;
	private static final int ACTION_DELETE_EMPTY_MESSAGE_ROOM = 11;
	private static final int ACTION_GET_USERS_IN_MESSAGE_ROOM = 12;

	private static List<String> loggedUsers = new ArrayList<String>();

	// user is the key
	private static Map<String, List<String>> usersRooms = new HashMap<String, List<String>>();

	// user is the key
	private static Map<String, List<String>> pendingUsersRooms = new HashMap<String, List<String>>();

	// room is the key, user is the subkey, messages are copied to each user
	private static Map<String, HashMap<String, List<String>>> msgUsersRooms = new HashMap<String, HashMap<String, List<String>>>();

	public void login(String user) throws OKMException {
		usersLoggedAction(user, ACTION_LOGIN);
	}

	public void logout(String user) throws OKMException {
		usersLoggedAction(user, ACTION_LOGOUT);
	}

	public List<String> getLoggedUsers() {
		return loggedUsers;
	}

	public String createNewChatRoom(String fromUser, String toUser) throws PrincipalAdapterException {
		String room = UUID.randomUUID().toString();

		// Add users to rooms
		usersRoomAction(room, toUser, ACTION_ADD_ROOM_TO_USER);
		pendingRoomAction(room, toUser, ACTION_ADD_PENDING_ROOM_TO_USER);
		usersRoomAction(room, fromUser, ACTION_ADD_ROOM_TO_USER);
		messageUserRoomAction(room, "", "", ACTION_CREATE_MESSAGE_ROOM);
		messageUserRoomAction(room, toUser, "", ACTION_CREATE_MESSAGE_USER_ROOM);
		messageUserRoomAction(room, fromUser, "", ACTION_CREATE_MESSAGE_USER_ROOM);
		return room;
	}

	public List<String> getPendingMessage(String user, String room) throws PrincipalAdapterException {
		return messageUserRoomAction(room, user, "", ACTION_GET_PENDING_USER_ROOM_MESSAGE);
	}

	public List<String> getPendingChatRoomUser(String user) {
		return pendingRoomAction("", user, ACTION_GET_PENDING_USER_FROM_ROOM);
	}

	public void addMessageToRoom(String user, String room, String msg) throws PrincipalAdapterException {
		messageUserRoomAction(room, user, msg, ACTION_ADD_USER_MESSAGE_TO_ROOM);
	}

	public void closeRoom(String user, String room) throws PrincipalAdapterException {
		usersRoomAction(room, user, ACTION_REMOVE_USER_ROOM);
		messageUserRoomAction(room, user, "", ACTION_REMOVE_USER_MESSAGE_ROOM);

		// Evaluate if message room should be deleted
		messageUserRoomAction(room, "", "", ACTION_DELETE_EMPTY_MESSAGE_ROOM);
	}

	public void addUserToChatRoom(String user, String room) throws PrincipalAdapterException {
		if (!messageUserRoomAction(room, "", "", ACTION_GET_USERS_IN_MESSAGE_ROOM).contains(user)) {
			usersRoomAction(room, user, ACTION_ADD_ROOM_TO_USER);
			pendingRoomAction(room, user, ACTION_ADD_PENDING_ROOM_TO_USER);
			messageUserRoomAction(room, user, "", ACTION_CREATE_MESSAGE_USER_ROOM);
		}
	}

	public int getNumberOfUsersInRoom(String room) throws PrincipalAdapterException {
		return messageUserRoomAction(room, "", "", ACTION_GET_USERS_IN_MESSAGE_ROOM).size();
	}

	public List<String> getUsersInRoom(String room) throws PrincipalAdapterException {
		return messageUserRoomAction(room, "", "", ACTION_GET_USERS_IN_MESSAGE_ROOM);
	}

	/**
	 * Synchronized users logged actions
	 * @throws OKMException
	 */
	private synchronized void usersLoggedAction(String user, int action) throws OKMException {
		switch (action) {
			case ACTION_LOGIN:
				if (!loggedUsers.contains(user)) {
					loggedUsers.add(user);
				} else {
					throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMChatService, ErrorCode.CAUSE_UserYetLogged), "User yet logged");
				}

				Collections.sort(loggedUsers);
				break;

			case ACTION_LOGOUT:
				if (loggedUsers.contains(user)) {
					loggedUsers.remove(user);
				}

				if (pendingUsersRooms.containsKey(user)) {
					pendingUsersRooms.remove(user);
				}

				if (usersRooms.containsKey(user)) {
					List<String> rooms = usersRooms.get(user);

					for (Iterator<String> it = rooms.iterator(); it.hasNext(); ) {
						String room = it.next();

						if (msgUsersRooms.containsKey(room)) {
							Map<String, List<String>> roomMessages = msgUsersRooms.get(room);
							if (roomMessages.containsKey(user)) {
								roomMessages.remove(user);
							}
						}
					}
				}

				break;
		}
	}

	/**
	 * Synchronized users room actions
	 */
	private synchronized void usersRoomAction(String room, String user, int action) {
		switch (action) {
			case ACTION_ADD_ROOM_TO_USER:
				if (!usersRooms.keySet().contains(user)) {
					List<String> userRoomList = new ArrayList<String>();
					userRoomList.add(room);
					usersRooms.put(user, userRoomList);
				} else {
					List<String> userRoomList = usersRooms.get(user);
					if (!userRoomList.contains(room)) {
						userRoomList.add(room);
					}
				}

				break;

			case ACTION_REMOVE_USER_ROOM:
				if (usersRooms.keySet().contains(user)) {
					List<String> userRoomList = usersRooms.get(user);

					if (userRoomList.contains(room)) {
						userRoomList.remove(room);
					}
				}

				break;
		}
	}

	/**
	 * Synchronized pending room actions
	 */
	private synchronized List<String> pendingRoomAction(String room, String user, int action) {
		switch (action) {
			case ACTION_ADD_PENDING_ROOM_TO_USER:
				if (!pendingUsersRooms.keySet().contains(user)) {
					List<String> userPendingRoomList = new ArrayList<String>();
					userPendingRoomList.add(room);
					pendingUsersRooms.put(user, userPendingRoomList);
				} else {
					List<String> userPendingRoomList = pendingUsersRooms.get(user);

					if (!userPendingRoomList.contains(room)) {
						userPendingRoomList.add(room);
					}
				}

				return new ArrayList<String>();

			case ACTION_GET_PENDING_USER_FROM_ROOM:
				if (pendingUsersRooms.keySet().contains(user)) {
					List<String> userRooms = pendingUsersRooms.get(user);
					pendingUsersRooms.remove(user);
					return userRooms;
				} else {
					return new ArrayList<String>();
				}

			default:
				return new ArrayList<String>();
		}
	}

	/**
	 * Synchronized message user room actions
	 */
	private synchronized List<String> messageUserRoomAction(String room, String user, String msg, int action) throws PrincipalAdapterException {
		switch (action) {
			case ACTION_GET_PENDING_USER_ROOM_MESSAGE:
				if (msgUsersRooms.containsKey(room) && msgUsersRooms.get(room).containsKey(user)) {
					List<String> messages = msgUsersRooms.get(room).get(user);
					msgUsersRooms.get(room).put(user, new ArrayList<String>());

					return messages;
				} else {
					return new ArrayList<String>();
				}

			case ACTION_ADD_USER_MESSAGE_TO_ROOM:
				String username = OKMAuth.getInstance().getName(null, user);
				//avoid cross scripting 
				if (msg.endsWith("</br>")) {
					msg = msg.substring(0, msg.lastIndexOf("</br>"));
				}
				msg = FormatUtil.cleanXSS(msg);
				msg = msg + "</br>";
				String message = "<b>" + username + "</b>: " + msg;

				if (msgUsersRooms.containsKey(room)) {
					Map<String, List<String>> roomMap = msgUsersRooms.get(room);

					for (Iterator<String> it = roomMap.keySet().iterator(); it.hasNext(); ) {
						String roomUser = it.next();

						// Pending message is not added to himself ( that's done by UI )
						if (!roomUser.equals(user)) {
							// Add message for each user available
							roomMap.get(roomUser).add(message);
						}
					}
				}

				return new ArrayList<String>();

			case ACTION_CREATE_MESSAGE_ROOM:
				if (!msgUsersRooms.containsKey(room)) {
					msgUsersRooms.put(room, new HashMap<String, List<String>>());
				}

				return new ArrayList<String>();

			case ACTION_CREATE_MESSAGE_USER_ROOM:
				if (msgUsersRooms.containsKey(room)) {
					if (!msgUsersRooms.get(room).containsKey(user)) {
						msgUsersRooms.get(room).put(user, new ArrayList<String>());
					}
				}

				return new ArrayList<String>();

			case ACTION_REMOVE_USER_MESSAGE_ROOM:
				if (msgUsersRooms.containsKey(room)) {
					if (msgUsersRooms.get(room).containsKey(user)) {
						msgUsersRooms.get(room).remove(user);
					}
				}

				return new ArrayList<String>();

			case ACTION_DELETE_EMPTY_MESSAGE_ROOM:
				// Room message without users must be deleted
				if (msgUsersRooms.containsKey(room)) {
					if (msgUsersRooms.get(room).keySet().size() == 0) {
						msgUsersRooms.remove(room);
					}
				}

				return new ArrayList<String>();

			case ACTION_GET_USERS_IN_MESSAGE_ROOM:
				if (msgUsersRooms.containsKey(room)) {
					Collection<String> userList = msgUsersRooms.get(room).keySet();
					return new ArrayList<String>(userList);
				} else {
					return new ArrayList<String>();
				}

			default:
				return new ArrayList<String>();
		}
	}
}
