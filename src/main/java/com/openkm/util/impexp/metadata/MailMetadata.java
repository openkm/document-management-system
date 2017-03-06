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

package com.openkm.util.impexp.metadata;

import java.util.*;

public class MailMetadata {
	// okm:mail
	private String uuid;
	private String path;
	private String name;
	private long size;
	private String from;
	private List<String> reply = new ArrayList<String>();
	private List<String> to = new ArrayList<String>();
	private List<String> cc = new ArrayList<String>();
	private List<String> bcc = new ArrayList<String>();
	private Calendar sentDate;
	private Calendar receivedDate;
	private String subject;
	private String content;
	private String mimeType;
	private String author;
	private Calendar created;
	private Set<String> keywords = new HashSet<String>();
	private Set<CategoryMetadata> categories = new HashSet<CategoryMetadata>();
	private List<DocumentMetadata> attachments = new ArrayList<DocumentMetadata>();

	// mix:scripting
	private String scripting;

	// okm:notes
	private List<NoteMetadata> notes = new ArrayList<NoteMetadata>();

	// mix:property_group
	private List<PropertyGroupMetadata> propertyGroups = new ArrayList<PropertyGroupMetadata>();

	// mix:accessControlled
	private Map<String, Integer> grantedUsers = new HashMap<String, Integer>();
	private Map<String, Integer> grantedRoles = new HashMap<String, Integer>();

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getReply() {
		return reply;
	}

	public void setReply(List<String> reply) {
		this.reply = reply;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public List<String> getCc() {
		return cc;
	}

	public void setCc(List<String> cc) {
		this.cc = cc;
	}

	public List<String> getBcc() {
		return bcc;
	}

	public void setBcc(List<String> bcc) {
		this.bcc = bcc;
	}

	public Calendar getSentDate() {
		return sentDate;
	}

	public void setSentDate(Calendar sentDate) {
		this.sentDate = sentDate;
	}

	public Calendar getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Calendar receivedDate) {
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

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}

	public Set<CategoryMetadata> getCategories() {
		return categories;
	}

	public void setCategories(Set<CategoryMetadata> categories) {
		this.categories = categories;
	}

	public List<DocumentMetadata> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<DocumentMetadata> attachments) {
		this.attachments = attachments;
	}

	public String getScripting() {
		return scripting;
	}

	public void setScripting(String scripting) {
		this.scripting = scripting;
	}

	public List<NoteMetadata> getNotes() {
		return notes;
	}

	public void setNotes(List<NoteMetadata> notes) {
		this.notes = notes;
	}

	public List<PropertyGroupMetadata> getPropertyGroups() {
		return propertyGroups;
	}

	public void setPropertyGroups(List<PropertyGroupMetadata> propertyGroups) {
		this.propertyGroups = propertyGroups;
	}

	public Map<String, Integer> getGrantedUsers() {
		return grantedUsers;
	}

	public void setGrantedUsers(Map<String, Integer> grantedUsers) {
		this.grantedUsers = grantedUsers;
	}

	public Map<String, Integer> getGrantedRoles() {
		return grantedRoles;
	}

	public void setGrantedRoles(Map<String, Integer> grantedRoles) {
		this.grantedRoles = grantedRoles;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("uuid=");
		sb.append(uuid);
		sb.append(", path=");
		sb.append(path);
		sb.append(", name=");
		sb.append(name);
		sb.append(", size=");
		sb.append(size);
		sb.append(", from=");
		sb.append(from);
		sb.append(", reply=");
		sb.append(reply);
		sb.append(", to=");
		sb.append(to);
		sb.append(", cc=");
		sb.append(cc);
		sb.append(", bcc=");
		sb.append(bcc);
		sb.append(", sentDate=");
		sb.append(sentDate == null ? null : sentDate.getTime());
		sb.append(", receivedDate=");
		sb.append(receivedDate == null ? null : receivedDate.getTime());
		sb.append(", subject=");
		sb.append(subject);
		sb.append(", content=");
		sb.append(content);
		sb.append(", mimeType=");
		sb.append(mimeType);
		sb.append(", author=");
		sb.append(author);
		sb.append(", created=");
		sb.append(created);
		sb.append(", keywords=");
		sb.append(keywords);
		sb.append(", categories=");
		sb.append(categories);
		sb.append(", attachments=");
		sb.append(attachments);
		sb.append(", scripting=");
		sb.append(scripting);
		sb.append(", notes=");
		sb.append(notes);
		sb.append(", propertyGroups=");
		sb.append(propertyGroups);
		sb.append(", grantedUsers=");
		sb.append(grantedUsers);
		sb.append(", grantedRoles=");
		sb.append(grantedRoles);
		sb.append("}");
		return sb.toString();
	}
}
