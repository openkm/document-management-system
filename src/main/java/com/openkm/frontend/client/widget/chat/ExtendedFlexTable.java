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

package com.openkm.frontend.client.widget.chat;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;
import com.openkm.frontend.client.Main;

/**
 * Extends FlexTable functionalities 
 *
 * @author jllort
 *
 */
public class ExtendedFlexTable extends FlexTable {

	private int selectedRow = -1;

	/**
	 * ExtendedFlexTable
	 */
	public ExtendedFlexTable() {
		super();

		// Adds double click event control to table ( on default only has CLICK )
		sinkEvents(Event.ONDBLCLICK);
		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Mark selected row or orders rows if header row (0) is clicked 
				// And row must be other than the selected one
				markSelectedRow(getCellForEvent(event).getRowIndex());
				Main.get().onlineUsersPopup.enableAcceptButton();
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.EventListener#onBrowserEvent(com.google.gwt.user.client.Event)
	 */
	public void onBrowserEvent(Event event) {
		int selectedRow = 0;

		if (DOM.eventGetType(event) == Event.ONDBLCLICK) {
			Element td = getMouseEventTargetCell(event);
			if (td == null) return;
			Element tr = DOM.getParent(td);
			Element body = DOM.getParent(tr);
			selectedRow = DOM.getChildIndex(body, tr);
			if (selectedRow >= 0) {
				markSelectedRow(selectedRow);
				Main.get().onlineUsersPopup.enableAcceptButton();
				DOM.eventCancelBubble(event, true);
				Main.get().onlineUsersPopup.executeAction();
			}
		}

		super.onBrowserEvent(event);
	}

	/**
	 * Method originally copied from HTMLTable superclass where it was defined private
	 * Now implemented differently to only return target cell if it'spart of 'this' table
	 */
	private Element getMouseEventTargetCell(Event event) {
		Element td = DOM.eventGetTarget(event);
		//locate enclosing td element
		while (!DOM.getElementProperty(td, "tagName").equalsIgnoreCase("td")) {
			// If we run out of elements, or run into the table itself, then give up.
			if ((td == null) || td == getElement())
				return null;
			td = DOM.getParent(td);
		}
		//test if the td is actually from this table
		Element tr = DOM.getParent(td);
		Element body = DOM.getParent(tr);
		if (body == this.getBodyElement()) {
			return td;
		}
		//Didn't find appropriate cell
		return null;
	}

	/**
	 * markSelectedRow
	 *
	 * @param row
	 */
	private void markSelectedRow(int row) {
		if (row != selectedRow) {
			styleRow(selectedRow, false);
			styleRow(row, true);
			selectedRow = row;
		}
	}

	/**
	 * Change the style row selected or unselected
	 *
	 * @param row The row afected
	 * @param selected Indicates selected unselected row
	 */
	public void styleRow(int row, boolean selected) {
		// Ensures that header is never changed
		if (row >= 0) {
			if (selected) {
				getRowFormatter().addStyleName(row, "okm-Table-SelectedRow");
			} else {
				getRowFormatter().removeStyleName(row, "okm-Table-SelectedRow");
			}
		}
	}

	/**
	 * Removes all rows except the first
	 */
	public void removeAllRows() {
		// Resets selected Rows and Col values
		selectedRow = -1;
		super.removeAllRows();
	}

	/**
	 * Gets the selected row value
	 *
	 * @return The selected row value
	 */
	public int getSelectedRow() {
		return selectedRow;
	}
}