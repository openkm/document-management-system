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

public class ProfileMenuHelp implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean helpVisible;
	private boolean documentationVisible;
	private boolean bugTrackingVisible;
	private boolean supportVisible;
	private boolean forumVisible;
	private boolean changelogVisible;
	private boolean webSiteVisible;
	private boolean aboutVisible;

	public boolean isHelpVisible() {
		return helpVisible;
	}

	public void setHelpVisible(boolean helpVisible) {
		this.helpVisible = helpVisible;
	}

	public boolean isDocumentationVisible() {
		return documentationVisible;
	}

	public void setDocumentationVisible(boolean documentationVisible) {
		this.documentationVisible = documentationVisible;
	}

	public boolean isBugTrackingVisible() {
		return bugTrackingVisible;
	}

	public void setBugTrackingVisible(boolean bugTrackingVisible) {
		this.bugTrackingVisible = bugTrackingVisible;
	}

	public boolean isSupportVisible() {
		return supportVisible;
	}

	public void setSupportVisible(boolean supportVisible) {
		this.supportVisible = supportVisible;
	}

	public boolean isForumVisible() {
		return forumVisible;
	}

	public void setForumVisible(boolean forumVisible) {
		this.forumVisible = forumVisible;
	}

	public boolean isChangelogVisible() {
		return changelogVisible;
	}

	public void setChangelogVisible(boolean changelogVisible) {
		this.changelogVisible = changelogVisible;
	}

	public boolean isWebSiteVisible() {
		return webSiteVisible;
	}

	public void setWebSiteVisible(boolean webSiteVisible) {
		this.webSiteVisible = webSiteVisible;
	}

	public boolean isAboutVisible() {
		return aboutVisible;
	}

	public void setAboutVisible(boolean aboutVisible) {
		this.aboutVisible = aboutVisible;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("helpVisible=");
		sb.append(helpVisible);
		sb.append(", documentationVisible=");
		sb.append(documentationVisible);
		sb.append(", bugTrackingVisible=");
		sb.append(bugTrackingVisible);
		sb.append(", supportVisible=");
		sb.append(supportVisible);
		sb.append(", forumVisible=");
		sb.append(forumVisible);
		sb.append(", changelogVisible=");
		sb.append(changelogVisible);
		sb.append(", webSiteVisible=");
		sb.append(webSiteVisible);
		sb.append(", aboutVisible=");
		sb.append(aboutVisible);
		sb.append("}");
		return sb.toString();
	}
}
