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

package com.openkm.bean;

import java.io.Serializable;

public class FormField implements Serializable {
	private static final long serialVersionUID = -2161651681617136626L;

	public static final int CHECKBOX = 0;
	public static final int INPUT = 1;
	public static final int TEXT_AREA = 2;
	public static final int SELECT = 3;
	public static final int SELECT_MULTI = 4;
	public static final int TRANSITION = 6;

	public static final String[] TYPE = {"CHECKBOX", "INPUT", "TEXT_AREA", "SELECT", "SELECT_MULTI", "RESERVED", "TRANSITION"};

	private int type;
	private String label;
	private String name;
	private String value;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("type=");
		sb.append(type);
		sb.append(", label=");
		sb.append(label);
		sb.append(", name=");
		sb.append(name);
		sb.append(", value=");
		sb.append(value);
		sb.append("}");
		return sb.toString();
	}
}
