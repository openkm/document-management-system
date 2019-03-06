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

package com.openkm.servlet.frontend;

import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.writer.CSVWriter;
import com.googlecode.jcsv.writer.internal.CSVWriterBuilder;
import com.googlecode.jcsv.writer.internal.DefaultCSVEntryConverter;
import com.openkm.core.*;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.util.CSVUtil;
import com.openkm.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * CSVExporterServlet http://code.google.com/p/jcsv/
 *
 * @author jllort
 */
public class CSVExporterServlet extends OKMHttpServlet {
	private static Logger log = LoggerFactory.getLogger(CSVExporterServlet.class);
	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		log.debug("service({}, {})", request, response);
		
		List<String[]> csvValues = new ArrayList<String[]>();
		boolean compact = WebUtils.getBoolean(request, "compact");
		String action = WebUtils.getString(request, "action");
		String lang = WebUtils.getString(request, "lang");
		String json = WebUtils.getString(request, "json");		
		json = URLDecoder.decode(json, "UTF-8");
		String fileName = "";
		
		try {
			if (action.equals("find")) {
				fileName = CSVUtil.createFind(lang, request.getRemoteUser(), csvValues, json, compact);
			} else if (action.equals("findSimpleQuery")) {
				fileName = CSVUtil.createFindSimpleQuery(lang, request.getRemoteUser(), csvValues, json);
			}

			// Prepare file headers
			WebUtils.prepareSendFile(request, response, fileName, MimeTypeConfig.MIME_CSV, false);

			// CSVWriter
			CSVStrategy strategyFormat = new CSVStrategy(Config.CSV_FORMAT_DELIMITER.toCharArray()[0],
					Config.CSV_FORMAT_QUOTE_CHARACTER.toCharArray()[0],
					Config.CSV_FORMAT_COMMENT_INDICATOR.toCharArray()[0], Config.CSV_FORMAT_SKIP_HEADER,
					Config.CSV_FORMAT_IGNORE_EMPTY_LINES);
			Writer out = new OutputStreamWriter(response.getOutputStream());
			CSVWriter<String[]> csvWriter = new CSVWriterBuilder<String[]>(out).strategy(strategyFormat)
					.entryConverter(new DefaultCSVEntryConverter()).build();
			csvWriter.writeAll(csvValues);
			csvWriter.flush();
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMCSVExporterService,
					ErrorCode.CAUSE_Parse), e.getMessage()));
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMCSVExporterService,
					ErrorCode.CAUSE_Repository), e.getMessage()));
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMCSVExporterService,
					ErrorCode.CAUSE_Database), e.getMessage()));
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMCSVExporterService,
					ErrorCode.CAUSE_AccessDenied), e.getMessage()));
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMCSVExporterService,
					ErrorCode.CAUSE_PathNotFound), e.getMessage()));
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMCSVExporterService,
					ErrorCode.CAUSE_PrincipalAdapter), e.getMessage()));
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMCSVExporterService,
					ErrorCode.CAUSE_NoSuchGroup), e.getMessage()));
		}
	}
}