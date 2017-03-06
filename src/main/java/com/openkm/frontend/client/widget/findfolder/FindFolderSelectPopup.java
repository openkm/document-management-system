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

package com.openkm.frontend.client.widget.findfolder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.*;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.service.OKMSearchService;
import com.openkm.frontend.client.service.OKMSearchServiceAsync;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.EventUtils;
import com.openkm.frontend.client.util.Util;

import java.util.HashMap;
import java.util.Iterator;

public class FindFolderSelectPopup extends DialogBox {
	private final OKMSearchServiceAsync searchService = (OKMSearchServiceAsync) GWT.create(OKMSearchService.class);

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	public ScrollPanel scrollFolderPanel;
	private Button cancelButton;
	private Button actionButton;
	public Status status;
	private TextBox keyword;
	private FlexTable folderTable;
	private int selectedRow = -1;

	public FindFolderSelectPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		status = new Status();
		status.setStyleName("okm-StatusPopup");

		vPanel = new VerticalPanel();
		vPanel.setWidth("700px");
		vPanel.setHeight("350px");
		hPanel = new HorizontalPanel();

		scrollFolderPanel = new ScrollPanel();
		scrollFolderPanel.setStyleName("okm-Popup-text");

		cancelButton = new Button(Main.i18n("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		actionButton = new Button(Main.i18n("search.result.menu.go.folder"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CommonUI.openPath(folderTable.getText(selectedRow, 1), "");
				hide();
			}
		});

		keyword = new TextBox();
		keyword.setWidth("692px");
		keyword.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (keyword.getText().length() >= 3 && !EventUtils.isNavigationKey(event.getNativeKeyCode()) &&
						!EventUtils.isModifierKey(event.getNativeKeyCode())) {
					GWTQueryParams gwtParams = new GWTQueryParams();
					int actualView = Main.get().mainPanel.desktop.navigator.stackPanel.getStackIndex();

					switch (actualView) {
						case UIDesktopConstants.NAVIGATOR_TAXONOMY:
							gwtParams.setPath(Main.get().taxonomyRootFolder.getPath());
							break;
						case UIDesktopConstants.NAVIGATOR_TEMPLATES:
							gwtParams.setPath(Main.get().templatesRootFolder.getPath());
							break;
						case UIDesktopConstants.NAVIGATOR_PERSONAL:
							gwtParams.setPath(Main.get().personalRootFolder.getPath());
							break;
						case UIDesktopConstants.NAVIGATOR_MAIL:
							gwtParams.setPath(Main.get().mailRootFolder.getPath());
							break;
						case UIDesktopConstants.NAVIGATOR_TRASH:
							gwtParams.setPath(Main.get().trashRootFolder.getPath());
							break;
					}

					gwtParams.setMimeType("");
					gwtParams.setKeywords("");
					gwtParams.setMimeType("");
					gwtParams.setName(keyword.getText() + "*");
					gwtParams.setAuthor("");
					gwtParams.setMailFrom("");
					gwtParams.setMailTo("");
					gwtParams.setMailSubject("");
					gwtParams.setOperator(GWTQueryParams.OPERATOR_AND);
					gwtParams.setLastModifiedFrom(null);
					gwtParams.setLastModifiedTo(null);
					gwtParams.setDomain(GWTQueryParams.FOLDER);
					gwtParams.setProperties(new HashMap<String, GWTPropertyParams>());

					find(gwtParams);
				} else {
					removeAllRows();
				}
			}
		});

		folderTable = new FlexTable();
		folderTable.setWidth("100%");
		folderTable.setCellPadding(2);
		folderTable.setCellSpacing(0);

		folderTable.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				markSelectedRow(folderTable.getCellForEvent(event).getRowIndex());
				evaluateEnableAction();
			}
		});

		folderTable.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				CommonUI.openPath(folderTable.getText(selectedRow, 1), "");
				hide();
			}
		});

		scrollFolderPanel.add(folderTable);
		scrollFolderPanel.setPixelSize(690, 300);

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
		folderTable.setStyleName("okm-NoWrap");
		folderTable.addStyleName("okm-Table-Row");
		keyword.setStyleName("okm-Input");

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("search.folder.filter"));
		cancelButton.setText(Main.i18n("button.close"));
		actionButton.setText(Main.i18n("search.result.menu.go.folder"));
	}

	/**
	 * Shows the popup 
	 */
	public void show() {
		initButtons();
		int left = (Window.getClientWidth() - 700) / 2;
		int top = (Window.getClientHeight() - 350) / 2;
		setPopupPosition(left, top);
		setText(Main.i18n("search.folder.filter"));

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
		while (folderTable.getRowCount() > 0) {
			folderTable.removeRow(0);
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
				folderTable.getRowFormatter().addStyleName(row, "okm-Table-SelectedRow");
			} else {
				folderTable.getRowFormatter().removeStyleName(row, "okm-Table-SelectedRow");
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

				if (gwtQueryResult.getFolder() != null) {
					GWTFolder folder = gwtQueryResult.getFolder();
					int rows = folderTable.getRowCount();

					// Looks if must change icon on parent if now has no childs and properties with user security atention
					if ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
						if (folder.isHasChildren()) {
							folderTable.setHTML(rows, 0, Util.imageItemHTML("img/menuitem_childs.gif"));
						} else {
							folderTable.setHTML(rows, 0, Util.imageItemHTML("img/menuitem_empty.gif"));
						}
					} else {
						if (folder.isHasChildren()) {
							folderTable.setHTML(rows, 0, Util.imageItemHTML("img/menuitem_childs_ro.gif"));
						} else {
							folderTable.setHTML(rows, 0, Util.imageItemHTML("img/menuitem_empty_ro.gif"));
						}
					}

					folderTable.setHTML(rows, 1, folder.getPath());
					folderTable.getCellFormatter().setWidth(rows, 0, "30px");
					folderTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_CENTER);
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
	 */
	private void find(GWTQueryParams params) {
		status.setFlagChilds();
		searchService.find(params, callbackFind);
	}
}