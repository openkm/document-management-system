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

package com.openkm.frontend.client.widget.categories;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;

public class CategoriesSelectPopup extends DialogBox {

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	public ScrollPanel scrollDirectoryPanel;
	private VerticalPanel verticalDirectoryPanel;
	private FolderSelectTree folderSelectTree;
	private Button cancelButton;
	private Button actionButton;
	public Status status;

	public CategoriesSelectPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		status = new Status();
		status.setStyleName("okm-StatusPopup");

		vPanel = new VerticalPanel();
		vPanel.setWidth("500px");
		vPanel.setHeight("300px");
		hPanel = new HorizontalPanel();

		scrollDirectoryPanel = new ScrollPanel();
		scrollDirectoryPanel.setSize("490px", "250px");
		scrollDirectoryPanel.setStyleName("okm-Popup-text");
		verticalDirectoryPanel = new VerticalPanel();
		verticalDirectoryPanel.setSize("100%", "100%");
		folderSelectTree = new FolderSelectTree();
		folderSelectTree.setSize("100%", "100%");

		verticalDirectoryPanel.add(folderSelectTree);
		scrollDirectoryPanel.add(verticalDirectoryPanel);

		cancelButton = new Button(Main.i18n("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		actionButton = new Button(Main.i18n("button.add"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				executeAction(folderSelectTree.getCategory());
			}
		});

		vPanel.add(new HTML("<br>"));
		vPanel.add(scrollDirectoryPanel);
		vPanel.add(new HTML("<br>"));
		hPanel.add(cancelButton);
		HTML space = new HTML();
		space.setWidth("50px");
		hPanel.add(space);
		hPanel.add(actionButton);
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(scrollDirectoryPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHeight(scrollDirectoryPanel, "250px");

		cancelButton.setStyleName("okm-NoButton");
		actionButton.setStyleName("okm-AddButton");

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * Executes the action
	 */
	public void executeAction(GWTFolder category) {
		int actualView = Main.get().mainPanel.desktop.navigator.getStackIndex();
		switch (actualView) {
			case UIDesktopConstants.NAVIGATOR_MAIL:
				Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mail.addCategory(category);
				break;

			default:
				// Filebrowser panel selected
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
					if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
						Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.addCategory(category);
					} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isFolderSelected()) {
						Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.addCategory(category);
					} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isMailSelected()) {
						Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mail.addCategory(category);
					}
				} else {
					// Otherside tree panel is selected
					Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.addCategory(category);
				}
				break;

		}

	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("categories.folder.select.label"));
		cancelButton.setText(Main.i18n("button.close"));
		actionButton.setText(Main.i18n("button.add"));
	}

	/**
	 * Shows the popup 
	 */
	public void show() {
		initButtons();
		int left = (Window.getClientWidth() - 500) / 2;
		int top = (Window.getClientHeight() - 300) / 2;
		setPopupPosition(left, top);
		setText(Main.i18n("categories.folder.select.label"));

		// Resets to initial tree value
		folderSelectTree.reset();
		super.show();
	}

	/**
	 * Enables or disables move button
	 *
	 * @param enable
	 */
	public void enable(boolean enable) {
		actionButton.setEnabled(enable);
	}

	/**
	 * Enables all button
	 */
	private void initButtons() {
		cancelButton.setEnabled(true);
		actionButton.setEnabled(false);
	}
}