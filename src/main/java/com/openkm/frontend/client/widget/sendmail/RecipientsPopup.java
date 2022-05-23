/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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

package com.openkm.frontend.client.widget.sendmail;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.widget.notify.NotifyHandler;
import com.openkm.frontend.client.widget.notify.NotifyPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ForPopup
 *
 * @author sochoa
 */
public class RecipientsPopup extends DialogBox implements NotifyHandler {

	public static final int NONE = 0;
	public static final int TO = 1;
	public static final int CC = 2;
	public static final int BCC = 3;
	public static final int REPLY_TO = 4;

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private Button closeButton;
	private Button acceptButton;
	public NotifyPanel notifyPanel;
	private String users;
	private String roles;
	private String mails;
	private ScrollPanel scrollPanel;

	private int type = NONE;

	public RecipientsPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		setText(Main.i18n("maileditor.recipient.label"));
		users = "";
		roles = "";
		mails = "";
		vPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();
		notifyPanel = new NotifyPanel(this);

		closeButton = new Button(Main.i18n("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				reset(NONE);
			}
		});

		acceptButton = new Button(Main.i18n("button.accept"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Only sends if there's some user selected
				users = notifyPanel.getUsersToNotify();
				roles = notifyPanel.getRolesToNotify();
				mails = notifyPanel.getExternalMailAddress();
				scrollPanel = null;
				FlowPanel fPanel = new FlowPanel();
				Map<String, Widget> recipientMap = new HashMap<>();
				List<String> usersList = new ArrayList<>();
				List<String> rolesList = new ArrayList<>();
				List<String> mailsList = new ArrayList<>();

				switch (type) {
					case TO:
						fPanel = Main.get().mailEditorPopup.toFPanel;
						recipientMap = Main.get().mailEditorPopup.toRecipientMap;
						usersList = Main.get().mailEditorPopup.toUsers;
						rolesList = Main.get().mailEditorPopup.toRoles;
						mailsList = Main.get().mailEditorPopup.toMails;
						scrollPanel = Main.get().mailEditorPopup.toScrollPanel;
						break;

					case CC:
						fPanel = Main.get().mailEditorPopup.ccFPanel;
						recipientMap = Main.get().mailEditorPopup.ccRecipientMap;
						usersList = Main.get().mailEditorPopup.ccUsers;
						rolesList = Main.get().mailEditorPopup.ccRoles;
						mailsList = Main.get().mailEditorPopup.ccMails;
						scrollPanel = Main.get().mailEditorPopup.ccScrollPanel;
						break;

					case BCC:
						fPanel = Main.get().mailEditorPopup.bccFPanel;
						recipientMap = Main.get().mailEditorPopup.bccRecipientMap;
						usersList = Main.get().mailEditorPopup.bccUsers;
						rolesList = Main.get().mailEditorPopup.bccRoles;
						mailsList = Main.get().mailEditorPopup.bccMails;
						scrollPanel = Main.get().mailEditorPopup.bccScrollPanel;
						break;

					case REPLY_TO:
						fPanel = Main.get().mailEditorPopup.replyToFPanel;
						recipientMap = Main.get().mailEditorPopup.replyToRecientMap;
						usersList = Main.get().mailEditorPopup.replyToUsers;
						rolesList = Main.get().mailEditorPopup.replyToRoles;
						mailsList = Main.get().mailEditorPopup.replyToMails;
						scrollPanel = Main.get().mailEditorPopup.replyToScrollPanel;
						break;
				}

				usersList.clear(); // Empty recipient lists and map
				rolesList.clear();
				mailsList.clear();

				if (scrollPanel != null) {
					scrollPanel.setHeight("");
				}

				recipientMap.clear();
				fPanel.clear();
				Main.get().mailEditorPopup.addRecipients(users, fPanel, scrollPanel, recipientMap, usersList);
				Main.get().mailEditorPopup.addRecipients(roles, fPanel, scrollPanel, recipientMap, rolesList);
				Main.get().mailEditorPopup.addRecipients(mails, fPanel, scrollPanel, recipientMap, mailsList);
				hide();
				reset(NONE);
			}
		});

		hPanel.add(closeButton);
		HTML space = new HTML("");
		hPanel.add(space);
		hPanel.add(acceptButton);

		hPanel.setCellWidth(space, "40px");

		vPanel.add(new HTML("<br>"));
		vPanel.add(notifyPanel);
		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(notifyPanel, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, VerticalPanel.ALIGN_CENTER);

		vPanel.setWidth("100%");

		closeButton.setStyleName("okm-NoButton");
		acceptButton.setStyleName("okm-YesButton");

		notifyPanel.addStyleName("okm-DisableSelect");

		setWidget(vPanel);
	}

	/**
	 * langRefresh Refreshing lang
	 */
	public void langRefresh() {
		closeButton.setHTML(Main.i18n("button.close"));
		acceptButton.setHTML(Main.i18n("button.accept"));
		notifyPanel.langRefresh();
	}

	/**
	 * Reset values
	 */
	public void reset(int type) {
		this.type = type;
		users = "";
		roles = "";
		mails = "";
		notifyPanel.reset();

		switch (type) {
			case TO:
				notifyPanel.getAll(Main.get().mailEditorPopup.toUsers, Main.get().mailEditorPopup.toRoles, getMails(Main.get().mailEditorPopup.toMails));
				break;

			case CC:
				notifyPanel.getAll(Main.get().mailEditorPopup.ccUsers, Main.get().mailEditorPopup.ccRoles, getMails(Main.get().mailEditorPopup.ccMails));
				break;

			case BCC:
				notifyPanel.getAll(Main.get().mailEditorPopup.bccUsers, Main.get().mailEditorPopup.bccRoles, getMails(Main.get().mailEditorPopup.bccMails));
				break;

			case REPLY_TO:
				notifyPanel.getAll(Main.get().mailEditorPopup.replyToUsers, Main.get().mailEditorPopup.replyToRoles, getMails(Main.get().mailEditorPopup.replyToMails));
				break;
		}

		acceptButton.setEnabled(false);
	}

	/**
	 * Gets the users string to notify
	 *
	 * @return The users string
	 */
	private String getMails(List<String> mailsList) {
		String externalMails = "";

		if (mailsList.size() > 0) {
			for (String s : mailsList) {
				externalMails += s + ",";
			}
		}

		// Removes last ',' character
		if (externalMails.length() > 0) {
			externalMails = externalMails.substring(0, externalMails.length() - 1);
		}

		return externalMails;
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

	@Override
	public void onChange() {
		evaluateSendButton();
	}

	/**
	 * evaluateSendButton
	 */
	public void evaluateSendButton() {
		boolean enabled = (!notifyPanel.getUsersToNotify().equals("") || !notifyPanel.getRolesToNotify().equals("")
				|| !notifyPanel.getExternalMailAddress().equals(""));
		acceptButton.setEnabled(enabled);
	}
}
