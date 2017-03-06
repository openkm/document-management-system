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

package com.openkm.frontend.client.widget.thesaurus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.service.OKMThesaurusService;
import com.openkm.frontend.client.service.OKMThesaurusServiceAsync;

import java.util.Iterator;
import java.util.List;

/**
 * ThesaurusPanel
 *
 * @author jllort
 *
 */
public class ThesaurusPanel extends Composite {
	private final OKMThesaurusServiceAsync thesaurusService = (OKMThesaurusServiceAsync) GWT.create(OKMThesaurusService.class);

	private static final int TAB_HEIGHT = 20;
	private final int TAB_TREE = 0;
	private final int TAB_KEYWORDS = 1;

	private TabLayoutPanel tabPanel;
	private VerticalPanel vPanel;
	private FolderSelectTree folderSelectTree;
	private VerticalPanel verticalDirectoryPanel;
	public ScrollPanel scrollDirectoryPanel;
	public ScrollPanel scrollKeywordPanel;
	private TextBox keyword;
	private FlexTable keywordTable;
	private VerticalPanel vPanelKeyword;
	private int selectedRow = -1;
	private int selectedTab = TAB_TREE;
	public Status status;

	/**
	 * ThesaurusPanel
	 */
	public ThesaurusPanel() {
		status = new Status();
		status.setStyleName("okm-StatusPopup");

		// Tree
		folderSelectTree = new FolderSelectTree();
		folderSelectTree.setSize("100%", "100%");
		verticalDirectoryPanel = new VerticalPanel();
		verticalDirectoryPanel.setSize("100%", "100%");
		scrollDirectoryPanel = new ScrollPanel();
		scrollDirectoryPanel.setSize("490px", "275px");
		scrollDirectoryPanel.addStyleName("okm-Background-White");
		scrollDirectoryPanel.addStyleName("okm-Border-Left");
		scrollDirectoryPanel.addStyleName("okm-Border-Right");
		scrollDirectoryPanel.addStyleName("okm-Border-Bottom");
		verticalDirectoryPanel.add(folderSelectTree);
		verticalDirectoryPanel.setCellHorizontalAlignment(folderSelectTree, HasAlignment.ALIGN_LEFT);
		scrollDirectoryPanel.add(verticalDirectoryPanel);

		// Keywords
		keywordTable = new FlexTable();
		keywordTable.setWidth("100%");
		keywordTable.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				markSelectedRow(keywordTable.getCellForEvent(event).getRowIndex());
				evaluateEnableAction();
			}
		});
		scrollKeywordPanel = new ScrollPanel();
		scrollKeywordPanel.add(keywordTable);
		scrollKeywordPanel.setStyleName("okm-Popup-text");

		keyword = new TextBox();
		keyword.setWidth("492px");
		keyword.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (keyword.getText().length() >= 3) {
					getKeywords(keyword.getText().toLowerCase());
				} else {
					removeAllRows();
				}
			}
		});

		vPanelKeyword = new VerticalPanel();
		vPanelKeyword.add(keyword);
		vPanelKeyword.add(scrollKeywordPanel);

		vPanelKeyword.setCellHeight(keyword, "25px");
		vPanelKeyword.setCellVerticalAlignment(keyword, HasAlignment.ALIGN_MIDDLE);

		// Tab Panel
		vPanel = new VerticalPanel();
		tabPanel = new TabLayoutPanel(TAB_HEIGHT, Unit.PX);
		tabPanel.setWidth("492px");
		tabPanel.setHeight("300px");

		tabPanel.add(scrollDirectoryPanel, Main.i18n("thesaurus.tab.tree"));
		tabPanel.add(vPanelKeyword, Main.i18n("thesaurus.tab.keywords"));
		tabPanel.selectTab(TAB_TREE);
		scrollDirectoryPanel.setPixelSize(490, 275);
		scrollKeywordPanel.setPixelSize(490, 250);

		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				selectedTab = event.getSelectedItem().intValue();
				evaluateEnableAction();
			}
		});

		vPanel.add(tabPanel);

		vPanel.addStyleName("okm-DisableSelect");
		keyword.setStyleName("okm-Input");
		keywordTable.setStyleName("okm-NoWrap");
		keywordTable.addStyleName("okm-Table-Row");

		initWidget(vPanel);
	}

	/**
	 * reset
	 */
	public void reset() {
		folderSelectTree.reset();
		removeAllRows();
		keyword.setText("");
		evaluateEnableAction();
	}

	/**
	 * Gets asyncronous root node
	 */
	final AsyncCallback<List<String>> callbackGetKeywords = new AsyncCallback<List<String>>() {
		public void onSuccess(List<String> result) {
			removeAllRows();
			for (Iterator<String> it = result.iterator(); it.hasNext(); ) {
				keywordTable.setHTML(keywordTable.getRowCount(), 0, it.next());
			}
			status.unsetFlagKeywords();
		}

		public void onFailure(Throwable caught) {
			status.unsetFlagKeywords();
			Main.get().showError("getKeywords", caught);
		}
	};

	/**
	 * Gets the root
	 */
	public void getKeywords(String filter) {
		status.setFlagKeywords();
		thesaurusService.getKeywords(filter, callbackGetKeywords);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		selectedTab = tabPanel.getSelectedIndex();

		while (tabPanel.getWidgetCount() > 0) {
			tabPanel.remove(0);
		}
		tabPanel.add(scrollDirectoryPanel, Main.i18n("thesaurus.tab.tree"));
		tabPanel.add(scrollKeywordPanel, Main.i18n("thesaurus.tab.keywords"));
		tabPanel.selectTab(selectedTab);

		scrollDirectoryPanel.setPixelSize(490, 275);
		scrollKeywordPanel.setPixelSize(490, 250);
	}

	/**
	 * removeAllRows
	 */
	private void removeAllRows() {
		selectedRow = -1;
		evaluateEnableAction();
		while (keywordTable.getRowCount() > 0) {
			keywordTable.removeRow(0);
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
				keywordTable.getRowFormatter().addStyleName(row, "okm-Table-SelectedRow");
			} else {
				keywordTable.getRowFormatter().removeStyleName(row, "okm-Table-SelectedRow");
			}
		}
	}

	/**
	 * evaluateEnableAction
	 */
	public void evaluateEnableAction() {
		if (isTabTreeSelected()) {
			Main.get().mainPanel.desktop.navigator.thesaurusTree.thesaurusSelectPopup.enable(folderSelectTree.evaluateEnableActionButton());
		} else if (isTabKeywordSelected()) {
			Main.get().mainPanel.desktop.navigator.thesaurusTree.thesaurusSelectPopup.enable(selectedRow >= 0);
		}
	}

	/**
	 * isTabTreeSelected
	 */
	public boolean isTabTreeSelected() {
		return (selectedTab == TAB_TREE);
	}

	/**
	 * isTabKeywordSelected
	 *
	 * @return
	 */
	public boolean isTabKeywordSelected() {
		return (selectedTab == TAB_KEYWORDS);
	}

	/**
	 * getActualPath
	 *
	 * @return
	 */
	public String getActualPath() {
		return folderSelectTree.getActualPath();
	}

	/**
	 * getSelectedKeyword
	 *
	 * @return
	 */
	public String getSelectedKeyword() {
		return keywordTable.getText(selectedRow, 0);
	}
}