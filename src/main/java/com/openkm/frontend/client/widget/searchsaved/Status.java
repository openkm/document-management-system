/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.frontend.client.widget.searchsaved;

import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.panel.ExtendedDockPanel;
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

	private boolean flag_getSearchs = false;
	private boolean flag_saveSearch = false;
	private boolean flag_deleteSearch = false;

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
		if (flag_getSearchs || flag_saveSearch || flag_deleteSearch) {
			int left = ((Main.get().mainPanel.search.getLeft() - 200) / 2) + ExtendedDockPanel.VERTICAL_BORDER_PANEL_WIDTH;
			int top = ((Main.get().mainPanel.search.getHeight() - 40) / 2) + TopPanel.PANEL_HEIGHT;
			setPopupPosition(left, top);
			Main.get().mainPanel.search.historySearch.scrollSearchSavedPanel.addStyleName("okm-PanelRefreshing");
			show();
		} else {
			hide();
			Main.get().mainPanel.search.historySearch.scrollSearchSavedPanel.removeStyleName("okm-PanelRefreshing");
		}
	}

	/**
	 * Sets get searchs flag
	 */
	public void setFlag_getSearchs() {
		msg.setHTML(Main.i18n("search.saved.status.getsearchs"));
		flag_getSearchs = true;
		refresh();
	}

	/**
	 * Unset get searchs flag
	 */
	public void unsetFlag_getSearchs() {
		flag_getSearchs = false;
		refresh();
	}

	/**
	 * Sets save search flag
	 */
	public void setFlag_saveSearch() {
		msg.setHTML(Main.i18n("search.saved.status.savesearch"));
		flag_saveSearch = true;
		refresh();
	}

	/**
	 * Unset save search flag
	 */
	public void unsetFlag_saveSearch() {
		flag_saveSearch = false;
		refresh();
	}

	/**
	 * Sets delete search flag
	 */
	public void setFlag_deleteSearch() {
		msg.setHTML(Main.i18n("search.saved.status.deletesearch"));
		flag_deleteSearch = true;
		refresh();
	}

	/**
	 * Unset delete search flag
	 */
	public void unsetFlag_deleteSearch() {
		flag_deleteSearch = false;
		refresh();
	}
}