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

package com.openkm.ws.endpoint;

import com.openkm.bean.form.FormElement;
import com.openkm.bean.workflow.ProcessDefinition;
import com.openkm.bean.workflow.ProcessInstance;
import com.openkm.bean.workflow.TaskInstance;
import com.openkm.bean.workflow.Token;
import com.openkm.core.*;
import com.openkm.module.ModuleManager;
import com.openkm.module.WorkflowModule;
import com.openkm.ws.util.FormElementComplex;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebService(name = "OKMWorkflow", serviceName = "OKMWorkflow", targetNamespace = "http://ws.openkm.com")
public class WorkflowService {
	private static Logger log = LoggerFactory.getLogger(WorkflowService.class);

	@WebMethod
	public void registerProcessDefinition(@WebParam(name = "token") String token, @WebParam(name = "pda") byte[] pda)
			throws ParseException, AccessDeniedException, RepositoryException, DatabaseException, WorkflowException, IOException {
		log.debug("registerProcessDefinition({}, {})", token, pda);
		ByteArrayInputStream bais = new ByteArrayInputStream(pda);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.registerProcessDefinition(token, bais);
		IOUtils.closeQuietly(bais);
		log.debug("registerProcessDefinition: void");
	}

	@WebMethod
	public void deleteProcessDefinition(@WebParam(name = "token") String token, @WebParam(name = "pdId") long pdId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("deleteProcessDefinition({}, {})", token, pdId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.deleteProcessDefinition(token, pdId);
		log.debug("deleteProcessDefinition: void");
	}

	@WebMethod
	public ProcessDefinition getProcessDefinition(@WebParam(name = "token") String token, @WebParam(name = "pdId") long pdId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("getProcessDefinition({}, {})", token, pdId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		ProcessDefinition result = wm.getProcessDefinition(token, pdId);
		log.debug("getProcessDefinition: {}", result);
		return result;
	}

	@WebMethod
	public byte[] getProcessDefinitionImage(@WebParam(name = "token") String token, @WebParam(name = "pdId") long pdId,
	                                        @WebParam(name = "node") String node) throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("getProcessDefinitionImage({}, {}, {})", new Object[]{token, pdId, node});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		byte[] result = wm.getProcessDefinitionImage(token, pdId, node);
		log.debug("getProcessDefinitionImage: {}", result);
		return result;
	}
	
	/*
	 * public Map<String, List<FormElement>> getProcessDefinitionForms(@WebParam(name = "token") String token,
	 * @WebParam(name = "pdId") long pdId) throws ParseException, RepositoryException,
	 * DatabaseException, WorkflowException {
	 * log.debug("getProcessDefinitionForms({})", pdId);
	 * WorkflowModule wm = ModuleManager.getWorkflowModule();
	 * Map<String, List<FormElement>> result = wm.getProcessDefinitionForms(pdId);
	 * log.debug("getProcessDefinitionForms: "+result);
	 * return result;
	 * }
	 */

	@WebMethod
	public ProcessInstance runProcessDefinition(@WebParam(name = "token") String token, @WebParam(name = "pdId") long pdId,
	                                            @WebParam(name = "uuid") String uuid, @WebParam(name = "values") FormElementComplex[] values)
			throws WorkflowException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("runProcessDefinition({}, {}, {}, {})", new Object[]{token, pdId, uuid, values});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		List<FormElement> al = new ArrayList<FormElement>();

		for (int i = 0; i < values.length; i++) {
			al.add(FormElementComplex.toFormElement(values[i]));
		}

		ProcessInstance result = wm.runProcessDefinition(token, pdId, uuid, al);
		log.debug("runProcessDefinition: {}", result);
		return result;
	}

