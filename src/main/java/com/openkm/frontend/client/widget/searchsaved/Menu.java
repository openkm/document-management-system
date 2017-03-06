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

package com.openkm.frontend.client.widget.searchsaved;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.extension.frontend.client.widget.messaging.MessagingToolBarBox;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTQueryParams;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.ConfirmPopup;

/**
 * Search saved menu
 *
 * @author jllort
 *
 */
public class Menu extends Composite {
	private MenuBar searchSavedMenu;
	private MenuItem run;
	private MenuItem delete;
	private MenuItem share;

	private boolean shareOption = false;

	/**
	 * Browser menu
	 */
	public Menu() {
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected

		// First initialize language values
		searchSavedMenu = new MenuBar(true);
		run = new MenuItem(Util.menuHTML("img/icon/actions/run.gif", Main.i18n("search.saved.run")), true, runSearch);
		run.addStyleName("okm-MenuItem");
		searchSavedMenu.addItem(run);
		delete = new MenuItem(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("search.saved.delete")), true, deleteSearch);
		delete.addStyleName("okm-MenuItem");
		searchSavedMenu.addItem(delete);
		share = new MenuItem(Util.menuHTML("img/icon/actions/share_query.gif", GeneralComunicator.i18nExtension("messaging.share.query")), true, shareSearch);
		share.addStyleName("okm-MenuItem");
		searchSavedMenu.addStyleName("okm-MenuBar");
		initWidget(searchSavedMenu);
	}

	// Command menu to save file
	Command runSearch = new Command() {
		public void execute() {
			Main.get().mainPanel.search.historySearch.searchSaved.getSearch();
			hide();
		}
	};

	// Command menu to go directory file
	Command deleteSearch = new Command() {
		public void execute() {
			Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_SAVED_SEARCH);
			Main.get().confirmPopup.show();
			hide();
		}
	};

	// Command menu to go directory file
	Command shareSearch = new Command() {
		public void execute() {
			if (shareOption) {
				MessagingToolBarBox.get().executeProposeQuery(MessagingToolBarBox.PROPOSED_QUERY_SAVE_SEARCH);
				hide();
			}
		}
	};

	/**
	 *  Refresh language values
	 */
	public void langRefresh() {
		run.setHTML(Util.menuHTML("img/icon/actions/run.gif", Main.i18n("search.saved.run")));
		delete.setHTML(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("search.saved.delete")));
		share.setHTML(Util.menuHTML("img/icon/actions/share_query.gif", GeneralComunicator.i18nExtension("messaging.share.query")));
	}

	/**
	 * Hide popup menu
	 */
	public void hide() {
		Main.get().mainPanel.search.historySearch.searchSaved.menuPopup.hide();
	}

	/**
	 * show share search
	 */
	public void showShareSearch() {
		searchSavedMenu.addItem(share);
	}

	/**
	 * evaluateMenuOptions
	 */
	public void evaluateMenuOptions() {
		GWTQueryParams search = Main.get().mainPanel.search.historySearch.searchSaved.getSavedSearch();
		if (search != null && !search.isShared()) {
			shareOption = true;
			enable(share);
		} else {
			shareOption = false;
			disable(share);
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
}