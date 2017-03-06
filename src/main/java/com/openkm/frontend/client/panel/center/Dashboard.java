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

package com.openkm.frontend.client.panel.center;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.constants.ui.UIDashboardConstants;
import com.openkm.frontend.client.extension.event.HasDashboardEvent;
import com.openkm.frontend.client.extension.event.handler.DashboardHandlerExtension;
import com.openkm.frontend.client.extension.event.hashandler.HasDashboardHandlerExtension;
import com.openkm.frontend.client.extension.widget.toolbar.ToolBarBoxExtension;
import com.openkm.frontend.client.widget.dashboard.*;
import com.openkm.frontend.client.widget.dashboard.keymap.KeyMapDashboard;
import com.openkm.frontend.client.widget.dashboard.workflow.WorkflowDashboard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Dashboard
 *
 * @author jllort
 *
 */
public class Dashboard extends Composite implements HasDashboardHandlerExtension, HasDashboardEvent {

	private boolean userVisible = false;
	private boolean mailVisible = false;
	private boolean newsVisible = false;
	private boolean generalVisible = false;
	private boolean workflowVisible = false;
	private boolean keywordsVisible = false;

	private VerticalPanel panel;
	private SimplePanel sp;
	private ScrollPanel scrollPanel;
	public HorizontalToolBar horizontalToolBar;
	public UserDashboard userDashboard;
	public MailDashboard mailDashboard;
	public NewsDashboard newsDashboard;
	public GeneralDashboard generalDashboard;
	public WorkflowDashboard workflowDashboard;
	public KeyMapDashboard keyMapDashboard;
	private Widget actualWidgetExtension;
	private int actualView = 0;
	private Timer dashboardRefreshing;
	private List<ToolBarBoxExtension> toolBarBoxExtensionList;
	private List<DashboardHandlerExtension> dashboardHandlerExtensionList;
	private int width = 0;
	private int height = 0;

	/**
	 * Dashboard
	 */
	public Dashboard() {
		toolBarBoxExtensionList = new ArrayList<ToolBarBoxExtension>();
		dashboardHandlerExtensionList = new ArrayList<DashboardHandlerExtension>();
		panel = new VerticalPanel();
		sp = new SimplePanel();
		userDashboard = new UserDashboard();
		mailDashboard = new MailDashboard();
		scrollPanel = new ScrollPanel();
		horizontalToolBar = new HorizontalToolBar();
		newsDashboard = new NewsDashboard();
		generalDashboard = new GeneralDashboard();
		workflowDashboard = new WorkflowDashboard();
		keyMapDashboard = new KeyMapDashboard();

		actualView = UIDashboardConstants.DASHBOARD_NONE;

		sp.add(scrollPanel);

		panel.add(horizontalToolBar);
		panel.add(sp);

		sp.setStyleName("okm-Input");

		initWidget(panel);
	}

	/**
	 * Sets the size on initialization
	 *
	 * @param width The max width of the widget
	 * @param height The max height of the widget
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		panel.setPixelSize(width - 2, height - 2);
		panel.setCellHeight(sp, "" + (height - 60 - 2) + "px");
		panel.setCellHeight(horizontalToolBar, "" + 60 + "px");
		sp.setPixelSize(width - 2, height - 60 - 2);
		scrollPanel.setPixelSize(width - 2, height - (60 + 2));
		userDashboard.setWidth(width - 2);
		mailDashboard.setWidth(width - 2);
		newsDashboard.setWidth(width - 2);
		generalDashboard.setWidth(width - 2);
		workflowDashboard.setWidth(width - 2, (height - (60 + 2)));
		keyMapDashboard.setSize("" + (width - 2), "" + (height - (60 + 2)));
		horizontalToolBar.setHeight("60px");
		horizontalToolBar.setWidth("100%");

		for (Iterator<ToolBarBoxExtension> it = toolBarBoxExtensionList.iterator(); it.hasNext(); ) {
			it.next().getWidget().setPixelSize(width - 2, height - (60 + 2));
		}

		newsDashboard.getUserSearchs(true); // Here must get all searchs to set correct width size
	}

	/**
	 * Refreshing language
	 */
	public void langRefresh() {
		userDashboard.langRefresh();
		mailDashboard.langRefresh();
		horizontalToolBar.langRefresh();
		generalDashboard.langRefresh();
		newsDashboard.langRefresh();
		workflowDashboard.langRefresh();
		keyMapDashboard.langRefresh();
	}

