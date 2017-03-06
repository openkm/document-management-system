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

package com.openkm.servlet.frontend;

import com.openkm.api.OKMPropertyGroup;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.Repository;
import com.openkm.bean.form.FormElement;
import com.openkm.core.*;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.bean.form.GWTOption;
import com.openkm.frontend.client.bean.form.GWTSelect;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMPropertyGroupService;
import com.openkm.servlet.frontend.util.PropertyGroupComparator;
import com.openkm.util.GWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PropertyGroup Servlet Class
 */
public class PropertyGroupServlet extends OKMRemoteServiceServlet implements OKMPropertyGroupService {
	private static Logger log = LoggerFactory.getLogger(PropertyGroupServlet.class);
	private static final long serialVersionUID = 2638205115826644606L;

	@Override
	public List<GWTPropertyGroup> getAllGroups() throws OKMException {
		log.debug("getAllGroups()");
		List<GWTPropertyGroup> groupList = new ArrayList<GWTPropertyGroup>();
		updateSessionManager();

		try {
			for (PropertyGroup pg : OKMPropertyGroup.getInstance().getAllGroups(null)) {
				if (pg.isVisible()) {
					GWTPropertyGroup group = GWTUtil.copy(pg);
					groupList.add(group);
				}
			}
			Collections.sort(groupList, PropertyGroupComparator.getInstance(getLanguage()));
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getAllGroups: {}", groupList);
		return groupList;
	}

	@Override
	public List<GWTPropertyGroup> getAllGroups(String path) throws OKMException {
		log.debug("getAllGroups({})", path);
		List<GWTPropertyGroup> groupList = new ArrayList<GWTPropertyGroup>();
		updateSessionManager();

		try {
			List<GWTPropertyGroup> actualGroupsList = getGroups(path);
			for (PropertyGroup pg : OKMPropertyGroup.getInstance().getAllGroups(null)) {
				if (pg.isVisible()) {
					GWTPropertyGroup group = GWTUtil.copy(pg);
					groupList.add(group);
				}
			}

			// Purge from list values that are assigned to document
			if (!actualGroupsList.isEmpty()) {
				for (GWTPropertyGroup group : actualGroupsList) {
					for (GWTPropertyGroup groupListElement : groupList) {
						if (groupListElement.getName().equals(group.getName())) {
							groupList.remove(groupListElement);
							break;
						}
					}
				}
			}
			Collections.sort(groupList, PropertyGroupComparator.getInstance(getLanguage()));
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (OKMException e) {
			log.error(e.getMessage(), e);
			throw e;
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getAllGroups: {}", groupList);
		return groupList;
	}

	@Override
	public void addGroup(String path, String grpName) throws OKMException {
		log.debug("addGroup({}, {})", path, grpName);
		updateSessionManager();

		try {
			OKMPropertyGroup.getInstance().addGroup(null, path, grpName);
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("addGroup: void");
	}

	@Override
	public List<GWTPropertyGroup> getGroups(String path) throws OKMException {
		log.debug("getGroups({})", path);
		List<GWTPropertyGroup> groupList = new ArrayList<GWTPropertyGroup>();
		updateSessionManager();

		try {
			if (!path.startsWith("/" + Repository.METADATA)) {
				for (PropertyGroup pg : OKMPropertyGroup.getInstance().getGroups(null, path)) {
					if (pg.isVisible()) {
						GWTPropertyGroup group = GWTUtil.copy(pg);
						groupList.add(group);
					}
				}
			}

			Collections.sort(groupList, PropertyGroupComparator.getInstance(getLanguage()));
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getGroups: {}", groupList);
		return groupList;
	}

	@Override
	public List<GWTFormElement> getProperties(String path, String grpName, boolean suggestion) throws OKMException {
		log.debug("getProperties({}, {}, {})", new Object[]{path, grpName, suggestion});
		List<GWTFormElement> properties = new ArrayList<GWTFormElement>();
		updateSessionManager();

		try {
			for (FormElement formElement : OKMPropertyGroup.getInstance().getProperties(null, path, grpName)) {
				GWTFormElement gWTFormElement = GWTUtil.copy(formElement);
				if (gWTFormElement instanceof GWTSelect) {
					GWTSelect select = (GWTSelect) gWTFormElement;
					if (suggestion && !select.getSuggestion().equals("")) {
						for (String suggestedValue : OKMPropertyGroup.getInstance().getSuggestions(null, path, grpName, select.getName())) {
							for (GWTOption option : select.getOptions()) {
								if (option.getValue().equals(suggestedValue)) {
									option.setSuggested(true);
								}
							}
						}
					}
				}
				properties.add(gWTFormElement);
			}
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getProperties: {}", properties);
		return properties;
	}

	@Override
	public List<GWTFormElement> getPropertyGroupForm(String grpName) throws OKMException {
		log.debug("getPropertyGroupForm({})", grpName);
		List<GWTFormElement> gwtProperties = new ArrayList<GWTFormElement>();
		updateSessionManager();

		try {
			for (FormElement formElement : OKMPropertyGroup.getInstance().getPropertyGroupForm(null, grpName)) {
				gwtProperties.add(GWTUtil.copy(formElement));
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getPropertyGroupForm: {}", gwtProperties);
		return gwtProperties;
	}

	@Override
	public List<GWTFormElement> getPropertyGroupForm(String grpName, String path, boolean suggestion) throws OKMException {
		log.debug("getPropertyGroupForm({},{},{})", new Object[]{grpName, path, suggestion});
		List<GWTFormElement> gwtProperties = new ArrayList<GWTFormElement>();
		updateSessionManager();

		try {
			for (FormElement formElement : OKMPropertyGroup.getInstance().getPropertyGroupForm(null, grpName)) {
				GWTFormElement gWTFormElement = GWTUtil.copy(formElement);
				if (gWTFormElement instanceof GWTSelect) {
					GWTSelect select = (GWTSelect) gWTFormElement;
					if (suggestion && !select.getSuggestion().equals("")) {
						for (String suggestedValue : OKMPropertyGroup.getInstance().getSuggestions(null, path, grpName, select.getName())) {
							for (GWTOption option : select.getOptions()) {
								if (option.getValue().equals(suggestedValue)) {
									option.setSuggested(true);
								}
							}
						}
					}
				}
				gwtProperties.add(gWTFormElement);
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getPropertyGroupForm: {}", gwtProperties);
		return gwtProperties;
	}

	@Override
	public void setProperties(String path, String grpName, List<GWTFormElement> formProperties) throws OKMException {
		log.debug("setProperties({}, {}, {})", new Object[]{path, grpName, formProperties});
		updateSessionManager();

		try {
			List<FormElement> properties = new ArrayList<FormElement>();

			for (GWTFormElement gWTformElement : formProperties) {
				properties.add(GWTUtil.copy(gWTformElement));
			}

			OKMPropertyGroup.getInstance().setProperties(null, path, grpName, properties);
		} catch (NoSuchPropertyException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_NoSuchProperty), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("setProperties: void");
	}

	@Override
	public void removeGroup(String path, String grpName) throws OKMException {
		log.debug("removeGroup({}, {})", path, grpName);
		updateSessionManager();

		try {
			OKMPropertyGroup.getInstance().removeGroup(null, path, grpName);
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("removeGroup: void");
	}
}
