/**
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

package com.openkm.frontend.client.widget.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.*;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.constants.ui.UIFileUploadConstants;
import com.openkm.frontend.client.extension.event.HasToolBarEvent;
import com.openkm.frontend.client.extension.event.handler.ToolBarHandlerExtension;
import com.openkm.frontend.client.extension.event.hashandler.HasToolBarHandlerExtension;
import com.openkm.frontend.client.extension.widget.toolbar.ToolBarButtonExtension;
import com.openkm.frontend.client.service.*;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.ConfirmPopup;
import com.openkm.frontend.client.widget.OriginPanel;
import com.openkm.frontend.client.widget.mainmenu.Bookmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * ToolBar
 *
 * @author jllort
 */
public class ToolBar extends Composite implements OriginPanel, HasToolBarEvent, HasToolBarHandlerExtension {
	private final OKMDocumentServiceAsync documentService = (OKMDocumentServiceAsync) GWT.create(OKMDocumentService.class);
	private final OKMFolderServiceAsync folderService = (OKMFolderServiceAsync) GWT.create(OKMFolderService.class);
	private final OKMPropertyGroupServiceAsync propertyGroupService = (OKMPropertyGroupServiceAsync) GWT.create(OKMPropertyGroupService.class);

	private HorizontalPanel panel;
	private ToolBarButton createFolder;
	private ToolBarButton find;
	private ToolBarButton download;
	private ToolBarButton downloadPdf;
	private ToolBarButton print;
	private ToolBarButton lock;
	private ToolBarButton unlock;
	private ToolBarButton addDocument;
	private ToolBarButton checkout;
	private ToolBarButton checkin;
	private ToolBarButton cancelCheckout;
	private ToolBarButton delete;
	private ToolBarButton addPropertyGroup;
	private ToolBarButton removePropertyGroup;
	private ToolBarButton startWorkflow;
	private ToolBarButton addSubscription;
	private ToolBarButton removeSubscription;
	private ToolBarButton home;
	private ToolBarButton refresh;
	private ToolBarButton splitterResize;
	private ToolBarButton omr;
	private Object node;
	private ResizeToolBarMenu resizeToolBarMenu;
	private FindToolBarMenu findToolBarMenu;
	private boolean evaluateGroup = false;
	private boolean evaluateWorkflow = false;
	private boolean evaluateDownload = false;

	private boolean enabled = true; // Indicates if toolbar is enabled or disabled
	private boolean propertyGroupEnabled = false; // Indicates if property group is enabled, used only on changing language
	private ToolBarOption toolBarOption;
	private int actualView;
	private HashMap<String, ToolBarOption> viewValues;
	private List<ToolBarButtonExtension> widgetExtensionList;
	private List<ToolBarHandlerExtension> toolBarHandlerExtensionList;

	// Used to store latest evaluation before massive actions selected
	private Object massiveObj1;
	private int massiveOriginPanel = 0;
	private boolean massiveOptions = false;

	private MouseOverHandler mouseOverHandler = new MouseOverHandler() {
		@Override
		public void onMouseOver(MouseOverEvent event) {
			Widget sender = (Widget) event.getSource();
			sender.addStyleName("okm-ToolBar-selected");
		}
	};

	private MouseOutHandler mouseOutHandler = new MouseOutHandler() {
		@Override
		public void onMouseOut(MouseOutEvent event) {
			Widget sender = (Widget) event.getSource();
			sender.removeStyleName("okm-ToolBar-selected");
		}
	};

