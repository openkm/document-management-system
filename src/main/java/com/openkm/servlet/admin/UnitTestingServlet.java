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

package com.openkm.servlet.admin;

import com.openkm.util.WebUtils;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Unit Testing servlet
 */
public class UnitTestingServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(UnitTestingServlet.class);
	private static final String[][] breadcrumb = new String[][]{new String[]{"experimental.jsp", "Experimental"},};

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		String test = WebUtils.getString(request, "test");
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		OutputStream os = response.getOutputStream();
		PrintStream ps = new PrintStream(os);

		header(ps, "Unit testing", breadcrumb);
		ps.flush();

		RunListener listener = new CustomListener(ps);
		JUnitCore junit = new JUnitCore();
		junit.addListener(listener);

		if (test != null && !test.isEmpty()) {
			try {
				Class<?> clazz = Class.forName(test);
				ps.println("<b>" + clazz.getCanonicalName() + "</b><br/>");
				ps.flush();
				ps.println("<pre>");
				ps.flush();
				junit.run(clazz);
				ps.println("</pre>");
				ps.flush();
			} catch (ClassNotFoundException e) {
				warn(ps, e.getMessage());
			}
		} else {
			for (Class<?> clazz : new Reflections("com.openkm.test.api").getSubTypesOf(TestCase.class)) {
				ps.println("<a style=\"color: black; font-weight:bold;\" href=\"UnitTesting?test=" + clazz.getCanonicalName() + "\">"
						+ clazz.getCanonicalName() + "</a><br/>");
				ps.flush();
				ps.println("<pre>");
				ps.flush();
				junit.run(clazz);
				ps.println("</pre>");
				ps.flush();
			}
		}

		ps.println("<span style=\"color: blue; font-weight:bold;\">&gt;&gt;&gt; End Of Unit Testing &lt;&lt;&lt;</span>");
		footer(ps);
		ps.flush();
		IOUtils.closeQuietly(ps);
		IOUtils.closeQuietly(os);
	}

	/**
	 * Print HTML page header
	 */
	private void header(PrintStream out, String title, String[][] breadcrumb) {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		out.println("<link rel=\"Shortcut icon\" href=\"favicon.ico\" />");
		out.println("<link rel=\"stylesheet\" href=\"css/style.css\" type=\"text/css\" />");
		out.println("<title>" + title + "</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<ul id=\"breadcrumb\">");

		for (String[] elto : breadcrumb) {
			out.println("<li class=\"path\">");
			out.print("<a href=\"" + elto[0] + "\">" + elto[1] + "</a>");
			out.print("</li>");
		}

		out.println("<li class=\"path\">" + title + "</li>");
		out.println("</ul>");
		out.println("<br/>");
	}

	/**
	 * Print HTML page footer
	 */
	private void footer(PrintStream out) {
		out.println("</body>");
		out.println("</html>");
	}

	/**
	 * Print warn messages
	 */
	public void warn(PrintStream out, String msg) {
		out.print("<div class=\"warn\">" + msg + "</div>");
	}

	/**
	 * Custom listener
	 */
	class CustomListener extends TextListener {
		private PrintStream fWriter;

		public CustomListener(PrintStream writer) {
			super(writer);
			fWriter = writer;
		}

		@Override
		protected void printFailure(Failure each, String prefix) {
			fWriter.println("<span style=\"color: red\">");
			fWriter.println(prefix + ") " + each.getTestHeader() + " => " + each.getMessage());
			fWriter.println(each.getException());

			for (String line : each.getTrace().split("\\r?\\n")) {
				if (line.trim().startsWith("at com.openkm")) {
					fWriter.println(line);
				}
			}

			fWriter.println("</span>");
		}
	}
}
