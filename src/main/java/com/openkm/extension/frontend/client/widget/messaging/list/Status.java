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

package com.openkm.extension.frontend.client.widget.messaging.list;

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
	private boolean flag_markProposeAsSeen = false;
	private boolean flag_deletePropose = false;
	private boolean flag_getProposedQueries = false;
	private boolean flag_getMessageSent = false;
	private boolean flag_getMessageReceived = false;
	private boolean flag_deleteMessageReceived = false;
	private boolean flag_deleteMessageSent = false;
	private boolean flag_markMessageReceivedAsSeen = false;

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
		if (flag_getProposedSubscriptions || flag_markProposeAsSeen || flag_deletePropose || flag_getProposedQueries ||
				flag_getMessageSent || flag_getMessageReceived || flag_deleteMessageReceived || flag_deleteMessageSent ||
				flag_markMessageReceivedAsSeen) {
			int left = ((MessagingToolBarBox.get().messageDashboard.messageBrowser.message.getOffsetWidth() - 200) / 2) +
					MessagingToolBarBox.get().messageDashboard.messageBrowser.message.getAbsoluteLeft();
			int top = ((MessagingToolBarBox.get().messageDashboard.messageBrowser.message.getOffsetHeight() - 40) / 2) +
					MessagingToolBarBox.get().messageDashboard.messageBrowser.message.getAbsoluteTop();
			setPopupPosition(left, top);
			MessagingToolBarBox.get().messageDashboard.messageBrowser.message.addStyleName("okm-PanelRefreshing");
			show();
		} else {
			hide();
			MessagingToolBarBox.get().messageDashboard.messageBrowser.message.removeStyleName("okm-PanelRefreshing");
		}
	}

	/**
	 * Sets get proposed subscription flag
	 */
	public void setFlag_getProposedSubscriptions() {
		msg.setHTML(GeneralComunicator.i18nExtension("dasboard.messaging.get.proposed.subscriptions"));
		flag_getProposedSubscriptions = true;
		refresh();
	}

	/**
	 * Unset get proposed subscription flag
	 */
	public void unsetFlag_getProposedSubscriptions() {
		flag_getProposedSubscriptions = false;
		refresh();
	}

	/**
	 * Sets mark propose as seen flag
	 */
	public void setFlag_markProposeAsSeen() {
		msg.setHTML(GeneralComunicator.i18nExtension("dasboard.messaging.mark.propose.seen"));
		flag_markProposeAsSeen = true;
		refresh();
	}

	/**
	 * Unset mark propose as seen flag
	 */
	public void unsetFlag_markProposeAsSeen() {
		flag_markProposeAsSeen = false;
		refresh();
	}

	/**
	 * Sets delete propose flag
	 */
	public void setFlag_deletePropose() {
		msg.setHTML(GeneralComunicator.i18nExtension("dasboard.messaging.delete.propose"));
		flag_deletePropose = true;
		refresh();
	}

	/**
	 * Unset delete propose flag
	 */
	public void unsetFlag_deletePropose() {
		flag_deletePropose = false;
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
	 * Sets get message sent flag
	 */
	public void setFlag_getMessageSent() {
		msg.setHTML(GeneralComunicator.i18nExtension("dasboard.messaging.get.message.sent"));
		flag_getMessageSent = true;
		refresh();
	}

	/**
	 * Unset get message sent flag
	 */
	public void unsetFlag_getMessageSent() {
		flag_getMessageSent = false;
		refresh();
	}

	/**
	 * Sets get message received flag
	 */
	public void setFlag_getMessageReceived() {
		msg.setHTML(GeneralComunicator.i18nExtension("dasboard.messaging.get.message.received"));
		flag_getMessageReceived = true;
		refresh();
	}

	/**
	 * Unset get message received flag
	 */
	public void unsetFlag_getMessageReceived() {
		flag_getMessageReceived = false;
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
	 * Sets mark message received as seen flag
	 */
	public void setFlag_markMessageReceivedAsSeen() {
		msg.setHTML(GeneralComunicator.i18nExtension("dasboard.messaging.mark.message.received.seen"));
		flag_markMessageReceivedAsSeen = true;
		refresh();
	}

	/**
	 * Unset mark message received as seen flag
	 */
	public void unsetFlag_markMessageReceivedAsSeen() {
		flag_markMessageReceivedAsSeen = false;
		refresh();
	}
}