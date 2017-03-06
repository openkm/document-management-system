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

package com.openkm.frontend.client.widget.massive;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.service.OKMMassiveService;
import com.openkm.frontend.client.service.OKMMassiveServiceAsync;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.richtext.RichTextToolbar;

/**
 * Notes popup
 *
 * @author jllort
 */
public class NotesPopup extends DialogBox {
	private final OKMMassiveServiceAsync massiveService = (OKMMassiveServiceAsync) GWT.create(OKMMassiveService.class);

	private VerticalPanel newNotePanel;
	private Button cancelButton;
	private Button addButton;
	public RichTextArea richTextArea;
	private RichTextToolbar richTextToolbar;
	private Grid gridRichText;
	private TextArea textArea;
	private Status status;
	private boolean isChrome = false;

	/**
	 * NotesPopup
	 */
	public NotesPopup() {
		// Establishes auto-close when click outside
		super(false, true);
		isChrome = (Util.getUserAgent().startsWith("safari") || Util.getUserAgent().startsWith("chrome"));
		setText(Main.i18n("general.menu.edit.add.note"));

		// Status
		status = new Status(this);
		status.setStyleName("okm-StatusPopup");

		newNotePanel = new VerticalPanel();

		richTextArea = new RichTextArea();
		richTextArea.setSize("100%", "14em");
		richTextToolbar = new RichTextToolbar(richTextArea);
		richTextArea.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				evaluateButtons();
			}
		});

		gridRichText = new Grid(2, 1);
		gridRichText.setStyleName("RichTextToolbar");
		gridRichText.addStyleName("okm-Input");
		gridRichText.setWidget(0, 0, richTextToolbar);
		gridRichText.setWidget(1, 0, richTextArea);

		textArea = new TextArea();
		textArea.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				evaluateButtons();
			}
		});
		textArea.setPixelSize(550, 160);
		textArea.setStyleName("okm-Input");

		if (isChrome) {
			newNotePanel.add(textArea);
		} else {
			newNotePanel.add(gridRichText);
		}

		addButton = new Button(Main.i18n("button.add"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addNote();
			}
		});
		addButton.setStyleName("okm-AddButton");

		cancelButton = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		cancelButton.setStyleName("okm-NoButton");

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(addButton);
		hPanel.add(new HTML("&nbsp;"));
		hPanel.add(cancelButton);
		newNotePanel.add(hPanel);

		newNotePanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		newNotePanel.setCellHeight(hPanel, "25px");
		newNotePanel.setCellVerticalAlignment(hPanel, HasAlignment.ALIGN_MIDDLE);

		hide();
		setWidget(newNotePanel);
	}

	/**
	 * addNote
	 */
	private void addNote() {
		String content = "";
		if (isChrome) {
			content = textArea.getText();
		} else {
			content = richTextArea.getHTML();
		}
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			status.setFlagAddNotes();
			massiveService.addNote(Main.get().mainPanel.desktop.browser.fileBrowser.getAllSelectedPaths(), content,
					new AsyncCallback<Object>() {
						@Override
						public void onSuccess(Object result) {
							Main.get().mainPanel.topPanel.toolBar.executeRefresh();
							status.unsetFlagAddNotes();
							hide();
						}

						@Override
						public void onFailure(Throwable caught) {
							status.unsetFlagAddNotes();
							GeneralComunicator.showError("addNote", caught);
						}
					});
		} else {
			if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.notes.addNote(content);
			} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isFolderSelected()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.notes.addNote(content);
			} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isMailSelected()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.notes.addNote(content);
			}
			hide();
		}
	}

	@Override
	public void center() {
		reset();
		super.center();
	}

	/**
	 * reset
	 */
	private void reset() {
		addButton.setEnabled(false);
		richTextArea.setText("");
		textArea.setText("");
		if (isChrome) {
			textArea.setFocus(true);
		} else {
			richTextArea.setFocus(true);
		}
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		setText(Main.i18n("general.menu.edit.add.note"));
		addButton.setHTML(Main.i18n("button.add"));
		cancelButton.setHTML(Main.i18n("button.cancel"));
		richTextToolbar.langRefresh();
	}

	/**
	 * evaluateButton
	 */
	private void evaluateButtons() {
		boolean buttonsEnabled = (isChrome) ? textArea.getText().trim().length() > 0 : richTextArea.getText().trim().length() > 0;
		if (addButton != null) { // loading case
			addButton.setEnabled(buttonsEnabled);
		}
	}
}