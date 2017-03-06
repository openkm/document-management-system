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

package com.openkm.frontend.client.widget.dashboard.workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTTaskInstance;
import com.openkm.frontend.client.constants.ui.UIDashboardConstants;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.service.OKMWorkflowService;
import com.openkm.frontend.client.service.OKMWorkflowServiceAsync;

import java.util.List;

/**
 * WorkflowDashboard
 *
 * @author jllort
 *
 */
public class WorkflowDashboard extends Composite {

	private final OKMWorkflowServiceAsync workflowService = (OKMWorkflowServiceAsync) GWT.create(OKMWorkflowService.class);

	private final int NUMBER_OF_COLUMNS = 2;

	private HorizontalPanel hPanel;
	private VerticalPanel vPanelLeft;
	private VerticalPanel vPanelRight;

	private WorkflowWidget pendingTasks;
	private WorkflowWidget pendingPooledTasks;
	public WorkflowFormPanel workflowFormPanel;

	private boolean showStatus = false;

	/**
	 * UserDashboard
	 */
	public WorkflowDashboard() {
		vPanelLeft = new VerticalPanel();
		vPanelRight = new VerticalPanel();
		hPanel = new HorizontalPanel();

		hPanel.add(vPanelLeft);
		hPanel.add(vPanelRight);

		pendingTasks = new WorkflowWidget("dashboard.workflow.pending.tasks", "img/icon/workflow.gif", true);
		pendingPooledTasks = new WorkflowWidget("dashboard.workflow.pending.tasks.unassigned", "img/icon/workflow.gif", true);
		pendingTasks.setIsWidgetPendingTask();
		pendingPooledTasks.setIsWidgetPooledTask();
		workflowFormPanel = new WorkflowFormPanel();

		vPanelLeft.add(pendingTasks);
		vPanelLeft.add(pendingPooledTasks);
		vPanelRight.add(workflowFormPanel);

		hPanel.setHeight("100%");
		vPanelRight.setHeight("100%");

		initWidget(hPanel);
	}

	/**
	 * Refreshing language
	 */
	public void langRefresh() {
		pendingTasks.langRefresh();
		pendingPooledTasks.langRefresh();
		workflowFormPanel.langRefresh();
	}

	/**
	 * setWidth
	 *
	 * @param width
	 */
	public void setWidth(int width, int height) {
		int columnWidth = width / NUMBER_OF_COLUMNS;

		// Trying to distribute widgets on columns with max size
		pendingTasks.setWidth(columnWidth);
		pendingPooledTasks.setWidth(columnWidth);
		workflowFormPanel.setWidth("" + columnWidth + "px");
		workflowFormPanel.setHeight(String.valueOf(height) + "px");
		hPanel.setHeight(String.valueOf(height) + "px");
		vPanelRight.setHeight(String.valueOf(height) + "px");
	}

	/**
	 * Get subscribed documents callback
	 */
	final AsyncCallback<List<GWTTaskInstance>> callbackFindUserTaskInstancess = new AsyncCallback<List<GWTTaskInstance>>() {
		public void onSuccess(List<GWTTaskInstance> result) {
			pendingTasks.setTasks(result);
			Main.get().mainPanel.bottomPanel.userInfo.setNewsWorkflows(result.size());
			pendingTasks.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("findUserTaskInstances", caught);
			pendingTasks.unsetRefreshing();
		}
	};

	/**
	 * Get subscribed pooled task instances callback
	 */
	final AsyncCallback<List<GWTTaskInstance>> callbackPooledTaskInstances = new AsyncCallback<List<GWTTaskInstance>>() {
		public void onSuccess(List<GWTTaskInstance> result) {
			pendingPooledTasks.setTasks(result);
			Main.get().mainPanel.bottomPanel.userInfo.setPooledTaskInstances(result.size());
			pendingPooledTasks.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("findPooledTaskInstances", caught);
			pendingPooledTasks.unsetRefreshing();
		}
	};

	/**
	 * Get task instance actor id callback
	 */
	final AsyncCallback<Object> callbackSetTaskInstanceActorId = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			pendingPooledTasks.resetPooledTaskInstance();
			pendingPooledTasks.unsetRefreshing();
			refreshAll();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("setTaskInstanceActorId", caught);
			pendingPooledTasks.resetPooledTaskInstance();
			pendingPooledTasks.unsetRefreshing();
		}
	};

	/**
	 * refresh all
	 */
	public void refreshAll() {
		showStatus = ((Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace() == UIDockPanelConstants.DASHBOARD) &&
				(Main.get().mainPanel.dashboard.getActualView() == UIDashboardConstants.DASHBOARD_WORKFLOW));
		findUserTaskInstances();
		findPooledTaskInstances();
	}

	/**
	 * findUserTaskInstances
	 */
	public void findUserTaskInstances() {
		if (showStatus) {
			pendingTasks.setRefreshing();
		}
		workflowService.findUserTaskInstances(callbackFindUserTaskInstancess);
	}

	/**
	 * findPooledTaskInstances
	 */
	private void findPooledTaskInstances() {
		if (showStatus) {
			pendingPooledTasks.setRefreshing();
		}
		workflowService.findPooledTaskInstances(callbackPooledTaskInstances);
	}

	/**
	 * setTaskInstanceActorId
	 */
	public void setTaskInstanceActorId() {
		if (pendingPooledTasks.getPooledTaskInstance() != null) {
			GWTTaskInstance taskInstance = pendingPooledTasks.getPooledTaskInstance();
			workflowService.setTaskInstanceActorId(taskInstance.getId(), callbackSetTaskInstanceActorId);
		}
	}

	/**
	 * setProcessToExecuteNextTask
	 *
	 * @param processToExecuteNextTask
	 */
	public void setProcessToExecuteNextTask(double processToExecuteNextTask) {
		pendingTasks.setProcessToExecuteNextTask(processToExecuteNextTask);
	}
}