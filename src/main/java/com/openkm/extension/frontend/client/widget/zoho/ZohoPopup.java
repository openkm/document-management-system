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

package com.openkm.extension.frontend.client.widget.zoho;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.service.OKMZohoService;
import com.openkm.extension.frontend.client.service.OKMZohoServiceAsync;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.constants.ui.UIDialogConstants;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * ZohoWriter
 *
 * @author jllort
 */
public class ZohoPopup extends DialogBox {
	private final OKMZohoServiceAsync zohoService = (OKMZohoServiceAsync) GWT.create(OKMZohoService.class);

	public static final int DEFAULT_WIDTH = 1020;
	public static final int DEFAULT_HEIGHT = 720;

	private VerticalPanel vPanel;
	private Frame frame;
	private Button close;

	/**
	 * ZohoWriterPopup
	 */
	public ZohoPopup(String title, String url, final String id) {
		super(false, true);

		int width = DEFAULT_WIDTH;
		int height = DEFAULT_HEIGHT;

		// Calculate size
		if (Main.get().mainPanel.getOffsetHeight() < height + UIDialogConstants.MARGIN) {
			height = Main.get().mainPanel.getOffsetHeight() - UIDialogConstants.MARGIN;
		}

		if (Main.get().mainPanel.getOffsetWidth() < width + UIDialogConstants.MARGIN) {
			width = Main.get().mainPanel.getOffsetWidth() - UIDialogConstants.MARGIN;
		}

		setText(title);
		vPanel = new VerticalPanel();

		frame = new Frame(url);
		DOM.setElementProperty(frame.getElement(), "frameborder", "0");
		DOM.setElementProperty(frame.getElement(), "marginwidth", "0");
		DOM.setElementProperty(frame.getElement(), "marginheight", "0");
		frame.setWidth(String.valueOf(width) + "px");
		frame.setHeight(String.valueOf(height - UIDialogConstants.FRAME_OFFSET - UIDialogConstants.DIALOG_TOP) + "px");
		frame.setStyleName("okm-Popup-text");

		close = new Button(GeneralComunicator.i18n("button.close"));
		close.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				close.setEnabled(false);
				zohoService.closeZohoWriter(id, new AsyncCallback<Object>() {
					@Override
					public void onSuccess(Object result) {
						hide();
					}

					@Override
					public void onFailure(Throwable caught) {
						GeneralComunicator.showError("closeZohoWriter", caught);
					}
				});
			}
		});

		close.addStyleName("okm-Input");

		vPanel.setWidth(String.valueOf(width) + "px");
		vPanel.setHeight(String.valueOf(height - UIDialogConstants.DIALOG_TOP) + "px");

		vPanel.add(frame);
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(close);
		vPanel.add(hPanel);

		vPanel.setCellHeight(frame, String.valueOf(height - UIDialogConstants.FRAME_OFFSET - UIDialogConstants.DIALOG_TOP) + "px");
		vPanel.setCellHeight(hPanel, String.valueOf(UIDialogConstants.BUTTONS_HEIGHT) + "px");
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(hPanel, HasAlignment.ALIGN_MIDDLE);

		setWidget(vPanel);
	}
}