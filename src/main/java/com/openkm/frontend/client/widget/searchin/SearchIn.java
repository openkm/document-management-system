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

package com.openkm.frontend.client.widget.searchin;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTPropertyParams;
import com.openkm.frontend.client.bean.GWTQueryParams;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.eastereggs.FuturamaWalking;
import com.openkm.frontend.client.widget.searchsaved.Status;

import java.util.*;

/**
 * SearchIn
 *
 * @author jllort
 */
public class SearchIn extends Composite implements HasPropertyHandler {
	private static final int TAB_HEIGHT = 20;
	private static final int CONTROLER_WIDTH = 380;
	private static final int MINIMUM_TAB_WIDTH = 400;

	private HorizontalPanel hPanel;
	public TabLayoutPanel tabPanel;
	public SearchSimple searchSimple;
	public SearchNormal searchNormal;
	public SearchAdvanced searchAdvanced;
	public SearchMetadata searchMetadata;
	public SearchControl searchControl;
	public FuturamaWalking futuramaWalking;
	public Status status;
	public int posTaxonomy = 0;
	private int posTemplates = 0;
	private int posPersonal = 0;
	private int posMail = 0;
	private int posTrash = 0;
	private int searchMode = SearchControl.SEARCH_MODE_SIMPLE;
	private int height = 0;
	private int tabWidth = 0;
	private int controlWidth = 0;

	/**
	 * SearchIn
	 */
	public SearchIn() {
		futuramaWalking = new FuturamaWalking();
		hPanel = new HorizontalPanel();
		tabPanel = new TabLayoutPanel(TAB_HEIGHT, Unit.PX);
		searchSimple = new SearchSimple();
		searchNormal = new SearchNormal();
		searchAdvanced = new SearchAdvanced();
		searchMetadata = new SearchMetadata(this);
		searchControl = new SearchControl();
		status = new Status();
		status.setStyleName("okm-StatusPopup");

		// Adding keyword listeners
		searchSimple.fullText.addKeyUpHandler(searchControl.keyUpHandler);
		searchNormal.content.addKeyUpHandler(searchControl.keyUpHandler);
		searchNormal.name.addKeyUpHandler(searchControl.keyUpHandler);
		searchNormal.keywords.addKeyUpHandler(searchControl.keyUpHandler);
		searchAdvanced.from.addKeyUpHandler(searchControl.keyUpHandler);
		searchAdvanced.to.addKeyUpHandler(searchControl.keyUpHandler);
		searchAdvanced.subject.addKeyUpHandler(searchControl.keyUpHandler);

		// By default is enabled simple mode
		tabPanel.add(searchSimple, Main.i18n("search.simple"));
		tabPanel.selectTab(0);

		Image verticalLine = new Image("img/transparent_pixel.gif");
		verticalLine.setStyleName("okm-Vertical-Line-Border");
		verticalLine.setSize("2px", "100%");
		hPanel.add(tabPanel);
		hPanel.add(verticalLine);
		hPanel.add(searchControl);

		hPanel.setCellWidth(verticalLine, "2px");
		hPanel.setCellHeight(verticalLine, "100%");
		hPanel.setCellWidth(searchControl, "" + CONTROLER_WIDTH + "px");
		hPanel.setCellVerticalAlignment(tabPanel, HasAlignment.ALIGN_TOP);
		hPanel.setCellVerticalAlignment(searchControl, HasAlignment.ALIGN_TOP);

		// Gets all users
		searchNormal.getAllUsers();

		initWidget(hPanel);
	}

