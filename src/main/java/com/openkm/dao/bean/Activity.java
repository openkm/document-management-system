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

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Entity
@Table(name = "OKM_ACTIVITY")
@org.hibernate.annotations.Table(appliesTo = "OKM_ACTIVITY",
		indexes = {
				// CREATE INDEX IDX_ACTIVITY_DATACT ON OKM_ACTIVITY(ACT_DATE, ACT_ACTION);
				@Index(name = "IDX_ACTIVITY_DATACT", columnNames = {"ACT_DATE", "ACT_ACTION"}),
				// CREATE INDEX IDX_ACTIVITY_USRACT ON OKM_ACTIVITY(ACT_USER, ACT_ACTION);
				@Index(name = "IDX_ACTIVITY_USRACT", columnNames = {"ACT_USER", "ACT_ACTION"})
		}
)
public class Activity implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int MAX_LENGTH = 4000;

	@Id
	@Column(name = "ACT_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "ACT_DATE")
	// CREATE INDEX IDX_ACTIVITY_DATE ON OKM_ACTIVITY(ACT_DATE);
	@Index(name = "IDX_ACTIVITY_DATE")
	private Calendar date;

	@Column(name = "ACT_USER", length = 64)
	private String user;

	@Column(name = "ACT_ACTION", length = 127)
	private String action;

	@Column(name = "ACT_ITEM", length = 64)
	private String item;

	@Column(name = "ACT_PATH")
	@Lob
	@Type(type = "org.hibernate.type.StringClobType")
	private String path;

	@Column(name = "ACT_PARAMS", length = MAX_LENGTH)
	// Changed to VARCHAR(4000) for Oracle compatibility
	// @Lob @Type(type = "org.hibernate.type.StringClobType")
	private String params;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		if (params != null && params.length() > MAX_LENGTH) {
			this.params = params.substring(0, MAX_LENGTH);
		} else {
			this.params = params;
		}
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=");
		sb.append(id);
		sb.append(", date=");
		sb.append(date == null ? null : date.getTime());
		sb.append(", user=");
		sb.append(user);
		sb.append(", action=");
		sb.append(action);
		sb.append(", item=");
		sb.append(item);
		sb.append(", path=");
		sb.append(path);
		sb.append(", params=");
		sb.append(params);
		sb.append("}");
		return sb.toString();
	}
}
