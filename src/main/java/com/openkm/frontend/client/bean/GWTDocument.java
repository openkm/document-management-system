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

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * GWTDocument
 *
 * @author jllort
 */
public class GWTDocument implements IsSerializable {
	public static final String TYPE = "okm:document";

	private String parentPath;
	private String name;
	private String path;
	private String author;
	private byte[] content;
	private Date created;
	private Date lastModified;
	private String mimeType;
	private boolean locked;
	private boolean checkedOut;
	private GWTVersion actualVersion;
	private int permissions;
	private GWTLockInfo lockInfo;
	private boolean subscribed;
	private boolean signed;
	private boolean convertibleToPdf;
	private boolean convertibleToSwf;
	private boolean convertibleToDxf;
	private String uuid;
	private boolean isAttachment = false;
	private boolean hasNotes = false;
	private String cipherName;
	private Set<GWTUser> subscriptors;
	private List<GWTNote> notes;
	private Set<GWTFolder> categories;
	private Set<String> keywords;
	private GWTUser user;

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

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isCheckedOut() {
		return checkedOut;
	}

	public void setCheckedOut(boolean checkedOut) {
		this.checkedOut = checkedOut;
	}

	public GWTVersion getActualVersion() {
		return actualVersion;
	}

	public void setActualVersion(GWTVersion actualVersion) {
		this.actualVersion = actualVersion;
	}

	public int getPermissions() {
		return permissions;
	}

	public void setPermissions(int permissions) {
		this.permissions = permissions;
	}

	public GWTLockInfo getLockInfo() {
		return lockInfo;
	}

	public void setLockInfo(GWTLockInfo lockInfo) {
		this.lockInfo = lockInfo;
	}

	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public boolean isAttachment() {
		return isAttachment;
	}

	public void setAttachment(boolean isAttachment) {
		this.isAttachment = isAttachment;
	}

	public boolean isHasNotes() {
		return hasNotes;
	}

	public void setHasNotes(boolean hasNotes) {
		this.hasNotes = hasNotes;
	}

	public String getCipherName() {
		return cipherName;
	}

	public void setCipherName(String cipherName) {
		this.cipherName = cipherName;
	}

	public Set<GWTUser> getSubscriptors() {
		return subscriptors;
	}

	public void setSubscriptors(Set<GWTUser> subscriptors) {
		this.subscriptors = subscriptors;
	}

	public List<GWTNote> getNotes() {
		return notes;
	}

	public void setNotes(List<GWTNote> notes) {
		this.notes = notes;
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

	public GWTUser getUser() {
		return user;
	}

	public void setUser(GWTUser user) {
		this.user = user;
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
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("path=");
		sb.append(path);
		//sb.append(", title="); sb.append(title);
		// sb.append(", description="); sb.append(description);
		sb.append(", mimeType=");
		sb.append(mimeType);
		sb.append(", author=");
		sb.append(author);
		sb.append(", permissions=");
		sb.append(permissions);
		sb.append(", created=");
		sb.append(created == null ? null : created.getTime());
		sb.append(", lastModified=");
		sb.append(lastModified == null ? null : lastModified.getTime());
		sb.append(", keywords=");
		sb.append(keywords);
		sb.append(", categories=");
		sb.append(categories);
		sb.append(", locked=");
		sb.append(locked);
		sb.append(", lockInfo=");
		sb.append(lockInfo);
		sb.append(", actualVersion=");
		sb.append(actualVersion);
		sb.append(", subscribed=");
		sb.append(subscribed);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", convertibleToPdf=");
		sb.append(convertibleToPdf);
		sb.append(", convertibleToSwf=");
		sb.append(convertibleToSwf);
		sb.append(", convertibleToDxf=");
		sb.append(convertibleToDxf);
		sb.append(", cipherName=");
		sb.append(cipherName);
		sb.append(", notes=");
		sb.append(notes);
		sb.append(", user=");
		sb.append(user == null ? null : user.getId());
		sb.append(", username=");
		sb.append(user == null ? null : user.getUsername());
		sb.append("}");
		return sb.toString();
	}
}
