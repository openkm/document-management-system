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

package com.openkm.frontend.client.widget.properties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTKeyword;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.extension.event.HasDocumentEvent;
import com.openkm.frontend.client.extension.event.HasFolderEvent;
import com.openkm.frontend.client.extension.event.HasMailEvent;
import com.openkm.frontend.client.service.OKMPropertyService;
import com.openkm.frontend.client.service.OKMPropertyServiceAsync;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.widget.ConfirmPopup;
import com.openkm.frontend.client.widget.WidgetUtil;
import com.openkm.frontend.client.widget.dashboard.ImageHover;
import com.openkm.frontend.client.widget.dashboard.keymap.TagCloud;

import java.util.*;

/**
 * KeywordManager
 *
 * @author jllort
 *
 */
public class KeywordManager {
	private final OKMPropertyServiceAsync propertyService = (OKMPropertyServiceAsync) GWT.create(OKMPropertyService.class);

	private HorizontalPanel keywordPanel;
	private SuggestBox suggestKey;
	private MultiWordSuggestOracle multiWordkSuggestKey;
	private List<String> keywordList;
	private List<String> keyWordsListPending; // Keyword list pending to be added ( each one is added sequentially )
	private Image thesaurusImage;
	private FlowPanel hKeyPanel;
	private SimplePanel sp;
	private Map<String, Widget> keywordMap;
	private TagCloud keywordsCloud;
	private HTML keywordsCloudText;
	private HTML keywordsText;
	private Set<String> keywords;
	private String path = "";
	private boolean remove;
	private boolean keyShortcutsEnabled = true;
	private boolean removeKeywordEnabled = false;
	private Object object;

