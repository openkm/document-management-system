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

package com.openkm.extension.dao.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * StapleGroup
 *
 * @author jllort
 *
 */
public class StapleGroup implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id = -1;
	private String user = "";
	private Set<Staple> staples = new HashSet<Staple>();

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Set<Staple> getStaples() {
		return staples;
	}

	public void setStaples(Set<Staple> staples) {
		this.staples = staples;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=");
		sb.append(id);
		sb.append(", user=");
		sb.append(user);
		sb.append(", staples=");
		sb.append(staples);
		sb.append("}");
		return sb.toString();
	}
}