	/**
	 * changeView
	 *
	 * @param view
	 */
	public void changeView(int view) {

		switch (actualView) {
			case UIDashboardConstants.DASHBOARD_USER:
				scrollPanel.remove(userDashboard);
				break;

			case UIDashboardConstants.DASHBOARD_MAIL:
				scrollPanel.remove(mailDashboard);
				break;

			case UIDashboardConstants.DASHBOARD_NEWS:
				scrollPanel.remove(newsDashboard);
				break;

			case UIDashboardConstants.DASHBOARD_GENERAL:
				scrollPanel.remove(generalDashboard);
				break;

			case UIDashboardConstants.DASHBOARD_WORKFLOW:
				scrollPanel.remove(workflowDashboard);
				break;

			case UIDashboardConstants.DASHBOARD_KEYMAP:
				scrollPanel.remove(keyMapDashboard);
				break;

			case UIDashboardConstants.DASHBOARD_EXTENSION:
				scrollPanel.remove(actualWidgetExtension);
				break;
		}

		switch (view) {
			case UIDashboardConstants.DASHBOARD_USER:
				scrollPanel.add(userDashboard);
				break;

			case UIDashboardConstants.DASHBOARD_MAIL:
				scrollPanel.add(mailDashboard);
				break;

			case UIDashboardConstants.DASHBOARD_NEWS:
				scrollPanel.add(newsDashboard);
				break;

			case UIDashboardConstants.DASHBOARD_GENERAL:
				scrollPanel.add(generalDashboard);
				break;

			case UIDashboardConstants.DASHBOARD_WORKFLOW:
				scrollPanel.add(workflowDashboard);
				break;

			case UIDashboardConstants.DASHBOARD_KEYMAP:
				scrollPanel.add(keyMapDashboard);
				break;

			case UIDashboardConstants.DASHBOARD_EXTENSION:
				actualWidgetExtension = toolBarBoxExtensionList.get(horizontalToolBar.getSelectedExtension()).getWidget();
				scrollPanel.add(actualWidgetExtension);
				break;
		}

		actualView = view;
		fireEvent(HasDashboardEvent.TOOLBOX_CHANGED);
	}

	/**
	 * getActualView
	 */
	public int getActualView() {
		return actualView;
	}


	/**
	 * @param widget
	 * @return
	 */
	public boolean isWidgetExtensionVisible(Widget widget) {
		if (actualView == UIDashboardConstants.DASHBOARD_EXTENSION && actualWidgetExtension.equals(widget)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Refresh all
	 */
	public void refreshAll() {
		if (userVisible) {
			userDashboard.refreshAll();
		}
		if (mailVisible) {
			mailDashboard.refreshAll();
		}
		if (newsVisible) {
			newsDashboard.refreshAll();
		}
		if (generalVisible) {
			generalDashboard.refreshAll();
		}
		if (workflowVisible) {
			workflowDashboard.refreshAll();
		}
		keyMapDashboard.refreshAll();
		fireEvent(HasDashboardEvent.DASHBOARD_REFRESH);
	}

	/**
	 * startRefreshingDashboard
	 *
	 * @param scheduleTime
	 */
	public void startRefreshingDashboard(double scheduleTime) {
		dashboardRefreshing = new Timer() {
			public void run() {
				refreshAll();
			}
		};

		dashboardRefreshing.scheduleRepeating(new Double(scheduleTime).intValue());
	}

	/**
	 * showUser
	 */
	public void showUser() {
		userVisible = true;
		horizontalToolBar.showUser();
	}

	/**
	 * showMail
	 */
	public void showMail() {
		mailVisible = true;
		horizontalToolBar.showMail();
	}

	/**
	 * showNews
	 */
	public void showNews() {
		newsVisible = true;
		horizontalToolBar.showNews();
	}

	/**
	 * showGeneral
	 */
	public void showGeneral() {
		generalVisible = true;
		horizontalToolBar.showGeneral();
	}

	/**
	 * showWorkflow
	 */
	public void showWorkflow() {
		workflowVisible = true;
		horizontalToolBar.showWorkflow();
	}

	/**
	 * showKeywords
	 */
	public void showKeywords() {
		keywordsVisible = true;
		horizontalToolBar.showKeywords();
	}

	/**
	 * init
	 */
	public void init() {
		if (userVisible) {
			changeView(UIDashboardConstants.DASHBOARD_USER);
		} else if (mailVisible) {
			changeView(UIDashboardConstants.DASHBOARD_MAIL);
		} else if (newsVisible) {
			changeView(UIDashboardConstants.DASHBOARD_NEWS);
		} else if (generalVisible) {
			changeView(UIDashboardConstants.DASHBOARD_GENERAL);
		} else if (workflowVisible) {
			changeView(UIDashboardConstants.DASHBOARD_WORKFLOW);
		} else if (keywordsVisible) {
			changeView(UIDashboardConstants.DASHBOARD_KEYMAP);
		} else if (!toolBarBoxExtensionList.isEmpty()) {
			changeView(UIDashboardConstants.DASHBOARD_EXTENSION);
		}
		horizontalToolBar.init();
	}

	/**
	 * addToolBarBoxExtension
	 *
	 * @param extension
	 */
	public void addToolBarBoxExtension(ToolBarBoxExtension extension) {
		toolBarBoxExtensionList.add(extension);
		horizontalToolBar.addToolBarBoxExtension(extension);
		extension.getWidget().setPixelSize(width - 2, height - (60 + 2));
	}

	@Override
	public void addDashboardHandlerExtension(DashboardHandlerExtension handlerExtension) {
		dashboardHandlerExtensionList.add(handlerExtension);
	}

	@Override
	public void fireEvent(DashboardEventConstant event) {
		for (DashboardHandlerExtension handlerExtension : dashboardHandlerExtensionList) {
			handlerExtension.onChange(event);
		}
	}
}