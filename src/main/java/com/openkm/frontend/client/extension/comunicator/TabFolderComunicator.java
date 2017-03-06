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

package com.openkm.frontend.client.extension.comunicator;

import com.google.gwt.user.client.ui.Widget;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTNote;

import java.util.Collection;

/**
 * TabFolderComunicator
 *
 * @author jllort
 *
 */
public class TabFolderComunicator {

	/**
	 * getSelectedTab
	 *
	 * @return
	 */
	public static int getSelectedTab() {
		return Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.getSelectedTab();
	}

	/**
	 * getFolder
	 *
	 * @return
	 */
	public static GWTFolder getFolder() {
		return Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.getFolder();
	}

	/**
	 * isVisibleButton
	 *
	 * @return
	 */
	public static boolean isVisibleButton() {
		return Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.isVisibleButton();
	}

	/**
	 * addKeyword
	 *
	 * @param keyword
	 */
	public static void addKeyword(String keyword) {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.addKeyword(keyword);
	}

	/**
	 * removeKeyword
	 *
	 * @param keyword
	 */
	public static void removeKeyword(String keyword) {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.removeKey(keyword);
	}

	/**
	 * addCategory
	 *
	 * @param keyword
	 */
	public static void addCategory(GWTFolder category) {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.addCategory(category);
	}

	/**
	 * removeKeyword
	 *
	 * @param keyword
	 */
	public static void removeCategory(String UUID) {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.removeCategory(UUID);
	}

	/**
	 * getKeywords
	 *
	 * @return
	 */
	public static Collection<String> getKeywords() {
		return Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.getKeywords();
	}

	/**
	 * getNotes
	 *
	 * @return
	 */
	public static Collection<GWTNote> getNotes() {
		return Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.notes.getNotes();
	}

	/**
	 * isWidgetExtensionVisible
	 *
	 * @param widget
	 * @return
	 */
	public static boolean isWidgetExtensionVisible(Widget widget) {
		return Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.isWidgetExtensionVisible(widget);
	}
}