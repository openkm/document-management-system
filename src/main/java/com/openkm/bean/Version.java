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

package com.openkm.bean;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Calendar;

@XmlRootElement(name = "version")
public class Version implements Serializable {
	private static final long serialVersionUID = 3219672583638952409L;

	private String name;
	private Calendar created;
	private long size;
	private String author;
	private boolean actual;
	private String comment;
	private String checksum;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public boolean isActual() {
		return actual;
	}

	public void setActual(boolean actual) {
		this.actual = actual;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("name=");
		sb.append(name);
		sb.append(", size=");
		sb.append(size);
		sb.append(", author=");
		sb.append(author);
		sb.append(", actual=");
		sb.append(actual);
		sb.append(", checksum=");
		sb.append(checksum);
		sb.append(", comment=");
		sb.append(comment);
		sb.append(", created=");
		sb.append(created == null ? null : created.getTime());
		sb.append("}");
		return sb.toString();
	}
}
