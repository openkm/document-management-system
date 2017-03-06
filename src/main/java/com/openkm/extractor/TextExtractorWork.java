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

package com.openkm.extractor;

import java.util.Calendar;

/**
 * @author pavila
 */
public class TextExtractorWork {
	private String docUuid;
	private String docPath;
	private String docVerUuid;
	private Calendar date = Calendar.getInstance();

	public String getDocUuid() {
		return docUuid;
	}

	public void setDocUuid(String docUuid) {
		this.docUuid = docUuid;
	}

	public String getDocPath() {
		return docPath;
	}

	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}

	public String getDocVerUuid() {
		return docVerUuid;
	}

	public void setDocVerUuid(String docVerUuid) {
		this.docVerUuid = docVerUuid;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("docUuid=").append(docUuid);
		sb.append(", docPath=").append(docPath);
		sb.append(", docVerUuid=").append(docVerUuid);
		sb.append(", date=");
		sb.append(date == null ? null : date.getTime());
		sb.append("}");
		return sb.toString();
	}
}
