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

package com.openkm.frontend.client.extension.widget.menu;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.frontend.client.constants.ui.UIMenuConstants;
import com.openkm.frontend.client.util.Util;

/**
 * MenuItemExtension
 *
 * @author jllort
 *
 */
public class MenuItemExtension extends MenuItem {

	private int menuLocation = UIMenuConstants.NEW_MENU;

	/**
	 * MenuItemExtension
	 *
	 * @param imageURL
	 * @param text
	 * @param cmd
	 */
	public MenuItemExtension(String imageURL, String text, Command cmd) {
		super(Util.menuHTML(imageURL, text), true, cmd);
		addStyleName("okm-MainMenuItem");
	}

	/**
	 * MenuItemExtension
	 *
	 * @param imageURL
	 * @param text
	 * @param menuBar
	 */
	public MenuItemExtension(String imageURL, String text, MenuBar menuBar) {
		super(Util.menuHTML(imageURL, text), true, menuBar);
		addStyleName("okm-MainMenuItem");
	}

	/**
	 * MenuItemExtension
	 *
	 * @param text
	 * @param cmd
	 */
	public MenuItemExtension(String text, MenuBar menuBar) {
		super(text, menuBar);
		addStyleName("okm-MainMenuBar");
	}

	/**
	 * setMenuLocation
	 *
	 * @param menuLocation
	 */
	public void setMenuLocation(int menuLocation) {
		this.menuLocation = menuLocation;
	}

	/**
	 * getMenuLocation
	 *
	 * @return
	 */
	public int getMenuLocation() {
		return menuLocation;
	}
}