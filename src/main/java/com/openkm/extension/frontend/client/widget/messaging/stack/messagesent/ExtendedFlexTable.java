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

package com.openkm.extension.frontend.client.widget.messaging.stack.messagesent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;
import com.openkm.extension.frontend.client.widget.messaging.MessagingToolBarBox;

/**
 * Extends FlexTable functionalities 
 *
 * @author jllort
 *
 */
public class ExtendedFlexTable extends FlexTable {

	private int mouseX = 0;
	private int mouseY = 0;
	private boolean panelSelected = true; // Indicates if panel is selected
	private int selectedRow = -1;

	/**
	 * ExtendedFlexTable
	 */
	public ExtendedFlexTable() {
		super();

		// Adds double click event control to table ( on default only has CLICK )
		sinkEvents(Event.ONDBLCLICK | Event.MOUSEEVENTS);
		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Mark selected row or orders rows if header row (0) is clicked 
				// And row must be other than the selected one
				markSelectedRow(getCellForEvent(event).getRowIndex());
				MessagingToolBarBox.get().messageDashboard.messageStack.messageSent.refreshMessagesSent();
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.EventListener#onBrowserEvent(com.google.gwt.user.client.Event)
	 */
	public void onBrowserEvent(Event event) {
		int selectedRow = 0;

		if (DOM.eventGetType(event) == Event.ONDBLCLICK || DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
			Element td = getMouseEventTargetCell(event);
			if (td == null) return;
			Element tr = DOM.getParent(td);
			Element body = DOM.getParent(tr);
			selectedRow = DOM.getChildIndex(body, tr);
		}

		// Only if selectedRow >= 0, indicates a document row value and must apear menu or double click action
		if (selectedRow >= 0) {

			// When de button mouse is released
			mouseX = DOM.eventGetClientX(event);
			mouseY = DOM.eventGetClientY(event);

			// On double click not sends event to onCellClicked across super.onBrowserEvent();
			if (DOM.eventGetType(event) == Event.ONDBLCLICK) {
				// Disables the event propagation the sequence is:
				// Two time entry onCellClicked before entry on onBrowserEvent and disables the
				// Tree onCellClicked that produces inconsistence error refreshing
				DOM.eventCancelBubble(event, true);
				MessagingToolBarBox.get().messageDashboard.messageStack.messageSent.refreshMessagesSent();

			} else if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
				switch (DOM.eventGetButton(event)) {
					case Event.BUTTON_RIGHT:
						markSelectedRow(selectedRow);
						MessagingToolBarBox.get().messageDashboard.messageStack.messageSent.menuPopup.setPopupPosition(mouseX, mouseY);
						MessagingToolBarBox.get().messageDashboard.messageStack.messageSent.menuPopup.show();
						DOM.eventPreventDefault(event); // Prevent to fire event to browser
						break;
					default:
						break;
				}
			}
		}
		super.onBrowserEvent(event);
	}

	/**
	 * markSelectedRow
	 *
	 * @param row
	 */
	private void markSelectedRow(int row) {
		setSelectedPanel(true);
		if (row != selectedRow) {
			styleRow(selectedRow, false);
			styleRow(row, true);
			selectedRow = row;
		}
	}

	/**
	 * Adds a new row
	 *
	 * @param user
	 */
	public void addRow(String user) {
		int rows = getRowCount();

		setHTML(rows, 0, user);
		setHTML(rows, 1, "");

		// The hidden column extends table to 100% width
		CellFormatter cellFormatter = getCellFormatter();
		cellFormatter.setWidth(rows, 1, "100%");

		getRowFormatter().setStyleName(rows, "okm-SearchSaved");
		setRowWordWarp(rows, 2, false);
	}

	/**
	 * Set the WordWarp for all the row cells
	 *
	 * @param row The row cell
	 * @param columns Number of row columns
	 * @param warp
	 */
	private void setRowWordWarp(int row, int columns, boolean warp) {
		CellFormatter cellFormatter = getCellFormatter();
		for (int i = 0; i < columns; i++) {
			cellFormatter.setWordWrap(row, i, false);
		}
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
	 * Sets the selected panel value
	 *
	 * @param selected The selected panel value
	 */
	public void setSelectedPanel(boolean selected) {
		// Case panel is not still selected and must enable this and disable result search panel
		if (!isPanelSelected() && selected) {
			MessagingToolBarBox.get().messageDashboard.messageBrowser.messageDetail.addStyleName("okm-PanelSelected");
		} else {
			MessagingToolBarBox.get().messageDashboard.messageBrowser.messageDetail.removeStyleName("okm-PanelSelected");
		}

		if (selected) {
			MessagingToolBarBox.get().messageDashboard.messageStack.scrollMessageSentPanel.addStyleName("okm-PanelSelected");
		} else {
			MessagingToolBarBox.get().messageDashboard.messageStack.scrollMessageSentPanel.removeStyleName("okm-PanelSelected");
		}
		panelSelected = selected;
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
	 * Finds row by id 
	 *
	 * @param id The id
	 * @return The selected row
	 */
	public int findSelectedRowById(String id) {
		int selected = -1;
		int rowIndex = 0;
		boolean found = false;

		// Looking for id on directories
		while (!found && rowIndex < getRowCount()) {
			if (getHTML(rowIndex, 0).equals(id)) {
				selected = rowIndex;
				found = true;
			}
			rowIndex++;
		}
		return selected;
	}

	/**
	 * Gets the selected row value
	 *
	 * @return The selected row value
	 */
	public int getSelectedRow() {
		return selectedRow;
	}

	/**
	 * getSelectedId
	 *
	 * @return
	 */
	public String getSelectedId() {
		if (selectedRow >= 0 && getRowCount() > 0) {
			return getHTML(selectedRow, 0);
		} else {
			return "";
		}
	}

	/**
	 * After deletes rows selects a new row 
	 */
	public void selectPrevRow() {
		// After deletes document or folder selects a previos row if not 0 or the next if exists ( next row is actual after delete )
		// RowCount minor value is 1 for header titles
		if (getRowCount() > 1) {
			if (selectedRow > 0) {
				selectedRow--;
			}
			styleRow(selectedRow, true);

		} else {
			// Case deletes all table rows
			selectedRow = -1;
		}
	}

	/**
	 * setSelectedRow
	 *
	 * @param row
	 */
	public void setSelectedRow(int row) {
		if (row >= 0 && row < getRowCount()) {
			markSelectedRow(row);
		}
	}

	/**
	 * Indicates if panel is selected
	 *
	 * @return The value of panel ( selected )
	 */
	public boolean isPanelSelected() {
		return panelSelected;
	}

	/**
	 * Gets the X position on mouse click
	 *
	 * @return The x position on mouse click
	 */
	public int getMouseX() {
		return mouseX;
	}

	/**
	 * Gets the Y position on mouse click
	 *
	 * @return The y position on mouse click
	 */
	public int getMouseY() {
		return mouseY;
	}
}