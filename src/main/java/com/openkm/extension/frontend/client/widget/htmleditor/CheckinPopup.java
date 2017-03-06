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

package com.openkm.extension.frontend.client.widget.htmleditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.extension.comunicator.FileBrowserComunicator;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.service.OKMDocumentService;
import com.openkm.frontend.client.service.OKMDocumentServiceAsync;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.notify.NotifyPanel;

/**
 * CheckinPopup
 *
 * @author jllort
 */
public class CheckinPopup extends DialogBox {
	private final OKMDocumentServiceAsync documentService = (OKMDocumentServiceAsync) GWT.create(OKMDocumentService.class);

	private Button closeButton;
	private Button sendButton;
	private HorizontalPanel hPanel;
	private VerticalPanel vPanel;
	private VerticalPanel vNotifyPanel;
	private ScrollPanel messageScroll;
	private TextArea message;
	private HTML commentTXT;
	private HorizontalPanel hIncreaseVersionPanel;
	private CheckBox increaseMajorVersion;
	private CheckBox increaseMinorVersion;
	public NotifyPanel notifyPanel;
	private HorizontalPanel vButtonPanel;
	private HTML versionCommentText = new HTML();
	private TextArea versionComment;
	private ScrollPanel versionCommentScrollPanel;
	private VerticalPanel vVersionCommentPanel;
	public CheckBox notifyToUser;
	private HTML notifyToUserText;
	private HorizontalPanel hNotifyPanel;
	private HTML errorNotify;
	private String docPath;

