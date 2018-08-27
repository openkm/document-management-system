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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTPropertyParams;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.service.OKMMassiveService;
import com.openkm.frontend.client.service.OKMMassiveServiceAsync;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.util.validator.ValidatorToFire;
import com.openkm.frontend.client.widget.form.FormManager;
import com.openkm.frontend.client.widget.searchin.GroupPopup;
import com.openkm.frontend.client.widget.searchin.HasPropertyHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * UpdatePropertyGroupPopup
 *
 * @author jllort
 */
public class UpdatePropertyGroupPopup extends DialogBox implements HasPropertyHandler, ValidatorToFire {
	private final OKMMassiveServiceAsync massiveService = GWT.create(OKMMassiveService.class);

	private VerticalPanel vPanel;
	private FlexTable table;
	private FormManager formManager;
	public GroupPopup groupPopup;
	public Button addGroup;
	public Button updateButton;
	public Button cancelButton;
	private CheckBox recursive;
	private HTML recursiveText;
	private List<String> uuidList;
	private HorizontalPanel recursivePanel;

	/**
	 * UpdatePropertyGroupPopup
	 */
	public UpdatePropertyGroupPopup() {
		// Establishes auto-close when click outside
		super(false, true);
		setText(Main.i18n("group.update.label"));

		uuidList = new ArrayList<String>();
		vPanel = new VerticalPanel();
		formManager = new FormManager(this);
		formManager.setIsMassiveUpdate(this);
		table = new FlexTable();
		table.setWidth("100%");
		vPanel.setWidth("100%");

		// Table padding and spacing properties
		formManager.getTable().setCellPadding(2);
		formManager.getTable().setCellSpacing(2);

		// popup to add new property
		groupPopup = new GroupPopup();
		groupPopup.setWidth("250px");
		groupPopup.setHeight("125px");
		groupPopup.setStyleName("okm-Popup");
		groupPopup.addStyleName("okm-DisableSelect");

		addGroup = new Button(Main.i18n("search.add.property.group"));
		addGroup.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				groupPopup.show(UIDockPanelConstants.DESKTOP);
			}
		});
		addGroup.setStyleName("okm-AddButton");
		addGroup.addStyleName("okm-NoWrap");

		// recursive
		recursivePanel = new HorizontalPanel();
		recursive = new CheckBox();
		recursiveText = new HTML(Main.i18n("group.update.recursive"));
		recursivePanel.add(recursive);
		recursivePanel.add(Util.hSpace("5px"));
		recursivePanel.add(recursiveText);
		recursivePanel.setCellVerticalAlignment(recursive, HasAlignment.ALIGN_MIDDLE);
		recursivePanel.setCellVerticalAlignment(recursiveText, HasAlignment.ALIGN_MIDDLE);

		// group
		table.setWidget(0, 0, Util.hSpace("5px"));
		table.setWidget(0, 1, addGroup);
		table.setWidget(0, 2, Util.hSpace("5px"));

		// metadata
		table.setWidget(1, 0, Util.hSpace("5px"));
		table.setWidget(1, 1, formManager.getTable());
		table.setWidget(1, 2, Util.hSpace("5px"));

		// recursive
		table.setWidget(2, 0, Util.hSpace("5px"));
		table.setWidget(2, 1, recursivePanel);
		table.setWidget(2, 2, Util.hSpace("5px"));

		// buttons
		updateButton = new Button(Main.i18n("button.update"));
		updateButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (formManager.getValidationProcessor().validate()) {
					uuidList.clear();
					final boolean isMassive = Main.get().mainPanel.desktop.browser.fileBrowser.isMassive();

					if (!isMassive) {
						uuidList.add(Main.get().mainPanel.topPanel.toolBar.getActualNodeUUID());
					} else {
						uuidList.addAll(Main.get().mainPanel.desktop.browser.fileBrowser.table.getAllSelectedUUIDs());
					}

					massiveService.setMixedProperties(uuidList, formManager.updateFormElementsValuesWithNewer(), recursive.getValue(),
							new AsyncCallback<Object>() {
								@Override
								public void onSuccess(Object result) {
									if (!isMassive) {
										hide();
										PropertyGroupUtils.refreshingActualNode(formManager.updateFormElementsValuesWithNewer(), false);
									}
								}

								@Override
								public void onFailure(Throwable caught) {
									Main.get().showError("setMixedProperties", caught);
								}
							});

					if (isMassive) {
						hide();
					}
				}
			}
		});
		updateButton.setStyleName("okm-YesButton");

		cancelButton = new Button(Main.i18n("button.cancel"));
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		cancelButton.setStyleName("okm-NoButton");

		HorizontalPanel buttonsPanel = new HorizontalPanel();
		buttonsPanel.add(Util.hSpace("5px"));
		buttonsPanel.add(cancelButton);
		buttonsPanel.add(Util.hSpace("5px"));
		buttonsPanel.add(updateButton);
		buttonsPanel.add(Util.hSpace("5px"));

		// Main panel
		vPanel.add(table);
		vPanel.add(Util.vSpace("5px"));
		vPanel.add(buttonsPanel);
		vPanel.add(Util.vSpace("5px"));

		table.getCellFormatter().setHorizontalAlignment(0, 1, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(table, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(buttonsPanel, HasAlignment.ALIGN_CENTER);

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * validationPassed
	 */
	private void validationPassed() {
		uuidList.clear();
		final boolean isMassive = Main.get().mainPanel.desktop.browser.fileBrowser.isMassive();

		if (!isMassive) {
			uuidList.add(Main.get().mainPanel.topPanel.toolBar.getActualNodeUUID());
		} else {
			uuidList.addAll(Main.get().mainPanel.desktop.browser.fileBrowser.table.getAllSelectedUUIDs());
		}

		massiveService.setMixedProperties(uuidList, formManager.updateFormElementsValuesWithNewer(), recursive.getValue(),
				new AsyncCallback<Object>() {
					@Override
					public void onSuccess(Object result) {
						if (!isMassive) {
							PropertyGroupUtils.refreshingActualNode(formManager.updateFormElementsValuesWithNewer(), false);
						} else {
							Main.get().mainPanel.topPanel.toolBar.executeRefresh();
						}
						hide();
					}

					@Override
					public void onFailure(Throwable caught) {
						Main.get().showError("setMixedProperties", caught);
					}
				});

		if (isMassive) {
			hide();
		}
	}
	
	/**
	 * reset
	 */
	public void reset() {
		formManager = new FormManager(this);
		formManager.setIsMassiveUpdate(this);
		table.setWidget(1, 1, formManager.getTable());
		recursive.setValue(false);
		recursivePanel.setVisible(false);
		updateButton.setEnabled(false);
	}

	/**
	 * Add property group
	 *
	 * @param grpName Group key
	 * @param propertyName Property group key
	 * @param gwtMetadata Property metada
	 * @param propertyValue The selected value
	 */
	public void addProperty(final GWTPropertyParams propertyParam) {
		formManager.addPropertyParam(propertyParam);
		formManager.edit();
		evaluateUpdateButton();
	}

	/**
	 * Gets the actual form elements
	 *
	 * @return The actual form elements values
	 */
	public Collection<String> getFormElementsKeys() {
		List<String> keyList = new ArrayList<String>();
		for (GWTFormElement formElement : formManager.getFormElements()) {
			keyList.add(formElement.getName());
		}
		return keyList;
	}

	/**
	 * evaluateUpdateButton
	 */
	private void evaluateUpdateButton() {
		boolean active = (formManager.getFormElements().size() > 0);
		updateButton.setEnabled(active);
		recursivePanel.setVisible(active);
		center();
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		setText(Main.i18n("group.update.label"));
		addGroup.setHTML(Main.i18n("search.add.property.group"));
		recursiveText.setHTML(Main.i18n("group.update.recursive"));
		updateButton.setHTML(Main.i18n("button.update"));
		cancelButton.setHTML(Main.i18n("button.cancel"));
	}

	@Override
	public void propertyRemoved() {
		evaluateUpdateButton();
		formManager.buildValidators();
	}

	@Override
	public void metadataValueChanged() {
	}
	
	@Override
	public void validationWithPluginsFinished(boolean result) {
		if (result) {
			validationPassed();
		}
	}
}