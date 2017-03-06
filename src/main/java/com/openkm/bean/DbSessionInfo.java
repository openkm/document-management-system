/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (C) 2006-2011  Paco Avila & Josep Llort
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

import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.Calendar;

public class DbSessionInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Calendar creation;
	private Calendar lastAccess;
	private Authentication auth;

	public Calendar getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Calendar lastAccess) {
		this.lastAccess = lastAccess;
	}

	public Calendar getCreation() {
		return creation;
	}

	public void setCreation(Calendar creation) {
		this.creation = creation;
	}

	public Authentication getAuth() {
		return auth;
	}

	public void setAuth(Authentication auth) {
		this.auth = auth;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("auth=");
		sb.append(auth);
		sb.append(", creation=");
		sb.append(creation == null ? null : creation.getTime());
		sb.append(", lastAccess=");
		sb.append(lastAccess == null ? null : lastAccess.getTime());
		sb.append("]");
		return sb.toString();
	}
}
