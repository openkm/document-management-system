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
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.service.OKMDocumentService;
import com.openkm.frontend.client.service.OKMDocumentServiceAsync;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * PdfMergePopup popup
 *
 * @author jllort
 *
 */
public class PdfMergePopup extends DialogBox {
	private final OKMDocumentServiceAsync documentService = (OKMDocumentServiceAsync) GWT.create(OKMDocumentService.class);

	private LinkedList<GWTDocument> data;
	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private VerticalPanel dataPanel;
	private HorizontalPanel hNamePanel;
	private Button cancel;
	private Button merge;
	private TextBox name;
	private HTML nameText;
	private FlexTable table;
	private Status status;

	/**
	 * PdfMergePopup
	 */
	public PdfMergePopup() {
		super(false, true);
		setText(Main.i18n("merge.pdf.label"));

		// Status
		status = new Status(this);
		status.setStyleName("okm-StatusPopup");

		// Main panel
		vPanel = new VerticalPanel();
		vPanel.setWidth("100%");

		// Documents table
		table = new FlexTable();
		table.setWidth("100%");
		table.setCellPadding(4);
		table.setCellSpacing(0);
		table.setStyleName("whitePanel");
		table.addStyleName("okm-NoWrap");
		table.addStyleName("okm-Input");

		// Buttons
		hPanel = new HorizontalPanel();
		cancel = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		cancel.setStyleName("okm-NoButton");

		merge = new Button(Main.i18n("button.merge"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				merge();
			}
		});
		merge.setStyleName("okm-YesButton");

		hPanel.add(cancel);
		hPanel.add(new HTML("&nbsp;"));
		hPanel.add(merge);

		// Name
		hNamePanel = new HorizontalPanel();
		name = new TextBox();
		name.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (name.getText().length() > 0) {
					if (KeyCodes.KEY_ENTER == event.getNativeKeyCode()) {
						merge();
					}
					merge.setEnabled(true);
				} else {
					merge.setEnabled(false);
				}
			}
		});
		name.setWidth("200px");
		name.setStyleName("okm-Input");
		nameText = new HTML(Main.i18n("merge.pdf.filename"));
		nameText.setStyleName("okm-NoWrap");

		hNamePanel.add(nameText);
		hNamePanel.add(Util.hSpace("5px"));
		hNamePanel.add(name);

		// Data panel
		dataPanel = new VerticalPanel();
		dataPanel.setWidth("95%");
		dataPanel.add(hNamePanel);
		dataPanel.add(Util.vSpace("5px"));
		dataPanel.add(table);

		vPanel.add(Util.vSpace("5px"));
		vPanel.add(dataPanel);
		vPanel.add(Util.vSpace("5px"));
		vPanel.add(hPanel);
		vPanel.add(Util.vSpace("5px"));
		vPanel.setCellHorizontalAlignment(dataPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * reset
	 *
	 * @param docs
	 */
	public void reset(List<GWTDocument> docs) {
		name.setText("");
		merge.setEnabled(false);
		data = new LinkedList<GWTDocument>();
		for (GWTDocument doc : docs) {
			data.add(doc);
		}
		draw();
	}

	/**
	 * draw
	 */
	private void draw() {
		table.removeAllRows();
		for (GWTDocument doc : data) {
			final int row = table.getRowCount();
			table.setHTML(row, 0, Util.mimeImageHTML(doc.getMimeType()));
			table.setHTML(row, 1, doc.getName());
			if (row > 0) {
				Image arrowUp = new Image(OKMBundleResources.INSTANCE.arrowUp());
				arrowUp.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						moveUp(row);
					}
				});
				arrowUp.setStyleName("okm-Hyperlink");
				table.setWidget(row, 2, arrowUp);
			} else {
				table.setHTML(row, 2, "");
			}
			if (data.size() - 1 > row) {
				Image arrowDown = new Image(OKMBundleResources.INSTANCE.arrowDown());
				arrowDown.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						moveDown(row);
					}
				});
				arrowDown.setStyleName("okm-Hyperlink");
				table.setWidget(row, 3, arrowDown);
			} else {
				table.setHTML(row, 3, "");
			}
			table.setHTML(row, 4, "");
			table.getCellFormatter().setWidth(row, 2, "20px");
			table.getCellFormatter().setWidth(row, 3, "20px");
			table.getCellFormatter().setWidth(row, 4, "100%");
			table.getCellFormatter().setHorizontalAlignment(row, 2, HasAlignment.ALIGN_CENTER);
			table.getCellFormatter().setHorizontalAlignment(row, 3, HasAlignment.ALIGN_CENTER);
		}
	}

	/**
	 * moveUp
	 *
	 * @param row
	 */
	private void moveUp(int row) {
		if (row > 0) {
			GWTDocument doc1 = data.get(row - 1);
			GWTDocument doc2 = data.get(row);
			data.set(row - 1, doc2);
			data.set(row, doc1);
			draw();
		}
	}

	/**
	 * moveDown
	 *
	 * @param row
	 */
	private void moveDown(int row) {
		if (data.size() - 1 > row) {
			GWTDocument doc1 = data.get(row);
			GWTDocument doc2 = data.get(row + 1);
			data.set(row, doc2);
			data.set(row + 1, doc1);
			draw();
		}
	}

	/**
	 * merge
	 */
	public void merge() {
		List<String> paths = new ArrayList<String>();
		for (GWTDocument doc : data) {
			paths.add(doc.getPath());
		}
		status.setFlagMerge();
		documentService.mergePdf(name.getText(), paths, new AsyncCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				Main.get().mainPanel.desktop.browser.fileBrowser.refreshOnlyFileBrowser();
				status.unsetFlagMerge();
				hide();
			}

			@Override
			public void onFailure(Throwable caught) {
				status.unsetFlagMerge();
				Main.get().showError("mergePdf", caught);
			}
		});
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		setText(Main.i18n("merge.pdf.label"));
		cancel.setHTML(Main.i18n("button.cancel"));
		merge.setHTML(Main.i18n("button.merge"));
		nameText.setHTML(Main.i18n("merge.pdf.filename"));
	}
}