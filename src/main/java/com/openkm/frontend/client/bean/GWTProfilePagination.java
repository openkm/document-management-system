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

/**
 * GWTProfilePagination
 *
 * @author jllort
 */
public class GWTProfilePagination implements IsSerializable {
	private boolean showFoldersEnabled = false;
	private boolean showDocumentsEnabled = false;
	private boolean showMailsEnabled = false;
	private boolean typeFilterEnabled;
	private boolean paginationEnabled;
	private boolean miscFilterEnabled;
	private String pageList;

	public boolean isShowFoldersEnabled() {
		return showFoldersEnabled;
	}

	public void setShowFoldersEnabled(boolean showFoldersEnabled) {
		this.showFoldersEnabled = showFoldersEnabled;
	}

	public boolean isShowDocumentsEnabled() {
		return showDocumentsEnabled;
	}

	public void setShowDocumentsEnabled(boolean showDocumentsEnabled) {
		this.showDocumentsEnabled = showDocumentsEnabled;
	}

	public boolean isShowMailsEnabled() {
		return showMailsEnabled;
	}

	public void setShowMailsEnabled(boolean showMailsEnabled) {
		this.showMailsEnabled = showMailsEnabled;
	}

	public boolean isTypeFilterEnabled() {
		return typeFilterEnabled;
	}

	public void setTypeFilterEnabled(boolean typeFilterEnabled) {
		this.typeFilterEnabled = typeFilterEnabled;
	}

	public boolean isPaginationEnabled() {
		return paginationEnabled;
	}

	public void setPaginationEnabled(boolean paginationEnabled) {
		this.paginationEnabled = paginationEnabled;
	}

	public boolean isMiscFilterEnabled() {
		return miscFilterEnabled;
	}

	public void setMiscFilterEnabled(boolean miscFilterEnabled) {
		this.miscFilterEnabled = miscFilterEnabled;
	}

	public String getPageList() {
		return pageList;
	}

	public void setPageList(String pageList) {
		this.pageList = pageList;
	}
}