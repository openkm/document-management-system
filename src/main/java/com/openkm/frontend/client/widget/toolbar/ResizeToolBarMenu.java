/**
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

package com.openkm.frontend.client.widget.toolbar;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.util.Util;

/**
 * ResizeToolBarMenu
 *
 * @author jllort
 *
 */
public class ResizeToolBarMenu extends DialogBox {

	public static final int NORMAL = 1;
	public static final int CLOSE_TREE = 2;
	public static final int CLOSE_PROPERTIES = 3;
	public static final int EXPAND_TREE = 4;
	public static final int EXPAND_FILEBROWSER = 5;
	public static final int EXPAND_PROPERTIES = 6;

	private MenuBar mainMenu;
	private MenuItem normalView;
	private MenuItem closeTreeView;
	private MenuItem closePropertiesView;
	private MenuItem expandTreeView;
	private MenuItem expandFileBrowserView;
	private MenuItem expandPropertiesView;
	private int actualView = NORMAL;

	/**
	 * ResizeToolBarMenu
	 */
	public ResizeToolBarMenu() {
		// Establishes auto-close when click outside
		super(true, true);

		mainMenu = new MenuBar(true);
		mainMenu.setStyleName("okm-SubMenuBar");
		normalView = new MenuItem(Util.menuHTML("img/icon/actions/normal_size.png", Main.i18n("general.menu.resize.normal")), true, new Command() {
			@Override
			public void execute() {
				restoreToNormal();
				hide();
			}
		});
		normalView.addStyleName("okm-MainMenuItem");
		closeTreeView = new MenuItem(Util.menuHTML("img/icon/actions/close_three_size.png", Main.i18n("general.menu.resize.close.tree")), true, new Command() {
			@Override
			public void execute() {
				if (actualView != CLOSE_TREE) {
					if (actualView != NORMAL) {
						restoreToNormal();
					}
					actualView = CLOSE_TREE;
					Main.get().mainPanel.desktop.closeTreeView();
				}
				hide();
			}
		});
		closeTreeView.addStyleName("okm-MainMenuItem");
		closePropertiesView = new MenuItem(Util.menuHTML("img/icon/actions/close_properties_size.png", Main.i18n("general.menu.resize.close.properties")), true, new Command() {
			@Override
			public void execute() {
				if (actualView != CLOSE_PROPERTIES) {
					if (actualView != NORMAL) {
						restoreToNormal();
					}
					actualView = CLOSE_PROPERTIES;
					Main.get().mainPanel.desktop.browser.expandFileBrowserView();
				}
				hide();
			}
		});
		closePropertiesView.addStyleName("okm-MainMenuItem");
		expandTreeView = new MenuItem(Util.menuHTML("img/icon/actions/close_three_size.png", Main.i18n("general.menu.resize.expand.tree")), true, new Command() {
			@Override
			public void execute() {
				if (actualView != EXPAND_TREE) {
					if (actualView != NORMAL) {
						restoreToNormal();
					}
					actualView = EXPAND_TREE;
					Main.get().mainPanel.desktop.expandTreeView();
				}
				hide();
			}
		});
		expandTreeView.addStyleName("okm-MainMenuItem");
		expandFileBrowserView = new MenuItem(Util.menuHTML("img/icon/actions/filebrowser_size.png", Main.i18n("general.menu.resize.expand.filebrowser")), true, new Command() {
			@Override
			public void execute() {
				if (actualView != EXPAND_FILEBROWSER) {
					if (actualView != NORMAL) {
						restoreToNormal();
					}
					actualView = EXPAND_FILEBROWSER;
					Main.get().mainPanel.desktop.closeTreeView();
					Main.get().mainPanel.desktop.browser.expandFileBrowserView();
				}
				hide();
			}
		});
		expandFileBrowserView.addStyleName("okm-MainMenuItem");
		expandPropertiesView = new MenuItem(Util.menuHTML("img/icon/actions/properties_size.png", Main.i18n("general.menu.resize.expand.properties")), true, new Command() {
			@Override
			public void execute() {
				if (actualView != EXPAND_PROPERTIES) {
					if (actualView != NORMAL) {
						restoreToNormal();
					}
					actualView = EXPAND_PROPERTIES;
					Main.get().mainPanel.desktop.closeTreeView();
					Main.get().mainPanel.desktop.browser.expandTabView();
				}
				hide();
			}
		});
		expandPropertiesView.addStyleName("okm-MainMenuItem");

		mainMenu.addItem(normalView);
		mainMenu.addItem(closeTreeView);
		mainMenu.addItem(closePropertiesView);
		mainMenu.addItem(expandTreeView);
		mainMenu.addItem(expandFileBrowserView);
		mainMenu.addItem(expandPropertiesView);

		setWidget(mainMenu);
	}

	/**
	 * restoreToNormal
	 */
	public void restoreToNormal() {
		switch (actualView) {
			case NORMAL:
				break;
			case CLOSE_TREE:
			case EXPAND_TREE:
				Main.get().mainPanel.desktop.restoreNormalView();
				break;
			case EXPAND_FILEBROWSER:
			case EXPAND_PROPERTIES:
				Main.get().mainPanel.desktop.restoreNormalView();
				Main.get().mainPanel.desktop.browser.restoreNormalView();
				break;
			case CLOSE_PROPERTIES:
				Main.get().mainPanel.desktop.browser.restoreNormalView();
				break;
		}
		actualView = NORMAL;
	}

	/**
	 * windowResized
	 */
	public void windowResized() {
		actualView = NORMAL;
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		normalView.setHTML(Util.menuHTML("img/icon/actions/normal_size.png", Main.i18n("general.menu.resize.normal")));
		closeTreeView.setHTML(Util.menuHTML("img/icon/actions/close_three_size.png", Main.i18n("general.menu.resize.close.tree")));
		closePropertiesView.setHTML(Util.menuHTML("img/icon/actions/close_Properties_size.png", Main.i18n("general.menu.resize.close.properties")));
		expandTreeView.setHTML(Util.menuHTML("img/icon/actions/expand_tree_size.png", Main.i18n("general.menu.resize.expand.tree")));
		expandFileBrowserView.setHTML(Util.menuHTML("img/icon/actions/filebrowser_size.png", Main.i18n("general.menu.resize.expand.filebrowser")));
		expandPropertiesView.setHTML(Util.menuHTML("img/icon/actions/properties_size.png", Main.i18n("general.menu.resize.expand.properties")));
	}
}