	/**
	 * setPixelSize
	 */
	public void setPixelSize(int width, int height) {
		super.setPixelSize(width, height);
		this.height = height;
		tabWidth = MINIMUM_TAB_WIDTH;
		controlWidth = CONTROLER_WIDTH;

		// Case width is too small the decision is distribute at 50%
		if (MINIMUM_TAB_WIDTH + CONTROLER_WIDTH > width) {
			if (width > 10) {
				tabWidth = width / 2;
				controlWidth = width - tabWidth;
			} else {
				// Minim values
				tabWidth = 10;
				controlWidth = 10;
			}
		} else if ((width - CONTROLER_WIDTH) > tabWidth) {
			tabWidth = width - CONTROLER_WIDTH; // Always trying expand tab panel
		}

		tabPanel.setWidth("" + (tabWidth - 2) + "px");
		tabPanel.setHeight("" + (height - 2) + "px");
		searchSimple.setPixelSize(tabWidth - 2, height - 22); // Substract tab height
		searchNormal.setPixelSize(tabWidth - 2, height - 22); // Substract tab height
		searchAdvanced.setPixelSize(tabWidth - 2, height - 22); // Substract tab height
		searchMetadata.setPixelSize(tabWidth - 2, height - 22); // Substract tab height
		searchControl.setPixelSize(controlWidth - 2, height - 2); // Substract tab height -2 pixels for vertical line too
	}

	/**
	 * Refreshing lang
	 */
	public void langRefresh() {
		searchNormal.langRefresh();
		searchAdvanced.langRefresh();
		searchMetadata.langRefresh();
		searchControl.langRefresh();
		int selectedTab = tabPanel.getSelectedIndex();
		switchSearchMode(searchMode);
		tabPanel.selectTab(selectedTab);
	}

	/**
	 * Gets the properties 
	 *
	 * @return The properties
	 */
	public Map<String, GWTPropertyParams> getProperties() {
		return searchMetadata.getUpdatedPropertyParamsWithValues();
	}

	/**
	 * Gets the actual form elements
	 *
	 * @return The actual form elements values
	 */
	public Collection<String> getFormElementsKeys() {
		List<String> keyList = new ArrayList<String>();
		for (GWTFormElement formElement : searchMetadata.getFormElements()) {
			keyList.add(formElement.getName());
		}
		return keyList;
	}

	@Override
	public void propertyRemoved() {
		searchControl.evaluateSearchButtonVisible();
		searchMetadata.groupPopup.enableAddGroupButton(); // Enables or disables button ( depends exist some item on list to be added )
	}

	@Override
	public void metadataValueChanged() {
		searchControl.evaluateSearchButtonVisible();
	}

	/**
	 * @param gWTParams
	 */
	public void setQuickSearch(String query) {
		searchControl.switchSearchMode(SearchControl.SEARCH_MODE_SIMPLE);
		searchSimple.fullText.setText(query);
		searchControl.evaluateSearchButtonVisible();
		searchControl.searchButton.setEnabled(true);
		searchControl.executeSearch();
	}

