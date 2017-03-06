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

package com.openkm.frontend.client.bean.form;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * GWTSelect
 *
 * @author jllort
 *
 */
public class GWTSelect extends GWTFormElement implements IsSerializable {
	public static final String TYPE_SIMPLE = "simple";
	public static final String TYPE_MULTIPLE = "multiple";
	private Collection<GWTOption> options = new ArrayList<GWTOption>();
	private List<GWTValidator> validators = new ArrayList<GWTValidator>();
	private String type = TYPE_SIMPLE;
	private String data = "";
	private String optionsData = "";
	private String suggestion = "";
	private String className = "";
	private boolean readonly = false;

	public Collection<GWTOption> getOptions() {
		return options;
	}

	public void setOptions(Collection<GWTOption> options) {
		this.options = options;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<GWTValidator> getValidators() {
		return validators;
	}

	public void setValidators(List<GWTValidator> validators) {
		this.validators = validators;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getOptionsData() {
		return optionsData;
	}

	public void setOptionsData(String optionsData) {
		this.optionsData = optionsData;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("label=");
		sb.append(label);
		sb.append(", name=");
		sb.append(name);
		sb.append(", width=");
		sb.append(width);
		sb.append(", height=");
		sb.append(height);
		sb.append(", readonly=");
		sb.append(readonly);
		sb.append(", type=");
		sb.append(type);
		sb.append(", options=");
		sb.append(options);
		sb.append(", validators=");
		sb.append(validators);
		sb.append(", data=");
		sb.append(data);
		sb.append(", suggestion=");
		sb.append(suggestion);
		sb.append("}");
		return sb.toString();
	}
}
