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

import com.google.gwt.user.client.ui.Widget;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.extension.comunicator.TabDocumentComunicator;
import com.openkm.frontend.client.extension.comunicator.TabFolderComunicator;
import com.openkm.frontend.client.extension.comunicator.TabMailComunicator;
import com.openkm.frontend.client.extension.comunicator.WorkspaceComunicator;
import com.openkm.frontend.client.extension.event.HasLanguageEvent;
import com.openkm.frontend.client.extension.event.HasLanguageEvent.LanguageEventConstant;
import com.openkm.frontend.client.extension.event.handler.LanguageHandlerExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * Forum
 *
 * @author jllort
 *
 */
public class Forum implements LanguageHandlerExtension {

	public static final int TAB_DOCUMENT = 0;
	public static final int TAB_FOLDER = 1;
	public static final int TAB_MAIL = 2;

	public static Forum singleton;
	private static final String UUID = "522e7720-5c54-11e0-80e3-0800200c9a66";
	private int selectedPanel = TAB_FOLDER;
	private TabDocumentForum tabDocumentForum;
	private TabFolderForum tabFolderForum;
	private TabMailForum tabMailForum;
	private ToolBarBoxForum toolBarBoxForum;
	public ConfirmPopup confirmPopup;
	public Status status;

	/**
	 * Forum
	 *
	 * @param uuidList
	 */
	public Forum(List<String> uuidList) {
		if (isRegistered(uuidList)) {
			singleton = this;
			tabDocumentForum = new TabDocumentForum();
			tabFolderForum = new TabFolderForum();
			tabMailForum = new TabMailForum();
			toolBarBoxForum = new ToolBarBoxForum();
			confirmPopup = new ConfirmPopup();
			confirmPopup.setWidth("300px");
			confirmPopup.setHeight("125px");
			confirmPopup.setStyleName("okm-Popup");
			confirmPopup.addStyleName("okm-DisableSelect");
			status = new Status();
			status.setStyleName("okm-StatusPopup");
		}
	}

	/**
	 * getExtensions
	 *
	 * @return
	 */
	public List<Object> getExtensions() {
		List<Object> extensions = new ArrayList<Object>();
		extensions.add(singleton);
		extensions.add(tabDocumentForum);
		extensions.add(tabFolderForum);
		extensions.add(tabMailForum);
		extensions.add(toolBarBoxForum);
		extensions.add(toolBarBoxForum.getToolBarBox());
		return extensions;
	}

	/**
	 * get
	 *
	 * @return
	 */
	public static Forum get() {
		return singleton;
	}

	/**
	 * getUuid
	 *
	 * @return
	 */
	public String getUuid() {
		switch (selectedPanel) {
			case TAB_DOCUMENT:
				return TabDocumentComunicator.getDocument().getUuid();

			case TAB_FOLDER:
				return TabFolderComunicator.getFolder().getUuid();

			case TAB_MAIL:
				return TabMailComunicator.getMail().getUuid();

			default:
				return null;
		}
	}

	/**
	 * setTabDocumentSelected
	 */
	public void setTabDocumentSelected() {
		selectedPanel = TAB_DOCUMENT;
	}

	/**
	 * setTabFolderSelected
	 */
	public void setTabFolderSelected() {
		selectedPanel = TAB_FOLDER;
	}

	/**
	 * setTabMailSelected
	 */
	public void setTabMailSelected() {
		selectedPanel = TAB_MAIL;
	}

	@Override
	public void onChange(LanguageEventConstant event) {
		if (event.equals(HasLanguageEvent.LANGUAGE_CHANGED)) {
			tabDocumentForum.langRefresh();
			tabFolderForum.langRefresh();
			tabMailForum.langRefresh();
			toolBarBoxForum.langRefresh();
			confirmPopup.langRefresh();
		}
	}

	public Widget getWidgetTab() {
		Widget widget = null;

		if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DESKTOP) {
			switch (selectedPanel) {
				case TAB_DOCUMENT:
					widget = tabDocumentForum;
					break;
				case TAB_FOLDER:
					widget = tabFolderForum;
					break;
				case TAB_MAIL:
					widget = tabMailForum;
					break;
			}
		} else if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DASHBOARD) {
			widget = toolBarBoxForum.getManager();
		}

		return widget;
	}

	/**
	 * isRegistered
	 *
	 * @param uuidList
	 * @return
	 */
	public static boolean isRegistered(List<String> uuidList) {
		return uuidList.contains(UUID);
	}
}