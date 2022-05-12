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

package com.openkm.frontend.client.widget.filebrowser;

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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.*;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.OriginPanel;
import com.openkm.frontend.client.widget.form.FormManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extends ExtendedScrollTable functionalities
 *
 * @author jllort
 *
 */
public class ExtendedScrollTable extends ScrollTable implements OriginPanel {

	// Actions on rows
	public static final int ACTION_NONE = 0;
	public static final int ACTION_RENAMING = 1;

	// Special event case
	private static final int EVENT_ONMOUSEDOWN_RIGHT = -2;

	// Drag pixels sensibility
	private static final int DRAG_PIXELS_SENSIBILITY = 3;

	// Holds the data rows of the table this is a list of RowData Object
	public Map<Integer, Object> data = new HashMap<Integer, Object>();
	public List<Integer> massiveSelected = new ArrayList<Integer>();
	private FixedWidthGrid dataTable;
	private FixedWidthFlexTable headerTable;

	private int selectedRow = -1;
	private int mouseX = 0;
	private int mouseY = 0;
	private int dataIndexValue = 0;
	private int rowAction = ACTION_NONE;
	private int mouseDownX = 0;
	private int mouseDownY = 0;

	private boolean dragged = false;
	private int oldMassiveSelected = 0;
	private ExtendedColumnSorter columnSorter;
	private FormManager formManager;

	// Columns
	private GWTProfileFileBrowser profileFileBrowser;
	public int colDataIndex = 0;
	public int colStatusIndex = 0; // Always is 0
	public int colMassiveIndex = 0;

