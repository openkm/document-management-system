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

package com.openkm.core;

import com.openkm.dao.MimeTypeDAO;
import com.openkm.dao.bean.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.util.List;

public class MimeTypeConfig {
	private static Logger log = LoggerFactory.getLogger(MimeTypeConfig.class);

	// MIME types => NOTE Keep on sync with default.sql
	public final static String MIME_UNDEFINED = "application/octet-stream";
	public final static String MIME_RTF = "application/rtf";
	public final static String MIME_PDF = "application/pdf";
	public final static String MIME_ZIP = "application/zip";
	public final static String MIME_POSTSCRIPT = "application/postscript";
	public final static String MIME_MS_WORD = "application/msword";
	public final static String MIME_MS_EXCEL = "application/vnd.ms-excel";
	public final static String MIME_MS_POWERPOINT = "application/vnd.ms-powerpoint";
	public final static String MIME_MS_WORD_2007 = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	public final static String MIME_MS_EXCEL_2007 = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public final static String MIME_MS_POWERPOINT_2007 = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
	public final static String MIME_OO_TEXT = "application/vnd.oasis.opendocument.text";
	public final static String MIME_OO_SPREADSHEET = "application/vnd.oasis.opendocument.spreadsheet";
	public final static String MIME_OO_PRESENTATION = "application/vnd.oasis.opendocument.presentation";
	public final static String MIME_SWF = "application/x-shockwave-flash";
	public final static String MIME_DXF = "image/vnd.dxf";
	public final static String MIME_DWG = "image/vnd.dwg";
	public final static String MIME_TIFF = "image/tiff";
	public final static String MIME_JPEG = "image/jpeg";
	public final static String MIME_GIF = "image/gif";
	public final static String MIME_PNG = "image/png";
	public final static String MIME_BMP = "image/bmp";
	public final static String MIME_PSD = "image/x-psd";
	public final static String MIME_ICO = "image/x-ico";
	public final static String MIME_HTML = "text/html";
	public final static String MIME_TEXT = "text/plain";
	public final static String MIME_XML = "text/xml";
	public final static String MIME_CSV = "text/csv";
	public final static String MIME_SQL = "text/x-sql";
	public final static String MIME_JAVA = "text/x-java";
	public final static String MIME_JAR = "application/x-java-archive";
	public final static String MIME_SH = "application/x-shellscript";
	public final static String MIME_BSH = "application/x-bsh";
	public final static String MIME_PHP = "application/x-php";
	public final static String MIME_SCALA = "text/x-scala";
	public final static String MIME_PYTHON = "text/x-python";
	public final static String MIME_PERL = "application/x-perl";
	public final static String MIME_JAVASCRIPT = "application/javascript";
	public final static String MIME_GROOVY = "text/x-groovy";
	public final static String MIME_DIFF = "text/x-diff";
	public final static String MIME_PASCAL = "text/x-pascal";
	public final static String MIME_CSS = "text/css";
	public final static String MIME_CSHARP = "text/x-csharp";
	public final static String MIME_CPP = "text/x-c++";
	public final static String MIME_AS3 = "application/x-font-truetype";
	public final static String MIME_APPLESCRIPT = "text/applescript";
	public final static String MIME_EML = "message/rfc822";

	//public final static String MIME_VB = "";
	//public final static String MIME_RUBY = "";
	//public final static String MIME_SASS = "";
	//public final static String MIME_POWERSHELL = "";
	//public final static String MIME_JAVAFX = "";
	//public final static String MIME_ERLANG = "";
	//public final static String MIME_COLDFUSION = "";


	// Registered MIME types
	public static MimetypesFileTypeMap mimeTypes = new MimetypesFileTypeMap();

	/**
	 * Load MIME types
	 */
	public static void loadMimeTypes() {
		try {
			List<MimeType> mimeTypeList = MimeTypeDAO.findAll("mt.id");
			MimeTypeConfig.mimeTypes = new MimetypesFileTypeMap();

			for (MimeType mt : mimeTypeList) {
				String entry = mt.getName();

				for (String ext : mt.getExtensions()) {
					entry += " " + ext;
				}

				log.debug("loadMimeTypes => Add Entry: {}", entry);
				MimeTypeConfig.mimeTypes.addMimeTypes(entry);
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
		}
	}
}
