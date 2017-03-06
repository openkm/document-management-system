/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.dao.bean;

import com.openkm.module.db.stuff.SetFieldBridge;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

@Entity
@Indexed
@Table(name = "OKM_NODE_MAIL")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NodeMail extends NodeBase {
	private static final long serialVersionUID = 1L;
	private static final int MAX_SUBJECT = 256;
	public static final String CONTENT_FIELD = "content";

	@Column(name = "NML_SIZE")
	private long size;

	@Column(name = "NML_FROM", length = 256)
	@Field(index = Index.TOKENIZED, store = Store.YES)
	private String from;

	@ElementCollection
	@Column(name = "NML_REPLY")
	@CollectionTable(name = "OKM_NODE_MAIL_REPLY", joinColumns = {@JoinColumn(name = "NMT_NODE")})
	@Field(index = Index.TOKENIZED, store = Store.YES)
	@FieldBridge(impl = SetFieldBridge.class)
	private Set<String> reply = new HashSet<String>();

	@ElementCollection
	@Column(name = "NML_TO")
	@CollectionTable(name = "OKM_NODE_MAIL_TO", joinColumns = {@JoinColumn(name = "NMT_NODE")})
	@Field(index = Index.TOKENIZED, store = Store.YES)
	@FieldBridge(impl = SetFieldBridge.class)
	private Set<String> to = new HashSet<String>();

	@ElementCollection
	@Column(name = "NML_CC")
	@CollectionTable(name = "OKM_NODE_MAIL_CC", joinColumns = {@JoinColumn(name = "NMC_NODE")})
	@Field(index = Index.TOKENIZED, store = Store.YES)
	@FieldBridge(impl = SetFieldBridge.class)
	private Set<String> cc = new HashSet<String>();

	@ElementCollection
	@Column(name = "NML_BCC")
	@CollectionTable(name = "OKM_NODE_MAIL_BCC", joinColumns = {@JoinColumn(name = "NMB_NODE")})
	@Field(index = Index.TOKENIZED, store = Store.YES)
	@FieldBridge(impl = SetFieldBridge.class)
	private Set<String> bcc = new HashSet<String>();

	@Column(name = "NML_SENT_DATE")
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	@CalendarBridge(resolution = Resolution.DAY)
	private Calendar sentDate;

	@Column(name = "NML_RECEIVED_DATE")
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	@CalendarBridge(resolution = Resolution.DAY)
	private Calendar receivedDate;

	@Column(name = "NML_SUBJECT", length = MAX_SUBJECT)
	@Field(index = Index.TOKENIZED, store = Store.YES)
	private String subject;

	@Column(name = "NML_CONTENT")
	@Lob
	@Type(type = "org.hibernate.type.StringClobType")
	@Field(index = Index.TOKENIZED, store = Store.NO)
	private String content;

	@Column(name = "NML_MIME_TYPE", length = 64)
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	private String mimeType;

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

	public Set<String> getReply() {
		return reply;
	}

	public void setReply(Set<String> reply) {
		this.reply = reply;
	}

	public Set<String> getTo() {
		return to;
	}

	public void setTo(Set<String> to) {
		this.to = to;
	}

	public Set<String> getCc() {
		return cc;
	}

	public void setCc(Set<String> cc) {
		this.cc = cc;
	}

	public Set<String> getBcc() {
		return bcc;
	}

	public void setBcc(Set<String> bcc) {
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
		if (subject != null && subject.length() > MAX_SUBJECT) {
			this.subject = subject.substring(0, MAX_SUBJECT);
		} else {
			this.subject = subject;
		}
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

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("uuid=").append(uuid);
		sb.append(", context=").append(context);
		sb.append(", path=").append(path);
		sb.append(", parent=").append(parent);
		sb.append(", author=").append(author);
		sb.append(", name=").append(name);
		sb.append(", size=").append(size);
		sb.append(", from=").append(from);
		sb.append(", reply=").append(reply);
		sb.append(", to=").append(to);
		sb.append(", cc=").append(cc);
		sb.append(", bcc=").append(bcc);
		sb.append(", sentDate=").append(sentDate);
		sb.append(", receivedDate=").append(receivedDate);
		sb.append(", subject=").append(subject);
		sb.append(", content=").append(content);
		sb.append(", mimeType=").append(mimeType);
		sb.append(", subscriptors=").append(subscriptors);
		sb.append(", keywords=").append(keywords);
		sb.append(", categories=").append(categories);
		// sb.append(", properties=").append(properties); Prevents Lazy Exception
		sb.append(", userPermissions=").append(userPermissions);
		sb.append(", rolePermissions=").append(rolePermissions);
		sb.append("}");
		return sb.toString();
	}
}
