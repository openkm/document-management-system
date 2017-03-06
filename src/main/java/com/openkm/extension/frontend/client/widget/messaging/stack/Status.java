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

package com.openkm.extension.frontend.client.widget.messaging.stack;

import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.extension.frontend.client.widget.messaging.MessagingToolBarBox;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * Status
 *
 * @author jllort
 *
 */
public class Status extends PopupPanel {

	private HorizontalPanel hPanel;
	private HTML msg;
	private HTML space;
	private Image image;

	private boolean flag_getProposedSubscriptions = false;
	private boolean flag_deleteProposedSubscription = false;
	private boolean flag_getProposedQueries = false;
	private boolean flag_deleteProposedQuery = false;
	private boolean flag_deleteMessageSent = false;
	private boolean flag_getMessagesSent = false;
	private boolean flag_deleteMessageReceived = false;
	private boolean flag_getMessagesReceived = false;

	/**
	 * Status
	 */
	public Status() {
		super(false, true);
		hPanel = new HorizontalPanel();
		image = new Image(OKMBundleResources.INSTANCE.indicator());
		msg = new HTML("");
		space = new HTML("");

		hPanel.add(image);
		hPanel.add(msg);
		hPanel.add(space);

		hPanel.setCellVerticalAlignment(image, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellVerticalAlignment(msg, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellHorizontalAlignment(image, HasAlignment.ALIGN_CENTER);
		hPanel.setCellWidth(image, "30px");
		hPanel.setCellWidth(space, "7px");

		hPanel.setHeight("25px");

		msg.setStyleName("okm-NoWrap");

		super.hide();
		setWidget(hPanel);
	}

	/**
	 * Refresh
	 */
	public void refresh() {
		if (flag_getProposedSubscriptions || flag_deleteProposedSubscription || flag_getProposedQueries ||
				flag_deleteProposedQuery || flag_deleteMessageSent || flag_getMessagesSent ||
				flag_deleteMessageReceived || flag_getMessagesReceived) {
			int left = ((MessagingToolBarBox.get().messageDashboard.messageStack.getOffsetWidth() - 200) / 2) +
					MessagingToolBarBox.get().messageDashboard.messageStack.getAbsoluteLeft();
			int top = ((MessagingToolBarBox.get().messageDashboard.messageStack.getOffsetHeight() - 40) / 2) +
					MessagingToolBarBox.get().messageDashboard.messageStack.getAbsoluteTop();
			setPopupPosition(left, top);
			MessagingToolBarBox.get().messageDashboard.messageStack.scrollProposeSubscriptionReceivedPanel.addStyleName("okm-PanelRefreshing");
			show();
		} else {
			hide();
			MessagingToolBarBox.get().messageDashboard.messageStack.scrollProposeSubscriptionReceivedPanel.removeStyleName("okm-PanelRefreshing");
		}
	}

	/**
	 * Sets get proposed subscriptions flag
	 */
	public void setFlag_getProposedSubscriptions() {
		msg.setHTML(GeneralComunicator.i18nExtension("dasboard.messaging.get.proposed.subscriptions"));
		flag_getProposedSubscriptions = true;
		refresh();
	}

	/**
	 * Unset get proposed subscriptions flag
	 */
	public void unsetFlag_getProposedSubscriptions() {
		flag_getProposedSubscriptions = false;
		refresh();
	}

	/**
	 * Sets delete proposed subscriptions flag
	 */
	public void setFlag_deleteProposedSubscription() {
		msg.setHTML(GeneralComunicator.i18nExtension("dasboard.messaging.delete.proposed.subscriptions"));
		flag_deleteProposedSubscription = true;
		refresh();
	}

	/**
	 * Unset delete proposed subscriptions flag
	 */
	public void unsetFlag_deleteProposedSubscriptions() {
		flag_deleteProposedSubscription = false;
		refresh();
	}

	/**
	 * Sets get proposed queries flag
	 */
	public void setFlag_getProposedQueries() {
		msg.setHTML(GeneralComunicator.i18nExtension("dasboard.messaging.get.proposed.queries"));
		flag_getProposedQueries = true;
		refresh();
	}

	/**
	 * Unset get proposed queries flag
	 */
	public void unsetFlag_getProposedQueries() {
		flag_getProposedQueries = false;
		refresh();
	}

	/**
	 * Sets delete proposed query flag
	 */
	public void setFlag_deleteProposedQuery() {
		msg.setHTML(GeneralComunicator.i18nExtension("dasboard.messaging.delete.proposed.query"));
		flag_deleteProposedQuery = true;
		refresh();
	}

	/**
	 * Unset delete proposed query flag
	 */
	public void unsetFlag_deleteProposedQuery() {
		flag_deleteProposedQuery = false;
		refresh();
	}

	/**
	 * Sets delete message sent flag
	 */
	public void setFlag_deleteMessageSent() {
		msg.setHTML(GeneralComunicator.i18nExtension("dasboard.messaging.delete.message.sent"));
		flag_deleteMessageSent = true;
		refresh();
	}

	/**
	 * Unset delete message sent flag
	 */
	public void unsetFlag_deleteMessageSent() {
		flag_deleteMessageSent = false;
		refresh();
	}

	/**
	 * Sets get messages sent flag
	 */
	public void setFlag_getMessagesSent() {
		msg.setHTML(GeneralComunicator.i18nExtension("dasboard.messaging.get.messages.sent"));
		flag_getMessagesSent = true;
		refresh();
	}

	/**
	 * Unset get messages sent flag
	 */
	public void unsetFlag_getMessagesSent() {
		flag_getMessagesSent = false;
		refresh();
	}

	/**
	 * Sets delete message received flag
	 */
	public void setFlag_deleteMessageReceived() {
		msg.setHTML(GeneralComunicator.i18nExtension("dasboard.messaging.delete.message.received"));
		flag_deleteMessageReceived = true;
		refresh();
	}

	/**
	 * Unset delete message received flag
	 */
	public void unsetFlag_deleteMessageReceived() {
		flag_deleteMessageReceived = false;
		refresh();
	}

	/**
	 * Sets get messages received flag
	 */
	public void setFlag_getMessagesReceived() {
		msg.setHTML(GeneralComunicator.i18nExtension("dasboard.messaging.get.messages.received"));
		flag_getMessagesReceived = true;
		refresh();
	}

	/**
	 * Unset get messages received flag
	 */
	public void unsetFlag_getMessagesReceived() {
		flag_getMessagesReceived = false;
		refresh();
	}
}