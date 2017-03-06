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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.openkm.extension.frontend.client.widget.messaging.MessagingToolBarBox;
import com.openkm.extension.frontend.client.widget.messaging.stack.messagereceived.MessageReceivedStack;
import com.openkm.extension.frontend.client.widget.messaging.stack.messagesent.MessageSentStack;
import com.openkm.extension.frontend.client.widget.messaging.stack.queryreceived.ProposedQueryReceivedStack;
import com.openkm.extension.frontend.client.widget.messaging.stack.subscriptionreceived.ProposedSubscriptionReceivedStack;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.UtilComunicator;

/**
 * MessagingStack panel
 *
 * @author jllort
 *
 */
public class MessageStack extends Composite {

	// Constants defining the current view
	public static final int NUMBER_OF_STACKS = 4;

	// Contants stack
	public static final int STACK_SUBSCRIPTION_RECEIVED = 0;
	public static final int STACK_QUERY_RECEIVED = 1;
	public static final int STACK_MESSAGES_RECEIVED = 2;
	public static final int STACK_MESSAGES_SENT = 3;

	// Stack
	public ExtendedStackPanel stackPanel;
	public ScrollPanel scrollProposeSubscriptionReceivedPanel;
	public ScrollPanel scrollProposeQueryReceivedPanel;
	public ScrollPanel scrollMessageReceivedPanel;
	public ScrollPanel scrollMessageSentPanel;
	public ProposedSubscriptionReceivedStack proposedSubscriptionReceived;
	public ProposedQueryReceivedStack proposedQueryReceived;
	public MessageReceivedStack messageReceived;
	public MessageSentStack messageSent;
	public Status status;

	/**
	 * MessageStack
	 */
	public MessageStack() {
		status = new Status();
		status.setStyleName("okm-StatusPopup");

		stackPanel = new ExtendedStackPanel();
		proposedSubscriptionReceived = new ProposedSubscriptionReceivedStack();
		proposedQueryReceived = new ProposedQueryReceivedStack();
		messageReceived = new MessageReceivedStack();
		messageSent = new MessageSentStack();
		scrollProposeSubscriptionReceivedPanel = new ScrollPanel();
		scrollProposeQueryReceivedPanel = new ScrollPanel();
		scrollMessageReceivedPanel = new ScrollPanel();
		scrollMessageSentPanel = new ScrollPanel();

		scrollProposeSubscriptionReceivedPanel.addStyleName("okm-PanelSelected");
		scrollProposeQueryReceivedPanel.addStyleName("okm-PanelSelected");
		scrollMessageReceivedPanel.addStyleName("okm-PanelSelected");
		scrollMessageSentPanel.addStyleName("okm-PanelSelected");

		scrollProposeSubscriptionReceivedPanel.add(proposedSubscriptionReceived);
		scrollProposeQueryReceivedPanel.add(proposedQueryReceived);
		scrollMessageReceivedPanel.add(messageReceived);
		scrollMessageSentPanel.add(messageSent);
		scrollProposeSubscriptionReceivedPanel.setSize("100%", "100%");
		scrollProposeQueryReceivedPanel.setSize("100%", "100%");
		scrollMessageSentPanel.setSize("100%", "100%");
		// Mandatory can not be a resource bundle due IE problem !
		stackPanel.add(scrollProposeSubscriptionReceivedPanel, UtilComunicator.createHeaderHTML("img/icon/actions/propose_subscription.png",
				GeneralComunicator.i18nExtension("messaging.label.propose")), true, UIDesktopConstants.STACK_HEIGHT);
		stackPanel.add(scrollProposeQueryReceivedPanel, UtilComunicator.createHeaderHTML("img/icon/actions/share_query.gif",
				GeneralComunicator.i18nExtension("messaging.label.share.query")), true, UIDesktopConstants.STACK_HEIGHT);
		stackPanel.add(scrollMessageReceivedPanel, UtilComunicator.createHeaderHTML("img/icon/actions/message_received.png",
				GeneralComunicator.i18nExtension("messaging.label.message.received")), true, UIDesktopConstants.STACK_HEIGHT);
		stackPanel.add(scrollMessageSentPanel, UtilComunicator.createHeaderHTML("img/icon/actions/message.png",
				GeneralComunicator.i18nExtension("messaging.label.message.sent")), true, UIDesktopConstants.STACK_HEIGHT);

		stackPanel.showWidget(0);
		stackPanel.setStartUpFinished();

		stackPanel.setStyleName("okm-StackPanel");
		stackPanel.addStyleName("okm-DisableSelect");
		initWidget(stackPanel);
	}

	/**
	 * refreshProposedSubscriptions
	 */
	public void refreshProposedSubscriptions() {
		proposedSubscriptionReceived.refreshProposedSubscriptions();
	}

	/**
	 * refreshProposedQueries
	 */
	public void refreshProposedQueries() {
		proposedQueryReceived.refreshProposedQueries();
	}

	/**
	 * refreshMessageSent
	 */
	public void refreshMessageSent() {
		messageSent.refreshMessagesSent();
	}

	/**
	 * refreshMessageReceived
	 */
	public void refreshMessageReceived() {
		messageReceived.refreshMessagesReceived();
	}

	/**
	 * isProposedSubscriptionVisible
	 */
	public boolean isProposedSubscriptionReceivedVisible() {
		return (stackPanel.getVisibleIndex() == STACK_SUBSCRIPTION_RECEIVED);
	}

