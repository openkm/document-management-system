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


package com.openkm.extension.frontend.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.openkm.frontend.client.extension.widget.menu.MenuBarExtension;
import com.openkm.frontend.client.extension.widget.menu.MenuItemExtension;

/**
 * MainMenuExample
 *
 * @author jllort
 *
 */
public class MainMenuExample {

	private MenuItemExtension exampleMenu;
	private MenuBarExtension subMenuExample;
	private MenuBarExtension subMenuExample2;
	private MenuItemExtension subMenuItem;
	private MenuItemExtension option1;
	private MenuItemExtension option2;
	private MenuItemExtension option3;
	private MenuItemExtension option4;

	/**
	 * MainMenuExample
	 */
	public MainMenuExample() {
		// All menu items
		option1 = new MenuItemExtension("img/box.png", "Option 1", option1Action);
		option2 = new MenuItemExtension("img/box.png", "Option 2", option2Action);
		option3 = new MenuItemExtension("img/box.png", "Option 3", option3Action);
		option4 = new MenuItemExtension("img/box.png", "Option 4", option4Action);

		// Secondary submenu
		subMenuExample2 = new MenuBarExtension();
		subMenuExample2.addItem(option3);
		subMenuExample2.addItem(option4);
		subMenuItem = new MenuItemExtension("img/box.png", "Sub menu", subMenuExample2); // is a secondary submenu

		// Principal submenu
		subMenuExample = new MenuBarExtension();
		subMenuExample.addItem(option1);
		subMenuExample.addItem(option2);
		subMenuExample.addItem(subMenuItem);

		// Principal menuitem
		exampleMenu = new MenuItemExtension("New Menu", subMenuExample); // is not a secondary submenu
	}

	public MenuItemExtension getNewMenu() {
		return exampleMenu;
	}

	/**
	 * option1Action
	 */
	Command option1Action = new Command() {
		public void execute() {
			Window.alert("option1 action");
		}
	};

	/**
	 * option2Action
	 */
	Command option2Action = new Command() {
		public void execute() {
			Window.alert("option2 action");
		}
	};

	/**
	 * option3Action
	 */
	Command option3Action = new Command() {
		public void execute() {
			Window.alert("option3 action");
		}
	};

	/**
	 * option4Action
	 */
	Command option4Action = new Command() {
		public void execute() {
			Window.alert("option4 action");
		}
	};
}