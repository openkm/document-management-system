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

package com.openkm.frontend.client.widget.startup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.openkm.extension.frontend.client.Customization;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTUserConfig;
import com.openkm.frontend.client.extension.ExtensionManager;
import com.openkm.frontend.client.extension.event.HasWidgetEvent;
import com.openkm.frontend.client.extension.event.handler.WidgetHandlerExtension;
import com.openkm.frontend.client.extension.event.hashandler.HasWidgetHandlerExtension;
import com.openkm.frontend.client.service.*;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.OriginPanel;
import com.openkm.frontend.client.widget.mainmenu.Bookmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jllort
 *
 */
public class StartUp implements HasWidgetHandlerExtension, HasWidgetEvent {
	public static final int STARTUP_STARTING = 0;
	public static final int STARTUP_GET_USER_VALUES = 1;
	public static final int STARTUP_GET_TAXONOMY_ROOT = 2;
	public static final int STARTUP_GET_CATEGORIES_ROOT = 3;
	public static final int STARTUP_GET_METADATA_ROOT = 4;
	public static final int STARTUP_GET_THESAURUS_ROOT = 5;
	public static final int STARTUP_GET_TEMPLATE_ROOT = 6;
	public static final int STARTUP_GET_PERSONAL = 7;
	public static final int STARTUP_GET_MAIL = 8;
	public static final int STARTUP_GET_TRASH = 9;
	public static final int STARTUP_GET_USER_HOME = 10;
	public static final int STARTUP_GET_BOOKMARKS = 11;
	public static final int STARTUP_INIT_TREE_NODES = 12;
	public static final int STARTUP_LOADING_HISTORY_SEARCH = 13;
	public static final int STARTUP_LOADING_TAXONOMY_EVAL_PARAMS = 14;
	public static final int STARTUP_LOADING_OPEN_PATH = 15;

	private final OKMRepositoryServiceAsync repositoryService = (OKMRepositoryServiceAsync) GWT
			.create(OKMRepositoryService.class);
	private final OKMAuthServiceAsync authService = (OKMAuthServiceAsync) GWT.create(OKMAuthService.class);
	private final OKMUserConfigServiceAsync userConfigService = (OKMUserConfigServiceAsync) GWT
			.create(OKMUserConfigService.class);
	private final OKMGeneralServiceAsync generalService = (OKMGeneralServiceAsync) GWT.create(OKMGeneralService.class);
	private final OKMFolderServiceAsync folderService = (OKMFolderServiceAsync) GWT.create(OKMFolderService.class);
	private final OKMDocumentServiceAsync documentService = (OKMDocumentServiceAsync) GWT
			.create(OKMDocumentService.class);

	private boolean enabled = true;
	private boolean error = false;
	private int status = -1;
	private String docPath = null;
	private String fldPath = null;
	private String taskInstanceId = null;
	public Timer keepAlive;
	private List<WidgetHandlerExtension> widgetHandlerExtensionList;

	/**
	 * StartUp
	 */
	public StartUp() {
		widgetHandlerExtensionList = new ArrayList<WidgetHandlerExtension>();
	}

	/**
	 * Init on first load
	 */
	public void init() {
		generalService.getEnabledExtensions(new AsyncCallback<List<String>>() {
			@Override
			public void onSuccess(List<String> result) {
				Main.get().setExtensionUuidList(result);

				// Only show registered extensions
				ExtensionManager.start(Customization.getExtensionWidgets(result));
				nextStatus(STARTUP_STARTING);
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("getEnabledExtensions", caught);
				nextStatus(STARTUP_STARTING);
			}
		});
	}

	/**
	 * Gets asynchronous taxonomy root node
	 */
	final AsyncCallback<GWTFolder> callbackGetRootFolder = new AsyncCallback<GWTFolder>() {
		public void onSuccess(GWTFolder result) {
			// Only executes on initialization and evaluate root Node permissions
			Main.get().taxonomyRootFolder = result;
			nextStatus(STARTUP_GET_CATEGORIES_ROOT);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetRootFolder", caught);
		}
	};

