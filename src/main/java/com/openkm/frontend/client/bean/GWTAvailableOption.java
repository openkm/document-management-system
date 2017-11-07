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

/**
 * GWTAvailableOption
 *
 * @author jllort
 *
 */
public class GWTAvailableOption implements IsSerializable {
	private boolean createFolderOption = true;
	private boolean findFolderOption = true;
	private boolean findDocumentOption = true;
	private boolean similarDocumentVisible = true;
	private boolean downloadOption = true;
	private boolean downloadPdfOption = true;
	private boolean lockOption = true;
	private boolean unLockOption = true;
	private boolean addDocumentOption = true;
	private boolean checkoutOption = true;
	private boolean checkinOption = true;
	private boolean cancelCheckoutOption = true;
	private boolean deleteOption = true;
	private boolean addPropertyGroupOption = true;
	private boolean updatePropertyGroupOption = true;
	private boolean removePropertyGroupOption = true;
	private boolean addSubscriptionOption = true;
	private boolean removeSubscriptionOption = true;
	private boolean homeOption = true;
	private boolean refreshOption = true;
	private boolean workflowOption = true;
	private boolean renameOption = true;
	private boolean copyOption = true;
	private boolean moveOption = true;
	private boolean addBookmarkOption = true;
	private boolean setHomeOption = true;
	private boolean exportOption = true;
	private boolean mediaPlayerOption = true;
	private boolean imageViewerOption = true;
	private boolean gotoFolderOption = true;
	private boolean createFromTemplateOption = true;
	private boolean purgeOption = true;
	private boolean restoreOption = true;
	private boolean purgeTrashOption = true;
	private boolean sendDocumentLinkOption = true;
	private boolean sendDocumentAttachmentOption = true;
	private boolean forwardMailOption = true;
	private boolean skinOption = true;
	private boolean debugOption = true;
	private boolean administrationOption = true;
	private boolean manageBookmarkOption = true;
	private boolean helpOption = true;
	private boolean documentationOption = true;
	private boolean bugReportOption = true;
	private boolean supportRequestOption = true;
	private boolean publicForumOption = true;
	private boolean versionChangesOption = true;
	private boolean projectWebOption = true;
	private boolean aboutOption = true;
	private boolean languagesOption = true;
	private boolean preferencesOption = true;
	private boolean convertOption = true;
	private boolean addNoteOption = true;
	private boolean addCategoryOption = true;
	private boolean addKeywordOption = true;
	private boolean removeNoteOption = true;
	private boolean removeCategoryOption = true;
	private boolean removeKeywordOption = true;
	private boolean mergePdfOption = true;
	private boolean omr = true;

	public GWTAvailableOption() {
	}

	public boolean isCreateFolderOption() {
		return createFolderOption;
	}

	public void setCreateFolderOption(boolean createFolderOption) {
		this.createFolderOption = createFolderOption;
	}

	public boolean isFindFolderOption() {
		return findFolderOption;
	}

	public void setFindFolderOption(boolean findFolderOption) {
		this.findFolderOption = findFolderOption;
	}

	public boolean isDownloadOption() {
		return downloadOption;
	}

	public void setDownloadOption(boolean downloadOption) {
		this.downloadOption = downloadOption;
	}

	public boolean isDownloadPdfOption() {
		return downloadPdfOption;
	}

	public void setDownloadPdfOption(boolean downloadPdfOption) {
		this.downloadPdfOption = downloadPdfOption;
	}

	public boolean isLockOption() {
		return lockOption;
	}

	public void setLockOption(boolean lockOption) {
		this.lockOption = lockOption;
	}

	public boolean isUnLockOption() {
		return unLockOption;
	}

	public void setUnLockOption(boolean unLockOption) {
		this.unLockOption = unLockOption;
	}

	public boolean isAddDocumentOption() {
		return addDocumentOption;
	}

	public void setAddDocumentOption(boolean addDocumentOption) {
		this.addDocumentOption = addDocumentOption;
	}

	public boolean isCheckoutOption() {
		return checkoutOption;
	}

	public void setCheckoutOption(boolean checkoutOption) {
		this.checkoutOption = checkoutOption;
	}

	public boolean isCheckinOption() {
		return checkinOption;
	}

	public void setCheckinOption(boolean checkinOption) {
		this.checkinOption = checkinOption;
	}

	public boolean isCancelCheckoutOption() {
		return cancelCheckoutOption;
	}

	public void setCancelCheckoutOption(boolean cancelCheckoutOption) {
		this.cancelCheckoutOption = cancelCheckoutOption;
	}

	public boolean isDeleteOption() {
		return deleteOption;
	}

	public void setDeleteOption(boolean deleteOption) {
		this.deleteOption = deleteOption;
	}

	public boolean isAddPropertyGroupOption() {
		return addPropertyGroupOption;
	}

