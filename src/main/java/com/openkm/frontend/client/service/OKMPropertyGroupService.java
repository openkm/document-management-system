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
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.bean.form.GWTFormElement;

import java.util.List;

/**
 * OKMPropertyGroupService
 *
 * @author jllort
 *
 */
@RemoteServiceRelativePath("PropertyGroup")
public interface OKMPropertyGroupService extends RemoteService {
	public List<GWTPropertyGroup> getAllGroups() throws OKMException;

	public List<GWTPropertyGroup> getAllGroups(String path) throws OKMException;

	public void addGroup(String path, String grpName) throws OKMException;

	public List<GWTPropertyGroup> getGroups(String path) throws OKMException;

	public List<GWTFormElement> getProperties(String path, String grpName, boolean suggestion) throws OKMException;

	public void setProperties(String path, String grpName, List<GWTFormElement> formProperties) throws OKMException;

	public void removeGroup(String path, String grpName) throws OKMException;

	public List<GWTFormElement> getPropertyGroupForm(String grpName) throws OKMException;

	public List<GWTFormElement> getPropertyGroupForm(String grpName, String path, boolean suggestion) throws OKMException;
}