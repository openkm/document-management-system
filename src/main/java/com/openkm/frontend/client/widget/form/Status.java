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

package com.openkm.frontend.client.widget.form;

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
	private Widget widget;
	private boolean flag_getDatabaseRecords = false;

	/**
	 * The status
	 */
	public Status(Widget widget) {
		super(false, true);
		this.widget = widget;
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
	 * Refreshing the panel
	 */
	public void refresh() {
		if (flag_getDatabaseRecords) {
			int left = (widget.getAbsoluteLeft() + (widget.getOffsetWidth() - 200) / 2);
			int top = (widget.getAbsoluteTop() + (widget.getOffsetHeight() - 40) / 2);

			if (left > 0 && top > 0) {
				setPopupPosition(left, top);
				super.show();
			}
		} else {
			super.hide();
		}
	}

	/**
	 * Sets the get database records flag
	 */
	public void setGetDatabaseRecords() {
		msg.setHTML(Main.i18n("group.properties.status.get.database.records"));
		flag_getDatabaseRecords = true;
		refresh();
	}

	/**
	 * Unset the get database records flag
	 */
	public void unsetGetDatabaseRecords() {
		flag_getDatabaseRecords = false;
		refresh();
	}
}