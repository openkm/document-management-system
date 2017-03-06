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
import com.openkm.frontend.client.bean.form.GWTFormElement;

import java.util.*;

/**
 * GWTMail
 *
 * @author jllort
 *
 */
public class GWTMail implements IsSerializable {
	private String path;
	private String uuid;
	private int permissions;
	private String from;
	private String[] reply = new String[]{};
	private String[] to;
	private String[] cc;
	private String[] bcc;
	private Date sentDate;
	private Date receivedDate;
	private String subject;
	private String content;
	private long size;
	private Collection<GWTDocument> attachments;
	private String parentPath;
	private String mimeType;
	private boolean hasNotes = false;
	private List<GWTNote> notes;
	private Set<GWTFolder> categories;
	private Set<String> keywords = new HashSet<String>();
	private String author;
	private Date created;

	// Extra columns
	private GWTFormElement column0;
	private GWTFormElement column1;
	private GWTFormElement column2;
	private GWTFormElement column3;
	private GWTFormElement column4;
	private GWTFormElement column5;
	private GWTFormElement column6;
	private GWTFormElement column7;
	private GWTFormElement column8;
	private GWTFormElement column9;

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getPermissions() {
		return permissions;
	}

	public void setPermissions(int permissions) {
		this.permissions = permissions;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String[] getReply() {
		return reply;
	}

	public void setReply(String[] reply) {
		this.reply = reply;
	}

	public String[] getTo() {
		return to;
	}

	public void setTo(String[] to) {
		this.to = to;
	}

	public String[] getCc() {
		return cc;
	}

	public void setCc(String[] cc) {
		this.cc = cc;
	}

	public String[] getBcc() {
		return bcc;
	}

	public void setBcc(String[] bcc) {
		this.bcc = bcc;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public Collection<GWTDocument> getAttachments() {
		return attachments;
	}

	public void setAttachments(Collection<GWTDocument> attachments) {
		this.attachments = attachments;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public List<GWTNote> getNotes() {
		return notes;
	}

	public void setNotes(List<GWTNote> notes) {
		this.notes = notes;
	}

	public boolean isHasNotes() {
		return hasNotes;
	}

	public void setHasNotes(boolean hasNotes) {
		this.hasNotes = hasNotes;
	}

	public Set<GWTFolder> getCategories() {
		return categories;
	}

	public void setCategories(Set<GWTFolder> categories) {
		this.categories = categories;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public GWTFormElement getColumn0() {
		return column0;
	}

	public void setColumn0(GWTFormElement column0) {
		this.column0 = column0;
	}

	public GWTFormElement getColumn1() {
		return column1;
	}

	public void setColumn1(GWTFormElement column1) {
		this.column1 = column1;
	}

	public GWTFormElement getColumn2() {
		return column2;
	}

	public void setColumn2(GWTFormElement column2) {
		this.column2 = column2;
	}

	public GWTFormElement getColumn3() {
		return column3;
	}

	public void setColumn3(GWTFormElement column3) {
		this.column3 = column3;
	}

	public GWTFormElement getColumn4() {
		return column4;
	}

	public void setColumn4(GWTFormElement column4) {
		this.column4 = column4;
	}

	public GWTFormElement getColumn5() {
		return column5;
	}

	public void setColumn5(GWTFormElement column5) {
		this.column5 = column5;
	}

	public GWTFormElement getColumn6() {
		return column6;
	}

	public void setColumn6(GWTFormElement column6) {
		this.column6 = column6;
	}

	public GWTFormElement getColumn7() {
		return column7;
	}

	public void setColumn7(GWTFormElement column7) {
		this.column7 = column7;
	}

	public GWTFormElement getColumn8() {
		return column8;
	}

	public void setColumn8(GWTFormElement column8) {
		this.column8 = column8;
	}

	public GWTFormElement getColumn9() {
		return column9;
	}

	public void setColumn9(GWTFormElement column9) {
		this.column9 = column9;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("path=").append(path);
		sb.append(", uuid=").append(uuid);
		sb.append(", permissions=").append(permissions);
		sb.append(", size=").append(size);
		sb.append(", from=").append(from);
		sb.append(", reply=").append(Arrays.toString(reply));
		sb.append(", to=").append(Arrays.toString(to));
		sb.append(", sentDate=").append(sentDate == null ? null : sentDate.getTime());
		sb.append(", receivedDate=").append(receivedDate == null ? null : receivedDate.getTime());
		sb.append(", subject=").append(subject);
		sb.append(", content=").append(content);
		sb.append(", attachments=").append(attachments);
		sb.append(", notes=").append(notes);
		sb.append(", categories=").append(categories);
		sb.append(", keywords=").append(keywords);
		sb.append(", author=").append(author);
		sb.append(", created=").append(created == null ? null : created.getTime());
		sb.append("]");
		return sb.toString();
	}
}