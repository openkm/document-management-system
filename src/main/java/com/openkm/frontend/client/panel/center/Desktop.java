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

package com.openkm.frontend.client.panel.center;

import com.google.gwt.user.client.ui.Composite;
import com.openkm.frontend.client.panel.left.Navigator;

/**
 * Desktop
 *
 * @author jllort
 *
 */
public class Desktop extends Composite {

	private final static int PANEL_LEFT_WIDTH = 225;
	public final static int SPLITTER_WIDTH = 10;

	private HorizontalSplitLayoutExtended horizontalSplitLayoutPanel;
	public Navigator navigator;
	public Browser browser;
	private int totalWidthSize = 0;
	private int width = 0;
	private int height = 0;
	private int left = PANEL_LEFT_WIDTH;
	private int right = 0;
	private int contractPreviousLeft = 0;

	/**
	 * Desktop
	 */
	public Desktop() {
		horizontalSplitLayoutPanel = new HorizontalSplitLayoutExtended(new HorizontalResizeHandler() {
			@Override
			public void onResize(int leftWidth, int rightHeight) {
				resizePanels();
			}
		});
		navigator = new Navigator();
		browser = new Browser();

		horizontalSplitLayoutPanel.addWest(navigator, PANEL_LEFT_WIDTH);
		horizontalSplitLayoutPanel.add(browser);

		initWidget(horizontalSplitLayoutPanel);
	}

	/**
	 * Sets the size on initialization
	 *
	 * @param width The max width of the widget
	 * @param height The max height of the widget
	 */
	public void setSize(int width, int height) {
		totalWidthSize = width;
		this.width = width;
		this.height = height;
		left = (int) (width * 0.2);
		left = left < PANEL_LEFT_WIDTH ? PANEL_LEFT_WIDTH : left;
		right = width - (left + SPLITTER_WIDTH);
		if (right < 0) {
			right = 0;
		}

		horizontalSplitLayoutPanel.setPixelSize(width, height);
		navigator.setSize(left, height);
		browser.setSize(right, height);
		horizontalSplitLayoutPanel.setSplitPosition(navigator, left, false);
	}

	/**
	 * expandTreeView
	 */
	public void expandTreeView() {
		contractPreviousLeft = left;
		left = totalWidthSize - SPLITTER_WIDTH;
		right = 0;
		horizontalSplitLayoutPanel.setSplitPosition(navigator, left, false);
		navigator.setSize(left, height);
		browser.setSize(right, height);
	}

	/**
	 * closeTreeView
	 */
	public void closeTreeView() {
		contractPreviousLeft = left;
		left = 0;
		right = totalWidthSize - SPLITTER_WIDTH;
		horizontalSplitLayoutPanel.setSplitPosition(navigator, left, false);
		navigator.setSize(left, height);
		browser.setSize(right, height);
	}

	/**
	 * restoreNormalView
	 */
	public void restoreNormalView() {
		left = contractPreviousLeft;
		right = totalWidthSize - (left + SPLITTER_WIDTH);
		horizontalSplitLayoutPanel.setSplitPosition(navigator, left, false);
		navigator.setSize(left, height);
		browser.setSize(right, height);
	}

	/**
	 * Sets the panel width on resizing
	 */
	private void resizePanels() {
		left = horizontalSplitLayoutPanel.getLeftWidth();
		right = horizontalSplitLayoutPanel.getRightWidth();
		navigator.setSize(left, height);
		if (right > 0) {
			browser.setWidth(right);
		}
	}

	/**
	 * refreshSpliterAfterAdded
	 */
	public void refreshSpliterAfterAdded() {
		horizontalSplitLayoutPanel.setSplitPosition(navigator, left, false);
	}

	/**
	 * getWidth
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * getHeight
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * getLeft
	 */
	public int getLeft() {
		return left;
	}

	/**
	 * getRight
	 */
	public int getRight() {
		return right;
	}
}