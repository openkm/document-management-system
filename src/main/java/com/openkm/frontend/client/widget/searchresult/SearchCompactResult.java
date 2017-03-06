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

import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollTableImages;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.*;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.ScrollTableHelper;
import com.openkm.frontend.client.util.Util;

/**
 * SearchCompactResult
 *
 * @author jllort
 *
 */
public class SearchCompactResult extends Composite {

	// Number of columns
	private int numberOfColumns = 0;

	public ExtendedScrollTable table;
	private FixedWidthFlexTable headerTable;
	private FixedWidthGrid dataTable;
	public MenuPopup menuPopup;
	private GWTProfileFileBrowser profileFileBrowser;

	/**
	 * SearchCompactResult
	 */
	public SearchCompactResult() {
		menuPopup = new MenuPopup();
		menuPopup.setStyleName("okm-MenuPopup");

		ScrollTableImages scrollTableImages = new ScrollTableImages() {
			public AbstractImagePrototype scrollTableAscending() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/sort_asc.gif");
					}

					public Image createImage() {
						return new Image("img/sort_asc.gif");
					}

					public String getHTML() {
						return "<img border=\"0\" src=\"img/sort_asc.gif\"/>";
					}
				};
			}

			public AbstractImagePrototype scrollTableDescending() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/sort_desc.gif");
					}

					public Image createImage() {
						return new Image("img/sort_desc.gif");
					}

					public String getHTML() {
						return "<img border=\"0\" src=\"img/sort_desc.gif\"/>";
					}
				};
			}

			public AbstractImagePrototype scrollTableFillWidth() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/fill_width.gif");
					}

					public Image createImage() {
						return new Image("img/fill_width.gif");
					}

					public String getHTML() {
						return "<img border=\"0\" src=\"img/fill_width.gif\"/>";
					}
				};
			}
		};

		headerTable = new FixedWidthFlexTable();
		dataTable = new FixedWidthGrid();
		table = new ExtendedScrollTable(dataTable, headerTable, scrollTableImages);
		table.setCellSpacing(0);
		table.setCellPadding(2);
		table.setSize("540px", "140px");

		table.addStyleName("okm-DisableSelect");
		table.addStyleName("okm-Input");

		initWidget(table);
	}

	/**
	 * Refreshing lang
	 */
	public void langRefresh() {
		int col = 0;
		headerTable.setHTML(0, col++, Main.i18n("search.result.score"));
		if (profileFileBrowser.isIconVisible()) {
			col++; // nothing to be translated here
		}
		if (profileFileBrowser.isNameVisible()) {
			headerTable.setHTML(0, col++, Main.i18n("search.result.name"));
		}
		if (profileFileBrowser.isSizeVisible()) {
			headerTable.setHTML(0, col++, Main.i18n("search.result.size"));
		}
		if (profileFileBrowser.isLastModifiedVisible()) {
			headerTable.setHTML(0, col++, Main.i18n("search.result.date.update"));
		}
		if (profileFileBrowser.isAuthorVisible()) {
			headerTable.setHTML(0, col++, Main.i18n("search.result.author"));
		}
		if (profileFileBrowser.isVersionVisible()) {
			headerTable.setHTML(0, col++, Main.i18n("search.result.version"));
		}
		menuPopup.langRefresh();
	}

	/**
	 * Removes all rows except the first
	 */
	public void removeAllRows() {
		// Purge all rows 
		while (dataTable.getRowCount() > 0) {
			dataTable.removeRow(0);
		}

		table.reset();
		table.getDataTable().resize(0, numberOfColumns);
	}

	/**
	 * Adds a document to the panel
	 *
	 * @param doc The doc to add
	 */
	public void addRow(GWTQueryResult gwtQueryResult) {
		table.addRow(gwtQueryResult);
	}

	/**
	 * Show the browser menu
	 */
	public void showMenu() {
		// The browser menu depends on actual view
		// Must substract top position from Y Screen Position
		menuPopup.setPopupPosition(table.getMouseX(), table.getMouseY());
		menuPopup.show();
	}

	/**
	 * Download document
	 */
	public void downloadDocument() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (table.isDocumentSelected()) {
				Util.downloadFileByUUID(getDocument().getUuid(), "");
			} else if (table.isAttachmentSelected()) {
				Util.downloadFileByUUID(getAttachment().getUuid(), "");
			}
		}
	}

	/**
	 * Open all folder path
	 */
	public void openAllFolderPath() {
		String docPath = "";
		String path = "";

		if (table.isDocumentSelected() || table.isAttachmentSelected()) {
			if (table.isAttachmentSelected()) {
				docPath = getAttachment().getParentPath();
			} else {
				docPath = getDocument().getPath();
			}

			path = Util.getParent(docPath);

		} else if (table.isFolderSelected()) {
			path = getFolder().getPath();

		} else if (table.isMailSelected()) {
			docPath = getMail().getPath();
			path = Util.getParent(docPath);
		}

		CommonUI.openPath(path, docPath);
		menuPopup.hide();
	}

	/**
	 * Gets a actual document object row
	 *
	 * @return The Document object value
	 */
	public GWTDocument getDocument() {
		//Row selected must be on table documents
		return table.getDocument();
	}

	/**
	 * Gets a actual attachment object row
	 *
	 * @return The Attachment object value
	 */
	public GWTDocument getAttachment() {
		//Row selected must be on table documents
		return table.getAttachment();
	}

	/**
	 * Gets a actual folder object row
	 *
	 * @return The folder object value
	 */
	public GWTFolder getFolder() {
		//Row selected must be on table documents
		return table.getFolder();
	}

	/**
	 * Gets a actual mail object row
	 *
	 * @return The mail object value
	 */
	public GWTMail getMail() {
		//Row selected must be on table documents
		return table.getMail();
	}

	/**
	 * Call Back get search
	 */
	final AsyncCallback<GWTQueryParams> callbackGetSearch = new AsyncCallback<GWTQueryParams>() {
		public void onSuccess(GWTQueryParams result) {
			GWTQueryParams gWTParams = result;
			Main.get().mainPanel.search.searchBrowser.searchIn.setSavedSearch(gWTParams);
			removeAllRows();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getSearch", caught);
		}
	};

	/**
	 * Indicates if panel is selected
	 *
	 * @return The value of panel ( selected )
	 */
	public boolean isPanelSelected() {
		return table.isPanelSelected();
	}

	/**
	 * Sets the selected panel value
	 *
	 * @param selected The select panel value
	 */
	public void setSelectedPanel(boolean selected) {
		table.setSelectedPanel(selected);
	}

	/**
	 * Fix width
	 */
	public void fixWidth() {
		table.fillWidth();
	}

	/**
	 * isSorted
	 */
	public boolean isSorted() {
		return table.isSorted();
	}

	/**
	 * refreshSort
	 */
	public void refreshSort() {
		table.refreshSort();
	}

	/**
	 * setProfileFileBrowser
	 *
	 * @param profileFileBrowser
	 */
	public void setProfileFileBrowser(GWTProfileFileBrowser profileFileBrowser) {
		this.profileFileBrowser = profileFileBrowser;

		int col = 0;
		// Relevance can not be hidden
		headerTable.setHTML(0, col, Main.i18n("search.result.score"));
		ScrollTableHelper.setColumnWidth(table, col, 80, ScrollTableHelper.FIXED);
		col++;


		if (profileFileBrowser.isIconVisible()) {
			headerTable.setHTML(0, col, "&nbsp;");
			ScrollTableHelper.setColumnWidth(table, col, Integer.parseInt(profileFileBrowser.getIconWidth()), ScrollTableHelper.FIXED);
			col++;
		}
		if (profileFileBrowser.isNameVisible()) {
			headerTable.setHTML(0, col, Main.i18n("search.result.name"));
			ScrollTableHelper.setColumnWidth(table, col, Integer.parseInt(profileFileBrowser.getNameWidth()), ScrollTableHelper.GREAT, true, false);
			col++;
		}
		if (profileFileBrowser.isSizeVisible()) {
			headerTable.setHTML(0, col, Main.i18n("search.result.size"));
			ScrollTableHelper.setColumnWidth(table, col, Integer.parseInt(profileFileBrowser.getSizeWidth()), ScrollTableHelper.MEDIUM);
			col++;
		}
		if (profileFileBrowser.isLastModifiedVisible()) {
			headerTable.setHTML(0, col, Main.i18n("search.result.date.update"));
			ScrollTableHelper.setColumnWidth(table, col, Integer.parseInt(profileFileBrowser.getLastModifiedWidth()), ScrollTableHelper.MEDIUM);
			col++;
		}
		if (profileFileBrowser.isAuthorVisible()) {
			headerTable.setHTML(0, col, Main.i18n("search.result.author"));
			ScrollTableHelper.setColumnWidth(table, col, Integer.parseInt(profileFileBrowser.getAuthorWidth()), ScrollTableHelper.MEDIUM, true, false);
			col++;
		}
		if (profileFileBrowser.isVersionVisible()) {
			headerTable.setHTML(0, col, Main.i18n("search.result.version"));
			ScrollTableHelper.setColumnWidth(table, col, Integer.parseInt(profileFileBrowser.getVersionWidth()), ScrollTableHelper.FIXED);
			col++;
		}
		if (profileFileBrowser.getColumn0() != null) {
			headerTable.setHTML(0, col, profileFileBrowser.getColumn0().getFormElement().getLabel());
			ScrollTableHelper.setColumnWidth(table, col, Integer.parseInt(profileFileBrowser.getColumn0Width()), ScrollTableHelper.MEDIUM, true, false);
			col++;
		}
		if (profileFileBrowser.getColumn1() != null) {
			headerTable.setHTML(0, col, profileFileBrowser.getColumn1().getFormElement().getLabel());
			ScrollTableHelper.setColumnWidth(table, col, Integer.parseInt(profileFileBrowser.getColumn1Width()), ScrollTableHelper.MEDIUM, true, false);
			col++;
		}
		if (profileFileBrowser.getColumn2() != null) {
			headerTable.setHTML(0, col, profileFileBrowser.getColumn2().getFormElement().getLabel());
			ScrollTableHelper.setColumnWidth(table, col, Integer.parseInt(profileFileBrowser.getColumn2Width()), ScrollTableHelper.MEDIUM, true, false);
			col++;
		}
		if (profileFileBrowser.getColumn3() != null) {
			headerTable.setHTML(0, col, profileFileBrowser.getColumn3().getFormElement().getLabel());
			ScrollTableHelper.setColumnWidth(table, col, Integer.parseInt(profileFileBrowser.getColumn3Width()), ScrollTableHelper.MEDIUM, true, false);
			col++;
		}
		if (profileFileBrowser.getColumn4() != null) {
			headerTable.setHTML(0, col, profileFileBrowser.getColumn4().getFormElement().getLabel());
			ScrollTableHelper.setColumnWidth(table, col, Integer.parseInt(profileFileBrowser.getColumn4Width()), ScrollTableHelper.MEDIUM, true, false);
			col++;
		}
		if (profileFileBrowser.getColumn5() != null) {
			headerTable.setHTML(0, col, profileFileBrowser.getColumn5().getFormElement().getLabel());
			ScrollTableHelper.setColumnWidth(table, col, Integer.parseInt(profileFileBrowser.getColumn5Width()), ScrollTableHelper.MEDIUM, true, false);
			col++;
		}
		if (profileFileBrowser.getColumn6() != null) {
			headerTable.setHTML(0, col, profileFileBrowser.getColumn6().getFormElement().getLabel());
			ScrollTableHelper.setColumnWidth(table, col, Integer.parseInt(profileFileBrowser.getColumn6Width()), ScrollTableHelper.MEDIUM, true, false);
			col++;
		}
		if (profileFileBrowser.getColumn7() != null) {
			headerTable.setHTML(0, col, profileFileBrowser.getColumn7().getFormElement().getLabel());
			ScrollTableHelper.setColumnWidth(table, col, Integer.parseInt(profileFileBrowser.getColumn7Width()), ScrollTableHelper.MEDIUM, true, false);
			col++;
		}
		if (profileFileBrowser.getColumn8() != null) {
			headerTable.setHTML(0, col, profileFileBrowser.getColumn8().getFormElement().getLabel());
			ScrollTableHelper.setColumnWidth(table, col, Integer.parseInt(profileFileBrowser.getColumn8Width()), ScrollTableHelper.MEDIUM, true, false);
			col++;
		}
		if (profileFileBrowser.getColumn9() != null) {
			headerTable.setHTML(0, col, profileFileBrowser.getColumn9().getFormElement().getLabel());
			ScrollTableHelper.setColumnWidth(table, col, Integer.parseInt(profileFileBrowser.getColumn9Width()), ScrollTableHelper.MEDIUM, true, false);
			col++;
		}
		headerTable.setHTML(0, col++, ""); // used to store data
		numberOfColumns = col;
		table.setColDataIndex(numberOfColumns - 1); // Columns starts with value 0
		table.setProfileFileBrowser(profileFileBrowser);
	}
}