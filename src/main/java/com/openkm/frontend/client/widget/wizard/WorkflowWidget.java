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

package com.openkm.frontend.client.widget.wizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.service.OKMWorkflowService;
import com.openkm.frontend.client.service.OKMWorkflowServiceAsync;
import com.openkm.frontend.client.util.validator.ValidatorToFire;
import com.openkm.frontend.client.widget.form.FormManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * WorkflowWidget
 *
 * @author jllort
 *
 */
public class WorkflowWidget extends Composite implements ValidatorToFire {
	private final OKMWorkflowServiceAsync workflowService = GWT.create(OKMWorkflowService.class);
	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private boolean drawed = false;
	private String name;
	private String uuid;
	private FormManager manager;
	private WorkflowWidgetToFire workflowWidgetToFire;
	private Map<String, Object> workflowVariables;

	/**
	 * WorkflowWidget
	 */
	public WorkflowWidget(String name, String uuid, WorkflowWidgetToFire workflowWidgetToFire, Map<String, Object> workflowVariables) {
		this.name = name;
		this.uuid = uuid;
		this.workflowWidgetToFire = workflowWidgetToFire;
		this.workflowVariables = workflowVariables;
		drawed = false;

		vPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();
		manager = new FormManager(this);

		vPanel.setWidth("300px");
		vPanel.setHeight("50px");

		vPanel.add(new HTML("<br>"));
		vPanel.add(manager.getTable());
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(hPanel, VerticalPanel.ALIGN_CENTER);

		initWidget(vPanel);
	}

	/**
	 * Gets asynchronous to run process definition
	 */
	final AsyncCallback<Object> callbackRunProcessDefinition = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			workflowWidgetToFire.finishedRunProcessDefinition();
			Main.get().mainPanel.dashboard.workflowDashboard.findUserTaskInstances();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("callbackRunProcessDefinition", caught);
		}
	};

	/**
	 * Run process definition
	 */
	public void runProcessDefinition() {
		if (drawed) {
			if (manager.getValidationProcessor().validate()) {
				runProcessDefinitionWithValues();
			} else {
				workflowWidgetToFire.hasPendingProcessDefinitionForms();
			}
		} else {
			getProcessDefinitionForms(name);
		}
	}

	/**
	 * runProcessDefinition with values
	 */
	private void runProcessDefinitionWithValues() {
		workflowService.runProcessDefinition(uuid, name, manager.updateFormElementsValuesWithNewer(), callbackRunProcessDefinition);
	}

	/**
	 * Get process definitions callback
	 */
	final AsyncCallback<Map<String, List<GWTFormElement>>> callbackGetProcessDefinitionForms = new AsyncCallback<Map<String, List<GWTFormElement>>>() {
		public void onSuccess(Map<String, List<GWTFormElement>> result) {
			// Initial task is always called start
			manager.setFormElements(result.get(Main.get().workspaceUserProperties.getWorkspace().getWorkflowRunConfigForm()));

			if (manager.getFormElements() != null) {
				manager.loadDataFromWorkflowVariables(workflowVariables);
				drawForm();
				workflowWidgetToFire.hasPendingProcessDefinitionForms();
			} else {
				manager.setFormElements(new ArrayList<GWTFormElement>());
				runProcessDefinitionWithValues();
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getProcessDefinitionForms", caught);
		}
	};

	/**
	 * getProcessDefinitionForms
	 */
	public void getProcessDefinitionForms(String name) {
		workflowService.getProcessDefinitionFormsByName(name, callbackGetProcessDefinitionForms);
	}

	/**
	 * drawForm
	 */
	private void drawForm() {
		manager.edit();
		drawed = true;
	}
	
	@Override
	public void validationWithPluginsFinished(boolean result) {
		if (result) {
			runProcessDefinitionWithValues();
		} else {
			workflowWidgetToFire.hasPendingProcessDefinitionForms();
		}
	}
}
