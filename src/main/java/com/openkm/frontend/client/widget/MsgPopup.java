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

package com.openkm.frontend.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTUINotification;
import com.openkm.frontend.client.util.Util;

/**
 * Message popup
 *
 * @author jllort
 *
 */
public class MsgPopup extends DialogBox {
	// private PopupPanel panel;
	private VerticalPanel vPanel;
	private Button buttonClean;
	private Button buttonClose;
	private ScrollPanel sPanel;
	private FlexTable table;
	private int lastId = -1;

	/**
	 * MsgPopup
	 */
	public MsgPopup() {
		// Establishes auto-close when click outside
		super(false, true);
		setTitle(Main.i18n("msg.title"));

		table = new FlexTable();
		table.setCellPadding(2);
		table.setCellSpacing(0);
		table.setWidth("100%");
		vPanel = new VerticalPanel();
		sPanel = new ScrollPanel();

		buttonClean = new Button(Main.i18n("button.clean"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
				hide();
			}
		});
		buttonClean.setStyleName("okm-CleanButton");
		buttonClose = new Button(Main.i18n("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		buttonClose.setStyleName("okm-YesButton");

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(buttonClean);
		hPanel.add(Util.hSpace("5px"));
		hPanel.add(buttonClose);

		vPanel.setWidth("550px");
		vPanel.setHeight("290px");
		sPanel.setWidth("530px");
		sPanel.setHeight("250px");
		sPanel.setStyleName("okm-Popup-text");

		vPanel.add(new HTML("<br>"));
		sPanel.add(table);
		vPanel.add(sPanel);
		vPanel.add(new HTML("<br>"));
		vPanel.add(buttonClose);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(table, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(sPanel, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(buttonClose, VerticalPanel.ALIGN_CENTER);

		hide();
		setWidget(vPanel);
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		buttonClose.setText(Main.i18n("button.close"));
		setTitle(Main.i18n("msg.title"));
	}

	/**
	 * reset
	 */
	private void reset() {
		table.removeAllRows();
		evaluateButtons();
	}

	/**
	 * Add message notification.
	 */
	public void add(GWTUINotification uin) {
		int row = table.getRowCount();
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		table.setHTML(row, 0, "<b>" + dtf.format(uin.getDate()) + "</b>");
		table.setHTML(row, 1, uin.getMessage());
		table.getCellFormatter().setWidth(row, 1, "100%");
		table.getCellFormatter().setVerticalAlignment(row, 0, HasAlignment.ALIGN_TOP);
		table.getCellFormatter().setVerticalAlignment(row, 1, HasAlignment.ALIGN_TOP);
		table.getCellFormatter().setStyleName(row, 0, "okm-NoWrap");
		if (uin.getId() > lastId) {
			if (uin.getAction() == GWTUINotification.ACTION_LOGOUT) {
				row++;
				int seconds = 240;
				HTML countDown = new HTML(Main.i18n("ui.logout") + " " + secondsToHTML(seconds));
				table.setWidget(row, 0, countDown);
				table.getFlexCellFormatter().setColSpan(row, 0, 2);
				table.getCellFormatter().setHorizontalAlignment(row, 0, HasAlignment.ALIGN_CENTER);
				logout(countDown, seconds);
				show();
			}
			if (uin.isShow()) {
				show();
			}
		}
		evaluateButtons();
	}

	@Override
	public void show() {
		setPopupPosition(Window.getClientWidth() - (550 + 20), Window.getClientHeight() - (290 + 80));
		evaluateButtons();
		super.show();
	}

	/**
	 * evaluateButtons
	 */
	private void evaluateButtons() {
		buttonClean.setEnabled(table.getRowCount() > 0);
	}

	/**
	 * setLastUIId
	 *
	 * @param id
	 */
	public void setLastUIId(int id) {
		this.lastId = id;
	}

	/**
	 * logout
	 */
	private void logout(final HTML countDown, final int seconds) {
		Timer timer = new Timer() {
			@Override
			public void run() {
				countDown.setHTML(Main.i18n("ui.logout") + " " + secondsToHTML(seconds));

				if (seconds > 1) {
					logout(countDown, seconds - 1);
				} else {
					hide();
					Main.get().logoutPopup.logout();
				}
			}
		};

		timer.schedule(1000);
	}

	/**
	 * secondsToHTML
	 */
	private String secondsToHTML(int seconds) {
		return "0" + (seconds / 60) + ":" + ((seconds % 60 > 9) ? (seconds % 60) : "0" + (seconds % 60));
	}
}