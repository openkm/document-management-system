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

package com.openkm.extension.frontend.client.widget.extraworkspace;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.widget.tabworkspace.TabWorkspaceExtension;

/**
 * TabWorkspace
 *
 * @author jllort
 *
 */
public class TabWorkspace extends TabWorkspaceExtension {
	private VerticalPanel vPanel;
	private Frame iframe;
	private String textLabel = "";
	private Timer timer;
	private TabBar tabBar;
	private int tabIndex = 0;

	/**
	 * TabWorkspace
	 */
	public TabWorkspace() {
		vPanel = new VerticalPanel();
		iframe = new Frame("about:blank");

		DOM.setElementProperty(iframe.getElement(), "frameborder", "0");
		DOM.setElementProperty(iframe.getElement(), "marginwidth", "0");
		DOM.setElementProperty(iframe.getElement(), "marginheight", "0");

		// Commented because on IE show clear if allowtransparency=true
		DOM.setElementProperty(iframe.getElement(), "allowtransparency", "false");
		DOM.setElementProperty(iframe.getElement(), "scrolling", "auto");

		iframe.setUrl(Main.CONTEXT + "/extra/index.jsp");
		iframe.setStyleName("okm-Iframe");

		vPanel.add(iframe);
		vPanel.setCellHorizontalAlignment(iframe, HasAlignment.ALIGN_CENTER);

		vPanel.setWidth("100%");
		vPanel.setHeight("100%");

		// User workspace values
		if (GeneralComunicator.getWorkspace() == null) {
			timer = new Timer() {
				@Override
				public void run() {
					if (GeneralComunicator.getWorkspace() == null) {
						firstTimeLoadingWorkspace();
					} else {
						init();
					}
				}
			};

			firstTimeLoadingWorkspace();
		} else {
			init();
		}

		initWidget(vPanel);
	}

	/**
	 * firstTimeLoadingWorkspace
	 */
	private void firstTimeLoadingWorkspace() {
		timer.schedule(200);
	}

	/**
	 * init
	 */
	private void init() {
		if (!GeneralComunicator.getWorkspace().getExtraTabWorkspaceLabel().isEmpty()) {
			textLabel = GeneralComunicator.getWorkspace().getExtraTabWorkspaceLabel();
		} else {
			textLabel = "Extra";
		}

		if (tabBar != null) {
			tabBar.setTabText(tabIndex, textLabel);
		}

		if (!GeneralComunicator.getWorkspace().getExtraTabWorkspaceUrl().isEmpty()) {
			String url = GeneralComunicator.getWorkspace().getExtraTabWorkspaceUrl();
			if (url.startsWith("http")) {
				iframe.setUrl(url);
			} else {
				iframe.setUrl(Main.CONTEXT + url);
			}
		}
	}

	/**
	 * Sets the size on initialization
	 *
	 * @param width The max width of the widget
	 * @param height The max height of the widget
	 */
	public void setPixelSize(int width, int height) {
		iframe.setPixelSize(width - 2, height - 2);
	}

	/**
	 * setTextLabel
	 */
	public void setTextLabel(String textLabel) {
		this.textLabel = textLabel;
	}

	@Override
	public String getTabText() {
		return textLabel;
	}

	@Override
	public void setTab(TabBar tabBar, int tabIndex) {
		this.tabBar = tabBar;
		this.tabIndex = tabIndex;
	}
}