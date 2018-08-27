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

package com.openkm.frontend.client.widget.searchresult;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.*;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.form.FormManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Extends ScrollTable functionalities 
 *
 * @author jllort
 *
 */
public class ExtendedScrollTable extends ScrollTable {

	// Holds the data rows of the table this is a list of RowData Object
	public Map<Integer, GWTQueryResult> data = new HashMap<Integer, GWTQueryResult>();
	private int mouseX = 0;
	private int mouseY = 0;
	private int dataIndexValue = 0;
	private boolean panelSelected = false; // Indicates if panel is selected
	private FixedWidthGrid dataTable;
	private FixedWidthFlexTable headerTable;
	private ExtendedColumnSorter columnSorter;
	private FormManager formManager;
	// Columns
	private GWTProfileFileBrowser profileFileBrowser;
	public int colDataIndex = 0;

	/**
	 * ExtendedScrollTable
	 */
	public ExtendedScrollTable(FixedWidthGrid dataTable, FixedWidthFlexTable headerTable, ScrollTableImages scrollTableImages) {
		super(dataTable, headerTable, scrollTableImages);
		this.dataTable = dataTable;
		this.headerTable = headerTable;

		formManager = new FormManager(null); // Used to draw extended columns

		dataTable.setSelectionPolicy(SelectionGrid.SelectionPolicy.ONE_ROW);
		setResizePolicy(ResizePolicy.FILL_WIDTH);
		setScrollPolicy(ScrollPolicy.BOTH);

		columnSorter = new ExtendedColumnSorter();
		dataTable.setColumnSorter(columnSorter);

		// Sets some events
		DOM.sinkEvents(getDataWrapper(), Event.ONDBLCLICK | Event.ONMOUSEDOWN);
	}

	/**
	 * isSorted
	 *
	 * @return
	 */
	public boolean isSorted() {
		return columnSorter.isSorted();
	}

	/**
	 * refreshSort
	 */
	public void refreshSort() {
		columnSorter.refreshSort();
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.EventListener#onBrowserEvent(com.google.gwt.user.client.Event)
	 */
	public void onBrowserEvent(Event event) {
		boolean headerFired = false; // Controls when event is fired by header

		// Case targe event is header must disable drag & drop
		if (headerTable.getEventTargetCell(event) != null) {
			headerFired = true;
		}

		// Selects the panel
		setSelectedPanel(true);

		// When de button mouse is released
		mouseX = DOM.eventGetClientX(event);
		mouseY = DOM.eventGetClientY(event);

		// On double click not sends event to onCellClicked across super.onBrowserEvent();
		if (DOM.eventGetType(event) == Event.ONDBLCLICK) {
			// Disables the event propagation the sequence is:
			// Two time entry onCellClicked before entry on onBrowserEvent and disbles the
			// Tree onCellClicked that produces inconsistence error refreshing
			DOM.eventCancelBubble(event, true);
			if ((isDocumentSelected() || isAttachmentSelected()) && Main.get().workspaceUserProperties.getWorkspace().getAvailableOption().isDownloadOption()) {
				Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.downloadDocument();
			}

		} else if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
			switch (DOM.eventGetButton(event)) {
				case Event.BUTTON_RIGHT:
					if (!headerFired) {
						if (isDocumentSelected() || isAttachmentSelected()) {
							Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.menuPopup.menu.checkMenuOptionPermissions(getDocument());
						} else if (isFolderSelected()) {
							Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.menuPopup.menu.checkMenuOptionPermissions(getFolder());
						} else if (isMailSelected()) {
							Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.menuPopup.menu.checkMenuOptionPermissions(getMail());
						}
						Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.menuPopup.menu.evaluateMenuOptions();
						Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.showMenu();
						DOM.eventPreventDefault(event); // Prevent to fire event to browser
					}
					break;
				default:
					break;
			}
		}

		super.onBrowserEvent(event);
	}

	/**
	 * Sets the selected panel value
	 *
	 * @param selected The selected panel value
	 */
	public void setSelectedPanel(boolean selected) {
		if (selected) {
			Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.addStyleName("okm-PanelSelected");
			Main.get().mainPanel.search.historySearch.searchSaved.setSelectedPanel(false);
			Main.get().mainPanel.search.historySearch.userNews.setSelectedPanel(false);
		} else {
			Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.removeStyleName("okm-PanelSelected");
		}
		panelSelected = selected;
	}

