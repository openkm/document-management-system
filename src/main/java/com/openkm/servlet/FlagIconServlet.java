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

package com.openkm.servlet;

import com.openkm.core.DatabaseException;
import com.openkm.dao.LanguageDAO;
import com.openkm.dao.bean.Language;
import com.openkm.util.SecureStore;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Flag Icon Servlet
 */
public class FlagIconServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(MimeIconServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String lgId = request.getPathInfo();
		OutputStream os = null;

		try {
			if (lgId.length() > 1) {
				Language language = LanguageDAO.findByPk(lgId.substring(1)); // The first character / must be removed

				if (language != null) {
					byte[] img = SecureStore.b64Decode(new String(language.getImageContent()));
					response.setContentType(language.getImageMime());
					response.setContentLength(img.length);
					os = response.getOutputStream();
					os.write(img);
					os.flush();
				}
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(os);
		}
	}
}
