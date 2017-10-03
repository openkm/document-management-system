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

package com.openkm.dao.bean;

import javax.persistence.*;
import java.io.Serializable;

/**
 * MailAccountFolder
 * 
 * @author sochoa
 */
@Entity
@Table(name = "OKM_MAIL_ACCOUNT_FOLDER", uniqueConstraints = {
		// ALTER TABLE OKM_MAIL_ACCOUNT_FOLDER ADD CONSTRAINT IDX_MAF_MAID_PATH
		// UNIQUE (MAF_MA_ID, MAF_FOLDER_PATH)
		@UniqueConstraint(name = "IDX_MAF_MAID_PATH", columnNames = { "MAF_MA_ID", "MAF_FOLDER_PATH" }) })
public class MailAccountFolder implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "MAF_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "MAF_MA_ID")
	private long mailAccountId = -1;

	@Column(name = "MAF_FOLDER_PATH", length = 64)
	private String folderPath = "";

	@ManyToOne
	@MapsId("mailAccountId.MAF_MA_ID")
	@JoinColumn(name = "MAF_MA_ID", insertable = true, updatable = true, nullable = false)
	private MailAccount mailAccount;

	@Column(name = "MAF_MLAST_UID")
	private long mailLastUid = 0;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMailAccountId() {
		return mailAccountId;
	}

	public void setMailAccountId(long mailAccountId) {
		this.mailAccountId = mailAccountId;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public MailAccount getMailAccount() {
		return mailAccount;
	}

	public void setMailAccount(MailAccount mailAccount) {
		this.mailAccount = mailAccount;
	}

	public long getMailLastUid() {
		return mailLastUid;
	}

	public void setMailLastUid(long mailLastUid) {
		this.mailLastUid = mailLastUid;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=").append(id);
		sb.append(", mailAccountId=").append(mailAccountId);
		sb.append(", folderPath=").append(folderPath);
		sb.append(", mailAccount=").append(mailAccount != null ? mailAccount.getId() : "");
		sb.append(", mailLastUuid=").append(mailLastUid);
		sb.append("}");
		return sb.toString();
	}
}
