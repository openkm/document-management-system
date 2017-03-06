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

package com.openkm.frontend.client.widget.security;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.service.OKMAuthService;
import com.openkm.frontend.client.service.OKMAuthServiceAsync;
import com.openkm.frontend.client.util.RoleComparator;
import com.openkm.frontend.client.util.Util;

import java.util.*;

/**
 * Security Group
 *
 * @author jllort
 */
public class SecurityRole extends Composite implements HasWidgets {
	private final OKMAuthServiceAsync authService = (OKMAuthServiceAsync) GWT.create(OKMAuthService.class);

	public RoleScrollTable assignedRole;
	public RoleScrollTable unassignedRole;
	private HorizontalPanel panel;
	private VerticalPanel buttonPanel;
	private SimplePanel spRight;
	private SimplePanel spHeight;
	private HTML addButton;
	private HTML removeButton;
	private String uuid = "";
	private int width = 612;
	private Map<String, Integer> actualGrants;
	private Map<String, Integer> changedGrants;
	private boolean evaluateGroup = false;
	private boolean evaluateHistory = false;
	private boolean evaluateWorkflow = false;
	private boolean evaluateDownload = false;

	/**
	 * Security group
	 */
	public SecurityRole() {
		actualGrants = new HashMap<String, Integer>();
		changedGrants = new HashMap<String, Integer>();
		panel = new HorizontalPanel();
		buttonPanel = new VerticalPanel();
		assignedRole = new RoleScrollTable(true);
		unassignedRole = new RoleScrollTable(false);
		spRight = new SimplePanel();
		spHeight = new SimplePanel();
		spRight.setWidth("1px");
		spHeight.setHeight("30px");
		addButton = new HTML(Util.imageHTML("img/icon/security/add.gif"));
		removeButton = new HTML(Util.imageHTML("img/icon/security/remove.gif"));

		buttonPanel.add(addButton);
		buttonPanel.add(spHeight); // separator
		buttonPanel.add(removeButton);

		addButton.addClickHandler(addButtonListener);
		removeButton.addClickHandler(removeButtonListener);
		addButton.setStyleName("okm-Hyperlink");
		removeButton.setStyleName("okm-Hyperlink");

		panel.add(unassignedRole);
		panel.add(buttonPanel);
		panel.add(assignedRole);

		panel.setCellWidth(buttonPanel, "20px");
		panel.setCellVerticalAlignment(buttonPanel, HasAlignment.ALIGN_MIDDLE);
		panel.setCellHorizontalAlignment(buttonPanel, HasAlignment.ALIGN_CENTER);

		assignedRole.addStyleName("okm-Border-Left");
		assignedRole.addStyleName("okm-Border-Bottom");
		assignedRole.addStyleName("okm-Border-Right");

		unassignedRole.addStyleName("okm-Border-Left");
		unassignedRole.addStyleName("okm-Border-Bottom");
		unassignedRole.addStyleName("okm-Border-Right");

		panel.setSize(String.valueOf(width) + "px", "365px");

		initWidget(panel);
	}

	/**
	 * initExtendedSecurity
	 */
	public void initExtendedSecurity(int extendedSecurity) {
		evaluateGroup = ((extendedSecurity & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP);
		evaluateHistory = ((extendedSecurity & GWTPermission.COMPACT_HISTORY) == GWTPermission.COMPACT_HISTORY);
		evaluateWorkflow = ((extendedSecurity & GWTPermission.START_WORKFLOW) == GWTPermission.START_WORKFLOW);
		evaluateDownload = ((extendedSecurity & GWTPermission.DOWNLOAD) == GWTPermission.DOWNLOAD);

		assignedRole.initExtendedSecurity(extendedSecurity);
		unassignedRole.initExtendedSecurity(extendedSecurity);

		if (((extendedSecurity & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP)) {
			width += 55;
		}

		if (((extendedSecurity & GWTPermission.COMPACT_HISTORY) == GWTPermission.COMPACT_HISTORY)) {
			width += 55;
		}

		if (((extendedSecurity & GWTPermission.START_WORKFLOW) == GWTPermission.START_WORKFLOW)) {
			width += 55;
		}

		if (((extendedSecurity & GWTPermission.DOWNLOAD) == GWTPermission.DOWNLOAD)) {
			width += 55;
		}

		panel.setSize(String.valueOf(width) + "px", "365px");
	}

	/**
	 * Add button listener
	 */
	ClickHandler addButtonListener = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (unassignedRole.getRole() != null) {
				addRole(unassignedRole.getRole());
			}
		}
	};

