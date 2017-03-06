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

package com.openkm.frontend.client.bean.extension;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;

/**
 * GWTWikiPage
 *
 * @author jllort
 *
 */
public class GWTWikiPage implements IsSerializable {
	private int id;
	private Date date;
	private String title;
	private String user;
	private String lockUser;
	private String node;
	private String content;
	private boolean deleted = false;

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public GWTWikiPage clone() {
		GWTWikiPage clone = new GWTWikiPage();
		clone.setContent(getContent());
		clone.setDate(getDate());
		clone.setId(getId());
		clone.setLockUser(getLockUser());
		clone.setTitle(getTitle());
		clone.setUser(getUser());
		clone.setNode(getNode());
		return clone;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLockUser() {
		return lockUser;
	}

	public void setLockUser(String lockUser) {
		this.lockUser = lockUser;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=");
		sb.append(id);
		sb.append(", user=");
		sb.append(user);
		sb.append(", title=");
		sb.append(title);
		sb.append(", lockUser=");
		sb.append(lockUser);
		sb.append(", node=");
		sb.append(node);
		sb.append(", date=");
		sb.append(date == null ? null : date);
		sb.append(", content=");
		sb.append(content);
		sb.append(", deleted=");
		sb.append(deleted);
		sb.append("}");
		return sb.toString();
	}
}
