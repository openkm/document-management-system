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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.bean.extension.GWTWikiPage;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * WikiEditor
 *
 * @author jllort
 *
 */
public class WikiEditor extends Composite {
	public static final int NONE = 0;
	public static final int CREATE = 1;
	public static final int UPDATE = 2;

	private VerticalPanel vPanel;
	private TextArea textArea;
	private WikiToolBarEditor toolbar;

	private Button cancel;
	private Button create;
	private Button update;
	private int action = NONE;
	private GWTWikiPage wikiPage;

	/**
	 * WikiEditor
	 */
	public WikiEditor(final WikiController controller) {
		SimplePanel sp = new SimplePanel();
		vPanel = new VerticalPanel();
		sp.add(vPanel);

		// Space
		HTML space = new HTML("");
		vPanel.add(space);

		// TextArea
		textArea = new TextArea();
		textArea.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				evaluateUpdateButton();
			}
		});

		toolbar = new WikiToolBarEditor(textArea);
		textArea.setSize("700px", "200px");
		textArea.setStyleName("okm-TextArea");
		textArea.addStyleName("okm-EnableSelect");
		HorizontalPanel textAreaPanel = new HorizontalPanel();
		HTML textAreLeftSpace = new HTML("&nbsp;");
		textAreaPanel.add(textAreLeftSpace);
		VerticalPanel editorPanel = new VerticalPanel();
		HorizontalPanel hPanel = new HorizontalPanel();
		HTML space5 = new HTML();
		hPanel.add(textArea);
		hPanel.add(space5);
		Widget smilesPanel = toolbar.getSmilesPanel();
		hPanel.add(smilesPanel);
		hPanel.setCellVerticalAlignment(textArea, HasAlignment.ALIGN_TOP);
		hPanel.setCellVerticalAlignment(smilesPanel, HasAlignment.ALIGN_TOP);
		hPanel.setCellWidth(space5, "5px");
		editorPanel.add(toolbar.getColorPanel());
		editorPanel.add(toolbar);
		editorPanel.add(hPanel);
		textAreaPanel.add(editorPanel);
		textAreaPanel.setCellWidth(textAreLeftSpace, "5px");
		vPanel.add(textAreaPanel);

		// Space
		HTML space2 = new HTML("&nbsp;");
		vPanel.add(space2);

		// Create
		create = new Button(GeneralComunicator.i18n("button.create"));
		create.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				switch (action) {
					case CREATE:
						GWTWikiPage newWikiPage = new GWTWikiPage();
						newWikiPage.setNode(Wiki.get().getUuid());

						// Case wiki is not a repository object with uuid
						if (newWikiPage.getNode() != null) {
							newWikiPage.setTitle(Wiki.get().getUuid());
						} else {
							newWikiPage.setTitle(wikiPage.getTitle());
						}

						newWikiPage.setUser(GeneralComunicator.getUser());
						newWikiPage.setContent(textArea.getText());
						controller.createNewWikiPage(newWikiPage);
						break;
				}
				GeneralComunicator.enableKeyShorcuts();
			}

			;
		});
		create.setStyleName("okm-AddButton");

		// Cancel 
		cancel = new Button(GeneralComunicator.i18n("button.cancel"));
		cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				switch (action) {
					case CREATE:
						controller.cancelWikiPageCreation();
						break;
					case UPDATE:
						controller.unlockWikiPage(wikiPage);
						break;
				}
				GeneralComunicator.enableKeyShorcuts();
			}
		});
		cancel.setStyleName("okm-NoButton");

		// update
		update = new Button(GeneralComunicator.i18n("button.accept"));
		update.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				switch (action) {
					case UPDATE:
						wikiPage.setContent(textArea.getText());
						wikiPage.setUser(GeneralComunicator.getUser());
						controller.updateWikiPage(wikiPage);
						break;
				}
				GeneralComunicator.enableKeyShorcuts();
			}
		});
		update.setStyleName("okm-YesButton");

		// Button panel
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(new HTML("&nbsp;"));
		buttonPanel.add(cancel);
		buttonPanel.add(new HTML("&nbsp;"));
		buttonPanel.add(create);
		buttonPanel.add(update);
		vPanel.add(buttonPanel);

		// Space
		HTML space3 = new HTML("");
		vPanel.add(space3);

		vPanel.setCellHorizontalAlignment(buttonPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHeight(space, "5px");
		vPanel.setCellHeight(space2, "5px");
		vPanel.setCellHeight(space3, "5px");

		initWidget(sp);
	}

	/**
	 * setEditorSize
	 *
	 * @param width
	 */
	public void setEditorSize(int width) {
		if (width - (WikiToolBarEditor.SMILES_TABLE_WIDTH + 45) > 700) {
			textArea.setSize("" + (width - (WikiToolBarEditor.SMILES_TABLE_WIDTH + 45)) + "px", "200px");
		} else {
			textArea.setSize("700px", "200px");
		}
	}

	/**
	 * addDocumentTag
	 *
	 * @param uuid
	 * @param docName
	 */
	public void addDocumentTag(String uuid, String docName) {
		toolbar.addDocumentTag(uuid, docName);
		evaluateUpdateButton();
	}

	/**
	 * addImageTag
	 */
	public void addImageTag(String url, String params) {
		toolbar.addImageTag(url, params);
		evaluateUpdateButton();
	}

	/**
	 * addFolderTag
	 *
	 * @param uuid
	 * @param fldName
	 */
	public void addFolderTag(String uuid, String fldName) {
		toolbar.addFolderTag(uuid, fldName);
		evaluateUpdateButton();
	}

	/**
	 * addWigiTag
	 *
	 * @param uuid
	 */
	public void addWigiTag(String wikiTitle) {
		toolbar.addWigiTag(wikiTitle);
		evaluateUpdateButton();
	}

	/**
	 * reset
	 */
	public void reset() {
		textArea.setText("");
		evaluateUpdateButton();
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		toolbar.langRefresh();
	}

	/**
	 * setAction
	 *
	 * @param action The action
	 */
	public void setAction(int action) {
		this.action = action;
		switch (action) {
			case CREATE:
				create.setVisible(true);
				update.setVisible(false);
				break;
			case UPDATE:
				create.setVisible(false);
				update.setVisible(true);
				break;
		}
	}

	public void setWikiPage(GWTWikiPage wikiPage) {
		this.wikiPage = wikiPage;
		if (wikiPage != null) {
			textArea.setText(wikiPage.getContent());
		}
		evaluateUpdateButton();
	}

	/**
	 * evaluateUpdateButton
	 */
	private void evaluateUpdateButton() {
		if (textArea.getText().equals("")) {
			update.setEnabled(false);
		} else {
			update.setEnabled(true);
		}
	}
}