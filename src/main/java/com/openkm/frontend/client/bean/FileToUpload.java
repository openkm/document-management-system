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

import com.google.gwt.user.client.ui.FileUpload;
import com.openkm.frontend.client.widget.form.HasWorkflow;
import com.openkm.frontend.client.widget.upload.FileUploadForm;

/**
 * FileToUpload
 *
 * @author jllort
 *
 */
public class FileToUpload implements Cloneable {
	public static final String DEFAULT_SIZE = "45";

	private FileUploadForm uploadForm;
	private FileUpload fileUpload;
	private int action;
	private String size = DEFAULT_SIZE;
	private boolean fireEvent = false;
	private String path = "";
	private boolean enableAddButton = true;
	private boolean enableImport = true;
	private String desiredDocumentName;
	private boolean lastToBeUploaded = false;
	private HasWorkflow workflow;
	private double workflowTaskId;
	private String workflowTransition;
	private String name = "";
	private String documentPath = "";
	private String documentUUID = "";

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public FileToUpload clone() {
		FileToUpload fileUpload = new FileToUpload();
		fileUpload.setAction(getAction());
		fileUpload.setDesiredDocumentName(getDesiredDocumentName());
		fileUpload.setDocumentPath(getDocumentPath());
		fileUpload.setDocumentUUID(getDocumentUUID());
		fileUpload.setEnableAddButton(isEnableAddButton());
		fileUpload.setEnableImport(isEnableImport());
		fileUpload.setFileUpload(getFileUpload());
		fileUpload.setFireEvent(isFireEvent());
		fileUpload.setLastToBeUploaded(isLastToBeUploaded());
		fileUpload.setName(getName());
		fileUpload.setPath(getPath());
		fileUpload.setSize(getSize());
		fileUpload.setWorkflow(getWorkflow());
		fileUpload.setWorkflowTaskId(getWorkflowTaskId());
		fileUpload.setWorkflowTransition(getWorkflowTransition());
		return fileUpload;
	}

	public FileUpload getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(FileUpload fileUpload) {
		this.fileUpload = fileUpload;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public boolean isFireEvent() {
		return fireEvent;
	}

	public void setFireEvent(boolean fireEvent) {
		this.fireEvent = fireEvent;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDesiredDocumentName() {
		return desiredDocumentName;
	}

	public void setDesiredDocumentName(String desiredDocumentName) {
		this.desiredDocumentName = desiredDocumentName;
	}

	public boolean isLastToBeUploaded() {
		return lastToBeUploaded;
	}

	public void setLastToBeUploaded(boolean lastToBeUploaded) {
		this.lastToBeUploaded = lastToBeUploaded;
	}

	public HasWorkflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(HasWorkflow workflow) {
		this.workflow = workflow;
	}

	public boolean isEnableImport() {
		return enableImport;
	}

	public void setEnableImport(boolean enableImport) {
		this.enableImport = enableImport;
	}

	public boolean isEnableAddButton() {
		return enableAddButton;
	}

	public void setEnableAddButton(boolean enableAddButton) {
		this.enableAddButton = enableAddButton;
	}

	public double getWorkflowTaskId() {
		return workflowTaskId;
	}

	public void setWorkflowTaskId(double workflowTaskId) {
		this.workflowTaskId = workflowTaskId;
	}

	public String getWorkflowTransition() {
		return workflowTransition;
	}

	public void setWorkflowTransition(String workflowTransition) {
		this.workflowTransition = workflowTransition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDocumentPath() {
		return documentPath;
	}

	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}

	public String getDocumentUUID() {
		return documentUUID;
	}

	public void setDocumentUUID(String documentUUID) {
		this.documentUUID = documentUUID;
	}

	public FileUploadForm getUploadForm() {
		return uploadForm;
	}

	public void setUploadForm(FileUploadForm uploadForm) {
		this.uploadForm = uploadForm;
	}
}