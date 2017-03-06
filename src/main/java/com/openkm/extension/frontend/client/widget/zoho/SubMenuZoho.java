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

package com.openkm.extension.frontend.client.widget.zoho;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.openkm.extension.frontend.client.service.OKMZohoService;
import com.openkm.extension.frontend.client.service.OKMZohoServiceAsync;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.constants.ui.UIMenuConstants;
import com.openkm.frontend.client.extension.comunicator.*;
import com.openkm.frontend.client.extension.widget.menu.MenuBarExtension;
import com.openkm.frontend.client.extension.widget.menu.MenuItemExtension;

import java.util.Map;

/**
 * SubMenuZoho
 *
 * @author jllort
 */
public class SubMenuZoho {
	private final OKMZohoServiceAsync zohoService = (OKMZohoServiceAsync) GWT.create(OKMZohoService.class);

	private MenuItemExtension zohoMenu;
	private MenuItemExtension zohoWriterMenu;
	private MenuItemExtension zohoSheetMenu;
	private MenuBarExtension subMenu;

	private boolean writer = false;
	private boolean sheet = false;

	/**
	 * SubMenuZoho
	 */
	public SubMenuZoho() {
		// All menu items
		zohoWriterMenu = new MenuItemExtension("img/icon/actions/zoho_writer.png",
				GeneralComunicator.i18nExtension("zoho.writer"), zohoWriter);
		zohoSheetMenu = new MenuItemExtension("img/icon/actions/zoho_sheet.png",
				GeneralComunicator.i18nExtension("zoho.sheet"), zohoSheet);

		zohoWriterMenu.addStyleName("okm-MenuItem-strike");
		zohoSheetMenu.addStyleName("okm-MenuItem-strike");

		// Principal submenu
		subMenu = new MenuBarExtension();
		subMenu.addItem(zohoWriterMenu);
		subMenu.addItem(zohoSheetMenu);

		// Principal menu
		zohoMenu = new MenuItemExtension("img/icon/actions/zoho.png", GeneralComunicator.i18nExtension("zoho.menu"),
				subMenu);
		zohoMenu.setMenuLocation(UIMenuConstants.MAIN_MENU_TOOLS);
	}

	/**
	 * getMenu
	 */
	public MenuItemExtension getMenu() {
		return zohoMenu;
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		zohoWriterMenu.setHTML(UtilComunicator.menuHTML("img/icon/actions/zoho_writer.png",
				GeneralComunicator.i18nExtension("zoho.writer")));
		zohoSheetMenu.setHTML(UtilComunicator.menuHTML("img/icon/actions/zoho_sheet.png",
				GeneralComunicator.i18nExtension("zoho.sheet")));
		zohoMenu.setHTML(UtilComunicator.menuHTML("img/icon/actions/zoho.png",
				GeneralComunicator.i18nExtension("zoho.menu")));
	}

	/**
	 * zohoWriter
	 */
	Command zohoWriter = new Command() {
		public void execute() {
			if (writer) {
				// Getting last two lowercase digits
				String lang = GeneralComunicator.getLang().substring(3).toLowerCase();
				zohoService.getZohoWriterUrl(Zoho.get().getUuid(), lang, new AsyncCallback<Map<String, String>>() {
					@Override
					public void onSuccess(Map<String, String> result) {
						ZohoPopup zohoWriterPopup = new ZohoPopup("Zoho Writer", result.get("url"), result.get("id"));
						zohoWriterPopup.setStyleName("okm-Popup");
						zohoWriterPopup.center();
					}

					@Override
					public void onFailure(Throwable caught) {
						GeneralComunicator.showError("getZohoWriterUrl", caught);
					}
				});
			}
		}
	};

	/**
	 * zohoSheet
	 */
	Command zohoSheet = new Command() {
		public void execute() {
			if (sheet) {
				// Getting last two lowercase digits
				String lang = GeneralComunicator.getLang().substring(3).toLowerCase();
				zohoService.getZohoSheetUrl(Zoho.get().getUuid(), lang, new AsyncCallback<Map<String, String>>() {
					@Override
					public void onSuccess(Map<String, String> result) {
						ZohoPopup zohoPopup = new ZohoPopup("Zoho Sheet", result.get("url"), result.get("id"));
						zohoPopup.setWidth("1020px");
						zohoPopup.setHeight("760px");
						zohoPopup.setStyleName("okm-Popup");
						zohoPopup.center();
					}

					@Override
					public void onFailure(Throwable caught) {
						GeneralComunicator.showError("getZohoWriterUrl", caught);
					}
				});
			}
		}
	};

	/**
	 * evaluateMenus
	 */
	public void evaluateMenus() {
		GWTFolder folder = NavigatorComunicator.getFolder(); // The actual folder selected in navigator view
		if (FileBrowserComunicator.isDocumentSelected()) {
			GWTDocument doc = TabDocumentComunicator.getDocument(); // Always usign tab document to prevent refreshing problems
			if ((doc.getMimeType().equals("application/msword") && doc.getName().endsWith("doc"))
					|| doc.getMimeType().equals(
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document")
					|| doc.getMimeType().equals("text/html")
					|| doc.getMimeType().equals("application/pdf")
					|| (doc.getMimeType().equals("application/vnd.oasis.opendocument.text") && doc.getName().endsWith(
					"odt")) || doc.getMimeType().equals("application/rtf")
					|| doc.getMimeType().equals("text/plain")) {

				if (((doc.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)
						&& ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)) {
					if (doc.isCheckedOut() || doc.isLocked()) {
						if (doc.getLockInfo().getOwner().equals(GeneralComunicator.getUser())
								|| GeneralComunicator.getWorkspace().isAdminRole()) {
							writer = true;
							sheet = false;
						} else {
							writer = false;
							sheet = false;
						}
					} else {
						writer = true;
						sheet = false;
					}
				} else {
					writer = false;
					sheet = false;
				}

			} else if (doc.getMimeType().equals("application/vnd.ms-excel")
					|| doc.getMimeType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
					|| (doc.getMimeType().equals("application/vnd.oasis.opendocument.spreadsheet") && doc.getName()
					.endsWith("ods")) || doc.getMimeType().equals("text/csv")) {
				if (((doc.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)
						&& ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)) {
					if (doc.isCheckedOut() || doc.isLocked()) {
						if (doc.getLockInfo().getOwner().equals(GeneralComunicator.getUser())
								|| GeneralComunicator.getWorkspace().isAdminRole()) {
							writer = false;
							sheet = true;
						} else {
							writer = false;
							sheet = false;
						}
					} else {
						writer = false;
						sheet = true;
					}
				} else {
					writer = false;
					sheet = false;
				}

			} else {
				writer = false;
				sheet = false;
			}
		} else {
			writer = false;
			sheet = false;
		}

		// Enable disabling menus
		if (writer) {
			zohoWriterMenu.removeStyleName("okm-MenuItem-strike");
		} else {
			zohoWriterMenu.addStyleName("okm-MenuItem-strike");
		}

		if (sheet) {
			zohoSheetMenu.removeStyleName("okm-MenuItem-strike");
		} else {
			zohoSheetMenu.addStyleName("okm-MenuItem-strike");
		}
	}

	/**
	 * disableAllMenus
	 */
	public void disableAllMenus() {
		writer = false;
		sheet = false;
		zohoWriterMenu.addStyleName("okm-MenuItem-strike");
		zohoSheetMenu.addStyleName("okm-MenuItem-strike");
	}
}