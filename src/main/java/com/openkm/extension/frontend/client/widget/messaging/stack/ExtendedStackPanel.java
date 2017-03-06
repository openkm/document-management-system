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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.openkm.extension.frontend.client.widget.messaging.MessagingToolBarBox;

/**
 * ExtendedStackPanel
 *
 * @author jllort
 *
 */
public class ExtendedStackPanel extends StackLayoutPanel {

	private boolean startupFinished = false; // to indicate process starting up has finished

	public ExtendedStackPanel() {
		super(Unit.PX);
	}

	@Override
	public void showWidget(int index) {
		if (startupFinished) {
			changeView(index);
		}

		super.showWidget(index);
		if (startupFinished) {
			MessagingToolBarBox.get().messageDashboard.messageBrowser.message.changeHeaderText();
		}
	}

	/**
	 * changeView
	 *
	 * @param index
	 */
	private void changeView(int index) {
		switch (index) {
			case MessageStack.STACK_SUBSCRIPTION_RECEIVED:
				MessagingToolBarBox.get().messageDashboard.messageStack.refreshProposedSubscriptions();
				break;
			case MessageStack.STACK_QUERY_RECEIVED:
				MessagingToolBarBox.get().messageDashboard.messageStack.refreshProposedQueries();
				break;
			case MessageStack.STACK_MESSAGES_RECEIVED:
				MessagingToolBarBox.get().messageDashboard.messageStack.refreshMessageReceived();
				break;
			case MessageStack.STACK_MESSAGES_SENT:
				MessagingToolBarBox.get().messageDashboard.messageStack.refreshMessageSent();
				break;
		}
	}

	/**
	 * setStartUpFinished
	 */
	public void setStartUpFinished() {
		startupFinished = true;
	}
}