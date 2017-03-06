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

import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.panel.ExtendedDockPanel;
import com.openkm.frontend.client.panel.center.Search;
import com.openkm.frontend.client.panel.center.SearchBrowser;
import com.openkm.frontend.client.panel.top.TopPanel;
import com.openkm.frontend.client.util.OKMBundleResources;

/**
 * Status
 *
 * @author jllort
 *
 */
public class Status extends PopupPanel {

	private HorizontalPanel hPanel;
	private HTML msg;
	private HTML space;
	private Image image;

	private boolean flag_findPaginated = false;
	private boolean flag_runSearch = false;
	private boolean flag_refreshResults = false;
	private boolean flag_refreshPropertyGroups = false;

	/**
	 * Status
	 */
	public Status() {
		super(false, true);
		hPanel = new HorizontalPanel();
		image = new Image(OKMBundleResources.INSTANCE.indicator());
		msg = new HTML("");
		space = new HTML("");

		hPanel.add(image);
		hPanel.add(msg);
		hPanel.add(space);

		hPanel.setCellVerticalAlignment(image, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellVerticalAlignment(msg, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellHorizontalAlignment(image, HasAlignment.ALIGN_CENTER);
		hPanel.setCellWidth(image, "30px");
		hPanel.setCellWidth(space, "7px");

		hPanel.setHeight("25px");

		msg.setStyleName("okm-NoWrap");

		super.hide();
		setWidget(hPanel);
	}

	/**
	 * Refresh
	 */
	public void refresh() {
		if (flag_findPaginated || flag_runSearch || flag_refreshResults || flag_refreshPropertyGroups) {
			int left = ((Main.get().mainPanel.search.getRight() - 200) / 2) + Main.get().mainPanel.search.getLeft() + Search.SPLITTER_WIDTH +
					ExtendedDockPanel.VERTICAL_BORDER_PANEL_WIDTH;
			int top = ((Main.get().mainPanel.search.searchBrowser.bottomHeight - 40) / 2) + TopPanel.PANEL_HEIGHT +
					Main.get().mainPanel.search.searchBrowser.topHeight + SearchBrowser.SPLITTER_HEIGHT;
			setPopupPosition(left, top);
			Main.get().mainPanel.search.searchBrowser.searchResult.addStyleName("okm-PanelRefreshing");
			super.show();
		} else {
			super.hide();
			Main.get().mainPanel.search.searchBrowser.searchResult.removeStyleName("okm-PanelRefreshing");
		}
	}

	/**
	 * Sets find by keywords flag
	 */
	public void setFlag_findPaginated() {
		msg.setHTML(Main.i18n("search.result.status.findPaginated"));
		flag_findPaginated = true;
		refresh();
	}

	/**
	 * Unset find by keywords flag
	 */
	public void unsetFlag_findPaginated() {
		flag_findPaginated = false;
		refresh();
	}

	/**
	 * Sets run search flag
	 */
	public void setFlag_runSearch() {
		msg.setHTML(Main.i18n("search.result.status.runsearch"));
		flag_runSearch = true;
		refresh();
	}

	/**
	 * Unset run search flag
	 */
	public void unsetFlag_runSearch() {
		flag_runSearch = false;
		refresh();
	}

	/**
	 * Sets resfresh results flag
	 */
	public void setFlag_refreshResults() {
		msg.setHTML(Main.i18n("search.result.status.refresh.results"));
		flag_refreshResults = true;
		refresh();
	}

	/**
	 * Unset refresh results flag
	 */
	public void unsetFlag_refreshResults() {
		flag_refreshResults = false;
		refresh();
	}

	/**
	 * Sets resfresh property groups flag
	 */
	public void setFlag_refreshPropertyGroups() {
		msg.setHTML(Main.i18n("search.result.status.refresh.property.groups"));
		flag_refreshPropertyGroups = true;
		refresh();
	}

	/**
	 * Unset refresh property groups flag
	 */
	public void unsetFlag_refreshPropertyGroups() {
		flag_refreshPropertyGroups = false;
		refresh();
	}
}