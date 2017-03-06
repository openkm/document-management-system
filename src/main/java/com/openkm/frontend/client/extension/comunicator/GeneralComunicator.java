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

package com.openkm.frontend.client.extension.comunicator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTWorkspace;
import com.openkm.frontend.client.bean.ToolBarOption;
import com.openkm.frontend.client.service.OKMRepositoryService;
import com.openkm.frontend.client.service.OKMRepositoryServiceAsync;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.Util;

import java.util.List;


/**
 * GeneralComunicator
 *
 * @author jllort
 *
 */
public class GeneralComunicator {
	private static final OKMRepositoryServiceAsync repositoryService = (OKMRepositoryServiceAsync) GWT.create(OKMRepositoryService.class);

	/**
	 * refreshUI
	 */
	public static void refreshUI() {
		Main.get().mainPanel.topPanel.toolBar.executeRefresh();
	}

	/**
	 * getToolBarOption
	 */
	public static ToolBarOption getToolBarOption() {
		return Main.get().mainPanel.topPanel.toolBar.getToolBarOption();
	}

	/**
	 * getLang
	 */
	public static String getLang() {
		return Main.get().getLang();
	}

	/**
	 * i18nExtension
	 */
	public static String i18nExtension(String property) {
		return Main.get().i18nExtension(property);
	}

	/**
	 * i18n
	 */
	public static String i18n(String property) {
		return Main.i18n(property);
	}

	/**
	 * Download Document
	 */
	public static void downloadDocument(boolean checkout) {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
			String docUuid = Main.get().mainPanel.desktop.browser.fileBrowser.getDocument().getUuid();
			Util.downloadFileByUUID(docUuid, (checkout ? "checkout" : ""));
		}
	}

	/**
	 * Download document as PDF
	 */
	public static void downloadDocumentPdf() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
			Util.downloadFilePdf(Main.get().mainPanel.desktop.browser.fileBrowser.getDocument().getUuid());
		}
	}

	/**
	 * extensionCallOwnDownload
	 */
	public static void extensionCallOwnDownload(String url) {
		final Element downloadIframe = RootPanel.get("__download").getElement();
		DOM.setElementAttribute(downloadIframe, "src", url);
	}

	/**
	 * download file by uuid
	 */
	public static void downloadFileByUUID(String uuid, String params) {
		Util.downloadFileByUUID(uuid, params);
	}

	/**
	 * download file by path
	 */
	@Deprecated
	public static void downloadFile(String path, final String params) {
		repositoryService.getUUIDByPath(path, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				Util.downloadFileByUUID(result, params);
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("getUUIDByPath", caught);
			}
		});

	}

	/**
	 * Sets the status
	 */
	public static void setStatus(String msg) {
		Main.get().mainPanel.bottomPanel.setStatus(msg);
	}

	/**
	 * Sets the status
	 *
	 */
	public static void resetStatus() {
		Main.get().mainPanel.bottomPanel.resetStatus();
	}

	/**
	 * showError
	 */
	public static void showError(String callback, Throwable caught) {
		Main.get().showError(callback, caught);
	}

	/**
	 * logout
	 */
	public static void logout() {
		Main.get().logoutPopup.logout();
	}

	/**
	 * refreshUserDocumentsSize
	 */
	public static void refreshUserDocumentsSize() {
		Main.get().workspaceUserProperties.getUserDocumentsSize();
	}

	/**
	 * getUserRoleList
	 */
	public static List<String> getUserRoleList() {
		return Main.get().workspaceUserProperties.getWorkspace().getRoleList();
	}

	/**
	 * getUser
	 */
	public static String getUser() {
		return Main.get().workspaceUserProperties.getUser().getId();
	}

	/**
	 * openAllFolderPath
	 */
	public static void openPath(String path, String docPath) {
		CommonUI.openPath(path, docPath);
	}

	/**
	 * openAllFolderPath
	 */
	public static void openAllFolderPath(String path, String docPath) {
		CommonUI.openPath(path, docPath);
	}

	/**
	 * getAppContext
	 */
	public static String getAppContext() {
		return Main.CONTEXT;
	}

	/**
	 * showNextWizard
	 */
	public static void showNextWizard() {
		Main.get().wizardPopup.showNextWizard();
	}

	/**
	 * getDocumentToSign
	 */
	public static GWTDocument getDocumentToSign() {
		return Main.get().wizardPopup.getDocumentToSign();
	}

	/**
	 * getSessionId
	 */
	public static String getSessionId() {
		return Main.get().workspaceUserProperties.getWorkspace().getSessionId();
	}

	/**
	 * getWorkspace
	 */
	public static GWTWorkspace getWorkspace() {
		return Main.get().workspaceUserProperties.getWorkspace();
	}

	/**
	 * enableKeyShorcuts
	 */
	public static void enableKeyShorcuts() {
		Main.get().mainPanel.enableKeyShorcuts();
	}

	/**
	 * disableKeyShorcuts
	 */
	public static void disableKeyShorcuts() {
		Main.get().mainPanel.disableKeyShorcuts();
	}

	/**
	 * openPathByUuid
	 */
	public static void openPathByUuid(String uuid) {
		CommonUI.openPathByUuid(uuid);
	}

	/**
	 * getFolderIcon
	 */
	public static String getFolderIcon(GWTFolder fld) {
		return CommonUI.getFolderIcon(fld);
	}

	/**
	 * get
	 *
	 * @return
	 */
	public static Main get() {
		return Main.get();
	}
}