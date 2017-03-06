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

import com.google.gwt.user.client.ui.Widget;
import com.openkm.extension.frontend.client.widget.wiki.finddocument.FindDocumentSelectPopup;
import com.openkm.extension.frontend.client.widget.wiki.findfolder.FindFolderSelectPopup;
import com.openkm.extension.frontend.client.widget.wiki.findwiki.FindWikiSelectPopup;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.extension.comunicator.*;
import com.openkm.frontend.client.extension.event.HasLanguageEvent;
import com.openkm.frontend.client.extension.event.HasLanguageEvent.LanguageEventConstant;
import com.openkm.frontend.client.extension.event.handler.LanguageHandlerExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * Wiki
 *
 * @author jllort
 *
 */
public class Wiki implements LanguageHandlerExtension {
	public static final int TAB_DOCUMENT = 0;
	public static final int TAB_FOLDER = 1;
	public static final int TAB_MAIL = 2;

	public static Wiki singleton;
	private static final String UUID = "a7b5a3c0-4b2f-11e0-b8af-0800200c9a66";

	private int selectedPanel = TAB_FOLDER;  // By default the folder tab is selected at starting 
	private TabDocumentWiki tabDocumentWiki;
	private TabFolderWiki tabFolderWiki;
	private TabMailWiki tabMailWiki;
	public Status status;
	public ConfirmPopup confirmPopup;
	public FindDocumentSelectPopup findDocumentSelectPopup;
	public FindFolderSelectPopup findFolderSelectPopup;
	public FindWikiSelectPopup findWikiSelectPopup;
	private ToolBarBoxWiki toolBarBoxWiki;

