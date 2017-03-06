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

package com.openkm.frontend.client.widget.searchin;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;

/**
 * FolderSelectPopup
 *
 * @author jllort
 *
 */
public class FolderSelectPopup extends DialogBox {

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private HorizontalPanel hListPanel;
	private ScrollPanel scrollDirectoryPanel;
	private VerticalPanel verticalDirectoryPanel;
	private FolderSelectTree folderSelectTree;
	private Button cancelButton;
	private Button actionButton;
	private boolean categories = false;

	/**
	 * FolderSelectPopup
	 */
	public FolderSelectPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		vPanel = new VerticalPanel();
		vPanel.setWidth("450px");
		vPanel.setHeight("300px");
		hPanel = new HorizontalPanel();
		hListPanel = new HorizontalPanel();
		hListPanel.setWidth("440px");

		scrollDirectoryPanel = new ScrollPanel();
		scrollDirectoryPanel.setSize("440px", "250px");
		scrollDirectoryPanel.setStyleName("okm-Popup-text");
		verticalDirectoryPanel = new VerticalPanel();
		verticalDirectoryPanel.setSize("100%", "100%");
		folderSelectTree = new FolderSelectTree();
		folderSelectTree.setSize("100%", "100%");

		verticalDirectoryPanel.add(folderSelectTree);
		scrollDirectoryPanel.add(verticalDirectoryPanel);

		cancelButton = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		actionButton = new Button(Main.i18n("button.select"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (categories) {
					setRepositoryPath(folderSelectTree.getActualPath(), folderSelectTree.getActualUuuid(), false);
				} else {
					setRepositoryPath(folderSelectTree.getActualPath(), false);
				}
			}
		});

		vPanel.add(new HTML("<br>"));
		vPanel.add(hListPanel);
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

		vPanel.setCellHorizontalAlignment(hListPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(scrollDirectoryPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHeight(scrollDirectoryPanel, "250px");

		cancelButton.setStyleName("okm-Input");
		actionButton.setStyleName("okm-Input");

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * Sets the repository path
	 */
	public void setRepositoryPath(String actualPath, String Uuid, boolean refresh) {
		Main.get().mainPanel.search.searchBrowser.searchIn.searchAdvanced.categoryUuid = Uuid;
		Main.get().mainPanel.search.searchBrowser.searchIn.searchAdvanced.categoryPath.setText(actualPath.substring(16)); // Removes /okm:categories;
		hide();
	}

	/**
	 * Sets the repository path
	 */
	public void setRepositoryPath(String actualPath, boolean refresh) {
		Main.get().mainPanel.search.searchBrowser.searchIn.searchAdvanced.path.setText(actualPath);
		hide();
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		if (categories) {
			setText(Main.i18n("search.category.filter"));
		} else {
			setText(Main.i18n("search.folder.filter"));
		}

		cancelButton.setText(Main.i18n("button.cancel"));
		actionButton.setText(Main.i18n("button.select"));
	}

	/**
	 * Shows the popup 
	 */
	public void show(boolean categories) {
		this.categories = categories;
		int left = (Window.getClientWidth() - 450) / 2;
		int top = (Window.getClientHeight() - 300) / 2;
		setPopupPosition(left, top);

		if (categories) {
			setText(Main.i18n("search.category.filter"));
		} else {
			setText(Main.i18n("search.folder.filter"));
		}

		// Resets to initial tree value
		folderSelectTree.reset(categories);
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
}