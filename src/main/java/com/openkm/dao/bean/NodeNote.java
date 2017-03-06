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

package com.openkm.dao.bean;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Entity
@Table(name = "OKM_NODE_NOTE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NodeNote implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "NNT_UUID", length = 64)
	private String uuid;

	@Column(name = "NNT_PARENT", length = 64)
	private String parent;

	@Column(name = "NNT_AUTHOR", length = 64)
	private String author;

	@Column(name = "NNT_CREATED")
	protected Calendar created;

	@Column(name = "NNT_TEXT")
	@Lob
	@Type(type = "org.hibernate.type.StringClobType")
	private String text;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("uuid=").append(uuid);
		sb.append(", parent=").append(parent);
		sb.append(", author=").append(author);
		sb.append(", created=").append(created == null ? null : created.getTime());
		sb.append(", text=").append(text);
		sb.append("}");
		return sb.toString();
	}
}
