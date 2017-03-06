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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.extension.GWTWikiPage;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

import java.util.List;

/**
 * WikiHistory
 *
 * @author jllort
 *
 */
public class WikiHistory extends Composite {
	private FlexTable table;
	private WikiController controller;
	private List<GWTWikiPage> wikiPages;

	/**
	 * WikiHistory
	 */
	public WikiHistory(final WikiController controller) {
		this.controller = controller;
		SimplePanel sp = new SimplePanel();

		table = new FlexTable();
		table.setCellPadding(5);
		table.setCellSpacing(0);
		sp.add(table);

		sp.setWidth("100%");

		initWidget(sp);
	}

	/**
	 * showHistory
	 *
	 * @param wikiPages
	 */
	public void showHistory(final List<GWTWikiPage> wikiPages, boolean locked) {
		this.wikiPages = wikiPages;
		for (GWTWikiPage wikiPage : wikiPages) {
			final int row = table.getRowCount();
			DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
			table.setHTML(row, 0, dtf.format(wikiPage.getDate()));
			table.setHTML(row, 1, wikiPage.getUser());
			// Restore button
			if (!locked && row > 0) {
				Button restoreButton = new Button(GeneralComunicator.i18n("button.restore"));
				restoreButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						controller.restoreWikiPage(wikiPages.get(row));
					}
				});
				restoreButton.setStyleName("okm-YesButton");
				table.setWidget(row, 2, restoreButton);
			} else {
				table.setHTML(row, 2, "");
			}
			// Show older version button
			if (row > 0) {
				Button showButton = new Button(GeneralComunicator.i18n("button.view"));
				showButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						controller.showHistoryWikiPageVersion(wikiPages.get(row));
					}
				});
				showButton.setStyleName("okm-ViewButton");
				table.setWidget(row, 3, showButton);
			} else {
				table.setHTML(row, 3, "");
			}

			// Show deleted
			if (wikiPage.isDeleted()) {
				HTML deleted = new HTML(GeneralComunicator.i18nExtension("wiki.deleted"));
				deleted.setStyleName("okm-Input-Error");
				table.setWidget(row, 4, deleted);
			} else {
				table.setHTML(row, 4, "");
			}

			table.getFlexCellFormatter().setVerticalAlignment(row, 0, HasAlignment.ALIGN_MIDDLE);
			table.getFlexCellFormatter().setVerticalAlignment(row, 1, HasAlignment.ALIGN_MIDDLE);
		}
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		int row = 0;
		if (wikiPages != null) {
			for (GWTWikiPage wikiPage : wikiPages) {
				if (wikiPage.isDeleted()) {
					HTML deleted = new HTML(GeneralComunicator.i18nExtension("wiki.deleted"));
					deleted.setStyleName("okm-Input-Error");
					table.setWidget(row, 4, deleted);
					row++;
				}
			}
		}
	}

	/**
	 * reset
	 */
	public void reset() {
		removeAllRows();
	}

	/**
	 * removeAllRows
	 */
	private void removeAllRows() {
		while (table.getRowCount() > 0) {
			table.removeRow(0);
		}
	}
}