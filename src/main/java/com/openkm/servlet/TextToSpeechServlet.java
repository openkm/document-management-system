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

import com.openkm.api.OKMDocument;
import com.openkm.bean.Document;
import com.openkm.core.*;
import com.openkm.util.DocConverter;
import com.openkm.util.WebUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Only for enjoy
 */
public class TextToSpeechServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(TextToSpeechServlet.class);
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		String cmd = "espeak -v mb-es1 -f input.txt | mbrola -e /usr/share/mbrola/voices/es1 - -.wav " +
				"| oggenc -Q - -o output.ogg";
		String text = WebUtils.getString(request, "text");
		String docPath = WebUtils.getString(request, "docPath");
		response.setContentType("audio/ogg");
		FileInputStream fis = null;
		OutputStream os = null;

		try {
			if (!text.equals("")) {
				FileUtils.writeStringToFile(new File("input.txt"), text);
			} else if (!docPath.equals("")) {
				InputStream is = OKMDocument.getInstance().getContent(null, docPath, false);
				Document doc = OKMDocument.getInstance().getProperties(null, docPath);
				DocConverter.getInstance().doc2txt(is, doc.getMimeType(), new File("input.txt"));
			}

			// Convert to voice
			ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", cmd);
			Process process = pb.start();
			process.waitFor();
			String info = IOUtils.toString(process.getInputStream());
			process.destroy();

			if (process.exitValue() == 1) {
				log.warn(info);
			}

			// Send to client
			os = response.getOutputStream();
			fis = new FileInputStream("output.ogg");
			IOUtils.copy(fis, os);
			os.flush();
		} catch (InterruptedException e) {
			log.warn(e.getMessage(), e);
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
		} catch (DatabaseException e) {
			log.warn(e.getMessage(), e);
		} catch (ConversionException e) {
			log.warn(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(fis);
			IOUtils.closeQuietly(os);
		}
	}
}
