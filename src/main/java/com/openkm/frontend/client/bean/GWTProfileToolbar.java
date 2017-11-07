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
 * GWTProfileToolbar
 *
 * @author jllort
 *
 */
public class GWTProfileToolbar implements IsSerializable {
	private boolean createFolderVisible;
	private boolean findFolderVisible;
	private boolean findDocumentVisible;
	private boolean similarDocumentVisible;
	private boolean downloadVisible;
	private boolean downloadPdfVisible;
	private boolean printVisible;
	private boolean lockVisible;
	private boolean unlockVisible;
	private boolean addDocumentVisible;
	private boolean checkoutVisible;
	private boolean checkinVisible;
	private boolean cancelCheckoutVisible;
	private boolean deleteVisible;
	private boolean addPropertyGroupVisible;
	private boolean removePropertyGroupVisible;
	private boolean startWorkflowVisible;
	private boolean addSubscriptionVisible;
	private boolean removeSubscriptionVisible;
	private boolean refreshVisible;
	private boolean homeVisible;
	private boolean uploaderVisible;
	private boolean splitterResizeVisible;
	private boolean omrVisible;

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

	public boolean isPrintVisible() {
		return printVisible;
	}

	public void setPrintVisible(boolean printVisible) {
		this.printVisible = printVisible;
	}

	public boolean isLockVisible() {
		return lockVisible;
	}

	public void setLockVisible(boolean lockVisible) {
		this.lockVisible = lockVisible;
	}

	public boolean isUnlockVisible() {
		return unlockVisible;
	}

	public void setUnlockVisible(boolean unlockVisible) {
		this.unlockVisible = unlockVisible;
	}

	public boolean isAddDocumentVisible() {
		return addDocumentVisible;
	}

	public void setAddDocumentVisible(boolean addDocumentVisible) {
		this.addDocumentVisible = addDocumentVisible;
	}

	public boolean isCheckoutVisible() {
		return checkoutVisible;
	}

	public void setCheckoutVisible(boolean checkoutVisible) {
		this.checkoutVisible = checkoutVisible;
	}

	public boolean isCheckinVisible() {
		return checkinVisible;
	}

	public void setCheckinVisible(boolean checkinVisible) {
		this.checkinVisible = checkinVisible;
	}

	public boolean isCancelCheckoutVisible() {
		return cancelCheckoutVisible;
	}

	public void setCancelCheckoutVisible(boolean cancelCheckoutVisible) {
		this.cancelCheckoutVisible = cancelCheckoutVisible;
	}

	public boolean isDeleteVisible() {
		return deleteVisible;
	}

	public void setDeleteVisible(boolean deleteVisible) {
		this.deleteVisible = deleteVisible;
	}

	public boolean isAddPropertyGroupVisible() {
		return addPropertyGroupVisible;
	}

	public void setAddPropertyGroupVisible(boolean addPropertyGroupVisible) {
		this.addPropertyGroupVisible = addPropertyGroupVisible;
	}

	public boolean isRemovePropertyGroupVisible() {
		return removePropertyGroupVisible;
	}

	public void setRemovePropertyGroupVisible(boolean removePropertyGroupVisible) {
		this.removePropertyGroupVisible = removePropertyGroupVisible;
	}

	public boolean isStartWorkflowVisible() {
		return startWorkflowVisible;
	}

	public void setStartWorkflowVisible(boolean startWorkflowVisible) {
		this.startWorkflowVisible = startWorkflowVisible;
	}

	public boolean isAddSubscriptionVisible() {
		return addSubscriptionVisible;
	}

	public void setAddSubscriptionVisible(boolean addSubscriptionVisible) {
		this.addSubscriptionVisible = addSubscriptionVisible;
	}

	public boolean isRemoveSubscriptionVisible() {
		return removeSubscriptionVisible;
	}

	public void setRemoveSubscriptionVisible(boolean removeSubscriptionVisible) {
		this.removeSubscriptionVisible = removeSubscriptionVisible;
	}

	public boolean isRefreshVisible() {
		return refreshVisible;
	}

	public void setRefreshVisible(boolean refreshVisible) {
		this.refreshVisible = refreshVisible;
	}

	public boolean isHomeVisible() {
		return homeVisible;
	}

	public void setHomeVisible(boolean homeVisible) {
		this.homeVisible = homeVisible;
	}

	public boolean isUploaderVisible() {
		return uploaderVisible;
	}

	public void setUploaderVisible(boolean uploaderVisible) {
		this.uploaderVisible = uploaderVisible;
	}

	public boolean isSplitterResizeVisible() {
		return splitterResizeVisible;
	}

	public void setSplitterResizeVisible(boolean splitterResizeVisible) {
		this.splitterResizeVisible = splitterResizeVisible;
	}
	
	public boolean isOmrVisible() {
		return omrVisible;
	}

	public void setOmrVisible(boolean omrVisible) {
		this.omrVisible = omrVisible;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("createFolderVisible=").append(createFolderVisible);
		sb.append(", findFolderVisible=").append(findFolderVisible);
		sb.append(", findDocumentVisible=").append(findDocumentVisible);
		sb.append(", downloadVisible=").append(downloadVisible);
		sb.append(", downloadPdfVisible=").append(downloadPdfVisible);
		sb.append(", printVisible=").append(printVisible);
		sb.append(", addDocumentVisible=").append(addDocumentVisible);
		sb.append(", lockVisible=").append(lockVisible);
		sb.append(", unlockVisible=").append(unlockVisible);
		sb.append(", checkinVisible=").append(checkinVisible);
		sb.append(", checkoutVisible=").append(checkoutVisible);
		sb.append(", cancelCheckoutVisible=").append(cancelCheckoutVisible);
		sb.append(", deleteVisible=").append(deleteVisible);
		sb.append(", addPropertyGroupVisible=").append(addPropertyGroupVisible);
		sb.append(", removePropertyGroupVisible=").append(removePropertyGroupVisible);
		sb.append(", startWorkflowVisible=").append(startWorkflowVisible);
		sb.append(", addSubscriptionVisible=").append(addSubscriptionVisible);
		sb.append(", removeSubscriptionVisible=").append(removeSubscriptionVisible);
		sb.append(", homeVisible=").append(homeVisible);
		sb.append(", refreshVisible=").append(refreshVisible);		
		sb.append(", uploaderVisible=").append(uploaderVisible);
		sb.append(", splitterResizeVisible=").append(splitterResizeVisible);		
		sb.append(", omrVisible=").append(omrVisible);
		sb.append("}");
		return sb.toString();
	}
}
