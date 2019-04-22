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

package com.openkm.dao.bean;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "OKM_AUTO_RULE")
public class AutomationRule implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String AT_PRE = Automation.AT_PRE;
	public static final String AT_POST = Automation.AT_POST;

	public static final String EVENT_DOCUMENT_CREATE = "doc_create";
	public static final String EVENT_DOCUMENT_UPDATE = "doc_update";
	public static final String EVENT_DOCUMENT_DELETE = "doc_delete";
	public static final String EVENT_DOCUMENT_RENAME = "doc_rename";
	public static final String EVENT_DOCUMENT_MOVE = "doc_move";

	public static final String EVENT_FOLDER_CREATE = "fld_create";

	public static final String EVENT_MAIL_CREATE = "mail_create";

	public static final String EVENT_TEXT_EXTRACTOR = "text_extractor";

	public static final String EVENT_CONVERSION_PDF = "convert_pdf";
	public static final String EVENT_CONVERSION_SWF = "convert_swf";

	public static final String EVENT_PROPERTY_GROUP_ADD = "prop_group_add";
	public static final String EVENT_PROPERTY_GROUP_SET = "prop_group_set";
	public static final String EVENT_PROPERTY_GROUP_REMOVE = "prop_group_remove";

	public static final String EVENT_USER_LOGIN = "user_login";
	public static final String EVENT_USER_LOGOUT = "user_logout";

	public static final Map<String, String> EVENTS = new LinkedHashMap<String, String>() {
		private static final long serialVersionUID = 1L;

		{
			put(AutomationRule.EVENT_DOCUMENT_CREATE, "Document create");
			put(AutomationRule.EVENT_DOCUMENT_UPDATE, "Document update");
			put(AutomationRule.EVENT_DOCUMENT_DELETE, "Document delete");
			put(AutomationRule.EVENT_DOCUMENT_RENAME, "Document rename");
			put(AutomationRule.EVENT_DOCUMENT_MOVE, "Document move");

			put(AutomationRule.EVENT_FOLDER_CREATE, "Folder create");

			put(AutomationRule.EVENT_MAIL_CREATE, "Mail creation");

			put(AutomationRule.EVENT_PROPERTY_GROUP_ADD, "Add metadata group");
			put(AutomationRule.EVENT_PROPERTY_GROUP_SET, "Set metadata group");
			put(AutomationRule.EVENT_PROPERTY_GROUP_REMOVE, "Remove metadata group");

			put(AutomationRule.EVENT_TEXT_EXTRACTOR, "Text extraction");

			put(AutomationRule.EVENT_CONVERSION_PDF, "Convert to PDF");
			put(AutomationRule.EVENT_CONVERSION_SWF, "Convert to SWF");

			put(AutomationRule.EVENT_USER_LOGIN, "User login");
			put(AutomationRule.EVENT_USER_LOGOUT, "User logout");
		}
	};
	
	@Id
	@Column(name = "ARL_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "ARL_NAME", length = 255)
	private String name;

	@Column(name = "ARL_EVENT", length = 32)
	private String event;

	@Column(name = "ARL_AT", length = 32)
	private String at;

	@Column(name = "ARL_ORDER")
	private int order;

	@Column(name = "ARL_EXCLUSIVE", nullable = false)
	@Type(type = "true_false")
	private Boolean exclusive = Boolean.FALSE;

	@Column(name = "ARL_ACTIVE", nullable = false)
	@Type(type = "true_false")
	private Boolean active = Boolean.FALSE;

	@OrderBy("order ASC")
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "AVL_RULE", referencedColumnName = "ARL_ID")
	private List<AutomationValidation> validations = new ArrayList<AutomationValidation>();

	@OrderBy("order ASC")
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "AAC_RULE", referencedColumnName = "ARL_ID")
	private List<AutomationAction> actions = new ArrayList<AutomationAction>();

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

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getAt() {
		return at;
	}

	public void setAt(String at) {
		this.at = at;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Boolean getExclusive() {
		if (exclusive == null) {
			return false;
		}
		return exclusive;
	}

	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}

	public Boolean getActive() {
		if (active == null) {
			return false;
		}
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<AutomationValidation> getValidations() {
		return validations;
	}

	public void setValidations(List<AutomationValidation> validations) {
		this.validations = validations;
	}

	public List<AutomationAction> getActions() {
		return actions;
	}

	public void setActions(List<AutomationAction> actions) {
		this.actions = actions;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=").append(id);
		sb.append(", name=").append(name);
		sb.append(", event=").append(event);
		sb.append(", at=").append(at);
		sb.append(", order=").append(order);
		sb.append(", exclusive=").append(exclusive);
		sb.append(", active=").append(active);
		sb.append(", validations=").append(validations);
		sb.append(", actions=").append(actions);
		sb.append("}");
		return sb.toString();
	}
}
