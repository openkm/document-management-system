/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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

package com.openkm.extension.frontend.client.widget.externaltab;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Frame;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.TabFolderComunicator;
import com.openkm.frontend.client.extension.event.HasFolderEvent;
import com.openkm.frontend.client.extension.event.HasFolderEvent.FolderEventConstant;
import com.openkm.frontend.client.extension.event.handler.FolderHandlerExtension;
import com.openkm.frontend.client.extension.widget.tabfolder.TabFolderExtension;

import java.util.List;

/**
 * ExternalTabFolder
 *
 * @author sochoa
 */
public class ExternalTabFolder extends TabFolderExtension implements FolderHandlerExtension {
	private static final String UUID = "ba5bc2e4-b1de-4cdd-8e63-459fde78867a";
	public static final String KEY = "extension.external.tab.folder";
	private String name = "";
	private String url = "";
	private Frame iframe;

	/**
	 *
	 */
	public ExternalTabFolder(List<String> uuidList) {
		if (isRegistered(uuidList)) {
			iframe = new Frame("about:blank");

			iframe.getElement().setPropertyString("frameborder", "0");
			iframe.getElement().setPropertyString("marginwidth", "0");
			iframe.getElement().setPropertyString("marginheight", "0");
			iframe.getElement().setPropertyString("scrolling", "yes");

			// Commented because on IE show clear if allowtransparency=true
			iframe.getElement().setPropertyString("allowtransparency", "false");
			iframe.setStyleName("okm-Iframe");
			initWidget(iframe);
		}
	}

	/**
	 * getSimpleName
	 */
	public static String getSimpleName() {
		return "com.openkm.extension.frontend.client.widget.externaltab.ExternalTabFolder";
	}

	@Override
	public void setPixelSize(int width, int height) {
		iframe.setSize(String.valueOf(width), String.valueOf(height));
	}

	@Override
	public String getTabText() {
		return name;
	}

	@Override
	public void set(GWTFolder doc) {
	}

	@Override
	public void setVisibleButtons(boolean visible) {
	}

	@Override
	public void onChange(FolderEventConstant event) {
		if (event.equals(HasFolderEvent.FOLDER_CHANGED)) {
			String uuid = TabFolderComunicator.getFolder().getUuid();
			String finalUrl = url + (url.contains("?") ? "&" : "?" + "uuid=" + URL.encodeQueryString(uuid) + "&userId=" + GeneralComunicator.getUser());
			iframe.setUrl(finalUrl);
		}
	}

	/**
	 * isRegistered
	 */
	public static boolean isRegistered(List<String> uuidList) {
		return uuidList.contains(UUID);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
