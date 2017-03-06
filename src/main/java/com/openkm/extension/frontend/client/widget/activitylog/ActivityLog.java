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

package com.openkm.extension.frontend.client.widget.activitylog;

import com.google.gwt.user.client.ui.Widget;
import com.openkm.frontend.client.extension.comunicator.TabDocumentComunicator;
import com.openkm.frontend.client.extension.comunicator.TabFolderComunicator;
import com.openkm.frontend.client.extension.comunicator.TabMailComunicator;
import com.openkm.frontend.client.extension.event.HasLanguageEvent;
import com.openkm.frontend.client.extension.event.HasLanguageEvent.LanguageEventConstant;
import com.openkm.frontend.client.extension.event.handler.LanguageHandlerExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * ActivityLog
 *
 * @author jllort
 *
 */
public class ActivityLog implements LanguageHandlerExtension {
	public static final int NONE = -1;
	public static final int TAB_DOCUMENT = 0;
	public static final int TAB_FOLDER = 1;
	public static final int TAB_MAIL = 2;

	private static ActivityLog singleton;
	private static final String UUID = "88ca0d10-39e2-11e0-9207-0800200c9a66";

	private int selectedPanel = TAB_FOLDER;  // By default the folder tab is selected at starting 
	private TabDocumentActivityLog tabDocumentActivityLog;
	private TabFolderActivityLog tabFolderActivityLog;
	private TabMailActivityLog tabMailActivityLog;
	public Status status;

	/**
	 * ActivityLog
	 */
	public ActivityLog(List<String> uuidList) {
		singleton = this;

		if (isRegistered(uuidList)) {
			tabDocumentActivityLog = new TabDocumentActivityLog();
			tabFolderActivityLog = new TabFolderActivityLog();
			tabMailActivityLog = new TabMailActivityLog();
			status = new Status();
			status.setStyleName("okm-StatusPopup");
		}
	}

	/**
	 * getExtensions
	 */
	public List<Object> getExtensions() {
		List<Object> extensions = new ArrayList<Object>();
		extensions.add(singleton);
		extensions.add(tabDocumentActivityLog);
		extensions.add(tabFolderActivityLog);
		extensions.add(tabMailActivityLog);
		return extensions;
	}

	/**
	 * ActivityLog
	 */
	public static ActivityLog get() {
		return singleton;
	}

	/**
	 * getWidgetTab
	 */
	public Widget getWidgetTab() {
		if (selectedPanel == TAB_DOCUMENT) {
			return tabDocumentActivityLog;
		}
		if (selectedPanel == TAB_FOLDER) {
			return tabFolderActivityLog;
		}
		if (selectedPanel == TAB_MAIL) {
			return tabMailActivityLog;
		} else {
			return null;
		}
	}

	/**
	 * getUuid
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
			tabDocumentActivityLog.langRefresh();
			tabFolderActivityLog.langRefresh();
			tabMailActivityLog.langRefresh();
		}
	}

	/**
	 * isRegistered
	 */
	public static boolean isRegistered(List<String> uuidList) {
		return uuidList.contains(UUID);
	}
}
