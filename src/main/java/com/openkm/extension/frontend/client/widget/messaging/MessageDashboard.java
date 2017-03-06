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

package com.openkm.extension.frontend.client.widget.messaging;

import com.google.gwt.user.client.ui.Composite;
import com.openkm.extension.frontend.client.widget.messaging.stack.MessageStack;
import com.openkm.frontend.client.panel.center.HorizontalResizeHandler;
import com.openkm.frontend.client.panel.center.HorizontalSplitLayoutExtended;

/**
 * MessageDashboard
 *
 * @author jllort
 *
 */
public class MessageDashboard extends Composite {

	private final static int PANEL_LEFT_WIDTH = 225;
	public final static int SPLITTER_WIDTH = 10;

	private HorizontalSplitLayoutExtended horizontalSplitLayoutPanel;
	public MessageStack messageStack;
	public MessageBrowser messageBrowser;
	private int width = 0;
	private int height = 0;
	private int left = 0;
	private int right = 0;

	/**
	 * MessageDashboard
	 */
	public MessageDashboard() {
		horizontalSplitLayoutPanel = new HorizontalSplitLayoutExtended(new HorizontalResizeHandler() {
			@Override
			public void onResize(int leftWidth, int rightWidth) {
				resizePanels();
			}
		});
		messageStack = new MessageStack();
		messageBrowser = new MessageBrowser();

		horizontalSplitLayoutPanel.addWest(messageStack, PANEL_LEFT_WIDTH);
		horizontalSplitLayoutPanel.add(messageBrowser);

		initWidget(horizontalSplitLayoutPanel);

		// Refreshing data panels;
		refreshAll();
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.UIObject#setPixelSize(int, int)
	 */
	public void setPixelSize(int width, int height) {
		this.width = width;
		this.height = height;
		left = PANEL_LEFT_WIDTH;
		right = this.width - (PANEL_LEFT_WIDTH + SPLITTER_WIDTH);
		horizontalSplitLayoutPanel.setPixelSize(this.width, this.height);
		messageStack.setSize(left, this.height);
		messageBrowser.setSize(right, this.height);
	}


	/**
	 * Sets the panel width on resizing
	 */
	private void resizePanels() {
		left = horizontalSplitLayoutPanel.getLeftWidth();
		right = horizontalSplitLayoutPanel.getRightWidth();
		messageStack.setSize(left, height);
		messageBrowser.setWidth(right);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		messageStack.langRefresh();
		messageBrowser.langRefresh();
	}

	/**
	 * refreshAll
	 */
	public void refreshAll() {
		messageStack.proposedSubscriptionReceived.findAllProposedSubscriptions();
		messageStack.proposedQueryReceived.findAllProposedQueries();
		messageStack.messageReceived.findAllMessageReceived();
		messageStack.messageSent.findAllUsersMessageSent();
	}

	/**
	 * getWidth
	 *
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * getHeight
	 *
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * getLeft
	 *
	 * @return
	 */
	public int getLeft() {
		return left;
	}

	/**
	 * getRight
	 *
	 * @return
	 */
	public int getRight() {
		return right;
	}
}