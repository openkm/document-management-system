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

package com.openkm.frontend.client.widget.dashboard.keymap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.*;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.dashboard.ImageHover;
import com.openkm.frontend.client.widget.dashboard.Score;
import com.openkm.frontend.client.widget.dashboard.Status;

import java.util.*;

/**
 * KeyMapTable
 *
 * @author jllort
 *
 */
public class KeyMapTable extends Composite {
	public static final int VISIBLE_SMALL = 0;
	public static final int VISIBLE_MEDIUM = 1;
	public static final int VISIBLE_BIG = 2;

	private FlexTable table;
	private List<FlexTable> tableDocumentList;
	private List<FlexTable> tableFolderList;
	private List<FlexTable> tableMailList;
	private List<HorizontalPanel> hKeyPanelList;
	private int visibleStatus = VISIBLE_MEDIUM;
	private List<String> firtRowList;
	public Status status;

	/**
	 * KeyMapTable
	 */
	public KeyMapTable() {
		status = new Status();
		status.setStyleName("okm-StatusPopup");

		table = new FlexTable();
		tableDocumentList = new ArrayList<FlexTable>();
		tableFolderList = new ArrayList<FlexTable>();
		tableMailList = new ArrayList<FlexTable>();
		hKeyPanelList = new ArrayList<HorizontalPanel>();
		firtRowList = new ArrayList<String>();
		visibleStatus = VISIBLE_MEDIUM;

		table.setWidth("100%");

		table.setCellSpacing(0);
		table.setCellPadding(2);

		initWidget(table);
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
		Collection<String> selectedKeyList = Main.get().mainPanel.dashboard.keyMapDashboard.getFiltering();
		int rows = table.getRowCount();
		int firstRow = rows;

		firtRowList.add("" + firstRow);
		GWTDocument doc = new GWTDocument();

		if (gwtQueryResult.getDocument() != null) {
			doc = gwtQueryResult.getDocument();
		} else if (gwtQueryResult.getAttachment() != null) {
			doc = gwtQueryResult.getAttachment();
		}

		final String docPath = doc.getPath();
		Image gotoDocument = new Image("img/icon/actions/goto_document.gif");
		gotoDocument.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CommonUI.openPath(Util.getParent(docPath), docPath);
			}

		});
		gotoDocument.setTitle(Main.i18n("dashboard.keyword.goto.document"));
		gotoDocument.setStyleName("okm-KeyMap-ImageHover");
		table.setWidget(rows, 0, gotoDocument);

		if (doc.isAttachment()) {
			SimplePanel sp = new SimplePanel(); // Solves some middle alignament problem derived from mimeImageHTML method
			sp.add(new HTML(Util.imageItemHTML("img/email_attach.gif") + Util.mimeImageHTML(doc.getMimeType())));
			table.setWidget(rows, 1, sp);
		} else {
			SimplePanel sp = new SimplePanel(); // Solves some middle alignament problem derived from mimeImageHTML method
			sp.add(new HTML(Util.mimeImageHTML(doc.getMimeType())));
			table.setWidget(rows, 1, sp);
		}
		Hyperlink hLink = new Hyperlink();
		hLink.setHTML(doc.getName());

		// On attachment case must remove last folder path, because it's internal usage not for visualization
		if (doc.isAttachment()) {
			hLink.setTitle(doc.getParentPath().substring(0, doc.getParentPath().lastIndexOf("/")));
		} else {
			hLink.setTitle(doc.getParentPath());
		}

		table.setWidget(rows, 2, hLink);

		// Format
		table.getCellFormatter().setHorizontalAlignment(rows, 0, HasAlignment.ALIGN_CENTER);
		table.getCellFormatter().setHorizontalAlignment(rows, 1, HasAlignment.ALIGN_RIGHT);
		table.getCellFormatter().setHorizontalAlignment(rows, 2, HasAlignment.ALIGN_LEFT);
		table.getCellFormatter().setVerticalAlignment(rows, 0, HasAlignment.ALIGN_MIDDLE);
		table.getCellFormatter().setVerticalAlignment(rows, 0, HasAlignment.ALIGN_MIDDLE);
		table.getCellFormatter().setVerticalAlignment(rows, 2, HasAlignment.ALIGN_MIDDLE);
		table.getFlexCellFormatter().setWidth(rows, 0, "24px");
		table.getFlexCellFormatter().setWidth(rows, 1, "47px");

		for (int i = 0; i < 2; i++) {
			table.getCellFormatter().addStyleName(rows, i, "okm-DisableSelect");
		}

		// Writing detail
		rows++; // Next row line
		FlexTable tableDocument = new FlexTable();
		FlexTable tableProperties = new FlexTable();
		FlexTable tableSubscribedUsers = new FlexTable();
		tableDocument.setWidget(0, 0, tableProperties);
		tableDocument.setHTML(0, 1, "");
		tableDocument.setWidget(0, 2, tableSubscribedUsers);
		tableDocument.getFlexCellFormatter().setVerticalAlignment(0, 0, HasAlignment.ALIGN_TOP);
		tableDocument.getFlexCellFormatter().setVerticalAlignment(0, 2, HasAlignment.ALIGN_TOP);
		tableDocument.getCellFormatter().setWidth(0, 0, "75%");
		tableDocument.getCellFormatter().setWidth(0, 1, "25px");
		tableDocument.getCellFormatter().setWidth(0, 2, "25%");

		tableDocument.setWidth("100%");
		table.setWidget(rows, 0, tableDocument);
		table.getFlexCellFormatter().setColSpan(rows, 0, 3);
		table.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_LEFT);

		tableDocument.setStyleName("okm-DisableSelect");
		tableProperties.setStyleName("okm-DisableSelect");
		tableSubscribedUsers.setStyleName("okm-DisableSelect");

		tableProperties.setHTML(0, 0, "<b>" + Main.i18n("document.folder") + "</b>");
		tableProperties.setHTML(0, 1, doc.getParentPath());
		tableProperties.setHTML(1, 0, "<b>" + Main.i18n("document.size") + "</b>");
		tableProperties.setHTML(1, 1, Util.formatSize(doc.getActualVersion().getSize()));
		tableProperties.setHTML(2, 0, "<b>" + Main.i18n("document.created") + "</b>");
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		tableProperties.setHTML(2, 1, dtf.format(doc.getCreated()) + " " + Main.i18n("document.by") + " " + doc.getAuthor());
		tableProperties.setHTML(3, 0, "<b>" + Main.i18n("document.lastmodified") + "</b>");
		tableProperties.setHTML(3, 1, dtf.format(doc.getLastModified()) + " " + Main.i18n("document.by") + " " + doc.getActualVersion().getAuthor());
		tableProperties.setHTML(4, 0, "<b>" + Main.i18n("document.mimetype") + "</b>");
		tableProperties.setHTML(4, 1, doc.getMimeType());
		tableProperties.setHTML(5, 0, "<b>" + Main.i18n("document.status") + "</b>");
		if (doc.isCheckedOut()) {
			tableProperties.setHTML(5, 1, Main.i18n("document.status.checkout") + " " + doc.getLockInfo().getOwner());
		} else if (doc.isLocked()) {
			tableProperties.setHTML(5, 1, Main.i18n("document.status.locked") + " " + doc.getLockInfo().getOwner());
		} else {
			tableProperties.setHTML(5, 1, Main.i18n("document.status.normal"));
		}
		tableProperties.setHTML(6, 0, "<b>" + Main.i18n("document.subscribed") + "</b>");
		if (doc.isSubscribed()) {
			tableProperties.setHTML(6, 1, Main.i18n("document.subscribed.yes"));
		} else {
			tableProperties.setHTML(6, 1, Main.i18n("document.subscribed.no"));
		}

		// Sets wordWrap for al rows
		for (int i = 0; i < 7; i++) {
			setRowWordWarp(i, 2, false, tableProperties);
		}

		// Setting subscribers
		tableSubscribedUsers.setHTML(0, 0, "<b>" + Main.i18n("document.subscribed.users") + "<b>");
		setRowWordWarp(0, 1, false, tableSubscribedUsers);

		// Sets the folder subscribers
		for (GWTUser subscriptor : doc.getSubscriptors()) {
			tableSubscribedUsers.setHTML(tableSubscribedUsers.getRowCount(), 0, subscriptor.getUsername());
			setRowWordWarp(tableSubscribedUsers.getRowCount() - 1, 1, false, tableSubscribedUsers);
		}

		HorizontalPanel hKeyPanel = addKeywords(table, doc.getKeywords(), selectedKeyList); // Drawing keywords

		// Setting visibility
		switch (visibleStatus) {
			case VISIBLE_SMALL:
				tableDocument.setVisible(false);
				hKeyPanel.setVisible(false);
				table.getCellFormatter().addStyleName(firstRow, 0, "okm-Table-BottomBorder");
				table.getCellFormatter().addStyleName(firstRow, 1, "okm-Table-BottomBorder");
				table.getCellFormatter().addStyleName(firstRow, 2, "okm-Table-BottomBorder");
				break;
			case VISIBLE_MEDIUM:
				tableDocument.setVisible(false);
				hKeyPanel.setVisible(true);
				break;
			case VISIBLE_BIG:
				tableDocument.setVisible(true);
				hKeyPanel.setVisible(true);
				break;
		}

		// Saving object for refreshing language and setting visible ( true / false )
		tableDocumentList.add(tableDocument);
		hKeyPanelList.add(hKeyPanel);
	}

	/**
	 * Lang refreshing
	 */
	public void langRefresh() {
		// Documents
		for (Iterator<FlexTable> it = tableDocumentList.iterator(); it.hasNext(); ) {
			FlexTable tableDocument = it.next();
			FlexTable tableProperties = (FlexTable) tableDocument.getWidget(0, 0);
			FlexTable tableSubscribedUsers = (FlexTable) tableDocument.getWidget(0, 2);

			tableProperties.setHTML(1, 0, "<b>" + Main.i18n("document.folder") + "</b>");
			tableProperties.setHTML(2, 0, "<b>" + Main.i18n("document.size") + "</b>");
			tableProperties.setHTML(3, 0, "<b>" + Main.i18n("document.created") + "</b>");
			tableProperties.setHTML(4, 0, "<b>" + Main.i18n("document.lastmodified") + "</b>");
			tableProperties.setHTML(5, 0, "<b>" + Main.i18n("document.mimetype") + "</b>");
			tableProperties.setHTML(6, 0, "<b>" + Main.i18n("document.status") + "</b>");
			tableProperties.setHTML(7, 0, "<b>" + Main.i18n("document.subscribed") + "</b>");

			tableSubscribedUsers.setHTML(0, 0, "<b>" + Main.i18n("document.subscribed.users") + "<b>");
		}
		// Mails
		for (Iterator<FlexTable> it = tableMailList.iterator(); it.hasNext(); ) {
			FlexTable tableMail = it.next();
			FlexTable tableProperties = (FlexTable) tableMail.getWidget(0, 0);

			tableProperties.setHTML(0, 0, "<b>" + Main.i18n("mail.folder") + "</b>");
			tableProperties.setHTML(1, 0, "<b>" + Main.i18n("mail.size") + "</b>");
			tableProperties.setHTML(2, 0, "<b>" + Main.i18n("mail.created") + "</b>");
			tableProperties.setHTML(3, 0, "<b>" + Main.i18n("mail.mimetype") + "</b>");
		}
	}

	/**
	 * Changes the visualization detail
	 *
	 * @param value The new visualization detail
	 */
	public void changeVisibilityDetail(int value) {
		visibleStatus = value;
		refreshingVisibilityDetail();
	}

	/**
	 * refreshing the visualizationa panels
	 */
	private void refreshingVisibilityDetail() {
		// Adding or removing style on first column to showing border when other panells are unvisible
		if (visibleStatus == VISIBLE_SMALL) {
			for (String row : firtRowList) {
				int firstRow = Integer.parseInt(row);
				table.getCellFormatter().addStyleName(firstRow, 0, "okm-Table-BottomBorder");
				table.getCellFormatter().addStyleName(firstRow, 1, "okm-Table-BottomBorder");
				table.getCellFormatter().addStyleName(firstRow, 2, "okm-Table-BottomBorder");
			}
		} else {
			for (String row : firtRowList) {
				int firstRow = Integer.parseInt(row);
				table.getCellFormatter().removeStyleName(firstRow, 0, "okm-Table-BottomBorder");
				table.getCellFormatter().removeStyleName(firstRow, 1, "okm-Table-BottomBorder");
				table.getCellFormatter().removeStyleName(firstRow, 2, "okm-Table-BottomBorder");
			}
		}

		boolean visible = false;
		switch (visibleStatus) {
			case VISIBLE_SMALL:
			case VISIBLE_MEDIUM:
				visible = false;
				break;

			case VISIBLE_BIG:
				visible = true;
				break;
		}

		// Setting document properties visibility
		for (FlexTable tableDocument : tableDocumentList) {
			// Setting visibility
			tableDocument.setVisible(visible);
		}
		// Setting mail properties visibility
		for (FlexTable tableFolder : tableFolderList) {
			// Setting visibility
			tableFolder.setVisible(visible);
		}
		// Setting mail properties visibility
		for (FlexTable tableMail : tableMailList) {
			// Setting visibility
			tableMail.setVisible(visible);
		}

		// Setting key panel visibility
		for (HorizontalPanel hKeyPanel : hKeyPanelList) {
			switch (visibleStatus) {
				case VISIBLE_SMALL:
					hKeyPanel.setVisible(false);
					break;
				case VISIBLE_MEDIUM:
					hKeyPanel.setVisible(true);
					break;
				case VISIBLE_BIG:
					hKeyPanel.setVisible(true);
					break;
			}
		}
	}

	/**
	 * Gets the actual detail
	 *
	 * @return The actual detail 
	 */
	public int getActualDetail() {
		return visibleStatus;
	}

	/**
	 * Adding folder
	 *
	 * @param gwtQueryResult Query result
	 * @param score The folder score
	 */
	private void addFolderRow(GWTQueryResult gwtQueryResult, Score score) {
		Collection<String> selectedKeyList = Main.get().mainPanel.dashboard.keyMapDashboard.getFiltering();
		int rows = table.getRowCount();
		int firstRow = rows;

		firtRowList.add("" + firstRow);

		GWTFolder folder = new GWTFolder();
		if (gwtQueryResult.getFolder() != null) {
			folder = gwtQueryResult.getFolder();
		}
		final String fldPath = folder.getPath();

		Image gotoFolder = new Image("img/icon/actions/goto_folder.gif");
		gotoFolder.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CommonUI.openPath(fldPath, null);
			}

		});
		gotoFolder.setTitle(Main.i18n("dashboard.keyword.goto.folder"));
		gotoFolder.setStyleName("okm-KeyMap-ImageHover");
		table.setWidget(rows, 0, gotoFolder);

		// Solves some middle alignament problem derived from mimeImageHTML method
		SimplePanel sp = new SimplePanel();

		if (folder.isHasChildren()) {
			sp.add(new HTML(Util.imageItemHTML("img/menuitem_childs.gif")));
		} else {
			sp.add(new HTML(Util.imageItemHTML("img/menuitem_empty.gif")));
		}

		table.setWidget(rows, 1, sp);
		Hyperlink hLink = new Hyperlink();
		hLink.setHTML(folder.getName());
		hLink.setTitle(folder.getPath());
		table.setWidget(rows, 2, hLink);

		// Format
		table.getCellFormatter().setHorizontalAlignment(rows, 0, HasAlignment.ALIGN_CENTER);
		table.getCellFormatter().setHorizontalAlignment(rows, 1, HasAlignment.ALIGN_RIGHT);
		table.getCellFormatter().setHorizontalAlignment(rows, 2, HasAlignment.ALIGN_LEFT);
		table.getCellFormatter().setVerticalAlignment(rows, 0, HasAlignment.ALIGN_MIDDLE);
		table.getCellFormatter().setVerticalAlignment(rows, 0, HasAlignment.ALIGN_MIDDLE);
		table.getCellFormatter().setVerticalAlignment(rows, 2, HasAlignment.ALIGN_MIDDLE);
		table.getFlexCellFormatter().setWidth(rows, 0, "24px");
		table.getFlexCellFormatter().setWidth(rows, 1, "47px");

		for (int i = 0; i < 2; i++) {
			table.getCellFormatter().addStyleName(rows, i, "okm-DisableSelect");
		}

		// Writing detail
		rows++; // Next row line
		FlexTable tableFolder = new FlexTable();
		FlexTable tableProperties = new FlexTable();
		FlexTable tableSubscribedUsers = new FlexTable();
		tableFolder.setWidget(0, 0, tableProperties);
		tableFolder.setHTML(0, 1, "");
		tableFolder.setWidget(0, 2, tableSubscribedUsers);
		tableFolder.getFlexCellFormatter().setVerticalAlignment(0, 0, HasAlignment.ALIGN_TOP);
		tableFolder.getFlexCellFormatter().setVerticalAlignment(0, 2, HasAlignment.ALIGN_TOP);
		tableFolder.getCellFormatter().setWidth(0, 0, "75%");
		tableFolder.getCellFormatter().setWidth(0, 1, "25px");
		tableFolder.getCellFormatter().setWidth(0, 2, "25%");

		tableFolder.setWidth("100%");
		table.setWidget(rows, 0, tableFolder);
		table.getFlexCellFormatter().setColSpan(rows, 0, 3);
		table.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_LEFT);

		tableFolder.setStyleName("okm-DisableSelect");
		tableProperties.setStyleName("okm-DisableSelect");
		tableSubscribedUsers.setStyleName("okm-DisableSelect");

		tableProperties.setHTML(0, 0, "<b>" + Main.i18n("folder.name") + "</b>");
		tableProperties.setHTML(0, 1, folder.getName());
		tableProperties.setHTML(1, 0, "<b>" + Main.i18n("folder.parent") + "</b>");
		tableProperties.setHTML(1, 1, folder.getParentPath());
		tableProperties.setHTML(2, 0, "<b>" + Main.i18n("folder.created") + "</b>");
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		tableProperties.setHTML(2, 1, dtf.format(folder.getCreated()) + " " + Main.i18n("folder.by") + " " + folder.getAuthor());
		tableProperties.setHTML(3, 0, "<b>" + Main.i18n("document.subscribed") + "</b>");
		if (folder.isSubscribed()) {
			tableProperties.setHTML(3, 1, Main.i18n("document.subscribed.yes"));
		} else {
			tableProperties.setHTML(3, 1, Main.i18n("document.subscribed.no"));
		}

		// Sets wordWrap for al rows
		for (int i = 0; i < 4; i++) {
			setRowWordWarp(i, 2, false, tableProperties);
		}

		// Setting subscribers
		tableSubscribedUsers.setHTML(0, 0, "<b>" + Main.i18n("folder.subscribed.users") + "<b>");
		setRowWordWarp(0, 1, false, tableSubscribedUsers);

		// Sets the folder subscribers
		for (GWTUser subscriptor : folder.getSubscriptors()) {
			tableSubscribedUsers.setHTML(tableSubscribedUsers.getRowCount(), 0, subscriptor.getUsername());
			setRowWordWarp(tableSubscribedUsers.getRowCount() - 1, 1, false, tableSubscribedUsers);
		}

		HorizontalPanel hKeyPanel = addKeywords(table, folder.getKeywords(), selectedKeyList); // Drawing keywords

		// Setting visibility
		switch (visibleStatus) {
			case VISIBLE_SMALL:
				tableFolder.setVisible(false);
				hKeyPanel.setVisible(false);
				table.getCellFormatter().addStyleName(firstRow, 0, "okm-Table-BottomBorder");
				table.getCellFormatter().addStyleName(firstRow, 1, "okm-Table-BottomBorder");
				table.getCellFormatter().addStyleName(firstRow, 2, "okm-Table-BottomBorder");
				break;
			case VISIBLE_MEDIUM:
				tableFolder.setVisible(false);
				hKeyPanel.setVisible(true);
				break;
			case VISIBLE_BIG:
				tableFolder.setVisible(true);
				hKeyPanel.setVisible(true);
				break;
		}

		// Saving object for refreshing language and setting visible ( true / false )
		tableFolderList.add(tableFolder);
		hKeyPanelList.add(hKeyPanel);
	}

	/**
	 * Adding mail
	 *
	 * @param gwtQueryResult Query result
	 * @param score The mail score
	 */
	private void addMailRow(GWTQueryResult gwtQueryResult, Score score) {
		Collection<String> selectedKeyList = Main.get().mainPanel.dashboard.keyMapDashboard.getFiltering();
		int rows = table.getRowCount();
		int firstRow = rows;

		firtRowList.add("" + firstRow);

		GWTMail mail = gwtQueryResult.getMail();
		;
		final String mailPath = mail.getPath();

		Image gotoMail = new Image("img/icon/actions/goto_document.gif");
		gotoMail.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CommonUI.openPath(Util.getParent(mailPath), mailPath);
			}

		});
		gotoMail.setTitle(Main.i18n("dashboard.keyword.goto.mail"));
		gotoMail.setStyleName("okm-KeyMap-ImageHover");
		table.setWidget(rows, 0, gotoMail);

		SimplePanel sp = new SimplePanel(); // Solves some middle alignament problem derived from mimeImageHTML method
		if (mail.getAttachments().size() > 0) {
			sp.add(new HTML(Util.imageItemHTML("img/email_attach.gif")));
		} else {
			sp.add(new HTML(Util.imageItemHTML("img/email.gif")));
		}
		table.setWidget(rows, 1, sp);
		Hyperlink hLink = new Hyperlink();
		hLink.setHTML(mail.getSubject());
		// On attachemt case must remove last folder path, because it's internal usage not for visualization
		hLink.setTitle(mail.getSubject());

		table.setWidget(rows, 2, hLink);

		// Format
		table.getCellFormatter().setHorizontalAlignment(rows, 0, HasAlignment.ALIGN_CENTER);
		table.getCellFormatter().setHorizontalAlignment(rows, 1, HasAlignment.ALIGN_RIGHT);
		table.getCellFormatter().setHorizontalAlignment(rows, 2, HasAlignment.ALIGN_LEFT);
		table.getCellFormatter().setVerticalAlignment(rows, 0, HasAlignment.ALIGN_MIDDLE);
		table.getCellFormatter().setVerticalAlignment(rows, 0, HasAlignment.ALIGN_MIDDLE);
		table.getCellFormatter().setVerticalAlignment(rows, 2, HasAlignment.ALIGN_MIDDLE);
		table.getFlexCellFormatter().setWidth(rows, 0, "24px");
		table.getFlexCellFormatter().setWidth(rows, 1, "47px");

		for (int i = 0; i < 2; i++) {
			table.getCellFormatter().addStyleName(rows, i, "okm-DisableSelect");
		}

		// Writing detail
		rows++; // Next row line
		FlexTable tableMail = new FlexTable();
		FlexTable tableProperties = new FlexTable();
		FlexTable tableSubscribedUsers = new FlexTable();
		tableMail.setWidget(0, 0, tableProperties);
		tableMail.setHTML(0, 1, "");
		tableMail.setWidget(0, 2, tableSubscribedUsers);
		tableMail.getFlexCellFormatter().setVerticalAlignment(0, 0, HasAlignment.ALIGN_TOP);
		tableMail.getFlexCellFormatter().setVerticalAlignment(0, 2, HasAlignment.ALIGN_TOP);
		tableMail.getCellFormatter().setWidth(0, 0, "75%");
		tableMail.getCellFormatter().setWidth(0, 1, "25px");
		tableMail.getCellFormatter().setWidth(0, 2, "25%");

		tableMail.setWidth("100%");
		table.setWidget(rows, 0, tableMail);
		table.getFlexCellFormatter().setColSpan(rows, 0, 3);
		table.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_LEFT);

		tableMail.setStyleName("okm-DisableSelect");
		tableProperties.setStyleName("okm-DisableSelect");
		tableSubscribedUsers.setStyleName("okm-DisableSelect");

		tableProperties.setHTML(0, 0, "<b>" + Main.i18n("mail.folder") + "</b>");
		tableProperties.setHTML(0, 1, mail.getParentPath());
		tableProperties.setHTML(1, 0, "<b>" + Main.i18n("mail.size") + "</b>");
		tableProperties.setHTML(1, 1, Util.formatSize(mail.getSize()));
		tableProperties.setHTML(2, 0, "<b>" + Main.i18n("mail.created") + "</b>");
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		tableProperties.setHTML(2, 1, dtf.format(mail.getCreated()) + " " + Main.i18n("mail.by") + " " + mail.getAuthor());
		tableProperties.setHTML(3, 0, "<b>" + Main.i18n("mail.mimetype") + "</b>");
		tableProperties.setHTML(3, 1, mail.getMimeType());

		// Sets wordWrap for al rows
		for (int i = 0; i < 4; i++) {
			setRowWordWarp(i, 2, false, tableProperties);
		}

		HorizontalPanel hKeyPanel = addKeywords(table, mail.getKeywords(), selectedKeyList); // Drawing keywords

		// Setting visibility
		switch (visibleStatus) {
			case VISIBLE_SMALL:
				tableMail.setVisible(false);
				hKeyPanel.setVisible(false);
				table.getCellFormatter().addStyleName(firstRow, 0, "okm-Table-BottomBorder");
				table.getCellFormatter().addStyleName(firstRow, 1, "okm-Table-BottomBorder");
				table.getCellFormatter().addStyleName(firstRow, 2, "okm-Table-BottomBorder");
				break;
			case VISIBLE_MEDIUM:
				tableMail.setVisible(false);
				hKeyPanel.setVisible(true);
				break;
			case VISIBLE_BIG:
				tableMail.setVisible(true);
				hKeyPanel.setVisible(true);
				break;
		}

		tableMailList.add(tableMail);
		hKeyPanelList.add(hKeyPanel);
	}

	/**
	 * removeAllRows
	 */
	private void removeAllRows() {
		while (table.getRowCount() > 0) {
			table.removeRow(0);
		}
	}

	/**
	 * Resets the values
	 */
	public void reset() {
		removeAllRows();
		tableDocumentList = new ArrayList<FlexTable>();
		hKeyPanelList = new ArrayList<HorizontalPanel>();
		firtRowList = new ArrayList<String>();
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
	 * Sets the refreshing
	 */
	public void setRefreshing() {
		int left = Main.get().mainPanel.dashboard.keyMapDashboard.scrollTable.getAbsoluteLeft() +
				(Main.get().mainPanel.dashboard.keyMapDashboard.scrollTable.getOffsetWidth() / 2);
		int top = Main.get().mainPanel.dashboard.keyMapDashboard.scrollTable.getAbsoluteTop() +
				(Main.get().mainPanel.dashboard.keyMapDashboard.scrollTable.getOffsetHeight() / 2);
		status.setFlag_getDashboard();
		status.refresh(left, top);
	}

	/**
	 * Unsets the refreshing
	 */
	public void unsetRefreshing() {
		status.unsetFlag_getDashboard();
	}

	/**
	 * addKeywords
	 *
	 * @param table
	 * @param keywords
	 * @param selectedKeyList
	 * @return
	 */
	private HorizontalPanel addKeywords(FlexTable table, Set<String> keywords, Collection<String> selectedKeyList) {
		int rows = table.getRowCount();
		// Writing keys
		rows++; // Next row line
		HorizontalPanel hKeyPanel = new HorizontalPanel();
		table.setWidget(rows, 0, hKeyPanel);
		table.getFlexCellFormatter().setColSpan(rows, 0, 3);
		table.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		table.getCellFormatter().addStyleName(rows, 0, "okm-Table-BottomBorder");

		for (final String keyword : keywords) {
			// First adds only new keywords
			if (!selectedKeyList.contains(keyword)) {
				HorizontalPanel externalPanel = new HorizontalPanel();
				HorizontalPanel hPanel = new HorizontalPanel();
				HTML space = new HTML();
				ImageHover add = new ImageHover("img/icon/actions/add_disabled.gif", "img/icon/actions/add.gif");
				add.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						// remove keyword on all keyword panels
						Main.get().mainPanel.dashboard.keyMapDashboard.selectKey(keyword);
					}
				});
				add.setStyleName("okm-KeyMap-ImageHover");
				hPanel.add(new HTML(keyword));
				hPanel.add(space);
				hPanel.add(add);
				hPanel.setCellWidth(space, "6px");
				hPanel.setStyleName("okm-KeyMap-Gray");
				HTML space1 = new HTML();
				externalPanel.add(hPanel);
				externalPanel.add(space1);
				externalPanel.setCellWidth(space1, "6px");
				hKeyPanel.add(externalPanel);
			}
		}

		for (final String keyword : selectedKeyList) {
			// Last adding selected keywords
			HorizontalPanel externalPanel = new HorizontalPanel();
			HorizontalPanel hPanel = new HorizontalPanel();
			HTML space = new HTML();
			ImageHover add = new ImageHover("img/icon/actions/delete_disabled.gif", "img/icon/actions/delete.gif");
			add.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// remove keyword on all keyword panels
					Main.get().mainPanel.dashboard.keyMapDashboard.removeKey(keyword);
				}
			});
			add.setStyleName("okm-KeyMap-ImageHover");
			hPanel.add(new HTML(keyword));
			hPanel.add(space);
			hPanel.add(add);
			hPanel.setCellWidth(space, "6px");
			hPanel.setStyleName("okm-KeyMap-Selected");
			HTML space1 = new HTML();
			externalPanel.add(hPanel);
			externalPanel.add(space1);
			externalPanel.setCellWidth(space1, "6px");
			hKeyPanel.add(externalPanel);
		}

		return hKeyPanel;
	}
}