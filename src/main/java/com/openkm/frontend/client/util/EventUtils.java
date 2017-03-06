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

package com.openkm.frontend.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.openkm.frontend.client.util.impl.EventImpl;

/**
 * Class with additional native method implementing browser specific code
 * related to Events.
 */
public class EventUtils {
	private static final EventImpl impl = GWT.create(EventImpl.class);

	/**
	 * Fire a click event, as if the user clicked on the element.
	 *
	 * @param element Element to fire click event on
	 */
	public static void fireClickEvent(Element element) {
		impl.fireClickEvent(element);
	}

	public static boolean isArrowKey(int keyCode) {
		if (KeyCodes.KEY_LEFT == keyCode || KeyCodes.KEY_RIGHT == keyCode || KeyCodes.KEY_UP == keyCode
				|| KeyCodes.KEY_DOWN == keyCode) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNavigationKey(int keyCode) {
		if (isArrowKey(keyCode) || KeyCodes.KEY_HOME == keyCode || KeyCodes.KEY_END == keyCode ||
				KeyCodes.KEY_PAGEUP == keyCode || KeyCodes.KEY_PAGEDOWN == keyCode) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isModifierKey(int keyCode) {
		if (KeyCodes.KEY_SHIFT == keyCode || KeyCodes.KEY_ALT == keyCode || KeyCodes.KEY_CTRL == keyCode
				|| KeyCodes.KEY_DOWN == keyCode) {
			return true;
		} else {
			return false;
		}
	}
}