	/**
	 * CheckinPopup
	 */
	public CheckinPopup() {
		super(false, false); // Modal = true indicates popup is centered
		setText(GeneralComunicator.i18n("fileupload.label.update"));

		hPanel = new HorizontalPanel();
		hPanel.setWidth("100%");
		vPanel = new VerticalPanel();

		// Notification error
		errorNotify = new HTML(GeneralComunicator.i18n("fileupload.label.must.select.users"));
		errorNotify.setWidth("364px");
		errorNotify.setVisible(false);
		errorNotify.setStyleName("fancyfileupload-failed");
		errorNotify.setVisible(false);

		// Comment
		versionCommentText = new HTML();
		versionComment = new TextArea();
		versionComment.setWidth("375px");
		versionComment.setHeight("50px");
		versionComment.setName("comment");
		versionComment.setStyleName("okm-TextArea");
		versionCommentText = new HTML(GeneralComunicator.i18n("fileupload.label.comment"));

		// TODO This is a workaround for a Firefox 2 bug
		// http://code.google.com/p/google-web-toolkit/issues/detail?id=891
		// Table for solve some visualization problems
		vVersionCommentPanel = new VerticalPanel();
		versionCommentScrollPanel = new ScrollPanel(versionComment);
		versionCommentScrollPanel.setAlwaysShowScrollBars(false);
		versionCommentScrollPanel.setSize("100%", "100%");
		vVersionCommentPanel.add(versionCommentText);
		vVersionCommentPanel.add(versionCommentScrollPanel);

		// Increase version
		increaseMajorVersion = new CheckBox();
		increaseMinorVersion = new CheckBox();
		increaseMajorVersion.setHTML(Main.i18n("fileupload.increment.major.version"));
		increaseMinorVersion.setHTML(Main.i18n("fileupload.increment.minor.version"));
		increaseMajorVersion.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				increaseMinorVersion.setValue(false);
			}
		});
		increaseMinorVersion.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				increaseMajorVersion.setValue(false);
			}
		});
		hIncreaseVersionPanel = new HorizontalPanel();
		hIncreaseVersionPanel.add(increaseMajorVersion);
		hIncreaseVersionPanel.add(Util.hSpace("5px"));
		hIncreaseVersionPanel.add(increaseMinorVersion);

		// Enable disable notification panel
		notifyToUser = new CheckBox();
		notifyToUser.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (notifyToUser.getValue()) {
					vNotifyPanel.setVisible(true);
					// TODO:Solves minor bug with IE
					if (Util.getUserAgent().startsWith("ie")) {
						notifyPanel.tabPanel.setWidth("374px");
						notifyPanel.tabPanel.setWidth("375px");
						notifyPanel.correcIEBug();
					}
				} else {
					vNotifyPanel.setVisible(false);
				}
			}
		});
		notifyToUserText = new HTML(GeneralComunicator.i18n("fileupload.label.users.notify"));

		hNotifyPanel = new HorizontalPanel();
		hNotifyPanel.add(notifyToUser);
		hNotifyPanel.add(notifyToUserText);
		hNotifyPanel.setCellVerticalAlignment(notifyToUser, VerticalPanel.ALIGN_MIDDLE);
		hNotifyPanel.setCellVerticalAlignment(notifyToUserText, VerticalPanel.ALIGN_MIDDLE);

		// Notification panel
		vNotifyPanel = new VerticalPanel();
		commentTXT = new HTML(GeneralComunicator.i18n("fileupload.label.notify.comment"));
		message = new TextArea();
		message.setName("message");
		message.setSize("375px", "60px");
		message.setStyleName("okm-TextArea");

		// TODO This is a workaround for a Firefox 2 bug
		// http://code.google.com/p/google-web-toolkit/issues/detail?id=891
		messageScroll = new ScrollPanel(message);
		messageScroll.setAlwaysShowScrollBars(false);

		notifyPanel = new NotifyPanel();
		vNotifyPanel.add(commentTXT);
		vNotifyPanel.add(messageScroll);
		vNotifyPanel.add(errorNotify);
		vNotifyPanel.add(new HTML("<br/>"));
		vNotifyPanel.add(notifyPanel);
		vNotifyPanel.add(new HTML("<br/>"));

		// Buttons
		vButtonPanel = new HorizontalPanel();
		closeButton = new Button(GeneralComunicator.i18n("fileupload.button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		closeButton.setStyleName("okm-NoButton");

		// Set up a click listener on the proceed check box
		sendButton = new Button(GeneralComunicator.i18n("fileupload.button.send"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String mails = notifyPanel.getExternalMailAddress();
				String users = notifyPanel.getUsersToNotify();
				String roles = notifyPanel.getRolesToNotify();
				String messageToSend = (notifyToUser.getValue()) ? message.getText() : "";

				if (!notifyToUser.getValue() || (notifyToUser.getValue() && (!users.equals("") || !roles.equals("") || !mails.equals("")))) {
					sendButton.setEnabled(false);
					int increaseVersion = 0;
					if (increaseMajorVersion.getValue()) {
						increaseVersion = 1;
					} else if (increaseMinorVersion.getValue()) {
						increaseVersion = 2;
					}
					HTMLEditor.get().status.setSetHTML();
					documentService.setHTMLContent(docPath, mails, users, roles, messageToSend, HTMLEditor.get().getTexteAreaText(),
							versionComment.getText(), increaseVersion, new AsyncCallback<String>() {
								@Override
								public void onSuccess(String result) {
									hide();
									FileBrowserComunicator.refreshOnlyFileBrowser();
									Main.get().mainPanel.dashboard.userDashboard.getUserLastModifiedDocuments();
									Main.get().mainPanel.dashboard.userDashboard.getUserCheckedOutDocuments();
									HTMLEditor.get().status.unsetSetHTML();
								}

								@Override
								public void onFailure(Throwable caught) {
									HTMLEditor.get().status.unsetSetHTML();
									GeneralComunicator.showError("setHTMLContent", caught);
								}
							});

					errorNotify.setVisible(false);
				} else {
					errorNotify.setVisible(true);
				}
			}
		});

		sendButton.setStyleName("okm-AddButton");

		vButtonPanel.add(closeButton);
		vButtonPanel.add(new HTML("&nbsp;&nbsp;"));
		vButtonPanel.add(sendButton);

		// Popup main panel
		vPanel.add(new HTML("<br/>"));
		vPanel.add(vVersionCommentPanel);
		vPanel.add(hIncreaseVersionPanel);
		vPanel.add(hNotifyPanel);
		vPanel.add(new HTML("<br/>"));
		vPanel.add(vNotifyPanel);
		vPanel.add(new HTML("<br/>"));
		vPanel.add(vButtonPanel);
		vPanel.add(new HTML("<br/>"));
		vPanel.setCellHorizontalAlignment(vVersionCommentPanel, HasAlignment.ALIGN_LEFT);
		vPanel.setCellHorizontalAlignment(hNotifyPanel, HasAlignment.ALIGN_LEFT);
		vPanel.setCellHorizontalAlignment(vNotifyPanel, HasAlignment.ALIGN_LEFT);
		vPanel.setCellHorizontalAlignment(vButtonPanel, HasAlignment.ALIGN_CENTER);

		hPanel.add(vPanel);
		hPanel.setCellHorizontalAlignment(vPanel, HasAlignment.ALIGN_CENTER);

		setWidget(hPanel);
	}

	/**
	 * reset
	 */
	public void reset(String docPath) {
		this.docPath = docPath;
		increaseMajorVersion.setValue(false);
		increaseMinorVersion.setValue(false);
		notifyPanel.reset();
		versionComment.setText("");
		message.setText("");
		notifyPanel.getAll();
		notifyToUser.setValue(false);
		vNotifyPanel.setVisible(false);
		errorNotify.setVisible(false);
		sendButton.setEnabled(true);
	}

	/**
	 * enableAdvancedFilter
	 */
	public void enableAdvancedFilter() {
		notifyPanel.enableAdvancedFilter();
	}

	/**
	 * enableNotifyExternalUsers
	 */
	public void enableNotifyExternalUsers() {
		notifyPanel.enableNotifyExternalUsers();
	}

	/**
	 * lanfRefresh
	 */
	public void langRefresh() {
		setText(GeneralComunicator.i18n("fileupload.label.update"));
		increaseMajorVersion.setHTML(Main.i18n("fileupload.increment.major.version"));
		increaseMinorVersion.setHTML(Main.i18n("fileupload.increment.minor.version"));
		closeButton.setText(GeneralComunicator.i18n("fileupload.button.close"));
		sendButton.setText(GeneralComunicator.i18n("fileupload.button.send"));
		commentTXT.setHTML(GeneralComunicator.i18n("fileupload.label.notify.comment"));
		versionCommentText.setHTML(GeneralComunicator.i18n("fileupload.label.comment"));
		notifyToUserText.setHTML(GeneralComunicator.i18n("fileupload.label.users.notify"));
		errorNotify.setHTML(GeneralComunicator.i18n("fileupload.label.must.select.users"));
		notifyPanel.langRefresh();
	}

	/**
	 * setIncrementalVersion
	 */
	public void setIncreaseVersion(int incrementVersion) {
		if (incrementVersion == 0) {
			vPanel.remove(hIncreaseVersionPanel);
		} else if (incrementVersion == 1) {
			hIncreaseVersionPanel.remove(increaseMinorVersion);
		}
	}
}