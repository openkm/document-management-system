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

package com.openkm.frontend.client.bean;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Collection;
import java.util.Date;

public class GWTToken implements IsSerializable {
	private double id;
	private String name;
	private boolean suspended;
	private GWTToken parent;
	private String node;
	private Date start;
	private Date end;
	private Collection<GWTTransition> availableTransitions;
	private Collection<GWTWorkflowComment> comments;
	private GWTProcessInstance processInstance;

	public double getId() {
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

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	public Collection<GWTWorkflowComment> getComments() {
		return comments;
	}

	public void setComments(Collection<GWTWorkflowComment> comments2) {
		this.comments = comments2;
	}

	public GWTToken getParent() {
		return parent;
	}

	public void setParent(GWTToken parent) {
		this.parent = parent;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Collection<GWTTransition> getAvailableTransitions() {
		return availableTransitions;
	}

	public void setAvailableTransitions(Collection<GWTTransition> availableTransitions) {
		this.availableTransitions = availableTransitions;
	}

	public GWTProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(GWTProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("id=");
		sb.append(id);
		sb.append(", name=");
		sb.append(name);
		sb.append(", node=");
		sb.append(node);
		sb.append(", availableTransitions=");
		sb.append(availableTransitions);
		sb.append(", suspended=");
		sb.append(suspended);
		sb.append(", start=");
		sb.append(start == null ? null : start.getTime());
		sb.append(", end=");
		sb.append(end == null ? null : end.getTime());
		sb.append(", parent=");
		sb.append(parent);
		sb.append(", comments=");
		sb.append(comments);
		sb.append(", processInstance=");
		sb.append(processInstance);
		sb.append("]");
		return sb.toString();
	}
}
