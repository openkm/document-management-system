/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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

package com.openkm.rest.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.openkm.ws.common.util.FormElementComplex;

@XmlRootElement(name = "processDefinitionForm")
public class ProcessDefinitionForm implements Serializable {
	private static final long serialVersionUID = 1L;
	private String key = null;
	List<FormElementComplex> formElementsComplex = new ArrayList<FormElementComplex>();

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<FormElementComplex> getFormElementsComplex() {
		return formElementsComplex;
	}

	public void setFormElementsComplex(List<FormElementComplex> formElementsComplex) {
		this.formElementsComplex = formElementsComplex;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("key=").append(key);
		sb.append(", formElementsComplex=").append(formElementsComplex);
		sb.append("}");
		return sb.toString();
	}
}