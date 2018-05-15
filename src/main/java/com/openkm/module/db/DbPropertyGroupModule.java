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

package com.openkm.module.db;

import com.google.gson.Gson;
import com.openkm.automation.AutomationException;
import com.openkm.automation.AutomationManager;
import com.openkm.automation.AutomationUtils;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.form.*;
import com.openkm.core.*;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.RegisteredPropertyGroupDAO;
import com.openkm.dao.bean.AutomationRule;
import com.openkm.dao.bean.NodeBase;
import com.openkm.dao.bean.RegisteredPropertyGroup;
import com.openkm.form.suggestion.Suggestion;
import com.openkm.form.suggestion.SuggestionException;
import com.openkm.module.PropertyGroupModule;
import com.openkm.module.db.base.BaseModule;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.FormUtils;
import com.openkm.util.PathUtils;
import com.openkm.util.SystemProfiling;
import com.openkm.util.UserActivity;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbPropertyGroupModule implements PropertyGroupModule {
	private static Logger log = LoggerFactory.getLogger(DbPropertyGroupModule.class);

	@Override
	public void addGroup(String token, String nodeId, String grpName) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException, AutomationException {
		log.debug("addGroup({}, {}, {})", new Object[]{token, nodeId, grpName});
		Authentication auth = null, oldAuth = null;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			NodeBase node = BaseModule.resolveNodeById(nodeId);

			// AUTOMATION - PRE
			Map<String, Object> env = new HashMap<>();
			env.put(AutomationUtils.NODE_UUID, node.getUuid());
			env.put(AutomationUtils.NODE_PATH, node.getPath());
			env.put(AutomationUtils.PARENT_PATH, PathUtils.getParent(node.getPath()));
			env.put(AutomationUtils.PARENT_UUID, node.getParent());
			env.put(AutomationUtils.PROPERTY_GROUP_NAME, grpName);
			AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_PROPERTY_GROUP_ADD, AutomationRule.AT_PRE, env);

			NodeBaseDAO.getInstance().addPropertyGroup(node.getUuid(), grpName);

			// AUTOMATION - POST
			AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_PROPERTY_GROUP_ADD, AutomationRule.AT_POST, env);

			// Activity log
			UserActivity.log(auth.getName(), "ADD_PROPERTY_GROUP", node.getUuid(), node.getPath(), grpName);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("addGroup: void");
	}

	@Override
	public void removeGroup(String token, String nodeId, String grpName) throws AccessDeniedException, PathNotFoundException,
			DatabaseException, AutomationException {
		log.debug("removeGroup({}, {}, {})", new Object[]{token, nodeId, grpName});
		Authentication auth = null, oldAuth = null;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			NodeBase node = BaseModule.resolveNodeById(nodeId);

			// AUTOMATION - PRE
			Map<String, Object> env = new HashMap<>();
			env.put(AutomationUtils.NODE_UUID, node.getUuid());
			env.put(AutomationUtils.NODE_PATH, node.getPath());
			env.put(AutomationUtils.PARENT_PATH, PathUtils.getParent(node.getPath()));
			env.put(AutomationUtils.PARENT_UUID, node.getParent());
			env.put(AutomationUtils.PROPERTY_GROUP_NAME, grpName);
			AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_PROPERTY_GROUP_REMOVE, AutomationRule.AT_PRE, env);

			NodeBaseDAO.getInstance().removePropertyGroup(node.getUuid(), grpName);

			// AUTOMATION - POST
			AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_PROPERTY_GROUP_REMOVE, AutomationRule.AT_POST, env);

			// Activity log
			UserActivity.log(auth.getName(), "REMOVE_PROPERTY_GROUP", node.getUuid(), node.getPath(), grpName);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("removeGroup: void");
	}

	@Override
	public List<PropertyGroup> getGroups(String token, String nodeId) throws IOException, ParseException, AccessDeniedException,
			PathNotFoundException, DatabaseException {
		log.debug("getGroups({})", token);
		ArrayList<PropertyGroup> ret = new ArrayList<>();
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;
		String nodeUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(nodeId)) {
				nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
			} else {
				nodeUuid = nodeId;
			}
			List<String> propGroups = NodeBaseDAO.getInstance().getPropertyGroups(nodeUuid);
			Map<PropertyGroup, List<FormElement>> pgf = FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML);

			// Only return registered property definitions
			for (String pgName : propGroups) {
				for (PropertyGroup pg : pgf.keySet()) {
					if (pg.getName().equals(pgName)) {
						ret.add(pg);
					}
				}
			}
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getGroups: {}", ret);
		return ret;
	}

	@Override
	public List<PropertyGroup> getAllGroups(String token) throws IOException, ParseException, AccessDeniedException, DatabaseException {
		log.debug("getAllGroups({})", token);
		ArrayList<PropertyGroup> ret = new ArrayList<>();
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			Map<PropertyGroup, List<FormElement>> pgf = FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML);

			// Only return registered property definitions
			for (RegisteredPropertyGroup rpg : RegisteredPropertyGroupDAO.getInstance().findAll()) {
				for (PropertyGroup pg : pgf.keySet()) {
					if (pg.getName().equals(rpg.getName())) {
						ret.add(pg);
					}
				}
			}
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getAllGroups: {}", ret);
		return ret;
	}

	@Override
	public List<FormElement> getProperties(String token, String nodeId, String grpName) throws IOException, ParseException,
			NoSuchGroupException, AccessDeniedException, PathNotFoundException, DatabaseException {
		log.debug("getProperties({}, {}, {})", new Object[]{token, nodeId, grpName});
		long begin = System.currentTimeMillis();
		Authentication auth = null, oldAuth = null;
		String nodePath = null;
		String nodeUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(nodeId)) {
				nodePath = nodeId;
				nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
			} else {
				nodePath = NodeBaseDAO.getInstance().getPathFromUuid(nodeId);
				nodeUuid = nodeId;
			}

			Map<PropertyGroup, List<FormElement>> pgfs = FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML);
			List<FormElement> pgf = FormUtils.getPropertyGroupForms(pgfs, grpName);
			List<FormElement> nodeProperties = new ArrayList<>();

			if (pgf != null) {
				Map<String, String> properties = NodeBaseDAO.getInstance().getProperties(nodeUuid, grpName);
				Gson gson = new Gson();

				for (FormElement fe : pgf) {
					String value = properties.get(fe.getName());

					if (fe instanceof Input) {
						((Input) fe).setValue(value == null ? "" : value);
					} else if (fe instanceof SuggestBox) {
						((SuggestBox) fe).setValue(value == null ? "" : value);
					} else if (fe instanceof CheckBox) {
						((CheckBox) fe).setValue(Boolean.parseBoolean(value));
					} else if (fe instanceof TextArea) {
						((TextArea) fe).setValue(value == null ? "" : value);
					} else if (fe instanceof Select) {
						if (value != null) {
							String[] values = gson.fromJson(value, String[].class);
							Select select = ((Select) fe);

							if (select.getType().equals(Select.TYPE_SIMPLE) && values.length > 1) {
								throw new ParseException("Inconsistent property definition: " + fe.getName());
							} else {
								for (Option opt : select.getOptions()) {
									for (int j = 0; j < values.length; j++) {
										if (opt.getValue().equals(values[j])) {
											if (select.getType().equals(Select.TYPE_SIMPLE)) {
												select.setValue(opt.getValue());
											} else {
												select.setValue(select.getValue().concat(opt.getValue()).concat(Config.LIST_SEPARATOR));
											}

											opt.setSelected(true);
										} else {
											// opt.setSelected(false);
										}
									}
								}

								if (select.getValue().endsWith(Config.LIST_SEPARATOR)) {
									select.setValue(select.getValue().substring(0, select.getValue().length() - 1));
								}
							}
						}
					} else if (fe instanceof Text) {
						// Ignore presentation property
					} else if (fe instanceof Separator) {
						// Ignore presentation property
					} else {
						throw new ParseException("Unknown property definition: " + fe.getName());
					}

					nodeProperties.add(fe);
				}
			} else {
				throw new NoSuchGroupException(grpName);
			}

			// Activity log
			UserActivity.log(auth.getName(), "GET_PROPERTY_GROUP_PROPERTIES", nodeUuid, nodePath, grpName + ", " + nodeProperties);

			SystemProfiling.log(nodePath + ", " + grpName, System.currentTimeMillis() - begin);
			log.trace("getProperties.Time: {}", System.currentTimeMillis() - begin);
			log.debug("getProperties: {}", nodeProperties);
			return nodeProperties;
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	/**
	 * Convenient method for GWTUtil.getExtraColumn()
	 */
	public FormElement getProperty(String token, String nodeId, String grpName, String propName) throws IOException, ParseException,
			NoSuchGroupException, AccessDeniedException, PathNotFoundException, DatabaseException {
		log.debug("getProperty({}, {}, {}, {})", new Object[]{token, nodeId, grpName, propName});
		long begin = System.currentTimeMillis();
		Authentication auth = null, oldAuth = null;
		String nodePath = null;
		String nodeUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(nodeId)) {
				nodePath = nodeId;
				nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
			} else {
				nodePath = NodeBaseDAO.getInstance().getPathFromUuid(nodeId);
				nodeUuid = nodeId;
			}

			Map<PropertyGroup, List<FormElement>> pgfs = FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML);
			Map<String, FormElement> pgfMap = FormUtils.getPropertyGroupFormsMap(pgfs, grpName);
			FormElement nodeProperty = null;

			if (pgfMap != null) {
				String value = NodeBaseDAO.getInstance().getProperty(nodeUuid, grpName, propName);
				FormElement fe = pgfMap.get(propName);
				Gson gson = new Gson();

				if (fe instanceof Input) {
					((Input) fe).setValue(value == null ? "" : value);
					nodeProperty = fe;
				} else if (fe instanceof SuggestBox) {
					((SuggestBox) fe).setValue(value == null ? "" : value);
					nodeProperty = fe;
				} else if (fe instanceof CheckBox) {
					((CheckBox) fe).setValue(Boolean.parseBoolean(value));
					nodeProperty = fe;
				} else if (fe instanceof TextArea) {
					((TextArea) fe).setValue(value == null ? "" : value);
					nodeProperty = fe;
				} else if (fe instanceof Select) {
					if (value != null) {
						String[] values = gson.fromJson(value, String[].class);
						Select select = ((Select) fe);

						if (select.getType().equals(Select.TYPE_SIMPLE) && values.length > 1) {
							throw new ParseException("Inconsistent property definition: " + fe.getName());
						} else {
							for (Option opt : select.getOptions()) {
								for (int j = 0; j < values.length; j++) {
									if (opt.getValue().equals(values[j])) {
										if (select.getType().equals(Select.TYPE_SIMPLE)) {
											select.setValue(opt.getValue());
										} else {
											select.setValue(select.getValue().concat(opt.getValue()).concat(Config.LIST_SEPARATOR));
										}

										opt.setSelected(true);
									} else {
										// opt.setSelected(false);
									}
								}
							}

							if (select.getValue().endsWith(Config.LIST_SEPARATOR)) {
								select.setValue(select.getValue().substring(0, select.getValue().length() - 1));
							}
						}
					}

					nodeProperty = fe;
				} else {
					throw new ParseException("Unknown property definition: " + fe.getName());
				}
			} else {
				throw new NoSuchGroupException(grpName);
			}

			// Activity log
			UserActivity.log(auth.getName(), "GET_PROPERTY_GROUP_PROPERTY", nodeUuid, nodePath, grpName + ", " + nodeProperty);

			SystemProfiling.log(nodePath + ", " + grpName, System.currentTimeMillis() - begin);
			log.trace("getProperty.Time: {}", System.currentTimeMillis() - begin);
			log.debug("getProperty: {}", nodeProperty);
			return nodeProperty;
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	@Override
	public void setProperties(String token, String nodeId, String grpName, List<FormElement> properties) throws ParseException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException, AutomationException {
		log.debug("setProperties({}, {}, {}, {})", new Object[]{token, nodeId, grpName, properties});
		Authentication auth = null, oldAuth = null;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			NodeBase node = BaseModule.resolveNodeById(nodeId);
			Map<String, String> nodProps = new HashMap<>();
			Gson gson = new Gson();

			// Now we can safely set all property values.
			for (FormElement fe : properties) {
				if (fe instanceof Input) {
					nodProps.put(fe.getName(), ((Input) fe).getValue());
				} else if (fe instanceof SuggestBox) {
					nodProps.put(fe.getName(), ((SuggestBox) fe).getValue());
				} else if (fe instanceof CheckBox) {
					nodProps.put(fe.getName(), Boolean.toString(((CheckBox) fe).getValue()));
				} else if (fe instanceof TextArea) {
					nodProps.put(fe.getName(), ((TextArea) fe).getValue());
				} else if (fe instanceof Select) {
					List<String> tmp = new ArrayList<>();

					for (Option opt : ((Select) fe).getOptions()) {
						if (opt.isSelected()) {
							tmp.add(opt.getValue());
						}
					}

					if (((Select) fe).getType().equals(Select.TYPE_SIMPLE) && tmp.size() > 1) {
						throw new ParseException("Inconsistent property definition: " + fe.getName());
					} else {
						String value = gson.toJson(tmp);
						nodProps.put(fe.getName(), value);
					}
				} else if (fe instanceof Text) {
					// Ignore presentation property
				} else if (fe instanceof Separator) {
					// Ignore presentation property
				} else {
					log.warn("Unknown property definition: {}", fe.getName());
					throw new ParseException("Unknown property definition: " + fe.getName());
				}
			}

			// AUTOMATION - PRE
			Map<String, Object> env = new HashMap<>();
			env.put(AutomationUtils.NODE_UUID, node.getUuid());
			env.put(AutomationUtils.NODE_PATH, node.getPath());
			env.put(AutomationUtils.PARENT_PATH, PathUtils.getParent(node.getPath()));
			env.put(AutomationUtils.PARENT_UUID, node.getParent());
			env.put(AutomationUtils.PROPERTY_GROUP_NAME, grpName);
			env.put(AutomationUtils.PROPERTY_GROUP_PROPERTIES, nodProps);
			AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_PROPERTY_GROUP_SET, AutomationRule.AT_PRE, env);

			NodeBaseDAO.getInstance().setProperties(node.getUuid(), grpName, nodProps);

			// AUTOMATION - POST
			AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_PROPERTY_GROUP_SET, AutomationRule.AT_POST, env);

			// Activity log
			UserActivity.log(auth.getName(), "SET_PROPERTY_GROUP_PROPERTIES", node.getUuid(), node.getPath(), grpName + ", " + properties);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("setProperties: void");
	}

	@Override
	public List<FormElement> getPropertyGroupForm(String token, String grpName) throws ParseException, IOException, AccessDeniedException,
			DatabaseException {
		log.debug("getPropertyGroupForm({}, {})", token, grpName);
		List<FormElement> ret = new ArrayList<>();
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			RegisteredPropertyGroup rpg = RegisteredPropertyGroupDAO.getInstance().findByPk(grpName);
			Map<PropertyGroup, List<FormElement>> pgf = FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML);
			List<FormElement> tmp = FormUtils.getPropertyGroupForms(pgf, grpName);

			// Only return registered property definitions
			for (FormElement fe : tmp) {
				for (String pgName : rpg.getProperties().keySet()) {
					if (fe.getName().equals(pgName)) {
						ret.add(fe);
					}
				}
			}
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getPropertyGroupForm: {}", ret);
		return ret;
	}

	@Override
	public boolean hasGroup(String token, String nodeId, String grpName) throws AccessDeniedException, PathNotFoundException,
			DatabaseException {
		log.debug("hasGroup({}, {}, {})", new Object[]{token, nodeId, grpName});
		boolean ret = false;
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;
		String nodeUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(nodeId)) {
				nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
			} else {
				nodeUuid = nodeId;
			}

			List<String> propGroups = NodeBaseDAO.getInstance().getPropertyGroups(nodeUuid);

			if (propGroups.contains(grpName)) {
				ret = true;
			}
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("hasGroup: {}", ret);
		return ret;
	}

	@Override
	public List<String> getSuggestions(String token, String nodeId, String grpName, String propName) throws AccessDeniedException,
			PathNotFoundException, IOException, ParseException, NoSuchGroupException, SuggestionException, DatabaseException {
		log.debug("getSuggestions({}, {}, {}, {})", new Object[]{token, nodeId, grpName, propName});
		long begin = System.currentTimeMillis();
		List<String> list = new ArrayList<>();
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;
		String nodePath = null;
		String nodeUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(nodeId)) {
				nodePath = nodeId;
				nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
			} else {
				nodePath = NodeBaseDAO.getInstance().getPathFromUuid(nodeId);
				nodeUuid = nodeId;
			}

			Map<PropertyGroup, List<FormElement>> pgfs = FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML);
			Map<String, FormElement> pgfMap = FormUtils.getPropertyGroupFormsMap(pgfs, grpName);

			if (pgfMap != null) {
				FormElement fe = pgfMap.get(propName);

				if (fe instanceof Select) {
					Select sel = (Select) fe;

					if (sel.getSuggestion() != null && !sel.getSuggestion().isEmpty()) {
						try {
							Object obj = Class.forName(sel.getSuggestion()).newInstance();
							list = ((Suggestion) obj).getSuggestions(nodeUuid, nodePath, sel);
						} catch (ClassNotFoundException e) {
							log.warn("ClassNotFoundException: " + sel.getSuggestion(), e);
						} catch (InstantiationException e) {
							log.warn("InstantiationException: " + sel.getSuggestion(), e);
						} catch (IllegalAccessException e) {
							log.warn("IllegalAccessException: " + sel.getSuggestion(), e);
						}
					}
				} else {
					throw new ParseException("Unknown property definition: " + fe.getName());
				}
			} else {
				throw new NoSuchGroupException(grpName);
			}
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(nodePath + ", " + propName, System.currentTimeMillis() - begin);
		log.trace("getSuggestions.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getSuggestions: {}", list);
		return list;
	}

	@Override
	public void registerDefinition(String token, String pgDef) throws ParseException, AccessDeniedException, DatabaseException, IOException {
		log.debug("registerDefinition({}, {})", new Object[]{token, pgDef});
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;
		long begin = System.currentTimeMillis();

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			FileUtils.writeStringToFile(new File(Config.PROPERTY_GROUPS_XML), pgDef, "UTF-8");
			DbRepositoryModule.registerPropertyGroups(Config.PROPERTY_GROUPS_XML);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(pgDef, System.currentTimeMillis() - begin);
		log.trace("registerDefinition.Time: {}", System.currentTimeMillis() - begin);
		log.debug("registerDefinition: void");
	}
}
