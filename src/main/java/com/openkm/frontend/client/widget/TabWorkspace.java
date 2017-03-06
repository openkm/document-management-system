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

package com.openkm.frontend.client.widget;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.Widget;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.extension.event.HasWorkspaceEvent;
import com.openkm.frontend.client.extension.event.handler.WorkspaceHandlerExtension;
import com.openkm.frontend.client.extension.event.hashandler.HasWorkspaceHandlerExtension;
import com.openkm.frontend.client.extension.widget.tabworkspace.TabWorkspaceExtension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Tab Workspace
 *
 * @author jllort
 *
 */
public class TabWorkspace extends Composite implements HasWorkspaceEvent, HasWorkspaceHandlerExtension {
	private static final int NUMBER_OF_TABS = 4;
	private boolean desktopVisible = false;
	private boolean searchVisible = false;
	private boolean dashboardVisible = false;
	private boolean adminitrationVisible = false;
	private List<TabWorkspaceExtension> widgetExtensionList;
	private List<WorkspaceHandlerExtension> workHandlerExtensionList;
	public boolean[] tabVisited = new boolean[NUMBER_OF_TABS];
	public TabBar tabBar;
	private int tabDesktopPos = 0;
	private int tabSearchPos = 0;
	private int tabDashboardPos = 0;
	private int tabAdministrationPos = 0;

