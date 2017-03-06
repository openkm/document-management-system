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

package com.openkm.frontend.client.widget.foldertree;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.openkm.frontend.client.Main;

/**
 * ExtendedPopupPanel
 *
 * @author jllort
 *
 */
public class ExtendedPopupPanel extends PopupPanel {

	/**
	 * ExtendedPopupPanel
	 */
	public ExtendedPopupPanel(boolean autoHide, boolean modal) {
		super(autoHide, modal);

		// Ensures when mouseup / onclick / ondblclick event is disabled drag flag and not consumed by popup
		Event.addNativePreviewHandler(new NativePreviewHandler() {
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				int type = event.getTypeInt();
				if (type == Event.ONMOUSEUP || type == Event.ONCLICK || type == Event.ONDBLCLICK) {
					Main.get().activeFolderTree.disableDragged();
				}
			}
		});
	}
}