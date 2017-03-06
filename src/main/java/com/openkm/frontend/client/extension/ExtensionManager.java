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

package com.openkm.frontend.client.extension;

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.constants.ui.UIMenuConstants;
import com.openkm.frontend.client.extension.event.handler.*;
import com.openkm.frontend.client.extension.widget.menu.MenuItemExtension;
import com.openkm.frontend.client.extension.widget.preview.PreviewExtension;
import com.openkm.frontend.client.extension.widget.tabdocument.TabDocumentExtension;
import com.openkm.frontend.client.extension.widget.tabfolder.TabFolderExtension;
import com.openkm.frontend.client.extension.widget.tabmail.TabMailExtension;
import com.openkm.frontend.client.extension.widget.tabworkspace.TabWorkspaceExtension;
import com.openkm.frontend.client.extension.widget.toolbar.ToolBarBoxExtension;
import com.openkm.frontend.client.extension.widget.toolbar.ToolBarButtonExtension;
import com.openkm.frontend.client.extension.widget.userinfo.UserInfoExtension;

import java.util.Iterator;
import java.util.List;

/**
 * ExtensionManager
 *
 * @author jllort
 */
public class ExtensionManager {

	// Declare here your new widgets
	public static void start(List<Object> extensions) {
		for (Iterator<Object> it = extensions.iterator(); it.hasNext(); ) {
			Object obj = it.next();

			// Registering widgets
			if (obj instanceof TabDocumentExtension) {
				addTabDocumentExtension((TabDocumentExtension) obj);
			} else if (obj instanceof TabFolderExtension) {
				addTabFolderExtension((TabFolderExtension) obj);
			} else if (obj instanceof TabMailExtension) {
				addTabMailExtension((TabMailExtension) obj);
			} else if (obj instanceof ToolBarButtonExtension) {
				addToolBarButtonExtension((ToolBarButtonExtension) obj);
			} else if (obj instanceof MenuItemExtension) {
				addMenuExtension((MenuItemExtension) obj);
			} else if (obj instanceof TabWorkspaceExtension) {
				addWorkspaceExtension((TabWorkspaceExtension) obj);
			} else if (obj instanceof ToolBarBoxExtension) {
				addToolBarBoxExtension((ToolBarBoxExtension) obj);
			} else if (obj instanceof PreviewExtension) {
				addPreviewExtension((PreviewExtension) obj);
			} else if (obj instanceof UserInfoExtension) {
				addUserInfoExtension((UserInfoExtension) obj);
			}

			// Registering handlers
			if (obj instanceof DocumentHandlerExtension) {
				addDocumentHandlerExtension((DocumentHandlerExtension) obj);
			}
			if (obj instanceof FolderHandlerExtension) {
				addFolderHandlerExtension((FolderHandlerExtension) obj);
			}
			if (obj instanceof MailHandlerExtension) {
				addMailHandlerExtension((MailHandlerExtension) obj);
			}
			if (obj instanceof ToolBarHandlerExtension) {
				addToolBarHandlerExtension((ToolBarHandlerExtension) obj);
			}
			if (obj instanceof LanguageHandlerExtension) {
				addLanguageHandlerExtension((LanguageHandlerExtension) obj);
			}
			if (obj instanceof NavigatorHandlerExtension) {
				addNavigatorHandlerExtension((NavigatorHandlerExtension) obj);
			}
			if (obj instanceof WorkspaceHandlerExtension) {
				addWorkspaceHandlerExtension((WorkspaceHandlerExtension) obj);
			}
			if (obj instanceof PropertyGroupHandlerExtension) {
				addPropertyGroupHandlerExtension((PropertyGroupHandlerExtension) obj);
			}
			if (obj instanceof DashboardHandlerExtension) {
				addDashboardHandlerExtension((DashboardHandlerExtension) obj);
			}
			if (obj instanceof WidgetHandlerExtension) {
				addWidgetHandlerExtension((WidgetHandlerExtension) obj);
			}
		}
	}

	/**
	 * addTabDocumentExtension
	 *
	 * @param extension
	 */
	private static void addTabDocumentExtension(TabDocumentExtension extension) {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.addDocumentExtension(extension);
	}

	/**
	 * addTabFolderExtension
	 *
	 * @param extension
	 */
	private static void addTabFolderExtension(TabFolderExtension extension) {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.addFolderExtension(extension);
	}

	/**
	 * addTabMailExtension
	 *
	 * @param extension
	 */
	private static void addTabMailExtension(TabMailExtension extension) {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.addMailExtension(extension);
	}

	/**
	 * addMenu
	 *
	 * @param extension
	 */
	private static void addMenuExtension(MenuItemExtension extension) {
		switch (extension.getMenuLocation()) {
			case UIMenuConstants.NEW_MENU:
			case UIMenuConstants.MAIN_MENU_FILE:
			case UIMenuConstants.MAIN_MENU_EDIT:
			case UIMenuConstants.MAIN_MENU_TOOLS:
			case UIMenuConstants.MAIN_MENU_BOOKMARS:
			case UIMenuConstants.MAIN_MENU_HELP:
				Main.get().mainPanel.topPanel.mainMenu.addMenuExtension(extension);
				break;
		}
	}

