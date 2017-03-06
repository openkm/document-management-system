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

package com.openkm.frontend.client.widget.properties.version;

import com.google.gwt.gen2.table.client.SortableGrid;
import com.google.gwt.gen2.table.client.SortableGrid.ColumnSorter;
import com.google.gwt.gen2.table.client.SortableGrid.ColumnSorterCallback;
import com.google.gwt.gen2.table.client.TableModelHelper.ColumnSortList;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTObjectToOrder;
import com.openkm.frontend.client.bean.GWTVersion;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.util.ColumnComparatorDate;
import com.openkm.frontend.client.util.ColumnComparatorDouble;
import com.openkm.frontend.client.util.ColumnComparatorText;

import java.util.*;

/**
 * ExtendedColumnSorter
 *
 * @author jllort
 */
public class ExtendedColumnSorter extends ColumnSorter {
	private int column = -1;
	boolean ascending = false;

	@Override
	public void onSortColumn(SortableGrid grid, ColumnSortList sortList, ColumnSorterCallback callback) {
		// Get the primary column, sort order, number of rows, number of columns
		column = sortList.getPrimaryColumn();
		ascending = sortList.isPrimaryAscending();
		sort(column, ascending);
		callback.onSortingComplete();
	}

	/**
	 * sort
	 */
	public void sort(int column, boolean ascending) {
		int rows = Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.version.getDataTable().getRowCount();
		int columns = Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.version.getDataTable().getColumnCount();
		Map<Integer, GWTVersion> data = new HashMap<Integer, GWTVersion>(Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.version.data);

		List<GWTObjectToOrder> elementToOrder = new ArrayList<GWTObjectToOrder>(); // List with column data,  and actual position

		if (column >= 0 && column <= 6
				&& column != UIDesktopConstants.DOCUMENT_HISTORY_COLUMN_BUTTON_SHOW
				&& column != UIDesktopConstants.DOCUMENT_HISTORY_COLUMN_BUTTON_RESTORE) {
			for (int i = 0; i < rows; i++) {
				String[] rowI = new String[columns];
				GWTObjectToOrder rowToOrder = new GWTObjectToOrder();
				for (int x = 0; x < columns; x++) {
					rowI[x] = Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.version.getDataTable().getHTML(i, x);
				}

				switch (column) {
					case UIDesktopConstants.DOCUMENT_HISTORY_COLUMN_VERSION:
						String version = data.get(i).getName();
						String numberParts[] = version.split("\\.");
						version = "";
						for (int x = 0; x < numberParts.length; x++) {
							switch (numberParts[x].length()) {
								case 1:
									version = version + "00" + numberParts[x];
									break;
								case 2:
									version = version + "0" + numberParts[x];
									break;
							}
						}
						if (numberParts.length == 2) {
							version = version + "000000";
						}
						if (numberParts.length == 3) {
							version = version + "000";
						}
						rowToOrder.setObject(new Double(version));
						rowToOrder.setDataId(String.valueOf(i));
						elementToOrder.add(rowToOrder);
						break;

					case UIDesktopConstants.DOCUMENT_HISTORY_COLUMN_AUTHOR:
					case UIDesktopConstants.DOCUMENT_HISTORY_COLUMN_COMMENT:
						// Text Lower case solves problem with sort ordering
						if (column == UIDesktopConstants.DOCUMENT_HISTORY_COLUMN_AUTHOR) {
							rowToOrder.setObject(data.get(i).getAuthor().toLowerCase());
						} else {
							if (data.get(i).getComment() != null) {
								rowToOrder.setObject(data.get(i).getComment().toLowerCase());
							} else {
								rowToOrder.setObject("");
							}
						}
						rowToOrder.setDataId(String.valueOf(i)); // Actual position value
						elementToOrder.add(rowToOrder);
						break;

					case UIDesktopConstants.DOCUMENT_HISTORY_COLUMN_DATE:
						rowToOrder.setObject(data.get(i).getCreated()); // Date  value
						rowToOrder.setDataId(String.valueOf(i)); // Actual position value
						elementToOrder.add(rowToOrder);
						break;

					case UIDesktopConstants.DOCUMENT_HISTORY_COLUMN_SIZE:
						rowToOrder.setObject(new Double((data.get(i)).getSize()));
						rowToOrder.setDataId(String.valueOf(i)); // Actual position value
						elementToOrder.add(rowToOrder);
						break;
				}
			}

			switch (column) {
				case UIDesktopConstants.DOCUMENT_HISTORY_COLUMN_VERSION:
				case UIDesktopConstants.DOCUMENT_HISTORY_COLUMN_SIZE:
					// Bytes
					Collections.sort(elementToOrder, ColumnComparatorDouble.getInstance());
					break;

				case UIDesktopConstants.DOCUMENT_HISTORY_COLUMN_AUTHOR:
				case UIDesktopConstants.DOCUMENT_HISTORY_COLUMN_COMMENT:
					// Text
					Collections.sort(elementToOrder, ColumnComparatorText.getInstance());
					break;

				case UIDesktopConstants.DOCUMENT_HISTORY_COLUMN_DATE:
					// Date
					Collections.sort(elementToOrder, ColumnComparatorDate.getInstance());
					break;
			}

			// Reversing if needed
			if (!ascending) {
				Collections.reverse(elementToOrder);
			}
			applySort(elementToOrder);
		}
	}

	/**
	 * @param elementList
	 * @param elementToOrder
	 */
	private void applySort(List<GWTObjectToOrder> elementToOrder) {
		// Data map
		Map<Integer, GWTVersion> data = new HashMap<Integer, GWTVersion>(Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.version.data);
		Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.version.reset();

		for (GWTObjectToOrder orderedColumn : elementToOrder) {
			Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.version.addRow(data.get(Integer.parseInt(orderedColumn.getDataId())));
		}
	}
}