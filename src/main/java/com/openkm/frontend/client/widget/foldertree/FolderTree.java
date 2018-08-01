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

package com.openkm.frontend.client.widget.foldertree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TreeItem;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.service.*;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.ConfirmPopup;
import com.openkm.frontend.client.widget.MenuPopup;
import com.openkm.frontend.client.widget.OriginPanel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Folder tree
 *
 * @author jllort
 *
 */
public class FolderTree extends Composite implements OriginPanel {

	// Definitions of folder actions
	public static final int ACTION_NONE = -1;
	public static final int ACTION_CREATE = 0;
	public static final int ACTION_RENAME = 1;
	public static final int ACTION_SECURITY_REFRESH = 2;

	private final OKMFolderServiceAsync folderService = GWT.create(OKMFolderService.class);
	private final OKMNotifyServiceAsync notifyService = GWT.create(OKMNotifyService.class);
	private final OKMRepositoryServiceAsync repositoryService = GWT.create(OKMRepositoryService.class);

	private ExtendedTree tree;
	public TreeItem actualItem;
	public FolderSelectPopup folderSelectPopup;
	public MenuPopup menuPopup;
	private boolean panelSelected = true; // Indicates if panel is selected

	public TreeItem tmpFolder; // Used temporary to create new folder
	private FolderTextBox renFolder; // Used temporary to rename a new folder
	public GWTFolder folderRoot; // To preserve folder root value
	public TreeItem rootItem;
	ArrayList<String> tmpAllPathFolder = new ArrayList<String>(); // Store node path elements
	public int folderAction = ACTION_NONE; // To control rename and create

	// folder actions
	private TreeItem otherTreeItemSelected = null;
	private String tmpRenameHtmlFolder;
	private boolean refreshFileBrowser = false;
	private boolean setTabFolderAfterRefresh = false;
	private boolean treeItemChanged = false;
	private boolean treeItemHasBeenClosed = false;
	private boolean autoOpenFolder = false;
	private boolean openingFldPath = false;
	private boolean flagFileBrowserFinished = true;

