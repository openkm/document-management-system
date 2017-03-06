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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDashboardDocumentResult;
import com.openkm.frontend.client.bean.GWTDashboardFolderResult;
import com.openkm.frontend.client.constants.ui.UIDashboardConstants;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.service.OKMDashboardService;
import com.openkm.frontend.client.service.OKMDashboardServiceAsync;

import java.util.List;

/**
 * UserDashboard
 *
 * @author jllort
 *
 */
public class UserDashboard extends Composite {
	private final OKMDashboardServiceAsync dashboardService = (OKMDashboardServiceAsync) GWT.create(OKMDashboardService.class);

	private final int NUMBER_OF_COLUMNS = 2;

	private HorizontalPanel hPanel;
	private VerticalPanel vPanelLeft;
	private VerticalPanel vPanelRight;

	private DashboardWidget lockedDocuments;
	private DashboardWidget chechoutDocuments;
	private DashboardWidget lastModifiedDocuments;
	private DashboardWidget subscribedDocuments;
	private DashboardWidget subscribedFolder;
	private DashboardWidget lastDownloadedDocuments;
	private DashboardWidget lastUploadedDocuments;

	private boolean showStatus = false;
	private int tmpSubscriptions = 0;
	private boolean checkoutDocumentFlag = false;
	private int checkouts = 0;

	/**
	 * UserDashboard
	 */
	public UserDashboard() {
		vPanelLeft = new VerticalPanel();
		vPanelRight = new VerticalPanel();
		hPanel = new HorizontalPanel();

		hPanel.add(vPanelLeft);
		hPanel.add(vPanelRight);

		lockedDocuments = new DashboardWidget("UserLockedDocuments", "dashboard.user.locked.documents",
				"img/icon/lock.gif", true, "userLockedDocuments");
		chechoutDocuments = new DashboardWidget("UserCheckedOutDocuments", "dashboard.user.checkout.documents",
				"img/icon/actions/checkout.gif", true, "userCheckedOutDocuments");
		lastModifiedDocuments = new DashboardWidget("UserLastModifiedDocuments",
				"dashboard.user.last.modified.documents", "img/icon/actions/checkin.gif", true,
				"userLastModifiedDocuments");
		lastDownloadedDocuments = new DashboardWidget("UserLastDownloadedDocuments",
				"dashboard.user.last.downloaded.documents", "img/icon/actions/download.gif", false,
				"userLastDownloadedDocuments");
		subscribedDocuments = new DashboardWidget("UserSubscribedDocuments", "dashboard.user.subscribed.documents",
				"img/icon/subscribed.gif", false, "userSubscribedDocuments");
		subscribedFolder = new DashboardWidget("UserSubscribedFolders", "dashboard.user.subscribed.folders",
				"img/icon/subscribed.gif", false, "userSubscribedFolders");
		lastUploadedDocuments = new DashboardWidget("UserLastUploadedDocuments",
				"dashboard.user.last.uploaded.documents", "img/icon/actions/add_document.gif", true,
				"userLastUploadedDocuments");

		vPanelLeft.add(lockedDocuments);
		vPanelLeft.add(chechoutDocuments);
		vPanelLeft.add(lastDownloadedDocuments);
		vPanelRight.add(lastModifiedDocuments);
		vPanelRight.add(lastUploadedDocuments);
		vPanelLeft.add(subscribedDocuments);
		vPanelLeft.add(subscribedFolder);

		initWidget(hPanel);
	}

	/**
	 * getCheckouts
	 *
	 * @return
	 */
	public int getCheckouts() {
		return checkouts;
	}

	/**
	 * @param checkouts
	 */
	public void setCheckouts(int checkouts) {
		this.checkouts = checkouts;
	}

	/**
	 * Refreshing language
	 */
	public void langRefresh() {
		lockedDocuments.langRefresh();
		chechoutDocuments.langRefresh();
		lastModifiedDocuments.langRefresh();
		lastDownloadedDocuments.langRefresh();
		subscribedDocuments.langRefresh();
		subscribedFolder.langRefresh();
		lastUploadedDocuments.langRefresh();
	}

	/**
	 * setWidth
	 *
	 * @param width
	 */
	public void setWidth(int width) {
		int columnWidth = width / NUMBER_OF_COLUMNS;

		// Trying to distribute widgets on columns with max size
		lockedDocuments.setWidth(columnWidth);
		chechoutDocuments.setWidth(columnWidth);
		lastModifiedDocuments.setWidth(columnWidth);
		lastDownloadedDocuments.setWidth(columnWidth);
		subscribedDocuments.setWidth(columnWidth);
		subscribedFolder.setWidth(columnWidth);
		lastUploadedDocuments.setWidth(columnWidth);
	}

