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

package com.openkm.frontend.client.widget.searchsaved;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Image;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTQueryParams;
import com.openkm.frontend.client.service.OKMSearchService;
import com.openkm.frontend.client.service.OKMSearchServiceAsync;
import com.openkm.frontend.client.util.OKMBundleResources;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Saved searches
 *
 * @author jllort
 */
public class SearchSaved extends Composite {
	private final OKMSearchServiceAsync searchService = (OKMSearchServiceAsync) GWT.create(OKMSearchService.class);

	private ExtendedFlexTable table;
	public MenuPopup menuPopup;
	private Status status;
	private boolean firstTime = true;

	// Holds the data rows of the table this is a list of RowData Object
	public Map<Long, GWTQueryParams> data;
	private long dataIndexValue = 0;
	private long searchIdToDelete = 0;

	/**
	 * SearchSaved
	 */
	public SearchSaved() {
		data = new HashMap<Long, GWTQueryParams>();
		table = new ExtendedFlexTable();
		menuPopup = new MenuPopup();
		menuPopup.setStyleName("okm-MenuPopup");
		status = new Status();
		status.setStyleName("okm-StatusPopup");

		table.setBorderWidth(0);
		table.setCellSpacing(0);
		table.setCellSpacing(0);

		table.sinkEvents(Event.ONDBLCLICK | Event.ONMOUSEDOWN);

		initWidget(table);
	}

	/**
	 * Inits on first load
	 */
	public void init() {
		getAllSearchs();
	}

	/**
	 * Show the browser menu
	 */
	public void showMenu() {
		// The browser menu depends on actual view
		// Must substract top position from Y Screen Position
		menuPopup.evaluateMenuOptions();
		menuPopup.setPopupPosition(table.getMouseX(), table.getMouseY());
		menuPopup.show();
	}

	/**
	 * Gets the selected row value
	 *
	 * @return The selected row value
	 */
	public int getSelectedRow() {
		return table.getSelectedRow();
	}

	/**
	 * Call Back get search
	 */
	final AsyncCallback<List<GWTQueryParams>> callbackGetSearchs = new AsyncCallback<List<GWTQueryParams>>() {
		public void onSuccess(List<GWTQueryParams> result) {
			table.removeAllRows();

			for (Iterator<GWTQueryParams> it = result.iterator(); it.hasNext(); ) {
				addRow(it.next());
			}
			if (!firstTime) {
				status.unsetFlag_getSearchs();
			} else {
				firstTime = false;
			}
		}

		public void onFailure(Throwable caught) {
			if (!firstTime) {
				status.unsetFlag_getSearchs();
			} else {
				firstTime = false;
			}

			Main.get().showError("GetSearchs", caught);
		}
	};

	/**
	 * Call Back delete search
	 */
	final AsyncCallback<Object> callbackDeleteSearch = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			table.removeRow(getSelectedRow());
			table.selectPrevRow();
			data.remove(new Long(searchIdToDelete));
			status.unsetFlag_deleteSearch();
		}

		public void onFailure(Throwable caught) {
			status.unsetFlag_deleteSearch();
			Main.get().showError("DeleteSearch", caught);
		}
	};

	/**
	 * addNewSavedSearch
	 *
	 * @param search Search parameters to be saved.
	 */
	public void addNewSavedSearch(GWTQueryParams search) {
		addRow(search);
	}

	/**
	 * Adds a new row
	 *
	 * @param search Search parameters to be added.
	 */
	private void addRow(GWTQueryParams search) {
		int rows = table.getRowCount();

		data.put(new Long(dataIndexValue), search);

		if (!search.isShared()) {
			table.setHTML(rows, 0, "&nbsp;");
		} else {
			table.setWidget(rows, 0, new Image(OKMBundleResources.INSTANCE.sharedQuery()));
		}

		table.setHTML(rows, 1, search.getQueryName());
		table.setHTML(rows, 2, "" + dataIndexValue++);
		table.setHTML(rows, 3, "");
		table.getCellFormatter().setVisible(rows, 2, false);

		// The hidden column extends table to 100% width
		CellFormatter cellFormatter = table.getCellFormatter();
		cellFormatter.setWidth(rows, 0, "30px");
		cellFormatter.setHeight(rows, 0, "20px");
		cellFormatter.setHorizontalAlignment(rows, 0, HasAlignment.ALIGN_CENTER);
		cellFormatter.setVerticalAlignment(rows, 0, HasAlignment.ALIGN_MIDDLE);
		cellFormatter.setWidth(rows, 3, "100%");

		table.getRowFormatter().setStyleName(rows, "okm-SearchSaved");
		setRowWordWarp(rows, 4, false);
	}

	/**
	 * Set the WordWarp for all the row cells
	 *
	 * @param row The row cell
	 * @param columns Number of row columns
	 * @param warp
	 */
	private void setRowWordWarp(int row, int columns, boolean warp) {
		CellFormatter cellFormatter = table.getCellFormatter();
		for (int i = 0; i < columns; i++) {
			cellFormatter.setWordWrap(row, i, false);
		}
	}

	/**
	 * Get searchs
	 */
	public void getAllSearchs() {
		if (!firstTime) {
			status.setFlag_getSearchs();
		}

		searchService.getAllSearchs(callbackGetSearchs);
	}

	/**
	 * Gets a search
	 */
	public void getSearch() {
		if (getSelectedRow() >= 0) {
			long id = Long.parseLong(table.getText(getSelectedRow(), 2));
			Main.get().mainPanel.search.searchBrowser.searchResult.getSearch(data.get(id));
		}
	}

	/**
	 * getSavedSearch
	 *
	 * @return Obtain the saved search parameters.
	 */
	public GWTQueryParams getSavedSearch() {
		if (getSelectedRow() >= 0) {
			long id = Long.parseLong(table.getText(getSelectedRow(), 2));
			return data.get(id);
		} else {
			return null;
		}
	}

	/**
	 * Deletes a Search
	 */
	public void deleteSearch() {
		if (getSelectedRow() >= 0) {
			status.setFlag_deleteSearch();
			searchIdToDelete = Long.parseLong(table.getText(getSelectedRow(), 2));

			if (!getSavedSearch().isShared()) {
				searchService.deleteSearch(data.get(searchIdToDelete).getId(), callbackDeleteSearch);
			} else {
				searchService.unshare(data.get(searchIdToDelete).getId(), callbackDeleteSearch);
			}
		}
	}

	/**
	 * Sets the selected panel value
	 *
	 * @param selected The selected panel value
	 */
	public void setSelectedPanel(boolean selected) {
		table.setSelectedPanel(selected);
	}

	/**
	 * Indicates if panel is selected
	 *
	 * @return The value of panel ( selected )
	 */
	public boolean isPanelSelected() {
		return table.isPanelSelected();
	}

	/**
	 *
	 */
	public void langRefresh() {
		menuPopup.langRefresh();
	}
}
