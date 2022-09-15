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
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.widget.filebrowser.GWTFilter;

import java.util.List;
import java.util.Map;

/**
 * @author jllort
 */
@RemoteServiceRelativePath("Mail")
public interface OKMMailService extends RemoteService {
	List<GWTMail> getChilds(String fldId, Map<String, GWTFilter> mapFilter) throws OKMException;

	void delete(String mailPath) throws OKMException;

	void move(String docPath, String destPath) throws OKMException;

	void purge(String mailPath) throws OKMException;

	void copy(String mailPath, String fldPath) throws OKMException;

	GWTMail getProperties(String mailPath) throws OKMException;

	GWTMail rename(String mailId, String newName) throws OKMException;

	Boolean isValid(String mailPath) throws OKMException;

	void forwardMail(String mailPath, String mails, String users, String roles, String message) throws OKMException;

	GWTMail sendMail(List<String> uuidList, Map<String, List<String>> recipients, String subject, String message,
					 boolean attachment) throws OKMException;

	GWTMail sendMail(List<String> uuidList, Map<String, List<String>> recipients, String subject, String message,
					 boolean attachment, String storePath) throws OKMException;

	List<GWTDocument> getAttachments(String uuid) throws OKMException;
}