	/**
	 * Directory Tree
	 */
	public FolderTree() {
		tree = new ExtendedTree();
		tmpFolder = new TreeItem();
		tmpFolder.setStyleName("okm-TreeItem");
		folderSelectPopup = new FolderSelectPopup();
		folderSelectPopup.setWidth("450px");
		folderSelectPopup.setHeight("440px");
		folderSelectPopup.setStyleName("okm-Popup");
		TreeItem rootItem = new TreeItem();
		rootItem.setHTML(Util.imageItemHTML("img/menuitem_childs.gif", "root_schema", "top"));
		rootItem.setStyleName("okm-TreeItem");
		rootItem.setUserObject(new GWTFolder());
		rootItem.setSelected(true);
		rootItem.setState(true);
		rootItem.addStyleName("okm-DisableSelect"); // Disables drag and drop
		// browser text selection
		tree.setStyleName("okm-Tree");
		tree.addStyleName("okm-PanelSelected");
		tree.addItem(rootItem);

		tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				if (!openingFldPath && flagFileBrowserFinished) {
					final TreeItem treeItem = event.getSelectedItem();
					Timer openIfNotClosed = new Timer() {
						@Override
						public void run() {
							if (!treeItemHasBeenClosed) {
								onTreeItemSelected(treeItem);
								// Case is closed parent which selected forder is the child
							} else if (treeItemHasBeenClosed && !actualItem.equals(treeItem) &&
									((GWTFolder) actualItem.getUserObject()).getPath().contains(((GWTFolder) treeItem.getUserObject()).getPath())) {
								actualItem.setSelected(false);
								treeItem.setSelected(true);
								onTreeItemSelected(treeItem);
							} else {
								treeItemHasBeenClosed = false;
							}
						}
					};
					if (treeItem != null) {
						openIfNotClosed.schedule(50);
					}
				}
			}
		});

		tree.addCloseHandler(new CloseHandler<TreeItem>() {
			@Override
			public void onClose(CloseEvent<TreeItem> event) {
				Main.get().draggable.clear();
				treeItemHasBeenClosed = true;
			}
		});
		tree.addOpenHandler(new OpenHandler<TreeItem>() {
			@Override
			public void onOpen(OpenEvent<TreeItem> event) {
				if (flagFileBrowserFinished) {
					TreeItem treeItem = event.getTarget();
					if (!openingFldPath && !autoOpenFolder) {
						if (!actualItem.equals(treeItem)) {
							actualItem.setSelected(false);
							treeItem.setSelected(true);
						}
						onTreeItemSelected(event.getTarget());
					} else {
						autoOpenFolder = false; // always reset value
					}
				}
			}
		});
		actualItem = tree.getItem(0);
		initWidget(tree);
	}

	/**
	 * Inits on first load
	 */
	public void init() {
	}

	/**
	 * Refresh language values
	 */
	public void langRefresh() {
		menuPopup.langRefresh();
		folderSelectPopup.langRefresh();
	}

	/**
	 * Refresh asyncronous subtree branch
	 */
	final AsyncCallback<List<GWTFolder>> callbackGetChilds = new AsyncCallback<List<GWTFolder>>() {
		@Override
		public void onSuccess(List<GWTFolder> result) {
			List<GWTFolder> folderList = result;

			autoOpenFolder = actualItem.getState(); // Actual folder open state
			// Case first refreshing when (childcount = 0 or treeItem changed ) and results >0 must ensure enables auto open
			if ((actualItem.getChildCount() == 0 || treeItemChanged) && result.size() > 0) {
				autoOpenFolder = true;
			}

			// Remove all items
			if (actualItem.getChildCount() > 0) {
				actualItem.removeItems();
			}

			// On refreshing not refreshed the actual item values but must
			// ensure that has childs value is consistent
			if (folderList.isEmpty()) {
				((GWTFolder) actualItem.getUserObject()).setHasChildren(false);
			} else {
				((GWTFolder) actualItem.getUserObject()).setHasChildren(true);
			}

			// Ads folders childs if exists
			for (Iterator<GWTFolder> it = folderList.iterator(); it.hasNext(); ) {
				GWTFolder folder = it.next();
				TreeItem folderItem = new TreeItem();
				folderItem.setHTML(folder.getName());
				folderItem.setUserObject(folder);
				folderItem.setStyleName("okm-TreeItem");

				// add values
				evaluesFolderIcon(folderItem);
				actualItem.addItem(folderItem);
			}
			// Evaluates special case has closed parent folder ( and selected folder was a child )
			if (!treeItemHasBeenClosed) {
				actualItem.setState(autoOpenFolder); // Enables auto open or close tree node
			} else {
				treeItemHasBeenClosed = false;
			}
			evaluesFolderIcon(actualItem);

			if (setTabFolderAfterRefresh) {
				// Refresh tab properties values on the last node
				showTabFolderProperties();
				setTabFolderAfterRefresh = false;
			}

			Main.get().mainPanel.desktop.navigator.status.unsetFlagChilds();
		}

		@Override
		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.navigator.status.unsetFlagChilds();
			Main.get().showError("GetChilds", caught);
		}
	};

	/**
	 * Refresh asyncronous subtree branch
	 */
	final AsyncCallback<List<GWTFolder>> callbackGetOnlyChilds = new AsyncCallback<List<GWTFolder>>() {
		@Override
		public void onSuccess(List<GWTFolder> result) {
			List<GWTFolder> folderList = result;

			// Remove olders items
			if (actualItem.getChildCount() > 0) {
				actualItem.removeItems();
			}

			// On refreshing not refreshed the actual item values but must
			// ensure that has childs value is consistent
			if (folderList.isEmpty()) {
				((GWTFolder) actualItem.getUserObject()).setHasChildren(false);
			} else {
				((GWTFolder) actualItem.getUserObject()).setHasChildren(true);
			}

			// Ads folders childs if exists
			for (Iterator<GWTFolder> it = folderList.iterator(); it.hasNext(); ) {
				GWTFolder folder = it.next();
				TreeItem folderItem = new TreeItem();
				folderItem.setHTML(folder.getName());
				folderItem.setUserObject(folder);
				folderItem.setStyleName("okm-TreeItem");

				// add values
				evaluesFolderIcon(folderItem);
				actualItem.addItem(folderItem);
			}

			actualItem.setState(true);
			evaluesFolderIcon(actualItem);

			if (!tmpAllPathFolder.isEmpty()) {
				int index = tmpAllPathFolder.size() - 1;
				String tmpPath = tmpAllPathFolder.get(index);
				tmpAllPathFolder.remove(index);
				if (index > 0) {
					setActiveNode(tmpPath, false, false);
				} else {
					setActiveNode(tmpPath, false, refreshFileBrowser);
				}
			} else {
				openingFldPath = false;
			}
		}

		@Override
		public void onFailure(Throwable caught) {
			Main.get().showError("GetOnlyChilds", caught);
		}
	};

	/**
	 * Refresh asyncronous subtree branch after an item menu is deleted
	 */
	final AsyncCallback<Object> callbackDelete = new AsyncCallback<Object>() {
		@Override
		public void onSuccess(Object result) {
			TreeItem tmpItem = actualItem;
			actualItem = actualItem.getParentItem();
			actualItem.setSelected(true);
			actualItem.setState(true);
			actualItem.removeItem(tmpItem);
			evaluesFolderIcon(actualItem);
			showTabFolderProperties();
			Main.get().mainPanel.desktop.navigator.status.unsetFlagDelete();
			refresh(true);
		}

		@Override
		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.navigator.status.unsetFlagDelete();
			Main.get().showError("Delete", caught);
		}
	};

	/**
	 * Gets actual folder node
	 */
	final AsyncCallback<GWTFolder> callbackGet = new AsyncCallback<GWTFolder>() {
		@Override
		public void onSuccess(GWTFolder result) {
			actualItem.setUserObject(result);
			evaluesFolderIcon(actualItem);

			if (folderAction == ACTION_SECURITY_REFRESH) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.setProperties((GWTFolder) actualItem
						.getUserObject());
				folderAction = ACTION_NONE;
			}

			Main.get().mainPanel.desktop.navigator.status.unsetFlagGet();
		}

		@Override
		public void onFailure(Throwable caught) {
			folderAction = ACTION_NONE; // Ensures on error folder action be
			// restores
			Main.get().mainPanel.desktop.navigator.status.unsetFlagGet();
			Main.get().showError("GetFolder", caught);
		}
	};

	/**
	 * Call back create
	 */
	final AsyncCallback<GWTFolder> callbackCreate = new AsyncCallback<GWTFolder>() {
		@Override
		public void onSuccess(GWTFolder result) {
			actualItem = actualItem.getParentItem(); // Restores the real
			// actualItem
			tmpFolder.setSelected(false);
			actualItem.setSelected(true);
			actualItem.setState(true);
			GWTFolder folder = result;
			GWTFolder folderItem = (GWTFolder) actualItem.getUserObject();
			folderItem.setHasChildren(true);
			actualItem.removeItem(tmpFolder);
			TreeItem newFolder = new TreeItem();
			newFolder.setHTML(Util.imageItemHTML("img/menuitem_empty.gif", folder.getName(),"top"));
			newFolder.setUserObject(folder);
			newFolder.setStyleName("okm-TreeItem");
			actualItem.addItem(newFolder);
			evaluesFolderIcon(newFolder);
			evaluesFolderIcon(actualItem);
			Main.get().mainPanel.desktop.navigator.status.unsetFlagCreate();
			if (Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_CATEGORIES) {
				Main.get().mainPanel.desktop.browser.fileBrowser.status.setFlagFolderChilds();
				Main.get().mainPanel.desktop.browser.fileBrowser.addFolder(folder);
			}

			// Special case when we are creating a folder and selects other tree
			// item before removing tmp folder
			// must changing to the real item selected
			if (otherTreeItemSelected != null) {
				onTreeItemSelected(otherTreeItemSelected);
				otherTreeItemSelected = null;
			}

			folderAction = ACTION_NONE;
		}

		@Override
		public void onFailure(Throwable caught) {
			// On error remove tmp folder
			removeTmpFolderCreate();
			folderAction = ACTION_NONE;
			Main.get().mainPanel.desktop.navigator.status.unsetFlagCreate();
			Main.get().showError("Create", caught);
		}
	};

	/**
	 * Call back rename
	 */
	final AsyncCallback<GWTFolder> callbackRename = new AsyncCallback<GWTFolder>() {
		@Override
		public void onSuccess(GWTFolder result) {
			GWTFolder folder = result;
			String oldPath = ((GWTFolder) actualItem.getUserObject()).getPath();
			actualItem.setUserObject(folder);
			evaluesFolderIcon(actualItem);
			showTabFolderProperties();
			String path = ((GWTFolder) actualItem.getUserObject()).getPath();
			Main.get().mainPanel.desktop.browser.fileBrowser.refresh(path);
			Main.get().mainPanel.desktop.navigator.status.unsetFlagRename();
			path = path.substring(1); // deletes first character "/" because
			// parent path not has
			oldPath = oldPath.substring(1); // deletes first character "/"
			// because parent path not has
			changePathBeforeRenaming(oldPath, path, actualItem);
			folderAction = ACTION_NONE;
		}

		@Override
		public void onFailure(Throwable caught) {
			folderAction = ACTION_NONE;
			Main.get().mainPanel.desktop.navigator.status.unsetFlagRename();
			Main.get().showError("Rename", caught);
			cancelRename(); // Cancel renaming on error
		}
	};

	/**
	 * Callback add subscription
	 */
	final AsyncCallback<Object> callbackAddSubscription = new AsyncCallback<Object>() {
		@Override
		public void onSuccess(Object result) {
			((GWTFolder) actualItem.getUserObject()).setSubscribed(true);
			((GWTFolder) actualItem.getUserObject()).getSubscriptors().add(
					Main.get().workspaceUserProperties.getUser()); // Adds user
			// on
			// subscribed
			// list

			GWTFolder folderParent;

			// Case actualItem is root then folder parent is actualItem folder
			if (actualItem == rootItem) {
				folderParent = (GWTFolder) actualItem.getUserObject();
			} else {
				folderParent = (GWTFolder) actualItem.getParentItem().getUserObject();
			}
			Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions((GWTFolder) actualItem
					.getUserObject(), folderParent, TREE_ROOT);
			evaluesFolderIcon(actualItem);
			Main.get().mainPanel.desktop.navigator.status.unsetFlagAddSubscription();
			showTabFolderProperties(); // Refresh folder properties
			Main.get().mainPanel.dashboard.userDashboard.getUserSubscribedFolders();
		}

		@Override
		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.navigator.status.unsetFlagAddSubscription();
			Main.get().showError("AddSubscription", caught);
		}
	};

	/**
	 * Callback remove subscription
	 */
	final AsyncCallback<Object> callbackRemoveSubscription = new AsyncCallback<Object>() {
		@Override
		public void onSuccess(Object result) {
			((GWTFolder) actualItem.getUserObject()).setSubscribed(false);
			((GWTFolder) actualItem.getUserObject()).getSubscriptors().remove(
					Main.get().workspaceUserProperties.getUser()); // Remove
			// user from
			// subscription
			// list
			GWTFolder folderParent;

			// Case actualItem is root then folder parent is actualItem folder
			if (actualItem == rootItem) {
				folderParent = (GWTFolder) actualItem.getUserObject();
			} else {
				folderParent = (GWTFolder) actualItem.getParentItem().getUserObject();
			}
			Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions((GWTFolder) actualItem
					.getUserObject(), folderParent, TREE_ROOT);
			evaluesFolderIcon(actualItem);
			Main.get().mainPanel.desktop.navigator.status.unsetFlagRemoveSubscription();
			showTabFolderProperties(); // Refresh folder properties
			Main.get().mainPanel.dashboard.userDashboard.getUserSubscribedFolders();
		}

		@Override
		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.navigator.status.unsetFlagRemoveSubscription();
			Main.get().showError("AddSubscription", caught);
		}
	};

	/**
	 * Refresh the folders on a item node
	 *
	 * @param path
	 *            The folder path selected to list items
	 */
	public void getChilds(String path) {
		folderService.getChilds(path, false, null, callbackGetChilds);
		Main.get().mainPanel.desktop.navigator.status.setFlagChilds();
	}

	/**
	 * Refresh the folders on a item node Used on firstTime loading for
	 * personal, trash and templates. Only loads tree folders not refresh
	 * filebrowser
	 *
	 * @param path
	 *            The folder path selected to list items
	 */
	public void getOnlyChilds(String path) {
		folderService.getChilds(path, false, null, callbackGetOnlyChilds);
	}

	/**
	 * Creates a new folder on actual item
	 *
	 * @param path
	 *            The folder subpath selected by user = name
	 */
	public void create(String path) {
		// On creation the actualItem is tmpFolder and must create from the
		// parent of tmpFolder ( the real actualItem )
		folderService.create(path, ((GWTFolder) actualItem.getParentItem().getUserObject()).getPath(),
				callbackCreate);
		Main.get().mainPanel.desktop.navigator.status.setFlagCreate();
		hideMenuPopup();
	}

	/**
	 * Rename a folder on actual item
	 *
	 * @param path
	 *            The folder subpath selected by user = name
	 */
	public void rename(String path) {
		folderService.rename(((GWTFolder) actualItem.getUserObject()).getPath(), path, callbackRename);
		Main.get().mainPanel.desktop.navigator.status.setFlagRename();
		hideMenuPopup();
	}

	/**
	 * Show a previos message to confirm delete
	 */
	public void confirmDelete() {
		// In categories stack view, must not fire deleting if user view some
		// document
		if (!(Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_CATEGORIES && Main
				.get().mainPanel.desktop.browser.fileBrowser.hasRows())) {
			Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_FOLDER);
			Main.get().confirmPopup.show();
		} else {
			String msg = Main.i18n("categories.folder.error.delete");
			OKMException exception = new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService,
					ErrorCode.CAUSE_AccessDenied), msg);
			Main.get().showError("delete", exception);
		}
	}

	/**
	 * Deletes a folder, the folder deleted is the actual item and refresh the
	 * parent
	 */
	public void delete() {
		String path = ((GWTFolder) actualItem.getUserObject()).getPath();
		folderService.delete(path, callbackDelete);
		Main.get().mainPanel.desktop.navigator.status.setFlagDelete();
		hideMenuPopup();
	}

	/**
	 * Deletes folder on trash after is moved
	 */
	public void deleteMovedOrRestored() {
		TreeItem tmpItem = actualItem;
		actualItem = actualItem.getParentItem();
		actualItem.setSelected(true);
		actualItem.setState(true);
		actualItem.removeItem(tmpItem);
		evaluesFolderIcon(actualItem);
		showTabFolderProperties();
		refresh(true);
	}

	/**
	 * Move folder on file browser ( only trash mode )
	 */
	public void move() {
		GWTFolder folderToRestore = (GWTFolder) actualItem.getUserObject();
		folderSelectPopup.setEntryPoint(FolderSelectPopup.ENTRYPOINT_TAXONOMY);
		folderSelectPopup.setToMove(folderToRestore);
		showDirectorySelectPopup();
	}

	/**
	 * Copy folder on file browser ( only trash mode )
	 */
	public void copy() {
		GWTFolder folderToCopy = (GWTFolder) actualItem.getUserObject();
		folderSelectPopup.setEntryPoint(FolderSelectPopup.ENTRYPOINT_TAXONOMY);
		folderSelectPopup.setToCopy(folderToCopy);
		showDirectorySelectPopup();
	}

	/**
	 * massiveCopy
	 */
	public void massiveCopy() {
		GWTFolder parentFolder = (GWTFolder) actualItem.getUserObject();
		folderSelectPopup.setEntryPoint(FolderSelectPopup.ENTRYPOINT_TAXONOMY);
		folderSelectPopup.setToMassiveCopy(parentFolder);
		showDirectorySelectPopup();
	}

	/**
	 * massiveMove
	 */
	public void massiveMove() {
		GWTFolder parentFolder = (GWTFolder) actualItem.getUserObject();
		folderSelectPopup.setEntryPoint(FolderSelectPopup.ENTRYPOINT_TAXONOMY);
		folderSelectPopup.setToMassiveMove(parentFolder);
		showDirectorySelectPopup();
	}

	/**
	 * Remove folder from tree after deleted from repository ( normaly deleted
	 * on file browser )
	 *
	 * @param path
	 *            The folder
	 */
	public void removeDeleted(String path) {
		boolean found = false;
		int i = 0;

		while (!found && actualItem.getChildCount() > i) {
			TreeItem tmp = actualItem.getChild(i);

			if (((GWTFolder) tmp.getUserObject()).getPath().equals(path)) {
				actualItem.removeItem(tmp);
				found = true;
			}
			i++;
		}

		// Looks if must change icon on parent if now has no childs and
		// properties
		if (actualItem.getChildCount() < 1) {
			GWTFolder folderItem = (GWTFolder) actualItem.getUserObject();
			folderItem.setHasChildren(false);
		}

		evaluesFolderIcon(actualItem);
	}

	/**
	 * Adds a subscription
	 */
	public void addSubscription() {
		Main.get().mainPanel.desktop.navigator.status.setFlagAddSubscription();
		notifyService.subscribe(((GWTFolder) actualItem.getUserObject()).getPath(), callbackAddSubscription);
	}

	/**
	 * Removes a subscription
	 */
	public void removeSubscription() {
		Main.get().mainPanel.desktop.navigator.status.setFlagRemoveSubscription();
		notifyService.unsubscribe(((GWTFolder) actualItem.getUserObject()).getPath(),
				callbackRemoveSubscription);
	}

	/**
	 * Get the child folder if folder child path exists on actual tree Item
	 * selected
	 *
	 * @param path
	 *            The path
	 * @return
	 */
	public TreeItem getChildFolder(String path) {
		boolean found = false;
		int i = 0;
		TreeItem tmp;

		while (!found && actualItem.getChildCount() > i) {
			tmp = actualItem.getChild(i);

			if (((GWTFolder) tmp.getUserObject()).getPath().equals(path)) {
				found = true;
				return tmp;
			}
			i++;
		}

		return null;
	}

	/**
	 * Refreshing when folder is renamed remotelly For example on file browser
	 * view
	 *
	 * @param path
	 *            The tree path ( last value )
	 * @param newFolder
	 *            The new object value
	 */
	public void renameRenamed(String path, GWTFolder newFolder) {
		boolean found = false;
		int i = 0;

		while (!found && actualItem.getChildCount() > i) {
			TreeItem tmp = actualItem.getChild(i);

			if (((GWTFolder) tmp.getUserObject()).getPath().equals(path)) {
				tmp.setUserObject(newFolder);
				// Evalues Folder Icon puts name and icon correct values from
				// object
				evaluesFolderIcon(tmp);
				found = true;
				String oldPath = path.substring(1); // deletes first character
				// "/" because parent path
				// not has and replace not
				// runs properly
				path = newFolder.getPath().substring(1); // deletes first
				// character "/"
				// because parent
				// path not has and
				// replace not runs
				// properly
				changePathBeforeRenaming(oldPath, path, tmp);
			}

			i++;
		}
	}

	/**
	 * Gets the actual folder (actualItem) and refresh all information on it
	 */
	private void get() {
		Main.get().mainPanel.desktop.navigator.status.setFlagGet();
		folderService.getProperties(((GWTFolder) actualItem.getUserObject()).getPath(), callbackGet);
	}

	/**
	 * Opens all path since folder path
	 *
	 * @param fldId
	 *            The folder path
	 * @param docPath
	 *            The document path
	 */
	public void openAllPathFolder(String fldId, String docPath) {
		flagFileBrowserFinished = false;
		openingFldPath = true;
		refreshFileBrowser = true;
		String rootPath = folderRoot.getPath();
		String actualPath = fldId;
		// getChilds method needs it for last folder)

		// Set the selected document to file browser
		// The browser is only called on the last child node refreshing and
		// restores selected row id
		if (refreshFileBrowser && docPath != null && !docPath.equals("")) {
			Main.get().mainPanel.desktop.browser.fileBrowser.cleanAllFilteringValues(); // Ensure the document will be selected
			Main.get().mainPanel.desktop.browser.fileBrowser.setSelectedRowId(docPath);
		} else {
			Main.get().mainPanel.desktop.browser.fileBrowser.setSelectedRowId(""); // We remove the selectedRowID
			if (!rootPath.equals(actualPath)) {  // Only if rootPath != actualPath ( case must open folders )
				setTabFolderAfterRefresh = true; // We must set tab folder properties on the last node
			}
		}

		while (actualPath.length() > 0) {
			tmpAllPathFolder.add(actualPath);
			actualPath = actualPath.substring(0, actualPath.lastIndexOf("/"));
		}
		// The last path value is root path, is added on tmpPath

		// Selects root node
		actualItem.setState(true);
		actualItem.setSelected(false);
		actualItem = rootItem;
		actualItem.setSelected(true);

		if (!tmpAllPathFolder.isEmpty()) {
			int index = tmpAllPathFolder.size() - 1;
			String tmpPath = tmpAllPathFolder.get(index);
			tmpAllPathFolder.remove(index);
			if (index > 0) {
				setActiveNode(tmpPath, true, false);
			} else {
				setActiveNode(tmpPath, true, refreshFileBrowser); // Never should go into
			}
		} else {
			openingFldPath = false;
			centerActualItemOnScroll(actualItem); // Centers the actualItem
			// selected if scroll is
			// showed
			if (refreshFileBrowser) {

				// && docPath!=null && !docPath.equals("")
				// Refreshing false and docPath="" special case parameter path
				// pased on browser uri to open
				// panels
				Main.get().mainPanel.desktop.browser.fileBrowser.refresh(rootPath);
			}
		}
	}

	private void onTreeItemSelected(TreeItem item) {
		// Try catch to prevent non controled error which stop foldertree and not send expected filebrowser finish signal to folder tree
		try {
			flagFileBrowserFinished = false;
			treeItemChanged = false;
			boolean refresh = true;
			boolean refreshResetExplorer = true;

			// Always mark panel as selected
			setSelectedPanel(true);

			// Evalutate especial cases rename and creating folder refreshing
			switch (folderAction) {
				case ACTION_CREATE:
					refresh = false;
					if (!actualItem.equals(item)) {
						// Special case when we are creating a folder and selects other
						// tree item
						FolderTextBox folder = (FolderTextBox) tmpFolder.getWidget();
						// Ensures is text writen before create folder on other case
						// eliminates
						if (folder.getText().length() > 0) {
							otherTreeItemSelected = item; // Used to save item selected
							create(folder.getText());
						} else {
							otherTreeItemSelected = item; // Used to save item selected
							removeTmpFolderCreate();
						}
					}
					break;

				case ACTION_RENAME:
					if (actualItem.equals(item)) {
						refresh = false;
					}
					break;

				default:
					// Case that not refreshing tree and file browser ( right click )
					if (actualItem.equals(item) && tree.isShowPopUP()) {
						refresh = false; // On right click must not refresh browser but
						// must change properties view
						showTabFolderProperties(); // an removes browser selected file
						// or document if any is selected to
						// change perspective
						Main.get().mainPanel.desktop.browser.fileBrowser.deselecSelectedRow();

					} else {
						// Disables actual item because on changing active node by
						// application this it's not changed
						// automatically
						if (!actualItem.equals(item)) {
							actualItem.setSelected(false);
							actualItem = item;
							refresh = true;
							treeItemChanged = true;
							if (tree.isShowPopUP()) {
								actualItem.setSelected(true);
							}
							// refreshType = false;
						} else {
							// When the same node is selected must refresh without
							// mantaining selected filebrowser row
							refresh = true;
							refreshResetExplorer = false;
							showTabFolderProperties(); // On this special case
							// refreshing tab folder
							// properties
							Main.get().mainPanel.desktop.browser.fileBrowser.deselecSelectedRow();
						}
					}
					// Evaluate privileges
					if (!isActualItemRoot()) {
						GWTFolder folderParent = (GWTFolder) actualItem.getParentItem().getUserObject();
						Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions(
								(GWTFolder) actualItem.getUserObject(), folderParent, TREE_ROOT);
					} else {
						Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions(
								(GWTFolder) actualItem.getUserObject(), folderRoot, TREE_ROOT);
					}

					break;
			}

			if (refresh) {
				refresh(refreshResetExplorer);
			} else {
				flagFileBrowserFinished = true;
			}

			// Only Shows menu popup if flag is enable and selected node is not root
			if (tree.isShowPopUP()) {
				menuPopup.setPopupPosition(tree.mouseX, tree.mouseY);
				// In thesaurus view must not be showed the menu popup
				if (Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_THESAURUS) {
					menuPopup.show();
				}
			}
		} catch (Exception e) {
			fileBrowserRefreshDone();
		}
	}

	/**
	 * canDrag
	 *
	 * @return
	 */
	public boolean canDrag() {
		return (folderAction == ACTION_NONE && !actualItem.equals(rootItem) &&
				Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_CATEGORIES &&
				Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_THESAURUS &&
				Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_TRASH);
	}

	/**
	 * Refresh the tree node
	 */
	public void refresh(final boolean reset) {
		flagFileBrowserFinished = false;
		hideMenuPopup();
		final String path = ((GWTFolder) actualItem.getUserObject()).getPath();
		folderService.getProperties(path, new AsyncCallback<GWTFolder>() {
			@Override
			public void onSuccess(GWTFolder result) {
				// Try catch to prevent non controled error which stop foldertree and not send expected filebrowser finish signal to folder tree
				try {
					actualItem.setUserObject(result); // Updates folder object with last values
					evaluesFolderIcon(actualItem); // Ensures to contemplate any security
					// folder privileges change refresh
					getChilds(path);

					// Case not resets always must show tabfolder properties
					if (!reset) {
						// Case exists a selected row must mantain other case mus show
						// folder properties on tab
						if (Main.get().mainPanel.desktop.browser.fileBrowser.isSelectedRow()) {
							Main.get().mainPanel.desktop.browser.fileBrowser.mantainSelectedRow();
						} else {
							showTabFolderProperties();
						}
					} else {
						showTabFolderProperties();
					}

					Main.get().mainPanel.desktop.browser.fileBrowser.refresh(path);
				} catch (Exception e) {
					fileBrowserRefreshDone();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("getProperties", caught);
				fileBrowserRefreshDone();
			}
		});
	}

	/**
	 * Set a new tree node
	 *
	 * @param path
	 * @param refreshRootNode
	 * @param refreshFileBrowser
	 */
	public void setActiveNode(String path, boolean refreshRootNode, boolean refreshFileBrowser) {
		// Evaluates first node as special case and then others
		if (actualItem == rootItem && refreshRootNode) {
			if (refreshFileBrowser) {
				getChilds(path);
				Main.get().mainPanel.desktop.browser.fileBrowser.refresh(path);
				refreshFileBrowser = false;
				openingFldPath = false;
				centerActualItemOnScroll(actualItem); // Centers the actualItem selected on scroll
			} else {
				getOnlyChilds(path);
			}
		} else if (actualItem.getChildCount() > 0) {
			int i = 0;
			boolean found = false;
			while (i < actualItem.getChildCount() && !found) {
				if (((GWTFolder) actualItem.getChild(i).getUserObject()).getPath().equals(path)) {
					found = true;
					actualItem.setState(true);
					actualItem.setSelected(false);
					actualItem = actualItem.getChild(i);
					actualItem.setSelected(true);
					if (refreshFileBrowser) {
						getChilds(path);
						Main.get().mainPanel.desktop.browser.fileBrowser.refresh(path);
						refreshFileBrowser = false;
						openingFldPath = false;
					} else {
						getOnlyChilds(path);
					}
					centerActualItemOnScroll(actualItem); // Centers the actualItem selected on scroll
				}

				i++;
			}
		} else {
			openingFldPath = false;
		}
		setSelectedPanel(true); // Select this panel
	}

	/**
	 * Add temporary folder on creation
	 */
	public void addTmpFolderCreate() {
		if (folderAction == ACTION_NONE) {
			folderAction = ACTION_CREATE;
			FolderTextBox newFolder = new FolderTextBox();
			tmpFolder = new TreeItem();
			tmpFolder.setWidget(newFolder);
			tmpFolder.setStyleName("okm-TreeItem");
			newFolder.reset();
			newFolder.setText(Main.i18n("tree.folder.new"));
			actualItem.addItem(tmpFolder);
			actualItem.setState(true);
			actualItem.setSelected(false);
			tmpFolder.setSelected(true);
			actualItem = tmpFolder;
			// When we create a new folder we enables selection ( after it,
			// we'll return to disable) for a normal
			// use of the input (if not, cursor and selections not runs
			// propertly )
			rootItem.removeStyleName("okm-DisableSelect"); // Disables drag and
			// drop browser text
			// selection)
			newFolder.setFocus();
		}
	}

	/**
	 * Remove temporary folder on creation Only executed when user closes popup
	 * name and
	 */
	public void removeTmpFolderCreate() {
		actualItem = tmpFolder.getParentItem();
		tmpFolder.setSelected(false);
		actualItem.setSelected(true);
		actualItem.removeItem(tmpFolder);
		folderAction = ACTION_NONE;

		// Special case when we are creating a folder and selects other tree
		// item before removing tmp folder
		// must changing to the real item selected
		if (otherTreeItemSelected != null) {
			onTreeItemSelected(otherTreeItemSelected);
			otherTreeItemSelected = null;
		}
	}

	/**
	 * Gets the actual path of the selected directory tree
	 *
	 * @return The actual path of selected directory
	 */
	public String getActualPath() {
		return ((GWTFolder) actualItem.getUserObject()).getPath();
	}

	/**
	 * Gets the actual tree item of the selected directory tree
	 *
	 * @return The actual tree item of selected directory
	 */
	public TreeItem getActualItem() {
		return actualItem;
	}

	/**
	 * Shows input text to rename selected folder
	 */
	public void rename() {
		if (folderAction == ACTION_NONE) {
			Main.get().mainPanel.disableKeyShorcuts(); // Disables key shortcuts
			// while renaming
			folderAction = ACTION_RENAME;
			renFolder = new FolderTextBox();
			renFolder.reset();
			renFolder.setText(actualItem.getText());
			tmpRenameHtmlFolder = actualItem.getHTML();
			actualItem.setWidget(renFolder);
			// When we create a new folder we enables selection ( after it,
			// we'll return to disable) for a normal
			// use of the input (if not, cursor and selections not runs
			// propertly )
			rootItem.removeStyleName("okm-DisableSelect"); // Disables drag and
			// drop browser text
			// selection)
			renFolder.setFocus();
		}
	}

	/**
	 * Cancels the renaming folder
	 */
	public void cancelRename() {
		actualItem.setHTML(tmpRenameHtmlFolder); // Restores initial icon and name values
		folderAction = ACTION_NONE;
	}

	/**
	 * Show Tab Folder Properties
	 */
	public void showTabFolderProperties() {
		Main.get().mainPanel.desktop.browser.tabMultiple.enableTabFolder();
		Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.setProperties((GWTFolder) actualItem.getUserObject());
	}

	/**
	 * Evalues actual folder icon to prevent other user interaction with the
	 * same folder this ensures icon and object hasChildsValue are consistent
	 */
	public void evaluesFolderIcon(TreeItem item) {
		GWTFolder folderItem = (GWTFolder) item.getUserObject();
		preventFolderInconsitences(item);

		// On case folder is subscribed mus change icon
		String subscribed = "";
		if (folderItem.isSubscribed()) {
			subscribed = "_subscribed";
		}

		// Looks if must change icon on parent if now has no childs and
		// properties with user security atention
		if ((folderItem.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
			if (folderItem.isHasChildren()) {
				item.setHTML(Util.imageItemHTML("img/menuitem_childs" + subscribed + ".gif", folderItem
						.getName(), "top"));
			} else {
				item.setHTML(Util.imageItemHTML("img/menuitem_empty" + subscribed + ".gif", folderItem
						.getName(), "top"));
			}
		} else {
			if (folderItem.isHasChildren()) {
				item.setHTML(Util.imageItemHTML("img/menuitem_childs_ro" + subscribed + ".gif", folderItem
						.getName(), "top"));
			} else {
				item.setHTML(Util.imageItemHTML("img/menuitem_empty_ro" + subscribed + ".gif", folderItem
						.getName(), "top"));
			}
		}
	}

	/**
	 * Prevents folder incosistences between server ( multi user deletes folder
	 * ) and tree nodes drawed
	 *
	 * @param item
	 *            The tree node
	 */
	public void preventFolderInconsitences(TreeItem item) {
		GWTFolder folderItem = (GWTFolder) item.getUserObject();

		// Case that must remove all items node
		if (item.getChildCount() > 0 && !folderItem.isHasChildren()) {
			while (item.getChildCount() > 0) {
				item.getChild(0).remove();
			}
		}

		if (item.getChildCount() < 1 && !folderItem.isHasChildren()) {
			folderItem.setHasChildren(false);
		}
	}

	/**
	 * Shows the directory select popup
	 */
	public void showDirectorySelectPopup() {
		hideMenuPopup();
		folderSelectPopup.show();
	}

	/**
	 * Return true if actualItem selected is root node, other case false
	 *
	 * @return value true or false on actualItem to root node comparation
	 */
	public boolean isActualItemRoot() {
		if (((GWTFolder) actualItem.getUserObject()).getPath().equals(folderRoot.getPath())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Indicates if panel is selected
	 *
	 * @return The value of panel ( selected )
	 */
	public boolean isPanelSelected() {
		return panelSelected;
	}

	/**
	 * Sets the selected panel value
	 *
	 * @param selected
	 *            The selected panel value
	 */
	public void setSelectedPanel(boolean selected) {

		// Case panel is not still selected and must enable this and disable
		// browser panel
		if (!isPanelSelected() && selected) {
			Main.get().mainPanel.desktop.browser.fileBrowser.setSelectedPanel(false);
		}

		// Before making other operation must change value of panel selected
		panelSelected = selected;

		if (selected) {
			addStyleName("okm-PanelSelected");
		} else {
			removeStyleName("okm-PanelSelected");
		}
	}

	/**
	 * Used only on changing stack
	 */
	public void forceSetSelectedPanel() {
		panelSelected = false;
		setSelectedPanel(true);
	}

	/**
	 * Hides the menu Popup
	 */
	public void hideMenuPopup() {
		if (menuPopup != null) {
			menuPopup.hide();
		}
	}

	/**
	 * Evaluates the special case when creating or renaming a folder, user
	 * changes to other active view or active panel to create folder or remove
	 * the tmp folder
	 */
	public void evaluateSpecialTmpFolderCases() {
		if (folderAction == ACTION_CREATE) {
			FolderTextBox folder = (FolderTextBox) actualItem.getWidget();
			if (folder.getText().length() > 0) {
				create(folder.getText());
			} else {
				removeTmpFolderCreate();
			}
		}
	}

	/**
	 * Indicates if is folder creating
	 *
	 * @return The boolean value if is folder creating
	 */
	public boolean isFolderCreating() {
		return (folderAction == ACTION_CREATE);
	}

	/**
	 * Indicates if is folder renaming
	 *
	 * @return The boolean value if is folder renaming
	 */
	public boolean isFolderRenaming() {
		return (folderAction == ACTION_RENAME);
	}

	/**
	 * Gets the actual folder action
	 *
	 * @return The boolean folder action
	 */
	public int getFolderAction() {
		return folderAction;
	}

	/**
	 * Refresh for security changes on actual tree node selected ( icon color )
	 * only refresh the actual
	 */
	public void securityRefresh() {
		folderAction = ACTION_SECURITY_REFRESH; // To make tab properties
		// refresh after get folder
		get();
	}

	/**
	 * Refresh tree iten values when tree folder is changed ( normally on file
	 * browser, or by security ).
	 *
	 * @param folder
	 *            The new folder values
	 */
	public void refreshChildValues(GWTFolder folder) {
		TreeItem tmpItem;
		GWTFolder gWTFolder;

		if (actualItem.getChildCount() > 0) {
			boolean found = false;
			int i = 0;

			while (!found && i < actualItem.getChildCount()) {
				tmpItem = actualItem.getChild(i);
				gWTFolder = (GWTFolder) tmpItem.getUserObject();
				if (folder.getPath().equals(gWTFolder.getPath())) {
					tmpItem.setUserObject(folder);
					evaluesFolderIcon(tmpItem);
					found = true;
				}
				i++;
			}
		}
	}

	/**
	 * Gets the actual folder
	 *
	 * @return The parent folder
	 */
	public GWTFolder getFolder() {
		return (GWTFolder) actualItem.getUserObject();
	}

	/**
	 * getParentFolder
	 *
	 * @return
	 */
	public GWTFolder getParentFolder() {
		if (actualItem != rootItem) {
			return (GWTFolder) actualItem.getParentItem().getUserObject();
		} else {
			return (GWTFolder) actualItem.getUserObject();
		}
	}

	/**
	 * elementClicked
	 *
	 * Returns the treeItem when and element is clicked, used to capture drag
	 * and drop tree Item
	 *
	 * @param element
	 * @return
	 */
	public TreeItem elementClicked(Element element) {
		return tree.elementClicked(element);
	}

	/**
	 * Change recursivelly all the childs path
	 *
	 * @param oldPath
	 *            The old path
	 * @param newPath
	 *            The new path
	 * @param itemToChange
	 *            The tree item to change the path
	 */
	public void changePathBeforeRenaming(String oldPath, String newPath, TreeItem itemToChange) {
		for (int i = 0; i < itemToChange.getChildCount(); i++) {
			TreeItem tmpItem = itemToChange.getChild(i);
			GWTFolder gwtFolder = ((GWTFolder) tmpItem.getUserObject());
			gwtFolder.setPath(gwtFolder.getPath().replaceAll(oldPath, newPath));
			gwtFolder.setParentPath(gwtFolder.getParentPath().replaceAll(oldPath, newPath));
			if (tmpItem.getChildCount() > 0) {
				changePathBeforeRenaming(oldPath, newPath, tmpItem);
			}
		}
	}

	/**
	 * Sets the home
	 */
	public void setHome() {
		GWTFolder folder = (GWTFolder) actualItem.getUserObject();
		Main.get().mainPanel.topPanel.mainMenu.bookmark.confirmSetHome(folder.getUuid(), folder.getPath(), false);
	}

	/**
	 * Confirm the purge
	 */
	public void confirmPurge() {
		Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_PURGE_FOLDER);
		Main.get().confirmPopup.show();
	}

	/**
	 * Confirm if really wants to purge trash
	 */
	public void confirmPurgeTrash() {
		Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_EMPTY_TRASH);
		Main.get().confirmPopup.show();
	}

	/**
	 * Refresh asyncronous subtree branch after an item menu is purged
	 */
	final AsyncCallback<Object> callbackPurge = new AsyncCallback<Object>() {
		@Override
		public void onSuccess(Object result) {
			TreeItem tmpItem = actualItem;
			actualItem = actualItem.getParentItem();
			actualItem.setSelected(true);
			actualItem.setState(true);
			actualItem.removeItem(tmpItem);
			evaluesFolderIcon(actualItem);
			showTabFolderProperties();
			Main.get().mainPanel.desktop.navigator.status.unsetFlagPurge();
			refresh(true);
		}

		@Override
		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.navigator.status.unsetFlagPurge();
			Main.get().showError("Purge", caught);
		}
	};

	/**
	 * Refresh asyncronous subtree branch after all trash is purged
	 */
	final AsyncCallback<Object> callbackPurgeTrash = new AsyncCallback<Object>() {
		@Override
		public void onSuccess(Object result) {
			actualItem = rootItem;
			actualItem.setSelected(true);
			actualItem.setState(true);
			evaluesFolderIcon(actualItem);
			showTabFolderProperties();
			while (actualItem.getChildCount() > 0) {
				actualItem.removeItems();
			}
			Main.get().workspaceUserProperties.getUserDocumentsSize();
			Main.get().mainPanel.desktop.navigator.status.unsetFlagPurgeTrash();
			// After purge trash must refresh desktop
			Main.get().mainPanel.topPanel.toolBar.executeRefresh();
		}

		@Override
		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.navigator.status.unsetFlagPurgeTrash();
			Main.get().showError("PurgeTrash", caught);
		}
	};

	/**
	 * Purge folder on file browser ( only trash mode )
	 */
	public void purge() {
		String path = ((GWTFolder) actualItem.getUserObject()).getPath();
		folderService.purge(path, callbackPurge);
		Main.get().mainPanel.desktop.navigator.status.setFlagPurge();
	}

	/**
	 * Purge all trash ( only trash mode )
	 */
	public void purgeTrash() {
		repositoryService.purgeTrash(callbackPurgeTrash);
		Main.get().mainPanel.desktop.navigator.status.setFlagPurgeTrash();
	}

	/**
	 * Restore folder on file browser ( only trash mode )
	 */
	public void restore() {
		GWTFolder folderToRestore = (GWTFolder) actualItem.getUserObject();
		folderSelectPopup.setEntryPoint(FolderSelectPopup.ENTRYPOINT_TAXONOMY);
		folderSelectPopup.setToRestore(folderToRestore);
		showDirectorySelectPopup();
	}

	/**
	 * Center the actual item on scroll
	 *
	 * @param item
	 *            The item to show visible
	 */
	private void centerActualItemOnScroll(TreeItem item) {
		Main.get().mainPanel.desktop.navigator.scrollTaxonomyPanel.ensureVisible(item);
	}

	/**
	 * Centers the actualItem on scroll
	 */
	public void centerActulItemOnScroll() {
		Main.get().mainPanel.desktop.navigator.scrollTaxonomyPanel.ensureVisible(actualItem);
	}

	/**
	 * Export to file
	 */
	public void exportFolderToFile() {
		Util.downloadFileByUUID(getFolder().getUuid(), "export");
	}

	/**
	 * disableDragged
	 */
	public void disableDragged() {
		tree.disableDragged();
	}

	public void fileBrowserRefreshDone() {
		flagFileBrowserFinished = true;
	}
}