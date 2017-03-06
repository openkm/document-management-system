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
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;

/**
 * GWTStapling
 *
 * @author jllort
 *
 */
public class GWTStaple implements IsSerializable {
	public static final String STAPLE_DOCUMENT = "okm:document";
	public static final String STAPLE_FOLDER = "okm:folder";
	public static final String STAPLE_MAIL = "okm:mail";

	private long id;
	private GWTFolder folder;
	private GWTMail mail;
	private GWTDocument doc;
	private String type;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public GWTFolder getFolder() {
		return folder;
	}

	public void setFolder(GWTFolder folder) {
		this.folder = folder;
	}

	public GWTMail getMail() {
		return mail;
	}

	public void setMail(GWTMail mail) {
		this.mail = mail;
	}

	public GWTDocument getDoc() {
		return doc;
	}

	public void setDoc(GWTDocument doc) {
		this.doc = doc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}