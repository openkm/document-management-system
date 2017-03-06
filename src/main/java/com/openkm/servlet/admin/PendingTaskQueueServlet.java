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

import com.google.gson.Gson;
import com.openkm.bean.ChangeSecurityParams;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.PendingTaskDAO;
import com.openkm.dao.bean.PendingTask;
import com.openkm.util.pendtask.NodeStatus;
import com.openkm.util.pendtask.PendingTaskExecutor;
import com.openkm.util.pendtask.PendingTaskProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pending task queue servlet
 */
public class PendingTaskQueueServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(PendingTaskQueueServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		ServletContext sc = getServletContext();
		request.setCharacterEncoding("UTF-8");
		Gson gson = new Gson();

		try {
			List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();

			for (PendingTask pt : PendingTaskDAO.getInstance().findAll()) {
				Map<String, Object> tsk = new HashMap<String, Object>();
				tsk.put("nodeUuid", pt.getNode());
				tsk.put("nodePath", NodeBaseDAO.getInstance().getPathFromUuid(pt.getNode()));
				tsk.put("created", pt.getCreated());
				tsk.put("running", PendingTaskExecutor.isRunningTask(pt.getId()));
				tsk.put("task", pt.getTask());

				if (pt.getParams() != null && !pt.getParams().isEmpty()) {
					if (PendingTask.TASK_CHANGE_SECURITY.equals(pt.getTask())) {
						ChangeSecurityParams params = gson.fromJson(pt.getParams(), ChangeSecurityParams.class);
						tsk.put("params", params);
					} else {
						tsk.put("params", pt.getParams());
					}
				} else {
					tsk.put("params", "");
				}

				if (pt.getStatus() != null && !pt.getStatus().isEmpty()) {
					List<Map<String, String>> nsList = new ArrayList<Map<String, String>>();

					for (NodeStatus nodStatus : PendingTaskProcessor.decodeStatus(pt.getStatus())) {
						Map<String, String> ns = new HashMap<String, String>();
						ns.put("nodeUuid", nodStatus.getNode());
						ns.put("nodePath", NodeBaseDAO.getInstance().getPathFromUuid(nodStatus.getNode()));
						ns.put("status", nodStatus.getStatus());

						nsList.add(ns);
					}

					tsk.put("status", nsList);
				} else {
					tsk.put("status", "");
				}

				tasks.add(tsk);
			}

			sc.setAttribute("pendingTasks", tasks);
			sc.getRequestDispatcher("/admin/pending_task_queue.jsp").forward(request, response);
		} catch (PathNotFoundException e) {
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			sendErrorRedirect(request, response, e);
		}
	}
}
