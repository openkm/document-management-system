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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.widget.eastereggs.Futurama;

/**
 * About popup
 *
 * @author jllort
 *
 */
public class AboutPopup extends DialogBox implements ClickHandler {
	private VerticalPanel vPanel;
	private Image logo;
	private HTML text;
	private HTML htmlAppVersion;
	private HTML htmlExtVersion;
	private Button button;
	private String msg = "<b>Be Open, my friend!</b><br>";
	private String copy = "&nbsp;&copy 2006-2018 OpenKM <br><br>";
	private String team = "<b>OpenKM Team</b><br><br>";
	private String web = "<a href=\"http://www.openkm.com\" target=\"_blank\">http://www.openkm.com</a><br><br>";
	private String appVersion = "Version 0.0&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	private String extVersion = "With Default Extension&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	private Futurama futurama;

	/**
	 * About popup
	 */
	public AboutPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		futurama = new Futurama();
		int left = (Window.getClientWidth() - 300) / 2;
		int top = (Window.getClientHeight() - 280) / 2;

		vPanel = new VerticalPanel();
		text = new HTML();
		button = new Button(Main.i18n("button.close"), this);
		logo = new Image("img/logo_openkm.gif");

		vPanel.setWidth("300px");
		vPanel.setHeight("195px");
		button.setStyleName("okm-YesButton");

		text.setHTML(msg);

		vPanel.add(new HTML("<br>"));
		vPanel.add(logo);
		htmlAppVersion = new HTML(appVersion);
		vPanel.add(htmlAppVersion);
		htmlExtVersion = new HTML(extVersion);
		vPanel.add(htmlExtVersion);
		vPanel.add(new HTML("<br>"));
		vPanel.add(text);
		HTML htmlWeb = new HTML(web);
		vPanel.add(htmlWeb);
		HTML htmlTeam = new HTML(team);
		vPanel.add(htmlTeam);
		HTML htmlCopy = new HTML(copy);
		vPanel.add(htmlCopy);
		vPanel.add(button);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(logo, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(htmlAppVersion, HasAlignment.ALIGN_RIGHT);
		vPanel.setCellHorizontalAlignment(htmlExtVersion, HasAlignment.ALIGN_RIGHT);
		vPanel.setCellHorizontalAlignment(text, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(htmlWeb, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(htmlTeam, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(htmlCopy, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(button, HasAlignment.ALIGN_CENTER);

		setPopupPosition(left, top);

		super.hide();
		setWidget(vPanel);
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = "Version " + appVersion + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		htmlAppVersion.setHTML(this.appVersion);
	}

	public void setExtVersion(String extVersion) {
		this.extVersion = "With " + extVersion + " Extension&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		htmlExtVersion.setHTML(this.extVersion);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
	public void onClick(ClickEvent event) {
		super.hide();
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("about.label"));
		button.setText(Main.i18n("button.close"));
	}

	/**
	 * Show the popup error
	 *
	 * @param msg Error message
	 */
	public void show() {
		setText(Main.i18n("about.label"));
		text.setHTML(msg);
		logo.setUrl("img/logo_openkm.gif");
		reset();
		super.show();
	}

	/**
	 * Change the image
	 *
	 * @param img The image
	 */
	public void changeImg(String img) {
		logo.setUrl(img);
	}

	/**
	 * Sets the text ( used by easter egg )
	 *
	 * @param msg The text
	 */
	public void setText(String msg) {
		text.setHTML(msg);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.DialogBox#onPreviewNativeEvent(com.google.gwt.user.client.Event.NativePreviewEvent)
	 */
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		if (event.getTypeInt() == Event.ONKEYPRESS) {
			futurama.evaluateKey((char) event.getNativeEvent().getKeyCode());
		}
	}

	/**
	 * Reset values
	 */
	public void reset() {
		futurama.reset();
	}
}