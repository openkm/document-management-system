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

/**
 * Query params
 *
 * @author jllort
 *
 */
public class GWTPropertyParams implements IsSerializable {

	private String grpName;
	private String grpLabel;
	private GWTFormElement formElement;
	private String value;

	public String getGrpName() {
		return grpName;
	}

	public void setGrpName(String grpName) {
		this.grpName = grpName;
	}

	public GWTFormElement getFormElement() {
		return formElement;
	}

	public void setFormElement(GWTFormElement formElement) {
		this.formElement = formElement;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getGrpLabel() {
		return grpLabel;
	}

	public void setGrpLabel(String grpLabel) {
		this.grpLabel = grpLabel;
	}
}