	/**
	 * Gets asynchronous template root node
	 */
	final AsyncCallback<GWTFolder> callbackGetTemplatesFolder = new AsyncCallback<GWTFolder>() {
		public void onSuccess(GWTFolder result) {
			// Only executes on initialization
			Main.get().templatesRootFolder = result;
			nextStatus(STARTUP_GET_PERSONAL);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetTemplatesFolder", caught);
		}
	};

	/**
	 * Gets asynchronous mail root node
	 */
	final AsyncCallback<GWTFolder> callbackGetMailFolder = new AsyncCallback<GWTFolder>() {
		public void onSuccess(GWTFolder result) {
			// Only executes on initialization
			Main.get().mailRootFolder = result;
			nextStatus(STARTUP_GET_TRASH);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetMailFolder", caught);
		}
	};

	/**
	 * Gets asynchronous thesaurus root node
	 */
	final AsyncCallback<GWTFolder> callbackGetThesaurusFolder = new AsyncCallback<GWTFolder>() {
		public void onSuccess(GWTFolder result) {
			// Only executes on initialization
			Main.get().thesaurusRootFolder = result;
			nextStatus(STARTUP_GET_TEMPLATE_ROOT);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetThesaurusFolder", caught);
		}
	};

	/**
	 * Gets asynchronous categories root node
	 */
	final AsyncCallback<GWTFolder> callbackGetCategoriesFolder = new AsyncCallback<GWTFolder>() {
		public void onSuccess(GWTFolder result) {
			// Only executes on initialization
			Main.get().categoriesRootFolder = result;
			nextStatus(STARTUP_GET_METADATA_ROOT);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetCategoriesFolder", caught);
		}
	};

	/**
	 * Gets asynchronous metadata root node
	 */
	final AsyncCallback<GWTFolder> callbackGetMetadataFolder = new AsyncCallback<GWTFolder>() {
		public void onSuccess(GWTFolder result) {
			// Only executes on initialization
			Main.get().metadataRootFolder = result;
			nextStatus(STARTUP_GET_THESAURUS_ROOT);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetCategoriesFolder", caught);
		}
	};

	/**
	 * Callback get user home
	 */
	final AsyncCallback<GWTUserConfig> callbackGetUserHome = new AsyncCallback<GWTUserConfig>() {
		public void onSuccess(GWTUserConfig result) {
			Main.get().userHome = result;
			nextStatus(STARTUP_GET_BOOKMARKS);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetUserHome", caught);
		}
	};

	/**
	 * Gets asynchronous personal documents node
	 */
	final AsyncCallback<GWTFolder> callbackGetPersonalFolder = new AsyncCallback<GWTFolder>() {
		public void onSuccess(GWTFolder result) {
			Main.get().personalRootFolder = result;
			nextStatus(STARTUP_GET_MAIL);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetPersonalFolder", caught);
		}
	};

	/**
	 * Gets asynchronous trash node
	 */
	final AsyncCallback<GWTFolder> callbackGetTrashFolder = new AsyncCallback<GWTFolder>() {
		public void onSuccess(GWTFolder result) {
			Main.get().trashRootFolder = result;
			nextStatus(STARTUP_GET_USER_HOME);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetTrashFolder", caught);
		}
	};

