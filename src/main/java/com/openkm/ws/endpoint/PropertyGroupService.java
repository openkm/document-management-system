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

import com.openkm.automation.AutomationException;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.form.*;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.module.ModuleManager;
import com.openkm.module.PropertyGroupModule;
import com.openkm.ws.util.FormElementComplex;
import com.openkm.ws.util.StringPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

@WebService(name = "OKMPropertyGroup", serviceName = "OKMPropertyGroup", targetNamespace = "http://ws.openkm.com")
public class PropertyGroupService {
	private static Logger log = LoggerFactory.getLogger(PropertyGroupService.class);

	@WebMethod
	public void addGroup(@WebParam(name = "token") String token, @WebParam(name = "nodePath") String nodePath,
	                     @WebParam(name = "grpName") String grpName) throws NoSuchGroupException, LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException, ExtensionException, AutomationException {
		log.debug("addGroup({}, {}, {})", new Object[]{token, nodePath, grpName});
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		cm.addGroup(token, nodePath, grpName);
		log.debug("addGroup: void");
	}

	@WebMethod
	public void removeGroup(@WebParam(name = "token") String token, @WebParam(name = "nodePath") String nodePath,
	                        @WebParam(name = "grpName") String grpName) throws AccessDeniedException, NoSuchGroupException, LockException,
			PathNotFoundException, RepositoryException, DatabaseException, ExtensionException, AutomationException {
		log.debug("removeGroup({}, {}, {})", new Object[]{token, nodePath, grpName});
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		cm.removeGroup(token, nodePath, grpName);
		log.debug("removeGroup: void");
	}

	@WebMethod
	public PropertyGroup[] getGroups(@WebParam(name = "token") String token, @WebParam(name = "nodePath") String nodePath)
			throws IOException, ParseException, AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getGroups({}, {})", token, nodePath);
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		List<PropertyGroup> col = cm.getGroups(token, nodePath);
		PropertyGroup[] result = col.toArray(new PropertyGroup[col.size()]);
		log.debug("getGroups: {}", result);
		return result;
	}

	@WebMethod
	public PropertyGroup[] getAllGroups(@WebParam(name = "token") String token) throws IOException, ParseException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getAllGroups({})", token);
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		List<PropertyGroup> col = cm.getAllGroups(token);
		PropertyGroup[] result = col.toArray(new PropertyGroup[col.size()]);
		log.debug("getAllGroups: {} ", result);
		return result;
	}

	@WebMethod
	public FormElementComplex[] getProperties(@WebParam(name = "token") String token,
	                                          @WebParam(name = "nodePath") String nodePath, @WebParam(name = "grpName") String grpName) throws IOException,
			ParseException, NoSuchGroupException, AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getProperties({}, {}, {})", new Object[]{token, nodePath, grpName});
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		List<FormElement> col = cm.getProperties(token, nodePath, grpName);
		FormElementComplex[] result = new FormElementComplex[col.size()];

		for (int i = 0; i < col.size(); i++) {
			result[i] = FormElementComplex.toFormElementComplex(col.get(i));
		}

		log.debug("getProperties: {}", result);
		return result;
	}

	@WebMethod
	public FormElementComplex[] getPropertyGroupForm(@WebParam(name = "token") String token, @WebParam(name = "grpName") String grpName)
			throws IOException, ParseException, NoSuchGroupException, AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("getPropertyGroupForm({}, {})", new Object[]{token, grpName});
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		List<FormElement> col = cm.getPropertyGroupForm(token, grpName);
		FormElementComplex[] result = new FormElementComplex[col.size()];

		for (int i = 0; i < col.size(); i++) {
			result[i] = FormElementComplex.toFormElementComplex(col.get(i));
		}

		log.debug("getPropertyGroupForm: {}", result);
		return result;
	}

	@WebMethod
	public void setProperties(@WebParam(name = "token") String token, @WebParam(name = "nodePath") String nodePath,
	                          @WebParam(name = "grpName") String grpName, @WebParam(name = "properties") FormElementComplex[] properties)
			throws IOException, ParseException, NoSuchPropertyException, NoSuchGroupException, LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException, ExtensionException,
			AutomationException {
		log.debug("setProperties({}, {}, {}, {})", new Object[]{token, nodePath, grpName, properties});
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		List<FormElement> al = new ArrayList<FormElement>();

		for (int i = 0; i < properties.length; i++) {
			al.add(FormElementComplex.toFormElement(properties[i]));
		}

		cm.setProperties(token, nodePath, grpName, al);
		log.debug("setProperties: void");
	}

	@WebMethod
	public void setPropertiesSimple(@WebParam(name = "token") String token, @WebParam(name = "nodePath") String nodePath,
	                                @WebParam(name = "grpName") String grpName, @WebParam(name = "properties") StringPair[] properties)
			throws IOException, ParseException, NoSuchPropertyException, NoSuchGroupException, LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException, ExtensionException,
			AutomationException {
		log.debug("setPropertiesSimple({}, {}, {}, {})", new Object[]{token, nodePath, grpName, properties});
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		List<FormElement> al = new ArrayList<FormElement>();
		HashMap<String, String> mapProps = new HashMap<String, String>();

		// Unmarshall HashMap
		for (StringPair sp : properties) {
			mapProps.put(sp.getKey(), sp.getValue());
		}

		for (FormElement fe : cm.getProperties(token, nodePath, grpName)) {
			String value = mapProps.get(fe.getName());

			if (value != null) {
				if (fe instanceof Input) {
					((Input) fe).setValue(value);
				} else if (fe instanceof SuggestBox) {
					((SuggestBox) fe).setValue(value);
				} else if (fe instanceof TextArea) {
					((TextArea) fe).setValue(value);
				} else if (fe instanceof CheckBox) {
					((CheckBox) fe).setValue(Boolean.valueOf(value));
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

		cm.setProperties(token, nodePath, grpName, al);
		log.debug("setPropertiesSimple: void");
	}

	@WebMethod
	public boolean hasGroup(@WebParam(name = "token") String token, @WebParam(name = "nodePath") String nodePath,
	                        @WebParam(name = "grpName") String grpName) throws IOException, ParseException, AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("hasGroup({}, {}, {})", new Object[]{token, nodePath, grpName});
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		boolean ret = cm.hasGroup(token, nodePath, grpName);
		log.debug("hasGroup: {}", ret);
		return ret;
	}
}
