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

package com.openkm.extension.frontend.client.widget.messaging;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.extension.frontend.client.widget.messaging.propose.MessagePopup;
import com.openkm.extension.frontend.client.widget.messaging.propose.ProposedQueryPopup;
import com.openkm.extension.frontend.client.widget.messaging.propose.ProposedSubscriptionPopup;
import com.openkm.extension.frontend.client.widget.messaging.userinfo.*;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.event.HasDashboardEvent;
import com.openkm.frontend.client.extension.event.HasDashboardEvent.DashboardEventConstant;
import com.openkm.frontend.client.extension.event.HasLanguageEvent;
import com.openkm.frontend.client.extension.event.HasLanguageEvent.LanguageEventConstant;
import com.openkm.frontend.client.extension.event.handler.DashboardHandlerExtension;
import com.openkm.frontend.client.extension.event.handler.LanguageHandlerExtension;
import com.openkm.frontend.client.extension.widget.toolbar.ToolBarBoxExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * MessagingToolBarBox
 *
 * @author jllort
 *
 */
public class MessagingToolBarBox implements LanguageHandlerExtension, DashboardHandlerExtension {
	public static final int PROPOSED_QUERY_NONE = -1;
	public static final int PROPOSED_QUERY_SAVE_SEARCH = 0;
	public static final int PROPOSED_QUERY_USER_NEWS = 1;

	private static MessagingToolBarBox singleton;
	private static final String UUID = "a1925a00-ef41-11df-98cf-0800200c9a66";

	public MessageDashboard messageDashboard;
	public ToolBarBoxExtension messagingToolBarBox;
	private ProposedSubscriptionReceivedUserInfoImage proposedSubscriptionReceivedUserInfoImage;
	private ProposedSubscriptionReceivedUserInfoValue proposedSubscriptionReceivedUserInfoValue;
	private ProposedQueryReceivedUserInfoImage proposedQueryReceivedUserInfoImage;
	private ProposedQueryReceivedUserInfoValue proposedQueryreceivedUserInfoValue;
	private MessageReceivedUserInfoImage messageReceivedUserInfoImage;
	private MessageReceivedUserInfoValue messageReceivedUserInfoValue;
	private SubMenuMessage subMenuMessage;
	public ProposedSubscriptionPopup proposedSubscriptionPopup;
	public ProposedQueryPopup proposedQueryPopup;
	public MessagePopup messagePopup;
	public ConfirmPopup confirmPopup;

	/**
	 * MessagingToolBarBox
	 */
	public MessagingToolBarBox(List<String> uuidList) {
		singleton = this;

		if (isRegistered(uuidList)) {
			messagingToolBarBox = new ToolBarBoxExtension(new Image(OKMBundleResources.INSTANCE.messaging()),
					GeneralComunicator.i18nExtension("dashboard.tab.messaging")) {
				@Override
				public Widget getWidget() {
					return messageDashboard;
				}
			};

			messageDashboard = new MessageDashboard();
			proposedSubscriptionReceivedUserInfoImage = new ProposedSubscriptionReceivedUserInfoImage();
			proposedSubscriptionReceivedUserInfoValue = new ProposedSubscriptionReceivedUserInfoValue();
			proposedQueryReceivedUserInfoImage = new ProposedQueryReceivedUserInfoImage();
			proposedQueryreceivedUserInfoValue = new ProposedQueryReceivedUserInfoValue();
			messageReceivedUserInfoImage = new MessageReceivedUserInfoImage();
			messageReceivedUserInfoValue = new MessageReceivedUserInfoValue();
			subMenuMessage = new SubMenuMessage();
			proposedSubscriptionPopup = new ProposedSubscriptionPopup();
			proposedSubscriptionPopup.setWidth("400px");
			proposedSubscriptionPopup.setHeight("100px");
			proposedSubscriptionPopup.setStyleName("okm-Popup");
			proposedQueryPopup = new ProposedQueryPopup();
			proposedQueryPopup.setWidth("400px");
			proposedQueryPopup.setHeight("100px");
			proposedQueryPopup.setStyleName("okm-Popup");
			messagePopup = new MessagePopup();
			messagePopup.setWidth("400px");
			messagePopup.setHeight("100px");
			messagePopup.setStyleName("okm-Popup");
			confirmPopup = new ConfirmPopup();
			confirmPopup.setWidth("300px");
			confirmPopup.setHeight("125px");
			confirmPopup.setStyleName("okm-Popup");
			confirmPopup.addStyleName("okm-DisableSelect");
		}
	}

