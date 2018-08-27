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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.*;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.extension.event.handler.PropertyGroupHandlerExtension;
import com.openkm.frontend.client.extension.event.hashandler.HasPropertyGroupHandlerExtension;
import com.openkm.frontend.client.service.OKMRepositoryService;
import com.openkm.frontend.client.service.OKMRepositoryServiceAsync;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.util.validator.ValidatorToFire;
import com.openkm.frontend.client.widget.ConfirmPopup;
import com.openkm.frontend.client.widget.filebrowser.FileBrowser;
import com.openkm.frontend.client.widget.propertygroup.PropertyGroupWidget;
import com.openkm.frontend.client.widget.propertygroup.PropertyGroupWidgetToFire;

import java.util.Arrays;

/**
 * PropertyGroup
 *
 * @author jllort
 */
public class PropertyGroup extends Composite implements HasPropertyGroupHandlerExtension, ValidatorToFire {
	private final OKMRepositoryServiceAsync repositoryService = GWT.create(OKMRepositoryService.class);
	
	private ScrollPanel scrollPanel;
	private PropertyGroupWidget propertyGroupWidget;
	private Button changeButton;
	private Button removeButton;
	private Button cancelButton;
	private boolean editValues = false;
	private FiredHorizontalPanel hPanelFired;
	private GWTPropertyGroup propertyGroup;
	private Object node;
	private String path = "";
	private String uuid = "";

