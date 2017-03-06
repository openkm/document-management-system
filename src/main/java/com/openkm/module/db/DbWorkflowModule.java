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

package com.openkm.module.db;

import com.openkm.bean.Permission;
import com.openkm.bean.form.FormElement;
import com.openkm.bean.workflow.ProcessDefinition;
import com.openkm.bean.workflow.ProcessInstance;
import com.openkm.bean.workflow.TaskInstance;
import com.openkm.bean.workflow.Token;
import com.openkm.core.*;
import com.openkm.module.WorkflowModule;
import com.openkm.module.common.CommonWorkflowModule;
import com.openkm.module.db.stuff.SecurityHelper;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.UserActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class DbWorkflowModule implements WorkflowModule {
	private static Logger log = LoggerFactory.getLogger(DbWorkflowModule.class);

	@Override
	public void registerProcessDefinition(String token, InputStream is) throws ParseException, AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException, IOException {
		log.debug("registerProcessDefinition({}, {})", token, is);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.registerProcessDefinition(is);

			// Activity log
			UserActivity.log(auth.getName(), "REGISTER_PROCESS_DEFINITION", null, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("registerProcessDefinition: void");
	}

	@Override
	public void deleteProcessDefinition(String token, long processDefinitionId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("deleteProcessDefinition({}, {})", token, processDefinitionId);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.deleteProcessDefinition(processDefinitionId);

			// Activity log
			UserActivity.log(auth.getName(), "DELETE_PROCESS_DEFINITION", "" + processDefinitionId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("deleteProcessDefinition: void");
	}

	@Override
	public ProcessDefinition getProcessDefinition(String token, long processDefinitionId) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("getProcessDefinition({}, {})", token, processDefinitionId);
		ProcessDefinition vo = null;
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			vo = CommonWorkflowModule.getProcessDefinition(processDefinitionId);

			// Activity log
			UserActivity.log(auth.getName(), "GET_PROCESS_DEFINITION", "" + processDefinitionId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getProcessDefinition: {}", vo);
		return vo;
	}

	@Override
	public byte[] getProcessDefinitionImage(String token, long processDefinitionId, String node)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("getProcessDefinitionImage({}, {}, {})", new Object[]{token, processDefinitionId, node});
		byte[] image = null;
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			image = CommonWorkflowModule.getProcessDefinitionImage(processDefinitionId, node);

			// Activity log
			UserActivity.log(auth.getName(), "GET_PROCESS_DEFINITION_IMAGE", "" + processDefinitionId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getProcessDefinitionImage: {}", image);
		return image;
	}

	@Override
	public Map<String, List<FormElement>> getProcessDefinitionForms(String token, long processDefinitionId)
			throws ParseException, AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("getProcessDefinitionForms({}, {})", token, processDefinitionId);
		Map<String, List<FormElement>> forms = null;
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			forms = CommonWorkflowModule.getProcessDefinitionForms(processDefinitionId);

			// Activity log
			UserActivity.log(auth.getName(), "GET_PROCESS_DEFINITION_FORMS", processDefinitionId + "", null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getProcessDefinitionForms: {}", forms);
		return forms;
	}

	@Override
	public ProcessInstance runProcessDefinition(String token, long processDefinitionId, String uuid,
	                                            List<FormElement> variables) throws WorkflowException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("runProcessDefinition({}, {}, {})", new Object[]{token, processDefinitionId, variables});
		ProcessInstance vo = null;
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			// Security Check
			if ((Config.SECURITY_EXTENDED_MASK & Permission.START_WORKFLOW) == Permission.START_WORKFLOW) {
				SecurityHelper.checkExtended(uuid, Permission.START_WORKFLOW);
			}

			vo = CommonWorkflowModule.runProcessDefinition(auth.getName(), processDefinitionId, uuid, variables);

			// Activity log
			UserActivity.log(auth.getName(), "RUN_PROCESS_DEFINITION", "" + processDefinitionId, null, variables.toString());
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("runProcessDefinition: {}", vo);
		return vo;
	}

	@Override
	public ProcessInstance sendProcessInstanceSignal(String token, long processInstanceId, String transitionName)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("sendProcessInstanceSignal({}, {}, {})", new Object[]{token, processInstanceId, transitionName});
		ProcessInstance vo = null;
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			vo = CommonWorkflowModule.sendProcessInstanceSignal(processInstanceId, transitionName);

			// Activity log
			UserActivity.log(auth.getName(), "SEND_PROCESS_INSTANCE_SIGNAL", "" + processInstanceId, null, transitionName);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("sendProcessInstanceSignal: {}", vo);
		return vo;
	}

	@Override
	public void endProcessInstance(String token, long processInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("endProcessInstance({}, {})", token, processInstanceId);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.endProcessInstance(processInstanceId);

			// Activity log
			UserActivity.log(auth.getName(), "END_PROCESS_INSTANCE", "" + processInstanceId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("endProcessInstance: void");
	}

	@Override
	public void deleteProcessInstance(String token, long processInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("deleteProcessInstance({}, {})", token, processInstanceId);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.deleteProcessInstance(processInstanceId);

			// Activity log
			UserActivity.log(auth.getName(), "DELETE_PROCESS_INSTANCE", "" + processInstanceId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("deleteProcessInstance: void");
	}

	@Override
	public List<ProcessInstance> findProcessInstances(String token, long processDefinitionId)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("findProcessInstances({}, {})", token, processDefinitionId);
		List<ProcessInstance> al = null;
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			al = CommonWorkflowModule.findProcessInstances(processDefinitionId);

			// Activity log
			UserActivity.log(auth.getName(), "FIND_PROCESS_INSTANCES", "" + processDefinitionId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("findProcessInstances: {}", al);
		return al;
	}

	@Override
	public List<ProcessDefinition> findAllProcessDefinitions(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("findAllProcessDefinitions({})", token);
		List<ProcessDefinition> al = null;
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			al = CommonWorkflowModule.findAllProcessDefinitions();

			// Activity log
			UserActivity.log(auth.getName(), "FIND_ALL_PROCESS_DEFINITIONS", null, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("findAllProcessDefinitions: {}", al);
		return al;
	}

	@Override
	public List<ProcessDefinition> findLatestProcessDefinitions(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("findLatestProcessDefinitions({})", token);
		List<ProcessDefinition> al = null;
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			al = CommonWorkflowModule.findLatestProcessDefinitions();

			// Activity log
			UserActivity.log(auth.getName(), "FIND_LATEST_PROCESS_DEFINITIONS", null, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("findLatestProcessDefinitions: {}", al);
		return al;
	}

	@Override
	public ProcessDefinition findLastProcessDefinition(String token, String name) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("findLastProcessDefinition({}, {})", token, name);
		ProcessDefinition pd = null;
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			pd = CommonWorkflowModule.findLastProcessDefinition(name);

			// Activity log
			UserActivity.log(auth.getName(), "FIND_LAST_PROCESS_DEFINITION", name, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("findLastProcessDefinition: {}", pd);
		return pd;
	}

	@Override
	public List<ProcessDefinition> findAllProcessDefinitionVersions(String token, String name)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("findAllProcessDefinitionVersions({}, {})", token, name);
		List<ProcessDefinition> al = null;
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			al = CommonWorkflowModule.findAllProcessDefinitionVersions(name);

			// Activity log
			UserActivity.log(auth.getName(), "FIND_ALL_PROCESS_DEFINITION_VERSIONS", name, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("findAllProcessDefinitionVersions: {}", al);
		return al;
	}

	@Override
	public ProcessInstance getProcessInstance(String token, long processInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("getProcessInstance({}, {})", token, processInstanceId);
		ProcessInstance vo = null;
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			vo = CommonWorkflowModule.getProcessInstance(processInstanceId);

			// Activity log
			UserActivity.log(auth.getName(), "GET_PROCESS_INSTANCE", "" + processInstanceId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getProcessInstance: {}", vo);
		return vo;
	}

	@Override
	public void suspendProcessInstance(String token, long processInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("suspendProcessInstance({}, {})", token, processInstanceId);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.suspendProcessInstance(processInstanceId);

			// Activity log
			UserActivity.log(auth.getName(), "SUSPEND_PROCESS_INSTANCE", "" + processInstanceId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("suspendProcessInstance: void");
	}

	@Override
	public void resumeProcessInstance(String token, long processInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("resumeProcessInstance({}, {})", token, processInstanceId);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.resumeProcessInstance(processInstanceId);

			// Activity log
			UserActivity.log(auth.getName(), "RESUME_PROCESS_INSTANCE", "" + processInstanceId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("resumeProcessInstance: void");
	}

	@Override
	public void addProcessInstanceVariable(String token, long processInstanceId, String name, Object value)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("addProcessInstanceVariable({}, {}, {}, {})", new Object[]{token, processInstanceId, name, value});
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.addProcessInstanceVariable(processInstanceId, name, value);

			// Activity log
			UserActivity.log(auth.getName(), "ADD_PROCESS_INSTANCE_VARIABLE", "" + processInstanceId, null, name + ", " + value);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("addProcessInstanceVariable: void");
	}

	@Override
	public void deleteProcessInstanceVariable(String token, long processInstanceId, String name)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("deleteProcessInstanceVariable({}, {}, {})", new Object[]{token, processInstanceId, name});
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.deleteProcessInstanceVariable(processInstanceId, name);

			// Activity log
			UserActivity.log(auth.getName(), "DELETE_PROCESS_INSTANCE_VARIABLE", "" + processInstanceId, null, name);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("deleteProcessInstanceVariable: void");
	}

	@Override
	public List<TaskInstance> findUserTaskInstances(String token) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("findUserTaskInstances({})", token);
		List<TaskInstance> al = null;
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			al = CommonWorkflowModule.findUserTaskInstances(auth.getName());

			// Activity log
			UserActivity.log(auth.getName(), "FIND_USER_TASK_INSTANCES", null, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("findUserTaskInstances: {}", al);
		return al;
	}

	@Override
	public List<TaskInstance> findPooledTaskInstances(String token) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("findPooledTaskInstances({})", token);
		List<TaskInstance> al = null;
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			al = CommonWorkflowModule.findPooledTaskInstances(auth.getName());

			// Activity log
			UserActivity.log(auth.getName(), "FIND_POOLED_TASK_INSTANCES", null, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("findPooledTaskInstances: {}", al);
		return al;
	}

	@Override
	public List<TaskInstance> findTaskInstances(String token, long processInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("findTaskInstances({}, {})", token, processInstanceId);
		List<TaskInstance> al = null;
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			al = CommonWorkflowModule.findTaskInstances(processInstanceId);

			// Activity log
			UserActivity.log(auth.getName(), "FIND_TASK_INSTANCES", "" + processInstanceId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("findTaskInstances: {}", al);
		return al;
	}

	@Override
	public void setTaskInstanceValues(String token, long taskInstanceId, String transitionName, List<FormElement> values)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("setTaskInstanceValues({}, {}, {}, {})",
				new Object[]{token, taskInstanceId, transitionName, values});
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.setTaskInstanceValues(taskInstanceId, transitionName, values);

			// Activity log
			UserActivity.log(auth.getName(), "SET_TASK_INSTANCE_VALUES", "" + taskInstanceId, null, transitionName);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("setTaskInstanceValues: void");
	}

	@Override
	public void addTaskInstanceComment(String token, long taskInstanceId, String message) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("addTaskInstanceComment({}, {}, {})", new Object[]{token, taskInstanceId, message});
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.addTaskInstanceComment(auth.getName(), taskInstanceId, message);

			// Activity log
			UserActivity.log(auth.getName(), "ADD_TASK_INSTANCE_COMMENT", "" + taskInstanceId, null, message);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("addTaskInstanceComment: void");
	}

	@Override
	public TaskInstance getTaskInstance(String token, long taskInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("getTaskInstance({}, {})", token, taskInstanceId);
		TaskInstance vo = null;
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			vo = CommonWorkflowModule.getTaskInstance(taskInstanceId);

			// Activity log
			UserActivity.log(auth.getName(), "GET_TASK_INSTANCE", "" + taskInstanceId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getTaskInstance: {}", vo);
		return vo;
	}

	@Override
	public void setTaskInstanceActorId(String token, long taskInstanceId, String actorId) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("setTaskInstanceActorId({}, {}, {})", new Object[]{token, taskInstanceId, actorId});
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.setTaskInstanceActorId(taskInstanceId, actorId);

			// Activity log
			UserActivity.log(auth.getName(), "SET_TASK_INSTANCE_ACTOR_ID", "" + taskInstanceId, null, actorId);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("setTaskInstanceActorId: void");
	}

	@Override
	public void addTaskInstanceVariable(String token, long taskInstanceId, String name, Object value)
			throws AccessDeniedException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("addTaskInstanceVariable({}, {}, {}, {})", new Object[]{token, taskInstanceId, name, value});
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.addTaskInstanceVariable(taskInstanceId, name, value);

			// Activity log
			UserActivity.log(auth.getName(), "ADD_TASK_INSTANCE_VARIABLE", "" + taskInstanceId, null, name + ", " + value);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("addTaskInstanceVariable: void");
	}

	@Override
	public void deleteTaskInstanceVariable(String token, long taskInstanceId, String name) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("deleteTaskInstanceVariable({}, {}, {})", new Object[]{token, taskInstanceId, name});
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.deleteTaskInstanceVariable(taskInstanceId, name);

			// Activity log
			UserActivity.log(auth.getName(), "DELETE_TASK_INSTANCE_VARIABLE", "" + taskInstanceId, null, name);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("deleteTaskInstanceVariable: void");
	}

	@Override
	public void startTaskInstance(String token, long taskInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("startTaskInstance({}, {})", token, taskInstanceId);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.startTaskInstance(taskInstanceId);

			// Activity log
			UserActivity.log(auth.getName(), "START_TASK_INSTANCE", "" + taskInstanceId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("startTaskInstance: void");
	}

	@Override
	public void endTaskInstance(String token, long taskInstanceId, String transitionName) throws AccessDeniedException,
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("endTaskInstance({}, {}, {})", new Object[]{token, taskInstanceId, transitionName});
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.endTaskInstance(taskInstanceId, transitionName);

			// Activity log
			UserActivity.log(auth.getName(), "END_TASK_INSTANCE", "" + taskInstanceId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("endTaskInstance: void");
	}

	@Override
	public void suspendTaskInstance(String token, long taskInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("suspendTaskInstance({}, {})", token, taskInstanceId);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.suspendTaskInstance(taskInstanceId);

			// Activity log
			UserActivity.log(auth.getName(), "SUSPEND_TASK_INSTANCE", "" + taskInstanceId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("suspendTaskInstance: void");
	}

	@Override
	public void resumeTaskInstance(String token, long taskInstanceId) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("resumeTaskInstance({}, {})", token, taskInstanceId);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.resumeTaskInstance(taskInstanceId);

			// Activity log
			UserActivity.log(auth.getName(), "RESUME_TASK_INSTANCE", "" + taskInstanceId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("resumeTaskInstance: void");
	}

	@Override
	public Token getToken(String token, long tokenId) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("getToken({}, {})", token, tokenId);
		Token vo = null;
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			vo = CommonWorkflowModule.getToken(tokenId);

			// Activity log
			UserActivity.log(auth.getName(), "GET_TOKEN", "" + tokenId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getToken: {}", vo);
		return vo;
	}

	@Override
	public void addTokenComment(String token, long tokenId, String message) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("addTokenComment({}, {}, {})", new Object[]{token, tokenId, message});
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.addTokenComment(auth.getName(), tokenId, message);

			// Activity log
			UserActivity.log(auth.getName(), "ADD_TOKEN_COMMENT", "" + tokenId, null, message);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("addTokenComment: void");
	}

	@Override
	public void suspendToken(String token, long tokenId) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("suspendToken({}, {})", token, tokenId);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.suspendToken(tokenId);

			// Activity log
			UserActivity.log(auth.getName(), "SUSPEND_TOKEN", "" + tokenId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("suspendToken: void");
	}

	@Override
	public void resumeToken(String token, long tokenId) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("resumeToken({}, {})", token, tokenId);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.resumeToken(tokenId);

			// Activity log
			UserActivity.log(auth.getName(), "RESUME_TOKEN", "" + tokenId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("resumeToken: void");
	}

	@Override
	public Token sendTokenSignal(String token, long tokenId, String transitionName) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("sendTokenSignal({}, {}, {})", new Object[]{token, tokenId, transitionName});
		Token vo = null;
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.sendTokenSignal(tokenId, transitionName);

			// Activity log
			UserActivity.log(auth.getName(), "SEND_TOKEN_SIGNAL", "" + tokenId, null, transitionName);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("sendTokenSignal: {}", vo);
		return vo;
	}

	@Override
	public void setTokenNode(String token, long tokenId, String nodeName) throws AccessDeniedException, RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("setTokenNode({}, {}, {})", new Object[]{token, tokenId, nodeName});
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.setTokenNode(tokenId, nodeName);

			// Activity log
			UserActivity.log(auth.getName(), "SEND_TOKEN_NODE", "" + tokenId, null, nodeName);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("setTokenNode: void");
	}

	@Override
	public void endToken(String token, long tokenId) throws AccessDeniedException, RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("endToken({}, {})", token, tokenId);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			CommonWorkflowModule.endToken(tokenId);

			// Activity log
			UserActivity.log(auth.getName(), "END_TOKEN", "" + tokenId, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("endToken: void");
	}
}
