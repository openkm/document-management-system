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

package com.openkm.frontend.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.bean.GWTTaskInstance;
import com.openkm.frontend.client.bean.GWTUINotification;
import com.openkm.frontend.client.constants.GWTRepository;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.service.*;
import com.openkm.frontend.client.widget.startup.StartUp;

import java.util.Date;

/**
 * @author jllort
 *
 */
public class CommonUI {
	private static final OKMRepositoryServiceAsync repositoryService = (OKMRepositoryServiceAsync) GWT.create(OKMRepositoryService.class);
	private static final OKMFolderServiceAsync folderService = (OKMFolderServiceAsync) GWT.create(OKMFolderService.class);
	private static final OKMDocumentServiceAsync documentService = (OKMDocumentServiceAsync) GWT.create(OKMDocumentService.class);
	private static final OKMMailServiceAsync mailService = (OKMMailServiceAsync) GWT.create(OKMMailService.class);
	private static final OKMWorkflowServiceAsync workflowService = (OKMWorkflowServiceAsync) GWT.create(OKMWorkflowService.class);

	public static final String FOLDER_IMAGE_WITH_CHILDREN = "menuitem_childs";
	public static final String FOLDER_IMAGE_EMPTY = "menuitem_empty";
	public static final String FOLDER_IMAGE_READONLY_WITH_CHILDREN = "menuitem_childs_ro";
	public static final String FOLDER_IMAGE_READONLY_EMPTY = "menuitem_empty_ro";
	public static final String FOLDER_IMAGE_WITH_SUBSCRIPTION = "_subscribed";

	/**
	 * Opens path
	 *
	 * @param path The parent path
	 * @param docPath The document full path
	 */
	public static void openPath(String path, String docPath) {
		boolean found = false;
		boolean visibleByProfile = true;

		// Evaluating if path is visible by profile
		if (docPath != null && !docPath.equals("")) {
			visibleByProfile = CommonUI.isVisiblePathByProfile(docPath);
		} else if (path != null && !path.equals("")) {
			visibleByProfile = CommonUI.isVisiblePathByProfile(path);
		}

		// Open folder path if only possible desktop and stack are visible in profiles
		if (Main.get().mainPanel.topPanel.tabWorkspace.isDesktopVisible() && visibleByProfile) {
			int stack = 0;
			if (Main.get().workspaceUserProperties.getWorkspace().isStackTaxonomy()) {
				if (path.startsWith(Main.get().taxonomyRootFolder.getPath())) {
					found = true;
					stack = UIDesktopConstants.NAVIGATOR_TAXONOMY;
				}
			}
			if (Main.get().workspaceUserProperties.getWorkspace().isStackCategoriesVisible()) {
				if (path.startsWith(Main.get().categoriesRootFolder.getPath())) {
					found = true;
					stack = UIDesktopConstants.NAVIGATOR_CATEGORIES;
				}
			}
			if (Main.get().workspaceUserProperties.getWorkspace().isStackMetadataVisible()) {
				if (path.startsWith(Main.get().metadataRootFolder.getPath())) {
					stack = UIDesktopConstants.NAVIGATOR_METADATA;
				}
			}
			if (Main.get().workspaceUserProperties.getWorkspace().isStackThesaurusVisible()) {
				if (path.startsWith(Main.get().thesaurusRootFolder.getPath())) {
					found = true;
					stack = UIDesktopConstants.NAVIGATOR_THESAURUS;
				}
			}
			if (Main.get().workspaceUserProperties.getWorkspace().isStackTemplatesVisible()) {
				if (path.startsWith(Main.get().templatesRootFolder.getPath())) {
					found = true;
					stack = UIDesktopConstants.NAVIGATOR_TEMPLATES;
				}
			}
			if (Main.get().workspaceUserProperties.getWorkspace().isStackPersonalVisible()) {
				if (path.startsWith(Main.get().personalRootFolder.getPath())) {
					found = true;
					stack = UIDesktopConstants.NAVIGATOR_PERSONAL;
				}
			}
			if (Main.get().workspaceUserProperties.getWorkspace().isStackMailVisible()) {
				if (path.startsWith(Main.get().mailRootFolder.getPath())) {
					found = true;
					stack = UIDesktopConstants.NAVIGATOR_MAIL;
				}
			}
			if (Main.get().workspaceUserProperties.getWorkspace().isStackTrashVisible()) {
				if (path.startsWith(Main.get().trashRootFolder.getPath())) {
					found = true;
					stack = UIDesktopConstants.NAVIGATOR_TRASH;
				}
			}

			if (found) {
				// At loading time in profiles can defined other tab than desktop. 
				// If actual loading status is STARTUP_LOADING_TAXONOMY_EVAL_PARAMS should not change tab
				// This indicates we're on startup loading
				if (Main.get().startUp.getStatus() != StartUp.STARTUP_LOADING_TAXONOMY_EVAL_PARAMS) {
					Main.get().mainPanel.topPanel.tabWorkspace.changeSelectedTab(UIDockPanelConstants.DESKTOP);
				}
				Main.get().mainPanel.desktop.navigator.stackPanel.showStack(stack, false);
				Main.get().activeFolderTree.openAllPathFolder(path, docPath);
			}
		}
	}

