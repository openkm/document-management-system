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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;

/**
 * Error popup
 *
 * @author jllort
 *
 */
public class ErrorPopup extends DialogBox implements ClickHandler {
	private VerticalPanel vPanel;
	private HTML text;
	private Button button;
	private ScrollPanel sPanel;
	private boolean logout;

	/**
	 * Error popup
	 */
	public ErrorPopup(boolean logout) {
		// Establishes auto-close when click outside
		super(false, true);

		this.logout = logout;

		vPanel = new VerticalPanel();
		text = new HTML();
		sPanel = new ScrollPanel();

		if (logout) {
			button = new Button(Main.i18n("button.logout"), this);
		} else {
			button = new Button(Main.i18n("button.close"), this);
		}

		vPanel.setWidth("380px");
		vPanel.setHeight("200px");
		sPanel.setWidth("370px");
		sPanel.setHeight("170px");
		sPanel.setStyleName("okm-Popup-text");

		vPanel.add(new HTML("<br>"));
		sPanel.add(text);
		vPanel.add(sPanel);
		vPanel.add(new HTML("<br>"));
		vPanel.add(button);
		vPanel.add(new HTML("<br>"));

		text.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		vPanel.setCellHorizontalAlignment(sPanel, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(button, VerticalPanel.ALIGN_CENTER);

		button.setStyleName("okm-YesButton");

		hide();
		setWidget(vPanel);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
	public void onClick(ClickEvent event) {
		Log.debug("onClick(" + event + ")");
		hide();
		// Removes all previous text for next errors messages, varios errors can be added simultanealy
		// on show(String msg )
		text.setText("");

		if (logout) {
			Log.debug("onClick: redirect");
			Window.open("index.jsp", "_self", "");
		}

		Log.debug("onClick: void");
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("error.label"));
		button.setText(Main.i18n("button.close"));
	}

	/**
	 * Show the popup error
	 *
	 * @param msg Error message
	 */
	public void show(String msg) {
		//TODO: aqui pueden haber problemas de concurrencia al ser llamado simultaneamente este método
		// cabe la posibilidad de perder algun mensaje de error.
		if (!text.getHTML().equals("")) {
			text.setHTML(text.getHTML() + "<br><br>" + msg);
		} else {
			text.setHTML(msg);
		}
		setText(Main.i18n("error.label"));
		int left = (Window.getClientWidth() - 380) / 2;
		int top = (Window.getClientHeight() - 200) / 2;
		setPopupPosition(left, top);
		super.show();
	}
}
