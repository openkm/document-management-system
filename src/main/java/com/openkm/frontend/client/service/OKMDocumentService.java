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
	public List<GWTDocument> getChilds(String fldId, Map<String, GWTFilter> mapFilter) throws OKMException;

	public List<GWTVersion> getVersionHistory(String docPath) throws OKMException;

	public void delete(String docPath) throws OKMException;

	public void checkout(String docPath) throws OKMException;

	public void cancelCheckout(String docPath) throws OKMException;

	public void lock(String docPath) throws OKMException;

	public void unlock(String docPath) throws OKMException;

	public GWTDocument rename(String docPath, String newName) throws OKMException;

	public void move(String docPath, String destPath) throws OKMException;

	public void purge(String docPath) throws OKMException;

	public void restoreVersion(String docPath, String versionId) throws OKMException;

	public GWTDocument get(String docPath) throws OKMException;

	public void copy(String docPath, String fldPath) throws OKMException;

	public Boolean isValid(String docPath) throws OKMException;

	public Long getVersionHistorySize(String docPath) throws OKMException;

	public void purgeVersionHistory(String docPath) throws OKMException;

	public void forceUnlock(String docPath) throws OKMException;

	public void forceCancelCheckout(String docPath) throws OKMException;

	public GWTDocument createFromTemplate(String docPath, String destinationPath, List<GWTFormElement> formProperties,
	                                      Map<String, List<Map<String, String>>> tableProperties) throws OKMException;

	public String updateFromTemplate(String docPath, String destinationPath, List<GWTFormElement> formProperties,
	                                 Map<String, List<Map<String, String>>> tableProperties) throws OKMException;

	public String convertToPdf(String docPath) throws OKMException;

	public void mergePdf(String docName, List<String> paths) throws OKMException;

	public List<GWTDocument> getAllTemplates() throws OKMException;

	public void createFromTemplate(String docPath, String fldPath, String name, GWTExtendedAttributes attributes) throws OKMException;

	public String getHTMLContent(String docPath, boolean checkout) throws OKMException;

	public void setHTMLContent(String docPath, String mails, String users, String roles, String message, String content, String comment,
	                           int increaseVersion) throws OKMException;

	/*
	 * ======================== LiveEdit methods =========================
	 */
	public void liveEditCheckin(String docPath, String mails, String users, String roles, String message, String comment,
	                            int increaseVersion) throws OKMException;

	public void liveEditForceCancelCheckout(String docPath) throws OKMException;

	public void liveEditCancelCheckout(String docPath) throws OKMException;
}