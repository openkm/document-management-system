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

package com.openkm.frontend.client.widget.filebrowser;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTAvailableOption;
import com.openkm.frontend.client.bean.ToolBarOption;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.MenuBase;

/**
 * CategoryMenu menu
 *
 * @author jllort
 *
 */
public class MassiveOperationsMenu extends MenuBase {
	private MenuBar dirMenu;
	private MenuItem selectAll;
	private MenuItem selectFolders;
	private MenuItem selectDocuments;
	private MenuItem selectMails;
	private MenuItem removeSelection;

	/**
	 * Category menu
	 */
	public MassiveOperationsMenu() {
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected

		// First initialize language values
		dirMenu = new MenuBar(true);
		selectAll = new MenuItem(Util.menuHTML("img/icon/actions/select_all.png", Main.i18n("filebrowser.menu.select.all")), true, massiveSelectAll);
		selectAll.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(selectAll);
		selectFolders = new MenuItem(Util.menuHTML("img/menuitem_empty.gif", Main.i18n("filebrowser.menu.select.all.folders")), true, massiveSelectFolders);
		selectFolders.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(selectFolders);
		selectDocuments = new MenuItem(Util.menuHTML("img/document.png", Main.i18n("filebrowser.menu.select.all.documents")), true, massiveSelectDocuments);
		selectDocuments.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(selectDocuments);
		selectMails = new MenuItem(Util.menuHTML("img/email.gif", Main.i18n("filebrowser.menu.select.all.mails")), true, massiveSelectMails);
		selectMails.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(selectMails);
		removeSelection = new MenuItem(Util.menuHTML("img/icon/actions/remove_all.png", Main.i18n("filebrowser.menu.remove.selection")), true, massiveRemoveSelection);
		removeSelection.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(removeSelection);
		dirMenu.setStyleName("okm-MenuBar");
		initWidget(dirMenu);

		enable(selectAll);
		enable(selectFolders);
		enable(selectDocuments);
		enable(selectMails);
	}

	// Command menu to select all file
	Command massiveSelectAll = new Command() {
		public void execute() {
			Main.get().mainPanel.desktop.browser.fileBrowser.selectAllMassive();
			hide();
		}
	};

	// Command menu to select all folders
	Command massiveSelectFolders = new Command() {
		public void execute() {
			Main.get().mainPanel.desktop.browser.fileBrowser.selectAllFoldersMassive();
			hide();
		}
	};

	// Command menu to select all documents
	Command massiveSelectDocuments = new Command() {
		public void execute() {
			Main.get().mainPanel.desktop.browser.fileBrowser.selectAllDocumentsMassive();
			hide();
		}
	};

	// Command menu to select all mails
	Command massiveSelectMails = new Command() {
		public void execute() {
			Main.get().mainPanel.desktop.browser.fileBrowser.selectAllMailsMassive();
			hide();
		}
	};

	// Command menu to remove selection file
	Command massiveRemoveSelection = new Command() {
		public void execute() {
			Main.get().mainPanel.desktop.browser.fileBrowser.removeAllMassive();
			hide();
		}
	};

	@Override
	public void langRefresh() {
		selectAll.setHTML(Util.menuHTML("img/icon/actions/select_all.png", Main.i18n("filebrowser.menu.select.all")));
		selectFolders.setHTML(Util.menuHTML("img/menuitem_empty.gif", Main.i18n("filebrowser.menu.select.all.folders")));
		selectDocuments.setHTML(Util.menuHTML("img/document.png", Main.i18n("filebrowser.menu.select.all.documents")));
		selectMails.setHTML(Util.menuHTML("img/email.gif", Main.i18n("filebrowser.menu.select.all.mails")));
		removeSelection.setHTML(Util.menuHTML("img/icon/actions/remove_all.png", Main.i18n("filebrowser.menu.remove.selection")));
	}

	@Override
	public void setOptions(ToolBarOption toolBarOption) {
	}

	@Override
	public void disableAllOptions() {
	}

	/**
	 * Evaluates menu options
	 */
	public void evaluateMenuOptions() {
		enable(selectAll);
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			enable(removeSelection);
		} else {
			disable(removeSelection);
		}
	}


	@Override
	public void setAvailableOption(GWTAvailableOption option) {
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

	/**
	 * Hide popup menu
	 */
	public void hide() {
		Main.get().mainPanel.desktop.browser.fileBrowser.massiveOperationsMenuPopup.hide();
	}
}