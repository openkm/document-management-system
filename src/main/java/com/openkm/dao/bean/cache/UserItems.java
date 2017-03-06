package com.openkm.dao.bean.cache;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "OKM_USER_ITEMS")
public class UserItems implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "UI_USER")
	private String user;

	@Column(name = "UI_FOLDERS")
	private long folders;

	@Column(name = "UI_DOCUMENTS")
	private long documents;

	@Column(name = "UI_MAILS")
	private long mails;

	@Column(name = "UI_SIZE")
	private long size;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public long getFolders() {
		return folders;
	}

	public void setFolders(long folders) {
		this.folders = folders;
	}

	public long getDocuments() {
		return documents;
	}

	public void setDocuments(long document) {
		this.documents = document;
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
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("user=");
		sb.append(user);
		sb.append(", documents=");
		sb.append(documents);
		sb.append(", mails=");
		sb.append(mails);
		sb.append(", folders=");
		sb.append(folders);
		sb.append(", size=");
		sb.append(size);
		sb.append("}");
		return sb.toString();
	}
}
