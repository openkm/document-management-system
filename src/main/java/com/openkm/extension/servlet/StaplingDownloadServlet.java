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

package com.openkm.extension.servlet;

import com.openkm.api.OKMDocument;
import com.openkm.api.OKMRepository;
import com.openkm.core.*;
import com.openkm.extension.dao.StapleGroupDAO;
import com.openkm.extension.dao.bean.Staple;
import com.openkm.extension.dao.bean.StapleGroup;
import com.openkm.util.ArchiveUtils;
import com.openkm.util.FileUtils;
import com.openkm.util.WebUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Staple Download Servlet
 */
public class StaplingDownloadServlet extends BaseServlet {
	private static Logger log = LoggerFactory.getLogger(StaplingDownloadServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		int sgId = WebUtils.getInt(request, "sgId");
		File tmpZip = File.createTempFile("okm", ".zip");

		try {
			String archive = "stapling.zip";
			response.setHeader("Content-disposition", "attachment; filename=\"" + archive + "\"");
			response.setContentType(MimeTypeConfig.MIME_ZIP);
			OutputStream out = response.getOutputStream();
			exportZip(sgId, out);
			out.flush();
			out.close();
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "RepositoryException: " + e.getMessage());
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} finally {
			FileUtils.deleteQuietly(tmpZip);
		}
	}

	/**
	 * Generate a zip file from a repository folder path   
	 */
	private void exportZip(int sgId, OutputStream os) throws RepositoryException, IOException,
			DatabaseException {
		log.debug("exportZip({}, {})", sgId, os);
		File tmpDir = null;
		BufferedWriter bw = null;
		OKMDocument okmDoc = OKMDocument.getInstance();
		OKMRepository okmRepo = OKMRepository.getInstance();

		try {
			tmpDir = FileUtils.createTempDir();
			bw = new BufferedWriter(new FileWriter(new File(tmpDir, "summary.txt")));
			StapleGroup sg = StapleGroupDAO.findByPk(sgId);

			for (Staple s : sg.getStaples()) {
				String nodeUuid = s.getNode();
				String path = null;

				try {
					path = okmRepo.getNodePath(null, nodeUuid);

					if (path != null) {
						int idx = path.indexOf('/', 1);

						if (idx > 0) {
							String relPath = path.substring(idx);
							File expFile = new File(tmpDir, relPath);
							expFile.getParentFile().mkdirs();
							FileOutputStream fos = new FileOutputStream(expFile);

							if (okmDoc.isValid(null, path)) {
								InputStream is = okmDoc.getContent(null, path, false);
								IOUtils.copy(is, fos);
								is.close();
							}

							bw.write(path + " - OK\n");
							bw.flush();
						}
					}
				} catch (PathNotFoundException e) {
					bw.write(path != null ? path : nodeUuid + " - " + e.getMessage() + "\n");
					bw.flush();
				} catch (AccessDeniedException e) {
					bw.write(path != null ? path : nodeUuid + " - " + e.getMessage() + "\n");
					bw.flush();
				}
			}

			// Zip files
			IOUtils.closeQuietly(bw);
			ArchiveUtils.createZip(tmpDir, "stapling", os);
		} catch (IOException e) {
			log.error("Error exporting zip", e);
			throw e;
		} finally {
			if (tmpDir != null) {
				try {
					org.apache.commons.io.FileUtils.deleteDirectory(tmpDir);
				} catch (IOException e) {
					log.error("Error deleting temporal directory", e);
					throw e;
				}
			}
		}

		log.debug("exportZip: void");
	}
}
