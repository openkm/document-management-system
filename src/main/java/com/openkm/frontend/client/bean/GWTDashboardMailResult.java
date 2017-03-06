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

import java.util.Date;

/**
 * @author jllort
 *
 */
public class GWTDashboardMailResult implements IsSerializable {
	private GWTMail mail;
	private boolean visited;
	private Date date;

	public GWTMail getMail() {
		return mail;
	}

	public void setMail(GWTMail mail) {
		this.mail = mail;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("GWTMail=");
		sb.append(mail);
		sb.append(", visited=");
		sb.append(visited);
		sb.append(", date=");
		sb.append(date);
		sb.append("]");
		return sb.toString();
	}
}
