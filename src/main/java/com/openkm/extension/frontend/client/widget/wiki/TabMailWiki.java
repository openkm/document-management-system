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

package com.openkm.extension.frontend.client.widget.wiki;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.event.HasMailEvent;
import com.openkm.frontend.client.extension.event.HasMailEvent.MailEventConstant;
import com.openkm.frontend.client.extension.event.handler.MailHandlerExtension;
import com.openkm.frontend.client.extension.widget.tabmail.TabMailExtension;

/**
 * TabMailWiki
 *
 * @author jllort
 *
 */
public class TabMailWiki extends TabMailExtension implements MailHandlerExtension {
	private String title = "";
	private ScrollPanel scrollPanel;
	private WikiManager wikiManager;

	/**
	 * TabMailWiki
	 */
	public TabMailWiki() {
		title = GeneralComunicator.i18nExtension("wiki.title");
		wikiManager = new WikiManager(false);
		scrollPanel = new ScrollPanel(wikiManager);

		initWidget(scrollPanel);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.UIObject#setPixelSize(int, int)
	 */
	public void setPixelSize(int width, int height) {
		scrollPanel.setPixelSize(width, height);
		wikiManager.setPixelSize(width, height);
	}

	/**
	 * addDocumentTag
	 *
	 * @param uuid
	 * @param docName
	 */
	public void addDocumentTag(String uuid, String docName) {
		wikiManager.addDocumentTag(uuid, docName);
	}

	/**
	 * addImageTag
	 */
	public void addImageTag(String url, String params) {
		wikiManager.addImageTag(url, params);
	}

	/**
	 * addFolderTag
	 *
	 * @param uuid
	 * @param fldName
	 */
	public void addFolderTag(String uuid, String fldName) {
		wikiManager.addFolderTag(uuid, fldName);
	}

	/**
	 * addWigiTag
	 *
	 * @param wikiTitle
	 */
	public void addWigiTag(String wikiTitle) {
		wikiManager.addWigiTag(wikiTitle);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		title = GeneralComunicator.i18nExtension("wiki.title");
		wikiManager.langRefresh();
	}

	@Override
	public String getTabText() {
		return title;
	}

	@Override
	public void onChange(MailEventConstant event) {
		if (event.equals(HasMailEvent.MAIL_CHANGED)) {
			Wiki.get().setTabMailSelected();
			wikiManager.findWikiPageByNode(Wiki.get().getUuid());
		}
	}

	@Override
	public void set(GWTMail mail) {
	}

	@Override
	public void setVisibleButtons(boolean visible) {
	}
}