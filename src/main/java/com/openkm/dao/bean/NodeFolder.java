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
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Indexed
@Table(name = "OKM_NODE_FOLDER")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NodeFolder extends NodeBase {
	private static final long serialVersionUID = 1L;

	@Column(name = "NFL_DESCRIPTION", length = 2048)
	@Field(index = Index.TOKENIZED, store = Store.YES)
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("uuid=").append(uuid);
		sb.append(", context=").append(context);
		sb.append(", path=").append(path);
		sb.append(", parent=").append(parent);
		sb.append(", author=").append(author);
		sb.append(", name=").append(name);
		sb.append(", description=").append(description);
		sb.append(", subscriptors=").append(subscriptors);
		sb.append(", keywords=").append(keywords);
		sb.append(", categories=").append(categories);
		// sb.append(", properties=").append(properties); Prevents Lazy Exception
		sb.append(", userPermissions=").append(userPermissions);
		sb.append(", rolePermissions=").append(rolePermissions);
		sb.append("}");
		return sb.toString();
	}
}
