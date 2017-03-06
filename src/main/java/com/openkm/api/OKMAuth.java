/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.api;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.module.AuthModule;
import com.openkm.module.ModuleManager;
import com.openkm.principal.PrincipalAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class OKMAuth implements AuthModule {
	private static Logger log = LoggerFactory.getLogger(OKMAuth.class);
	private static OKMAuth instance = new OKMAuth();

	private OKMAuth() {
	}

	public static OKMAuth getInstance() {
		return instance;
	}

	@Override
	public void login() throws RepositoryException, DatabaseException {
		log.debug("login()");
		AuthModule am = ModuleManager.getAuthModule();
		am.login();
		log.debug("login: void");
	}

	@Override
	public String login(String user, String pass) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("login({}, {})", user, pass);
		AuthModule am = ModuleManager.getAuthModule();
		String token = am.login(user, pass);
		log.debug("login: {}", token);
		return token;
	}

	@Override
	public void logout(String token) throws RepositoryException, DatabaseException {
		log.debug("logout({})", token);
		AuthModule am = ModuleManager.getAuthModule();
		am.logout(token);
		log.debug("logout: void");
	}

	@Override
	public void grantUser(String token, String nodePath, String user, int permissions, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("grantUser({}, {}, {}, {})", new Object[]{token, nodePath, user, permissions});
		AuthModule am = ModuleManager.getAuthModule();
		am.grantUser(token, nodePath, user, permissions, recursive);
		log.debug("grantUser: void");
	}

	@Override
	public void revokeUser(String token, String nodePath, String user, int permissions, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("revokeUser({}, {}, {}, {})", new Object[]{token, nodePath, user, permissions});
		AuthModule am = ModuleManager.getAuthModule();
		am.revokeUser(token, nodePath, user, permissions, recursive);
		log.debug("revokeUser: void");
	}

	@Override
	public Map<String, Integer> getGrantedUsers(String token, String nodePath) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getGrantedUsers({}, {})", token, nodePath);
		AuthModule am = ModuleManager.getAuthModule();
		Map<String, Integer> grantedUsers = am.getGrantedUsers(token, nodePath);
		log.debug("getGrantedUsers: {}", grantedUsers);
		return grantedUsers;
	}

	@Override
	public void grantRole(String token, String nodePath, String role, int permissions, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("grantRole({}, {}, {}, {})", new Object[]{token, nodePath, role, permissions});
		AuthModule am = ModuleManager.getAuthModule();
		am.grantRole(token, nodePath, role, permissions, recursive);
		log.debug("grantRole: void");
	}

	@Override
	public void revokeRole(String token, String nodePath, String role, int permissions, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("revokeRole({}, {}, {}, {})", new Object[]{token, nodePath, role, permissions});
		AuthModule am = ModuleManager.getAuthModule();
		am.revokeRole(token, nodePath, role, permissions, recursive);
		log.debug("revokeRole: void");
	}

	@Override
	public Map<String, Integer> getGrantedRoles(String token, String nodePath) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getGrantedRoles({})", nodePath);
		AuthModule am = ModuleManager.getAuthModule();
		Map<String, Integer> grantedRoles = am.getGrantedRoles(token, nodePath);
		log.debug("getGrantedRoles: {}", grantedRoles);
		return grantedRoles;
	}

	@Override
	public List<String> getUsers(String token) throws PrincipalAdapterException {
		log.debug("getUsers({})", token);
		AuthModule am = ModuleManager.getAuthModule();
		List<String> users = am.getUsers(token);
		log.debug("getUsers: {}", users);
		return users;
	}

	@Override
	public List<String> getRoles(String token) throws PrincipalAdapterException {
		log.debug("getRoles({})", token);
		AuthModule am = ModuleManager.getAuthModule();
		List<String> roles = am.getRoles(token);
		log.debug("getRoles: {}", roles);
		return roles;
	}

	@Override
	public List<String> getUsersByRole(String token, String role) throws PrincipalAdapterException {
		log.debug("getUsersByRole({}, {})", token, role);
		AuthModule am = ModuleManager.getAuthModule();
		List<String> users = am.getUsersByRole(token, role);
		log.debug("getUsersByRole: {}", users);
		return users;
	}

	@Override
	public List<String> getRolesByUser(String token, String user) throws PrincipalAdapterException {
		log.debug("getRolesByUser({}, {})", token, user);
		AuthModule am = ModuleManager.getAuthModule();
		List<String> users = am.getRolesByUser(token, user);
		log.debug("getRolesByUser: {}", users);
		return users;
	}

	@Override
	public String getMail(String token, String user) throws PrincipalAdapterException {
		log.debug("getMail({}, {})", token, user);
		AuthModule am = ModuleManager.getAuthModule();
		String mail = am.getMail(token, user);
		log.debug("getMail: {}", mail);
		return mail;
	}

	@Override
	public String getName(String token, String user) throws PrincipalAdapterException {
		log.debug("getName({}, {})", token, user);
		AuthModule am = ModuleManager.getAuthModule();
		String name = am.getName(token, user);
		log.debug("getName: {}", name);
		return name;
	}

	@Override
	public void changeSecurity(String token, String nodePath, Map<String, Integer> grantUsers, Map<String, Integer> revokeUsers,
	                           Map<String, Integer> grantRoles, Map<String, Integer> revokeRoles, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("changeSecurity({}, {}, {}, {}, {}, {})", new Object[]{token, nodePath, grantUsers, revokeUsers, grantRoles,
				revokeRoles, recursive});
		AuthModule am = ModuleManager.getAuthModule();
		am.changeSecurity(token, nodePath, grantUsers, revokeUsers, grantRoles, revokeRoles, recursive);
		log.debug("changeSecurity: void");
	}

	/*
	 * ------------------------------------------------------------------
	 * These methods only works if using the OpenKM user database.
	 * ------------------------------------------------------------------
	 */
	@Override
	public void createUser(String token, String user, String password, String email, String name, boolean active)
			throws PrincipalAdapterException {
		log.debug("createUser({}, {}, {}, {}, {}, {})", new Object[]{token, user, password, active});
		AuthModule am = ModuleManager.getAuthModule();
		am.createUser(token, user, password, email, name, active);
		log.debug("createUser: void");
	}

	@Override
	public void deleteUser(String token, String user) throws PrincipalAdapterException {
		log.debug("deleteUser({}, {})", new Object[]{token, user});
		AuthModule am = ModuleManager.getAuthModule();
		am.deleteUser(token, user);
		log.debug("deleteUser: void");
	}

	@Override
	public void updateUser(String token, String user, String password, String email, String name, boolean active) throws PrincipalAdapterException {
		log.debug("updateUser({}, {}, {}, {}, {}, {})", new Object[]{token, user, password, email, name, active});
		AuthModule am = ModuleManager.getAuthModule();
		am.updateUser(token, user, password, email, name, active);
		log.debug("updateUser: void");
	}

	@Override
	public void createRole(String token, String role, boolean active) throws PrincipalAdapterException {
		log.debug("createRole({}, {}, {})", new Object[]{token, role, active});
		AuthModule am = ModuleManager.getAuthModule();
		am.createRole(token, role, active);
		log.debug("createRole: void");
	}

	@Override
	public void deleteRole(String token, String role) throws PrincipalAdapterException {
		log.debug("deleteRole({}, {})", new Object[]{token, role});
		AuthModule am = ModuleManager.getAuthModule();
		am.deleteRole(token, role);
		log.debug("deleteUser: void");
	}

	@Override
	public void updateRole(String token, String role, boolean active) throws PrincipalAdapterException {
		log.debug("updateRole({}, {}, {})", new Object[]{token, role, active});
		AuthModule am = ModuleManager.getAuthModule();
		am.updateRole(token, role, active);
		log.debug("updateRole: void");
	}

	@Override
	public void assignRole(String token, String user, String role) throws PrincipalAdapterException {
		log.debug("assignRole({}, {}, {})", new Object[]{token, user, role});
		AuthModule am = ModuleManager.getAuthModule();
		am.assignRole(token, user, role);
		log.debug("assignRole: void");
	}

	@Override
	public void removeRole(String token, String user, String role) throws PrincipalAdapterException {
		log.debug("removeRole({}, {}, {})", new Object[]{token, user, role});
		AuthModule am = ModuleManager.getAuthModule();
		am.removeRole(token, user, role);
		log.debug("removeRole: void");
	}
}
