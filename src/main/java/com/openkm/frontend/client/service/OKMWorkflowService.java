/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.openkm.frontend.client.OKMException;
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
@RemoteServiceRelativePath("Workflow")
public interface OKMWorkflowService extends RemoteService {
	List<GWTProcessDefinition> findLatestProcessDefinitions() throws OKMException;

	void runProcessDefinition(String UUID, String name, List<GWTFormElement> formElements) throws OKMException;

	List<GWTTaskInstance> findUserTaskInstances() throws OKMException;

	Map<String, List<GWTFormElement>> getProcessDefinitionForms(double id) throws OKMException;

	Map<String, List<GWTFormElement>> getProcessDefinitionFormsByName(String name) throws OKMException;

	void setTaskInstanceValues(double id, String transitionName, List<GWTFormElement> formElements) throws OKMException;

	void addComment(double tokenId, String message) throws OKMException;

	List<GWTTaskInstance> findPooledTaskInstances() throws OKMException;

	void setTaskInstanceActorId(double id) throws OKMException;

	void startTaskInstance(double id) throws OKMException;

	List<GWTProcessInstance> findProcessInstancesByNode(String uuid) throws OKMException;

	List<GWTProcessInstanceLogEntry> findLogsByProcessInstance(int processInstanceId) throws OKMException;

	GWTTaskInstance getUserTaskInstance(long taskInstanceId) throws OKMException;
}
