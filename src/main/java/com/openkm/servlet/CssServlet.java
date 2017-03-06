/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.servlet;

import com.openkm.core.DatabaseException;
import com.openkm.core.MimeTypeConfig;
import com.openkm.dao.CssDAO;
import com.openkm.dao.bean.Css;
import com.openkm.util.WebUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Css Styles Servlet
 */
public class CssServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(CssServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String path = request.getPathInfo();
		OutputStream os = null;

		try {
			if (path.length() > 1) {
				String[] foo = path.substring(1).split("/");

				if (foo.length > 1) {
					String context = foo[0];
					String name = foo[1];
					Css css = CssDAO.getInstance().findByContextAndName(context, name);

					if (css == null) {
						InputStream is = null;

						try {
							if (Css.CONTEXT_FRONTEND.equals(context)) {

							} else if (Css.CONTEXT_EXTENSION.equals(context)) {
								if ("htmlEditor".equals(name)) {
									is = getServletContext().getResourceAsStream("/css/tiny_mce/content.css");
								}
							}

							if (is != null) {
								css = new Css();
								css.setContent(IOUtils.toString(is));
								css.setContext(context);
								css.setName(name);
								css.setActive(true);
							}
						} finally {
							IOUtils.closeQuietly(is);
						}
					}

					if (css != null) {
						// Prepare file headers
						WebUtils.prepareSendFile(request, response, css.getName() + ".css", MimeTypeConfig.MIME_CSS, false);
						PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF8"), true);
						out.append(css.getContent());
						out.flush();
					}
				}
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(os);
		}
	}
}
