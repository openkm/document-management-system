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

package com.openkm.bean;

import javax.xml.bind.annotation.XmlSeeAlso;
import java.io.Serializable;
import java.util.*;

/**
 * @author pavila
 *
 */
@XmlSeeAlso({Document.class, Folder.class, Mail.class})
public class Node implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String AUTHOR = "okm:author";
	public static final String NAME = "okm:name";

	protected Calendar created;
	protected String path;
	protected String author;
	protected int permissions;
	protected String uuid;
	protected boolean subscribed;
	protected Set<String> subscriptors = new HashSet<String>();
	protected Set<String> keywords = new HashSet<String>();
	protected Set<Folder> categories = new HashSet<Folder>();
	protected List<Note> notes = new ArrayList<Note>();

	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getPermissions() {
		return permissions;
	}

	public void setPermissions(int permissions) {
		this.permissions = permissions;
	}

	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}

	public Set<String> getSubscriptors() {
		return subscriptors;
	}

	public void setSubscriptors(Set<String> subscriptors) {
		this.subscriptors = subscriptors;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public List<Note> getNotes() {
		return notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}

	public Set<Folder> getCategories() {
		return categories;
	}

	public void setCategories(Set<Folder> categories) {
		this.categories = categories;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("path=").append(path);
		sb.append(", permissions=").append(permissions);
		sb.append(", created=").append(created == null ? null : created.getTime());
		sb.append(", subscribed=").append(subscribed);
		sb.append(", subscriptors=").append(subscriptors);
		sb.append(", uuid=").append(uuid);
		sb.append(", keywords=").append(keywords);
		sb.append(", categories=").append(categories);
		sb.append(", notes=").append(notes);
		sb.append("}");
		return sb.toString();
	}
}
