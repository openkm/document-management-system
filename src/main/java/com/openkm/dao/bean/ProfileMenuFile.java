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

import java.io.Serializable;

public class ProfileMenuFile implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean createFolderVisible;
	private boolean findFolderVisible;
	private boolean findDocumentVisible;
	private boolean similarDocumentVisible;
	private boolean goFolderVisible;
	private boolean downloadVisible;
	private boolean downloadPdfVisible;
	private boolean addDocumentVisible;
	private boolean startWorkflowVisible;
	private boolean refreshVisible;
	private boolean exportVisible;
	private boolean createFromTemplateVisible;
	private boolean purgeVisible;
	private boolean restoreVisible;
	private boolean purgeTrashVisible;
	private boolean sendDocumentLinkVisible;
	private boolean sendDocumentAttachmentVisible;
	private boolean forwardMailVisible;

	public boolean isCreateFolderVisible() {
		return createFolderVisible;
	}

	public void setCreateFolderVisible(boolean createFolderVisible) {
		this.createFolderVisible = createFolderVisible;
	}

	public boolean isFindFolderVisible() {
		return findFolderVisible;
	}

	public void setFindFolderVisible(boolean findFolderVisible) {
		this.findFolderVisible = findFolderVisible;
	}

	public boolean isFindDocumentVisible() {
		return findDocumentVisible;
	}

	public void setFindDocumentVisible(boolean findDocumentVisible) {
		this.findDocumentVisible = findDocumentVisible;
	}

	public boolean isSimilarDocumentVisible() {
		return similarDocumentVisible;
	}

	public void setSimilarDocumentVisible(boolean similarDocumentVisible) {
		this.similarDocumentVisible = similarDocumentVisible;
	}

	public boolean isGoFolderVisible() {
		return goFolderVisible;
	}

	public void setGoFolderVisible(boolean goFolderVisible) {
		this.goFolderVisible = goFolderVisible;
	}

	public boolean isDownloadVisible() {
		return downloadVisible;
	}

	public void setDownloadVisible(boolean downloadVisible) {
		this.downloadVisible = downloadVisible;
	}

	public boolean isDownloadPdfVisible() {
		return downloadPdfVisible;
	}

	public void setDownloadPdfVisible(boolean downloadPdfVisible) {
		this.downloadPdfVisible = downloadPdfVisible;
	}

	public boolean isAddDocumentVisible() {
		return addDocumentVisible;
	}

	public void setAddDocumentVisible(boolean addDocumentVisible) {
		this.addDocumentVisible = addDocumentVisible;
	}

	public boolean isStartWorkflowVisible() {
		return startWorkflowVisible;
	}

	public void setStartWorkflowVisible(boolean startWorkflowVisible) {
		this.startWorkflowVisible = startWorkflowVisible;
	}

	public boolean isRefreshVisible() {
		return refreshVisible;
	}

	public void setRefreshVisible(boolean refreshVisible) {
		this.refreshVisible = refreshVisible;
	}

	public boolean isExportVisible() {
		return exportVisible;
	}

	public void setExportVisible(boolean exportVisible) {
		this.exportVisible = exportVisible;
	}

	public boolean isCreateFromTemplateVisible() {
		return createFromTemplateVisible;
	}

	public void setCreateFromTemplateVisible(boolean createFromTemplateVisible) {
		this.createFromTemplateVisible = createFromTemplateVisible;
	}

	public boolean isPurgeVisible() {
		return purgeVisible;
	}

	public void setPurgeVisible(boolean purgeVisible) {
		this.purgeVisible = purgeVisible;
	}

	public boolean isRestoreVisible() {
		return restoreVisible;
	}

	public void setRestoreVisible(boolean restoreVisible) {
		this.restoreVisible = restoreVisible;
	}

	public boolean isPurgeTrashVisible() {
		return purgeTrashVisible;
	}

	public void setPurgeTrashVisible(boolean purgeTrashVisible) {
		this.purgeTrashVisible = purgeTrashVisible;
	}

	public boolean isSendDocumentLinkVisible() {
		return sendDocumentLinkVisible;
	}

	public void setSendDocumentLinkVisible(boolean sendDocumentLinkVisible) {
		this.sendDocumentLinkVisible = sendDocumentLinkVisible;
	}

	public boolean isSendDocumentAttachmentVisible() {
		return sendDocumentAttachmentVisible;
	}

	public void setSendDocumentAttachmentVisible(boolean sendDocumentAttachmentVisible) {
		this.sendDocumentAttachmentVisible = sendDocumentAttachmentVisible;
	}

	public boolean isForwardMailVisible() {
		return forwardMailVisible;
	}

	public void setForwardMailVisible(boolean forwardMailVisible) {
		this.forwardMailVisible = forwardMailVisible;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("createFolderVisible=").append(createFolderVisible);
		sb.append(", findFolderVisible=").append(findFolderVisible);
		sb.append(", findDocumentVisible=").append(findDocumentVisible);
		sb.append(", similarDocumentVisible=");
		sb.append(similarDocumentVisible);
		sb.append(", goFolderVisible=").append(goFolderVisible);
		sb.append(", downloadVisible=").append(downloadVisible);
		sb.append(", downloadPdfVisible=").append(downloadPdfVisible);
		;
		sb.append(", addDocumentVisible=").append(addDocumentVisible);
		sb.append(", startWorkflowVisible=").append(startWorkflowVisible);
		sb.append(", refreshVisible=").append(refreshVisible);
		sb.append("}");
		return sb.toString();
	}
}
