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

public class Upload extends FormElement {
	private static final long serialVersionUID = 1L;
	public static final String TYPE_CREATE = "create";
	public static final String TYPE_UPDATE = "update";
	private List<Validator> validators = new ArrayList<Validator>();
	private String type = TYPE_CREATE;
	private String folderPath = "";
	private String folderUuid = "";
	private String documentName = "";
	private String documentUuid = "";
	private String data = "";

	public Upload() {
		width = "33";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public String getFolderUuid() {
		return folderUuid;
	}

	public void setFolderUuid(String folderUuid) {
		this.folderUuid = folderUuid;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDocumentUuid() {
		return documentUuid;
	}

	public void setDocumentUuid(String documentUuid) {
		this.documentUuid = documentUuid;
	}

	public List<Validator> getValidators() {
		return validators;
	}

	public void setValidators(List<Validator> validators) {
		this.validators = validators;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("label=").append(label);
		sb.append(", name=").append(name);
		sb.append(", width=").append(width);
		sb.append(", height=").append(height);
		sb.append(", folderPath=").append(folderPath);
		sb.append(", folderUuid=").append(folderUuid);
		sb.append(", documentName=").append(documentName);
		sb.append(", documentUuid=").append(documentUuid);
		sb.append(", type=").append(type);
		sb.append(", data=").append(data);
		sb.append(", validators=").append(validators);
		sb.append("}");
		return sb.toString();
	}
}
