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

import com.openkm.core.Config;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.FormUtils;
import com.openkm.util.JBPMUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.jbpm.JbpmContext;
import org.jbpm.file.def.FileDefinition;
import org.jbpm.graph.def.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * Workflow Register Servlet
 */
public class WorkflowRegisterServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(WorkflowRegisterServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 * Handle GET and POST
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("service({}, {}", request, response);
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		try {
			String user = PrincipalUtils.getUser();

			if (Config.ADMIN_USER.equals(user)) {
				String msg = handleRequest(request);
				log.info("Status: {}", msg);
				out.print(msg);
				out.flush();
			} else {
				log.warn("Workflow should be registered by {}", Config.ADMIN_USER);
			}
		} catch (FileUploadException e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "FileUploadException: " + e.getMessage());
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "IOException: " + e.getMessage());
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	@SuppressWarnings("unchecked")
	private String handleRequest(HttpServletRequest request) throws FileUploadException, IOException, Exception {
		log.debug("handleRequest({})", request);

		if (ServletFileUpload.isMultipartContent(request)) {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<FileItem> items = upload.parseRequest(request);

			if (items.isEmpty()) {
				String msg = "No process file in the request";
				log.warn(msg);
				return msg;
			} else {
				FileItem fileItem = (FileItem) items.get(0);

				if (fileItem.getContentType().indexOf("application/x-zip-compressed") == -1) {
					String msg = "Not a process archive";
					log.warn(msg);
					throw new Exception(msg);
				} else {
					log.info("Deploying process archive: {}", fileItem.getName());
					JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
					InputStream isForms = null;
					ZipInputStream zis = null;

					try {
						zis = new ZipInputStream(fileItem.getInputStream());
						ProcessDefinition processDefinition = ProcessDefinition.parseParZipInputStream(zis);

						// Check XML form definition
						FileDefinition fileDef = processDefinition.getFileDefinition();
						isForms = fileDef.getInputStream("forms.xml");
						FormUtils.parseWorkflowForms(isForms);

						log.debug("Created a processdefinition: {}", processDefinition.getName());
						jbpmContext.deployProcessDefinition(processDefinition);
						return "Process " + processDefinition.getName() + " deployed successfully";
					} finally {
						IOUtils.closeQuietly(isForms);
						IOUtils.closeQuietly(zis);
						jbpmContext.close();
					}
				}
			}
		} else {
			log.warn("Not a multipart request");
			return "Not a multipart request";
		}
	}
}
