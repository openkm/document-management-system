/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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

package com.openkm.frontend.client.extension.comunicator;

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;

import java.util.List;

/**
 * FileBrowserCommunicator
 *
 * @author jllort
 */
public class FileBrowserCommunicator {
	/**
	 * isDocumentSelected
	 */
	public static boolean isDocumentSelected() {
		return Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected();
	}

	/**
	 * isFolderSelected
	 */
	public static boolean isFolderSelected() {
		return Main.get().mainPanel.desktop.browser.fileBrowser.isFolderSelected();
	}

	/**
	 * isMailSelected
	 */
	public static boolean isMailSelected() {
		return Main.get().mainPanel.desktop.browser.fileBrowser.isMailSelected();
	}

	/**
	 * getDocument
	 */
	public static GWTDocument getDocument() {
		return Main.get().mainPanel.desktop.browser.fileBrowser.getDocument();
	}

	/**
	 * getFolder
	 */
	public static GWTFolder getFolder() {
		return Main.get().mainPanel.desktop.browser.fileBrowser.getFolder();
	}

	/**
	 * getMail
	 */
	public static GWTMail getMail() {
		return Main.get().mainPanel.desktop.browser.fileBrowser.getMail();
	}

	/**
	 * isPanelSelected
	 */
	public static boolean isPanelSelected() {
		return Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected();
	}

	/**
	 * refreshOnlyFileBrowser
	 */
	public static void refreshOnlyFileBrowser() {
		Main.get().mainPanel.desktop.browser.fileBrowser.refreshOnlyFileBrowser();
	}

	/**
	 * isMassive
	 */
	public static boolean isMassive() {
		return Main.get().mainPanel.desktop.browser.fileBrowser.isMassive();
	}

	/**
	 * getAllSelectedUUIDs
	 */
	public static List<String> getAllSelectedUUIDs() {
		return Main.get().mainPanel.desktop.browser.fileBrowser.table.getAllSelectedUUIDs();
	}
}
