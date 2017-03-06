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
import com.openkm.frontend.client.bean.GWTDashboardMailResult;
import com.openkm.frontend.client.constants.ui.UIDashboardConstants;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.service.OKMDashboardService;
import com.openkm.frontend.client.service.OKMDashboardServiceAsync;

import java.util.List;

/**
 * MailDashboard
 *
 * @author jllort
 *
 */
public class MailDashboard extends Composite {
	private final OKMDashboardServiceAsync dashboardService = (OKMDashboardServiceAsync) GWT.create(OKMDashboardService.class);

	private final int NUMBER_OF_COLUMNS = 2;

	private HorizontalPanel hPanel;
	private VerticalPanel vPanelLeft;
	private VerticalPanel vPanelRight;

	private DashboardWidget userLastImportedMails;
	private DashboardWidget userLastImportedAttachments;

	private boolean showStatus = false;

	/**
	 * GeneralDashboard
	 */
	public MailDashboard() {
		vPanelLeft = new VerticalPanel();
		vPanelRight = new VerticalPanel();
		hPanel = new HorizontalPanel();

		userLastImportedMails = new DashboardWidget("UserLastImportedMails",
				"dashboard.mail.last.imported.mails", "img/email.gif", true, "userLastImportedMails");
		userLastImportedAttachments = new DashboardWidget("UserLastImportedMailAttachments",
				"dashboard.mail.last.imported.attached.documents", "img/email_attach.gif", true,
				"userLastImportedMailAttachments");

		vPanelLeft.add(userLastImportedMails);
		vPanelRight.add(userLastImportedAttachments);

		hPanel.add(vPanelLeft);
		hPanel.add(vPanelRight);

		initWidget(hPanel);
	}

	/**
	 * Refreshing language
	 */
	public void langRefresh() {
		userLastImportedMails.langRefresh();
		userLastImportedAttachments.langRefresh();
	}

	/**
	 * setWidth
	 *
	 * @param width
	 */
	public void setWidth(int width) {
		int columnWidth = width / NUMBER_OF_COLUMNS;

		// Trying to distribute widgets on columns with max size
		userLastImportedMails.setWidth(columnWidth);
		userLastImportedAttachments.setWidth(columnWidth);
	}

	/**
	 * Get last user imported mails callback
	 */
	final AsyncCallback<List<GWTDashboardMailResult>> callbackGetUserLastImportedMails = new AsyncCallback<List<GWTDashboardMailResult>>() {
		public void onSuccess(List<GWTDashboardMailResult> result) {
			userLastImportedMails.setMails(result);
			userLastImportedMails.setHeaderResults(result.size());
			userLastImportedMails.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getUserLastImportedMails", caught);
			userLastImportedMails.unsetRefreshing();
		}
	};

	/**
	 * Gets last imported mail attachments documents callback
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetUserLastImportedMailAttachments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result) {
			userLastImportedAttachments.setDocuments(result);
			userLastImportedAttachments.setHeaderResults(result.size());
			userLastImportedAttachments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getUserLastImportedMailAttachments", caught);
			userLastImportedAttachments.unsetRefreshing();
		}
	};

	/**
	 * getLastWeekTopDownloadedDocuments
	 */
	public void getUserLastImportedMails() {
		if (!showStatus) {
			userLastImportedMails.setRefreshing();
		}
		dashboardService.getUserLastImportedMails(callbackGetUserLastImportedMails);
	}

	/**
	 * getLastModifiedDocuments
	 */
	public void getUserLastImportedMailAttachments() {
		if (!showStatus) {
			userLastImportedAttachments.setRefreshing();
		}
		dashboardService.getUserLastImportedMailAttachments(callbackGetUserLastImportedMailAttachments);
	}

	/**
	 * Refresh all panels
	 */
	public void refreshAll() {
		showStatus = ((Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace() == UIDockPanelConstants.DASHBOARD) &&
				(Main.get().mainPanel.dashboard.getActualView() == UIDashboardConstants.DASHBOARD_MAIL));
		getUserLastImportedMails();
		getUserLastImportedMailAttachments();
	}
}
