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

package com.openkm.extension.frontend.client.widget.messaging.detail;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.bean.extension.*;

/**
 * Detail
 *
 * @author jllort
 *
 */
public class Detail extends Composite {
	private VerticalPanel detailPanel;
	public ProposedSubscriptionReceivedDetail proposedSubscriptionReceivedDetail;
	public ProposedSubscriptionSentDetail proposedSubscriptionSentDetail;
	public ProposedQueryReceivedDetail proposedQueryReceivedDetail;
	public ProposedQuerySentDetail proposedQuerySentDetail;
	public MessageSentDetail messageSentDetail;
	public MessageReceivedDetail messageReceivedDetail;
	public Status status;

	/**
	 * MessageDetail
	 */
	public Detail() {
		status = new Status();
		status.setStyleName("okm-StatusPopup");

		detailPanel = new VerticalPanel();
		proposedSubscriptionReceivedDetail = new ProposedSubscriptionReceivedDetail();
		proposedSubscriptionSentDetail = new ProposedSubscriptionSentDetail();
		proposedQueryReceivedDetail = new ProposedQueryReceivedDetail();
		proposedQuerySentDetail = new ProposedQuerySentDetail();
		messageReceivedDetail = new MessageReceivedDetail();
		messageSentDetail = new MessageSentDetail();

		initWidget(detailPanel);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.UIObject#setPixelSize(int, int)
	 */
	public void setPixelSize(int width, int height) {
		detailPanel.setPixelSize(width, height);
		proposedSubscriptionReceivedDetail.setPixelSize(width, height);
		proposedSubscriptionSentDetail.setPixelSize(width, height);
		proposedQueryReceivedDetail.setPixelSize(width, height);
		messageReceivedDetail.setPixelSize(width, height);
		messageSentDetail.setPixelSize(width, height);
	}

	/**
	 * reset
	 */
	public void reset() {
		while (detailPanel.getWidgetCount() > 0) {
			detailPanel.remove(0);
		}
	}


	/**
	 * set
	 *
	 * @param propose
	 */
	public void set(GWTProposedSubscriptionReceived propose) {
		reset();
		proposedSubscriptionReceivedDetail.set(propose);
		detailPanel.add(proposedSubscriptionReceivedDetail);
	}

	/**
	 * set
	 *
	 * @param propose
	 */
	public void set(GWTProposedSubscriptionSent propose) {
		reset();
		proposedSubscriptionSentDetail.set(propose);
		detailPanel.add(proposedSubscriptionSentDetail);
	}

	/**
	 * set
	 *
	 * @param message sent
	 */
	public void set(GWTTextMessageSent messageSent) {
		reset();
		messageSentDetail.set(messageSent);
		detailPanel.add(messageSentDetail);
	}

	/**
	 * set
	 *
	 * @param message received
	 */
	public void set(GWTMessageReceived messageReceived) {
		reset();
		messageReceivedDetail.set(messageReceived);
		detailPanel.add(messageReceivedDetail);
	}

	/**
	 * set
	 *
	 * @param propose
	 */
	public void set(GWTProposedQueryReceived propose) {
		reset();
		proposedQueryReceivedDetail.set(propose);
		detailPanel.add(proposedQueryReceivedDetail);
	}

	/**
	 * set
	 *
	 * @param propose
	 */
	public void set(GWTProposedQuerySent propose) {
		reset();
		proposedQuerySentDetail.set(propose);
		detailPanel.add(proposedQuerySentDetail);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		proposedSubscriptionReceivedDetail.langRefresh();
		proposedSubscriptionSentDetail.langRefresh();
		proposedQueryReceivedDetail.langRefresh();
		proposedQuerySentDetail.langRefresh();
		messageReceivedDetail.langRefresh();
		messageSentDetail.langRefresh();
	}
}