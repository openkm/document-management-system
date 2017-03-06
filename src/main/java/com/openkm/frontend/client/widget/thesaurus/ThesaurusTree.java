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

package com.openkm.frontend.client.widget.thesaurus;

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.widget.MenuPopup;
import com.openkm.frontend.client.widget.foldertree.FolderTree;

/**
 * Thesaurus tree
 *
 * @author jllort
 *
 */
public class ThesaurusTree extends FolderTree {

	public ThesaurusSelectPopup thesaurusSelectPopup;

	/**
	 * Inits on first load
	 */
	public void init() {
		menuPopup = new MenuPopup(new ThesaurusMenu());
		menuPopup.setStyleName("okm-MenuPopup");

		thesaurusSelectPopup = new ThesaurusSelectPopup();
		thesaurusSelectPopup.setWidth("400px");
		thesaurusSelectPopup.setHeight("300px");
		thesaurusSelectPopup.setStyleName("okm-Popup");

		folderRoot = Main.get().thesaurusRootFolder;

		actualItem.setUserObject(folderRoot);
		evaluesFolderIcon(actualItem);
		actualItem.setState(true);
		rootItem = actualItem;  // Preserves actualItem value
	}

	/**
	 * Move folder on file browser ( only trash mode )
	 */
	public void move() {
	}

	/**
	 * Copy folder on file browser ( only trash mode )
	 */
	public void copy() {
	}
}