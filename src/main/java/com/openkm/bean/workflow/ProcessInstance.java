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

package com.openkm.bean.workflow;

import com.openkm.bean.form.*;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author pavila
 */
@XmlSeeAlso({Button.class, Input.class, TextArea.class, Select.class, CheckBox.class, SuggestBox.class})
@XmlRootElement(name = "processInstance")
public class ProcessInstance implements Serializable {
	private static final long serialVersionUID = -2917421131012124036L;

	private long id;
	private int version;
	private String key;
	private Calendar start;
	private Calendar end;
	private boolean ended;
	private boolean suspended;
	private Token rootToken;
	private Map<String, Object> variables;
	private List<Token> allTokens;
	private ProcessDefinition processDefinition;

	public ProcessInstance() {
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public void setAllTokens(List<Token> allTokens) {
		this.allTokens = allTokens;
	}

	public List<Token> getAllTokens() {
		return allTokens;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	public Calendar getStart() {
		return start;
	}

	public void setStart(Calendar start) {
		this.start = start;
	}

	public Calendar getEnd() {
		return end;
	}

	public void setEnd(Calendar end) {
		this.end = end;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Token getRootToken() {
		return rootToken;
	}

	public void setRootToken(Token rootToken) {
		this.rootToken = rootToken;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("id=");
		sb.append(id);
		sb.append(", version=");
		sb.append(version);
		sb.append(", key=");
		sb.append(key);
		sb.append(", ended=");
		sb.append(ended);
		sb.append(", suspended=");
		sb.append(suspended);
		sb.append(", variables=");
		sb.append(variables);
		sb.append(", rootToken=");
		sb.append(rootToken);
		sb.append(", allTokens=");
		sb.append(allTokens);
		sb.append(", start=");
		sb.append(start == null ? null : start.getTime());
		sb.append(", end=");
		sb.append(end == null ? null : end.getTime());
		sb.append(", processDefinition=");
		sb.append(processDefinition);
		sb.append("]");
		return sb.toString();
	}
}
