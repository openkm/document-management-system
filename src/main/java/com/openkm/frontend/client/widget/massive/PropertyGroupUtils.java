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

package com.openkm.frontend.client.widget.massive;

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.GWTProfileFileBrowser;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.widget.filebrowser.FileBrowser;

import java.util.List;

public class PropertyGroupUtils {
	/**
	 * refreshingActualNode
	 */
	public static void refreshingActualNode(List<GWTFormElement> formElements, boolean disablePropetyGroup) {
		GWTProfileFileBrowser pfb = Main.get().workspaceUserProperties.getWorkspace().getProfileFileBrowser();
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive() && pfb.isExtraColumns()) {
			Main.get().mainPanel.topPanel.toolBar.executeRefresh(); // Case massive and could be affected several rows
		} else {
			Object node = Main.get().mainPanel.topPanel.toolBar.getActualNode();
			if (node != null) {
				if (Main.get().mainPanel.topPanel.toolBar.isNodeDocument()) {
					GWTDocument doc = (GWTDocument) Main.get().mainPanel.topPanel.toolBar.getActualNode();
					if (pfb.isExtraColumns() && Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected() &&
							Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
						Main.get().mainPanel.desktop.browser.fileBrowser.setFileBrowserAction(FileBrowser.ACTION_PROPERTY_GROUP_REFRESH_DOCUMENT);
						Main.get().mainPanel.desktop.browser.fileBrowser.refreshDocumentValues();
					}
					Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.setProperties(doc);
				} else if (Main.get().mainPanel.topPanel.toolBar.isNodeFolder()) {
					GWTFolder folder = (GWTFolder) Main.get().mainPanel.topPanel.toolBar.getActualNode();
					if (pfb.isExtraColumns() && Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected() &&
							Main.get().mainPanel.desktop.browser.fileBrowser.isFolderSelected()) {
						Main.get().mainPanel.desktop.browser.fileBrowser.setFileBrowserAction(FileBrowser.ACTION_PROPERTY_GROUP_REFRESH_FOLDER);
						Main.get().mainPanel.desktop.browser.fileBrowser.refreshFolderValues();
					}
					Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.setProperties(folder);
				} else if (Main.get().mainPanel.topPanel.toolBar.isNodeMail()) {
					GWTMail mail = (GWTMail) Main.get().mainPanel.topPanel.toolBar.getActualNode();
					if (pfb.isExtraColumns() && Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected() &&
							Main.get().mainPanel.desktop.browser.fileBrowser.isMailSelected()) {
						Main.get().mainPanel.desktop.browser.fileBrowser.setFileBrowserAction(FileBrowser.ACTION_PROPERTY_GROUP_REFRESH_MAIL);
						Main.get().mainPanel.desktop.browser.fileBrowser.refreshMailValues();
					}
					Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.setProperties(mail);
				}
				// Case there's only one items (white) then
				// there's no item to be added and must disable addPropertyGroup
				// true only when add new property not when is updating
				if (disablePropetyGroup) {
					Main.get().mainPanel.topPanel.toolBar.disableAddPropertyGroup();
				}
			}
		}
	}
}