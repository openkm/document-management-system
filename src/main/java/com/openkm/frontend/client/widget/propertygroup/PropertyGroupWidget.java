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

package com.openkm.frontend.client.widget.propertygroup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.extension.event.HasPropertyGroupEvent;
import com.openkm.frontend.client.extension.event.handler.PropertyGroupHandlerExtension;
import com.openkm.frontend.client.extension.event.hashandler.HasPropertyGroupHandlerExtension;
import com.openkm.frontend.client.service.OKMPropertyGroupService;
import com.openkm.frontend.client.service.OKMPropertyGroupServiceAsync;
import com.openkm.frontend.client.util.validator.ExtendedDefaultValidatorProcessor;
import com.openkm.frontend.client.util.validator.ValidatorToFire;
import com.openkm.frontend.client.widget.form.FormManager;

import java.util.*;

/**
 * PropertyGroupWidget
 *
 * @author jllort
 *
 */
public class PropertyGroupWidget extends Composite implements HasPropertyGroupEvent, HasPropertyGroupHandlerExtension {
	private final OKMPropertyGroupServiceAsync propertyGroupService = GWT.create(OKMPropertyGroupService.class);

	private String path;
	private CellFormatter cellFormatter;
	private PropertyGroupWidgetToFire propertyGroupWidgetToFire;
	private List<PropertyGroupHandlerExtension> propertyGroupHandlerExtensionList;
	private Map<String, GWTFormElement> propertyGroupVariablesMap = new HashMap<String, GWTFormElement>();
	private GWTPropertyGroup propertyGroup;
	private FormManager manager;
	private ScrollPanel scrollPropertyGroup;

	/**
	 * PropertyGroup
	 *
	 * @param path The document path
	 * @param propertyGroup The group
	 * @param widget Widget at first row
	 * @param propertyGroupWidgetToFire widget with methods to be fired
	 */
	public PropertyGroupWidget(String path, GWTPropertyGroup propertyGroup, Widget widget, PropertyGroupWidgetToFire propertyGroupWidgetToFire, ValidatorToFire validatorToFire) {
		start(path, propertyGroup, widget, propertyGroupWidgetToFire, validatorToFire);
	}
	/**
	 * start
	 */
	private void start(String path, GWTPropertyGroup propertyGroup, Widget widget, PropertyGroupWidgetToFire propertyGroupWidgetToFire, ValidatorToFire validatorToFire) {
		propertyGroupHandlerExtensionList = new ArrayList<PropertyGroupHandlerExtension>();
		manager = new FormManager(validatorToFire);
		this.path = path;
		this.propertyGroup = propertyGroup;
		this.propertyGroupWidgetToFire = propertyGroupWidgetToFire;

		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setWidth("100%");

		FlexTable propertyGroupTable = manager.getTable();
		propertyGroupTable.setWidth("100%");

		FlexTable widgetTable = new FlexTable();
		widgetTable.setCellPadding(0);
		widgetTable.setCellSpacing(0);
		widgetTable.setWidth("100%");
		widgetTable.setWidget(0, 0, widget);
		widgetTable.getFlexCellFormatter().setColSpan(0, 0, 2);

		// Widget format
		widgetTable.getCellFormatter().setHorizontalAlignment(0, 0, HasAlignment.ALIGN_CENTER);
		widgetTable.getCellFormatter().setVerticalAlignment(0, 0, HasAlignment.ALIGN_MIDDLE);

		RowFormatter rowFormatter = widgetTable.getRowFormatter();
		rowFormatter.setStyleName(0, "okm-Security-Title");

		cellFormatter = widgetTable.getCellFormatter(); // Gets the cell formatter

		// Format borders and margins
		cellFormatter.addStyleName(0, 0, "okm-Security-Title-RightBorder");

		vPanel.add(widgetTable);

		scrollPropertyGroup = new ScrollPanel();
		scrollPropertyGroup.add(propertyGroupTable);
		vPanel.add(scrollPropertyGroup);

		initWidget(vPanel);
	}

