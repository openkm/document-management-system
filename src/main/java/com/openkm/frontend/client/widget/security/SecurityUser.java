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

package com.openkm.frontend.client.widget.security;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTGrantedUser;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.bean.GWTUser;
import com.openkm.frontend.client.service.OKMAuthService;
import com.openkm.frontend.client.service.OKMAuthServiceAsync;
import com.openkm.frontend.client.util.GWTGrantedUserComparator;
import com.openkm.frontend.client.util.OKMBundleResources;

import java.util.*;

/**
 * Security User
 *
 * @author jllort
 */
public class SecurityUser extends Composite {
	private final OKMAuthServiceAsync authService = (OKMAuthServiceAsync) GWT.create(OKMAuthService.class);

	public UserScrollTable assignedUser;
	public UserScrollTable unassignedUser;
	private HorizontalPanel panel;
	private VerticalPanel buttonPanel;
	private SimplePanel spRight;
	private SimplePanel spHeight;
	private Image addButton;
	private Image removeButton;
	private String uuid = "";
	private int width = 612;
	private Map<String, Integer> actualGrants;
	private Map<String, Integer> changedGrants;
	private boolean evaluateGroup = false;
	private boolean evaluateHistory = false;
	private boolean evaluateWorkflow = false;
	private boolean evaluateDownload = false;

	/**
	 * Security user
	 */
	public SecurityUser() {
		actualGrants = new HashMap<String, Integer>();
		changedGrants = new HashMap<String, Integer>();
		panel = new HorizontalPanel();
		buttonPanel = new VerticalPanel();
		assignedUser = new UserScrollTable(true);
		unassignedUser = new UserScrollTable(false);
		spRight = new SimplePanel();
		spHeight = new SimplePanel();
		spRight.setWidth("1px");
		spHeight.setHeight("30px");

		addButton = new Image(OKMBundleResources.INSTANCE.add());
		removeButton = new Image(OKMBundleResources.INSTANCE.remove());

		buttonPanel.add(addButton);
		buttonPanel.add(spHeight); // separator
		buttonPanel.add(removeButton);

		addButton.addClickHandler(addButtonHandler);
		removeButton.addClickHandler(removeButtonHandler);
		addButton.setStyleName("okm-Hyperlink");
		removeButton.setStyleName("okm-Hyperlink");

		panel.add(unassignedUser);
		panel.add(buttonPanel);
		panel.add(assignedUser);

		panel.setCellWidth(buttonPanel, "20px");
		panel.setCellVerticalAlignment(buttonPanel, HasAlignment.ALIGN_MIDDLE);
		panel.setCellHorizontalAlignment(buttonPanel, HasAlignment.ALIGN_CENTER);

		assignedUser.addStyleName("okm-Border-Left");
		assignedUser.addStyleName("okm-Border-Right");

		unassignedUser.addStyleName("okm-Border-Left");
		unassignedUser.addStyleName("okm-Border-Right");

		panel.setSize(String.valueOf(width) + "px", "365px");

		initWidget(panel);
	}

