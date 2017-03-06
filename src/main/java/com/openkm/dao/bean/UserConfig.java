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

package com.openkm.dao.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class UserConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	private String user;
	private String homePath;
	private String homeType;
	private String homeNode;
	private Profile profile;
	private Set<String> lockTokens = new HashSet<String>();

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getHomePath() {
		return homePath;
	}

	public void setHomePath(String homePath) {
		this.homePath = homePath;
	}

	public String getHomeNode() {
		return homeNode;
	}

	public void setHomeNode(String homeNode) {
		this.homeNode = homeNode;
	}

	public String getHomeType() {
		return homeType;
	}

	public void setHomeType(String homeType) {
		this.homeType = homeType;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Set<String> getLockTokens() {
		return lockTokens;
	}

	public void setLockTokens(Set<String> lockTokens) {
		this.lockTokens = lockTokens;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("user=");
		sb.append(user);
		sb.append(", homePath=");
		sb.append(homePath);
		sb.append(", homeType=");
		sb.append(homeType);
		sb.append(", homeNode=");
		sb.append(homeNode);
		sb.append(", profile=");
		sb.append(profile);
		sb.append(", lockTokens=");
		sb.append(lockTokens);
		sb.append("}");
		return sb.toString();
	}
}
