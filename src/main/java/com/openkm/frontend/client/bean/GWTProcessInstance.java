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

import java.util.Date;
import java.util.Map;

/**
 * GWTProcessInstance
 *
 * @author jllort
 *
 */
public class GWTProcessInstance implements IsSerializable {

	private double id;
	private int version;
	private String key = "";
	private Date start;
	private Date end;
	private boolean ended;
	private boolean suspended;
	private Map<String, Object> variables;
	private GWTProcessDefinition processDefinition;
	private GWTToken rootToken;

	/**
	 * GWTProcessInstance
	 */
	public GWTProcessInstance() {
	}

	public double getId() {
		return id;
	}

	public void setId(double id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isEnded() {
		return ended;
	}

	public void setEnded(boolean ended) {
		this.ended = ended;
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	public GWTProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void setProcessDefinition(GWTProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public GWTToken getRootToken() {
		return rootToken;
	}

	public void setRootToken(GWTToken rootToken) {
		this.rootToken = rootToken;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}
}