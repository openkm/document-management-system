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
@Table(name = "OKM_PROFILING")
@org.hibernate.annotations.Table(appliesTo = "OKM_PROFILING",
		indexes = {
				// CREATE INDEX IDX_PROFILING_DATCLS ON OKM_PROFILING(PRL_DATE, PRL_CLAZZ);
				@Index(name = "IDX_PROFILING_DATCLS", columnNames = {"PRL_DATE", "PRL_CLAZZ"}),
				// CREATE INDEX IDX_PROFILING_USRCLS ON OKM_PROFILING(PRL_USER, PRL_CLAZZ);
				@Index(name = "IDX_PROFILING_USRCLS", columnNames = {"PRL_USER", "PRL_CLAZZ"})
		}
)
public class Profiling implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int MAX_LENGTH = 4000;

	@Id
	@Column(name = "PRL_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "PRL_DATE")
	// CREATE INDEX IDX_PROFILING_DATE ON OKM_PROFILING(PRL_DATE);
	@Index(name = "IDX_PROFILING_DATE")
	private Calendar date;

	@Column(name = "PRL_USER", length = 64)
	private String user;

	@Column(name = "PRL_CLAZZ", length = 127)
	private String clazz;

	@Column(name = "PRL_METHOD", length = 127)
	private String method;

	@Column(name = "PRL_PARAMS", length = MAX_LENGTH)
	// Changed to VARCHAR(4000) for Oracle compatibility
	// @Lob @Type(type = "org.hibernate.type.StringClobType")
	private String params;

	@Column(name = "PRL_TIME")
	private Long time;

	@Column(name = "PRL_TRACE")
	@Lob
	@Type(type = "org.hibernate.type.StringClobType")
	private String trace;

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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
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

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getTrace() {
		return trace;
	}

	public void setTrace(String trace) {
		this.trace = trace;
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
		sb.append(", clazz=");
		sb.append(clazz);
		sb.append(", method=");
		sb.append(method);
		sb.append(", params=");
		sb.append(params);
		sb.append(", time=");
		sb.append(time);
		sb.append(", trace=");
		sb.append(trace);
		sb.append("}");
		return sb.toString();
	}
}
