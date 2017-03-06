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

@XmlRootElement(name = "note")
public class Note implements Serializable {
	private static final long serialVersionUID = 913105621262127904L;

	public static final String TYPE = "okm:note";
	public static final String LIST = "okm:notes";
	public static final String LIST_TYPE = "okm:notes";
	public static final String MIX_TYPE = "mix:notes";
	public static final String DATE = "okm:date";
	public static final String USER = "okm:user";
	public static final String TEXT = "okm:text";

	private Calendar date;
	private String author;
	private String text;
	private String path;

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("date=");
		sb.append(date == null ? null : date.getTime());
		sb.append(", author=");
		sb.append(author);
		sb.append(", text=");
		sb.append(text);
		sb.append(", path=");
		sb.append(path);
		sb.append("}");
		return sb.toString();
	}
}
