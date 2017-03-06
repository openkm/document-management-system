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

package com.openkm.frontend.client.widget.filebrowser;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controller
 *
 * @author jllort
 *
 */
public class Controller {
	private boolean showFolders = true;
	private boolean showDocuments = true;
	private boolean showMails = true;
	private boolean paginated = true;
	private int selectedRowsLimit = 0;
	private int total = 0;
	private int offset = 0;
	private String selectedRowId;
	private int selectedOrderBy = 0;
	private boolean reverse = false;
	private Map<String, GWTFilter> mapFilter = new LinkedHashMap<String, GWTFilter>();

	public boolean isShowFolder() {
		return showFolders;
	}

	public void setShowFolders(boolean showFolders) {
		this.showFolders = showFolders;
	}

	public boolean isShowDocuments() {
		return showDocuments;
	}

	public void setShowDocuments(boolean showDocuments) {
		this.showDocuments = showDocuments;
	}

	public boolean isShowMails() {
		return showMails;
	}

	public void setMails(boolean showMails) {
		this.showMails = showMails;
	}

	public int getSelectedRowsLimit() {
		return selectedRowsLimit;
	}

	public void setSelectedRowsLimit(int selectedRowsLimit) {
		this.selectedRowsLimit = selectedRowsLimit;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public boolean isPaginated() {
		return paginated;
	}

	public void setPaginated(boolean paginated) {
		this.paginated = paginated;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String getSelectedRowId() {
		return selectedRowId;
	}

	public void setSelectedRowId(String selectedRowId) {
		this.selectedRowId = selectedRowId;
	}

	public int getSelectedOrderBy() {
		return selectedOrderBy;
	}

	public void setSelectedOrderBy(int selectedOrderBy) {
		this.selectedOrderBy = selectedOrderBy;
	}

	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	public Map<String, GWTFilter> getMapFilter() {
		return mapFilter;
	}

	public void setMapFilter(Map<String, GWTFilter> mapFilter) {
		this.mapFilter = mapFilter;
	}
}