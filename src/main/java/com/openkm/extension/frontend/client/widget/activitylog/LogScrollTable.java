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

package com.openkm.extension.frontend.client.widget.activitylog;

import com.google.gwt.gen2.table.client.AbstractScrollTable.ResizePolicy;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollPolicy;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollTableImages;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Image;
import com.openkm.frontend.client.bean.extension.GWTActivity;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * LogScrollTable
 *
 * @author jllort
 *
 */
public class LogScrollTable extends Composite {
	private ScrollTable table;
	private FixedWidthFlexTable headerTable;
	private FixedWidthGrid dataTable;

	/**
	 * UserScrollTable
	 */
	public LogScrollTable() {

		ScrollTableImages scrollTableImages = new ScrollTableImages() {
			public AbstractImagePrototype scrollTableAscending() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/sort_asc.gif");
					}

					public Image createImage() {
						return new Image("img/sort_asc.gif");
					}

					public String getHTML() {
						return "<img border=\"0\" src=\"img/sort_asc.gif\"/>";
					}
				};
			}

			public AbstractImagePrototype scrollTableDescending() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/sort_desc.gif");
					}

					public Image createImage() {
						return new Image("img/sort_desc.gif");
					}

					public String getHTML() {
						return "<img border=\"0\" src=\"img/sort_desc.gif\"/>";
					}
				};
			}

			public AbstractImagePrototype scrollTableFillWidth() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/fill_width.gif");
					}

					public Image createImage() {
						return new Image("img/fill_width.gif");
					}

					public String getHTML() {
						return "<img border=\"0\" src=\"img/fill_width.gif\"/>";
					}
				};
			}
		};

		headerTable = new FixedWidthFlexTable();
		dataTable = new FixedWidthGrid();

		table = new ScrollTable(dataTable, headerTable, scrollTableImages);
		table.setCellSpacing(0);
		table.setCellPadding(2);
		table.setPreferredColumnWidth(0, 220);
		table.setPreferredColumnWidth(1, 120);
		table.setPreferredColumnWidth(2, 120);
		table.setPreferredColumnWidth(3, 380);

		// Level 1 headers
		table.setSize("50px", "50px");
		headerTable.setHTML(0, 0, "<b>" + GeneralComunicator.i18nExtension("activitylog.action") + "</b>");
		headerTable.setHTML(0, 1, "<b>" + GeneralComunicator.i18nExtension("activitylog.user") + "</b>");
		headerTable.setHTML(0, 2, "<b>" + GeneralComunicator.i18nExtension("activitylog.date") + "</b>");
		headerTable.setHTML(0, 3, "<b>" + GeneralComunicator.i18nExtension("activitylog.params") + "</b>");

		// Table data
		dataTable.setSelectionPolicy(SelectionGrid.SelectionPolicy.ONE_ROW);
		table.setResizePolicy(ResizePolicy.UNCONSTRAINED);
		table.setScrollPolicy(ScrollPolicy.BOTH);

		initWidget(table);
	}

	/**
	 * Lang refresh
	 */
	public void langRefresh() {
		headerTable.setHTML(0, 0, "<b>" + GeneralComunicator.i18nExtension("activitylog.action") + "</b>");
		headerTable.setHTML(0, 1, "<b>" + GeneralComunicator.i18nExtension("activitylog.user") + "</b>");
		headerTable.setHTML(0, 2, "<b>" + GeneralComunicator.i18nExtension("activitylog.date") + "</b>");
		headerTable.setHTML(0, 3, "<b>" + GeneralComunicator.i18nExtension("activitylog.params") + "</b>");
	}

	/**
	 * Adds new user name row
	 *
	 * @param userName The user name value
	 */
	public void addRow(GWTActivity activity) {
		int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);
		dataTable.setHTML(rows, 0, activity.getAction());
		dataTable.setHTML(rows, 1, activity.getUser());
		DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18n("general.date.pattern"));
		dataTable.setHTML(rows, 2, dtf.format(activity.getDate()));
		dataTable.setHTML(rows, 3, activity.getParams());
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 1, HasAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 2, HasAlignment.ALIGN_CENTER);
	}

	/**
	 * Selects the last row
	 */
	public void selectLastRow() {
		if (dataTable.getRowCount() > 0) {
			dataTable.selectRow(dataTable.getRowCount() - 1, true);
		}
	}

	/**
	 * Removes all rows except the first
	 */
	public void removeAllRows() {
		// Purge all rows
		while (dataTable.getRowCount() > 0) {
			dataTable.removeRow(0);
		}
	}

	/**
	 * Reset table values
	 */
	public void reset() {
		removeAllRows();
	}

	/**
	 * Removes the selected row
	 */
	public void removeSelectedRow() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			int selectedRow = ((Integer) dataTable.getSelectedRows().iterator().next()).intValue();
			dataTable.removeRow(selectedRow);

			if (dataTable.getRowCount() > 0) {
				if (dataTable.getRowCount() > selectedRow) {
					dataTable.selectRow(selectedRow, true);
				} else {
					dataTable.selectRow(selectedRow - 1, true);
				}
			}
		}
	}

	/**
	 * fillWidth
	 */
	public void fillWidth() {
		table.fillWidth();
	}

	/**
	 * getDataTable
	 *
	 * @return FixedWidthGrid
	 */
	public FixedWidthGrid getDataTable() {
		return table.getDataTable();
	}
}