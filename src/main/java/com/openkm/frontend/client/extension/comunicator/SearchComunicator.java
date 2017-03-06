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

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTQueryParams;

/**
 * SearchComunicator
 *
 * @author jllort
 *
 */
public class SearchComunicator {

	/**
	 * setSavedSearch
	 */
	public static void setSavedSearch(GWTQueryParams params) {
		Main.get().mainPanel.search.searchBrowser.searchIn.setSavedSearch(params);
	}

	/**
	 * getAllSearchs
	 */
	public static void getAllSearchs() {
		Main.get().mainPanel.search.historySearch.searchSaved.getAllSearchs();
	}

	/**
	 * getUserSearchs
	 */
	public static void getUserSearchs() {
		Main.get().mainPanel.search.historySearch.userNews.getUserSearchs();
	}

	/**
	 * getSelectedRowSearchSaved
	 *
	 * @return
	 */
	public static int getSelectedRowSearchSaved() {
		return Main.get().mainPanel.search.historySearch.searchSaved.getSelectedRow();
	}

	/**
	 * getSelectedRowUserNews
	 *
	 * @return
	 */
	public static int getSelectedRowUserNews() {
		return Main.get().mainPanel.search.historySearch.userNews.getSelectedRow();
	}

	/**
	 * getSavedSearch
	 *
	 * @return
	 */
	public static GWTQueryParams getSavedSearch() {
		return Main.get().mainPanel.search.historySearch.searchSaved.getSavedSearch();
	}

	/**
	 * getSavedUserNews
	 *
	 * @return
	 */
	public static GWTQueryParams getSavedUserNews() {
		return Main.get().mainPanel.search.historySearch.userNews.getSavedSearch();
	}
}