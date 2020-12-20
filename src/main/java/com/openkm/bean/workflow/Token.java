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

package com.openkm.bean.workflow;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author pavila
 */
@XmlRootElement(name = "token")
public class Token implements Serializable {
	private static final long serialVersionUID = 9113136516768190724L;

	private long id;
	private String name;
	private boolean suspended;
	private Token parent;
	private String node;
	private Calendar start;
	private Calendar end;
	private List<Transition> availableTransitions;
	private List<Comment> comments;
	private ProcessInstance processInstance;

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

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public Token getParent() {
		return parent;
	}

	public void setParent(Token parent) {
		this.parent = parent;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public Calendar getStart() {
		return start;
	}

	public void setStart(Calendar start) {
		this.start = start;
	}

	public Calendar getEnd() {
		return end;
	}

	public void setEnd(Calendar end) {
		this.end = end;
	}

	public List<Transition> getAvailableTransitions() {
		return availableTransitions;
	}

	public void setAvailableTransitions(List<Transition> availableTransitions) {
		this.availableTransitions = availableTransitions;
	}

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
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
