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

package com.openkm.frontend.client.widget.searchresult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTQueryParams;
import com.openkm.frontend.client.bean.GWTQueryResult;
import com.openkm.frontend.client.bean.GWTResultSet;
import com.openkm.frontend.client.service.OKMSearchService;
import com.openkm.frontend.client.service.OKMSearchServiceAsync;
import com.openkm.frontend.client.widget.searchin.SearchControl;

import java.util.Iterator;

/**
 * @author jllort
 *
 */
public class SearchResult extends Composite {
	private final OKMSearchServiceAsync searchService = (OKMSearchServiceAsync) GWT.create(OKMSearchService.class);

	SimplePanel sp;
	public SearchCompactResult searchCompactResult;
	public SearchFullResult searchFullResult;
	public Status status;
	private GWTResultSet resultSet = new GWTResultSet();
	private int resultsViewMode = SearchControl.RESULTS_VIEW_NORMAL;


	public SearchResult() {
		sp = new SimplePanel();
		searchCompactResult = new SearchCompactResult();
		searchFullResult = new SearchFullResult();
		sp.add(searchFullResult);

		status = new Status();
		status.setStyleName("okm-StatusPopup");

		initWidget(sp);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.UIObject#setPixelSize(int, int)
	 */
	public void setPixelSize(int width, int height) {
		sp.setPixelSize(width, height);
		searchFullResult.setPixelSize(width, height);
		searchCompactResult.setPixelSize(width, height);
		searchCompactResult.fixWidth();
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		searchCompactResult.langRefresh();
		if (resultsViewMode == SearchControl.RESULTS_VIEW_NORMAL) {
			drawResults();
		}
	}

	/**
	 * Get the saved search
	 *
	 * @param params
	 */
	public void getSearch(GWTQueryParams params) {
		Main.get().mainPanel.search.searchBrowser.searchIn.setSavedSearch(params);
		searchCompactResult.removeAllRows();
	}

	/**
	 * removeAllRows
	 */
	public void removeAllRows() {
		switch (resultsViewMode) {
			case SearchControl.RESULTS_VIEW_COMPACT:
				searchCompactResult.removeAllRows();
				break;

			case SearchControl.RESULTS_VIEW_NORMAL:
				searchFullResult.removeAllRows();
				break;
		}
	}

	/**
	 * Call Back find paginated
	 */
	final AsyncCallback<GWTResultSet> callbackFindPaginated = new AsyncCallback<GWTResultSet>() {
		public void onSuccess(GWTResultSet result) {
			resultSet = result;
			drawResults();
			status.unsetFlag_findPaginated();
		}

		public void onFailure(Throwable caught) {
			status.unsetFlag_findPaginated();
			Main.get().showError("FindPaginated", caught);
		}
	};

	/**
	 * drawResults
	 */
	private void drawResults() {
		Main.get().mainPanel.search.searchBrowser.searchIn.searchControl.controlSearch.refreshControl(resultSet.getTotal());
		removeAllRows();

		for (Iterator<GWTQueryResult> it = resultSet.getResults().iterator(); it.hasNext(); ) {
			GWTQueryResult gwtQueryResult = it.next();

			switch (resultsViewMode) {
				case SearchControl.RESULTS_VIEW_COMPACT:
					searchCompactResult.addRow(gwtQueryResult);
					break;

				case SearchControl.RESULTS_VIEW_NORMAL:
					searchFullResult.addRow(gwtQueryResult);
					break;
			}
		}

		if (resultsViewMode == SearchControl.RESULTS_VIEW_COMPACT && searchCompactResult.isSorted()) {
			searchCompactResult.refreshSort();
		}
	}

	/**
	 * Find paginated
	 *
	 * @param words The path id
	 */
	public void findPaginated(GWTQueryParams params, int offset, int limit) {
		status.setFlag_findPaginated();
		searchService.findPaginated(params, offset, limit, callbackFindPaginated);
	}

	/**
	 * findSimpleQueryPaginated
	 *
	 * @param words The path id
	 */
	public void findSimpleQueryPaginated(String statement, int offset, int limit) {
		status.setFlag_findPaginated();
		searchService.findSimpleQueryPaginated(statement, offset, limit, callbackFindPaginated);
	}

	/**
	 * Sets the selected panel value
	 *
	 * @param selected The select panel value
	 */
	public void setSelectedPanel(boolean selected) {
		searchCompactResult.setSelectedPanel(selected);
	}

	/**
	 * switchResultsViewMode
	 *
	 * @param mode
	 */
	public void switchResultsViewMode(int mode) {
		resultsViewMode = mode;
		switch (resultsViewMode) {
			case SearchControl.RESULTS_VIEW_COMPACT:
				sp.remove(searchFullResult);
				sp.add(searchCompactResult);
				break;

			case SearchControl.RESULTS_VIEW_NORMAL:
				sp.remove(searchCompactResult);
				sp.add(searchFullResult);
				break;
		}
		status.setFlag_refreshResults();
		drawResults();
		status.unsetFlag_refreshResults();
	}
}