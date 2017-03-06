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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.service.OKMMassiveService;
import com.openkm.frontend.client.service.OKMMassiveServiceAsync;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Categories popup
 *
 * @author jllort
 */
public class CategoriesPopup extends DialogBox {
	private final OKMMassiveServiceAsync massiveService = (OKMMassiveServiceAsync) GWT.create(OKMMassiveService.class);

	private FlexTable table;
	private CellFormatter cellFormatter;
	private VerticalPanel vPanel;
	private ScrollPanel scrollDirectoryPanel;
	private VerticalPanel verticalDirectoryPanel;
	private FolderSelectTree folderSelectTree;
	private Button add;
	private Button close;
	private FlexTable tableSubscribedCategories;
	private Collection<GWTFolder> assignedCategories;
	private boolean remove = true;
	private Status status;

	/**
	 * CategoriesPopup
	 */
	public CategoriesPopup() {
		// Establishes auto-close when click outside
		super(false, true);
		setText(Main.i18n("category.add"));

		// Status
		status = new Status(this);
		status.setStyleName("okm-StatusPopup");

		table = new FlexTable();
		tableSubscribedCategories = new FlexTable();
		assignedCategories = new ArrayList<GWTFolder>();
		cellFormatter = table.getCellFormatter(); // Gets the cell formatter
		table.setWidth("100%");
		table.setCellPadding(0);
		table.setCellSpacing(2);

		// Categories
		vPanel = new VerticalPanel();
		vPanel.setWidth("470px");
		vPanel.setHeight("175px");

		scrollDirectoryPanel = new ScrollPanel();
		scrollDirectoryPanel.setSize("460px", "150px");
		scrollDirectoryPanel.setStyleName("okm-Popup-text");
		verticalDirectoryPanel = new VerticalPanel();
		verticalDirectoryPanel.setSize("100%", "100%");
		folderSelectTree = new FolderSelectTree();
		folderSelectTree.setSize("100%", "100%");

		verticalDirectoryPanel.add(folderSelectTree);
		scrollDirectoryPanel.add(verticalDirectoryPanel);

		add = new Button(Main.i18n("button.add"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addCategory(folderSelectTree.getCategory());
			}
		});
		add.setEnabled(false);

