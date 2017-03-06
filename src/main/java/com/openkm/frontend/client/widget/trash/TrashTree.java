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

package com.openkm.frontend.client.widget.trash;

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.widget.MenuPopup;
import com.openkm.frontend.client.widget.foldertree.FolderSelectPopup;
import com.openkm.frontend.client.widget.foldertree.FolderTree;

public class TrashTree extends FolderTree {

	/**
	 * Inits on first load
	 */
	public void init() {
		menuPopup = new MenuPopup(new TrashMenu());
		menuPopup.setStyleName("okm-MenuPopup");

		folderRoot = Main.get().trashRootFolder;

		// Sets the context on list context search values
		Main.get().repositoryContext.setContextTrash(folderRoot.getPath());
		Main.get().mainPanel.search.searchBrowser.searchIn.setContextValue(folderRoot.getPath(), UIDesktopConstants.NAVIGATOR_TRASH);

		rootItem = actualItem; // Save rootItem
		actualItem.setUserObject(folderRoot);
		evaluesFolderIcon(actualItem);
		actualItem.setState(true);
	}

	/**
	 * Move folder on file browser ( only trash mode )
	 */
	public void move() {
		GWTFolder folderToRestore = (GWTFolder) actualItem.getUserObject();
		folderSelectPopup.setEntryPoint(FolderSelectPopup.ENTRYPOINT_TRASH);
		folderSelectPopup.setToMove(folderToRestore);
		showDirectorySelectPopup();
		hideMenuPopup();
	}

	/**
	 * Copy folder on file browser ( only trash mode )
	 */
	public void copy() {
		GWTFolder folderToCopy = (GWTFolder) actualItem.getUserObject();
		folderSelectPopup.setEntryPoint(FolderSelectPopup.ENTRYPOINT_TRASH);
		folderSelectPopup.setToCopy(folderToCopy);
		showDirectorySelectPopup();
		hideMenuPopup();
	}
}