	/**
	 * @param dataTable
	 * @param headerTable
	 * @param scrollTableImages
	 */
	public ExtendedScrollTable(FixedWidthGrid dataTable, FixedWidthFlexTable headerTable,
	                           ScrollTableImages scrollTableImages) {
		super(dataTable, headerTable, scrollTableImages);
		formManager = new FormManager(null); // Used to draw extended columns

		this.dataTable = dataTable;
		this.headerTable = headerTable;

		// Table data SortableFixedWidthGrid.HOVERING_POLICY_CELL
		dataTable.setSelectionPolicy(SelectionGrid.SelectionPolicy.ONE_ROW);
		setResizePolicy(ResizePolicy.FILL_WIDTH);
		setScrollPolicy(ScrollPolicy.BOTH);

		columnSorter = new ExtendedColumnSorter();
		dataTable.setColumnSorter(columnSorter);

		// Sets some events
		DOM.sinkEvents(getDataWrapper(), Event.ONCLICK | Event.ONDBLCLICK | Event.ONMOUSEDOWN | Event.ONMOUSEUP
				| Event.ONMOUSEMOVE | Event.ONMOUSEUP);
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

	/**
	 * Resets the values
	 */
	public void reset() {
		selectedRow = -1;
		mouseX = 0;
		mouseY = 0;
		dataIndexValue = 0;

		// Reset rowAction
		rowAction = ACTION_NONE;

		// Only resets rows table the header is never reset
		data = new HashMap<Integer, Object>();
		massiveSelected = new ArrayList<Integer>();
	}

	/**
	 * Sets the selected row
	 *
	 * @param row The row number
	 */
	public void setSelectedRow(int row) {
		// Log.debug("ExtendedScrollTable setSelectedRow:"+row);
		dataTable.selectRow(row, true);
		selectedRow = row;
	}

	/**
	 * addRow
	 *
	 * @param folder
	 */
	public void addRow(GWTFolder folder) {
		addRow(folder, false);
	}

	/**
	 * Sets the values in specifed row/column
	 * Expects a Comparable Object for sorting
	 *
	 * Update indicates should be updated selected row otherside inserts new
	 *
	 * @param rows The actual table row
	 * @param GWTFolder The folder
	 */
	public void addRow(GWTFolder folder, boolean update) {
		int col = 0;
		final int row = (update) ? getSelectedRow() : dataTable.getRowCount();
		if (update) {
			data.put(new Integer(dataTable.getText(row, colDataIndex)), folder);
		} else {
			dataTable.insertRow(row);
		}

		// Sets folder object
		data.put(new Integer(dataIndexValue), folder);

		// Subscribed is a special case, must add icon with others
		if (profileFileBrowser.isStatusVisible()) {
			if (folder.isSubscribed()) {
				dataTable.setHTML(row, col, Util.imageItemHTML("img/icon/subscribed.gif"));
			} else {
				dataTable.setHTML(row, col, "&nbsp;");
			}

			if (folder.isHasNotes()) {
				dataTable.setHTML(row, col, dataTable.getHTML(row, col) + Util.imageItemHTML("img/icon/note.gif"));
			}
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_RIGHT);
		}

		if (profileFileBrowser.isMassiveVisible()) {
			// Checkbox
			final CheckBox checkBox = new CheckBox();
			checkBox.setStyleName("okm-CheckBox");
			checkBox.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					oldMassiveSelected = massiveSelected.size();

					if (checkBox.getValue()) {
						massiveSelected.add(new Integer(dataTable.getText(row, colDataIndex)));
					} else {
						massiveSelected.remove(new Integer(dataTable.getText(row, colDataIndex)));
					}
				}
			});

			dataTable.setWidget(row, col, checkBox);
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.isIconVisible()) {
			// Looks if must change icon on parent if now has no childs and properties with user security atention
			if ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
				if (folder.isHasChildren()) {
					dataTable.setHTML(row, col, Util.imageItemHTML("img/menuitem_childs.gif"));
				} else {
					dataTable.setHTML(row, col, Util.imageItemHTML("img/menuitem_empty.gif"));
				}
			} else {
				if (folder.isHasChildren()) {
					dataTable.setHTML(row, col, Util.imageItemHTML("img/menuitem_childs_ro.gif"));
				} else {
					dataTable.setHTML(row, col, Util.imageItemHTML("img/menuitem_empty_ro.gif"));
				}
			}

			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.isNameVisible()) {
			dataTable.setHTML(row, col, folder.getName());
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.isSizeVisible()) {
			dataTable.setHTML(row, col, "&nbsp;");
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.isLastModifiedVisible()) {
			DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
			dataTable.setHTML(row, col, dtf.format(folder.getCreated()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.isAuthorVisible()) {
			dataTable.setHTML(row, col, folder.getUser().getUsername());
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.isVersionVisible()) {
			dataTable.setHTML(row, col, "&nbsp;");
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.getColumn0() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(folder.getColumn0()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn1() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(folder.getColumn1()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn2() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(folder.getColumn2()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn3() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(folder.getColumn3()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn4() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(folder.getColumn4()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn5() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(folder.getColumn5()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn6() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(folder.getColumn6()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn7() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(folder.getColumn7()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn8() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(folder.getColumn8()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn9() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(folder.getColumn9()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (!update) {
			dataTable.setHTML(row, colDataIndex, "" + (dataIndexValue++));
			dataTable.getCellFormatter().setVisible(row, colDataIndex, false);
		}

		for (int i = 0; i < colDataIndex; i++) {
			dataTable.getCellFormatter().addStyleName(row, i, "okm-DisableSelect");
		}
	}

	/**
	 * addNoteIconToSelectedRow
	 */
	public void addNoteIconToSelectedRow() {
		if (profileFileBrowser.isStatusVisible()) {
			dataTable.setHTML(selectedRow, colStatusIndex,
					dataTable.getHTML(selectedRow, colStatusIndex) + Util.imageItemHTML("img/icon/note.gif"));
		}
	}

	/**
	 * deleteNoteIconToSelectedRow
	 */
	public void deleteNoteIconToSelectedRow() {
		if (profileFileBrowser.isStatusVisible()) {
			String htmlValue = dataTable.getHTML(selectedRow, colStatusIndex);
			String newHtmlIcons = "";

			if (htmlValue.indexOf("edit.gif") >= 0) {
				newHtmlIcons = Util.imageItemHTML("img/icon/edit.gif");
			} else if (htmlValue.indexOf("lock.gif") > 0) {
				newHtmlIcons = Util.imageItemHTML("img/icon/lock.gif");
			} else {
				newHtmlIcons = "&nbsp;";
			}

			if (htmlValue.indexOf("subscribed.gif") >= 0) {
				newHtmlIcons += Util.imageItemHTML("img/icon/subscribed.gif");
			}

			dataTable.setHTML(selectedRow, colStatusIndex, newHtmlIcons);
		}
	}

	/**
	 * Sets the document to the row
	 *
	 * @param rows The table row
	 * @param doc The document
	 */
	public void addRow(GWTDocument doc) {
		addRow(doc, false);
	}

	/**
	 * Sets the document to the row
	 *
	 * Update indicates should be updated selected row otherside inserts new
	 */
	public void addRow(GWTDocument doc, boolean update) {
		int col = 0;
		final int row = (update) ? getSelectedRow() : dataTable.getRowCount();

		if (update) {
			data.put(new Integer(dataTable.getText(row, colDataIndex)), doc);
		} else {
			dataTable.insertRow(row);
		}

		// Sets document object
		data.put(new Integer(dataIndexValue), doc);

		if (profileFileBrowser.isStatusVisible()) {
			if (doc.isCheckedOut()) {
				dataTable.setHTML(row, col, Util.imageItemHTML("img/icon/edit.gif"));
			} else if (doc.isLocked()) {
				dataTable.setHTML(row, col, Util.imageItemHTML("img/icon/lock.gif"));
			} else {
				dataTable.setHTML(row, col, "&nbsp;");
			}

			// Subscribed is a special case, must add icon with others
			if (doc.isSubscribed()) {
				dataTable
						.setHTML(row, col, dataTable.getHTML(row, col) + Util.imageItemHTML("img/icon/subscribed.gif"));
			}

			// Document has notes
			if (doc.isHasNotes()) {
				dataTable.setHTML(row, col, dataTable.getHTML(row, col) + Util.imageItemHTML("img/icon/note.gif"));
			}

			// Document encrypted
			if (doc.getCipherName() != null && !doc.getCipherName().equals("")) {
				dataTable.setHTML(row, col,
						dataTable.getHTML(row, col) + Util.imageItemHTML("img/icon/actions/crypt.png"));
			}

			// Document signed
			if (doc.isSigned()) {
				dataTable.setHTML(row, col,
						dataTable.getHTML(row, col) + Util.imageItemHTML("img/icon/actions/digital_signature.png"));
			}

			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_RIGHT);
		}

		if (profileFileBrowser.isMassiveVisible()) {
			// Checkbox
			final CheckBox checkBox = new CheckBox();
			checkBox.setStyleName("okm-CheckBox");
			checkBox.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					oldMassiveSelected = massiveSelected.size();

					if (checkBox.getValue()) {
						massiveSelected.add(new Integer(dataTable.getText(row, colDataIndex)));
					} else {
						massiveSelected.remove(new Integer(dataTable.getText(row, colDataIndex)));
					}
					evaluateMergePdf(); // Only when document checkbox is changed ( only document can be merged )
				}
			});

			dataTable.setWidget(row, col, checkBox);
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.isIconVisible()) {
			dataTable.setHTML(row, col, Util.mimeImageHTML(doc.getMimeType()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.isNameVisible()) {
			dataTable.setHTML(row, col, doc.getName());
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.isSizeVisible()) {
			dataTable.setHTML(row, col, Util.formatSize(doc.getActualVersion().getSize()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.isLastModifiedVisible()) {
			DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
			dataTable.setHTML(row, col, dtf.format(doc.getLastModified()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.isAuthorVisible()) {
			dataTable.setHTML(row, col, doc.getActualVersion().getUser().getUsername());
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.isVersionVisible()) {
			Hyperlink hLink = new Hyperlink();
			hLink.setText(doc.getActualVersion().getName());
			hLink.setTitle(doc.getActualVersion().getComment());
			dataTable.setWidget(row, col, hLink);
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.getColumn0() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(doc.getColumn0()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn1() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(doc.getColumn1()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn2() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(doc.getColumn2()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn3() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(doc.getColumn3()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn4() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(doc.getColumn4()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn5() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(doc.getColumn5()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn6() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(doc.getColumn6()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn7() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(doc.getColumn7()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn8() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(doc.getColumn8()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn9() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(doc.getColumn9()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (!update) {
			dataTable.setHTML(row, colDataIndex, "" + (dataIndexValue++));
			dataTable.getCellFormatter().setVisible(row, colDataIndex, false);
		}

		for (int i = 0; i < colDataIndex; i++) {
			dataTable.getCellFormatter().addStyleName(row, i, "okm-DisableSelect");
		}
	}

	/**
	 * addRow
	 */
	public void addRow(GWTMail mail) {
		addRow(mail, false);
	}

	/**
	 * Sets the mail to the row
	 *
	 * Update indicates should be updated selected row otherside inserts new
	 */
	public void addRow(GWTMail mail, boolean update) {
		int col = 0;
		final int row = (update) ? getSelectedRow() : dataTable.getRowCount();

		if (update) {
			data.put(new Integer(dataTable.getText(row, colDataIndex)), mail);
		} else {
			dataTable.insertRow(row);
		}

		// Sets document object
		data.put(new Integer(dataIndexValue), mail);

		if (profileFileBrowser.isStatusVisible()) {
			// Mail is never checkout or subscribed ( because can not be changed )
			if (mail.isHasNotes()) {
				dataTable.setHTML(row, col, dataTable.getHTML(row, col) + Util.imageItemHTML("img/icon/note.gif"));
			} else {
				dataTable.setHTML(row, col, "&nbsp;");
			}
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_RIGHT);
		}

		if (profileFileBrowser.isMassiveVisible()) {
			// Checkbox
			final CheckBox checkBox = new CheckBox();
			checkBox.setStyleName("okm-CheckBox");
			checkBox.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					oldMassiveSelected = massiveSelected.size();

					if (checkBox.getValue()) {
						massiveSelected.add(new Integer(dataTable.getText(row, colDataIndex)));
					} else {
						massiveSelected.remove(new Integer(dataTable.getText(row, colDataIndex)));
					}
				}
			});

			dataTable.setWidget(row, col, checkBox);
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.isIconVisible()) {
			if (mail.isHasAttachments()) {
				dataTable.setHTML(row, col, Util.imageItemHTML("img/email_attach.gif"));
			} else {
				dataTable.setHTML(row, col, Util.imageItemHTML("img/email.gif"));
			}
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.isNameVisible()) {
			dataTable.setHTML(row, col, mail.getSubject());
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.isSizeVisible()) {
			dataTable.setHTML(row, col, Util.formatSize(mail.getSize()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.isLastModifiedVisible()) {
			DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
			dataTable.setHTML(row, col, dtf.format(mail.getReceivedDate()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.isAuthorVisible()) {
			dataTable.setHTML(row, col, Util.showMailName(mail.getFrom()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.isVersionVisible()) {
			dataTable.setHTML(row, col, "&nbsp;");
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_CENTER);
		}

		if (profileFileBrowser.getColumn0() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(mail.getColumn0()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn1() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(mail.getColumn1()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn2() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(mail.getColumn2()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn3() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(mail.getColumn3()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn4() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(mail.getColumn4()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn5() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(mail.getColumn5()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn6() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(mail.getColumn6()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn7() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(mail.getColumn7()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn8() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(mail.getColumn8()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (profileFileBrowser.getColumn9() != null) {
			dataTable.setWidget(row, col, formManager.getDrawFormElement(mail.getColumn9()));
			dataTable.getCellFormatter().setHorizontalAlignment(row, col++, HasHorizontalAlignment.ALIGN_LEFT);
		}

		if (!update) {
			dataTable.setHTML(row, colDataIndex, "" + (dataIndexValue++));
			dataTable.getCellFormatter().setVisible(row, colDataIndex, false);
		}

		for (int i = 0; i < colDataIndex; i++) {
			dataTable.getCellFormatter().addStyleName(row, i, "okm-DisableSelect");
		}
	}

	/**
	 * Deselects the selected row
	 */
	public void deselecSelectedRow() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			int selectedRow = ((Integer) dataTable.getSelectedRows().iterator().next()).intValue();
			dataTable.deselectRow(selectedRow);
		}
		selectedRow = -1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.user.client.EventListener#onBrowserEvent(com.google.gwt.user.client.Event)
	 */
	public void onBrowserEvent(Event event) {
		boolean headerFired = false; // Controls when event is fired by header

		// Case targe event is header must disable drag & drop
		if (headerTable.getEventTargetCell(event) != null) {
			dragged = false;
			headerFired = true;
		}

		boolean isRenaming = false;

		// If some action is on course must do speacil actions, this must be made before selected row
		// is changed
		switch (rowAction) {
			case ACTION_RENAMING:
				isRenaming = true;
				break;
		}

		// When de button mouse is released
		mouseX = DOM.eventGetClientX(event);
		mouseY = DOM.eventGetClientY(event);

		int type = DOM.eventGetType(event);

		if (type == Event.ONMOUSEDOWN && DOM.eventGetButton(event) == Event.BUTTON_RIGHT) {
			type = EVENT_ONMOUSEDOWN_RIGHT; // Special case, that must be so much similar to click event
		}

		switch (type) {
			case Event.ONCLICK:
			case EVENT_ONMOUSEDOWN_RIGHT:

				// Only for right mouuse button
				if (!headerFired && type == EVENT_ONMOUSEDOWN_RIGHT) {
					Main.get().mainPanel.desktop.browser.fileBrowser.showMenu();
					DOM.eventPreventDefault(event); // Prevent to fire event to browser
				}

				if (dataTable.getEventTargetCell(event) != null) {
					// Mark panel as selected and disables tree navigator panel
					if (getSelectedRow() >= 0 && !Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
						Main.get().mainPanel.desktop.browser.fileBrowser.setSelectedPanel(true);
					}

					// And row must be other than the selected one
					if (getSelectedRow() >= 0 && getSelectedRow() != selectedRow) {
						// If some action is on course must do special actions, this must be made before selected row
						// is changed
						switch (rowAction) {
							case ACTION_RENAMING:
								if (getSelectedRow() != selectedRow) {
									Main.get().mainPanel.desktop.browser.fileBrowser.hideRename(selectedRow);
								}
								break;
						}

						selectedRow = getSelectedRow();

						refreshTabValues();
					} else if (getSelectedRow() >= 0 && getSelectedRow() == selectedRow) {
						// Special case should evaluate massive because toolbar will not be refreshed
						if (oldMassiveSelected == 0 && massiveSelected.size() > 0) {
							Main.get().mainPanel.topPanel.toolBar.enableMassiveView();
							oldMassiveSelected = massiveSelected.size();
						} else if (oldMassiveSelected == 1 && massiveSelected.size() == 0) {
							Main.get().mainPanel.topPanel.toolBar.disableMassiveView();
							oldMassiveSelected = 0;
						}
					}
				}
				break;

			case Event.ONDBLCLICK:
				// On double click not sends event to onCellClicked across super.onBrowserEvent();
				// Disables the event propagation the sequence is:
				// Two time entry onCellClicked before entry on onBrowserEvent and disables the
				// Tree onCellClicked that produces inconsistency error refreshing
				DOM.eventCancelBubble(event, true);

				if (!headerFired && getSelectedRow() >= 0) {
					if (isFolderSelected()) {
						Main.get().mainPanel.desktop.browser.tabMultiple.enableTabFolder();

						// Must not refresh properties on double click if row is already selected
						if (getSelectedRow() != selectedRow) {
							Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.setProperties(getFolder());
						}

						Main.get().activeFolderTree.setActiveNode(getFolder().getPath(), false, true);
					} else if (isMailSelected()) {
						Main.get().mainPanel.desktop.browser.tabMultiple.enableTabMail();
						GWTMail mail = getMail();
						Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions(mail, Main.get().activeFolderTree.getFolder());

						// We come here before executing click ( click is always executed )
						if (!isRenaming && Main.get().mainPanel.topPanel.toolBar.getToolBarOption().downloadOption) {
							if (Main.get().workspaceUserProperties.getWorkspace().getAvailableOption().isDownloadOption()) {
								downloadMail();
							}
						}
					} else {
						Main.get().mainPanel.desktop.browser.tabMultiple.enableTabDocument();
						GWTDocument doc = getDocument();
						Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions(doc, Main.get().activeFolderTree.getFolder());

						// We come here before executing click ( click is always executed )
						if (!isRenaming && Main.get().mainPanel.topPanel.toolBar.getToolBarOption().downloadOption) {
							if (Main.get().workspaceUserProperties.getWorkspace().getAvailableOption().isDownloadOption()) {
								downloadDocument(false);
							}
						}
					}
				}
				break;

			case Event.ONMOUSEMOVE:
				if (isDragged() && mouseDownX > 0 && mouseDownY > 0 && evalDragPixelSensibility()) {

					// Implements drag & drop
					int noAction = FileBrowser.ACTION_NONE;

					// On trash drag is disabled
					if (isSelectedRow()
							&& Main.get().mainPanel.desktop.browser.fileBrowser.fileBrowserAction == noAction
							&& Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_CATEGORIES
							&& Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_THESAURUS
							&& Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_TRASH) {
						String dragText = "";

						if (isDocumentSelected()) {
							GWTDocument doc = getDocument();
							dragText = Util.mimeImageHTML(doc.getMimeType()) + doc.getName();
						} else if (isFolderSelected()) {
							GWTFolder folder = getFolder();

							if ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
								if (folder.isHasChildren()) {
									dragText = Util.imageItemHTML("img/menuitem_childs.gif");
								} else {
									dragText = Util.imageItemHTML("img/menuitem_empty.gif");
								}
							} else {
								if (folder.isHasChildren()) {
									dragText = Util.imageItemHTML("img/menuitem_childs_ro.gif");
								} else {
									dragText = Util.imageItemHTML("img/menuitem_empty_ro.gif");
								}
							}

							dragText += folder.getName();
						} else if (isMailSelected()) {
							GWTMail mail = getMail();

							if (mail.isHasAttachments()) {
								dragText = Util.imageItemHTML("img/email_attach.gif");
							} else {
								dragText = Util.imageItemHTML("img/email.gif");
							}

							dragText += mail.getSubject();
						}

						Main.get().draggable.show(dragText, OriginPanel.FILE_BROWSER);
					}

					unsetDraged();
				}
				break;

			case Event.ONMOUSEOUT:
				unsetDraged();
				break;

			case Event.ONMOUSEDOWN:
				// saves initial mouse positions
				mouseDownX = mouseX;
				mouseDownY = mouseY;
				dragged = true;
				break;

			case Event.ONMOUSEUP:
				unsetDraged();
				break;
		}

		super.onBrowserEvent(event);
	}

	/**
	 * evalDragPixelSensibility
	 *
	 * @return
	 */
	private boolean evalDragPixelSensibility() {
		if (mouseDownX - mouseX >= DRAG_PIXELS_SENSIBILITY) {
			return true;
		} else if (mouseX - mouseDownX >= DRAG_PIXELS_SENSIBILITY) {
			return true;
		} else if (mouseDownY - mouseY >= DRAG_PIXELS_SENSIBILITY) {
			return true;
		} else if (mouseY - mouseDownY >= DRAG_PIXELS_SENSIBILITY) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the path of the selected document or folder
	 *
	 * @return The id
	 */
	public String getSelectedId() {
		String id = "";

		if (getSelectedRow() >= 0) {
			if (isFolderSelected()) {
				id = getFolder().getPath();
			} else if (isMailSelected()) {
				id = getMail().getPath();
			} else if (isDocumentSelected()) {
				id = getDocument().getPath();
			}
		}

		return id;
	}

	/**
	 * Finds row by id document or folder
	 *
	 * @param id The id
	 * @return The selected row
	 */
	public int findSelectedRowById(String id) {
		int selected = 0;
		int rowIndex = 0;
		boolean found = false;

		// Looking for id on directories
		while (!found && rowIndex < data.size()) {
			if (data.get(rowIndex) instanceof GWTFolder) {
				if (((GWTFolder) data.get(rowIndex)).getPath().equals(id)) {
					selected = rowIndex;
					found = true;
				}
			} else if (data.get(rowIndex) instanceof GWTMail) {
				if (((GWTMail) data.get(rowIndex)).getPath().equals(id)) {
					selected = rowIndex;
					found = true;
				}
			} else if (data.get(rowIndex) instanceof GWTDocument) {
				if (((GWTDocument) data.get(rowIndex)).getPath().equals(id)) {
					selected = rowIndex;
					found = true;
				}
			}

			rowIndex++;
		}

		if (found) {
			found = false;
			rowIndex = 0;
			int tmpSelected = selected;
			selected = 0;
			while (!found && rowIndex < dataTable.getRowCount()) {
				if (dataTable.getText(rowIndex, colDataIndex).equals(String.valueOf(tmpSelected))) {
					found = true;
					selected = rowIndex;
				}

				rowIndex++;
			}
		} else {
			selected = -1;
		}

		return selected;
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

		// Log.debug("ExtendedScrollTable selectedRow:"+selectedRow);
		return selectedRow;
	}

	/**
	 * Reset selected rows
	 */
	public void resetSelectedRows() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			dataTable.getSelectedRows().clear();
		}
	}

	/**
	 * Restores the selected row value
	 *
	 * @param selectedRow The selected row
	 */
	public void restoreSelectedRow(int selectedRow) {
		// Log.debug("ExtendedScrollTable restoreSelectedRow:"+selectedRow);
		this.selectedRow = selectedRow;
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
	 * Gets a actual document object row
	 *
	 * @return
	 */
	public GWTDocument getDocument() {
		if (isDocumentSelected()) {
			return (GWTDocument) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), colDataIndex)));
		} else {
			return null;
		}
	}

	/**
	 * Sets the document object to actual row
	 *
	 * @param doc The document
	 */
	public void setDocument(GWTDocument doc) {
		if (isDocumentSelected()) {
			addRow(doc, true);
		}
	}

	/**
	 * Deletes document row
	 */
	public void delete() {
		if (isSelectedRow()) {
			// Log.debug("ExtendedScrollTable delete:");
			data.remove(Integer.parseInt(dataTable.getText(getSelectedRow(), colDataIndex)));
			dataTable.removeRow(getSelectedRow());
			selectPrevRow();
		}
	}

	/**
	 * After deletes document or folder selects a row
	 */
	public void selectPrevRow() {
		// Log.debug("ExtendedScrollTable selectPrevRow");
		// Log.debug("ExtendedScrollTable selectPrevRow -> dataTable.getRowCount():"+ dataTable.getRowCount());
		// Log.debug("ExtendedScrollTable selectPrevRow -> selectedRow:"+selectedRow);
		if (dataTable.getRowCount() > 0) {
			if (dataTable.getRowCount() > selectedRow) {
				// Log.debug("selectPrevRow:"+selectedRow);
				if (selectedRow > 0) { // Special case when selects rows with drag & drop could be possible selectedRow
					// is really no selected
					dataTable.selectRow(selectedRow, true);
				} else {
					dataTable.selectRow(0, true);
				}
			} else {
				// Log.debug("selectPrevRow-1:"+(selectedRow-1));
				dataTable.selectRow(selectedRow - 1, true);
			}
		}
	}

	/**
	 * selectUp
	 */
	public void selectUp() {
		if (dataTable.getRowCount() > 0 && selectedRow > 0) {
			dataTable.selectRow(--selectedRow, true);
			refreshTabValues();
		}
	}

	/**
	 * selectDown
	 */
	public void selectDown() {
		if (dataTable.getRowCount() > 0 && (dataTable.getRowCount() - 1 > selectedRow)) {
			dataTable.selectRow(++selectedRow, true);
			refreshTabValues();
		}
	}

	/**
	 * refreshTabValues
	 */
	private void refreshTabValues() {
		if (isFolderSelected()) {
			Main.get().mainPanel.desktop.browser.tabMultiple.enableTabFolder();
			GWTFolder folder = getFolder();
			Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.setProperties(folder);
			Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions(folder,
					Main.get().activeFolderTree.getFolder(), FILE_BROWSER);
		} else if (isMailSelected()) {
			Main.get().mainPanel.desktop.browser.tabMultiple.enableTabMail();
			GWTMail mail = getMail();
			Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.setProperties(mail);
			Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions(mail,
					Main.get().activeFolderTree.getFolder());
		} else {
			Main.get().mainPanel.desktop.browser.tabMultiple.enableTabDocument();
			GWTDocument doc = getDocument();
			Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.setProperties(doc);
			Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions(doc,
					Main.get().activeFolderTree.getFolder());
		}
	}

	/**
	 * Gets a actual Folder object row
	 *
	 * @return
	 */
	public GWTFolder getFolder() {
		// Row selected must be on table folder
		if (isFolderSelected()) {
			return (GWTFolder) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), colDataIndex)));
		} else {
			return null;
		}
	}

	/**
	 * Gets a actual Mail object row
	 *
	 * @return
	 */
	public GWTMail getMail() {
		// Row selected must be on table mail
		if (isMailSelected()) {
			return (GWTMail) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), colDataIndex)));
		} else {
			return null;
		}
	}

	/**
	 * Sets the mail to the row
	 *
	 * @param rows The table row
	 * @param doc The document
	 */
	public void setMail(GWTMail mail) {
		if (isMailSelected()) {
			addRow(mail, true);
		}
	}

	/**
	 * Sets the folder to the selected row
	 *
	 * @param folder The folder object
	 */
	public void setFolder(GWTFolder folder) {
		// Row selected must be on table folder
		if (isFolderSelected()) {
			addRow(folder, true);
		}
	}

	/**
	 * Return true or false if actual selected row is document
	 *
	 * @return True or False if actual row is document type
	 */
	public boolean isDocumentSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), colDataIndex))) instanceof GWTDocument) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Return true or false if actual selected row is folder
	 *
	 * @return True or False if actual row is folder type
	 */
	public boolean isFolderSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			// Log.debug("ExtendedScrollTable isFolderSelected: key " + dataTable.getText(getSelectedRow(),7));
			// Log.debug("ExtendedScrollTable isFolderSelected: data size" + data.size());

			if (data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), colDataIndex))) instanceof GWTFolder) {
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
			// Log.debug("ExtendedScrollTable isFolderSelected: key " + dataTable.getText(getSelectedRow(),8));
			// Log.debug("ExtendedScrollTable isFolderSelected: data size" + data.size());

			if (data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), colDataIndex))) instanceof GWTMail) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Return true or false if it's a selected row
	 *
	 * @return True or false selected row
	 */
	public boolean isSelectedRow() {
		// Log.debug("isSelectedRow:");
		if (!dataTable.getSelectedRows().isEmpty()) {
			// Log.debug("isSelectedRow: true");
			return true;
		} else {
			// Log.debug("isSelectedRow: false");
			return false;
		}
	}

	/**
	 * Download documents
	 */
	public void downloadDocuments(boolean checkout, List<String> uuidList) {
		if (uuidList.size() > 0) {
			Util.downloadFilesByUUID(uuidList, (checkout ? "checkout" : ""));
		}
	}

	/**
	 * Download document
	 */
	public void downloadDocument(boolean checkout) {
		// Log.debug("downloadDocument()");
		if (isDocumentSelected()) {
			// Log.debug("jump to download");
			Util.downloadFileByUUID(getDocument().getUuid(), (checkout ? "checkout" : ""));
		}
		// Log.debug("downloadDocument: void");
	}

	/**
	 * Download mail
	 */
	public void downloadMail() {
		// Log.debug("downloadMail()");
		if (isMailSelected()) {
			// Log.debug("jump to download");
			Util.downloadFileByUUID(getMail().getUuid(), "");
		}
		// Log.debug("downloadMail: void");
	}

	/**
	 * Download document as PDF
	 */
	public void downloadDocumentPdf() {
		// Log.debug("downloadDocumentPdf()");
		if (isDocumentSelected()) {
			Log.debug("jump to download");
			Util.downloadFilePdf(getDocument().getUuid());
		}
		// Log.debug("downloadDocumentPdf: void");
	}

	/**
	 * print
	 */
	public void print() {
		// Log.debug("print()");
		if (isDocumentSelected()) {
			Log.debug("jump to download");
			Util.print(getDocument().getUuid());
		}
		// Log.debug("print: void");
	}

	/**
	 * Gets the checkout flag
	 *
	 * @return Checkout state
	 */
	public boolean isCheckout() {
		// Row selected must be on table documents only this can be checkout
		if (isDocumentSelected()) {
			return getDocument().isCheckedOut();
		} else {
			return false;
		}
	}

	/**
	 * Gets the locked flag
	 *
	 * @return locked state
	 */
	public boolean isLocked() {
		// Row selected must be on table documents only this can be checkout
		if (isDocumentSelected()) {
			return getDocument().isLocked();
		} else {
			return false;
		}
	}

	/**
	 * Sets the actual action on rows
	 *
	 * @param action The action
	 */
	public void setAction(int action) {
		rowAction = action;
	}

	/**
	 * Resets the row action
	 */
	public void resetAction() {
		rowAction = ACTION_NONE;
	}

	/**
	 * isDragged Returns true or false if is dragged
	 *
	 * @return Return dragged value
	 */
	private boolean isDragged() {
		return dragged;
	}

	/**
	 * unsetDraged
	 *
	 * Sets dragged flag to false;
	 */
	private void unsetDraged() {
		this.dragged = false;
		mouseDownX = 0;
		mouseDownY = 0;
	}

	/**
	 * hasRows
	 *
	 * @return has rows
	 */
	public boolean hasRows() {
		return dataTable.getRowCount() > 0;
	}

	/**
	 * selectAllMassive
	 */
	public void selectAllMassive() {
		massiveSelected = new ArrayList<Integer>();
		for (int i = 0; dataTable.getRowCount() > i; i++) {
			CheckBox checkBox = (CheckBox) dataTable.getWidget(i, colMassiveIndex);
			checkBox.setValue(true);
			massiveSelected.add(Integer.parseInt(dataTable.getText(i, colDataIndex)));
		}
		evaluateMergePdf();
	}

	/**
	 * selectAllFoldersMassive
	 */
	public void selectAllFoldersMassive() {
		massiveSelected = new ArrayList<Integer>();
		for (int i = 0; dataTable.getRowCount() > i; i++) {
			if (data.get(Integer.parseInt(dataTable.getText(i, colDataIndex))) instanceof GWTFolder) {
				CheckBox checkBox = (CheckBox) dataTable.getWidget(i, colMassiveIndex);
				checkBox.setValue(true);
				massiveSelected.add(Integer.parseInt(dataTable.getText(i, colDataIndex)));
			}
		}
	}

	/**
	 * selectAllDocumentsMassive
	 */
	public void selectAllDocumentsMassive() {
		massiveSelected = new ArrayList<Integer>();
		for (int i = 0; dataTable.getRowCount() > i; i++) {
			if (data.get(Integer.parseInt(dataTable.getText(i, colDataIndex))) instanceof GWTDocument) {
				CheckBox checkBox = (CheckBox) dataTable.getWidget(i, colMassiveIndex);
				checkBox.setValue(true);
				massiveSelected.add(Integer.parseInt(dataTable.getText(i, colDataIndex)));
			}
		}
		evaluateMergePdf();
	}

	/**
	 * selectAllMailsMassive
	 */
	public void selectAllMailsMassive() {
		massiveSelected = new ArrayList<Integer>();
		for (int i = 0; dataTable.getRowCount() > i; i++) {
			if (data.get(Integer.parseInt(dataTable.getText(i, colDataIndex))) instanceof GWTMail) {
				CheckBox checkBox = (CheckBox) dataTable.getWidget(i, colMassiveIndex);
				checkBox.setValue(true);
				massiveSelected.add(Integer.parseInt(dataTable.getText(i, colDataIndex)));
			}
		}
	}

	/**
	 * removeAllMassive
	 */
	public void removeAllMassive() {
		massiveSelected = new ArrayList<Integer>();
		for (int i = 0; dataTable.getRowCount() > i; i++) {
			CheckBox checkBox = (CheckBox) dataTable.getWidget(i, colMassiveIndex);
			checkBox.setValue(false);
		}
		evaluateMergePdf();
	}

	/**
	 * isMassive
	 *
	 * @return
	 */
	public boolean isMassive() {
		return (massiveSelected.size() > 0);
	}

	/**
	 * getAllSelectedPaths
	 *
	 * @return
	 */
	public List<String> getAllSelectedPaths() {
		List<String> paths = new ArrayList<String>();
		for (int i = 0; dataTable.getRowCount() > i; i++) {
			CheckBox checkBox = (CheckBox) dataTable.getWidget(i, colMassiveIndex);
			if (checkBox.getValue()) {
				Object obj = data.get(Integer.parseInt(dataTable.getText(i, colDataIndex)));
				if (obj instanceof GWTDocument) {
					paths.add(((GWTDocument) obj).getPath());
				} else if (obj instanceof GWTFolder) {
					paths.add(((GWTFolder) obj).getPath());
				} else if (obj instanceof GWTMail) {
					paths.add(((GWTMail) obj).getPath());
				}
			}
		}
		return paths;
	}

	/**
	 * getAllSelectedUUIDs
	 */
	public List<String> getAllSelectedUUIDs() {
		List<String> uuidList = new ArrayList<String>();

		for (int i = 0; dataTable.getRowCount() > i; i++) {
			CheckBox checkBox = (CheckBox) dataTable.getWidget(i, colMassiveIndex);

			if (checkBox.getValue()) {
				Object obj = data.get(Integer.parseInt(dataTable.getText(i, colDataIndex)));
				if (obj instanceof GWTDocument) {
					uuidList.add(((GWTDocument) obj).getUuid());
				} else if (obj instanceof GWTFolder) {
					uuidList.add(((GWTFolder) obj).getUuid());
				} else if (obj instanceof GWTMail) {
					uuidList.add(((GWTMail) obj).getUuid());
				}
			}
		}

		return uuidList;
	}

	/**
	 * getAllSelectedDocumentsUUIDs
	 */
	public List<String> getAllSelectedDocumentsUUIDs() {
		List<String> uuidList = new ArrayList<String>();

		for (int i = 0; dataTable.getRowCount() > i; i++) {
			CheckBox checkBox = (CheckBox) dataTable.getWidget(i, colMassiveIndex);

			if (checkBox.getValue()) {
				Object obj = data.get(Integer.parseInt(dataTable.getText(i, colDataIndex)));
				if (obj instanceof GWTDocument) {
					uuidList.add(((GWTDocument) obj).getUuid());
				}
			}
		}

		return uuidList;
	}

	/**
	 * getAllSelectedDocumentsPaths
	 */
	public List<String> getAllSelectedDocumentsPaths() {
		List<String> pathList = new ArrayList<String>();

		for (int i = 0; dataTable.getRowCount() > i; i++) {
			CheckBox checkBox = (CheckBox) dataTable.getWidget(i, colMassiveIndex);

			if (checkBox.getValue()) {
				Object obj = data.get(Integer.parseInt(dataTable.getText(i, colDataIndex)));
				if (obj instanceof GWTDocument) {
					pathList.add(((GWTDocument) obj).getPath());
				}
			}
		}

		return pathList;
	}

	/**
	 * getAllSelectedMailUUIDs
	 */
	public List<String> getAllSelectedMailUUIDs() {
		List<String> uuidList = new ArrayList<String>();

		for (int i = 0; dataTable.getRowCount() > i; i++) {
			CheckBox checkBox = (CheckBox) dataTable.getWidget(i, colMassiveIndex);

			if (checkBox.getValue()) {
				Object obj = data.get(Integer.parseInt(dataTable.getText(i, colDataIndex)));
				if (obj instanceof GWTMail) {
					uuidList.add(((GWTMail) obj).getUuid());
				}
			}
		}

		return uuidList;
	}

	/**
	 * getAllSelectedPdfDocuments
	 *
	 * @return
	 */
	public List<GWTDocument> getAllSelectedPdfDocuments() {
		List<GWTDocument> docs = new ArrayList<GWTDocument>();
		for (int i = 0; dataTable.getRowCount() > i; i++) {
			CheckBox checkBox = (CheckBox) dataTable.getWidget(i, colMassiveIndex);
			if (checkBox.getValue()) {
				Object obj = data.get(Integer.parseInt(dataTable.getText(i, colDataIndex)));
				if (obj instanceof GWTDocument) {
					GWTDocument doc = (GWTDocument) obj;
					if (doc.getMimeType().equals("application/pdf")) {
						docs.add(doc);
					}
				}
			}
		}
		return docs;
	}

	/**
	 * evaluateMergePdf
	 */
	private void evaluateMergePdf() {
		if (isMassive() &&
				(Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_THESAURUS &&
						Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_CATEGORIES &&
						Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_METADATA &&
						Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_TRASH) &&
				getAllSelectedPdfDocuments().size() > 1) {
			Main.get().mainPanel.topPanel.toolBar.enablePdfMerge();
		} else {
			Main.get().mainPanel.topPanel.toolBar.disablePdfMerge();
		}
	}

	/**
	 * getColDataIndex
	 *
	 * @return
	 */
	public int getColDataIndex() {
		return colDataIndex;
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

	/**
	 * setColMassiveIndex
	 *
	 * @param colMassiveIndex
	 */
	public void setColMassiveIndex(int colMassiveIndex) {
		this.colMassiveIndex = colMassiveIndex;
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
}
