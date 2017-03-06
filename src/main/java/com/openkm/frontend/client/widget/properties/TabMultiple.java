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

package com.openkm.frontend.client.widget.properties;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Tab Multiple
 *
 * @author jllort
 *
 */
public class TabMultiple extends Composite {

	private final int TAB_FOLDER = 0;
	private final int TAB_DOCUMENT = 1;
	private final int TAB_MAIL = 2;
	private int actualTab = -1;

	private VerticalPanel panel;
	public TabFolder tabFolder;
	public TabDocument tabDocument;
	public TabMail tabMail;
	public Status status;

	/**
	 * Tab multiple
	 */
	public TabMultiple() {
		panel = new VerticalPanel();
		tabFolder = new TabFolder();
		tabDocument = new TabDocument();
		tabMail = new TabMail();
		status = new Status();
		status.setStyleName("okm-StatusPopup");

		panel.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
		panel.setSize("100%", "100%");

		initWidget(panel);
	}

	/**
	 * Inits on first load
	 */
	public void init() {
		enableTabFolder();
	}

	/**
	 * Sets visibility to buttons ( true / false )
	 *
	 * @param visible The visible value
	 */
	public void setVisibleButtons(boolean visible) {
		tabFolder.setVisibleButtons(visible);
		tabDocument.setVisibleButtons(visible);
	}

	/**
	 * Enables tab folder
	 */
	public void enableTabFolder() {
		if (actualTab != TAB_FOLDER) {
			removeAll();
			panel.add(tabFolder);
			tabFolder.resizingIncubatorWidgets();
			actualTab = TAB_FOLDER;
		}
	}

	/**
	 * Enables tab documents
	 */
	public void enableTabDocument() {
		if (actualTab != TAB_DOCUMENT) {
			removeAll();
			panel.add(tabDocument);
			tabDocument.resizingIncubatorWidgets();
			actualTab = TAB_DOCUMENT;
		}
	}

	/**
	 * Enables tab mail
	 */
	public void enableTabMail() {
		if (actualTab != TAB_MAIL) {
			removeAll();
			panel.add(tabMail);
			tabMail.resizingIncubatorWidgets();
			actualTab = TAB_MAIL;
		}
	}

	/**
	 * Removes all tabs
	 */
	public void removeAll() {
		panel.remove(tabFolder);
		panel.remove(tabDocument);
		panel.remove(tabMail);
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		tabFolder.langRefresh();
		tabDocument.langRefresh();
		tabMail.langRefresh();
	}

	/**
	 * Refresh security values
	 */
	public void securityRefresh() {
		switch (actualTab) {
			case TAB_FOLDER:
				tabFolder.securityRefresh();
				break;
			case TAB_DOCUMENT:
				tabDocument.securityRefresh();
				break;
			case TAB_MAIL:
				tabMail.securityRefresh();
				break;
		}
	}

	/**
	 * resetNumericFolderValues
	 */
	public void resetNumericFolderValues() {
		tabFolder.resetNumericFolderValues();
	}

	/**
	 * setNumberOfFolders
	 */
	public void setNumberOfFolders(int num) {
		tabFolder.setNumberOfFolders(num);
	}

	/**
	 * setNumberOfDocuments
	 */
	public void setNumberOfDocuments(int num) {
		tabFolder.setNumberOfDocuments(num);
	}

	/**
	 * setNumberOfMails
	 */
	public void setNumberOfMails(int num) {
		tabFolder.setNumberOfMails(num);
	}

	/**
	 * Sets the size
	 *
	 * @param width int The width size
	 * @param height int The height size
	 */
	public void setPixelSize(int width, int height) {
		tabFolder.setPixelSize(width, height);
		tabDocument.setPixelSize(width, height);
		tabMail.setPixelSize(width, height);
	}
}