	/**
	 * Sets the saved search
	 *
	 * @param gWTParams The params
	 */
	public void setSavedSearch(GWTQueryParams gWTParams) {
		searchControl.switchSearchMode(SearchControl.SEARCH_MODE_ADVANCED);
		if (gWTParams.getPath().startsWith(Main.get().repositoryContext.getContextTaxonomy())) {
			searchNormal.context.setSelectedIndex(posTaxonomy);
		} else if (gWTParams.getPath().startsWith(Main.get().repositoryContext.getContextPersonal())) {
			searchNormal.context.setSelectedIndex(posTemplates);
		} else if (gWTParams.getPath().startsWith(Main.get().repositoryContext.getContextTemplates())) {
			searchNormal.context.setSelectedIndex(posPersonal);
		} else if (gWTParams.getPath().startsWith(Main.get().repositoryContext.getContextMail())) {
			searchNormal.context.setSelectedIndex(posMail);
		} else if (gWTParams.getPath().startsWith(Main.get().repositoryContext.getContextTrash())) {
			searchNormal.context.setSelectedIndex(posTrash);
		} else {
			searchNormal.context.setSelectedIndex(posTaxonomy);
		}

		if (!gWTParams.getCategoryUuid().equals("")) {
			searchAdvanced.categoryUuid = gWTParams.getCategoryUuid();
			searchAdvanced.categoryPath.setText(gWTParams.getCategoryPath());
		}

		// Detecting if user has setting some folder path filter or there's only a context one
		if (!gWTParams.getPath().equals(Main.get().repositoryContext.getContextTaxonomy()) &&
				!gWTParams.getPath().equals(Main.get().repositoryContext.getContextPersonal()) &&
				!gWTParams.getPath().equals(Main.get().repositoryContext.getContextTemplates()) &&
				!gWTParams.getPath().equals(Main.get().repositoryContext.getContextMail()) &&
				!gWTParams.getPath().equals(Main.get().repositoryContext.getContextTrash())) {
			searchAdvanced.path.setText(gWTParams.getPath());
		} else {
			searchAdvanced.path.setText("");
		}

		searchNormal.content.setText(gWTParams.getContent());
		searchNormal.name.setText(gWTParams.getName());
		searchNormal.keywords.setText(gWTParams.getKeywords());
		searchControl.saveUserNews.setValue(gWTParams.isDashboard());

		searchAdvanced.from.setText(gWTParams.getMailFrom());
		searchAdvanced.to.setText(gWTParams.getMailTo());
		searchAdvanced.subject.setText(gWTParams.getMailSubject());

		if (gWTParams.getOperator().equals(GWTQueryParams.OPERATOR_AND)) {
			searchControl.searchTypeAnd.setValue(true);
			searchControl.searchTypeOr.setValue(false);
		} else {
			searchControl.searchTypeAnd.setValue(false);
			searchControl.searchTypeOr.setValue(true);
		}

		// Document type
		if ((gWTParams.getDomain() & GWTQueryParams.DOCUMENT) != 0) {
			searchAdvanced.typeDocument.setValue(true);
		} else {
			searchAdvanced.typeDocument.setValue(false);
		}

		if ((gWTParams.getDomain() & GWTQueryParams.FOLDER) != 0) {
			searchAdvanced.typeFolder.setValue(true);
		} else {
			searchAdvanced.typeFolder.setValue(false);
		}

		if ((gWTParams.getDomain() & GWTQueryParams.MAIL) != 0) {
			searchAdvanced.typeMail.setValue(true);
			searchAdvanced.tableMail.setVisible(true);
		} else {
			searchAdvanced.typeMail.setValue(false);
			searchAdvanced.tableMail.setVisible(false);
		}

		searchAdvanced.mimeTypes.setSelectedIndex(0);

		// TODO: on api mime must not return null, this must be revised
		if (gWTParams.getMimeType() != null && !gWTParams.getMimeType().equals("")) {
			for (int i = 0; i < searchAdvanced.mimeTypes.getItemCount(); i++) {
				if (searchAdvanced.mimeTypes.getValue(i).equals(gWTParams.getMimeType())) {
					searchAdvanced.mimeTypes.setSelectedIndex(i);
				}
			}

		}

		searchNormal.userListBox.setSelectedIndex(0);

		if (gWTParams.getAuthor() != null && !gWTParams.getAuthor().equals("")) {
			for (int i = 0; i < searchNormal.userListBox.getItemCount(); i++) {
				if (searchNormal.userListBox.getValue(i).equals(gWTParams.getAuthor())) {
					searchNormal.userListBox.setSelectedIndex(i);
				}
			}
		}

		if (gWTParams.getLastModifiedFrom() != null) {
			searchNormal.modifyDateFrom = gWTParams.getLastModifiedFrom();
			DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.day.pattern"));
			searchNormal.startDate.setText(dtf.format(searchNormal.modifyDateFrom));
		} else {
			searchNormal.modifyDateFrom = null;
			searchNormal.startDate.setText("");
		}

		if (gWTParams.getLastModifiedTo() != null) {
			searchNormal.modifyDateTo = gWTParams.getLastModifiedTo();
			DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.day.pattern"));
			searchNormal.endDate.setText(dtf.format(searchNormal.modifyDateTo));
		} else {
			searchNormal.modifyDateTo = null;
			searchNormal.endDate.setText("");
		}

		resetMetadata();
		addPropertyParams(gWTParams.getProperties());
		searchControl.evaluateSearchButtonVisible();
		searchControl.searchButton.setEnabled(true);
		searchControl.executeSearch();
	}

	/**
	 * Remove all metadata
	 */
	public void resetMetadata() {
		searchMetadata.reset();
	}

