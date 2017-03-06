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

package com.openkm.bean.form;

import java.io.Serializable;

/**
 * http://www.javascript-coder.com/html-form/javascript-form-validation.phtml
 */
public class Validator implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String TYPE_REQUIRED = "req";
	public static final String TYPE_ALPHABETIC = "alpha";
	public static final String TYPE_DECIMAL = "dec";
	public static final String TYPE_NUMERIC = "num";
	public static final String TYPE_EMAIL = "email";
	public static final String TYPE_URL = "url";
	public static final String TYPE_MAXLENGTH = "maxlen";
	public static final String TYPE_MINLENGTH = "minlen";
	public static final String TYPE_LESSTHAN = "lt";
	public static final String TYPE_GREATERTHAN = "gt";
	public static final String TYPE_MINIMUN = "min";
	public static final String TYPE_MAXIMUN = "max";
	public static final String TYPE_REGEXP = "regexp";
	private String type = "";
	private String parameter = "";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("type=").append(type);
		sb.append(", parameter=").append(parameter);
		sb.append("}");
		return sb.toString();
	}
}
