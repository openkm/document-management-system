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

package com.openkm.frontend.client.widget.eastereggs;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * FuturamaWalking
 *
 * @author jllort
 */
public class FuturamaWalking extends PopupPanel {
	private VerticalPanel vPanel;
	private Image logo;
	private Timer move;
	private static int left = 0;
	private static int top = 0;

	/**
	 * FuturamaWalking
	 */
	public FuturamaWalking() {
		// Establishes auto-close when click outside
		super(false, false);
		left = -180;
		top = Window.getClientHeight() - 80 - 21;

		vPanel = new VerticalPanel();
		vPanel.setWidth("186px");
		vPanel.setHeight("80px");

		logo = new Image("img/eastereggs/futurama_walking.gif");
		vPanel.add(logo);

		setPopupPosition(left, top);

		hide();
		setWidget(vPanel);
	}

	/**
	 * Evaluate
	 */
	public void evaluate(String name) {
		if (name.equals("futurama")) {
			left = -180;
			top = Window.getClientHeight() - 80 - 21;
			show();
			setPopupPosition(left, top);

			move = new Timer() {
				public void run() {
					left += 2;
					setPopupPosition(left, top);

					if (left < Window.getClientWidth() + 180) {
						move.schedule(40);
					} else {
						move.cancel();
						hide();
					}
				}
			};

			move.schedule(40);
		}
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.PopupPanel#setPopupPosition(int, int)
	 */
	public void setPopupPosition(int left, int top) {
		// Removed popup not can has negative position
		Element elem = getElement();
		DOM.setStyleAttribute(elem, "left", left + "px");
		DOM.setStyleAttribute(elem, "top", top + "px");
	}
}