	/**
	 * Folder listener
	 */
	ClickHandler createFolderHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.createFolderOption) {
				executeFolderDirectory();
				fireEvent(HasToolBarEvent.EXECUTE_CREATE_FOLDER);
			}
		}
	};

	/**
	 * Execute create folder
	 */
	public void executeFolderDirectory() {
		Main.get().activeFolderTree.addTmpFolderCreate();
	}

	/**
	 * executeFindFolder
	 */
	public void executeFindFolder() {
		Main.get().findFolderSelectPopup.show();
		fireEvent(HasToolBarEvent.EXECUTE_FIND_FOLDER);
	}

	/**
	 * executeFindDocument
	 */
	public void executeFindDocument() {
		Main.get().findDocumentSelectPopup.show();
		fireEvent(HasToolBarEvent.EXECUTE_FIND_DOCUMENT);
	}

	/**
	 * executeFindDocument
	 */
	public void executeFindSimilarDocument() {
		Main.get().findSimilarDocumentSelectPopup.show();
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
			Main.get().findSimilarDocumentSelectPopup.find(Main.get().mainPanel.desktop.browser.fileBrowser.getDocument().getUuid());
		}
		fireEvent(HasToolBarEvent.EXECUTE_FIND_SIMILAR_DOCUMENT);
	}

	/**
	 * Lock Handler
	 */
	ClickHandler lockHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.lockOption) {
				executeLock();
			}
		}
	};

	/**
	 * Execute unlock
	 */
	public void executeLock() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_LOCK_MASSIVE);
			Main.get().confirmPopup.center();
		} else {
			Main.get().mainPanel.desktop.browser.fileBrowser.lock();
		}
		fireEvent(HasToolBarEvent.EXECUTE_LOCK);
	}

	/**
	 * Unlock Handler
	 */
	ClickHandler unLockHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.unLockOption) {
				executeUnlock();
			}
		}
	};

	/**
	 * Execute lock
	 */
	public void executeUnlock() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_UNLOCK_MASSIVE);
			Main.get().confirmPopup.center();
		} else {
			GWTDocument doc = Main.get().mainPanel.desktop.browser.fileBrowser.getDocument();
			if (doc.getLockInfo().getOwner().equals(Main.get().workspaceUserProperties.getUser().getId())) {
				Main.get().mainPanel.desktop.browser.fileBrowser.unlock();
				fireEvent(HasToolBarEvent.EXECUTE_UNLOCK);
			} else if (Main.get().workspaceUserProperties.getWorkspace().isAdminRole()) {
				Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_FORCE_UNLOCK);
				Main.get().confirmPopup.show();
			}
		}
	}

	/**
	 * Add document Handler
	 */
	ClickHandler addDocumentHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.addDocumentOption) {
				if (Main.get().mainPanel.bottomPanel.userInfo.isQuotaExceed()) {
					Main.get().showError("UserQuotaExceed",
							new OKMException("OKM-" + ErrorCode.ORIGIN_OKMBrowser + ErrorCode.CAUSE_QuotaExceed, ""));
				} else {
					executeAddDocument();
				}
			}
		}
	};

	/**
	 * Execute adds documents
	 */
	public void executeAddDocument() {
		FileToUpload fileToUpload = new FileToUpload();
		fileToUpload.setFileUpload(new FileUpload());
		fileToUpload.setPath(Main.get().activeFolderTree.getActualPath());
		fileToUpload.setAction(UIFileUploadConstants.ACTION_INSERT);
		Main.get().fileUpload.addPendingFileToUpload(fileToUpload);
		fireEvent(HasToolBarEvent.EXECUTE_ADD_DOCUMENT);
	}

	/**
	 * Delete Handler
	 */
	ClickHandler deleteHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.deleteOption) {
				executeDelete();
			}
		}
	};

	/**
	 * Executes delete option
	 */
	public void executeDelete() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_MASSIVE);
			Main.get().confirmPopup.center();
		} else {
			if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
				Main.get().mainPanel.desktop.browser.fileBrowser.confirmDelete();
			} else if (Main.get().activeFolderTree.isPanelSelected()) {
				Main.get().activeFolderTree.confirmDelete();
			}
		}

		fireEvent(HasToolBarEvent.EXECUTE_DELETE);
	}

	/**
	 * Executes delete option
	 */
	public void executeCopy() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			Main.get().activeFolderTree.massiveCopy();
		} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
			Main.get().mainPanel.desktop.browser.fileBrowser.copy();
		} else if (Main.get().activeFolderTree.isPanelSelected()) {
			Main.get().activeFolderTree.copy();
		}

		fireEvent(HasToolBarEvent.EXECUTE_COPY);
	}

	/**
	 * Executes move option
	 */
	public void executeMove() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			Main.get().activeFolderTree.massiveMove();
		} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
			Main.get().mainPanel.desktop.browser.fileBrowser.move();
		} else if (Main.get().activeFolderTree.isPanelSelected()) {
			Main.get().activeFolderTree.move();
		}

		fireEvent(HasToolBarEvent.EXECUTE_MOVE);
	}

	/**
	 * Executes rename option
	 */
	public void executeRename() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
			Main.get().mainPanel.desktop.browser.fileBrowser.rename();
		} else if (Main.get().activeFolderTree.isPanelSelected()) {
			Main.get().activeFolderTree.rename();
		}

		fireEvent(HasToolBarEvent.EXECUTE_RENAME);
	}

	/**
	 * addNote
	 */
	public void addNote() {
		Main.get().notesPopup.center();
		Main.get().notesPopup.richTextArea.setFocus(true); // Solves safari bug
	}

	/**
	 * addNote
	 */
	public void addCategory() {
		Main.get().categoriesPopup.reset();
		Main.get().categoriesPopup.center();
	}

	/**
	 * addNote
	 */
	public void addKeyword() {
		Main.get().keywordsPopup.reset();
		Main.get().keywordsPopup.center();
	}

	/**
	 * addPropertyGroup
	 */
	public void addPropertyGroup() {
		Main.get().propertyGroupPopup.reset();
		Main.get().propertyGroupPopup.center();
		fireEvent(HasToolBarEvent.EXECUTE_ADD_PROPERTY_GROUP);
	}

	/**
	 * updatePropertyGroup
	 */
	public void updatePropertyGroup() {
		if (toolBarOption.updatePropertyGroupOption) {
			Main.get().updatePropertyGroupPopup.reset();
			Main.get().updatePropertyGroupPopup.center();
		}
	}

	/**
	 * mergePdf
	 */
	public void mergePdf() {
		Main.get().pdfMergePopup.reset(Main.get().mainPanel.desktop.browser.fileBrowser.getAllSelectedPdfDocuments());
		Main.get().pdfMergePopup.center();
	}

	/**
	 * Edit Handler
	 */
	ClickHandler editHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.checkoutOption) {
				executeCheckout();
			}
		}
	};

	/**
	 * Execute check out
	 */
	public void executeCheckout() {
		if (Main.get().mainPanel.bottomPanel.userInfo.isQuotaExceed()) {
			Main.get().showError("UserQuotaExceed",
					new OKMException("OKM-" + ErrorCode.ORIGIN_OKMBrowser + ErrorCode.CAUSE_QuotaExceed, ""));
		} else {
			Main.get().mainPanel.desktop.browser.fileBrowser.checkout();
			fireEvent(HasToolBarEvent.EXECUTE_CHECKOUT);
		}
	}

	/**
	 * Checkin Handler
	 */
	ClickHandler checkinHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.checkinOption) {
				executeCheckin();
			}
		}
	};

	/**
	 * Execute checkin
	 */
	public void executeCheckin() {
		Main.get().mainPanel.desktop.browser.fileBrowser.checkin();
		fireEvent(HasToolBarEvent.EXECUTE_CHECKIN);
	}

	/**
	 * Checkout cancel Handler
	 */
	ClickHandler cancelCheckoutHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.cancelCheckoutOption) {
				executeCancelCheckout();
			}
		}
	};

	/**
	 * Cancel the check out
	 */
	public void executeCancelCheckout() {
		GWTDocument doc = Main.get().mainPanel.desktop.browser.fileBrowser.getDocument();

		if (doc.getLockInfo().getOwner().equals(Main.get().workspaceUserProperties.getUser().getId())) {
			Main.get().mainPanel.desktop.browser.fileBrowser.cancelCheckout();
			fireEvent(HasToolBarEvent.EXECUTE_CANCEL_CHECKOUT);
		} else if (Main.get().workspaceUserProperties.getWorkspace().isAdminRole()) {
			Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_FORCE_CANCEL_CHECKOUT);
			Main.get().confirmPopup.show();
		}
	}

	/**
	 * Download Handler
	 */
	ClickHandler downloadHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.downloadOption) {
				executeDownload();
			}
		}
	};

	/**
	 * Download as PDF Handler
	 */
	ClickHandler downloadPdfHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.downloadPdfOption) {
				executeDownloadPdf();
			}
		}
	};

	/**
	 * Print as PDF Handler
	 */
	ClickHandler printHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.printOption) {
				executePrint();
			}
		}
	};

	/**
	 * Download document
	 */
	public void executeDownload() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			Main.get().mainPanel.desktop.browser.fileBrowser.massiveDownload();
		} else {
			if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
				Main.get().mainPanel.desktop.browser.fileBrowser.table.downloadDocument(false);
			} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isMailSelected()) {
				Main.get().mainPanel.desktop.browser.fileBrowser.table.downloadMail();
			}
		}

		fireEvent(HasToolBarEvent.EXECUTE_DOWNLOAD_DOCUMENT);
	}

	/**
	 * Download document as PDF
	 */
	public void executeDownloadPdf() {
		Main.get().mainPanel.desktop.browser.fileBrowser.table.downloadDocumentPdf();
		fireEvent(HasToolBarEvent.EXECUTE_DOWNLOAD_PDF_DOCUMENT);
	}

	/**
	 * Convert document
	 */
	public void executeConvert() {
		if (toolBarOption.convertOption) {
			if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
				Main.get().convertPopup.reset(Main.get().mainPanel.desktop.browser.fileBrowser.getDocument());
				Main.get().convertPopup.center();
			}
		}
	}

	/**
	 * executePrint
	 */
	public void executePrint() {
		Main.get().mainPanel.desktop.browser.fileBrowser.table.print();
	}

	/**
	 * Add property group Handler
	 */
	ClickHandler addPropertyGroupHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.addPropertyGroupOption) {
				addPropertyGroup();
			}
		}
	};

	/**
	 * executeAddBookmark
	 */
	public void executeAddBookmark() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
			if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
				String path = Main.get().mainPanel.desktop.browser.fileBrowser.getDocument().getPath();
				Main.get().mainPanel.topPanel.mainMenu.bookmarkPopup.show(path, path.substring(path.lastIndexOf("/") + 1));
			} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isFolderSelected()) {
				String path = Main.get().mainPanel.desktop.browser.fileBrowser.getFolder().getPath();
				Main.get().mainPanel.topPanel.mainMenu.bookmarkPopup.show(path, path.substring(path.lastIndexOf("/") + 1));
			}
		} else if (Main.get().activeFolderTree.isPanelSelected()) {
			String path = Main.get().activeFolderTree.getActualPath();
			Main.get().mainPanel.topPanel.mainMenu.bookmarkPopup.show(path, path.substring(path.lastIndexOf("/") + 1));
		}
	}

	/**
	 * Add workflowgroup
	 */
	public void executeAddWorkflow() {
		Main.get().workflowPopup.show();
		fireEvent(HasToolBarEvent.EXECUTE_ADD_WORKFLOW);
	}

	/**
	 * Remove property group Handler
	 */
	ClickHandler removePropertyGroupHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.removePropertyGroupOption && toolBarOption.firedRemovePropertyGroupOption) {
				executeRemovePropertyGroup();
			}
		}
	};

	/**
	 * Add workflow Handler
	 */
	ClickHandler startWorkflowHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.workflowOption) {
				executeAddWorkflow();
			}
		}
	};

	/**
	 * Execute remove property group
	 */
	public void executeRemovePropertyGroup() {
		Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_PROPERTY_GROUP);
		Main.get().confirmPopup.show();
		fireEvent(HasToolBarEvent.EXECUTE_REMOVE_PROPERTY_GROUP);
	}

	/**
	 * Add subscription
	 */
	ClickHandler addSubscriptionHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.addSubscription) {
				executeAddSubscription();
			}
		}
	};

	/**
	 * Execute add subscription
	 */
	public void executeAddSubscription() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
			Main.get().mainPanel.desktop.browser.fileBrowser.addSubscription();
		} else if (Main.get().activeFolderTree.isPanelSelected()) {
			Main.get().activeFolderTree.addSubscription();
		}

		fireEvent(HasToolBarEvent.EXECUTE_ADD_SUBSCRIPTION);
	}

	/**
	 * Remove subscription
	 */
	ClickHandler removeSubscriptionHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.removeSubscription) {
				executeRemoveSubscription();
			}
		}
	};
	
	/**
	 * Arrow rotate clock wise Handler
	 */
	ClickHandler arrowHomeHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.homeOption) {
				executeGoToUserHome();
			}
		}
	};

	/**
	 * Execute remove property group
	 */
	public void executeRemoveSubscription() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
			Main.get().mainPanel.desktop.browser.fileBrowser.removeSubscription();
		} else if (Main.get().activeFolderTree.isPanelSelected()) {
			Main.get().activeFolderTree.removeSubscription();
		}

		fireEvent(HasToolBarEvent.EXECUTE_REMOVE_SUBSCRIPTION);
	}

	/**
	 * Arrow rotate clock wise Handler
	 */
	ClickHandler arrowRefreshHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.refreshOption) {
				executeRefresh();
			}
		}
	};

	/**
	 * OMR Handler
	 */
	ClickHandler omrHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.omrOption) {
				executeOmr();
			}
		}
	};
	
	/**
	 * Refreshing workspace
	 */
	public void executeRefresh() {
		Main.get().mainPanel.topPanel.mainMenu.refreshAvailableTemplates();

		switch (Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace()) {
			case UIDockPanelConstants.DESKTOP:
				int actualView = Main.get().mainPanel.desktop.navigator.stackPanel.getStackIndex();

				switch (actualView) {
					case UIDesktopConstants.NAVIGATOR_TAXONOMY:
					case UIDesktopConstants.NAVIGATOR_CATEGORIES:
					case UIDesktopConstants.NAVIGATOR_METADATA:
					case UIDesktopConstants.NAVIGATOR_THESAURUS:
					case UIDesktopConstants.NAVIGATOR_TEMPLATES:
					case UIDesktopConstants.NAVIGATOR_PERSONAL:
					case UIDesktopConstants.NAVIGATOR_MAIL:
						Main.get().activeFolderTree.refresh(false);
						break;

					case UIDesktopConstants.NAVIGATOR_TRASH:
						Main.get().activeFolderTree.refresh(false);
						break;
				}
				break;

			case UIDockPanelConstants.SEARCH:
				break;

			case UIDockPanelConstants.DASHBOARD:
				Main.get().mainPanel.dashboard.refreshAll();
				break;
		}

		fireEvent(HasToolBarEvent.EXECUTE_REFRESH);
	}

	/**
	 * executeOmr
	 */
	public void executeOmr() {
		if (toolBarOption.omrOption) {
			if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
					Main.get().omrPopup.reset(Main.get().mainPanel.desktop.browser.fileBrowser.getDocument());
					Main.get().omrPopup.center();
					fireEvent(HasToolBarEvent.EXECUTE_OMR);
				}
			}
		}
	}
		
	/**
	 * Goes home
	 */
	public void executeGoToUserHome() {
		// First must validate path is correct
		if (Main.get().userHome != null && !Main.get().userHome.getHomePath().equals("")) {
			if (Main.get().userHome.getHomeType().equals(Bookmark.BOOKMARK_DOCUMENT)) {
				documentService.isValid(Main.get().userHome.getHomePath(), new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						if (result.booleanValue()) {
							String path = Main.get().userHome.getHomePath();
							CommonUI.openPath(Util.getParent(path), path);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Main.get().showError("isValid", caught);
					}
				});
			} else if (Main.get().userHome.getHomeType().equals(Bookmark.BOOKMARK_FOLDER)) {
				folderService.isValid(Main.get().userHome.getHomePath(), new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						if (result.booleanValue()) {
							CommonUI.openPath(Util.getParent(Main.get().userHome.getHomePath()), "");
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Main.get().showError("isValid", caught);
					}
				});
			}
		}

		fireEvent(HasToolBarEvent.EXECUTE_GO_HOME);
	}

	/**
	 * Execute add export
	 */
	public void executeExport() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
			Main.get().mainPanel.desktop.browser.fileBrowser.exportFolderToFile();
		} else if (Main.get().activeFolderTree.isPanelSelected()) {
			Main.get().activeFolderTree.exportFolderToFile();
		}

		fireEvent(HasToolBarEvent.EXECUTE_EXPORT_TO_ZIP);
	}

	/**
	 * executePurge
	 */
	public void executePurge() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
			Main.get().mainPanel.desktop.browser.fileBrowser.confirmPurge();
		} else if (Main.get().activeFolderTree.isPanelSelected()) {
			Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_PURGE_FOLDER);
			Main.get().confirmPopup.show();
		}
	}

	/**
	 * executeRestore
	 */
	public void executeRestore() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
			Main.get().mainPanel.desktop.browser.fileBrowser.restore();
		} else if (Main.get().activeFolderTree.isPanelSelected()) {
			Main.get().activeFolderTree.restore();
		}
	}

	/**
	 * executeCreateFromTemplate
	 */
	public void executeCreateFromTemplate() {
		Main.get().mainPanel.desktop.browser.fileBrowser.createFromTemplate();
	}

	/**
	 * executePurgeTrash
	 */
	public void executePurgeTrash() {
		Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_EMPTY_TRASH);
		Main.get().confirmPopup.show();
	}

	/**
	 * Gets the HTML space code
	 *
	 * @return Space tool bar code
	 */
	private HTML space() {
		HTML space = new HTML(" ");
		space.setStyleName("okm-ToolBar-space");
		return space;
	}

	/**
	 * Tool Bar
	 */
	public ToolBar() {
		actualView = UIDesktopConstants.NAVIGATOR_TAXONOMY;
		viewValues = new HashMap<String, ToolBarOption>();
		toolBarOption = getDefaultRootToolBar();
		widgetExtensionList = new ArrayList<ToolBarButtonExtension>();
		toolBarHandlerExtensionList = new ArrayList<ToolBarHandlerExtension>();

		// ONLY TO DEVELOPMENT TESTING
		// enableAllToolBarForTestingPurpose();
		find = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.find()), Main.i18n("general.menu.find"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (toolBarOption.findOption) {
					findToolBarMenu.setPopupPosition(find.getAbsoluteLeft() + 20, find.getAbsoluteTop() + 6);
					findToolBarMenu.show();
				}
			}
		});

		lock = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.lockDisabled()), Main.i18n("general.menu.edit.lock"), lockHandler);

		unlock = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.lockDisabled()), Main.i18n("general.menu.edit.unlock"),
				unLockHandler);

		createFolder = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.createFolder()), Main.i18n("tree.menu.directory.create"),
				createFolderHandler);

		addDocument = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.addDocument()), Main.i18n("general.menu.file.add.document"),
				addDocumentHandler);

		delete = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.deleteDisabled()), Main.i18n("general.menu.edit.delete"),
				deleteHandler);

		checkout = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.checkoutDisabled()), Main.i18n("general.menu.edit.checkout"),
				editHandler);

		checkin = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.checkinDisabled()), Main.i18n("general.menu.edit.checkin"),
				checkinHandler);

		cancelCheckout = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.cancelCheckoutDisabled()),
				Main.i18n("general.menu.edit.cancel.checkout"), cancelCheckoutHandler);

		download = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.downloadDisabled()),
				Main.i18n("general.menu.file.download.document"), downloadHandler);

		downloadPdf = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.downloadPdfDisabled()),
				Main.i18n("general.menu.file.download.document.pdf"), downloadPdfHandler);

		print = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.printDisabled()), Main.i18n("general.menu.file.print"),
				printHandler);

		addPropertyGroup = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.addPropertyGroupDisabled()),
				Main.i18n("general.menu.edit.add.property.group"), addPropertyGroupHandler);

		removePropertyGroup = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.removePropertyGroupDisabled()),
				Main.i18n("general.menu.edit.remove.property.group"), removePropertyGroupHandler);

		startWorkflow = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.startWorkflowDisabled()),
				Main.i18n("general.menu.file.start.workflow"), startWorkflowHandler);

		addSubscription = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.addSubscriptionDisabled()),
				Main.i18n("general.menu.edit.add.subscription"), addSubscriptionHandler);

		removeSubscription = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.removeSubscriptionDisabled()),
				Main.i18n("general.menu.edit.remove.subscription"), removeSubscriptionHandler);

		home = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.home()), Main.i18n("general.menu.bookmark.home"), arrowHomeHandler);

		refresh = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.refresh()), Main.i18n("general.menu.file.refresh"),
				arrowRefreshHandler);

		splitterResize = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.splitterResize()),
				Main.i18n("general.menu.splitter.resize"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (toolBarOption.splitterResizeOption) {
					resizeToolBarMenu.setPopupPosition(splitterResize.getAbsoluteLeft() + 20, splitterResize.getAbsoluteTop() + 6);
					resizeToolBarMenu.show();
				}
			}
		});
		
		omr = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.omr()), Main.i18n("general.menu.file.omr"), omrHandler);

		find.addMouseOverHandler(mouseOverHandler);
		find.addMouseOutHandler(mouseOutHandler);
		lock.addMouseOverHandler(mouseOverHandler);
		lock.addMouseOutHandler(mouseOutHandler);
		unlock.addMouseOverHandler(mouseOverHandler);
		unlock.addMouseOutHandler(mouseOutHandler);
		createFolder.addMouseOverHandler(mouseOverHandler);
		createFolder.addMouseOutHandler(mouseOutHandler);
		addDocument.addMouseOverHandler(mouseOverHandler);
		addDocument.addMouseOutHandler(mouseOutHandler);
		delete.addMouseOverHandler(mouseOverHandler);
		delete.addMouseOutHandler(mouseOutHandler);
		checkout.addMouseOverHandler(mouseOverHandler);
		checkout.addMouseOutHandler(mouseOutHandler);
		checkin.addMouseOverHandler(mouseOverHandler);
		checkin.addMouseOutHandler(mouseOutHandler);
		cancelCheckout.addMouseOverHandler(mouseOverHandler);
		cancelCheckout.addMouseOutHandler(mouseOutHandler);
		download.addMouseOverHandler(mouseOverHandler);
		download.addMouseOutHandler(mouseOutHandler);
		downloadPdf.addMouseOverHandler(mouseOverHandler);
		downloadPdf.addMouseOutHandler(mouseOutHandler);
		print.addMouseOverHandler(mouseOverHandler);
		print.addMouseOutHandler(mouseOutHandler);
		addPropertyGroup.addMouseOverHandler(mouseOverHandler);
		addPropertyGroup.addMouseOutHandler(mouseOutHandler);
		removePropertyGroup.addMouseOverHandler(mouseOverHandler);
		removePropertyGroup.addMouseOutHandler(mouseOutHandler);
		startWorkflow.addMouseOverHandler(mouseOverHandler);
		startWorkflow.addMouseOutHandler(mouseOutHandler);
		addSubscription.addMouseOverHandler(mouseOverHandler);
		addSubscription.addMouseOutHandler(mouseOutHandler);
		removeSubscription.addMouseOverHandler(mouseOverHandler);
		removeSubscription.addMouseOutHandler(mouseOutHandler);
		home.addMouseOverHandler(mouseOverHandler);
		home.addMouseOutHandler(mouseOutHandler);
		refresh.addMouseOverHandler(mouseOverHandler);
		refresh.addMouseOutHandler(mouseOutHandler);
		splitterResize.addMouseOverHandler(mouseOverHandler);
		splitterResize.addMouseOutHandler(mouseOutHandler);
		omr.addMouseOverHandler(mouseOverHandler);
		omr.addMouseOutHandler(mouseOutHandler);
		
		find.setStyleName("okm-ToolBar-button");
		lock.setStyleName("okm-ToolBar-button");
		unlock.setStyleName("okm-ToolBar-button");
		createFolder.setStyleName("okm-ToolBar-button");
		addDocument.setStyleName("okm-ToolBar-button");
		delete.setStyleName("okm-ToolBar-button-disabled");
		checkout.setStyleName("okm-ToolBar-button-disabled");
		checkin.setStyleName("okm-ToolBar-button-disabled");
		cancelCheckout.setStyleName("okm-ToolBar-button-disabled");
		download.setStyleName("okm-ToolBar-button-disabled");
		downloadPdf.setStyleName("okm-ToolBar-button-disabled");
		print.setStyleName("okm-ToolBar-button-disabled");
		addPropertyGroup.setStyleName("okm-ToolBar-button-disabled");
		removePropertyGroup.setStyleName("okm-ToolBar-button-disabled");
		startWorkflow.setStyleName("okm-ToolBar-button-disabled");
		addSubscription.setStyleName("okm-ToolBar-button-disabled");
		removeSubscription.setStyleName("okm-ToolBar-button-disabled");
		home.setStyleName("okm-ToolBar-button-disabled");
		refresh.setStyleName("okm-ToolBar-button-disabled");
		splitterResize.setStyleName("okm-ToolBar-button-disabled");
		omr.setStyleName("okm-ToolBar-button-disabled");
		
		panel = new HorizontalPanel();
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		panel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		panel.addStyleName("okm-ToolBar");
		panel.add(space());
		panel.add(find);
		panel.add(space());
		panel.add(download);
		panel.add(space());
		panel.add(downloadPdf);
		panel.add(space());
		panel.add(print);
		panel.add(space());
		panel.add(new Image(OKMBundleResources.INSTANCE.separator())); // pos 9
		panel.add(lock);
		panel.add(space());
		panel.add(unlock);
		panel.add(space());
		panel.add(new Image(OKMBundleResources.INSTANCE.separator()));
		panel.add(createFolder);
		panel.add(space());
		panel.add(addDocument);
		panel.add(space());
		panel.add(checkout);
		panel.add(space());
		panel.add(checkin);
		panel.add(space());
		panel.add(cancelCheckout);
		panel.add(space());
		panel.add(delete);
		panel.add(space());
		panel.add(new Image(OKMBundleResources.INSTANCE.separator()));
		panel.add(addPropertyGroup);
		panel.add(space());
		panel.add(removePropertyGroup);
		panel.add(space());
		panel.add(new Image(OKMBundleResources.INSTANCE.separator()));
		panel.add(startWorkflow);
		panel.add(space());
		panel.add(new Image(OKMBundleResources.INSTANCE.separator()));
		panel.add(addSubscription);
		panel.add(space());
		panel.add(removeSubscription);
		panel.add(space());
		panel.add(new Image(OKMBundleResources.INSTANCE.separator()));
		panel.add(refresh);
		panel.add(space());
		panel.add(home);
		panel.add(space());
		panel.add(new Image(OKMBundleResources.INSTANCE.separator()));
		panel.add(splitterResize);
		panel.add(space());
		panel.add(omr);
		panel.add(space());
		
		// Hide all buttons at startup
		for (int i = 0; i < panel.getWidgetCount(); i++) {
			panel.getWidget(i).setVisible(false);
		}

		resizeToolBarMenu = new ResizeToolBarMenu();
		findToolBarMenu = new FindToolBarMenu();

		initWidget(panel);
	}

	/**
	 * Checks permissions associated to folder and tool button enabled actions
	 *
	 * @param folder The folder
	 * @param folderParent the folder parent
	 * @param origin The Origin panel
	 */
	public void checkToolButtonPermissions(GWTFolder folder, GWTFolder folderParent, int originPanel) {
		node = folder; // saves last done evaluated
		this.massiveObj1 = folderParent;
		this.massiveOriginPanel = originPanel;

		// Disable all menu options
		disableAllOptions();

		// folderParent.setPermissions((byte)(GWTPermission.DELETE |
		// GWTPermission.READ | GWTPermission.SECURITY |
		// GWTPermission.WRITE));
		// Only if toolbar is enabled must change tools icons values
		boolean isRoot = Util.isRoot(folder.getPath());

		if (isEnabled()) {
			// Enable quick search
			if (Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace() == UIDockPanelConstants.DESKTOP
					&& (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TAXONOMY
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TEMPLATES
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_PERSONAL
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_MAIL || Main.get().mainPanel.desktop.navigator
					.getStackIndex() == UIDesktopConstants.NAVIGATOR_TRASH)) {
				toolBarOption.findOption = true;
				toolBarOption.findFolderOption = true;
				toolBarOption.findDocumentOption = true;
			}

			if (Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_THESAURUS
					&& Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_CATEGORIES
					&& Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_METADATA) {
				toolBarOption.workflowOption = true;
			}

			// On folder parent don't enables subscription
			if (!isRoot) {
				toolBarOption.exportOption = true;
				if (folder.isSubscribed()) {
					toolBarOption.removeSubscription = true;
				} else {
					toolBarOption.addSubscription = true;
				}
			}

			// Enables or disables deleting ( in root is not enabled by default
			if (!isRoot
					&& ((folderParent.getPermissions() & GWTPermission.DELETE) == GWTPermission.DELETE)
					&& ((folder.getPermissions() & GWTPermission.DELETE) == GWTPermission.DELETE)
					&& (Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_THESAURUS && Main.get().mainPanel.desktop.navigator
					.getStackIndex() != UIDesktopConstants.NAVIGATOR_METADATA)
					&& !((originPanel == OriginPanel.FILE_BROWSER) && Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_CATEGORIES)) {
				toolBarOption.deleteOption = true;
			}

			if (evaluateGroup && ((folder.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP)
					&& ((folderParent.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP)) {
				toolBarOption.addPropertyGroupOption = true;
				toolBarOption.updatePropertyGroupOption = true;
				toolBarOption.removePropertyGroupOption = true;
			}
			if (evaluateWorkflow && ((folder.getPermissions() & GWTPermission.START_WORKFLOW) == GWTPermission.START_WORKFLOW)) {
				toolBarOption.workflowOption = true;
			} else if (Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_THESAURUS
					&& Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_CATEGORIES
					&& Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_METADATA) {
				toolBarOption.workflowOption = true;
			}

			if ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
				// Case segurity property group grant is not enabled
				if (!evaluateGroup) {
					toolBarOption.addPropertyGroupOption = true;
					toolBarOption.updatePropertyGroupOption = true;
					toolBarOption.removePropertyGroupOption = true;
				}

				toolBarOption.addNoteOption = true;
				toolBarOption.addCategoryOption = true;
				toolBarOption.addKeywordOption = true;
				// Evaluates special case root node that must not be deleted;
				if (!isRoot) {
					toolBarOption.renameOption = true;
					toolBarOption.copyOption = true;
					toolBarOption.moveOption = true;
				}

				// Enable uploading buttons
				if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TAXONOMY
						|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TEMPLATES
						|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_PERSONAL
						|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_MAIL
						|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TRASH) {
					toolBarOption.addDocumentOption = true;
					toolBarOption.createFolderOption = true;
					toolBarOption.createFromTemplateOption = true;
				} else if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_CATEGORIES) {
					toolBarOption.createFolderOption = true;
				}
			}

			// Enable property groups
			boolean getGroups = false;
			if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_THESAURUS
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_CATEGORIES
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_METADATA) {
				// Evaluate real parent folder
				if (!evaluateGroup) {
					getGroups = ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE);
				} else {
					getGroups = ((folder.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP);
				}
			} else {
				// Evalute real parent folder
				if (!evaluateGroup) {
					getGroups = ((folderParent.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)
							&& ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE);
				} else {
					getGroups = ((folder.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP)
							&& ((folderParent.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP);
				}
			}
			if (getGroups) {
				// Always enable it ( not controls button, only boolean value )
				toolBarOption.removePropertyGroupOption = true;
				// Evaluates enable or disable property group buttons
				if (evaluateGroup) {
					if ((folder.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP) {
						getAllGroups();
					}
				} else {
					getAllGroups();
				}
			}

			// Disables add document, delete and create directory from thesaurus
			// view
			if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_THESAURUS
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_CATEGORIES
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_METADATA) {
				toolBarOption.addSubscription = false;

				if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_THESAURUS
						|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_METADATA) {
					toolBarOption.deleteOption = false;
				}
			}

			// The remove property group is special case depends on tab property
			// enabled, with this call we force to set
			// false
			evaluateRemovePropertyGroup(false);

			if (Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace() == UIDockPanelConstants.DESKTOP) {
				toolBarOption.refreshOption = true;
				toolBarOption.homeOption = true;
				toolBarOption.bookmarkOption = true;
				toolBarOption.goOption = true;
				toolBarOption.splitterResizeOption = true;
			} else if (Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace() == UIDockPanelConstants.DASHBOARD) {
				toolBarOption.refreshOption = true;
			}

			// Disable move & copy option in categories case
			if ((Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_CATEGORIES)) {
				if (originPanel == OriginPanel.FILE_BROWSER) {
					toolBarOption.moveOption = false;
					toolBarOption.copyOption = false;
				} else if (originPanel == OriginPanel.TREE_ROOT) {
					toolBarOption.copyOption = false;
					toolBarOption.addCategoryOption = false;
					toolBarOption.addKeywordOption = false;
				}
			}
			// Disable move & copy option in metadata case
			if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_METADATA) {
				toolBarOption.moveOption = false;
				toolBarOption.copyOption = false;
			}
		} else if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TRASH) {
			toolBarOption.purgeTrash = true;

			if (isRoot) {
				toolBarOption.purge = false;
				toolBarOption.restore = false;
			} else {
				toolBarOption.purge = true;
				toolBarOption.restore = true;
			}

		}

		// Checking extension button
		for (Iterator<ToolBarButtonExtension> it = widgetExtensionList.iterator(); it.hasNext(); ) {
			it.next().checkPermissions(folder, folderParent, originPanel);
		}

		fireEvent(HasToolBarEvent.EXECUTE_CHECK_FOLDER_PERMISSION);

		// Sets the permission to menus
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			enableMassiveView();
		} else {
			propagateToolBarOptions();
			evaluateShowIcons();
		}
	}

	/**
	 * Checks permissions associated to document and tool button enabled actions
	 *
	 * @param doc The document
	 */
	public void checkToolButtonPermissions(GWTDocument doc, GWTFolder folder) {
		node = doc; // saves last done evaluated
		this.massiveObj1 = folder;
		this.massiveOriginPanel = 0;

		// Disable all menu options
		disableAllOptions();

		// Only if toolbar is enabled must change tools icons values
		if (isEnabled()) {
			// Enable quick search
			if (Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace() == UIDockPanelConstants.DESKTOP
					&& (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TAXONOMY
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TEMPLATES
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_PERSONAL
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_MAIL || Main.get().mainPanel.desktop.navigator
					.getStackIndex() == UIDesktopConstants.NAVIGATOR_TRASH)) {
				toolBarOption.findOption = true;
				toolBarOption.findFolderOption = true;
				toolBarOption.findDocumentOption = true;
				toolBarOption.findSimilarDocumentOption = true;
			}

			boolean disable = false;
			String user = Main.get().workspaceUserProperties.getUser().getId();

			if (evaluateDownload && ((doc.getPermissions() & GWTPermission.DOWNLOAD) == GWTPermission.DOWNLOAD)) {
				toolBarOption.downloadOption = true;
			} else if (!evaluateDownload) {
				toolBarOption.downloadOption = true;
			}

			if (evaluateWorkflow && ((doc.getPermissions() & GWTPermission.START_WORKFLOW) == GWTPermission.START_WORKFLOW)) {
				toolBarOption.workflowOption = true;
			} else if (Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_THESAURUS
					&& Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_CATEGORIES
					&& Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_METADATA) {
				toolBarOption.workflowOption = true;
			}

			if (evaluateGroup && ((folder.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP)
					&& ((folder.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP)) {
				toolBarOption.addPropertyGroupOption = true;
				toolBarOption.updatePropertyGroupOption = true;
				toolBarOption.removePropertyGroupOption = true;
			}

			if (toolBarOption.downloadOption && doc.isConvertibleToPdf()) {
				toolBarOption.downloadPdfOption = true;
				toolBarOption.printOption = true;
			} else {
				toolBarOption.downloadPdfOption = false;
				if (doc.getMimeType().equals("application/pdf")) {
					toolBarOption.printOption = true; // pdf files are printable
				}
			}

			// Checking delete permissions
			if (((doc.getPermissions() & GWTPermission.DELETE) == GWTPermission.DELETE)
					&& ((folder.getPermissions() & GWTPermission.DELETE) == GWTPermission.DELETE) && !doc.isCheckedOut() && !doc.isLocked()
					&& Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_THESAURUS
					&& Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_CATEGORIES
					&& Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_METADATA) {
				toolBarOption.deleteOption = true;
			}

			// Enable related with creation new documents
			if (((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)) {
				if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TAXONOMY
						|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TEMPLATES
						|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_PERSONAL) {
					toolBarOption.addDocumentOption = true;
					toolBarOption.createFolderOption = true;

				} else if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_CATEGORIES
						|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_MAIL) {
					toolBarOption.createFolderOption = true;
				}

				// Conversion
				if (doc.isConvertibleToPdf()
						&& (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TAXONOMY
						|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TEMPLATES
						|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_PERSONAL || Main
						.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_MAIL)) {
					toolBarOption.convertOption = true;
				}
			}

			if ((doc.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
				if (!doc.isCheckedOut() && !doc.isLocked()) {
					toolBarOption.checkoutOption = true;
					toolBarOption.lockOption = true;
					toolBarOption.checkinOption = false;
					toolBarOption.cancelCheckoutOption = false;
					toolBarOption.unLockOption = false;
					toolBarOption.addNoteOption = true;
					toolBarOption.addCategoryOption = true;
					toolBarOption.addKeywordOption = true;

					if (doc.isSubscribed()) {
						toolBarOption.removeSubscription = true;
					} else if (!doc.isLocked()) {
						toolBarOption.addSubscription = true;
					}
					
					if (doc.getMimeType().startsWith("image/png")) {
						toolBarOption.omrOption = true;
					}
				} else {
					if (doc.isCheckedOut()) {
						if (doc.getLockInfo().getOwner().equals(user) || Main.get().workspaceUserProperties.getWorkspace().isAdminRole()) {
							if (doc.getLockInfo().getOwner().equals(user)) {
								toolBarOption.checkinOption = true;
								if (!evaluateGroup) {
									toolBarOption.addPropertyGroupOption = true;
									toolBarOption.updatePropertyGroupOption = true;
									toolBarOption.removePropertyGroupOption = true;
								} else if (((folder.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP)
										&& ((doc.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP)) {
									toolBarOption.addPropertyGroupOption = true;
									toolBarOption.updatePropertyGroupOption = true;
									toolBarOption.removePropertyGroupOption = true;
								}
								
								if (doc.getMimeType().equals("image/png")) {
									toolBarOption.omrOption = true;
								}

								toolBarOption.addNoteOption = true;
								toolBarOption.addCategoryOption = true;
								toolBarOption.addKeywordOption = true;
							} else {
								toolBarOption.checkinOption = false;
								toolBarOption.addPropertyGroupOption = false;
								toolBarOption.updatePropertyGroupOption = false;
								toolBarOption.removePropertyGroupOption = false;
								toolBarOption.addNoteOption = false;
								toolBarOption.addCategoryOption = false;
								toolBarOption.addKeywordOption = false;
							}

							toolBarOption.cancelCheckoutOption = true;
							toolBarOption.checkoutOption = false;
							toolBarOption.lockOption = false;
							toolBarOption.unLockOption = false;
						} else {
							disable = true;
						}
					} else {
						if (doc.getLockInfo().getOwner().equals(user) || Main.get().workspaceUserProperties.getWorkspace().isAdminRole()) {
							toolBarOption.unLockOption = true;
							toolBarOption.checkinOption = false;
							toolBarOption.cancelCheckoutOption = false;
							toolBarOption.checkoutOption = false;
							toolBarOption.lockOption = false;

							if (!evaluateGroup) {
								toolBarOption.addPropertyGroupOption = true;
								toolBarOption.updatePropertyGroupOption = true;
								toolBarOption.removePropertyGroupOption = true;
							} else if (((folder.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP)
									&& ((doc.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP)) {
								toolBarOption.addPropertyGroupOption = true;
								toolBarOption.updatePropertyGroupOption = true;
								toolBarOption.removePropertyGroupOption = true;
							}

							if (doc.getMimeType().equals("image/png")) {
								toolBarOption.omrOption = true;
							}
							
							toolBarOption.addNoteOption = true;
							toolBarOption.addCategoryOption = true;
							toolBarOption.addKeywordOption = false;

							if (doc.isSubscribed()) {
								toolBarOption.removeSubscription = true;
							} else if (!doc.isLocked()) {
								toolBarOption.addSubscription = true;
							}
						} else {
							disable = true;
						}
					}
				}
			} else {
				disable = true;
			}

			// Enable property groups
			boolean getGroups = false;
			if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_THESAURUS
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_CATEGORIES
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_METADATA) {
				// Evaluate real parent folder
				if (!evaluateGroup) {
					getGroups = ((doc.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE);
				} else {
					getGroups = ((doc.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP);
				}
			} else {
				// Evalute real parent folder
				if (!evaluateGroup) {
					getGroups = ((doc.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)
							&& ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE);
				} else {
					getGroups = ((folder.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP)
							&& ((doc.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP);
				}
			}
			if (getGroups) {
				toolBarOption.renameOption = true;
				toolBarOption.copyOption = true;
				toolBarOption.moveOption = true;
				// Always enable it ( not controls button, only boolean value )
				toolBarOption.removePropertyGroupOption = true;
				// Evaluates enable or disable property group buttons
				if (evaluateGroup) {
					if ((folder.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP) {
						getAllGroups();
					}
				} else {
					getAllGroups();
				}
			}

			if (disable) {
				toolBarOption.lockOption = false;
				toolBarOption.unLockOption = false;
				toolBarOption.checkoutOption = false;
				toolBarOption.checkinOption = false;
				toolBarOption.cancelCheckoutOption = false;
				toolBarOption.addPropertyGroupOption = false;
				toolBarOption.updatePropertyGroupOption = false;
				toolBarOption.removePropertyGroupOption = false;
			}

			// If not personal and trash can send mail
			if (Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_PERSONAL
					&& Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_TRASH) {
				toolBarOption.sendDocumentLinkOption = true;
				toolBarOption.sendDocumentAttachmentOption = true;
			}

			// Create from template
			if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TEMPLATES) {
				toolBarOption.createFromTemplateOption = true;
			} else if ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE
					&& (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TAXONOMY
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TEMPLATES
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_PERSONAL
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_MAIL || Main.get().mainPanel.desktop.navigator
					.getStackIndex() == UIDesktopConstants.NAVIGATOR_TRASH)) {
				toolBarOption.createFromTemplateOption = true;
			}

			if (Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace() == UIDockPanelConstants.DESKTOP) {
				toolBarOption.refreshOption = true;
				toolBarOption.homeOption = true;
				toolBarOption.bookmarkOption = true;
				toolBarOption.goOption = true;
				toolBarOption.splitterResizeOption = true;
			} else if (Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace() == UIDockPanelConstants.DASHBOARD) {
				toolBarOption.refreshOption = true;
			}

			// Disable move & copy option in categories and metadata case
			if ((Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_CATEGORIES || Main.get().mainPanel.desktop.navigator
					.getStackIndex() == UIDesktopConstants.NAVIGATOR_METADATA)) {
				toolBarOption.moveOption = false;
				toolBarOption.copyOption = false;
			}
		} else if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TRASH) {
			toolBarOption.purgeTrash = true;
			toolBarOption.purge = true;
			toolBarOption.restore = true;
		}

		// Checking extension button
		for (Iterator<ToolBarButtonExtension> it = widgetExtensionList.iterator(); it.hasNext(); ) {
			it.next().checkPermissions(doc, folder);
		}

		fireEvent(HasToolBarEvent.EXECUTE_CHECK_DOCUMENT_PERMISSION);

		// Sets the permission to menus
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			enableMassiveView();
		} else {
			propagateToolBarOptions();
			evaluateShowIcons();
		}
	}

	/**
	 * Checks permissions associated to document and tool button enabled actions
	 *
	 * @param mail The Mail
	 */
	public void checkToolButtonPermissions(GWTMail mail, GWTFolder folder) {
		node = mail; // saves last done evaluated
		this.massiveObj1 = folder;
		this.massiveOriginPanel = 0;

		// Disable all menu options
		disableAllOptions();

		// Only if toolbar is enabled must change tools icons values
		if (isEnabled()) {
			toolBarOption.findSimilarDocumentOption = false;

			// Enable quick search
			if (Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace() == UIDockPanelConstants.DESKTOP
					&& (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TAXONOMY
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TEMPLATES
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_PERSONAL
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_MAIL || Main.get().mainPanel.desktop.navigator
					.getStackIndex() == UIDesktopConstants.NAVIGATOR_TRASH)) {
				toolBarOption.findOption = true;
				toolBarOption.findFolderOption = true;
				toolBarOption.findDocumentOption = true;
			}

			if (evaluateDownload && ((mail.getPermissions() & GWTPermission.DOWNLOAD) == GWTPermission.DOWNLOAD)) {
				toolBarOption.downloadOption = true;
			} else if (!evaluateDownload) {
				toolBarOption.downloadOption = true;
			}

			if (((mail.getPermissions() & GWTPermission.DELETE) == GWTPermission.DELETE)
					&& ((folder.getPermissions() & GWTPermission.DELETE) == GWTPermission.DELETE)) {
				toolBarOption.deleteOption = true;
			}

			if (evaluateGroup && ((folder.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP)
					&& ((folder.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP)) {
				toolBarOption.addPropertyGroupOption = true;
				toolBarOption.updatePropertyGroupOption = true;
				toolBarOption.removePropertyGroupOption = true;
			}

			if (evaluateWorkflow && ((mail.getPermissions() & GWTPermission.START_WORKFLOW) == GWTPermission.START_WORKFLOW)) {
				toolBarOption.workflowOption = true;
			} else if (Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_THESAURUS
					&& Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_CATEGORIES
					&& Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_METADATA) {
				toolBarOption.workflowOption = true;
			}

			// Enable uploading buttons
			if (((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)) {
				if ((Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TAXONOMY
						|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TEMPLATES || Main.get().mainPanel.desktop.navigator
						.getStackIndex() == UIDesktopConstants.NAVIGATOR_PERSONAL)) {
					toolBarOption.addDocumentOption = true;
					toolBarOption.createFolderOption = true;
					toolBarOption.createFromTemplateOption = true;
				} else if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_CATEGORIES
						|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_MAIL) {
					toolBarOption.createFolderOption = true;
				}
			}

			if (((mail.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)) {
				toolBarOption.addNoteOption = true;
				toolBarOption.addCategoryOption = true;
				toolBarOption.addKeywordOption = true;

				if ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
					toolBarOption.renameOption = true;
					toolBarOption.copyOption = true;
					toolBarOption.moveOption = true;
					if (!evaluateGroup) {
						toolBarOption.addPropertyGroupOption = true;
						toolBarOption.updatePropertyGroupOption = true;
						toolBarOption.removePropertyGroupOption = true;
					}

					// On mail panel is not able to uploading files
					if (Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_MAIL) {
						toolBarOption.addDocumentOption = true;
					}
				}
			}

			// Enable property groups
			boolean getGroups = false;
			if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_THESAURUS
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_CATEGORIES
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_METADATA) {
				// Evaluate real parent folder
				if (!evaluateGroup) {
					getGroups = ((mail.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE);
				} else {
					getGroups = ((mail.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP);
				}
			} else {
				// Evalute real parent folder
				if (!evaluateGroup) {
					getGroups = ((mail.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)
							&& ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE);
				} else {
					getGroups = ((folder.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP)
							&& ((mail.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP);
				}
			}
			if (getGroups) {
				// Always enable it ( not controls button, only boolean value )
				toolBarOption.removePropertyGroupOption = true;
				// Evaluates enable or disable property group buttons
				if (evaluateGroup) {
					if ((folder.getPermissions() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP) {
						getAllGroups();
					}
				} else {
					getAllGroups();
				}
			}

			// Always allo forward mail
			toolBarOption.mailForwardOption = true;

			// Alwasys disable add subscription ( mail has not version )
			toolBarOption.addSubscription = false;
			toolBarOption.removeSubscription = false;

			if (Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace() == UIDockPanelConstants.DESKTOP) {
				toolBarOption.refreshOption = true;
				toolBarOption.homeOption = true;
				toolBarOption.bookmarkOption = true;
				toolBarOption.goOption = true;
				toolBarOption.splitterResizeOption = true;
			} else if (Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace() == UIDockPanelConstants.DASHBOARD) {
				toolBarOption.refreshOption = true;
			}

			// Disable move & copy option in categories and metadata case
			if ((Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_CATEGORIES || Main.get().mainPanel.desktop.navigator
					.getStackIndex() == UIDesktopConstants.NAVIGATOR_METADATA)) {
				toolBarOption.moveOption = false;
				toolBarOption.copyOption = false;
			}
		} else if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_TRASH) {
			toolBarOption.purgeTrash = true;
			toolBarOption.purge = true;
			toolBarOption.restore = true;
		}

		// Checking extension button
		for (Iterator<ToolBarButtonExtension> it = widgetExtensionList.iterator(); it.hasNext(); ) {
			it.next().checkPermissions(mail, folder);
		}

		fireEvent(HasToolBarEvent.EXECUTE_CHECK_MAIL_PERMISSION);

		// Sets the permission to menus
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			enableMassiveView();
		} else {
			propagateToolBarOptions();
			evaluateShowIcons();
		}
	}

	/**
	 * enableMassiveView
	 */
	public void enableMassiveView() {
		// Disable
		disableAllOptions();

		// Enable massive options
		massiveOptions = true;

		// Enable// Enable
		if (Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_TRASH) {
			toolBarOption.addCategoryOption = true;
			toolBarOption.addKeywordOption = true;
			toolBarOption.copyOption = true;
			toolBarOption.moveOption = true;
			toolBarOption.deleteOption = true;
			boolean hasDocuments = Main.get().mainPanel.desktop.browser.fileBrowser.table.getAllSelectedDocumentsUUIDs().size() > 0;
			toolBarOption.lockOption = hasDocuments;
			toolBarOption.unLockOption = hasDocuments;
			toolBarOption.downloadOption = hasDocuments;
			toolBarOption.sendDocumentAttachmentOption = hasDocuments;
			toolBarOption.sendDocumentLinkOption = hasDocuments;
			toolBarOption.mailForwardOption = Main.get().mainPanel.desktop.browser.fileBrowser.table.getAllSelectedMailUUIDs().size() > 0;
			toolBarOption.addPropertyGroupOption = true;
			toolBarOption.updatePropertyGroupOption = true;

			// Merge pdf
			if ((Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_THESAURUS
					&& Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_CATEGORIES
					&& Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_METADATA && Main.get().mainPanel.desktop.navigator
					.getStackIndex() != UIDesktopConstants.NAVIGATOR_TRASH)
					&& Main.get().mainPanel.desktop.browser.fileBrowser.getAllSelectedPdfDocuments().size() > 1) {
				toolBarOption.mergePdfOption = true;
			}
		}

		propagateToolBarOptions();
		evaluateShowIcons();
	}

	/**
	 * disableMassiveView
	 */
	public void disableMassiveView() {
		if (massiveOptions) {
			if (node instanceof GWTFolder) {
				checkToolButtonPermissions((GWTFolder) node, (GWTFolder) massiveObj1, massiveOriginPanel);
			} else if (node instanceof GWTDocument) {
				checkToolButtonPermissions((GWTDocument) node, (GWTFolder) massiveObj1);
			} else if (node instanceof GWTMail) {
				checkToolButtonPermissions((GWTMail) node, (GWTFolder) massiveObj1);
			}

			massiveOptions = false;
		}
	}

	/**
	 * Indicates if toolBar is enabled
	 *
	 * @return The value of enabled / disabled toolbar
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Enables create folder
	 */
	public void enableCreateFolder() {
		toolBarOption.createFolderOption = true;
		createFolder.setStyleName("okm-ToolBar-button");
		createFolder.setResource(OKMBundleResources.INSTANCE.createFolder());
		createFolder.setTitle(Main.i18n("tree.menu.directory.create"));
	}

	/**
	 * Disables create folder
	 */
	public void disableCreateFolder() {
		toolBarOption.createFolderOption = false;
		createFolder.setStyleName("okm-ToolBar-button-disabled");
		createFolder.setResource(OKMBundleResources.INSTANCE.createFolderDisabled());
		createFolder.setTitle(Main.i18n("tree.menu.directory.create"));
	}

	/**
	 * Enables create folder
	 */
	public void enableFind() {
		toolBarOption.findOption = true;
		find.setStyleName("okm-ToolBar-button");
		find.setResource(OKMBundleResources.INSTANCE.find());
		find.setTitle(Main.i18n("general.menu.find"));
	}

	/**
	 * Disables create folder
	 */
	public void disableFind() {
		toolBarOption.findOption = false;
		find.setStyleName("okm-ToolBar-button-disabled");
		find.setResource(OKMBundleResources.INSTANCE.findDisabled());
		find.setTitle(Main.i18n("general.menu.find"));
	}

	/**
	 * Enables edit button
	 */
	public void enableCheckout() {
		toolBarOption.checkoutOption = true;
		checkout.setStyleName("okm-ToolBar-button");
		checkout.setResource(OKMBundleResources.INSTANCE.checkout());
		checkout.setTitle(Main.i18n("general.menu.edit.checkout"));
	}

	/**
	 * Disables edit button
	 */
	public void disableCheckout() {
		toolBarOption.checkoutOption = false;
		checkout.setStyleName("okm-ToolBar-button-disabled");
		checkout.setResource(OKMBundleResources.INSTANCE.checkoutDisabled());
		checkout.setTitle(Main.i18n("general.menu.edit.checkout"));
	}

	/**
	 * Enables checkin button
	 */
	public void enableCheckin() {
		toolBarOption.checkinOption = true;
		checkin.setStyleName("okm-ToolBar-button");
		checkin.setResource(OKMBundleResources.INSTANCE.checkin());
		checkin.setTitle(Main.i18n("general.menu.edit.checkin"));
	}

	/**
	 * Disables checkin button
	 */
	public void disableCheckin() {
		toolBarOption.checkinOption = false;
		checkin.setStyleName("okm-ToolBar-button-disabled");
		checkin.setResource(OKMBundleResources.INSTANCE.checkinDisabled());
		checkin.setTitle(Main.i18n("general.menu.edit.checkin"));
	}

	/**
	 * Enables checkout cancel button
	 */
	public void enableCancelCheckout() {
		toolBarOption.cancelCheckoutOption = true;
		cancelCheckout.setStyleName("okm-ToolBar-button");
		cancelCheckout.setResource(OKMBundleResources.INSTANCE.cancelCheckout());
		cancelCheckout.setTitle(Main.i18n("general.menu.edit.cancel.checkout"));
	}

	/**
	 * Disables checkout cancel button
	 */
	public void disableCancelCheckout() {
		toolBarOption.cancelCheckoutOption = false;
		cancelCheckout.setStyleName("okm-ToolBar-button-disabled");
		cancelCheckout.setResource(OKMBundleResources.INSTANCE.cancelCheckoutDisabled());
		cancelCheckout.setTitle(Main.i18n("general.menu.edit.cancel.checkout"));
	}

	/**
	 * Disables lock button
	 */
	public void disableLock() {
		toolBarOption.lockOption = false;
		lock.setStyleName("okm-ToolBar-button-disabled");
		lock.setResource(OKMBundleResources.INSTANCE.lockDisabled());
		lock.setTitle(Main.i18n("general.menu.edit.lock"));
	}

	/**
	 * Enables lock button
	 */
	public void enableLock() {
		toolBarOption.lockOption = true;
		lock.setStyleName("okm-ToolBar-button");
		lock.setResource(OKMBundleResources.INSTANCE.lock());
		lock.setTitle(Main.i18n("general.menu.edit.lock"));
	}

	/**
	 * Disables delete lock button
	 */
	public void disableUnlock() {
		toolBarOption.unLockOption = false;
		unlock.setStyleName("okm-ToolBar-button-disabled");
		unlock.setResource(OKMBundleResources.INSTANCE.unLockDisabled());
		unlock.setTitle(Main.i18n("general.menu.edit.unlock"));
	}

	/**
	 * Enables delete lock button
	 */
	public void enableUnlock() {
		toolBarOption.unLockOption = true;
		unlock.setStyleName("okm-ToolBar-button");
		unlock.setResource(OKMBundleResources.INSTANCE.unLock());
		unlock.setTitle(Main.i18n("general.menu.edit.unlock"));
	}

	/**
	 * Disables download button
	 */
	public void disableDownload() {
		toolBarOption.downloadOption = false;
		download.setStyleName("okm-ToolBar-button-disabled");
		download.setResource(OKMBundleResources.INSTANCE.downloadDisabled());
		download.setTitle(Main.i18n("general.menu.file.download.document"));
	}

	/**
	 * Enables download button
	 */
	public void enableDownload() {
		toolBarOption.downloadOption = true;
		download.setStyleName("okm-ToolBar-button");
		download.setResource(OKMBundleResources.INSTANCE.download());
		download.setTitle(Main.i18n("general.menu.file.download.document"));
	}

	/**
	 * Disables download as PDF button
	 */
	public void disableDownloadPdf() {
		toolBarOption.downloadPdfOption = false;
		downloadPdf.setStyleName("okm-ToolBar-button-disabled");
		downloadPdf.setResource(OKMBundleResources.INSTANCE.downloadPdfDisabled());
		downloadPdf.setTitle(Main.i18n("general.menu.file.download.document.pdf"));
	}

	/**
	 * Enables download as PDF button
	 */
	public void enableDownloadPdf() {
		toolBarOption.downloadPdfOption = true;
		downloadPdf.setStyleName("okm-ToolBar-button");
		downloadPdf.setResource(OKMBundleResources.INSTANCE.downloadPdf());
		downloadPdf.setTitle(Main.i18n("general.menu.file.download.document.pdf"));
	}

	/**
	 * Disables print button
	 */
	public void disablePrint() {
		toolBarOption.printOption = false;
		print.setStyleName("okm-ToolBar-button-disabled");
		print.setResource(OKMBundleResources.INSTANCE.printDisabled());
		print.setTitle(Main.i18n("general.menu.file.print"));
	}

	/**
	 * Enables print button
	 */
	public void enablePrint() {
		toolBarOption.printOption = true;
		print.setStyleName("okm-ToolBar-button");
		print.setResource(OKMBundleResources.INSTANCE.print());
		print.setTitle(Main.i18n("general.menu.file.print"));
	}

	/**
	 * Disables delete button
	 */
	public void disableDelete() {
		toolBarOption.deleteOption = false;
		delete.setStyleName("okm-ToolBar-button-disabled");
		delete.setResource(OKMBundleResources.INSTANCE.deleteDisabled());
		delete.setTitle(Main.i18n("general.menu.edit.delete"));
	}

	/**
	 * Enables delete button
	 */
	public void enableDelete() {
		toolBarOption.deleteOption = true;
		delete.setStyleName("okm-ToolBar-button");
		delete.setResource(OKMBundleResources.INSTANCE.delete());
		delete.setTitle(Main.i18n("general.menu.edit.delete"));
	}

	/**
	 * Disable arrow rotate clockwise
	 */
	public void disableRefresh() {
		toolBarOption.refreshOption = false;
		refresh.setStyleName("okm-ToolBar-button-disabled");
		refresh.setResource(OKMBundleResources.INSTANCE.refreshDisabled());
		refresh.setTitle(Main.i18n("general.menu.file.refresh"));
	}

	/**
	 * Enables Rotate ClockWise Arrow
	 */
	public void enableRefresh() {
		toolBarOption.refreshOption = true;
		refresh.setStyleName("okm-ToolBar-button");
		refresh.setResource(OKMBundleResources.INSTANCE.refresh());
		refresh.setTitle(Main.i18n("general.menu.file.refresh"));
	}

	/**
	 * Disables add document
	 */
	public void disableAddDocument() {
		toolBarOption.addDocumentOption = false;
		addDocument.setStyleName("okm-ToolBar-button-disabled");
		addDocument.setResource(OKMBundleResources.INSTANCE.addDocumentDisabled());
		addDocument.setTitle(Main.i18n("general.menu.file.add.document"));
	}

	/**
	 * Enables add document
	 */
	public void enableAddDocument() {
		toolBarOption.addDocumentOption = true;
		addDocument.setStyleName("okm-ToolBar-button");
		addDocument.setResource(OKMBundleResources.INSTANCE.addDocument());
		addDocument.setTitle(Main.i18n("general.menu.file.add.document"));
	}

	/**
	 * Disables add property group
	 */
	public void disableAddPropertyGroup() {
		if (!Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			Main.get().mainPanel.topPanel.mainMenu.disableAddPropertyGroup();
			Main.get().mainPanel.desktop.browser.fileBrowser.disableAddPropertyGroup();
		}
		toolBarOption.addPropertyGroupOption = false;
		addPropertyGroup.setStyleName("okm-ToolBar-button-disabled");
		addPropertyGroup.setResource(OKMBundleResources.INSTANCE.addPropertyGroupDisabled());
		addPropertyGroup.setTitle(Main.i18n("general.menu.edit.add.property.group"));
	}

	/**
	 * Enables add property group
	 */
	public void enableAddPropertyGroup() {
		if (!Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			Main.get().mainPanel.topPanel.mainMenu.enableAddPropertyGroup();
			Main.get().mainPanel.desktop.browser.fileBrowser.enableAddPropertyGroup();
		}
		toolBarOption.addPropertyGroupOption = true;
		addPropertyGroup.setStyleName("okm-ToolBar-button");
		addPropertyGroup.setResource(OKMBundleResources.INSTANCE.addPropertyGroup());
		addPropertyGroup.setTitle(Main.i18n("general.menu.edit.add.property.group"));
	}

	/**
	 * Enables update property group
	 */
	public void enableUpdatePropertyGroup() {
		Main.get().mainPanel.topPanel.mainMenu.enableUpdatePropertyGroup();
		toolBarOption.updatePropertyGroupOption = true;
	}

	/**
	 * enablePdfMerge
	 */
	public void enablePdfMerge() {
		toolBarOption.mergePdfOption = true;
		Main.get().mainPanel.topPanel.mainMenu.enablePdfMerge();
		Main.get().mainPanel.desktop.browser.fileBrowser.enablePdfMerge();
	}

	/**
	 * disablePdfMerge
	 */
	public void disablePdfMerge() {
		toolBarOption.mergePdfOption = false;
		Main.get().mainPanel.topPanel.mainMenu.disablePdfMerge();
		Main.get().mainPanel.desktop.browser.fileBrowser.disablePdfMerge();
	}

	/**
	 * Disables add subscription
	 */
	public void disableAddSubscription() {
		toolBarOption.addSubscription = false;
		addSubscription.setStyleName("okm-ToolBar-button-disabled");
		addSubscription.setResource(OKMBundleResources.INSTANCE.addSubscriptionDisabled());
		addSubscription.setTitle(Main.i18n("general.menu.edit.add.subscription"));
	}

	/**
	 * Enables add subscription
	 */
	public void enableAddSubscription() {
		toolBarOption.addSubscription = true;
		addSubscription.setStyleName("okm-ToolBar-button");
		addSubscription.setResource(OKMBundleResources.INSTANCE.addSubscription());
		addSubscription.setTitle(Main.i18n("general.menu.edit.add.subscription"));
	}

	/**
	 * Disables remove subscription
	 */
	public void disableRemoveSubscription() {
		toolBarOption.removeSubscription = false;
		removeSubscription.setStyleName("okm-ToolBar-button-disabled");
		removeSubscription.setResource(OKMBundleResources.INSTANCE.removeSubscriptionDisabled());
		removeSubscription.setTitle(Main.i18n("general.menu.edit.remove.subscription"));
	}

	/**
	 * Enables remove subscription
	 */
	public void enableRemoveSubscription() {
		toolBarOption.removeSubscription = true;
		removeSubscription.setStyleName("okm-ToolBar-button");
		removeSubscription.setResource(OKMBundleResources.INSTANCE.removeSubscription());
		removeSubscription.setTitle(Main.i18n("general.menu.edit.remove.subscription"));
	}

	/**
	 * Disables remove subscription
	 */
	public void disableHome() {
		toolBarOption.homeOption = false;
		home.setStyleName("okm-ToolBar-button-disabled");
		home.setResource(OKMBundleResources.INSTANCE.homeDisabled());
		home.setTitle(Main.i18n("general.menu.bookmark.home"));
	}

	/**
	 * Enables remove subscription
	 */
	public void enableHome() {
		toolBarOption.homeOption = true;
		home.setStyleName("okm-ToolBar-button");
		home.setResource(OKMBundleResources.INSTANCE.home());
		home.setTitle(Main.i18n("general.menu.bookmark.home"));
	}

	/**
	 * enableRestore
	 */
	public void enableRestore() {
		toolBarOption.restore = true;
	}

	/**
	 * disableRestore
	 */
	public void disableRestore() {
		toolBarOption.restore = false;
	}

	/**
	 * enablePurge
	 */
	public void enablePurge() {
		toolBarOption.purge = true;
	}

	/**
	 * disablePurge
	 */
	public void disablePurge() {
		toolBarOption.purge = false;
	}

	/**
	 * enablePurgeTrash
	 */
	public void enablePurgeTrash() {
		toolBarOption.purgeTrash = true;
	}

	/**
	 * disablePurgeTrash
	 */
	public void disablePurgeTrash() {
		toolBarOption.purgeTrash = false;
	}

	/**
	 * Disables workflow
	 */
	public void disableWorkflow() {
		toolBarOption.workflowOption = false;
		startWorkflow.setStyleName("okm-ToolBar-button-disabled");
		startWorkflow.setResource(OKMBundleResources.INSTANCE.startWorkflowDisabled());
		startWorkflow.setTitle(Main.i18n("general.menu.file.start.workflow"));
	}

	/**
	 * Enables workflow
	 */
	public void enableWorkflow() {
		toolBarOption.workflowOption = true;
		startWorkflow.setStyleName("okm-ToolBar-button");
		startWorkflow.setResource(OKMBundleResources.INSTANCE.startWorkflow());
		startWorkflow.setTitle(Main.i18n("general.menu.file.start.workflow"));
	}

	/**
	 * Disables splitter resize
	 */
	public void disableSplitterResize() {
		toolBarOption.splitterResizeOption = false;
		splitterResize.setStyleName("okm-ToolBar-button-disabled");
		splitterResize.setResource(OKMBundleResources.INSTANCE.splitterResizeDisabled());
		splitterResize.setTitle(Main.i18n("general.menu.splitter.resize"));
	}

	/**
	 * Enables splitter resize
	 */
	public void enableSplitterResize() {
		toolBarOption.splitterResizeOption = true;
		splitterResize.setStyleName("okm-ToolBar-button");
		splitterResize.setResource(OKMBundleResources.INSTANCE.splitterResize());
		splitterResize.setTitle(Main.i18n("general.menu.splitter.resize"));
	}

	/**
	 * Disables omr
	 */
	public void disableOmr() {
		toolBarOption.omrOption = false;
		omr.setStyleName("okm-ToolBar-button-disabled");
		omr.setResource(OKMBundleResources.INSTANCE.omrDisabled());
		omr.setTitle(Main.i18n("general.menu.file.omr"));
	}
	
	/**
	 * Enables omr
	 */
	public void enableOmr() {
		toolBarOption.omrOption = true;
		omr.setStyleName("okm-ToolBar-button");
		omr.setResource(OKMBundleResources.INSTANCE.omr());
		omr.setTitle(Main.i18n("general.menu.file.omr"));
	}
	
	/**
	 * Disables fired property group
	 */
	public void disableFiredRemovePropertyGroup() {
		toolBarOption.firedRemovePropertyGroupOption = false;
	}

	/**
	 * Enables fired property group
	 */
	public void enableFiredRemovePropertyGroup() {
		toolBarOption.firedRemovePropertyGroupOption = true;
	}

	/**
	 * Enables rename
	 */
	public void enableRename() {
		toolBarOption.renameOption = true;
	}

	/**
	 * Disable rename
	 */
	public void disableRename() {
		toolBarOption.renameOption = false;
	}

	/**
	 * Enables copy
	 */
	public void enableCopy() {
		toolBarOption.copyOption = true;
	}

	/**
	 * Disable copy
	 */
	public void disableCopy() {
		toolBarOption.copyOption = false;
	}

	/**
	 * Enables move
	 */
	public void enableMove() {
		toolBarOption.moveOption = true;
	}

	/**
	 * Disable move
	 */
	public void disableMove() {
		toolBarOption.moveOption = false;
	}

	/**
	 * Enables export
	 */
	public void enableExport() {
		toolBarOption.exportOption = true;
	}

	/**
	 * Disable export
	 */
	public void disableExport() {
		toolBarOption.exportOption = false;
	}

	/**
	 * Enables add note
	 */
	public void enableAddNote() {
		toolBarOption.addNoteOption = true;
	}

	/**
	 * Disable add note
	 */
	public void disableAddNote() {
		toolBarOption.addNoteOption = false;
	}

	/**
	 * Only used on development to testing purposes
	 */
	/*
	 * private void enableAllToolBarForTestingPurpose() {
	 * enableCreateDirectory(); enableFindFolder(); enableAddDocument();
	 * enableCheckout(); enableCheckin(); enableCancelCheckout(); enableLock();
	 * enableUnlock(); enableDownload(); enableDownloadPdf(); enableDelete();
	 * enableAddPropertyGroup(); enableRemovePropertyGroup();
	 * enableAddSubscription(); enableRemoveSubscription();
	 * enableFiredRemovePropertyGroup(); enableHome(); enableRefresh();
	 * enableRename(); enableCopy(); enableMove(); enableExport();
	 * enableWorkflow(); enableAddNote(); enableScanner(); enableUploader(); }
	 */

	/**
	 * Gets the default Tool Bar object values for root
	 *
	 * @return The default toolBarOption for init root
	 */
	public ToolBarOption getDefaultRootToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();

		tmpToolBarOption.createFolderOption = true;
		tmpToolBarOption.findOption = true;
		tmpToolBarOption.findFolderOption = true;
		tmpToolBarOption.findDocumentOption = true;
		tmpToolBarOption.findSimilarDocumentOption = false;
		tmpToolBarOption.addDocumentOption = true;
		tmpToolBarOption.checkoutOption = false;
		tmpToolBarOption.checkinOption = false;
		tmpToolBarOption.cancelCheckoutOption = false;
		tmpToolBarOption.lockOption = false;
		tmpToolBarOption.unLockOption = false;
		tmpToolBarOption.downloadOption = false;
		tmpToolBarOption.downloadPdfOption = false;
		tmpToolBarOption.printOption = false;
		tmpToolBarOption.deleteOption = false;
		tmpToolBarOption.addPropertyGroupOption = false;
		tmpToolBarOption.updatePropertyGroupOption = false;
		tmpToolBarOption.removePropertyGroupOption = false;
		tmpToolBarOption.addSubscription = false;
		tmpToolBarOption.removeSubscription = false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption = true;
		tmpToolBarOption.refreshOption = true;
		tmpToolBarOption.renameOption = false;
		tmpToolBarOption.copyOption = false;
		tmpToolBarOption.sendDocumentLinkOption = false;
		tmpToolBarOption.sendDocumentAttachmentOption = false;
		tmpToolBarOption.mailForwardOption = false;
		tmpToolBarOption.moveOption = false;
		tmpToolBarOption.exportOption = false;
		tmpToolBarOption.workflowOption = false;
		tmpToolBarOption.addNoteOption = false;
		tmpToolBarOption.splitterResizeOption = true;
		tmpToolBarOption.bookmarkOption = true;
		tmpToolBarOption.goOption = false;
		tmpToolBarOption.createFromTemplateOption = false;
		tmpToolBarOption.restore = false;
		tmpToolBarOption.purge = false;
		tmpToolBarOption.purgeTrash = true;
		tmpToolBarOption.mergePdfOption = false;
		tmpToolBarOption.omrOption = false;
		tmpToolBarOption.convertOption = false;

		return tmpToolBarOption;
	}

	/**
	 * Gets the default Tool Bar object values for categories
	 *
	 * @return The default toolBarOption for templates
	 */
	public ToolBarOption getDefaultCategoriesToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();

		tmpToolBarOption.createFolderOption = true;
		tmpToolBarOption.findOption = false;
		tmpToolBarOption.findFolderOption = false;
		tmpToolBarOption.findDocumentOption = false;
		tmpToolBarOption.findSimilarDocumentOption = false;
		tmpToolBarOption.addDocumentOption = false;
		tmpToolBarOption.checkoutOption = false;
		tmpToolBarOption.checkinOption = false;
		tmpToolBarOption.cancelCheckoutOption = false;
		tmpToolBarOption.lockOption = false;
		tmpToolBarOption.unLockOption = false;
		tmpToolBarOption.downloadOption = false;
		tmpToolBarOption.downloadPdfOption = false;
		tmpToolBarOption.printOption = false;
		tmpToolBarOption.deleteOption = false;
		tmpToolBarOption.addPropertyGroupOption = false;
		tmpToolBarOption.updatePropertyGroupOption = false;
		tmpToolBarOption.removePropertyGroupOption = false;
		tmpToolBarOption.addSubscription = false;
		tmpToolBarOption.removeSubscription = false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption = false;
		tmpToolBarOption.refreshOption = true;
		tmpToolBarOption.renameOption = false;
		tmpToolBarOption.copyOption = false;
		tmpToolBarOption.sendDocumentLinkOption = false;
		tmpToolBarOption.sendDocumentAttachmentOption = false;
		tmpToolBarOption.mailForwardOption = false;
		tmpToolBarOption.moveOption = false;
		tmpToolBarOption.exportOption = false;
		tmpToolBarOption.workflowOption = false;
		tmpToolBarOption.addNoteOption = false;
		tmpToolBarOption.splitterResizeOption = true;
		tmpToolBarOption.bookmarkOption = false;
		tmpToolBarOption.goOption = false;
		tmpToolBarOption.createFromTemplateOption = false;
		tmpToolBarOption.restore = false;
		tmpToolBarOption.purge = false;
		tmpToolBarOption.purgeTrash = true;
		tmpToolBarOption.mergePdfOption = false;
		tmpToolBarOption.omrOption = false;
		tmpToolBarOption.convertOption = false;

		return tmpToolBarOption;
	}

	/**
	 * Gets the default Tool Bar object values for thesaurus
	 *
	 * @return The default toolBarOption for templates
	 */
	public ToolBarOption getDefaultThesaurusToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();

		tmpToolBarOption.createFolderOption = false;
		tmpToolBarOption.findOption = false;
		tmpToolBarOption.findFolderOption = false;
		tmpToolBarOption.findDocumentOption = false;
		tmpToolBarOption.findSimilarDocumentOption = false;
		tmpToolBarOption.addDocumentOption = false;
		tmpToolBarOption.checkoutOption = false;
		tmpToolBarOption.checkinOption = false;
		tmpToolBarOption.cancelCheckoutOption = false;
		tmpToolBarOption.lockOption = false;
		tmpToolBarOption.unLockOption = false;
		tmpToolBarOption.downloadOption = false;
		tmpToolBarOption.downloadPdfOption = false;
		tmpToolBarOption.printOption = false;
		tmpToolBarOption.deleteOption = false;
		tmpToolBarOption.addPropertyGroupOption = false;
		tmpToolBarOption.updatePropertyGroupOption = false;
		tmpToolBarOption.removePropertyGroupOption = false;
		tmpToolBarOption.addSubscription = false;
		tmpToolBarOption.removeSubscription = false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption = false;
		tmpToolBarOption.refreshOption = true;
		tmpToolBarOption.renameOption = false;
		tmpToolBarOption.copyOption = false;
		tmpToolBarOption.sendDocumentLinkOption = false;
		tmpToolBarOption.sendDocumentAttachmentOption = false;
		tmpToolBarOption.mailForwardOption = false;
		tmpToolBarOption.moveOption = false;
		tmpToolBarOption.exportOption = false;
		tmpToolBarOption.workflowOption = false;
		tmpToolBarOption.addNoteOption = false;
		tmpToolBarOption.splitterResizeOption = true;
		tmpToolBarOption.bookmarkOption = false;
		tmpToolBarOption.goOption = false;
		tmpToolBarOption.createFromTemplateOption = false;
		tmpToolBarOption.restore = false;
		tmpToolBarOption.purge = false;
		tmpToolBarOption.purgeTrash = true;
		tmpToolBarOption.mergePdfOption = false;
		tmpToolBarOption.omrOption = false;
		tmpToolBarOption.convertOption = false;

		return tmpToolBarOption;
	}

	/**
	 * Gets the default Tool Bar object values for metadata
	 *
	 * @return The default toolBarOption for templates
	 */
	public ToolBarOption getDefaultMetadataToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();

		tmpToolBarOption.createFolderOption = false;
		tmpToolBarOption.findOption = false;
		tmpToolBarOption.findFolderOption = false;
		tmpToolBarOption.findDocumentOption = false;
		tmpToolBarOption.findSimilarDocumentOption = false;
		tmpToolBarOption.addDocumentOption = false;
		tmpToolBarOption.checkoutOption = false;
		tmpToolBarOption.checkinOption = false;
		tmpToolBarOption.cancelCheckoutOption = false;
		tmpToolBarOption.lockOption = false;
		tmpToolBarOption.unLockOption = false;
		tmpToolBarOption.downloadOption = false;
		tmpToolBarOption.downloadPdfOption = false;
		tmpToolBarOption.printOption = false;
		tmpToolBarOption.deleteOption = false;
		tmpToolBarOption.addPropertyGroupOption = false;
		tmpToolBarOption.updatePropertyGroupOption = false;
		tmpToolBarOption.removePropertyGroupOption = false;
		tmpToolBarOption.addSubscription = false;
		tmpToolBarOption.removeSubscription = false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption = false;
		tmpToolBarOption.refreshOption = true;
		tmpToolBarOption.renameOption = false;
		tmpToolBarOption.copyOption = false;
		tmpToolBarOption.sendDocumentLinkOption = false;
		tmpToolBarOption.sendDocumentAttachmentOption = false;
		tmpToolBarOption.mailForwardOption = false;
		tmpToolBarOption.moveOption = false;
		tmpToolBarOption.exportOption = false;
		tmpToolBarOption.workflowOption = false;
		tmpToolBarOption.addNoteOption = false;
		tmpToolBarOption.splitterResizeOption = true;
		tmpToolBarOption.bookmarkOption = false;
		tmpToolBarOption.goOption = false;
		tmpToolBarOption.createFromTemplateOption = false;
		tmpToolBarOption.restore = false;
		tmpToolBarOption.purge = false;
		tmpToolBarOption.purgeTrash = true;
		tmpToolBarOption.mergePdfOption = false;
		tmpToolBarOption.omrOption = false;
		tmpToolBarOption.convertOption = false;

		return tmpToolBarOption;
	}

	/**
	 * Gets the default Tool Bar object values for trash
	 *
	 * @return The default toolBarOption for trash
	 */
	public ToolBarOption getDefaultTrashToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();

		tmpToolBarOption.createFolderOption = false;
		tmpToolBarOption.findOption = true;
		tmpToolBarOption.findFolderOption = true;
		tmpToolBarOption.findDocumentOption = true;
		tmpToolBarOption.findSimilarDocumentOption = false;
		tmpToolBarOption.addDocumentOption = false;
		tmpToolBarOption.checkoutOption = false;
		tmpToolBarOption.checkinOption = false;
		tmpToolBarOption.cancelCheckoutOption = false;
		tmpToolBarOption.lockOption = false;
		tmpToolBarOption.unLockOption = false;
		tmpToolBarOption.downloadOption = false;
		tmpToolBarOption.downloadPdfOption = false;
		tmpToolBarOption.printOption = false;
		tmpToolBarOption.deleteOption = false;
		tmpToolBarOption.addPropertyGroupOption = false;
		tmpToolBarOption.updatePropertyGroupOption = false;
		tmpToolBarOption.removePropertyGroupOption = false;
		tmpToolBarOption.addSubscription = false;
		tmpToolBarOption.removeSubscription = false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption = false;
		tmpToolBarOption.refreshOption = true;
		tmpToolBarOption.renameOption = false;
		tmpToolBarOption.copyOption = false;
		tmpToolBarOption.sendDocumentLinkOption = false;
		tmpToolBarOption.sendDocumentAttachmentOption = false;
		tmpToolBarOption.mailForwardOption = false;
		tmpToolBarOption.moveOption = false;
		tmpToolBarOption.exportOption = false;
		tmpToolBarOption.workflowOption = false;
		tmpToolBarOption.addNoteOption = false;
		tmpToolBarOption.splitterResizeOption = true;
		tmpToolBarOption.bookmarkOption = false;
		tmpToolBarOption.goOption = false;
		tmpToolBarOption.createFromTemplateOption = false;
		tmpToolBarOption.restore = false;
		tmpToolBarOption.purge = false;
		tmpToolBarOption.purgeTrash = true;
		tmpToolBarOption.mergePdfOption = false;
		tmpToolBarOption.omrOption = false;
		tmpToolBarOption.convertOption = false;

		return tmpToolBarOption;
	}

	/**
	 * Gets the default Tool Bar object values for templates
	 *
	 * @return The default toolBarOption for templates
	 */
	public ToolBarOption getDefaultTemplatesToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();

		tmpToolBarOption.createFolderOption = true;
		tmpToolBarOption.findOption = true;
		tmpToolBarOption.findFolderOption = true;
		tmpToolBarOption.findDocumentOption = true;
		tmpToolBarOption.findSimilarDocumentOption = false;
		tmpToolBarOption.addDocumentOption = true;
		tmpToolBarOption.checkoutOption = false;
		tmpToolBarOption.checkinOption = false;
		tmpToolBarOption.cancelCheckoutOption = false;
		tmpToolBarOption.lockOption = false;
		tmpToolBarOption.unLockOption = false;
		tmpToolBarOption.downloadOption = false;
		tmpToolBarOption.downloadPdfOption = false;
		tmpToolBarOption.printOption = false;
		tmpToolBarOption.deleteOption = false;
		tmpToolBarOption.addPropertyGroupOption = false;
		tmpToolBarOption.updatePropertyGroupOption = false;
		tmpToolBarOption.removePropertyGroupOption = false;
		tmpToolBarOption.addSubscription = false;
		tmpToolBarOption.removeSubscription = false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption = false;
		tmpToolBarOption.refreshOption = true;
		tmpToolBarOption.renameOption = false;
		tmpToolBarOption.copyOption = false;
		tmpToolBarOption.sendDocumentLinkOption = false;
		tmpToolBarOption.sendDocumentAttachmentOption = false;
		tmpToolBarOption.mailForwardOption = false;
		tmpToolBarOption.moveOption = false;
		tmpToolBarOption.exportOption = false;
		tmpToolBarOption.workflowOption = false;
		tmpToolBarOption.addNoteOption = false;
		tmpToolBarOption.splitterResizeOption = true;
		tmpToolBarOption.bookmarkOption = false;
		tmpToolBarOption.goOption = false;
		tmpToolBarOption.createFromTemplateOption = false;
		tmpToolBarOption.restore = false;
		tmpToolBarOption.purge = false;
		tmpToolBarOption.purgeTrash = true;
		tmpToolBarOption.mergePdfOption = false;
		tmpToolBarOption.omrOption = false;
		tmpToolBarOption.convertOption = false;

		return tmpToolBarOption;
	}

	/**
	 * Gets the default Tool Bar object values for my documents
	 *
	 * @return The default toolBarOption for templates
	 */
	public ToolBarOption getDefaultMyDocumentsToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();

		tmpToolBarOption.createFolderOption = true;
		tmpToolBarOption.findOption = true;
		tmpToolBarOption.findFolderOption = true;
		tmpToolBarOption.findDocumentOption = true;
		tmpToolBarOption.findSimilarDocumentOption = false;
		tmpToolBarOption.addDocumentOption = true;
		tmpToolBarOption.checkoutOption = false;
		tmpToolBarOption.checkinOption = false;
		tmpToolBarOption.cancelCheckoutOption = false;
		tmpToolBarOption.lockOption = false;
		tmpToolBarOption.unLockOption = false;
		tmpToolBarOption.downloadOption = false;
		tmpToolBarOption.downloadPdfOption = false;
		tmpToolBarOption.printOption = false;
		tmpToolBarOption.deleteOption = false;
		tmpToolBarOption.addPropertyGroupOption = false;
		tmpToolBarOption.updatePropertyGroupOption = false;
		tmpToolBarOption.removePropertyGroupOption = false;
		tmpToolBarOption.addSubscription = false;
		tmpToolBarOption.removeSubscription = false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption = false;
		tmpToolBarOption.refreshOption = true;
		tmpToolBarOption.renameOption = false;
		tmpToolBarOption.copyOption = false;
		tmpToolBarOption.sendDocumentLinkOption = false;
		tmpToolBarOption.sendDocumentAttachmentOption = false;
		tmpToolBarOption.mailForwardOption = false;
		tmpToolBarOption.moveOption = false;
		tmpToolBarOption.exportOption = false;
		tmpToolBarOption.workflowOption = false;
		tmpToolBarOption.addNoteOption = false;
		tmpToolBarOption.splitterResizeOption = true;
		tmpToolBarOption.bookmarkOption = false;
		tmpToolBarOption.goOption = false;
		tmpToolBarOption.createFromTemplateOption = false;
		tmpToolBarOption.restore = false;
		tmpToolBarOption.purge = false;
		tmpToolBarOption.purgeTrash = true;
		tmpToolBarOption.mergePdfOption = false;
		tmpToolBarOption.omrOption = false;
		tmpToolBarOption.convertOption = false;

		return tmpToolBarOption;
	}

	/**
	 * Gets the default Tool Bar object values for mail
	 *
	 * @return The default toolBarOption for mail
	 */
	public ToolBarOption getDefaultMailToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();

		tmpToolBarOption.createFolderOption = true;
		tmpToolBarOption.findOption = true;
		tmpToolBarOption.findFolderOption = true;
		tmpToolBarOption.findDocumentOption = true;
		tmpToolBarOption.findSimilarDocumentOption = false;
		tmpToolBarOption.addDocumentOption = true;
		tmpToolBarOption.checkoutOption = false;
		tmpToolBarOption.checkinOption = false;
		tmpToolBarOption.cancelCheckoutOption = false;
		tmpToolBarOption.lockOption = false;
		tmpToolBarOption.unLockOption = false;
		tmpToolBarOption.downloadOption = false;
		tmpToolBarOption.downloadPdfOption = false;
		tmpToolBarOption.printOption = false;
		tmpToolBarOption.deleteOption = false;
		tmpToolBarOption.addPropertyGroupOption = false;
		tmpToolBarOption.updatePropertyGroupOption = false;
		tmpToolBarOption.removePropertyGroupOption = false;
		tmpToolBarOption.addSubscription = false;
		tmpToolBarOption.removeSubscription = false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption = true;
		tmpToolBarOption.refreshOption = true;
		tmpToolBarOption.renameOption = false;
		tmpToolBarOption.copyOption = false;
		tmpToolBarOption.sendDocumentLinkOption = false;
		tmpToolBarOption.sendDocumentAttachmentOption = false;
		tmpToolBarOption.mailForwardOption = false;
		tmpToolBarOption.moveOption = false;
		tmpToolBarOption.exportOption = false;
		tmpToolBarOption.workflowOption = false;
		tmpToolBarOption.addNoteOption = false;
		tmpToolBarOption.splitterResizeOption = true;
		tmpToolBarOption.bookmarkOption = false;
		tmpToolBarOption.goOption = false;
		tmpToolBarOption.createFromTemplateOption = false;
		tmpToolBarOption.restore = false;
		tmpToolBarOption.purge = false;
		tmpToolBarOption.purgeTrash = true;
		tmpToolBarOption.mergePdfOption = false;
		tmpToolBarOption.omrOption = false;
		tmpToolBarOption.convertOption = false;

		return tmpToolBarOption;
	}

	/**
	 * Gets the default Tool Bar object values for search
	 *
	 * @return The default toolBarOption for search
	 */
	public ToolBarOption getDefaultSearchToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();

		tmpToolBarOption.createFolderOption = false;
		tmpToolBarOption.findOption = false;
		tmpToolBarOption.findFolderOption = false;
		tmpToolBarOption.findDocumentOption = false;
		tmpToolBarOption.findSimilarDocumentOption = false;
		tmpToolBarOption.addDocumentOption = false;
		tmpToolBarOption.checkoutOption = false;
		tmpToolBarOption.checkinOption = false;
		tmpToolBarOption.cancelCheckoutOption = false;
		tmpToolBarOption.lockOption = false;
		tmpToolBarOption.unLockOption = false;
		tmpToolBarOption.downloadOption = false;
		tmpToolBarOption.downloadPdfOption = false;
		tmpToolBarOption.printOption = false;
		tmpToolBarOption.deleteOption = false;
		tmpToolBarOption.addPropertyGroupOption = false;
		tmpToolBarOption.updatePropertyGroupOption = false;
		tmpToolBarOption.removePropertyGroupOption = false;
		tmpToolBarOption.addSubscription = false;
		tmpToolBarOption.removeSubscription = false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption = false;
		tmpToolBarOption.refreshOption = false;
		tmpToolBarOption.renameOption = false;
		tmpToolBarOption.copyOption = false;
		tmpToolBarOption.sendDocumentLinkOption = false;
		tmpToolBarOption.sendDocumentAttachmentOption = false;
		tmpToolBarOption.mailForwardOption = false;
		tmpToolBarOption.moveOption = false;
		tmpToolBarOption.exportOption = false;
		tmpToolBarOption.workflowOption = false;
		tmpToolBarOption.addNoteOption = false;
		tmpToolBarOption.splitterResizeOption = false;
		tmpToolBarOption.bookmarkOption = false;
		tmpToolBarOption.goOption = false;
		tmpToolBarOption.createFromTemplateOption = false;
		tmpToolBarOption.restore = false;
		tmpToolBarOption.purge = false;
		tmpToolBarOption.purgeTrash = true;
		tmpToolBarOption.mergePdfOption = false;
		tmpToolBarOption.omrOption = false;
		tmpToolBarOption.convertOption = false;

		return tmpToolBarOption;
	}

	/**
	 * Gets the default Tool Bar object values for dashboard
	 *
	 * @return The default toolBarOption for search
	 */
	public ToolBarOption getDefaultDashboardToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();

		tmpToolBarOption.createFolderOption = false;
		tmpToolBarOption.findOption = false;
		tmpToolBarOption.findFolderOption = false;
		tmpToolBarOption.findDocumentOption = false;
		tmpToolBarOption.findSimilarDocumentOption = false;
		tmpToolBarOption.addDocumentOption = false;
		tmpToolBarOption.checkoutOption = false;
		tmpToolBarOption.checkinOption = false;
		tmpToolBarOption.cancelCheckoutOption = false;
		tmpToolBarOption.lockOption = false;
		tmpToolBarOption.unLockOption = false;
		tmpToolBarOption.downloadOption = false;
		tmpToolBarOption.downloadPdfOption = false;
		tmpToolBarOption.printOption = false;
		tmpToolBarOption.deleteOption = false;
		tmpToolBarOption.addPropertyGroupOption = false;
		tmpToolBarOption.updatePropertyGroupOption = false;
		tmpToolBarOption.removePropertyGroupOption = false;
		tmpToolBarOption.addSubscription = false;
		tmpToolBarOption.removeSubscription = false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption = false;
		tmpToolBarOption.refreshOption = true;
		tmpToolBarOption.renameOption = false;
		tmpToolBarOption.copyOption = false;
		tmpToolBarOption.sendDocumentLinkOption = false;
		tmpToolBarOption.sendDocumentAttachmentOption = false;
		tmpToolBarOption.mailForwardOption = false;
		tmpToolBarOption.moveOption = false;
		tmpToolBarOption.exportOption = false;
		tmpToolBarOption.workflowOption = false;
		tmpToolBarOption.addNoteOption = false;
		tmpToolBarOption.splitterResizeOption = false;
		tmpToolBarOption.bookmarkOption = false;
		tmpToolBarOption.goOption = false;
		tmpToolBarOption.createFromTemplateOption = false;
		tmpToolBarOption.restore = false;
		tmpToolBarOption.purge = false;
		tmpToolBarOption.purgeTrash = true;
		tmpToolBarOption.mergePdfOption = false;
		tmpToolBarOption.omrOption = false;
		tmpToolBarOption.convertOption = false;

		return tmpToolBarOption;
	}

	/**
	 * Gets the default Tool Bar object values for administration
	 *
	 * @return The default toolBarOption for search
	 */
	public ToolBarOption getDefaultAdministrationToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();

		tmpToolBarOption.createFolderOption = false;
		tmpToolBarOption.findOption = false;
		tmpToolBarOption.findFolderOption = false;
		tmpToolBarOption.findDocumentOption = false;
		tmpToolBarOption.findSimilarDocumentOption = false;
		tmpToolBarOption.addDocumentOption = false;
		tmpToolBarOption.checkoutOption = false;
		tmpToolBarOption.checkinOption = false;
		tmpToolBarOption.cancelCheckoutOption = false;
		tmpToolBarOption.lockOption = false;
		tmpToolBarOption.unLockOption = false;
		tmpToolBarOption.downloadOption = false;
		tmpToolBarOption.downloadPdfOption = false;
		tmpToolBarOption.printOption = false;
		tmpToolBarOption.deleteOption = false;
		tmpToolBarOption.addPropertyGroupOption = false;
		tmpToolBarOption.updatePropertyGroupOption = false;
		tmpToolBarOption.removePropertyGroupOption = false;
		tmpToolBarOption.addSubscription = false;
		tmpToolBarOption.removeSubscription = false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption = false;
		tmpToolBarOption.refreshOption = false;
		tmpToolBarOption.renameOption = false;
		tmpToolBarOption.copyOption = false;
		tmpToolBarOption.sendDocumentLinkOption = false;
		tmpToolBarOption.sendDocumentAttachmentOption = false;
		tmpToolBarOption.mailForwardOption = false;
		tmpToolBarOption.moveOption = false;
		tmpToolBarOption.exportOption = false;
		tmpToolBarOption.workflowOption = false;
		tmpToolBarOption.addNoteOption = false;
		tmpToolBarOption.splitterResizeOption = false;
		tmpToolBarOption.bookmarkOption = false;
		tmpToolBarOption.goOption = false;
		tmpToolBarOption.createFromTemplateOption = false;
		tmpToolBarOption.restore = false;
		tmpToolBarOption.purge = false;
		tmpToolBarOption.purgeTrash = true;
		tmpToolBarOption.mergePdfOption = false;
		tmpToolBarOption.omrOption = false;
		tmpToolBarOption.convertOption = false;

		return tmpToolBarOption;
	}

	/**
	 * Gets the default Tool Bar object values for extensions
	 *
	 * @return The default toolBarOption for search
	 */
	public ToolBarOption getDefaultExtensionsToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();

		tmpToolBarOption.createFolderOption = false;
		tmpToolBarOption.findOption = false;
		tmpToolBarOption.findFolderOption = false;
		tmpToolBarOption.findDocumentOption = false;
		tmpToolBarOption.findSimilarDocumentOption = false;
		tmpToolBarOption.addDocumentOption = false;
		tmpToolBarOption.checkoutOption = false;
		tmpToolBarOption.checkinOption = false;
		tmpToolBarOption.cancelCheckoutOption = false;
		tmpToolBarOption.lockOption = false;
		tmpToolBarOption.unLockOption = false;
		tmpToolBarOption.downloadOption = false;
		tmpToolBarOption.downloadPdfOption = false;
		tmpToolBarOption.printOption = false;
		tmpToolBarOption.deleteOption = false;
		tmpToolBarOption.addPropertyGroupOption = false;
		tmpToolBarOption.updatePropertyGroupOption = false;
		tmpToolBarOption.removePropertyGroupOption = false;
		tmpToolBarOption.addSubscription = false;
		tmpToolBarOption.removeSubscription = false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption = false;
		tmpToolBarOption.refreshOption = false;
		tmpToolBarOption.renameOption = false;
		tmpToolBarOption.copyOption = false;
		tmpToolBarOption.sendDocumentLinkOption = false;
		tmpToolBarOption.sendDocumentAttachmentOption = false;
		tmpToolBarOption.mailForwardOption = false;
		tmpToolBarOption.moveOption = false;
		tmpToolBarOption.exportOption = false;
		tmpToolBarOption.workflowOption = false;
		tmpToolBarOption.addNoteOption = false;
		tmpToolBarOption.splitterResizeOption = false;
		tmpToolBarOption.bookmarkOption = false;
		tmpToolBarOption.goOption = false;
		tmpToolBarOption.createFromTemplateOption = false;
		tmpToolBarOption.restore = false;
		tmpToolBarOption.purge = false;
		tmpToolBarOption.purgeTrash = true;
		tmpToolBarOption.mergePdfOption = false;
		tmpToolBarOption.omrOption = false;
		tmpToolBarOption.convertOption = false;

		return tmpToolBarOption;
	}

	/**
	 * Evaluate show Icons based on toolBarOption values
	 */
	public void evaluateShowIcons() {
		if (toolBarOption.createFolderOption) {
			enableCreateFolder();
		} else {
			disableCreateFolder();
		}

		if (toolBarOption.findOption) {
			enableFind();
		} else {
			disableFind();
		}

		if (toolBarOption.addDocumentOption) {
			enableAddDocument();
		} else {
			disableAddDocument();
		}

		if (toolBarOption.checkoutOption) {
			enableCheckout();
		} else {
			disableCheckout();
		}

		if (toolBarOption.checkinOption) {
			enableCheckin();
		} else {
			disableCheckin();
		}

		if (toolBarOption.cancelCheckoutOption) {
			enableCancelCheckout();
		} else {
			disableCancelCheckout();
		}

		if (toolBarOption.lockOption) {
			enableLock();
		} else {
			disableLock();
		}

		if (toolBarOption.unLockOption) {
			enableUnlock();
		} else {
			disableUnlock();
		}

		if (toolBarOption.downloadOption) {
			enableDownload();
		} else {
			disableDownload();
		}

		if (toolBarOption.downloadPdfOption) {
			enableDownloadPdf();
		} else {
			disableDownloadPdf();
		}

		if (toolBarOption.printOption) {
			enablePrint();
		} else {
			disablePrint();
		}

		if (toolBarOption.deleteOption) {
			enableDelete();
		} else {
			disableDelete();
		}

		if (toolBarOption.addPropertyGroupOption) {
			enableAddPropertyGroup();
		} else {
			disableAddPropertyGroup();
		}

		// Special case removePropertyGroupOption is only evaluated on
		// TabDocument and TabFolder tab
		// changing by evaluateRemoveGroupProperty method
		if (!toolBarOption.removePropertyGroupOption) {
			// We evaluate for changing panel desktop / search ( only disable
			// option )
			removePropertyGroup.setStyleName("okm-ToolBar-button-disabled");
			removePropertyGroup.setResource(OKMBundleResources.INSTANCE.removePropertyGroupDisabled());
			removePropertyGroup.setTitle(Main.i18n("general.menu.edit.remove.property.group"));
		}

		if (toolBarOption.workflowOption) {
			enableWorkflow();
		} else {
			disableWorkflow();
		}

		if (toolBarOption.addSubscription) {
			enableAddSubscription();
		} else {
			disableAddSubscription();
		}

		if (toolBarOption.removeSubscription) {
			enableRemoveSubscription();
		} else {
			disableRemoveSubscription();
		}

		if (toolBarOption.homeOption) {
			enableHome();
		} else {
			disableHome();
		}

		if (toolBarOption.refreshOption) {
			enableRefresh();
		} else {
			disableRefresh();
		}

		if (toolBarOption.splitterResizeOption) {
			enableSplitterResize();
		} else {
			disableSplitterResize();
		}
		
		if (toolBarOption.omrOption) {
			enableOmr();
		} else {
			disableOmr();
		}

		// Checking extension button
		for (Iterator<ToolBarButtonExtension> it = widgetExtensionList.iterator(); it.hasNext(); ) {
			ToolBarButtonExtension button = it.next();
			button.enable(button.isEnabled());
		}
	}

	/**
	 * Evaluate the remove group property
	 */
	public void evaluateRemovePropertyGroup(boolean propertyGroupEnabled) {
		// Show or hide removeGroupProperty depends on two cases, the property
		// is enabled by security user and
		// must be one tab group selected

		// We save to used on changing language
		this.propertyGroupEnabled = propertyGroupEnabled;

		// Sets fired property
		if (propertyGroupEnabled) {
			enableFiredRemovePropertyGroup();
		} else {
			disableFiredRemovePropertyGroup();
		}

		// Show or hides button
		if (toolBarOption.removePropertyGroupOption && toolBarOption.firedRemovePropertyGroupOption) {
			removePropertyGroup.setStyleName("okm-ToolBar-button");
			removePropertyGroup.setResource(OKMBundleResources.INSTANCE.removePropertyGroup());
			removePropertyGroup.setTitle(Main.i18n("general.menu.edit.remove.property.group"));
			Main.get().mainPanel.topPanel.mainMenu.enableRemovePropertyGroup();
		} else {
			removePropertyGroup.setStyleName("okm-ToolBar-button-disabled");
			removePropertyGroup.setResource(OKMBundleResources.INSTANCE.removePropertyGroupDisabled());
			removePropertyGroup.setTitle(Main.i18n("general.menu.edit.remove.property.group"));
			Main.get().mainPanel.topPanel.mainMenu.disableRemovePropertyGroup();
		}
	}

	/**
	 * Save changes to the actual view Must be called after mainPanel actual
	 * view is changed
	 */
	public void changeView(int view, int newMainPanelView) {
		boolean toolBarEnabled = true;
		int mainPanelView = Main.get().mainPanel.getActualView();

		// Evaluates actual desktop view to put values
		switch (mainPanelView) {
			case UIDockPanelConstants.DESKTOP:
				// Saves actual view values on hashMap
				switch (actualView) {
					case UIDesktopConstants.NAVIGATOR_TAXONOMY:
						viewValues.put("view_root:option", toolBarOption);
						break;

					case UIDesktopConstants.NAVIGATOR_CATEGORIES:
						viewValues.put("view_categories:option", toolBarOption);
						break;

					case UIDesktopConstants.NAVIGATOR_METADATA:
						viewValues.put("view_metadata:option", toolBarOption);
						break;

					case UIDesktopConstants.NAVIGATOR_THESAURUS:
						viewValues.put("view_thesaurus:option", toolBarOption);
						break;

					case UIDesktopConstants.NAVIGATOR_TRASH:
						viewValues.put("view_trash:option", toolBarOption);
						break;

					case UIDesktopConstants.NAVIGATOR_TEMPLATES:
						viewValues.put("view_templates:option", toolBarOption);
						break;

					case UIDesktopConstants.NAVIGATOR_PERSONAL:
						viewValues.put("view_my_documents:option", toolBarOption);
						break;

					case UIDesktopConstants.NAVIGATOR_MAIL:
						viewValues.put("view_mail:option", toolBarOption);
						break;
				}
				break;

			case UIDockPanelConstants.SEARCH:
				viewValues.put("view_search:option", toolBarOption);
				break;

			case UIDockPanelConstants.DASHBOARD:
				viewValues.put("view_dashboard:option", toolBarOption);
				break;

			case UIDockPanelConstants.ADMINISTRATION:
				viewValues.put("view_administration:option", toolBarOption);
				break;

			case UIDockPanelConstants.EXTENSIONS:
				viewValues.put("view_extension:option", toolBarOption);
				break;
		}

		// Evaluates new desktop view to restore values
		switch (newMainPanelView) {
			case UIDockPanelConstants.DESKTOP:
				switch (view) {
					case UIDesktopConstants.NAVIGATOR_TAXONOMY:
						if (viewValues.containsKey("view_root:option")) {
							toolBarOption = viewValues.get("view_root:option");
						}
						toolBarEnabled = true;
						break;

					case UIDesktopConstants.NAVIGATOR_CATEGORIES:
						if (viewValues.containsKey("view_categories:option")) {
							toolBarOption = viewValues.get("view_categories:option");
						} else {
							toolBarOption = getDefaultCategoriesToolBar();
						}
						toolBarEnabled = true;
						break;

					case UIDesktopConstants.NAVIGATOR_METADATA:
						if (viewValues.containsKey("view_metadata:option")) {
							toolBarOption = viewValues.get("view_metadata:option");
						} else {
							toolBarOption = getDefaultMetadataToolBar();
						}
						toolBarEnabled = true;
						break;

					case UIDesktopConstants.NAVIGATOR_THESAURUS:
						if (viewValues.containsKey("view_thesaurus:option")) {
							toolBarOption = viewValues.get("view_thesaurus:option");
						} else {
							toolBarOption = getDefaultThesaurusToolBar();
						}
						toolBarEnabled = true;
						break;

					case UIDesktopConstants.NAVIGATOR_TRASH:
						if (viewValues.containsKey("view_trash:option")) {
							toolBarOption = viewValues.get("view_trash:option");
						} else {
							toolBarOption = getDefaultTrashToolBar();
						}
						toolBarEnabled = false;
						break;

					case UIDesktopConstants.NAVIGATOR_TEMPLATES:
						if (viewValues.containsKey("view_templates:option")) {
							toolBarOption = viewValues.get("view_templates:option");
						} else {
							toolBarOption = getDefaultTemplatesToolBar();
						}
						toolBarEnabled = true;
						break;

					case UIDesktopConstants.NAVIGATOR_PERSONAL:
						if (viewValues.containsKey("view_my_documents:option")) {
							toolBarOption = viewValues.get("view_my_documents:option");
						} else {
							toolBarOption = getDefaultMyDocumentsToolBar();
						}
						toolBarEnabled = true;
						break;

					case UIDesktopConstants.NAVIGATOR_MAIL:
						if (viewValues.containsKey("view_mail:option")) {
							toolBarOption = viewValues.get("view_mail:option");
						} else {
							toolBarOption = getDefaultMailToolBar();
						}
						toolBarEnabled = true;
						break;
				}
				break;

			case UIDockPanelConstants.SEARCH:
				if (viewValues.containsKey("view_search:option")) {
					toolBarOption = viewValues.get("view_search:option");
				} else {
					toolBarOption = getDefaultSearchToolBar();
				}
				toolBarEnabled = false;
				break;

			case UIDockPanelConstants.DASHBOARD:
				if (viewValues.containsKey("view_dashboard:option")) {
					toolBarOption = viewValues.get("view_dashboard:option");
				} else {
					toolBarOption = getDefaultDashboardToolBar();
				}
				toolBarEnabled = false;
				break;

			case UIDockPanelConstants.ADMINISTRATION:
				if (viewValues.containsKey("view_administration:option")) {
					toolBarOption = viewValues.get("view_administration:option");
				} else {
					toolBarOption = getDefaultAdministrationToolBar();
				}
				toolBarEnabled = false;
				break;

			case UIDockPanelConstants.EXTENSIONS:
				if (viewValues.containsKey("view_extension:option")) {
					toolBarOption = viewValues.get("view_extension:option");
				} else {
					toolBarOption = getDefaultExtensionsToolBar();
				}
				toolBarEnabled = false;
				break;
		}

		if (Main.get().activeFolderTree.menuPopup != null) {
			// Sets the permission to menus
			propagateToolBarOptions();
		}

		// Enables before evaluate show icons, order is important because can
		// evaluate
		// icons if enabled is false always before evaluate icons must be
		// enabled
		enabled = true;
		evaluateShowIcons(); // Evalues icons to show
		enabled = toolBarEnabled;
		actualView = view; // Sets the new view active

		// Sets the permission to main menu
		Main.get().mainPanel.topPanel.mainMenu.setOptions(toolBarOption);

		fireEvent(HasToolBarEvent.EXECUTE_CHANGED_VIEW);
	}

	/**
	 * Gets asynchronous to get all groups
	 */
	final AsyncCallback<List<GWTPropertyGroup>> callbackGetAllGroups = new AsyncCallback<List<GWTPropertyGroup>>() {
		@Override
		public void onSuccess(List<GWTPropertyGroup> result) {
			// List of groups to be added
			if (!result.isEmpty()) {
				enableAddPropertyGroup();
			} else {
				disableAddPropertyGroup();
			}
		}

		@Override
		public void onFailure(Throwable caught) {
			disableAddPropertyGroup();
			Main.get().showError("GetAllGroups", caught);
		}
	};

	/**
	 * Gets all property groups
	 */
	private void getAllGroups() {
		if (!Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			String path = getActualNodePath();

			if (!path.equals("")) {
				propertyGroupService.getAllGroups(path, callbackGetAllGroups);
			}
		}
	}

	/**
	 * Obtain current node path.
	 */
	public String getActualNodePath() {
		String path = "";

		if (node instanceof GWTDocument) {
			path = ((GWTDocument) node).getPath();
		} else if (node instanceof GWTFolder) {
			path = ((GWTFolder) node).getPath();
		} else if (node instanceof GWTMail) {
			path = ((GWTMail) node).getPath();
		}

		return path;
	}

	/**
	 * Obtain current node uuid.
	 */
	public String getActualNodeUUID() {
		String path = "";

		if (node instanceof GWTDocument) {
			path = ((GWTDocument) node).getUuid();
		} else if (node instanceof GWTFolder) {
			path = ((GWTFolder) node).getUuid();
		} else if (node instanceof GWTMail) {
			path = ((GWTMail) node).getUuid();
		}

		return path;
	}

	/**
	 * getActualNode
	 */
	public Object getActualNode() {
		return node;
	}

	/**
	 * getMainToolBarPanel
	 */
	public HorizontalPanel getMainToolBarPanel() {
		return panel;
	}

	/**
	 * isNodeDocument
	 */
	public boolean isNodeDocument() {
		return (node != null && node instanceof GWTDocument);
	}

	/**
	 * isNodeFolder
	 */
	public boolean isNodeFolder() {
		return (node != null && node instanceof GWTFolder);
	}

	/**
	 * isNodeMail
	 */
	public boolean isNodeMail() {
		return (node != null && node instanceof GWTMail);
	}

	/**
	 * Sets the user home
	 */
	public void setUserHome(String user, String uuid, String path, String type) {
		Main.get().userHome.setHomeNode(uuid);
		Main.get().userHome.setUser(user);
		Main.get().userHome.setHomePath(path);
		Main.get().userHome.setHomeType(type);
		fireEvent(HasToolBarEvent.EXECUTE_SET_USER_HOME);
	}

	/**
	 * Create html uploader applet code
	 */
	public void setUploaderApplet(String sessionId, String path) {
		Widget uploaderApplet = RootPanel.get("uploaderApplet");
		uploaderApplet.setSize("1px", "1px");
		uploaderApplet.getElement().setInnerHTML(
				"<applet code=\"com.openkm.applet.Uploader\" name=\"Uploader\" width=\"1\" height=\"1\" mayscript archive=\"../uploader.jar\">"
						+ "<param name=\"sessionId\" value=\"" + sessionId + "\">" + "<param name=\"path\" value=\"" + path + "\">"
						+ "<param name=\"lang\" value=\"" + Main.get().getLang() + "\">" + "</applet>");
	}

	/**
	 * destroyUploaderApplet
	 */
	public void destroyUploaderApplet() {
		Widget uploadApplet = RootPanel.get("uploaderApplet");
		uploadApplet.getElement().setInnerHTML("");
	}

	/**
	 * Lang refresh
	 */
	public void langRefresh() {
		evaluateShowIcons();
		evaluateRemovePropertyGroup(propertyGroupEnabled);
		resizeToolBarMenu.langRefresh();
		findToolBarMenu.langRefresh();
	}

	/**
	 * Gets the tool bar option
	 *
	 * @return The actual toolBar Option
	 */
	public ToolBarOption getToolBarOption() {
		return toolBarOption;
	}

	/**
	 * setToolBarOption
	 */
	public void setToolBarOption(ToolBarOption toolBarOption) {
		this.toolBarOption = toolBarOption;
		propagateToolBarOptions(); // Always must propagate tool bar options
	}

	/**
	 * propagateToolBarOptions
	 */
	private void propagateToolBarOptions() {
		Main.get().mainPanel.topPanel.mainMenu.setOptions(toolBarOption);
		Main.get().mainPanel.desktop.browser.fileBrowser.setOptions(toolBarOption);
		Main.get().activeFolderTree.menuPopup.setOptions(toolBarOption);
		findToolBarMenu.setOptions(toolBarOption);

		if (node instanceof GWTFolder) {
			// Sets the visible values to note tab
			Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.notes.setVisibleAddNote(toolBarOption.addNoteOption);
		} else if (node instanceof GWTDocument) {
			// Sets the visible values to note tab
			Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.notes.setVisibleAddNote(toolBarOption.addNoteOption);
		} else if (node instanceof GWTMail) {
			// Sets the visible values to note tab
			Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.notes.setVisibleAddNote(toolBarOption.addNoteOption);
		}

		// Propagates merge pdf
		if (toolBarOption.mergePdfOption) {
			enablePdfMerge();
		} else {
			disablePdfMerge();
		}
	}

	/**
	 * disableAll
	 */
	private void disableAllOptions() {
		toolBarOption.createFolderOption = false;
		toolBarOption.findOption = false;
		toolBarOption.findFolderOption = false;
		toolBarOption.findDocumentOption = false;
		toolBarOption.findSimilarDocumentOption = false;
		toolBarOption.downloadOption = false;
		toolBarOption.downloadPdfOption = false;
		toolBarOption.printOption = false;
		toolBarOption.lockOption = false;
		toolBarOption.unLockOption = false;
		toolBarOption.addDocumentOption = false;
		toolBarOption.checkoutOption = false;
		toolBarOption.checkinOption = false;
		toolBarOption.cancelCheckoutOption = false;
		toolBarOption.deleteOption = false;
		toolBarOption.addPropertyGroupOption = false;
		toolBarOption.updatePropertyGroupOption = false;
		toolBarOption.removePropertyGroupOption = false;
		toolBarOption.firedRemovePropertyGroupOption = false;
		toolBarOption.addSubscription = false;
		toolBarOption.removeSubscription = false;
		toolBarOption.homeOption = false;
		toolBarOption.refreshOption = false;
		toolBarOption.renameOption = false;
		toolBarOption.copyOption = false;
		toolBarOption.sendDocumentLinkOption = false;
		toolBarOption.sendDocumentAttachmentOption = false;
		toolBarOption.mailForwardOption = false;
		toolBarOption.moveOption = false;
		toolBarOption.exportOption = false;
		toolBarOption.workflowOption = false;
		toolBarOption.addNoteOption = false;
		toolBarOption.addCategoryOption = false;
		toolBarOption.addKeywordOption = false;
		toolBarOption.splitterResizeOption = false;
		toolBarOption.bookmarkOption = false;
		toolBarOption.goOption = false;
		toolBarOption.createFromTemplateOption = false;
		toolBarOption.restore = false;
		toolBarOption.purge = false;
		toolBarOption.purgeTrash = false;
		toolBarOption.mergePdfOption = false;
		toolBarOption.omrOption = false;
		toolBarOption.convertOption = false;
		Main.get().mainPanel.topPanel.mainMenu.disableAllOptions();
		Main.get().mainPanel.desktop.browser.fileBrowser.disableAllOptions();
		Main.get().activeFolderTree.menuPopup.disableAllOptions();
	}

	/**
	 * setAvailableOption
	 */
	public void setAvailableOption(GWTProfileToolbar option) {
		findToolBarMenu.setAvailableOption(option); // Quick search options

		// FIRST
		find.setVisible(option.isFindFolderVisible() || option.isFindDocumentVisible() || option.isSimilarDocumentVisible());
		panel.getWidget(2).setVisible(option.isFindFolderVisible() || option.isFindDocumentVisible() || option.isSimilarDocumentVisible()); // Hide
		// space

		download.setVisible(option.isDownloadVisible());
		panel.getWidget(4).setVisible(option.isDownloadVisible()); // hide space
		downloadPdf.setVisible(option.isDownloadPdfVisible());
		panel.getWidget(6).setVisible(option.isDownloadPdfVisible()); // hide
		// space
		print.setVisible(option.isPrintVisible());
		panel.getWidget(8).setVisible(option.isPrintVisible()); // hide space
		panel.getWidget(9).setVisible(
				option.isFindFolderVisible() || option.isDownloadVisible() || option.isDownloadPdfVisible() || option.isPrintVisible()); // hide
		// separator

		// SECOND
		lock.setVisible(option.isLockVisible());
		panel.getWidget(11).setVisible(option.isLockVisible()); // hide space
		unlock.setVisible(option.isUnlockVisible());
		panel.getWidget(13).setVisible(option.isUnlockVisible()); // hide space
		panel.getWidget(14).setVisible(option.isLockVisible() || option.isUnlockVisible()); // hide
		// separator

		// THIRD
		createFolder.setVisible(option.isCreateFolderVisible());
		panel.getWidget(16).setVisible(option.isCreateFolderVisible()); // Hide
		// space
		addDocument.setVisible(option.isAddDocumentVisible());
		panel.getWidget(18).setVisible(option.isAddDocumentVisible()); // hide
		// space
		checkout.setVisible(option.isCheckoutVisible());
		panel.getWidget(20).setVisible(option.isCheckoutVisible()); // hide
		// space
		checkin.setVisible(option.isCheckinVisible());
		panel.getWidget(22).setVisible(option.isCheckinVisible()); // hide space
		cancelCheckout.setVisible(option.isCancelCheckoutVisible());
		panel.getWidget(24).setVisible(option.isCancelCheckoutVisible()); // hide
		// space
		delete.setVisible(option.isDeleteVisible());
		panel.getWidget(26).setVisible(option.isDeleteVisible()); // hide space
		panel.getWidget(27).setVisible(
				option.isCreateFolderVisible() || option.isAddDocumentVisible() || option.isCheckoutVisible() || option.isCheckinVisible()
						|| option.isCancelCheckoutVisible() || option.isDeleteVisible()); // hide
		// separator

		// FOURTH
		addPropertyGroup.setVisible(option.isAddPropertyGroupVisible());
		panel.getWidget(29).setVisible(option.isAddPropertyGroupVisible()); // hide
		// space
		removePropertyGroup.setVisible(option.isRemovePropertyGroupVisible());
		panel.getWidget(31).setVisible(option.isRemovePropertyGroupVisible()); // hide
		// space
		panel.getWidget(32).setVisible(option.isAddPropertyGroupVisible() || option.isRemovePropertyGroupVisible()); // hide
		// separator

		// FIFTH
		startWorkflow.setVisible(option.isStartWorkflowVisible());
		panel.getWidget(34).setVisible(option.isStartWorkflowVisible()); // hide
		// space
		panel.getWidget(35).setVisible(option.isStartWorkflowVisible()); // hide
		// separator

		// SIXTH
		addSubscription.setVisible(option.isAddSubscriptionVisible());
		panel.getWidget(37).setVisible(option.isAddSubscriptionVisible()); // hide
		// space
		removeSubscription.setVisible(option.isRemoveSubscriptionVisible());
		panel.getWidget(39).setVisible(option.isRemoveSubscriptionVisible()); // hide
		// space
		panel.getWidget(40).setVisible(option.isAddSubscriptionVisible() || option.isRemoveSubscriptionVisible()); // hide
		// separator

		// SEVENTH
		refresh.setVisible(option.isRefreshVisible());
		panel.getWidget(42).setVisible(option.isRefreshVisible()); // hide space
		home.setVisible(option.isHomeVisible());
		panel.getWidget(44).setVisible(option.isHomeVisible()); // hide space
		panel.getWidget(45).setVisible(option.isHomeVisible() || option.isRefreshVisible()); // hide
		// separator
		splitterResize.setVisible(option.isSplitterResizeVisible());
		panel.getWidget(47).setVisible(option.isSplitterResizeVisible()); // hide
		// space
		omr.setVisible(option.isOmrVisible());
		panel.getWidget(49).setVisible(option.isOmrVisible()); // hide space
	}

	/**
	 * windowResized
	 */
	public void windowResized() {
		resizeToolBarMenu.windowResized();
	}

	/**
	 * initExtendedSecurity
	 */
	public void initExtendedSecurity(int extendedSecurity) {
		evaluateGroup = ((extendedSecurity & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP);
		evaluateWorkflow = ((extendedSecurity & GWTPermission.START_WORKFLOW) == GWTPermission.START_WORKFLOW);
		evaluateDownload = ((extendedSecurity & GWTPermission.DOWNLOAD) == GWTPermission.DOWNLOAD);
	}

	/**
	 * addToolBarButton
	 */
	public void addToolBarButtonExtension(ToolBarButtonExtension extension) {
		extension.addMouseOverHandler(mouseOverHandler);
		extension.addMouseOutHandler(mouseOutHandler);
		extension.setStyleName("okm-ToolBar-button");
		widgetExtensionList.add(extension);
		panel.add(extension);
	}

	@Override
	public void addToolBarHandlerExtension(ToolBarHandlerExtension handlerExtension) {
		toolBarHandlerExtensionList.add(handlerExtension);
	}

	@Override
	public void fireEvent(ToolBarEventConstant event) {
		for (Iterator<ToolBarHandlerExtension> it = toolBarHandlerExtensionList.iterator(); it.hasNext(); ) {
			it.next().onChange(event);
		}
	}

	/**
	 * initJavaScriptApi
	 */
	public native void initJavaScriptApi(ToolBar toolBar) /*-{
        $wnd.destroyUploaderApplet = function () {
            toolBar.@com.openkm.frontend.client.widget.toolbar.ToolBar::destroyUploaderApplet()();
            return true;
        }

        $wnd.refreshFolder = function () {
            toolBar.@com.openkm.frontend.client.widget.toolbar.ToolBar::executeRefresh()();
            return true;
        }

        $wnd.jsRefreshFolder = function () {
            toolBar.@com.openkm.frontend.client.widget.toolbar.ToolBar::executeRefresh()();
            return true;
        }

        $wnd.jsCancelCheckout = function () {
            toolBar.@com.openkm.frontend.client.widget.toolbar.ToolBar::executeCancelCheckout()();
            return true;
        }
    }-*/;
}