	public void setAddPropertyGroupOption(boolean addPropertyGroupOption) {
		this.addPropertyGroupOption = addPropertyGroupOption;
	}

	public boolean isUpdatePropertyGroupOption() {
		return updatePropertyGroupOption;
	}

	public void setUpdatePropertyGroupOption(boolean updatePropertyGroupOption) {
		this.updatePropertyGroupOption = updatePropertyGroupOption;
	}

	public boolean isRemovePropertyGroupOption() {
		return removePropertyGroupOption;
	}

	public void setRemovePropertyGroupOption(boolean removePropertyGroupOption) {
		this.removePropertyGroupOption = removePropertyGroupOption;
	}

	public boolean isAddSubscriptionOption() {
		return addSubscriptionOption;
	}

	public void setAddSubscriptionOption(boolean addSubscriptionOption) {
		this.addSubscriptionOption = addSubscriptionOption;
	}

	public boolean isRemoveSubscriptionOption() {
		return removeSubscriptionOption;
	}

	public void setRemoveSubscriptionOption(boolean removeSubscriptionOption) {
		this.removeSubscriptionOption = removeSubscriptionOption;
	}

	public boolean isHomeOption() {
		return homeOption;
	}

	public void setHomeOption(boolean homeOption) {
		this.homeOption = homeOption;
	}

	public boolean isRefreshOption() {
		return refreshOption;
	}

	public void setRefreshOption(boolean refreshOption) {
		this.refreshOption = refreshOption;
	}

	public boolean isWorkflowOption() {
		return workflowOption;
	}

	public void setWorkflowOption(boolean workflowOption) {
		this.workflowOption = workflowOption;
	}

	public boolean isRenameOption() {
		return renameOption;
	}

	public void setRenameOption(boolean renameOption) {
		this.renameOption = renameOption;
	}

	public boolean isMoveOption() {
		return moveOption;
	}

	public void setMoveOption(boolean moveOption) {
		this.moveOption = moveOption;
	}

	public boolean isCopyOption() {
		return copyOption;
	}

	public void setCopyOption(boolean copyOption) {
		this.copyOption = copyOption;
	}

	public boolean isAddBookmarkOption() {
		return addBookmarkOption;
	}

	public void setAddBookmarkOption(boolean addBookmarkOption) {
		this.addBookmarkOption = addBookmarkOption;
	}

	public boolean isSetHomeOption() {
		return setHomeOption;
	}

	public void setSetHomeOption(boolean setHomeOption) {
		this.setHomeOption = setHomeOption;
	}

	public boolean isExportOption() {
		return exportOption;
	}

	public void setExportOption(boolean exportOption) {
		this.exportOption = exportOption;
	}

	public boolean isMediaPlayerOption() {
		return mediaPlayerOption;
	}

	public void setMediaPlayerOption(boolean mediaPlayerOption) {
		this.mediaPlayerOption = mediaPlayerOption;
	}

	public boolean isImageViewerOption() {
		return imageViewerOption;
	}

	public void setImageViewerOption(boolean imageViewerOption) {
		this.imageViewerOption = imageViewerOption;
	}

	public boolean isGotoFolderOption() {
		return gotoFolderOption;
	}

	public void setGotoFolderOption(boolean gotoFolderOption) {
		this.gotoFolderOption = gotoFolderOption;
	}

	public boolean isCreateFromTemplateOption() {
		return createFromTemplateOption;
	}

	public void setCreateFromTemplateOption(boolean createFromTemplateOption) {
		this.createFromTemplateOption = createFromTemplateOption;
	}

	public boolean isPurgeOption() {
		return purgeOption;
	}

	public void setPurgeOption(boolean purgeOption) {
		this.purgeOption = purgeOption;
	}

	public boolean isRestoreOption() {
		return restoreOption;
	}

	public void setRestoreOption(boolean restoreOption) {
		this.restoreOption = restoreOption;
	}

	public boolean isPurgeTrashOption() {
		return purgeTrashOption;
	}

	public void setPurgeTrashOption(boolean purgeTrashOption) {
		this.purgeTrashOption = purgeTrashOption;
	}

	public boolean isSendDocumentLinkOption() {
		return sendDocumentLinkOption;
	}

	public void setSendDocumentLinkOption(boolean sendDocumentLinkOption) {
		this.sendDocumentLinkOption = sendDocumentLinkOption;
	}

	public boolean isSendDocumentAttachmentOption() {
		return sendDocumentAttachmentOption;
	}

	public void setSendDocumentAttachmentOption(boolean sendDocumentAttachmentOption) {
		this.sendDocumentAttachmentOption = sendDocumentAttachmentOption;
	}

	public boolean isForwardMailOption() {
		return forwardMailOption;
	}

