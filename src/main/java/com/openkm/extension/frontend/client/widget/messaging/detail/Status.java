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

import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.extension.frontend.client.widget.messaging.MessagingToolBarBox;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;


/**
 * Status
 *
 * @author jllort
 *
 */
public class Status extends PopupPanel {

	private HorizontalPanel hPanel;
	private HTML msg;
	private HTML space;
	private Image image;

	private boolean flag_addSubscription = false;
	private boolean flag_markProposeAsAccepted = false;
	private boolean flag_addQuery = false;

	/**
	 * Status
	 */
	public Status() {
		super(false, true);
		hPanel = new HorizontalPanel();
		image = new Image(OKMBundleResources.INSTANCE.indicator());
		msg = new HTML("");
		space = new HTML("");

		hPanel.add(image);
		hPanel.add(msg);
		hPanel.add(space);

		hPanel.setCellVerticalAlignment(image, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellVerticalAlignment(msg, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellHorizontalAlignment(image, HasAlignment.ALIGN_CENTER);
		hPanel.setCellWidth(image, "30px");
		hPanel.setCellWidth(space, "7px");

		hPanel.setHeight("25px");

		msg.setStyleName("okm-NoWrap");

		super.hide();
		setWidget(hPanel);
	}

	/**
	 * Refresh
	 */
	public void refresh() {
		if (flag_addSubscription || flag_markProposeAsAccepted || flag_addQuery) {
			int left = ((MessagingToolBarBox.get().messageDashboard.messageBrowser.messageDetail.getOffsetWidth() - 200) / 2) +
					MessagingToolBarBox.get().messageDashboard.messageBrowser.messageDetail.getAbsoluteLeft();
			int top = ((MessagingToolBarBox.get().messageDashboard.messageBrowser.messageDetail.getOffsetHeight() - 40) / 2) +
					MessagingToolBarBox.get().messageDashboard.messageBrowser.messageDetail.getAbsoluteTop();
			setPopupPosition(left, top);
			MessagingToolBarBox.get().messageDashboard.messageBrowser.messageDetail.addStyleName("okm-PanelRefreshing");
			show();
		} else {
			hide();
			MessagingToolBarBox.get().messageDashboard.messageBrowser.messageDetail.removeStyleName("okm-PanelRefreshing");
		}
	}

	/**
	 * Sets add subscription flag
	 */
	public void setFlag_addSubscription() {
		msg.setHTML(GeneralComunicator.i18nExtension("dasboard.messaging.add.subscription"));
		flag_addSubscription = true;
		refresh();
	}

	/**
	 * Unset add subscription  flag
	 */
	public void unsetFlag_addSubscription() {
		flag_addSubscription = false;
		refresh();
	}

	/**
	 * Sets mark propose as accepted flag
	 */
	public void setFlag_markProposeAsAccepted() {
		msg.setHTML(GeneralComunicator.i18nExtension("dasboard.messaging.mark.propose.accepted"));
		flag_markProposeAsAccepted = true;
		refresh();
	}

	/**
	 * Unset mark propose as accepted  flag
	 */
	public void unsetFlag_markProposalAsAccepted() {
		flag_markProposeAsAccepted = false;
		refresh();
	}

	/**
	 * Sets add query flag
	 */
	public void setFlag_addQuery() {
		msg.setHTML(GeneralComunicator.i18nExtension("dasboard.messaging.add.query"));
		flag_addQuery = true;
		refresh();
	}

	/**
	 * Unset add query  flag
	 */
	public void unsetFlag_addQuery() {
		flag_addQuery = false;
		refresh();
	}
}