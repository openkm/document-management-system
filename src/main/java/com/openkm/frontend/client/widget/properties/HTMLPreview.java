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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;

/**
 * HTMLPreview
 *
 * @author jllort
 *
 */
public class HTMLPreview extends Composite {
	private Frame iframe;

	/**
	 * HTMLPreview
	 */
	public HTMLPreview() {
		iframe = new Frame("about:blank");

		DOM.setElementProperty(iframe.getElement(), "frameborder", "0");
		DOM.setElementProperty(iframe.getElement(), "marginwidth", "0");
		DOM.setElementProperty(iframe.getElement(), "marginheight", "0");
		//DOM.setElementProperty(iframe.getElement(), "scrolling", "yes");

		// Commented because on IE show clear if allowtransparency=true
		DOM.setElementProperty(iframe.getElement(), "allowtransparency", "false");

		iframe.setStyleName("okm-Iframe");
		iframe.addStyleName("okm-EnableSelect");

		initWidget(iframe);
	}

	/**
	 * showHTML
	 */
	public void showHTML(GWTDocument doc) {
		String core = Main.get().workspaceUserProperties.getWorkspace().getHtmlSyntaxHighlighterCore();
		String theme = Main.get().workspaceUserProperties.getWorkspace().getHtmlSyntaxHighlighterTheme();
		iframe.setUrl(Main.CONTEXT + "/HtmlPreview?mimeType=" + doc.getMimeType() + "&core=" + core
				+ "&theme=" + theme + "&uuid=" + doc.getUuid());
	}

	/**
	 * isPreviewAvailable
	 */
	public static boolean isPreviewAvailable(String mime) {
		return mime.equals("text/html");
	}
}