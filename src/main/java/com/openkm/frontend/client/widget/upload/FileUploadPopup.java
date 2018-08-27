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

package com.openkm.frontend.client.widget.upload;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.FileToUpload;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.constants.ui.UIFileUploadConstants;

import java.util.Collection;

/**
 * File Upload
 *
 * @author jllort
 *
 */
public class FileUploadPopup extends DialogBox {
	private Button closeButton;
	private Button addButton;
	private Button sendButton;
	private VerticalPanel vPanel;
	private HorizontalPanel vButtonPanel;
	private FancyFileUpload ffUpload;
	private int popupWidth = 415;
	private int popupHeight = 125;
	private boolean enableAddButton = false;

	/**
	 * File upload
	 */
	public FileUploadPopup() {
		super(false, false); // Modal = true indicates popup is centered
		ffUpload = new FancyFileUpload();
		vPanel = new VerticalPanel();
		vButtonPanel = new HorizontalPanel();

		closeButton = new Button(Main.i18n("fileupload.button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				executeClose();
			}
		}
		);
		addButton = new Button(Main.i18n("fileupload.button.add.other.file"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addButton.setVisible(false); // Add new file button must be unvisible after clicking
				sendButton.setVisible(true);
				FileToUpload fileToUpload = new FileToUpload();
				fileToUpload.setFileUpload(new FileUpload());
				fileToUpload.setPath(Main.get().activeFolderTree.getActualPath());
				fileToUpload.setAction(UIFileUploadConstants.ACTION_INSERT);
				addPendingFileToUpload(fileToUpload);
			}
		}
		);
		addButton.setVisible(false);

		sendButton = new Button();
		sendButton.setText(Main.i18n("fileupload.button.send"));

		// Set up a click listener on the proceed check box
		sendButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				executeSend();
			}
		});

		vPanel.setWidth("415px");
		vPanel.setHeight("100px");

		vPanel.add(new HTML("<br/>"));
		vPanel.add(ffUpload);

		ffUpload.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (ffUpload.getUploadState() == FancyFileUpload.PENDING_STATE ||
						ffUpload.getUploadState() == FancyFileUpload.UPLOADING_STATE) {
//            		if (ffUpload.isPendingFileToUpload()) {
//            			addButton.setVisible(false);
//            			sendButton.setVisible(true);
//            		}
				} else if (ffUpload.getUploadState() == FancyFileUpload.EMPTY_STATE ||
						ffUpload.getUploadState() == FancyFileUpload.FAILED_STATE ||
						ffUpload.getUploadState() == FancyFileUpload.UPLOADED_STATE) {
					if (ffUpload.getUploadState() != FancyFileUpload.EMPTY_STATE && enableAddButton) {
//            			if (ffUpload.getUploadState() == FancyFileUpload.UPLOADED_STATE) {
//            				//boolean visible = !ffUpload.isWizard();
//            				//closeButton.setVisible(visible);
//            				//sendButton.setVisible(false);
//           					//addButton.setVisible(visible);
//            			} else {
//            				addButton.setVisible(true);
//            				sendButton.setVisible(false);
//            			}
					} else {
						// on failed or empty state
						if (ffUpload.getAction() == UIFileUploadConstants.ACTION_UPDATE &&
								(ffUpload.getUploadState() != FancyFileUpload.EMPTY_STATE &&
										ffUpload.getUploadState() != FancyFileUpload.FAILED_STATE)) {
							sendButton.setVisible(false); // checkin case
						}
					}
				}
			}
		});

		vButtonPanel.add(closeButton);
		vButtonPanel.add(new HTML("&nbsp;&nbsp;"));
		vButtonPanel.add(addButton);
		vButtonPanel.add(new HTML("&nbsp;&nbsp;"));
		vButtonPanel.add(sendButton);

		vPanel.add(vButtonPanel);
		vPanel.add(new HTML("<br/>"));

		vPanel.setCellHorizontalAlignment(ffUpload, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(vButtonPanel, VerticalPanel.ALIGN_CENTER);

		closeButton.setStyleName("okm-NoButton");
		addButton.setStyleName("okm-AddButton");
		sendButton.setStyleName("okm-AddButton");

		setWidget(vPanel);
	}

	/**
	 * executeClose
	 */
	protected void executeClose() {
		setPopupPosition(-450, 0);
		setModal(false);
		addButton.setVisible(false);
		sendButton.setVisible(false);
		ffUpload.close();
	}

	/**
	 * executeSend
	 */
	protected void executeSend() {
		if (Main.get().mainPanel.bottomPanel.userInfo.isQuotaExceed()) {
			Main.get().showError("UserQuotaExceed", new OKMException("OKM-" + ErrorCode.ORIGIN_OKMBrowser + ErrorCode.CAUSE_QuotaExceed, ""));
		} else {
			ffUpload.mails.setText(ffUpload.notifyPanel.getExternalMailAddress());
			;
			ffUpload.users.setText(ffUpload.notifyPanel.getUsersToNotify());
			ffUpload.roles.setText(ffUpload.notifyPanel.getRolesToNotify());

			if (ffUpload.notifyToUser.getValue() && ffUpload.users.getText().equals("") && ffUpload.roles.getText().equals("") && ffUpload.mails.getText().equals("")) {
				ffUpload.errorNotify.setVisible(true);
			} else if (ffUpload.getFilename() != null && !ffUpload.getFilename().equals("")) {
				addButton.setVisible(true);
				sendButton.setVisible(false);
				ffUpload.pendingUpload();
				resetOnlyShowUploading();
			}
		}
	}


	/**
	 * Language refresh
	 */
	public void langRefresh() {
		closeButton.setHTML(Main.i18n("button.close"));
		addButton.setHTML(Main.i18n("fileupload.button.add.other.file"));
		sendButton.setText(Main.i18n("fileupload.button.send"));

		if (ffUpload.getAction() == UIFileUploadConstants.ACTION_INSERT) {
			setText(Main.i18n("fileupload.label.insert"));
		} else {
			setText(Main.i18n("fileupload.label.update"));
		}

		ffUpload.langRefresh();
	}

	/**
	 * Show file upload popup
	 */
	protected void showPopup(boolean enableAddButton, boolean enableImport, boolean enableNotifyButton) {
		this.enableAddButton = enableAddButton;
		setWidth("" + popupWidth);
		setHeight("" + popupHeight);
		ffUpload.init(); // Inits to correct center position
		center();

		// Allways must initilize htmlForm for tree path initialization
		langRefresh();
		if (!ffUpload.isActualFileUploading() && !ffUpload.isPendingOnFileUploadQueue()) {
			ffUpload.reset(enableImport, enableNotifyButton);
		}
		setModal(true);
	}

	/**
	 * Hide file upload
	 */
	@Override
	public void hide() {
		if (ffUpload.getAction() == UIFileUploadConstants.ACTION_UPDATE) {
			if (Main.get().mainPanel.desktop.browser.fileBrowser.table.isDocumentSelected()) {
				GWTDocument doc = Main.get().mainPanel.desktop.browser.fileBrowser.table.getDocument();
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.setProperties(doc);
			}
		}
		super.hide();
	}

	/**
	 * resetOnlyShowUploading
	 */
	public void resetOnlyShowUploading() {
		ffUpload.resetOnlyShowUploading();
	}

	/**
	 * resetAfterWizardFinished
	 */
	public void resetAfterWizardFinished(boolean refresh) {
		ffUpload.refresh(refresh);
		closeButton.setVisible(true);
		addButton.setVisible(true);
		super.show();
	}

	/**
	 * disableErrorNotify
	 */
	public void disableErrorNotify() {
		ffUpload.disableErrorNotify();
	}

	/**
	 * enableAdvancedFilter
	 */
	public void enableAdvancedFilter() {
		ffUpload.enableAdvancedFilter();
	}

	/**
	 * enableNotifyExternalUsers
	 */
	public void enableNotifyExternalUsers() {
		ffUpload.enableNotifyExternalUsers();
	}

	/**
	 * setIncrementalVersion
	 */
	public void setIncreaseVersion(int increaseVersion) {
		ffUpload.setIncreaseVersion(increaseVersion);
	}

	/**
	 * @param filesToUpload
	 */
	public void enqueueFileToUpload(Collection<FileToUpload> filesToUpload) {
		ffUpload.enqueueFileToUpload(filesToUpload);
	}

	/**
	 * addPendingFileToUpload
	 *
	 * @param pendingFileToUpload
	 */
	public void addPendingFileToUpload(FileToUpload pendingFileToUpload) {
		setModal(true);
		addButton.setVisible(false);
		sendButton.setVisible(true);
		ffUpload.addPendingFileToUpload(pendingFileToUpload);
	}

	/**
	 * setUploadNotifyUsers
	 * @param visible
	 */
	public void setUploadNotifyUsers(boolean visible) {
		ffUpload.setUploadNotifyUsers(visible);
	}

	/**
	 * initJavaScriptApi
	 */
	public void initJavaScriptApi() {
		ffUpload.initJavaScriptApi(ffUpload);
	}
}