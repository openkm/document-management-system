/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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

package com.openkm.frontend.client.widget.sendmail;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.UtilComunicator;

/**
 * Warning Mail Panel
 *
 * @author sochoa
 */
public class WarningMailPopup extends DialogBox {

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private HTML textWarning;
	private Button acceptButton;

	/**
	 * Warning Mail popup
	 */
	public WarningMailPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		setText(GeneralComunicator.i18n("maileditor.warning.label"));

		vPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();
		textWarning = new HTML();
		textWarning.setStyleName("okm-NoWrap");

		acceptButton = new Button(GeneralComunicator.i18n("button.accept"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		vPanel.setWidth("300px");
		vPanel.setHeight("50px");
		acceptButton.setStyleName("okm-YesButton");
		textWarning.setHTML("");
		hPanel.add(acceptButton);

		vPanel.add(UtilComunicator.vSpace("5px"));
		vPanel.add(textWarning);
		vPanel.add(UtilComunicator.vSpace("10px"));
		vPanel.add(hPanel);
		vPanel.add(UtilComunicator.vSpace("10px"));

		vPanel.setCellHorizontalAlignment(textWarning, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, VerticalPanel.ALIGN_CENTER);

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(GeneralComunicator.i18n("maileditor.warning.label"));
		acceptButton.setText(GeneralComunicator.i18n("button.accept"));
	}

	/**
	 * getTextWarning
	 */
	public HTML getTextWarning() {
		return textWarning;
	}
}