	/**
	 * Add property params
	 *
	 * @param hproperties The table properties map
	 */
	private void addPropertyParams(Map<String, GWTPropertyParams> hproperties) {
		for (Iterator<String> it = hproperties.keySet().iterator(); it.hasNext(); ) {
			searchMetadata.addProperty((GWTPropertyParams) hproperties.get(it.next()));
		}
	}

	/**
	 * Sets the context values
	 *
	 * @param contextValue The context value
	 * @param stackView The stack view
	 */
	public void setContextValue(String contextValue, int stackView) {
		searchNormal.setContextValue(contextValue, stackView);
	}

	/**
	 * showTemplates
	 */
	public void showTemplates() {
		searchNormal.showTemplates();
	}

	/**
	 * showPersonal
	 */
	public void showPersonal() {
		searchNormal.showPersonal();
	}

	/**
	 * showMail
	 */
	public void showMail() {
		searchNormal.showMail();
	}

	/**
	 * showTrash
	 */
	public void showTrash() {
		searchNormal.showTrash();
	}

	/**
	 * getSelectedView
	 *
	 * @return
	 */
	public int getSelectedView() {
		int index = searchNormal.context.getSelectedIndex();
		if (index == posTaxonomy) {
			return UIDesktopConstants.NAVIGATOR_TAXONOMY;
		} else if (index == posTemplates) {
			return UIDesktopConstants.NAVIGATOR_TEMPLATES;
		} else if (index == posPersonal) {
			return UIDesktopConstants.NAVIGATOR_PERSONAL;
		} else if (index == posMail) {
			return UIDesktopConstants.NAVIGATOR_MAIL;
		} else {
			return UIDesktopConstants.NAVIGATOR_TRASH;
		}
	}

	/**
	 * switchSearchMode
	 *
	 * @param mode
	 */
	public void switchSearchMode(int mode) {
		this.searchMode = mode;
		switch (searchMode) {
			case SearchControl.SEARCH_MODE_SIMPLE:
				while (tabPanel.getWidgetCount() > 0) {
					tabPanel.remove(0);
				}

				tabPanel.add(searchSimple, Main.i18n("search.simple"));
				tabPanel.selectTab(0);
				break;

			case SearchControl.SEARCH_MODE_ADVANCED:
				while (tabPanel.getWidgetCount() > 0) {
					tabPanel.remove(0);
				}

				tabPanel.add(searchNormal, Main.i18n("search.normal"));
				tabPanel.add(searchAdvanced, Main.i18n("search.advanced"));
				tabPanel.add(searchMetadata, Main.i18n("search.metadata"));
				tabPanel.selectTab(0);
				break;
		}
		// TODO:Solves minor bug with IE ( now shows contents )
		if (Util.getUserAgent().startsWith("ie")) {
			tabPanel.setWidth("" + (tabWidth - 20) + "px");
			tabPanel.setHeight("" + (height - 20) + "px");
			searchSimple.setPixelSize(tabWidth - 20, height - 42); // Substract tab height
			searchNormal.setPixelSize(tabWidth - 20, height - 42); // Substract tab height
			searchAdvanced.setPixelSize(tabWidth - 20, height - 42); // Substract tab height
			searchMetadata.setPixelSize(tabWidth - 20, height - 42); // Substract tab height
			Timer timer = new Timer() {
				@Override
				public void run() {
					tabPanel.setWidth("" + (tabWidth - 2) + "px");
					tabPanel.setHeight("" + (height - 2) + "px");
					searchSimple.setPixelSize(tabWidth - 2, height - 22); // Substract tab height
					searchNormal.setPixelSize(tabWidth - 2, height - 22); // Substract tab height
					searchAdvanced.setPixelSize(tabWidth - 2, height - 22); // Substract tab height
					searchMetadata.setPixelSize(tabWidth - 2, height - 22); // Substract tab height
				}
			};
			timer.schedule(350);
		}
	}

	/**
	 * setLoadFinish
	 */
	public void setLoadFinish() {
		searchSimple.setLoadFinish();
	}
}
