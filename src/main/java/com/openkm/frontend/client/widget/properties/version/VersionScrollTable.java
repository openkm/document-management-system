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

package com.openkm.frontend.client.widget.properties.version;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ResizePolicy;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollPolicy;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollTableImages;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.bean.GWTVersion;
import com.openkm.frontend.client.service.OKMDocumentService;
import com.openkm.frontend.client.service.OKMDocumentServiceAsync;
import com.openkm.frontend.client.util.ScrollTableHelper;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.ConfirmPopup;

import java.util.*;

/**
 * VersionScrollTable
 *
 * @author jllort
 */
public class VersionScrollTable extends Composite implements ClickHandler {
	private final OKMDocumentServiceAsync documentService = (OKMDocumentServiceAsync) GWT.create(OKMDocumentService.class);

	// Number of columns
	public static final int NUMBER_OF_COLUMNS = 7;

	private GWTDocument doc;
	private ScrollTable table;
	private FixedWidthFlexTable headerTable;
	private FixedWidthGrid dataTable;
	private ExtendedColumnSorter columnSorter;
	public List<String> versions;
	private boolean visibleButtons = true;
	private Button purge;
	private List<Button> buttonView;
	private List<Button> buttonRestore;
	private boolean evaluateHistory = false;
	public Map<Integer, GWTVersion> data = new HashMap<Integer, GWTVersion>();

	/**
	 * Version
	 */
	public VersionScrollTable() {
		versions = new ArrayList<String>();
		buttonView = new ArrayList<Button>();
		buttonRestore = new ArrayList<Button>();

		purge = new Button(Main.i18n("version.purge.document"), this);
		purge.setStyleName("okm-CompactButton");
		purge.setEnabled(false);

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
		columnSorter = new ExtendedColumnSorter();
		table = new ScrollTable(dataTable, headerTable, scrollTableImages);
		dataTable.setColumnSorter(columnSorter);
		table.setCellSpacing(0);
		table.setCellPadding(2);
		table.setSize("540px", "140px");
		ScrollTableHelper.setColumnWidth(table, 0, 60, ScrollTableHelper.FIXED);
		ScrollTableHelper.setColumnWidth(table, 1, 120, ScrollTableHelper.MEDIUM);
		ScrollTableHelper.setColumnWidth(table, 2, 120, ScrollTableHelper.MEDIUM, true, false);
		ScrollTableHelper.setColumnWidth(table, 3, 60, ScrollTableHelper.MEDIUM);
		ScrollTableHelper.setColumnWidth(table, 4, 90, ScrollTableHelper.FIXED);
		ScrollTableHelper.setColumnWidth(table, 5, 140, ScrollTableHelper.FIXED);
		ScrollTableHelper.setColumnWidth(table, 6, 150, ScrollTableHelper.MEDIUM, true, false);

		table.setColumnSortable(4, false);
		table.setColumnSortable(5, false);

		// Level 1 headers
		headerTable.setHTML(0, 0, Main.i18n("version.name"));
		headerTable.setHTML(0, 1, Main.i18n("version.created"));
		headerTable.setHTML(0, 2, Main.i18n("version.author"));
		headerTable.setHTML(0, 3, Main.i18n("version.size"));
		headerTable.setHTML(0, 4, "&nbsp;");
		headerTable.setWidget(0, 5, purge);
		headerTable.setHTML(0, 6, Main.i18n("version.comment"));

		headerTable.getCellFormatter().setHorizontalAlignment(0, 5, HasAlignment.ALIGN_CENTER);
		headerTable.getCellFormatter().setVerticalAlignment(0, 5, HasAlignment.ALIGN_MIDDLE);

		// Table data
		dataTable.setSelectionPolicy(SelectionGrid.SelectionPolicy.ONE_ROW);
		table.setResizePolicy(ResizePolicy.FILL_WIDTH);
		table.setScrollPolicy(ScrollPolicy.BOTH);

		headerTable.addStyleName("okm-DisableSelect");
		dataTable.addStyleName("okm-DisableSelect");

		initWidget(table);
	}

	/**
	 * initExtendedSecurity
	 *
	 * @param extendedSecurity
	 */
	public void initExtendedSecurity(int extendedSecurity) {
		evaluateHistory = ((extendedSecurity & GWTPermission.COMPACT_HISTORY) == GWTPermission.COMPACT_HISTORY);
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		headerTable.setHTML(0, 0, Main.i18n("version.name"));
		headerTable.setHTML(0, 1, Main.i18n("version.created"));
		headerTable.setHTML(0, 2, Main.i18n("version.author"));
		headerTable.setHTML(0, 3, Main.i18n("version.size"));
		headerTable.setHTML(0, 6, Main.i18n("version.comment"));
		purge.setHTML(Main.i18n("version.purge.document"));

		// Translate all view buttons
		if (!buttonView.isEmpty()) {
			for (Iterator<Button> it = buttonView.iterator(); it.hasNext(); ) {
				Button button = it.next();
				button.setHTML(Main.i18n("button.view"));
			}
		}

		if (!buttonRestore.isEmpty()) {
			for (Iterator<Button> it = buttonRestore.iterator(); it.hasNext(); ) {
				Button button = it.next();
				button.setHTML(Main.i18n("button.restore"));
			}
		}
	}

	/**
	 * Sets the document
	 *
	 * @param GWTDocument The document
	 */
	public void set(GWTDocument doc) {
		this.doc = doc;
	}

	/**
	 * Removes all rows except the first
	 */
	public void reset() {
		// Purge all rows except first
		while (dataTable.getRowCount() > 0) {
			dataTable.removeRow(0);
		}
		dataTable.resize(0, NUMBER_OF_COLUMNS);
		versions = new ArrayList<String>();
		data = new HashMap<Integer, GWTVersion>();
	}

