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

package com.openkm.extension.frontend.client.widget.messaging.list;

import com.google.gwt.gen2.table.client.SortableGrid;
import com.google.gwt.gen2.table.client.SortableGrid.ColumnSorter;
import com.google.gwt.gen2.table.client.SortableGrid.ColumnSorterCallback;
import com.google.gwt.gen2.table.client.TableModelHelper.ColumnSortList;
import com.openkm.extension.frontend.client.widget.messaging.MessagingToolBarBox;
import com.openkm.frontend.client.bean.GWTObjectToOrder;
import com.openkm.frontend.client.bean.extension.*;
import com.openkm.frontend.client.util.ColumnComparatorDate;
import com.openkm.frontend.client.util.ColumnComparatorText;

import java.util.*;

/**
 * ExtendedColumnSorter
 *
 * @author jllort
 *
 */
public class ExtendedColumnSorter extends ColumnSorter {

	private String selectedRowDataID = "";

	/* (non-Javadoc)
	 * @see com.google.gwt.widgetideas.table.client.SortableGrid$ColumnSorter#onSortColumn(com.google.gwt.widgetideas.table.client.SortableGrid, com.google.gwt.widgetideas.table.client.TableModel.ColumnSortList, com.google.gwt.widgetideas.table.client.SortableGrid.ColumnSorterCallback)
	 */
	public void onSortColumn(SortableGrid grid, ColumnSortList sortList, ColumnSorterCallback callback) {
		// Get the primary column, sort order, number of rows, number of columns
		int column = sortList.getPrimaryColumn();
		boolean ascending = sortList.isPrimaryAscending();
		int rows = MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.getDataTable().getRowCount();
		int columns = MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.getDataTable().getColumnCount();
		int selectedRow = MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.getSelectedRow();
		Map<Integer, Object> data = new HashMap<Integer, Object>(MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.data);

		List<String[]> elementList = new ArrayList<String[]>();                    // List with all data
		List<GWTObjectToOrder> elementToOrder = new ArrayList<GWTObjectToOrder>();    // List with column data, and actual position

		// Gets the data values and set on a list of String arrays ( element by column )
		for (int i = 0; i < rows; i++) {
			String[] rowI = new String[columns];
			GWTObjectToOrder rowToOrder = new GWTObjectToOrder();

			for (int x = 0; x < columns; x++) {
				rowI[x] = MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.getDataTable().getHTML(i, x);
			}

			elementList.add(i, rowI);

			switch (column) {
				case 0:
				case 1:
				case 2:
				case 4:
					// Text
					rowToOrder.setObject(rowI[column].toLowerCase());        // Lower case solves problem with sort ordering
					rowToOrder.setDataId("" + i);                            // Actual position value
					elementToOrder.add(rowToOrder);
					break;

				case 3:
					// Date
					if (data.get(Integer.parseInt(rowI[5])) instanceof GWTProposedSubscriptionReceived) {
						rowToOrder.setObject(((GWTProposedSubscriptionReceived) data.get(Integer.parseInt(rowI[5]))).getSentDate()); // Date value
					} else if (data.get(Integer.parseInt(rowI[5])) instanceof GWTProposedSubscriptionSent) {
						rowToOrder.setObject(((GWTProposedSubscriptionSent) data.get(Integer.parseInt(rowI[5]))).getSentDate()); // Date value
					} else if (data.get(Integer.parseInt(rowI[5])) instanceof GWTProposedQueryReceived) {
						rowToOrder.setObject(((GWTProposedQueryReceived) data.get(Integer.parseInt(rowI[5]))).getSentDate()); // Date value
					} else if (data.get(Integer.parseInt(rowI[5])) instanceof GWTProposedQuerySent) {
						rowToOrder.setObject(((GWTProposedQuerySent) data.get(Integer.parseInt(rowI[5]))).getSentDate()); // Date value
					} else if (data.get(Integer.parseInt(rowI[5])) instanceof GWTMessageReceived) {
						rowToOrder.setObject(((GWTMessageReceived) data.get(Integer.parseInt(rowI[5]))).getSentDate()); // Date value
					} else if (data.get(Integer.parseInt(rowI[5])) instanceof GWTTextMessageSent) {
						rowToOrder.setObject(((GWTTextMessageSent) data.get(Integer.parseInt(rowI[5]))).getSentDate()); // Date value
					}
					rowToOrder.setDataId("" + i);                                                 // Actual position value
					elementToOrder.add(rowToOrder);
					break;
			}

			// Saves the selected row
			if (selectedRow == i) {
				selectedRowDataID = rowToOrder.getDataId();
			}
		}

		switch (column) {
			case 0:
			case 1:
			case 2:
			case 4:
				// Text
				Collections.sort(elementToOrder, ColumnComparatorText.getInstance());
				break;

			case 3:
				// Date
				Collections.sort(elementToOrder, ColumnComparatorDate.getInstance());
				break;
		}

		// Reversing if needed
		if (!ascending) {
			Collections.reverse(elementToOrder);
		}

		applySort(elementList, elementToOrder);
		callback.onSortingComplete();
	}

	/**
	 * @param elementList
	 * @param elementToOrder
	 */
	private void applySort(List<String[]> elementList, List<GWTObjectToOrder> elementToOrder) {
		// Removing all values
		while (MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.getDataTable().getRowCount() > 0) {
			MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.getDataTable().removeRow(0);
		}

		// Data map
		Map<Integer, Object> data = new HashMap<Integer, Object>(MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.data);
		MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.reset();

		int column = 0;
		for (Iterator<GWTObjectToOrder> it = elementToOrder.iterator(); it.hasNext(); ) {
			GWTObjectToOrder orderedColumn = it.next();
			String[] row = elementList.get(Integer.parseInt(orderedColumn.getDataId()));

			MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.addRow((Object) data.get(Integer.parseInt(row[5])));

			// Sets selectedRow
			if (!selectedRowDataID.equals("") && selectedRowDataID.equals(row[5])) {
				MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.setSelectedRow(column);
				selectedRowDataID = "";
			}

			column++;
		}
	}
}
