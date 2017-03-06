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

/**
 * @author jllort
 *
 */
public class GWTTerm implements IsSerializable {

	private String text;
	private String id;
	private String comment = "";
	private String prefTermID = null;
	private boolean isLeaf = false;

	/**
	 * GWTTerm
	 */
	public GWTTerm() {
	}

	/**
	 * GWTTerm
	 *
	 * @param text
	 * @param id
	 */
	public GWTTerm(String text, String id) {
		this.text = text;
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getPrefTermID() {
		if (prefTermID == null) return "";
		return prefTermID;
	}

	public void setPrefTermID(String prefTermID) {
		this.prefTermID = prefTermID;
	}

	public boolean isPreferred() {
		return prefTermID == null ? true : false;
	}


	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean leaf) {
		isLeaf = leaf;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if ((o == null) || !(o instanceof GWTTerm)) return false;

		GWTTerm term = (GWTTerm) o;

		if (comment != null ? !comment.equals(term.comment) : term.comment != null) return false;
		if (id != null ? !id.equals(term.id) : term.id != null) return false;
		if (text != null ? !text.equals(term.text) : term.text != null) return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = (text != null ? text.hashCode() : 0);
		result = 31 * result + (id != null ? id.hashCode() : 0);
		result = 31 * result + (comment != null ? comment.hashCode() : 0);
		return result;
	}
}
