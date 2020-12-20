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

package com.openkm.rest.endpoint;

import com.openkm.bean.form.FormElement;
import com.openkm.bean.workflow.ProcessDefinition;
import com.openkm.bean.workflow.ProcessInstance;
import com.openkm.bean.workflow.TaskInstance;
import com.openkm.module.ModuleManager;
import com.openkm.module.WorkflowModule;
import com.openkm.rest.GenericException;
import com.openkm.rest.util.*;
import com.openkm.ws.common.util.FormElementComplex;
import io.swagger.annotations.Api;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Api(description = "workflow-service", value = "workflow-service")
@Path("/workflow")
public class WorkflowService {
	private static final Logger log = LoggerFactory.getLogger(WorkflowService.class);

	@POST
	@Path("/registerProcessDefinition")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// The "content" parameter comes in the POST request body (encoded as XML or JSON).
	public void registerProcessDefinition(List<Attachment> atts) throws GenericException {
		try {
			log.debug("registerProcessDefinition({})", atts);
			InputStream is = null;

			for (Attachment att : atts) {
				if ("content".equals(att.getContentDisposition().getParameter("name"))) {
					is = att.getDataHandler().getInputStream();
				}
			}

			WorkflowModule wm = ModuleManager.getWorkflowModule();
			wm.registerProcessDefinition(null, is);
			IOUtils.closeQuietly(is);
			log.debug("registerProcessDefinition: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@DELETE
	@Path("/deleteProcessDefinition")
	public void deleteProcessDefinition(@QueryParam("pdId") long pdId) throws GenericException {
		try {
			log.debug("deleteProcessDefinition({})", pdId);
			WorkflowModule wm = ModuleManager.getWorkflowModule();
			wm.deleteProcessDefinition(null, pdId);
			log.debug("deleteProcessDefinition: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getProcessDefinition")
	public ProcessDefinition getProcessDefinition(@QueryParam("pdId") long pdId) throws GenericException {
		try {
			log.debug("getProcessDefinition({})", pdId);
			WorkflowModule wm = ModuleManager.getWorkflowModule();
			ProcessDefinition result = wm.getProcessDefinition(null, pdId);
			log.debug("getProcessDefinition: {}", result);
			return result;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/runProcessDefinition")
	// The "values" parameter comes in the POST request body (encoded as XML or JSON).
	public ProcessInstance runProcessDefinition(@QueryParam("pdId") long pdId, @QueryParam("uuid") String uuid,
			FormElementComplexList values) throws GenericException {
		try {
			log.debug("runProcessDefinition({}, {}, {})", pdId, uuid, values);
			WorkflowModule wm = ModuleManager.getWorkflowModule();
			List<FormElement> al = new ArrayList<>();

			for (FormElementComplex fec : values.getList()) {
				al.add(FormElementComplex.toFormElement(fec));
			}

			ProcessInstance result = wm.runProcessDefinition(null, pdId, uuid, al);
			log.debug("runProcessDefinition: {}", result);
			return result;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/findProcessInstances")
	public ProcessInstanceList findProcessInstances(@QueryParam("pdId") long pdId) throws GenericException {
		try {
			log.debug("findProcessInstances({})", pdId);
			WorkflowModule wm = ModuleManager.getWorkflowModule();
			List<ProcessInstance> col = wm.findProcessInstances(null, pdId);
			ProcessInstanceList pil = new ProcessInstanceList();
			pil.getList().addAll(col);
			log.debug("findProcessInstances: {}", pil);
			return pil;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/findAllProcessDefinitions")
	public ProcessDefinitionList findAllProcessDefinitions() throws GenericException {
		try {
			log.debug("findAllProcessDefinitions()");
			WorkflowModule wm = ModuleManager.getWorkflowModule();
			List<ProcessDefinition> col = wm.findAllProcessDefinitions(null);
			ProcessDefinitionList pdl = new ProcessDefinitionList();
			pdl.getList().addAll(col);
			log.debug("findAllProcessDefinitions: {}", pdl);
			return pdl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/findLatestProcessDefinitions")
	public ProcessDefinitionList findLatestProcessDefinitions() throws GenericException {
		try {
			log.debug("findLatestProcessDefinitions()");
			WorkflowModule wm = ModuleManager.getWorkflowModule();
			List<ProcessDefinition> col = wm.findLatestProcessDefinitions(null);
			ProcessDefinitionList pdl = new ProcessDefinitionList();
			pdl.getList().addAll(col);
			log.debug("findLatestProcessDefinitions: {}", pdl);
			return pdl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/findLastProcessDefinition")
	public ProcessDefinition findLastProcessDefinition(@QueryParam("name") String name) throws GenericException {
		try {
			log.debug("findLastProcessDefinition({})", name);
			WorkflowModule wm = ModuleManager.getWorkflowModule();
			ProcessDefinition result = wm.findLastProcessDefinition(null, name);
			log.debug("findLastProcessDefinition: {}", result);
			return result;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getProcessInstance")
	public ProcessInstance getProcessInstance(@QueryParam("piId") long piId) throws GenericException {
		try {
			log.debug("getProcessInstance({})", piId);
			WorkflowModule wm = ModuleManager.getWorkflowModule();
			ProcessInstance result = wm.getProcessInstance(null, piId);
			log.debug("getProcessInstance: {}", result);
			return result;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/findUserTaskInstances")
	public TaskInstanceList findUserTaskInstances() throws GenericException {
		try {
			log.debug("findUserTaskInstances()");
			WorkflowModule wm = ModuleManager.getWorkflowModule();
			List<TaskInstance> col = wm.findUserTaskInstances(null);
			TaskInstanceList til = new TaskInstanceList();
			til.getList().addAll(col);
			log.debug("findUserTaskInstances: {}", til);
			return til;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/findTaskInstances")
	public TaskInstanceList findTaskInstances(@QueryParam("piId") long piId) throws GenericException {
		try {
			log.debug("findTaskInstances({})", piId);
			WorkflowModule wm = ModuleManager.getWorkflowModule();
			List<TaskInstance> col = wm.findTaskInstances(null, piId);
			TaskInstanceList til = new TaskInstanceList();
			til.getList().addAll(col);
			log.debug("findTaskInstances: {}", til);
			return til;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/setTaskInstanceValues")
	// The "values" parameter comes in the POST request body (encoded as XML or JSON).
	public void setTaskInstanceValues(@QueryParam("tiId") long tiId, @QueryParam("transName") String transName,
			FormElementComplexList values) throws GenericException {
		try {
			log.debug("setTaskInstanceValues({}, {}, {})", tiId, transName, values);
			WorkflowModule wm = ModuleManager.getWorkflowModule();
			List<FormElement> al = new ArrayList<>();

			for (FormElementComplex fec : values.getList()) {
				al.add(FormElementComplex.toFormElement(fec));
			}

			wm.setTaskInstanceValues(null, tiId, transName, al);
			log.debug("setTaskInstanceValues: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getTaskInstance")
	public TaskInstance getTaskInstance(@QueryParam("tiId") long tiId) throws GenericException {
		try {
			log.debug("getTaskInstance({})", tiId);
			WorkflowModule wm = ModuleManager.getWorkflowModule();
			TaskInstance result = wm.getTaskInstance(null, tiId);
			log.debug("getTaskInstance: {}", result);
			return result;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/setTaskInstanceActorId")
	public void setTaskInstanceActorId(@QueryParam("tiId") long tiId, @QueryParam("actorId") String actorId)
			throws GenericException {
		try {
			log.debug("setTaskInstanceActorId({}, {})", tiId, actorId);
			WorkflowModule wm = ModuleManager.getWorkflowModule();
			wm.setTaskInstanceActorId(null, tiId, actorId);
			log.debug("setTaskInstanceActorId: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/startTaskInstance")
	public void startTaskInstance(@QueryParam("tiId") long tiId) throws GenericException {
		try {
			log.debug("startTaskInstance({})", tiId);
			WorkflowModule wm = ModuleManager.getWorkflowModule();
			wm.startTaskInstance(null, tiId);
			log.debug("startTaskInstance: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/endTaskInstance")
	public void endTaskInstance(@QueryParam("tiId") long tiId, @QueryParam("transName") String transName)
			throws GenericException {
		try {
			log.debug("endTaskInstance({}, {})", tiId, transName);
			WorkflowModule wm = ModuleManager.getWorkflowModule();
			wm.endTaskInstance(null, tiId, transName);
			log.debug("endTaskInstance: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getProcessDefinitionForms")
	public ProcessDefinitionFormsList getProcessDefinitionForms(@QueryParam("pdId") long pdId) throws GenericException {
		try {
			log.debug("getProcessDefinitionForms({})", pdId);
			WorkflowModule wm = ModuleManager.getWorkflowModule();
			Map<String, List<FormElement>> list = wm.getProcessDefinitionForms(null, pdId);
			ProcessDefinitionFormsList pdfl = new ProcessDefinitionFormsList();

			for (String key : list.keySet()) {
				ProcessDefinitionForm pdf = new ProcessDefinitionForm();
				pdf.setKey(key);
				List<FormElement> formElements = list.get(key);
				for (FormElement fe : formElements) {
					FormElementComplex fec = FormElementComplex.toFormElementComplex(fe);
					pdf.getFormElementsComplex().add(fec);
				}
				pdfl.getList().add(pdf);
			}

			log.debug("getProcessDefinitionForms:" + pdfl);
			return pdfl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
}
