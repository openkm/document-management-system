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

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author agallego
 */
@Entity
@Table(name = "OKM_PLUGIN")
public class Plugin implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "PLG_CLASS")
	private String className;

	@Column(name = "PLG_ACTIVE")
	@Type(type = "true_false")
	private Boolean active = Boolean.FALSE;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Boolean getActive() {
		if (active == null) {
			return false;
		}
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("className=").append(className);
		sb.append(", active=").append(active);
		sb.append("}");
		return sb.toString();
	}
}
