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

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Entity
@Table(name = "OKM_PENDING_TASK")
@org.hibernate.annotations.Table(appliesTo = "OKM_PENDING_TASK",
		indexes = {
				// CREATE INDEX IDX_PENDING_NODETASK ON OKM_PENDING_TASK(PTK_NODE, PTK_TASK);
				@Index(name = "IDX_PENDING_NODETASK", columnNames = {"PTK_NODE", "PTK_TASK"})
		}
)
public class PendingTask implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String TASK_TEXT_EXTRACTION = "text_extraction";
	public static final String TASK_CHANGE_SECURITY = "change_security";
	public static final String TASK_UPDATE_PATH = "update_path";

	@Id
	@Column(name = "PTK_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "PTK_NODE", length = 64)
	private String node;

	@Column(name = "PTK_TASK", length = 32)
	private String task;

	@Column(name = "PTK_PARAMS", length = 2048)
	private String params;

	@Column(name = "PTK_CREATED")
	private Calendar created;

	@Column(name = "PTK_STATUS")
	@Lob
	@Type(type = "org.hibernate.type.StringClobType")
	private String status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("node=").append(node);
		sb.append(", task=").append(task);
		sb.append(", params=").append(params);
		sb.append(", created=").append(created == null ? null : created.getTime());
		sb.append(", status=").append(status);
		sb.append("}");
		return sb.toString();
	}
}
