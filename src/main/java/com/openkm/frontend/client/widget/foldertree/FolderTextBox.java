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

package com.openkm.frontend.client.widget.foldertree;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.util.Util;

/**
 * File textBox
 *
 * @author jllort
 *
 */
public class FolderTextBox extends Composite {
	private TextBox textBox;
	private boolean keyShortcutsEnabled = false;

	/**
	 * File textBox
	 */
	public FolderTextBox() {
		Main.get().mainPanel.disableKeyShorcuts(); // Disables shortcuts
		HorizontalPanel panel = new HorizontalPanel();
		//panel.setSize("100%", "100%");
		textBox = new TextBox();
		textBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				int action = Main.get().activeFolderTree.getFolderAction();

				switch (event.getNativeKeyCode()) {
					case (char) KeyCodes.KEY_ENTER:
						switch (action) {
							case FolderTree.ACTION_CREATE:
								Main.get().activeFolderTree.rootItem.addStyleName("okm-DisableSelect"); // Disables drag and drop browser text selection
								if (textBox.getText().length() > 0) {
									Main.get().activeFolderTree.create(textBox.getText());
								} else {
									Main.get().activeFolderTree.removeTmpFolderCreate();
								}
								break;

							case FolderTree.ACTION_RENAME:
								Main.get().activeFolderTree.rootItem.addStyleName("okm-DisableSelect"); // Disables drag and drop browser text selection
								if (textBox.getText().length() > 0) {
									Main.get().activeFolderTree.rename(textBox.getText());
								} else {
									Main.get().activeFolderTree.cancelRename();
								}
								break;
						}
						if (!keyShortcutsEnabled) {
							Main.get().mainPanel.enableKeyShorcuts(); // Enables general keys applications
						}
						break;

					case (char) KeyCodes.KEY_ESCAPE:
						switch (action) {
							case FolderTree.ACTION_CREATE:
								Main.get().activeFolderTree.rootItem.addStyleName("okm-DisableSelect"); // Disables drag and drop browser text selection
								Main.get().activeFolderTree.removeTmpFolderCreate();
								break;

							case FolderTree.ACTION_RENAME:
								Main.get().activeFolderTree.rootItem.addStyleName("okm-DisableSelect"); // Disables drag and drop browser text selection
								Main.get().activeFolderTree.cancelRename();
								break;
						}
						if (!keyShortcutsEnabled) {
							Main.get().mainPanel.enableKeyShorcuts(); // Enables general keys applications
						}
						break;
				}
			}
		});
		textBox.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				if (!keyShortcutsEnabled) {
					Main.get().mainPanel.enableKeyShorcuts(); // Enables general keys applications
					keyShortcutsEnabled = true;
				}
			}
		});
		textBox.setVisibleLength(20);
		textBox.setStyleName("okm-FileBrowser-TextBox");
		panel.add(new HTML(Util.imageItemHTML("img/menuitem_empty.gif", "", "top")));
		panel.add(textBox);
		initWidget(panel);
	}

	/**
	 * Resets text Box values
	 */
	public void reset() {
		textBox.setText("");
	}

	/**
	 * Gets the text value
	 *
	 * @return The text
	 */
	public String getText() {
		return textBox.getText();
	}

	/**
	 * Sets text input box
	 *
	 * @param text The text
	 */
	public void setText(String text) {
		textBox.setVisibleLength(text.length() + 5);
		textBox.setText(text);
	}

	/**
	 * Sets windows focus to input
	 */
	public void setFocus() {
		textBox.selectAll();
		textBox.setFocus(true);
	}
}