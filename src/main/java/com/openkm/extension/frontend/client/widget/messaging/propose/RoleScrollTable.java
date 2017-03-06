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

package com.openkm.extension.frontend.client.widget.messaging.propose;

import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollPolicy;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollTableImages;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

import java.util.ArrayList;
import java.util.List;

/**
 * RoleScrollTable
 *
 * @author jllort
 *
 */
public class RoleScrollTable extends Composite {

	public static final int NUMBER_OF_COLUMNS = 1;

	private ScrollTable table;
	private FixedWidthFlexTable headerTable;
	private FixedWidthGrid dataTable;
	private boolean isRolesToNofity = false;

	/**
	 * RoleScrollTable
	 *
	 * @param isAssigned
	 */
	public RoleScrollTable(boolean isRolesToNofity) {
		this.isRolesToNofity = isRolesToNofity;

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
		table.setCellPadding(0);
		table.setSize("175px", "140px");

		// Level 1 headers
		if (isRolesToNofity) {
			headerTable.setHTML(0, 0, GeneralComunicator.i18nExtension("messaging.label.roles.to.notify"));
		} else {
			headerTable.setHTML(0, 0, GeneralComunicator.i18nExtension("messaging.label.roles"));
		}

		table.setColumnWidth(0, 167);

		// Table data
		dataTable.setSelectionPolicy(SelectionGrid.SelectionPolicy.ONE_ROW);

		table.setScrollPolicy(ScrollPolicy.BOTH);

		initWidget(table);
	}

	/**
	 * Adds new roleName name row
	 *
	 * @param roleName The user name value
	 */
	public void addRow(String roleName) {
		int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);
		dataTable.setHTML(rows, 0, roleName);
	}

	/**
	 * Gets the role
	 *
	 * @return The role
	 */
	public String getRole() {
		String role = null;

		if (!dataTable.getSelectedRows().isEmpty()) {
			int selectedRow = ((Integer) dataTable.getSelectedRows().iterator().next()).intValue();
			if (dataTable.isRowSelected(selectedRow)) {
				role = dataTable.getHTML(((Integer) dataTable.getSelectedRows().iterator().next()).intValue(), 0);
			}
		}

		return role;
	}

	/**
	 * Selects the las row
	 */
	public void selectLastRow() {
		if (dataTable.getRowCount() > 0) {
			dataTable.selectRow(dataTable.getRowCount() - 1, true);
		}
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
	 * Gets the users string to notify
	 *
	 * @return The users string
	 */
	public String getRolesToNotify() {
		String roles = "";

		if (dataTable.getRowCount() > 0) {
			for (int i = 0; i < dataTable.getRowCount(); i++) {
				roles += dataTable.getText(i, 0) + ",";
			}
		}

		// Removes last ',' character
		if (roles.length() > 0) {
			roles = roles.substring(0, roles.length() - 1);
		}

		return roles;
	}

	/**
	 * Gets the users list to notify
	 *
	 * @return The users list
	 */
	public List<String> getRolesToNotifyList() {
		List<String> rolesList = new ArrayList<String>();

		if (dataTable.getRowCount() > 0) {
			for (int i = 0; i < dataTable.getRowCount(); i++) {
				rolesList.add(dataTable.getText(i, 0));
			}
		}

		return rolesList;
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
		getDataTable().resize(0, NUMBER_OF_COLUMNS);
	}

	/**
	 * Lang refresh
	 */
	public void langRefresh() {
		if (isRolesToNofity) {
			headerTable.setHTML(0, 0, GeneralComunicator.i18nExtension("messaging.label.roles.to.notify"));
		} else {
			headerTable.setHTML(0, 0, GeneralComunicator.i18nExtension("messaging.label.roles"));
		}
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