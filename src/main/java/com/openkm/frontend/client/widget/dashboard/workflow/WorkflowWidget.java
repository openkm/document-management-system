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

package com.openkm.frontend.client.widget.dashboard.workflow;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTTaskInstance;
import com.openkm.frontend.client.widget.ConfirmPopup;
import com.openkm.frontend.client.widget.dashboard.Status;

import java.util.List;
import java.util.ListIterator;

/**
 * WorkflowWidget
 *
 * @author jllort
 *
 */
public class WorkflowWidget extends Composite {

	private static final int TYPE_PENDING_TASK = 1;
	private static final int TYPE_POOLED_TASK = 2;

	private static int HEADER_SQUARE = 24;
	private static int SEPARATOR_HEIGHT = 20;
	private static int SEPARATOR_WIDTH = 20;

	private VerticalPanel vPanel;
	private SimplePanel spTop;
	private HorizontalPanel hPanel;
	private SimplePanel spLeft;
	private VerticalPanel vCenterPanel;
	private SimplePanel spRight;
	private Header header;
	private SimplePanel panelData;
	private FlexTable table;
	private Image zoomImage;
	private boolean zoom = false;
	private boolean flagZoom = true;
	public Status status;
	private String headerTextKey;
	private int widgetType = TYPE_PENDING_TASK;
	private GWTTaskInstance taskInstancePooled = null;
	private double processToExecuteNextTask = -1;

	/**
	 * WorkflowWidget
	 */
	public WorkflowWidget(String headerTextKey, String iconUrl, boolean zoom) {
		status = new Status();
		status.setStyleName("okm-StatusPopup");

		spTop = new SimplePanel();
		spLeft = new SimplePanel();
		spRight = new SimplePanel();
		panelData = new SimplePanel();
		table = new FlexTable();
		vCenterPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();
		header = new Header(iconUrl, zoom);
		vPanel = new VerticalPanel();
		this.headerTextKey = headerTextKey;

		// Sets or unsets visible table
		table.setVisible(zoom);

		header.setHeaderText(Main.i18n(headerTextKey));

		panelData.add(table);

		vCenterPanel.add(header);
		vCenterPanel.add(panelData);

		hPanel.add(spLeft);
		hPanel.add(vCenterPanel);
		hPanel.add(spRight);

		vPanel.add(spTop);
		vPanel.add(hPanel);

		spTop.setHeight("" + SEPARATOR_HEIGHT + "px");
		spLeft.setWidth("" + SEPARATOR_WIDTH + "px");
		spRight.setWidth("" + SEPARATOR_WIDTH + "px");

		vPanel.setStyleName("okm-DashboardWidget ");
		panelData.setStyleName("data");
		table.setStyleName("okm-NoWrap");

		panelData.setWidth("99.6%");
		header.setWidth("100%");

		table.setCellPadding(0);
		table.setCellSpacing(0);

		vPanel.addStyleName("okm-DisableSelect");

		initWidget(vPanel);
	}

	/**
	 * setHeaderText
	 *
	 * @param text
	 */
	public void setHeaderText(String text) {
		header.setHeaderText(text);
	}

	/**
	 * setHeaderResults
	 *
	 * @param value
	 */
	public void setHeaderResults(int value) {
		header.setHeaderResults(value);
	}

	/**
	 * setWidth
	 *
	 * @param width
	 */
	public void setWidth(int width) {
		vCenterPanel.setWidth("" + (width - 2 * SEPARATOR_WIDTH) + "px");
	}

	/**
	 * removeAllRows
	 */
	private void removeAllRows() {
		while (table.getRowCount() > 0) {
			table.removeRow(0);
		}
	}

