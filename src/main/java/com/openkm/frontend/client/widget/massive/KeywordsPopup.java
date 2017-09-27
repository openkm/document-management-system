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
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTKeyword;
import com.openkm.frontend.client.service.OKMMassiveService;
import com.openkm.frontend.client.service.OKMMassiveServiceAsync;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.widget.WidgetUtil;
import com.openkm.frontend.client.widget.dashboard.ImageHover;
import com.openkm.frontend.client.widget.dashboard.keymap.TagCloud;
import com.openkm.frontend.client.widget.thesaurus.ThesaurusSelectPopup;

import java.util.*;

/**
 * Keywords popup
 *
 * @author jllort
 */
public class KeywordsPopup extends DialogBox {
	private final OKMMassiveServiceAsync massiveService = (OKMMassiveServiceAsync) GWT.create(OKMMassiveService.class);

	private FlexTable table;
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
	private Image thesaurusImage;
	private boolean keyShortcutsEnabled = true;
	private boolean remove = true;
	private Button close;
	private Status status;

	/**
	 * KeywordsPopup
	 */
	public KeywordsPopup() {
		// Establishes auto-close when click outside
		super(false, true);
		setText(Main.i18n("keyword.add"));

		// Status
		status = new Status(this);
		status.setStyleName("okm-StatusPopup");

		table = new FlexTable();
		table.setWidth("100%");
		table.setCellPadding(0);
		table.setCellSpacing(2);

		cellFormatter = table.getCellFormatter(); // Gets the cell formatter

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
					Main.get().mainPanel.enableKeyShorcuts(); // Enables general keys applications
					String keys[] = suggestKey.getText().split(" "); // Separates keywords by space
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
				Main.get().mainPanel.disableKeyShorcuts(); // Disables key shortcuts while updating
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
					Main.get().mainPanel.enableKeyShorcuts(); // Enables general keys applications
					keyShortcutsEnabled = true;
				}
			}
		});

		thesaurusImage = new Image(OKMBundleResources.INSTANCE.bookOpenIcon());
		thesaurusImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.desktop.navigator.thesaurusTree.thesaurusSelectPopup
						.show(ThesaurusSelectPopup.MASSIVE);
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

		close = new Button(Main.i18n("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
					Main.get().mainPanel.topPanel.toolBar.executeRefresh();
					Main.get().mainPanel.dashboard.keyMapDashboard.refreshAll();
				}
				Main.get().mainPanel.enableKeyShorcuts();
				hide();
			}
		});
		close.setStyleName("okm-NoButton");

		hKeyPanel.setWidth("250px");
		vPanel.setCellHeight(space, "5px");

		keywordPanel.add(vPanel);

		table.setHTML(0, 0, "<b>" + Main.i18n("document.keywords") + "</b>");
		table.setWidget(0, 1, keywordPanel);
		cellFormatter.setVerticalAlignment(0, 0, HasAlignment.ALIGN_TOP);
		table.setHTML(1, 0, "");
		table.getFlexCellFormatter().setHeight(1, 0, "5px");
		table.setHTML(2, 0, "<b>" + Main.i18n("document.keywords.cloud") + "</b>");
		table.getFlexCellFormatter().setColSpan(2, 0, 2);
		table.setWidget(3, 0, keywordsCloud);
		table.getFlexCellFormatter().setColSpan(3, 0, 2);
		cellFormatter.setHorizontalAlignment(3, 0, HasAlignment.ALIGN_LEFT);
		table.setWidget(4, 0, close);
		table.getFlexCellFormatter().setColSpan(4, 0, 2);
		cellFormatter.setHorizontalAlignment(4, 0, HasAlignment.ALIGN_CENTER);

		table.setStyleName("okm-DisableSelect");
		suggestKey.setStyleName("okm-KeyMap-Suggest");
		suggestKey.addStyleName("okm-Input");
		hKeyPanel.setStylePrimaryName("okm-cloudWrap");
		keywordsCloud.setStylePrimaryName("okm-cloudWrap");
		thesaurusImage.addStyleName("okm-Hyperlink");

		setWidget(table);
	}

	/**
	 * reset
	 */
	public void reset() {
		// Evaluate remove button
		if (Main.get().workspaceUserProperties.getWorkspace().getAvailableOption().isRemoveKeywordOption()) {
			remove = true;
		} else {
			remove = false;
		}

		// Reloading keyword list
		multiWordkSuggestKey.clear();
		keywordList = new ArrayList<String>();
		for (GWTKeyword key : Main.get().mainPanel.dashboard.keyMapDashboard.getAllKeywordList()) {
			String keyword = key.getKeyword();
			multiWordkSuggestKey.add(keyword);
			keywordList.add(keyword);
		}

		keyWordsListPending = new ArrayList<String>();
		keywordMap = new HashMap<String, Widget>();
		suggestKey.setText("");
		hKeyPanel.clear();
		if (!Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			// Filebrowser panel selected
			if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
					docKeywords = Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.getKeywords();
				} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isFolderSelected()) {
					docKeywords = Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.getKeywords();
				} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isMailSelected()) {
					docKeywords = Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mail.getKeywords();
				}
			} else {
				// Otherside tree panel is selected
				docKeywords = Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.getKeywords();
			}

			// Draw buttons and tagcloud
			for (String keyword : docKeywords) {
				Widget keywordButton = getKeyWidget(keyword, remove);
				keywordMap.put(keyword, keywordButton);
				hKeyPanel.add(keywordButton);
			}

		} else {
			docKeywords = new HashSet<String>(); // hashset to be compatible with document.getKeywords(); etc..
		}
		// Dra
		WidgetUtil.drawTagCloud(keywordsCloud, docKeywords);
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
				Widget keywordButton = getKeyWidget(keyword, remove); // Always allow remove added keyword preventing error adding
				keywordMap.put(keyword, keywordButton);
				hKeyPanel.add(keywordButton);
				docKeywords.add(keyword);
				addKeyword(keyword);
				Main.get().mainPanel.dashboard.keyMapDashboard.increaseKeywordRate(keyword);
			} else if (keyWordsListPending.isEmpty()) {
				WidgetUtil.drawTagCloud(keywordsCloud, docKeywords);
			} else {
				addPendingKeyWordsList();	
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
			WidgetUtil.drawTagCloud(keywordsCloud, docKeywords);
		}
	}

	/**
	 * addKeyword document
	 */
	public void addKeyword(String keyword) {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			status.setFlagAddKeywords();
			massiveService.addKeyword(Main.get().mainPanel.desktop.browser.fileBrowser.getAllSelectedPaths(), keyword,
					new AsyncCallback<Object>() {
						@Override
						public void onSuccess(Object result) {
							if (keyWordsListPending.isEmpty()) {
								WidgetUtil.drawTagCloud(keywordsCloud, docKeywords);
							} else {
								addPendingKeyWordsList();
							}
							status.unsetFlagAddKeywords();
						}

						@Override
						public void onFailure(Throwable caught) {
							if (keyWordsListPending.isEmpty()) {
								WidgetUtil.drawTagCloud(keywordsCloud, docKeywords);
							} else {
								addPendingKeyWordsList();
							}
							status.unsetFlagAddKeywords();
							Main.get().showError("addKeyword", caught);
						}
					});
		} else {
			// Filebrowser panel selected
			if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.addKeyword(keyword);
				} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isFolderSelected()) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.addKeyword(keyword);
				} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isMailSelected()) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mail.addKeyword(keyword);
				}
			} else {
				// Otherside tree panel is selected
				Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.addKeyword(keyword);
			}
			if (keyWordsListPending.isEmpty()) {
				WidgetUtil.drawTagCloud(keywordsCloud, docKeywords);
			} else {
				addPendingKeyWordsList();
			}
		}
	}

	/**
	 * removeKeyword document
	 */
	public void removeKeyword(String keyword) {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			status.setFlagRemoveKeywords();
			massiveService.removeKeyword(Main.get().mainPanel.desktop.browser.fileBrowser.getAllSelectedPaths(),
					keyword, new AsyncCallback<Object>() {
						@Override
						public void onSuccess(Object result) {
							status.unsetFlagRemoveKeywords();
						}

						@Override
						public void onFailure(Throwable caught) {
							status.unsetFlagRemoveKeywords();
							Main.get().showError("removeKeyword", caught);
						}
					});
		} else {
			// Filebrowser panel selected
			if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.removeKeyword(keyword);
				} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isFolderSelected()) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.removeKeyword(keyword);
				} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isMailSelected()) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mail.removeKeyword(keyword);
				}
			} else {
				// Otherside tree panel is selected
				Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.removeKeyword(keyword);
			}
		}
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		setText(Main.i18n("keyword.add"));
	}
}