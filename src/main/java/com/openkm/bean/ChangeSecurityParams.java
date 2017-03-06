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

package com.openkm.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author pavila
 *
 */
public class ChangeSecurityParams implements Serializable {
	private static final long serialVersionUID = 1L;
	private String user = new String();
	private Set<String> roles = new HashSet<String>();
	private Map<String, Integer> grantUsers = new HashMap<String, Integer>();
	private Map<String, Integer> revokeUsers = new HashMap<String, Integer>();
	private Map<String, Integer> grantRoles = new HashMap<String, Integer>();
	private Map<String, Integer> revokeRoles = new HashMap<String, Integer>();

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public Map<String, Integer> getGrantUsers() {
		return grantUsers;
	}

	public void setGrantUsers(Map<String, Integer> grantUsers) {
		this.grantUsers = grantUsers;
	}

	public Map<String, Integer> getRevokeUsers() {
		return revokeUsers;
	}

	public void setRevokeUsers(Map<String, Integer> revokeUsers) {
		this.revokeUsers = revokeUsers;
	}

	public Map<String, Integer> getGrantRoles() {
		return grantRoles;
	}

	public void setGrantRoles(Map<String, Integer> grantRoles) {
		this.grantRoles = grantRoles;
	}

	public Map<String, Integer> getRevokeRoles() {
		return revokeRoles;
	}

	public void setRevokeRoles(Map<String, Integer> revokeRoles) {
		this.revokeRoles = revokeRoles;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("user=");
		sb.append(user);
		sb.append(", roles=");
		sb.append(roles);
		sb.append(", grantUsers=");
		sb.append(grantUsers);
		sb.append(", revokeUsers=");
		sb.append(revokeUsers);
		sb.append(", grantRoles=");
		sb.append(grantRoles);
		sb.append(", revokeRoles=");
		sb.append(revokeRoles);
		sb.append("}");
		return sb.toString();
	}
}
