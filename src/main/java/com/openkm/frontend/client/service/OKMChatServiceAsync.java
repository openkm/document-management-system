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

package com.openkm.frontend.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.openkm.frontend.client.bean.GWTUser;

import java.util.List;

/**
 * @author jllort
 *
 */
public interface OKMChatServiceAsync {
	public void login(AsyncCallback<?> callback);

	public void logout(AsyncCallback<?> callback);

	public void getLoggedUsers(AsyncCallback<List<GWTUser>> callback);

	public void createNewChatRoom(String user, AsyncCallback<String> callback);

	public void getPendingChatRoomUser(AsyncCallback<List<String>> callback);

	public void getPendingMessage(String room, AsyncCallback<List<String>> callback);

	public void addMessageToRoom(String room, String msg, AsyncCallback<?> callback);

	public void closeRoom(String room, AsyncCallback<?> callback);

	public void addUserToChatRoom(String room, String user, AsyncCallback<?> callback);

	public void usersInRoom(String room, AsyncCallback<String> callback);

	public void getUsersInRoom(String room, AsyncCallback<List<String>> callback);
}