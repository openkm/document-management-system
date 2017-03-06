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

package com.openkm.frontend.client.extension.comunicator;

import com.google.gwt.user.client.ui.Widget;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTMail;

/**
 * TabMailComunicator
 *
 * @author jllort
 *
 */
public class TabMailComunicator {

	/**
	 * getSelectedTab
	 *
	 * @return
	 */
	public static int getSelectedTab() {
		return Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.getSelectedTab();
	}

	/**
	 * getMail
	 *
	 * @return
	 */
	public static GWTMail getMail() {
		return Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.getMail();
	}

	/**
	 * isWidgetExtensionVisible
	 *
	 * @param widget
	 * @return
	 */
	public static boolean isWidgetExtensionVisible(Widget widget) {
		return Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.isWidgetExtensionVisible(widget);
	}
}