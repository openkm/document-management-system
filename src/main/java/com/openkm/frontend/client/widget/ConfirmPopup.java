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

package com.openkm.frontend.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTTaskInstance;
import com.openkm.frontend.client.bean.form.GWTButton;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.widget.Draggable.ObjectToMove;
import com.openkm.frontend.client.widget.form.FormManager.ValidationButton;
import com.openkm.frontend.client.widget.form.HasWorkflow;
import com.openkm.frontend.client.widget.properties.CategoryManager.CategoryToRemove;
import com.openkm.frontend.client.widget.properties.KeywordManager.KeywordToRemove;
import com.openkm.frontend.client.widget.properties.Notes.NoteToDelete;
import eu.maydu.gwt.validation.client.ValidationProcessor;

/**
 * Confirm panel
 *
 * @author jllort
 */
public class ConfirmPopup extends DialogBox {

	public static final int NO_ACTION = 0;
	public static final int CONFIRM_DELETE_FOLDER = 1;
	public static final int CONFIRM_DELETE_DOCUMENT = 2;
	public static final int CONFIRM_EMPTY_TRASH = 3;
	public static final int CONFIRM_PURGE_FOLDER = 4;
	public static final int CONFIRM_PURGE_DOCUMENT = 5;
	public static final int CONFIRM_DELETE_PROPERTY_GROUP = 6;
	public static final int CONFIRM_PURGE_VERSION_HISTORY_DOCUMENT = 7;
	public static final int CONFIRM_RESTORE_HISTORY_DOCUMENT = 8;
	public static final int CONFIRM_SET_DEFAULT_HOME = 9;
	public static final int CONFIRM_DELETE_SAVED_SEARCH = 10;
	public static final int CONFIRM_DELETE_USER_NEWS = 11;
	public static final int CONFIRM_DELETE_MAIL = 12;
	public static final int CONFIRM_PURGE_MAIL = 13;
	public static final int CONFIRM_GET_POOLED_WORKFLOW_TASK = 14;
	public static final int CONFIRM_FORCE_UNLOCK = 15;
	public static final int CONFIRM_FORCE_CANCEL_CHECKOUT = 16;
	public static final int CONFIRM_WORKFLOW_ACTION = 17;
	public static final int CONFIRM_DELETE_NOTE_DOCUMENT = 18;
	public static final int CONFIRM_DELETE_NOTE_FOLDER = 19;
	public static final int CONFIRM_DELETE_NOTE_MAIL = 20;
	public static final int CONFIRM_DRAG_DROP_MOVE_DOCUMENT = 21;
	public static final int CONFIRM_DRAG_DROP_MOVE_FOLDER_FROM_TREE = 22;
	public static final int CONFIRM_DRAG_DROP_MOVE_FOLDER_FROM_BROWSER = 23;
	public static final int CONFIRM_DRAG_DROP_MOVE_MAIL = 24;
	public static final int CONFIRM_DELETE_CATEGORY_FOLDER = 25;
	public static final int CONFIRM_DELETE_CATEGORY_DOCUMENT = 26;
	public static final int CONFIRM_DELETE_CATEGORY_MAIL = 27;
	public static final int CONFIRM_DELETE_KEYWORD_FOLDER = 28;
	public static final int CONFIRM_DELETE_KEYWORD_DOCUMENT = 29;
	public static final int CONFIRM_DELETE_KEYWORD_MAIL = 30;
	public static final int CONFIRM_DELETE_MASSIVE = 31;
	public static final int CONFIRM_LOGOUT_DOCUMENTS_CHECKOUT = 32;
	public static final int CONFIRM_FORCE_CHAT_LOGIN = 39;
	public static final int CONFIRM_LOCK_MASSIVE = 41;
	public static final int CONFIRM_UNLOCK_MASSIVE = 42;

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private HTML text;
	private Button cancelButton;
	private Button acceptButton;
	private int action = 0;
	private Object object;

	/**
	 * Confirm popup
	 */
	public ConfirmPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		vPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();
		text = new HTML();
		text.setStyleName("okm-NoWrap");
		text.addStyleName("okm-Padding");

