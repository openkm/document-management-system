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

package com.openkm.util.impexp;

public class ImpExpStats {
	private boolean ok = true;
	private long documents;
	private long folders;
	private long mails;
	private long size;

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public long getDocuments() {
		return documents;
	}

	public void setDocuments(long documents) {
		this.documents = documents;
	}

	public long getFolders() {
		return folders;
	}

	public void setFolders(long folders) {
		this.folders = folders;
	}

	public long getMails() {
		return mails;
	}

	public void setMails(long mails) {
		this.mails = mails;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("documents=");
		sb.append(documents);
		sb.append(", folders=");
		sb.append(folders);
		sb.append(", mails=");
		sb.append(mails);
		sb.append(", size=");
		sb.append(size);
		sb.append(", ok=");
		sb.append(ok);
		sb.append("}");
		return sb.toString();
	}
}
