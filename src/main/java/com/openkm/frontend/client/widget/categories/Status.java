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

package com.openkm.frontend.client.widget.categories;

import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
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
	private boolean flag_getChilds = false;
	private boolean flag_root = false;

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
	 * Refreshing satus
	 */
	public void refresh() {
		if (flag_getChilds || flag_root) {
			int left = ((Main.get().mainPanel.desktop.navigator.categoriesTree.categoriesSelectPopup.getOffsetWidth() - 125) / 2) +
					Main.get().mainPanel.desktop.navigator.categoriesTree.categoriesSelectPopup.getAbsoluteLeft();
			int top = ((Main.get().mainPanel.desktop.navigator.categoriesTree.categoriesSelectPopup.getOffsetHeight() - 40) / 2) +
					Main.get().mainPanel.desktop.navigator.categoriesTree.categoriesSelectPopup.getAbsoluteTop();
			setPopupPosition(left, top);
			Main.get().mainPanel.desktop.navigator.categoriesTree.categoriesSelectPopup.scrollDirectoryPanel.addStyleName("okm-PanelRefreshing");
			super.show();
		} else {
			super.hide();
			Main.get().mainPanel.desktop.navigator.categoriesTree.categoriesSelectPopup.scrollDirectoryPanel.removeStyleName("okm-PanelRefreshing");
		}
	}

	/**
	 * Set childs flag
	 */
	public void setFlagChilds() {
		msg.setHTML(Main.i18n("tree.status.refresh.folder"));
		flag_getChilds = true;
		refresh();
	}

	/**
	 * Unset childs flag
	 */
	public void unsetFlagChilds() {
		flag_getChilds = false;
		refresh();
	}

	/**
	 * Set remove root flag
	 */
	public void setFlagRoot() {
		msg.setHTML(Main.i18n("tree.status.refresh.get.root"));
		flag_root = true;
		refresh();
	}

	/**
	 * Unset remove root flag
	 */
	public void unsetFlagRoot() {
		flag_root = false;
		refresh();
	}
}