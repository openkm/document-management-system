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

package com.openkm.frontend.client.widget.taxonomy;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.FileToUpload;
import com.openkm.frontend.client.bean.GWTAvailableOption;
import com.openkm.frontend.client.bean.ToolBarOption;
import com.openkm.frontend.client.constants.ui.UIFileUploadConstants;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.MenuBase;

/**
 * Taxonomy menu
 *
 * @author jllort
 *
 */
public class TaxonomyMenu extends MenuBase {

	private ToolBarOption toolBarOption;
	private MenuBar dirMenu;
	private MenuItem create;
	private MenuItem remove;
	private MenuItem rename;
	private MenuItem move;
	private MenuItem copy;
	private MenuItem bookmark;
	private MenuItem home;
	private MenuItem addDocument;
	private MenuItem export;

	public TaxonomyMenu() {
		toolBarOption = new ToolBarOption();
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected

		// First initialize language values
		dirMenu = new MenuBar(true);
		create = new MenuItem(Util.menuHTML("img/icon/actions/add_folder.gif", Main.i18n("tree.menu.directory.create")), true, addFolder);
		create.addStyleName("okm-MenuItem");
		dirMenu.addItem(create);
		remove = new MenuItem(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("tree.menu.directory.remove")), true, delFolder);
		remove.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(remove);
		rename = new MenuItem(Util.menuHTML("img/icon/actions/rename.gif", Main.i18n("tree.menu.directory.rename")), true, renFolder);
		rename.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(rename);
		move = new MenuItem(Util.menuHTML("img/icon/actions/move_folder.gif", Main.i18n("tree.menu.directory.move")), true, moveFolder);
		move.addStyleName("okm-MenuItem");
		dirMenu.addItem(move);
		copy = new MenuItem(Util.menuHTML("img/icon/actions/copy.gif", Main.i18n("tree.menu.directory.copy")), true, copyFolder);
		copy.addStyleName("okm-MenuItem");
		dirMenu.addItem(copy);
		addDocument = new MenuItem(Util.menuHTML("img/icon/actions/add_document.gif", Main.i18n("tree.menu.directory.add.document")), true, addDocumentFolder);
		addDocument.addStyleName("okm-MenuItem");
		dirMenu.addItem(addDocument);
		bookmark = new MenuItem(Util.menuHTML("img/icon/actions/add_bookmark.gif", Main.i18n("tree.menu.add.bookmark")), true, addBookmark);
		bookmark.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(bookmark);
		home = new MenuItem(Util.menuHTML("img/icon/actions/bookmark.gif", Main.i18n("tree.menu.set.home")), true, setHome);
		home.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(home);
		export = new MenuItem(Util.menuHTML("img/icon/actions/export.gif", Main.i18n("tree.menu.export")), true, exportToFile);
		export.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(export);
		dirMenu.setStyleName("okm-MenuBar");
		initWidget(dirMenu);
	}

	// Command menu to add a new Directory
	Command addFolder = new Command() {
		public void execute() {
			if (toolBarOption.createFolderOption) {
				Main.get().mainPanel.topPanel.toolBar.executeFolderDirectory();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	// Command menu to delete a new Directory
	Command delFolder = new Command() {
		public void execute() {
			if (toolBarOption.deleteOption) {
				Main.get().activeFolderTree.confirmDelete();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	// Command menu to delete a new Directory
	Command renFolder = new Command() {
		public void execute() {
			if (toolBarOption.renameOption) {
				Main.get().mainPanel.topPanel.toolBar.executeRename();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	// Command menu to refresh actual Directory
	Command moveFolder = new Command() {
		public void execute() {
			if (toolBarOption.moveOption) {
				Main.get().activeFolderTree.move();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	// Command menu to refresh actual Directory
	Command copyFolder = new Command() {
		public void execute() {
			if (toolBarOption.copyOption) {
				Main.get().activeFolderTree.copy();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	// Command menu to add Document to actual Directory
	Command addDocumentFolder = new Command() {
		public void execute() {
			if (toolBarOption.addDocumentOption) {
				FileToUpload fileToUpload = new FileToUpload();
				fileToUpload.setFileUpload(new FileUpload());
				fileToUpload.setPath((String) Main.get().activeFolderTree.getActualPath());
				fileToUpload.setAction(UIFileUploadConstants.ACTION_INSERT);
				Main.get().fileUpload.addPendingFileToUpload(fileToUpload);
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	// Command menu to add bookmark
	Command addBookmark = new Command() {
		public void execute() {
			if (toolBarOption.bookmarkOption) {
				Main.get().mainPanel.topPanel.toolBar.executeAddBookmark();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	// Command menu to set default home
	Command setHome = new Command() {
		public void execute() {
			if (toolBarOption.homeOption) {
				Main.get().activeFolderTree.setHome();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	// Command menu to set default home
	Command exportToFile = new Command() {
		public void execute() {
			if (toolBarOption.exportOption) {
				Main.get().activeFolderTree.exportFolderToFile();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	@Override
	public void evaluateMenuOptions() {
		if (toolBarOption.createFolderOption) {
			enable(create);
		} else {
			disable(create);
		}
		if (toolBarOption.deleteOption) {
			enable(remove);
		} else {
			disable(remove);
		}
		if (toolBarOption.renameOption) {
			enable(rename);
		} else {
			disable(rename);
		}
		if (toolBarOption.moveOption) {
			enable(move);
		} else {
			disable(move);
		}
		if (toolBarOption.copyOption) {
			enable(copy);
		} else {
			disable(copy);
		}
		if (toolBarOption.addDocumentOption) {
			enable(addDocument);
		} else {
			disable(addDocument);
		}
		if (toolBarOption.bookmarkOption) {
			enable(bookmark);
		} else {
			disable(bookmark);
		}
		if (toolBarOption.homeOption) {
			enable(home);
		} else {
			disable(home);
		}
		if (toolBarOption.exportOption) {
			enable(export);
		} else {
			disable(export);
		}
	}

	@Override
	public void langRefresh() {
		create.setHTML(Util.menuHTML("img/icon/actions/add_folder.gif", Main.i18n("tree.menu.directory.create")));
		remove.setHTML(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("tree.menu.directory.remove")));
		rename.setHTML(Util.menuHTML("img/icon/actions/rename.gif", Main.i18n("tree.menu.directory.rename")));
		move.setHTML(Util.menuHTML("img/icon/actions/move_folder.gif", Main.i18n("tree.menu.directory.move")));
		copy.setHTML(Util.menuHTML("img/icon/actions/copy.gif", Main.i18n("tree.menu.directory.copy")));
		addDocument.setHTML(Util.menuHTML("img/icon/actions/add_document.gif", Main.i18n("tree.menu.directory.add.document")));
		bookmark.setHTML(Util.menuHTML("img/icon/actions/add_bookmark.gif", Main.i18n("tree.menu.add.bookmark")));
		home.setHTML(Util.menuHTML("img/icon/actions/bookmark.gif", Main.i18n("tree.menu.set.home")));
		export.setHTML(Util.menuHTML("img/icon/actions/export.gif", Main.i18n("tree.menu.export")));
	}

	@Override
	public void setAvailableOption(GWTAvailableOption option) {
		if (!option.isCreateFolderOption()) {
			dirMenu.removeItem(create);
		}
		if (!option.isDeleteOption()) {
			dirMenu.removeItem(remove);
		}
		if (!option.isRenameOption()) {
			dirMenu.removeItem(rename);
		}
		if (!option.isMoveOption()) {
			dirMenu.removeItem(move);
		}
		if (!option.isCopyOption()) {
			dirMenu.removeItem(copy);
		}
		if (!option.isAddDocumentOption()) {
			dirMenu.removeItem(addDocument);
		}
		if (!option.isAddBookmarkOption()) {
			dirMenu.removeItem(bookmark);
		}
		if (!option.isSetHomeOption()) {
			dirMenu.removeItem(home);
		}
		if (!option.isExportOption()) {
			dirMenu.removeItem(export);
		}
	}

	@Override
	public void setOptions(ToolBarOption toolBarOption) {
		this.toolBarOption = toolBarOption;
		toolBarOption.bookmarkOption = true;
		evaluateMenuOptions();
	}

	@Override
	public void disableAllOptions() {
		toolBarOption = new ToolBarOption();
		evaluateMenuOptions();
	}

	@Override
	public void enableAddPropertyGroup() {
	}

	@Override
	public void disableAddPropertyGroup() {
	}

	@Override
	public void disablePdfMerge() {
	}

	@Override
	public void enablePdfMerge() {
	}
}