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

package com.openkm.module.db;

import com.google.gson.Gson;
import com.openkm.automation.AutomationManager;
import com.openkm.automation.AutomationUtils;
import com.openkm.bean.ChangeSecurityParams;
import com.openkm.bean.Permission;
import com.openkm.bean.Repository;
import com.openkm.core.*;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.NodeFolderDAO;
import com.openkm.dao.PendingTaskDAO;
import com.openkm.dao.bean.AutomationRule;
import com.openkm.dao.bean.NodeFolder;
import com.openkm.dao.bean.PendingTask;
import com.openkm.module.AuthModule;
import com.openkm.module.common.CommonAuthModule;
import com.openkm.module.db.stuff.DbSessionManager;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.GenericHolder;
import com.openkm.util.PathUtils;
import com.openkm.util.StackTraceUtils;
import com.openkm.util.UserActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DbAuthModule implements AuthModule, ApplicationContextAware {
	private static Logger log = LoggerFactory.getLogger(DbAuthModule.class);
	private static ApplicationContext appCtx;

	@Override
	public void setApplicationContext(ApplicationContext appCtx) throws BeansException {
		DbAuthModule.appCtx = appCtx;
	}

	@Override
	public void login() throws RepositoryException, DatabaseException {
		log.debug("login()");

		try {
			Authentication auth = PrincipalUtils.getAuthentication();

			if (auth != null) {
				String user = auth.getName();
				loadUserData(user);

				// Activity log
				// @see com.openkm.spring.LoggerListener
			} else {
				throw new RepositoryException("User not authenticated");
			}
		} catch (DatabaseException e) {
			throw e;
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
		} catch (ItemExistsException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
		}

		log.debug("grantRole: void");
	}

	@Override
	public String login(String user, String password) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("login({}, {})", user, password);
		String token = UUID.randomUUID().toString();

		try {
			if (Config.SYSTEM_MAINTENANCE) {
				throw new AccessDeniedException("System under maintenance");
			} else {
				GenericHolder.set(token);

				AuthenticationManager authMgr = (AuthenticationManager) appCtx.getBean("authenticationManager");
				Authentication auth = new UsernamePasswordAuthenticationToken(user, password);
				auth = authMgr.authenticate(auth);
				log.debug("Authentication: {}", auth);

				DbSessionManager.getInstance().add(token, auth);
				loadUserData(user);

				// Activity log
				// @see com.openkm.spring.LoggerListener
			}
		} catch (AuthenticationException e) {
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			GenericHolder.unset();
		}

		log.debug("login: {}", token);
		return token;
	}

	@Override
	public void logout(String token) throws RepositoryException, DatabaseException {
		log.debug("logout({})", token);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (auth != null) {
				if (!Config.SYSTEM_USER.equals(auth.getName())) {
					DbSessionManager.getInstance().remove(token);

					// AUTOMATION - PRE
					Map<String, Object> env = new HashMap<>();
					env.put(AutomationUtils.USER, PrincipalUtils.getUser());
					try {
						AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_USER_LOGOUT, AutomationRule.AT_PRE, env);
					} catch (Exception e) {
						log.info("Automation ERROR: {}", e.getCause());
					}

					// Activity log
					UserActivity.log(auth.getName(), "LOGOUT", token, null, null);
				} else {
					log.warn("'" + Config.SYSTEM_USER + "' user should not logout");
					StackTraceUtils.logTrace(log);
				}
			}
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("logout: void");
	}

	@Override
	public void grantUser(String token, String nodeId, String guser, int permissions, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("grantUser({}, {}, {}, {}, {})", new Object[]{token, nodeId, guser, permissions, recursive});
		Authentication auth = null, oldAuth = null;
		String nodePath = null;
		String nodeUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(nodeId)) {
				nodePath = nodeId;
				nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
			} else {
				nodePath = NodeBaseDAO.getInstance().getPathFromUuid(nodeId);
				nodeUuid = nodeId;
			}

			NodeBaseDAO.getInstance().grantUserPermissions(nodeUuid, guser, permissions, recursive);

			// Activity log
			UserActivity.log(auth.getName(), "GRANT_USER", nodeUuid, nodePath, guser + ", " + permissions);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("grantUser: void");
	}

	@Override
	public void revokeUser(String token, String nodeId, String guser, int permissions, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("revokeUser({}, {}, {}, {}, {})", new Object[]{token, nodeId, guser, permissions, recursive});
		Authentication auth = null, oldAuth = null;
		String nodePath = null;
		String nodeUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(nodeId)) {
				nodePath = nodeId;
				nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
			} else {
				nodePath = NodeBaseDAO.getInstance().getPathFromUuid(nodeId);
				nodeUuid = nodeId;
			}

			NodeBaseDAO.getInstance().revokeUserPermissions(nodeUuid, guser, permissions, recursive);

			// Activity log
			UserActivity.log(auth.getName(), "REVOKE_USER", nodeUuid, nodePath, guser + ", " + permissions);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("revokeUser: void");
	}

	@Override
	public Map<String, Integer> getGrantedUsers(String token, String nodeId) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getGrantedUsers({}, {})", token, nodeId);
		Map<String, Integer> users = new HashMap<String, Integer>();
		@SuppressWarnings("unused")
		Authentication oldAuth = null;
		@SuppressWarnings("unused")
		String nodeUuid = null;

		try {
			if (token == null) {
				PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(nodeId)) {
				nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
			} else {
				nodeUuid = nodeId;
			}

			users = NodeBaseDAO.getInstance().getUserPermissions(nodeUuid);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getGrantedUsers: {}", users);
		return users;
	}

	@Override
	public void grantRole(String token, String nodeId, String role, int permissions, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("grantRole({}, {}, {}, {}, {})", new Object[]{token, nodeId, role, permissions, recursive});
		Authentication auth = null, oldAuth = null;
		String nodePath = null;
		String nodeUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(nodeId)) {
				nodePath = nodeId;
				nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
			} else {
				nodePath = NodeBaseDAO.getInstance().getPathFromUuid(nodeId);
				nodeUuid = nodeId;
			}

			NodeBaseDAO.getInstance().grantRolePermissions(nodeUuid, role, permissions, recursive);

			// Activity log
			UserActivity.log(auth.getName(), "GRANT_ROLE", nodeUuid, nodePath, role + ", " + permissions);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("grantRole: void");
	}

	@Override
	public void revokeRole(String token, String nodeId, String role, int permissions, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("revokeRole({}, {}, {}, {}, {})", new Object[]{token, nodeId, role, permissions, recursive});
		Authentication auth = null, oldAuth = null;
		String nodePath = null;
		String nodeUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(nodeId)) {
				nodePath = nodeId;
				nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
			} else {
				nodePath = NodeBaseDAO.getInstance().getPathFromUuid(nodeId);
				nodeUuid = nodeId;
			}

			NodeBaseDAO.getInstance().revokeRolePermissions(nodeUuid, role, permissions, recursive);

			// Activity log
			UserActivity.log(auth.getName(), "REVOKE_ROLE", nodeUuid, nodePath, role + ", " + permissions);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("revokeRole: void");
	}

	@Override
	public void changeSecurity(String token, String nodeId, Map<String, Integer> grantUsers, Map<String, Integer> revokeUsers,
	                           Map<String, Integer> grantRoles, Map<String, Integer> revokeRoles, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("changeSecurity({}, {}, {}, {}, {}, {}, {})", new Object[]{token, nodeId, grantUsers, revokeUsers, grantRoles,
				revokeRoles, recursive});
		Authentication auth = null, oldAuth = null;
		String nodePath = null;
		String nodeUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(nodeId)) {
				nodePath = nodeId;
				nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
			} else {
				nodePath = NodeBaseDAO.getInstance().getPathFromUuid(nodeId);
				nodeUuid = nodeId;
			}

			if (recursive) {
				if (NodeBaseDAO.getInstance().subTreeHasMoreThanNodes(nodePath, Config.SECURITY_LIVE_CHANGE_NODE_LIMIT)) {
					// Add pending task because will take too long to complete
					ChangeSecurityParams params = new ChangeSecurityParams();
					params.setUser(PrincipalUtils.getUser());
					params.setRoles(PrincipalUtils.getRoles());
					params.setGrantUsers(grantUsers);
					params.setRevokeUsers(revokeUsers);
					params.setGrantRoles(grantRoles);
					params.setRevokeRoles(revokeRoles);
					Gson gson = new Gson();
					PendingTask pt = new PendingTask();
					pt.setNode(nodeUuid);
					pt.setTask(PendingTask.TASK_CHANGE_SECURITY);
					pt.setParams(gson.toJson(params));
					pt.setCreated(Calendar.getInstance());
					PendingTaskDAO.getInstance().create(pt);
				} else {
					NodeBaseDAO.getInstance().changeSecurity(nodeUuid, grantUsers, revokeUsers, grantRoles, revokeRoles, true);
				}
			} else {
				NodeBaseDAO.getInstance().changeSecurity(nodeUuid, grantUsers, revokeUsers, grantRoles, revokeRoles, false);
			}

			// Activity log
			UserActivity.log(auth.getName(), "CHANGE_SECURITY", nodeUuid, nodePath, grantUsers + ", " + revokeUsers + ", " + grantRoles
					+ ", " + revokeRoles + ", " + recursive);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("changeSecurity: void");
	}

	@Override
	public Map<String, Integer> getGrantedRoles(String token, String nodeId) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getGrantedRoles({}, {})", token, nodeId);
		Map<String, Integer> roles = new HashMap<String, Integer>();
		@SuppressWarnings("unused")
		Authentication oldAuth = null;
		@SuppressWarnings("unused")
		String nodeUuid = null;

		try {
			if (token == null) {
				PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(nodeId)) {
				nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
			} else {
				nodeUuid = nodeId;
			}

			roles = NodeBaseDAO.getInstance().getRolePermissions(nodeUuid);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getGrantedRoles: {}", roles);
		return roles;
	}

	@Override
	public List<String> getUsers(String token) throws PrincipalAdapterException {
		List<String> users = new ArrayList<String>();
		Authentication oldAuth = null;

		try {
			if (token == null) {
				PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				PrincipalUtils.getAuthenticationByToken(token);
			}

			users = CommonAuthModule.getUsers();
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		return users;
	}

	@Override
	public List<String> getRoles(String token) throws PrincipalAdapterException {
		List<String> roles = new ArrayList<String>();
		Authentication oldAuth = null;

		try {
			if (token == null) {
				PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				PrincipalUtils.getAuthenticationByToken(token);
			}

			roles = CommonAuthModule.getRoles();
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		return roles;
	}

	@Override
	public List<String> getUsersByRole(String token, String role) throws PrincipalAdapterException {
		List<String> users = new ArrayList<String>();
		Authentication oldAuth = null;

		try {
			if (token == null) {
				PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				PrincipalUtils.getAuthenticationByToken(token);
			}

			users = CommonAuthModule.getUsersByRole(role);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		return users;
	}

	@Override
	public List<String> getRolesByUser(String token, String user) throws PrincipalAdapterException {
		List<String> roles = new ArrayList<String>();
		Authentication oldAuth = null;

		try {
			if (token == null) {
				PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				PrincipalUtils.getAuthenticationByToken(token);
			}

			roles = CommonAuthModule.getRolesByUser(user);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		return roles;
	}

	@Override
	public String getMail(String token, String user) throws PrincipalAdapterException {
		String mail = null;
		Authentication oldAuth = null;

		try {
			if (token == null) {
				PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				PrincipalUtils.getAuthenticationByToken(token);
			}

			mail = CommonAuthModule.getMail(user);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		return mail;
	}

	@Override
	public String getName(String token, String user) throws PrincipalAdapterException {
		String name = null;
		Authentication oldAuth = null;

		try {
			if (token == null) {
				PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				PrincipalUtils.getAuthenticationByToken(token);
			}

			name = CommonAuthModule.getName(user);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		return name;
	}

	/**
	 * Load user data
	 */
	public static void loadUserData(String user) throws PathNotFoundException, AccessDeniedException, ItemExistsException,
			DatabaseException {
		log.debug("loadUserData({})", user);
		String baseTrashPath = "/" + Repository.TRASH;
		String basePersonalPath = "/" + Repository.PERSONAL;
		String baseMailPath = "/" + Repository.MAIL;
		String userTrashPath = baseTrashPath + "/" + user;
		String userPersonalPath = basePersonalPath + "/" + user;
		String userMailPath = baseMailPath + "/" + user;

		synchronized (user) {
			if (!NodeBaseDAO.getInstance().itemPathExists(userTrashPath)) {
				log.info("Create {}/{}", Repository.TRASH, user);
				createBase(user, baseTrashPath);
			}

			if (!NodeBaseDAO.getInstance().itemPathExists(userPersonalPath)) {
				log.info("Create {}/{}", Repository.PERSONAL, user);
				createBase(user, basePersonalPath);
			}

			if (!NodeBaseDAO.getInstance().itemPathExists(userMailPath)) {
				log.info("Create {}/{}", Repository.MAIL, user);
				createBase(user, baseMailPath);
			}
		}

		log.debug("loadUserData: void");
	}

	/**
	 * Create base node
	 */
	private static void createBase(String user, String basePath) throws PathNotFoundException, AccessDeniedException, ItemExistsException,
			DatabaseException {
		log.debug("createBase({}, {})", user, basePath);
		String baseUuid = NodeBaseDAO.getInstance().getUuidFromPath(basePath);
		NodeFolder nFolder = new NodeFolder();

		// Add basic properties
		nFolder.setParent(baseUuid);
		nFolder.setAuthor(user);
		nFolder.setName(user);
		nFolder.setContext(PathUtils.fixContext(basePath));
		nFolder.setUuid(UUID.randomUUID().toString());
		nFolder.setCreated(Calendar.getInstance());

		if (Config.STORE_NODE_PATH) {
			nFolder.setPath(basePath + "/" + user);
		}

		// Auth info
		int perms = Permission.READ | Permission.WRITE | Permission.DELETE | Permission.SECURITY;
		nFolder.getUserPermissions().put(user, perms);
		NodeFolderDAO.getInstance().create(nFolder);
	}

	/*
	 * ------------------------------------------------------------------
	 * These methods only works if using the OpenKM user database.
	 * ------------------------------------------------------------------
	 */
	@Override
	public void createUser(String token, String user, String password, String email, String name, boolean active)
			throws PrincipalAdapterException {
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonAuthModule.getPrincipalAdapter().createUser(user, password, email, name, active);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	@Override
	public void deleteUser(String token, String user) throws PrincipalAdapterException {
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonAuthModule.getPrincipalAdapter().deleteUser(user);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	@Override
	public void updateUser(String token, String user, String password, String email, String name, boolean active) throws PrincipalAdapterException {
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonAuthModule.getPrincipalAdapter().updateUser(user, password, email, name, active);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	@Override
	public void createRole(String token, String role, boolean active) throws PrincipalAdapterException {
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonAuthModule.getPrincipalAdapter().createRole(role, active);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	@Override
	public void deleteRole(String token, String role) throws PrincipalAdapterException {
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonAuthModule.getPrincipalAdapter().deleteRole(role);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	@Override
	public void updateRole(String token, String role, boolean active) throws PrincipalAdapterException {
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonAuthModule.getPrincipalAdapter().updateRole(role, active);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	@Override
	public void assignRole(String token, String user, String role) throws PrincipalAdapterException {
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonAuthModule.getPrincipalAdapter().assignRole(user, role);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	@Override
	public void removeRole(String token, String user, String role) throws PrincipalAdapterException {
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonAuthModule.getPrincipalAdapter().removeRole(user, role);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}
}
