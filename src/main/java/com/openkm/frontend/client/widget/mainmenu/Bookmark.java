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

package com.openkm.frontend.client.widget.mainmenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTBookmark;
import com.openkm.frontend.client.service.OKMBookmarkService;
import com.openkm.frontend.client.service.OKMBookmarkServiceAsync;
import com.openkm.frontend.client.service.OKMUserConfigService;
import com.openkm.frontend.client.service.OKMUserConfigServiceAsync;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.ConfirmPopup;
import com.openkm.frontend.client.widget.startup.StartUp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Bookmark on menu
 *
 * @author jllort
 *
 */
public class Bookmark {

	private final OKMBookmarkServiceAsync bookmarkService = (OKMBookmarkServiceAsync) GWT
			.create(OKMBookmarkService.class);
	private final OKMUserConfigServiceAsync userConfigService = (OKMUserConfigServiceAsync) GWT
			.create(OKMUserConfigService.class);

	public static final String BOOKMARK_DOCUMENT = "okm:document";
	public static final String BOOKMARK_FOLDER = "okm:folder";

	private List<MenuItem> bookmarks = new ArrayList<MenuItem>();
	private String uuid = "";
	private String nodePath = "";
	private boolean document = false;

	/**
	 * Bookmark
	 */
	public Bookmark() {
	}

	/**
	 * Inits on first load
	 */
	public void init() {
		getAll();
	}

	/**
	 * Callback get all
	 */
	final AsyncCallback<List<GWTBookmark>> callbackGetAll = new AsyncCallback<List<GWTBookmark>>() {
		public void onSuccess(List<GWTBookmark> result) {
			List<GWTBookmark> bookmarkList = result;
			MenuBar subMenuBookmark = Main.get().mainPanel.topPanel.mainMenu.subMenuBookmark;

			// Resets all bookmark menu
			resetMenu();
			bookmarks = new ArrayList<MenuItem>();

			for (Iterator<GWTBookmark> it = bookmarkList.iterator(); it.hasNext(); ) {
				final GWTBookmark bookmark = it.next();
				String icon = "";

				if (bookmark.getType().equals(BOOKMARK_DOCUMENT)) {
					icon = "img/icon/menu/document_bookmark.gif";
				} else if (bookmark.getType().equals(BOOKMARK_FOLDER)) {
					icon = "img/icon/menu/folder_bookmark.gif";
				}

				MenuItem tmpBookmark = new MenuItem(Util.menuHTML(icon, bookmark.getName()), true, new Command() {
					public void execute() {
						bookmarkService.get(bookmark.getId(), new AsyncCallback<GWTBookmark>() {
							@Override
							public void onSuccess(GWTBookmark result) {
								String validPath = result.getPath();
								if (result.getType().equals(BOOKMARK_DOCUMENT) && validPath != null
										&& !validPath.equals("")) {
									// Opens folder passed by parameter
									CommonUI.openPath(Util.getParent(validPath), validPath);

								} else if (bookmark.getType().equals(BOOKMARK_FOLDER) && validPath != null
										&& !validPath.equals("")) {
									// Opens document passed by parameter
									CommonUI.openPath(validPath, "");
								}
							}

							@Override
							public void onFailure(Throwable caught) {
								Main.get().showError("getBookmark", caught);
							}
						});
					}
				});

				tmpBookmark.addStyleName("okm-MainMenuItem");
				subMenuBookmark.addItem(tmpBookmark);
				bookmarks.add(tmpBookmark); // Save menuItem to list to refreshing management
			}

			Main.get().startUp.nextStatus(StartUp.STARTUP_INIT_TREE_NODES); // Sets the next status to loading
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getAll", caught);
		}
	};

	/**
	 * Callback add
	 */
	final AsyncCallback<GWTBookmark> callbackAdd = new AsyncCallback<GWTBookmark>() {
		public void onSuccess(GWTBookmark result) {

			MenuBar subMenuBookmark = Main.get().mainPanel.topPanel.mainMenu.subMenuBookmark;
			final GWTBookmark bookmark = result;

			String icon = "";

			if (bookmark.getType().equals(BOOKMARK_DOCUMENT)) {
				icon = "img/icon/menu/document_bookmark.gif";
			} else if (bookmark.getType().equals(BOOKMARK_FOLDER)) {
				icon = "img/icon/menu/folder_bookmark.gif";
			}

			MenuItem tmpBookmark = new MenuItem(Util.menuHTML(icon, bookmark.getName()), true, new Command() {
				public void execute() {
					bookmarkService.get(bookmark.getId(), new AsyncCallback<GWTBookmark>() {
						@Override
						public void onSuccess(GWTBookmark result) {
							String validPath = result.getPath();

							if (result.getType().equals(BOOKMARK_DOCUMENT) && validPath != null
									&& !validPath.equals("")) {
								// Opens folder passed by parameter
								CommonUI.openPath(Util.getParent(validPath), validPath);
							} else if (bookmark.getType().equals(BOOKMARK_FOLDER) && validPath != null
									&& !validPath.equals("")) {
								// Opens document passed by parameter
								CommonUI.openPath(validPath, "");
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							Main.get().showError("getBookmark", caught);
						}
					});
				}
			});

			tmpBookmark.addStyleName("okm-MainMenuItem");
			subMenuBookmark.addItem(tmpBookmark);
			bookmarks.add(tmpBookmark); // Save menuItem to list to refreshing management
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("add", caught);
		}
	};

	/**
	 * Callback set user home
	 */
	final AsyncCallback<Object> callbackSetUserHome = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			String user = Main.get().workspaceUserProperties.getUser().getId();

			if (document) {
				Main.get().mainPanel.topPanel.toolBar.setUserHome(user, uuid, nodePath, BOOKMARK_DOCUMENT);
			} else {
				Main.get().mainPanel.topPanel.toolBar.setUserHome(user, uuid, nodePath, BOOKMARK_FOLDER);
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("setUserHome", caught);
		}
	};

	/**
	 * Resets all widgets on menu
	 */
	private void resetMenu() {
		if (!bookmarks.isEmpty()) {
			MenuBar subMenuBookmark = Main.get().mainPanel.topPanel.mainMenu.subMenuBookmark;

			for (Iterator<MenuItem> it = bookmarks.iterator(); it.hasNext(); ) {
				subMenuBookmark.removeItem(it.next());
			}
		}
	}

	/**
	 * Gets the bookmark list from the server
	 *
	 */
	public void getAll() {
		bookmarkService.getAll(callbackGetAll);
	}

	/**
	 * Adds a bookmark
	 *
	 * @param nodePath String The node path
	 * @param name String The bookmark name
	 * @param document boolean is document
	 */
	public void add(String nodePath, String name) {
		bookmarkService.add(nodePath, name, callbackAdd);
	}

	/**
	 * Sets the user home
	 *
	 */
	public void setUserHome() {
		if (nodePath != null && !nodePath.equals("")) {
			userConfigService.setUserHome(nodePath, callbackSetUserHome);
		}
	}

	/**
	 * Show confirmation to set default home
	 *
	 * @param nodePath String The node path
	 * @param document boolean is document
	 */
	public void confirmSetHome(String uuid, String nodePath, boolean document) {
		this.uuid = uuid;
		this.nodePath = nodePath;
		this.document = document;
		Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_SET_DEFAULT_HOME);
		Main.get().confirmPopup.show();
	}
}