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

package com.openkm.frontend.client.widget.notify;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.service.OKMAuthService;
import com.openkm.frontend.client.service.OKMAuthServiceAsync;
import com.openkm.frontend.client.util.OKMBundleResources;

import java.util.Iterator;
import java.util.List;

/**
 * NotifyRole
 *
 * @author jllort
 */
public class NotifyRole extends Composite {
	private final OKMAuthServiceAsync authService = GWT.create(OKMAuthService.class);

	public static final int DEFAULT = 1;
	public static final int FILTER = 2;
	private HorizontalPanel hPanel;
	private RoleScrollTable notifyRolesTable;
	private RoleScrollTable rolesTable;
	private VerticalPanel buttonPanel;
	private Image addButton;
	private Image removeButton;
	private NotifyHandler notifyChange;
	private List<String> selectedRoles;

	/**
	 * NotifyRole
	 */
	public NotifyRole(NotifyHandler notifyChange) {
		hPanel = new HorizontalPanel();
		notifyRolesTable = new RoleScrollTable(true);
		notifyRolesTable.addDoubleClickHandler(removeTableHandler);
		rolesTable = new RoleScrollTable(false);
		rolesTable.addDoubleClickHandler(addTableHandler);

		buttonPanel = new VerticalPanel();
		addButton = new Image(OKMBundleResources.INSTANCE.add());
		removeButton = new Image(OKMBundleResources.INSTANCE.remove());

		HTML space = new HTML("");
		buttonPanel.add(addButton);
		buttonPanel.add(space); // separator
		buttonPanel.add(removeButton);

		buttonPanel.setCellHeight(space, "40px");

		addButton.addClickHandler(addButtonHandler);
		removeButton.addClickHandler(removeButtonHandler);
		addButton.setStyleName("okm-Hyperlink");
		removeButton.setStyleName("okm-Hyperlink");

		hPanel.setSize("374px", "140px");
		hPanel.add(rolesTable);
		hPanel.add(buttonPanel);
		hPanel.add(notifyRolesTable);
		hPanel.setCellVerticalAlignment(buttonPanel, VerticalPanel.ALIGN_MIDDLE);
		hPanel.setCellHorizontalAlignment(buttonPanel, HorizontalPanel.ALIGN_CENTER);
		hPanel.setCellWidth(buttonPanel, "20px");

		notifyRolesTable.addStyleName("okm-Border-Left");
		notifyRolesTable.addStyleName("okm-Border-Right");
		notifyRolesTable.addStyleName("okm-Border-Bottom");
		rolesTable.addStyleName("okm-Border-Left");
		rolesTable.addStyleName("okm-Border-Right");
		rolesTable.addStyleName("okm-Border-Bottom");

		reset();

		initWidget(hPanel);
	}

	/**
	 * correcIEBug
	 */
	public void correcIEBug() {
		// TODO:Solves minor bug with IE ( UI defect extra size needed )
		hPanel.setCellWidth(buttonPanel, "25px");
	}

	/**
	 * reset
	 */
	public void reset() {
		notifyRolesTable.reset();
		rolesTable.reset();
	}

	/**
	 * resetAvailableRoles
	 */
	public void resetAvailableRolesTable() {
		rolesTable.reset();
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		notifyRolesTable.langRefresh();
		rolesTable.langRefresh();
	}

	/**
	 * Add button handler
	 */
	ClickHandler addButtonHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			addRole();
		}
	};

	/**
	 * Add Table handler
	 */
	DoubleClickHandler addTableHandler = new DoubleClickHandler() {
		@Override
		public void onDoubleClick(DoubleClickEvent event) {
			addRole();
		}
	};
	/**
	 * Remove button handler
	 */
	ClickHandler removeButtonHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			removeRole();
		}
	};

	DoubleClickHandler removeTableHandler = new DoubleClickHandler() {
		@Override
		public void onDoubleClick(DoubleClickEvent event) {
			removeRole();
		}
	};

	/**
	 * addRole
	 */
	private void addRole() {
		if (rolesTable.getRole() != null) {
			notifyRolesTable.addRow(rolesTable.getRole());
			if (Main.get().mailEditorPopup.recipientsPopup.notifyPanel.roles != null) {
				Main.get().mailEditorPopup.recipientsPopup.notifyPanel.roles.add(rolesTable.getRole());
			}
			notifyRolesTable.selectLastRow();
			rolesTable.removeSelectedRow();
			Main.get().fileUpload.disableErrorNotify(); // Used in both widgets
			Main.get().notifyPopup.disableErrorNotify(); // has no bad efeccts disabling
			notifyChange.onChange();
		}
	}

	/**
	 * removeRole
	 */
	private void removeRole() {
		if (notifyRolesTable.getRole() != null) {
			rolesTable.addRow(notifyRolesTable.getRole());
			if (Main.get().mailEditorPopup.recipientsPopup.notifyPanel.roles != null) {
				Main.get().mailEditorPopup.recipientsPopup.notifyPanel.roles.remove(notifyRolesTable.getRole());
			}
			rolesTable.selectLastRow();
			notifyRolesTable.removeSelectedRow();
			notifyChange.onChange();
		}
	}

	/**
	 * Call back get all roles
	 */
	final AsyncCallback<List<String>> callbackAllRoles = new AsyncCallback<List<String>>() {
		public void onSuccess(List<String> result) {
			for (String role : result) {
				if (selectedRoles != null) {
					if (!selectedRoles.contains(role)) {
						rolesTable.addRow(role);
					} else {
						notifyRolesTable.addRow(role);
					}
				} else {
					rolesTable.addRow(role);
				}
			}
			if (selectedRoles != null) {
				selectedRoles = null;
				notifyChange.onChange();
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetAllRoles", caught);
		}
	};

	/**
	 * Call back get Filter Roles
	 */
	final AsyncCallback<List<String>> callbackFilterRoles = new AsyncCallback<List<String>>() {
		public void onSuccess(List<String> result) {
			for (Iterator<String> it = result.iterator(); it.hasNext(); ) {
				String role = it.next();
				if (selectedRoles != null) {
					if (!selectedRoles.contains(role)) {
						rolesTable.addRow(role);
					}
				} else {
					rolesTable.addRow(role);
				}
			}
			if (selectedRoles != null) {
				selectedRoles = null;
				notifyChange.onChange();
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetFilterRoles", caught);
		}
	};

	/**
	 * Gets all roles
	 */
	public void getAllRoles(List<String> selectedRoles, int type) {
		this.selectedRoles = selectedRoles;
		switch (type) {
			case DEFAULT:
				authService.getAllRoles(callbackAllRoles);
				break;

			case FILTER:
				if (selectedRoles != null) {
					for (String role : selectedRoles) {
						notifyRolesTable.addRow(role);
					}
					selectedRoles = null;
					notifyChange.onChange();
				}
				break;
		}
	}

	/**
	 * Gets all roles
	 */
	public void getFilteredAllRoles(String filter) {
		authService.getFilteredAllRoles(filter, notifyRolesTable.getRolesToNotifyList(), callbackAllRoles);
	}

	/**
	 * getRolesToNotify
	 */
	public String getRolesToNotify() {
		return notifyRolesTable.getRolesToNotify();
	}
}