	/**
	 * Gets asyncronous to group properties
	 */
	final AsyncCallback<List<GWTFormElement>> callbackGetProperties = new AsyncCallback<List<GWTFormElement>>() {
		@Override
		public void onSuccess(List<GWTFormElement> result) {
			manager.setFormElements(result);
			if (!propertyGroupVariablesMap.isEmpty()) {
				manager.loadDataFromPropertyGroupVariables(propertyGroupVariablesMap);
			}
			manager.draw(propertyGroup.isReadonly());

			if (propertyGroupWidgetToFire != null) {
				propertyGroupWidgetToFire.finishedGetProperties();
			}

			fireEvent(HasPropertyGroupEvent.PROPERTYGROUP_GET_PROPERTIES);

			int heightManager = manager.getTable().getOffsetHeight();
			int heightWindow = Window.getClientHeight();
			if (heightManager > heightWindow) {
				scrollPropertyGroup.setHeight((heightWindow * 0.8) + "px");
			} else {
				scrollPropertyGroup.setHeight("100%");
			}
		}

		@Override
		public void onFailure(Throwable caught) {
			Main.get().showError("getMetaData", caught);

			if (propertyGroupWidgetToFire != null) {
				propertyGroupWidgetToFire.finishedGetProperties();
			}
		}
	};

	/**
	 * Gets asyncronous to set properties
	 */
	final AsyncCallback<Object> callbackSetProperties = new AsyncCallback<Object>() {
		@Override
		public void onSuccess(Object result) {
			if (propertyGroupWidgetToFire != null) {
				propertyGroupWidgetToFire.finishedSetProperties();
			}
			fireEvent(HasPropertyGroupEvent.PROPERTYGROUP_CHANGED);
		}

		@Override
		public void onFailure(Throwable caught) {
			if (propertyGroupWidgetToFire != null) {
				propertyGroupWidgetToFire.finishedSetProperties();
			}
			Main.get().showError("setProperties", caught);
		}
	};

	/**
	 * Gets asyncronous to remove document group properties
	 */
	final AsyncCallback<Object> callbackRemoveGroup = new AsyncCallback<Object>() {
		@Override
		public void onSuccess(Object result) {
			if (propertyGroupWidgetToFire != null) {
				propertyGroupWidgetToFire.finishedRemoveGroup();
			}
			fireEvent(HasPropertyGroupEvent.PROPERTYGROUP_REMOVED);
		}

		@Override
		public void onFailure(Throwable caught) {
			Main.get().showError("callbackRemoveGroup", caught);
		}
	};

	/**
	 * edit
	 */
	public void edit() {
		manager.edit();
		fireEvent(HasPropertyGroupEvent.PROPERTYGROUP_EDIT);
	}

	/**
	 * updateFormElementsValuesWithNewer
	 *
	 * @return
	 */
	public List<GWTFormElement> updateFormElementsValuesWithNewer() {
		return manager.updateFormElementsValuesWithNewer();
	}

	/**
	 * Sets the properties values
	 */
	public void setProperties() {
		manager.updateFormElementsValuesWithNewer();
		manager.draw(propertyGroup.isReadonly());
		propertyGroupService.setProperties(path, propertyGroup.getName(), manager.updateFormElementsValuesWithNewer(), callbackSetProperties);
	}

	/**
	 * Cancel edition and restores values
	 */
	public void cancelEdit() {
		manager.draw(propertyGroup.isReadonly());
		fireEvent(HasPropertyGroupEvent.PROPERTYGROUP_CANCEL_EDIT);
	}

	/**
	 * Gets all group properties
	 */
	public void getProperties(boolean suggestion) {
		propertyGroupService.getProperties(path, propertyGroup.getName(), suggestion, callbackGetProperties);
	}

	/**
	 * Remove the document property group
	 */
	public void removeGroup() {
		propertyGroupService.removeGroup(path, propertyGroup.getName(), callbackRemoveGroup);
	}

	/**
	 * Gets the group name
	 *
	 * @return The group name
	 */
	public String getGrpName() {
		return propertyGroup.getName();
	}

	/**
	 * getValidationProcessor
	 */
	public ExtendedDefaultValidatorProcessor getValidationProcessor() {
		return manager.getValidationProcessor();
	}

	/**
	 * getManager
	 */
	public FormManager getManager() {
		return manager;
	}

	@Override
	public void fireEvent(PropertyGroupEventConstant event) {
		for (Iterator<PropertyGroupHandlerExtension> it = propertyGroupHandlerExtensionList.iterator(); it.hasNext(); ) {
			it.next().onChange(event);
		}
	}

	@Override
	public void addPropertyGroupHandlerExtension(PropertyGroupHandlerExtension handlerExtension) {
		propertyGroupHandlerExtensionList.add(handlerExtension);
	}
}
