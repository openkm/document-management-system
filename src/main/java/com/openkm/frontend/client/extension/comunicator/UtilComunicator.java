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

package com.openkm.frontend.client.extension.comunicator;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.HTML;
import com.openkm.frontend.client.util.Util;


/**
 * UtilComunicator
 *
 * @author jllort
 *
 */
public class UtilComunicator {

	/**
	 * formatSize
	 *
	 * @param size
	 * @return
	 */
	public static String formatSize(double size) {
		return Util.formatSize(size);
	}

	/**
	 * createHeaderHTML
	 *
	 * @param imageURL
	 * @param caption
	 * @return
	 */
	public static String createHeaderHTML(String imageURL, String caption) {
		return Util.createHeaderHTML(imageURL, caption);
	}

	/**
	 * imageItemHTML
	 *
	 * @param imageUrl
	 * @param title
	 * @param align
	 * @return
	 */
	public static String imageItemHTML(String imageUrl, String title, String align) {
		return Util.imageItemHTML(imageUrl, title, align);
	}

	/**
	 * menuHTML
	 *
	 * @param imageUrl
	 * @param text
	 * @return
	 */
	public static String menuHTML(String imageUrl, String text) {
		return Util.menuHTML(imageUrl, text);
	}

	/**
	 * menuHTML
	 *
	 * @param text
	 * @return
	 */
	public static String menuHTMLWithoutIcon(String text) {
		return Util.menuHTMLWithouIcon(text);
	}

	/**
	 * imageItemHTML
	 *
	 * @param imageUrl
	 * @return
	 */
	public static String imageItemHTML(String imageUrl) {
		return Util.imageHTML(imageUrl);
	}

	/**
	 * getTextAsBoldHTML
	 *
	 * @param text
	 * @param mark
	 * @return
	 */
	public static String getTextAsBoldHTML(String text, boolean mark) {
		return Util.getTextAsBoldHTML(text, mark);
	}

	/**
	 * getUserAgent
	 *
	 * @return
	 */
	public static String getUserAgent() {
		return Util.getUserAgent();
	}

	/**
	 * Get item name from path.
	 *
	 * @param path The complete item path.
	 * @return The name of the item.
	 */
	public static String getName(String path) {
		return Util.getName(path);
	}

	/**
	 * get parent
	 *
	 * @param path
	 * @return
	 */
	public static String getParent(String path) {
		return Util.getParent(path);
	}

	/**
	 * Generate HTML icon for mime-type document
	 *
	 * @param mime The document mime-type
	 * @return the html image of mime-type file
	 */
	public static String mimeImageHTML(String mime) {
		return Util.mimeImageHTML(mime);
	}

	/**
	 * hSpace
	 *
	 * @param width
	 * @return
	 */
	public static HTML hSpace(String width) {
		return Util.hSpace(width);
	}

	/**
	 * vSpace
	 *
	 * @param height
	 * @return
	 */
	public static HTML vSpace(String height) {
		return Util.vSpace(height);
	}

	/**
	 * isSearchableKey
	 *
	 * @param event
	 * @return
	 */
	public static boolean isSearchableKey(KeyUpEvent event) {
		if (event != null) {
			return Util.isSearchableKey(event);
		} else {
			return true;
		}
	}
}