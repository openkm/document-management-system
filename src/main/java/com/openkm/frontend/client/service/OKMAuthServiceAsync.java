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
import com.openkm.frontend.client.bean.GWTGrantedUser;
import com.openkm.frontend.client.bean.GWTUser;

import java.util.List;
import java.util.Map;

/**
 * @author jllort
 */
public interface OKMAuthServiceAsync {
	public void logout(AsyncCallback<?> callback);

	public void getGrantedUsers(String nodePath, AsyncCallback<List<GWTGrantedUser>> callback);

	public void getGrantedRoles(String nodePath, AsyncCallback<Map<String, Integer>> callback);

	public void getRemoteUser(AsyncCallback<String> callback);

	public void getUngrantedUsers(String nodePath, AsyncCallback<List<GWTGrantedUser>> callback);

	public void getUngrantedRoles(String nodePath, AsyncCallback<List<String>> callback);

	public void getFilteredUngrantedUsers(String nodePath, String filter, AsyncCallback<List<GWTGrantedUser>> callback);

	public void getFilteredUngrantedRoles(String nodePath, String filter, AsyncCallback<List<String>> callback);

	public void grantUser(String path, String user, int permissions, boolean recursive, AsyncCallback<?> callback);

	public void revokeUser(String path, String user, boolean recursive, AsyncCallback<?> callback);

	public void revokeUser(String path, String user, int permissions, boolean recursive, AsyncCallback<?> callback);

	public void grantRole(String path, String role, int permissions, boolean recursive, AsyncCallback<?> callback);

	public void revokeRole(String path, String role, boolean recursive, AsyncCallback<?> callback);

	public void revokeRole(String path, String role, int permissions, boolean recursive, AsyncCallback<?> callback);

	public void keepAlive(AsyncCallback<?> callback);

	public void getAllUsers(AsyncCallback<List<GWTUser>> callback);

	public void getAllRoles(AsyncCallback<List<String>> callback);

	public void getFilteredAllUsers(String filter, List<String> selectedUsers, AsyncCallback<List<GWTUser>> callback);

	public void getFilteredAllRoles(String filter, List<String> selectedRoles, AsyncCallback<List<String>> callback);

	public void changeSecurity(String path, Map<String, Integer> grantUsers, Map<String, Integer> revokeUsers,
	                           Map<String, Integer> grantRoles, Map<String, Integer> revokeRoles, boolean recursive,
	                           AsyncCallback<?> callback);
}