	/**
	 * Call back add new granted user
	 */
	final AsyncCallback<Object> callbackKeepAlive = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
		}

		public void onFailure(Throwable caught) {
			Main.get().mainPanel.bottomPanel.setStatus("status.keep.alive.error", true);
		}
	};

	/**
	 * Gets asynchronous to add a group
	 */
	final AsyncCallback<Map<String, String>> callbackGetPropertyGroupTranslations = new AsyncCallback<Map<String, String>>() {
		public void onSuccess(Map<String, String> result) {
			Main.get().hPropertyGroupI18n = result;

		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetPropertyGroupTranslations", caught);
		}
	};

	/**
	 * Gets the trash
	 */
	public void getTrash() {
		repositoryService.getTrashFolder(callbackGetTrashFolder);
	}

	/**
	 * Gets the personal documents
	 */
	public void getPersonal() {
		repositoryService.getPersonalFolder(callbackGetPersonalFolder);
	}

	/**
	 * Gets the user home
	 *
	 */
	public void getUserHome() {
		userConfigService.getUserHome(callbackGetUserHome);
	}

	/**
	 * Gets the template
	 */
	public void getTemplate() {
		repositoryService.getTemplatesFolder(callbackGetTemplatesFolder);
	}

	/**
	 * Gets the mail
	 */
	public void getMail() {
		repositoryService.getMailFolder(callbackGetMailFolder);
	}

	/**
	 * Gets the thesaurus
	 */
	public void getThesaurus() {
		repositoryService.getThesaurusFolder(callbackGetThesaurusFolder);
	}

	/**
	 * getCategories
	 */
	public void getCategories() {
		repositoryService.getCategoriesFolder(callbackGetCategoriesFolder);
	}

	/**
	 * getCategories
	 */
	public void getMetadata() {
		repositoryService.getMetadataFolder(callbackGetMetadataFolder);
	}

	/**
	 * Gets the taxonomy
	 */
	public void getRoot() {
		repositoryService.getRootFolder(callbackGetRootFolder);
	}

	/**
	 * startKeepAlive
	 */
	public void startKeepAlive(double scheduleTime) {
		// KeepAlieve thread
		keepAlive = new Timer() {
			public void run() {
				authService.keepAlive(callbackKeepAlive);
			}
		};

		keepAlive.scheduleRepeating(new Double(scheduleTime).intValue()); // 15 min
	}

	/**
	 * Opens a document destination passed by url parameter
	 */
	private void openDocumentByBrowserURLParam() {
		fldPath = Main.get().fldPath;
		docPath = Main.get().docPath;
		taskInstanceId = Main.get().taskInstanceId;
		// Always reset variables
		Main.get().docPath = null;
		Main.get().fldPath = null;
		Main.get().taskInstanceId = null;

		// Simulate we pass params by broser ( take a look really are not passed )
		// to show user home on loading
		if (fldPath == null || fldPath.equals("")) {
			if (Main.get().userHome.getHomeType().equals(Bookmark.BOOKMARK_DOCUMENT)) {
				docPath = Main.get().userHome.getHomePath();
				fldPath = Util.getParent(Main.get().userHome.getHomePath());
			} else if (Main.get().userHome.getHomeType().equals(Bookmark.BOOKMARK_FOLDER)) {
				fldPath = Main.get().userHome.getHomePath();
			}
		}

		// Evaluating if path is visible by profile, othercase change default root
		if (docPath != null && !docPath.equals("")) {
			if (!CommonUI.isVisiblePathByProfile(docPath)) {
				fldPath = CommonUI.getRealVisiblePathByProfile(docPath); // Will get default root in first visible stack
				docPath = null;
			}
		} else if (fldPath != null && !fldPath.equals("")) {
			if (!CommonUI.isVisiblePathByProfile(fldPath)) {
				fldPath = CommonUI.getRealVisiblePathByProfile(fldPath); // Will get default root in first visible stack
			}
		}

		if (docPath != null && !docPath.equals("")) {
			documentService.isValid(docPath, new AsyncCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean result) {
					if (result.booleanValue()) {
						// Opens folder passed by parameter
						CommonUI.openPath(fldPath, docPath);
					} else {
						CommonUI.openPath(CommonUI.getDefaultRootPathByProfile(), "");
					}
					CommonUI.openUserTaskInstance(taskInstanceId); // Always trying opening taskInstance
					Main.get().startUp.nextStatus(StartUp.STARTUP_LOADING_OPEN_PATH);
				}

				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("isValid", caught);
					CommonUI.openPath(CommonUI.getDefaultRootPathByProfile(), "");
					CommonUI.openUserTaskInstance(taskInstanceId); // Always trying opening taskInstance
					Main.get().startUp.nextStatus(StartUp.STARTUP_LOADING_OPEN_PATH);
				}
			});
		} else if (fldPath != null && !fldPath.equals("")) {
			folderService.isValid(fldPath, new AsyncCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean result) {
					if (result.booleanValue()) {
						boolean isRoot = Util.isRoot(fldPath);
						if (!isRoot) {
							// Opens folder passed by parameter ( if is not root then opening fldPaht will refreshing toolbar and other panel values )
							CommonUI.openPath(fldPath, "");
							CommonUI.openUserTaskInstance(taskInstanceId); // Always trying opening taskInstance
							Main.get().startUp.nextStatus(StartUp.STARTUP_LOADING_OPEN_PATH);
						} else {
							folderService.getProperties(fldPath, new AsyncCallback<GWTFolder>() {
								@Override
								public void onSuccess(final GWTFolder result) {
									folderService.getProperties(fldPath, new AsyncCallback<GWTFolder>() {
										@Override
										public void onSuccess(GWTFolder parent) {
											Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions(result, parent, OriginPanel.TREE_ROOT);
											Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.setProperties(result);

											// Opens folder passed by parameter
											CommonUI.openPath(fldPath, "");
											CommonUI.openUserTaskInstance(taskInstanceId); // Always trying opening taskInstance
											Main.get().startUp.nextStatus(StartUp.STARTUP_LOADING_OPEN_PATH);
										}

										@Override
										public void onFailure(Throwable caught) {
											Main.get().showError("getProperties", caught);
											CommonUI.openPath(fldPath, "");
											CommonUI.openUserTaskInstance(taskInstanceId); // Always trying opening taskInstance
											Main.get().startUp.nextStatus(StartUp.STARTUP_LOADING_OPEN_PATH);
										}
									});
								}

								@Override
								public void onFailure(Throwable caught) {
									Main.get().showError("getProperties", caught);
									CommonUI.openPath(fldPath, "");
									CommonUI.openUserTaskInstance(taskInstanceId); // Always trying opening taskInstance
									Main.get().startUp.nextStatus(StartUp.STARTUP_LOADING_OPEN_PATH);
								}
							});
						}
					} else {
						CommonUI.openPath(CommonUI.getDefaultRootPathByProfile(), "");
						CommonUI.openUserTaskInstance(taskInstanceId); // Always trying opening taskInstance
						Main.get().startUp.nextStatus(StartUp.STARTUP_LOADING_OPEN_PATH);
					}
				}

				public void onFailure(Throwable caught) {
					Main.get().showError("isValid", caught);
					CommonUI.openPath(CommonUI.getDefaultRootPathByProfile(), "");
					CommonUI.openUserTaskInstance(taskInstanceId); // Always trying opening taskInstance
					Main.get().startUp.nextStatus(StartUp.STARTUP_LOADING_OPEN_PATH);
				}
			});
		} else {
			CommonUI.openPath(CommonUI.getDefaultRootPathByProfile(), "");
			CommonUI.openUserTaskInstance(taskInstanceId); // Always trying opening taskInstance
			Main.get().startUp.nextStatus(StartUp.STARTUP_LOADING_OPEN_PATH);
		}
	}

	/**
	 * Sets the next status
	 *
	 * @param status The new status
	 */
	public void nextStatus(int status) {
		if (enabled) {
			// Status is always incremental
			if (this.status < status) {
				this.status = status;

				switch (status) {
					case STARTUP_STARTING:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.starting.loading"), STARTUP_STARTING);
						nextStatus(STARTUP_GET_USER_VALUES);
						break;

					case STARTUP_GET_USER_VALUES:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.user.values"),
								STARTUP_GET_USER_VALUES);
						Main.get().workspaceUserProperties.init();
						break;

					case STARTUP_GET_TAXONOMY_ROOT:
						if (Main.get().workspaceUserProperties.getWorkspace().isStackTaxonomy()) {
							Main.get().startUpPopup.addStatus(Main.i18n("startup.taxonomy"), STARTUP_GET_TAXONOMY_ROOT);
							getRoot();
						} else {
							Main.get().startUpPopup.jumpActual();
							nextStatus(STARTUP_GET_CATEGORIES_ROOT);
						}
						break;

					case STARTUP_GET_CATEGORIES_ROOT:
						if (Main.get().workspaceUserProperties.getWorkspace().isStackCategoriesVisible()) {
							Main.get().startUpPopup.addStatus(Main.i18n("startup.categories"), STARTUP_GET_CATEGORIES_ROOT);
							getCategories();
						} else {
							Main.get().startUpPopup.jumpActual();
							nextStatus(STARTUP_GET_METADATA_ROOT);
						}
						break;

					case STARTUP_GET_METADATA_ROOT:
						if (Main.get().workspaceUserProperties.getWorkspace().isStackMetadataVisible()) {
							Main.get().startUpPopup.addStatus(Main.i18n("startup.metadata"), STARTUP_GET_METADATA_ROOT);
							getMetadata();
						} else {
							Main.get().startUpPopup.jumpActual();
							nextStatus(STARTUP_GET_THESAURUS_ROOT);
						}
						break;

					case STARTUP_GET_THESAURUS_ROOT:
						if (Main.get().workspaceUserProperties.getWorkspace().isStackThesaurusVisible()) {
							Main.get().startUpPopup.addStatus(Main.i18n("startup.thesaurus"), STARTUP_GET_THESAURUS_ROOT);
							getThesaurus();
						} else {
							Main.get().startUpPopup.jumpActual();
							nextStatus(STARTUP_GET_TEMPLATE_ROOT);
						}
						break;

					case STARTUP_GET_TEMPLATE_ROOT:
						if (Main.get().workspaceUserProperties.getWorkspace().isStackTemplatesVisible()) {
							Main.get().startUpPopup.addStatus(Main.i18n("startup.template"), STARTUP_GET_TEMPLATE_ROOT);
							getTemplate();
						} else {
							Main.get().startUpPopup.jumpActual();
							nextStatus(STARTUP_GET_PERSONAL);
						}
						break;

					case STARTUP_GET_PERSONAL:
						if (Main.get().workspaceUserProperties.getWorkspace().isStackPersonalVisible()) {
							Main.get().startUpPopup.addStatus(Main.i18n("startup.personal"), STARTUP_GET_PERSONAL);
							getPersonal();
						} else {
							Main.get().startUpPopup.jumpActual();
							nextStatus(STARTUP_GET_MAIL);
						}
						break;

					case STARTUP_GET_MAIL:
						if (Main.get().workspaceUserProperties.getWorkspace().isStackMailVisible()) {
							Main.get().startUpPopup.addStatus(Main.i18n("startup.mail"), STARTUP_GET_MAIL);
							getMail();
						} else {
							Main.get().startUpPopup.jumpActual();
							nextStatus(STARTUP_GET_TRASH);
						}
						break;

					case STARTUP_GET_TRASH:
						if (Main.get().workspaceUserProperties.getWorkspace().isStackTrashVisible()) {
							Main.get().startUpPopup.addStatus(Main.i18n("startup.trash"), STARTUP_GET_TRASH);
							getTrash();
						} else {
							Main.get().startUpPopup.jumpActual();
							nextStatus(STARTUP_GET_USER_HOME);
						}
						break;

					case STARTUP_GET_USER_HOME:
						// Operations to be done after loading stacks
						if (Main.get().workspaceUserProperties.getWorkspace().isStackTaxonomy()) {
							Main.get().activeFolderTree = Main.get().mainPanel.desktop.navigator.taxonomyTree;
						} else if (Main.get().workspaceUserProperties.getWorkspace().isStackCategoriesVisible()) {
							Main.get().activeFolderTree = Main.get().mainPanel.desktop.navigator.categoriesTree;
						} else if (Main.get().workspaceUserProperties.getWorkspace().isStackMetadataVisible()) {
							Main.get().activeFolderTree = Main.get().mainPanel.desktop.navigator.metadataTree;
						} else if (Main.get().workspaceUserProperties.getWorkspace().isStackThesaurusVisible()) {
							Main.get().activeFolderTree = Main.get().mainPanel.desktop.navigator.thesaurusTree;
						} else if (Main.get().workspaceUserProperties.getWorkspace().isStackTemplatesVisible()) {
							Main.get().activeFolderTree = Main.get().mainPanel.desktop.navigator.templateTree;
						} else if (Main.get().workspaceUserProperties.getWorkspace().isStackPersonalVisible()) {
							Main.get().activeFolderTree = Main.get().mainPanel.desktop.navigator.personalTree;
						} else if (Main.get().workspaceUserProperties.getWorkspace().isStackMailVisible()) {
							Main.get().activeFolderTree = Main.get().mainPanel.desktop.navigator.mailTree;
						} else if (Main.get().workspaceUserProperties.getWorkspace().isStackTrashVisible()) {
							Main.get().activeFolderTree = Main.get().mainPanel.desktop.navigator.trashTree;
						}
						Main.get().mainPanel.desktop.browser.fileBrowser.table.fillWidth(); // Sets de columns size
						Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.init();
						Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.init();
						Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.init();
						Main.get().startUpPopup.addStatus(Main.i18n("startup.user.home"), STARTUP_GET_USER_HOME);
						getUserHome();
						break;

					case STARTUP_GET_BOOKMARKS:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.bookmarks"), STARTUP_GET_BOOKMARKS);

						// Initialize bookmarks
						Main.get().mainPanel.topPanel.mainMenu.bookmark.getAll();

						// Initialize tab multiple
						Main.get().mainPanel.desktop.browser.tabMultiple.init();
						break;

					case STARTUP_INIT_TREE_NODES:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.init.tree.nodes"), STARTUP_INIT_TREE_NODES);
						if (Main.get().workspaceUserProperties.getWorkspace().isStackTaxonomy()) {
							Main.get().mainPanel.desktop.navigator.taxonomyTree.init();
						}
						if (Main.get().workspaceUserProperties.getWorkspace().isStackCategoriesVisible()) {
							Main.get().mainPanel.desktop.navigator.categoriesTree.init();
						}
						if (Main.get().workspaceUserProperties.getWorkspace().isStackMetadataVisible()) {
							Main.get().mainPanel.desktop.navigator.metadataTree.init();
						}
						if (Main.get().workspaceUserProperties.getWorkspace().isStackThesaurusVisible()) {
							Main.get().mainPanel.desktop.navigator.thesaurusTree.init();
						}
						if (Main.get().workspaceUserProperties.getWorkspace().isStackTemplatesVisible()) {
							Main.get().mainPanel.desktop.navigator.templateTree.init();
						}
						if (Main.get().workspaceUserProperties.getWorkspace().isStackPersonalVisible()) {
							Main.get().mainPanel.desktop.navigator.personalTree.init();
						}
						if (Main.get().workspaceUserProperties.getWorkspace().isStackMailVisible()) {
							Main.get().mainPanel.desktop.navigator.mailTree.init();
						}
						if (Main.get().workspaceUserProperties.getWorkspace().isStackTrashVisible()) {
							Main.get().mainPanel.desktop.navigator.trashTree.init();
						}
						Main.get().startUp.nextStatus(StartUp.STARTUP_LOADING_HISTORY_SEARCH);
						break;

					case STARTUP_LOADING_HISTORY_SEARCH:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.history.search"),
								STARTUP_LOADING_HISTORY_SEARCH);

						// Initialize history saved
						Main.get().mainPanel.search.historySearch.searchSaved.init();

						Main.get().mainPanel.search.historySearch.userNews.init();
						Main.get().mainPanel.setVisible(true);

						// Some actions ( menus / etc. ) must be set at ends startup
						// After init widget methods ares all yet finished
						Main.get().workspaceUserProperties.setAvailableAction();

						Main.get().startUp.nextStatus(StartUp.STARTUP_LOADING_TAXONOMY_EVAL_PARAMS);
						break;

					case STARTUP_LOADING_TAXONOMY_EVAL_PARAMS:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.taxonomy.eval.params"),
								STARTUP_LOADING_TAXONOMY_EVAL_PARAMS);
						openDocumentByBrowserURLParam();
						break;

					case STARTUP_LOADING_OPEN_PATH:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.taxonomy.open.path"), STARTUP_LOADING_OPEN_PATH);
						enabled = false;
						Main.get().mainPanel.search.setLoadFinish();

						fireEvent(HasWidgetEvent.FINISH_STARTUP);

						if (!error) {
							Main.get().startUpPopup.hide();
						}

						if (Util.getUserAgent().startsWith("ie")) {
							Timer timer = new Timer() {
								@Override
								public void run() {
									Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.correctIEDefect();
									Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.correctIEDefect();
									Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.correctIEDefect();
								}
							};
							timer.schedule(500);
						}
						break;
				}
			}
		}
	}

	/**
	 * Disable
	 */
	public void disable() {
		enabled = false;
	}

	/**
	 * Tries to recover after an error
	 */
	public void recoverFromError() {
		error = true;
		Main.get().startUpPopup.button.setVisible(true);

		if (status < STARTUP_LOADING_OPEN_PATH) {
			// This range are sequential calls
			if (status < STARTUP_INIT_TREE_NODES) {
				nextStatus(status + 1); // Tries to execute next initializing
			} else {
				nextStatus(status + 1); // Tries to execute next initializing
			}
		} else {
			enabled = false;
		}
	}

	public String getStatusMsg(int status) {
		String msg = "";

		switch (status) {
			case STARTUP_STARTING:
				msg = Main.i18n("startup.starting.loading");
				break;

			case STARTUP_GET_USER_VALUES:
				msg = Main.i18n("startup.loading.user.values");
				break;

			case STARTUP_GET_TAXONOMY_ROOT:
				msg = Main.i18n("startup.taxonomy");
				break;

			case STARTUP_GET_CATEGORIES_ROOT:
				msg = Main.i18n("startup.categories");
				break;

			case STARTUP_GET_THESAURUS_ROOT:
				msg = Main.i18n("startup.thesaurus");
				break;

			case STARTUP_GET_TEMPLATE_ROOT:
				msg = Main.i18n("startup.template");
				break;

			case STARTUP_GET_PERSONAL:
				msg = Main.i18n("startup.personal");
				break;

			case STARTUP_GET_MAIL:
				msg = Main.i18n("startup.mail");
				break;

			case STARTUP_GET_TRASH:
				msg = Main.i18n("startup.trash");
				break;

			case STARTUP_GET_USER_HOME:
				msg = Main.i18n("startup.user.home");
				getUserHome();
				break;

			case STARTUP_GET_BOOKMARKS:
				msg = Main.i18n("startup.bookmarks");
				break;

			case STARTUP_INIT_TREE_NODES:
				msg = Main.i18n("startup.loading.taxonomy");
				break;

			case STARTUP_LOADING_HISTORY_SEARCH:
				msg = Main.i18n("startup.loading.history.search");
				break;

			case STARTUP_LOADING_TAXONOMY_EVAL_PARAMS:
				msg = Main.i18n("startup.loading.taxonomy.eval.params");
				break;

			case STARTUP_LOADING_OPEN_PATH:
				msg = Main.i18n("startup.loading.taxonomy.open.path");
				break;
		}

		return msg;
	}

	/**
	 * getStatus
	 */
	public int getStatus() {
		return status;
	}

	@Override
	public void addWidgetHandlerExtension(WidgetHandlerExtension handlerExtension) {
		widgetHandlerExtensionList.add(handlerExtension);
	}

	@Override
	public void fireEvent(WidgetEventConstant event) {
		for (WidgetHandlerExtension handlerExtension : widgetHandlerExtensionList) {
			handlerExtension.onChange(event);
		}
	}
}