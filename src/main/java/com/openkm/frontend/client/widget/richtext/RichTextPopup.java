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

package com.openkm.frontend.client.widget.richtext;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;

/**
 * RichtTextPopup
 *
 * @author jllort
 *
 */
public class RichTextPopup extends DialogBox {

	public static final int NO_ACTION = 0;
	public static final int ACTION_ENTER_URL = 1;
	public static final int ACTION_ENTER_IMAGE_URL = 2;

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private TextBox url;
	private Button cancelButton;
	private Button addButton;
	private int action = NO_ACTION;
	RichTextAction richTextAction;

	/**
	 * RichTextPopup
	 */
	public RichTextPopup(RichTextAction richTextAction) {
		// Establishes auto-close when click outside
		super(false, true);
		this.richTextAction = richTextAction;
		setText("");

		vPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();
		HTML space3 = new HTML("");
		hPanel.add(space3);
		hPanel.add(vPanel);
		HTML space4 = new HTML("");
		hPanel.add(space4);

		HTML space = new HTML("");
		vPanel.add(space);

		url = new TextBox();
		url.setWidth("290px");
		vPanel.add(url);
		HTML space2 = new HTML("");
		vPanel.add(space2);

		cancelButton = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		addButton = new Button(Main.i18n("button.add"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				execute();
				hide();
			}
		});
		HorizontalPanel hPanel2 = new HorizontalPanel();
		hPanel2.add(cancelButton);
		hPanel2.add(new HTML("&nbsp;"));
		hPanel2.add(addButton);
		vPanel.add(hPanel2);
		HTML space5 = new HTML("");
		vPanel.add(space5);

		vPanel.setCellHorizontalAlignment(hPanel2, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHeight(space, "5px");
		vPanel.setCellHeight(space2, "5px");
		vPanel.setCellHeight(space5, "5px");

		hPanel.setCellWidth(space3, "5px");
		hPanel.setCellWidth(space4, "5px");

		url.setStyleName("okm-Input");
		cancelButton.setStyleName("okm-NoButton");
		addButton.setStyleName("okm-AddButton");

		super.hide();
		setWidget(hPanel);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		cancelButton.setText(Main.i18n("button.cancel"));
		addButton.setText(Main.i18n("button.add"));
	}

	/**
	 * setAction
	 *
	 * @param action
	 */
	public void setAction(int action) {
		this.action = action;
		switch (action) {
			case ACTION_ENTER_URL:
				setText(Main.i18n("richtext.add.url"));
				break;

			case ACTION_ENTER_IMAGE_URL:
				setText(Main.i18n("richtext.add.image.url"));
				break;
		}
	}

	/**
	 * execute
	 */
	public void execute() {
		if (url.getText().startsWith("http://")) {
			switch (action) {
				case ACTION_ENTER_URL:
					richTextAction.insertURL(url.getText());
					break;

				case ACTION_ENTER_IMAGE_URL:
					richTextAction.insertImageURL(url.getText());
					break;
			}
		}
	}

	/**
	 * Shows de popup
	 */
	public void show() {
		url.setText("http://");
		int left = (Window.getClientWidth() - 300) / 2;
		int top = (Window.getClientHeight() - 125) / 2;
		setPopupPosition(left, top);
		super.show();
	}
}