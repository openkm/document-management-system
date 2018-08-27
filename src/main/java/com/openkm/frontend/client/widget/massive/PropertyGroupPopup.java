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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.service.*;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.validator.ValidatorToFire;
import com.openkm.frontend.client.widget.form.FormManager;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * PropertyGroupPopup popup
 *
 * @author jllort
 */
public class PropertyGroupPopup extends DialogBox implements ValidatorToFire {
	private static final OKMRepositoryServiceAsync repositoryService = GWT.create(OKMRepositoryService.class);
	private final OKMPropertyGroupServiceAsync propertyGroupService = GWT.create(OKMPropertyGroupService.class);
	private final OKMMassiveServiceAsync massiveService = GWT.create(OKMMassiveService.class);

	public static final int PHASE_NONE = 0;
	public static final int PHASE_SELECT = 1;
	public static final int PHASE_SHOW_PROPERTIES = 2;
	public static final int PHASE_PROPERTIES_ADDED = 3;

	private FlexTable table;
	private HorizontalPanel hPanel;
	private Button cancel;
	private Button add;
	private ListBox listBox;
	private String path;
	private String uuid;
	private FormManager manager;
	private HTML propertyGroupName;
	private FlexTable propertyGroupTable;
	private ScrollPanel scrollPropertyGroup;
	private boolean groupsLoaded = false;
	private int phase = PHASE_NONE;
	private Status status;
	private String grpName;
	
