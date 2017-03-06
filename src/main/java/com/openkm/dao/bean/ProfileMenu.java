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

public class ProfileMenu implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean fileVisible;
	private boolean editVisible;
	private boolean toolsVisible;
	private boolean bookmarksVisible;
	private boolean templatesVisible;
	private boolean helpVisible;
	private ProfileMenuFile prfFile = new ProfileMenuFile();
	private ProfileMenuBookmark prfBookmark = new ProfileMenuBookmark();
	private ProfileMenuTool prfTool = new ProfileMenuTool();
	private ProfileMenuEdit prfEdit = new ProfileMenuEdit();
	private ProfileMenuHelp prfHelp = new ProfileMenuHelp();

	public boolean isFileVisible() {
		return fileVisible;
	}

	public void setFileVisible(boolean fileVisible) {
		this.fileVisible = fileVisible;
	}

	public boolean isEditVisible() {
		return editVisible;
	}

	public void setEditVisible(boolean editVisible) {
		this.editVisible = editVisible;
	}

	public boolean isToolsVisible() {
		return toolsVisible;
	}

	public void setToolsVisible(boolean toolsVisible) {
		this.toolsVisible = toolsVisible;
	}

	public boolean isBookmarksVisible() {
		return bookmarksVisible;
	}

	public void setBookmarksVisible(boolean bookmarksVisible) {
		this.bookmarksVisible = bookmarksVisible;
	}

	public boolean isTemplatesVisible() {
		return templatesVisible;
	}

	public void setTemplatesVisible(boolean templatesVisible) {
		this.templatesVisible = templatesVisible;
	}

	public boolean isHelpVisible() {
		return helpVisible;
	}

	public void setHelpVisible(boolean helpVisible) {
		this.helpVisible = helpVisible;
	}

	public ProfileMenuFile getPrfFile() {
		return prfFile;
	}

	public void setPrfFile(ProfileMenuFile prfFile) {
		this.prfFile = prfFile;
	}

	public ProfileMenuBookmark getPrfBookmark() {
		return prfBookmark;
	}

	public void setPrfBookmark(ProfileMenuBookmark prfBookmark) {
		this.prfBookmark = prfBookmark;
	}

	public ProfileMenuTool getPrfTool() {
		return prfTool;
	}

	public void setPrfTool(ProfileMenuTool prfTool) {
		this.prfTool = prfTool;
	}

	public ProfileMenuEdit getPrfEdit() {
		return prfEdit;
	}

	public void setPrfEdit(ProfileMenuEdit prfEdit) {
		this.prfEdit = prfEdit;
	}

	public ProfileMenuHelp getPrfHelp() {
		return prfHelp;
	}

	public void setPrfHelp(ProfileMenuHelp prfHelp) {
		this.prfHelp = prfHelp;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("fileVisible=").append(fileVisible);
		sb.append(", editVisible=").append(editVisible);
		sb.append(", toolsVisible=").append(toolsVisible);
		sb.append(", bookmarksVisible=").append(bookmarksVisible);
		sb.append(", templatesVisible=").append(templatesVisible);
		sb.append(", helpVisible=").append(helpVisible);
		sb.append(", prfFile=").append(prfFile);
		sb.append(", prfBookmark=").append(prfBookmark);
		sb.append(", prfTool=").append(prfTool);
		sb.append(", prfEdit=").append(prfEdit);
		sb.append(", prfHelp=").append(prfHelp);
		sb.append("}");
		return sb.toString();
	}
}