	/**
	 * isProposedQueryVisible
	 *
	 * @return
	 */
	public boolean isProposedQueryReceivedVisible() {
		return (stackPanel.getVisibleIndex() == STACK_QUERY_RECEIVED);
	}

	/**
	 * isMessagesSentVisible
	 *
	 * @return
	 */
	public boolean isMessagesSentVisible() {
		return (stackPanel.getVisibleIndex() == STACK_MESSAGES_SENT);
	}

	/**
	 * isMessagesReceivedVisible
	 *
	 * @return
	 */
	public boolean isMessagesReceivedVisible() {
		return (stackPanel.getVisibleIndex() == STACK_MESSAGES_RECEIVED);
	}

	/**
	 * Refresh language descriptions
	 */
	public void langRefresh() {
		int notSeen = MessagingToolBarBox.get().messageDashboard.messageStack.proposedSubscriptionReceived.getNotSeen();
		String text = GeneralComunicator.i18nExtension("messaging.label.propose") + ((notSeen > 0) ? "&nbsp;(" + notSeen + ")" : "");
		stackPanel.setHeaderHTML(STACK_SUBSCRIPTION_RECEIVED, UtilComunicator.createHeaderHTML("img/icon/actions/propose_subscription.png", text));
		notSeen = MessagingToolBarBox.get().messageDashboard.messageStack.proposedQueryReceived.getNotSeen();
		text = GeneralComunicator.i18nExtension("messaging.label.share.query") + ((notSeen > 0) ? "&nbsp;(" + notSeen + ")" : "");
		stackPanel.setHeaderHTML(STACK_QUERY_RECEIVED, UtilComunicator.createHeaderHTML("img/icon/actions/share_query.gif", text));
		notSeen = MessagingToolBarBox.get().messageDashboard.messageStack.messageReceived.getNotSeen();
		text = GeneralComunicator.i18nExtension("messaging.label.message.received") + ((notSeen > 0) ? "&nbsp;(" + notSeen + ")" : "");
		stackPanel.setHeaderHTML(STACK_MESSAGES_RECEIVED, UtilComunicator.createHeaderHTML("img/icon/actions/message_received.png", text));
		stackPanel.setHeaderHTML(STACK_MESSAGES_SENT, UtilComunicator.createHeaderHTML("img/icon/actions/message.png",
				GeneralComunicator.i18nExtension("messaging.label.message.sent")));
		proposedSubscriptionReceived.langRefresh();
		proposedQueryReceived.langRefresh();
		messageSent.langRefresh();
	}

	/**
	 * refreshProposedSubscriptionStack
	 */
	public void refreshProposedSubscriptionNotSeenStack() {
		int notSeen = MessagingToolBarBox.get().messageDashboard.messageStack.proposedSubscriptionReceived.getNotSeen();
		String text = GeneralComunicator.i18nExtension("messaging.label.propose") + ((notSeen > 0) ? "&nbsp;(" + notSeen + ")" : "");
		stackPanel.setHeaderHTML(STACK_SUBSCRIPTION_RECEIVED, UtilComunicator.createHeaderHTML("img/icon/actions/propose_subscription.png", text));

	}

	/**
	 * refreshProposedQueryNotSeenStack
	 */
	public void refreshProposedQueryNotSeenStack() {
		int notSeen = MessagingToolBarBox.get().messageDashboard.messageStack.proposedQueryReceived.getNotSeen();
		String text = GeneralComunicator.i18nExtension("messaging.label.share.query") + ((notSeen > 0) ? "&nbsp;(" + notSeen + ")" : "");
		stackPanel.setHeaderHTML(STACK_QUERY_RECEIVED, UtilComunicator.createHeaderHTML("img/icon/actions/share_query.gif", text));
	}

	/**
	 * refreshMessageReceivedNotSeenStack
	 */
	public void refreshMessageReceivedNotSeenStack() {
		int notSeen = MessagingToolBarBox.get().messageDashboard.messageStack.messageReceived.getNotSeen();
		String text = GeneralComunicator.i18nExtension("messaging.label.message.received") + ((notSeen > 0) ? "&nbsp;(" + notSeen + ")" : "");
		stackPanel.setHeaderHTML(STACK_MESSAGES_RECEIVED, UtilComunicator.createHeaderHTML("img/icon/actions/message_received.png", text));
	}


	/**
	 * Resizes all objects on the widget the panel and the tree
	 *
	 * @param width
	 *            The widget width
	 * @param height
	 *            The widget height
	 */
	public void setSize(int width, int height) {
		stackPanel.setPixelSize(width - 2, height - 2); // -2 caused by border
		// Substract 2 pixels for borders on stackPanel
		scrollProposeSubscriptionReceivedPanel.setSize("" + (width - 2) + "px", "" + (height - 2 - (NUMBER_OF_STACKS * UIDesktopConstants.STACK_HEIGHT)) + "px");
		scrollProposeQueryReceivedPanel.setSize("" + (width - 2) + "px", "" + (height - 2 - (NUMBER_OF_STACKS * UIDesktopConstants.STACK_HEIGHT)) + "px");
		scrollMessageReceivedPanel.setSize("" + (width - 2) + "px", "" + (height - 2 - (NUMBER_OF_STACKS * UIDesktopConstants.STACK_HEIGHT)) + "px");
		scrollMessageSentPanel.setSize("" + (width - 2) + "px", "" + (height - 2 - (NUMBER_OF_STACKS * UIDesktopConstants.STACK_HEIGHT)) + "px");
	}
}
