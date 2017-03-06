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

package com.openkm.frontend.client.widget.mainmenu;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;

/**
 * BookmarkPopup
 *
 * @author jllort
 *
 */
public class BookmarkPopup extends DialogBox {
	private VerticalPanel vPanel;
	private Label name;
	private HorizontalPanel hPanel;
	private VerticalPanel valuesPanel;
	private TextBox textBox;
	private Button acceptButton;
	private Button cancelButton;
	private String nodePath = "";
	private ScrollPanel scrollPanel;

	/**
	 * BookmarkPopup
	 */
	public BookmarkPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		vPanel = new VerticalPanel();
		valuesPanel = new VerticalPanel();
		name = new Label(Main.i18n("bookmark.name"));

		hPanel = new HorizontalPanel();

		textBox = new TextBox();
		textBox.setStyleName("okm-Input");
		textBox.setMaxLength(90);
		textBox.setWidth("300px");

		textBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if ((char) KeyCodes.KEY_ENTER == event.getCharCode()) {
					if (!nodePath.equals("") && !textBox.getText().equals("")) {
						Main.get().mainPanel.topPanel.mainMenu.bookmark.add(nodePath, textBox.getText());
					}
					reset();
					hide();
				}
			}
		});

		cancelButton = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
				hide();
			}
		}
		);
		cancelButton.setStyleName("okm-NoButton");

		acceptButton = new Button(Main.i18n("button.accept"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!nodePath.equals("") && !textBox.getText().equals("")) {
					Main.get().mainPanel.topPanel.mainMenu.bookmark.add(nodePath, textBox.getText());
				}
				reset();
				hide();
			}
		}
		);
		acceptButton.setStyleName("okm-AddButton ");

		hPanel.add(cancelButton);
		hPanel.add(new HTML("&nbsp;"));
		hPanel.add(acceptButton);

		// TODO This is a workaround for a Firefox 2 bug
		// http://code.google.com/p/google-web-toolkit/issues/detail?id=891
		// Table for solve some visualization problems
		scrollPanel = new ScrollPanel(textBox);
		scrollPanel.setAlwaysShowScrollBars(false);
		scrollPanel.setSize("100%", "100%");

		valuesPanel.add(name);
		valuesPanel.add(scrollPanel);
		valuesPanel.setCellHorizontalAlignment(name, HorizontalPanel.ALIGN_LEFT);
		valuesPanel.setCellHorizontalAlignment(scrollPanel, HorizontalPanel.ALIGN_LEFT);
		valuesPanel.setWidth("300px");

		vPanel.setWidth("310px");
		vPanel.setHeight("100px");

		vPanel.add(new HTML("<br>"));
		vPanel.add(valuesPanel);
		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(name, VerticalPanel.ALIGN_LEFT);
		vPanel.setCellHorizontalAlignment(valuesPanel, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, VerticalPanel.ALIGN_CENTER);
		vPanel.removeStyleDependentName("okm-DisableSelect");

		center();
		hide();
		setWidget(vPanel);
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("bookmark.label"));
		name.setText(Main.i18n("bookmark.name"));
		cancelButton.setText(Main.i18n("button.cancel"));
		acceptButton.setText(Main.i18n("button.accept"));
	}

	/**
	 * Show the popup bookmark
	 *
	 */
	public void show(String nodePath, String name) {
		setText(Main.i18n("bookmark.label"));
		this.nodePath = nodePath;
		textBox.setText(name);
		center();
		textBox.setFocus(true);
	}

	/**
	 * Reset values
	 */
	private void reset() {
		nodePath = "";
		textBox.setText("");
	}
}