	/**
	 * Wiki
	 *
	 * @param uuidList
	 */
	public Wiki(List<String> uuidList) {
		if (isRegistered(uuidList)) {
			singleton = this;
			tabDocumentWiki = new TabDocumentWiki();
			tabFolderWiki = new TabFolderWiki();
			tabMailWiki = new TabMailWiki();
			status = new Status();
			status.setStyleName("okm-StatusPopup");
			confirmPopup = new ConfirmPopup();
			confirmPopup.setWidth("300px");
			confirmPopup.setHeight("125px");
			confirmPopup.setStyleName("okm-Popup");
			confirmPopup.addStyleName("okm-DisableSelect");
			findDocumentSelectPopup = new FindDocumentSelectPopup();
			findDocumentSelectPopup.setWidth("700px");
			findDocumentSelectPopup.setHeight("390px");
			findDocumentSelectPopup.setStyleName("okm-Popup");
			findDocumentSelectPopup.addStyleName("okm-DisableSelect");
			findFolderSelectPopup = new FindFolderSelectPopup();
			findFolderSelectPopup.setWidth("700px");
			findFolderSelectPopup.setHeight("390px");
			findFolderSelectPopup.setStyleName("okm-Popup");
			findFolderSelectPopup.addStyleName("okm-DisableSelect");
			findWikiSelectPopup = new FindWikiSelectPopup();
			findWikiSelectPopup.setWidth("400px");
			findWikiSelectPopup.setHeight("390px");
			findWikiSelectPopup.setStyleName("okm-Popup");
			findWikiSelectPopup.addStyleName("okm-DisableSelect");
			toolBarBoxWiki = new ToolBarBoxWiki();
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
		extensions.add(tabDocumentWiki);
		extensions.add(tabFolderWiki);
		extensions.add(tabMailWiki);
		extensions.add(toolBarBoxWiki);
		extensions.add(toolBarBoxWiki.getToolBarBox());
		return extensions;
	}

	/**
	 * Wiki
	 *
	 * @return
	 */
	public static Wiki get() {
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
				widget = tabDocumentWiki;
			} else if (selectedPanel == TAB_FOLDER) {
				widget = tabFolderWiki;
			} else if (selectedPanel == TAB_MAIL) {
				widget = tabMailWiki;
			}
		} else if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DASHBOARD) {
			widget = toolBarBoxWiki.getManager();
		}

		return widget;
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
	 * Add document tag
	 */
	public void addDocumentTag(String nodeUuid, String docName) {
		if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DESKTOP) {
			switch (selectedPanel) {
				case TAB_DOCUMENT:
					tabDocumentWiki.addDocumentTag(nodeUuid, docName);
					break;

				case TAB_FOLDER:
					tabFolderWiki.addDocumentTag(nodeUuid, docName);
					break;

				case TAB_MAIL:
					tabMailWiki.addDocumentTag(nodeUuid, docName);
					break;
			}
		} else if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DASHBOARD) {
			toolBarBoxWiki.addDocumentTag(nodeUuid, docName);
		}
	}

	/**
	 * Add image tag
	 */
	public void addImageTag(String url, String params) {
		if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DESKTOP) {
			switch (selectedPanel) {
				case TAB_DOCUMENT:
					tabDocumentWiki.addImageTag(url, params);
					break;

				case TAB_FOLDER:
					tabFolderWiki.addImageTag(url, params);
					break;

				case TAB_MAIL:
					tabMailWiki.addImageTag(url, params);
					break;
			}
		} else if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DASHBOARD) {
			toolBarBoxWiki.addDocumentTag(url, params);
		}
	}

	/**
	 * addFolderTag
	 */
	public void addFolderTag(String nodeUuid, String fldName) {
		if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DESKTOP) {
			switch (selectedPanel) {
				case TAB_DOCUMENT:
					tabDocumentWiki.addFolderTag(nodeUuid, fldName);
					break;

				case TAB_FOLDER:
					tabFolderWiki.addFolderTag(nodeUuid, fldName);
					break;

				case TAB_MAIL:
					tabMailWiki.addFolderTag(nodeUuid, fldName);
					break;
			}
		} else if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DASHBOARD) {
			toolBarBoxWiki.addFolderTag(nodeUuid, fldName);
		}
	}

	/**
	 * addWigiTag
	 *
	 * @param wikiTitle
	 */
	public void addWigiTag(String wikiTitle) {
		if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DESKTOP) {
			switch (selectedPanel) {
				case TAB_DOCUMENT:
					tabDocumentWiki.addWigiTag(wikiTitle);
					break;

				case TAB_FOLDER:
					tabFolderWiki.addWigiTag(wikiTitle);
					break;

				case TAB_MAIL:
					tabMailWiki.addWigiTag(wikiTitle);
					break;
			}
		} else if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DASHBOARD) {
			toolBarBoxWiki.addWigiTag(wikiTitle);
		}
	}

	@Override
	public void onChange(LanguageEventConstant event) {
		if (event.equals(HasLanguageEvent.LANGUAGE_CHANGED)) {
			tabDocumentWiki.langRefresh();
			tabFolderWiki.langRefresh();
			tabMailWiki.langRefresh();
			confirmPopup.langRefresh();
			findDocumentSelectPopup.langRefresh();
			toolBarBoxWiki.langRefresh();
		}
	}

	/**
	 * openWikiPage
	 *
	 * @param wikiTitle
	 */
	public static void openWikiPage(String wikiTitle) {
		WorkspaceComunicator.changeSelectedTab(UIDockPanelConstants.DASHBOARD);
		DashboardComunicator.showToolBoxExtension(singleton.toolBarBoxWiki.getToolBarBox());
		singleton.toolBarBoxWiki.openWikiPage(wikiTitle);
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

	/**
	 * initJavaScriptApi
	 *
	 * @param toolBar
	 */
	public native void initJavaScriptApi(Wiki wiki) /*-{
        $wnd.jsOpenWikiPage = function (wikiTitle) {
            @com.openkm.extension.frontend.client.widget.wiki.Wiki::openWikiPage(Ljava/lang/String;)(wikiTitle);
            return true;
        }
        $wnd.openWikiPage = function (wikiTitle) {
            @com.openkm.extension.frontend.client.widget.wiki.Wiki::openWikiPage(Ljava/lang/String;)(wikiTitle);
            return true;
        }
    }-*/;
}
