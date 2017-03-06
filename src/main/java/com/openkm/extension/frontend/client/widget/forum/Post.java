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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.bean.extension.GWTForumPost;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * Post
 *
 * @author jllort
 *
 */
public class Post extends Composite {
	private FlexTable table;
	ForumController controller;

	/**
	 * Post
	 */
	public Post(ForumController controller) {
		this.controller = controller;
		table = new FlexTable();
		table.setWidth("100%");

		initWidget(table);
	}

	/**
	 * removeAllRows
	 */
	public void removeAllRows() {
		table.removeAllRows();
	}

	/**
	 * addRow
	 *
	 * @param post
	 */
	public void addRow(final GWTForumPost post) {
		int row = table.getRowCount();
		final boolean isFirst = (row == 0);

		// Header
		HorizontalPanel header = new HorizontalPanel();
		header.addStyleName("okm-NoWrap");
		HTML subject = new HTML(post.getSubject());
		subject.setStyleName("okm-Forum-Topic");
		header.add(subject);
		header.setCellHorizontalAlignment(subject, HasAlignment.ALIGN_LEFT);
		table.setWidget(row, 0, header);

		if (post.getUser().equals(GeneralComunicator.getUser()) || GeneralComunicator.getWorkspace().isAdminRole()) {
			HorizontalPanel actionPanel = new HorizontalPanel();
			Button quote = new Button(GeneralComunicator.i18nExtension("forum.button.quote"));
			quote.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					controller.quotePost(post);
				}
			});

			quote.setStyleName("okm-CommentButton");
			Image edit = new Image(OKMBundleResources.INSTANCE.pencil());
			edit.setStyleName("okm-Hyperlink");
			edit.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (isFirst) {
						controller.editTopic(post);
					} else {
						controller.editPost(post);
					}
				}
			});

			Image delete = new Image(OKMBundleResources.INSTANCE.delete());
			delete.setStyleName("okm-Hyperlink");
			delete.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Forum.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_POST, post.getId(), controller);
					Forum.get().confirmPopup.center();
				}
			});

			actionPanel.add(quote);
			actionPanel.add(new HTML("&nbsp;"));
			actionPanel.add(edit);
			actionPanel.add(new HTML("&nbsp;"));
			actionPanel.add(delete);
			HTML space = new HTML("&nbsp;");
			actionPanel.add(space);
			actionPanel.setCellVerticalAlignment(quote, HasAlignment.ALIGN_MIDDLE);
			actionPanel.setCellVerticalAlignment(edit, HasAlignment.ALIGN_MIDDLE);
			actionPanel.setCellVerticalAlignment(delete, HasAlignment.ALIGN_MIDDLE);
			actionPanel.setCellWidth(space, "5px");
			table.setWidget(row, 1, actionPanel);
			table.getCellFormatter().setHorizontalAlignment(row, 1, HasAlignment.ALIGN_RIGHT);
		} else {
			table.setWidget(row, 1, new HTML(""));
		}

		// New row
		row++;

		// User
		HorizontalPanel topicUser = new HorizontalPanel();
		topicUser.addStyleName("okm-NoWrap");
		HTML by = new HTML(GeneralComunicator.i18nExtension("forum.user.by") + "&nbsp;");
		by.setStyleName("okm-Forum-Gray");
		topicUser.add(by);
		HTML user = new HTML(post.getUser());
		user.setStyleName("okm-Forum-User");
		topicUser.add(user);
		HTML separator = new HTML("&nbsp;&raquo;&nbsp;");
		separator.setStyleName("okm-Forum-Gray");
		topicUser.add(separator);
		DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18n("general.date.pattern"));
		topicUser.add(new HTML(dtf.format(post.getDate())));
		table.setWidget(row, 0, topicUser);
		table.setHTML(row++, 1, "");

		// Message
		table.setHTML(row, 0, ForumToolBarEditor.bbcode(post.getMessage())); // code tags to html
		table.getFlexCellFormatter().setColSpan(row++, 0, 2);

		// Separator
		table.setHTML(row, 0, "");
		table.getCellFormatter().setHeight(row, 0, "2px");
		table.getCellFormatter().setStyleName(row, 0, "okm-Mail");
		table.getFlexCellFormatter().setColSpan(row, 0, 2);
	}

	/**
	 * actionsVisible
	 *
	 * @param visible
	 */
	public void actionsVisible(boolean visible) {
		int rows = table.getRowCount();
		int i = 0;
		while (i < rows) {
			table.getCellFormatter().setVisible(i, 1, visible); // First row has action icons
			i = i + 2; // Jump next two rows
		}
	}

	/**
	 * getFirstTopicTitle
	 *
	 * @return
	 */
	public String getFirstTopicTitle() {
		if (table.getRowCount() > 0) {
			return ((HTML) ((HorizontalPanel) table.getWidget(0, 0)).getWidget(0)).getHTML();
		} else {
			return null;
		}
	}
}