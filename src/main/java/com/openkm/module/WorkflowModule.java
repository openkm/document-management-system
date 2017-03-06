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

package com.openkm.module;

import com.openkm.bean.form.FormElement;
import com.openkm.bean.workflow.ProcessDefinition;
import com.openkm.bean.workflow.ProcessInstance;
import com.openkm.bean.workflow.TaskInstance;
import com.openkm.bean.workflow.Token;
import com.openkm.core.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface WorkflowModule {

	/**
	 * Register a new process definition in the workflow engine.
	 *
	 * @param token The session authorization token.
	 * @param is Input stream where process definition can be read.
	 * @throws ParseException If there is an error parsing the forms.xml file.
	 * @throws RepositoryException If there is a general repository error.
	 * @throws DatabaseException If there is a general database error.
	 * @throws WorkflowException If there is any workflow engine error.
	 */
	public void registerProcessDefinition(String token, InputStream is) throws ParseException,
			AccessDeniedException, RepositoryException, DatabaseException, WorkflowException, IOException;

	/**
	 * Delete a previously registered process definition.
	 *
	 * @param token The session authorization token.
	 * @param processDefinitionId Process definition identifier.
	 * @throws RepositoryException If there is a general repository error.
	 * @throws DatabaseException If there is a general database error.
	 * @throws WorkflowException If there is any workflow engine error.
	 */
	public void deleteProcessDefinition(String token, long processDefinitionId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException;

	/**
	 * Gets a process definition from the engine by the identifier.
	 *
	 * @param token The session authorization token.
	 * @param processDefinitionId Process definition identifier.
	 * @throws RepositoryException If there is a general repository error.
	 * @throws DatabaseException If there is a general database error.
	 * @throws WorkflowException If there is any workflow engine error.
	 */
	public ProcessDefinition getProcessDefinition(String token, long processDefinitionId) throws
			AccessDeniedException, RepositoryException, DatabaseException, WorkflowException;

	/**
	 * Get a visual representation of the process definition.
	 *
	 * @param token The session authorization token.
	 * @param processDefinitionId Process definition identifier.
	 * @param node Mark the designed node as active, if not null.
	 * @throws RepositoryException If there is a general repository error.
	 * @throws DatabaseException If there is a general database error.
	 * @throws WorkflowException If there is any workflow engine error.
	 */
	public byte[] getProcessDefinitionImage(String token, long processDefinitionId, String node) throws
			AccessDeniedException, RepositoryException, DatabaseException, WorkflowException;

	/**
	 * Obtain a map with the forms defined in the process definition.
	 *
	 * @param token The session authorization token.
	 * @param processDefinitionId Process definition identifier.
	 * @throws ParseException If there is an error parsing the forms.xml file.
	 * @throws RepositoryException If there is a general repository error.
	 * @throws DatabaseException If there is a general database error.
	 * @throws WorkflowException If there is any workflow engine error.
	 */
	public Map<String, List<FormElement>> getProcessDefinitionForms(String token, long processDefinitionId)
			throws ParseException, AccessDeniedException, RepositoryException, DatabaseException, WorkflowException;

	/**
	 * Run a process definition to create a process instance: begins a worflow process.
	 *
	 * @param token The session authorization token.
	 * @param processDefinitionId Process definition identifier.
	 * @param uuid Node identifier associated with the created process instance.
	 * @param vars A list of form elements with variable definitions.
	 * @return The created process instance.
	 * @throws RepositoryException If there is a general repository error.
	 * @throws DatabaseException If there is a general database error.
	 * @throws WorkflowException If there is any workflow engine error.
	 */
	public ProcessInstance runProcessDefinition(String token, long processDefinitionId, String uuid,
	                                            List<FormElement> vars) throws WorkflowException, AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 *
	 */
	public ProcessInstance sendProcessInstanceSignal(String token, long processInstanceId,
	                                                 String transitionName) throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void endProcessInstance(String token, long processInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void deleteProcessInstance(String token, long processInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException;

	/**
	 *
	 */
	public List<ProcessInstance> findProcessInstances(String token, long processDefinitionId) throws
			AccessDeniedException, RepositoryException, DatabaseException, WorkflowException;

	/**
	 * Get list of registered process definitions
	 */
	public List<ProcessDefinition> findAllProcessDefinitions(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException;

	/**
	 * Get list of registered process definitions. Only last version for each process
	 */
	public List<ProcessDefinition> findLatestProcessDefinitions(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException;

	/**
	 * Get last version of a given process definition.
	 */
	public ProcessDefinition findLastProcessDefinition(String token, String name) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException;

	/**
	 * Get list of registered process definitions versions
	 */
	public List<ProcessDefinition> findAllProcessDefinitionVersions(String token, String name) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException;

	/**
	 *
	 */
	public ProcessInstance getProcessInstance(String token, long processInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void suspendProcessInstance(String token, long processInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void resumeProcessInstance(String token, long processInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void addProcessInstanceVariable(String token, long processInstanceId, String name, Object value)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void deleteProcessInstanceVariable(String token, long processInstanceId, String name) throws
			AccessDeniedException, RepositoryException, DatabaseException, WorkflowException;

	/**
	 *
	 */
	public List<TaskInstance> findUserTaskInstances(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException;

	/**
	 *
	 */
	public List<TaskInstance> findPooledTaskInstances(String token) throws RepositoryException,
			AccessDeniedException, DatabaseException, WorkflowException;

	/**
	 *
	 */
	public List<TaskInstance> findTaskInstances(String token, long processInstanceId) throws
			AccessDeniedException, RepositoryException, DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void setTaskInstanceValues(String token, long taskInstanceId, String transitionName,
	                                  List<FormElement> values) throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void addTaskInstanceComment(String token, long taskInstanceId, String message) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException;

	/**
	 *
	 */
	public TaskInstance getTaskInstance(String token, long taskInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void setTaskInstanceActorId(String token, long taskInstanceId, String actorId) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void addTaskInstanceVariable(String token, long taskInstanceId, String name, Object value) throws
			AccessDeniedException, RepositoryException, DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void deleteTaskInstanceVariable(String token, long taskInstanceId, String name) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void startTaskInstance(String token, long taskInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void endTaskInstance(String token, long taskInstanceId, String transitionName) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void suspendTaskInstance(String token, long taskInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void resumeTaskInstance(String token, long taskInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException;

	/**
	 *
	 */
	public Token getToken(String token, long tokenId) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException;

	/**
	 *
	 */
	public void addTokenComment(String token, long tokenId, String message) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void suspendToken(String token, long tokenId) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException;

	/**
	 *
	 */
	public void resumeToken(String token, long tokenId) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException;

	/**
	 *
	 */
	public Token sendTokenSignal(String token, long tokenId, String transitionName) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void setTokenNode(String token, long tokenId, String nodeName) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException;

	/**
	 *
	 */
	public void endToken(String token, long tokenId) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException;
}
