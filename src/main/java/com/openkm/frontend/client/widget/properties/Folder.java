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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.openkm.extension.frontend.client.widget.messaging.MessagingToolBarBox;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.bean.GWTUser;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.Clipboard;
import com.openkm.frontend.client.widget.properties.CategoryManager.CategoryToRemove;
import com.openkm.frontend.client.widget.properties.KeywordManager.KeywordToRemove;
import com.openkm.frontend.client.widget.thesaurus.ThesaurusSelectPopup;

import java.util.Collection;

/**
 * Folder
 *
 * @author jllort
 */
public class Folder extends Composite {
	private ScrollPanel scrollPanel;
	private FlexTable tableProperties;
	private FlexTable tableSubscribedUsers;
	private FlexTable table;
	private GWTFolder folder;
	private HorizontalPanel hPanelSubscribedUsers;
	private HTML subcribedUsersText;
	private Image proposeSubscribeImage;
	private CategoryManager categoryManager;
	private KeywordManager keywordManager;
	private boolean remove = true;

	/**
	 * The folder
	 */
	public Folder() {
		folder = new GWTFolder();
		categoryManager = new CategoryManager(CategoryManager.ORIGIN_FOLDER);
		keywordManager = new KeywordManager(ThesaurusSelectPopup.FOLDER_PROPERTIES);
		table = new FlexTable();
		tableProperties = new FlexTable();
		tableSubscribedUsers = new FlexTable();
		scrollPanel = new ScrollPanel(table);

		tableProperties.setHTML(0, 0, "<b>" + Main.i18n("folder.uuid") + "</b>");
		tableProperties.setHTML(0, 1, "");
		tableProperties.setHTML(1, 0, "<b>" + Main.i18n("folder.name") + "</b>");
		tableProperties.setHTML(1, 1, "");
		tableProperties.setHTML(2, 0, "<b>" + Main.i18n("folder.parent") + "</b>");
		tableProperties.setHTML(2, 1, "");
		tableProperties.setHTML(3, 0, "<b>" + Main.i18n("folder.created") + "</b>");
		tableProperties.setHTML(3, 1, "");
		tableProperties.setHTML(4, 0, "<b>" + Main.i18n("folder.subscribed") + "</b>");
		tableProperties.setHTML(4, 1, "");
		tableProperties.setHTML(5, 0, "<b>" + Main.i18n("folder.number.folders") + "</b>");
		tableProperties.setHTML(5, 1, "");
		tableProperties.setHTML(6, 0, "<b>" + Main.i18n("folder.number.documents") + "</b>");
		tableProperties.setHTML(6, 1, "");
		tableProperties.setHTML(7, 0, "<b>" + Main.i18n("folder.number.mails") + "</b>");
		tableProperties.setHTML(7, 1, "");
		tableProperties.setHTML(8, 0, "<b>" + Main.i18n("folder.keywords") + "</b>");
		tableProperties.setHTML(8, 1, "");
		tableProperties.setHTML(9, 0, "<b>" + Main.i18n("folder.url") + "</b>");
		tableProperties.setWidget(9, 1, new HTML(""));
		tableProperties.setHTML(10, 0, "<b>" + Main.i18n("folder.webdav") + "</b>");
		tableProperties.setWidget(10, 1, new HTML(""));

		tableProperties.getCellFormatter().setVerticalAlignment(8, 0, HasAlignment.ALIGN_TOP);

		// Sets the tagcloud
		keywordManager.getKeywordCloud().setWidth("350px");

		VerticalPanel vPanel2 = new VerticalPanel();

		hPanelSubscribedUsers = new HorizontalPanel();
		subcribedUsersText = new HTML("<b>" + Main.i18n("folder.subscribed.users") + "<b>");
		proposeSubscribeImage = new Image(OKMBundleResources.INSTANCE.proposeSubscription());
		proposeSubscribeImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MessagingToolBarBox.get().executeProposeSubscription(folder.getUuid(), GWTFolder.TYPE);
			}
		});

		hPanelSubscribedUsers.add(subcribedUsersText);
		hPanelSubscribedUsers.add(new HTML("&nbsp;"));
		hPanelSubscribedUsers.setCellVerticalAlignment(subcribedUsersText, HasAlignment.ALIGN_MIDDLE);

		vPanel2.add(hPanelSubscribedUsers);
		vPanel2.add(tableSubscribedUsers);
		HTML space2 = new HTML("");
		vPanel2.add(space2);
		vPanel2.add(keywordManager.getKeywordCloudText());
		vPanel2.add(keywordManager.getKeywordCloud());
		HTML space3 = new HTML("");
		vPanel2.add(space3);
		vPanel2.add(categoryManager.getPanelCategories());
		vPanel2.add(categoryManager.getSubscribedCategoriesTable());

		vPanel2.setCellHeight(space2, "10px");
		vPanel2.setCellHeight(space3, "10px");

		table.setWidget(0, 0, tableProperties);
		table.setHTML(0, 1, "");
		table.setWidget(0, 2, vPanel2);

		// The hidden column extends table to 100% width
		CellFormatter cellFormatter = table.getCellFormatter();
		cellFormatter.setWidth(0, 1, "25px");
		cellFormatter.setVerticalAlignment(0, 0, HasAlignment.ALIGN_TOP);
		cellFormatter.setVerticalAlignment(0, 2, HasAlignment.ALIGN_TOP);

		// Sets wordWrap for al rows
		for (int i = 0; i < 11; i++) {
			setRowWordWarp(i, 0, true, tableProperties);
		}

		setRowWordWarp(0, 0, true, tableSubscribedUsers);

		tableProperties.setStyleName("okm-DisableSelect");
		tableSubscribedUsers.setStyleName("okm-DisableSelect");
		proposeSubscribeImage.addStyleName("okm-Hyperlink");

		initWidget(scrollPanel);
	}

	/**
	 * Set the WordWarp for all the row cells
	 *
	 * @param row     The row cell
	 * @param columns Number of row columns
	 * @param warp
	 * @param table   The table to change word wrap
	 */
	private void setRowWordWarp(int row, int columns, boolean warp, FlexTable table) {
		CellFormatter cellFormatter = table.getCellFormatter();

		for (int i = 0; i < columns; i++) {
			cellFormatter.setWordWrap(row, i, false);
		}
	}

	/**
	 *
	 */
	public GWTFolder get() {
		return folder;
	}

	/**
	 * Sets the folder values
	 *
	 * @param folder The folder object
	 */
	public void set(GWTFolder fld) {
		this.folder = fld;

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(new HTML(folder.getUuid()));
		hPanel.add(Util.hSpace("3px"));
		hPanel.add(new Clipboard(folder.getUuid()));

		tableProperties.setWidget(0, 1, hPanel);
		tableProperties.setHTML(1, 1, folder.getName());
		tableProperties.setHTML(2, 1, folder.getParentPath());
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		tableProperties.setHTML(3, 1, dtf.format(folder.getCreated()) + " " + Main.i18n("folder.by") + " " + folder.getUser().getUsername());

		if (folder.isSubscribed()) {
			tableProperties.setHTML(4, 1, Main.i18n("folder.subscribed.yes"));
		} else {
			tableProperties.setHTML(4, 1, Main.i18n("folder.subscribed.no"));
		}

		tableProperties.setWidget(8, 1, keywordManager.getKeywordPanel());

		// Enable select
		tableProperties.getFlexCellFormatter().setStyleName(0, 1, "okm-EnableSelect");
		tableProperties.getFlexCellFormatter().setStyleName(1, 1, "okm-EnableSelect");
		tableProperties.getFlexCellFormatter().setStyleName(2, 1, "okm-EnableSelect");

		remove = ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE);

		// Enables or disables change keywords with user permissions and document is not check-out or locked
		if (remove) {
			keywordManager.setVisible(true);
			categoryManager.setVisible(true);
		} else {
			keywordManager.setVisible(false);
			categoryManager.setVisible(false);
		}

		// Case categories, metadata or thesausus view is enabled file browser panel must be selected to have keywords and
		// categories tab panel enabled. Never should be asigned to categories, metadata or thesaurus folders.
		if ((Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_CATEGORIES ||
				Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_METADATA ||
				Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_THESAURUS)
				&& !Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
			keywordManager.setVisible(false);
			categoryManager.setVisible(false);
		}

		// Sets wordWrap for all rows
		for (int i = 0; i < 9; i++) {
			setRowWordWarp(i, 1, true, tableProperties);
		}

		// Remove all table rows >= 0
		tableSubscribedUsers.removeAllRows();

		// Sets the folder subscribers
		for (GWTUser subscriptor : folder.getSubscriptors()) {
			tableSubscribedUsers.setHTML(tableSubscribedUsers.getRowCount(), 0, subscriptor.getUsername());
			setRowWordWarp(tableSubscribedUsers.getRowCount() - 1, 0, true, tableSubscribedUsers);
		}

		// Some preoperties only must be visible on taxonomy or trash view
		int actualView = Main.get().mainPanel.desktop.navigator.getStackIndex();
		switch (actualView) {
			case UIDesktopConstants.NAVIGATOR_TAXONOMY:   // Some preperties only must be visible on taxonomy or trash view
			case UIDesktopConstants.NAVIGATOR_TRASH:
				tableSubscribedUsers.setVisible(true);
				tableProperties.getRowFormatter().setVisible(4, true); // Is user subscribed
				tableProperties.getRowFormatter().setVisible(5, true); // Number of folders
				tableProperties.getRowFormatter().setVisible(6, true); // Number of documents
				tableProperties.getRowFormatter().setVisible(7, true); // Number of e-mails
				break;

			case UIDesktopConstants.NAVIGATOR_THESAURUS:
			case UIDesktopConstants.NAVIGATOR_CATEGORIES:
				tableSubscribedUsers.setVisible(true);
				tableProperties.getRowFormatter().setVisible(4, false);
				tableProperties.getRowFormatter().setVisible(5, true);
				tableProperties.getRowFormatter().setVisible(6, true);
				tableProperties.getRowFormatter().setVisible(7, false);
				break;
			case UIDesktopConstants.NAVIGATOR_MAIL:
				tableSubscribedUsers.setVisible(false);
				tableProperties.getRowFormatter().setVisible(4, false);
				tableProperties.getRowFormatter().setVisible(5, true);
				tableProperties.getRowFormatter().setVisible(6, false);
				tableProperties.getRowFormatter().setVisible(7, false);
				break;

			case UIDesktopConstants.NAVIGATOR_PERSONAL:
				tableSubscribedUsers.setVisible(false); // Some data must not be visible on personal view
				tableProperties.getRowFormatter().setVisible(4, false);
				tableProperties.getRowFormatter().setVisible(5, true); // Number of folders
				tableProperties.getRowFormatter().setVisible(6, true); // Number of documents
				tableProperties.getRowFormatter().setVisible(7, true); // Number of e-mails
				break;
		}

		// Propose subscription must be disabled on trash
		if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TRASH) {
			proposeSubscribeImage.setVisible(false);
		} else {
			proposeSubscribeImage.setVisible(true);
		}

		// keywords
		keywordManager.reset();
		keywordManager.setObject(folder, remove);
		keywordManager.drawAll();

		// Categories
		categoryManager.removeAllRows();
		categoryManager.setObject(folder, remove);
		categoryManager.drawAll();

		// URL clipboard button
		String url = Main.get().workspaceUserProperties.getApplicationURL();
		url += "?uuid=" + URL.encodeQueryString(URL.encodeQueryString(folder.getUuid()));
		tableProperties.setWidget(9, 1, new Clipboard(url));

		// Webdav
		String webdavUrl = Main.get().workspaceUserProperties.getApplicationURL();
		String webdavPath = folder.getPath();

		// Replace only in case webdav fix is enabled
		if (Main.get().workspaceUserProperties.getWorkspace() != null && Main.get().workspaceUserProperties.getWorkspace().isWebdavFix()) {
			webdavPath = webdavPath.replace("okm:", "okm_");
		}

		// Login case write empty folder
		if (!webdavUrl.isEmpty()) {
			webdavPath = Util.encodePathElements(webdavPath);
			webdavUrl = webdavUrl.substring(0, webdavUrl.lastIndexOf('/')) + "/webdav" + webdavPath;
		}

		tableProperties.setWidget(10, 1, new Clipboard(webdavUrl));
	}

	/**
	 * resetNumericFolderValues
	 */
	public void resetNumericFolderValues() {
		tableProperties.setHTML(5, 1, "");
		tableProperties.setHTML(6, 1, "");
		tableProperties.setHTML(7, 1, "");
	}

	/**
	 * setNumberOfFolders
	 */
	public void setNumberOfFolders(int num) {
		tableProperties.setHTML(5, 1, "" + num);
	}

	/**
	 * setNumberOfDocuments
	 */
	public void setNumberOfDocuments(int num) {
		tableProperties.setHTML(6, 1, "" + num);
	}

	/**
	 * setNumberOfMails
	 */
	public void setNumberOfMails(int num) {
		tableProperties.setHTML(7, 1, "" + num);
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		tableProperties.setHTML(0, 0, "<b>" + Main.i18n("folder.uuid") + "</b>");
		tableProperties.setHTML(1, 0, "<b>" + Main.i18n("folder.name") + "</b>");
		tableProperties.setHTML(2, 0, "<b>" + Main.i18n("folder.parent") + "</b>");
		tableProperties.setHTML(3, 0, "<b>" + Main.i18n("folder.created") + "</b>");
		tableProperties.setHTML(4, 0, "<b>" + Main.i18n("folder.subscribed") + "</b>");
		tableProperties.setHTML(5, 0, "<b>" + Main.i18n("folder.number.folders") + "</b>");
		tableProperties.setHTML(6, 0, "<b>" + Main.i18n("folder.number.documents") + "</b>");
		tableProperties.setHTML(7, 0, "<b>" + Main.i18n("folder.number.mails") + "</b>");
		tableProperties.setHTML(8, 0, "<b>" + Main.i18n("folder.keywords") + "</b>");
		tableProperties.setHTML(9, 0, "<b>" + Main.i18n("folder.url") + "</b>");
		tableProperties.setHTML(10, 0, "<b>" + Main.i18n("folder.webdav") + "</b>");
		keywordManager.langRefresh();
        categoryManager.langRefresh();

		if (folder != null) {
			if (folder.isSubscribed()) {
				tableProperties.setHTML(4, 1, Main.i18n("folder.subscribed.yes"));
			} else {
				tableProperties.setHTML(4, 1, Main.i18n("folder.subscribed.no"));
			}
		}

		subcribedUsersText.setHTML("<b>" + Main.i18n("folder.subscribed.users") + "<b>");
	}

	/**
	 * showProposedSusbcription
	 */
	public void showProposedSusbcription() {
		// Adds to panel
		hPanelSubscribedUsers.add(proposeSubscribeImage);
	}

	/**
	 * addKeyword document
	 */
	public void addKeyword(String keyword) {
		keywordManager.addKeyword(keyword);
	}

	/**
	 * removeKeyword document
	 */
	public void removeKeyword(String keyword) {
		keywordManager.removeKeyword(keyword);
	}

	/**
	 * removeKeyword
	 *
	 * @param ktr
	 */
	public void removeKeyword(KeywordToRemove ktr) {
		keywordManager.removeKeyword(ktr);
	}

	/**
	 * addCategory document
	 */
	public void addCategory(GWTFolder category) {
		categoryManager.addCategory(category);
	}

	/**
	 * removeCategory document
	 */
	public void removeCategory(String UUID) {
		categoryManager.removeCategory(UUID);
	}

	/**
	 * removeCategory
	 *
	 * @param category
	 */
	public void removeCategory(CategoryToRemove obj) {
		categoryManager.removeCategory(obj);
	}

	/**
	 * Sets visibility to buttons ( true / false )
	 *
	 * @param visible The visible value
	 */
	public void setVisibleButtons(boolean visible) {
		keywordManager.setVisible(visible);
		categoryManager.setVisible(visible);
	}

	/**
	 * Removes a key
	 *
	 * @param keyword The key to be removed
	 */
	public void removeKey(String keyword) {
		keywordManager.removeKey(keyword);
	}

	/**
	 * addKeywordToPendinList
	 *
	 * @param key
	 */
	public void addKeywordToPendinList(String key) {
		keywordManager.addKeywordToPendinList(key);
	}

	/**
	 * Adds keywords sequentially
	 */
	public void addPendingKeyWordsList() {
		keywordManager.addPendingKeyWordsList();
	}

	/**
	 * getKeywords
	 *
	 * @return
	 */
	public Collection<String> getKeywords() {
		return folder.getKeywords();
	}

	/**
	 * @param enabled
	 */
	public void setKeywordEnabled(boolean enabled) {
		keywordManager.setKeywordEnabled(enabled);
	}

	/**
	 * showAddCategory
	 */
	public void showAddCategory() {
		categoryManager.showAddCategory();
	}

	/**
	 * showRemoveCategory
	 */
	public void showRemoveCategory() {
		categoryManager.showRemoveCategory();
	}

	/**
	 * showAddKeyword
	 */
	public void showAddKeyword() {
		keywordManager.showAddKeyword();
	}

	/**
	 * showRemoveKeyword
	 */
	public void showRemoveKeyword() {
		keywordManager.showRemoveKeyword();
	}
}