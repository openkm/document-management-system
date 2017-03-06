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

package com.openkm.extension.frontend.client.widget.messaging.userinfo;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.extension.frontend.client.widget.messaging.MessagingToolBarBox;
import com.openkm.extension.frontend.client.widget.messaging.stack.MessageStack;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.extension.comunicator.DashboardComunicator;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.WorkspaceComunicator;
import com.openkm.frontend.client.extension.widget.userinfo.UserInfoExtension;

/**
 * MessageReceivedUserInfoImage
 *
 * @author jllort
 *
 */
public class MessageReceivedUserInfoImage extends UserInfoExtension {

	private Image image;

	/**
	 * MessageReceivedUserInfoImage
	 */
	public MessageReceivedUserInfoImage() {
		image = new Image(OKMBundleResources.INSTANCE.messageReceived());
		image.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				WorkspaceComunicator.changeSelectedTab(UIDockPanelConstants.DASHBOARD);
				DashboardComunicator.showToolBoxExtension(MessagingToolBarBox.get().messagingToolBarBox);
				MessagingToolBarBox.get().messageDashboard.messageStack.stackPanel.showWidget(MessageStack.STACK_MESSAGES_RECEIVED);
			}
		});
		image.setTitle(GeneralComunicator.i18nExtension("messaging.user.info.new.message.received"));
		image.setStyleName("okm-Hyperlink");
		initWidget(image);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		image.setTitle(GeneralComunicator.i18nExtension("messaging.user.info.new.message.received"));
	}
}