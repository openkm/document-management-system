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

package com.openkm.frontend.client.widget.properties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.GWTNote;
import com.openkm.frontend.client.extension.event.HasDocumentEvent;
import com.openkm.frontend.client.service.OKMNoteService;
import com.openkm.frontend.client.service.OKMNoteServiceAsync;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.ConfirmPopup;
import com.openkm.frontend.client.widget.richtext.RichTextToolbar;

import java.util.Collection;
import java.util.Iterator;

/**
 * Notes
 *
 * @author jllort
 *
 */
public class Notes extends Composite {
	private final OKMNoteServiceAsync noteService = (OKMNoteServiceAsync) GWT.create(OKMNoteService.class);
	public static final int DOCUMENT_NOTE = 1;
	public static final int FOLDER_NOTE = 2;
	public static final int MAIL_NOTE = 3;

	private FlexTable tableNotes;
	private Object object;
	private Button addButton;
	private Button updateButton;
	private Button cancelButton;
	private ScrollPanel scrollPanel;
	public RichTextArea richTextArea;
	private RichTextToolbar richTextToolbar;
	private VerticalPanel newNotePanel;
	private TextArea textArea;
	private HTML addNote;
	private Grid gridRichText;
	boolean visibleButtons = true;
	boolean addNoteOption = false;
	boolean isEditingNote = false;
	private String editedNotePath = "";
	private int editedNoteRow = 0;
	private boolean removeNoteEnabled = false;
	private boolean isChrome = false;
	int type = 0;

