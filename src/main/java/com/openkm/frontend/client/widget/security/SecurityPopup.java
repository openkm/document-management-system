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

package com.openkm.frontend.client.widget.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.service.OKMAuthService;
import com.openkm.frontend.client.service.OKMAuthServiceAsync;
import com.openkm.frontend.client.util.Util;

import java.util.List;
import java.util.Map;

/**
 * Security popup
 *
 * @author jllort
 *
 */
public class SecurityPopup extends DialogBox {
	private final OKMAuthServiceAsync authService = (OKMAuthServiceAsync) GWT.create(OKMAuthService.class);

	public Status status;
	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	public CheckBox recursive;
	private Button close;
	private Button change;
	private SimplePanel sp;
	public SecurityPanel securityPanel;
	private int width = 612;
	private String uuid = "";

	/**
	 * Security popup
	 */
	public SecurityPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		status = new Status();
		vPanel = new VerticalPanel();
		sp = new SimplePanel();
		securityPanel = new SecurityPanel();
		recursive = new CheckBox(Main.i18n("security.recursive"));
		close = new Button(Main.i18n("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.desktop.browser.tabMultiple.securityRefresh();
				hide();
			}
		});

		change = new Button(Main.i18n("button.change"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final boolean recursiveChecked = recursive.getValue();

				if (!recursiveChecked) {
					Main.get().securityPopup.status.setFlag_update();
				}

				List<Map<String, Integer>> userGrants = securityPanel.securityUser.getNewGrants();
				List<Map<String, Integer>> roleGrants = securityPanel.securityRole.getNewGrants();
				Map<String, Integer> addUsers = userGrants.get(0);
				Map<String, Integer> revokeUsers = userGrants.get(1);
				Map<String, Integer> addRoles = roleGrants.get(0);
				Map<String, Integer> revokeRoles = roleGrants.get(1);
				authService.changeSecurity(uuid, addUsers, revokeUsers, addRoles, revokeRoles, recursiveChecked, new AsyncCallback<Object>() {
					@Override
					public void onSuccess(Object result) {
						if (!recursiveChecked) {
							Main.get().securityPopup.status.unsetFlag_update();
							Main.get().mainPanel.desktop.browser.tabMultiple.securityRefresh();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						if (!recursiveChecked) {
							Main.get().securityPopup.status.unsetFlag_update();
						}
						Main.get().showError("changeSecurity", caught);
					}
				});

				if (recursiveChecked) {
					Timer timer = new Timer() {
						@Override
						public void run() {
							Main.get().mainPanel.desktop.browser.tabMultiple.securityRefresh();
						}
					};
					timer.schedule(200);
				}
				hide();
			}
		});

		hPanel = new HorizontalPanel();
		hPanel.add(close);

		sp.setHeight("4px");

		vPanel.add(sp);
		vPanel.add(securityPanel);
		vPanel.add(recursive);
		vPanel.add(hPanel);
		vPanel.add(Util.vSpace("5px"));

		vPanel.setCellHeight(sp, "4px");
		vPanel.setCellHeight(hPanel, "25px");
		vPanel.setCellHorizontalAlignment(securityPanel, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(hPanel, VerticalPanel.ALIGN_MIDDLE);

		vPanel.setWidth(String.valueOf(width) + "px");

		close.setStyleName("okm-NoButton");
		change.setStyleName("okm-ChangeButton");
		status.setStyleName("okm-StatusPopup");

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * initExtendedSecurity
	 */
	public void initExtendedSecurity(int extendedSecurity) {
		securityPanel.initExtendedSecurity(extendedSecurity);

		if (((extendedSecurity & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP)) {
			width += 55;
		}

		if (((extendedSecurity & GWTPermission.COMPACT_HISTORY) == GWTPermission.COMPACT_HISTORY)) {
			width += 55;
		}

		if (((extendedSecurity & GWTPermission.START_WORKFLOW) == GWTPermission.START_WORKFLOW)) {
			width += 55;
		}

		if (((extendedSecurity & GWTPermission.DOWNLOAD) == GWTPermission.DOWNLOAD)) {
			width += 55;
		}

		vPanel.setWidth(String.valueOf(width) + "px");
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("security.label"));
		recursive.setText(Main.i18n("security.recursive"));
		close.setText(Main.i18n("button.close"));
		change.setText(Main.i18n("button.change"));
		securityPanel.langRefresh();
	}

	/**
	 * Show the security popup
	 */
	public void show(String uuid) {
		this.uuid = uuid;
		int left = (Window.getClientWidth() - width) / 2;
		int top = (Window.getClientHeight() - 400) / 2;
		setPopupPosition(left, top);
		setText(Main.i18n("security.label"));
		securityPanel.reset(uuid);
		change.setEnabled(false);
		super.show();

		// TODO:Solves minor bug with IE
		if (Util.getUserAgent().startsWith("ie")) {
			securityPanel.tabPanel.setWidth(String.valueOf(width) + "px");
			securityPanel.tabPanel.setWidth(String.valueOf((width + 1)) + "px");
		}

		// Fill width must be done on visible widgets
		securityPanel.fillWidth();
	}

	/**
	 * enableAdvancedFilter
	 */
	public void enableAdvancedFilter() {
		securityPanel.enableAdvancedFilter();
	}

	/**
	 * enableSecurityModeMultiple
	 */
	public void enableSecurityModeMultiple() {
		hPanel.add(new HTML("&nbsp;"));
		hPanel.add(change);
	}

	/**
	 * @param enableChangeButton
	 */
	public void enableChangeButton(boolean enable) {
		change.setEnabled(enable);
	}
}