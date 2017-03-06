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

import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * Status
 *
 * @author jllort
 *
 */
public class Status extends PopupPanel {
	private HorizontalPanel hPanel;
	private HTML msg;
	private HTML space;
	private Image image;

	private boolean flag_createForum = false;
	private boolean flag_updateForum = false;
	private boolean flag_createPost = false;
	private boolean flag_createTopic = false;
	private boolean flag_updatePost = false;
	private boolean flag_updateTopic = false;
	private boolean flag_getTopics = false;
	private boolean flag_getPosts = false;
	private boolean flag_deletePost = false;
	private boolean flag_getForums = false;
	private boolean flag_deleteForum = false;

	/**
	 * The status
	 */
	public Status() {
		super(false, true);
		hPanel = new HorizontalPanel();
		image = new Image(OKMBundleResources.INSTANCE.indicator());
		msg = new HTML("");
		space = new HTML("");

		hPanel.add(image);
		hPanel.add(msg);
		hPanel.add(space);

		hPanel.setCellVerticalAlignment(image, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellVerticalAlignment(msg, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellHorizontalAlignment(image, HasAlignment.ALIGN_CENTER);
		hPanel.setCellWidth(image, "30px");
		hPanel.setCellWidth(space, "7px");

		hPanel.setHeight("25px");

		msg.setStyleName("okm-NoWrap");

		super.hide();
		setWidget(hPanel);
	}

	/**
	 * Refreshing the panel
	 */
	public void refresh() {
		if (flag_createForum || flag_updateForum || flag_createPost || flag_createTopic || flag_updatePost
				|| flag_updateTopic || flag_getTopics || flag_getPosts || flag_deletePost || flag_getForums
				|| flag_deleteForum) {
			Widget tab = Forum.get().getWidgetTab();

			if (tab != null) { // Can be null if desktop is not visible
				int left = tab.getAbsoluteLeft() + (tab.getOffsetWidth() - 200) / 2;
				int top = tab.getAbsoluteTop() + (tab.getOffsetHeight() - 40) / 2;

				if (left > 0 && top > 0) {
					setPopupPosition(left, top);
					super.show();
				}
			}

		} else {
			super.hide();
		}
	}

	/**
	 * Sets the create forum flag
	 */
	public void setCreateForum() {
		msg.setHTML(GeneralComunicator.i18nExtension("status.forum.create.forum"));
		flag_createForum = true;
		refresh();
	}

	/**
	 * Unset the create forum flag
	 */
	public void unsetCreateForum() {
		flag_createForum = false;
		refresh();
	}

	/**
	 * Sets the update forum flag
	 */
	public void setUpdateForum() {
		msg.setHTML(GeneralComunicator.i18nExtension("status.forum.update.forum"));
		flag_updateForum = true;
		refresh();
	}

	/**
	 * Unset the update forum flag
	 */
	public void unsetUpdateForum() {
		flag_updateForum = false;
		refresh();
	}

	/**
	 * Sets the create post flag
	 */
	public void setCreatePost() {
		msg.setHTML(GeneralComunicator.i18nExtension("status.forum.create.post"));
		flag_createPost = true;
		refresh();
	}

	/**
	 * Unset the create post flag
	 */
	public void unsetCreatePost() {
		flag_createPost = false;
		refresh();
	}

	/**
	 * Sets the create topic flag
	 */
	public void setCreateTopic() {
		msg.setHTML(GeneralComunicator.i18nExtension("status.forum.create.topic"));
		flag_createTopic = true;
		refresh();
	}

	/**
	 * Unset the create post flag
	 */
	public void unsetCreateTopic() {
		flag_createTopic = false;
		refresh();
	}

	/**
	 * Sets the update post flag
	 */
	public void setUpdatePost() {
		msg.setHTML(GeneralComunicator.i18nExtension("status.forum.update.post"));
		flag_updatePost = true;
		refresh();
	}

	/**
	 * Unset the update post flag
	 */
	public void unsetUpdatePost() {
		flag_updatePost = false;
		refresh();
	}

	/**
	 * Sets the update topic flag
	 */
	public void setUpdateTopic() {
		msg.setHTML(GeneralComunicator.i18nExtension("status.forum.update.topic"));
		flag_createTopic = true;
		refresh();
	}

	/**
	 * Unset the update post flag
	 */
	public void unsetUpdateTopic() {
		flag_createTopic = false;
		refresh();
	}

	/**
	 * Sets the get topics flag
	 */
	public void setGetTopics() {
		msg.setHTML(GeneralComunicator.i18nExtension("status.forum.get.topics"));
		flag_getTopics = true;
		refresh();
	}

	/**
	 * Unset the get topics flag
	 */
	public void unsetGetTopics() {
		flag_getTopics = false;
		refresh();
	}

	/**
	 * Sets the get posts flag
	 */
	public void setGetPosts() {
		msg.setHTML(GeneralComunicator.i18nExtension("status.forum.get.posts"));
		flag_getPosts = true;
		refresh();
	}

	/**
	 * Unset the get pots flag
	 */
	public void unsetGetPosts() {
		flag_getPosts = false;
		refresh();
	}

	/**
	 * Sets the delete post flag
	 */
	public void setDeletePost() {
		msg.setHTML(GeneralComunicator.i18nExtension("status.forum.delete.post"));
		flag_deletePost = true;
		refresh();
	}

	/**
	 * Unset the delete post flag
	 */
	public void unsetDeletePost() {
		flag_deletePost = false;
		refresh();
	}

	/**
	 * Sets the get forums flag
	 */
	public void setGetForums() {
		msg.setHTML(GeneralComunicator.i18nExtension("status.forum.get.forums"));
		flag_getForums = true;
		refresh();
	}

	/**
	 * Unset the get forums flag
	 */
	public void unsetGetForums() {
		flag_getForums = false;
		refresh();
	}

	/**
	 * Sets the delete forum flag
	 */
	public void setDeleteForum() {
		msg.setHTML(GeneralComunicator.i18nExtension("status.forum.delete.forum"));
		flag_deleteForum = true;
		refresh();
	}

	/**
	 * Unset the delete forum flag
	 */
	public void unsetDeleteForum() {
		flag_deleteForum = false;
		refresh();
	}
}