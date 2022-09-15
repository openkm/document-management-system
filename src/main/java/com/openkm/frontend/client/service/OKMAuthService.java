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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTGrantedUser;
import com.openkm.frontend.client.bean.GWTUser;

import java.util.List;
import java.util.Map;

/**
 * @author jllort
 */
@RemoteServiceRelativePath("Auth")
public interface OKMAuthService extends RemoteService {
	void logout() throws OKMException;

	List<GWTGrantedUser> getGrantedUsers(String nodePath) throws OKMException;

	Map<String, Integer> getGrantedRoles(String nodePath) throws OKMException;

	String getRemoteUser();

	List<GWTGrantedUser> getUngrantedUsers(String nodePath) throws OKMException;

	List<String> getUngrantedRoles(String nodePath) throws OKMException;

	List<GWTGrantedUser> getFilteredUngrantedUsers(String nodePath, String filter) throws OKMException;

	List<String> getFilteredUngrantedRoles(String nodePath, String filter) throws OKMException;

	void grantUser(String path, String user, int permissions, boolean recursive) throws OKMException;

	void revokeUser(String path, String user, boolean recursive) throws OKMException;

	void revokeUser(String path, String user, int permissions, boolean recursive) throws OKMException;

	void grantRole(String path, String role, int permissions, boolean recursive) throws OKMException;

	void revokeRole(String path, String role, boolean recursive) throws OKMException;

	void revokeRole(String path, String role, int permissions, boolean recursive) throws OKMException;

	void keepAlive() throws OKMException;

	List<GWTUser> getAllUsers() throws OKMException;

	List<GWTUser> getUsers(List<String> users) throws OKMException;

	List<String> getAllRoles() throws OKMException;

	List<GWTUser> getFilteredAllUsers(String filter, List<String> selectedUsers) throws OKMException;

	List<String> getFilteredAllRoles(String filter, List<String> selectedRoles) throws OKMException;

	void changeSecurity(String path, Map<String, Integer> grantUsers, Map<String, Integer> revokeUsers,
	                           Map<String, Integer> grantRoles, Map<String, Integer> revokeRoles, boolean recursive) throws OKMException;
}