	@WebMethod
	public ProcessInstance sendProcessInstanceSignal(@WebParam(name = "token") String token,
	                                                 @WebParam(name = "piId") long piId, @WebParam(name = "transName") String transName) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("sendProcessInstanceSignal({}, {}, {})", new Object[]{token, piId, transName});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		ProcessInstance result = wm.sendProcessInstanceSignal(token, piId, transName);
		log.debug("sendProcessInstanceSignal: {}", result);
		return result;
	}

	@WebMethod
	public void endProcessInstance(@WebParam(name = "token") String token, @WebParam(name = "piId") long piId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("endProcessInstance({}, {})", token, piId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.endProcessInstance(token, piId);
		log.debug("endProcessInstance: void");
	}

	@WebMethod
	public void deleteProcessInstance(@WebParam(name = "token") String token, @WebParam(name = "piId") long piId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("deleteProcessInstance({}, {})", token, piId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.deleteProcessInstance(token, piId);
		log.debug("deleteProcessInstance: void");
	}

	@WebMethod
	public ProcessInstance[] findProcessInstances(@WebParam(name = "token") String token, @WebParam(name = "pdId") long pdId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("findProcessInstances({}, {})", token, pdId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		List<ProcessInstance> col = wm.findProcessInstances(token, pdId);
		ProcessInstance[] result = col.toArray(new ProcessInstance[col.size()]);
		log.debug("findProcessInstances: {}", result);
		return result;
	}

	@WebMethod
	public ProcessDefinition[] findAllProcessDefinitions(@WebParam(name = "token") String token) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("findAllProcessDefinitions({})", token);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		List<ProcessDefinition> col = wm.findAllProcessDefinitions(token);
		ProcessDefinition[] result = col.toArray(new ProcessDefinition[col.size()]);
		log.debug("findAllProcessDefinitions: {}", result);
		return result;
	}

	@WebMethod
	public ProcessDefinition[] findLatestProcessDefinitions(@WebParam(name = "token") String token) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("findLatestProcessDefinitions({})", token);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		List<ProcessDefinition> col = wm.findLatestProcessDefinitions(token);
		ProcessDefinition[] result = col.toArray(new ProcessDefinition[col.size()]);
		log.debug("findLatestProcessDefinitions: {}", result);
		return result;
	}

	@WebMethod
	public ProcessDefinition[] findAllProcessDefinitionVersions(@WebParam(name = "token") String token,
	                                                            @WebParam(name = "name") String name) throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("findAllProcessDefinitionVersions({}, {})", token, name);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		List<ProcessDefinition> col = wm.findAllProcessDefinitionVersions(token, name);
		ProcessDefinition[] result = col.toArray(new ProcessDefinition[col.size()]);
		log.debug("findAllProcessDefinitionVersions: {}", result);
		return result;
	}

	@WebMethod
	public long findLastProcessDefinitionId(@WebParam(name = "token") String token, @WebParam(name = "name") String name)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("findLastProcessDefinitionVersion({}, {})", token, name);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		List<ProcessDefinition> col = wm.findAllProcessDefinitionVersions(token, name);
		long lastProcDefId = 0;

		for (ProcessDefinition procDef : col) {
			if (procDef.getId() > lastProcDefId) {
				lastProcDefId = procDef.getId();
			}
		}

		log.debug("findLastProcessDefinitionVersion: {}", lastProcDefId);
		return lastProcDefId;
	}

	@WebMethod
	public ProcessInstance getProcessInstance(@WebParam(name = "token") String token, @WebParam(name = "piId") long piId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("getProcessInstance({}, {})", token, piId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		ProcessInstance result = wm.getProcessInstance(token, piId);
		log.debug("getProcessInstance: {}", result);
		return result;
	}

	@WebMethod
	public void suspendProcessInstance(@WebParam(name = "token") String token, @WebParam(name = "piId") long piId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("suspendProcessInstance({}, {})", token, piId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.suspendProcessInstance(token, piId);
		log.debug("suspendProcessInstance: void");
	}

	@WebMethod
	public void resumeProcessInstance(@WebParam(name = "token") String token, @WebParam(name = "piId") long piId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("resumeProcessInstance({}, {})", token, piId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.resumeProcessInstance(token, piId);
		log.debug("resumeProcessInstance: void");
	}

	@WebMethod
	public void addProcessInstanceVariable(@WebParam(name = "token") String token, @WebParam(name = "piId") long piId,
	                                       @WebParam(name = "name") String name, @WebParam(name = "value") Object value) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("addProcessInstanceVariable({}, {}, {}, {})", new Object[]{token, piId, name, value});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.addProcessInstanceVariable(token, piId, name, value);
		log.debug("addProcessInstanceVariable: void");
	}

	@WebMethod
	public void deleteProcessInstanceVariable(@WebParam(name = "token") String token, @WebParam(name = "piId") long piId,
	                                          @WebParam(name = "name") String name) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("deleteProcessInstanceVariable({}, {}, {})", new Object[]{token, piId, name});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.deleteProcessInstanceVariable(token, piId, name);
		log.debug("deleteProcessInstanceVariable: void");
	}

	@WebMethod
	public TaskInstance[] findUserTaskInstances(@WebParam(name = "token") String token) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("findUserTaskInstances({})", token);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		List<TaskInstance> col = wm.findUserTaskInstances(token);
		TaskInstance[] result = col.toArray(new TaskInstance[col.size()]);
		log.debug("findUserTaskInstances: {}", result);
		return result;
	}

	@WebMethod
	public TaskInstance[] findPooledTaskInstances(@WebParam(name = "token") String token) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("findPooledTaskInstances({})", token);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		List<TaskInstance> col = wm.findPooledTaskInstances(token);
		TaskInstance[] result = col.toArray(new TaskInstance[col.size()]);
		log.debug("findPooledTaskInstances: {}", result);
		return result;
	}

	@WebMethod
	public TaskInstance[] findTaskInstances(@WebParam(name = "token") String token, @WebParam(name = "piId") long piId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("findTaskInstances({}, {})", token, piId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		List<TaskInstance> col = wm.findTaskInstances(token, piId);
		TaskInstance[] result = col.toArray(new TaskInstance[col.size()]);
		log.debug("findTaskInstances: {}", result);
		return result;
	}

	@WebMethod
	public void setTaskInstanceValues(@WebParam(name = "token") String token, @WebParam(name = "tiId") long tiId,
	                                  @WebParam(name = "transName") String transName, @WebParam(name = "values") FormElementComplex[] values)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("setTaskInstanceValues({}, {}, {}, {})", new Object[]{token, tiId, transName, values});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		List<FormElement> al = new ArrayList<FormElement>();

		for (int i = 0; i < values.length; i++) {
			al.add(FormElementComplex.toFormElement(values[i]));
		}

		wm.setTaskInstanceValues(token, tiId, transName, al);
		log.debug("setTaskInstanceValues: void");
	}

	@WebMethod
	public void addTaskInstanceComment(@WebParam(name = "token") String token, @WebParam(name = "tiId") long tiId,
	                                   @WebParam(name = "message") String message) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("addTaskInstanceComment({}, {}, {})", new Object[]{token, tiId, message});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.addTaskInstanceComment(token, tiId, message);
		log.debug("addTaskInstanceComment: void");
	}

	@WebMethod
	public TaskInstance getTaskInstance(@WebParam(name = "token") String token, @WebParam(name = "tiId") long tiId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("getTaskInstance({}, {})", token, tiId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		TaskInstance result = wm.getTaskInstance(token, tiId);
		log.debug("getTaskInstance: {}", result);
		return result;
	}

	@WebMethod
	public void setTaskInstanceActorId(@WebParam(name = "token") String token, @WebParam(name = "tiId") long tiId,
	                                   @WebParam(name = "actorId") String actorId) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("setTaskInstanceActorId({}, {}, {})", new Object[]{token, tiId, actorId});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.setTaskInstanceActorId(token, tiId, actorId);
		log.debug("setTaskInstanceActorId: void");
	}

	@WebMethod
	public void addTaskInstanceVariable(@WebParam(name = "token") String token, @WebParam(name = "tiId") long tiId,
	                                    @WebParam(name = "name") String name, @WebParam(name = "value") Object value) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("addTaskInstanceVariable({}, {}, {}, {})", new Object[]{token, tiId, name, value});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.addTaskInstanceVariable(token, tiId, name, value);
		log.debug("addTaskInstanceVariable: void");
	}

	@WebMethod
	public void deleteTaskInstanceVariable(@WebParam(name = "token") String token, @WebParam(name = "tiId") long tiId,
	                                       @WebParam(name = "name") String name) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("deleteTaskInstanceVariable({}, {}, {})", new Object[]{token, tiId, name});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.deleteTaskInstanceVariable(token, tiId, name);
		log.debug("deleteTaskInstanceVariable: void");
	}

	@WebMethod
	public void startTaskInstance(@WebParam(name = "token") String token, @WebParam(name = "tiId") long tiId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("startTaskInstance({}, {})", token, tiId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.startTaskInstance(token, tiId);
		log.debug("startTaskInstance: void");
	}

	@WebMethod
	public void endTaskInstance(@WebParam(name = "token") String token, @WebParam(name = "tiId") long tiId,
	                            @WebParam(name = "transName") String transName) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("endTaskInstance({}, {}, {})", new Object[]{token, tiId, transName});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.endTaskInstance(token, tiId, transName);
		log.debug("endTaskInstance: void");
	}

	@WebMethod
	public void suspendTaskInstance(@WebParam(name = "token") String token, @WebParam(name = "tiId") long tiId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("suspendTaskInstance({}, {})", token, tiId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.suspendTaskInstance(token, tiId);
		log.debug("suspendTaskInstance: void");
	}

	@WebMethod
	public void resumeTaskInstance(@WebParam(name = "token") String token, @WebParam(name = "tiId") long tiId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("resumeTaskInstance({}, {})", token, tiId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.resumeTaskInstance(token, tiId);
		log.debug("resumeTaskInstance: void");
	}

	@WebMethod
	public Token getToken(@WebParam(name = "token") String token, @WebParam(name = "tkId") long tkId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("getToken({}, {})", token, tkId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		Token result = wm.getToken(token, tkId);
		log.debug("getToken: {}", result);
		return result;
	}

	@WebMethod
	public void addTokenComment(@WebParam(name = "token") String token, @WebParam(name = "tkId") long tkId,
	                            @WebParam(name = "message") String message) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("addTokenComment({}, {}, {})", new Object[]{token, tkId, message});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.addTokenComment(token, tkId, message);
		log.debug("addTokenComment: void");
	}

	@WebMethod
	public void suspendToken(@WebParam(name = "token") String token, @WebParam(name = "tkId") long tkId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("suspendToken({}, {})", token, tkId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.suspendToken(token, tkId);
		log.debug("suspendToken: void");
	}

	@WebMethod
	public void resumeToken(@WebParam(name = "token") String token, @WebParam(name = "tkId") long tkId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("resumeToken({}, {})", token, tkId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.resumeToken(token, tkId);
		log.debug("resumeToken: void");
	}

	@WebMethod
	public Token sendTokenSignal(@WebParam(name = "token") String token, @WebParam(name = "tkId") long tkId,
	                             @WebParam(name = "transName") String transName) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("sendTokenSignal({}, {}, {})", new Object[]{token, tkId, transName});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		Token result = wm.sendTokenSignal(token, tkId, transName);
		log.debug("sendTokenSignal: {}", result);
		return result;
	}

	@WebMethod
	public void setTokenNode(@WebParam(name = "token") String token, @WebParam(name = "tkId") long tkId,
	                         @WebParam(name = "nodeName") String nodeName) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("setTokenNode({}, {}, {})", new Object[]{token, tkId, nodeName});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.setTokenNode(token, tkId, nodeName);
		log.debug("setTokenNode: void");
	}

	@WebMethod
	public void endToken(@WebParam(name = "token") String token, @WebParam(name = "tkId") long tkId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("endToken({}, {})", token, tkId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.endToken(token, tkId);
		log.debug("endToken: void");
	}
}
