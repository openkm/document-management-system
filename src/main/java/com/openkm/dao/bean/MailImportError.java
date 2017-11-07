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

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 * MailImportError
 * 
 * @author sochoa
 */
@Entity
@Table(name = "OKM_MAIL_IMPORT_ERROR")
public class MailImportError implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int MAX_SUBJECT = 512;

	@Id
	@Column(name = "MIE_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "MIE_MAIL_UID")
	private String mailUid;

	@Column(name = "MIE_MAIL_SUBJECT", length = MAX_SUBJECT)
	private String mailSubject;

	@Column(name = "MIE_ERROR_MESSAGE")
	@Lob
	@Type(type = "org.hibernate.type.StringClobType")
	private String errorMessage;

	@Column(name = "MIE_IMPORT_DATE")
	protected Calendar importDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMailUid() {
		return mailUid;
	}

	public void setMailUid(String mailUid) {
		this.mailUid = mailUid;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		if (mailSubject != null && mailSubject.length() > MAX_SUBJECT) {
			this.mailSubject = mailSubject.substring(0, MAX_SUBJECT);
		} else {
			this.mailSubject = mailSubject;
		}
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Calendar getImportDate() {
		return importDate;
	}

	public void setImportDate(Calendar importDate) {
		this.importDate = importDate;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=").append(id);
		sb.append(", mailUid=").append(mailUid);
		sb.append(", mailSubject=").append(mailSubject);
		sb.append(", errorMessage=").append(errorMessage);
		sb.append(", importDate=").append(importDate == null ? null : importDate.getTime());
		sb.append("}");
		return sb.toString();
	}
}
