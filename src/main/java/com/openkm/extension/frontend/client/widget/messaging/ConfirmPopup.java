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

package com.openkm.extension.frontend.client.widget.messaging;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * Confirm panel
 *
 * @author jllort
 *
 */
public class ConfirmPopup extends DialogBox {

	public static final int NO_ACTION = 0;
	public static final int CONFIRM_DELETE_MESSAGE_SENT = 1;
	public static final int CONFIRM_DELETE_MESSAGE_SENT_TO_USER = 2;
	public static final int CONFIRM_DELETE_MESSAGE_RECEIVED = 3;
	public static final int CONFIRM_DELETE_MESSAGE_RECEIVED_FROM_USER = 4;
	public static final int CONFIRM_DELETE_PROPOSED_QUERY_RECEIVED = 5;
	public static final int CONFIRM_DELETE_PROPOSED_QUERY_RECEIVED_FROM_USER = 6;
	public static final int CONFIRM_DELETE_PROPOSED_QUERY_SENT = 7;
	public static final int CONFIRM_DELETE_PROPOSED_SUBSCRIPTION_RECEIVED = 8;
	public static final int CONFIRM_DELETE_PROPOSED_SUBSCRIPTION_RECEIVED_FROM_USER = 9;
	public static final int CONFIRM_DELETE_PROPOSED_SUBSCRIPTION_SENT = 10;

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private HTML text;
	private Button cancelButton;
	private Button acceptButton;
	private int action = 0;
	private Object object;

	/**
	 * Confirm popup
	 */
	public ConfirmPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		vPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();
		text = new HTML();
		text.setStyleName("okm-NoWrap");

