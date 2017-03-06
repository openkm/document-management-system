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

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Entity
@Table(name = "OKM_DASHBOARD_ACTIVITY")
@org.hibernate.annotations.Table(appliesTo = "OKM_DASHBOARD_ACTIVITY",
		indexes = {
				// CREATE INDEX IDX_DASH_ACTI_DATACT ON OKM_DASHBOARD_ACTIVITY(DAC_DATE, DAC_ACTION);
				@Index(name = "IDX_DASH_ACTI_DATACT", columnNames = {"DAC_DATE", "DAC_ACTION"}),
				// CREATE INDEX IDX_DASH_ACTI_USRACT ON OKM_DASHBOARD_ACTIVITY(DAC_USER, DAC_ACTION);
				@Index(name = "IDX_DASH_ACTI_USRACT", columnNames = {"DAC_USER", "DAC_ACTION"})
		}
)
public class DashboardActivity implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int MAX_LENGTH = 4000;

	@Id
	@Column(name = "DAC_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "DAC_DATE")
	// CREATE INDEX IDX_DASH_ACTI_DATE ON OKM_DASHBOARD_ACTIVITY(DAC_DATE);
	@Index(name = "IDX_DASH_ACTI_DATE")
	private Calendar date;

	@Column(name = "DAC_USER", length = 64)
	private String user;

	@Column(name = "DAC_ACTION", length = 127)
	private String action;

	@Column(name = "DAC_ITEM", length = 64)
	private String item;

	@Column(name = "DAC_PATH", length = MAX_LENGTH)
	// Changed to VARCHAR(4000) for Oracle compatibility
	// @Lob @Type(type = "org.hibernate.type.StringClobType")
	private String path;

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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if (path != null && path.length() > MAX_LENGTH) {
			this.path = path.substring(0, MAX_LENGTH);
		} else {
			this.path = path;
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=").append(id);
		sb.append(", date=").append(date == null ? null : date.getTime());
		sb.append(", user=").append(user);
		sb.append(", action=").append(action);
		sb.append(", item=").append(item);
		sb.append(", path=").append(path);
		sb.append("}");
		return sb.toString();
	}
}
