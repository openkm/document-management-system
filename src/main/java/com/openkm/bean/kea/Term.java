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

package com.openkm.bean.kea;

import java.io.Serializable;

/**
 * Term
 *
 * @author jllort
 *
 */
public class Term implements Serializable {

	private static final long serialVersionUID = 290660580424913769L;

	private String text;
	private String uid;

	/**
	 * Term
	 */
	public Term() {
	}

	/**
	 * Term
	 * @param text
	 * @param uid
	 */
	public Term(String uid, String text) {
		this.uid = uid;
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if ((o == null) || !(o instanceof Term)) return false;

		Term term = (Term) o;

		if (uid != null ? !uid.equals(term.uid) : term.uid != null) return false;
		if (text != null ? !text.equals(term.text) : term.text != null) return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = (text != null ? text.hashCode() : 0);
		result = 31 * result + (uid != null ? uid.hashCode() : 0);
		return result;
	}
}
