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

package com.openkm.extension.frontend.client.widget.base;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * ColoredFlextTable
 *
 * @author jllort
 */
public class ColoredFlexTable extends FlexTable {
	public static final String ROW_STYLE_NAME = "gwt-FlexTable-row";

	/**
	 * ColoredFlexTable
	 */
	public ColoredFlexTable() {
		setStyleName("gwt-FlexTable");
		sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);
	}

	/**
	 * setStyleRow
	 */
	public void setStyleRow(int row, boolean selected) {
		// Ensures that header is never changed
		if (row >= 0 && row <= (getRowCount() - 1)) {
			if (selected) {
				getRowFormatter().addStyleName(row, "okm-FileBrowser-SelectedRow");
			} else {
				getRowFormatter().removeStyleName(row, "okm-FileBrowser-SelectedRow");
			}
		}
	}

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		Element td = getEventTargetCell(event);
		if (td == null)
			return;
		Element tr = DOM.getParent(td);

		switch (DOM.eventGetType(event)) {
			case Event.ONMOUSEOVER:
				tr.addClassName(ROW_STYLE_NAME + "-mouseover");
				break;

			case Event.ONMOUSEOUT: {
				tr.removeClassName(ROW_STYLE_NAME + "-mouseover");
				break;
			}
		}
	}
}