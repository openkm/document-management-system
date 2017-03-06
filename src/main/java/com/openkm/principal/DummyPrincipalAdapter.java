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

import com.openkm.core.Config;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DummyPrincipalAdapter implements PrincipalAdapter {
	private static Logger log = LoggerFactory.getLogger(DummyPrincipalAdapter.class);
	private static final String TEST_USER = "test";

	@Override
	public List<String> getUsers() throws PrincipalAdapterException {
		log.debug("getUsers()");
		List<String> list = new ArrayList<String>();
		list.add(Config.ADMIN_USER);
		list.add(TEST_USER);
		log.debug("getUsers: {}", list);
		return list;
	}

	@Override
	public List<String> getRoles() throws PrincipalAdapterException {
		log.debug("getRoles()");
		List<String> list = new ArrayList<String>();
		list.add(Config.DEFAULT_ADMIN_ROLE);
		list.add(Config.DEFAULT_USER_ROLE);
		log.debug("getRoles: {}", list);
		return list;
	}

	@Override
	public List<String> getUsersByRole(String role) throws PrincipalAdapterException {
		List<String> list = new ArrayList<String>();

		if (role.equals(Config.DEFAULT_ADMIN_ROLE)) {
			list.add(Config.ADMIN_USER);
		} else if (role.equals(Config.DEFAULT_USER_ROLE)) {
			list.add(Config.ADMIN_USER);
			list.add(TEST_USER);
		}

		return list;
	}

	@Override
	public List<String> getRolesByUser(String user) throws PrincipalAdapterException {
		List<String> list = new ArrayList<String>();

		if (user.equals(Config.ADMIN_USER)) {
			list.add(Config.DEFAULT_ADMIN_ROLE);
			list.add(Config.DEFAULT_USER_ROLE);
		} else if (user.equals(TEST_USER)) {
			list.add(Config.DEFAULT_USER_ROLE);
		}

		return list;
	}

	@Override
	public String getMail(String user) throws PrincipalAdapterException {
		String mail = null;

		if (user.equals(Config.ADMIN_USER)) {
			mail = "admin@openkm.com";
		} else if (user.equals(TEST_USER)) {
			mail = "test@openkm.com";
		}

		return mail;
	}

	@Override
	public String getName(String user) throws PrincipalAdapterException {
		String name = null;

		if (user.equals(Config.ADMIN_USER)) {
			name = "Administrator";
		} else if (user.equals(TEST_USER)) {
			name = "Test";
		}

		return name;
	}

	@Override
	public String getPassword(String user) throws PrincipalAdapterException {
		String password = null;

		if (user.equals(Config.ADMIN_USER)) {
			password = "admin";
		} else if (user.equals(TEST_USER)) {
			password = "test";
		}

		return password;
	}

	@Override
	public void createUser(String user, String password, String email, String name, boolean active) throws PrincipalAdapterException {
		throw new NotImplementedException("createUser");
	}

	@Override
	public void deleteUser(String user) throws PrincipalAdapterException {
		throw new NotImplementedException("deleteUser");
	}

	@Override
	public void updateUser(String user, String password, String email, String name, boolean active) throws PrincipalAdapterException {
		throw new NotImplementedException("updateUser");
	}

	@Override
	public void createRole(String role, boolean active) throws PrincipalAdapterException {
		throw new NotImplementedException("createRole");
	}

	@Override
	public void deleteRole(String role) throws PrincipalAdapterException {
		throw new NotImplementedException("deleteRole");
	}

	@Override
	public void updateRole(String role, boolean active) throws PrincipalAdapterException {
		throw new NotImplementedException("updateRole");
	}

	@Override
	public void assignRole(String user, String role) throws PrincipalAdapterException {
		throw new NotImplementedException("assignRole");
	}

	@Override
	public void removeRole(String user, String role) throws PrincipalAdapterException {
		throw new NotImplementedException("removeRole");
	}
}
