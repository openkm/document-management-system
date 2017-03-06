/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2011  Paco Avila & Josep Llort
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

package com.openkm.extension.frontend.client.widget.stapling;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.widget.tabdocument.TabDocumentExtension;

/**
 * TabDocumentStapling
 *
 * @author jllort
 *
 */
public class TabDocumentStapling extends TabDocumentExtension {
	private ScrollPanel scrollPanel;
	private VerticalPanel vPanel;
	private String title = "";
	private FlexTable table;

	public TabDocumentStapling() {
		title = GeneralComunicator.i18nExtension("stapling.tab.document.title");

		vPanel = new VerticalPanel();
		scrollPanel = new ScrollPanel(vPanel);
		table = new FlexTable();
		vPanel.add(table);


		initWidget(scrollPanel);
	}

	@Override
	public String getTabText() {
		return title;
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		title = GeneralComunicator.i18nExtension("stapling.tab.document.title");
	}

	/**
	 * getTable
	 *
	 * @return
	 */
	public FlexTable getTable() {
		return table;
	}
}