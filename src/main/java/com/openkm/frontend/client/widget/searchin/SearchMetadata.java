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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTPropertyParams;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.widget.form.FormManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * SearchMetadata
 *
 * @author jllort
 *
 */
public class SearchMetadata extends Composite {
	private ScrollPanel scrollPanel;
	private FlexTable table;
	private FormManager formManager;
	public GroupPopup groupPopup;
	public Button addGroup;

	/**
	 * SearchMetadata
	 */
	public SearchMetadata(HasPropertyHandler propertyHandler) {
		formManager = new FormManager(propertyHandler, null);
		table = new FlexTable();
		scrollPanel = new ScrollPanel(table);

		// Table padding and spacing properties
		formManager.getTable().setCellPadding(2);
		formManager.getTable().setCellSpacing(2);

		groupPopup = new GroupPopup();
		groupPopup.setWidth("300px");
		groupPopup.setHeight("125px");
		groupPopup.setStyleName("okm-Popup");
		groupPopup.addStyleName("okm-DisableSelect");

		addGroup = new Button(Main.i18n("search.add.property.group"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				groupPopup.show(UIDockPanelConstants.SEARCH);
			}
		});

		table.setWidget(0, 0, addGroup);
		table.setWidget(1, 0, formManager.getTable());

		addGroup.setStyleName("okm-AddButton");
		addGroup.addStyleName("okm-NoWrap");

		initWidget(scrollPanel);
	}

	/**
	 * getFormElements
	 *
	 * @return
	 */
	public Collection<GWTFormElement> getFormElements() {
		return formManager.getFormElements();
	}

	/**
	 * getPropertyParams
	 *
	 * @return
	 */
	public Map<String, GWTPropertyParams> getUpdatedPropertyParamsWithValues() {
		return formManager.getPropertyParams();
	}

	/**
	 * updateFormElementsValuesWithNewer
	 *
	 * @return
	 */
	public List<GWTFormElement> updateFormElementsValuesWithNewer() {
		return formManager.updateFormElementsValuesWithNewer();
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		addGroup.setHTML(Main.i18n("search.add.property.group"));
		groupPopup.langRefresh();
	}

	/**
	 * reset
	 */
	public void reset() {
		formManager.setFormElements(new ArrayList<GWTFormElement>()); // reset all values
		formManager.draw();
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
	}
}