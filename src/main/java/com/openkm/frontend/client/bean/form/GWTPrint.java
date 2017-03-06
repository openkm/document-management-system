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
import java.util.List;

/**
 * GWTPrint
 *
 * @author jllort
 *
 */
public class GWTPrint extends GWTFormElement implements IsSerializable {
	private List<GWTValidator> validators = new ArrayList<GWTValidator>();
	private List<GWTNode> nodes = new ArrayList<GWTNode>();
	private String data = "";

	public GWTPrint() {
		super.width = "150px";
	}

	public List<GWTValidator> getValidators() {
		return validators;
	}

	public void setValidators(List<GWTValidator> validators) {
		this.validators = validators;
	}

	public List<GWTNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<GWTNode> nodes) {
		this.nodes = nodes;
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
		sb.append("label=");
		sb.append(label);
		sb.append(", name=");
		sb.append(name);
		sb.append(", width=");
		sb.append(width);
		sb.append(", height=");
		sb.append(height);
		sb.append(", data=");
		sb.append(data);
		sb.append(", nodes=");
		sb.append(nodes);
		sb.append(", validators=");
		sb.append(validators);
		sb.append("}");
		return sb.toString();
	}
}
