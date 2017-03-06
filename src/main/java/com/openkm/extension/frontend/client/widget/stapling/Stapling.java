/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2011 Paco Avila & Josep Llort
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

package com.openkm.extension.frontend.client.widget.stapling;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.service.OKMStaplingService;
import com.openkm.extension.frontend.client.service.OKMStaplingServiceAsync;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.extension.GWTStaple;
import com.openkm.frontend.client.bean.extension.GWTStapleGroup;
import com.openkm.frontend.client.constants.service.RPCService;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.extension.comunicator.*;
import com.openkm.frontend.client.extension.event.*;
import com.openkm.frontend.client.extension.event.HasDocumentEvent.DocumentEventConstant;
import com.openkm.frontend.client.extension.event.HasFolderEvent.FolderEventConstant;
import com.openkm.frontend.client.extension.event.HasLanguageEvent.LanguageEventConstant;
import com.openkm.frontend.client.extension.event.HasMailEvent.MailEventConstant;
import com.openkm.frontend.client.extension.event.HasNavigatorEvent.NavigatorEventConstant;
import com.openkm.frontend.client.extension.event.HasWorkspaceEvent.WorkspaceEventConstant;
import com.openkm.frontend.client.extension.event.handler.*;
import com.openkm.frontend.client.extension.widget.toolbar.ToolBarButtonExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jllort
 *
 */