		cancelButton = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		acceptButton = new Button(Main.i18n("button.accept"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				execute();
				hide();
			}
		});

		vPanel.setWidth("300px");
		vPanel.setHeight("100px");
		cancelButton.setStyleName("okm-NoButton");
		acceptButton.setStyleName("okm-YesButton");

		text.setHTML("");

		hPanel.add(cancelButton);
		hPanel.add(new HTML("&nbsp;&nbsp;"));
		hPanel.add(acceptButton);

		vPanel.add(new HTML("<br>"));
		vPanel.add(text);
		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(text, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, VerticalPanel.ALIGN_CENTER);

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * Execute the confirmed action
	 */
	private void execute() {
		switch (action) {

			case CONFIRM_DELETE_FOLDER:
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
					Main.get().mainPanel.desktop.browser.fileBrowser.delete();
				} else if (Main.get().activeFolderTree.isPanelSelected()) {
					Main.get().activeFolderTree.delete();
				}
				break;

			case CONFIRM_DELETE_DOCUMENT:
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
					Main.get().mainPanel.desktop.browser.fileBrowser.delete();
				}
				break;

			case CONFIRM_EMPTY_TRASH:
				// Ensures DESKTOP view is enabled
				if (Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace() != UIDockPanelConstants.DESKTOP) {
					Main.get().mainPanel.topPanel.tabWorkspace.changeSelectedTab(UIDockPanelConstants.DESKTOP);
				}

				// Ensures that trash view is enabled
				if (Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_TRASH) {
					Main.get().mainPanel.desktop.navigator.stackPanel.showStack(UIDesktopConstants.NAVIGATOR_TRASH, false);
				}

				Main.get().activeFolderTree.purgeTrash();
				break;

			case CONFIRM_PURGE_FOLDER:
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
					Main.get().mainPanel.desktop.browser.fileBrowser.purge();
				} else if (Main.get().activeFolderTree.isPanelSelected()) {
					Main.get().activeFolderTree.purge();
				}
				break;

			case CONFIRM_PURGE_DOCUMENT:
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
					Main.get().mainPanel.desktop.browser.fileBrowser.purge();
				}
				break;

			case CONFIRM_DELETE_PROPERTY_GROUP:
				if (Main.get().mainPanel.topPanel.toolBar.isNodeDocument()) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.removePropertyGroup();
				} else if (Main.get().mainPanel.topPanel.toolBar.isNodeFolder()) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.removePropertyGroup();
				} else if (Main.get().mainPanel.topPanel.toolBar.isNodeMail()) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.removePropertyGroup();
				}
				// Always if a property group is deleted add property button on
				// tool bar must be enabled, we execute to ensure this
				Main.get().mainPanel.topPanel.toolBar.enableAddPropertyGroup();
				break;

			case CONFIRM_PURGE_VERSION_HISTORY_DOCUMENT:
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.version.purgeVersionHistory();
				break;

			case CONFIRM_RESTORE_HISTORY_DOCUMENT:
				if (object != null && object instanceof String) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.version.restoreVersion((String) object);
				}
				break;

			case CONFIRM_SET_DEFAULT_HOME:
				Main.get().mainPanel.topPanel.mainMenu.bookmark.setUserHome();
				break;

			case CONFIRM_DELETE_SAVED_SEARCH:
				Main.get().mainPanel.search.historySearch.searchSaved.deleteSearch();
				break;

			case CONFIRM_DELETE_USER_NEWS:
				Main.get().mainPanel.search.historySearch.userNews.deleteSearch();
				break;

			case CONFIRM_DELETE_MAIL:
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
					Main.get().mainPanel.desktop.browser.fileBrowser.delete();
				}
				break;

			case CONFIRM_PURGE_MAIL:
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
					Main.get().mainPanel.desktop.browser.fileBrowser.purge();
				}
				break;

			case CONFIRM_GET_POOLED_WORKFLOW_TASK:
				Main.get().mainPanel.dashboard.workflowDashboard.setTaskInstanceActorId();
				break;

			case CONFIRM_FORCE_UNLOCK:
				Main.get().mainPanel.desktop.browser.fileBrowser.forceUnlock();
				break;

			case CONFIRM_FORCE_CANCEL_CHECKOUT:
				Main.get().mainPanel.desktop.browser.fileBrowser.forceCancelCheckout();
				break;

			case CONFIRM_WORKFLOW_ACTION:
				if (object != null && object instanceof ValidationButton) {
					ValidationButton validationButton = (ValidationButton) object;
					GWTButton gWTButton = validationButton.getButton();
					ValidationProcessor validationProcessor = validationButton.getValidationProcessor();
					HasWorkflow workflow = validationButton.getWorkflow();
					GWTTaskInstance taskInstance = validationButton.getTaskInstance();
					if (gWTButton.isValidate()) {
						if (validationProcessor.validate()) {
							if (gWTButton.getTransition().equals("")) {
								workflow.setTaskInstanceValues(taskInstance.getId(), null);
							} else {
								workflow.setTaskInstanceValues(taskInstance.getId(), gWTButton.getTransition());
							}
							validationButton.disableAllButtonList();
						}
					} else {
						if (gWTButton.getTransition().equals("")) {
							workflow.setTaskInstanceValues(taskInstance.getId(), null);
						} else {
							workflow.setTaskInstanceValues(taskInstance.getId(), gWTButton.getTransition());
						}
						validationButton.disableAllButtonList();
					}
				}
				break;

			case CONFIRM_DELETE_NOTE_DOCUMENT:
				if (object != null && object instanceof NoteToDelete) {
					NoteToDelete noteToDelete = (NoteToDelete) object;
					Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.notes.deleteNote(noteToDelete.getNotePath(),
							noteToDelete.getRow());
				}
				break;

			case CONFIRM_DELETE_NOTE_FOLDER:
				if (object != null && object instanceof NoteToDelete) {
					NoteToDelete noteToDelete = (NoteToDelete) object;
					Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.notes.deleteNote(noteToDelete.getNotePath(),
							noteToDelete.getRow());
				}
				break;

			case CONFIRM_DELETE_NOTE_MAIL:
				if (object != null && object instanceof NoteToDelete) {
					NoteToDelete noteToDelete = (NoteToDelete) object;
					Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.notes.deleteNote(noteToDelete.getNotePath(),
							noteToDelete.getRow());
				}
				break;

			case CONFIRM_DRAG_DROP_MOVE_DOCUMENT:
				Main.get().draggable.modeDocument((ObjectToMove) object);
				break;

			case CONFIRM_DRAG_DROP_MOVE_FOLDER_FROM_TREE:
				Main.get().draggable.modeFolderFromTree((ObjectToMove) object);
				break;

			case CONFIRM_DRAG_DROP_MOVE_FOLDER_FROM_BROWSER:
				Main.get().draggable.modeFolderFromBrowser((ObjectToMove) object);
				break;

			case CONFIRM_DRAG_DROP_MOVE_MAIL:
				Main.get().draggable.modeMail((ObjectToMove) object);
				break;

			case CONFIRM_DELETE_CATEGORY_FOLDER:
				Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.removeCategory((CategoryToRemove) object);
				break;

			case CONFIRM_DELETE_CATEGORY_DOCUMENT:
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.removeCategory((CategoryToRemove) object);
				break;

			case CONFIRM_DELETE_CATEGORY_MAIL:
				Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mail.removeCategory((CategoryToRemove) object);
				break;

			case CONFIRM_DELETE_KEYWORD_FOLDER:
				Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.removeKeyword((KeywordToRemove) object);
				break;

			case CONFIRM_DELETE_KEYWORD_DOCUMENT:
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.removeKeyword((KeywordToRemove) object);
				break;

			case CONFIRM_DELETE_KEYWORD_MAIL:
				Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mail.removeKeyword((KeywordToRemove) object);
				break;

			case CONFIRM_DELETE_MASSIVE:
				Main.get().mainPanel.desktop.browser.fileBrowser.deleteMasive();
				break;

			case CONFIRM_LOGOUT_DOCUMENTS_CHECKOUT:
				Main.get().logoutPopup.logout();
				break;

			case CONFIRM_FORCE_CHAT_LOGIN:
				Main.get().mainPanel.bottomPanel.userInfo.forceLogin();
				break;

			case CONFIRM_LOCK_MASSIVE:
				Main.get().mainPanel.desktop.browser.fileBrowser.lockMasive();
				break;

			case CONFIRM_UNLOCK_MASSIVE:
				Main.get().mainPanel.desktop.browser.fileBrowser.unlockMasive();
				break;
		}

		action = NO_ACTION; // Resets action value
	}

	/**
	 * Sets the action to be confirmed
	 *
	 * @param action The action to be confirmed
	 */
	public void setConfirm(int action) {
		this.action = action;
		switch (action) {

			case CONFIRM_DELETE_FOLDER:
				text.setHTML(Main.i18n("confirm.delete.folder"));
				break;

			case CONFIRM_DELETE_DOCUMENT:
				text.setHTML(Main.i18n("confirm.delete.document"));
				break;

			case CONFIRM_EMPTY_TRASH:
				text.setHTML(Main.i18n("confirm.delete.trash"));
				break;

			case CONFIRM_PURGE_FOLDER:
				text.setHTML(Main.i18n("confirm.purge.folder"));
				break;

			case CONFIRM_PURGE_DOCUMENT:
				text.setHTML(Main.i18n("confirm.purge.document"));
				break;

			case CONFIRM_DELETE_PROPERTY_GROUP:
				text.setHTML(Main.i18n("confirm.delete.property.group"));
				break;

			case CONFIRM_PURGE_VERSION_HISTORY_DOCUMENT:
				text.setHTML(Main.i18n("confirm.purge.version.history.document"));
				break;

			case CONFIRM_RESTORE_HISTORY_DOCUMENT:
				text.setHTML(Main.i18n("confirm.purge.restore.document"));
				break;

			case CONFIRM_SET_DEFAULT_HOME:
				text.setHTML(Main.i18n("confirm.set.default.home"));
				break;

			case CONFIRM_DELETE_SAVED_SEARCH:
				text.setHTML(Main.i18n("confirm.delete.saved.search"));
				break;

			case CONFIRM_DELETE_USER_NEWS:
				text.setHTML(Main.i18n("confirm.delete.user.news"));
				break;

			case CONFIRM_DELETE_MAIL:
				text.setHTML(Main.i18n("confirm.delete.mail"));
				break;

			case CONFIRM_GET_POOLED_WORKFLOW_TASK:
				text.setHTML(Main.i18n("confirm.get.pooled.workflow.task"));
				break;

			case CONFIRM_FORCE_UNLOCK:
				text.setHTML(Main.i18n("confirm.force.unlock"));
				break;

			case CONFIRM_FORCE_CANCEL_CHECKOUT:
				text.setHTML(Main.i18n("confirm.force.cancel.checkout"));
				break;

			case CONFIRM_WORKFLOW_ACTION:
				break;

			case CONFIRM_DELETE_NOTE_DOCUMENT:
				text.setHTML(Main.i18n("confirm.delete.note"));
				break;

			case CONFIRM_DELETE_NOTE_FOLDER:
				text.setHTML(Main.i18n("confirm.delete.note"));
				break;

			case CONFIRM_DELETE_NOTE_MAIL:
				text.setHTML(Main.i18n("confirm.delete.note"));
				break;

			case CONFIRM_DRAG_DROP_MOVE_DOCUMENT:
				text.setHTML(Main.i18n("confirm.dragdrop.document"));
				break;

			case CONFIRM_DRAG_DROP_MOVE_FOLDER_FROM_TREE:
			case CONFIRM_DRAG_DROP_MOVE_FOLDER_FROM_BROWSER:
				text.setHTML(Main.i18n("confirm.dragdrop.folder"));
				break;

			case CONFIRM_DRAG_DROP_MOVE_MAIL:
				text.setHTML(Main.i18n("confirm.dragdrop.mail"));
				break;

			case CONFIRM_DELETE_CATEGORY_FOLDER:
			case CONFIRM_DELETE_CATEGORY_DOCUMENT:
			case CONFIRM_DELETE_CATEGORY_MAIL:
				text.setHTML(Main.i18n("confirm.category.delete"));
				break;

			case CONFIRM_DELETE_KEYWORD_FOLDER:
			case CONFIRM_DELETE_KEYWORD_DOCUMENT:
			case CONFIRM_DELETE_KEYWORD_MAIL:
				text.setHTML(Main.i18n("confirm.keyword.delete"));
				break;

			case CONFIRM_DELETE_MASSIVE:
				text.setHTML(Main.i18n("confirm.massive.delete"));
				break;

			case CONFIRM_LOGOUT_DOCUMENTS_CHECKOUT:
				text.setHTML(Main.i18n("confirm.logout.documents.checkout"));
				break;

			case CONFIRM_FORCE_CHAT_LOGIN:
				text.setHTML(Main.i18n("user.info.chat.force.login"));
				break;

			case CONFIRM_LOCK_MASSIVE:
				text.setHTML(Main.i18n("confirm.massive.lock"));
				break;

			case CONFIRM_UNLOCK_MASSIVE:
				text.setHTML(Main.i18n("confirm.massive.unlock"));
				break;
		}
	}

	/**
	 * setConfirmationText
	 *
	 * @param text
	 */
	public void setConfirmationText(String text) {
		this.text.setHTML(text);
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("confirm.label"));
		cancelButton.setText(Main.i18n("button.cancel"));
		acceptButton.setText(Main.i18n("button.accept"));
	}

	/**
	 * Sets the value to object
	 *
	 * @param object The object to set
	 */
	public void setValue(Object object) {
		this.object = object;
	}

	/**
	 * Get the object value
	 *
	 * @return The object
	 */
	public Object getValue() {
		return this.object;
	}

	/**
	 * Shows de popup
	 */
	public void show() {
		setText(Main.i18n("confirm.label"));
		int left = (Window.getClientWidth() - 300) / 2;
		int top = (Window.getClientHeight() - 125) / 2;
		setPopupPosition(left, top);
		super.show();
	}
}