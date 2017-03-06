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

package com.openkm.extension.frontend.client.widget.workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTProcessInstance;
import com.openkm.frontend.client.bean.GWTProcessInstanceLogEntry;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.service.OKMWorkflowService;
import com.openkm.frontend.client.service.OKMWorkflowServiceAsync;

import java.util.List;

/**
 * WorkflowManager
 *
 * @author jllort
 *
 */
public class WorkflowManager extends Composite implements WorkflowController {
	private final OKMWorkflowServiceAsync workflowService = (OKMWorkflowServiceAsync) GWT.create(OKMWorkflowService.class);
	private VerticalPanel vPanel;
	private TabToolbarWorkflow toolbar;
	public WorkflowTable workflowTable;
	public WorkflowDetailTable workflowDetailTable;
	private Image workflowGraph;
	public ScrollPanel scrollGraphPanel;

	// Toolbar height
	public static final int TOOLBAR_HEADER = 25;

	/**
	 * WorkflowManager
	 */
	public WorkflowManager() {
		workflowGraph = new Image();
		scrollGraphPanel = new ScrollPanel(workflowGraph);
		toolbar = new TabToolbarWorkflow(this);
		workflowTable = new WorkflowTable(this);
		workflowDetailTable = new WorkflowDetailTable();
		vPanel = new VerticalPanel();
		vPanel.add(toolbar); // Always visible

		toolbar.setHeight("" + TOOLBAR_HEADER + "px");
		toolbar.setWidth("100%");
		vPanel.setCellHeight(toolbar, "" + TOOLBAR_HEADER + "px");

		initWidget(vPanel);
	}

	/**
	 * findProcessInstancesByNode
	 *
	 * @param uuid
	 */
	public void findProcessInstancesByNode(String uuid) {
		workflowTable.removeAllRows();
		toolbar.switchViewMode(TabToolbarWorkflow.MODE_WORKFLOW_LIST);
		vPanel.add(workflowTable);
		vPanel.remove(scrollGraphPanel);
		vPanel.remove(workflowDetailTable);
		workflowService.findProcessInstancesByNode(uuid, new AsyncCallback<List<GWTProcessInstance>>() {
			@Override
			public void onSuccess(List<GWTProcessInstance> result) {
				workflowTable.reset();
				workflowTable.getDataTable().resize(0, WorkflowTable.NUMBER_OF_COLUMNS);
				for (final GWTProcessInstance processInstance : result) {
					workflowTable.addRow(processInstance);
				}
				workflowTable.fillWidth();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("findProcessInstancesByNode", caught);
			}
		});
	}

	@Override
	public void findLogsByProcessInstance(int processInstanceId) {
		workflowDetailTable.removeAllRows();
		vPanel.remove(workflowTable);
		vPanel.remove(scrollGraphPanel);
		vPanel.add(workflowDetailTable);
		toolbar.switchViewMode(TabToolbarWorkflow.MODE_WORKFLOW_DETAIL);
		workflowService.findLogsByProcessInstance(processInstanceId, new AsyncCallback<List<GWTProcessInstanceLogEntry>>() {
			@Override
			public void onSuccess(List<GWTProcessInstanceLogEntry> result) {
				workflowDetailTable.reset();
				workflowDetailTable.getDataTable().resize(0, WorkflowDetailTable.NUMBER_OF_COLUMNS);
				for (GWTProcessInstanceLogEntry instanceLogEntry : result) {
					workflowDetailTable.addRow(instanceLogEntry);
				}
				workflowDetailTable.fillWidth();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("findLogsByProcessInstance", caught);
			}
		});
	}

	@Override
	public void showGraph(int processId, String taskNode) {
		workflowGraph.setUrl(Main.CONTEXT + "/extension/WorkflowGraph?id=" + processId + "&node=" + taskNode);
		vPanel.remove(workflowTable);
		vPanel.remove(workflowDetailTable);
		vPanel.add(scrollGraphPanel);
		toolbar.switchViewMode(TabToolbarWorkflow.MODE_WORKFLOW_GRAPH);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		toolbar.langRefresh();
		workflowTable.langRefresh();
		workflowDetailTable.langRefresh();
	}

	@Override
	public void goHome() {
		vPanel.add(workflowTable);
		vPanel.remove(workflowDetailTable);
		vPanel.remove(scrollGraphPanel);
		toolbar.switchViewMode(TabToolbarWorkflow.MODE_WORKFLOW_LIST);
	}

	/**
	 * fillWidth
	 */
	public void fillWidth() {
		switch (toolbar.getMode()) {
			case TabToolbarWorkflow.MODE_WORKFLOW_LIST:
				workflowTable.fillWidth();
				break;

			case TabToolbarWorkflow.MODE_WORKFLOW_DETAIL:
				workflowDetailTable.fillWidth();
				break;

			case TabToolbarWorkflow.MODE_WORKFLOW_GRAPH:
				break;
		}
	}
}