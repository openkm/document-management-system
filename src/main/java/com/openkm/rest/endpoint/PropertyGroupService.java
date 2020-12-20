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

import com.openkm.bean.form.*;
import com.openkm.core.Config;
import com.openkm.core.MimeTypeConfig;
import com.openkm.module.ModuleManager;
import com.openkm.module.PropertyGroupModule;
import com.openkm.rest.GenericException;
import com.openkm.rest.util.*;
import com.openkm.util.FormUtils;
import com.openkm.ws.common.util.FormElementComplex;
import io.swagger.annotations.Api;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.*;

@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Api(description = "gropertyGroup-service", value = "gropertyGroup-service")
@Path("/propertyGroup")
public class PropertyGroupService {
	private static Logger log = LoggerFactory.getLogger(PropertyGroupService.class);

	@PUT
	@Path("/addGroup")
	public void addGroup(@QueryParam("nodeId") String nodeId, @QueryParam("grpName") String grpName) throws GenericException {
		try {
			log.debug("addGroup({}, {})", nodeId, grpName);
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			cm.addGroup(null, nodeId, grpName);
			log.debug("addGroup: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@DELETE
	@Path("/removeGroup")
	public void removeGroup(@QueryParam("nodeId") String nodeId, @QueryParam("grpName") String grpName) throws GenericException {
		try {
			log.debug("removeGroup({}, {})", nodeId, grpName);
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			cm.removeGroup(null, nodeId, grpName);
			log.debug("removeGroup: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getGroups")
	public PropertyGroupList getGroups(@QueryParam("nodeId") String nodeId) throws GenericException {
		try {
			log.debug("getGroups({})", nodeId);
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			PropertyGroupList pgl = new PropertyGroupList();
			pgl.getList().addAll(cm.getGroups(null, nodeId));
			log.debug("getGroups: {}", pgl);
			return pgl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getAllGroups")
	public PropertyGroupList getAllGroups() throws GenericException {
		try {
			log.debug("getAllGroups()");
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			PropertyGroupList pgl = new PropertyGroupList();
			pgl.getList().addAll(cm.getAllGroups(null));
			log.debug("getAllGroups: {} ", pgl);
			return pgl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getProperties")
	public FormElementComplexList getProperties(@QueryParam("nodeId") String nodeId, @QueryParam("grpName") String grpName)
			throws GenericException {
		try {
			log.debug("getProperties({}, {})", nodeId, grpName);
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			FormElementComplexList fecl = new FormElementComplexList();

			for (FormElement fe : cm.getProperties(null, nodeId, grpName)) {
				FormElementComplex fec = FormElementComplex.toFormElementComplex(fe);
				fecl.getList().add(fec);
			}

			log.debug("getProperties: {}", fecl);
			return fecl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getPropertyGroupForm")
	public FormElementComplexList getPropertyGroupForm(@QueryParam("grpName") String grpName)
			throws GenericException {
		try {
			log.debug("getPropertyGroupForm({})", grpName);
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			FormElementComplexList fecl = new FormElementComplexList();

			for (FormElement fe : cm.getPropertyGroupForm(null, grpName)) {
				FormElementComplex fec = FormElementComplex.toFormElementComplex(fe);
				fecl.getList().add(fec);
			}

			log.debug("getPropertyGroupForm: {}", fecl);
			return fecl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/setProperties")
	// The "properties" parameter comes in the POST request body (encoded as XML or JSON).
	public void setProperties(@QueryParam("nodeId") String nodeId, @QueryParam("grpName") String grpName, FormElementComplexList properties)
			throws GenericException {
		try {
			log.debug("setProperties({}, {}, {})", nodeId, grpName, properties);
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			List<FormElement> al = new ArrayList<>();

			for (FormElementComplex fec : properties.getList()) {
				al.add(FormElementComplex.toFormElement(fec));
			}

			cm.setProperties(null, nodeId, grpName, al);
			log.debug("setProperties: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/setPropertiesSimple")
	// The "properties" parameter comes in the POST request body (encoded as XML or JSON).
	public void setPropertiesSimple(@QueryParam("nodeId") String nodeId, @QueryParam("grpName") String grpName,
									SimplePropertyGroupList properties) throws GenericException {
		try {
			log.debug("setPropertiesSimple({}, {}, {})", nodeId, grpName, properties);
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			List<FormElement> al = new ArrayList<>();
			HashMap<String, String> mapProps = new HashMap<>();

			// Unmarshall
			for (SimplePropertyGroup spg : properties.getList()) {
				mapProps.put(spg.getName(), spg.getValue());
			}

			for (FormElement fe : cm.getProperties(null, nodeId, grpName)) {
				String value = mapProps.get(fe.getName());

				if (value != null) {
					if (fe instanceof Input) {
						((Input) fe).setValue(value);
					} else if (fe instanceof SuggestBox) {
						((SuggestBox) fe).setValue(value);
					} else if (fe instanceof TextArea) {
						((TextArea) fe).setValue(value);
					} else if (fe instanceof CheckBox) {
						((CheckBox) fe).setValue(Boolean.parseBoolean(value));
					} else if (fe instanceof Select) {
						Select sel = (Select) fe;

						for (Option opt : sel.getOptions()) {
							StringTokenizer st = new StringTokenizer(value, Config.LIST_SEPARATOR);

							while (st.hasMoreTokens()) {
								String optVal = st.nextToken().trim();
								if (opt.getValue().equals(optVal)) {
									opt.setSelected(true);
									break;
								} else {
									opt.setSelected(false);
								}
							}
						}
					}

					al.add(fe);
				}
			}

			cm.setProperties(null, nodeId, grpName, al);
			log.debug("setPropertiesSimple: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/hasGroup")
	@Produces(MimeTypeConfig.MIME_TEXT)
	public Boolean hasGroup(@QueryParam("nodeId") String nodeId, @QueryParam("grpName") String grpName) throws GenericException {
		try {
			log.debug("hasGroup({}, {})", nodeId, grpName);
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			boolean ret = cm.hasGroup(null, nodeId, grpName);
			log.debug("hasGroup: {}", ret);
			return ret;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getPropertiesSimple")
	public SimplePropertyGroupList getPropertiesSimple(@QueryParam("nodeId") String nodeId, @QueryParam("grpName") String grpName)
			throws GenericException {
		try {
			log.debug("getPropertiesSimple({}, {})", nodeId, grpName);
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			List<FormElement> formElements = cm.getProperties(null, nodeId, grpName);
			Map<String, String> props = new HashMap<>();
			FormUtils.fillMap(formElements, props);
			SimplePropertyGroupList propGroupList = new SimplePropertyGroupList();

			// Marshall
			for (Map.Entry<String, String> entry : props.entrySet()) {
				SimplePropertyGroup spg = new SimplePropertyGroup();
				spg.setName(entry.getKey());
				spg.setValue(entry.getValue());
				propGroupList.getList().add(spg);
			}

			log.debug("getPropertiesSimple: {}", propGroupList);
			return propGroupList;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getSuggestions")
	public SuggestionList getSuggestions(@QueryParam("nodeId") String nodeId, @QueryParam("grpName") String grpName,
										 @QueryParam("propName") String propName) throws GenericException {
		try {
			log.debug("getSuggestions()");
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			SuggestionList sl = new SuggestionList();
			sl.getList().addAll(cm.getSuggestions(null, nodeId, grpName, propName));
			log.debug("getSuggestions: {}", sl);
			return sl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@POST
	@Path("/registerDefinition")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void registerDefinition(List<Attachment> atts) throws GenericException {
		log.debug("registerDefinition({})", atts);
		InputStream is = null;

		try {
			for (Attachment att : atts) {
				if ("pgDef".equals(att.getContentDisposition().getParameter("name"))) {
					is = att.getDataHandler().getInputStream();
				}
			}

			String pgDef = IOUtils.toString(is);
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			cm.registerDefinition(null, pgDef);
			log.debug("registerDefinition: void");
		} catch (Exception e) {
			throw new GenericException(e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
}
