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

import java.io.Serializable;
import java.util.Calendar;

public class ActivityFilter implements Serializable {
	private static final long serialVersionUID = 1L;
	private Calendar begin;
	private Calendar end;
	private String user;
	private String action;
	private String item;

	public Calendar getBegin() {
		return begin;
	}

	public void setBegin(Calendar begin) {
		this.begin = begin;
	}

	public Calendar getEnd() {
		return end;
	}

	public void setEnd(Calendar end) {
		this.end = end;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("begin=");
		sb.append(begin == null ? null : begin.getTime());
		sb.append(", end=");
		sb.append(end == null ? null : end.getTime());
		sb.append(", user=");
		sb.append(user);
		sb.append(", action=");
		sb.append(action);
		sb.append(", item=");
		sb.append(item);
		sb.append("}");
		return sb.toString();
	}
}