	/**
	 * Notes
	 */
	public Notes(int type) {
		this.type = type;
		isChrome = (Util.getUserAgent().startsWith("safari") || Util.getUserAgent().startsWith("chrome"));
		tableNotes = new FlexTable();
		scrollPanel = new ScrollPanel(tableNotes);
		newNotePanel = new VerticalPanel();
		addNote = new HTML("<b>" + Main.i18n("general.menu.edit.add.note") + "</b>");
		richTextArea = new RichTextArea();
		richTextArea.setSize("100%", "14em");
		richTextToolbar = new RichTextToolbar(richTextArea);
		richTextArea.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				evaluateButtons();
			}
		});
		// richTextToolbar.setWidth("100%");

		gridRichText = new Grid(2, 1);
		gridRichText.setStyleName("RichTextToolbar");
		gridRichText.addStyleName("okm-Input");
		gridRichText.setWidget(0, 0, richTextToolbar);
		gridRichText.setWidget(1, 0, richTextArea);

		textArea = new TextArea();
		textArea.setPixelSize(550, 160);
		textArea.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				evaluateButtons();
			}
		});
		textArea.setStyleName("okm-Input");

		addButton = new Button(Main.i18n("button.add"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addNote();
			}
		});
		addButton.setEnabled(false);

		updateButton = new Button(Main.i18n("button.update"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setNote(editedNotePath, getTextNote(), editedNoteRow);
			}
		});
		updateButton.setEnabled(false);

		cancelButton = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
			}
		});
		updateButton.setVisible(false);
		cancelButton.setVisible(false);

		newNotePanel.add(Util.vSpace("40px"));
		newNotePanel.add(addNote);
		if (isChrome) {
			newNotePanel.add(textArea);
		} else {
			newNotePanel.add(gridRichText);
		}
		newNotePanel.add(Util.vSpace("10px"));
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(cancelButton);
		hPanel.add(new HTML("&nbsp;"));
		hPanel.add(addButton);
		hPanel.add(new HTML("&nbsp;"));
		hPanel.add(updateButton);
		newNotePanel.add(hPanel);

		newNotePanel.setCellHorizontalAlignment(addNote, HasAlignment.ALIGN_CENTER);
		if (isChrome) {
			newNotePanel.setCellHorizontalAlignment(textArea, HasAlignment.ALIGN_CENTER);
		} else {
			newNotePanel.setCellHorizontalAlignment(gridRichText, HasAlignment.ALIGN_CENTER);
		}
		newNotePanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);

		addButton.setStyleName("okm-AddButton");
		updateButton.setStyleName("okm-YesButton");
		cancelButton.setStyleName("okm-NoButton");

		tableNotes.setWidth("100%");

		newNotePanel.setVisible(false);

		initWidget(scrollPanel);
	}

	/**
	 * Sets the document values
	 *
	 * @param doc The document object
	 */
	public void set(GWTDocument doc) {
		reset();
		object = doc;
		setRichTextAreaText("");

		while (tableNotes.getRowCount() > 0) {
			tableNotes.removeRow(0);
		}

		for (Iterator<GWTNote> it = doc.getNotes().iterator(); it.hasNext(); ) {
			writeNote(it.next());
		}

		writeAddNote();
	}

	/**
	 * Sets the folder values
	 *
	 * @param doc The folder object
	 */
	public void set(GWTFolder folder) {
		reset();
		object = folder;
		setRichTextAreaText("");

		while (tableNotes.getRowCount() > 0) {
			tableNotes.removeRow(0);
		}

		for (Iterator<GWTNote> it = folder.getNotes().iterator(); it.hasNext(); ) {
			writeNote(it.next());
		}

		writeAddNote();
	}

	/**
	 * Sets the folder values
	 *
	 * @param doc The folder object
	 */
	public void set(GWTMail mail) {
		reset();
		object = mail;
		setRichTextAreaText("");

		while (tableNotes.getRowCount() > 0) {
			tableNotes.removeRow(0);
		}

		for (Iterator<GWTNote> it = mail.getNotes().iterator(); it.hasNext(); ) {
			writeNote(it.next());
		}

		writeAddNote();
	}

	/**
	 * writeAddNote
	 */
	private void writeAddNote() {
		int row = tableNotes.getRowCount();
		tableNotes.setWidget(row, 0, newNotePanel);
		tableNotes.getFlexCellFormatter().setColSpan(row, 0, 2);
		tableNotes.getCellFormatter().setHorizontalAlignment(row, 0, HasAlignment.ALIGN_CENTER);
	}

	/**
	 * Writes the note 
	 *
	 * @param note
	 */
	private void writeNote(final GWTNote note) {
		int row = tableNotes.getRowCount();
		tableNotes.setHTML(row, 0, "<b>" + note.getUser().getUsername() + "</b>");
		Image editNote = new Image(OKMBundleResources.INSTANCE.noteEdit());
		Image deleteNote = new Image(OKMBundleResources.INSTANCE.noteDelete());
		editNote.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
				isEditingNote = true;
				addButton.setVisible(false);
				updateButton.setVisible(true);
				cancelButton.setVisible(true);
				editedNoteRow = tableNotes.getCellForEvent(event).getRowIndex() + 2; // The text row is + 2
				editedNotePath = note.getPath();
				setTextNoteToEditor(tableNotes.getHTML(editedNoteRow, 0));
			}
		});

		deleteNote.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				switch (type) {
					case DOCUMENT_NOTE:
						Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_NOTE_DOCUMENT);
						break;
					case FOLDER_NOTE:
						Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_NOTE_FOLDER);
						break;
					case MAIL_NOTE:
						Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_NOTE_MAIL);
						break;
				}

				NoteToDelete noteToDelete = new NoteToDelete(note.getPath(), tableNotes.getCellForEvent(event).getRowIndex());
				Main.get().confirmPopup.setValue(noteToDelete);
				Main.get().confirmPopup.show();
			}
		});

		editNote.setStyleName("okm-Hyperlink");
		deleteNote.setStyleName("okm-Hyperlink");

		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		HTML space = new HTML("");
		HTML space2 = new HTML("");
		HTML date = new HTML(dtf.format(note.getDate()));
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(editNote);
		hPanel.add(space);

		if (removeNoteEnabled) {
			hPanel.add(deleteNote);
		}

		hPanel.add(space2);
		hPanel.add(date);
		hPanel.setCellWidth(space, "5px");
		hPanel.setCellWidth(space2, "5px");

		if (note.getAuthor().equals(Main.get().workspaceUserProperties.getUser().getId())) {
			if (visibleButtons || addNoteOption) {
				editNote.setVisible(true);
			} else {
				editNote.setVisible(false);
			}
		} else {
			editNote.setVisible(false);
		}

		if (note.getAuthor().equals(Main.get().workspaceUserProperties.getUser().getId()) ||
				(Main.get().workspaceUserProperties.getWorkspace() != null &&
						Main.get().workspaceUserProperties.getWorkspace().isAdminRole())) {
			if (visibleButtons || addNoteOption) {
				deleteNote.setVisible(true);
			} else {
				deleteNote.setVisible(false);
			}
		} else {
			deleteNote.setVisible(false);
		}

		tableNotes.setWidget(row, 1, hPanel);
		tableNotes.getCellFormatter().setHorizontalAlignment(row, 1, HasAlignment.ALIGN_RIGHT);
		tableNotes.getRowFormatter().setStyleName(row, "okm-Notes-Title");
		tableNotes.getCellFormatter().setHeight(row, 1, "30px");
		tableNotes.getCellFormatter().setVerticalAlignment(row, 0, HasAlignment.ALIGN_BOTTOM);
		tableNotes.getCellFormatter().setVerticalAlignment(row, 1, HasAlignment.ALIGN_BOTTOM);
		row++;
		tableNotes.setHTML(row, 0, "");
		tableNotes.getCellFormatter().setHeight(row, 0, "6px");
		tableNotes.getRowFormatter().setStyleName(row, "okm-Notes-Line");
		tableNotes.getFlexCellFormatter().setColSpan(row, 0, 2);
		row++;
		tableNotes.setHTML(row, 0, note.getText());
		tableNotes.getFlexCellFormatter().setColSpan(row, 0, 2);
	}

	/**
	 * Lang refresh
	 */
	public void langRefresh() {
		addButton.setHTML(Main.i18n("button.add"));
		updateButton.setHTML(Main.i18n("button.update"));
		cancelButton.setHTML(Main.i18n("button.cancel"));
		addNote.setHTML("<b>" + Main.i18n("general.menu.edit.add.note") + "</b>");
		richTextToolbar.langRefresh();
	}

	/**
	 * Sets visibility to buttons ( true / false )
	 *
	 * @param visible The visible value
	 */
	public void setVisibleButtons(boolean visible) {
		visibleButtons = visible;
		addButton.setVisible(visible);
		addNote.setVisible(visible);
		gridRichText.setVisible(visible);
		textArea.setVisible(visible);
	}

	/**
	 * Sets the visible add note or not
	 *
	 * @param visible The visible value
	 */
	public void setVisibleAddNote(boolean visible) {
		addNoteOption = visible && visibleButtons;
		addButton.setVisible(addNoteOption);
		addNote.setVisible(addNoteOption);
		gridRichText.setVisible(addNoteOption);
		textArea.setVisible(addNoteOption);
	}

	/**
	 * Callback addNote 
	 */
	final AsyncCallback<GWTNote> callbackAddNote = new AsyncCallback<GWTNote>() {
		public void onSuccess(GWTNote result) {
			tableNotes.removeRow(tableNotes.getRowCount() - 1); // Deletes last row = addComment
			writeNote(result);
			writeAddNote();
			reset();

			if (object instanceof GWTDocument) {
				GWTDocument document = (GWTDocument) object;
				document.getNotes().add(result);

				if (!document.isHasNotes()) {
					Main.get().mainPanel.desktop.browser.fileBrowser.addNoteIconToSelectedRow();
					document.setHasNotes(true);
				}
			} else if (object instanceof GWTFolder) {
				GWTFolder folder = (GWTFolder) object;
				folder.getNotes().add(result);

				// If is added first note must adding some icon on filebrowser
				if (!folder.isHasNotes() && !Main.get().activeFolderTree.isPanelSelected()) {
					Main.get().mainPanel.desktop.browser.fileBrowser.addNoteIconToSelectedRow();
					folder.setHasNotes(true);
				}
			} else if (object instanceof GWTMail) {
				GWTMail mail = (GWTMail) object;
				mail.getNotes().add(result);

				if (!mail.isHasNotes()) {
					Main.get().mainPanel.desktop.browser.fileBrowser.addNoteIconToSelectedRow();
					mail.setHasNotes(true);
				}
			}

			Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.fireEvent(HasDocumentEvent.NOTE_ADDED);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("addNote", caught);
		}
	};

	/**
	 * addNote
	 */
	private void addNote() {
		boolean hasText = (isChrome) ? textArea.getText().trim().length() > 0 : richTextArea.getText().trim().length() > 0;
		if (hasText) {
			noteService.add(getPath(), getTextNote(), callbackAddNote);
		}
	}

	/**
	 * addNote
	 */
	public void addNote(String text) {
		if (text.length() > 0) {
			noteService.add(getPath(), text, callbackAddNote);
		}
	}

	/**
	 * getPath
	 */
	private String getPath() {
		String path = "";

		if (object instanceof GWTDocument) {
			path = ((GWTDocument) object).getPath();
		} else if (object instanceof GWTFolder) {
			path = ((GWTFolder) object).getPath();
		} else if (object instanceof GWTMail) {
			path = ((GWTMail) object).getPath();
		}

		return path;
	}

	/**
	 * getTextNote
	 */
	private String getTextNote() {
		if (isChrome) {
			return textArea.getText();
		} else {
			return richTextArea.getHTML();
		}
	}

	/**
	 * setTextNoteToEditor
	 */
	private void setTextNoteToEditor(String text) {
		if (isChrome) {
			textArea.setText(text);
		} else {
			richTextArea.setHTML(text);
		}
		evaluateButtons();
	}

	/**
	 * reset
	 */
	private void reset() {
		isEditingNote = false;
		editedNotePath = "";
		editedNoteRow = 0;
		setRichTextAreaText("");
		addButton.setHTML(Main.i18n("button.add"));

		if (visibleButtons) {
			addButton.setVisible(true);
		}

		updateButton.setVisible(false);
		cancelButton.setVisible(false);
	}

	/**
	 * deleteNote
	 */
	public void deleteNote(final String notePath, final int row) {
		noteService.delete(notePath, new AsyncCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				tableNotes.removeRow(row); // row + 0
				tableNotes.removeRow(row); // row + 1;
				tableNotes.removeRow(row); // row + 2

				for (GWTNote note : getNotes()) {
					if (note.getPath().equals(notePath)) {
						getNotes().remove(note);
						break;
					}
				}

				if (getNotes().isEmpty()) {
					if (object instanceof GWTDocument) {
						((GWTDocument) object).setHasNotes(false);
					} else if (object instanceof GWTFolder) {
						((GWTFolder) object).setHasNotes(false);
					} else if (object instanceof GWTMail) {
						((GWTMail) object).setHasNotes(false);
					}

					Main.get().mainPanel.desktop.browser.fileBrowser.deleteNoteIconToSelectedRow();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("removeNote", caught);
			}
		});
	}

	/**
	 * setNote
	 */
	private void setNote(String notePath, final String text, final int row) {
		noteService.set(notePath, text, new AsyncCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				String text = (String) result;
				tableNotes.setHTML(row, 0, text);

				for (GWTNote note : getNotes()) {
					if (note.getPath().equals(editedNotePath)) {
						note.setText(text);
						break;
					}
				}

				reset();
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("setNote", caught);
			}
		});
	}

	/**
	 * getNotes
	 */
	public Collection<GWTNote> getNotes() {
		if (object instanceof GWTDocument) {
			return ((GWTDocument) object).getNotes();
		} else if (object instanceof GWTFolder) {
			return ((GWTFolder) object).getNotes();
		} else if (object instanceof GWTMail) {
			return ((GWTMail) object).getNotes();
		} else {
			return null;
		}
	}

	/**
	 * setAvailableOption
	 */
	public void showAddNote() {
		newNotePanel.setVisible(true);
	}

	/**
	 * showRemoveNote
	 */
	public void showRemoveNote() {
		removeNoteEnabled = true;
	}

	/**
	 * evaluateButton
	 */
	private void evaluateButtons() {
		boolean buttonsEnabled = (isChrome) ? textArea.getText().trim().length() > 0 : richTextArea.getText().trim().length() > 0;
		if (addButton != null) { // loading case
			addButton.setEnabled(buttonsEnabled);
		}
		if (updateButton != null) { // loading case
			updateButton.setEnabled(buttonsEnabled);
		}
	}

	/**
	 * setRichTextAreaText
	 */
	private void setRichTextAreaText(String text) {
		if (isChrome) {
			textArea.setText(text);
		} else {
			richTextArea.setText(text);
		}
		evaluateButtons();
	}

	/**
	 * NoteToDelete
	 *
	 * @author jllort
	 *
	 */
	public class NoteToDelete {
		private String notePath = "";
		private int row = 0;

		/**
		 * NoteToDelete
		 */
		public NoteToDelete(String notePath, int row) {
			this.notePath = notePath;
			this.row = row;
		}

		/**
		 * getNotePath
		 */
		public String getNotePath() {
			return notePath;
		}

		/**
		 * getRow
		 */
		public int getRow() {
			return row;
		}
	}
}
