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

package com.openkm.frontend.client.widget.form;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTWorkspace;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.widget.searchin.HasPropertyHandler;

/**
 * FolderSelectPopup
 *
 * @author jllort
 *
 */
public class FolderSelectPopup extends DialogBox {

	private ListBox contextListBox;
	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private HorizontalPanel hListPanel;
	private HorizontalPanel hContextPanel;
	private HTML contextTxt;
	private ScrollPanel scrollDirectoryPanel;
	private VerticalPanel verticalDirectoryPanel;
	private FolderSelectTree folderSelectTree;
	private Button cancelButton;
	private Button actionButton;
	private TextBox textBox;
	private HasPropertyHandler propertyHandler;
	private boolean categoriesVisible = false;
	private boolean thesaurusVisible = false;
	private boolean templatesVisible = false;
	private boolean personalVisible = false;
	private boolean mailVisible = false;

	/**
	 * FolderSelectPopup
	 */
	public FolderSelectPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		vPanel = new VerticalPanel();
		vPanel.setWidth("300px");
		vPanel.setHeight("200px");
		hPanel = new HorizontalPanel();
		hListPanel = new HorizontalPanel();
		hContextPanel = new HorizontalPanel();

		contextTxt = new HTML(Main.i18n("search.context"));
		contextListBox = new ListBox();
		contextListBox.setStyleName("okm-Select");

		contextListBox.addChangeHandler(new ChangeHandler() {
			                                @Override
			                                public void onChange(ChangeEvent event) {
				                                folderSelectTree.changeView(Integer.parseInt(contextListBox.getValue(contextListBox.getSelectedIndex())));
			                                }
		                                }
		);
		hContextPanel.add(contextTxt);
		hContextPanel.add(new HTML("&nbsp;&nbsp;"));
		hContextPanel.add(contextListBox);
		hContextPanel.setCellVerticalAlignment(contextTxt, HasVerticalAlignment.ALIGN_MIDDLE);

		hListPanel.add(hContextPanel);
		hListPanel.setWidth("290px");

		setText(Main.i18n("search.folder.filter"));

		scrollDirectoryPanel = new ScrollPanel();
		scrollDirectoryPanel.setSize("290px", "150px");
		scrollDirectoryPanel.setStyleName("okm-Popup-text");
		verticalDirectoryPanel = new VerticalPanel();
		verticalDirectoryPanel.setSize("100%", "100%");
		folderSelectTree = new FolderSelectTree();
		folderSelectTree.setSize("100%", "100%");

		verticalDirectoryPanel.add(folderSelectTree);
		scrollDirectoryPanel.add(verticalDirectoryPanel);

		cancelButton = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		actionButton = new Button(Main.i18n("button.select"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textBox.setValue(folderSelectTree.getActualPath());
				if (propertyHandler != null) {
					propertyHandler.metadataValueChanged();
				}
				hide();
			}
		});

		vPanel.add(new HTML("<br>"));
		vPanel.add(hListPanel);
		vPanel.add(new HTML("<br>"));
		vPanel.add(scrollDirectoryPanel);
		vPanel.add(new HTML("<br>"));
		hPanel.add(cancelButton);
		HTML space = new HTML();
		space.setWidth("50px");
		hPanel.add(space);
		hPanel.add(actionButton);
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(hListPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(scrollDirectoryPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHeight(scrollDirectoryPanel, "150px");

		cancelButton.setStyleName("okm-Input");
		actionButton.setStyleName("okm-Input");

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		contextTxt.setHTML(Main.i18n("search.context"));
		setText(Main.i18n("search.folder.filter"));
		cancelButton.setText(Main.i18n("button.cancel"));
		actionButton.setText(Main.i18n("button.select"));

		removeAllContextListItems();
		int count = 0;
		contextListBox.setItemText(count++, Main.i18n("leftpanel.label.taxonomy"));
		if (categoriesVisible) {
			contextListBox.setItemText(count++, Main.i18n("leftpanel.label.categories"));
		}
		if (thesaurusVisible) {
			contextListBox.setItemText(count++, Main.i18n("leftpanel.label.thesaurus"));
		}
		if (templatesVisible) {
			contextListBox.setItemText(count++, Main.i18n("leftpanel.label.templates"));
		}
		if (personalVisible) {
			contextListBox.setItemText(count++, Main.i18n("leftpanel.label.my.documents"));
		}
		if (mailVisible) {
			contextListBox.setItemText(count++, Main.i18n("leftpanel.label.mail"));
		}
	}

	/**
	 * Shows the popup 
	 */
	public void show(TextBox textBox, HasPropertyHandler propertyHandler) {
		this.textBox = textBox;
		this.propertyHandler = propertyHandler;
		int left = (Window.getClientWidth() - 300) / 2;
		int top = (Window.getClientHeight() - 200) / 2;
		setPopupPosition(left, top);

		// Resets to initial tree value
		folderSelectTree.reset();

		GWTWorkspace workspace = Main.get().workspaceUserProperties.getWorkspace();
		categoriesVisible = workspace.isStackCategoriesVisible();
		thesaurusVisible = workspace.isStackThesaurusVisible();
		templatesVisible = workspace.isStackTemplatesVisible();
		personalVisible = workspace.isStackPersonalVisible();
		mailVisible = workspace.isStackMailVisible();

		removeAllContextListItems();
		contextListBox.addItem(Main.i18n("leftpanel.label.taxonomy"), "" + UIDesktopConstants.NAVIGATOR_TAXONOMY);
		if (categoriesVisible) {
			contextListBox.addItem(Main.i18n("leftpanel.label.categories"), "" + UIDesktopConstants.NAVIGATOR_CATEGORIES);
		}
		if (thesaurusVisible) {
			contextListBox.addItem(Main.i18n("leftpanel.label.thesaurus"), "" + UIDesktopConstants.NAVIGATOR_THESAURUS);
		}
		if (templatesVisible) {
			contextListBox.addItem(Main.i18n("leftpanel.label.templates"), "" + UIDesktopConstants.NAVIGATOR_TEMPLATES);
		}
		if (personalVisible) {
			contextListBox.addItem(Main.i18n("leftpanel.label.my.documents"), "" + UIDesktopConstants.NAVIGATOR_PERSONAL);
		}
		if (mailVisible) {
			contextListBox.addItem(Main.i18n("leftpanel.label.mail"), "" + UIDesktopConstants.NAVIGATOR_MAIL);
		}

		super.show();
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
	 * removeAllContextListItems
	 */
	private void removeAllContextListItems() {
		while (contextListBox.getItemCount() > 0) {
			contextListBox.removeItem(0);
		}
	}
}