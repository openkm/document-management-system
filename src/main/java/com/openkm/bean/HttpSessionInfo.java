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
import java.util.Calendar;

public class HttpSessionInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String user;
	private String ip;
	private String host;
	private String id;
	private Calendar creation;
	private Calendar lastAccess;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Calendar getCreation() {
		return creation;
	}

	public void setCreation(Calendar creation) {
		this.creation = creation;
	}

	public Calendar getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Calendar lastAccess) {
		this.lastAccess = lastAccess;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("user=");
		sb.append(user);
		sb.append(", ip=");
		sb.append(ip);
		sb.append(", id=");
		sb.append(id);
		sb.append(", creation=");
		sb.append(creation == null ? null : creation.getTime());
		sb.append(", lastAccess=");
		sb.append(lastAccess == null ? null : lastAccess.getTime());
		sb.append("}");
		return sb.toString();
	}
}
