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

import com.openkm.core.Config;
import com.openkm.util.FormatUtil;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

/**
 * Tail servlet
 */
public class TailServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(TailServlet.class);
	private static final String[][] breadcrumb = new String[][]{new String[]{"experimental.jsp", "Experimental"},};

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		String fsPath = WebUtils.getString(request, "fsPath");
		int timeout = WebUtils.getInt(request, "timeout", 60);
		PrintWriter out = response.getWriter();
		header(out, "Tail", breadcrumb);
		out.flush();

		out.println("<form action=\"Tail\">");
		out.println("<table class=\"form\" align=\"center\">");
		out.println("<tr>");
		out.println("<td>File</td>");
		out.println("<td colspan=\"2\"><input type=\"text\" size=\"50\" name=\"fsPath\" id=\"fsPath\" value=\"" + fsPath + "\"></td>");
		out.println("<td><a class=\"ds\" href=\"../extension/DataBrowser?action=fs&sel=doc&dst=fsPath&root=" + Config.INSTANCE_CHROOT_PATH + "\"><img src=\"img/action/browse_fs.png\"/></a></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td>Timeout</td>");
		out.println("<td><input type=\"text\" size=\"5\" name=\"timeout\" value=\"" + timeout + "\"></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td colspan=\"3\" align=\"right\"><input type=\"submit\" value=\"Send\"></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("</form>");

		try {
			File file = new File(fsPath);

			if (file.exists() && file.canRead()) {
				out.println("<hr/>");
				tail(file, out, timeout);

				// Activity log
				UserActivity.log(request.getRemoteUser(), "ADMIN_TAIL", null, null, file.getPath());
			} else {
				// sc.setAttribute("results", null);
			}
		} catch (InterruptedException e) {
			sendErrorRedirect(request, response, e);
		} finally {
			footer(out);
			out.flush();
			out.close();
		}
	}

	/**
	 * Do the magic
	 */
	private void tail(File file, PrintWriter out, int timeout) throws InterruptedException, IOException {
		long filePointer = 0;
		int toLeave = 0;
		int row = 0;
		out.println("<table class=\"results\" width=\"90%\">");
		out.println("<tr><th>#</th><th>Line</th></tr>");

		while (true) {
			long len = file.length();

			if (len < filePointer) {
				// Log must have been shorted or deleted.
				out.print("<tr class=\"fuzzy\"><td colspan=\"2\" align=\"center\">");
				out.print("<b>Log file was reset. Restarting logging from start of file.</b>");
				out.println("</td></tr>");
				out.flush();
				filePointer = 0;
				row = 0;
				toLeave = 0;
			} else if (len > filePointer) {
				// File must have had something added to it!
				RandomAccessFile raf = new RandomAccessFile(file, "r");
				raf.seek(filePointer);
				String line = null;

				while ((line = raf.readLine()) != null) {
					out.print("<tr class=\"" + (row++ % 2 == 0 ? "even" : "odd") + "\">");
					out.print("<td>" + row + "</td><td>");
					out.print(FormatUtil.escapeHtml(new String(line.getBytes("ISO-8859-1"), "UTF-8")));
					out.println("</td></tr>");
					out.flush();
				}

				filePointer = raf.getFilePointer();
				raf.close();
				toLeave = 0;
			} else {
				if (toLeave++ > timeout) {
					break;
				}
			}

			Thread.sleep(1000);
		}

		out.print("<tr class=\"fuzzy\"><td colspan=\"2\" align=\"center\">");
		out.print("<b>Log file was not modified for " + timeout + " seconds</b>");
		out.println("</td></tr>");
		out.println("</table>");
	}
}
