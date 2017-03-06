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

package com.openkm.frontend.client.widget.searchin;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.util.TimeHelper;
import com.openkm.frontend.client.util.Util;

/**
 * SearchSimple
 *
 * @author jllort
 *
 */
public class SearchSimple extends Composite {
	private ScrollPanel scrollPanel;
	private VerticalPanel vPanel;
	public TextBox fullText;
	private final static int REFRESH_WAITING_TIME = 100;
	private final static String TIME_HELPER_KEY = "SCROLL_SIMPLE_SEARCH";
	private boolean loadFinish = false;
	private boolean finalResizeInProgess = false;
	int width = 0;
	int height = 0;

	/**
	 * SearchSimple
	 */
	public SearchSimple() {
		fullText = new TextBox();
		fullText.setWidth("365px");
		vPanel = new VerticalPanel();
		HTML spacer = Util.vSpace("70px");
		vPanel.add(spacer);
		vPanel.add(fullText);
		vPanel.setCellHeight(spacer, "70px");
		vPanel.setCellVerticalAlignment(fullText, HasAlignment.ALIGN_TOP);
		vPanel.setCellHorizontalAlignment(fullText, HasAlignment.ALIGN_CENTER);
		scrollPanel = new ScrollPanel(vPanel);
		fullText.setStyleName("okm-Input");

		initWidget(scrollPanel);
	}

	@Override
	public void setPixelSize(int width, int height) {
		this.width = width;
		this.height = height;
		scrollPanel.setPixelSize(width, height);
		vPanel.setPixelSize(width, height);

		// Solve some problems with chrome
		if (loadFinish && Util.getUserAgent().equals("chrome") &&
				Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace() == UIDockPanelConstants.SEARCH) {
			if (!TimeHelper.hasControlTime(TIME_HELPER_KEY)) {
				TimeHelper.hasElapsedEnoughtTime(TIME_HELPER_KEY, REFRESH_WAITING_TIME);
				timeControl();
			} else {
				TimeHelper.changeControlTime(TIME_HELPER_KEY);
			}
		}
	}

	/**
	 * timeControl
	 */
	private void timeControl() {
		if (TimeHelper.hasElapsedEnoughtTime(TIME_HELPER_KEY, REFRESH_WAITING_TIME)) {
			if (!finalResizeInProgess) {
				finalResizeInProgess = true;
				int tmpHeight = height;
				int tmpWidth = width;

				// Solve some problems with chrome
				if (Util.getUserAgent().equals("chrome")) {
					if (tmpHeight - 20 > 0) {
						tmpHeight -= 20;
					} else {
						tmpHeight = 0;
					}
					if (width - 20 > 0) {
						tmpWidth -= 20;
					} else {
						tmpWidth = 0;
					}
					vPanel.setPixelSize(tmpWidth, tmpHeight);
				}
				new Timer() {
					@Override
					public void run() {
						vPanel.setPixelSize(width, height);
						TimeHelper.removeControlTime(TIME_HELPER_KEY);
						finalResizeInProgess = false;
					}
				}.schedule(50);
			}
		} else {
			new Timer() {
				@Override
				public void run() {
					timeControl();
				}
			}.schedule(50);
		}
	}

	/**
	 * setLoadFinish
	 */
	public void setLoadFinish() {
		loadFinish = true;
	}
}