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

package com.openkm.frontend.client.widget.searchresult;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTAvailableOption;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.util.Util;

/**
 * Search result menu
 *
 * @author jllort
 *
 */
public class Menu extends Composite {

	private boolean downloadOption = false;
	private boolean goOption = false;
	private boolean findSimilarDocumentOption = false;

	private MenuBar searchMenu;
	private MenuItem download;
	private MenuItem go;
	private MenuItem findSimilarDocument;

	/**
	 * Browser menu
	 */
	public Menu() {
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected

		// First initialize language values
		searchMenu = new MenuBar(true);
		download = new MenuItem(Util.menuHTML("img/icon/actions/download.gif", Main.i18n("search.result.menu.download")), true, downloadFile);
		download.addStyleName("okm-MenuItem");
		go = new MenuItem(Util.menuHTML("img/icon/actions/goto_folder.gif", Main.i18n("search.result.menu.go.folder")), true, goDirectory);
		go.addStyleName("okm-MenuItem");
		findSimilarDocument = new MenuItem(Util.menuHTML("img/icon/actions/similar_find.png", Main.i18n("general.menu.file.find.similar.document")), true, findSimilarOKM);
		findSimilarDocument.addStyleName("okm-MenuItem");

		searchMenu.addItem(download);
		searchMenu.addItem(go);
		searchMenu.addItem(findSimilarDocument);
		searchMenu.setStyleName("okm-MenuBar");

		initWidget(searchMenu);
	}

	// Command menu to download file
	Command downloadFile = new Command() {
		public void execute() {
			if (downloadOption) {
				Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.downloadDocument();
			}
			hide();
		}
	};

	// Command menu to go directory file
	Command goDirectory = new Command() {
		public void execute() {
			if (goOption) {
				Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.openAllFolderPath();
			}
			hide();
		}
	};

	// Command menu to find similardocument
	Command findSimilarOKM = new Command() {
		public void execute() {
			if (findSimilarDocumentOption) {
				Main.get().mainPanel.topPanel.toolBar.executeFindSimilarDocument();
				hide();
			}
		}
	};

	/**
	 *  Refresh language values
	 */
	public void langRefresh() {
		download.setHTML(Util.menuHTML("img/icon/actions/download.gif", Main.i18n("search.result.menu.download")));
		go.setHTML(Util.menuHTML("img/icon/actions/goto_folder.gif", Main.i18n("search.result.menu.go.folder")));
		findSimilarDocument.setHTML(Util.menuHTML("img/icon/actions/similar_find.png", Main.i18n("general.menu.file.find.similar.document")));
	}

	/**
	 * Hide popup menu
	 */
	public void hide() {
		Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.menuPopup.hide();
	}

	/**
	 * Checks permissions
	 *
	 * @param folder
	 */
	public void checkMenuOptionPermissions(GWTFolder folder) {
		downloadOption = false;
		goOption = true;
		findSimilarDocumentOption = false;
	}

	/**
	 * Checks permissions
	 *
	 * @param doc
	 */
	public void checkMenuOptionPermissions(GWTDocument doc) {
		downloadOption = true;
		goOption = true;
		findSimilarDocumentOption = true;
	}

	/**
	 * Checks permissions
	 *
	 * @param mail
	 */
	public void checkMenuOptionPermissions(GWTMail mail) {
		downloadOption = true;
		goOption = true;
		findSimilarDocumentOption = false;
	}

	/**
	 * Evaluates menu options
	 */
	public void evaluateMenuOptions() {
		if (downloadOption) {
			enable(download);
		} else {
			disable(download);
		}
		if (goOption) {
			enable(go);
		} else {
			disable(go);
		}
		if (findSimilarDocumentOption) {
			enable(findSimilarDocument);
		} else {
			disable(findSimilarDocument);
		}
		if (Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.table.isFolderSelected()) {
			go.setHTML(Util.menuHTML("img/icon/actions/goto_folder.gif", Main.i18n("search.result.menu.go.folder")));
		} else {
			go.setHTML(Util.menuHTML("img/icon/actions/goto_document.gif", Main.i18n("search.result.menu.go.document")));
		}
	}

	/**
	 * Enables menu item
	 *
	 * @param menuItem The menu item
	 */
	public void enable(MenuItem menuItem) {
		menuItem.addStyleName("okm-MenuItem");
		menuItem.removeStyleName("okm-MenuItem-strike");
	}

	/**
	 * Disable the menu item
	 *
	 * @param menuItem The menu item
	 */
	public void disable(MenuItem menuItem) {
		menuItem.removeStyleName("okm-MenuItem");
		menuItem.addStyleName("okm-MenuItem-strike");
	}

	/**
	 * setAvailableOption
	 *
	 * @param option
	 */
	public void setAvailableOption(GWTAvailableOption option) {
		if (!option.isDownloadOption()) {
			searchMenu.removeItem(download);
		}
		if (!option.isGotoFolderOption()) {
			searchMenu.removeItem(go);
		}
		if (!option.isSimilarDocumentVisible()) {
			searchMenu.removeItem(findSimilarDocument);
		}
	}
}