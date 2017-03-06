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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.service.OKMAuthService;
import com.openkm.frontend.client.service.OKMAuthServiceAsync;

/**
 * Logout
 *
 * @author jllort
 */
public class LogoutPopup extends DialogBox implements ClickHandler {
	private VerticalPanel vPanel;
	private HTML text;
	private Button button;

	private final OKMAuthServiceAsync authService = (OKMAuthServiceAsync) GWT.create(OKMAuthService.class);

	/**
	 * Logout popup
	 */
	public LogoutPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		vPanel = new VerticalPanel();
		text = new HTML(Main.i18n("logout.logout"));
		button = new Button(Main.i18n("button.close"), this);

		vPanel.setWidth("250px");
		vPanel.setHeight("100px");

		vPanel.add(new HTML("<br>"));
		vPanel.add(text);
		vPanel.add(new HTML("<br>"));
		vPanel.add(button);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(text, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(button, VerticalPanel.ALIGN_CENTER);

		button.setStyleName("okm-YesButton");

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * OKM logout
	 */
	final AsyncCallback<Object> callbackLogout = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			text.setText(Main.i18n("logout.closed"));
			button.setEnabled(true);
		}

		public void onFailure(Throwable caught) {
			Log.debug("callbackLogout.onFailure(" + caught + ")");
			Main.get().showError("Logout", caught);
		}
	};

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
	public void onClick(ClickEvent event) {
		super.hide();
		Window.open("index.jsp", "_self", null);
	}

	/**
	 * Lang refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("logout.label"));
		text.setText(Main.i18n("logout.logout"));
		button.setText(Main.i18n("button.close"));
	}

	/**
	 * OKM Logout
	 */
	public void logout() {
		button.setEnabled(false);
		int left = (Window.getClientWidth() - 300) / 2;
		int top = (Window.getClientHeight() - 200) / 2;
		setPopupPosition(left, top);
		setText(Main.i18n("logout.label"));
		show();
		Log.debug("Logout()");

		if (Main.get().mainPanel.bottomPanel.userInfo.isConnectedToChat()) {
			text.setText(Main.i18n("chat.logout"));
			disconnectChat();
		} else {
			text.setText(Main.i18n("logout.logout"));
			authService.logout(callbackLogout);
			Log.debug("Logout: void");
		}
	}

	/**
	 * disconnectChat
	 *
	 * Recursivelly disconnecting chat rooms and chat before login out
	 */
	private void disconnectChat() {
		Main.get().mainPanel.bottomPanel.userInfo.logoutChat();
		logoutAfterChat();
	}

	/**
	 * logoutAfterChat();
	 */
	private void logoutAfterChat() {
		Timer timer = new Timer() {
			@Override
			public void run() {
				if (!Main.get().mainPanel.bottomPanel.userInfo.isPendingToClose()) {
					authService.logout(callbackLogout);
				} else {
					logoutAfterChat();
				}
			}
		};

		timer.schedule(100); // Each minute seconds refreshing connected users
		Log.debug("Logout: void");
	}
}