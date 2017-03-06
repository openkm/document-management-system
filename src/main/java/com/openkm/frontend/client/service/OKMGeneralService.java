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
import com.openkm.frontend.client.bean.GWTConfig;
import com.openkm.frontend.client.bean.GWTConverterStatus;
import com.openkm.frontend.client.bean.GWTFileUploadingStatus;
import com.openkm.frontend.client.bean.GWTTestMail;

import java.util.List;

/**
 * @author jllort
 *
 */
@RemoteServiceRelativePath("General")
public interface OKMGeneralService extends RemoteService {
	public GWTFileUploadingStatus getFileUploadStatus();

	public GWTConverterStatus getConversionStatus();

	public GWTTestMail testMailConnection(String protocol, String host, String user, String password, String mailFolder);

	public List<String> getEnabledExtensions() throws OKMException;

	public GWTConfig getConfigValue(String key) throws OKMException;
}