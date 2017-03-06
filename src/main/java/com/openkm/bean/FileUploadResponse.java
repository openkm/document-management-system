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

package com.openkm.bean;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * FileUploadResponse
 *
 * @author jllort
 *
 */
public class FileUploadResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean hasAutomation = false;
	private String path = "";
	private List<String> groupsList = new ArrayList<String>();
	private List<String> workflowList = new ArrayList<String>();
	private boolean showWizardCategories = false;
	private boolean showWizardKeywords = false;
	private boolean digitalSignature = false;

	private String error = "";

	public String getPath() {
		return path;
	}

	public void setPath(String path) throws UnsupportedEncodingException {
		this.path = URLEncoder.encode(path, "UTF-8");
	}

	public List<String> getGroupsList() {
		return groupsList;
	}

	public void setGroupsList(List<String> groupsList) {
		this.groupsList = groupsList;
	}

	public List<String> getWorkflowList() {
		return workflowList;
	}

	public void setWorkflowList(List<String> workflowList) {
		this.workflowList = workflowList;
	}

	public boolean isShowWizardCategories() {
		return showWizardCategories;
	}

	public void setShowWizardCategories(boolean showWizardCategories) {
		this.showWizardCategories = showWizardCategories;
	}

	public boolean isShowWizardKeywords() {
		return showWizardKeywords;
	}

	public void setShowWizardKeywords(boolean showWizardKeywords) {
		this.showWizardKeywords = showWizardKeywords;
	}

	public boolean isHasAutomation() {
		return hasAutomation;
	}

	public void setHasAutomation(boolean hasAutomation) {
		this.hasAutomation = hasAutomation;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean isDigitalSignature() {
		return digitalSignature;
	}

	public void setDigitalSignature(boolean digitalSignature) {
		this.digitalSignature = digitalSignature;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("path=").append(path);
		sb.append(", showWizardCategories=").append(showWizardCategories);
		sb.append(", showWizardKeywords=").append(showWizardKeywords);
		sb.append(", groupsList=").append(groupsList);
		sb.append(", workflowList=").append(workflowList);
		sb.append(", hasAutomation=").append(hasAutomation);
		sb.append(", error=").append(error);
		sb.append(", digitalSignature=").append(digitalSignature);
		sb.append("}");
		return sb.toString();
	}
}