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

package com.openkm.extension.frontend.client.widget.workflow;

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
 * Workflow
 *
 * @author jllort
 *
 */
public class Workflow implements LanguageHandlerExtension {
	public static final int TAB_DOCUMENT = 0;
	public static final int TAB_FOLDER = 1;
	public static final int TAB_MAIL = 2;

	public static Workflow singleton;
	private static final String UUID = "fa7f4556-3249-4268-88e0-0dd78a79872a";

	private int selectedPanel = TAB_FOLDER;  // By default the folder tab is selected at starting 
	private TabDocumentWorkflow tabDocumentWorkflow;
	private TabFolderWorkflow tabFolderWorkflow;

	/**
	 * Workflow
	 *
	 * @param uuidList
	 */
	public Workflow(List<String> uuidList) {
		if (isRegistered(uuidList)) {
			singleton = this;
			tabDocumentWorkflow = new TabDocumentWorkflow();
			tabFolderWorkflow = new TabFolderWorkflow();
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
		extensions.add(tabDocumentWorkflow);
		extensions.add(tabFolderWorkflow);
		return extensions;
	}

	/**
	 * get
	 *
	 * @return
	 */
	public static Workflow get() {
		return singleton;
	}

	/**
	 * getWidgetTab
	 *
	 * @return
	 */
	public Widget getWidgetTab() {
		Widget widget = null;

		if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DESKTOP) {
			if (selectedPanel == TAB_DOCUMENT) {
				widget = tabDocumentWorkflow;
			} else if (selectedPanel == TAB_FOLDER) {
				widget = tabFolderWorkflow;
			} else if (selectedPanel == TAB_MAIL) {
				//widget =  tabMailWiki;
			}
		}
//		else if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DASHBOARD) {
//			widget = toolBarBoxWiki.getManager();
//		}

		return widget;
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

	/**
	 * getUuid
	 *
	 * @return
	 */
	public String getUuid() {
		if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DESKTOP) {
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
		} else {
			return null;
		}
	}

	@Override
	public void onChange(LanguageEventConstant event) {
		if (event.equals(HasLanguageEvent.LANGUAGE_CHANGED)) {
			tabDocumentWorkflow.langRefresh();
		}
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