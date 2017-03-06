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

package com.openkm.extension.frontend.client.widget.macros;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.openkm.extension.frontend.client.bean.GWTMacros;
import com.openkm.extension.frontend.client.service.OKMMacrosService;
import com.openkm.extension.frontend.client.service.OKMMacrosServiceAsync;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.extension.comunicator.*;
import com.openkm.frontend.client.extension.event.*;
import com.openkm.frontend.client.extension.event.HasDocumentEvent.DocumentEventConstant;
import com.openkm.frontend.client.extension.event.HasFolderEvent.FolderEventConstant;
import com.openkm.frontend.client.extension.event.HasLanguageEvent.LanguageEventConstant;
import com.openkm.frontend.client.extension.event.HasMailEvent.MailEventConstant;
import com.openkm.frontend.client.extension.event.HasWorkspaceEvent.WorkspaceEventConstant;
import com.openkm.frontend.client.extension.event.handler.*;
import com.openkm.frontend.client.extension.widget.toolbar.ToolBarButtonExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * Macros
 *
 * @author jllort
 */
public class Macros implements DocumentHandlerExtension, FolderHandlerExtension, MailHandlerExtension, WorkspaceHandlerExtension,
		LanguageHandlerExtension {
	public static final int TAB_DOCUMENT = 0;
	public static final int TAB_FOLDER = 1;
	public static final int TAB_MAIL = 2;

	private final OKMMacrosServiceAsync macrosService = (OKMMacrosServiceAsync) GWT.create(OKMMacrosService.class);
	public static Macros singleton;
	public static final String UUID = "c60082c2-7d4c-4750-901b-a817f246cfa1";

	private ToolBarButton button;
	private boolean enabled = false;
	private List<GWTMacros> actions = new ArrayList<GWTMacros>();
	private int selectedPanel = TAB_FOLDER;
	private GWTMacros selectedAction;
	public Status status;

	/**
	 * FastAction
	 *
	 * @param uuidList
	 */
	public Macros(List<String> uuidList) {
		if (isRegistered(uuidList)) {
			singleton = this;
			status = new Status();
			status.setStyleName("okm-StatusPopup");
			button = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.fastActionDisabled()),
					GeneralComunicator.i18nExtension("macros.button.title"), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (enabled) {
						status.setExecuteAction();
						macrosService.executeAction(selectedAction, getPath(), new AsyncCallback<Object>() {
							@Override
							public void onSuccess(Object result) {
								switch (selectedPanel) {
									case TAB_DOCUMENT:
									case TAB_MAIL:
										GeneralComunicator.refreshUI();
										break;
									case TAB_FOLDER:
										// Calculating new folder path
										String fldPath = getPath();
										fldPath = fldPath.substring(0, fldPath.lastIndexOf("/"));
										GeneralComunicator.openAllFolderPath(fldPath, null);
										break;
								}
								status.unsetExecuteAction();
							}

							@Override
							public void onFailure(Throwable caught) {
								status.unsetExecuteAction();
								GeneralComunicator.showError("executeAction", caught);
							}
						});
					}
				}
			});
			macrosService.getActions(new AsyncCallback<List<GWTMacros>>() {
				@Override
				public void onSuccess(List<GWTMacros> result) {
					actions = result;
				}

				@Override
				public void onFailure(Throwable caught) {
					GeneralComunicator.showError("getActions", caught);
				}
			});
		}
	}

	/**
	 * getExtensions
	 */
	public List<Object> getExtensions() {
		List<Object> extensions = new ArrayList<Object>();
		extensions.add(singleton);
		extensions.add(button);
		return extensions;
	}

	/**
	 * getPath
	 */
	public String getPath() {
		switch (selectedPanel) {
			case TAB_DOCUMENT:
				if (TabDocumentComunicator.getDocument() != null) {
					return TabDocumentComunicator.getDocument().getPath();
				} else {
					return null;
				}

			case TAB_FOLDER:
				if (TabFolderComunicator.getFolder() != null) {
					return TabFolderComunicator.getFolder().getPath();
				} else {
					return null;
				}

			case TAB_MAIL:
				if (TabMailComunicator.getMail() != null) {
					return TabMailComunicator.getMail().getPath();
				} else {
					return null;
				}

			default:
				return null;
		}
	}

	@Override
	public void onChange(DocumentEventConstant event) {
		if (event.equals(HasDocumentEvent.DOCUMENT_CHANGED)) {
			selectedPanel = TAB_DOCUMENT;
			enabled = actionFound(getPath());
			button.evaluateShowIcon();
		}
	}

	@Override
	public void onChange(FolderEventConstant event) {
		if (event.equals(HasFolderEvent.FOLDER_CHANGED)) {
			selectedPanel = TAB_FOLDER;
			enabled = actionFound(getPath());
			button.evaluateShowIcon();
		}
	}

	@Override
	public void onChange(MailEventConstant event) {
		if (event.equals(HasMailEvent.MAIL_CHANGED)) {
			selectedPanel = TAB_MAIL;
			enabled = actionFound(getPath());
			button.evaluateShowIcon();
		}
	}

	@Override
	public void onChange(WorkspaceEventConstant event) {
		if (event.equals(HasWorkspaceEvent.STACK_CHANGED)) {
			if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DESKTOP) {
				enabled = actionFound(getPath());
			} else {
				enabled = false;
			}
			button.evaluateShowIcon();
		}
	}

	/**
	 * actionFound
	 *
	 * @param path
	 * @return
	 */
	private boolean actionFound(String path) {
		boolean found = false;
		selectedAction = null;
		if (path != null) {
			for (GWTMacros action : actions) {
				if (path.startsWith(action.getPathOrigin())) {
					selectedAction = action;
					found = true;
					break;
				}
			}
		}
		return found;
	}

	@Override
	public void onChange(LanguageEventConstant event) {
		if (event.equals(HasLanguageEvent.LANGUAGE_CHANGED)) {
			button.setTitle(GeneralComunicator.i18nExtension("macros.button.title"));
		}
	}

	/**
	 * ToolBarButton
	 *
	 * @author jllort
	 */
	private class ToolBarButton extends ToolBarButtonExtension {

		public ToolBarButton(Image image, String title, ClickHandler handler) {
			super(image, title, handler);
		}

		@Override
		public void checkPermissions(GWTFolder folder, GWTFolder folderParent, int originPanel) {
		}

		@Override
		public void checkPermissions(GWTDocument doc, GWTFolder folder) {
		}

		@Override
		public void checkPermissions(GWTMail mail, GWTFolder folder) {
		}

		@Override
		public void enable(boolean enable) {
		}

		@Override
		public boolean isEnabled() {
			return enabled;
		}

		/**
		 * evaluateShowIcon
		 */
		public void evaluateShowIcon() {
			if (enabled) {
				enable();
			} else {
				disable();
			}
		}

		/**
		 * enable
		 */
		private void enable() {
			setStyleName("okm-ToolBar-button");
			setResource(OKMBundleResources.INSTANCE.fastAction());
			setTitle(GeneralComunicator.i18nExtension("macros.button.title"));
		}

		/**
		 * disable
		 */
		private void disable() {
			setStyleName("okm-ToolBar-button-disabled");
			setResource(OKMBundleResources.INSTANCE.fastActionDisabled());
			setTitle(GeneralComunicator.i18nExtension("macros.button.title"));
		}
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