/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.servlet;

import com.ibm.icu.util.Calendar;
import com.openkm.core.Config;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Rss Servlet
 */
@WebServlet("/Rss")
public class RssServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(RssServlet.class);
	private static final long serialVersionUID = 1L;
	private static String date = "";
	private static String rssData = "";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		InputStream rss = null;
		InputStream is = null;

		try {
			// Only refresh RSS one time per day
			Calendar cal = Calendar.getInstance();
			String today = String.valueOf(cal.get(Calendar.YEAR)) + String.valueOf(cal.get(Calendar.MONTH) + 1) + String.valueOf(cal.get(Calendar.DATE));

			if (date.isEmpty() || !date.equals(today)) {
				date = today;
				rss = new URL("https://www.openkm.com/news.rss?ver=com&limit=" + Config.RSS_NEWS_MAX_SIZE).openStream();
				rssData = IOUtils.toString(rss);
				log.debug("Remote RSS News\n" + rssData);
			}

			is = new ByteArrayInputStream(rssData.getBytes(StandardCharsets.UTF_8));
			response.setContentType("text/xml; charset=UTF-8");
			response.setHeader("Content-type", "application/xhtml+xml");
			PrintWriter out = response.getWriter();
			IOUtils.copy(is, out);
			out.close();
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} finally {
			IOUtils.closeQuietly(rss);
			IOUtils.closeQuietly(is);
		}
	}
}
