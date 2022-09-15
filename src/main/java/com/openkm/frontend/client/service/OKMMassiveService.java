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
import com.openkm.frontend.client.bean.form.GWTFormElement;

import java.util.List;

/**
 * @author jllort
 *
 */
@RemoteServiceRelativePath("Massive")
public interface OKMMassiveService extends RemoteService {
	void copy(List<String> paths, String fldPath) throws OKMException;

	void move(List<String> paths, String fldPath) throws OKMException;

	void delete(List<String> paths) throws OKMException;

	void addNote(List<String> paths, String text) throws OKMException;

	void addCategory(List<String> paths, String category) throws OKMException;

	void removeCategory(List<String> paths, String category) throws OKMException;

	void addKeyword(List<String> paths, String keyword) throws OKMException;

	void removeKeyword(List<String> paths, String keyword) throws OKMException;

	void addPropertyGroup(List<String> paths, String grpName) throws OKMException;

	void setProperties(List<String> paths, String grpName, List<GWTFormElement> formProperties) throws OKMException;

	void lock(List<String> paths) throws OKMException;

	void unlock(List<String> paths) throws OKMException;

	void notify(List<String> uuids, String mails, String users, String roles, String message, boolean attachment) throws OKMException;

	void forwardMail(List<String> uuids, String mails, String users, String roles, String message) throws OKMException;

	void setMixedProperties(List<String> uuidList, List<GWTFormElement> formProperties, boolean recursive) throws OKMException;
}
