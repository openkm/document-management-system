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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Query params
 *
 * @author jllort
 */
public class GWTQueryParams implements IsSerializable, Cloneable {
	public static final int DOCUMENT = 1;
	public static final int FOLDER = 2;
	public static final int MAIL = 4;
	public static final String OPERATOR_AND = "and";
	public static final String OPERATOR_OR = "or";

	private long id;
	private String queryName;
	private String name;
	private String keywords;
	private String content;
	private String path;
	private String mimeType;
	private String author;
	private Date lastModifiedFrom;
	private Date lastModifiedTo;
	private boolean isDashboard = false;
	private long domain = 0;
	private String mailFrom = "";
	private String mailTo = "";
	private String mailSubject = "";
	private String categoryUuid = "";
	private String categoryPath = "";
	private boolean isShared = false;
	private String operator = OPERATOR_AND;
	private Map<String, GWTPropertyParams> properties = new HashMap<String, GWTPropertyParams>();

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public GWTQueryParams clone() {
		GWTQueryParams newParans = new GWTQueryParams();
		newParans.setAuthor(getAuthor());
		newParans.setCategoryPath(getCategoryPath());
		newParans.setCategoryUuid(getCategoryUuid());
		newParans.setContent(getContent());
		newParans.setDashboard(isDashboard());
		newParans.setDomain(getDomain());
		newParans.setId(getId());
		newParans.setKeywords(getKeywords());
		newParans.setLastModifiedFrom(getLastModifiedFrom());
		newParans.setLastModifiedTo(getLastModifiedTo());
		newParans.setMailFrom(getMailFrom());
		newParans.setMailSubject(getMailSubject());
		newParans.setMailTo(getMailTo());
		newParans.setMimeType(getMimeType());
		newParans.setName(getName());
		newParans.setOperator(getOperator());
		newParans.setPath(getPath());
		Map<String, GWTPropertyParams> newProperties = new HashMap<String, GWTPropertyParams>();

		for (String key : properties.keySet()) {
			newProperties.put(key, properties.get(key));
		}

		newParans.setProperties(newProperties);
		newParans.setQueryName(getQueryName());
		newParans.setShared(isShared());
		return newParans;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, GWTPropertyParams> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, GWTPropertyParams> finalProperties) {
		this.properties = finalProperties;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getLastModifiedFrom() {
		return lastModifiedFrom;
	}

	public void setLastModifiedFrom(Date lastModifiedFrom) {
		this.lastModifiedFrom = lastModifiedFrom;
	}

	public Date getLastModifiedTo() {
		return lastModifiedTo;
	}

	public void setLastModifiedTo(Date lastModifiedTo) {
		this.lastModifiedTo = lastModifiedTo;
	}

	public boolean isDashboard() {
		return isDashboard;
	}

	public void setDashboard(boolean isDashboard) {
		this.isDashboard = isDashboard;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getDomain() {
		return domain;
	}

	public void setDomain(long domain) {
		this.domain = domain;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public String getMailTo() {
		return mailTo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getCategoryUuid() {
		return categoryUuid;
	}

	public void setCategoryUuid(String uuid) {
		this.categoryUuid = uuid;
	}

	public String getCategoryPath() {
		return categoryPath;
	}

	public void setCategoryPath(String categoryPath) {
		this.categoryPath = categoryPath;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public boolean isShared() {
		return isShared;
	}

	public void setShared(boolean isShared) {
		this.isShared = isShared;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("name=");
		sb.append(name);
		sb.append(", keywords=");
		sb.append(keywords);
		sb.append(", content=");
		sb.append(content);
		sb.append(", path=");
		sb.append(path);
		sb.append(", mimeType=");
		sb.append(mimeType);
		sb.append(", author=");
		sb.append(author);
		sb.append(", isDashboard=" + isDashboard);
		sb.append(", isShared=" + isShared);
		sb.append(", lastModifiedFrom=");
		sb.append(lastModifiedFrom == null ? null : lastModifiedFrom.getTime());
		sb.append(", lastModifiedTo=");
		sb.append(lastModifiedTo == null ? null : lastModifiedTo.getTime());
		sb.append(", properties=");
		sb.append(properties);
		sb.append("]");
		return sb.toString();
	}
}