	public void setForwardMailOption(boolean forwardMailOption) {
		this.forwardMailOption = forwardMailOption;
	}

	public boolean isSkinOption() {
		return skinOption;
	}

	public void setSkinOption(boolean skinOption) {
		this.skinOption = skinOption;
	}

	public boolean isDebugOption() {
		return debugOption;
	}

	public void setDebugOption(boolean debugOption) {
		this.debugOption = debugOption;
	}

	public boolean isAdministrationOption() {
		return administrationOption;
	}

	public void setAdministrationOption(boolean administrationOption) {
		this.administrationOption = administrationOption;
	}

	public boolean isManageBookmarkOption() {
		return manageBookmarkOption;
	}

	public void setManageBookmarkOption(boolean manageBookmarkOption) {
		this.manageBookmarkOption = manageBookmarkOption;
	}

	public boolean isHelpOption() {
		return helpOption;
	}

	public void setHelpOption(boolean helpOption) {
		this.helpOption = helpOption;
	}

	public boolean isDocumentationOption() {
		return documentationOption;
	}

	public void setDocumentationOption(boolean documentationOption) {
		this.documentationOption = documentationOption;
	}

	public boolean isBugReportOption() {
		return bugReportOption;
	}

	public void setBugReportOption(boolean bugReportOption) {
		this.bugReportOption = bugReportOption;
	}

	public boolean isSupportRequestOption() {
		return supportRequestOption;
	}

	public void setSupportRequestOption(boolean supportRequestOption) {
		this.supportRequestOption = supportRequestOption;
	}

	public boolean isPublicForumOption() {
		return publicForumOption;
	}

	public void setPublicForumOption(boolean publicForumOption) {
		this.publicForumOption = publicForumOption;
	}

	public boolean isVersionChangesOption() {
		return versionChangesOption;
	}

	public void setVersionChangesOption(boolean versionChangesOption) {
		this.versionChangesOption = versionChangesOption;
	}

	public boolean isProjectWebOption() {
		return projectWebOption;
	}

	public void setProjectWebOption(boolean projectWebOption) {
		this.projectWebOption = projectWebOption;
	}

	public boolean isAboutOption() {
		return aboutOption;
	}

	public void setAboutOption(boolean aboutOption) {
		this.aboutOption = aboutOption;
	}

	public boolean isLanguagesOption() {
		return languagesOption;
	}

	public void setLanguagesOption(boolean languagesOption) {
		this.languagesOption = languagesOption;
	}

	public boolean isPreferencesOption() {
		return preferencesOption;
	}

	public void setPreferencesOption(boolean preferencesOption) {
		this.preferencesOption = preferencesOption;
	}

	public boolean isConvertOption() {
		return convertOption;
	}

	public void setConvertOption(boolean convertOption) {
		this.convertOption = convertOption;
	}

	public boolean isFindDocumentOption() {
		return findDocumentOption;
	}

	public void setFindDocumentOption(boolean findDocumentOption) {
		this.findDocumentOption = findDocumentOption;
	}

	public boolean isSimilarDocumentVisible() {
		return similarDocumentVisible;
	}

	public void setSimilarDocumentVisible(boolean similarDocumentVisible) {
		this.similarDocumentVisible = similarDocumentVisible;
	}

	public boolean isAddNoteOption() {
		return addNoteOption;
	}

	public void setAddNoteOption(boolean addNoteOption) {
		this.addNoteOption = addNoteOption;
	}

	public boolean isAddCategoryOption() {
		return addCategoryOption;
	}

	public void setAddCategoryOption(boolean addCategoryOption) {
		this.addCategoryOption = addCategoryOption;
	}

	public boolean isAddKeywordOption() {
		return addKeywordOption;
	}

	public void setAddKeywordOption(boolean addKeywordOption) {
		this.addKeywordOption = addKeywordOption;
	}

	public boolean isRemoveNoteOption() {
		return removeNoteOption;
	}

	public void setRemoveNoteOption(boolean removeNoteOption) {
		this.removeNoteOption = removeNoteOption;
	}

	public boolean isRemoveCategoryOption() {
		return removeCategoryOption;
	}

	public void setRemoveCategoryOption(boolean removeCategoryOption) {
		this.removeCategoryOption = removeCategoryOption;
	}

	public boolean isRemoveKeywordOption() {
		return removeKeywordOption;
	}

	public void setRemoveKeywordOption(boolean removeKeywordOption) {
		this.removeKeywordOption = removeKeywordOption;
	}

	public boolean isMergePdfOption() {
		return mergePdfOption;
	}

	public void setMergePdfOption(boolean mergeOption) {
		this.mergePdfOption = mergeOption;
	}
	
	public boolean isOmr() {
		return omr;
	}

	public void setOmr(boolean omr) {
		this.omr = omr;
	}
	
}
