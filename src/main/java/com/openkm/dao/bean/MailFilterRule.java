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

public class MailFilterRule implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIELD_FROM = "FROM";
	public static final String FIELD_TO = "TO";
	public static final String FIELD_SUBJECT = "SUBJECT";
	public static final String FIELD_CONTENT = "CONTENT";
	public static final String FIELD_ATTACHMENT = "ATTACHMENT";

	public static final String OPERATION_EQUALS = "EQUALS";
	public static final String OPERATION_NOT_EQUALS = "NOT_EQUALS";
	public static final String OPERATION_CONTAINS = "CONTAINS";
	public static final String OPERATION_ENDS_WITH = "ENDS_WITH";
	public static final String OPERATION_STARTS_WITH = "STARTS_WITH";

	private long id;
	private String field;
	private String operation;
	private String value;
	private boolean active;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=");
		sb.append(id);
		sb.append(", field=");
		sb.append(field);
		sb.append(", operation=");
		sb.append(operation);
		sb.append(", value=");
		sb.append(value);
		sb.append(", active=");
		sb.append(active);
		sb.append("}");
		return sb.toString();
	}
}
