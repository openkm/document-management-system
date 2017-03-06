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

package com.openkm.module.common;

import com.openkm.core.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class CommonGeneralModule {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(CommonGeneralModule.class);

	/**
	 * Clean preview cache for this document
	 */
	public static void cleanPreviewCache(String uuid) {
		new File(Config.REPOSITORY_CACHE_DXF + File.separator + uuid + ".dxf").delete();
		new File(Config.REPOSITORY_CACHE_PDF + File.separator + uuid + ".pdf").delete();
		new File(Config.REPOSITORY_CACHE_SWF + File.separator + uuid + ".swf").delete();
	}
}