public class Stapling implements DocumentHandlerExtension, FolderHandlerExtension, MailHandlerExtension,
		NavigatorHandlerExtension, LanguageHandlerExtension, WorkspaceHandlerExtension {
	public static final int TAB_DOCUMENT = 0;
	public static final int TAB_FOLDER = 1;
	public static final int TAB_MAIL = 2;

	private static Stapling singleton;
	private static final String UUID = "25af39c0-580f-431c-8852-0b6430b4dc1d";

	private final OKMStaplingServiceAsync staplingService = (OKMStaplingServiceAsync) GWT.create(OKMStaplingService.class);

	private ToolBarButtonStart buttonStart;
	private ToolBarButtonStop buttonStop;
	public TabDocumentStapling tabDocument;
	public TabFolderStapling tabFolder;
	public TabMailStapling tabMail;
	private boolean enabled = false;
	private boolean startWasEnabled = false;
	private boolean stopWasEnabled = false;
	private int actualWorkspace = 0;
	private String groupId = "";
	private String firstUUID = "";
	private String firstType = "";
	private String groupIdMarkedToDelete = "";
	private GWTStapleGroup actualStapleGroup = new GWTStapleGroup();
	private boolean isValidStackWithStapling = true;
	private List<Button> addButtonList = new ArrayList<Button>();
	private List<Button> deleteButtonList = new ArrayList<Button>();
	private List<Button> downloadButtonList = new ArrayList<Button>();
	private ConfirmPopup confirmPopup;
	private Status status;
	private int selectedPanel = TAB_FOLDER;

	public Stapling(List<String> uuidList) {
		singleton = this;
		if (isRegistered(uuidList)) {
			confirmPopup = new ConfirmPopup();
			confirmPopup.setWidth("300px");
			confirmPopup.setHeight("125px");
			confirmPopup.setStyleName("okm-Popup");
			confirmPopup.addStyleName("okm-DisableSelect");
			status = new Status();
			status.setStyleName("okm-StatusPopup");
			buttonStart = new ToolBarButtonStart(new Image(OKMBundleResources.INSTANCE.staplingDisabled()),
					GeneralComunicator.i18nExtension("stapling.document.title"), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (enabled) {
						String docTypeTemp = "";
						String uuidTemp = getUuid();
						String nameTemp = getName();
						switch (selectedPanel) {
							case TAB_DOCUMENT:
								docTypeTemp = GWTStaple.STAPLE_DOCUMENT;
								break;
							case TAB_FOLDER:
								docTypeTemp = GWTStaple.STAPLE_FOLDER;
								break;
							case TAB_MAIL:
								docTypeTemp = GWTStaple.STAPLE_MAIL;
								break;
						}

						final String docType = docTypeTemp;
						final String uuid = uuidTemp;
						final String name = nameTemp;

						if (groupId.equals("")) {
							// First case creation stapling
							if (firstUUID.equals("")) {
								firstUUID = uuid;
								firstType = docType;
								GeneralComunicator.setStatus(GeneralComunicator
										.i18nExtension("stapling.status.started"));
								buttonStart.enable(false); // Disables add button
								buttonStart.evaluateShowIcons();
								buttonStop.setVisible(true); // Show cancel button
								enableAddButtons(false);
							} else if (!firstUUID.equals(uuid)) {
								status.setAddStapling();
								staplingService.create(GeneralComunicator.getUser(), firstUUID, firstType,
										uuid, docType, new AsyncCallback<String>() {
											@Override
											public void onSuccess(String result) {
												groupId = result;
												buttonStart.enable(false); // Disables button
												buttonStart.evaluateShowIcons();
												if (docType.equals(GWTStaple.STAPLE_FOLDER)) {
													refresh(tabFolder.getTable(), uuid); // Refreshing stapling
												} else if (docType.equals(GWTStaple.STAPLE_DOCUMENT)) {
													refresh(tabDocument.getTable(), uuid); // Refreshing
													// stapling
												} else if (docType.equals(GWTStaple.STAPLE_MAIL)) {
													refresh(tabMail.getTable(), uuid); // Refreshing stapling
												}
												status.unsetAddStapling();
											}

											@Override
											public void onFailure(Throwable caught) {
												GeneralComunicator.showError("create", caught);
												status.unsetAddStapling();
											}
										});
							}
						} else {
							staplingService.add(groupId, uuid, docType, new AsyncCallback<Object>() {
								@Override
								public void onSuccess(Object result) {
									GeneralComunicator.setStatus(GeneralComunicator.i18nExtension("stapling.done") + " - " + name);
									buttonStart.enable(false); // Disables button
									buttonStart.evaluateShowIcons();
									if (docType.equals(GWTStaple.STAPLE_FOLDER)) {
										refresh(tabFolder.getTable(), uuid); // Refreshing stapling
									} else if (docType.equals(GWTStaple.STAPLE_DOCUMENT)) {
										refresh(tabDocument.getTable(), uuid); // Refreshing stapling
									} else if (docType.equals(GWTStaple.STAPLE_MAIL)) {
										refresh(tabMail.getTable(), uuid); // Refreshing stapling
									}
								}

								@Override
								public void onFailure(Throwable caught) {
									GeneralComunicator.showError("add", caught);
								}
							});
						}
					}
				}
			});

			buttonStop = new ToolBarButtonStop(new Image(OKMBundleResources.INSTANCE.staplingStop()),
					GeneralComunicator.i18nExtension("stapling.document.stop.title"), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					stopStapling();
				}
			});
			buttonStop.setStyleName("okm-ToolBar-button");
			buttonStop.setVisible(false); // Only appears to stopping process after first button

			// The tab document
			tabDocument = new TabDocumentStapling();
			tabDocument.setSize("100%", "100%");

			// The tab folder
			tabFolder = new TabFolderStapling();
			tabFolder.setSize("100%", "100%");

			// The tab mail
			tabMail = new TabMailStapling();
			tabMail.setSize("100%", "100%");
		}
	}

	/**
	 * getUuid
	 */
	private String getUuid() {
		switch (selectedPanel) {
			case TAB_DOCUMENT:
				return TabDocumentComunicator.getDocument().getUuid();

			case TAB_FOLDER:
				return TabFolderComunicator.getFolder().getUuid();

			case TAB_MAIL:
				return TabMailComunicator.getMail().getUuid();

			default:
				return null;
		}
	}

	/**
	 * getUuid
	 */
	private String getName() {
		switch (selectedPanel) {
			case TAB_DOCUMENT:
				return TabDocumentComunicator.getDocument().getName();

			case TAB_FOLDER:
				return TabFolderComunicator.getFolder().getName();

			case TAB_MAIL:
				return TabMailComunicator.getMail().getSubject();

			default:
				return null;
		}
	}

	/**
	 * get
	 *
	 * @return
	 */
	public static Stapling get() {
		return singleton;
	}

	/**
	 * getExtensions
	 *
	 * @return
	 */
	public List<Object> getExtensions() {
		List<Object> extensions = new ArrayList<Object>();
		extensions.add(singleton);
		extensions.add(buttonStart);
		extensions.add(buttonStop);
		extensions.add(tabDocument);
		extensions.add(tabFolder);
		extensions.add(tabMail);
		extensions.add(status);
		return extensions;
	}

	@Override
	public void onChange(DocumentEventConstant event) {
		if (event.equals(HasDocumentEvent.DOCUMENT_CHANGED)) {
			selectedPanel = TAB_DOCUMENT;
			if (NavigatorComunicator.isTaxonomyShown() || NavigatorComunicator.isCategoriesShown()
					|| NavigatorComunicator.isThesaurusShown()) {
				refresh(tabDocument.getTable(), getUuid());
			} else {
				if (buttonStop.isVisible()) {
					enableAddButtons(true);
					stopStapling();
				}
				buttonStop.enable(false);
				buttonStart.evaluateShowIcons();
				tabDocument.getTable().removeAllRows();
			}
		} else if (event.equals(HasDocumentEvent.DOCUMENT_DELETED)) {
			staplingService.removeAllStapleByUuid(getUuid(), new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
				}

				@Override
				public void onFailure(Throwable caught) {
					GeneralComunicator.showError("removeAllStapleByUuid", caught);
				}
			});
		}
	}

	@Override
	public void onChange(MailEventConstant event) {
		if (event.equals(HasMailEvent.MAIL_CHANGED)) {
			selectedPanel = TAB_MAIL;
			if (NavigatorComunicator.isTaxonomyShown() || NavigatorComunicator.isCategoriesShown()
					|| NavigatorComunicator.isThesaurusShown()) {
				refresh(tabMail.getTable(), getUuid());
			} else {
				if (buttonStop.isVisible()) {
					enableAddButtons(true);
					stopStapling();
				}
				buttonStop.enable(false);
				buttonStart.evaluateShowIcons();
				tabDocument.getTable().removeAllRows();
			}
		} else if (event.equals(HasMailEvent.MAIL_DELETED)) {
			staplingService.removeAllStapleByUuid(getUuid(), new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
				}

				@Override
				public void onFailure(Throwable caught) {
					GeneralComunicator.showError("removeAllStapleByUuid", caught);
				}
			});
		}
	}

	@Override
	public void onChange(FolderEventConstant event) {
		if (event.equals(HasFolderEvent.FOLDER_CHANGED)) {
			selectedPanel = TAB_FOLDER;
			// At starting root uuid node is null
			if (NavigatorComunicator.isTaxonomyShown() || NavigatorComunicator.isCategoriesShown()
					|| NavigatorComunicator.isThesaurusShown()) {
				refresh(tabFolder.getTable(), getUuid());
			} else {
				if (buttonStop.isVisible()) {
					buttonStop.setVisible(false);
					stopStapling();
				}
				buttonStop.enable(false);
				buttonStart.evaluateShowIcons();
				tabFolder.getTable().removeAllRows();
			}
		} else if (event.equals(HasFolderEvent.FOLDER_DELETED)) {
			staplingService.removeAllStapleByUuid(getUuid(), new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
				}

				@Override
				public void onFailure(Throwable caught) {
					GeneralComunicator.showError("removeAllStapleByUuid", caught);
				}
			});
		}
	}

	@Override
	public void onChange(NavigatorEventConstant event) {
		if (event.equals(HasNavigatorEvent.STACK_CHANGED)) {
			if (!NavigatorComunicator.isTaxonomyShown() && !NavigatorComunicator.isCategoriesShown()
					&& !NavigatorComunicator.isThesaurusShown()) {
				isValidStackWithStapling = false;
				if (buttonStop.isVisible()) {
					buttonStop.setVisible(false);
					stopStapling();
				}
				buttonStop.enable(false);
				buttonStart.evaluateShowIcons();
			} else {
				isValidStackWithStapling = true;
			}
		}
	}

	@Override
	public void onChange(WorkspaceEventConstant event) {
		if (event.equals(HasWorkspaceEvent.STACK_CHANGED)) {
			// First stack is always desktop
			if (WorkspaceComunicator.getSelectedWorkspace() != UIDockPanelConstants.DESKTOP) {
				if (actualWorkspace == UIDockPanelConstants.DESKTOP) {
					startWasEnabled = enabled; // Save status before changing workspace
					stopWasEnabled = buttonStop.isEnabled();
					enabled = false;
					buttonStart.enable(false);
					buttonStop.enable(false);
					buttonStart.evaluateShowIcons();
					buttonStop.evaluateShowIcons();
				}
			} else {
				enabled = startWasEnabled;
				buttonStart.enable(startWasEnabled);
				buttonStop.enable(stopWasEnabled);
				buttonStart.evaluateShowIcons();
				buttonStop.evaluateShowIcons();
			}
			actualWorkspace = WorkspaceComunicator.getSelectedTab();
		}
	}

	/**
	 * ToolBarButton
	 *
	 * @author jllort
	 *
	 */
	private class ToolBarButtonStart extends ToolBarButtonExtension {

		public ToolBarButtonStart(Image image, String title, ClickHandler handler) {
			super(image, title, handler);
		}

		@Override
		public void checkPermissions(GWTFolder folder, GWTFolder folderParent, int originPanel) {
			enabled = isEnabledButton(folder.getUuid());
			evaluateShowIcons();
		}

		@Override
		public void checkPermissions(GWTDocument doc, GWTFolder folder) {
			enabled = isEnabledButton(doc.getUuid());
			evaluateShowIcons();
		}

		@Override
		public void checkPermissions(GWTMail mail, GWTFolder folder) {
			enabled = isEnabledButton(mail.getUuid());
			evaluateShowIcons();
		}

		@Override
		public void enable(boolean enable) {
			enabled = enable;
		}

		@Override
		public boolean isEnabled() {
			return enabled;
		}

		/**
		 * evaluateShowIcons
		 */
		public void evaluateShowIcons() {
			if (enabled) {
				enableStapling();
			} else {
				disableStapling();
			}
		}

		/**
		 * enableStapling
		 */
		private void enableStapling() {
			setStyleName("okm-ToolBar-button");
			setResource(OKMBundleResources.INSTANCE.stapling());
			setTitle(GeneralComunicator.i18nExtension("stapling.document.title"));
		}

		/**
		 * disableStapling
		 */
		private void disableStapling() {
			setStyleName("okm-ToolBar-button-disabled");
			setResource(OKMBundleResources.INSTANCE.staplingDisabled());
			setTitle(GeneralComunicator.i18nExtension("stapling.document.title"));
		}
	}

	/**
	 * ToolBarButtonStop
	 *
	 * @author jllort
	 *
	 */
	private class ToolBarButtonStop extends ToolBarButtonExtension {

		// The boolean enabled is not shared with logic ( this button is always enabled )
		private boolean enabled = true;

		public ToolBarButtonStop(Image image, String title, ClickHandler handler) {
			super(image, title, handler);
		}

		@Override
		public void checkPermissions(GWTFolder folder, GWTFolder folderParent, int originPanel) {
			enabled = true;
			evaluateShowIcons();
		}

		@Override
		public void checkPermissions(GWTDocument doc, GWTFolder folder) {
			enabled = true;
			evaluateShowIcons();
		}

		@Override
		public void checkPermissions(GWTMail mail, GWTFolder folder) {
			enabled = true;
			evaluateShowIcons();
			;
		}

		@Override
		public void enable(boolean enable) {
			this.enabled = enable;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		/**
		 * evaluateShowIcons
		 */
		public void evaluateShowIcons() {
			if (enabled) {
				enableStapling();
			} else {
				disableStapling();
			}
		}

		/**
		 * enableStapling
		 */
		private void enableStapling() {
			setStyleName("okm-ToolBar-button");
			setResource(OKMBundleResources.INSTANCE.staplingStop());
			setTitle(GeneralComunicator.i18nExtension("stapling.document.title"));
		}

		/**
		 * disableStapling
		 */
		private void disableStapling() {
			setStyleName("okm-ToolBar-button-disabled");
			setResource(OKMBundleResources.INSTANCE.staplingStopDisabled());
			setTitle(GeneralComunicator.i18nExtension("stapling.document.title"));
		}
	}

	/**
	 * getGroupId
	 *
	 * @return
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * refreshFolder
	 *
	 * @param uuid
	 */
	public void refreshFolder(String uuid) {
		refresh(tabFolder.getTable(), uuid);
	}

	/**
	 * refreshDocument
	 *
	 * @param uuid
	 */
	public void refreshDocument(String uuid) {
		refresh(tabDocument.getTable(), uuid);
	}

	/**
	 * refreshMail
	 *
	 * @param uuid
	 */
	public void refreshMail(String uuid) {
		refresh(tabMail.getTable(), uuid);
	}

	/**
	 * refresh
	 *
	 * @param uuid
	 */
	public void refresh(final FlexTable table, final String uuid) {
		status.setGetStapleds();
		staplingService.getAll(uuid, new AsyncCallback<List<GWTStapleGroup>>() {
			@Override
			public void onSuccess(List<GWTStapleGroup> result) {
				actualStapleGroup = new GWTStapleGroup();
				table.removeAllRows();
				addButtonList = new ArrayList<Button>();
				deleteButtonList = new ArrayList<Button>();
				downloadButtonList = new ArrayList<Button>();
				int count = 0;
				for (final GWTStapleGroup sg : result) {
					// Saving actual staplingGroup
					if (Stapling.get().getGroupId().equals(String.valueOf(sg.getId()))) {
						actualStapleGroup = sg;
					}

					// Adding first column de name "group - number"
					table.setHTML(table.getRowCount(), 0, "&nbsp;"); // head space
					int row = table.getRowCount();
					if (sg.getStaples().size() > 0) {
						HorizontalPanel hPanel = new HorizontalPanel();
						HTML groupTitle = new HTML("<b>" + GeneralComunicator.i18nExtension("stapling.group") + " - "
								+ (count + 1) + "</b>");
						hPanel.add(groupTitle);
						HTML space = new HTML("&nbsp;");
						hPanel.add(space);
						hPanel.setCellWidth(space, "10px");
						// Add button only available to user owner
						if (sg.getUser().equals(GeneralComunicator.getUser())) {
							Button addButton = new Button(GeneralComunicator.i18n("button.add"));
							addButton.setVisible(!buttonStop.isVisible()); // If stop button is visible must no be
							// showed the add button
							addButton.addClickHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									groupId = String.valueOf(sg.getId());
									buttonStart.enable(false);
									buttonStop.setVisible(true);
									enableAddButtons(false);
									groupId = String.valueOf(sg.getId());
									buttonStart.evaluateShowIcons();
								}
							});
							addButtonList.add(addButton);
							hPanel.add(addButton);
							hPanel.setCellVerticalAlignment(addButton, HasAlignment.ALIGN_MIDDLE);
							addButton.setStyleName("okm-AddButton");

							HTML space2 = new HTML("");
							hPanel.add(space2);
							hPanel.setCellWidth(space2, "5px");

							Button deleteButton = new Button(GeneralComunicator.i18n("button.delete"));
							deleteButton.setVisible(!buttonStop.isVisible()); // If stop button is visible must no be
							// showed the add button
							deleteButton.addClickHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									groupIdMarkedToDelete = String.valueOf(sg.getId());
									confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_STAPLING_GROUP);
									confirmPopup.center();
								}
							});
							deleteButtonList.add(deleteButton);
							hPanel.add(deleteButton);
							hPanel.setCellVerticalAlignment(deleteButton, HasAlignment.ALIGN_MIDDLE);
							deleteButton.setStyleName("okm-DeleteButton");

							HTML space3 = new HTML("");
							hPanel.add(space3);
							hPanel.setCellWidth(space3, "5px");
						}

						Button downloadButton = new Button(GeneralComunicator.i18nExtension("button.download"));
						downloadButton.setVisible(!buttonStop.isVisible()); // If stop button is visible must no be
						// showed the add button
						downloadButton.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								groupId = String.valueOf(sg.getId());
								String url = RPCService.StaplingDownloadService + "?sgId=" + groupId;
								GeneralComunicator.extensionCallOwnDownload(url);
							}
						});
						downloadButtonList.add(downloadButton);
						hPanel.add(downloadButton);
						hPanel.setCellVerticalAlignment(downloadButton, HasAlignment.ALIGN_MIDDLE);
						downloadButton.setStyleName("okm-DownloadZipButton");

						hPanel.setCellWidth(space, "20px");
						hPanel.setCellVerticalAlignment(groupTitle, HasAlignment.ALIGN_MIDDLE);
						table.setWidget(row, 0, hPanel);
						table.getFlexCellFormatter().setColSpan(row, 0, 5);
						table.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasAlignment.ALIGN_CENTER);
						for (GWTStaple st : sg.getStaples()) {
							if (st.getType().equals(GWTStaple.STAPLE_DOCUMENT)) {
								StapleTableManager.addDocument(table, st, uuid,
										sg.getUser().equals(GeneralComunicator.getUser()));
							} else if (st.getType().equals(GWTStaple.STAPLE_FOLDER)) {
								StapleTableManager.addFolder(table, st, uuid,
										sg.getUser().equals(GeneralComunicator.getUser()));
							} else if (st.getType().equals(GWTStaple.STAPLE_MAIL)) {
								StapleTableManager.addMail(table, st, uuid,
										sg.getUser().equals(GeneralComunicator.getUser()));
							}
						}
					}
					count++;
				}
				status.unsetGetStapleds();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("getAll", caught);
				status.unsetGetStapleds();
			}
		});
	}

	/**
	 * isIntoStaplingInGroup
	 *
	 * @param uuid
	 * @return
	 */
	public boolean isIntoStaplingInGroup(String uuid) {
		boolean found = false;

		for (GWTStaple gst : actualStapleGroup.getStaples()) {
			String uuidTemp = "";
			if (gst.getType().equals(GWTStaple.STAPLE_DOCUMENT)) {
				uuidTemp = gst.getDoc().getUuid();
			} else if (gst.getType().equals(GWTStaple.STAPLE_FOLDER)) {
				uuidTemp = gst.getFolder().getUuid();
			} else if (gst.getType().equals(GWTStaple.STAPLE_MAIL)) {
				uuidTemp = gst.getMail().getUuid();
			}
			if (uuidTemp.equals(uuid)) {
				found = true;
				break;
			}
		}

		return found;
	}

	/**
	 * stopStapling
	 */
	private void stopStapling() {
		if (!firstUUID.equals("") || !groupId.equals("")) {
			firstUUID = "";
			firstType = "";
			groupId = "";
			GeneralComunicator.setStatus(GeneralComunicator.i18nExtension("stapling.status.finished"));
			if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DESKTOP) {
				buttonStart.enable(true);
			} else {
				buttonStart.enable(false);
			}
			buttonStart.evaluateShowIcons();
			buttonStop.setVisible(false);
			enableAddButtons(true);
		}
	}

	/**
	 * isEnabledButton
	 *
	 * @param uuid
	 * @return
	 */
	private boolean isEnabledButton(String uuid) {
		boolean enabled = isValidStackWithStapling && !isIntoStaplingInGroup(uuid) && !firstUUID.equals(uuid);

		return enabled;
	}

	/**
	 * enableAddButtons
	 *
	 * @param enable
	 */
	private void enableAddButtons(boolean enable) {
		for (Button button : addButtonList) {
			button.setVisible(enable);
		}

		for (Button button : deleteButtonList) {
			button.setVisible(enable);
		}

		for (Button button : downloadButtonList) {
			button.setVisible(enable);
		}
	}

	@Override
	public void onChange(LanguageEventConstant event) {
		if (event.equals(HasLanguageEvent.LANGUAGE_CHANGED)) {
			buttonStart.setTitle(GeneralComunicator.i18nExtension("stapling.document.title"));
			buttonStop.setTitle(GeneralComunicator.i18nExtension("stapling.document.title"));
			for (Button button : addButtonList) {
				button.setTitle(GeneralComunicator.i18n("button.add"));
			}
			for (Button button : deleteButtonList) {
				button.setTitle(GeneralComunicator.i18n("button.delete"));
			}
			for (Button button : downloadButtonList) {
				button.setTitle(GeneralComunicator.i18nExtension("button.download"));
			}
			tabDocument.langRefresh();
			tabFolder.langRefresh();
			tabMail.langRefresh();
			confirmPopup.langRefresh();
		}
	}

	/**
	 * deleteStaplingGroup
	 */
	public void deleteStaplingGroup() {
		if (!groupIdMarkedToDelete.equals("")) {
			status.setDeleteStaplingGroup();
			staplingService.remove(groupIdMarkedToDelete, new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
					refresh(tabFolder.getTable(), getUuid()); // Refreshing stapling
					status.unsetDeleteStaplingGroup();
				}

				@Override
				public void onFailure(Throwable caught) {
					GeneralComunicator.showError("remove", caught);
					status.unsetDeleteStaplingGroup();
				}
			});
		}
		groupIdMarkedToDelete = "";
	}

	/**
	 * isRegistered
	 *
	 * @param uuidList
	 * @return
	 */
	public static boolean isRegistered(List<String> uuidList) {
		return uuidList.contains(UUID);
	}
}