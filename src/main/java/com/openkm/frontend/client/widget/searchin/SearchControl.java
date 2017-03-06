/**
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.bean.GWTQueryParams;
import com.openkm.frontend.client.bean.form.*;
import com.openkm.frontend.client.constants.ui.UISearchConstants;
import com.openkm.frontend.client.service.OKMPropertyGroupService;
import com.openkm.frontend.client.service.OKMPropertyGroupServiceAsync;
import com.openkm.frontend.client.service.OKMSearchService;
import com.openkm.frontend.client.service.OKMSearchServiceAsync;

import java.util.List;

/**
 * SearchControl
 *
 * @author jllort
 *
 */
public class SearchControl extends Composite {
	private final OKMSearchServiceAsync searchService = (OKMSearchServiceAsync) GWT.create(OKMSearchService.class);
	private final OKMPropertyGroupServiceAsync propertyGroupService = (OKMPropertyGroupServiceAsync) GWT.create(OKMPropertyGroupService.class);

	public static final int SEARCH_MODE_SIMPLE = 0;
	public static final int SEARCH_MODE_ADVANCED = 1;
	public static final int RESULTS_VIEW_NORMAL = 0;
	public static final int RESULTS_VIEW_COMPACT = 1;

	private ScrollPanel scrollPanel;
	private FlexTable table;
	public Button searchButton;
	private Button saveSearchButton;
	private Button cleanButton;
	private TextBox searchSavedName;
	private GWTQueryParams params;
	public KeyUpHandler keyUpHandler;
	private boolean isUserNews = false;
	public ControlSearchIn controlSearch;
	private ListBox resultPage;
	HorizontalPanel searchTypePanel;
	public final CheckBox searchTypeAnd;
	public final CheckBox searchTypeOr;
	public CheckBox advancedView;
	public CheckBox compactResultsView;
	public CheckBox showPropertyGroups;
	public CheckBox saveUserNews;
	private HTML resultsPageText;
	private HTML searchTypeText;
	private int searchMode = SEARCH_MODE_SIMPLE;
	private int resultsViewMode = RESULTS_VIEW_COMPACT;
	private int minSearchCharacters = 3;

