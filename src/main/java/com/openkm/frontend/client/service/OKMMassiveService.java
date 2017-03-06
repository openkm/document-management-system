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
import com.openkm.frontend.client.bean.form.GWTFormElement;

import java.util.List;

/**
 * @author jllort
 *
 */
@RemoteServiceRelativePath("Massive")
public interface OKMMassiveService extends RemoteService {
	public void copy(List<String> paths, String fldPath) throws OKMException;

	public void move(List<String> paths, String fldPath) throws OKMException;

	public void delete(List<String> paths) throws OKMException;

	public void addNote(List<String> paths, String text) throws OKMException;

	public void addCategory(List<String> paths, String category) throws OKMException;

	public void removeCategory(List<String> paths, String category) throws OKMException;

	public void addKeyword(List<String> paths, String keyword) throws OKMException;

	public void removeKeyword(List<String> paths, String keyword) throws OKMException;

	public void addPropertyGroup(List<String> paths, String grpName) throws OKMException;

	public void setProperties(List<String> paths, String grpName, List<GWTFormElement> formProperties) throws OKMException;

	public void lock(List<String> paths) throws OKMException;

	public void unlock(List<String> paths) throws OKMException;

	public void notify(List<String> uuids, String mails, String users, String roles, String message, boolean attachment) throws OKMException;

	public void forwardMail(List<String> uuids, String mails, String users, String roles, String message) throws OKMException;

	public void setMixedProperties(List<String> uuidList, List<GWTFormElement> formProperties, boolean recursive) throws OKMException;
}