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

import com.openkm.core.DatabaseException;
import com.openkm.extractor.TextExtractorWork;
import com.openkm.extractor.TextExtractorWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Text extraction queue servlet
 */
public class TextExtractionQueueServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(TextExtractionQueueServlet.class);
	private static final int MAX_RESULTS = 20;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		ServletContext sc = getServletContext();
		request.setCharacterEncoding("UTF-8");

		try {
			List<TextExtractorWork> pending = TextExtractorWorker.getPendingWorks(MAX_RESULTS + 1);
			List<TextExtractorWork> pendingWorks = new ArrayList<TextExtractorWork>();
			Iterator<TextExtractorWork> it = pending.iterator();
			int row = 0;

			while (row < MAX_RESULTS && it.hasNext()) {
				pendingWorks.add(it.next());
			}

			sc.setAttribute("pendingWorks", pendingWorks);
			sc.setAttribute("inProgress", TextExtractorWorker.isRunning());
			sc.setAttribute("pendingSize", TextExtractorWorker.getPendingSize());
			sc.setAttribute("lastExecution", TextExtractorWorker.lastExecution());
			sc.setAttribute("inProgressWorks", TextExtractorWorker.getInProgressWorks());
			sc.getRequestDispatcher("/admin/text_extraction_queue.jsp").forward(request, response);
		} catch (DatabaseException e) {
			sendErrorRedirect(request, response, e);
		}
	}
}