	/**
	 * openPathByUuid
	 *
	 * @param uuid
	 */
	public static void openPathByUuid(String uuid) {
		repositoryService.getPathByUUID(uuid, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				final String path = result;
				folderService.isValid(path, new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						if (result.booleanValue()) {
							openPath(path, null);
						} else {
							documentService.isValid(path, new AsyncCallback<Boolean>() {
								@Override
								public void onSuccess(Boolean result) {
									if (result.booleanValue()) {
										openPath(Util.getParent(path), path);
									} else {
										mailService.isValid(path, new AsyncCallback<Boolean>() {
											@Override
											public void onSuccess(Boolean result) {
												if (result.booleanValue()) {
													openPath(Util.getParent(path), path);
												} else {
													// not aplicable
												}
											}

											@Override
											public void onFailure(Throwable caught) {
												Main.get().showError("isValid", caught);
											}
										});
									}
								}

								@Override
								public void onFailure(Throwable caught) {
									Main.get().showError("isValid", caught);
								}
							});
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Main.get().showError("isValid", caught);
					}
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("getPathByUUID", caught);
			}
		});
	}

	/**
	 * getRealVisiblePathByProfile
	 *
	 * If path is not visible in user stack panels, return the first visible path
	 *
	 * @param path
	 * @return
	 */
	public static String getRealVisiblePathByProfile(String path) {
		// If folder destination is not visible should select newer destination
		if (!isVisiblePathByProfile(path)) {
			if (Main.get().workspaceUserProperties.getWorkspace().isStackTaxonomy()) {
				return Main.get().taxonomyRootFolder.getPath();
			} else if (Main.get().workspaceUserProperties.getWorkspace().isStackCategoriesVisible()) {
				return Main.get().categoriesRootFolder.getPath();
			} else if (Main.get().workspaceUserProperties.getWorkspace().isStackMetadataVisible()) {
				return Main.get().metadataRootFolder.getPath();
			} else if (Main.get().workspaceUserProperties.getWorkspace().isStackThesaurusVisible()) {
				return Main.get().thesaurusRootFolder.getPath();
			} else if (Main.get().workspaceUserProperties.getWorkspace().isStackTemplatesVisible()) {
				return Main.get().templatesRootFolder.getPath();
			} else if (Main.get().workspaceUserProperties.getWorkspace().isStackPersonalVisible()) {
				return Main.get().personalRootFolder.getPath();
			} else if (Main.get().workspaceUserProperties.getWorkspace().isStackMailVisible()) {
				return Main.get().mailRootFolder.getPath();
			} else if (Main.get().workspaceUserProperties.getWorkspace().isStackTrashVisible()) {
				return Main.get().trashRootFolder.getPath();
			}
		}
		return path; // if none stack is visible has no relevance the path returned
	}

	/**
	 * getDefaultRootLoadingPath
	 */
	public static String getDefaultRootPathByProfile() {
		if (Main.get().workspaceUserProperties.getWorkspace().isStackTaxonomy()) {
			return Main.get().taxonomyRootFolder.getPath();
		} else if (Main.get().workspaceUserProperties.getWorkspace().isStackCategoriesVisible()) {
			return Main.get().categoriesRootFolder.getPath();
		} else if (Main.get().workspaceUserProperties.getWorkspace().isStackThesaurusVisible()) {
			return Main.get().thesaurusRootFolder.getPath();
		} else if (Main.get().workspaceUserProperties.getWorkspace().isStackTemplatesVisible()) {
			return Main.get().templatesRootFolder.getPath();
		} else if (Main.get().workspaceUserProperties.getWorkspace().isStackPersonalVisible()) {
			return Main.get().personalRootFolder.getPath();
		} else if (Main.get().workspaceUserProperties.getWorkspace().isStackMailVisible()) {
			return Main.get().mailRootFolder.getPath();
		} else if (Main.get().workspaceUserProperties.getWorkspace().isStackTrashVisible()) {
			return Main.get().trashRootFolder.getPath();
		} else {
			return Main.get().taxonomyRootFolder.getPath();
		}
	}

	/**
	 * isVisiblePathByProfile
	 *
	 * Evaluates if path is visible to user profiles
	 *
	 * @param path
	 * @return
	 */
	public static boolean isVisiblePathByProfile(String path) {
		if (!Main.get().workspaceUserProperties.getWorkspace().isStackTaxonomy()
				&& path.startsWith("/" + GWTRepository.ROOT)) {
			return false;
		} else if (!Main.get().workspaceUserProperties.getWorkspace().isStackCategoriesVisible()
				&& path.startsWith("/" + GWTRepository.CATEGORIES)) {
			return false;
		} else if (!Main.get().workspaceUserProperties.getWorkspace().isStackMetadataVisible()
				&& path.startsWith("/" + GWTRepository.METADATA)) {
			return false;
		} else if (!Main.get().workspaceUserProperties.getWorkspace().isStackThesaurusVisible()
				&& path.startsWith("/" + GWTRepository.THESAURUS)) {
			return false;
		} else if (!Main.get().workspaceUserProperties.getWorkspace().isStackTemplatesVisible()
				&& path.startsWith("/" + GWTRepository.TEMPLATES)) {
			return false;
		} else if (!Main.get().workspaceUserProperties.getWorkspace().isStackPersonalVisible()
				&& path.startsWith("/" + GWTRepository.PERSONAL)) {
			return false;
		} else if (!Main.get().workspaceUserProperties.getWorkspace().isStackMailVisible()
				&& path.startsWith("/" + GWTRepository.MAIL)) {
			return false;
		} else if (!Main.get().workspaceUserProperties.getWorkspace().isStackTrashVisible()
				&& path.startsWith("/" + GWTRepository.TRASH)) {
			return false;
		}
		return true; // if none stack is visible has no relevance the path returned
	}

	/**
	 * Opens workflow dashboard workspace with required pending user task instance.
	 *
	 * @param taskInstanceId ID of required task instance
	 */
	public static void openUserTaskInstance(String taskInstanceId) {
		if (Main.get().workspaceUserProperties.getWorkspace().isTabDashboardVisible() &&
				Main.get().workspaceUserProperties.getWorkspace().isDashboardWorkflowVisible() &&
				taskInstanceId != null && !taskInstanceId.equals("")) {
			Main.get().mainPanel.topPanel.tabWorkspace.changeSelectedTab(UIDockPanelConstants.DASHBOARD);
			Main.get().mainPanel.dashboard.horizontalToolBar.showWorkflowView();
			workflowService.getUserTaskInstance(new Long(taskInstanceId).longValue(), new AsyncCallback<GWTTaskInstance>() {
				@Override
				public void onSuccess(GWTTaskInstance taskInstance) {
					// Taskintance = null indicates is not valid user task instance
					if (taskInstance != null) {
						// Opens pending user task
						Main.get().mainPanel.dashboard.workflowDashboard.workflowFormPanel.setTaskInstance(taskInstance);
					} else {
						Main.get().showError("getTaskInstance (taskInstance==null)", new Throwable(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_General)));
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("isValidUserPendingTask", caught);
				}
			});
		}
	}

	/**
	 * disableExtension
	 */
	public static void disableExtension(String name) {
		GWTUINotification uin = new GWTUINotification();
		uin.setAction(GWTUINotification.ACTION_NONE);
		uin.setType(GWTUINotification.TYPE_TEMPORAL);
		uin.setDate(new Date());
		uin.setShow(true);
		uin.setMessage("[" + name + "] " + Main.i18n("browser.java.support.not.found.extension.disabled"));
		Main.get().mainPanel.bottomPanel.userInfo.addUINotification(uin);
	}

	/**
	 * getFolderIcon
	 */
	public static String getFolderIcon(GWTFolder fld) {
		// url is ./ because this method call is always done from /frontend/
		String url = "img/";
		if ((fld.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
			if (fld.isHasChildren()) {
				url += FOLDER_IMAGE_WITH_CHILDREN;
			} else {
				url += FOLDER_IMAGE_EMPTY;
			}
		} else {
			if (fld.isHasChildren()) {
				url += FOLDER_IMAGE_READONLY_WITH_CHILDREN;
			} else {
				url += FOLDER_IMAGE_READONLY_EMPTY;
			}
		}
		if (fld.isSubscribed()) {
			url += FOLDER_IMAGE_WITH_SUBSCRIPTION; // image subscription at ends
		}
		url += ".gif";
		return url;
	}

	/**
	 * initJavaScriptApi
	 *
	 * @param toolBar
	 */
	public native void initJavaScriptApi(CommonUI commonUI) /*-{
        $wnd.openPathByUuid = function (uuid) {
            @com.openkm.frontend.client.util.CommonUI::openPathByUuid(Ljava/lang/String;)(uuid);
            return true;
        }
        $wnd.openPath = function (folderPath, docPath) {
            @com.openkm.frontend.client.util.CommonUI::openPath(Ljava/lang/String;Ljava/lang/String;)(folderPath, docPath);
            return true;
        };
        $wnd.jsOpenPathByUuid = function (uuid) {
            @com.openkm.frontend.client.util.CommonUI::openPathByUuid(Ljava/lang/String;)(uuid);
            return true;
        }
        $wnd.jsOpenPath = function (folderPath, docPath) {
            @com.openkm.frontend.client.util.CommonUI::openPath(Ljava/lang/String;Ljava/lang/String;)(folderPath, docPath);
            return true;
        };
        $wnd.jsOpenUserTaskInstance = function (taskInstanceId) {
            @com.openkm.frontend.client.util.CommonUI::openUserTaskInstance(Ljava/lang/String;)(taskInstanceId);
            return true;
        };
    }-*/;
}