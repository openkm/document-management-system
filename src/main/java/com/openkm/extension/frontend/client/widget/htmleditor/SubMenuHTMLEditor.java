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

package com.openkm.extension.frontend.client.widget.htmleditor;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.frontend.client.constants.ui.UIMenuConstants;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.UtilComunicator;
import com.openkm.frontend.client.extension.widget.menu.MenuBarExtension;
import com.openkm.frontend.client.extension.widget.menu.MenuItemExtension;

/**
 * SubMenuHTMLEditor
 *
 * @author jllort
 *
 */
public class SubMenuHTMLEditor {
	private MenuItemExtension htmlEditorMenu;
	private MenuItemExtension htmlEditorEnabled;
	private MenuItemExtension htmlEditorDisabled;
	private MenuBarExtension subMenu;
	private boolean enabled = true;

	/**
	 * SubMenuLiveEditor
	 */
	public SubMenuHTMLEditor() {
		// All menu items
		htmlEditorEnabled = new MenuItemExtension("img/icon/security/yes.gif", GeneralComunicator.i18n("general.menu.enable"), enableHTMLEditor);
		htmlEditorEnabled.addStyleName("okm-MenuItem-strike");
		htmlEditorDisabled = new MenuItemExtension("img/icon/security/no.gif", GeneralComunicator.i18n("general.menu.disable"), disableHTMLEditor);
		htmlEditorDisabled.addStyleName("okm-MenuItem-strike");
		enable(htmlEditorEnabled);
		disable(htmlEditorDisabled);

		// Principal submenu
		subMenu = new MenuBarExtension();
		subMenu.addItem(htmlEditorEnabled);
		subMenu.addItem(htmlEditorDisabled);

		// Principal menu
		htmlEditorMenu = new MenuItemExtension("img/icon/actions/checkout.gif", "HTML editor", subMenu);
		htmlEditorMenu.setMenuLocation(UIMenuConstants.MAIN_MENU_TOOLS);
	}

	/**
	 * getMenu
	 */
	public MenuItemExtension getMenu() {
		return htmlEditorMenu;
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		htmlEditorEnabled.setHTML(UtilComunicator.menuHTML("img/icon/security/yes.gif", GeneralComunicator.i18n("general.menu.enable")));
		htmlEditorDisabled.setHTML(UtilComunicator.menuHTML("img/icon/security/no.gif", GeneralComunicator.i18n("general.menu.disable")));
		htmlEditorMenu.setHTML(UtilComunicator.menuHTML("img/icon/actions/checkout.gif", "HTML editor"));
	}

	/**
	 * enableHTMLEditor
	 */
	Command enableHTMLEditor = new Command() {
		public void execute() {
			enabled = true;
			enable(htmlEditorEnabled);
			disable(htmlEditorDisabled);
		}
	};

	/**
	 * disableHTMLEditor
	 */
	Command disableHTMLEditor = new Command() {
		public void execute() {
			enabled = false;
			disable(htmlEditorEnabled);
			enable(htmlEditorDisabled);
		}
	};

	/**
	 * Enables menu item
	 *
	 * @param menuItem The menu item
	 */
	private void enable(MenuItem menuItem) {
		menuItem.removeStyleName("okm-MenuItem-strike");
	}

	/**
	 * Disables the menu item with and strike
	 *
	 * @param menuItem The menu item
	 */
	private void disable(MenuItem menuItem) {
		menuItem.addStyleName("okm-MenuItem-strike");
	}

	/**
	 * isEnabled
	 *
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}
}