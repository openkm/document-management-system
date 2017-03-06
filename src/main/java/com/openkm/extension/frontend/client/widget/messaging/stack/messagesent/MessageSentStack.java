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

package com.openkm.extension.frontend.client.widget.messaging.stack.messagesent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.openkm.extension.frontend.client.service.OKMMessageService;
import com.openkm.extension.frontend.client.service.OKMMessageServiceAsync;
import com.openkm.extension.frontend.client.widget.messaging.MessagingToolBarBox;
import com.openkm.frontend.client.bean.extension.GWTMessageSent;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

import java.util.List;

/**
 * MessageSendStack
 *
 * @author jllort
 *
 */

/**
 * @author jllort
 *
 */
public class MessageSentStack extends Composite {

	private final OKMMessageServiceAsync messageService = (OKMMessageServiceAsync) GWT.create(OKMMessageService.class);

	private ExtendedFlexTable table;
	private boolean firstTime = true;
	private String selectedId = "";
	public MenuPopup menuPopup;

	/**
	 * Propose
	 */
	public MessageSentStack() {
		menuPopup = new MenuPopup();
		menuPopup.setStyleName("okm-MenuPopup");

		table = new ExtendedFlexTable();

		table.setBorderWidth(0);
		table.setCellSpacing(0);
		table.setCellSpacing(0);

		table.sinkEvents(Event.ONDBLCLICK | Event.ONMOUSEDOWN);

		initWidget(table);

	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		menuPopup.langRefresh();
	}

	/**
	 * Gets the selected row value
	 *
	 * @return The selected row value
	 */
	public int getSelectedRow() {
		return table.getSelectedRow();
	}

	/**
	 * findAllUsersMessageSent
	 */
	public void findAllUsersMessageSent() {
		if (!firstTime) {
			MessagingToolBarBox.get().messageDashboard.messageStack.status.setFlag_getMessagesSent();
		}
		messageService.findSentUsersTo(new AsyncCallback<List<String>>() {
			public void onSuccess(List<String> result) {
				mantainSelectedRow(); // Each time refreshing might mantaining selected row
				table.removeAllRows();

				for (String user : result) {
					table.addRow(user);
				}
				if (!firstTime) {
					MessagingToolBarBox.get().messageDashboard.messageStack.status.unsetFlag_getMessagesSent();
				} else {
					firstTime = false;
				}
				// Only drawing if proposed subscription is visible
				if (MessagingToolBarBox.get().messageDashboard.messageStack.isMessagesSentVisible()) {
					refreshMessagesSent();
				}
			}

			public void onFailure(Throwable caught) {
				if (!firstTime) {
					MessagingToolBarBox.get().messageDashboard.messageStack.status.unsetFlag_getMessagesSent();
				} else {
					firstTime = false;
				}
				GeneralComunicator.showError("findSentUsersTo", caught);
			}
		});
	}

	/**
	 * Gets proposed subscriptions
	 */
	public void refreshMessagesSent() {
		int selectedRow = table.findSelectedRowById(selectedId);
		cleanSelectedRow(); // Cleaning selected row
		MessagingToolBarBox.get().messageDashboard.messageBrowser.message.removeAllRows();
		table.setSelectedRow(selectedRow);
		MessagingToolBarBox.get().messageDashboard.messageBrowser.messageDetail.reset();
		if (getSelectedRow() >= 0) {
			MessagingToolBarBox.get().messageDashboard.messageBrowser.message.status.setFlag_getMessageSent();
			String user = table.getText(getSelectedRow(), 0);
			messageService.findSentFromMeToUser(user, new AsyncCallback<List<GWTMessageSent>>() {
				@Override
				public void onSuccess(List<GWTMessageSent> result) {
					for (GWTMessageSent message : result) {
						if (message.getTextMessageSent() != null) {
							MessagingToolBarBox.get().messageDashboard.messageBrowser.message.addRow(message.getTextMessageSent());
						} else if (message.getProposedQuerySent() != null) {
							MessagingToolBarBox.get().messageDashboard.messageBrowser.message.addRow(message.getProposedQuerySent());
						} else if (message.getProposedSubscriptionSent() != null) {
							MessagingToolBarBox.get().messageDashboard.messageBrowser.message.addRow(message.getProposedSubscriptionSent());
						}
					}
					MessagingToolBarBox.get().messageDashboard.messageBrowser.message.status.unsetFlag_getMessageSent();
				}

				@Override
				public void onFailure(Throwable caught) {
					MessagingToolBarBox.get().messageDashboard.messageBrowser.message.status.unsetFlag_getMessageSent();
					GeneralComunicator.showError("findSentFromMeToUser", caught);
				}
			});
		}
	}

	/**
	 * Sets the selected panel value
	 *
	 * @param selected The selected panel value
	 */
	public void setSelectedPanel(boolean selected) {
		table.setSelectedPanel(selected);
	}

	/**
	 * Indicates if panel is selected
	 *
	 * @return The value of panel ( selected )
	 */
	public boolean isPanelSelected() {
		return table.isPanelSelected();
	}

	/**
	 * mantainSelectedRow
	 */
	public void mantainSelectedRow() {
		if (table.getSelectedRow() >= 0) {
			selectedId = table.getSelectedId();
		} else {
			selectedId = "";
		}
	}

	/**
	 * cleanSelectedRow
	 */
	public void cleanSelectedRow() {
		selectedId = "";
	}

	/**isSelectedRow
	 *
	 * @return
	 */
	public boolean isSelectedRow() {
		return (getSelectedRow() >= 0);
	}

	/**
	 * getSelectedUser
	 *
	 * @return
	 */
	public String getSelectedUser() {
		if (table.getSelectedRow() >= 0) {
			return table.getSelectedId();
		} else {
			return "";
		}
	}

	/**
	 * deleteAllMessagesSentToUser
	 */
	public void deleteAllMessagesSentToUser() {
		if (MessagingToolBarBox.get().messageDashboard.messageStack.isMessagesSentVisible() && isSelectedRow()) {
			MessagingToolBarBox.get().messageDashboard.messageStack.status.setFlag_deleteMessageSent();
			messageService.deleteSentFromMeToUser(table.getHTML(getSelectedRow(), 0), new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
					MessagingToolBarBox.get().messageDashboard.messageStack.status.unsetFlag_deleteMessageSent();
					table.removeRow(getSelectedRow());
					table.selectPrevRow();
					findAllUsersMessageSent();
				}

				@Override
				public void onFailure(Throwable caught) {
					MessagingToolBarBox.get().messageDashboard.messageStack.status.unsetFlag_deleteMessageSent();
					GeneralComunicator.showError("deleteSentFromMeToUser", caught);
				}
			});
		}
	}
}
	