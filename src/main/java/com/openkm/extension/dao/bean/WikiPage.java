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

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Entity
@Table(name = "OKM_WIKI_PAGE")
public class WikiPage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "WKP_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "WKP_DATE")
	private Calendar date;

	@Column(name = "WKP_TITLE", length = 256)
	private String title;

	@Column(name = "WKP_USER", length = 64)
	private String user;

	@Column(name = "WKP_LOCK_USER", length = 64)
	private String lockUser;

	@Column(name = "WKP_NODE", length = 64)
	private String node;

	@Column(name = "WKP_DELETED", nullable = false)
	@Type(type = "true_false")
	private boolean deleted;

	@Column(name = "WKP_CONTENT")
	@Lob
	@Type(type = "org.hibernate.type.StringClobType")
	private String content;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=");
		sb.append(id);
		sb.append(", title=");
		sb.append(title);
		sb.append(", user=");
		sb.append(user);
		sb.append(", node=");
		sb.append(node);
		sb.append(", deleted=");
		sb.append(deleted);
		sb.append(", lockUser=");
		sb.append(lockUser);
		sb.append(", date=");
		sb.append(date == null ? null : date.getTime());
		sb.append(", content=");
		sb.append(content);
		sb.append("}");
		return sb.toString();
	}
}
