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

package com.openkm.extension.frontend.client.widget.messaging.stack.subscriptionreceived;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.openkm.extension.frontend.client.service.OKMProposedSubscriptionService;
import com.openkm.extension.frontend.client.service.OKMProposedSubscriptionServiceAsync;
import com.openkm.extension.frontend.client.widget.messaging.MessagingToolBarBox;
import com.openkm.frontend.client.bean.extension.GWTProposedSubscriptionReceived;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ProposedSubscriptionStack
 *
 * @author jllort
 *
 */
public class ProposedSubscriptionReceivedStack extends Composite {

	private final OKMProposedSubscriptionServiceAsync proposedSubscriptionService = (OKMProposedSubscriptionServiceAsync) GWT.create(OKMProposedSubscriptionService.class);

	private ExtendedFlexTable table;
	private boolean firstTime = true;
	private String selectedId = "";
	public MenuPopup menuPopup;
	public int notSeen = 0;
	Map<String, Long> userSeen;

	/**
	 * Propose
	 */
	public ProposedSubscriptionReceivedStack() {
		userSeen = new HashMap<String, Long>();

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
	 * findAllProposedSubscriptions
	 */
	public void findAllProposedSubscriptions() {
		if (!firstTime) {
			MessagingToolBarBox.get().messageDashboard.messageStack.status.setFlag_getProposedSubscriptions();
		}
		proposedSubscriptionService.findProposedSubscriptionsUsersFrom(new AsyncCallback<Map<String, Long>>() {
			public void onSuccess(Map<String, Long> result) {
				notSeen = 0;
				mantainSelectedRow(); // Each time refreshing might mantaining selected row
				table.removeAllRows();
				userSeen = result;

				for (String user : result.keySet()) {
					table.addRow(user, result.get(user).intValue() == 0);
					notSeen += result.get(user).intValue();
				}
				if (!firstTime) {
					MessagingToolBarBox.get().messageDashboard.messageStack.status.unsetFlag_getProposedSubscriptions();
				} else {
					firstTime = false;
				}
				// Only drawing if proposed subscription is visible
				if (MessagingToolBarBox.get().messageDashboard.messageStack.isProposedSubscriptionReceivedVisible()) {
					refreshProposedSubscriptions();
				}
				MessagingToolBarBox.get().messageDashboard.messageStack.refreshProposedSubscriptionNotSeenStack();
				MessagingToolBarBox.get().refreshProposedSubscriptionNotSeenValues();
			}

			public void onFailure(Throwable caught) {
				if (!firstTime) {
					MessagingToolBarBox.get().messageDashboard.messageStack.status.unsetFlag_getProposedSubscriptions();
				} else {
					firstTime = false;
				}

				GeneralComunicator.showError("findProposedSubscriptionsUsersFrom", caught);
			}
		});
	}

	/**
	 * Gets proposed subscriptions
	 */
	public void refreshProposedSubscriptions() {
		int selectedRow = table.findSelectedRowById(selectedId);
		cleanSelectedRow(); // Cleaning selected row
		MessagingToolBarBox.get().messageDashboard.messageBrowser.message.removeAllRows();
		table.setSelectedRow(selectedRow);
		MessagingToolBarBox.get().messageDashboard.messageBrowser.messageDetail.reset();

		if (getSelectedRow() >= 0) {
			MessagingToolBarBox.get().messageDashboard.messageBrowser.message.status.setFlag_getProposedSubscriptions();
			String user = table.getText(getSelectedRow(), 1);

			proposedSubscriptionService.findProposedSubscriptionByMeFromUser(user, new AsyncCallback<List<GWTProposedSubscriptionReceived>>() {
				@Override
				public void onSuccess(List<GWTProposedSubscriptionReceived> result) {
					for (GWTProposedSubscriptionReceived proposedSubscription : result) {
						MessagingToolBarBox.get().messageDashboard.messageBrowser.message.addRow(proposedSubscription);
					}

					MessagingToolBarBox.get().messageDashboard.messageBrowser.message.status.unsetFlag_getProposedSubscriptions();
				}

				@Override
				public void onFailure(Throwable caught) {
					MessagingToolBarBox.get().messageDashboard.messageBrowser.message.status.unsetFlag_getProposedSubscriptions();
					GeneralComunicator.showError("findProposedSubscriptionByMeFromUser", caught);
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
	 * deleteProposedSubscriptionByMeFromUser
	 */
	public void deleteProposedSubscriptionByMeFromUser() {
		if (MessagingToolBarBox.get().messageDashboard.messageStack.isProposedSubscriptionReceivedVisible() && isSelectedRow()) {
			MessagingToolBarBox.get().messageDashboard.messageStack.status.setFlag_deleteProposedSubscription();
			proposedSubscriptionService.deleteProposedSubscriptionByMeFromUser(table.getHTML(getSelectedRow(), 1), new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
					MessagingToolBarBox.get().messageDashboard.messageStack.status.unsetFlag_deleteProposedSubscriptions();
					table.removeRow(getSelectedRow());
					table.selectPrevRow();
					findAllProposedSubscriptions();
				}

				@Override
				public void onFailure(Throwable caught) {
					MessagingToolBarBox.get().messageDashboard.messageStack.status.unsetFlag_deleteProposedSubscriptions();
					GeneralComunicator.showError("deleteProposedSubscritpionByMeFromUser", caught);
				}
			});
		}
	}

	/**
	 * markProposeAsAccepted
	 *
	 * @param id
	 */
	public void markProposeAsSeen() {
		String user = getSelectedUser();
		if (userSeen.containsKey(user)) {
			Long pendingSeen = userSeen.get(user);
			userSeen.put(user, new Long(pendingSeen.intValue() - 1));
			if ((pendingSeen.intValue() - 1) == 0) {
				table.markActualRowAsSeen();
			}
		}
		notSeen--;
		MessagingToolBarBox.get().messageDashboard.messageStack.refreshProposedSubscriptionNotSeenStack();
		MessagingToolBarBox.get().refreshProposedSubscriptionNotSeenValues();
	}

	/**
	 * getNotSeen
	 *
	 * @return
	 */
	public int getNotSeen() {
		return notSeen;
	}
}
	