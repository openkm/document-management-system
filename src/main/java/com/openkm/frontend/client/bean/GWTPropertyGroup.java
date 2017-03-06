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

package com.openkm.frontend.client.bean;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author jllort
 *
 */
public class GWTPropertyGroup implements IsSerializable {
	public static final String GROUP = "okg";
	public static final String GROUP_URI = "http://www.openkm.org/group/1.0";
	public static final String GROUP_PROPERTY = "okp";
	public static final String GROUP_PROPERTY_URI = "http://www.openkm.org/group/property/1.0";

	private String label = "";
	private String name = "";
	private boolean visible = true;
	private boolean readonly = false;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("label=");
		sb.append(label);
		sb.append(", name=");
		sb.append(name);
		sb.append(", visible=");
		sb.append(visible);
		sb.append(", readonly=");
		sb.append(readonly);
		sb.append("}");
		return sb.toString();
	}
}
