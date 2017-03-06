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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTMimeType;
import com.openkm.frontend.client.util.OKMBundleResources;

import java.util.List;

/**
 * @author jllort
 *
 */
public class SearchAdvanced extends Composite {
	private ScrollPanel scrollPanel;
	private FlexTable table;
	public HorizontalPanel pathExplorerPanel;
	public HorizontalPanel categoryExplorerPanel;
	public TextBox path;
	public Image pathExplorer;
	public Image pathClean;
	public Image categoryExplorer;
	public Image categoryClean;
	public FolderSelectPopup folderSelectPopup;
	public TextBox categoryPath;
	public String categoryUuid = "";
	public HorizontalPanel typePanel;
	public CheckBox typeDocument;
	public CheckBox typeFolder;
	public CheckBox typeMail;
	public FlexTable tableMail;
	public ListBox mimeTypes;
	public TextBox from;
	public TextBox to;
	public TextBox subject;
	public HTML mailText;

	/**
	 * SearchAdvanced
	 */
	public SearchAdvanced() {
		table = new FlexTable();
		scrollPanel = new ScrollPanel(table);

		// Sets the folder explorer
		folderSelectPopup = new FolderSelectPopup();
		pathExplorerPanel = new HorizontalPanel();
		path = new TextBox();
		path.setWidth("320px");
		path.setReadOnly(true);
		pathExplorer = new Image(OKMBundleResources.INSTANCE.folderExplorer());
		pathClean = new Image(OKMBundleResources.INSTANCE.cleanIcon());
		pathClean.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				path.setText("");
			}
		});
		pathClean.setStyleName("okm-Hyperlink");

		pathExplorerPanel.add(path);
		pathExplorerPanel.add(new HTML("&nbsp;"));
		pathExplorerPanel.add(pathExplorer);
		pathExplorerPanel.add(new HTML("&nbsp;"));
		pathExplorerPanel.add(pathClean);

		pathExplorer.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				folderSelectPopup.show(false);
			}
		});

		pathExplorerPanel.setCellVerticalAlignment(pathExplorer, HasAlignment.ALIGN_MIDDLE);

		// Sets the category explorer
		categoryExplorerPanel = new HorizontalPanel();
		categoryUuid = "";
		categoryPath = new TextBox();
		categoryPath.setWidth("320px");
		categoryPath.setReadOnly(true);
		categoryExplorer = new Image(OKMBundleResources.INSTANCE.folderExplorer());
		categoryClean = new Image(OKMBundleResources.INSTANCE.cleanIcon());
		categoryClean.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				categoryPath.setText("");
				categoryUuid = "";
			}

		});
		categoryClean.setStyleName("okm-Hyperlink");

		categoryExplorerPanel.add(categoryPath);
		categoryExplorerPanel.add(new HTML("&nbsp;"));
		categoryExplorerPanel.add(categoryExplorer);
		categoryExplorerPanel.add(new HTML("&nbsp;"));
		categoryExplorerPanel.add(categoryClean);

		categoryExplorer.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				folderSelectPopup.show(true);
			}
		});

		categoryExplorerPanel.setCellVerticalAlignment(categoryExplorer, HasAlignment.ALIGN_MIDDLE);

		// Sets type document
		tableMail = new FlexTable();
		typePanel = new HorizontalPanel();
		typeDocument = new CheckBox(Main.i18n("search.type.document"));
		typeDocument.setValue(true);
		typeFolder = new CheckBox(Main.i18n("search.type.folder"));
		typeFolder.setValue(false);
		typeMail = new CheckBox(Main.i18n("search.type.mail"));
		typeMail.setValue(false);

		typeMail.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (typeMail.getValue()) {
					mailText.setVisible(true);
					tableMail.setVisible(true);
				} else {
					mailText.setVisible(false);
					tableMail.setVisible(false);
				}
			}
		});

		typePanel.add(typeDocument);
		typePanel.add(new HTML("&nbsp;"));
		typePanel.add(typeFolder);
		typePanel.add(new HTML("&nbsp;"));
		typePanel.add(typeMail);
		typePanel.add(new HTML("&nbsp;"));

		// Sets mime types values
		mimeTypes = new ListBox();
		mimeTypes.addItem(" ", "");

		mimeTypes.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				Main.get().mainPanel.search.searchBrowser.searchIn.searchControl.evaluateSearchButtonVisible();
			}
		});

		mailText = new HTML(Main.i18n("search.type.mail"));
		mailText.setVisible(false);
		table.setHTML(1, 0, Main.i18n("search.folder"));
		table.setWidget(1, 1, pathExplorerPanel);
		table.setHTML(2, 0, Main.i18n("search.category"));
		table.setWidget(2, 1, categoryExplorerPanel);
		table.setHTML(3, 0, Main.i18n("search.type"));
		table.setWidget(3, 1, typePanel);
		table.setHTML(4, 0, Main.i18n("search.mimetype"));
		table.setWidget(4, 1, mimeTypes);
		table.setWidget(5, 0, mailText);
		table.setWidget(5, 1, tableMail);
		table.getCellFormatter().setVerticalAlignment(5, 0, HasAlignment.ALIGN_TOP);

		// Adding mail search params
		from = new TextBox();
		to = new TextBox();
		subject = new TextBox();
		tableMail.setHTML(0, 0, Main.i18n("mail.from"));
		tableMail.setWidget(0, 1, from);
		tableMail.setHTML(1, 0, Main.i18n("mail.to"));
		tableMail.setWidget(1, 1, to);
		tableMail.setHTML(2, 0, Main.i18n("mail.subject"));
		tableMail.setWidget(2, 1, subject);
		setRowWordWarp(tableMail, 0, 2, false);
		setRowWordWarp(tableMail, 1, 2, false);
		setRowWordWarp(tableMail, 2, 2, false);
		setRowWordWarp(tableMail, 3, 2, false);
		tableMail.setVisible(false);

		path.setStyleName("okm-Input");
		categoryPath.setStyleName("okm-Input");
		folderSelectPopup.setStyleName("okm-Popup");
		folderSelectPopup.addStyleName("okm-DisableSelect");
		from.setStyleName("okm-Input");
		to.setStyleName("okm-Input");
		subject.setStyleName("okm-Input");
		mimeTypes.setStyleName("okm-Select");

		initWidget(scrollPanel);
	}

	/**
	 * enableMailSearch
	 */
	public void enableMailSearch() {
		typeMail.setValue(true);
		mailText.setVisible(true);
		tableMail.setVisible(true);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		table.setHTML(1, 0, Main.i18n("search.folder"));
		table.setHTML(2, 0, Main.i18n("search.category"));
		table.setHTML(3, 0, Main.i18n("search.type"));
		table.setHTML(4, 0, Main.i18n("search.mimetype"));
		mailText.setHTML(Main.i18n("search.type.mail"));

		tableMail.setHTML(0, 0, Main.i18n("mail.from"));
		tableMail.setHTML(1, 0, Main.i18n("mail.to"));
		tableMail.setHTML(2, 0, Main.i18n("mail.subject"));

		typeDocument.setText(Main.i18n("search.type.document"));
		typeFolder.setText(Main.i18n("search.type.folder"));
		typeMail.setText(Main.i18n("search.type.mail"));

		folderSelectPopup.langRefresh();
	}

	/**
	 * Set the WordWarp for all the row cells
	 *
	 * @param row The row cell
	 * @param columns Number of row columns
	 * @param warp
	 */
	private void setRowWordWarp(FlexTable table, int row, int columns, boolean wrap) {
		CellFormatter cellFormatter = table.getCellFormatter();

		for (int i = 0; i < columns; i++) {
			cellFormatter.setWordWrap(row, i, wrap);
		}
	}

	/**
	 * setMimeTypes
	 */
	public void setMimeTypes(List<GWTMimeType> mimeTypesList) {
		for (GWTMimeType mt : mimeTypesList) {
			mimeTypes.addItem(mt.getDescription(), mt.getName());
		}
	}
}