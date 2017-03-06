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

package com.openkm.extension.frontend.client.widget.forum;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.bean.extension.GWTForumTopic;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.widget.searchresult.ExtendedColumnSorter;

/**
 * Extends ScrollTable functionalities 
 *
 * @author jllort
 *
 */
public class TopicScrollTable extends ScrollTable {
	private FixedWidthGrid dataTable;
	private ForumController controller;

	/**
	 * TopicScrollTable
	 */
	public TopicScrollTable(FixedWidthGrid dataTable, FixedWidthFlexTable headerTable, ScrollTableImages scrollTableImages,
	                        ForumController controller) {
		super(dataTable, headerTable, scrollTableImages);
		this.dataTable = dataTable;
		this.controller = controller;

		setResizePolicy(ResizePolicy.UNCONSTRAINED);
		setScrollPolicy(ScrollPolicy.BOTH);
		dataTable.setSelectionEnabled(false); // Disables selection

		dataTable.setColumnSorter(new ExtendedColumnSorter());

		// Sets some events
		DOM.sinkEvents(getDataWrapper(), Event.ONDBLCLICK | Event.ONMOUSEDOWN);
	}

	/**
	 * Gets the selected row
	 *
	 * @return The selected row
	 */
	public int getSelectedRow() {
		int selectedRow = -1;

		if (!dataTable.getSelectedRows().isEmpty()) {
			selectedRow = ((Integer) dataTable.getSelectedRows().iterator().next()).intValue();
		}

		return selectedRow;
	}

	/**
	 * addRow
	 *
	 * @param topic
	 */
	public void addRow(final GWTForumTopic topic) {
		int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);

		// Topic column
		VerticalPanel topicPanel = new VerticalPanel();
		Anchor anchor = new Anchor();
		anchor.setHTML("<b>" + topic.getTitle() + "</b>");
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.refreshPosts(topic.getId(), topic.getTitle());
			}
		});
		anchor.setStyleName("okm-Forum-Topic");
		HorizontalPanel topicUser = new HorizontalPanel();
		HTML by = new HTML(GeneralComunicator.i18nExtension("forum.user.by") + "&nbsp;");
		by.setStyleName("okm-Forum-Gray");
		topicUser.add(by);
		HTML user = new HTML(topic.getUser());
		user.setStyleName("okm-Forum-User");
		topicUser.add(user);
		HTML separator = new HTML("&nbsp;&raquo;&nbsp;");
		separator.setStyleName("okm-Forum-Gray");
		topicUser.add(separator);
		DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18n("general.date.pattern"));
		HTML date = new HTML(dtf.format(topic.getDate()));
		date.setStyleName("okm-Forum-Gray");
		topicUser.add(date);
		topicPanel.add(anchor);
		topicPanel.add(topicUser);
		dataTable.setWidget(rows, 0, topicPanel);

		// Reply column
		dataTable.setHTML(rows, 1, "" + topic.getReplies());

		// View column
		dataTable.setHTML(rows, 2, "" + topic.getViews());

		// Last post column
		VerticalPanel lastPost = new VerticalPanel();
		HorizontalPanel lastUserTopic = new HorizontalPanel();
		HTML by2 = new HTML(GeneralComunicator.i18nExtension("forum.user.by") + "&nbsp;");
		by2.setStyleName("okm-Forum-Gray");
		lastUserTopic.add(by2);
		HTML lastUser = new HTML(topic.getLastPostUser());
		lastUser.setStyleName("okm-Forum-User");
		lastUserTopic.add(lastUser);
		lastPost.add(lastUserTopic);
		dtf = DateTimeFormat.getFormat(GeneralComunicator.i18n("general.date.pattern"));
		HTML date2 = new HTML(dtf.format(topic.getLastPostDate()));
		date2.setStyleName("okm-Forum-Gray");
		lastPost.add(date2);
		dataTable.setWidget(rows, 3, lastPost);

		// Format
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setVerticalAlignment(rows, 0, HasAlignment.ALIGN_MIDDLE);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 1, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setVerticalAlignment(rows, 1, HasAlignment.ALIGN_MIDDLE);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 2, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setVerticalAlignment(rows, 2, HasAlignment.ALIGN_MIDDLE);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 3, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setVerticalAlignment(rows, 3, HasAlignment.ALIGN_MIDDLE);
	}
}