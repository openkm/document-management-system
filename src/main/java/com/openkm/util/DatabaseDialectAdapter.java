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

package com.openkm.util;

import org.apache.commons.io.IOUtils;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.SQLServerDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseDialectAdapter {
	private static Logger log = LoggerFactory.getLogger(DatabaseDialectAdapter.class);

	/**
	 * Adapt "default.sql" to every supported database
	 */
	public static String dialectAdapter(InputStream is, String dialect) {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		String line;

		try {
			br = new BufferedReader(new InputStreamReader(is));

			if (Oracle10gDialect.class.getCanonicalName().equals(dialect)) {
				log.info("Generation SQL for Oracle10gDialect...");

				while ((line = br.readLine()) != null) {
					sb.append(oracleAdapter(line)).append("\n");
				}
			} else if (SQLServerDialect.class.getCanonicalName().equals(dialect)) {
				log.info("Generation SQL for SQLServerDialect...");

				while ((line = br.readLine()) != null) {
					sb.append(sqlServerAdapter(line)).append("\n");
				}
			} else {
				log.info("Generation SQL for GeneralDialect...");

				while ((line = br.readLine()) != null) {
					sb.append(line).append("\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(is);
		}

		return sb.toString().trim();
	}

	/**
	 * Oracle special stuff
	 */
	private static String oracleAdapter(String line) {
		log.debug("oracleAdapter({})", line);
		String str;

		if (line.startsWith("INSERT INTO OKM_FORUM")) {
			str = line.replaceAll("NOW\\(\\)", "SYSDATE");
		} else if (line.startsWith("INSERT INTO OKM_WIKI_PAGE")) {
			str = line.replaceAll("NOW\\(\\)", "SYSDATE");
		} else {
			str = line;
		}

		log.debug("oracleAdapter: {}", str);
		return str;
	}

	/**
	 * SQL Server special stuff
	 */
	private static String sqlServerAdapter(String line) {
		log.debug("sqlServerAdapter({})", line);
		StringBuilder sb = new StringBuilder();

		if (line.startsWith("INSERT INTO OKM_PROFILE")) {
			sb.append("SET IDENTITY_INSERT OKM_PROFILE ON;").append("\n");
			sb.append(line).append("\n");
			sb.append("SET IDENTITY_INSERT OKM_PROFILE OFF;");
		} else if (line.startsWith("INSERT INTO OKM_MIME_TYPE")) {
			sb.append("SET IDENTITY_INSERT OKM_MIME_TYPE ON;").append("\n");
			sb.append(line).append("\n");
			sb.append("SET IDENTITY_INSERT OKM_MIME_TYPE OFF;");
		} else if (line.startsWith("INSERT INTO OKM_FORUM")) {
			sb.append("SET IDENTITY_INSERT OKM_FORUM ON;").append("\n");
			sb.append(line.replaceAll("NOW\\(\\)", "GETDATE()")).append("\n");
			sb.append("SET IDENTITY_INSERT OKM_FORUM OFF;");
		} else if (line.startsWith("INSERT INTO OKM_WIKI_PAGE")) {
			sb.append("SET IDENTITY_INSERT OKM_WIKI_PAGE ON;").append("\n");
			sb.append(line.replaceAll("NOW\\(\\)", "GETDATE()")).append("\n");
			sb.append("SET IDENTITY_INSERT OKM_WIKI_PAGE OFF;");
		} else {
			sb.append(line);
		}

		log.debug("sqlServerAdapter: {}", sb.toString());
		return sb.toString();
	}
}
