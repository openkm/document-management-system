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

package com.openkm.extension.frontend.client.widget.workflow;

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
import com.openkm.frontend.client.bean.GWTProcessInstanceLogEntry;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * WorkflowDetailTable
 *
 * @author jllort
 *
 */
public class WorkflowDetailTable extends Composite {
	public static final int NUMBER_OF_COLUMNS = 6;

	private ScrollTable table;
	private FixedWidthFlexTable headerTable;
	private FixedWidthGrid dataTable;

	/**
	 * WorkflowTable
	 *
	 * @param isAssigned
	 */
	public WorkflowDetailTable() {
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
		table.setColumnWidth(0, 50);
		table.setColumnWidth(3, 150);

		// Level 1 headers
		table.setSize("50px", "50px");
		headerTable.setHTML(0, 0, "<b>" + GeneralComunicator.i18nExtension("workflow.detail.id") + "</b>");
		headerTable.setHTML(0, 1, "<b>" + GeneralComunicator.i18nExtension("workflow.detail.name") + "</b>");
		headerTable.setHTML(0, 2, "<b>" + GeneralComunicator.i18nExtension("workflow.detail.token") + "</b>");
		headerTable.setHTML(0, 3, "<b>" + GeneralComunicator.i18nExtension("workflow.detail.date") + "</b>");
		headerTable.setHTML(0, 4, "<b>" + GeneralComunicator.i18nExtension("workflow.detail.type") + "</b>");
		headerTable.setHTML(0, 5, "<b>" + GeneralComunicator.i18nExtension("workflow.detail.info") + "</b>");

		// Table data
		dataTable.setSelectionPolicy(SelectionGrid.SelectionPolicy.ONE_ROW);
		table.setResizePolicy(ResizePolicy.UNCONSTRAINED);
		table.setScrollPolicy(ScrollPolicy.BOTH);

		initWidget(table);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		headerTable.setHTML(0, 0, "<b>" + GeneralComunicator.i18nExtension("workflow.detail.id") + "</b>");
		headerTable.setHTML(0, 1, "<b>" + GeneralComunicator.i18nExtension("workflow.detail.name") + "</b>");
		headerTable.setHTML(0, 2, "<b>" + GeneralComunicator.i18nExtension("workflow.detail.token") + "</b>");
		headerTable.setHTML(0, 3, "<b>" + GeneralComunicator.i18nExtension("workflow.detail.date") + "</b>");
		headerTable.setHTML(0, 4, "<b>" + GeneralComunicator.i18nExtension("workflow.detail.type") + "</b>");
		headerTable.setHTML(0, 5, "<b>" + GeneralComunicator.i18nExtension("workflow.detail.info") + "</b>");
	}

	/**
	 * addRow
	 *
	 * @param instanceLogEntry
	 */
	public void addRow(GWTProcessInstanceLogEntry instanceLogEntry) {
		int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);
		dataTable.setHTML(rows, 0, String.valueOf(instanceLogEntry.getProcessDefinitionId()));
		dataTable.setHTML(rows, 1, instanceLogEntry.getProcessDefinitionName());
		dataTable.setHTML(rows, 2, instanceLogEntry.getToken());
		DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18n("general.date.pattern"));
		dataTable.setHTML(rows, 3, dtf.format(instanceLogEntry.getDate()));
		dataTable.setHTML(rows, 4, instanceLogEntry.getType());
		dataTable.setHTML(rows, 5, instanceLogEntry.getInfo());
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 3, HasAlignment.ALIGN_CENTER);
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
	 * getDataTable
	 *
	 * @return FixedWidthGrid
	 */
	public FixedWidthGrid getDataTable() {
		return table.getDataTable();
	}

	/**
	 * fillWidth
	 */
	public void fillWidth() {
		table.fillWidth();
	}
}