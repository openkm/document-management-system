/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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
import com.openkm.frontend.client.bean.GWTGrantedUser;
import com.openkm.frontend.client.bean.GWTUser;

import java.util.List;
import java.util.Map;

/**
 * @author jllort
 */
public interface OKMAuthServiceAsync {
	void logout(AsyncCallback<?> callback);

	void getGrantedUsers(String nodePath, AsyncCallback<List<GWTGrantedUser>> callback);

	void getGrantedRoles(String nodePath, AsyncCallback<Map<String, Integer>> callback);

	void getRemoteUser(AsyncCallback<String> callback);

	void getUngrantedUsers(String nodePath, AsyncCallback<List<GWTGrantedUser>> callback);

	void getUngrantedRoles(String nodePath, AsyncCallback<List<String>> callback);

	void getFilteredUngrantedUsers(String nodePath, String filter, AsyncCallback<List<GWTGrantedUser>> callback);

	void getFilteredUngrantedRoles(String nodePath, String filter, AsyncCallback<List<String>> callback);

	void grantUser(String path, String user, int permissions, boolean recursive, AsyncCallback<?> callback);

	void revokeUser(String path, String user, boolean recursive, AsyncCallback<?> callback);

	void revokeUser(String path, String user, int permissions, boolean recursive, AsyncCallback<?> callback);

	void grantRole(String path, String role, int permissions, boolean recursive, AsyncCallback<?> callback);

	void revokeRole(String path, String role, boolean recursive, AsyncCallback<?> callback);

	void revokeRole(String path, String role, int permissions, boolean recursive, AsyncCallback<?> callback);

	void keepAlive(AsyncCallback<?> callback);

	void getAllUsers(AsyncCallback<List<GWTUser>> callback);

	void getUsers(List<String> users, AsyncCallback<List<GWTUser>> callback);

	void getAllRoles(AsyncCallback<List<String>> callback);

	void getFilteredAllUsers(String filter, List<String> selectedUsers, AsyncCallback<List<GWTUser>> callback);

	void getFilteredAllRoles(String filter, List<String> selectedRoles, AsyncCallback<List<String>> callback);

	void changeSecurity(String path, Map<String, Integer> grantUsers, Map<String, Integer> revokeUsers,
						Map<String, Integer> grantRoles, Map<String, Integer> revokeRoles, boolean recursive,
						AsyncCallback<?> callback);
}
