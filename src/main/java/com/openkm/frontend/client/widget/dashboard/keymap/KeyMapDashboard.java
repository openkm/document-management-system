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

package com.openkm.frontend.client.widget.dashboard.keymap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTKeyword;
import com.openkm.frontend.client.bean.GWTQueryParams;
import com.openkm.frontend.client.bean.GWTQueryResult;
import com.openkm.frontend.client.bean.GWTResultSet;
import com.openkm.frontend.client.constants.ui.UIDashboardConstants;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.service.OKMSearchService;
import com.openkm.frontend.client.service.OKMSearchServiceAsync;
import com.openkm.frontend.client.widget.dashboard.AnchorExtended;
import com.openkm.frontend.client.widget.dashboard.ControlSearchIn;
import com.openkm.frontend.client.widget.dashboard.ImageHover;

import java.util.*;

/**
 * KeyMapDashboard
 *
 * @author jllort
 *
 */
@SuppressWarnings("deprecation")
public class KeyMapDashboard extends Composite {

	private final OKMSearchServiceAsync searchService = (OKMSearchServiceAsync) GWT.create(OKMSearchService.class);
	private HorizontalSplitPanel horizontalSplitPanel;
	private KeywordWidget keyAllTable;
	private KeywordWidget keyTopTable;
	private KeywordWidget keyRelatedTable;
	private VerticalPanel vPanel;
	private HorizontalPanel controlPanel;
	private HorizontalPanel paginationPanel;
	private Image clean;
	private Image small;
	private Image medium;
	private Image big;
	private HTML keywordsTXT;
	private HorizontalPanel selectedKeyPanel;
	private HorizontalPanel suggestKeyPanel;
	private Map<String, Widget> selectedKeyMap;
	private SuggestBox suggestKey;
	private MultiWordSuggestOracle multiWordSuggestKey;
	private List<String> keywordList;
	public ScrollPanel scrollTable;
	HTML flowPanelDivisor;
	public KeyMapTable table;
	private int offset = 0;
	private int limit = 10;
	ListBox context;
	private ListBox resultPage;
	private HTML resultPageTXT;
	private ControlSearchIn controlSearchIn;
	private List<GWTKeyword> allKeywordList;
	private List<GWTKeyword> relatedKeywordList;
	private Map<String, String> rateMap;
	private int totalMaxFrequency = 1;
	private int totalMinFrequency = 1;
	private TagCloud tagCloud;
	private boolean personalVisible = false;
	private boolean mailVisible = false;
	private boolean trashVisible = false;
	private boolean templatesVisible = false;
	private boolean firstTime = true;
	private boolean refresh = false;
	private int posTaxonomy = 0;
	private int posTemplates = 0;
	private int posPersonal = 0;
	private int posMail = 0;
	private int posTrash = 0;
	private int posAllContext = 0;
	private boolean dashboardVisible = false;

