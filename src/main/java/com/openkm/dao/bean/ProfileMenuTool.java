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

public class ProfileMenuTool implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean languagesVisible;
	private boolean skinVisible;
	private boolean debugVisible;
	private boolean administrationVisible;
	private boolean preferencesVisible;
	private boolean omrVisible;
	private boolean convertVisible;

	public boolean isLanguagesVisible() {
		return languagesVisible;
	}

	public void setLanguagesVisible(boolean languagesVisible) {
		this.languagesVisible = languagesVisible;
	}

	public boolean isSkinVisible() {
		return skinVisible;
	}

	public void setSkinVisible(boolean skinVisible) {
		this.skinVisible = skinVisible;
	}

	public boolean isDebugVisible() {
		return debugVisible;
	}

	public void setDebugVisible(boolean debugVisible) {
		this.debugVisible = debugVisible;
	}

	public boolean isAdministrationVisible() {
		return administrationVisible;
	}

	public void setAdministrationVisible(boolean administrationVisible) {
		this.administrationVisible = administrationVisible;
	}

	public boolean isPreferencesVisible() {
		return preferencesVisible;
	}

	public void setPreferencesVisible(boolean preferencesVisible) {
		this.preferencesVisible = preferencesVisible;
	}
	
	public boolean isOmrVisible() {
		return omrVisible;
	}

	public void setOmrVisible(boolean omrVisible) {
		this.omrVisible = omrVisible;
	}

	public boolean isConvertVisible() {
		return convertVisible;
	}

	public void setConvertVisible(boolean convertVisible) {
		this.convertVisible = convertVisible;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("languagesVisible=").append(languagesVisible);
		sb.append(", skinVisible=").append(skinVisible);
		sb.append(", debugVisible=").append(debugVisible);
		sb.append(", administrationVisible=").append(administrationVisible);
		sb.append(", preferencesVisible=").append(preferencesVisible);		
		sb.append(", omrVisible=").append(omrVisible);
		sb.append(", convertVisible=").append(convertVisible);		
		sb.append("}");
		return sb.toString();
	}
}