		close = new Button(Main.i18n("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
					Main.get().mainPanel.topPanel.toolBar.executeRefresh();
				}
				hide();
			}
		});

		vPanel.add(scrollDirectoryPanel);
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(close);
		hPanel.add(new HTML("&nbsp;"));
		hPanel.add(add);
		vPanel.add(hPanel);

		vPanel.setCellHorizontalAlignment(scrollDirectoryPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(hPanel, HasAlignment.ALIGN_MIDDLE);
		vPanel.setCellHeight(scrollDirectoryPanel, "150px");
		vPanel.setCellHeight(hPanel, "25px");

		table.setWidget(0, 0, vPanel);
		table.getFlexCellFormatter().setColSpan(1, 0, 2);
		cellFormatter.setHorizontalAlignment(1, 0, HasAlignment.ALIGN_CENTER);

		table.setHTML(1, 0, "&nbsp;<b>" + Main.i18n("document.categories") + "</b>");
		table.getFlexCellFormatter().setColSpan(2, 0, 2);
		cellFormatter.setHorizontalAlignment(2, 0, HasAlignment.ALIGN_LEFT);

		table.setWidget(2, 0, tableSubscribedCategories);
		table.getFlexCellFormatter().setColSpan(3, 0, 2);
		cellFormatter.setHorizontalAlignment(3, 0, HasAlignment.ALIGN_LEFT);

		setRowWordWarp(0, 0, true, tableSubscribedCategories);

		table.setStyleName("okm-DisableSelect");
		close.setStyleName("okm-NoButton");
		add.setStyleName("okm-AddButton");
		tableSubscribedCategories.setStyleName("okm-DisableSelect");

		setWidget(table);
	}

	/**
	 * reset
	 */
	public void reset() {
		folderSelectTree.reset();
		assignedCategories = new ArrayList<GWTFolder>();
		tableSubscribedCategories.removeAllRows();
		if (!Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
				assignedCategories = Main.get().mainPanel.desktop.browser.fileBrowser.getDocument().getCategories();
			} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isFolderSelected()) {
				assignedCategories = Main.get().mainPanel.desktop.browser.fileBrowser.getFolder().getCategories();
			} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isMailSelected()) {
				assignedCategories = Main.get().mainPanel.desktop.browser.fileBrowser.getMail().getCategories();
			}
		}
	}

	/**
	 * addCategory document
	 */
	public void addCategory(GWTFolder category) {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			if (!existCategory(category.getUuid())) {
				assignedCategories.add(category);
				drawCategory(category, remove);

				status.setFlagCategories();
				massiveService.addCategory(Main.get().mainPanel.desktop.browser.fileBrowser.getAllSelectedPaths(),
						category.getUuid(), new AsyncCallback<Object>() {
							@Override
							public void onSuccess(Object result) {
								status.unsetFlagCategories();
							}

							@Override
							public void onFailure(Throwable caught) {
								status.unsetFlagCategories();
								Main.get().showError("addNote", caught);
							}
						});
			}
		} else {
			// Aqui hem de fer alguna cosa !!!!!!!!!
			if (!existCategory(category.getUuid())) {
				drawCategory(category, remove);

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
			}
		}
	}

	/**
	 * removeCategory document
	 */
	public void removeCategory(final String UUID) {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			status.setFlagRemoveCategories();
			massiveService.removeCategory(Main.get().mainPanel.desktop.browser.fileBrowser.getAllSelectedPaths(), UUID,
					new AsyncCallback<Object>() {
						@Override
						public void onSuccess(Object result) {
							for (GWTFolder fld : assignedCategories) {
								if (fld.getUuid().equals(UUID)) {
									assignedCategories.remove(fld);
									break;
								}
							}
							status.unsetFlagRemoveCategories();
						}

						@Override
						public void onFailure(Throwable caught) {
							status.unsetFlagRemoveCategories();
							Main.get().showError("addNote", caught);
						}
					});
		} else {
			// Filebrowser panel selected
			if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.removeCategory(UUID);
				} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isFolderSelected()) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.removeCategory(UUID);
				} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isMailSelected()) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mail.removeCategory(UUID);
				}
			} else {
				// Otherside tree panel is selected
				Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.removeCategory(UUID);
			}
		}
	}

	/**
	 * existCategory
	 *
	 * @param Uuid
	 * @return
	 */
	private boolean existCategory(String Uuid) {
		boolean found = false;
		for (GWTFolder fld : assignedCategories) {
			if (fld.getUuid().equals(Uuid)) {
				found = true;
				break;
			}
		}
		return found;
	}

	/**
	 * Enables or disables move button
	 *
	 * @param enable
	 */
	public void enable(boolean enable) {
		add.setEnabled(enable);
	}

	/**
	 * drawCategory
	 *
	 * @param category
	 */
	private void drawCategory(final GWTFolder category, boolean remove) {
		int row = tableSubscribedCategories.getRowCount();
		Anchor anchor = new Anchor();

		// Looks if must change icon on parent if now has no childs and properties with user security atention
		String path = category.getPath().substring(16); // Removes /okm:categories

		if (category.isHasChildren()) {
			anchor.setHTML(Util.imageItemHTML("img/menuitem_childs.gif", path, "top"));
		} else {
			anchor.setHTML(Util.imageItemHTML("img/menuitem_empty.gif", path, "top"));
		}

		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				CommonUI.openPath(category.getPath(), null);
			}
		});
		anchor.setStyleName("okm-KeyMap-ImageHover");

		Image delete = new Image(OKMBundleResources.INSTANCE.deleteIcon());
		delete.setStyleName("okm-KeyMap-ImageHover");
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				removeCategory(category.getUuid());
				tableSubscribedCategories.removeRow(tableSubscribedCategories.getCellForEvent(event).getRowIndex());
			}
		});

		tableSubscribedCategories.setWidget(row, 0, anchor);
		if (remove) {
			tableSubscribedCategories.setWidget(row, 1, delete);
		} else {
			tableSubscribedCategories.setWidget(row, 1, new HTML(""));
		}
		setRowWordWarp(row, 1, true, tableSubscribedCategories);
	}

	/**
	 * Set the WordWarp for all the row cells
	 *
	 * @param row The row cell
	 * @param columns Number of row columns
	 * @param warp
	 * @param table The table to change word wrap
	 */
	private void setRowWordWarp(int row, int columns, boolean warp, FlexTable table) {
		CellFormatter cellFormatter = table.getCellFormatter();
		for (int i = 0; i < columns; i++) {
			cellFormatter.setWordWrap(row, i, warp);
		}
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		setText(Main.i18n("category.add"));
	}
}