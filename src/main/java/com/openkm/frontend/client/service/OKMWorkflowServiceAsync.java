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

package com.openkm.frontend.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.openkm.frontend.client.bean.GWTProcessDefinition;
import com.openkm.frontend.client.bean.GWTProcessInstance;
import com.openkm.frontend.client.bean.GWTProcessInstanceLogEntry;
import com.openkm.frontend.client.bean.GWTTaskInstance;
import com.openkm.frontend.client.bean.form.GWTFormElement;

import java.util.List;
import java.util.Map;


/**
 * @author jllort
 *
 */
public interface OKMWorkflowServiceAsync {
	public void findLatestProcessDefinitions(AsyncCallback<List<GWTProcessDefinition>> callback);

	public void runProcessDefinition(String UUID, String name, List<GWTFormElement> formElements, AsyncCallback<?> callback);

	public void findUserTaskInstances(AsyncCallback<List<GWTTaskInstance>> callback);

	public void getProcessDefinitionForms(double id, AsyncCallback<Map<String, List<GWTFormElement>>> callback);

	public void getProcessDefinitionFormsByName(String name, AsyncCallback<Map<String, List<GWTFormElement>>> callback);

	public void setTaskInstanceValues(double id, String transitionName, List<GWTFormElement> formElements, AsyncCallback<?> callback);

	public void addComment(double tokenId, String message, AsyncCallback<?> callback);

	public void findPooledTaskInstances(AsyncCallback<List<GWTTaskInstance>> callback);

	public void setTaskInstanceActorId(double id, AsyncCallback<?> callback);

	public void startTaskInstance(double id, AsyncCallback<?> callback);

	public void findProcessInstancesByNode(String uuid, AsyncCallback<List<GWTProcessInstance>> callback);

	public void findLogsByProcessInstance(int processInstanceId, AsyncCallback<List<GWTProcessInstanceLogEntry>> callback);

	public void getUserTaskInstance(long taskInstanceId, AsyncCallback<GWTTaskInstance> callback);
}