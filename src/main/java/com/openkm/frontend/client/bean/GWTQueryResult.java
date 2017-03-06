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
 * GWTQueryResult
 *
 * @author jllort
 *
 */
public class GWTQueryResult implements IsSerializable {
	private GWTDocument document;
	private GWTFolder folder;
	private GWTMail mail;
	private GWTDocument attachment;
	private String excerpt;

	private long score;

	public GWTDocument getDocument() {
		return document;
	}

	public void setDocument(GWTDocument document) {
		this.document = document;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
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

	public GWTDocument getAttachment() {
		return attachment;
	}

	public void setAttachment(GWTDocument attachment) {
		this.attachment = attachment;
	}

	public String getExcerpt() {
		return excerpt;
	}

	public void setExcerpt(String excerpt) {
		this.excerpt = excerpt;
	}
}