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

package com.openkm.extension.dao.bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

public class Forum implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String name;
	private String description;
	private Calendar date;
	private String lastPostUser;
	private Calendar lastPostDate;
	private int numTopics;
	private int numPosts;
	private boolean active;
	private Set<ForumTopic> topics = new LinkedHashSet<ForumTopic>();

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
		if (name != null && name.length() > 255) {
			this.name = name.substring(0, 255);
		} else {
			this.name = name;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Set<ForumTopic> getTopics() {
		return topics;
	}

	public void setTopics(Set<ForumTopic> topics) {
		this.topics = topics;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public String getLastPostUser() {
		return lastPostUser;
	}

	public void setLastPostUser(String lastPostUser) {
		this.lastPostUser = lastPostUser;
	}

	public Calendar getLastPostDate() {
		return lastPostDate;
	}

	public void setLastPostDate(Calendar lastPostDate) {
		this.lastPostDate = lastPostDate;
	}

	public int getNumTopics() {
		return numTopics;
	}

	public void setNumTopics(int numTopics) {
		this.numTopics = numTopics;
	}

	public int getNumPosts() {
		return numPosts;
	}

	public void setNumPosts(int numPosts) {
		this.numPosts = numPosts;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=");
		sb.append(id);
		sb.append(", name=");
		sb.append(name);
		sb.append(", description=");
		sb.append(description);
		sb.append(", date=");
		sb.append(date == null ? null : date.getTime());
		sb.append(", lastPostUser=");
		sb.append(lastPostUser);
		sb.append(", lastPostDate=");
		sb.append(lastPostDate);
		sb.append(", numTopics=");
		sb.append(numTopics);
		sb.append(", numPosts=");
		sb.append(numPosts);
		sb.append(", active=");
		sb.append(active);
		sb.append(", topics=");
		sb.append(topics);
		sb.append("}");
		return sb.toString();
	}
}
