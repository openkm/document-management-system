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

/**
 * ForumTopic
 *
 * @author pavila
 */
public class ForumTopic implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String title;
	private Calendar date;
	private String user;
	private String node;
	private int replies;
	private int views;
	private String lastPostUser;
	private Calendar lastPostDate;
	private Set<ForumPost> posts = new LinkedHashSet<ForumPost>();

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

	public int getReplies() {
		return replies;
	}

	public void setReplies(int replies) {
		this.replies = replies;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Set<ForumPost> getPosts() {
		return posts;
	}

	public void setPosts(Set<ForumPost> posts) {
		this.posts = posts;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=");
		sb.append(id);
		sb.append(", title=");
		sb.append(title);
		sb.append(", date=");
		sb.append(date == null ? null : date.getTime());
		sb.append(", user=");
		sb.append(user);
		sb.append(", node=");
		sb.append(node);
		sb.append(", replies=");
		sb.append(replies);
		sb.append(", views=");
		sb.append(views);
		sb.append(", lastPostUser=");
		sb.append(lastPostUser);
		sb.append(", lastPostDate=");
		sb.append(lastPostDate);
		sb.append(", posts=");
		sb.append(posts);
		sb.append("}");
		return sb.toString();
	}
}
