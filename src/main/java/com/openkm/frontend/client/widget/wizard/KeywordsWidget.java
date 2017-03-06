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

package com.openkm.frontend.client.widget.wizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTKeyword;
import com.openkm.frontend.client.service.OKMPropertyService;
import com.openkm.frontend.client.service.OKMPropertyServiceAsync;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.widget.dashboard.ImageHover;
import com.openkm.frontend.client.widget.dashboard.keymap.TagCloud;
import com.openkm.frontend.client.widget.thesaurus.ThesaurusSelectPopup;

import java.util.*;

/**
 * KeywordsWidget
 *
 * @author jllort
 *
 */
public class KeywordsWidget extends Composite {

	private final OKMPropertyServiceAsync propertyService = (OKMPropertyServiceAsync) GWT.create(OKMPropertyService.class);

	private FlexTable table;
	private String docPath;
	private CellFormatter cellFormatter;
	private HorizontalPanel keywordPanel;
	private SuggestBox suggestKey;
	private MultiWordSuggestOracle multiWordkSuggestKey;
	private List<String> keywordList;
	private List<String> keyWordsListPending; // Keyword list pending to be added ( each one is added sequentially )
	private TagCloud keywordsCloud;
	private Map<String, Widget> keywordMap;
	private FlowPanel hKeyPanel;
	private Collection<String> docKeywords;
	private boolean remove = true;
	private Image thesaurusImage;
	private boolean keyShortcutsEnabled = true;

