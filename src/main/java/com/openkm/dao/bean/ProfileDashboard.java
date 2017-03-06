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

import java.io.Serializable;

public class ProfileDashboard implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean userVisible;
	private boolean mailVisible;
	private boolean newsVisible;
	private boolean generalVisible;
	private boolean workflowVisible;
	private boolean keywordsVisible;

	public boolean isUserVisible() {
		return userVisible;
	}

	public void setUserVisible(boolean userVisible) {
		this.userVisible = userVisible;
	}

	public boolean isMailVisible() {
		return mailVisible;
	}

	public void setMailVisible(boolean mailVisible) {
		this.mailVisible = mailVisible;
	}

	public boolean isNewsVisible() {
		return newsVisible;
	}

	public void setNewsVisible(boolean newsVisible) {
		this.newsVisible = newsVisible;
	}

	public boolean isGeneralVisible() {
		return generalVisible;
	}

	public void setGeneralVisible(boolean generalVisible) {
		this.generalVisible = generalVisible;
	}

	public boolean isWorkflowVisible() {
		return workflowVisible;
	}

	public void setWorkflowVisible(boolean workflowVisible) {
		this.workflowVisible = workflowVisible;
	}

	public boolean isKeywordsVisible() {
		return keywordsVisible;
	}

	public void setKeywordsVisible(boolean keywordsVisible) {
		this.keywordsVisible = keywordsVisible;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("userVisible=");
		sb.append(userVisible);
		sb.append(", mailVisible=");
		sb.append(mailVisible);
		sb.append(", newsVisible=");
		sb.append(newsVisible);
		sb.append(", generalVisible=");
		sb.append(generalVisible);
		sb.append(", workflowVisible=");
		sb.append(workflowVisible);
		sb.append(", keywordsVisible=");
		sb.append(keywordsVisible);
		sb.append("}");
		return sb.toString();
	}
}