	/**
	 * addToolBarButtonExtension
	 *
	 * @param extension Extension to add
	 */
	private static void addToolBarButtonExtension(ToolBarButtonExtension extension) {
		Main.get().mainPanel.topPanel.toolBar.addToolBarButtonExtension(extension);
	}

	/**
	 * addWorkspaceExtension
	 *
	 * @param extension Extension to add
	 */
	private static void addWorkspaceExtension(TabWorkspaceExtension extension) {
		Main.get().mainPanel.topPanel.tabWorkspace.addWorkspaceExtension(extension);
	}

	/**
	 * addToolBarBoxExtension
	 *
	 * @param extension Extension to add
	 */
	private static void addToolBarBoxExtension(ToolBarBoxExtension extension) {
		Main.get().mainPanel.dashboard.addToolBarBoxExtension(extension);
	}

	/**
	 * addPreviewExtension
	 *
	 * @param extension Extension to add
	 */
	private static void addPreviewExtension(PreviewExtension extension) {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.addPreviewExtension(extension);
	}

	/**
	 * addUserInfoExtension
	 *
	 * @param extension Extension to add
	 */
	private static void addUserInfoExtension(UserInfoExtension extension) {
		Main.get().mainPanel.bottomPanel.userInfo.addUserInfoExtension(extension);
	}

	/**
	 * addDocumentHandlerExtension
	 *
	 * @param handlerExtension
	 */
	private static void addDocumentHandlerExtension(DocumentHandlerExtension handlerExtension) {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.addDocumentHandlerExtension(handlerExtension);
		Main.get().mainPanel.desktop.browser.fileBrowser.addDocumentHandlerExtension(handlerExtension);
	}

	/**
	 * addFolderHandlerExtension
	 *
	 * @param handlerExtension
	 */
	private static void addFolderHandlerExtension(FolderHandlerExtension handlerExtension) {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.addFolderHandlerExtension(handlerExtension);
		Main.get().mainPanel.desktop.browser.fileBrowser.addFolderHandlerExtension(handlerExtension);
	}

	/**
	 * addMailHandlerExtension
	 *
	 * @param handlerExtension
	 */
	private static void addMailHandlerExtension(MailHandlerExtension handlerExtension) {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.addMailHandlerExtension(handlerExtension);
		Main.get().mainPanel.desktop.browser.fileBrowser.addMailHandlerExtension(handlerExtension);
	}

	/**
	 * addToolBarHandlerExtension
	 *
	 * @param handlerExtension
	 */
	private static void addToolBarHandlerExtension(ToolBarHandlerExtension handlerExtension) {
		Main.get().mainPanel.topPanel.toolBar.addToolBarHandlerExtension(handlerExtension);
	}

	/**
	 * addLanguageHandlerExtension
	 *
	 * @param handlerExtension
	 */
	private static void addLanguageHandlerExtension(LanguageHandlerExtension handlerExtension) {
		Main.get().addLanguageHandlerExtension(handlerExtension);
	}

	/**
	 * addNavigatorHandlerExtension
	 *
	 * @param handlerExtension
	 */
	private static void addNavigatorHandlerExtension(NavigatorHandlerExtension handlerExtension) {
		Main.get().mainPanel.desktop.navigator.stackPanel.addNavigatorHandlerExtension(handlerExtension);
	}

	/**
	 * addWorkspaceHandlerExtension
	 *
	 * @param handlerExtension
	 */
	private static void addWorkspaceHandlerExtension(WorkspaceHandlerExtension handlerExtension) {
		Main.get().mainPanel.topPanel.tabWorkspace.addWorkspaceHandlerExtension(handlerExtension);
	}

	/**
	 * addPropertyGroupHandlerExtension
	 *
	 * @param handlerExtension
	 */
	private static void addPropertyGroupHandlerExtension(PropertyGroupHandlerExtension handlerExtension) {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.addPropertyGroupHandlerExtension(handlerExtension);
		Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.addPropertyGroupHandlerExtension(handlerExtension);
	}

	/**
	 * addDashboardHandlerExtension
	 *
	 * @param handlerExtension
	 */
	private static void addDashboardHandlerExtension(DashboardHandlerExtension handlerExtension) {
		Main.get().mainPanel.dashboard.addDashboardHandlerExtension(handlerExtension);
	}

	/**
	 * addWidgetHandlerExtension
	 *
	 * @param handlerExtension
	 */
	private static void addWidgetHandlerExtension(WidgetHandlerExtension handlerExtension) {
		Main.get().startUp.addWidgetHandlerExtension(handlerExtension);
	}
}