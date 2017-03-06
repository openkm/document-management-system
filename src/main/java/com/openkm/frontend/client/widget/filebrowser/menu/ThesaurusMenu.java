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

package com.openkm.frontend.client.widget.filebrowser.menu;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTAvailableOption;
import com.openkm.frontend.client.bean.ToolBarOption;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.MenuBase;

/**
 * ThesaurusMenu menu
 *
 * @author jllort
 *
 */
public class ThesaurusMenu extends MenuBase {
	private ToolBarOption toolBarOption;
	private MenuBar dirMenu;
	private MenuItem checkout;
	private MenuItem checkin;
	private MenuItem cancelCheckout;
	private MenuItem delete;
	private MenuItem rename;
	private MenuItem download;
	private MenuItem lock;
	private MenuItem unlock;
	private MenuItem note;
	private MenuItem category;
	private MenuItem keyword;
	private MenuItem propertyGroup;
	private MenuItem updatePropertyGroup;
	private MenuItem go;

	/**
	 * Thesaurus menu
	 */
	public ThesaurusMenu() {
		toolBarOption = new ToolBarOption();
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected

		// First initialize language values
		dirMenu = new MenuBar(true);
		download = new MenuItem(Util.menuHTML("img/icon/actions/download.gif", Main.i18n("filebrowser.menu.download")), true, downloadFile);
		download.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(download);
		checkout = new MenuItem(Util.menuHTML("img/icon/actions/checkout.gif", Main.i18n("filebrowser.menu.checkout")), true, checkoutFile);
		checkout.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(checkout);
		checkin = new MenuItem(Util.menuHTML("img/icon/actions/checkin.gif", Main.i18n("filebrowser.menu.checkin")), true, checkinFile);
		checkin.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(checkin);
		cancelCheckout = new MenuItem(Util.menuHTML("img/icon/actions/cancel_checkout.gif", Main.i18n("filebrowser.menu.checkout.cancel")), true, cancelCheckinFile);
		cancelCheckout.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(cancelCheckout);
		lock = new MenuItem(Util.menuHTML("img/icon/actions/lock.gif", Main.i18n("filebrowser.menu.lock")), true, lockFile);
		lock.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(lock);
		unlock = new MenuItem(Util.menuHTML("img/icon/actions/unlock.gif", Main.i18n("filebrowser.menu.unlock")), true, unlockFile);
		unlock.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(unlock);
		delete = new MenuItem(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("filebrowser.menu.delete")), true, deleteFile);
		delete.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(delete);
		rename = new MenuItem(Util.menuHTML("img/icon/actions/rename.gif", Main.i18n("general.menu.edit.rename")), true, renameFile);
		rename.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(rename);
		note = new MenuItem(Util.menuHTML("img/icon/actions/add_note.png", Main.i18n("general.menu.edit.add.note")), true, addNote);
		note.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(note);
		category = new MenuItem(Util.menuHTML("img/icon/stackpanel/table_key.gif", Main.i18n("category.add")), true, addCategory);
		category.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(category);
		keyword = new MenuItem(Util.menuHTML("img/icon/actions/book_add.png", Main.i18n("keyword.add")), true, addKeyword);
		keyword.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(keyword);
		propertyGroup = new MenuItem(Util.menuHTML("img/icon/actions/add_property_group.gif", Main.i18n("general.menu.edit.add.property.group")), true, addPropertyGroup);
		propertyGroup.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(propertyGroup);
		updatePropertyGroup = new MenuItem(Util.menuHTML("img/icon/actions/update_property_group.png", Main.i18n("general.menu.edit.update.property.group")), true, updatePropertyGroupOKM);
		updatePropertyGroup.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(updatePropertyGroup);
		go = new MenuItem(Util.menuHTML("img/icon/actions/goto_folder.gif", Main.i18n("search.result.menu.go.folder")), true, goDirectory);
		go.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(go);