	/**
	 * Setting documents
	 *
	 * @param docList document list
	 */
	public void setTasks(List<GWTTaskInstance> taskIntanceList) {
		int tasksNotViewed = 0;
		removeAllRows();

		for (ListIterator<GWTTaskInstance> it = taskIntanceList.listIterator(); it.hasNext(); ) {
			int row = table.getRowCount();
			final GWTTaskInstance taskInstanceResult = it.next();

			if (taskInstanceResult.getProcessInstance().getId() == processToExecuteNextTask) {
				processToExecuteNextTask = -1;
				Main.get().mainPanel.dashboard.workflowDashboard.workflowFormPanel.setTaskInstance(taskInstanceResult);
			}

			Anchor taskName = new Anchor();
			taskName.setText(taskInstanceResult.getName());
			taskName.setTitle(taskInstanceResult.getProcessInstance().getProcessDefinition().getName());

			switch (widgetType) {
				case TYPE_PENDING_TASK:
					taskName.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							Main.get().mainPanel.dashboard.workflowDashboard.workflowFormPanel.setTaskInstance(taskInstanceResult);
						}
					});
					break;

				case TYPE_POOLED_TASK:
					taskName.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_GET_POOLED_WORKFLOW_TASK);
							Main.get().confirmPopup.show();
							taskInstancePooled = taskInstanceResult;
						}
					});
					break;
			}

			taskName.setStyleName("okm-Hyperlink");

			table.setHTML(row, 0, "");
			table.setWidget(row, 1, taskName);
			DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
			table.setHTML(row, 2, dtf.format(taskInstanceResult.getCreate()));
			table.getCellFormatter().setWidth(row, 0, "20px");
			table.getCellFormatter().setWidth(row, 1, "100%"); // Table sets de 100% of space
			table.getCellFormatter().setHorizontalAlignment(row, 2, HasAlignment.ALIGN_RIGHT);

			tasksNotViewed++;
			table.getRowFormatter().setStyleName(row, "okm-NotViewed");
		}

		header.setHeaderNotViewedResults(tasksNotViewed);
	}

	/**
	 * getNotViewed
	 *
	 * @return
	 */
	public int getNotViewed() {
		return header.getNotViewed();
	}

	/**
	 * Refreshing language
	 */
	public void langRefresh() {
		header.setHeaderText(Main.i18n(headerTextKey));
		header.setHeaderNotViewedResults(header.getNotViewed());
	}

	/**
	 * Header
	 *
	 * @author jllort
	 *
	 */
	private class Header extends HorizontalPanel implements HasClickHandlers {

		private SimplePanel spLeft;
		private SimplePanel spRight;
		private SimplePanel iconImagePanel;
		private HorizontalPanel titlePanel;
		private HTML headerText;
		private HTML headerResults;
		private HTML headerNotViewedResults;
		private int notViewed = 0;
		private Image iconImage;

		/**
		 * Header
		 */
		public Header(String iconUrl, boolean visible) {
			super();
			sinkEvents(Event.ONCLICK);

			iconImage = new Image(iconUrl);

			zoom = visible;
			if (zoom) {
				zoomImage = new Image("img/zoom_out.gif");
			} else {
				zoomImage = new Image("img/zoom_in.gif");
			}
			zoomImage.setStyleName("okm-Hyperlink");

			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (flagZoom) {
						zoom = !zoom;
						table.setVisible(zoom);
						if (zoom) {
							zoomImage.setUrl("img/zoom_out.gif");
						} else {
							zoomImage.setUrl("img/zoom_in.gif");
						}
					} else {
						flagZoom = true;
					}
				}
			});

			setHeight("" + HEADER_SQUARE);

			spLeft = new SimplePanel();
			spRight = new SimplePanel();
			iconImagePanel = new SimplePanel();
			titlePanel = new HorizontalPanel();
			headerText = new HTML("");
			headerResults = new HTML("");
			headerNotViewedResults = new HTML("");

			iconImagePanel.add(iconImage);

			titlePanel.add(headerText);
			titlePanel.add(headerResults);

			titlePanel.setCellVerticalAlignment(headerResults, HasAlignment.ALIGN_MIDDLE);
			titlePanel.setCellVerticalAlignment(headerNotViewedResults, HasAlignment.ALIGN_MIDDLE);
			titlePanel.setCellHorizontalAlignment(headerResults, HasAlignment.ALIGN_LEFT);
			titlePanel.setCellHorizontalAlignment(headerNotViewedResults, HasAlignment.ALIGN_LEFT);

			add(spLeft);
			add(iconImagePanel);
			add(titlePanel);
			add(headerNotViewedResults);
			add(zoomImage);
			add(spRight);

			setCellHorizontalAlignment(headerNotViewedResults, HasAlignment.ALIGN_RIGHT);
			setCellVerticalAlignment(iconImagePanel, HasAlignment.ALIGN_MIDDLE);
			setCellVerticalAlignment(titlePanel, HasAlignment.ALIGN_MIDDLE);
			setCellVerticalAlignment(headerNotViewedResults, HasAlignment.ALIGN_MIDDLE);
			setCellVerticalAlignment(zoomImage, HasAlignment.ALIGN_MIDDLE);

			setCellWidth(spLeft, "10px");
			setCellWidth(iconImagePanel, "22px");
			setCellWidth(zoomImage, "22px");
			setCellWidth(spRight, "10px");

			setStyleName("header");
		}

		/**
		 * setHeaderText
		 *
		 * @param text
		 */
		public void setHeaderText(String text) {
			headerText.setHTML(text);
		}

		/**
		 * setHeaderResults
		 *
		 * @param value
		 */
		public void setHeaderResults(int value) {
			headerResults.setHTML("&nbsp;&nbsp;(" + value + ")&nbsp;&nbsp;");
		}

		/**
		 * setHeaderNotViewedResults
		 *
		 * @param value
		 */
		public void setHeaderNotViewedResults(int value) {
			notViewed = value;
			if (value > 0) {
				headerNotViewedResults.setHTML("&nbsp;" + value + "&nbsp;&nbsp;");
				titlePanel.setStyleName("okm-NotViewed");
				headerNotViewedResults.setStyleName("okm-NotViewed");

			} else {
				headerNotViewedResults.setHTML("");
				titlePanel.removeStyleName("okm-NotViewed");
				headerNotViewedResults.removeStyleName("okm-NotViewed");
			}
		}

		/**
		 * getNotViewed
		 *
		 * @return
		 */
		public int getNotViewed() {
			return notViewed;
		}

		/* (non-Javadoc)
		 * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
		 */
		@Override
		public HandlerRegistration addClickHandler(ClickHandler handler) {
			return addHandler(handler, ClickEvent.getType());
		}
	}

	/**
	 * Sets the refreshing
	 */
	public void setRefreshing() {
		int left = getAbsoluteLeft() + ((getOffsetWidth() - status.getOffsetWidth()) / 2);
		int top = getAbsoluteTop() + ((getOffsetHeight() - status.getOffsetHeight()) / 2);
		status.setFlag_getDashboard();
		if (zoom) {
			status.refresh(left, top);
		}
	}

	/**
	 * Unsets the refreshing
	 */
	public void unsetRefreshing() {
		status.unsetFlag_getDashboard();
	}

	/**
	 * Sets the widget as pending task 
	 */
	public void setIsWidgetPendingTask() {
		widgetType = TYPE_PENDING_TASK;
	}

	/**
	 * Sets the widget as pooled task 
	 */
	public void setIsWidgetPooledTask() {
		widgetType = TYPE_POOLED_TASK;
	}

	/**
	 * The users gets the pooled task instance
	 */
	public GWTTaskInstance getPooledTaskInstance() {
		return taskInstancePooled;
	}

	/**
	 * resets the pooled task instance
	 */
	public void resetPooledTaskInstance() {
		taskInstancePooled = null;
	}

	/**
	 * @param processToExecuteNextTask
	 */
	public void setProcessToExecuteNextTask(double processToExecuteNextTask) {
		this.processToExecuteNextTask = processToExecuteNextTask;
	}
}