	/**
	 * KeywordsWidget
	 *
	 * @param grpName The group name
	 * @param widget Widget at firs row
	 */
	public KeywordsWidget(String docPath, Widget widget) {
		table = new FlexTable();
		this.docPath = docPath;

		docKeywords = new ArrayList<String>();
		keywordMap = new HashMap<String, Widget>();
		keyWordsListPending = new ArrayList<String>();
		keywordsCloud = new TagCloud();
		keywordsCloud.setWidth("350px");

		keywordPanel = new HorizontalPanel();
		multiWordkSuggestKey = new MultiWordSuggestOracle();
		keywordList = new ArrayList<String>();
		suggestKey = new SuggestBox(multiWordkSuggestKey);
		suggestKey.setHeight("20px");
		suggestKey.setText(Main.i18n("dashboard.keyword.suggest"));
		suggestKey.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if ((char) KeyCodes.KEY_ENTER == event.getNativeKeyCode() && keyWordsListPending.isEmpty()) {
					String keys[] = suggestKey.getText().split(" ");    // Separates keywords by space
					for (int i = 0; i < keys.length; i++) {
						keyWordsListPending.add(keys[i]);
					}
					addPendingKeyWordsList();
					suggestKey.setText("");
				}
			}
		});
		suggestKey.getTextBox().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (suggestKey.getText().equals(Main.i18n("dashboard.keyword.suggest"))) {
					suggestKey.setText("");
				}

			}
		});

		suggestKey.getTextBox().addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				if (!keyShortcutsEnabled) {
					Main.get().mainPanel.enableKeyShorcuts();            // Enables general keys applications
					keyShortcutsEnabled = true;
				}
			}
		});

		suggestKey.getTextBox().addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				if (keyShortcutsEnabled) {
					Main.get().mainPanel.disableKeyShorcuts();
					keyShortcutsEnabled = false;
				}
			}
		});

		thesaurusImage = new Image(OKMBundleResources.INSTANCE.bookOpenIcon());
		thesaurusImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.desktop.navigator.thesaurusTree.thesaurusSelectPopup.show(ThesaurusSelectPopup.WIZARD);
			}
		});

		VerticalPanel vPanel = new VerticalPanel();
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(suggestKey);
		hPanel.add(new HTML("&nbsp;"));
		hPanel.add(thesaurusImage);
		hKeyPanel = new FlowPanel();
		HTML space = new HTML("");
		vPanel.add(hPanel);
		vPanel.add(space);
		vPanel.add(hKeyPanel);

		hKeyPanel.setWidth("250px");
		vPanel.setCellHeight(space, "5px");

		keywordPanel.add(vPanel);

		cellFormatter = table.getCellFormatter(); // Gets the cell formatter
		table.setWidth("100%");

		table.setWidget(0, 0, widget);
		table.getFlexCellFormatter().setColSpan(0, 0, 2);
		cellFormatter.addStyleName(0, 0, "okm-Security-Title-RightBorder"); // Border and margins

		RowFormatter rowFormatter = table.getRowFormatter();
		rowFormatter.setStyleName(0, "okm-Security-Title");

		// Widget format
		cellFormatter.setHorizontalAlignment(0, 0, HasAlignment.ALIGN_CENTER);
		cellFormatter.setVerticalAlignment(0, 0, HasAlignment.ALIGN_MIDDLE);

		table.setHTML(1, 0, "<b>" + Main.i18n("document.keywords") + "</b>");
		table.setWidget(1, 1, keywordPanel);
		cellFormatter.setVerticalAlignment(1, 0, HasAlignment.ALIGN_TOP);
		table.setHTML(2, 0, "");
		table.getFlexCellFormatter().setHeight(2, 0, "5px");
		table.setHTML(3, 0, "<b>" + Main.i18n("document.keywords.cloud") + "</b>");
		table.getFlexCellFormatter().setColSpan(3, 0, 2);
		table.setWidget(4, 0, keywordsCloud);
		table.getFlexCellFormatter().setColSpan(4, 0, 2);
		cellFormatter.setHorizontalAlignment(4, 0, HasAlignment.ALIGN_CENTER);

		// Reloading keyword list
		multiWordkSuggestKey.clear();
		keywordList = new ArrayList<String>();
		for (Iterator<GWTKeyword> it = Main.get().mainPanel.dashboard.keyMapDashboard.getAllKeywordList().iterator(); it.hasNext(); ) {
			String keyword = it.next().getKeyword();
			multiWordkSuggestKey.add(keyword);
			keywordList.add(keyword);
		}

		table.setStyleName("okm-DisableSelect");
		suggestKey.setStyleName("okm-KeyMap-Suggest");
		suggestKey.addStyleName("okm-Input");
		hKeyPanel.setStylePrimaryName("okm-cloudWrap");
		keywordsCloud.setStylePrimaryName("okm-cloudWrap");
		thesaurusImage.addStyleName("okm-Hyperlink");

		initWidget(table);
	}

	/**
	 * Removes a key
	 *
	 * @param keyword The key to be removed
	 */
	public void removeKey(String keyword) {
		if (keywordMap.containsKey(keyword)) {
			keywordMap.remove(keyword);
			docKeywords.remove(keyword);
			removeKeyword(keyword);
			Main.get().mainPanel.dashboard.keyMapDashboard.decreaseKeywordRate(keyword);
			drawTagCloud(docKeywords);
		}
	}

	/**
	 * addKeywordToPendinList
	 *
	 * @param key
	 */
	public void addKeywordToPendinList(String key) {
		keyWordsListPending.add(key);
	}

	/**
	 * Adds keywords sequentially
	 *
	 */
	public void addPendingKeyWordsList() {
		if (!keyWordsListPending.isEmpty()) {
			String keyword = keyWordsListPending.remove(0);
			if (!keywordMap.containsKey(keyword) && keyword.length() > 0) {
				for (Iterator<String> it = keywordMap.keySet().iterator(); it.hasNext(); ) {
					String key = it.next();
					if (!keywordList.contains(key)) {
						multiWordkSuggestKey.add(key);
						keywordList.add(key);
					}
				}
				Widget keywordButton = getKeyWidget(keyword, remove);
				keywordMap.put(keyword, keywordButton);
				hKeyPanel.add(keywordButton);
				docKeywords.add(keyword);
				addKeyword(keyword);
				Main.get().mainPanel.dashboard.keyMapDashboard.increaseKeywordRate(keyword);
			} else if (keyWordsListPending.isEmpty()) {
				drawTagCloud(docKeywords);
			}
		}
	}

	/**
	 * Get a new widget keyword
	 *
	 * @param keyword The keyword
	 *
	 * @return The widget
	 */
	private HorizontalPanel getKeyWidget(final String keyword, boolean remove) {
		final HorizontalPanel externalPanel = new HorizontalPanel();
		HorizontalPanel hPanel = new HorizontalPanel();
		HTML space = new HTML();
		ImageHover delete = new ImageHover("img/icon/actions/delete_disabled.gif", "img/icon/actions/delete.gif");
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				removeKey(keyword);
				hKeyPanel.remove(externalPanel);
			}
		});
		delete.setStyleName("okm-KeyMap-ImageHover");
		hPanel.add(new HTML(keyword));
		hPanel.add(space);
		if (remove) {
			hPanel.add(delete);
		}
		hPanel.setCellWidth(space, "6px");
		hPanel.setStyleName("okm-KeyMap-Gray");
		HTML space1 = new HTML();
		externalPanel.add(hPanel);
		externalPanel.add(space1);
		externalPanel.setCellWidth(space1, "6px");
		externalPanel.setStylePrimaryName("okm-cloudTags");
		return externalPanel;
	}

	/**
	 * Draws a tag cloud
	 */
	private void drawTagCloud(Collection<String> keywords) {
		// Deletes all tag clouds keys
		keywordsCloud.clear();
		keywordsCloud.setMinFrequency(Main.get().mainPanel.dashboard.keyMapDashboard.getTotalMinFrequency());
		keywordsCloud.setMaxFrequency(Main.get().mainPanel.dashboard.keyMapDashboard.getTotalMaxFrequency());

		for (Iterator<String> it = keywords.iterator(); it.hasNext(); ) {
			String keyword = it.next();
			HTML tagKey = new HTML(keyword);
			tagKey.setStyleName("okm-cloudTags");
			Style linkStyle = tagKey.getElement().getStyle();
			int fontSize = keywordsCloud.getLabelSize(Main.get().mainPanel.dashboard.keyMapDashboard.getKeywordRate(keyword));
			linkStyle.setProperty("fontSize", fontSize + "pt");
			linkStyle.setProperty("color", keywordsCloud.getColor(fontSize));
			if (fontSize > 0) {
				linkStyle.setProperty("top", (keywordsCloud.getMaxFontSize() - fontSize) / 2 + "px");
			}
			keywordsCloud.add(tagKey);
		}
	}

	/**
	 * Callback addKeyword document
	 */
	final AsyncCallback<Object> callbackAddKeywords = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			if (keyWordsListPending.isEmpty()) {
				drawTagCloud(docKeywords);
			} else {
				addPendingKeyWordsList();
			}
		}

		public void onFailure(Throwable caught) {
			if (keyWordsListPending.isEmpty()) {
				drawTagCloud(docKeywords);
			} else {
				addPendingKeyWordsList();
			}
			Main.get().showError("AddKeyword", caught);
		}
	};

	/**
	 * Callback removeKeyword document
	 */
	final AsyncCallback<Object> callbackRemoveKeywords = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("RemoveKeyword", caught);
		}
	};

	/**
	 * addKeyword document
	 */
	public void addKeyword(String keyword) {
		propertyService.addKeyword(docPath, keyword, callbackAddKeywords);
	}

	/**
	 * removeKeyword document
	 */
	public void removeKeyword(String keyword) {
		propertyService.removeKeyword(docPath, keyword, callbackRemoveKeywords);
	}
}
