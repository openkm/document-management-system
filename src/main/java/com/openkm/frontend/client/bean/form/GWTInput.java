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
import com.openkm.frontend.client.bean.GWTFolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * GWTInput
 *
 * @author jllort
 *
 */
public class GWTInput extends GWTFormElement implements IsSerializable {
	public static final String TYPE_TEXT = "text";
	public static final String TYPE_DATE = "date";
	public static final String TYPE_LINK = "link";
	public static final String TYPE_FOLDER = "folder";
	private List<GWTValidator> validators = new ArrayList<GWTValidator>();
	private String type = TYPE_TEXT;
	private String value = "";
	private Date date;
	private Date dateTo; // Used only for search
	private boolean readonly = false;
	private String data = "";
	private GWTFolder folder = new GWTFolder();

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<GWTValidator> getValidators() {
		return validators;
	}

	public void setValidators(List<GWTValidator> validators) {
		this.validators = validators;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public GWTFolder getFolder() {
		return folder;
	}

	public void setFolder(GWTFolder folder) {
		this.folder = folder;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("label=");
		sb.append(label);
		sb.append(", name=");
		sb.append(name);
		sb.append(", value=");
		sb.append(value);
		sb.append(", width=");
		sb.append(width);
		sb.append(", height=");
		sb.append(height);
		sb.append(", readonly=");
		sb.append(readonly);
		sb.append(", type=");
		sb.append(type);
		sb.append(", validators=");
		sb.append(validators);
		sb.append(", date=");
		sb.append(date);
		sb.append(", data=");
		sb.append(data);
		sb.append("}");
		return sb.toString();
	}
}
