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

package com.openkm.util;

import com.openkm.api.OKMPropertyGroup;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.form.FormElement;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.ParseException;
import com.openkm.core.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PropertyGroupUtils
 *
 * @author jllort
 */
public class PropertyGroupUtils {
	private static Logger log = LoggerFactory.getLogger(PropertyGroupUtils.class);

	/**
	 * getAllGroupsProperties
	 */
	public static List<String> getAllGroupsProperties() throws IOException, ParseException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getAllGroupsProperties()");
		List<String> pgProperties = new ArrayList<String>();

		for (PropertyGroup pgrp : OKMPropertyGroup.getInstance().getAllGroups(null)) {
			for (FormElement fe : OKMPropertyGroup.getInstance().getPropertyGroupForm(null, pgrp.getName())) {
				pgProperties.add(fe.getName());
			}
		}

		log.debug("getAllGroupsProperties: " + pgProperties);
		return pgProperties;
	}

	/**
	 * getRelatedGroupsFromProperties
	 */
	public static List<String> getRelatedGroupsFromProperties(List<String> properties) {
		List<String> groups = new ArrayList<String>();

		for (String property : properties) {
			if (isValidPropertyName(property)) {
				String group = property.replaceAll("okp:", "okg:").substring(0, property.indexOf("."));
				if (!groups.contains(group)) {
					groups.add(group);
				}
			}
		}

		return groups;
	}

	/**
	 * isValidPropertyName
	 */
	public static boolean isValidPropertyName(String property) {
		return (property.length() > 0 && property.startsWith("okp:") && property.indexOf(".") > 0);
	}

	/**
	 * propertyNameContainsGroup
	 */
	public static boolean propertyNameContainsGroup(String grpName, String property) {
		return property.startsWith(grpName.replaceAll("okg:", "okp:"));
	}
}