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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class UsersRolesPrincipalAdapter implements PrincipalAdapter {
	private static Logger log = LoggerFactory.getLogger(UsersRolesPrincipalAdapter.class);

	@Override
	public List<String> getUsers() throws PrincipalAdapterException {
		log.debug("getUsers()");
		List<String> list = new ArrayList<String>();
		Properties prop = new Properties();

		try {
			prop.load(new FileInputStream(Config.HOME_DIR + "/server/default/conf/props/openkm-users.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Enumeration<Object> e = prop.keys(); e.hasMoreElements(); ) {
			String user = (String) e.nextElement();
			if (!Config.SYSTEM_USER.equals(user)) {
				list.add(user);
			}
		}

		log.debug("getUsers: {}", list);
		return list;
	}

	@Override
	public List<String> getRoles() throws PrincipalAdapterException {
		log.debug("getRoles()");
		List<String> list = new ArrayList<String>();
		Properties prop = new Properties();

		try {
			prop.load(new FileInputStream(Config.HOME_DIR + "/server/default/conf/props/openkm-roles.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Enumeration<Object> e = prop.elements(); e.hasMoreElements(); ) {
			for (StringTokenizer st = new StringTokenizer((String) e.nextElement(), ","); st.hasMoreTokens(); ) {
				String role = st.nextToken();

				if (!Config.DEFAULT_ADMIN_ROLE.equals(role) && !list.contains(role)) {
					list.add(role);
				}
			}
		}

		log.debug("getRoles: {}", list);
		return list;
	}

	@Override
	public List<String> getUsersByRole(String role) throws PrincipalAdapterException {
		throw new NotImplementedException("getUsersByRole");
	}

	@Override
	public List<String> getRolesByUser(String user) throws PrincipalAdapterException {
		throw new NotImplementedException("getRolesByUser");
	}

	@Override
	public String getMail(String user) throws PrincipalAdapterException {
		log.debug("getMail({})", user);
		Properties prop = new Properties();
		String mail = null;

		try {
			prop.load(new FileInputStream(Config.HOME_DIR + "/server/default/conf/props/openkm-emails.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mail = prop.getProperty(user);
		log.debug("getMail: {}", mail);
		return mail;
	}

	@Override
	public String getName(String user) throws PrincipalAdapterException {
		log.debug("getName({})", user);
		Properties prop = new Properties();
		String name = null;

		try {
			prop.load(new FileInputStream(Config.HOME_DIR + "/server/default/conf/props/openkm-names.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		name = prop.getProperty(user);
		log.debug("getName: {}", name);
		return name;
	}

	@Override
	public String getPassword(String user) throws PrincipalAdapterException {
		throw new UnsupportedOperationException("Not implemented");
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
