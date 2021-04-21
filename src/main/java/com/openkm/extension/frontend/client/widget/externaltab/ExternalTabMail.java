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
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.TabMailComunicator;
import com.openkm.frontend.client.extension.event.HasMailEvent;
import com.openkm.frontend.client.extension.event.HasMailEvent.MailEventConstant;
import com.openkm.frontend.client.extension.event.handler.MailHandlerExtension;
import com.openkm.frontend.client.extension.widget.tabmail.TabMailExtension;

import java.util.List;

/**
 * ExternalTabMail
 *
 * @author sochoa
 */
public class ExternalTabMail extends TabMailExtension implements MailHandlerExtension {
	private static final String UUID = "80db33ae-a702-41ff-8818-e72daf0ba068";
	public static final String KEY = "extension.external.tab.mail";
	private String name = "";
	private String url = "";
	private Frame iframe;

	/**
	 *
	 */
	public ExternalTabMail(List<String> uuidList) {
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
		return "com.openkm.extension.frontend.client.widget.externaltab.ExternalTabMail";
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
	public void set(GWTMail mail) {
	}

	@Override
	public void setVisibleButtons(boolean visible) {
	}

	@Override
	public void onChange(MailEventConstant event) {
		if (event.equals(HasMailEvent.MAIL_CHANGED)) {
			String uuid = TabMailComunicator.getMail().getUuid();
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
