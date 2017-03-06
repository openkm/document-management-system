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

package com.openkm.extension.frontend.client.widget.wiki.findwiki;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.service.OKMWikiService;
import com.openkm.extension.frontend.client.service.OKMWikiServiceAsync;
import com.openkm.extension.frontend.client.widget.wiki.Wiki;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

import java.util.List;

/**
 * FindWikiSelectPopup
 *
 * @author jllort
 *
 */
public class FindWikiSelectPopup extends DialogBox {
	private final OKMWikiServiceAsync wikiService = (OKMWikiServiceAsync) GWT.create(OKMWikiService.class);

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	public ScrollPanel scrollFolderPanel;
	private Button cancelButton;
	private Button actionButton;
	public Status status;
	private TextBox keyword;
	private FlexTable wikiTable;
	private int selectedRow = -1;

	/**
	 * FindWikiSelectPopup
	 */
	public FindWikiSelectPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		status = new Status();
		status.setStyleName("okm-StatusPopup");

		vPanel = new VerticalPanel();
		vPanel.setWidth("400px");
		vPanel.setHeight("350px");
		hPanel = new HorizontalPanel();

		scrollFolderPanel = new ScrollPanel();
		scrollFolderPanel.setStyleName("okm-Popup-text");

		cancelButton = new Button(GeneralComunicator.i18n("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		actionButton = new Button(GeneralComunicator.i18nExtension("wiki.add.link"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Wiki.get().addWigiTag(wikiTable.getText(selectedRow, 0));
				hide();
			}
		});

		keyword = new TextBox();
		keyword.setWidth("392px");
		keyword.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (keyword.getText().length() >= 3) {
					find(keyword.getText());
				} else {
					removeAllRows();
				}
			}
		});
		wikiTable = new FlexTable();
		wikiTable.setWidth("100%");
		wikiTable.setCellPadding(2);
		wikiTable.setCellSpacing(0);
		wikiTable.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				markSelectedRow(wikiTable.getCellForEvent(event).getRowIndex());
				evaluateEnableAction();
			}
		});

		scrollFolderPanel.add(wikiTable);
		scrollFolderPanel.setPixelSize(390, 300);

		vPanel.add(keyword);
		vPanel.add(scrollFolderPanel);
		vPanel.add(new HTML("<br>"));
		hPanel.add(cancelButton);
		HTML space = new HTML();
		space.setWidth("50px");
		hPanel.add(space);
		hPanel.add(actionButton);
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(keyword, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(keyword, HasAlignment.ALIGN_MIDDLE);
		vPanel.setCellHorizontalAlignment(scrollFolderPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHeight(keyword, "25px");
		vPanel.setCellHeight(scrollFolderPanel, "300px");

		cancelButton.setStyleName("okm-NoButton");
		actionButton.setStyleName("okm-YesButton");
		wikiTable.setStyleName("okm-NoWrap");
		wikiTable.addStyleName("okm-Table-Row");
		keyword.setStyleName("okm-Input");

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(GeneralComunicator.i18nExtension("wiki.search.filter"));
		cancelButton.setText(GeneralComunicator.i18n("button.close"));
		actionButton.setText(GeneralComunicator.i18nExtension("wiki.add.link"));
	}

	/**
	 * Shows the popup 
	 */
	public void show() {
		initButtons();
		int left = (Window.getClientWidth() - 400) / 2;
		int top = (Window.getClientHeight() - 350) / 2;
		setPopupPosition(left, top);
		setText(GeneralComunicator.i18nExtension("wiki.search.filter"));

		// Resets to initial tree value
		removeAllRows();
		keyword.setText("");
		evaluateEnableAction();
		super.show();
		keyword.setFocus(true);
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
		while (wikiTable.getRowCount() > 0) {
			wikiTable.removeRow(0);
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
				wikiTable.getRowFormatter().addStyleName(row, "okm-Table-SelectedRow");
			} else {
				wikiTable.getRowFormatter().removeStyleName(row, "okm-Table-SelectedRow");
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
	 * Find
	 *
	 * @param params
	 */
	private void find(String title) {
		status.setFlagGetWikiPage();
		wikiService.findAllLatestByTitleFiltered(title, new AsyncCallback<List<String>>() {
			@Override
			public void onSuccess(List<String> result) {
				removeAllRows();
				for (String title : result) {
					int rows = wikiTable.getRowCount();
					wikiTable.setHTML(rows, 0, title);
				}
				status.unsetFlagGetWikiPage();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("findAllLatestByTitleFiltered", caught);
				status.unsetFlagGetWikiPage();
			}
		});
	}
}