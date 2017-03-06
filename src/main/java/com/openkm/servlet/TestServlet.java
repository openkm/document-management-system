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

import com.openkm.core.MimeTypeConfig;
import com.openkm.spring.PrincipalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Only for testing purposes
 */
public class TestServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(TestServlet.class);
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String token = (String) request.getSession().getAttribute("token");
		PrintWriter out = response.getWriter();
		log.info("Token: " + token);
		out.println("Token: " + token);
		response.setContentType(MimeTypeConfig.MIME_HTML);

		try {
			out.println("User: " + PrincipalUtils.getUser() + "<br/>");
			out.println("Roles: " + PrincipalUtils.getRoles() + "<br/>");
			out.println("Authentication: " + PrincipalUtils.getAuthentication() + "<br/>");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}
}
