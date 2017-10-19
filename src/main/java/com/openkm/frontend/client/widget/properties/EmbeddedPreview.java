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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.openkm.frontend.client.Main;

/**
 * HTMLPreview
 *
 * @author jllort
 */
public class EmbeddedPreview extends Composite {
	private Frame iframe;

	public static String PDF_URL = "pdfUrl=";
	public static String SWF_URL = "swfUrl=";
	public static String HTM_URL = "htmUrl=";
	public static String COD_URL = "codUrl=";
	public static String MPG_URL = "mpgUrl=";
	public static String PDFJS_URL = "file=";

	/**
	 * HTMLPreview
	 */
	public EmbeddedPreview() {
		iframe = new Frame("about:blank");

		iframe.getElement().setPropertyString("frameborder", "0");
		iframe.getElement().setPropertyString("marginwidth", "0");
		iframe.getElement().setPropertyString("marginheight", "0");
		iframe.getElement().setAttribute("allowFullScreen", "true");

		// Commented because on IE show clear if allowtransparency=true
		iframe.getElement().setPropertyString("allowtransparency", "false");

		iframe.setStyleName("okm-Iframe");
		iframe.addStyleName("okm-EnableSelect");

		initWidget(iframe);
	}

	/**
	 * show
	 */
	public void showEmbedded(String url) {
		iframe.setUrl(Main.CONTEXT + "/preview/pdfjs/viewer.html?" + url);
	}

	/**
	 * clear url
	 */
	public void clear() {
		iframe.setUrl("about:blank");
	}

}