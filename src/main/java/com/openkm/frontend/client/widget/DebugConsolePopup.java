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

package com.openkm.frontend.client.widget;

import com.allen_sauer.gwt.log.client.DivLogger;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;

/**
 * DebugConsolePopup
 *
 * @author jllort
 *
 */
public class DebugConsolePopup extends DialogBox implements ClickHandler {

	private VerticalPanel vPanel;
	private Button button;
	private HTML text;

	/**
	 * Logout popup
	 */
	public DebugConsolePopup() {
		// Establishes auto-close when click outside
		super(false, false);

		setText(Main.i18n("debug.console.label"));
		vPanel = new VerticalPanel();
		button = new Button(Main.i18n("button.close"), this);
		text = new HTML(Main.i18n("debug.enable.disable"));

		vPanel.add(new HTML("<br>"));
		vPanel.add(text);
		vPanel.add(Log.getLogger(DivLogger.class).getWidget());
		vPanel.add(new HTML("<br>"));
		vPanel.add(button);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(button, VerticalPanel.ALIGN_CENTER);

		button.setStyleName("okm-YesButton");

		super.hide();
		Log.getLogger(DivLogger.class).getWidget().setVisible(true);
		setWidget(vPanel);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
	public void onClick(ClickEvent event) {
		super.hide();
	}

	/**
	 * Lang refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("debug.console.label"));
		text.setHTML(Main.i18n("debug.enable.disable"));
	}
}