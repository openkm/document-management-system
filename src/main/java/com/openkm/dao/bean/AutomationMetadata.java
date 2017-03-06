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

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "OKM_AUTO_METADATA", uniqueConstraints = {
		// ALTER TABLE OKM_AUTO_METADATA ADD CONSTRAINT IDX_AUTO_MD_ATCLS UNIQUE (AMD_AT, AMD_CLASS_NAME)
		@UniqueConstraint(name = "IDX_AUTO_MD_ATCLS", columnNames = {"AMD_AT", "AMD_CLASS_NAME"})})
public class AutomationMetadata implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String GROUP_ACTION = "action";
	public static final String GROUP_VALIDATION = "validation";

	public static final String TYPE_TEXT = "text";
	public static final String TYPE_INTEGER = "integer";
	public static final String TYPE_BOOLEAN = "boolean";
	public static final String TYPE_TEXTAREA = "textarea";

	public static final String AT_PRE = "pre";
	public static final String AT_POST = "post";

	public static final String SOURCE_FOLDER = "okm:folder";

	@Id
	@Column(name = "AMD_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "AMD_NAME", length = 255)
	private String name;

	@Column(name = "AMD_GROUP", length = 255)
	private String group;

	@Column(name = "AMD_CLASS_NAME", length = 255)
	private String className;

	@Column(name = "AMD_AT", length = 32)
	private String at;

	@Transient
	private boolean active;

	@Column(name = "AMD_TYPE00", length = 32)
	private String type00;

	@Column(name = "AMD_TYPE01", length = 32)
	private String type01;

	@Column(name = "AMD_SRC00", length = 32)
	private String source00;

	@Column(name = "AMD_SRC01", length = 32)
	private String source01;

	@Column(name = "AMD_DESC00", length = 32)
	private String description00;

	@Column(name = "AMD_DESC01", length = 32)
	private String description01;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getAt() {
		return at;
	}

	public void setAt(String at) {
		this.at = at;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getType00() {
		return type00;
	}

	public void setType00(String type00) {
		this.type00 = type00;
	}

	public String getType01() {
		return type01;
	}

	public void setType01(String type01) {
		this.type01 = type01;
	}

	public String getSource00() {
		return source00;
	}

	public void setSource00(String source00) {
		this.source00 = source00;
	}

	public String getSource01() {
		return source01;
	}

	public void setSource01(String source01) {
		this.source01 = source01;
	}

	public String getDescription00() {
		return description00;
	}

	public void setDescription00(String description00) {
		this.description00 = description00;
	}

	public String getDescription01() {
		return description01;
	}

	public void setDescription01(String description01) {
		this.description01 = description01;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=").append(id);
		sb.append(", name=").append(name);
		sb.append(", group=").append(group);
		sb.append(", className=").append(className);
		sb.append(", at=").append(at);
		sb.append(", type00=").append(type00);
		sb.append(", type01=").append(type01);
		sb.append(", source00=").append(source00);
		sb.append(", source01=").append(source01);
		sb.append(", description00=").append(description00);
		sb.append(", description01=").append(description01);
		sb.append("}");
		return sb.toString();
	}
}
