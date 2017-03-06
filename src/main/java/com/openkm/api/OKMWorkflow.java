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

package com.openkm.api;

import com.openkm.bean.form.FormElement;
import com.openkm.bean.workflow.ProcessDefinition;
import com.openkm.bean.workflow.ProcessInstance;
import com.openkm.bean.workflow.TaskInstance;
import com.openkm.bean.workflow.Token;
import com.openkm.core.*;
import com.openkm.module.ModuleManager;
import com.openkm.module.WorkflowModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author pavila
 *
 */
public class OKMWorkflow implements WorkflowModule {
	private static Logger log = LoggerFactory.getLogger(OKMWorkflow.class);
	private static OKMWorkflow instance = new OKMWorkflow();

	private OKMWorkflow() {
	}

	public static OKMWorkflow getInstance() {
		return instance;
	}

	@Override
	public void registerProcessDefinition(String token, InputStream is) throws ParseException,
			AccessDeniedException, RepositoryException, DatabaseException, WorkflowException, IOException {
		log.debug("registerProcessDefinition({}, {})", token, is);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.registerProcessDefinition(token, is);
		log.debug("registerProcessDefinition: void");
	}

	@Override
	public void deleteProcessDefinition(String token, long processDefinitionId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("deleteProcessDefinition({}, {})", token, processDefinitionId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.deleteProcessDefinition(token, processDefinitionId);
		log.debug("deleteProcessDefinition: void");
	}

	@Override
	public ProcessDefinition getProcessDefinition(String token, long processDefinitionId) throws
			AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("getProcessDefinition({}, {})", token, processDefinitionId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		ProcessDefinition result = wm.getProcessDefinition(token, processDefinitionId);
		log.debug("getProcessDefinition: {}", result);
		return result;
	}

	@Override
	public byte[] getProcessDefinitionImage(String token, long processDefinitionId, String node) throws
			AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("getProcessDefinitionImage({}, {}, {})", new Object[]{token, processDefinitionId, node});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		byte[] result = wm.getProcessDefinitionImage(token, processDefinitionId, node);
		log.debug("getProcessDefinitionImage: {}", result);
		return result;
	}

	@Override
	public Map<String, List<FormElement>> getProcessDefinitionForms(String token, long processDefinitionId)
			throws ParseException, AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("getProcessDefinitionForms({}, {})", token, processDefinitionId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		Map<String, List<FormElement>> result = wm.getProcessDefinitionForms(token, processDefinitionId);
		log.debug("getProcessDefinitionForms: {}", result);
		return result;
	}