	/**
	 * SearchControl
	 */
	public SearchControl() {
		table = new FlexTable();
		table.setCellPadding(2);
		table.setCellSpacing(2);
		scrollPanel = new ScrollPanel(table);
		advancedView = new CheckBox(Main.i18n("search.view.advanced"));
		advancedView.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (advancedView.getValue()) {
					clean();
					switchSearchMode(SEARCH_MODE_ADVANCED);
				} else {
					clean();
					switchSearchMode(SEARCH_MODE_SIMPLE);
				}
			}
		});
		compactResultsView = new CheckBox(Main.i18n("search.view.compact.results"));
		compactResultsView.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (compactResultsView.getValue()) {
					switchResultsViewMode(RESULTS_VIEW_COMPACT);
					table.getCellFormatter().setVisible(2, 0, false); // hide view property groups
				} else {
					switchResultsViewMode(RESULTS_VIEW_NORMAL);
					table.getCellFormatter().setVisible(2, 0, true);  // show view property groups
				}
			}
		});
		showPropertyGroups = new CheckBox(Main.i18n("search.view.property.groups"));
		showPropertyGroups.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (searchButton.isEnabled()) {
					executeSearch();
				}
			}
		});
		saveUserNews = new CheckBox(Main.i18n("search.save.as.news"));
		searchSavedName = new TextBox();
		searchSavedName.setWidth("200px");
		controlSearch = new ControlSearchIn();
		resultPage = new ListBox();
		resultPage.addItem("10", "10");
		resultPage.addItem("20", "20");
		resultPage.addItem("30", "30");
		resultPage.addItem("50", "50");
		resultPage.addItem("100", "100");

		keyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				evaluateSearchButtonVisible();

				if (KeyCodes.KEY_ENTER == event.getNativeKeyCode() && searchButton.isEnabled()) {
					executeSearch();
				}
			}
		};

		searchButton = new Button(Main.i18n("button.search"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				executeSearch();
			}
		});

		cleanButton = new Button(Main.i18n("button.clean"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				clean();
			}
		});

		saveSearchButton = new Button(Main.i18n("button.save.search"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				long domain = 0;
				SearchNormal searchNormal = Main.get().mainPanel.search.searchBrowser.searchIn.searchNormal;
				SearchAdvanced searchAdvanced = Main.get().mainPanel.search.searchBrowser.searchIn.searchAdvanced;
				String operator = GWTQueryParams.OPERATOR_AND;
				params = new GWTQueryParams();

				if (!searchAdvanced.path.getText().equals("")) {
					params.setPath(searchAdvanced.path.getText());
				} else {
					params.setPath(searchNormal.context.getValue(searchNormal.context.getSelectedIndex()));
				}

				if (!searchAdvanced.categoryUuid.equals("")) {
					params.setCategoryUuid(searchAdvanced.categoryUuid);
					params.setCategoryPath(searchAdvanced.categoryPath.getText().substring(16)); // removes /okm:category 
				}

				params.setContent(searchNormal.content.getText());
				params.setName(searchNormal.name.getText());
				params.setKeywords(searchNormal.keywords.getText());
				params.setProperties(Main.get().mainPanel.search.searchBrowser.searchIn.getProperties());
				params.setAuthor(searchNormal.userListBox.getValue(searchNormal.userListBox.getSelectedIndex()));
				params.setLastModifiedFrom(searchNormal.modifyDateFrom);
				params.setLastModifiedTo(searchNormal.modifyDateTo);
				params.setDashboard(saveUserNews.getValue());
				params.setMailFrom(searchAdvanced.from.getText());
				params.setMailTo(searchAdvanced.to.getText());
				params.setMailSubject(searchAdvanced.subject.getText());

				if (searchAdvanced.typeDocument.getValue()) {
					domain += GWTQueryParams.DOCUMENT;
				}

				if (searchAdvanced.typeFolder.getValue()) {
					domain += GWTQueryParams.FOLDER;
				}

				if (searchAdvanced.typeMail.getValue()) {
					domain += GWTQueryParams.MAIL;
				}

				params.setDomain(domain);

				if (searchTypeAnd.getValue()) {
					operator = GWTQueryParams.OPERATOR_AND;
				} else {
					operator = GWTQueryParams.OPERATOR_OR;
				}

				params.setOperator(operator);

				// Removes dates if dashboard is checked
				if (saveUserNews.getValue()) {
					params.setLastModifiedFrom(null);
					params.setLastModifiedTo(null);
				}

				params.setMimeType(searchAdvanced.mimeTypes.getValue(searchAdvanced.mimeTypes.getSelectedIndex()));

				if (!searchSavedName.getText().equals("")) {
					saveSearchButton.setEnabled(false);
					params.setQueryName(searchSavedName.getText());
					isUserNews = params.isDashboard();
					saveSearch(params, "sql");
				}
			}
		});

		searchSavedName.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				evalueSaveSearchButtonVisible();
			}
		});

		searchButton.setEnabled(false);
		saveSearchButton.setEnabled(false);

		// Type of search
		searchTypePanel = new HorizontalPanel();
		searchTypePanel.setVisible(true);  // On OpenKM 4.0 has hidden AND / OR option list
		searchTypeAnd = new CheckBox("AND");
		searchTypeOr = new CheckBox("OR");
		searchTypeAnd.setValue(true);
		searchTypeOr.setValue(false);

		searchTypeAnd.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				searchTypeOr.setValue(!searchTypeAnd.getValue()); // Always set changed between and and or type
			}
		});

		searchTypeOr.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				searchTypeAnd.setValue(!searchTypeOr.getValue()); // Always set changed between and and or type
			}
		});

		HTML space1 = new HTML("");
		searchTypePanel.add(searchTypeAnd);
		searchTypePanel.add(space1);
		searchTypePanel.add(searchTypeOr);
		searchTypePanel.setCellWidth(space1, "10px");

		table.setWidget(0, 0, advancedView);
		table.setWidget(1, 0, compactResultsView);
		table.setWidget(2, 0, showPropertyGroups);
		table.setWidget(3, 0, saveUserNews);
		table.setWidget(4, 0, saveSearchButton);
		table.setWidget(4, 1, searchSavedName);

		resultsPageText = new HTML(Main.i18n("search.page.results"));
		table.setWidget(5, 0, resultsPageText);
		table.setWidget(5, 1, resultPage);

		searchTypeText = new HTML(Main.i18n("search.type"));
		table.setHTML(6, 0, Main.i18n("search.type"));
		table.setWidget(6, 1, searchTypePanel);

		table.setWidget(6, 0, cleanButton);
		table.setWidget(6, 1, searchButton);

		table.setWidget(7, 0, controlSearch);

		table.getCellFormatter().setHorizontalAlignment(4, 0, HasAlignment.ALIGN_RIGHT);
		table.getCellFormatter().setHorizontalAlignment(5, 0, HasAlignment.ALIGN_RIGHT);
		table.getCellFormatter().setHorizontalAlignment(6, 0, HasAlignment.ALIGN_RIGHT);
		table.getFlexCellFormatter().setColSpan(0, 0, 2);
		table.getFlexCellFormatter().setColSpan(1, 0, 2);
		table.getFlexCellFormatter().setColSpan(2, 0, 2);
		table.getFlexCellFormatter().setColSpan(3, 0, 2);
		table.getFlexCellFormatter().setColSpan(7, 0, 2);

		// By default is enabled search mode simple
		table.getCellFormatter().setVisible(3, 0, false);
		table.getCellFormatter().setVisible(4, 0, false);
		table.getCellFormatter().setVisible(4, 1, false);

		searchButton.setStyleName("okm-SearchButton");
		saveSearchButton.setStyleName("okm-SaveButton");
		saveSearchButton.addStyleName("okm-NoWrap");
		cleanButton.setStyleName("okm-CleanButton");
		searchSavedName.setStyleName("okm-Input");
		resultPage.setStyleName("okm-Input");

		initWidget(scrollPanel);
	}

	/**
	 * Executes the search
	 */
	public void executeSearch() {
		switch (searchMode) {
			case SEARCH_MODE_SIMPLE:
				SearchSimple searchSimple = Main.get().mainPanel.search.searchBrowser.searchIn.searchSimple;
				Main.get().mainPanel.search.searchBrowser.searchIn.futuramaWalking.evaluate(searchSimple.fullText.getText());
				controlSearch.executeSearch(searchSimple.fullText.getText(), Integer.parseInt(resultPage.getItemText(resultPage.getSelectedIndex())));
				break;

			case SEARCH_MODE_ADVANCED:
				long domain = 0;
				SearchNormal searchNormal = Main.get().mainPanel.search.searchBrowser.searchIn.searchNormal;
				SearchAdvanced searchAdvanced = Main.get().mainPanel.search.searchBrowser.searchIn.searchAdvanced;
				GWTQueryParams gwtParams = new GWTQueryParams();
				gwtParams.setContent(searchNormal.content.getText());

				if (!searchAdvanced.path.getText().equals("")) {
					gwtParams.setPath(searchAdvanced.path.getText());
				} else {
					gwtParams.setPath(searchNormal.context.getValue(searchNormal.context.getSelectedIndex()));
				}

				if (!searchAdvanced.categoryUuid.equals("")) {
					gwtParams.setCategoryUuid(searchAdvanced.categoryUuid);
				}

				gwtParams.setKeywords(searchNormal.keywords.getText());
				gwtParams.setMimeType("");
				gwtParams.setName(searchNormal.name.getText());
				gwtParams.setAuthor(searchNormal.userListBox.getValue(searchNormal.userListBox.getSelectedIndex()));

				gwtParams.setMailFrom(searchAdvanced.from.getText());
				gwtParams.setMailTo(searchAdvanced.to.getText());
				gwtParams.setMailSubject(searchAdvanced.subject.getText());

				if (searchTypeAnd.getValue()) {
					gwtParams.setOperator(GWTQueryParams.OPERATOR_AND);
				} else {
					gwtParams.setOperator(GWTQueryParams.OPERATOR_OR);
				}

				if (searchNormal.modifyDateFrom != null && searchNormal.modifyDateTo != null) {
					gwtParams.setLastModifiedFrom(searchNormal.modifyDateFrom);
					gwtParams.setLastModifiedTo(searchNormal.modifyDateTo);
				} else {
					gwtParams.setLastModifiedFrom(null);
					gwtParams.setLastModifiedTo(null);
				}

				if (searchAdvanced.typeDocument.getValue()) {
					domain += GWTQueryParams.DOCUMENT;
				}

				if (searchAdvanced.typeFolder.getValue()) {
					domain += GWTQueryParams.FOLDER;
				}

				if (searchAdvanced.typeMail.getValue()) {
					domain += GWTQueryParams.MAIL;
				}

				gwtParams.setDomain(domain);
				gwtParams.setProperties(Main.get().mainPanel.search.searchBrowser.searchIn.getProperties());
				gwtParams.setMimeType(searchAdvanced.mimeTypes.getValue(searchAdvanced.mimeTypes.getSelectedIndex()));
				Main.get().mainPanel.search.searchBrowser.searchIn.futuramaWalking.evaluate(searchNormal.content.getText());
				controlSearch.executeSearch(gwtParams, Integer.parseInt(resultPage.getItemText(resultPage.getSelectedIndex())));
				break;
		}
	}

	/**
	 * Evalues seach button visibility
	 */
	public void evaluateSearchButtonVisible() {
		switch (searchMode) {
			case SEARCH_MODE_SIMPLE:
				SearchSimple searchSimple = Main.get().mainPanel.search.searchBrowser.searchIn.searchSimple;
				if (searchSimple.fullText.getText().length() >= minSearchCharacters) {
					searchButton.setEnabled(true);
				} else {
					searchButton.setEnabled(false);
				}
				break;

			case SEARCH_MODE_ADVANCED:
				SearchNormal searchNormal = Main.get().mainPanel.search.searchBrowser.searchIn.searchNormal;
				SearchAdvanced searchAdvanced = Main.get().mainPanel.search.searchBrowser.searchIn.searchAdvanced;
				SearchMetadata searchMetadata = Main.get().mainPanel.search.searchBrowser.searchIn.searchMetadata;

				if (searchNormal.content.getText().length() >= minSearchCharacters || searchNormal.name.getText().length() >= minSearchCharacters ||
						searchNormal.keywords.getText().length() >= minSearchCharacters || searchAdvanced.from.getText().length() >= minSearchCharacters ||
						searchAdvanced.to.getText().length() >= minSearchCharacters || searchAdvanced.subject.getText().length() >= minSearchCharacters) {
					searchButton.setEnabled(true);
				} else {
					searchButton.setEnabled(false);
				}

				// Evaluates Mime Types
				if (searchAdvanced.mimeTypes.getSelectedIndex() > 0) {
					searchButton.setEnabled(true);
				}

				// Evaluates user list
				if (searchNormal.userListBox.getSelectedIndex() > 0) {
					searchButton.setEnabled(true);
				}

				// Evaluates date range
				if (searchNormal.modifyDateFrom != null && searchNormal.modifyDateTo != null) {
					searchButton.setEnabled(true);
				}

				// Evaluates properties to enable button
				for (GWTFormElement formElement : searchMetadata.updateFormElementsValuesWithNewer()) {
					if (formElement instanceof GWTInput) {
						if (((GWTInput) formElement).getValue().length() >= minSearchCharacters) {
							searchButton.setEnabled(true);
							break;
						}
					} else if (formElement instanceof GWTTextArea) {
						if (((GWTTextArea) formElement).getValue().length() >= minSearchCharacters) {
							searchButton.setEnabled(true);
							break;
						}
					} else if (formElement instanceof GWTSuggestBox) {
						if (!((GWTSuggestBox) formElement).getValue().equals("")) {
							searchButton.setEnabled(true);
							break;
						}
					} else if (formElement instanceof GWTCheckBox) {
						// Checkbox case assume is selected to enable search
						if (((GWTCheckBox) formElement).getValue()) {
							searchButton.setEnabled(true);
							break;
						}
					} else if (formElement instanceof GWTSelect) {
						// Checkbox case assume is selected to enable search
						GWTSelect select = (GWTSelect) formElement;
						for (GWTOption option : select.getOptions()) {
							if (option.isSelected()) {
								searchButton.setEnabled(true);
								break;
							}
						}
					}
				}
				break;
		}

		// After evaluating search button, must evaluate save search too
		evalueSaveSearchButtonVisible();
	}

	/**
	 * Evalues Save Search button visibility
	 */
	public void evalueSaveSearchButtonVisible() {
		switch (searchMode) {
			case SEARCH_MODE_SIMPLE:
				saveSearchButton.setEnabled(false);
				break;

			case SEARCH_MODE_ADVANCED:
				if (searchSavedName.getText().length() > 0 && searchButton.isEnabled()) {
					saveSearchButton.setEnabled(true);
				} else {
					saveSearchButton.setEnabled(false);
				}
				break;
		}
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		searchButton.setHTML(Main.i18n("button.search"));
		cleanButton.setHTML(Main.i18n("button.clean"));
		saveSearchButton.setHTML(Main.i18n("button.save.search"));
		advancedView.setText(Main.i18n("search.view.advanced"));
		compactResultsView.setText(Main.i18n("search.view.compact.results"));
		showPropertyGroups.setText(Main.i18n("search.view.property.groups"));
		saveUserNews.setText(Main.i18n("search.save.as.news"));
		resultsPageText.setHTML(Main.i18n("search.page.results"));
		searchTypeText.setHTML(Main.i18n("search.type"));
		controlSearch.langRefresh();
	}

	/**
	 * Save a search
	 */
	public void saveSearch(GWTQueryParams params, String type) {
		Main.get().mainPanel.search.searchBrowser.searchIn.status.setFlag_saveSearch();
		searchService.saveSearch(params, type, callbackSaveSearch);
	}

	/**
	 * Call Back save search 
	 */
	final AsyncCallback<Long> callbackSaveSearch = new AsyncCallback<Long>() {
		public void onSuccess(Long result) {
			params.setId(result.intValue());

			if (isUserNews) {
				Main.get().mainPanel.search.historySearch.userNews.addNewSavedSearch(params.clone());
				Main.get().mainPanel.search.historySearch.stackPanel.showWidget(UISearchConstants.SEARCH_USER_NEWS);
				Main.get().mainPanel.dashboard.newsDashboard.getUserSearchs(true);
			} else {
				Main.get().mainPanel.search.historySearch.searchSaved.addNewSavedSearch(params.clone());
				Main.get().mainPanel.search.historySearch.stackPanel.showWidget(UISearchConstants.SEARCH_SAVED);
			}

			searchSavedName.setText(""); // Clean name atfer saved
			Main.get().mainPanel.search.searchBrowser.searchIn.status.unsetFlag_saveSearch();
		}

		public void onFailure(Throwable caught) {
			Main.get().mainPanel.search.searchBrowser.searchIn.status.unsetFlag_saveSearch();
			Main.get().showError("SaveSearch", caught);
		}
	};

	/**
	 * switchSearchMode
	 *
	 * @param mode
	 */
	public void switchSearchMode(int mode) {
		searchMode = mode;
		switch (searchMode) {
			case SEARCH_MODE_SIMPLE:
				table.getCellFormatter().setVisible(3, 0, false);
				table.getCellFormatter().setVisible(4, 0, false);
				table.getCellFormatter().setVisible(4, 1, false);
				break;

			case SEARCH_MODE_ADVANCED:
				advancedView.setValue(true); // switch search mode can be done by execute saved query
				table.getCellFormatter().setVisible(3, 0, true);
				table.getCellFormatter().setVisible(4, 0, true);
				table.getCellFormatter().setVisible(4, 1, true);
				break;
		}
		Main.get().mainPanel.search.searchBrowser.searchIn.switchSearchMode(searchMode);
	}

	/**
	 * switchResultsViewMode
	 *
	 * @param mode
	 */
	private void switchResultsViewMode(int mode) {
		resultsViewMode = mode;
		Main.get().mainPanel.search.searchBrowser.searchResult.switchResultsViewMode(resultsViewMode);
	}

	/**
	 * clean
	 */
	private void clean() {
		SearchSimple searchSimple = Main.get().mainPanel.search.searchBrowser.searchIn.searchSimple;
		SearchNormal searchNormal = Main.get().mainPanel.search.searchBrowser.searchIn.searchNormal;
		SearchAdvanced searchAdvanced = Main.get().mainPanel.search.searchBrowser.searchIn.searchAdvanced;
		final SearchMetadata searchMetada = Main.get().mainPanel.search.searchBrowser.searchIn.searchMetadata;
		searchSimple.fullText.setText("");
		searchNormal.context.setSelectedIndex(Main.get().mainPanel.search.searchBrowser.searchIn.posTaxonomy);
		searchNormal.content.setText("");
		searchAdvanced.path.setText("");
		searchAdvanced.categoryPath.setText("");
		searchAdvanced.categoryUuid = "";
		searchNormal.name.setText("");
		searchNormal.keywords.setText("");
		searchSavedName.setText("");
		searchButton.setEnabled(false);
		saveSearchButton.setEnabled(false);
		controlSearch.setVisible(false);
		Main.get().mainPanel.search.searchBrowser.searchIn.resetMetadata();
		searchAdvanced.typeDocument.setValue(true);
		searchAdvanced.typeFolder.setValue(false);
		searchAdvanced.typeMail.setValue(false);
		searchAdvanced.mimeTypes.setSelectedIndex(0);
		searchMetada.addGroup.setEnabled(false);
		propertyGroupService.getAllGroups(new AsyncCallback<List<GWTPropertyGroup>>() {
			@Override
			public void onSuccess(List<GWTPropertyGroup> result) {
				searchMetada.addGroup.setEnabled(result.size() > 0);
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("getAllGroups", caught);
			}
		});
		searchNormal.userListBox.setSelectedIndex(0);
		searchNormal.startDate.setText("");
		searchNormal.endDate.setText("");
		searchNormal.modifyDateFrom = null;
		searchNormal.modifyDateTo = null;
		searchAdvanced.from.setText("");
		searchAdvanced.to.setText("");
		searchAdvanced.subject.setText("");
		Main.get().mainPanel.search.searchBrowser.searchResult.removeAllRows();
	}

	/**
	 * getSearchMode
	 *
	 * @return
	 */
	public int getSearchMode() {
		return searchMode;
	}

	/**
	 * setMinSearchCharacters
	 *
	 * @param minSearchCharacters
	 */
	public void setMinSearchCharacters(int minSearchCharacters) {
		this.minSearchCharacters = minSearchCharacters;
	}
}