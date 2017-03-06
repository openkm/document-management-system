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

public class MailFilter implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String path;
	private String node;
	private boolean grouping = true;
	private boolean active = false;
	private Set<MailFilterRule> filterRules = new HashSet<MailFilterRule>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public boolean isGrouping() {
		return grouping;
	}

	public void setGrouping(boolean grouping) {
		this.grouping = grouping;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Set<MailFilterRule> getFilterRules() {
		return filterRules;
	}

	public void setFilterRules(Set<MailFilterRule> filterRules) {
		this.filterRules = filterRules;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=");
		sb.append(id);
		sb.append(", path=");
		sb.append(path);
		sb.append(", node=");
		sb.append(node);
		sb.append(", grouping=");
		sb.append(grouping);
		sb.append(", active=");
		sb.append(active);
		sb.append(", filterRules=");
		sb.append(filterRules);
		sb.append("}");
		return sb.toString();
	}
}