	/**
	 * Remove button listener
	 */
	ClickHandler removeButtonListener = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (assignedRole.getRole() != null) {
				revokeRole(assignedRole.getRole());
			}
		}
	};

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		assignedRole.langRefresh();
		unassignedRole.langRefresh();
	}

	/**
	 * Resets the values
	 */
	public void reset() {
		assignedRole.reset();
		unassignedRole.reset();
		assignedRole.getDataTable().resize(0, assignedRole.getNumberOfColumns());
		unassignedRole.getDataTable().resize(0, unassignedRole.getNumberOfColumns());
	}

	/**
	 * resetUnassigned
	 */
	public void resetUnassigned() {
		unassignedRole.reset();
		unassignedRole.getDataTable().resize(0, unassignedRole.getNumberOfColumns());
	}

	/**
	 * Gets the granted roles
	 */
	public void getGrantedRoles() {
		if (uuid != null) {
			actualGrants = new HashMap<String, Integer>();
			changedGrants = new HashMap<String, Integer>();
			authService.getGrantedRoles(uuid, new AsyncCallback<Map<String, Integer>>() {
				public void onSuccess(Map<String, Integer> result) {
					List<String> rolesList = new ArrayList<String>();

					// Ordering grant roles to list
					for (Iterator<String> it = result.keySet().iterator(); it.hasNext(); ) {
						rolesList.add(it.next());
					}

					Collections.sort(rolesList, RoleComparator.getInstance());

					for (Iterator<String> it = rolesList.iterator(); it.hasNext(); ) {
						String groupName = it.next();
						Integer permission = (Integer) result.get(groupName);
						actualGrants.put(groupName, permission);
						assignedRole.addRow(groupName, permission, false);
					}
				}

				public void onFailure(Throwable caught) {
					Main.get().showError("GetGrantedRoles", caught);
				}
			});
		}
	}

	/**
	 * Gets the granted roles
	 */
	public void getUngrantedRoles() {
		if (uuid != null) {
			authService.getUngrantedRoles(uuid, new AsyncCallback<List<String>>() {
				public void onSuccess(List<String> result) {
					for (String role : result) {
						unassignedRole.addRow(role, false);
					}
				}

				public void onFailure(Throwable caught) {
					Main.get().showError("GetUngrantedRoles", caught);
				}
			});
		}
	}

	/**
	 * Gets the granted roles by filter
	 */
	public void getFilteredUngrantedRoles(String filter) {
		if (uuid != null) {
			resetUnassigned();
			authService.getFilteredUngrantedRoles(uuid, filter, new AsyncCallback<List<String>>() {
				public void onSuccess(List<String> result) {
					for (String role : result) {
						unassignedRole.addRow(role, false);
					}
				}

				public void onFailure(Throwable caught) {
					Main.get().showError("GetUngrantedRoles", caught);
				}
			});
		}
	}

	/**
	 * Grant the role
	 */
	public void addRole(final String role) {
		if (uuid != null) {
			if (!Main.get().workspaceUserProperties.getWorkspace().isSecurityModeMultiple()) {
				Main.get().securityPopup.status.setFlag_update();
				authService.grantRole(uuid, role, GWTPermission.READ, Main.get().securityPopup.recursive.getValue(),
						new AsyncCallback<Object>() {
							public void onSuccess(Object result) {
								assignedRole.addRow(role, new Integer(GWTPermission.READ), false);
								unassignedRole.removeSelectedRow();
								Main.get().securityPopup.status.unsetFlag_update();
							}

							public void onFailure(Throwable caught) {
								Main.get().securityPopup.status.unsetFlag_update();
								Main.get().showError("AddRole", caught);
							}
						});
			} else {
				boolean modified = false;

				if (isGrantChanged(role, new Integer(GWTPermission.READ))) {
					changedGrants.put(role, GWTPermission.READ);
					modified = true;
				} else {
					changedGrants.remove(role);
				}

				unassignedRole.removeSelectedRow();
				assignedRole.addRow(role, new Integer(GWTPermission.READ), modified);
				Main.get().securityPopup.securityPanel.evaluateChangeButton();
			}
		}
	}

	/**
	 * Revokes all role permissions
	 *
	 * @param user The role
	 */
	public void revokeRole(final String role) {
		if (uuid != null) {
			if (!Main.get().workspaceUserProperties.getWorkspace().isSecurityModeMultiple()) {
				Main.get().securityPopup.status.setFlag_update();
				authService.revokeRole(uuid, role, Main.get().securityPopup.recursive.getValue(),
						new AsyncCallback<Object>() {
							public void onSuccess(Object result) {
								unassignedRole.addRow(role, false);
								unassignedRole.selectLastRow();
								assignedRole.removeSelectedRow();
								Main.get().securityPopup.status.unsetFlag_update();
							}

							public void onFailure(Throwable caught) {
								Main.get().securityPopup.status.unsetFlag_update();
								Main.get().showError("RevokeRole", caught);
							}
						});
			} else {
				boolean modified = false;

				if (isGrantChanged(role, new Integer(GWTPermission.REMOVED))) {
					changedGrants.put(role, GWTPermission.REMOVED);
					modified = true;
				} else {
					changedGrants.remove(role);
				}

				unassignedRole.addRow(role, modified);
				unassignedRole.selectLastRow();
				assignedRole.removeSelectedRow();
				Main.get().securityPopup.securityPanel.evaluateChangeButton();
			}
		}
	}

	/**
	 * Grant the role
	 *
	 * @param user The granted role
	 * @param permissions The permissions value
	 */
	public void grant(String role, int permissions, boolean recursive, final int flag_property) {
		if (uuid != null) {
			Log.debug("RoleScrollTable.grant(" + role + ", " + permissions + ", " + recursive + ")");

			if (!Main.get().workspaceUserProperties.getWorkspace().isSecurityModeMultiple()) {
				Main.get().securityPopup.status.setFlag_update();
				authService.grantRole(uuid, role, permissions, recursive, new AsyncCallback<Object>() {
					public void onSuccess(Object result) {
						Log.debug("RoleScrollTable.callbackGrantRole.onSuccess(" + result + ")");
						Main.get().securityPopup.status.unsetFlag_update();
					}

					public void onFailure(Throwable caught) {
						Log.debug("RoleScrollTable.callbackGrantRole.onFailure(" + caught + ")");

						int col = 0;
						col++; // Name

						if (flag_property > RoleScrollTable.PROPERTY_READ) {
							col++;
						}

						if (flag_property > RoleScrollTable.PROPERTY_WRITE) {
							col++;
						}

						if (flag_property > RoleScrollTable.PROPERTY_DELETE) {
							col++;
						}

						if (flag_property > RoleScrollTable.PROPERTY_SECURITY) {
							col++;
						}

						if (evaluateGroup && flag_property > RoleScrollTable.PROPERTY_GROUP) {
							col++;
						}

						if (evaluateHistory && flag_property > RoleScrollTable.PROPERTY_HISTORY) {
							col++;
						}

						if (evaluateWorkflow && flag_property > RoleScrollTable.PROPERTY_START_WORKFLOW) {
							col++;
						}

						if (evaluateDownload && flag_property > RoleScrollTable.PROPERTY_DOWNLOAD) {
							col++;
						}

						int selectedRow = assignedRole.getSelectedRow();
						if (selectedRow >= 0) {
							((CheckBox) assignedRole.getDataTable().getWidget(selectedRow, col)).setValue(false);
						}

						Main.get().securityPopup.status.unsetFlag_update();
						Main.get().showError("GrantRole", caught);
					}
				});
			} else {
				int newGrant = 0;

				if (!changedGrants.containsKey(role) && actualGrants.containsKey(role)) { // Case start new grant with checkbox change
					newGrant = actualGrants.get(role).intValue();
				} else {
					newGrant = changedGrants.get(role).intValue();
				}

				switch (flag_property) {
					case RoleScrollTable.PROPERTY_READ:
						newGrant += GWTPermission.READ;
						break;

					case RoleScrollTable.PROPERTY_WRITE:
						newGrant += GWTPermission.WRITE;
						break;

					case RoleScrollTable.PROPERTY_DELETE:
						newGrant += GWTPermission.DELETE;
						break;

					case RoleScrollTable.PROPERTY_SECURITY:
						newGrant += GWTPermission.SECURITY;
						break;

					case RoleScrollTable.PROPERTY_GROUP:
						newGrant += GWTPermission.PROPERTY_GROUP;
						break;

					case RoleScrollTable.PROPERTY_HISTORY:
						newGrant += GWTPermission.COMPACT_HISTORY;
						break;

					case RoleScrollTable.PROPERTY_START_WORKFLOW:
						newGrant += GWTPermission.START_WORKFLOW;
						break;

					case RoleScrollTable.PROPERTY_DOWNLOAD:
						newGrant += GWTPermission.DOWNLOAD;
						break;
				}

				if (isGrantChanged(role, newGrant)) {
					changedGrants.put(role, newGrant);
					assignedRole.markModifiedSelectedRow(true);
				} else {
					changedGrants.remove(role);
					assignedRole.markModifiedSelectedRow(false);
				}

				Main.get().securityPopup.securityPanel.evaluateChangeButton();
			}
		}
	}

	/**
	 * Revoke the role grant
	 *
	 * @param user The role
	 * @param permissions The permissions value
	 */
	public void revoke(String role, int permissions, boolean recursive, final int flag_property) {
		if (uuid != null) {
			Log.debug("RoleScrollTable.revoke(" + role + ", " + permissions + ", " + recursive + ")");

			if (!Main.get().workspaceUserProperties.getWorkspace().isSecurityModeMultiple()) {
				Main.get().securityPopup.status.setFlag_update();
				authService.revokeRole(uuid, role, permissions, recursive, new AsyncCallback<Object>() {
					public void onSuccess(Object result) {
						Log.debug("RoleScrollTable.callbackRevokeRole.onSuccess(" + result + ")");
						FixedWidthGrid dataTable = assignedRole.getDataTable();

						// If user has no grants must be deleted
						if (!dataTable.getSelectedRows().isEmpty()) {
							int selectedRow = ((Integer) dataTable.getSelectedRows().iterator().next()).intValue();
							if (!hasSomeCheckBox(selectedRow)) {
								unassignedRole.addRow(dataTable.getText(selectedRow, 0), false);
								assignedRole.removeSelectedRow();
							}
						}

						Main.get().securityPopup.status.unsetFlag_update();
					}

					public void onFailure(Throwable caught) {
						Log.debug("RoleScrollTable.callbackRevokeRole.onFailure(" + caught + ")");

						int col = 0;
						col++; // Name

						if (flag_property > RoleScrollTable.PROPERTY_READ) {
							col++;
						}

						if (flag_property > RoleScrollTable.PROPERTY_WRITE) {
							col++;
						}

						if (flag_property > RoleScrollTable.PROPERTY_DELETE) {
							col++;
						}

						if (flag_property > RoleScrollTable.PROPERTY_SECURITY) {
							col++;
						}

						if (evaluateGroup && flag_property > RoleScrollTable.PROPERTY_GROUP) {
							col++;
						}

						if (evaluateHistory && flag_property > RoleScrollTable.PROPERTY_HISTORY) {
							col++;
						}

						if (evaluateWorkflow && flag_property > RoleScrollTable.PROPERTY_START_WORKFLOW) {
							col++;
						}

						if (evaluateDownload && flag_property > RoleScrollTable.PROPERTY_DOWNLOAD) {
							col++;
						}

						int selectedRow = assignedRole.getSelectedRow();
						if (selectedRow >= 0) {
							((CheckBox) assignedRole.getDataTable().getWidget(selectedRow, col)).setValue(true);
						}

						Main.get().securityPopup.status.unsetFlag_update();
						Main.get().showError("RevokeRole", caught);
					}
				});
			} else {
				int newGrant = 0;

				if (changedGrants.containsKey(role)) { // Case start new grant with checkbox change
					newGrant = changedGrants.get(role).intValue();
				} else if (actualGrants.containsKey(role)) {
					newGrant = actualGrants.get(role).intValue();
				}

				switch (flag_property) {
					case RoleScrollTable.PROPERTY_READ:
						newGrant -= GWTPermission.READ;
						break;

					case RoleScrollTable.PROPERTY_WRITE:
						newGrant -= GWTPermission.WRITE;
						break;

					case RoleScrollTable.PROPERTY_DELETE:
						newGrant -= GWTPermission.DELETE;
						break;

					case RoleScrollTable.PROPERTY_SECURITY:
						newGrant -= GWTPermission.SECURITY;
						break;

					case RoleScrollTable.PROPERTY_GROUP:
						newGrant -= GWTPermission.PROPERTY_GROUP;
						break;

					case RoleScrollTable.PROPERTY_HISTORY:
						newGrant -= GWTPermission.COMPACT_HISTORY;
						break;

					case RoleScrollTable.PROPERTY_START_WORKFLOW:
						newGrant -= GWTPermission.START_WORKFLOW;
						break;

					case RoleScrollTable.PROPERTY_DOWNLOAD:
						newGrant -= GWTPermission.DOWNLOAD;
						break;
				}

				boolean modified = false;

				if (isGrantChanged(role, newGrant)) {
					modified = true;
					changedGrants.put(role, newGrant);
					assignedRole.markModifiedSelectedRow(modified);
				} else {
					changedGrants.remove(role);
					assignedRole.markModifiedSelectedRow(modified);
				}

				FixedWidthGrid dataTable = assignedRole.getDataTable();

				if (!dataTable.getSelectedRows().isEmpty()) {
					int selectedRow = ((Integer) dataTable.getSelectedRows().iterator().next()).intValue();

					if (!hasSomeCheckBox(selectedRow)) {
						unassignedRole.addRow(dataTable.getText(selectedRow, 0), modified);
						unassignedRole.selectLastRow();
						assignedRole.removeSelectedRow();
					}
				}

				Main.get().securityPopup.securityPanel.evaluateChangeButton();
			}
		}
	}

	/**
	 * isNewGrant
	 */
	private boolean isGrantChanged(String role, int permission) {
		if (actualGrants.containsKey(role)) {
			return (permission != actualGrants.get(role).intValue());
		} else {
			// true if not removing some grant that
			return (permission != GWTPermission.REMOVED);
		}
	}

	/**
	 * Sets the uuid
	 *
	 * @param uuid The uuid
	 */
	public void setUuid(String uuid) {
		assignedRole.setUuid(uuid);
		this.uuid = uuid;
	}

	/**
	 * fillWidth
	 */
	public void fillWidth() {
		assignedRole.fillWidth();
		unassignedRole.fillWidth();
	}

	/**
	 * getNewGrants
	 */
	public List<Map<String, Integer>> getNewGrants() {
		List<Map<String, Integer>> grants = new ArrayList<Map<String, Integer>>();
		Map<String, Integer> addGrants = new HashMap<String, Integer>();
		Map<String, Integer> revokeGrants = new HashMap<String, Integer>();
		grants.add(addGrants);
		grants.add(revokeGrants);

		for (String role : changedGrants.keySet()) {
			if (changedGrants.get(role).intValue() == GWTPermission.REMOVED) {
				// If actualGrants not contains role will be strange case
				if (actualGrants.containsKey(role)) {
					revokeGrants.put(role, actualGrants.get(role)); // Remove all actual grants
				}
			} else {
				if (actualGrants.containsKey(role)) { // test diferences
					// Table A=actual grants B=Change grants
					// A B  XOR           
					// 0 0   0      
					// 0 1   1    B & (XOR) -> 1  ( add grant )   
					// 1 0   1    A & (XOR) -> 1  ( revoke grant )
					// 1 1   0       
					int bitDiference = changedGrants.get(role).intValue() ^ actualGrants.get(role).intValue();
					int addBit = changedGrants.get(role).intValue() & bitDiference;
					int revokeBit = actualGrants.get(role).intValue() & bitDiference;

					if (addBit != 0) {
						addGrants.put(role, addBit);
					}

					if (revokeBit != 0) {
						revokeGrants.put(role, revokeBit);
					}
				} else {
					addGrants.put(role, changedGrants.get(role));
				}
			}
		}

		return grants;
	}

	/**
	 * hasChangedGrants
	 */
	public boolean hasChangedGrants() {
		return changedGrants.size() > 0;
	}

	/**
	 * hasSomeCheckBox
	 */
	private boolean hasSomeCheckBox(int row) {
		FixedWidthGrid dataTable = assignedRole.getDataTable();
		// If user has no grants must be deleted
		int col = 0;
		col++; // Name

		boolean isChecked = ((CheckBox) dataTable.getWidget(row, col++)).getValue()
				|| ((CheckBox) dataTable.getWidget(row, col++)).getValue()
				|| ((CheckBox) dataTable.getWidget(row, col++)).getValue()
				|| ((CheckBox) dataTable.getWidget(row, col++)).getValue();

		if (evaluateGroup) {
			isChecked = isChecked
					|| ((CheckBox) dataTable.getWidget(row, col++)).getValue();
		}

		if (evaluateHistory) {
			isChecked = isChecked
					|| ((CheckBox) dataTable.getWidget(row, col++)).getValue();
		}

		if (evaluateWorkflow) {
			isChecked = isChecked
					|| ((CheckBox) dataTable.getWidget(row, col++)).getValue();
		}

		if (evaluateDownload) {
			isChecked = isChecked
					|| ((CheckBox) dataTable.getWidget(row, col++)).getValue();
		}

		return isChecked;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.google.gwt.user.client.ui.HasWidgets#add(com.google.gwt.user.client
	 * .ui.Widget)
	 */
	public void add(Widget w) {
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasWidgets#clear()
	 */
	public void clear() {
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasWidgets#iterator()
	 */
	public Iterator<Widget> iterator() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.google.gwt.user.client.ui.HasWidgets#remove(com.google.gwt.user.client
	 * .ui.Widget)
	 */
	public boolean remove(Widget w) {
		return true;
	}
}