	/**
	 * Gets the locked documents callback
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetUserLockedDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result) {
			lockedDocuments.setDocuments(result);
			lockedDocuments.setHeaderResults(result.size());
			Main.get().mainPanel.bottomPanel.userInfo.setLockedDocuments(result.size());
			lockedDocuments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getUserLockedDocuments", caught);
			lockedDocuments.unsetRefreshing();
		}
	};

	/**
	 * Gets the checkout documents callback
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetUserCheckOutDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result) {
			checkouts = result.size();
			chechoutDocuments.setDocuments(result);
			chechoutDocuments.setHeaderResults(checkouts);
			Main.get().mainPanel.bottomPanel.userInfo.setCheckoutDocuments(checkouts);
			chechoutDocuments.unsetRefreshing();
			checkoutDocumentFlag = false; // Marks rpc calls are finished and can checkout document
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getUserCheckedOutDocuments", caught);
			chechoutDocuments.unsetRefreshing();
		}
	};

	/**
	 * Gets last modified documents callback
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetUserLastModifiedDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result) {
			lastModifiedDocuments.setDocuments(result);
			lastModifiedDocuments.setHeaderResults(result.size());
			lastModifiedDocuments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getUserLastModifiedDocuments", caught);
			lastModifiedDocuments.unsetRefreshing();
		}
	};

	/**
	 * Get subscribed documents callback
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetUserSubscribedDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result) {
			subscribedDocuments.setDocuments(result);
			subscribedDocuments.setHeaderResults(result.size());
			tmpSubscriptions = result.size();
			getUserSubscribedFolders();
			subscribedDocuments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getUserSubscribedDocuments", caught);
			subscribedDocuments.unsetRefreshing();
		}
	};

	/**
	 * Gets the subscribed folders
	 */
	final AsyncCallback<List<GWTDashboardFolderResult>> callbackGetUserSubscribedFolders = new AsyncCallback<List<GWTDashboardFolderResult>>() {
		public void onSuccess(List<GWTDashboardFolderResult> result) {
			subscribedFolder.setFolders(result);
			subscribedFolder.setHeaderResults(result.size());
			tmpSubscriptions += result.size();
			Main.get().mainPanel.bottomPanel.userInfo.setSubscriptions(tmpSubscriptions);
			subscribedFolder.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getUserSubscribedFolders", caught);
			subscribedFolder.unsetRefreshing();
		}
	};

	/**
	 * Gets the downloaded documents
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetUserLastDownloadedDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result) {
			lastDownloadedDocuments.setDocuments(result);
			lastDownloadedDocuments.setHeaderResults(result.size());
			lastDownloadedDocuments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getUserLastDownloadedDocuments", caught);
			lastDownloadedDocuments.unsetRefreshing();
		}
	};

	/**
	 * Gets the last uploaded documents
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetUserLastUploadedDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result) {
			lastUploadedDocuments.setDocuments(result);
			lastUploadedDocuments.setHeaderResults(result.size());
			lastUploadedDocuments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("callbackGetUserLastUploadedDocuments", caught);
			lastUploadedDocuments.unsetRefreshing();
		}
	};

	/**
	 * getUserLockedDocuments
	 */
	public void getUserLockedDocuments() {
		if (showStatus) {
			lockedDocuments.setRefreshing();
		}

		dashboardService.getUserLockedDocuments(callbackGetUserLockedDocuments);
	}

	/**
	 * setPendingCheckoutDocumentFlag
	 *
	 * Flag to ensure RPC calls are finished
	 */
	public void setPendingCheckoutDocumentFlag() {
		checkoutDocumentFlag = true;
	}

	public boolean isPendingCheckoutDocumentFlag() {
		return checkoutDocumentFlag;
	}

	/**
	 * getUserCheckedOutDocuments
	 */
	public void getUserCheckedOutDocuments() {
		if (showStatus) {
			chechoutDocuments.setRefreshing();
		}

		dashboardService.getUserCheckedOutDocuments(callbackGetUserCheckOutDocuments);
	}

	/**
	 * getUserLastModifiedDocuments
	 */
	public void getUserLastModifiedDocuments() {
		if (showStatus) {
			lastModifiedDocuments.setRefreshing();
		}

		dashboardService.getUserLastModifiedDocuments(callbackGetUserLastModifiedDocuments);
	}

	/**
	 * getUserSubscribedDocuments
	 */
	public void getUserSubscribedDocuments() {
		if (showStatus) {
			subscribedDocuments.setRefreshing();
		}

		dashboardService.getUserSubscribedDocuments(callbackGetUserSubscribedDocuments);
	}

	/**
	 * getUserSubscribedFolders
	 */
	public void getUserSubscribedFolders() {
		if (showStatus) {
			subscribedFolder.setRefreshing();
		}

		dashboardService.getUserSubscribedFolders(callbackGetUserSubscribedFolders);
	}

	/**
	 * getUserLastDownloadedDocuments
	 */
	public void getUserLastDownloadedDocuments() {
		if (showStatus) {
			lastDownloadedDocuments.setRefreshing();
		}

		dashboardService.getUserLastDownloadedDocuments(callbackGetUserLastDownloadedDocuments);
	}

	/**
	 * getUserLastUploadedDocuments
	 */
	public void getUserLastUploadedDocuments() {
		if (showStatus) {
			lastUploadedDocuments.setRefreshing();
		}

		dashboardService.getUserLastUploadedDocuments(callbackGetUserLastUploadedDocuments);
	}

	/**
	 * Refresh all panels
	 */
	public void refreshAll() {
		showStatus = ((Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace() == UIDockPanelConstants.DASHBOARD) &&
				(Main.get().mainPanel.dashboard.getActualView() == UIDashboardConstants.DASHBOARD_USER));
		getUserLockedDocuments();
		getUserCheckedOutDocuments();
		getUserLastModifiedDocuments();
		getUserSubscribedDocuments();
		getUserLastDownloadedDocuments();
		getUserLastUploadedDocuments();
	}
}
