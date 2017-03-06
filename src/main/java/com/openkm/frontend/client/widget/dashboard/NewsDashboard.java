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

package com.openkm.frontend.client.widget.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDashboardDocumentResult;
import com.openkm.frontend.client.bean.GWTQueryParams;
import com.openkm.frontend.client.constants.ui.UIDashboardConstants;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.service.OKMDashboardService;
import com.openkm.frontend.client.service.OKMDashboardServiceAsync;

import java.util.*;

/**
 * NewsDashboard
 *
 * @author jllort
 *
 */
public class NewsDashboard extends WidgetToFire {

	private final OKMDashboardServiceAsync dashboardService = (OKMDashboardServiceAsync) GWT.create(OKMDashboardService.class);

	private final int NUMBER_OF_COLUMNS = 2;

	private HorizontalPanel hPanel;
	private Map<String, DashboardWidget> hWidgetSearch = new HashMap<String, DashboardWidget>();
	private Map<String, GWTQueryParams> keyMap = new HashMap<String, GWTQueryParams>();
	private VerticalPanel vPanelLeft;
	private VerticalPanel vPanelRight;
	private int columnWidth = 0;
	private int actualSearchRefreshing = 0;
	private String actualRefreshingKey = "0";
	private boolean refreshFind = true;
	private int newsDocuments = 0;

	private boolean showStatus = false;

	/**
	 * NewsDashboard
	 */
	public NewsDashboard() {
		vPanelLeft = new VerticalPanel();
		vPanelRight = new VerticalPanel();
		hPanel = new HorizontalPanel();

		hPanel.add(vPanelLeft);
		hPanel.add(vPanelRight);

		initWidget(hPanel);
	}

	/**
	 * Gets all search callback
	 */
	final AsyncCallback<List<GWTQueryParams>> callbackGetUserSearchs = new AsyncCallback<List<GWTQueryParams>>() {
		public void onSuccess(List<GWTQueryParams> result) {
			// Drops widget panel , prevent user deletes query
			for (Iterator<String> it = keyMap.keySet().iterator(); it.hasNext(); ) {
				int key = Integer.parseInt(it.next());
				boolean found = false;

				// looking for key
				for (Iterator<GWTQueryParams> itx = result.iterator(); itx.hasNext(); ) {
					GWTQueryParams params = itx.next();
					if (params.getId() == key) {
						found = true;
						break;
					}
				}

				// if has been removed must remove from list
				if (!found) {
					DashboardWidget dashboardWidget = hWidgetSearch.get(key);
					if (dashboardWidget.getParent().equals(vPanelLeft)) {
						vPanelLeft.remove(dashboardWidget);
					} else if (dashboardWidget.getParent().equals(vPanelRight)) {
						vPanelRight.remove(dashboardWidget);
					}
					keyMap.remove("" + key);
				}
			}

			// Adds new widget
			for (ListIterator<GWTQueryParams> it = result.listIterator(); it.hasNext(); ) {
				GWTQueryParams params = it.next();
				String key = "" + params.getId();
				if (!keyMap.keySet().contains(key)) {
					keyMap.put(key, params);
					DashboardWidget dashboardWidget = new DashboardWidget(key, params.getQueryName(), "img/icon/news.gif", true, "news_" + key);
					dashboardWidget.setWidgetToFire(Main.get().mainPanel.dashboard.newsDashboard);
					hWidgetSearch.put(key, dashboardWidget);
					dashboardWidget.setWidth(columnWidth);
					dashboardWidget.setHeaderResults(0);

					// Distribute widgets left / rigth
					if (vPanelLeft.getWidgetCount() <= vPanelRight.getWidgetCount()) {
						vPanelLeft.add(dashboardWidget);
					} else {
						vPanelRight.add(dashboardWidget);
					}
				}
			}

			if (refreshFind) {
				refreshFind = false;
				refreshAll();
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getUserSearchs", caught);
		}
	};

	/**
	 * Gets the find search callback
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackFind = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result) {
			DashboardWidget dashboardWidget = hWidgetSearch.get(actualRefreshingKey);
			dashboardWidget.setDocuments(result);
			dashboardWidget.setHeaderResults(result.size());
			newsDocuments += dashboardWidget.getNotViewed();
			find(actualSearchRefreshing++);
			dashboardWidget.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("find", caught);
			hWidgetSearch.get(actualRefreshingKey).unsetRefreshing();
		}
	};

	/**
	 * setWidth
	 */
	public void setWidth(int width) {
		this.columnWidth = width / NUMBER_OF_COLUMNS;
	}

	/**
	 * getAllSearchs
	 */
	public void getUserSearchs(boolean refreshFind) {
		this.refreshFind = refreshFind;
		dashboardService.getUserSearchs(callbackGetUserSearchs);
	}

	/**
	 * refreshAllSearchs
	 */
	private void find(int value) {
		if (keyMap.keySet().size() > value) {
			List<String> keySet = new ArrayList<String>(keyMap.keySet());
			actualRefreshingKey = keySet.get(value);
			if (!showStatus) {
				hWidgetSearch.get(actualRefreshingKey).setRefreshing();
			}
			dashboardService.find(Integer.parseInt(actualRefreshingKey), callbackFind);
		} else {
			Main.get().mainPanel.bottomPanel.userInfo.setNewsDocuments(newsDocuments);
		}
	}

	/**
	 * Refreshing all searchs
	 */
	public void refreshAll() {
		showStatus = ((Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace() == UIDockPanelConstants.DASHBOARD) &&
				(Main.get().mainPanel.dashboard.getActualView() == UIDashboardConstants.DASHBOARD_NEWS));
		newsDocuments = 0;
		actualSearchRefreshing = 0;
		find(actualSearchRefreshing++);
	}

	@Override
	public void decrementNewDocuments(int value) {
		newsDocuments -= value;
		Main.get().mainPanel.bottomPanel.userInfo.setNewsDocuments(newsDocuments);
	}

	/**
	 * Refreshing language
	 */
	public void langRefresh() {
		for (String key : keyMap.keySet()) {
			DashboardWidget dashboardWidget = hWidgetSearch.get(key);
			dashboardWidget.langRefresh();
		}
	}
}