	/**
	 * KeywordManager
	 *
	 * @param selectedFrom
	 */
	public KeywordManager(final int selectedFrom) {
		// Keywords
		keywordsCloud = new TagCloud();
		keywordsCloud.setWidth("350px");
		keywordsCloudText = new HTML("<b>" + Main.i18n("document.keywords.cloud") + "</b>");
		keywordsText = new HTML("<b>" + Main.i18n("document.keywords") + "</b>");
		keywordMap = new HashMap<String, Widget>();
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
				Main.get().mainPanel.desktop.navigator.thesaurusTree.thesaurusSelectPopup.show(selectedFrom);
			}
		});

		keywordPanel = new HorizontalPanel();
		sp = new SimplePanel();
		sp.setWidth("16px");
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
		keywordPanel.add(sp);
		keywordPanel.setVisible(false);

		suggestKey.setStyleName("okm-KeyMap-Suggest");
		suggestKey.addStyleName("okm-Input");
		thesaurusImage.addStyleName("okm-Hyperlink");
		hKeyPanel.setStylePrimaryName("okm-cloudWrap");
		keywordsCloud.setStylePrimaryName("okm-cloudWrap");
	}

	/**
	 * setObject
	 *
	 * @param object
	 * @param remove
	 */
	public void setObject(Object object, boolean remove) {
		this.object = object;
		this.remove = remove;
		keywords = new HashSet<String>();
		if (object instanceof GWTDocument) {
			keywords = ((GWTDocument) object).getKeywords();
			path = ((GWTDocument) object).getPath();
		} else if (object instanceof GWTFolder) {
			keywords = ((GWTFolder) object).getKeywords();
			path = ((GWTFolder) object).getPath();
		} else if (object instanceof GWTMail) {
			keywords = ((GWTMail) object).getKeywords();
			path = ((GWTMail) object).getPath();
		}
	}

	/**
	 * drawAll
	 */
	public void drawAll() {
		hKeyPanel.clear();
		for (String keyword : keywords) {
			Widget keywordButton = getKeyWidget(keyword, remove);
			keywordMap.put(keyword, keywordButton);
			hKeyPanel.add(keywordButton);
		}

		// Reloading keyword list
		multiWordkSuggestKey.clear();
		keywordList = new ArrayList<String>();
		for (GWTKeyword key : Main.get().mainPanel.dashboard.keyMapDashboard.getAllKeywordList()) {
			String keyword = key.getKeyword();
			multiWordkSuggestKey.add(keyword);
			keywordList.add(keyword);
		}

		WidgetUtil.drawTagCloud(keywordsCloud, keywords);
		keywordsCloudText.setVisible(true);
		keywordsCloud.setVisible(true);
	}

	/**
	 * setVisible
	 *
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		suggestKey.setVisible(visible);
		thesaurusImage.setVisible(visible);
	}

	/**
	 * reset
	 */
	public void reset() {
		hKeyPanel.clear();
		keywordMap = new HashMap<String, Widget>();
		keyWordsListPending = new ArrayList<String>();
	}

	/**
	 * getKeywordText
	 *
	 * @return
	 */
	public Widget getKeywordText() {
		return keywordsText;
	}

	/**
	 * getKeywordPanel
	 *
	 * @return
	 */
	public Widget getKeywordPanel() {
		return keywordPanel;
	}

	/**
	 * getKeywordCloudText
	 *
	 * @return
	 */
	public Widget getKeywordCloudText() {
		return keywordsCloudText;
	}

	/**
	 * getKeywordCloud
	 *
	 * @return
	 */
	public Widget getKeywordCloud() {
		return keywordsCloud;
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
	 * Removes a key
	 *
	 * @param keyword The key to be removed
	 */
	public void removeKey(String keyword) {
		if (keywordMap.containsKey(keyword)) {
			keywordMap.remove(keyword);
			keywords.remove(keyword);
			removeKeyword(keyword);
			Main.get().mainPanel.dashboard.keyMapDashboard.decreaseKeywordRate(keyword);
			WidgetUtil.drawTagCloud(keywordsCloud, keywords);
			if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_THESAURUS) {
				GWTFolder folder = ((GWTFolder) Main.get().activeFolderTree.actualItem.getUserObject());
				// When remove the keyword for which are browsing must refreshing filebrowser view
				if (folder.getPath().substring(folder.getPath().lastIndexOf("/") + 1).replace(" ", "_").equals(keyword)) {
					Main.get().mainPanel.desktop.browser.fileBrowser.refresh(folder.getPath());
				}
			}
		}
	}

	/**
	 * Adds keywords sequentially
	 *
	 */
	public void addPendingKeyWordsList() {
		if (!keyWordsListPending.isEmpty()) {
			addKeyword(keyWordsListPending.remove(0));
		}
	}

	/**
	 * Callback addKeyword document
	 */
	final AsyncCallback<Object> callbackAddKeywords = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			String keyword = (String) result;
			Widget keywordButton = getKeyWidget(keyword, remove);
			keywordMap.put(keyword, keywordButton);
			hKeyPanel.add(keywordButton);
			keywords.add(keyword);

			if (keyWordsListPending.isEmpty()) {				
				WidgetUtil.drawTagCloud(keywordsCloud, keywords);

				if (object instanceof GWTDocument) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.fireEvent(HasDocumentEvent.KEYWORD_ADDED);
				} else if (object instanceof GWTFolder) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.fireEvent(HasFolderEvent.KEYWORD_ADDED);
				} else if (object instanceof GWTMail) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.fireEvent(HasMailEvent.KEYWORD_ADDED);
				}
			} else {
				addPendingKeyWordsList();
			}
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetKeywords();
		}

		public void onFailure(Throwable caught) {
			if (keyWordsListPending.isEmpty()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetKeywords();
				WidgetUtil.drawTagCloud(keywordsCloud, keywords);
			} else {
				addPendingKeyWordsList();
			}

			Main.get().showError("AddKeyword", caught);
		}
	};

	/**
	 * Callback removeKeyword mail
	 */
	final AsyncCallback<Object> callbackRemoveKeywords = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetKeywords();
			if (object instanceof GWTDocument) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.fireEvent(HasDocumentEvent.KEYWORD_REMOVED);
			} else if (object instanceof GWTFolder) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.fireEvent(HasFolderEvent.KEYWORD_REMOVED);
			} else if (object instanceof GWTMail) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.fireEvent(HasMailEvent.KEYWORD_REMOVED);
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetKeywords();
			Main.get().showError("RemoveKeyword", caught);
		}
	};

	/**
	 * @param enabled
	 */
	public void setKeywordEnabled(boolean enabled) {
		suggestKey.getTextBox().setEnabled(enabled);
	}

	/**
	 * addKeyword mail
	 */
	public void addKeyword(String keyword) {
		if (!keywordMap.containsKey(keyword) && keyword.length() > 0) {
			for (Iterator<String> it = keywordMap.keySet().iterator(); it.hasNext(); ) {
				String key = it.next();

				if (!keywordList.contains(key)) {
					multiWordkSuggestKey.add(key);
					keywordList.add(key);
				}
			}

			Main.get().mainPanel.desktop.browser.tabMultiple.status.setKeywords();
			propertyService.addKeyword(path, keyword, callbackAddKeywords);
			Main.get().mainPanel.dashboard.keyMapDashboard.increaseKeywordRate(keyword);
		} else if (keyWordsListPending.isEmpty()) {
			WidgetUtil.drawTagCloud(keywordsCloud, keywords);
		} else {
			addPendingKeyWordsList();	
		}	
	}

	/**
	 * removeKeyword mail
	 */
	public void removeKeyword(String keyword) {
		Main.get().mainPanel.desktop.browser.tabMultiple.status.setKeywords();
		propertyService.removeKeyword(path, keyword, callbackRemoveKeywords);
	}

	/**
	 * removeKeyword 
	 *
	 * @param ktr
	 */
	public void removeKeyword(KeywordToRemove ktr) {
		removeKey(ktr.getKeyword());
		hKeyPanel.remove(ktr.getExternalPanel());
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
				KeywordToRemove ktr = new KeywordToRemove(externalPanel, keyword);
				if (object instanceof GWTDocument) {
					Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_KEYWORD_DOCUMENT);
				} else if (object instanceof GWTFolder) {
					Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_KEYWORD_FOLDER);
				} else if (object instanceof GWTMail) {
					Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_KEYWORD_MAIL);
				}
				Main.get().confirmPopup.setValue(ktr);
				Main.get().confirmPopup.show();
			}
		});
		delete.setStyleName("okm-KeyMap-ImageHover");
		hPanel.add(new HTML(keyword));
		hPanel.add(space);
		if (remove && removeKeywordEnabled) {
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
	 * showAddKeyword
	 */
	public void showAddKeyword() {
		keywordPanel.setVisible(true);
	}

	/**
	 * showRemoveKeyword
	 */
	public void showRemoveKeyword() {
		removeKeywordEnabled = true;
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		keywordsCloudText.setHTML("<b>" + Main.i18n("document.keywords.cloud") + "</b>");
		keywordsText.setHTML("<b>" + Main.i18n("document.keywords") + "</b>");
	}

	/**
	 * KeywordToRemove
	 *
	 * @author jllort
	 *
	 */
	public class KeywordToRemove {
		private HorizontalPanel externalPanel;
		private String keyword;

		public KeywordToRemove(HorizontalPanel externalPanel, String keyword) {
			this.externalPanel = externalPanel;
			this.keyword = keyword;
		}

		public HorizontalPanel getExternalPanel() {
			return externalPanel;
		}

		public void setExternalPanel(HorizontalPanel externalPanel) {
			this.externalPanel = externalPanel;
		}

		public String getKeyword() {
			return keyword;
		}

		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}
	}
}