	/**
	 * PropertyGroupPopup popup
	 */
	public PropertyGroupPopup() {
		// Establishes auto-close when click outside
		super(false, true);
		setText(Main.i18n("group.label"));

		// Status
		status = new Status(this);
		status.setStyleName("okm-StatusPopup");

		table = new FlexTable();
		table.setCellPadding(4);
		table.setCellSpacing(0);
		table.setWidth("100%");
		hPanel = new HorizontalPanel();
		manager = new FormManager(this);

		propertyGroupTable = manager.getTable();
		propertyGroupTable.setWidth("100%");

		scrollPropertyGroup = new ScrollPanel();
		scrollPropertyGroup.add(propertyGroupTable);

		cancel = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
					Main.get().mainPanel.topPanel.toolBar.executeRefresh();
				}
				groupsLoaded = false;
				hide();
			}
		});

		add = new Button(Main.i18n("button.add"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addGroup();
			}
		});

		listBox = new ListBox();
		listBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				if (listBox.getSelectedIndex() > 0) {
					add.setEnabled(true);
				} else {
					add.setEnabled(false);
				}
			}
		});

		listBox.setStyleName("okm-Select");

		HorizontalPanel grpNamePanel = new HorizontalPanel();
		propertyGroupName = new HTML("");
		grpNamePanel.add(propertyGroupName);
		grpNamePanel.setWidth("100%");
		grpNamePanel.setCellHorizontalAlignment(propertyGroupName, HasAlignment.ALIGN_CENTER);

		cancel.setStyleName("okm-NoButton");
		add.setStyleName("okm-AddButton");
		add.setEnabled(false);

		hPanel.add(cancel);
		hPanel.add(new HTML("&nbsp;&nbsp;"));
		hPanel.add(add);

		hPanel.setCellHorizontalAlignment(cancel, VerticalPanel.ALIGN_CENTER);
		hPanel.setCellHorizontalAlignment(add, VerticalPanel.ALIGN_CENTER);

		table.setWidget(0, 0, listBox);
		table.setWidget(1, 0, grpNamePanel);
		table.setWidget(2, 0, scrollPropertyGroup);
		table.setWidget(3, 0, hPanel);

		table.getCellFormatter().setStyleName(1, 0, "okm-Security-Title");
		table.getCellFormatter().addStyleName(1, 0, "okm-Security-Title-RightBorder");

		table.getCellFormatter().setHorizontalAlignment(0, 0, HasAlignment.ALIGN_CENTER);
		table.getCellFormatter().setHorizontalAlignment(1, 0, HasAlignment.ALIGN_CENTER);
		table.getCellFormatter().setHorizontalAlignment(2, 0, HasAlignment.ALIGN_CENTER);
		table.getCellFormatter().setHorizontalAlignment(3, 0, HasAlignment.ALIGN_CENTER);

		super.hide();
		setWidget(table);
	}

	/**
	 * Enables close button
	 */
	public void enableClose() {
		cancel.setEnabled(true);
		Main.get().mainPanel.setVisible(true); // Shows main panel when all
		// widgets are loaded
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("group.label"));
		cancel.setText(Main.i18n("button.cancel"));
		add.setText(Main.i18n("button.add"));
	}

	/**
	 * reset
	 */
	public void reset() {
		switchPhase(PHASE_SELECT);
		if (!groupsLoaded) {
			groupsLoaded = true;
			getAllGroups(); // Gets all groups
		}
	}

	/**
	 * drawPhase
	 */
	private void switchPhase(int phase) {
		this.phase = phase;
		switch (phase) {
			case PHASE_PROPERTIES_ADDED:
				listBox.removeItem(listBox.getSelectedIndex());
				listBox.setSelectedIndex(0);
				// not break because continues with phase select
			case PHASE_SELECT:
				table.getCellFormatter().setVisible(0, 0, true);
				table.getCellFormatter().setVisible(1, 0, false);
				table.getCellFormatter().setVisible(2, 0, false);
				add.setEnabled(false);
				break;

			case PHASE_SHOW_PROPERTIES:
				table.getCellFormatter().setVisible(0, 0, false);
				table.getCellFormatter().setVisible(1, 0, true);
				table.getCellFormatter().setVisible(2, 0, true);
				add.setEnabled(true);
				int heightManager = manager.getTable().getOffsetHeight();
				int heightWindow = Window.getClientHeight();
				if (heightManager > heightWindow) {
					scrollPropertyGroup.setHeight((heightWindow * 0.8) + "px");
				} else {
					scrollPropertyGroup.setHeight("100%");
				}
				scrollPropertyGroup.setWidth((manager.getTable().getOffsetWidth() + 15) + "px");
				break;
		}
		center();
	}

	/**
	 * Gets all property groups
	 */
	private void getAllGroups() {
		if (!Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
			path = Main.get().mainPanel.topPanel.toolBar.getActualNodePath();
			uuid = Main.get().mainPanel.topPanel.toolBar.getActualNodeUUID();
			if (!path.equals("")) {
				propertyGroupService.getAllGroups(path, new AsyncCallback<List<GWTPropertyGroup>>() {
					@Override
					public void onSuccess(List<GWTPropertyGroup> result) {
						listBox.clear();
						listBox.addItem("", ""); // Adds empty value

						for (Iterator<GWTPropertyGroup> it = result.iterator(); it.hasNext(); ) {
							GWTPropertyGroup group = it.next();
							listBox.addItem(group.getLabel(), group.getName());
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Main.get().showError("GetAllGroups", caught);
					}
				});
			}
		} else {
			propertyGroupService.getAllGroups(new AsyncCallback<List<GWTPropertyGroup>>() {
				@Override
				public void onSuccess(List<GWTPropertyGroup> result) {
					listBox.clear();
					listBox.addItem("", ""); // Adds empty value

					for (Iterator<GWTPropertyGroup> it = result.iterator(); it.hasNext(); ) {
						GWTPropertyGroup group = it.next();
						listBox.addItem(group.getLabel(), group.getName());
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("GetAllGroups", caught);
				}
			});
		}
	}

	/**
	 * Add a group to a document
	 */
	private void addGroup() {
		if (listBox.getSelectedIndex() > 0) {
			grpName = listBox.getValue(listBox.getSelectedIndex());
			if (phase == PHASE_SHOW_PROPERTIES) {
				List<String> uuids;

				if (!Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
					uuids = Arrays.asList(Main.get().mainPanel.topPanel.toolBar.getActualNodeUUID());
				} else {
					uuids = Main.get().mainPanel.desktop.browser.fileBrowser.getAllSelectedUUIDs();
				}

				manager.getValidationProcessor().validate(uuids); // Allow plugins
			} else {
				propertyGroupName.setHTML(listBox.getItemText(listBox.getSelectedIndex()));
				// Case massive or non single document selected
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive() || Main.get().mainPanel.topPanel.toolBar.getActualNode() instanceof GWTDocument) {
					propertyGroupService.getPropertyGroupForm(grpName, new AsyncCallback<List<GWTFormElement>>() {
						@Override
						public void onSuccess(List<GWTFormElement> result) {
							manager.setFormElements(result);
							manager.edit();
							switchPhase(PHASE_SHOW_PROPERTIES);
						}

						@Override
						public void onFailure(Throwable caught) {
							Main.get().showError("getPropertyGroupForm", caught);
						}
					});
				} else {
					// Case single document is selected suggestion should be enabled
					propertyGroupService.getPropertyGroupForm(grpName, path, true, new AsyncCallback<List<GWTFormElement>>() {
						@Override
						public void onSuccess(List<GWTFormElement> result) {
							manager.setFormElements(result);
							manager.edit();
							switchPhase(PHASE_SHOW_PROPERTIES);
						}

						@Override
						public void onFailure(Throwable caught) {
							Main.get().showError("getPropertyGroupForm", caught);
						}
					});
				}
			}
		}
	}
	
	@Override
	public void validationWithPluginsFinished(boolean result) {
		if (result) {
			if (!Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
				status.setFlagAddPropertyGroup();
				propertyGroupService.addGroup(path, grpName, new AsyncCallback<Object>() {
					@Override
					public void onSuccess(Object result) {
						// Adding properties
						propertyGroupService.setProperties(path, grpName, manager.updateFormElementsValuesWithNewer(),
								new AsyncCallback<Object>() {
									@Override
									public void onSuccess(Object result) {
										repositoryService.getPathByUUID(uuid, new AsyncCallback<String>() {
											@Override
											public void onSuccess(String newPath) {
												if (!path.equals(newPath)) {
													path = newPath;
													CommonUI.openPathByUuid(uuid);
												} else {
													PropertyGroupUtils.refreshingActualNode(manager.updateFormElementsValuesWithNewer(), (listBox.getItemCount() == 1));
												}

												switchPhase(PHASE_PROPERTIES_ADDED);
												status.unsetFlagAddPropertyGroup();
											}

											@Override
											public void onFailure(Throwable caught) {
												status.unsetFlagAddPropertyGroup();
												Main.get().showError("setProperties", caught);
											}
										});
									}

									@Override
									public void onFailure(Throwable caught) {
										status.unsetFlagAddPropertyGroup();
										Main.get().showError("setProperties", caught);
									}
								});
					}

					@Override
					public void onFailure(Throwable caught) {
						status.unsetFlagAddPropertyGroup();
						Main.get().showError("AddGroup", caught);
					}
				});
			} else {
				status.setFlagAddPropertyGroup();
				massiveService.addPropertyGroup(Main.get().mainPanel.desktop.browser.fileBrowser.getAllSelectedPaths(), grpName,
						new AsyncCallback<Object>() {
							@Override
							public void onSuccess(Object result) {
								massiveService.setProperties(
										Main.get().mainPanel.desktop.browser.fileBrowser.getAllSelectedPaths(), grpName,
										manager.updateFormElementsValuesWithNewer(), new AsyncCallback<Object>() {
											@Override
											public void onSuccess(Object result) {
												PropertyGroupUtils.refreshingActualNode(
														manager.updateFormElementsValuesWithNewer(), (listBox.getItemCount() == 1));
												switchPhase(PHASE_PROPERTIES_ADDED);
												status.unsetFlagAddPropertyGroup();
											}

											@Override
											public void onFailure(Throwable caught) {
												Main.get().showError("setProperties", caught);
												status.unsetFlagAddPropertyGroup();
											}
										});
							}

							@Override
							public void onFailure(Throwable caught) {
								status.unsetFlagAddPropertyGroup();
								Main.get().showError("AddGroup", caught);
							}
						});
			}
		}
	}
}
