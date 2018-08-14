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

package com.openkm.bean;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Calendar;

/**
 * @author pavila
 *
 */
@XmlRootElement(name = "document")
public class Document extends Node {
	private static final long serialVersionUID = 1L;

	public static final String TYPE = "okm:document";
	public static final String CONTENT = "okm:content";
	public static final String CONTENT_TYPE = "okm:resource";
	public static final String SIZE = "okm:size";
	public static final String LANGUAGE = "okm:language";
	public static final String VERSION_COMMENT = "okm:versionComment";
	public static final String NAME = "okm:name";
	public static final String TEXT = "okm:text";
	public static final String TITLE = "okm:title";
	public static final String DESCRIPTION = "okm:description";

	private String title = "";
	private String description = "";
	private String language = "";
	private Calendar lastModified;
	private String mimeType;
	private boolean locked;
	private boolean checkedOut;
	private Version actualVersion;
	private LockInfo lockInfo;
	private boolean signed;
	private boolean convertibleToPdf;
	private boolean convertibleToSwf;
	private String cipherName;

	public LockInfo getLockInfo() {
		return lockInfo;
	}

	public void setLockInfo(LockInfo lockInfo) {
		this.lockInfo = lockInfo;
	}

	public Calendar getLastModified() {
		return lastModified;
	}

	public void setLastModified(Calendar lastModified) {
		this.lastModified = lastModified;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isCheckedOut() {
		return checkedOut;
	}

	public void setCheckedOut(boolean checkedOut) {
		this.checkedOut = checkedOut;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public Version getActualVersion() {
		return actualVersion;
	}

	public void setActualVersion(Version actualVersion) {
		this.actualVersion = actualVersion;
	}

	public boolean isConvertibleToPdf() {
		return convertibleToPdf;
	}

	public void setConvertibleToPdf(boolean convertibleToPdf) {
		this.convertibleToPdf = convertibleToPdf;
	}

	public boolean isConvertibleToSwf() {
		return convertibleToSwf;
	}

	public void setConvertibleToSwf(boolean convertibleToSwf) {
		this.convertibleToSwf = convertibleToSwf;
	}

	public void setCipherName(String cipherName) {
		this.cipherName = cipherName;
	}

	public String getCipherName() {
		return cipherName;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("path=").append(path);
		sb.append(", title=").append(title);
		sb.append(", description=").append(description);
		sb.append(", mimeType=").append(mimeType);
		sb.append(", author=").append(author);
		sb.append(", permissions=").append(permissions);
		sb.append(", created=").append(created == null ? null : created.getTime());
		sb.append(", lastModified=").append(lastModified == null ? null : lastModified.getTime());
		sb.append(", keywords=").append(keywords);
		sb.append(", categories=").append(categories);
		sb.append(", locked=").append(locked);
		sb.append(", lockInfo=").append(lockInfo);
		sb.append(", actualVersion=").append(actualVersion);
		sb.append(", subscribed=").append(subscribed);
		sb.append(", subscriptors=").append(subscriptors);
		sb.append(", uuid=").append(uuid);
		sb.append(", convertibleToPdf=").append(convertibleToPdf);
		sb.append(", convertibleToSwf=").append(convertibleToSwf);
		sb.append(", cipherName=").append(cipherName);
		sb.append(", notes=").append(notes);
		sb.append(", language=").append(language);
		sb.append("}");
		return sb.toString();
	}
}
