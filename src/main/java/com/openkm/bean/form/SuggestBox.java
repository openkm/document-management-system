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

import java.util.ArrayList;
import java.util.List;

public class SuggestBox extends FormElement {
	private static final long serialVersionUID = 1L;
	private List<Validator> validators = new ArrayList<Validator>();
	private String value = "";
	private String data = "";
	private boolean readonly = false;
	String table = "";
	String dialogTitle = "";
	String filterQuery = "";
	String valueQuery = "";
	int filterMinLen = 0;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Validator> getValidators() {
		return validators;
	}

	public void setValidators(List<Validator> validators) {
		this.validators = validators;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getFilterQuery() {
		return filterQuery;
	}

	public void setFilterQuery(String filterQuery) {
		this.filterQuery = filterQuery;
	}

	public String getValueQuery() {
		return valueQuery;
	}

	public void setValueQuery(String valueQuery) {
		this.valueQuery = valueQuery;
	}

	public String getDialogTitle() {
		return dialogTitle;
	}

	public void setDialogTitle(String dialogTitle) {
		this.dialogTitle = dialogTitle;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getFilterMinLen() {
		return filterMinLen;
	}

	public void setFilterMinLen(int filterMinLen) {
		this.filterMinLen = filterMinLen;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("label=").append(label);
		sb.append(", name=").append(name);
		sb.append(", value=").append(value);
		sb.append(", data=").append(data);
		sb.append(", width=").append(width);
		sb.append(", height=").append(height);
		sb.append(", readonly=").append(readonly);
		sb.append(", table=").append(table);
		sb.append(", filterQuery=").append(filterQuery);
		sb.append(", valueQuery=").append(valueQuery);
		sb.append(", dialogTitle=").append(dialogTitle);
		sb.append(", filterMinLen=").append(filterMinLen);
		sb.append(", validators=").append(validators);
		sb.append("}");
		return sb.toString();
	}
}
