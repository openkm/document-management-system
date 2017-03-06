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

package com.openkm.frontend.client.widget.trash;

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
	private MenuItem restore;
	private MenuItem purge;
	private MenuItem purgeTrash;

	public TrashMenu() {
		toolBarOption = new ToolBarOption();
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected

		// First initialize language values
		dirMenu = new MenuBar(true);
		restore = new MenuItem(Util.menuHTML("img/icon/actions/restore.gif", Main.i18n("general.menu.file.restore")), true, restoreFolder);
		restore.addStyleName("okm-MenuItem");
		dirMenu.addItem(restore);
		purge = new MenuItem(Util.menuHTML("img/icon/actions/purge.gif", Main.i18n("general.menu.file.purge")), true, purgeFolder);
		purge.addStyleName("okm-MenuItem");
		dirMenu.addItem(purge);
		purgeTrash = new MenuItem(Util.menuHTML("img/icon/actions/purge_trash.gif", Main.i18n("general.menu.file.purge.trash")), true, purgeTrashFolder);
		purgeTrash.addStyleName("okm-MenuItem");
		dirMenu.addItem(purgeTrash);
		dirMenu.setStyleName("okm-MenuBar");
		initWidget(dirMenu);
	}

	// Command menu to restore Directory
	Command restoreFolder = new Command() {
		public void execute() {
			if (toolBarOption.restore) {
				Main.get().mainPanel.topPanel.toolBar.executeRestore();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	// Command menu to remove a Directory
	Command purgeFolder = new Command() {
		public void execute() {
			if (toolBarOption.purge) {
				Main.get().mainPanel.topPanel.toolBar.executePurge();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	// Command menu to remove all trash folder
	Command purgeTrashFolder = new Command() {
		public void execute() {
			if (toolBarOption.purgeTrash) {
				Main.get().mainPanel.topPanel.toolBar.executePurgeTrash();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	@Override
	public void langRefresh() {
		restore.setHTML(Util.menuHTML("img/icon/actions/restore.gif", Main.i18n("general.menu.file.restore")));
		purge.setHTML(Util.menuHTML("img/icon/actions/purge.gif", Main.i18n("general.menu.file.purge")));
		purgeTrash.setHTML(Util.menuHTML("img/icon/actions/purge_trash.gif", Main.i18n("general.menu.file.purge.trash")));
	}

	@Override
	public void evaluateMenuOptions() {
		if (toolBarOption.restore) {
			enable(restore);
		} else {
			disable(restore);
		}
		if (toolBarOption.purge) {
			enable(purge);
		} else {
			disable(purge);
		}
		if (toolBarOption.purgeTrash) {
			enable(purgeTrash);
		} else {
			disable(purgeTrash);
		}
	}

	@Override
	public void setAvailableOption(GWTAvailableOption option) {
		if (!option.isRestoreOption()) {
			dirMenu.removeItem(restore);
		}
		if (!option.isPurgeOption()) {
			dirMenu.removeItem(purge);
		}
		if (!option.isPurgeTrashOption()) {
			dirMenu.removeItem(purgeTrash);
		}
	}

	@Override
	public void setOptions(ToolBarOption toolBarOption) {
		this.toolBarOption = toolBarOption;
		evaluateMenuOptions();
	}

	@Override
	public void disableAllOptions() {
		toolBarOption = new ToolBarOption();
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
}