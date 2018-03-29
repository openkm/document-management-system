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

package com.openkm.dao.bean;

import com.openkm.automation.Action;
import com.openkm.automation.Validation;

import java.io.Serializable;

public class Automation implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String AT_PRE = "pre";
	public static final String AT_POST = "post";

	public static final String PARAM_TYPE_EMPTY = "";
	public static final String PARAM_TYPE_TEXT = "text";
	public static final String PARAM_TYPE_LONG = "long";
	public static final String PARAM_TYPE_INTEGER = "integer";
	public static final String PARAM_TYPE_BOOLEAN = "boolean";
	public static final String PARAM_TYPE_TEXTAREA = "textarea";
	public static final String PARAM_TYPE_CODE = "code";
	public static final String PARAM_TYPE_USER = "user";
	public static final String PARAM_TYPE_ROLE = "role";
	public static final String PARAM_TYPE_OMR = "okm:omr";

	public static final String PARAM_SOURCE_EMPTY = "";
	public static final String PARAM_SOURCE_FOLDER = "okm:folder";
	public static final String PARAM_SOURCE_OMR = "okm:omr";

	public static final String PARAM_DESCRIPTION_EMPTY = "";

	private String name;
	private String className;
	private String at;
	private boolean post = false;
	private boolean pre = false;
	private boolean active;
	private String type00;
	private String type01;
	private String type02;
	private String source00;
	private String source01;
	private String source02;
	private String description00;
	private String description01;
	private String description02;

	public Automation() {
	}

	public Automation(Validation val) {
		this.className = val.getClass().getName();
		this.setPost(val.hasPost());
		this.setPre(val.hasPre());
		this.setName(val.getName());
		this.setType00(val.getParamType00());
		this.setType01(val.getParamType01());
		this.setType02(val.getParamType02());
		this.setSource00(val.getParamSrc00());
		this.setSource01(val.getParamSrc01());
		this.setSource02(val.getParamSrc02());
		this.setDescription00(val.getParamDesc00());
		this.setDescription01(val.getParamDesc01());
		this.setDescription02(val.getParamDesc02());
	}

	public Automation(Action act) {
		this.className = act.getClass().getName();
		this.setPost(act.hasPost());
		this.setPre(act.hasPre());
		this.setName(act.getName());
		this.setType00(act.getParamType00());
		this.setType01(act.getParamType01());
		this.setType02(act.getParamType02());
		this.setSource00(act.getParamSrc00());
		this.setSource01(act.getParamSrc01());
		this.setSource02(act.getParamSrc02());
		this.setDescription00(act.getParamDesc00());
		this.setDescription01(act.getParamDesc01());
		this.setDescription02(act.getParamDesc02());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getAt() {
		return at;
	}

	public void setAt(String at) {
		this.at = at;
	}

	public boolean isPost() {
		return post;
	}

	public void setPost(boolean post) {
		this.post = post;
	}

	public boolean isPre() {
		return pre;
	}

	public void setPre(boolean pre) {
		this.pre = pre;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getType00() {
		return type00;
	}

	public void setType00(String type00) {
		this.type00 = type00;
	}

	public String getType01() {
		return type01;
	}

	public void setType01(String type01) {
		this.type01 = type01;
	}

	public String getSource00() {
		return source00;
	}

	public void setSource00(String source00) {
		this.source00 = source00;
	}

	public String getSource01() {
		return source01;
	}

	public void setSource01(String source01) {
		this.source01 = source01;
	}

	public String getDescription00() {
		return description00;
	}

	public void setDescription00(String description00) {
		this.description00 = description00;
	}

	public String getDescription01() {
		return description01;
	}

	public void setDescription01(String description01) {
		this.description01 = description01;
	}

	public String getType02() {
		return type02;
	}

	public void setType02(String type02) {
		this.type02 = type02;
	}

	public String getDescription02() {
		return description02;
	}

	public void setDescription02(String description02) {
		this.description02 = description02;
	}

	public String getSource02() {
		return source02;
	}

	public void setSource02(String source02) {
		this.source02 = source02;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(", name=").append(name);
		sb.append(", className=").append(className);
		sb.append(", at=").append(at);
		sb.append(", type00=").append(type00);
		sb.append(", type01=").append(type01);
		sb.append(", type02=").append(type02);
		sb.append(", source00=").append(source00);
		sb.append(", source01=").append(source01);
		sb.append(", source02=").append(source02);
		sb.append(", description00=").append(description00);
		sb.append(", description01=").append(description01);
		sb.append(", description02=").append(description02);
		sb.append("}");
		return sb.toString();
	}
}
