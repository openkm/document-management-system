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
	public void logout() throws OKMException;

	public List<GWTGrantedUser> getGrantedUsers(String nodePath) throws OKMException;

	public Map<String, Integer> getGrantedRoles(String nodePath) throws OKMException;

	public String getRemoteUser();

	public List<GWTGrantedUser> getUngrantedUsers(String nodePath) throws OKMException;

	public List<String> getUngrantedRoles(String nodePath) throws OKMException;

	public List<GWTGrantedUser> getFilteredUngrantedUsers(String nodePath, String filter) throws OKMException;

	public List<String> getFilteredUngrantedRoles(String nodePath, String filter) throws OKMException;

	public void grantUser(String path, String user, int permissions, boolean recursive) throws OKMException;

	public void revokeUser(String path, String user, boolean recursive) throws OKMException;

	public void revokeUser(String path, String user, int permissions, boolean recursive) throws OKMException;

	public void grantRole(String path, String role, int permissions, boolean recursive) throws OKMException;

	public void revokeRole(String path, String role, boolean recursive) throws OKMException;

	public void revokeRole(String path, String role, int permissions, boolean recursive) throws OKMException;

	public void keepAlive() throws OKMException;

	public List<GWTUser> getAllUsers() throws OKMException;

	public List<String> getAllRoles() throws OKMException;

	public List<GWTUser> getFilteredAllUsers(String filter, List<String> selectedUsers) throws OKMException;

	public List<String> getFilteredAllRoles(String filter, List<String> selectedRoles) throws OKMException;

	public void changeSecurity(String path, Map<String, Integer> grantUsers, Map<String, Integer> revokeUsers,
	                           Map<String, Integer> grantRoles, Map<String, Integer> revokeRoles, boolean recursive) throws OKMException;
}