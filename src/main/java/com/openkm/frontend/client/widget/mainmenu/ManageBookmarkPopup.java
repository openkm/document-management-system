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

package com.openkm.frontend.client.widget.mainmenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTBookmark;
import com.openkm.frontend.client.service.OKMBookmarkService;
import com.openkm.frontend.client.service.OKMBookmarkServiceAsync;
import com.openkm.frontend.client.util.Util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ManageBookmarkPopup
 *
 * @author jllort
 *
 */
public class ManageBookmarkPopup extends DialogBox {

	private final OKMBookmarkServiceAsync bookmarkService = (OKMBookmarkServiceAsync) GWT.create(OKMBookmarkService.class);

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private Button cancelButton;
	private Button deleteButton;
	private Button updateButton;
	private FlexTable table;
	private FlexTable tableBookmark;
	private ScrollPanel scrollPanel;
	private ScrollPanel scrollPanelBookmark;
	private TextBox textBox;
	private Map<String, GWTBookmark> bookmarkMap = new HashMap<String, GWTBookmark>();
	private int selectedRow = -1;
	private int columns = 3;

	/**
	 * ManageBookmarkPopup
	 */
	public ManageBookmarkPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		vPanel = new VerticalPanel();
		textBox = new TextBox();
		textBox.setStyleName("okm-Input");
		textBox.setVisibleLength(40);
		textBox.setMaxLength(90);
		textBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if ((char) KeyCodes.KEY_ENTER == event.getNativeKeyCode()) {
					if (textBox.getText().length() > 0) {
						rename(Integer.parseInt(table.getText(selectedRow, 2)), textBox.getText());
					}

				} else if ((char) KeyCodes.KEY_ESCAPE == event.getNativeKeyCode()) {
					tableBookmark.setHTML(0, 1, table.getText(selectedRow, 1));
					deleteButton.setEnabled(true);
					updateButton.setEnabled(true);
				}
			}
		});

		cancelButton = new Button(Main.i18n("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.topPanel.mainMenu.bookmark.getAll(); // Refreshing menu after edit bookmarks
				hide();
			}
		}
		);
		cancelButton.setStyleName("okm-NoButton");

		deleteButton = new Button(Main.i18n("button.delete"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (selectedRow >= 0) {
					remove(Integer.parseInt(table.getText(selectedRow, 2)));
				}
			}
		}
		);
		deleteButton.setStyleName("okm-DeleteButton");
		deleteButton.setEnabled(false);

		updateButton = new Button(Main.i18n("button.update"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (selectedRow >= 0) {
					textBox.setText(table.getHTML(selectedRow, 1));
					tableBookmark.setWidget(0, 1, textBox);
					updateButton.setEnabled(false);
					deleteButton.setEnabled(false);
					textBox.setFocus(true);
				}
			}
		}
		);
		updateButton.setStyleName("okm-ChangeButton");
		updateButton.setEnabled(false);

		table = new FlexTable();
		table.setBorderWidth(0);
		table.setCellSpacing(0);
		table.setCellSpacing(0);
		table.setWidth("100%");
		table.addStyleName("okm-DisableSelect");

		table.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Mark selected row or orders rows if header row (0) is clicked 
				// And row must be other than the selected one
				int row = table.getCellForEvent(event).getRowIndex();
				if (row != selectedRow) {
					styleRow(selectedRow, false);
					styleRow(row, true);
					selectedRow = row;
					if (bookmarkMap.containsKey(table.getText(row, 2))) {
						GWTBookmark bookmark = (GWTBookmark) bookmarkMap.get(table.getText(row, 2));
						tableBookmark.setHTML(0, 1, bookmark.getName());
						tableBookmark.setHTML(0, 2, table.getText(row, 2));
						tableBookmark.getFlexCellFormatter().setVisible(0, 2, false);
						tableBookmark.setHTML(1, 1, bookmark.getPath());
						if (bookmark.getType().equals(Bookmark.BOOKMARK_DOCUMENT)) {
							tableBookmark.setHTML(2, 1, Main.i18n("bookmark.type.document"));
						} else if (bookmark.getType().equals(Bookmark.BOOKMARK_FOLDER)) {
							tableBookmark.setHTML(2, 1, Main.i18n("bookmark.type.folder"));
						}
					}
					deleteButton.setEnabled(true);
					updateButton.setEnabled(true);
				}
			}
		});

		scrollPanel = new ScrollPanel(table);
		scrollPanel.setSize("380px", "150px");
		scrollPanel.setStyleName("okm-Bookmark-Panel");

		// Selected Bookmark data
		tableBookmark = new FlexTable();
		tableBookmark.setBorderWidth(0);
		tableBookmark.setCellSpacing(0);
		tableBookmark.setCellSpacing(0);
		tableBookmark.setWidth("100%");

		tableBookmark.setHTML(0, 0, "<b>" + Main.i18n("bookmark.name") + "</b>");
		tableBookmark.setHTML(0, 1, "");
		tableBookmark.setHTML(1, 0, "<b>" + Main.i18n("bookmark.path") + "</b>");
		tableBookmark.setHTML(1, 1, "");
		tableBookmark.setHTML(2, 0, "<b>" + Main.i18n("bookmark.type") + "</b>");
		tableBookmark.setHTML(2, 1, "");

		tableBookmark.getCellFormatter().setWidth(0, 0, "15%");
		tableBookmark.getCellFormatter().setWidth(0, 1, "15%");
		tableBookmark.getCellFormatter().setWidth(0, 2, "70%");

		// Set no wrap
		CellFormatter cellFormatter = tableBookmark.getCellFormatter();
		for (int i = 0; i < columns; i++) {
			cellFormatter.setWordWrap(0, i, false);
			cellFormatter.setWordWrap(1, i, false);
			cellFormatter.setWordWrap(2, i, false);
		}

		scrollPanelBookmark = new ScrollPanel(tableBookmark);
		scrollPanelBookmark.setWidth("380px");
		scrollPanelBookmark.setStyleName("okm-Bookmark-Panel");
		scrollPanelBookmark.setAlwaysShowScrollBars(false);

		hPanel = new HorizontalPanel();
		hPanel.add(deleteButton);
		hPanel.add(new HTML("&nbsp;&nbsp;"));
		hPanel.add(updateButton);

		vPanel.add(new HTML("<br>"));
		vPanel.add(scrollPanelBookmark);
		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));
		vPanel.add(scrollPanel);
		vPanel.add(new HTML("<br>"));
		vPanel.add(cancelButton);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(scrollPanelBookmark, HorizontalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HorizontalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(scrollPanel, HorizontalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(cancelButton, HorizontalPanel.ALIGN_CENTER);

		vPanel.setWidth("100%");

		center();
		hide();
		setWidget(vPanel);
	}

	/**
	 * Callback get all
	 */
	final AsyncCallback<List<GWTBookmark>> callbackGetAll = new AsyncCallback<List<GWTBookmark>>() {
		public void onSuccess(List<GWTBookmark> result) {
			int row = table.getRowCount();
			bookmarkMap = new HashMap<String, GWTBookmark>();

			for (Iterator<GWTBookmark> it = result.iterator(); it.hasNext(); ) {
				GWTBookmark bookmark = (GWTBookmark) it.next();
				bookmarkMap.put(String.valueOf(bookmark.getId()), bookmark);

				String icon = "";
				if (bookmark.getType().equals(Bookmark.BOOKMARK_DOCUMENT)) {
					icon = "img/icon/menu/document_bookmark.gif";
				} else if (bookmark.getType().equals(Bookmark.BOOKMARK_FOLDER)) {
					icon = "img/icon/menu/folder_bookmark.gif";
				}

				table.setHTML(row, 0, Util.imageHTML(icon));
				table.setHTML(row, 1, bookmark.getName());
				table.setHTML(row, 2, String.valueOf(bookmark.getId()));
				table.getRowFormatter().setStyleName(row, "okm-Table-Row");
				table.getCellFormatter().setWidth(row, 0, "25px");
				table.getCellFormatter().setVisible(row, 2, false);
				setRowWordWarp(row, 2, false);

				if (row == 0) {
					tableBookmark.setHTML(0, 1, bookmark.getName());
					tableBookmark.setHTML(0, 2, String.valueOf(bookmark.getId()));
					tableBookmark.setHTML(1, 1, bookmark.getPath());
					tableBookmark.setHTML(2, 1, bookmark.getType());
					if (bookmark.getType().equals(Bookmark.BOOKMARK_DOCUMENT)) {
						tableBookmark.setHTML(2, 1, Main.i18n("bookmark.type.document"));
					} else if (bookmark.getType().equals(Bookmark.BOOKMARK_FOLDER)) {
						tableBookmark.setHTML(2, 1, Main.i18n("bookmark.type.folder"));
					}
					tableBookmark.getCellFormatter().setVisible(0, 2, false);
					deleteButton.setEnabled(true);
					updateButton.setEnabled(true);
					styleRow(row, true);
					selectedRow = row;
				}

				row++;
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getAll", caught);
		}
	};

	/**
	 * Callback remove
	 */
	final AsyncCallback<Object> callbackRemove = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			if (selectedRow >= 0) {
				bookmarkMap.remove(table.getText(selectedRow, 2));
				table.removeRow(selectedRow);
				if (table.getRowCount() > 0) {
					if (selectedRow != 0) {
						selectedRow--;
						styleRow(selectedRow, true);
					} else {
						styleRow(selectedRow, true);
					}
					GWTBookmark bookmark = (GWTBookmark) bookmarkMap.get(table.getText(selectedRow, 2));
					tableBookmark.setHTML(0, 1, bookmark.getName());
					tableBookmark.setHTML(0, 2, String.valueOf(bookmark.getId()));
					tableBookmark.setHTML(1, 1, bookmark.getPath());
					tableBookmark.setHTML(2, 1, bookmark.getType());
					if (bookmark.getType().equals(Bookmark.BOOKMARK_DOCUMENT)) {
						tableBookmark.setHTML(2, 1, Main.i18n("bookmark.type.document"));
					} else if (bookmark.getType().equals(Bookmark.BOOKMARK_FOLDER)) {
						tableBookmark.setHTML(2, 1, Main.i18n("bookmark.type.folder"));
					}
				} else {
					deleteButton.setEnabled(false);
					updateButton.setEnabled(false);
					selectedRow = -1;
				}
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("remove", caught);
		}
	};

	/**
	 * Callback rename
	 */
	final AsyncCallback<GWTBookmark> callbackRename = new AsyncCallback<GWTBookmark>() {
		public void onSuccess(GWTBookmark result) {
			if (selectedRow >= 0) {
				bookmarkMap.remove(table.getText(selectedRow, 2));
				bookmarkMap.put(table.getText(selectedRow, 2), result);
				tableBookmark.setHTML(0, 1, result.getName());
				table.setHTML(selectedRow, 1, result.getName());
			}
			deleteButton.setEnabled(true);
			updateButton.setEnabled(true);
		}

		public void onFailure(Throwable caught) {
			deleteButton.setEnabled(true);
			updateButton.setEnabled(true);
			Main.get().showError("rename", caught);
		}
	};

	/**
	 * Gets the bookmark list from the server
	 *
	 */
	public void getAll() {
		bookmarkService.getAll(callbackGetAll);
	}

	/**
	 * Remove bookmark 
	 *
	 * @param id
	 */
	private void remove(int id) {
		bookmarkService.remove(id, callbackRemove);
	}

	/**
	 * Rename bookmark 
	 *
	 * @param id
	 * @param newName
	 */
	private void rename(int id, String newName) {
		bookmarkService.rename(id, newName, callbackRename);
	}

	/**
	 * Removes all rows
	 */
	private void removeAll() {
		bookmarkMap = new HashMap<String, GWTBookmark>();
		while (table.getRowCount() > 0) {
			table.removeRow(0);
		}
	}

	/**
	 * Show the popup
	 */
	public void showPopup() {
		setText(Main.i18n("bookmark.edit.label"));
		tableBookmark.setHTML(0, 1, "");
		tableBookmark.setHTML(1, 1, "");
		tableBookmark.setHTML(2, 1, "");
		deleteButton.setEnabled(false);
		updateButton.setEnabled(false);

		selectedRow = -1;
		removeAll();
		getAll();
		center();
	}

	/**
	 * Change the style row selected or unselected
	 *
	 * @param row The row afected
	 * @param selected Indicates selected unselected row
	 */
	private void styleRow(int row, boolean selected) {
		if (row >= 0) {
			if (selected) {
				table.getRowFormatter().addStyleName(row, "okm-Table-SelectedRow");
			} else {
				table.getRowFormatter().removeStyleName(row, "okm-Table-SelectedRow");
			}
		}
	}

	/**
	 * Set the WordWarp for all the row cells
	 *
	 * @param row The row cell
	 * @param columns Number of row columns
	 * @param warp
	 */
	private void setRowWordWarp(int row, int columns, boolean warp) {
		CellFormatter cellFormatter = table.getCellFormatter();
		for (int i = 0; i < columns; i++) {
			cellFormatter.setWordWrap(row, i, false);
		}
	}

	/**
	 * Refreshing language
	 */
	public void langRefresh() {
		setText(Main.i18n("bookmark.edit.label"));
		tableBookmark.setHTML(0, 0, "<b>" + Main.i18n("bookmark.name") + "</b>");
		tableBookmark.setHTML(1, 0, "<b>" + Main.i18n("bookmark.path") + "</b>");
		tableBookmark.setHTML(2, 0, "<b>" + Main.i18n("bookmark.type") + "</b>");
		cancelButton.setHTML(Main.i18n("button.close"));
		deleteButton.setHTML(Main.i18n("button.delete"));
		updateButton.setHTML(Main.i18n("button.update"));
	}
}

