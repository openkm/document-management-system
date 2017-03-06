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

package com.openkm.frontend.client.widget.startup;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jllort
 *
 */
public class StartUpPopup extends DialogBox implements ClickHandler {

	private final static int IE_SIZE_RECTIFICATION = (Util.getUserAgent().startsWith("ie") ? 2 : 0);

	private VerticalPanel vPanel;
	private VerticalPanel status;
	private ScrollPanel scrollPanel;
	private FlexTable table;
	public Button button;
	public int actual = -1;
	private List<HTML> msgList = new ArrayList<HTML>();

	public StartUpPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		vPanel = new VerticalPanel();
		status = new VerticalPanel();
		table = new FlexTable();
		button = new Button(Main.i18n("button.close"), this);
		scrollPanel = new ScrollPanel(status);
		scrollPanel.setAlwaysShowScrollBars(false);
		scrollPanel.setSize("490px", "160px");
		scrollPanel.setStyleName("okm-Input");

		status.setWidth("" + (490 - IE_SIZE_RECTIFICATION) + "px");

		vPanel.add(new HTML("&nbsp;"));
		vPanel.add(scrollPanel);
		vPanel.add(new HTML("&nbsp;"));
		vPanel.add(table);
		vPanel.add(button);
		vPanel.add(new HTML("&nbsp;"));

		button.setVisible(false);
		button.setStyleName("okm-YesButton");

		vPanel.setCellHorizontalAlignment(scrollPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(table, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(button, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(scrollPanel, HasAlignment.ALIGN_MIDDLE);

		int left = (Window.getClientWidth() - 510) / 2;
		int top = (Window.getClientHeight() - 220) / 2;
		vPanel.setWidth("510px");
		vPanel.setHeight("220px");

		for (int i = 0; i < StartUp.STARTUP_LOADING_HISTORY_SEARCH; i++) {
			table.setWidget(0, i, new Image(OKMBundleResources.INSTANCE.loadedDisabledIcon()));
		}

		setText(Main.i18n("startup.openkm"));
		setPopupPosition(left, top);
		setWidget(vPanel);
	}

	/**
	 * Add new status message
	 *
	 * @param text The text
	 * @param actual The actual status
	 */
	public void addStatus(String text, int actual) {
		HTML tmpHTML;

		// We've jumped normally due to errors, must mark as incorrect
		if (this.actual + 1 < actual) {
			Window.alert("Current error: " + this.actual + ", New error: " + actual);

			for (int i = this.actual + 1; i < actual; i++) {
				tmpHTML = new HTML("&nbsp;" + Main.get().startUp.getStatusMsg(i));
				tmpHTML.setStyleName("okm-Input-Error");
				tmpHTML.setWordWrap(false);
				status.add(tmpHTML);
				scrollPanel.ensureVisible(tmpHTML);
				table.setWidget(0, i, new Image(OKMBundleResources.INSTANCE.loadedErrorIcon()));
			}
		}

		tmpHTML = new HTML("&nbsp;" + text);
		tmpHTML.setWordWrap(false);
		msgList.add(tmpHTML);
		if (msgList.size() > 11) {
			status.remove((HTML) msgList.remove(0)); // Only shows 10 messages on panel, when arrives 10 remove the first
		}
		status.add(tmpHTML);
		scrollPanel.ensureVisible(tmpHTML);
		table.setWidget(0, actual, new Image(OKMBundleResources.INSTANCE.loadedIcon()));
		this.actual = actual;
	}

	/**
	 * jumpActual
	 */
	public void jumpActual() {
		actual++;
	}

	/**
	 * addError
	 */
	public void addError(String error) {
		HTML tmpHTML;
		tmpHTML = new HTML("&nbsp;" + error);
		tmpHTML.setStyleName("okm-Input-Error");
		tmpHTML.setWordWrap(false);
		msgList.add(tmpHTML);

		if (msgList.size() > 11) {
			// Only shows 10 messages on panel, when arrives 10 remove the first
			status.remove((HTML) msgList.remove(0));
		}

		status.add(tmpHTML);
		scrollPanel.ensureVisible(tmpHTML);
		table.setWidget(0, actual, new Image(OKMBundleResources.INSTANCE.loadedErrorIcon()));
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
	public void onClick(ClickEvent event) {
		Main.get().startUp.disable();
		hide();
	}
}
