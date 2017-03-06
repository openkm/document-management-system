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
import com.openkm.frontend.client.bean.form.GWTFormElement;

import java.util.List;

/**
 * GWTReport
 *
 * @author jllort
 */
public class GWTReport implements IsSerializable {
	private long id;
	private String name;
	private String fileContent;
	private String fileMime;
	private String fileName;
	private boolean active;
	private List<GWTFormElement> formElements;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}

	public String getFileMime() {
		return fileMime;
	}

	public void setFileMime(String fileMime) {
		this.fileMime = fileMime;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<GWTFormElement> getFormElements() {
		return formElements;
	}

	public void setFormElements(List<GWTFormElement> formElements) {
		this.formElements = formElements;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=");
		sb.append(id);
		sb.append(", name=");
		sb.append(name);
		sb.append(", fileName=");
		sb.append(fileName);
		sb.append(", fileMime=");
		sb.append(fileMime);
		sb.append(", fileContent=");
		sb.append("[BIG]");
		sb.append(", active=");
		sb.append(active);
		sb.append("}");
		return sb.toString();
	}
}
