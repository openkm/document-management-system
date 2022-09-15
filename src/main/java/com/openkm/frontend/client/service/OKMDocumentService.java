/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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

package com.openkm.frontend.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTExtendedAttributes;
import com.openkm.frontend.client.bean.GWTVersion;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.widget.filebrowser.GWTFilter;

import java.util.List;
import java.util.Map;

/**
 * @author jllort
 */
@RemoteServiceRelativePath("Document")
public interface OKMDocumentService extends RemoteService {
	List<GWTDocument> getChilds(String fldId, Map<String, GWTFilter> mapFilter) throws OKMException;

	List<GWTVersion> getVersionHistory(String docPath) throws OKMException;

	void delete(String docPath) throws OKMException;

	void checkout(String docPath) throws OKMException;

	void cancelCheckout(String docPath) throws OKMException;

	void lock(String docPath) throws OKMException;

	void unlock(String docPath) throws OKMException;

	GWTDocument rename(String docPath, String newName) throws OKMException;

	void move(String docPath, String destPath) throws OKMException;

	void purge(String docPath) throws OKMException;

	void restoreVersion(String docPath, String versionId) throws OKMException;

	GWTDocument getProperties(String docPath) throws OKMException;

	void copy(String docPath, String fldPath) throws OKMException;

	Boolean isValid(String docPath) throws OKMException;

	Long getVersionHistorySize(String docPath) throws OKMException;

	void purgeVersionHistory(String docPath) throws OKMException;

	void forceUnlock(String docPath) throws OKMException;

	void forceCancelCheckout(String docPath) throws OKMException;

	GWTDocument createFromTemplate(String docPath, String destinationPath, List<GWTFormElement> formProperties,
									Map<String, List<Map<String, String>>> tableProperties) throws OKMException;

	String updateFromTemplate(String docPath, String destinationPath, List<GWTFormElement> formProperties,
							  Map<String, List<Map<String, String>>> tableProperties) throws OKMException;

	String convertToPdf(String docPath) throws OKMException;

	void mergePdf(String docName, List<String> paths) throws OKMException;

	List<GWTDocument> getAllTemplates() throws OKMException;

	void createFromTemplate(String docPath, String fldPath, String name, GWTExtendedAttributes attributes) throws OKMException;

	String getHTMLContent(String docPath, boolean checkout) throws OKMException;

	void setHTMLContent(String docPath, String mails, String users, String roles, String message, String content, String comment,
						int increaseVersion) throws OKMException;

	/*
	 * ======================== LiveEdit methods =========================
	 */
	void liveEditCheckin(String docPath, String mails, String users, String roles, String message, String comment,
						 int increaseVersion) throws OKMException;

	void liveEditForceCancelCheckout(String docPath) throws OKMException;

	void liveEditCancelCheckout(String docPath) throws OKMException;
}
