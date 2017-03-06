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

package com.openkm.servlet.frontend;

import com.openkm.api.OKMAuth;
import com.openkm.bean.Permission;
import com.openkm.bean.Repository;
import com.openkm.core.*;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTGrantedUser;
import com.openkm.frontend.client.bean.GWTUser;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMAuthService;
import com.openkm.frontend.client.util.GWTGrantedUserComparator;
import com.openkm.frontend.client.util.RoleComparator;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.servlet.frontend.util.GWTUserComparator;
import com.openkm.util.MappingUtils;
import com.openkm.util.UserActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Servlet Class
 */
public class AuthServlet extends OKMRemoteServiceServlet implements OKMAuthService {
	private static Logger log = LoggerFactory.getLogger(AuthServlet.class);
	private static final long serialVersionUID = 2638205115826644606L;

	@Override
	public void logout() throws OKMException {
		log.debug("logout()");
		updateSessionManager();

		try {
			OKMAuth.getInstance().logout(null);
			getThreadLocalRequest().getSession().invalidate();
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("logout: void");
	}

	@Override
	public Map<String, Integer> getGrantedRoles(String nodePath) throws OKMException {
		log.debug("getGrantedRoles({})", nodePath);
		Map<String, Integer> hm = new HashMap<String, Integer>();
		updateSessionManager();

		try {
			if (!nodePath.startsWith("/" + Repository.METADATA)) {
				Map<String, Integer> tmp = OKMAuth.getInstance().getGrantedRoles(null, nodePath);
				hm = MappingUtils.map(tmp);
			}
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getGrantedRoles: {}", hm);
		return hm;
	}

	@Override
	public List<GWTGrantedUser> getGrantedUsers(String nodePath) throws OKMException {
		log.debug("getGrantedUsers({})", nodePath);
		List<GWTGrantedUser> guList = new ArrayList<GWTGrantedUser>();
		updateSessionManager();

		try {
			if (!nodePath.startsWith("/" + Repository.METADATA)) {
				Map<String, Integer> tmp = OKMAuth.getInstance().getGrantedUsers(null, nodePath);
				Map<String, Integer> hm = MappingUtils.map(tmp);

				for (String userId : hm.keySet()) {
					GWTGrantedUser gu = new GWTGrantedUser();
					gu.setPermisions(hm.get(userId));
					GWTUser user = new GWTUser();
					user.setId(userId);
					user.setUsername(OKMAuth.getInstance().getName(null, userId));
					gu.setUser(user);
					guList.add(gu);
				}
			}

			Collections.sort(guList, GWTGrantedUserComparator.getInstance());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getGrantedUsers: {}", guList);
		return guList;
	}

	@Override
	public String getRemoteUser() {
		log.debug("getRemoteUser()");
		updateSessionManager();
		String user = getThreadLocalRequest().getRemoteUser();
		log.debug("getRemoteUser: {}", user);
		return user;
	}

	@Override
	public List<GWTGrantedUser> getUngrantedUsers(String nodePath) throws OKMException {
		log.debug("getUngrantedUsers({})", nodePath);
		List<GWTGrantedUser> guList = new ArrayList<GWTGrantedUser>();
		updateSessionManager();

		try {
			Collection<String> grantedUsers = OKMAuth.getInstance().getGrantedUsers(null, nodePath).keySet();

			for (String userId : OKMAuth.getInstance().getUsers(null)) {
				if (!grantedUsers.contains(userId)) {
					GWTGrantedUser gu = new GWTGrantedUser();
					gu.setPermisions(0);
					GWTUser user = new GWTUser();
					user.setId(userId);
					user.setUsername(OKMAuth.getInstance().getName(null, userId));
					gu.setUser(user);
					guList.add(gu);
				}
			}

			Collections.sort(guList, GWTGrantedUserComparator.getInstance());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getUngrantedUsers: {}", guList);
		return guList;
	}

	@Override
	public List<String> getUngrantedRoles(String nodePath) throws OKMException {
		log.debug("getUngrantedRoles({})", nodePath);
		List<String> roleList = new ArrayList<String>();
		updateSessionManager();

		try {
			Collection<String> grantedRoles = OKMAuth.getInstance().getGrantedRoles(null, nodePath).keySet();

			// Not add roles that are granted
			for (String role : OKMAuth.getInstance().getRoles(null)) {
				if (!grantedRoles.contains(role) && checkConnectionRole(role)) {
					roleList.add(role);
				}
			}

			Collections.sort(roleList, RoleComparator.getInstance());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getUngrantedRoles: {}", roleList);
		return roleList;
	}

	@Override
	public List<GWTGrantedUser> getFilteredUngrantedUsers(String nodePath, String filter) throws OKMException {
		log.debug("getFilteredUngrantedUsers({})", nodePath);
		List<GWTGrantedUser> guList = new ArrayList<GWTGrantedUser>();
		updateSessionManager();

		try {
			Collection<String> col = OKMAuth.getInstance().getUsers(null);
			Collection<String> grantedUsers = OKMAuth.getInstance().getGrantedUsers(null, nodePath).keySet();

			for (String userId : col) {
				String userName = OKMAuth.getInstance().getName(null, userId);

				if (userName != null && !grantedUsers.contains(userId) && userName.toLowerCase().startsWith(filter.toLowerCase())) {
					GWTGrantedUser gu = new GWTGrantedUser();
					gu.setPermisions(0);
					GWTUser user = new GWTUser();
					user.setId(userId);
					user.setUsername(userName);
					gu.setUser(user);
					guList.add(gu);
				}
			}

			Collections.sort(guList, GWTGrantedUserComparator.getInstance());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getFilteredUngrantedUsers: {}", guList);
		return guList;
	}

	@Override
	public List<String> getFilteredUngrantedRoles(String nodePath, String filter) throws OKMException {
		log.debug("getFilteredUngrantedRoles({})", nodePath);
		List<String> roleList = new ArrayList<String>();
		updateSessionManager();

		try {
			Collection<String> grantedRoles = OKMAuth.getInstance().getGrantedRoles(null, nodePath).keySet();

			// Not add roles that are granted
			for (String role : OKMAuth.getInstance().getRoles(null)) {
				if (!grantedRoles.contains(role) && role.toLowerCase().startsWith(filter.toLowerCase())
						&& checkConnectionRole(role)) {
					roleList.add(role);
				}
			}

			Collections.sort(roleList, RoleComparator.getInstance());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getFilteredUngrantedRoles: {}", roleList);
		return roleList;
	}

	@Override
	public void grantUser(String path, String user, int permissions, boolean recursive) throws OKMException {
		log.debug("grantUser({}, {}, {}, {})", new Object[]{path, user, permissions, recursive});
		updateSessionManager();

		try {
			OKMAuth.getInstance().grantUser(null, path, user, permissions, recursive);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("grantUser: void");
	}

	@Override
	public void revokeUser(String path, String user, boolean recursive) throws OKMException {
		log.debug("revokeUser({}, {}, {})", new Object[]{path, user, recursive});
		updateSessionManager();

		try {
			OKMAuth oKMAuth = OKMAuth.getInstance();
			int allGrants = Permission.ALL_GRANTS;

			if ((Config.SECURITY_EXTENDED_MASK & Permission.PROPERTY_GROUP) == Permission.PROPERTY_GROUP) {
				allGrants = allGrants | Permission.PROPERTY_GROUP;
			}

			if ((Config.SECURITY_EXTENDED_MASK & Permission.COMPACT_HISTORY) == Permission.COMPACT_HISTORY) {
				allGrants = allGrants | Permission.COMPACT_HISTORY;
			}

			if ((Config.SECURITY_EXTENDED_MASK & Permission.START_WORKFLOW) == Permission.START_WORKFLOW) {
				allGrants = allGrants | Permission.START_WORKFLOW;
			}

			if ((Config.SECURITY_EXTENDED_MASK & Permission.DOWNLOAD) == Permission.DOWNLOAD) {
				allGrants = allGrants | Permission.DOWNLOAD;
			}

			oKMAuth.revokeUser(null, path, user, allGrants, recursive);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("revokeUser: void");
	}

	@Override
	public void revokeUser(String path, String user, int permissions, boolean recursive) throws OKMException {
		log.debug("revokeUser({}, {}, {}, {})", new Object[]{path, user, permissions, recursive});
		updateSessionManager();

		try {
			OKMAuth.getInstance().revokeUser(null, path, user, permissions, recursive);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("revokeUser: void");
	}

	@Override
	public void grantRole(String path, String role, int permissions, boolean recursive) throws OKMException {
		log.debug("grantRole({}, {}, {}, {})", new Object[]{path, role, permissions, recursive});
		updateSessionManager();

		try {
			OKMAuth.getInstance().grantRole(null, path, role, permissions, recursive);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("grantRole: void");
	}

	@Override
	public void revokeRole(String path, String role, boolean recursive) throws OKMException {
		log.debug("revokeRole({}, {}, {})", new Object[]{path, role, recursive});
		updateSessionManager();

		try {
			if (!(Config.SYSTEM_DEMO && (path.equals("/" + Repository.ROOT) || path.equals("/" + Repository.CATEGORIES) || path
					.equals("/" + Repository.TEMPLATES)))) {
				OKMAuth oKMAuth = OKMAuth.getInstance();
				int allGrants = Permission.ALL_GRANTS;

				if ((Config.SECURITY_EXTENDED_MASK & Permission.PROPERTY_GROUP) == Permission.PROPERTY_GROUP) {
					allGrants = allGrants | Permission.PROPERTY_GROUP;
				}

				if ((Config.SECURITY_EXTENDED_MASK & Permission.COMPACT_HISTORY) == Permission.COMPACT_HISTORY) {
					allGrants = allGrants | Permission.COMPACT_HISTORY;
				}

				if ((Config.SECURITY_EXTENDED_MASK & Permission.START_WORKFLOW) == Permission.START_WORKFLOW) {
					allGrants = allGrants | Permission.START_WORKFLOW;
				}

				if ((Config.SECURITY_EXTENDED_MASK & Permission.DOWNLOAD) == Permission.DOWNLOAD) {
					allGrants = allGrants | Permission.DOWNLOAD;
				}

				oKMAuth.revokeRole(null, path, role, allGrants, recursive);
			}
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("revokeRole: void");
	}

	@Override
	public void revokeRole(String path, String role, int permissions, boolean recursive) throws OKMException {
		log.debug("revokeRole({}, {}, {}, {})", new Object[]{path, role, permissions, recursive});
		updateSessionManager();

		try {
			if (!(Config.SYSTEM_DEMO && (path.equals("/" + Repository.ROOT) || path.equals("/" + Repository.CATEGORIES) || path
					.equals("/" + Repository.TEMPLATES)))) {
				OKMAuth.getInstance().revokeRole(null, path, role, permissions, recursive);
			}
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("revokeRole: void");
	}

	@Override
	public void keepAlive() throws OKMException {
		log.debug("keepAlive()");
		updateSessionManager();
		String user = getThreadLocalRequest().getRemoteUser();

		// Activity log
		UserActivity.log(user, "KEEP_ALIVE", null, null, null);
		log.debug("keepAlive: void");
	}

	@Override
	public List<GWTUser> getAllUsers() throws OKMException {
		log.debug("getAllUsers()");
		List<GWTUser> userList = new ArrayList<GWTUser>();
		updateSessionManager();

		try {
			for (String userId : OKMAuth.getInstance().getUsers(null)) {
				GWTUser user = new GWTUser();
				user.setId(userId);
				user.setUsername(OKMAuth.getInstance().getName(null, userId));
				userList.add(user);
			}

			Collections.sort(userList, GWTUserComparator.getInstance(getLanguage()));
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_PrincipalAdapter),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getAllUsers: {}", userList);
		return userList;
	}

	@Override
	public List<String> getAllRoles() throws OKMException {
		log.debug("getAllRoles()");
		List<String> roleList = new ArrayList<String>();
		updateSessionManager();

		try {
			for (String role : OKMAuth.getInstance().getRoles(null)) {
				if (checkConnectionRole(role)) {
					roleList.add(role);
				}
			}

			Collections.sort(roleList, RoleComparator.getInstance());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_PrincipalAdapter),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getAllRoles: {}", roleList);
		return roleList;
	}

	@Override
	public List<GWTUser> getFilteredAllUsers(String filter, List<String> selectedUsers) throws OKMException {
		log.debug("getFilteredAllUsers()");
		List<GWTUser> userList = new ArrayList<GWTUser>();
		updateSessionManager();

		try {
			for (String userId : OKMAuth.getInstance().getUsers(null)) {
				String userName = OKMAuth.getInstance().getName(null, userId);
				if (userName.toLowerCase().startsWith(filter.toLowerCase()) && !selectedUsers.contains(userId)) {
					GWTUser user = new GWTUser();
					user.setId(userId);
					user.setUsername(OKMAuth.getInstance().getName(null, userId));
					userList.add(user);
				}
			}

			Collections.sort(userList, GWTUserComparator.getInstance(getLanguage()));
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_PrincipalAdapter),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getFilteredAllUsers: {}", userList);
		return userList;
	}

	@Override
	public List<String> getFilteredAllRoles(String filter, List<String> selectedRoles) throws OKMException {
		log.debug("getFilteredAllRoles()");
		List<String> roleList = new ArrayList<String>();
		updateSessionManager();

		try {
			for (String role : OKMAuth.getInstance().getRoles(null)) {
				if (role.toLowerCase().startsWith(filter.toLowerCase()) && !selectedRoles.contains(role)
						&& checkConnectionRole(role)) {
					roleList.add(role);
				}
			}

			Collections.sort(roleList, RoleComparator.getInstance());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_PrincipalAdapter),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getFilteredAllRoles: {}", roleList);
		return roleList;
	}

	@Override
	public void changeSecurity(String path, Map<String, Integer> grantUsers, Map<String, Integer> revokeUsers,
	                           Map<String, Integer> grantRoles, Map<String, Integer> revokeRoles, boolean recursive) throws OKMException {
		log.debug("changeSecurity({}, {}, {}, {}, {}, {})", new Object[]{path, grantUsers, revokeUsers, grantRoles,
				revokeRoles, recursive});
		updateSessionManager();

		try {
			if (!(Config.SYSTEM_DEMO && (path.equals("/" + Repository.ROOT) || path.equals("/" + Repository.CATEGORIES) || path
					.equals("/" + Repository.TEMPLATES)))) {
				OKMAuth.getInstance().changeSecurity(null, path, grantUsers, revokeUsers, grantRoles, revokeRoles, recursive);
			}
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMAuthService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("changeSecurity: void");
	}

	private boolean checkConnectionRole(String role) {
		if (Config.PRINCIPAL_HIDE_CONNECTION_ROLES) {
			return !role.equals(Config.DEFAULT_USER_ROLE) && !role.equals(Config.DEFAULT_ADMIN_ROLE);
		} else {
			return true;
		}
	}
}
