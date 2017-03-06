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

package com.openkm.frontend.client.widget.thesaurus;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;

/**
 * ThesaurusSelectPopup
 *
 * @author jllort
 *
 */
public class ThesaurusSelectPopup extends DialogBox {
	public static final int NONE = -1;
	public static final int DOCUMENT_PROPERTIES = 0;
	public static final int WIZARD = 1;
	public static final int MAIL_PROPERTIES = 2;
	public static final int FOLDER_PROPERTIES = 3;
	public static final int MASSIVE = 4;

	public ThesaurusPanel thesaurusPanel;
	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private Button cancelButton;
	private Button actionButton;
	private int selectedFrom = NONE;

	/**
	 * ThesaurusSelectPopup
	 */
	public ThesaurusSelectPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		thesaurusPanel = new ThesaurusPanel();

		vPanel = new VerticalPanel();
		vPanel.setWidth("500px");
		vPanel.setHeight("325px");
		hPanel = new HorizontalPanel();

		cancelButton = new Button(Main.i18n("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		actionButton = new Button(Main.i18n("button.add"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (thesaurusPanel.isTabTreeSelected()) {
					executeAction(thesaurusPanel.getActualPath());
				} else if (thesaurusPanel.isTabKeywordSelected()) {
					executeAction(thesaurusPanel.getSelectedKeyword());
				}
			}
		});


		vPanel.add(thesaurusPanel);
		vPanel.add(new HTML("<br>"));
		hPanel.add(cancelButton);
		HTML space = new HTML();
		space.setWidth("50px");
		hPanel.add(space);
		hPanel.add(actionButton);
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(thesaurusPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHeight(thesaurusPanel, "300px");

		cancelButton.setStyleName("okm-NoButton");
		actionButton.setStyleName("okm-AddButton");

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * Executes the action
	 */
	public void executeAction(String actualPath) {
		String keyword = actualPath.substring(actualPath.lastIndexOf("/") + 1).replace(" ", "_");
		switch (selectedFrom) {
			case DOCUMENT_PROPERTIES:
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.addKeywordToPendinList(keyword);
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.addPendingKeyWordsList();
				break;
			case WIZARD:
				Main.get().wizardPopup.keywordsWidget.addKeywordToPendinList(keyword);
				Main.get().wizardPopup.keywordsWidget.addPendingKeyWordsList();
				break;
			case MAIL_PROPERTIES:
				Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mail.addKeywordToPendinList(keyword);
				Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mail.addPendingKeyWordsList();
				break;
			case FOLDER_PROPERTIES:
				Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.addKeywordToPendinList(keyword);
				Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.folder.addPendingKeyWordsList();
				break;
			case MASSIVE:
				Main.get().keywordsPopup.addKeywordToPendinList(keyword);
				Main.get().keywordsPopup.addPendingKeyWordsList();
				break;

		}
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("thesaurus.directory.select.label"));
		cancelButton.setText(Main.i18n("button.close"));
		actionButton.setText(Main.i18n("button.add"));
		thesaurusPanel.langRefresh();
	}

	/**
	 * Shows the popup 
	 */
	public void show(int selectedFrom) {
		this.selectedFrom = selectedFrom;
		initButtons();
		int left = (Window.getClientWidth() - 400) / 2;
		int top = (Window.getClientHeight() - 325) / 2;
		setPopupPosition(left, top);
		setText(Main.i18n("thesaurus.directory.select.label"));

		// Resets to initial tree value
		thesaurusPanel.reset();

		center();
	}

	/**
	 * Enables or disables move button
	 *
	 * @param enable
	 */
	public void enable(boolean enable) {
		actionButton.setEnabled(enable);
	}

	/**
	 * Enables all button
	 */
	private void initButtons() {
		cancelButton.setEnabled(true);
		actionButton.setEnabled(false);
	}
}