	/**
	 * Adds a version to the history table
	 *
	 * @param version The Version to add
	 */
	public void addRow(GWTVersion version) {
		final int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);
		dataTable.setHTML(rows, 0, version.getName());
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		dataTable.setHTML(rows, 1, dtf.format(version.getCreated()));
		dataTable.setHTML(rows, 2, version.getUser().getUsername());
		dataTable.setHTML(rows, 3, Util.formatSize(version.getSize()));
		dataTable.setHTML(rows, 6, version.getComment());
		versions.add(version.getName());
		data.put(rows, version);

		// Special case when visibleButtons are false, widget are on trash, must
		// disable all buttons,
		// but must enable the actual version to view ( on default is not
		// enabled because is active one )
		if (version.isActual() && visibleButtons) {
			dataTable.selectRow(rows, true);
		} else {

			// Only on trash widget it'll occurs
			if (version.isActual()) {
				dataTable.selectRow(rows, true);
			}

			Button restoreButton = new Button(Main.i18n("button.restore"), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					List<String> versions = Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.version.versions;
					String ver = (String) versions.get(rows);
					Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_RESTORE_HISTORY_DOCUMENT);
					Main.get().confirmPopup.setValue(ver);
					Main.get().confirmPopup.show();
				}
			});

			restoreButton.setVisible(visibleButtons);

			if ((doc.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE && !doc.isCheckedOut() && !doc.isLocked()) {
				restoreButton.setEnabled(true);
			} else {
				restoreButton.setEnabled(false);
			}

			dataTable.setWidget(rows, 5, restoreButton);
			dataTable.getCellFormatter().setHorizontalAlignment(rows, 5, HorizontalPanel.ALIGN_CENTER);
			buttonRestore.add(restoreButton);
			restoreButton.setStyleName("okm-YesButton");
		}

		Button viewButton = new Button(Main.i18n("button.view"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				List<String> versions = Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.version.versions;
				String ver = (String) versions.get(rows);
				Util.downloadFileByUUID(doc.getUuid(), "ver=" + ver);
			}
		});
		viewButton.setVisible(Main.get().workspaceUserProperties.getWorkspace().isTabDocumentVersionDownloadVisible());

		dataTable.setWidget(rows, 4, viewButton);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 4, HorizontalPanel.ALIGN_CENTER);
		buttonView.add(viewButton);
		viewButton.setStyleName("okm-ViewButton");
	}

	/**
	 * Refresh the version history
	 */
	final AsyncCallback<List<GWTVersion>> callbackGetVersionHistory = new AsyncCallback<List<GWTVersion>>() {
		public void onSuccess(List<GWTVersion> result) {
			reset();

			// Initializes buttons lists ( to make language translations )
			buttonView = new ArrayList<Button>();
			buttonRestore = new ArrayList<Button>();

			// When there's more than one version document can purge it
			if (result.size() > 1) {
				if (evaluateHistory) {
					purge.setEnabled((doc.getPermissions() & GWTPermission.COMPACT_HISTORY) == GWTPermission.COMPACT_HISTORY);
				} else {
					purge.setEnabled(true);
				}
			} else {
				purge.setEnabled(false);
			}

			for (Iterator<GWTVersion> it = result.iterator(); it.hasNext(); ) {
				GWTVersion version = it.next();
				addRow(version);
			}

			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetVersionHistory();
		}

		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetVersionHistory();
			Main.get().showError("GetVersionHistory", caught);
		}
	};

	/**
	 * Refresh the version history after restoring version
	 */
	final AsyncCallback<Object> callbackRestoreVersion = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetRestoreVersion();
			Main.get().mainPanel.topPanel.toolBar.executeRefresh();
		}

		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetRestoreVersion();
			Main.get().showError("GetVersionHistory", caught);
		}
	};

	/**
	 * Refresh the version history after purge version
	 */
	final AsyncCallback<Object> callbackPurgeVersionHistory = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetPurgeVersionHistory();
			Main.get().mainPanel.topPanel.toolBar.executeRefresh();
			Main.get().workspaceUserProperties.getUserDocumentsSize();
		}

		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetPurgeVersionHistory();
			Main.get().showError("purgeVersionHistory", caught);
		}
	};

	/**
	 * Gets the version history on the server
	 */
	public void getVersionHistory() {
		if (doc != null) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.setVersionHistory();
			documentService.getVersionHistory(doc.getUuid(), callbackGetVersionHistory);
		}
	}

	/**
	 * Revert to a history document
	 */
	public void restoreVersion(String versionId) {
		if (doc != null) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.setRestoreVersion();
			documentService.restoreVersion(doc.getPath(), versionId, callbackRestoreVersion);
		}
	}

	/**
	 * Purges a version history
	 */
	public void purgeVersionHistory() {
		if (doc != null) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.setPurgeVersionHistory();
			documentService.purgeVersionHistory(doc.getPath(), callbackPurgeVersionHistory);
		}
	}

	/**
	 * Sets visibility to buttons ( true / false )
	 *
	 * @param visible The visible value
	 */
	public void setVisibleButtons(boolean visible) {
		visibleButtons = visible;
	}

	/**
	 * @return the data table
	 */
	public FixedWidthGrid getDataTable() {
		return dataTable;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event
	 * .dom.client.ClickEvent)
	 */
	public void onClick(ClickEvent event) {
		Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_PURGE_VERSION_HISTORY_DOCUMENT);
		Main.get().confirmPopup.show();
	}

	/**
	 * fillWidth
	 */
	public void fillWidth() {
		table.fillWidth();
	}
}
