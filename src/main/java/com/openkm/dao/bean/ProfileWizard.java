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

package com.openkm.dao.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ProfileWizard implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean keywordsEnabled;
	private boolean categoriesEnabled;
	private Set<String> propertyGroups = new HashSet<String>();
	private Set<String> workflows = new HashSet<String>();

	public boolean isKeywordsEnabled() {
		return keywordsEnabled;
	}

	public void setKeywordsEnabled(boolean keywordsEnabled) {
		this.keywordsEnabled = keywordsEnabled;
	}

	public boolean isCategoriesEnabled() {
		return categoriesEnabled;
	}

	public void setCategoriesEnabled(boolean categoriesEnabled) {
		this.categoriesEnabled = categoriesEnabled;
	}

	public Set<String> getPropertyGroups() {
		return propertyGroups;
	}

	public void setPropertyGroups(Set<String> propertyGroups) {
		this.propertyGroups = propertyGroups;
	}

	public Set<String> getWorkflows() {
		return workflows;
	}

	public void setWorkflows(Set<String> workflows) {
		this.workflows = workflows;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("keywordsEnabled=");
		sb.append(keywordsEnabled);
		sb.append(", categoriesEnabled=");
		sb.append(categoriesEnabled);
		sb.append(", propertyGroups=");
		sb.append(propertyGroups);
		sb.append(", workflows=");
		sb.append(workflows);
		sb.append("}");
		return sb.toString();
	}
}