	/**
	 * initExtendedSecurity
	 *
	 * @param extendedSecurity
	 */
	public void initExtendedSecurity(int extendedSecurity) {
		evaluateGroup = ((extendedSecurity & GWTPermission.PROPERTY_GROUP) == GWTPermission.PROPERTY_GROUP);
		evaluateHistory = ((extendedSecurity & GWTPermission.COMPACT_HISTORY) == GWTPermission.COMPACT_HISTORY);
		evaluateWorkflow = ((extendedSecurity & GWTPermission.START_WORKFLOW) == GWTPermission.START_WORKFLOW);
		evaluateDownload = ((extendedSecurity & GWTPermission.DOWNLOAD) == GWTPermission.DOWNLOAD);

		assignedUser.initExtendedSecurity(extendedSecurity);
		unassignedUser.initExtendedSecurity(extendedSecurity);

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
	ClickHandler addButtonHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (unassignedUser.getUser() != null) {
				addUser(unassignedUser.getUser());
			}
		}
	};

	/**
	 * Remove button listener
	 */
	ClickHandler removeButtonHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (assignedUser.getUser() != null) {
				removeUser(assignedUser.getUser());
			}
		}
	};

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		assignedUser.langRefresh();
		unassignedUser.langRefresh();
	}

	/**
	 * Resets the values
	 */
	public void reset() {
		assignedUser.reset();
		unassignedUser.reset();
		assignedUser.getDataTable().resize(0, assignedUser.getNumberOfColumns());
		unassignedUser.getDataTable().resize(0, unassignedUser.getNumberOfColumns());
	}

	/**
	 * resetUnassigned
	 */
	public void resetUnassigned() {
		unassignedUser.reset();
		unassignedUser.getDataTable().resize(0, unassignedUser.getNumberOfColumns());
	}

	/**
	 * Gets the granted users
	 */
	public void getGrantedUsers() {
		if (uuid != null) {
			actualGrants = new HashMap<String, Integer>();
			changedGrants = new HashMap<String, Integer>();
			authService.getGrantedUsers(uuid, new AsyncCallback<List<GWTGrantedUser>>() {
				@Override
				public void onSuccess(List<GWTGrantedUser> result) {
					Collections.sort(result, GWTGrantedUserComparator.getInstance());

					for (GWTGrantedUser gu : result) {
						actualGrants.put(gu.getUser().getId(), gu.getPermissions());
						assignedUser.addRow(gu.getUser(), gu.getPermissions(), false);
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("GetGrantedUsers", caught);
				}
			});
		}
	}

	/**
	 * Gets the ungranted users
	 */
	public void getUngrantedUsers() {
		if (uuid != null) {
			authService.getUngrantedUsers(uuid, new AsyncCallback<List<GWTGrantedUser>>() {
				@Override
				public void onSuccess(List<GWTGrantedUser> result) {
					for (GWTGrantedUser gu : result) {
						unassignedUser.addRow(gu.getUser(), false);
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("GetUngrantedUsers", caught);
				}
			});
		}
	}

	/**
	 * Gets the ungranted users by filter
	 */
	public void getFilteredUngrantedUsers(String filter) {
		if (uuid != null) {
			resetUnassigned();
			authService.getFilteredUngrantedUsers(uuid, filter, new AsyncCallback<List<GWTGrantedUser>>() {
				@Override
				public void onSuccess(List<GWTGrantedUser> result) {
					for (GWTGrantedUser gu : result) {
						unassignedUser.addRow(gu.getUser(), false);
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("GetUngrantedUsers", caught);
				}
			});
		}
	}

	/**
	 * Grant the user
	 */
	public void addUser(final GWTUser user) {
		if (uuid != null) {
			if (!Main.get().workspaceUserProperties.getWorkspace().isSecurityModeMultiple()) {
				Main.get().securityPopup.status.setFlag_update();
				authService.grantUser(uuid, user.getId(), GWTPermission.READ,
						Main.get().securityPopup.recursive.getValue(), new AsyncCallback<Object>() {
							public void onSuccess(Object result) {
								assignedUser.addRow(user, new Integer(GWTPermission.READ), false);
								unassignedUser.removeSelectedRow();
								Main.get().securityPopup.status.unsetFlag_update();
							}

							public void onFailure(Throwable caught) {
								Main.get().securityPopup.status.unsetFlag_update();
								Main.get().showError("AddUser", caught);
							}
						});
			} else {
				boolean modified = false;

				if (isGrantChanged(user.getId(), new Integer(GWTPermission.READ))) {
					changedGrants.put(user.getId(), GWTPermission.READ);
					modified = true;
				} else {
					changedGrants.remove(user.getId());
				}

				unassignedUser.removeSelectedRow();
				assignedUser.addRow(user, new Integer(GWTPermission.READ), modified);
				Main.get().securityPopup.securityPanel.evaluateChangeButton();
			}
		}
	}

	/**
	 * Revokes all user permissions
	 */
	public void removeUser(final GWTUser user) {
		if (uuid != null) {
			if (!Main.get().workspaceUserProperties.getWorkspace().isSecurityModeMultiple()) {
				Main.get().securityPopup.status.setFlag_update();
				authService.revokeUser(uuid, user.getId(), Main.get().securityPopup.recursive.getValue(),
						new AsyncCallback<Object>() {
							public void onSuccess(Object result) {
								unassignedUser.addRow(user, false);
								unassignedUser.selectLastRow();
								assignedUser.removeSelectedRow();
								Main.get().securityPopup.status.unsetFlag_update();
							}

							public void onFailure(Throwable caught) {
								Main.get().securityPopup.status.unsetFlag_update();
								Main.get().showError("RevokeUser", caught);
							}
						});
			} else {
				boolean modified = false;

				if (isGrantChanged(user.getId(), new Integer(GWTPermission.REMOVED))) {
					changedGrants.put(user.getId(), GWTPermission.REMOVED);
					modified = true;
				} else {
					changedGrants.remove(user.getId());
				}

				unassignedUser.addRow(user, modified);
				unassignedUser.selectLastRow();
				assignedUser.removeSelectedRow();
				Main.get().securityPopup.securityPanel.evaluateChangeButton();
			}
		}
	}

	/**
	 * Grant the user
	 *
	 * @param user The granted user
	 * @param permissions The permissions value
	 */
	public void grant(String user, int permissions, boolean recursive, final int flag_property) {
		if (uuid != null) {
			Log.debug("UserScrollTable.grant(" + user + ", " + permissions + ", " + recursive + ")");

			if (!Main.get().workspaceUserProperties.getWorkspace().isSecurityModeMultiple()) {
				Main.get().securityPopup.status.setFlag_update();
				authService.grantUser(uuid, user, permissions, recursive, new AsyncCallback<Object>() {
					public void onSuccess(Object result) {
						Log.debug("RoleScrollTable.callbackGrantUser.onSuccess(" + result + ")");
						Main.get().securityPopup.status.unsetFlag_update();
					}

					public void onFailure(Throwable caught) {
						Log.debug("RoleScrollTable.callbackGrantUser.onFailure(" + caught + ")");

						int col = 0;
						col++; // Name

						if (flag_property > UserScrollTable.PROPERTY_READ) {
							col++;
						}

						if (flag_property > UserScrollTable.PROPERTY_WRITE) {
							col++;
						}

						if (flag_property > UserScrollTable.PROPERTY_DELETE) {
							col++;
						}

						if (flag_property > UserScrollTable.PROPERTY_SECURITY) {
							col++;
						}

						if (evaluateGroup && flag_property > UserScrollTable.PROPERTY_GROUP) {
							col++;
						}

						if (evaluateHistory && flag_property > UserScrollTable.PROPERTY_HISTORY) {
							col++;
						}

						if (evaluateWorkflow && flag_property > UserScrollTable.PROPERTY_START_WORKFLOW) {
							col++;
						}

						if (evaluateDownload && flag_property > UserScrollTable.PROPERTY_DOWNLOAD) {
							col++;
						}

						int selectedRow = assignedUser.getSelectedRow();
						if (selectedRow >= 0) {
							((CheckBox) assignedUser.getDataTable().getWidget(selectedRow, col)).setValue(false);
						}

						Main.get().securityPopup.status.unsetFlag_update();
						Main.get().showError("GrantUser", caught);
					}
				});
			} else {
				int newGrant = 0;
				if (!changedGrants.containsKey(user) && actualGrants.containsKey(user)) { // Case start new grant with checkbox change
					newGrant = actualGrants.get(user).intValue();
				} else {
					newGrant = changedGrants.get(user).intValue();
				}

				switch (flag_property) {
					case UserScrollTable.PROPERTY_READ:
						newGrant += GWTPermission.READ;
						break;

					case UserScrollTable.PROPERTY_WRITE:
						newGrant += GWTPermission.WRITE;
						break;

					case UserScrollTable.PROPERTY_DELETE:
						newGrant += GWTPermission.DELETE;
						break;

					case UserScrollTable.PROPERTY_SECURITY:
						newGrant += GWTPermission.SECURITY;
						break;

					case UserScrollTable.PROPERTY_GROUP:
						newGrant += GWTPermission.PROPERTY_GROUP;
						break;

					case UserScrollTable.PROPERTY_HISTORY:
						newGrant += GWTPermission.COMPACT_HISTORY;
						break;

					case UserScrollTable.PROPERTY_START_WORKFLOW:
						newGrant += GWTPermission.START_WORKFLOW;
						break;

					case UserScrollTable.PROPERTY_DOWNLOAD:
						newGrant += GWTPermission.DOWNLOAD;
						break;
				}

				if (isGrantChanged(user, newGrant)) {
					changedGrants.put(user, newGrant);
					assignedUser.markModifiedSelectedRow(true);
				} else {
					changedGrants.remove(user);
					assignedUser.markModifiedSelectedRow(false);
				}

				Main.get().securityPopup.securityPanel.evaluateChangeButton();
			}
		}
	}

	/**
	 * Revoke the user grant
	 *
	 * @param user The user
	 * @param permissions The permissions value
	 */
	public void revoke(String user, int permissions, boolean recursive, final int flag_property) {
		if (uuid != null) {
			Log.debug("UserScrollTable.revoke(" + user + ", " + permissions + ", " + recursive + ")");

			if (!Main.get().workspaceUserProperties.getWorkspace().isSecurityModeMultiple()) {
				Main.get().securityPopup.status.setFlag_update();
				authService.revokeUser(uuid, user, permissions, recursive, new AsyncCallback<Object>() {
					public void onSuccess(Object result) {
						Log.debug("RoleScrollTable.callbackRevokeUser.onSuccess(" + result + ")");
						FixedWidthGrid dataTable = assignedUser.getDataTable();

						if (!dataTable.getSelectedRows().isEmpty()) {
							int selectedRow = ((Integer) dataTable.getSelectedRows().iterator().next()).intValue();
							if (!hasSomeCheckBox(selectedRow)) {
								GWTUser userToRemove = new GWTUser();
								userToRemove.setId(dataTable.getHTML(selectedRow, assignedUser.getNumberOfColumns() - 1));
								userToRemove.setUsername(dataTable.getHTML(selectedRow, 0));
								unassignedUser.addRow(userToRemove, false);
								assignedUser.removeSelectedRow();
							}
						}

						Main.get().securityPopup.status.unsetFlag_update();
					}

					public void onFailure(Throwable caught) {
						Log.debug("RoleScrollTable.callbackRevokeUser.onFailure(" + caught + ")");

						int col = 0;
						col++; // Name

						if (flag_property > UserScrollTable.PROPERTY_READ) {
							col++;
						}

						if (flag_property > UserScrollTable.PROPERTY_WRITE) {
							col++;
						}

						if (flag_property > UserScrollTable.PROPERTY_DELETE) {
							col++;
						}

						if (flag_property > UserScrollTable.PROPERTY_SECURITY) {
							col++;
						}

						if (evaluateGroup && flag_property > UserScrollTable.PROPERTY_GROUP) {
							col++;
						}

						if (evaluateHistory && flag_property > UserScrollTable.PROPERTY_HISTORY) {
							col++;
						}

						if (evaluateWorkflow && flag_property > UserScrollTable.PROPERTY_START_WORKFLOW) {
							col++;
						}

						if (evaluateDownload && flag_property > UserScrollTable.PROPERTY_DOWNLOAD) {
							col++;
						}

						int selectedRow = assignedUser.getSelectedRow();
						if (selectedRow >= 0) {
							((CheckBox) assignedUser.getDataTable().getWidget(selectedRow, col)).setValue(true);
						}

						Main.get().securityPopup.status.unsetFlag_update();
						Main.get().showError("RevokeUser", caught);
					}
				});
			} else {
				int newGrant = 0;

				if (changedGrants.containsKey(user)) { // Case start new grant with checkbox change
					newGrant = changedGrants.get(user).intValue();
				} else if (actualGrants.containsKey(user)) {
					newGrant = actualGrants.get(user).intValue();
				}

				switch (flag_property) {
					case UserScrollTable.PROPERTY_READ:
						newGrant -= GWTPermission.READ;
						break;

					case UserScrollTable.PROPERTY_WRITE:
						newGrant -= GWTPermission.WRITE;
						break;

					case UserScrollTable.PROPERTY_DELETE:
						newGrant -= GWTPermission.DELETE;
						break;

					case UserScrollTable.PROPERTY_SECURITY:
						newGrant -= GWTPermission.SECURITY;
						break;

					case UserScrollTable.PROPERTY_GROUP:
						newGrant -= GWTPermission.PROPERTY_GROUP;
						break;

					case UserScrollTable.PROPERTY_HISTORY:
						newGrant -= GWTPermission.COMPACT_HISTORY;
						break;

					case UserScrollTable.PROPERTY_START_WORKFLOW:
						newGrant -= GWTPermission.START_WORKFLOW;
						break;

					case UserScrollTable.PROPERTY_DOWNLOAD:
						newGrant -= GWTPermission.DOWNLOAD;
						break;
				}

				boolean modified = false;

				if (isGrantChanged(user, newGrant)) {
					modified = true;
					changedGrants.put(user, newGrant);
					assignedUser.markModifiedSelectedRow(modified);
				} else {
					changedGrants.remove(user);
					assignedUser.markModifiedSelectedRow(modified);
				}

				FixedWidthGrid dataTable = assignedUser.getDataTable();

				if (!dataTable.getSelectedRows().isEmpty()) {
					int selectedRow = ((Integer) dataTable.getSelectedRows().iterator().next()).intValue();

					if (!hasSomeCheckBox(selectedRow)) {
						GWTUser userToRemove = new GWTUser();
						userToRemove.setId(dataTable.getHTML(selectedRow, assignedUser.getNumberOfColumns() - 1));
						userToRemove.setUsername(dataTable.getHTML(selectedRow, 0));
						unassignedUser.addRow(userToRemove, modified);
						unassignedUser.selectLastRow();
						assignedUser.removeSelectedRow();
					}
				}

				Main.get().securityPopup.securityPanel.evaluateChangeButton();
			}
		}
	}

	/**
	 * isNewGrant
	 */
	private boolean isGrantChanged(String userId, int permission) {
		if (actualGrants.containsKey(userId)) {
			return (permission != actualGrants.get(userId).intValue());
		} else {
			return (permission != GWTPermission.REMOVED); // true if not removing some grant that
		}
	}

	/**
	 * Sets the uuid
	 *
	 * @param uuid The uuid
	 */
	public void setUuid(String uuid) {
		assignedUser.setUuid(uuid);
		this.uuid = uuid;
	}

	/**
	 * fillWidth
	 */
	public void fillWidth() {
		assignedUser.fillWidth();
		unassignedUser.fillWidth();
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

		for (String user : changedGrants.keySet()) {
			if (changedGrants.get(user).intValue() == GWTPermission.REMOVED) {
				// If actualGrants not contains role will be strange case
				if (actualGrants.containsKey(user)) {
					revokeGrants.put(user, actualGrants.get(user)); // Remove all actual grants
				}
			} else {
				if (actualGrants.containsKey(user)) { // test differences
					// Table A=actual grants B=Change grants
					// A B  XOR           
					// 0 0   0      
					// 0 1   1    B & (XOR) -> 1  ( add grant )   
					// 1 0   1    A & (XOR) -> 1  ( revoke grant )
					// 1 1   0       
					int bitDiference = changedGrants.get(user).intValue() ^ actualGrants.get(user).intValue();
					int addBit = changedGrants.get(user).intValue() & bitDiference;
					int revokeBit = actualGrants.get(user).intValue() & bitDiference;

					if (addBit != 0) {
						addGrants.put(user, addBit);
					}

					if (revokeBit != 0) {
						revokeGrants.put(user, revokeBit);
					}
				} else {
					addGrants.put(user, changedGrants.get(user));
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
		FixedWidthGrid dataTable = assignedUser.getDataTable();

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
}