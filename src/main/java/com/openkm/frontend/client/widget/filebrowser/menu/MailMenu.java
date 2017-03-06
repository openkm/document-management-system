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
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.MenuBase;

/**
 * Browser menu
 *
 * @author jllort
 *
 */
public class MailMenu extends MenuBase {

	private ToolBarOption toolBarOption;
	private MenuBar dirMenu;
	private MenuItem delete;
	private MenuItem rename;
	private MenuItem move;
	private MenuItem copy;
	private MenuItem convert;
	private MenuItem note;
	private MenuItem category;
	private MenuItem keyword;
	private MenuItem propertyGroup;
	private MenuItem updatePropertyGroup;
	private MenuItem merge;

	/**
	 * MailMenu
	 */
	public MailMenu() {
		toolBarOption = new ToolBarOption();
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected

		// First initialize language values
		dirMenu = new MenuBar(true);
		delete = new MenuItem(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("filebrowser.menu.delete")), true, deleteFile);
		delete.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(delete);
		rename = new MenuItem(Util.menuHTML("img/icon/actions/rename.gif", Main.i18n("general.menu.edit.rename")), true, renameFile);
		rename.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(rename);
		move = new MenuItem(Util.menuHTML("img/icon/actions/move_document.gif", Main.i18n("general.menu.edit.move")), true, moveFile);
		move.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(move);
		copy = new MenuItem(Util.menuHTML("img/icon/actions/copy.gif", Main.i18n("general.menu.edit.copy")), true, copyFile);
		copy.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(copy);
		convert = new MenuItem(Util.menuHTML("img/icon/menu/convert.png", Main.i18n("general.menu.tools.convert")), true, showConvert);
		convert.addStyleName("okm-MainMenuItem");
		dirMenu.addItem(convert);
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
		merge = new MenuItem(Util.menuHTML("img/icon/actions/merge_pdf.png", Main.i18n("general.menu.edit.merge.pdf")), true, mergePdf);
		merge.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(merge);

		dirMenu.setStyleName("okm-MenuBar");
		initWidget(dirMenu);
	}

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

	// Command menu to rename file
	Command moveFile = new Command() {
		public void execute() {
			if (toolBarOption.moveOption) {
				Main.get().mainPanel.topPanel.toolBar.executeMove();
				hide();
			}
		}
	};

	// Command menu to rename file
	Command copyFile = new Command() {
		public void execute() {
			if (toolBarOption.copyOption) {
				Main.get().mainPanel.topPanel.toolBar.executeCopy();
				hide();
			}
		}
	};

	// Command menu to convert
	Command showConvert = new Command() {
		public void execute() {
			if (toolBarOption.convertOption) {
				Main.get().mainPanel.topPanel.toolBar.executeConvert();
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

	// Command menu to merge pdf
	Command mergePdf = new Command() {
		public void execute() {
			if (toolBarOption.mergePdfOption) {
				Main.get().mainPanel.topPanel.toolBar.mergePdf();
				hide();
			}
		}
	};

	@Override
	public void langRefresh() {
		delete.setHTML(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("filebrowser.menu.delete")));
		rename.setHTML(Util.menuHTML("img/icon/actions/rename.gif", Main.i18n("general.menu.edit.rename")));
		move.setHTML(Util.menuHTML("img/icon/actions/move_document.gif", Main.i18n("general.menu.edit.move")));
		copy.setHTML(Util.menuHTML("img/icon/actions/copy.gif", Main.i18n("general.menu.edit.copy")));
		convert.setHTML(Util.menuHTML("img/icon/menu/convert.png", Main.i18n("general.menu.tools.convert")));
		note.setHTML(Util.menuHTML("img/icon/actions/add_note.png", Main.i18n("general.menu.edit.add.note")));
		category.setHTML(Util.menuHTML("img/icon/stackpanel/table_key.gif", Main.i18n("category.add")));
		keyword.setHTML(Util.menuHTML("img/icon/actions/book_add.png", Main.i18n("keyword.add")));
		propertyGroup.setHTML(Util.menuHTML("img/icon/actions/add_property_group.gif", Main.i18n("general.menu.edit.add.property.group")));
		updatePropertyGroup.setHTML(Util.menuHTML("img/icon/actions/update_property_group.png", Main.i18n("general.menu.edit.update.property.group")));
		merge.setHTML(Util.menuHTML("img/icon/actions/merge_pdf.png", Main.i18n("general.menu.edit.merge.pdf")));
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

	/**
	 * Evaluates menu options
	 */
	public void evaluateMenuOptions() {
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
		if (toolBarOption.convertOption) {
			enable(convert);
		} else {
			disable(convert);
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
		if (toolBarOption.mergePdfOption) {
			enable(merge);
		} else {
			disable(merge);
		}
	}

	/**
	 * Hide popup menu
	 */
	public void hide() {
		Main.get().mainPanel.desktop.browser.fileBrowser.mailMenuPopup.hide();
	}

	@Override
	public void setAvailableOption(GWTAvailableOption option) {
		if (!option.isDeleteOption()) {
			dirMenu.removeItem(delete);
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
		if (!option.isConvertOption()) {
			dirMenu.removeItem(convert);
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
		if (!option.isMergePdfOption()) {
			dirMenu.removeItem(merge);
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
		if (dirMenu != null) { // Condition caused by loading case
			toolBarOption.mergePdfOption = false;
		}
		disable(merge);
	}

	@Override
	public void enablePdfMerge() {
		if (dirMenu != null) { // Condition caused by loading case
			toolBarOption.mergePdfOption = true;
		}
		enable(merge);
	}
}