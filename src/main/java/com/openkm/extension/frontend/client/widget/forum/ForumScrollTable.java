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
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.bean.extension.GWTForum;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.widget.searchresult.ExtendedColumnSorter;

/**
 * Extends ScrollTable functionalities
 *
 * @author jllort
 */
public class ForumScrollTable extends ScrollTable {
	public static final int FORUM_DOCUMENT_DISCUSSION_ID = 1;
	private FixedWidthGrid dataTable;
	private ForumController controller;

	/**
	 * ForumScrollTable
	 */
	public ForumScrollTable(FixedWidthGrid dataTable, FixedWidthFlexTable headerTable,
	                        ScrollTableImages scrollTableImages, ForumController controller) {
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
	 * @param forum
	 */
	public void addRow(final GWTForum forum) {
		int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);

		// Topic column
		VerticalPanel topicPanel = new VerticalPanel();
		Anchor anchor = new Anchor();
		anchor.setHTML("<b>" + forum.getName() + "</b>");
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.refreshTopics(forum.getId(), forum.getName());
			}
		});
		anchor.setStyleName("okm-Forum-Topic");
		HTML description = new HTML(ForumToolBarEditor.bbcode(forum.getDescription()));
		description.setStyleName("okm-Forum-Gray");
		HorizontalPanel hPanel = new HorizontalPanel();
		// Problem on starting workspace is still null
		if (GeneralComunicator.getWorkspace() != null && GeneralComunicator.getWorkspace().isAdminRole()) {
			HorizontalPanel actionPanel = new HorizontalPanel();
			Image edit = new Image(OKMBundleResources.INSTANCE.pencil());
			edit.setStyleName("okm-Hyperlink");
			edit.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					controller.editForum(forum);
				}
			});
			Image delete = new Image(OKMBundleResources.INSTANCE.delete());
			delete.setStyleName("okm-Hyperlink");
			delete.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Forum.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_FORUM, forum.getId(), controller);
					Forum.get().confirmPopup.center();
				}
			});
			actionPanel.add(edit);
			actionPanel.add(new HTML("&nbsp;"));
			if (forum.getId() != FORUM_DOCUMENT_DISCUSSION_ID) {
				actionPanel.add(delete);
				actionPanel.add(new HTML("&nbsp;"));
			}
			hPanel.add(actionPanel);
			hPanel.setCellWidth(actionPanel, "40px");
			hPanel.setCellHorizontalAlignment(actionPanel, HasAlignment.ALIGN_LEFT);
		}
		hPanel.setWidth("100%");
		hPanel.add(anchor);
		topicPanel.add(hPanel);
		topicPanel.add(description);

		dataTable.setWidget(rows, 0, topicPanel);

		// Reply column
		dataTable.setHTML(rows, 1, "" + forum.getNumTopics());

		// View column
		dataTable.setHTML(rows, 2, "" + forum.getNumPosts());

		// Last post column
		VerticalPanel lastPost = new VerticalPanel();
		HorizontalPanel lastUserTopic = new HorizontalPanel();
		HTML by2 = new HTML(GeneralComunicator.i18nExtension("forum.user.by") + "&nbsp;");
		by2.setStyleName("okm-Forum-Gray");
		lastUserTopic.add(by2);
		HTML lastUser = new HTML(forum.getLastPostUser());
		lastUser.setStyleName("okm-Forum-User");
		lastUserTopic.add(lastUser);
		lastPost.add(lastUserTopic);
		DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18n("general.date.pattern"));
		HTML date2 = new HTML(dtf.format(forum.getLastPostDate()));
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