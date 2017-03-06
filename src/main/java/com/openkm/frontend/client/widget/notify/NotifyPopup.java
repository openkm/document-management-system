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

package com.openkm.frontend.client.widget.notify;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.service.*;
import com.openkm.frontend.client.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * NotifyPopup
 *
 * @author jllort
 */
public class NotifyPopup extends DialogBox {

	private static final int NONE = -1;
	public static final int NOTIFY_WITH_LINK = 0;
	public static final int NOTIFY_WITH_ATTACHMENT = 1;
	public static final int FORWARD_MAIL = 2;

	private final OKMNotifyServiceAsync notifyService = (OKMNotifyServiceAsync) GWT.create(OKMNotifyService.class);
	private final OKMMassiveServiceAsync massiveService = (OKMMassiveServiceAsync) GWT.create(OKMMassiveService.class);
	private final OKMMailServiceAsync mailService = (OKMMailServiceAsync) GWT.create(OKMMailService.class);

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private Button closeButton;
	private Button sendButton;
	private TextArea message;
	private ScrollPanel messageScroll;
	private NotifyPanel notifyPanel;
	private HTML commentTXT;
	private HTML errorNotify;
	private String mails;
	private String users;
	private String roles;
	private String path;
	private List<String> uuidList;
	private int type = NONE;
	private boolean isMassive = false;

	public NotifyPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		setText(Main.i18n("notify.label"));
		mails = "";
		users = "";
		roles = "";
		uuidList = new ArrayList<String>();

		vPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();
		notifyPanel = new NotifyPanel();
		message = new TextArea();
		message.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				sendButton.setEnabled(message.getText().trim().length() > 0);
			}
		});

		errorNotify = new HTML(Main.i18n("fileupload.label.must.select.users"));
		errorNotify.setWidth("364px");
		errorNotify.setVisible(false);
		errorNotify.setStyleName("fancyfileupload-failed");

		commentTXT = new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + Main.i18n("fileupload.label.notify.comment"));

		closeButton = new Button(Main.i18n("fileupload.button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				reset(NONE);
			}
		});

		sendButton = new Button(Main.i18n("button.send"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Only sends if there's some user selected
				mails = notifyPanel.getExternalMailAddress();
				users = notifyPanel.getUsersToNotify();
				roles = notifyPanel.getRolesToNotify();

				if (!users.equals("") || !roles.equals("") || !mails.equals("")) {
					errorNotify.setVisible(false);
					sendLinkNotification();
					hide();
					reset(NONE);
				} else {
					errorNotify.setVisible(true);
				}
			}
		});

		hPanel.add(closeButton);
		HTML space = new HTML("");
		hPanel.add(space);
		hPanel.add(sendButton);

		hPanel.setCellWidth(space, "40px");

		message.setSize("374px", "60px");
		message.setStyleName("okm-TextArea");
		// TODO This is a workaround for a Firefox 2 bug
		// http://code.google.com/p/google-web-toolkit/issues/detail?id=891
		messageScroll = new ScrollPanel(message);
		messageScroll.setAlwaysShowScrollBars(false);

		vPanel.add(new HTML("<br>"));
		vPanel.add(commentTXT);
		vPanel.add(messageScroll);
		vPanel.add(errorNotify);
		vPanel.add(new HTML("<br>"));
		vPanel.add(notifyPanel);
		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(errorNotify, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(messageScroll, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(notifyPanel, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, VerticalPanel.ALIGN_CENTER);

		vPanel.setWidth("100%");

		closeButton.setStyleName("okm-NoButton");
		sendButton.setStyleName("okm-YesButton");

		commentTXT.addStyleName("okm-DisableSelect");
		notifyPanel.addStyleName("okm-DisableSelect");

		setWidget(vPanel);
	}

	/**
	 * langRefresh Refreshing lang
	 */
	public void langRefresh() {
		switch (type) {
			case NOTIFY_WITH_LINK:
				setText(Main.i18n("notify.label"));
				break;

			case NOTIFY_WITH_ATTACHMENT:
				setText(Main.i18n("notify.label.attachment"));
				break;

			case FORWARD_MAIL:
				setText(Main.i18n("notify.label.forward.mail"));
				break;
		}

		closeButton.setHTML(Main.i18n("button.close"));
		sendButton.setHTML(Main.i18n("button.send"));
		commentTXT = new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + Main.i18n("fileupload.label.notify.comment"));
		errorNotify.setHTML(Main.i18n("fileupload.label.must.select.users"));
		notifyPanel.langRefresh();
	}

	/**
	 * executeSendDocument
	 */
	public void executeSendDocument(int type) {
		isMassive = (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()
				&& Main.get().mainPanel.desktop.browser.fileBrowser.table.getAllSelectedDocumentsUUIDs().size() > 0);
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
			reset(type);

			if (!isMassive) {
				path = Main.get().mainPanel.desktop.browser.fileBrowser.getDocument().getPath();
			} else {
				uuidList.addAll(Main.get().mainPanel.desktop.browser.fileBrowser.table.getAllSelectedDocumentsUUIDs());
			}

			super.center();
			message.setFocus(true);
			IEBugCorrection();
		}
	}

	/**
	 * executeSendDocument
	 */
	public void executeForwardMail() {
		isMassive = (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()
				&& Main.get().mainPanel.desktop.browser.fileBrowser.table.getAllSelectedMailUUIDs().size() > 0);
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isMailSelected()) {
			reset(FORWARD_MAIL);

			if (!isMassive) {
				path = Main.get().mainPanel.desktop.browser.fileBrowser.getMail().getPath();
			} else {
				uuidList.addAll(Main.get().mainPanel.desktop.browser.fileBrowser.table.getAllSelectedMailUUIDs());
			}

			super.center();
			message.setFocus(true);
			IEBugCorrection();
		}
	}

	/**
	 * IEBugCorrection
	 */
	private void IEBugCorrection() {
		// Another pathetic IE bug ( apologies if anyone is offended )
		if (Util.getUserAgent().startsWith("ie")) {
			notifyPanel.tabPanel.setWidth("374px");
			notifyPanel.tabPanel.setWidth("375px");
			notifyPanel.correcIEBug();
		}
	}

	/**
	 * Call back send link notification
	 */
	final AsyncCallback<Object> callbackNotify = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("notify", caught);
		}
	};

	/**
	 * Sens the link notification
	 */
	private void sendLinkNotification() {
		switch (type) {
			case NOTIFY_WITH_LINK:
				if (!isMassive) {
					notifyService.notify(path, mails, users, roles, message.getText(), false, callbackNotify);
				} else {
					massiveService.notify(uuidList, mails, users, roles, message.getText(), false, callbackNotify);
				}
				break;

			case NOTIFY_WITH_ATTACHMENT:
				if (!isMassive) {
					notifyService.notify(path, mails, users, roles, message.getText(), true, callbackNotify);
				} else {
					massiveService.notify(uuidList, mails, users, roles, message.getText(), true, callbackNotify);
				}
				break;

			case FORWARD_MAIL:
				if (!isMassive) {
					mailService.forwardMail(path, mails, users, roles, message.getText(), new AsyncCallback<Object>() {
						@Override
						public void onSuccess(Object result) {
						}

						@Override
						public void onFailure(Throwable caught) {
							Main.get().showError("forwardMail", caught);
						}
					});
				} else {
					massiveService.forwardMail(uuidList, mails, users, roles, message.getText(), new AsyncCallback<Object>() {
						@Override
						public void onSuccess(Object result) {
						}

						@Override
						public void onFailure(Throwable caught) {
							Main.get().showError("forwardMail", caught);
						}
					});
				}
				break;
		}
	}

	/**
	 * Reste values
	 */
	private void reset(int type) {
		this.type = type;

		switch (type) {
			case NOTIFY_WITH_LINK:
				setText(Main.i18n("notify.label"));
				break;

			case NOTIFY_WITH_ATTACHMENT:
				setText(Main.i18n("notify.label.attachment"));
				break;

			case FORWARD_MAIL:
				setText(Main.i18n("notify.label.forward.mail"));
				break;
		}

		mails = "";
		users = "";
		roles = "";
		message.setText("");
		notifyPanel.reset();
		notifyPanel.getAll();
		path = "";
		uuidList.clear();
		errorNotify.setVisible(false);
		sendButton.setEnabled(false);
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
	 * disableErrorNotify
	 */
	public void disableErrorNotify() {
		errorNotify.setVisible(false);
	}
}
