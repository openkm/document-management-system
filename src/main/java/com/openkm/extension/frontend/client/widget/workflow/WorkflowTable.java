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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ResizePolicy;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollPolicy;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollTableImages;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.bean.GWTProcessInstance;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * WorkflowTable
 *
 * @author jllort
 *
 */
public class WorkflowTable extends Composite {
	public static final int NUMBER_OF_COLUMNS = 6;

	private ScrollTable table;
	private FixedWidthFlexTable headerTable;
	private FixedWidthGrid dataTable;
	private WorkflowController controller;

	/**
	 * WorkflowTable
	 *
	 * @param isAssigned
	 */
	public WorkflowTable(WorkflowController controller) {
		this.controller = controller;
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
		table.setColumnWidth(1, 50);
		table.setPreferredColumnWidth(2, 120);
		table.setColumnWidth(3, 150);
		table.setColumnWidth(3, 150);

		// Level 1 headers
		table.setSize("50px", "50px");
		headerTable.setHTML(0, 0, "<b>" + GeneralComunicator.i18nExtension("workflow.id") + "</b>");
		headerTable.setHTML(0, 1, "<b>" + GeneralComunicator.i18nExtension("workflow.version") + "</b>");
		headerTable.setHTML(0, 2, "<b>" + GeneralComunicator.i18nExtension("workflow.name") + "</b>");
		headerTable.setHTML(0, 3, "<b>" + GeneralComunicator.i18nExtension("workflow.start.date") + "</b>");
		headerTable.setHTML(0, 4, "<b>" + GeneralComunicator.i18nExtension("workflow.end.date") + "</b>");
		headerTable.setHTML(0, 5, "");

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
		headerTable.setHTML(0, 0, "<b>" + GeneralComunicator.i18nExtension("workflow.id") + "</b>");
		headerTable.setHTML(0, 1, "<b>" + GeneralComunicator.i18nExtension("workflow.version") + "</b>");
		headerTable.setHTML(0, 2, "<b>" + GeneralComunicator.i18nExtension("workflow.name") + "</b>");
		headerTable.setHTML(0, 3, "<b>" + GeneralComunicator.i18nExtension("workflow.start.date") + "</b>");
		headerTable.setHTML(0, 4, "<b>" + GeneralComunicator.i18nExtension("workflow.end.date") + "</b>");
	}

	/**
	 * addRow
	 *
	 * @param processInstance
	 */
	public void addRow(final GWTProcessInstance processInstance) {
		int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);
		dataTable.setHTML(rows, 0, String.valueOf(processInstance.getId()));
		dataTable.setHTML(rows, 1, String.valueOf(processInstance.getVersion()));
		dataTable.setHTML(rows, 2, processInstance.getProcessDefinition().getName());
		DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18n("general.date.pattern"));
		dataTable.setHTML(rows, 3, dtf.format(processInstance.getStart()));
		if (processInstance.getEnd() != null) {
			dataTable.setHTML(rows, 4, dtf.format(processInstance.getEnd()));
		} else {
			dataTable.setHTML(rows, 4, "");
		}
		HorizontalPanel hPanel = new HorizontalPanel();
		Image showImage = new Image(OKMBundleResources.INSTANCE.search());
		showImage.setStyleName("okm-Mail-Link");
		showImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.findLogsByProcessInstance(new Double(processInstance.getId()).intValue());
			}
		});
		hPanel.add(showImage);
		hPanel.setCellWidth(showImage, "20px");
		hPanel.setCellHorizontalAlignment(showImage, HasAlignment.ALIGN_CENTER);
		Image showDiagram = new Image(OKMBundleResources.INSTANCE.chartOrganisation());
		showDiagram.setStyleName("okm-Mail-Link");
		showDiagram.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.showGraph(new Double(processInstance.getProcessDefinition().getId()).intValue(),
						processInstance.getRootToken().getNode());

			}
		});
		hPanel.add(showDiagram);
		hPanel.setCellWidth(showDiagram, "20px");
		hPanel.setCellHorizontalAlignment(showDiagram, HasAlignment.ALIGN_CENTER);
		dataTable.setWidget(rows, 5, hPanel);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 1, HasAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 3, HasAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 5, HasAlignment.ALIGN_LEFT);
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