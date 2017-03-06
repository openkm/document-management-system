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

package com.openkm.principal;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.dao.AuthDAO;
import com.openkm.dao.bean.Role;
import com.openkm.dao.bean.User;
import com.openkm.spring.PrincipalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DatabasePrincipalAdapter implements PrincipalAdapter {
	private static Logger log = LoggerFactory.getLogger(DatabasePrincipalAdapter.class);

	@Override
	public List<String> getUsers() throws PrincipalAdapterException {
		log.debug("getUsers()");
		List<String> list = new ArrayList<String>();

		try {
			List<User> col = AuthDAO.findAllUsers(Config.PRINCIPAL_DATABASE_FILTER_INACTIVE_USERS);

			for (Iterator<User> it = col.iterator(); it.hasNext(); ) {
				User dbUser = it.next();
				list.add(dbUser.getId());
			}
		} catch (DatabaseException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		}

		log.debug("getUsers: {}", list);
		return list;
	}

	@Override
	public List<String> getRoles() throws PrincipalAdapterException {
		log.debug("getRoles()");
		List<String> list = new ArrayList<String>();

		try {
			List<Role> col = AuthDAO.findAllRoles();

			for (Iterator<Role> it = col.iterator(); it.hasNext(); ) {
				Role dbRole = it.next();
				list.add(dbRole.getId());
			}
		} catch (DatabaseException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		}

		log.debug("getRoles: {}", list);
		return list;
	}

	@Override
	public List<String> getUsersByRole(String role) throws PrincipalAdapterException {
		log.debug("getUsersByRole({})", role);
		List<String> list = new ArrayList<String>();

		try {
			List<User> col = AuthDAO.findUsersByRole(role, true);

			for (Iterator<User> it = col.iterator(); it.hasNext(); ) {
				User dbUser = it.next();
				list.add(dbUser.getId());
			}
		} catch (DatabaseException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		}

		log.debug("getUsersByRole: {}", list);
		return list;
	}

	@Override
	public List<String> getRolesByUser(String user) throws PrincipalAdapterException {
		log.debug("getRolesByUser({})", user);
		List<String> list = new ArrayList<String>();

		try {
			List<Role> col = AuthDAO.findRolesByUser(user, true);

			for (Iterator<Role> it = col.iterator(); it.hasNext(); ) {
				Role dbRole = it.next();
				list.add(dbRole.getId());
			}
		} catch (DatabaseException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		}

		log.debug("getRolesByUser: {}", list);
		return list;
	}

	@Override
	public String getMail(String user) throws PrincipalAdapterException {
		log.debug("getMail({})", user);
		String mail = null;

		try {
			User usr = AuthDAO.findUserByPk(user);

			if (usr != null && !usr.getEmail().equals("")) {
				mail = usr.getEmail();
			}
		} catch (DatabaseException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		}

		log.debug("getMail: {}", mail);
		return mail;
	}

	@Override
	public String getName(String user) throws PrincipalAdapterException {
		log.debug("getName({})", user);
		String name = null;

		try {
			User usr = AuthDAO.findUserByPk(user);

			if (usr != null && !usr.getName().equals("")) {
				name = usr.getName();
			}
		} catch (DatabaseException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		}

		log.debug("getName: {}", name);
		return name;
	}

	@Override
	public String getPassword(String user) throws PrincipalAdapterException {
		log.debug("getPassword({})", user);
		String password = null;

		try {
			User usr = AuthDAO.findUserByPk(user);

			if (usr != null && !usr.getName().equals("")) {
				password = usr.getPassword();
			}
		} catch (DatabaseException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		}

		log.debug("getPassword: {}", password);
		return password;
	}

	@Override
	public void createUser(String user, String password, String email, String name, boolean active) throws PrincipalAdapterException {
		try {
			if (PrincipalUtils.hasRole(Config.DEFAULT_ADMIN_ROLE)) {
				User usr = new User();
				usr.setId(user);
				usr.setPassword(password);
				usr.setName(name);
				usr.setEmail(email);
				usr.setActive(active);
				AuthDAO.createUser(usr);
			} else {
				throw new PrincipalAdapterException("Only administrators can create users");
			}
		} catch (DatabaseException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		}
	}

	@Override
	public void deleteUser(String user) throws PrincipalAdapterException {
		try {
			if (PrincipalUtils.hasRole(Config.DEFAULT_ADMIN_ROLE)) {
				AuthDAO.deleteUser(user);
			} else {
				throw new PrincipalAdapterException("Only administrators can delete users");
			}
		} catch (DatabaseException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		}
	}

	@Override
	public void updateUser(String user, String password, String email, String name, boolean active) throws PrincipalAdapterException {
		try {
			if (PrincipalUtils.hasRole(Config.DEFAULT_ADMIN_ROLE)) {
				User usr = new User();
				usr.setId(user);
				usr.setPassword(password);
				usr.setName(name);
				usr.setEmail(email);
				usr.setActive(active);
				AuthDAO.updateUser(usr);
			} else {
				throw new PrincipalAdapterException("Only administrators can delete users");
			}
		} catch (DatabaseException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		}
	}

	@Override
	public void createRole(String role, boolean active) throws PrincipalAdapterException {
		try {
			if (PrincipalUtils.hasRole(Config.DEFAULT_ADMIN_ROLE)) {
				Role rol = new Role();
				rol.setId(role);
				rol.setActive(active);
				AuthDAO.createRole(rol);
			} else {
				throw new PrincipalAdapterException("Only administrators can create roles");
			}
		} catch (DatabaseException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		}
	}

	@Override
	public void deleteRole(String role) throws PrincipalAdapterException {
		try {
			if (PrincipalUtils.hasRole(Config.DEFAULT_ADMIN_ROLE)) {
				AuthDAO.deleteRole(role);
			} else {
				throw new PrincipalAdapterException("Only administrators can delete roles");
			}
		} catch (DatabaseException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		}
	}

	@Override
	public void updateRole(String role, boolean active) throws PrincipalAdapterException {
		try {
			if (PrincipalUtils.hasRole(Config.DEFAULT_ADMIN_ROLE)) {
				Role rol = new Role();
				rol.setId(role);
				rol.setActive(active);
				AuthDAO.updateRole(rol);
			} else {
				throw new PrincipalAdapterException("Only administrators can create roles");
			}
		} catch (DatabaseException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		}
	}

	@Override
	public void assignRole(String user, String role) throws PrincipalAdapterException {
		try {
			if (PrincipalUtils.hasRole(Config.DEFAULT_ADMIN_ROLE)) {
				User usr = AuthDAO.findUserByPk(user);
				Role rol = AuthDAO.findRoleByPk(role);
				usr.getRoles().add(rol);
				AuthDAO.updateUser(usr);
			} else {
				throw new PrincipalAdapterException("Only administrators can assign roles");
			}
		} catch (DatabaseException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		}
	}

	@Override
	public void removeRole(String user, String role) throws PrincipalAdapterException {
		try {
			if (PrincipalUtils.hasRole(Config.DEFAULT_ADMIN_ROLE)) {
				User usr = AuthDAO.findUserByPk(user);
				Role rol = AuthDAO.findRoleByPk(role);
				usr.getRoles().remove(rol);
				AuthDAO.updateUser(usr);
			} else {
				throw new PrincipalAdapterException("Only administrators can remove roles");
			}
		} catch (DatabaseException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		} catch (AccessDeniedException e) {
			throw new PrincipalAdapterException(e.getMessage(), e);
		}
	}
}
