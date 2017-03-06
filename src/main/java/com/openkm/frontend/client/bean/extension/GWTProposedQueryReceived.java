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

package com.openkm.frontend.client.bean.extension;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.openkm.frontend.client.bean.GWTQueryParams;

import java.util.Date;

public class GWTProposedQueryReceived implements IsSerializable {
	private long id;
	private String from;
	private String to;
	private String user;
	private String comment;
	private boolean accepted;
	private Date seenDate;
	private Date sentDate;
	private GWTQueryParams params;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getComment() {
		return comment;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public Date getSeenDate() {
		return seenDate;
	}

	public void setSeenDate(Date seenDate) {
		this.seenDate = seenDate;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	public GWTQueryParams getParams() {
		return params;
	}

	public void setParams(GWTQueryParams params) {
		this.params = params;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=");
		sb.append(id);
		sb.append(", from=");
		sb.append(from);
		sb.append(", to=");
		sb.append(to);
		sb.append(", user=");
		sb.append(user);
		sb.append(", accepted=");
		sb.append(accepted);
		sb.append(", seenDate=");
		sb.append(seenDate == null ? null : seenDate.getTime());
		sb.append(", sentDate=");
		sb.append(sentDate == null ? null : sentDate.getTime());
		sb.append(", params=");
		sb.append(params);
		sb.append("}");
		return sb.toString();
	}
}