		dirMenu.setStyleName("okm-MenuBar");
		initWidget(dirMenu);
	}

	// Command menu to download file
	Command downloadFile = new Command() {
		public void execute() {
			if (toolBarOption.downloadOption) {
				Main.get().mainPanel.topPanel.toolBar.executeDownload();
				hide();
			}
		}
	};

	// Command menu to checkout file
	Command checkoutFile = new Command() {
		public void execute() {
			if (toolBarOption.checkoutOption) {
				Main.get().mainPanel.topPanel.toolBar.executeCheckout();
				hide();
			}
		}
	};

	// Command menu to checkin file
	Command checkinFile = new Command() {
		public void execute() {
			if (toolBarOption.checkinOption) {
				Main.get().mainPanel.topPanel.toolBar.executeCheckin();
				hide();
			}
		}
	};

	// Command menu to cancel checkin file
	Command cancelCheckinFile = new Command() {
		public void execute() {
			if (toolBarOption.cancelCheckoutOption) {
				Main.get().mainPanel.topPanel.toolBar.executeCancelCheckout();
				hide();
			}
		}
	};

	// Command menu to lock file
	Command lockFile = new Command() {
		public void execute() {
			if (toolBarOption.lockOption) {
				Main.get().mainPanel.topPanel.toolBar.executeLock();
				hide();
			}
		}
	};

	// Command menu to unlock file
	Command unlockFile = new Command() {
		public void execute() {
			if (toolBarOption.unLockOption) {
				Main.get().mainPanel.topPanel.toolBar.executeUnlock();
				hide();
			}
		}
	};

	// Command menu to lock file
	Command deleteFile = new Command() {
		public void execute() {
			if (toolBarOption.deleteOption) {
				Main.get().mainPanel.topPanel.toolBar.executeDelete();
				hide();
			}
		}
	};

	// Command menu to rename file
	Command renameFile = new Command() {
		public void execute() {
			if (toolBarOption.renameOption) {
				Main.get().mainPanel.topPanel.toolBar.executeRename();
				hide();
			}
		}
	};

	// Command menu to add note
	Command addNote = new Command() {
		public void execute() {
			if (toolBarOption.addNoteOption) {
				Main.get().mainPanel.topPanel.toolBar.addNote();
				hide();
			}
		}
	};

	// Command menu to add category
	Command addCategory = new Command() {
		public void execute() {
			if (toolBarOption.addCategoryOption) {
				Main.get().mainPanel.topPanel.toolBar.addCategory();
				hide();
			}
		}
	};

	// Command menu to add category
	Command addKeyword = new Command() {
		public void execute() {
			if (toolBarOption.addKeywordOption) {
				Main.get().mainPanel.topPanel.toolBar.addKeyword();
				hide();
			}
		}
	};

	// Command menu to add property group
	Command addPropertyGroup = new Command() {
		public void execute() {
			if (toolBarOption.addPropertyGroupOption) {
				Main.get().mainPanel.topPanel.toolBar.addPropertyGroup();
				hide();
			}
		}
	};

	// Command menu to update property group
	Command updatePropertyGroupOKM = new Command() {
		public void execute() {
			if (toolBarOption.updatePropertyGroupOption) {
				Main.get().mainPanel.topPanel.toolBar.updatePropertyGroup();
				hide();
			}
		}
	};

	// Command menu to go directory file
	Command goDirectory = new Command() {
		public void execute() {
			if (toolBarOption.goOption) {
				String docPath = "";
				String path = "";
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
					docPath = Main.get().mainPanel.desktop.browser.fileBrowser.getDocument().getPath();
					path = Util.getParent(docPath);
				} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isFolderSelected()) {
					path = Main.get().mainPanel.desktop.browser.fileBrowser.getFolder().getPath();
				} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isMailSelected()) {
					docPath = Main.get().mainPanel.desktop.browser.fileBrowser.getMail().getPath();
					path = Util.getParent(docPath);
				}
				CommonUI.openPath(path, docPath);
				hide();
			}
		}
	};

	@Override
	public void langRefresh() {
		checkout.setHTML(Util.menuHTML("img/icon/actions/checkout.gif", Main.i18n("filebrowser.menu.checkout")));
		checkin.setHTML(Util.menuHTML("img/icon/actions/checkin.gif", Main.i18n("filebrowser.menu.checkin")));
		delete.setHTML(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("filebrowser.menu.delete")));
		rename.setHTML(Util.menuHTML("img/icon/actions/rename.gif", Main.i18n("general.menu.edit.rename")));
		cancelCheckout.setHTML(Util.menuHTML("img/icon/actions/cancel_checkout.gif", Main.i18n("filebrowser.menu.checkout.cancel")));
		lock.setHTML(Util.menuHTML("img/icon/actions/lock.gif", Main.i18n("filebrowser.menu.lock")));
		unlock.setHTML(Util.menuHTML("img/icon/actions/unlock.gif", Main.i18n("filebrowser.menu.unlock")));
		download.setHTML(Util.menuHTML("img/icon/actions/download.gif", Main.i18n("filebrowser.menu.download")));
		note.setHTML(Util.menuHTML("img/icon/actions/add_note.png", Main.i18n("general.menu.edit.add.note")));
		category.setHTML(Util.menuHTML("img/icon/stackpanel/table_key.gif", Main.i18n("category.add")));
		keyword.setHTML(Util.menuHTML("img/icon/actions/book_add.png", Main.i18n("keyword.add")));
		propertyGroup.setHTML(Util.menuHTML("img/icon/actions/add_property_group.gif", Main.i18n("general.menu.edit.add.property.group")));
		updatePropertyGroup.setHTML(Util.menuHTML("img/icon/actions/update_property_group.png", Main.i18n("general.menu.edit.update.property.group")));
		go.setHTML(Util.menuHTML("img/icon/actions/goto_folder.gif", Main.i18n("search.result.menu.go.folder")));
	}

	@Override
	public void setOptions(ToolBarOption toolBarOption) {
		this.toolBarOption = toolBarOption;
		evaluateMenuOptions();
	}

	@Override
	public void disableAllOptions() {
		toolBarOption = new ToolBarOption();
		evaluateMenuOptions();
	}

	/**
	 * Evaluates menu options
	 */
	public void evaluateMenuOptions() {
		if (toolBarOption.downloadOption) {
			enable(download);
		} else {
			disable(download);
		}
		if (toolBarOption.deleteOption) {
			enable(delete);
		} else {
			disable(delete);
		}
		if (toolBarOption.renameOption) {
			enable(rename);
		} else {
			disable(rename);
		}
		if (toolBarOption.checkoutOption) {
			enable(checkout);
		} else {
			disable(checkout);
		}
		if (toolBarOption.checkinOption) {
			enable(checkin);
		} else {
			disable(checkin);
		}
		if (toolBarOption.cancelCheckoutOption) {
			enable(cancelCheckout);
		} else {
			disable(cancelCheckout);
		}
		if (toolBarOption.lockOption) {
			enable(lock);
		} else {
			disable(lock);
		}
		if (toolBarOption.unLockOption) {
			enable(unlock);
		} else {
			disable(unlock);
		}
		if (toolBarOption.addNoteOption) {
			enable(note);
		} else {
			disable(note);
		}
		if (toolBarOption.addCategoryOption) {
			enable(category);
		} else {
			disable(category);
		}
		if (toolBarOption.addKeywordOption) {
			enable(keyword);
		} else {
			disable(keyword);
		}
		if (toolBarOption.addPropertyGroupOption) {
			enable(propertyGroup);
		} else {
			disable(propertyGroup);
		}
		if (toolBarOption.updatePropertyGroupOption) {
			enable(updatePropertyGroup);
		} else {
			disable(updatePropertyGroup);
		}
		if (toolBarOption.goOption) {
			enable(go);
		} else {
			disable(go);
		}
	}

	@Override
	public void setAvailableOption(GWTAvailableOption option) {
		if (!option.isDownloadOption()) {
			dirMenu.removeItem(download);
		}
		if (!option.isDeleteOption()) {
			dirMenu.removeItem(delete);
		}
		if (!option.isRenameOption()) {
			dirMenu.removeItem(rename);
		}
		if (!option.isCheckoutOption()) {
			dirMenu.removeItem(checkout);
		}
		if (!option.isCheckinOption()) {
			dirMenu.removeItem(checkin);
		}
		if (!option.isCancelCheckoutOption()) {
			dirMenu.removeItem(cancelCheckout);
		}
		if (!option.isLockOption()) {
			dirMenu.removeItem(lock);
		}
		if (!option.isUnLockOption()) {
			dirMenu.removeItem(unlock);
		}
		if (!option.isAddNoteOption()) {
			dirMenu.removeItem(note);
		}
		if (!option.isAddCategoryOption()) {
			dirMenu.removeItem(category);
		}
		if (!option.isAddKeywordOption()) {
			dirMenu.removeItem(keyword);
		}
		if (!option.isAddPropertyGroupOption()) {
			dirMenu.removeItem(propertyGroup);
		}
		if (!option.isUpdatePropertyGroupOption()) {
			dirMenu.removeItem(updatePropertyGroup);
		}
		if (!option.isGotoFolderOption()) {
			dirMenu.removeItem(go);
		}
	}

	@Override
	public void enableAddPropertyGroup() {
		if (dirMenu != null) { // Condition caused by loading case
			toolBarOption.addPropertyGroupOption = true;
		}
		enable(propertyGroup);
	}

	@Override
	public void disableAddPropertyGroup() {
		if (dirMenu != null) { // Condition caused by loading case
			toolBarOption.addPropertyGroupOption = false;
		}
		disable(propertyGroup);
	}

	@Override
	public void disablePdfMerge() {
	}

	@Override
	public void enablePdfMerge() {
	}

	/**
	 * Hide popup menu
	 */
	public void hide() {
		Main.get().mainPanel.desktop.browser.fileBrowser.thesaurusMenuPopup.hide();
	}
}