		cancelButton = new Button(GeneralComunicator.i18nExtension("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		acceptButton = new Button(GeneralComunicator.i18nExtension("button.accept"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				execute();
				hide();
			}
		});

		vPanel.setWidth("300px");
		vPanel.setHeight("100px");
		cancelButton.setStyleName("okm-NoButton");
		acceptButton.setStyleName("okm-YesButton");

		text.setHTML("");

		hPanel.add(cancelButton);
		hPanel.add(new HTML("&nbsp;&nbsp;"));
		hPanel.add(acceptButton);

		vPanel.add(new HTML("<br>"));
		vPanel.add(text);
		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(text, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, VerticalPanel.ALIGN_CENTER);

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * Execute the confirmed action
	 */
	private void execute() {
		switch (action) {

			case CONFIRM_DELETE_MESSAGE_SENT:
				if (MessagingToolBarBox.get().messageDashboard.messageStack.isMessagesSentVisible() &&
						MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.isMessageSentSelected()) {
					MessagingToolBarBox.get().messageDashboard.messageBrowser.message.deleteMessageSent();
				}
				break;

			case CONFIRM_DELETE_MESSAGE_SENT_TO_USER:
				if (MessagingToolBarBox.get().messageDashboard.messageStack.isMessagesSentVisible() &&
						MessagingToolBarBox.get().messageDashboard.messageStack.messageSent.isSelectedRow()) {
					MessagingToolBarBox.get().messageDashboard.messageStack.messageSent.deleteAllMessagesSentToUser();
				}
				break;

			case CONFIRM_DELETE_MESSAGE_RECEIVED:
				if (MessagingToolBarBox.get().messageDashboard.messageStack.isMessagesReceivedVisible() &&
						MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.isMessageReceivedSelected()) {
					MessagingToolBarBox.get().messageDashboard.messageBrowser.message.deleteMessageReceived();
				}
				break;

			case CONFIRM_DELETE_MESSAGE_RECEIVED_FROM_USER:
				if (MessagingToolBarBox.get().messageDashboard.messageStack.isMessagesReceivedVisible() &&
						MessagingToolBarBox.get().messageDashboard.messageStack.messageReceived.isSelectedRow()) {
					MessagingToolBarBox.get().messageDashboard.messageStack.messageReceived.deleteAllMessagesFromUser();
				}
				break;

			case CONFIRM_DELETE_PROPOSED_QUERY_RECEIVED:
				if (MessagingToolBarBox.get().messageDashboard.messageStack.isProposedQueryReceivedVisible() &&
						MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.isProposedQueryReceivedSelected()) {
					MessagingToolBarBox.get().messageDashboard.messageBrowser.message.deleteProposedQueryReceived();
				}
				break;

			case CONFIRM_DELETE_PROPOSED_QUERY_RECEIVED_FROM_USER:
				if (MessagingToolBarBox.get().messageDashboard.messageStack.isProposedQueryReceivedVisible() &&
						MessagingToolBarBox.get().messageDashboard.messageStack.proposedQueryReceived.isSelectedRow()) {
					MessagingToolBarBox.get().messageDashboard.messageStack.proposedQueryReceived.deleteProposedQueryByMeFromUser();
				}
				break;

			case CONFIRM_DELETE_PROPOSED_QUERY_SENT:
				if (MessagingToolBarBox.get().messageDashboard.messageStack.isMessagesSentVisible() &&
						MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.isProposedQuerySentSelected()) {
					MessagingToolBarBox.get().messageDashboard.messageBrowser.message.deleteProposedQuerySent();
				}
				break;

			case CONFIRM_DELETE_PROPOSED_SUBSCRIPTION_RECEIVED:
				if (MessagingToolBarBox.get().messageDashboard.messageStack.isProposedSubscriptionReceivedVisible() &&
						MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.isProposedSubscriptionReceivedSelected()) {
					MessagingToolBarBox.get().messageDashboard.messageBrowser.message.deleteProposedSubscriptionReceived();
				}
				break;

			case CONFIRM_DELETE_PROPOSED_SUBSCRIPTION_RECEIVED_FROM_USER:
				if (MessagingToolBarBox.get().messageDashboard.messageStack.isProposedSubscriptionReceivedVisible() &&
						MessagingToolBarBox.get().messageDashboard.messageStack.proposedSubscriptionReceived.isSelectedRow()) {
					MessagingToolBarBox.get().messageDashboard.messageStack.proposedSubscriptionReceived.deleteProposedSubscriptionByMeFromUser();
				}
				break;

			case CONFIRM_DELETE_PROPOSED_SUBSCRIPTION_SENT:
				if (MessagingToolBarBox.get().messageDashboard.messageStack.isMessagesSentVisible() &&
						MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.isProposedSubscriptionSentSelected()) {
					MessagingToolBarBox.get().messageDashboard.messageBrowser.message.deleteProposedSubscriptionSent();
				}
				break;
		}

		action = NO_ACTION; // Resets action value
	}

	/**
	 * Sets the action to be confirmed
	 *
	 * @param action The action to be confirmed
	 */
	public void setConfirm(int action) {
		this.action = action;
		switch (action) {

			case CONFIRM_DELETE_MESSAGE_SENT:
				text.setHTML(GeneralComunicator.i18nExtension("confirm.delete.message.sent"));
				break;

			case CONFIRM_DELETE_MESSAGE_SENT_TO_USER:
				text.setHTML(GeneralComunicator.i18nExtension("confirm.delete.message.sent.to.user"));
				break;

			case CONFIRM_DELETE_MESSAGE_RECEIVED:
				text.setHTML(GeneralComunicator.i18nExtension("confirm.delete.message.received"));
				break;

			case CONFIRM_DELETE_MESSAGE_RECEIVED_FROM_USER:
				text.setHTML(GeneralComunicator.i18nExtension("confirm.delete.message.received.from.user"));
				break;

			case CONFIRM_DELETE_PROPOSED_QUERY_RECEIVED:
				text.setHTML(GeneralComunicator.i18nExtension("confirm.delete.message.proposed.query"));
				break;

			case CONFIRM_DELETE_PROPOSED_QUERY_SENT:
				text.setHTML(GeneralComunicator.i18nExtension("confirm.delete.message.proposed.query.sent"));
				break;

			case CONFIRM_DELETE_PROPOSED_QUERY_RECEIVED_FROM_USER:
				text.setHTML(GeneralComunicator.i18nExtension("confirm.delete.message.proposed.query.from.user"));
				break;

			case CONFIRM_DELETE_PROPOSED_SUBSCRIPTION_RECEIVED:
				text.setHTML(GeneralComunicator.i18nExtension("confirm.delete.message.proposed.subscription"));
				break;

			case CONFIRM_DELETE_PROPOSED_SUBSCRIPTION_RECEIVED_FROM_USER:
				text.setHTML(GeneralComunicator.i18nExtension("confirm.delete.message.proposed.subscription.from.user"));
				break;

			case CONFIRM_DELETE_PROPOSED_SUBSCRIPTION_SENT:
				text.setHTML(GeneralComunicator.i18nExtension("confirm.delete.message.proposed.subscription.sent"));
				break;
		}
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(GeneralComunicator.i18nExtension("confirm.label"));
		cancelButton.setText(GeneralComunicator.i18nExtension("button.cancel"));
		acceptButton.setText(GeneralComunicator.i18nExtension("button.accept"));
	}

	/**
	 * Sets the value to object
	 *
	 * @param object The object to set
	 */
	public void setValue(Object object) {
		this.object = object;
	}

	/**
	 * Get the object value
	 *
	 * @return The object
	 */
	public Object getValue() {
		return this.object;
	}

	/**
	 * Shows de popup
	 */
	public void show() {
		setText(GeneralComunicator.i18nExtension("confirm.label"));
		int left = (Window.getClientWidth() - 300) / 2;
		int top = (Window.getClientHeight() - 125) / 2;
		setPopupPosition(left, top);
		super.show();
	}
}