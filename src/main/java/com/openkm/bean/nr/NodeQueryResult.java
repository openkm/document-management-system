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

package com.openkm.bean.nr;

import com.openkm.dao.bean.NodeDocument;
import com.openkm.dao.bean.NodeFolder;
import com.openkm.dao.bean.NodeMail;

import java.io.Serializable;

public class NodeQueryResult implements Serializable {
	private static final long serialVersionUID = -1L;
	private NodeDocument document;
	private NodeFolder folder;
	private NodeMail mail;
	private NodeDocument attachment;
	private String excerpt;
	private float score;

	public NodeDocument getDocument() {
		return document;
	}

	public void setDocument(NodeDocument document) {
		this.document = document;
	}

	public NodeFolder getFolder() {
		return folder;
	}

	public void setFolder(NodeFolder folder) {
		this.folder = folder;
	}

	public NodeMail getMail() {
		return mail;
	}

	public void setMail(NodeMail mail) {
		this.mail = mail;
	}

	public NodeDocument getAttachment() {
		return attachment;
	}

	public void setAttachment(NodeDocument attachment) {
		this.attachment = attachment;
	}

	public String getExcerpt() {
		return excerpt;
	}

	public void setExcerpt(String excerpt) {
		this.excerpt = excerpt;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("document=");
		sb.append(document);
		sb.append(", folder=");
		sb.append(folder);
		sb.append(", mail=");
		sb.append(mail);
		sb.append(", attachment=");
		sb.append(attachment);
		sb.append(", excerpt=");
		sb.append(excerpt);
		sb.append(", score=");
		sb.append(score);
		sb.append("}");
		return sb.toString();
	}
}
