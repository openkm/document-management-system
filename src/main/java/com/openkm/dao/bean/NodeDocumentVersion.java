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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Entity
@Indexed
@Table(name = "OKM_NODE_DOCUMENT_VERSION", uniqueConstraints = {
		@UniqueConstraint(name = "IDX_NOD_DOC_VER_PARNAM", columnNames = {"NDV_PARENT", "NDV_NAME"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.hibernate.annotations.Table(appliesTo = "OKM_NODE_DOCUMENT_VERSION",
		indexes = {
				// CREATE INDEX IDX_NOD_DOC_VER_PARCUR ON OKM_NODE_DOCUMENT_VERSION(NDV_PARENT, NDV_CURRENT);
				@org.hibernate.annotations.Index(name = "IDX_NOD_DOC_VER_PARCUR", columnNames = {"NDV_PARENT", "NDV_CURRENT"})
		}
)
public class NodeDocumentVersion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@DocumentId
	@Column(name = "NDV_UUID", length = 64)
	private String uuid;

	@Column(name = "NDV_PARENT", length = 64)
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	// CREATE INDEX IDX_NDOC_VERSION_PARENT ON OKM_NODE_DOCUMENT_VERSION(NDV_PARENT);
	@org.hibernate.annotations.Index(name = "IDX_NOD_DOC_VER_PARENT")
	private String parent;

	// The UUID of the previous version
	@Column(name = "NDV_PREVIOUS", length = 64)
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	private String previous;

	@Column(name = "NDV_SIZE")
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	private long size;

	@Column(name = "NDV_AUTHOR", length = 64)
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	private String author;

	@Column(name = "NDV_CREATED")
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	@CalendarBridge(resolution = Resolution.DAY)
	private Calendar created;

	@Column(name = "NDV_NAME", length = 64)
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	private String name;

	@Column(name = "NDV_CURRENT", nullable = false)
	@Type(type = "true_false")
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	private boolean current;

	@Column(name = "NDV_COMMENT", length = 2048)
	@Field(index = Index.TOKENIZED, store = Store.YES)
	private String comment;

	@Column(name = "NDV_MIME_TYPE", length = 128)
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	private String mimeType;

	@Column(name = "NDV_CHECKSUM", length = 32)
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	private String checksum;

	// http://stackoverflow.com/questions/3677380/proper-hibernate-annotation-for-byte
	@Column(name = "NDV_CONTENT")
	@Lob
	private byte[] content;

	@Transient
	@Field(index = Index.TOKENIZED, store = Store.NO)
	private String text;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getPrevious() {
		return previous;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("uuid=").append(uuid);
		sb.append(", parent=").append(parent);
		sb.append(", previous=").append(previous);
		sb.append(", size=").append(size);
		sb.append(", author=").append(author);
		sb.append(", created=").append(created == null ? null : created.getTime());
		sb.append(", name=").append(name);
		sb.append(", current=").append(current);
		sb.append(", comment=").append(comment);
		sb.append(", mimeType=").append(mimeType);
		sb.append(", checksum=").append(checksum);
		sb.append(", content=").append(String.valueOf(content));
		sb.append(", text=").append(text);
		sb.append("}");
		return sb.toString();
	}
}
