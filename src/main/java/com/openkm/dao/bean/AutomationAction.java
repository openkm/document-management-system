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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "OKM_AUTO_ACTION")
public class AutomationAction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "AAC_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "AAC_CLASS_NAME", length = 255)
	private String className;
	
	@Column(name = "AAC_ORDER")
	private int order;

	@ElementCollection
	@Column(name = "AAP_PARAM")
	@OrderColumn(name = "AAP_ORDER")
	@CollectionTable(name = "OKM_AUTO_ACTION_PARAMS", joinColumns = {@JoinColumn(name = "AAP_VALIDATION")})
	@Lob
	@Type(type = "org.hibernate.type.StringClobType")
	private List<String> params = new ArrayList<String>();

	@Column(name = "AAC_ACTIVE", nullable = false)
	@Type(type = "true_false")
	private Boolean active = Boolean.FALSE;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}

	public Boolean getActive() {
		if (active == null) {
			return false;
		}
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=").append(id);
		sb.append(", className=").append(className);
		sb.append(", order=").append(order);
		sb.append(", params=").append(params);
		sb.append(", active=").append(active);
		sb.append("}");
		return sb.toString();
	}
}