	/**
	 * KeyMapDashboard
	 */
	public KeyMapDashboard() {
		horizontalSplitPanel = new HorizontalSplitPanel();
		keyAllTable = new KeywordWidget(Main.i18n("dashboard.keyword.all"));
		keyTopTable = new KeywordWidget(Main.i18n("dashboard.keyword.top"));
		keyRelatedTable = new KeywordWidget(Main.i18n("dashboard.keyword.related"));

		allKeywordList = new ArrayList<GWTKeyword>();
		relatedKeywordList = new ArrayList<GWTKeyword>();
		rateMap = new HashMap<String, String>();
		HTML space = new HTML("&nbsp;");
		flowPanelDivisor = new HTML("&nbsp;");

		tagCloud = new TagCloud();
		table = new KeyMapTable();
		VerticalPanel contentPanel = new VerticalPanel();
		contentPanel.add(tagCloud);
		contentPanel.add(space);
		contentPanel.add(flowPanelDivisor);
		contentPanel.add(table);
		contentPanel.setWidth("100%");
		tagCloud.setWidth("100%");
		space.setWidth("100%");
		space.setHeight("10px");
		flowPanelDivisor.setWidth("100%");
		flowPanelDivisor.setHeight("5px");
		scrollTable = new ScrollPanel(contentPanel);

		tagCloud.setStylePrimaryName("okm-cloudWrap");
		flowPanelDivisor.setStyleName("okm-cloudSeparator");
		table.addStyleName("okm-DisableSelect");

		vPanel = new VerticalPanel();
		controlPanel = new HorizontalPanel();
		paginationPanel = new HorizontalPanel();
		selectedKeyMap = new HashMap<String, Widget>();
		multiWordSuggestKey = new MultiWordSuggestOracle();
		keywordList = new ArrayList<String>();
		suggestKey = new SuggestBox(multiWordSuggestKey);
		suggestKey.setHeight("20px");
		suggestKey.setText(Main.i18n("dashboard.keyword.suggest"));
		suggestKey.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if ((char) KeyCodes.KEY_ENTER == event.getNativeKeyCode()) {
					selectKey(suggestKey.getText());
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

		vPanel.add(controlPanel);
		vPanel.add(scrollTable);
		vPanel.add(paginationPanel);

		// Image control
		HorizontalPanel imageControlPanel = new HorizontalPanel();
		HTML space1 = new HTML();
		HTML space2 = new HTML();
		small = new Image("img/icon/actions/description_small_disabled.gif");
		small.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (table.getActualDetail() != KeyMapTable.VISIBLE_SMALL) {
					disableAllDetailIcons();
					table.changeVisibilityDetail(KeyMapTable.VISIBLE_SMALL);
					small.setUrl("img/icon/actions/description_small.gif");
				}
			}
		});
		medium = new Image("img/icon/actions/description_medium.gif"); // It's enabled view by default
		medium.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (table.getActualDetail() != KeyMapTable.VISIBLE_MEDIUM) {
					disableAllDetailIcons();
					table.changeVisibilityDetail(KeyMapTable.VISIBLE_MEDIUM);
					medium.setUrl("img/icon/actions/description_medium.gif");
				}
			}
		});
		big = new Image("img/icon/actions/description_big_disabled.gif");
		big.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (table.getActualDetail() != KeyMapTable.VISIBLE_BIG) {
					disableAllDetailIcons();
					table.changeVisibilityDetail(KeyMapTable.VISIBLE_BIG);
					big.setUrl("img/icon/actions/description_big.gif");
				}
			}
		});
		imageControlPanel.add(space1);
		imageControlPanel.add(small);
		imageControlPanel.add(medium);
		imageControlPanel.add(big);
		imageControlPanel.add(space2);
		imageControlPanel.setCellWidth(space1, "8px");
		imageControlPanel.setCellWidth(small, "21px");
		imageControlPanel.setCellWidth(medium, "21px");
		imageControlPanel.setCellWidth(big, "21px");
		imageControlPanel.setCellHeight(small, "20px");
		imageControlPanel.setCellHeight(medium, "20px");
		imageControlPanel.setCellHeight(big, "20px");
		imageControlPanel.setCellWidth(space2, "8px");
		imageControlPanel.setCellHorizontalAlignment(small, HasAlignment.ALIGN_CENTER);
		imageControlPanel.setCellHorizontalAlignment(medium, HasAlignment.ALIGN_CENTER);
		imageControlPanel.setCellHorizontalAlignment(big, HasAlignment.ALIGN_CENTER);
		imageControlPanel.setCellVerticalAlignment(small, HasAlignment.ALIGN_MIDDLE);
		imageControlPanel.setCellVerticalAlignment(medium, HasAlignment.ALIGN_MIDDLE);
		imageControlPanel.setCellVerticalAlignment(big, HasAlignment.ALIGN_MIDDLE);

		// KeyWords text
		keywordsTXT = new HTML();
		keywordsTXT.setHTML("<b>" + Main.i18n("dashboard.keyword") + "</b>");
		HTML space3 = new HTML();
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(keywordsTXT);
		hPanel.add(space3);
		hPanel.setCellWidth(space3, "8px");
		hPanel.setCellVerticalAlignment(keywordsTXT, HasAlignment.ALIGN_MIDDLE);

		selectedKeyPanel = new HorizontalPanel();
		suggestKeyPanel = new HorizontalPanel();
		HTML space4 = new HTML();
		clean = new Image("img/icon/actions/clean_disabled.gif");
		clean.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (selectedKeyMap.keySet().size() > 0) {
					// Resets keywordPanel
					for (String key : selectedKeyMap.keySet()) {
						selectedKeyPanel.remove((Widget) selectedKeyMap.get(key));
					}
					selectedKeyMap = new HashMap<String, Widget>();
					keyAllTable.unselectAllRows();
					keyTopTable.unselectAllRows();
					keyRelatedTable.unselectAllRows();
					keyRelatedTable.setVisible(false);
					table.reset();
					context.setSelectedIndex(posAllContext);
					controlSearchIn.refreshControl(0);

					getKeywordMap(); // Gets related keyMap
					refreshClean();
				}
			}
		});
		clean.setTitle(Main.i18n("dashboard.keyword.clean.keywords"));
		suggestKeyPanel.add(suggestKey); // Always must be the last
		suggestKeyPanel.add(space4);
		suggestKeyPanel.add(clean);
		suggestKeyPanel.setCellWidth(space4, "8px");
		suggestKeyPanel.setCellWidth(clean, "21px");
		suggestKeyPanel.setCellHorizontalAlignment(space4, HasAlignment.ALIGN_RIGHT);
		suggestKeyPanel.setCellVerticalAlignment(suggestKey, HasAlignment.ALIGN_MIDDLE);
		suggestKeyPanel.setCellVerticalAlignment(clean, HasAlignment.ALIGN_MIDDLE);

		selectedKeyPanel.add(hPanel);
		selectedKeyPanel.add(suggestKeyPanel); // Always must be the last
		selectedKeyPanel.setCellVerticalAlignment(hPanel, HasAlignment.ALIGN_MIDDLE);
		selectedKeyPanel.setCellVerticalAlignment(suggestKeyPanel, HasAlignment.ALIGN_MIDDLE);

		controlPanel.add(imageControlPanel);
		controlPanel.add(selectedKeyPanel);

		controlPanel.setCellWidth(imageControlPanel, "80px");
		controlPanel.setCellVerticalAlignment(imageControlPanel, HasAlignment.ALIGN_MIDDLE);
		controlPanel.setCellVerticalAlignment(selectedKeyPanel, HasAlignment.ALIGN_MIDDLE);

		// Pagination
		HorizontalPanel internalPaginationPanel = new HorizontalPanel();
		context = new ListBox();
		context.setStyleName("okm-Select");
		int count = 0;
		posTaxonomy = count++;
		context.addItem(Main.i18n("leftpanel.label.taxonomy"), "");
		if (templatesVisible) {
			posTemplates = count++;
			context.addItem(Main.i18n("leftpanel.label.templates"), "");
		}
		if (personalVisible) {
			posPersonal = count++;
			context.addItem(Main.i18n("leftpanel.label.my.documents"), "");
		}
		if (mailVisible) {
			posMail = count++;
			context.addItem(Main.i18n("leftpanel.label.mail"), "");
		}
		if (trashVisible) {
			posTrash = count++;
			context.addItem(Main.i18n("leftpanel.label.trash"), "");
		}
		posAllContext = count++;
		context.addItem(Main.i18n("leftpanel.label.all.repository"), "");
		context.setSelectedIndex(posAllContext);

		context.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				controlSearchIn.executeSearch(limit);
			}
		});

		resultPage = new ListBox();
		resultPage.addItem("10", "10");
		resultPage.addItem("20", "20");
		resultPage.addItem("30", "30");

		resultPage.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				limit = Integer.valueOf(resultPage.getValue(resultPage.getSelectedIndex()));
				controlSearchIn.executeSearch(limit);
			}
		});

		HTML space5 = new HTML();
		HTML space6 = new HTML();
		HTML space7 = new HTML();
		resultPageTXT = new HTML(Main.i18n("search.page.results"));
		controlSearchIn = new ControlSearchIn();
		controlSearchIn.refreshControl(0);
		internalPaginationPanel.add(space5);
		internalPaginationPanel.add(context);
		internalPaginationPanel.add(space6);
		internalPaginationPanel.add(resultPageTXT);
		internalPaginationPanel.add(space7);
		internalPaginationPanel.add(resultPage);

		internalPaginationPanel.setCellWidth(space5, "8px");
		internalPaginationPanel.setCellWidth(space6, "8px");
		internalPaginationPanel.setCellWidth(space7, "8px");
		internalPaginationPanel.setCellHorizontalAlignment(context, HasAlignment.ALIGN_LEFT);
		internalPaginationPanel.setCellVerticalAlignment(context, HasAlignment.ALIGN_MIDDLE);
		internalPaginationPanel.setCellVerticalAlignment(resultPageTXT, HasAlignment.ALIGN_MIDDLE);
		internalPaginationPanel.setCellVerticalAlignment(resultPage, HasAlignment.ALIGN_MIDDLE);

		HTML space8 = new HTML();
		HTML space9 = new HTML();
		paginationPanel.add(internalPaginationPanel);
		paginationPanel.add(space8);
		paginationPanel.add(controlSearchIn);
		paginationPanel.add(space9);
		paginationPanel.setCellWidth(space8, "8px");
		paginationPanel.setCellWidth(space9, "8px");
		paginationPanel.setCellHorizontalAlignment(internalPaginationPanel, HasAlignment.ALIGN_LEFT);
		paginationPanel.setCellVerticalAlignment(internalPaginationPanel, HasAlignment.ALIGN_MIDDLE);
		paginationPanel.setCellVerticalAlignment(controlSearchIn, HasAlignment.ALIGN_MIDDLE);
		paginationPanel.setCellHorizontalAlignment(controlSearchIn, HasAlignment.ALIGN_RIGHT);

		suggestKey.setStyleName("okm-KeyMap-Suggest");
		suggestKey.addStyleName("okm-Input");
		controlPanel.setStyleName("okm-KeyMapControl");
		controlPanel.addStyleName("okm-NoWrap");
		imageControlPanel.addStyleName("okm-NoWrap");
		selectedKeyPanel.addStyleName("okm-NoWrap");
		paginationPanel.setStyleName("okm-PaginationControl");
		paginationPanel.addStyleName("okm-NoWrap");
		internalPaginationPanel.addStyleName("okm-NoWrap");
		keywordsTXT.addStyleName("okm-NoWrap");
		resultPage.setStyleName("okm-Input");
		resultPageTXT.addStyleName("okm-NoWrap");
		hPanel.addStyleName("okm-NoWrap");
		clean.setStyleName("okm-KeyMap-ImageHover");
		small.setStyleName("okm-KeyMap-ImageHover");
		medium.setStyleName("okm-KeyMap-ImageHover");
		big.setStyleName("okm-KeyMap-ImageHover");
		tagCloud.setStylePrimaryName("okm-cloudWrap");

		VerticalPanel vKeyPanel = new VerticalPanel();
		vKeyPanel.setWidth("100%");

		vKeyPanel.add(keyRelatedTable);
		vKeyPanel.add(keyTopTable);
		vKeyPanel.add(keyAllTable);

		keyRelatedTable.setVisible(false); // By default related table is only visible when has some content

		horizontalSplitPanel.setRightWidget(vKeyPanel);
		horizontalSplitPanel.setLeftWidget(vPanel);

		initWidget(horizontalSplitPanel);
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.UIObject#setSize(java.lang.String, java.lang.String)
	 */
	public void setSize(String width, String height) {
		horizontalSplitPanel.setSize(width + "px", height + "px");
		horizontalSplitPanel.setSplitPosition("" + (Integer.valueOf(width) - 220) + "px");
		vPanel.setSize("100%", height + "px");
		controlPanel.setSize("100%", "30px");
		paginationPanel.setSize("100%", "30px");
		scrollTable.setSize("100%", "" + (Integer.valueOf(height) - 60) + "px");
	}

	/**
	 * Refreshing language
	 */
	public void langRefresh() {
		keywordsTXT.setHTML("<b>" + Main.i18n("dashboard.keyword") + "</b>");
		suggestKey.setText(Main.i18n("dashboard.keyword.suggest"));
		keyAllTable.setHeaderText(Main.i18n("dashboard.keyword.all"));
		keyTopTable.setHeaderText(Main.i18n("dashboard.keyword.top"));
		keyRelatedTable.setHeaderText(Main.i18n("dashboard.keyword.related"));

		resultPageTXT.setHTML(Main.i18n("search.page.results"));
		controlSearchIn.langRefresh();

		context.setItemText(posTaxonomy, Main.i18n("leftpanel.label.taxonomy"));
		if (templatesVisible) {
			context.setItemText(posTemplates, Main.i18n("leftpanel.label.templates"));
		}
		if (personalVisible) {
			context.setItemText(posPersonal, Main.i18n("leftpanel.label.my.documents"));
		}
		if (mailVisible) {
			context.setItemText(posMail, Main.i18n("leftpanel.label.mail"));
		}
		if (trashVisible) {
			context.setItemText(posTrash, Main.i18n("leftpanel.label.trash"));
		}
		context.setItemText(posAllContext, Main.i18n("leftpanel.label.all.repository"));

		table.langRefresh();
	}

	/**
	 * Gets the keyword map callback
	 */
	final AsyncCallback<List<GWTKeyword>> callbackGetKeywordMap = new AsyncCallback<List<GWTKeyword>>() {
		public void onSuccess(List<GWTKeyword> result) {
			List<GWTKeyword> top10List = new ArrayList<GWTKeyword>();
			multiWordSuggestKey.clear();
			keywordList = new ArrayList<String>();
			keyAllTable.reset();
			allKeywordList.clear();
			rateMap.clear();
			for (GWTKeyword keyword : result) {
				allKeywordList.add(keyword);
				rateMap.put(keyword.getKeyword(), "" + keyword.getFrequency());
				multiWordSuggestKey.add(keyword.getKeyword());
				keywordList.add(keyword.getKeyword());
				if (dashboardVisible) {
					keyAllTable.add(keyword);
					if (keyword.isTop10()) {
						top10List.add(keyword);
					}
				}
			}
			keyAllTable.unsetRefreshing();

			keyTopTable.reset();
			// Adding top 10 ordered from greater to lower
			if (dashboardVisible) {
				while (!top10List.isEmpty()) {
					// Looking for max
					int max = 0;
					GWTKeyword selectedKeyword = new GWTKeyword();
					for (GWTKeyword tmpKeyword : top10List) {
						if (max < tmpKeyword.getFrequency()) {
							selectedKeyword = tmpKeyword;
							max = tmpKeyword.getFrequency();
						}
					}
					top10List.remove(selectedKeyword);
					if (dashboardVisible) {
						keyTopTable.add(selectedKeyword);
					}
				}
			}

			if (refresh) {
				refresh = false;
				if (dashboardVisible) {
					// Restoring selected keywords after refreshing
					for (String keyword : selectedKeyMap.keySet()) {
						keyAllTable.selectRow(keyword);
						keyTopTable.selectRow(keyword);
					}
				}
			}

			keyTopTable.unsetRefreshing();

			getKeywordMap(getFiltering()); // Call for getting related keywords
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getKeywordMap", caught);
			keyAllTable.unsetRefreshing();
			keyTopTable.unsetRefreshing();
		}
	};

	/**
	 * Gets the keyword map callback
	 */
	final AsyncCallback<List<GWTKeyword>> callbackGetKeywordMapFiltered = new AsyncCallback<List<GWTKeyword>>() {
		public void onSuccess(List<GWTKeyword> result) {
			if (dashboardVisible) {
				keyRelatedTable.reset();
				keyRelatedTable.setVisible(result.size() > 0); // Show or hide table if has values
				relatedKeywordList.clear();
				for (Iterator<GWTKeyword> it = result.iterator(); it.hasNext(); ) {
					GWTKeyword keyword = it.next();
					keyRelatedTable.add(keyword);
					relatedKeywordList.add(keyword);
				}
				keyRelatedTable.unsetRefreshing();
				drawTagCloud(); // Draws tag cloud
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getKeywordMap", caught);
			keyRelatedTable.unsetRefreshing();
		}
	};

	/**
	 * Gets the find paginated callback
	 */
	final AsyncCallback<GWTResultSet> callbackFindPaginated = new AsyncCallback<GWTResultSet>() {
		public void onSuccess(GWTResultSet result) {
			table.reset();
			for (GWTQueryResult queryResult : result.getResults()) {
				if (queryResult.getDocument() != null) {
					table.addRow(queryResult);
				} else if (queryResult.getFolder() != null) {
					table.addRow(queryResult);
				} else if (queryResult.getMail() != null) {
					table.addRow(queryResult);
				}
			}
			controlSearchIn.refreshControl(result.getTotal());
			table.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("findPaginated", caught);
			table.unsetRefreshing();
		}
	};

	/**
	 * getKeywordMap
	 */
	public void getKeywordMap() {
		if (showStatus()) {
			keyAllTable.setRefreshing();
			keyTopTable.setRefreshing();
		}
		searchService.getKeywordMap(new ArrayList<String>(), callbackGetKeywordMap);
	}

	/**
	 * getKeywordMap
	 */
	public void getKeywordMap(List<String> filter) {
		// Only make call when collection is not empty, other case there's no filtering by keyword and
		// result it'll be inconsistent
		if (!filter.isEmpty()) {
			if (showStatus() && keyRelatedTable.isVisible()) {
				keyRelatedTable.setRefreshing();
			}
			searchService.getKeywordMap(filter, callbackGetKeywordMapFiltered);
		} else if (dashboardVisible) {
			keyRelatedTable.reset();
			keyRelatedTable.setVisible(false);
			drawTagCloud(); // Draws tag cloud
		}
	}

	/**
	 * Fins paginated setting the offset and limit
	 *
	 * @param offset The offset
	 * @param limit The limit
	 */
	public void findPaginated(int offset, int limit) {
		this.offset = offset;
		this.limit = limit;

		if (showStatus()) {
			table.setRefreshing();
		}
		GWTQueryParams params = new GWTQueryParams();
		params.setKeywords(getWordsToFilter());
		params.setDomain(GWTQueryParams.DOCUMENT | GWTQueryParams.FOLDER | GWTQueryParams.MAIL); // Only make searches
		// for documents
		limit = Integer.parseInt(resultPage.getItemText(resultPage.getSelectedIndex()));

		int index = context.getSelectedIndex();
		if (index == posTaxonomy) {
			params.setPath(Main.get().repositoryContext.getContextTaxonomy());
		} else if (index == posTemplates) {
			params.setPath(Main.get().repositoryContext.getContextTemplates());
		} else if (index == posPersonal) {
			params.setPath(Main.get().repositoryContext.getContextPersonal());
		} else if (index == posMail) {
			params.setPath(Main.get().repositoryContext.getContextMail());
		} else if (index == posTrash) {
			params.setPath(Main.get().repositoryContext.getContextTrash());
		} else if (index == posAllContext) {
			params.setPath("");
		}

		// Only execute search if there's some keyword
		if (params.getKeywords().equals("")) {
			table.reset();
			controlSearchIn.refreshControl(0);
			table.unsetRefreshing();
		} else {
			searchService.findPaginated(params, offset, limit, callbackFindPaginated);
		}
	}

	/**
	 * Refresh all panels
	 */
	public void refreshAll() {
		if (dashboardVisible) {
			if (firstTime) {
				findPaginated(offset, limit);
				firstTime = false;
			} else {
				controlSearchIn.executeSearch(limit);
			}
			refresh = true;
		}
		getKeywordMap(); // After finishing it call drawTagCloud
	}

	/**
	 * selects a key
	 *
	 * @param keyword the key
	 */
	public void selectKey(final String keyword) {
		// Only adds keyword if not exist
		if (!selectedKeyMap.keySet().contains(keyword) && keyword.length() > 0 && dashboardVisible) {
			selectedKeyPanel.remove(suggestKeyPanel); // Always is setting the last, must be removed
			HorizontalPanel externalPanel = new HorizontalPanel();
			HorizontalPanel hPanel = new HorizontalPanel();
			HTML html = new HTML();
			HTML space = new HTML();
			ImageHover remove = new ImageHover("img/icon/actions/delete_disabled.gif", "img/icon/actions/delete.gif");
			remove.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// remove keyword on all keyword panels
					keyAllTable.unselectRow(keyword);
					keyTopTable.unselectRow(keyword);
					keyRelatedTable.unselectRow(keyword);
					removeKey(keyword);
				}
			});
			remove.setStyleName("okm-KeyMap-ImageHover");
			html.setHTML(keyword);
			hPanel.add(html);
			hPanel.add(space);
			hPanel.add(remove);
			hPanel.setCellWidth(space, "6px");
			hPanel.setStyleName("okm-KeyMap-Selected");
			hPanel.addStyleName("okm-NoWrap");
			HTML space1 = new HTML();
			externalPanel.add(hPanel);
			externalPanel.add(space1);
			externalPanel.setCellWidth(space1, "6px");
			externalPanel.addStyleName("okm-NoWrap");
			selectedKeyPanel.add(externalPanel);
			selectedKeyPanel.add(suggestKeyPanel); // Always is setting the last
			selectedKeyPanel.setCellVerticalAlignment(externalPanel, HasAlignment.ALIGN_MIDDLE);
			selectedKeyMap.put(keyword, externalPanel);
			// Selects keyword on all keyword panels
			keyAllTable.selectRow(keyword);
			keyTopTable.selectRow(keyword);
			keyRelatedTable.selectRow(keyword);
			controlSearchIn.executeSearch(limit);

			getKeywordMap(getFiltering()); // Gets related keyMap
			refreshClean();
		}
	}

	/**
	 * Remove a key
	 *
	 * @param keyword The key
	 */
	public void removeKey(String keyword) {
		if (selectedKeyMap.containsKey(keyword) && dashboardVisible) {
			selectedKeyPanel.remove((Widget) selectedKeyMap.get(keyword));
			selectedKeyMap.remove(keyword);
		}
		// Selects keyword on all keyword panels
		if (dashboardVisible) {
			keyAllTable.unselectRow(keyword);
			keyTopTable.unselectRow(keyword);
			keyRelatedTable.unselectRow(keyword);
		}
		controlSearchIn.executeSearch(limit);
		getKeywordMap(getFiltering()); // Gets related keyMap
		refreshClean();
	}

	/**
	 * Gets the filtering
	 *
	 * @return
	 */
	public List<String> getFiltering() {
		List<String> filtering = new ArrayList<String>();
		for (String key : selectedKeyMap.keySet()) {
			filtering.add(key);
		}
		return filtering;
	}

	/**
	 * Gets the filtering words
	 *
	 * @return The words
	 */
	private String getWordsToFilter() {
		String words = "";
		for (String key : selectedKeyMap.keySet()) {
			words += key + " ";
		}
		return words;
	}

	/**
	 * Disables all detail icons
	 */
	private void disableAllDetailIcons() {
		small.setUrl("img/icon/actions/description_small_disabled.gif");
		medium.setUrl("img/icon/actions/description_medium_disabled.gif");
		big.setUrl("img/icon/actions/description_big_disabled.gif");
	}

	/**
	 * Refresh the clean
	 */
	private void refreshClean() {
		if (selectedKeyMap.keySet().size() > 0) {
			clean.setUrl("img/icon/actions/clean.gif");
		} else {
			clean.setUrl("img/icon/actions/clean_disabled.gif");
		}
	}

	/**
	 * Draws a tag cloud
	 */
	private void drawTagCloud() {
		if (dashboardVisible) {
			// Deletes all tag clouds keys
			tagCloud.clear();

			// Show or hides division between tag cloud and results
			flowPanelDivisor.setVisible(selectedKeyMap.size() > 0 || allKeywordList.size() > 0);

			if (selectedKeyMap.size() > 0) {
				tagCloud.calculateFrequencies(relatedKeywordList);
				for (Iterator<GWTKeyword> it = relatedKeywordList.iterator(); it.hasNext(); ) {
					final GWTKeyword keyword = it.next();
					AnchorExtended tagLink = new AnchorExtended(keyword.getKeyword(), true);
					tagLink.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							selectKey(keyword.getKeyword());
						}
					});
					tagLink.setStyleName("okm-cloudTags");
					int fontSize = tagCloud.getLabelSize(keyword.getFrequency());
					tagLink.setProperty("fontSize", fontSize + "pt");
					tagLink.setProperty("color", tagCloud.getColor(fontSize));
					if (fontSize > 0) {
						tagLink.setProperty("top", (tagCloud.getMaxFontSize() - fontSize) / 2 + "px");
					}
					tagCloud.add(tagLink);
				}
			} else {
				tagCloud.calculateFrequencies(allKeywordList);
				// Sets the maximun an minumum frequencies ( used by document properties tag cloud )
				totalMaxFrequency = tagCloud.getMaxFrequency();
				totalMinFrequency = tagCloud.getMinFrequency();
				for (Iterator<GWTKeyword> it = allKeywordList.iterator(); it.hasNext(); ) {
					final GWTKeyword keyword = it.next();
					AnchorExtended tagLink = new AnchorExtended(keyword.getKeyword(), true);
					tagLink.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							selectKey(keyword.getKeyword());
						}
					});
					tagLink.setStyleName("okm-cloudTags");
					int fontSize = tagCloud.getLabelSize(keyword.getFrequency());
					tagLink.setProperty("fontSize", fontSize + "pt");
					tagLink.setProperty("color", tagCloud.getColor(fontSize));
					if (fontSize > 0) {
						tagLink.setProperty("top", (tagCloud.getMaxFontSize() - fontSize) / 2 + "px");
					}
					tagCloud.add(tagLink);
				}
			}
		}
	}

	/**
	 * Gets the max frequency
	 *
	 * @return The max frequency
	 */
	public int getTotalMaxFrequency() {
		return totalMaxFrequency;
	}

	/**
	 * Get the min frequency
	 *
	 * @return The min frequency
	 */
	public int getTotalMinFrequency() {
		return totalMinFrequency;
	}

	/**
	 * Gets the keyword rate
	 *
	 * @param keyword The keywrod
	 *
	 * @return The keyword rate
	 */
	public int getKeywordRate(String keyword) {
		int rate = 1;
		if (rateMap.keySet().contains((keyword))) {
			rate = Integer.parseInt(rateMap.get(keyword));
		}
		return rate;
	}

	/**
	 * Gets the all keywords list
	 *
	 * @return All keyword list
	 */
	public List<GWTKeyword> getAllKeywordList() {
		return allKeywordList;
	}

	/**
	 * Increase the keyword rate with one
	 *
	 * @param keyword The keyword to change rate
	 */
	public void increaseKeywordRate(String keyword) {
		int rate = 1;
		if (rateMap.keySet().contains((keyword))) {
			rate = Integer.parseInt(rateMap.get(keyword));
			rate++;
		}
		rateMap.put(keyword, "" + rate);
		if (dashboardVisible) {
			keyAllTable.increaseKeywordRate(keyword, true);
			keyTopTable.increaseKeywordRate(keyword, false);
			keyRelatedTable.increaseKeywordRate(keyword, false);
		}
		refreshFrequencies();
		// In case keyword is selected must change results
		if (selectedKeyMap.containsKey(keyword)) {
			controlSearchIn.executeSearch(limit);
			getKeywordMap(getFiltering()); // Gets related keyMap
			refreshClean();
		}
		// We add new keyword in suggest list
		if (!keywordList.contains(keyword)) {
			multiWordSuggestKey.add(keyword);
			keywordList.add(keyword);
		}
	}

	/**
	 * Decrease the keyword rate with one
	 *
	 * @param keyword The keyword to change rate
	 */
	public void decreaseKeywordRate(String keyword) {
		if (rateMap.keySet().contains((keyword))) {
			int rate = Integer.parseInt(rateMap.get(keyword));
			rate--;
			if (rate <= 0) {
				// Case that is needed to remove some keyword is better to refreshing all data to prevent
				// visualization inconsistenses
				if (selectedKeyMap.containsKey(keyword) && dashboardVisible) {
					selectedKeyPanel.remove((Widget) selectedKeyMap.get(keyword)); // removes selected keyword
				}
				refreshAll();
			} else {
				rateMap.put(keyword, "" + rate);
				if (dashboardVisible) {
					keyAllTable.decreaseKeywordRate(keyword);
					keyTopTable.decreaseKeywordRate(keyword);
					keyRelatedTable.decreaseKeywordRate(keyword);
				}
				refreshFrequencies();

				// In case keyword is selected must change results
				if (selectedKeyMap.containsKey(keyword)) {
					controlSearchIn.executeSearch(limit);
					getKeywordMap(getFiltering()); // Gets related keyMap
					refreshClean();
				}
			}
		}
	}

	/**
	 * refreshing the frequencies
	 */
	private void refreshFrequencies() {
		if (dashboardVisible) {
			tagCloud.calculateFrequencies(allKeywordList);
		}
		// Sets the maximun an minumum frequencies ( used by document properties tag cloud )
		totalMaxFrequency = tagCloud.getMaxFrequency();
		totalMinFrequency = tagCloud.getMinFrequency();
	}

	/**
	 * showTemplates
	 */
	public void showTemplates() {
		// removing allcontext and add after
		context.removeItem(posAllContext);
		posTemplates = posAllContext;
		posAllContext++;
		context.addItem(Main.i18n("leftpanel.label.templates"), "");
		context.addItem(Main.i18n("leftpanel.label.all.repository"), "");
		templatesVisible = true;
	}

	/**
	 * showPersonal
	 */
	public void showPersonal() {
		// removing allcontext and add after
		context.removeItem(posAllContext);
		posPersonal = posAllContext;
		posAllContext++;
		context.addItem(Main.i18n("leftpanel.label.my.documents"), "");
		context.addItem(Main.i18n("leftpanel.label.all.repository"), "");
		personalVisible = true;
	}

	/**
	 * showMail
	 */
	public void showMail() {
		// removing allcontext and add after
		context.removeItem(posAllContext);
		posMail = posAllContext;
		posAllContext++;
		context.addItem(Main.i18n("leftpanel.label.mail"), "");
		context.addItem(Main.i18n("leftpanel.label.all.repository"), "");
		mailVisible = true;
	}

	/**
	 * showPersonal
	 */
	public void showTrash() {
		// removing allcontext and add after
		context.removeItem(posAllContext);
		posTrash = posAllContext;
		posAllContext++;
		context.addItem(Main.i18n("leftpanel.label.trash"), "");
		context.addItem(Main.i18n("leftpanel.label.all.repository"), "");
		trashVisible = true;
	}

	/**
	 * showStatus
	 *
	 * @return
	 */
	private boolean showStatus() {
		return (Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace() == UIDockPanelConstants.DASHBOARD)
				&& (Main.get().mainPanel.dashboard.getActualView() == UIDashboardConstants.DASHBOARD_KEYMAP);
	}

	/**
	 * setDashboardKeywordsVisible
	 *
	 * @param visible
	 */
	public void setDashboardKeywordsVisible(boolean visible) {
		dashboardVisible = visible;
	}
}
