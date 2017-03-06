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

package com.openkm.extension.frontend.client.widget.wiki;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * NewWikiPagePopup
 *
 * @author jllort
 *
 */
public class NewWikiPagePopup extends DialogBox {
	private VerticalPanel vPanel;
	private HTML titleText;
	private TextBox title;
	private Button cancelButton;
	private Button addButton;

	/**
	 * NewWikiPagePopup
	 */
	public NewWikiPagePopup() {
		// Establishes auto-close when click outside
		super(false, true);

		setText(GeneralComunicator.i18nExtension("wiki.page.add"));

		vPanel = new VerticalPanel();
		vPanel.setWidth("200px");
		vPanel.setHeight("50px");

		HorizontalPanel hPanel = new HorizontalPanel();
		titleText = new HTML(GeneralComunicator.i18nExtension("wiki.page.title"));
		title = new TextBox();
		title.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				addButton.setEnabled((title.getText().length() > 0));
			}
		});
		title.setWidth("250px");
		title.setStyleName("okm-Input");
		hPanel.add(new HTML("&nbsp;"));
		hPanel.add(titleText);
		hPanel.add(new HTML("&nbsp;&nbsp;"));
		hPanel.add(title);
		hPanel.add(new HTML("&nbsp;"));

		hPanel.setCellVerticalAlignment(titleText, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellVerticalAlignment(title, HasAlignment.ALIGN_MIDDLE);

		cancelButton = new Button(GeneralComunicator.i18n("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		cancelButton.setStyleName("okm-NoButton");

		addButton = new Button(GeneralComunicator.i18n("button.add"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!title.getText().equals("")) {
					Wiki.get().addWigiTag(title.getText());
				}
				hide();
			}
		});
		addButton.setStyleName("okm-AddButton");

		HorizontalPanel buttonsPanel = new HorizontalPanel();
		buttonsPanel.add(cancelButton);
		buttonsPanel.add(new HTML("&nbsp;"));
		buttonsPanel.add(addButton);

		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));
		vPanel.add(buttonsPanel);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(buttonsPanel, HasAlignment.ALIGN_CENTER);

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * reset
	 */
	public void reset() {
		title.setText("");
		addButton.setEnabled(false);
	}

	/**
	 * setFocus
	 */
	public void setFocus() {
		title.setFocus(true);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		setText(GeneralComunicator.i18nExtension("wiki.page.add"));
		titleText.setHTML(GeneralComunicator.i18nExtension("wiki.page.title"));
	}
}