	/**
	 * Tab Workspace
	 */
	public TabWorkspace() {
		widgetExtensionList = new ArrayList<TabWorkspaceExtension>();
		workHandlerExtensionList = new ArrayList<WorkspaceHandlerExtension>();
		tabBar = new TabBar();
		tabBar.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				int index = indexCorrectedChangeViewIndex(event.getSelectedItem().intValue());

				switch (index) {
					case UIDockPanelConstants.DESKTOP:
						Main.get().mainPanel.setView(UIDockPanelConstants.DESKTOP);
						if (Main.get().activeFolderTree != null) { // On start up is null
							Main.get().activeFolderTree.centerActulItemOnScroll(); // Center the actual item every time
						}
						break;

					case UIDockPanelConstants.SEARCH:
						Main.get().mainPanel.setView(UIDockPanelConstants.SEARCH);
						break;

					case UIDockPanelConstants.DASHBOARD:
						Main.get().mainPanel.setView(UIDockPanelConstants.DASHBOARD);
						break;

					case UIDockPanelConstants.ADMINISTRATION:
						Main.get().mainPanel.setView(UIDockPanelConstants.ADMINISTRATION);
						break;

					default:
						Main.get().mainPanel.setView(index);
						break;
				}

				fireEvent(HasWorkspaceEvent.STACK_CHANGED);
				tabVisited[index] = true;
			}
		});

		initWidget(tabBar);
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		int selected = tabBar.getSelectedTab();

		while (tabBar.getTabCount() > 0) {
			tabBar.selectTab(0);
			tabBar.removeTab(0);
		}

		if (desktopVisible) {
			tabBar.addTab(Main.i18n("tab.workspace.desktop"));
		}

		if (searchVisible) {
			tabBar.addTab(Main.i18n("tab.workspace.search"));
		}

		if (dashboardVisible) {
			tabBar.addTab(Main.i18n("tab.workspace.dashboard"));
		}

		if (adminitrationVisible) {
			tabBar.addTab(Main.i18n("tab.workspace.administration"));
		}

		for (Iterator<TabWorkspaceExtension> it = widgetExtensionList.iterator(); it.hasNext(); ) {
			tabBar.addTab(it.next().getTabText());
		}

		tabBar.selectTab(selected);
	}

	/**
	 * Gets the selected workspace 
	 *
	 * @return The selected workspace
	 */
	public int getSelectedWorkspace() {
		return indexCorrectedChangeViewIndex(tabBar.getSelectedTab());
	}

	/**
	 * Changes the selected tab index selected
	 *
	 * @param tabIndex The tab index value
	 */
	public void changeSelectedTab(int tabIndex) {
		switch (tabIndex) {
			case UIDockPanelConstants.DESKTOP:
				tabBar.selectTab(tabDesktopPos);
				Main.get().mainPanel.setView(UIDockPanelConstants.DESKTOP);
				break;

			case UIDockPanelConstants.SEARCH:
				tabBar.selectTab(tabSearchPos);
				Main.get().mainPanel.setView(UIDockPanelConstants.SEARCH);
				break;

			case UIDockPanelConstants.DASHBOARD:
				tabBar.selectTab(tabDashboardPos);
				Main.get().mainPanel.setView(UIDockPanelConstants.DASHBOARD);
				break;

			case UIDockPanelConstants.ADMINISTRATION:
				tabBar.selectTab(tabAdministrationPos);
				Main.get().mainPanel.setView(UIDockPanelConstants.ADMINISTRATION);
				break;
		}

		tabVisited[tabIndex] = true;
	}

	/**
	 * indexCorrectedChangeViewIndex
	 *
	 * @return index correction made depending visible panels
	 */
	public int indexCorrectedChangeViewIndex(int index) {
		int corrected = index;

		if (!desktopVisible && corrected >= UIDockPanelConstants.DESKTOP) {
			corrected++;
		}

		if (!searchVisible && corrected >= UIDockPanelConstants.SEARCH) {
			corrected++;
		}

		if (!dashboardVisible && corrected >= UIDockPanelConstants.DASHBOARD) {
			corrected++;
		}

		if (!adminitrationVisible && corrected >= UIDockPanelConstants.ADMINISTRATION) {
			corrected++;
		}

		return corrected;
	}

	/**
	 * showDesktop
	 */
	public void showDesktop() {
		tabBar.addTab(Main.i18n("tab.workspace.desktop"));
		tabDesktopPos = tabBar.getTabCount() - 1;
		desktopVisible = true;

	}

	/**
	 * showSearh
	 */
	public void showSearh() {
		tabBar.addTab(Main.i18n("tab.workspace.search"));
		tabSearchPos = tabBar.getTabCount() - 1;
		searchVisible = true;
	}

	/**
	 * showDashboard
	 */
	public void showDashboard() {
		tabBar.addTab(Main.i18n("tab.workspace.dashboard"));
		tabDashboardPos = tabBar.getTabCount() - 1;
		dashboardVisible = true;
	}

	/**
	 * showAdministration
	 */
	public void showAdministration() {
		tabBar.addTab(Main.i18n("tab.workspace.administration"));
		tabAdministrationPos = tabBar.getTabCount() - 1;
		adminitrationVisible = true;
	}

	/**
	 * showExtensionTabs
	 */
	public boolean showExtensionTabs() {
		for (TabWorkspaceExtension tabExtension : widgetExtensionList) {
			tabBar.addTab(tabExtension.getTabText());
			tabExtension.setTab(tabBar, tabBar.getTabCount() - 1);
		}
		return !widgetExtensionList.isEmpty();
	}

	/**
	 * getTabExtensionIndex
	 */
	public int getTabExtensionIndex(TabWorkspaceExtension widget) {
		int count = 0;
		for (TabWorkspaceExtension tabExtension : widgetExtensionList) {
			if (tabExtension.equals(widget)) {
				return (NUMBER_OF_TABS + count);
			}
			count++;
		}
		return -1;
	}

	/**
	 * isDesktopVisible
	 */
	public boolean isDesktopVisible() {
		return desktopVisible;
	}

	/**
	 * init
	 */
	public void init() {
		if (tabBar.getTabCount() > 0) {
			tabBar.selectTab(0);
		}
	}

	/**
	 * getSelectedTab
	 */
	public int getSelectedTab() {
		return tabBar.getSelectedTab();
	}

	/**
	 * addWorkspaceExtension
	 */
	public void addWorkspaceExtension(TabWorkspaceExtension extension) {
		widgetExtensionList.add(extension);
		extension.setPixelSize(Main.get().mainPanel.getCenterWidth(), Main.get().mainPanel.getCenterHeight());
	}

	/**
	 * getWidgetExtensionByIndex
	 */
	public Widget getWidgetExtensionByIndex(int index) {
		return (Widget) widgetExtensionList.get(index - NUMBER_OF_TABS);
	}

	@Override
	public void addWorkspaceHandlerExtension(WorkspaceHandlerExtension handlerExtension) {
		workHandlerExtensionList.add(handlerExtension);
	}

	@Override
	public void fireEvent(WorkspaceEventConstant event) {
		for (Iterator<WorkspaceHandlerExtension> it = workHandlerExtensionList.iterator(); it.hasNext(); ) {
			it.next().onChange(event);
		}
	}

	/**
	 * Start trick again.
	 */
	public void resetTabVisited() {
		for (int i = 0; i < NUMBER_OF_TABS; i++) {
			tabVisited[i] = false;
		}
	}

	/**
	 * Check if current tab was already visited.
	 */
	public boolean isVisited() {
		return tabVisited[tabBar.getSelectedTab()];
	}
}