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

package com.openkm.frontend.client.widget.findsimilar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTQueryResult;
import com.openkm.frontend.client.bean.GWTResultSet;
import com.openkm.frontend.client.service.OKMSearchService;
import com.openkm.frontend.client.service.OKMSearchServiceAsync;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.Util;

import java.util.Iterator;

/**
 * FindSimilarDocumentSelectPopup
 *
 * @author jllort
 *
 */
public class FindSimilarDocumentSelectPopup extends DialogBox {
	private final OKMSearchServiceAsync searchService = (OKMSearchServiceAsync) GWT.create(OKMSearchService.class);

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	public ScrollPanel scrollDocumentPanel;
	private Button cancelButton;
	private Button actionButton;
	public Status status;
	private FlexTable documentTable;
	private int selectedRow = -1;

	/**
	 * FindSimilarDocumentSelectPopup
	 */
	public FindSimilarDocumentSelectPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		status = new Status();
		status.setStyleName("okm-StatusPopup");

		vPanel = new VerticalPanel();
		vPanel.setWidth("700px");
		vPanel.setHeight("350px");
		hPanel = new HorizontalPanel();

		scrollDocumentPanel = new ScrollPanel();
		scrollDocumentPanel.setStyleName("okm-Popup-text");

		cancelButton = new Button(Main.i18n("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		actionButton = new Button(Main.i18n("search.result.menu.go.document"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String docPath = documentTable.getText(selectedRow, 1);
				CommonUI.openPath(Util.getParent(docPath), docPath);
				hide();
			}
		});


		documentTable = new FlexTable();
		documentTable.setWidth("100%");
		documentTable.setCellPadding(2);
		documentTable.setCellSpacing(0);

		documentTable.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				markSelectedRow(documentTable.getCellForEvent(event).getRowIndex());
				evaluateEnableAction();
			}
		});

		documentTable.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				String docPath = documentTable.getText(selectedRow, 1);
				CommonUI.openPath(Util.getParent(docPath), docPath);
				hide();
			}
		});

		scrollDocumentPanel.add(documentTable);
		scrollDocumentPanel.setPixelSize(690, 300);

		vPanel.add(scrollDocumentPanel);
		vPanel.add(new HTML("<br>"));
		hPanel.add(cancelButton);
		HTML space = new HTML();
		space.setWidth("50px");
		hPanel.add(space);
		hPanel.add(actionButton);
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(scrollDocumentPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHeight(scrollDocumentPanel, "300px");

		cancelButton.setStyleName("okm-NoButton");
		actionButton.setStyleName("okm-YesButton");
		documentTable.setStyleName("okm-NoWrap");
		documentTable.addStyleName("okm-Table-Row");

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("search.similar.document"));
		cancelButton.setText(Main.i18n("button.close"));
		actionButton.setText(Main.i18n("search.result.menu.go.document"));
	}

	/**
	 * Shows the popup 
	 */
	public void show() {
		initButtons();
		int left = (Window.getClientWidth() - 700) / 2;
		int top = (Window.getClientHeight() - 350) / 2;
		setPopupPosition(left, top);
		setText(Main.i18n("search.similar.document"));

		// Resets to initial tree value
		removeAllRows();
		evaluateEnableAction();
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

	/**
	 * Enables all button
	 */
	private void initButtons() {
		cancelButton.setEnabled(true);
		actionButton.setEnabled(false);
	}

	/**
	 * removeAllRows
	 */
	private void removeAllRows() {
		selectedRow = -1;
		evaluateEnableAction();
		while (documentTable.getRowCount() > 0) {
			documentTable.removeRow(0);
		}
	}

	/**
	 * markSelectedRow
	 *
	 * @param row
	 */
	private void markSelectedRow(int row) {
		// And row must be other than the selected one
		if (row != selectedRow) {
			styleRow(selectedRow, false);
			styleRow(row, true);
			selectedRow = row;
		}
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
				documentTable.getRowFormatter().addStyleName(row, "okm-Table-SelectedRow");
			} else {
				documentTable.getRowFormatter().removeStyleName(row, "okm-Table-SelectedRow");
			}
		}
	}

	/**
	 * evaluateEnableAction
	 */
	private void evaluateEnableAction() {
		enable(selectedRow >= 0);
	}

	/**
	 * Call Back find
	 */
	final AsyncCallback<GWTResultSet> callbackFind = new AsyncCallback<GWTResultSet>() {
		public void onSuccess(GWTResultSet result) {
			GWTResultSet resultSet = result;
			removeAllRows();

			for (Iterator<GWTQueryResult> it = resultSet.getResults().iterator(); it.hasNext(); ) {
				GWTQueryResult gwtQueryResult = it.next();

				if (gwtQueryResult.getDocument() != null) {
					GWTDocument doc = gwtQueryResult.getDocument();
					int rows = documentTable.getRowCount();
					documentTable.setHTML(rows, 0, Util.mimeImageHTML(doc.getMimeType()));
					documentTable.setHTML(rows, 1, doc.getPath());
					documentTable.getCellFormatter().setWidth(rows, 0, "30px");
					documentTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_CENTER);
				}
			}

			status.unsetFlagChilds();
		}

		public void onFailure(Throwable caught) {
			status.unsetFlagChilds();
			Main.get().showError("Find", caught);
		}
	};

	/**
	 * Find
	 *
	 * @param uuid
	 */
	public void find(String uuid) {
		status.setFlagChilds();
		searchService.findMoreLikeThis(uuid, new AsyncCallback<GWTResultSet>() {
			@Override
			public void onSuccess(GWTResultSet result) {
				GWTResultSet resultSet = result;
				removeAllRows();

				for (Iterator<GWTQueryResult> it = resultSet.getResults().iterator(); it.hasNext(); ) {
					GWTQueryResult gwtQueryResult = it.next();

					if (gwtQueryResult.getDocument() != null) {
						GWTDocument doc = gwtQueryResult.getDocument();
						int rows = documentTable.getRowCount();
						documentTable.setHTML(rows, 0, Util.mimeImageHTML(doc.getMimeType()));
						documentTable.setHTML(rows, 1, doc.getPath());
						documentTable.getCellFormatter().setWidth(rows, 0, "30px");
						documentTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_CENTER);
					}
				}

				status.unsetFlagChilds();
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("findMoreLikeThis", caught);
			}
		});
	}
}