	/**
	 * Is panel selected
	 *
	 * @return The panel selected value
	 */
	public boolean isPanelSelected() {
		return panelSelected;
	}

	/**
	 * Gets the X position on mouse click
	 *
	 * @return The x position on mouse click
	 */
	public int getMouseX() {
		return mouseX;
	}

	/**
	 * Gets the Y position on mouse click
	 *
	 * @return The y position on mouse click
	 */
	public int getMouseY() {
		return mouseY;
	}

	/**
	 * Gets the selected row
	 *
	 * @return The selected row
	 */
	public int getSelectedRow() {
		int selectedRow = -1;

		if (!dataTable.getSelectedRows().isEmpty()) {
			selectedRow = ((Integer) dataTable.getSelectedRows().iterator().next()).intValue();
		}

		Log.debug("ExtendedScrollPanel selectedRow:" + selectedRow);
		return selectedRow;
	}

	/**
	 * Resets the values
	 */
	public void reset() {
		mouseX = 0;
		mouseY = 0;
		dataIndexValue = 0;

		// Only resets rows table the header is never reset
		data = new HashMap<Integer, GWTQueryResult>();
	}

	/**
	 * Adds a document to the panel
	 *
	 * @param doc The doc to add
	 */
	public void addRow(GWTQueryResult gwtQueryResult) {
		if (gwtQueryResult.getDocument() != null || gwtQueryResult.getAttachment() != null) {
			addDocumentRow(gwtQueryResult, new Score(gwtQueryResult.getScore()));
		} else if (gwtQueryResult.getFolder() != null) {
			addFolderRow(gwtQueryResult, new Score(gwtQueryResult.getScore()));
		} else if (gwtQueryResult.getMail() != null) {
			addMailRow(gwtQueryResult, new Score(gwtQueryResult.getScore()));
		}
	}

	/**
	 * Adding document row
	 *
	 * @param gwtQueryResult Query result
	 * @param score Document score
	 */
	private void addDocumentRow(GWTQueryResult gwtQueryResult, Score score) {
		int col = 0;
		int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);

		GWTDocument doc = new GWTDocument();
		if (gwtQueryResult.getDocument() != null) {
			doc = gwtQueryResult.getDocument();
		} else if (gwtQueryResult.getAttachment() != null) {
			doc = gwtQueryResult.getAttachment();
		}

		// Sets folder object
		data.put(new Integer(dataIndexValue), gwtQueryResult);