	/**
	 * ToolBarBoxExtension
	 */
	public ToolBarBoxExtension getToolBarBox() {
		return messagingToolBarBox;
	}

	/**
	 * getExtensions
	 */
	public List<Object> getExtensions() {
		List<Object> extensions = new ArrayList<Object>();
		extensions.add(singleton);
		extensions.add(messagingToolBarBox);
		extensions.add(proposedSubscriptionReceivedUserInfoImage);
		extensions.add(proposedSubscriptionReceivedUserInfoValue);
		extensions.add(proposedQueryReceivedUserInfoImage);
		extensions.add(proposedQueryreceivedUserInfoValue);
		extensions.add(messageReceivedUserInfoImage);
		extensions.add(messageReceivedUserInfoValue);
		extensions.add(subMenuMessage.getMenu());
		return extensions;
	}

	/**
	 * get
	 */
	public static MessagingToolBarBox get() {
		return singleton;
	}

	/**
	 * langRefresh
	 */
	private void langRefresh() {
		messagingToolBarBox.setLabelText(GeneralComunicator.i18nExtension("dashboard.tab.messaging"));
		messageDashboard.langRefresh();
		proposedSubscriptionReceivedUserInfoImage.langRefresh();
		subMenuMessage.langRefresh();
		proposedSubscriptionPopup.langRefresh();
		proposedQueryPopup.langRefresh();
		messagePopup.langRefresh();
	}

	/**
	 * refreshProposedSubscriptionNotSeenValues
	 */
	public void refreshProposedSubscriptionNotSeenValues() {
		int notSeen = MessagingToolBarBox.get().messageDashboard.messageStack.proposedSubscriptionReceived.getNotSeen();
		proposedSubscriptionReceivedUserInfoValue.updateValue(notSeen);
	}

	/**
	 * refreshProposedQueryNotSeenValues
	 */
	public void refreshProposedQueryNotSeenValues() {
		int notSeen = MessagingToolBarBox.get().messageDashboard.messageStack.proposedQueryReceived.getNotSeen();
		proposedQueryreceivedUserInfoValue.updateValue(notSeen);
	}

	/**
	 * refreshProposedQueryNotSeenValues
	 */
	public void refreshMessageReceivedNotSeenValues() {
		int notSeen = MessagingToolBarBox.get().messageDashboard.messageStack.messageReceived.getNotSeen();
		messageReceivedUserInfoValue.updateValue(notSeen);
	}

	/**
	 * executeProposeSubscription
	 */
	public void executeProposeSubscription(String uuid, String type) {
		proposedSubscriptionPopup.executeProposeSubscription(uuid, type);
	}

	/**
	 * executeProposeQuery
	 */
	public void executeProposeQuery(int type) {
		proposedQueryPopup.executeProposeQuery(type);
	}

	/**
	 * executeSendMessage
	 */
	public void executeSendMessage() {
		messagePopup.executeSendMessage();
	}

	/**
	 * isRegistered
	 */
	public static boolean isRegistered(List<String> uuidList) {
		return uuidList.contains(UUID);
	}

	@Override
	public void onChange(LanguageEventConstant event) {
		if (event.equals(HasLanguageEvent.LANGUAGE_CHANGED)) {
			langRefresh();
		}
	}

	@Override
	public void onChange(DashboardEventConstant event) {
		if (event.equals(HasDashboardEvent.DASHBOARD_REFRESH)) {
			messageDashboard.refreshAll();
		}
	}

}