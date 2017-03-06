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

import com.google.gwt.user.client.Command;
import com.openkm.frontend.client.constants.ui.UIMenuConstants;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.UtilComunicator;
import com.openkm.frontend.client.extension.widget.menu.MenuBarExtension;
import com.openkm.frontend.client.extension.widget.menu.MenuItemExtension;

/**
 * SubMenuMessage
 *
 * @author jllort
 *
 */
public class SubMenuMessage {

	private MenuItemExtension messageMenu;
	private MenuBarExtension subMenuMessage;
	private MenuItemExtension sendNewMessage;

	/**
	 * SubMenuMessage
	 */
	public SubMenuMessage() {
		// All menu items
		sendNewMessage = new MenuItemExtension("img/icon/actions/new_message.png", GeneralComunicator.i18nExtension("messaging.send.new.message"), sendMessage);

		// Principal submenu
		subMenuMessage = new MenuBarExtension();
		subMenuMessage.addItem(sendNewMessage);
		messageMenu = new MenuItemExtension("img/icon/actions/message.png", GeneralComunicator.i18nExtension("messaging.menu"), subMenuMessage);
		messageMenu.setMenuLocation(UIMenuConstants.MAIN_MENU_TOOLS);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		sendNewMessage.setHTML(UtilComunicator.menuHTML("img/icon/actions/new_message.png", GeneralComunicator.i18nExtension("messaging.send.new.message")));
		messageMenu.setHTML(UtilComunicator.menuHTML("img/icon/actions/message.png", GeneralComunicator.i18nExtension("messaging.menu")));
	}

	/**
	 * @return
	 */
	public MenuItemExtension getMenu() {
		return messageMenu;
	}

	/**
	 * option1Action
	 */
	Command sendMessage = new Command() {
		public void execute() {
			MessagingToolBarBox.get().executeSendMessage();
		}
	};
}