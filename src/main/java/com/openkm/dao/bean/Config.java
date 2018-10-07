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

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "OKM_CONFIG")
public class Config implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String STRING = "string";
	public static final String TEXT = "text";
	public static final String BOOLEAN = "boolean";
	public static final String INTEGER = "integer";
	public static final String LONG = "long";
	public static final String FILE = "file";
	public static final String SELECT = "select";
	public static final String HIDDEN = "hidden";
	public static final String LIST = "list";
	public static final String HTML = "html";

	@Id
	@Column(name = "CFG_KEY", length = 64)
	private String key;

	@Column(name = "CFG_TYPE", length = 32)
	private String type;

	@Column(name = "CFG_VALUE")
	@Lob
	@Type(type = "org.hibernate.type.StringClobType")
	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("key=");
		sb.append(key);
		sb.append(", type=");
		sb.append(type);
		sb.append(", value=");
		sb.append(value);
		sb.append("}");
		return sb.toString();
	}
}
