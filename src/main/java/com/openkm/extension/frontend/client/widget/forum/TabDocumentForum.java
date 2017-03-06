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

import com.google.gwt.user.client.ui.ScrollPanel;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.event.HasDocumentEvent;
import com.openkm.frontend.client.extension.event.HasDocumentEvent.DocumentEventConstant;
import com.openkm.frontend.client.extension.event.handler.DocumentHandlerExtension;
import com.openkm.frontend.client.extension.widget.tabdocument.TabDocumentExtension;

/**
 * TabDocumentForum
 *
 * @author jllort
 *
 */
public class TabDocumentForum extends TabDocumentExtension implements DocumentHandlerExtension {

	private ScrollPanel scrollPanel;
	private String title = "";
	private TopicManager topicManager;

	/**
	 * TabDocumentForum
	 */
	public TabDocumentForum() {
		title = GeneralComunicator.i18nExtension("forum.tab.title");
		topicManager = new TopicManager();
		topicManager.setForumId(ForumScrollTable.FORUM_DOCUMENT_DISCUSSION_ID);
		scrollPanel = new ScrollPanel(topicManager);

		initWidget(scrollPanel);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.UIObject#setPixelSize(int, int)
	 */
	public void setPixelSize(int width, int height) {
		scrollPanel.setPixelSize(width, height);
		topicManager.setPixelSize(width, height);
	}

	@Override
	public String getTabText() {
		return title;
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		title = GeneralComunicator.i18nExtension("forum.tab.title");
		topicManager.langRefresh();
	}

	@Override
	public void onChange(DocumentEventConstant event) {
		if (event.equals(HasDocumentEvent.DOCUMENT_CHANGED)) {
			Forum.get().setTabDocumentSelected();
			topicManager.refreshTopics(Forum.get().getUuid());
		}
	}
}