	@Override
	public ProcessInstance runProcessDefinition(String token, long processDefinitionId, String uuid,
	                                            List<FormElement> variables) throws WorkflowException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("runProcessDefinition({}, {}, {}, {})", new Object[]{token, processDefinitionId, uuid, variables});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		ProcessInstance result = wm.runProcessDefinition(token, processDefinitionId, uuid, variables);
		log.debug("runProcessDefinition: {}", result);
		return result;
	}

	@Override
	public ProcessInstance sendProcessInstanceSignal(String token, long processInstanceId,
	                                                 String transitionName) throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("sendProcessInstanceSignal({}, {}, {})", new Object[]{token, processInstanceId, transitionName});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		ProcessInstance result = wm.sendProcessInstanceSignal(token, processInstanceId, transitionName);
		log.debug("sendProcessInstanceSignal: {}", result);
		return result;
	}

	@Override
	public void endProcessInstance(String token, long processInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("endProcessInstance({}, {})", token, processInstanceId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.endProcessInstance(token, processInstanceId);
		log.debug("endProcessInstance: void");
	}

	@Override
	public void deleteProcessInstance(String token, long processInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("deleteProcessInstance({}, {})", token, processInstanceId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.deleteProcessInstance(token, processInstanceId);
		log.debug("deleteProcessInstance: void");
	}

	@Override
	public List<ProcessInstance> findProcessInstances(String token, long processDefinitionId) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("findProcessInstances({}, {})", token, processDefinitionId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		List<ProcessInstance> result = wm.findProcessInstances(token, processDefinitionId);
		log.debug("findProcessInstances: {}", result);
		return result;
	}

	@Override
	public List<ProcessDefinition> findAllProcessDefinitions(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("findAllProcessDefinitions({})", token);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		List<ProcessDefinition> result = wm.findAllProcessDefinitions(token);
		log.debug("findAllProcessDefinitions: {}", result);
		return result;
	}

	@Override
	public List<ProcessDefinition> findLatestProcessDefinitions(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("findLatestProcessDefinitions({})", token);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		List<ProcessDefinition> result = wm.findLatestProcessDefinitions(token);
		log.debug("findLatestProcessDefinitions: {}", result);
		return result;
	}

	@Override
	public ProcessDefinition findLastProcessDefinition(String token, String name) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("findLastProcessDefinition({})", token);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		ProcessDefinition result = wm.findLastProcessDefinition(token, name);
		log.debug("findLastProcessDefinition: {}", result);
		return result;
	}

	@Override
	public List<ProcessDefinition> findAllProcessDefinitionVersions(String token, String name) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("findAllProcessDefinitionVersions({}, {})", token, name);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		List<ProcessDefinition> result = wm.findAllProcessDefinitionVersions(token, name);
		log.debug("findAllProcessDefinitionVersions: {}", result);
		return result;
	}

	@Override
	public ProcessInstance getProcessInstance(String token, long processInstanceId) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("getProcessInstance({}, {})", token, processInstanceId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		ProcessInstance result = wm.getProcessInstance(token, processInstanceId);
		log.debug("getProcessInstance: {}", result);
		return result;
	}

	@Override
	public void suspendProcessInstance(String token, long processInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("suspendProcessInstance({}, {})", token, processInstanceId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.suspendProcessInstance(token, processInstanceId);
		log.debug("suspendProcessInstance: void");
	}

	@Override
	public void resumeProcessInstance(String token, long processInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("resumeProcessInstance({}, {})", token, processInstanceId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.resumeProcessInstance(token, processInstanceId);
		log.debug("resumeProcessInstance: void");
	}

	@Override
	public void addProcessInstanceVariable(String token, long processInstanceId, String name, Object value)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("addProcessInstanceVariable({}, {}, {}, {})", new Object[]{token, processInstanceId, name, value});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.addProcessInstanceVariable(token, processInstanceId, name, value);
		log.debug("addProcessInstanceVariable: void");
	}

	@Override
	public void deleteProcessInstanceVariable(String token, long processInstanceId, String name) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("deleteProcessInstanceVariable({}, {}, {})", new Object[]{token, processInstanceId, name});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.deleteProcessInstanceVariable(token, processInstanceId, name);
		log.debug("deleteProcessInstanceVariable: void");
	}

	@Override
	public List<TaskInstance> findUserTaskInstances(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("findUserTaskInstances({})", token);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		List<TaskInstance> result = wm.findUserTaskInstances(token);
		log.debug("findUserTaskInstances: {}", result);
		return result;
	}

	@Override
	public List<TaskInstance> findPooledTaskInstances(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("findPooledTaskInstances({})", token);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		List<TaskInstance> result = wm.findPooledTaskInstances(token);
		log.debug("findPooledTaskInstances: {}", result);
		return result;
	}

	@Override
	public List<TaskInstance> findTaskInstances(String token, long processInstanceId) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("findTaskInstances({}, {})", token, processInstanceId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		List<TaskInstance> result = wm.findTaskInstances(token, processInstanceId);
		log.debug("findTaskInstances: {}", result);
		return result;
	}

	@Override
	public void setTaskInstanceValues(String token, long taskInstanceId, String transitionName,
	                                  List<FormElement> values) throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("setTaskInstanceValues({}, {}, {}, {})", new Object[]{token, taskInstanceId, transitionName, values});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.setTaskInstanceValues(token, taskInstanceId, transitionName, values);
		log.debug("setTaskInstanceValues: void");
	}

	@Override
	public void addTaskInstanceComment(String token, long taskInstanceId, String message) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("addTaskInstanceComment({}, {}, {})", new Object[]{token, taskInstanceId, message});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.addTaskInstanceComment(token, taskInstanceId, message);
		log.debug("addTaskInstanceComment: void");
	}

	@Override
	public TaskInstance getTaskInstance(String token, long taskInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("getTaskInstance({}, {})", token, taskInstanceId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		TaskInstance result = wm.getTaskInstance(token, taskInstanceId);
		log.debug("getTaskInstance: {}", result);
		return result;
	}

	@Override
	public void setTaskInstanceActorId(String token, long taskInstanceId, String actorId) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("setTaskInstanceActorId({}, {}, {})", new Object[]{token, taskInstanceId, actorId});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.setTaskInstanceActorId(token, taskInstanceId, actorId);
		log.debug("setTaskInstanceActorId: void");
	}

	@Override
	public void addTaskInstanceVariable(String token, long taskInstanceId, String name, Object value) throws
			AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("addTaskInstanceVariable({}, {}, {}, {})", new Object[]{token, taskInstanceId, name, value});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.addTaskInstanceVariable(token, taskInstanceId, name, value);
		log.debug("addTaskInstanceVariable: void");
	}

	@Override
	public void deleteTaskInstanceVariable(String token, long taskInstanceId, String name) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("deleteTaskInstanceVariable({}, {}, {})", new Object[]{token, taskInstanceId, name});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.deleteTaskInstanceVariable(token, taskInstanceId, name);
		log.debug("deleteTaskInstanceVariable: void");
	}

	@Override
	public void startTaskInstance(String token, long taskInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("startTaskInstance({}, {})", token, taskInstanceId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.startTaskInstance(token, taskInstanceId);
		log.debug("startTaskInstance: void");
	}

	@Override
	public void endTaskInstance(String token, long taskInstanceId, String transitionName) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("endTaskInstance({}, {} ,{})", new Object[]{token, taskInstanceId, transitionName});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.endTaskInstance(token, taskInstanceId, transitionName);
		log.debug("endTaskInstance: void");
	}

	@Override
	public void suspendTaskInstance(String token, long taskInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("suspendTaskInstance({}, {})", token, taskInstanceId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.suspendTaskInstance(token, taskInstanceId);
		log.debug("suspendTaskInstance: void");
	}

	@Override
	public void resumeTaskInstance(String token, long taskInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("resumeTaskInstance({}, {})", token, taskInstanceId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.resumeTaskInstance(token, taskInstanceId);
		log.debug("resumeTaskInstance: void");
	}

	@Override
	public Token getToken(String token, long tokenId) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("getToken({}, {})", token, tokenId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		Token result = wm.getToken(token, tokenId);
		log.debug("getToken: {}", result);
		return result;
	}

	@Override
	public void addTokenComment(String token, long tokenId, String message) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("addTokenComment({}, {}, {})", new Object[]{token, tokenId, message});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.addTokenComment(token, tokenId, message);
		log.debug("addTokenComment: void");
	}

	@Override
	public void suspendToken(String token, long tokenId) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("suspendToken({}, {})", token, tokenId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.suspendToken(token, tokenId);
		log.debug("suspendToken: void");
	}

	@Override
	public void resumeToken(String token, long tokenId) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("resumeToken({}, {})", token, tokenId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.resumeToken(token, tokenId);
		log.debug("resumeToken: void");
	}

	@Override
	public Token sendTokenSignal(String token, long tokenId, String transitionName) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("sendTokenSignal({}, {}, {})", new Object[]{token, tokenId, transitionName});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		Token result = wm.sendTokenSignal(token, tokenId, transitionName);
		log.debug("sendTokenSignal: {}", result);
		return result;
	}

	@Override
	public void setTokenNode(String token, long tokenId, String nodeName) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("setTokenNode({}, {}, {})", new Object[]{token, tokenId, nodeName});
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.setTokenNode(token, tokenId, nodeName);
		log.debug("setTokenNode: void");
	}

	@Override
	public void endToken(String token, long tokenId) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("endToken({}, {})", token, tokenId);
		WorkflowModule wm = ModuleManager.getWorkflowModule();
		wm.endToken(token, tokenId);
		log.debug("endToken: void");
	}
}
