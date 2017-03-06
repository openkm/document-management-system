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

package com.openkm.extension.frontend.client.widget.wiki;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.openkm.frontend.client.bean.extension.GWTWikiPage;

/**
 * WikiPage
 *
 * @author jllort
 */
public class WikiPage extends Composite {
	private FlexTable table;

	/**
	 * WikiPage
	 */
	public WikiPage() {
		table = new FlexTable();
		table.setWidth("100%");

		initWidget(table);
	}

	/**
	 * setContent
	 *
	 * @param wikiPage
	 */
	public void setContent(GWTWikiPage wikiPage) {
		if (wikiPage != null) {
			// Build header for normal wiki pages
			String header = "";

			if (wikiPage.getNode() == null || wikiPage.getNode().equals("")) {
				header += "<h2>" + wikiPage.getTitle() + "</h2>";
				header += "<hr></br>";
			}

			table.setHTML(0, 0, WikiToolBarEditor.bbcode(header + wikiPage.getContent()));
		} else {
			table.setHTML(0, 0, "");
		}
	}
}