	/**
	 * PropertyGroup
	 */
	public PropertyGroup(GWTPropertyGroup propertyGroup, final Object node, boolean visible, boolean readOnly) {
		this.node = node;		
		int permissions = 0;

		if (node instanceof GWTDocument) {
			path = ((GWTDocument) node).getPath();
			uuid = ((GWTDocument) node).getUuid();
			permissions = ((GWTDocument) node).getPermissions();
		} else if (node instanceof GWTFolder) {
			path = ((GWTFolder) node).getPath();
			uuid = ((GWTFolder) node).getUuid();
			permissions = ((GWTFolder) node).getPermissions();
		} else if (node instanceof GWTMail) {
			path = ((GWTMail) node).getPath();
			uuid = ((GWTMail) node).getUuid();
			permissions = ((GWTMail) node).getPermissions();
		}	

		hPanelFired = new FiredHorizontalPanel();
		propertyGroupWidget = new PropertyGroupWidget(path, propertyGroup, hPanelFired, hPanelFired, this);
		scrollPanel = new ScrollPanel(propertyGroupWidget);
		this.propertyGroup = propertyGroup;

		changeButton = new Button(Main.i18n("button.change"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!editValues) {
					Main.get().mainPanel.disableKeyShorcuts(); // Disables key shortcuts while updating
					changeButton.setHTML(Main.i18n("button.memory"));
					cancelButton.setVisible(true);
					edit();
					editValues = true;
					removeButton.setVisible(false);
				} else {
					propertyGroupWidget.getValidationProcessor().validate(Arrays.asList(uuid)); // Allow plugins
				}
			}
		});

		removeButton = new Button(Main.i18n("button.delete"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!editValues) {
					Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_PROPERTY_GROUP);
					Main.get().confirmPopup.show();
				}
			}
		});

		cancelButton = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (editValues) {
					Main.get().mainPanel.enableKeyShorcuts(); // Enables general keys applications
					changeButton.setHTML(Main.i18n("button.change"));
					editValues = false;
					cancelEdit();
					removeButton.setVisible(true);
					cancelButton.setVisible(false);
				}
			}
		});

		// Checking button permisions
		if (!visible) {
			changeButton.setVisible(visible);
			removeButton.setVisible(visible);
		} else if ((Main.get().workspaceUserProperties.getWorkspace().getSecurityExtendedMask() & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP) {
			if ((Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_THESAURUS
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_CATEGORIES
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_METADATA)
					&& ((permissions & GWTPermission.WRITE) == GWTPermission.WRITE)) {
				changeButton.setVisible(!readOnly);
				removeButton.setVisible(true);
			} else if (((permissions & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP)) {
				changeButton.setVisible(!readOnly);
				removeButton.setVisible(true);
			} else {
				changeButton.setVisible(false);
				removeButton.setVisible(false);
			}
		} else if ((Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_THESAURUS
				|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_CATEGORIES
				|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_METADATA)
				&& ((permissions & GWTPermission.WRITE) == GWTPermission.WRITE)) {
			changeButton.setVisible(!readOnly);
			removeButton.setVisible(true);
		} else if (((permissions & GWTPermission.WRITE) == GWTPermission.WRITE)) {
			changeButton.setVisible(!readOnly);
			removeButton.setVisible(true);
		} else {
			changeButton.setVisible(false);
			removeButton.setVisible(false);
		}

		hPanelFired.add(changeButton);
		hPanelFired.add(Util.hSpace("10px"));
		hPanelFired.add(cancelButton);

		if (Main.get().workspaceUserProperties.getWorkspace().getProfileToolbar().isRemovePropertyGroupVisible()) {
			hPanelFired.add(Util.hSpace("10px"));
			hPanelFired.add(removeButton);
		}

		cancelButton.setVisible(false);  // Not shows cancel button

		// Button format
		changeButton.setStyleName("okm-ChangeButton");
		removeButton.setStyleName("okm-DeleteButton");
		cancelButton.setStyleName("okm-NoButton");

		getProperties(false);
		initWidget(scrollPanel);
	}

	/**
	 * isRemovePropertyGroupEnabled
	 */
	public boolean isRemovePropertyGroupEnabled() {
		return removeButton.isVisible();
	}

	/**
	 * @return
	 */
	public boolean isUpdatePropertyGrouupEnabled() {
		return changeButton.isEnabled() && changeButton.isVisible();
	}

	/**
	 * Lang refresh
	 */
	public void langRefresh() {
		changeButton.setHTML(Main.i18n("button.change"));
		removeButton.setHTML(Main.i18n("button.delete"));
	}

	/**
	 * Gets all group properties
	 */
	private void getProperties(boolean suggestion) {
		propertyGroupWidget.getProperties(suggestion);
	}

	/**
	 * Remove the document property group
	 */
	public void removeGroup() {
		propertyGroupWidget.removeGroup();
	}

	/**
	 * set document property group
	 */
	public void setProperties() {
		Main.get().mainPanel.desktop.browser.tabMultiple.status.setGroupProperties();
		propertyGroupWidget.setProperties();
	}

	/**
	 * edit document property group
	 */
	public void edit() {
		propertyGroupWidget.edit();
	}

	/**
	 * edit document property group
	 */
	public void cancelEdit() {
		propertyGroupWidget.cancelEdit();
	}

	/**
	 * Gets the group name
	 *
	 * @return The group name
	 */
	public String getGrpName() {
		return propertyGroup.getName();
	}

	/**
	 * Gets the group label
	 *
	 * @return The group label
	 */
	public String getGrpLabel() {
		return propertyGroup.getLabel();
	}

	/**
	 * FiredHorizontalPanel
	 *
	 * @author jllort
	 */
	private class FiredHorizontalPanel extends Composite implements PropertyGroupWidgetToFire {
		private HorizontalPanel hPanel;

		public FiredHorizontalPanel() {
			hPanel = new HorizontalPanel();
			initWidget(hPanel);
		}

		@Override
		public void finishedGetProperties() {
		}

		@Override
		public void finishedSetProperties() {
			if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_THESAURUS
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_CATEGORIES
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_METADATA) {
				Main.get().activeFolderTree.refresh(true);
			} else {
				refreshingActualNode();
				
				// If the node has been moved, open in file browser
				repositoryService.getPathByUUID(uuid, new AsyncCallback<String>() {
					@Override
					public void onSuccess(String nodePath) {
						if (!nodePath.equalsIgnoreCase(path)) {
							CommonUI.openPathByUuid(uuid);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Main.get().showError("getPathByUUID", caught);
					}
				});
			}
						
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetGroupProperties();
		}

		@Override
		public void finishedRemoveGroup() {
			if (Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_THESAURUS
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_CATEGORIES
					|| Main.get().mainPanel.desktop.navigator.getStackIndex() == UIDesktopConstants.NAVIGATOR_METADATA) {
				Main.get().activeFolderTree.refresh(true);
			} else {
				refreshingActualNode();
			}
		}

		/**
		 * add
		 *
		 * @param widget
		 */
		public void add(Widget widget) {
			hPanel.add(widget);
		}

		/**
		 * Used when properties are changed and filebrowser has extra columns
		 * refreshingActualNode
		 */
		private void refreshingActualNode() {
			GWTProfileFileBrowser pfb = Main.get().workspaceUserProperties.getWorkspace().getProfileFileBrowser();
			if (pfb.isExtraColumns() && Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
				if (node instanceof GWTDocument) {
					Main.get().mainPanel.desktop.browser.fileBrowser.setFileBrowserAction(FileBrowser.ACTION_PROPERTY_GROUP_REFRESH_DOCUMENT);
					Main.get().mainPanel.desktop.browser.fileBrowser.refreshDocumentValues();
				} else if (node instanceof GWTFolder) {
					Main.get().mainPanel.desktop.browser.fileBrowser.setFileBrowserAction(FileBrowser.ACTION_PROPERTY_GROUP_REFRESH_FOLDER);
					Main.get().mainPanel.desktop.browser.fileBrowser.refreshFolderValues();
				} else if (node instanceof GWTMail) {
					Main.get().mainPanel.desktop.browser.fileBrowser.setFileBrowserAction(FileBrowser.ACTION_PROPERTY_GROUP_REFRESH_MAIL);
					Main.get().mainPanel.desktop.browser.fileBrowser.refreshMailValues();
				}
			}
		}
	}

	@Override
	public void addPropertyGroupHandlerExtension(PropertyGroupHandlerExtension handlerExtension) {
		propertyGroupWidget.addPropertyGroupHandlerExtension(handlerExtension);
	}
	
	@Override
	public void validationWithPluginsFinished(boolean result) {
		if (result) {
			Main.get().mainPanel.enableKeyShorcuts(); // Enables  general keys applications
			changeButton.setHTML(Main.i18n("button.change"));
			setProperties();
			editValues = false;
			removeButton.setVisible(true);
			cancelButton.setVisible(false);
		}
	}
}
