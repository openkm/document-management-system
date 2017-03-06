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

package com.openkm.frontend.client.widget.searchin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.bean.GWTPropertyParams;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.service.OKMPropertyGroupService;
import com.openkm.frontend.client.service.OKMPropertyGroupServiceAsync;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Group popup
 *
 * @author jllort
 *
 */
public class GroupPopup extends DialogBox {
	private final OKMPropertyGroupServiceAsync propertyGroupService = (OKMPropertyGroupServiceAsync) GWT.create(OKMPropertyGroupService.class);

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private Button closeButton;
	private Button addButton;
	private ListBox groupListBox;
	private ListBox propertyListBox;
	private List<GWTFormElement> formElementList = new ArrayList<GWTFormElement>();
	private FlexTable table;
	private Label groupLabel;
	private Label propertyLabel;
	private int validate = -1;
	private int origin = UIDockPanelConstants.SEARCH;

	/**
	 * About popup
	 */
	public GroupPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		vPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();
		groupLabel = new Label(Main.i18n("group.group"));
		propertyLabel = new Label(Main.i18n("group.property.group"));
		table = new FlexTable();

		closeButton = new Button(Main.i18n("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		addButton = new Button(Main.i18n("button.add"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (propertyListBox.getSelectedIndex() > 0) {
					String grpName = groupListBox.getValue(groupListBox.getSelectedIndex());
					String grpLabel = groupListBox.getItemText(groupListBox.getSelectedIndex());
					String propertyName = propertyListBox.getValue(propertyListBox.getSelectedIndex());
					for (Iterator<GWTFormElement> it = formElementList.iterator(); it.hasNext(); ) {
						GWTFormElement formElement = it.next();
						if (formElement.getName().endsWith(propertyName)) {
							GWTPropertyParams param = new GWTPropertyParams();
							param.setGrpName(grpName);
							param.setGrpLabel(grpLabel);
							param.setFormElement(formElement);
							switch (origin) {
								case UIDockPanelConstants.SEARCH:
									Main.get().mainPanel.search.searchBrowser.searchIn.searchMetadata.addProperty(param);
									break;
								case UIDockPanelConstants.DESKTOP:
									Main.get().updatePropertyGroupPopup.addProperty(param);
									break;
							}
						}
					}
				}
				enableAddGroupButton(); // Enables or disables add group button ( if exist some property still to be added )
				hide();
			}
		});

		groupListBox = new ListBox();
		groupListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (groupListBox.getSelectedIndex() > 0) {
					propertyListBox.clear();
					getMetaData();
				} else {
					propertyListBox.clear();
					propertyListBox.setVisible(false);
					propertyLabel.setVisible(false);
					addButton.setEnabled(false);
				}
			}
		});
		groupListBox.setStyleName("okm-Select");

		propertyListBox = new ListBox();
		propertyListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (propertyListBox.getSelectedIndex() > 0) {
					addButton.setEnabled(true);
				} else {
					addButton.setEnabled(false);
				}
			}
		});
		propertyListBox.setStyleName("okm-Select");
		propertyListBox.setVisible(false);
		propertyLabel.setVisible(false);

		vPanel.setWidth("300px");
		vPanel.setHeight("100px");
		closeButton.setStyleName("okm-NoButton");
		addButton.setStyleName("okm-AddButton");
		addButton.setEnabled(false);

		hPanel.add(closeButton);
		hPanel.add(new HTML("&nbsp;&nbsp;"));
		hPanel.add(addButton);

		hPanel.setCellHorizontalAlignment(closeButton, VerticalPanel.ALIGN_CENTER);
		hPanel.setCellHorizontalAlignment(addButton, VerticalPanel.ALIGN_CENTER);

		table.setWidget(0, 0, groupLabel);
		table.setWidget(0, 1, groupListBox);
		table.setWidget(1, 0, propertyLabel);
		table.setWidget(1, 1, propertyListBox);

		vPanel.add(new HTML("<br>"));
		vPanel.add(table);
		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(table, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, VerticalPanel.ALIGN_CENTER);

		super.hide();
		setWidget(vPanel);
	}

	public void show(int origin) {
		this.origin = origin;
		show();
	}

	/**
	 * Gets asynchronous to get all groups
	 */
	final AsyncCallback<List<GWTPropertyGroup>> callbackGetAllGroups = new AsyncCallback<List<GWTPropertyGroup>>() {
		public void onSuccess(List<GWTPropertyGroup> result) {
			groupListBox.clear();
			groupListBox.addItem("", ""); // Adds empty value

			for (Iterator<GWTPropertyGroup> it = result.iterator(); it.hasNext(); ) {
				GWTPropertyGroup group = it.next();
				groupListBox.addItem(group.getLabel(), group.getName());
			}

			validate = 1;
			validateGroupsNoEmpty(); // Enables or disables add group button ( case exist some value to be added on list )
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetAllGroups", caught);
		}
	};

	/**
	 * Gets asynchronous to get property group form properties
	 */
	final AsyncCallback<List<GWTFormElement>> callbackGetPropertyGroupForm = new AsyncCallback<List<GWTFormElement>>() {
		public void onSuccess(List<GWTFormElement> result) {
			formElementList = result;
			propertyListBox.clear();
			propertyListBox.setVisible(true);
			propertyLabel.setVisible(true);
			propertyListBox.addItem("", ""); // First item is always blank

			Collection<String> actualProperties = new ArrayList<String>();
			switch (origin) {
				case UIDockPanelConstants.SEARCH:
					actualProperties = Main.get().mainPanel.search.searchBrowser.searchIn.getFormElementsKeys();
					break;
				case UIDockPanelConstants.DESKTOP:
					actualProperties = Main.get().updatePropertyGroupPopup.getFormElementsKeys();
					break;
			}


			for (Iterator<GWTFormElement> it = result.iterator(); it.hasNext(); ) {
				GWTFormElement formElement = it.next();
				if (!actualProperties.contains(formElement.getName())) { // Only appears properties not stil added
					propertyListBox.addItem(formElement.getLabel(), formElement.getName());
				}
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getMetaData", caught);
		}
	};

	/**
	 * Gets asyncronous to get property group form properties and validate is there's one not assigned
	 */
	final AsyncCallback<List<GWTFormElement>> callbackGetPropertyGroupFormDataToValidate = new AsyncCallback<List<GWTFormElement>>() {
		public void onSuccess(List<GWTFormElement> result) {
			formElementList = result;

			Collection<String> actualProperties = new ArrayList<String>();
			switch (origin) {
				case UIDockPanelConstants.SEARCH:
					actualProperties = Main.get().mainPanel.search.searchBrowser.searchIn.getFormElementsKeys();
					break;
				case UIDockPanelConstants.DESKTOP:
					actualProperties = Main.get().updatePropertyGroupPopup.getFormElementsKeys();
					break;
			}
			boolean found = false;

			for (Iterator<GWTFormElement> it = result.iterator(); it.hasNext(); ) {
				GWTFormElement formElement = it.next();
				String propertyName = formElement.getName();
				if (!actualProperties.contains(propertyName)) { // Only appears properties not stil added
					found = true;
				}
			}

			// Removes the item on list
			if (!found) {
				groupListBox.removeItem(validate); // When removing object it's not necessary to increment validate value
			} else {
				validate++;
			}
			validateGroupsNoEmpty();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getMetaData", caught);
		}
	};

	/**
	 * Enables close button
	 */
	public void enableClose() {
		closeButton.setEnabled(true);
		Main.get().mainPanel.setVisible(true); // Shows main panel when all widgets are loaded
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("group.label"));
		closeButton.setText(Main.i18n("button.close"));
		addButton.setText(Main.i18n("button.add"));
		groupLabel.setText(Main.i18n("group.group"));
		propertyLabel.setText(Main.i18n("group.property.group"));
	}

	/**
	 * Show the popup error
	 *
	 * @param msg Error message
	 */
	public void show() {
		int left = (Window.getClientWidth() - 300) / 2;
		int top = (Window.getClientHeight() - 100) / 2;
		setPopupPosition(left, top);
		setText(Main.i18n("group.label"));
		validate = -1;
		groupListBox.clear();
		propertyListBox.clear();
		propertyListBox.setVisible(false);
		propertyLabel.setVisible(false);
		getAllGroups(); // Gets all groups
		addButton.setEnabled(false);
		super.show();
	}

	/**
	 * Gets all property groups
	 */
	private void getAllGroups() {
		propertyGroupService.getAllGroups(callbackGetAllGroups);
	}

	/**
	 * Gets all metadata group properties 
	 */
	private void getMetaData() {
		propertyGroupService.getPropertyGroupForm(groupListBox.getValue(groupListBox.getSelectedIndex()), callbackGetPropertyGroupForm);
	}

	/**
	 * Enables or disables add group button ( case exist some item to be added )
	 */
	public void enableAddGroupButton() {
		groupListBox.clear();
		propertyListBox.clear();
		getAllGroups(); // Gets all groups
	}

	/**
	 * Validates that exist some item to add
	 */
	private void validateGroupsNoEmpty() {
		if (groupListBox.getItemCount() > validate) {
			String value = groupListBox.getValue(validate);
			propertyGroupService.getPropertyGroupForm(value, callbackGetPropertyGroupFormDataToValidate);
		} else {
			switch (origin) {
				case UIDockPanelConstants.SEARCH:
					// Validate button 
					if (groupListBox.getItemCount() > 1) {
						Main.get().mainPanel.search.searchBrowser.searchIn.searchMetadata.addGroup.setEnabled(true);
					} else {
						Main.get().mainPanel.search.searchBrowser.searchIn.searchMetadata.addGroup.setEnabled(false);
					}
					validate = -1; // Resets values
					break;
				case UIDockPanelConstants.DESKTOP:
					// Validate button 
					if (groupListBox.getItemCount() > 1) {
						Main.get().updatePropertyGroupPopup.addGroup.setEnabled(true);
					} else {
						Main.get().updatePropertyGroupPopup.addGroup.setEnabled(false);
					}
					validate = -1; // Resets values
					break;
			}

		}
	}
}
