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
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author pavila
 */
@XmlRootElement(name = "taskInstance")
public class TaskInstance implements Serializable, Comparable<TaskInstance> {
	private static final long serialVersionUID = -2927629831091624036L;

	private long id;
	private Calendar start;
	private Calendar end;
	private Calendar dueDate;
	private String name;
	private String description;
	private String actorId;
	private Set<String> pooledActors;
	private Calendar create;
	private boolean open;
	private boolean last;
	private boolean suspended;
	private boolean startTaskInstance;
	private List<Comment> comments;
	private Token token;
	private List<Transition> availableTransitions;
	private Map<String, Object> variables;
	private ProcessInstance processInstance;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Calendar getCreate() {
		return create;
	}

	public void setCreate(Calendar create) {
		this.create = create;
	}

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
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

	public Calendar getDueDate() {
		return dueDate;
	}

	public void setDueDate(Calendar dueDate) {
		this.dueDate = dueDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public boolean isStartTaskInstance() {
		return startTaskInstance;
	}

	public void setStartTaskInstance(boolean startTaskInstance) {
		this.startTaskInstance = startTaskInstance;
	}

	public Set<String> getPooledActors() {
		return pooledActors;
	}

	public void setPooledActors(Set<String> pooledActors) {
		this.pooledActors = pooledActors;
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	public int compareTo(TaskInstance arg0) {
		return new Long(this.id).compareTo(arg0.id);
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	public List<Transition> getAvailableTransitions() {
		return availableTransitions;
	}

	public void setAvailableTransitions(List<Transition> availableTransitions) {
		this.availableTransitions = availableTransitions;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("name=");
		sb.append(name);
		sb.append(", description=");
		sb.append(description);
		sb.append(", id=");
		sb.append(id);
		sb.append(", actorId=");
		sb.append(actorId);
		sb.append(", pooledActors=");
		sb.append(pooledActors);
		sb.append(", open=");
		sb.append(open);
		sb.append(", last=");
		sb.append(last);
		sb.append(", suspended=");
		sb.append(suspended);
		sb.append(", startTaskInstance=");
		sb.append(startTaskInstance);
		sb.append(", create=");
		sb.append(create == null ? null : create.getTime());
		sb.append(", dueDate=");
		sb.append(dueDate == null ? null : dueDate.getTime());
		sb.append(", start=");
		sb.append(start == null ? null : start.getTime());
		sb.append(", end=");
		sb.append(end == null ? null : end.getTime());
		sb.append(", comments=");
		sb.append(comments);
		sb.append(", token=");
		sb.append(token);
		sb.append(", availableTransitions=");
		sb.append(availableTransitions);
		sb.append(", variables=");
		sb.append(variables);
		sb.append(", processInstance=");
		sb.append(processInstance);
		sb.append("]");
		return sb.toString();
	}
}
