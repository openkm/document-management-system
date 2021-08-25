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

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.openkm.api.OKMDocument;
import com.openkm.core.*;
import com.openkm.servlet.admin.BaseServlet;
import com.openkm.util.WebUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * Syntax Highlighter Servlet
 */
public class SyntaxHighlighterServlet extends BaseServlet {
	private static final Logger log = LoggerFactory.getLogger(SyntaxHighlighterServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		String uuid = WebUtils.getString(request, "uuid");
		String mimeType = WebUtils.getString(request, "mimeType");
		String core = WebUtils.getString(request, "core");
		String theme = WebUtils.getString(request, "theme");
		CharsetDetector detector = new CharsetDetector();
		InputStream fis = null;

		try {
			fis = OKMDocument.getInstance().getContent(null, uuid, false);
			detector.setText(new BufferedInputStream(fis));
			CharsetMatch cm = detector.detect();
			String content = cm.getString();
			handlePreviewContent(request, response, mimeType, content, core, theme);
		} catch (PathNotFoundException | AccessDeniedException | DatabaseException | RepositoryException | LockException e) {
			sendErrorRedirect(request, response, e);
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}

	/**
	 * Used when syntax highlight
	 */
	private void sendHighlight(HttpServletRequest request, HttpServletResponse response, String brushType, String brush,
	                           String core, String theme, String content) throws ServletException, IOException {
		ServletContext sc = getServletContext();
		sc.setAttribute("brushType", brushType);
		sc.setAttribute("brush", brush);
		sc.setAttribute("cssCore", core);
		sc.setAttribute("cssTheme", theme);
		sc.setAttribute("content", content);
		sc.getRequestDispatcher("/syntax_highlighter.jsp").forward(request, response);
	}

	/**
	 * Used when no syntax highlight
	 */
	private void sendContent(HttpServletRequest request, HttpServletResponse response, String content) throws IOException {
		PrintWriter out = response.getWriter();
		out.print(content);
		out.flush();
		IOUtils.closeQuietly(out);
	}

	/**
	 * Handle syntax highlight representation
	 */
	private void handlePreviewContent(HttpServletRequest request, HttpServletResponse response, String mimeType,
	                                  String content, String core, String theme) throws ServletException, IOException {
		if (mimeType.equals("text/html")) {
			sendContent(request, response, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_JAVA) || mimeType.equals(MimeTypeConfig.MIME_BSH)) {
			sendHighlight(request, response, "java", "shBrushJava.js", core, theme, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_XML)) {
			sendHighlight(request, response, "xml", "shBrushXml.js", core, theme, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_SQL)) {
			sendHighlight(request, response, "sql", "shBrushSql.js", core, theme, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_SCALA)) {
			sendHighlight(request, response, "scala", "shBrushScala.js", core, theme, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_PYTHON)) {
			sendHighlight(request, response, "python", "shBrushPython.js", core, theme, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_PHP)) {
			sendHighlight(request, response, "php", "shBrushPhp.js", core, theme, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_PERL)) {
			sendHighlight(request, response, "perl", "shBrushPerl.js", core, theme, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_TEXT)) {
			sendHighlight(request, response, "plain", "shBrushPlain.js", core, theme, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_JAVASCRIPT)) {
			sendHighlight(request, response, "javascript", "shBrushJScript.js", core, theme, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_GROOVY)) {
			sendHighlight(request, response, "groovy", "shBrushGroovy.js", core, theme, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_DIFF)) {
			sendHighlight(request, response, "diff", "shBrushDiff.js", core, theme, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_PASCAL)) {
			sendHighlight(request, response, "pascal", "shBrushDelphi.js", core, theme, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_CSS)) {
			sendHighlight(request, response, "css", "shBrushCss.js", core, theme, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_CSHARP)) {
			sendHighlight(request, response, "csharp", "shBrushCSharp.js", core, theme, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_CPP)) {
			sendHighlight(request, response, "c", "shBrushCpp.js", core, theme, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_SH)) {
			sendHighlight(request, response, "bash", "shBrushBash.js", core, theme, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_AS3)) {
			sendHighlight(request, response, "as3", "shBrushAS3.js", core, theme, content);
		} else if (mimeType.equals(MimeTypeConfig.MIME_APPLESCRIPT)) {
			sendHighlight(request, response, "applescript", "shBrushAppleScript.js", core, theme, content);
		} else {
			sendContent(request, response, content);
		}
	}
}