		// Score is always visible
		dataTable.setHTML(rows, col++, score.getHTML());
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_LEFT);

		if (profileFileBrowser.isIconVisible()) {
			dataTable.setHTML(rows, col, dataTable.getHTML(rows, col) + Util.mimeImageHTML(doc.getMimeType()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_RIGHT);
		}
		if (profileFileBrowser.isNameVisible()) {
			Anchor anchor = new Anchor();
			anchor.setHTML(doc.getName());
			anchor.setStyleName("okm-Hyperlink");

			// On attachemt case must remove last folder path, because it's internal usage not for visualization
			String path = "";
			if (doc.isAttachment()) {
				anchor.setTitle(Util.getParent(doc.getParentPath()));
				path = doc.getParentPath();
			} else {
				anchor.setTitle(doc.getParentPath());
				path = doc.getPath();
			}
			final String docPath = path;
			anchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					CommonUI.openPath(Util.getParent(docPath), docPath);
				}
			});

			dataTable.setWidget(rows, col, anchor);
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}
		if (profileFileBrowser.isSizeVisible()) {
			dataTable.setHTML(rows, 3, Util.formatSize(doc.getActualVersion().getSize()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}
		if (profileFileBrowser.isLastModifiedVisible()) {
			DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
			dataTable.setHTML(rows, col, dtf.format(doc.getLastModified()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}
		if (profileFileBrowser.isAuthorVisible()) {
			dataTable.setHTML(rows, col, doc.getActualVersion().getUser().getUsername());
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}
		if (profileFileBrowser.isVersionVisible()) {
			dataTable.setHTML(rows, col, doc.getActualVersion().getName());
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}
		if (profileFileBrowser.getColumn0() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(doc.getColumn0()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn1() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(doc.getColumn1()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn2() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(doc.getColumn2()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn3() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(doc.getColumn3()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn4() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(doc.getColumn4()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn5() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(doc.getColumn5()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn6() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(doc.getColumn6()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn7() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(doc.getColumn7()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn8() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(doc.getColumn8()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn9() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(doc.getColumn9()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}
		dataTable.setHTML(rows, col, "" + (dataIndexValue++));
		dataTable.getCellFormatter().setVisible(rows, col, false);

		for (int i = 0; i < col; i++) {
			dataTable.getCellFormatter().addStyleName(rows, i, "okm-DisableSelect");
		}
	}

	/**
	 * Adding folder
	 *
	 * @param gwtQueryResult Query result
	 * @param score The folder score
	 */
	private void addFolderRow(GWTQueryResult gwtQueryResult, Score score) {
		int col = 0;
		int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);

		final GWTFolder folder = gwtQueryResult.getFolder();

		// Sets folder object
		data.put(new Integer(dataIndexValue), gwtQueryResult);

		// Score is always visible 
		dataTable.setHTML(rows, col, score.getHTML());
		dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);

		if (profileFileBrowser.isIconVisible()) {
			// Looks if must change icon on parent if now has no childs and properties with user security atention
			if ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
				if (folder.isHasChildren()) {
					dataTable.setHTML(rows, col, Util.imageItemHTML("img/menuitem_childs.gif"));
				} else {
					dataTable.setHTML(rows, col, Util.imageItemHTML("img/menuitem_empty.gif"));
				}
			} else {
				if (folder.isHasChildren()) {
					dataTable.setHTML(rows, col, Util.imageItemHTML("img/menuitem_childs_ro.gif"));
				} else {
					dataTable.setHTML(rows, col, Util.imageItemHTML("img/menuitem_empty_ro.gif"));
				}
			}
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_RIGHT);
		}

		if (profileFileBrowser.isNameVisible()) {
			Anchor anchor = new Anchor();
			anchor.setHTML(folder.getName());
			anchor.setTitle(folder.getParentPath());
			anchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					CommonUI.openPath(folder.getPath(), "");
				}
			});
			anchor.setStyleName("okm-Hyperlink");
			dataTable.setWidget(rows, col, anchor);
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}
		if (profileFileBrowser.isSizeVisible()) {
			dataTable.setHTML(rows, col, "&nbsp;");
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}
		if (profileFileBrowser.isLastModifiedVisible()) {
			DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
			dataTable.setHTML(rows, col, dtf.format(folder.getCreated()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}
		if (profileFileBrowser.isAuthorVisible()) {
			dataTable.setHTML(rows, col, folder.getUser().getUsername());
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}
		if (profileFileBrowser.isVersionVisible()) {
			dataTable.setHTML(rows, col, "&nbsp;");
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}
		if (profileFileBrowser.getColumn0() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(folder.getColumn0()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}
		if (profileFileBrowser.getColumn1() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(folder.getColumn1()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn2() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(folder.getColumn2()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn3() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(folder.getColumn3()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn4() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(folder.getColumn4()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn5() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(folder.getColumn5()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn6() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(folder.getColumn6()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn7() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(folder.getColumn7()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn8() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(folder.getColumn8()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn9() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(folder.getColumn9()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}
		dataTable.setHTML(rows, col, "" + (dataIndexValue++));
		dataTable.getCellFormatter().setVisible(rows, col, false);

		for (int i = 0; i < col; i++) {
			dataTable.getCellFormatter().addStyleName(rows, i, "okm-DisableSelect");
		}
	}

	/**
	 * Adding mail
	 *
	 * @param gwtQueryResult Query result
	 * @param score The mail score
	 */
	private void addMailRow(GWTQueryResult gwtQueryResult, Score score) {
		int col = 0;
		int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);

		final GWTMail mail = gwtQueryResult.getMail();

		// Sets folder object
		data.put(new Integer(dataIndexValue), gwtQueryResult);

		// Score is always visible
		dataTable.setHTML(rows, col, score.getHTML());
		dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);

		if (profileFileBrowser.isIconVisible()) {
			if (mail.getAttachments().size() > 0) {
				dataTable.setHTML(rows, col, Util.imageItemHTML("img/email_attach.gif"));
			} else {
				dataTable.setHTML(rows, col, Util.imageItemHTML("img/email.gif"));
			}
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_RIGHT);
		}
		if (profileFileBrowser.isNameVisible()) {
			Anchor anchor = new Anchor();
			anchor.setHTML(mail.getSubject());
			anchor.setTitle(mail.getParentPath());
			anchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					String docPath = mail.getPath();
					CommonUI.openPath(Util.getParent(docPath), docPath);
				}
			});
			anchor.setStyleName("okm-Hyperlink");
			dataTable.setWidget(rows, col, anchor);
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}
		if (profileFileBrowser.isSizeVisible()) {
			dataTable.setHTML(rows, col, Util.formatSize(mail.getSize()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}
		if (profileFileBrowser.isLastModifiedVisible()) {
			DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
			dataTable.setHTML(rows, col, dtf.format(mail.getReceivedDate()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}
		if (profileFileBrowser.isAuthorVisible()) {
			dataTable.setHTML(rows, col, mail.getFrom());
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}
		if (profileFileBrowser.isVersionVisible()) {
			dataTable.setHTML(rows, col, "&nbsp;");
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}
		if (profileFileBrowser.getColumn0() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(mail.getColumn0()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn1() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(mail.getColumn1()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn2() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(mail.getColumn2()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn3() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(mail.getColumn3()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn4() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(mail.getColumn4()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn5() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(mail.getColumn5()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn6() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(mail.getColumn6()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn7() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(mail.getColumn7()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn8() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(mail.getColumn8()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn9() != null) {
			dataTable.setWidget(rows, col, formManager.getDrawFormElement(mail.getColumn9()));
			dataTable.getCellFormatter().setHorizontalAlignment(rows, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}
		dataTable.setHTML(rows, col, "" + (dataIndexValue++));
		dataTable.getCellFormatter().setVisible(rows, col, false);

		for (int i = 0; i < col; i++) {
			dataTable.getCellFormatter().addStyleName(rows, i, "okm-DisableSelect");
		}
	}

	/**
	 * Sets the selected row
	 *
	 * @param row The row number
	 */
	public void setSelectedRow(int row) {
		Log.debug("ExtendedScrollPanel setSelectedRow:" + row);
		dataTable.selectRow(row, true);
	}

	/**
	 * Gets a actual document object row
	 *
	 * @return
	 */
	public GWTDocument getDocument() {
		if (isDocumentSelected()) {
			return ((GWTQueryResult) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), colDataIndex)))).getDocument();
		} else {
			return null;
		}
	}

	/**
	 * Gets a actual attachment object row
	 *
	 * @return
	 */
	public GWTDocument getAttachment() {
		if (isAttachmentSelected()) {
			return ((GWTQueryResult) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), colDataIndex)))).getAttachment();
		} else {
			return null;
		}
	}

	/**
	 * Gets a actual document object row
	 *
	 * @return
	 */
	public GWTFolder getFolder() {
		if (isFolderSelected()) {
			return ((GWTQueryResult) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), colDataIndex)))).getFolder();
		} else {
			return null;
		}
	}

	/**
	 * Gets a actual mail object row
	 *
	 * @return
	 */
	public GWTMail getMail() {
		if (isMailSelected()) {
			return ((GWTQueryResult) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), colDataIndex)))).getMail();
		} else {
			return null;
		}
	}

	/**
	 * Return true or false if actual selected row is document
	 *
	 * @return True or False if actual row is document type
	 */
	public boolean isDocumentSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (((GWTQueryResult) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), colDataIndex)))).getDocument() != null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Return true or false if actual selected row is attachment
	 *
	 * @return True or False if actual row is attachment type
	 */
	public boolean isAttachmentSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (((GWTQueryResult) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), colDataIndex)))).getAttachment() != null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Return true or false if actual selected row is mail
	 *
	 * @return True or False if actual row is mail type
	 */
	public boolean isFolderSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (((GWTQueryResult) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), colDataIndex)))).getFolder() != null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Return true or false if actual selected row is mail
	 *
	 * @return True or False if actual row is mail type
	 */
	public boolean isMailSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (((GWTQueryResult) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), colDataIndex)))).getMail() != null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * setProfileFileBrowser
	 *
	 * @param profileFileBrowser
	 */
	public void setProfileFileBrowser(GWTProfileFileBrowser profileFileBrowser) {
		this.profileFileBrowser = profileFileBrowser;
		columnSorter.setProfileFileBrowser(profileFileBrowser);
	}

	/**
	 * setDataColumn
	 *
	 * @param dataColumn
	 */
	public void setColDataIndex(int colDataIndex) {
		this.colDataIndex = colDataIndex;
		columnSorter.setColDataIndex(colDataIndex);
	}
}