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

package com.openkm.frontend.client.widget.filebrowser.menu;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTAvailableOption;
import com.openkm.frontend.client.bean.ToolBarOption;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.MenuBase;

/**
 * Trash menu
 *
 * @author jllort
 *
 */
public class TrashMenu extends MenuBase {

	private ToolBarOption toolBarOption;
	private MenuBar dirMenu;
	private MenuItem restoreItem;
	private MenuItem purgeItem;

	public TrashMenu() {
		toolBarOption = new ToolBarOption();
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected

		// First initialize language values
		dirMenu = new MenuBar(true);
		restoreItem = new MenuItem(Util.menuHTML("img/icon/actions/restore.gif", Main.i18n("general.menu.file.restore")), true, restore);
		restoreItem.addStyleName("okm-MenuItem");
		dirMenu.addItem(restoreItem);
		purgeItem = new MenuItem(Util.menuHTML("img/icon/actions/purge.gif", Main.i18n("general.menu.file.purge")), true, purge);
		purgeItem.addStyleName("okm-MenuItem");
		dirMenu.addItem(purgeItem);
		dirMenu.addStyleName("okm-MenuBar");
		initWidget(dirMenu);
	}

	// Command menu to restore Directory or Document
	Command restore = new Command() {
		public void execute() {
			if (toolBarOption.restore) {
				Main.get().mainPanel.topPanel.toolBar.executeRestore();
				Main.get().mainPanel.desktop.browser.fileBrowser.trashMenuPopup.hide();
			}
		}
	};

	// Command menu to remove a Directory or Document
	Command purge = new Command() {
		public void execute() {
			if (toolBarOption.purge) {
				Main.get().mainPanel.topPanel.toolBar.executePurge();
				Main.get().mainPanel.desktop.browser.fileBrowser.trashMenuPopup.hide();
			}
		}
	};

	@Override
	public void langRefresh() {
		restoreItem.setHTML(Util.menuHTML("img/icon/actions/restore.gif", Main.i18n("general.menu.file.restore")));
		purgeItem.setHTML(Util.menuHTML("img/icon/actions/purge.gif", Main.i18n("general.menu.file.purge")));
	}

	@Override
	public void setAvailableOption(GWTAvailableOption option) {
		if (!option.isRestoreOption()) {
			dirMenu.removeItem(restoreItem);
		}
		if (!option.isPurgeOption()) {
			dirMenu.removeItem(purgeItem);
		}
	}

	@Override
	public void evaluateMenuOptions() {
		if (toolBarOption.restore) {
			enable(restoreItem);
		} else {
			disable(restoreItem);
		}
		if (toolBarOption.purge) {
			enable(purgeItem);
		} else {
			disable(purgeItem);
		}
	}

	@Override
	public void setOptions(ToolBarOption toolBarOption) {
		this.toolBarOption = toolBarOption;
		evaluateMenuOptions();
	}

	@Override
	public void enableAddPropertyGroup() {
	}

	@Override
	public void disableAddPropertyGroup() {
	}

	@Override
	public void disablePdfMerge() {
	}

	@Override
	public void enablePdfMerge() {
	}

	@Override
	public void disableAllOptions() {
		toolBarOption = new ToolBarOption();
		evaluateMenuOptions();
	}
}