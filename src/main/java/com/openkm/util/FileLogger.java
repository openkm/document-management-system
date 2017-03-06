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

import com.openkm.core.Config;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLogger {
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";
	private static final String LEVEL_INFO = "INFO ";
	private static final String LEVEL_WARN = "WARN ";
	private static final String LEVEL_ERROR = "ERROR";

	private Writer cLogger = null;

	/**
	 * Create a new file logger.
	 *
	 * @param baseName The base name of the log file
	 * @throws IOException If there is an exception when creating.
	 */
	public FileLogger(String baseName) throws IOException {
		cLogger = new FileWriter(getLogFile(baseName), true);
	}

	/**
	 * Write message to log file.
	 *
	 * @param message Message to write.
	 * @throws IOException If there is an exception when writing.
	 */
	public void info(String message, Object... params) throws IOException {
		cLogger.write(getLogEntry(LEVEL_INFO, message, params));
		cLogger.flush();
	}

	/**
	 * Write message to log file.
	 *
	 * @param message Message to write.
	 * @throws IOException If there is an exception when writing.
	 */
	public void warn(String message, Object... params) throws IOException {
		cLogger.write(getLogEntry(LEVEL_WARN, message, params));
		cLogger.flush();
	}

	/**
	 * Write message to log file.
	 *
	 * @param message Message to write.
	 * @throws IOException If there is an exception when writing.
	 */
	public void error(String message, Object... params) throws IOException {
		cLogger.write(getLogEntry(LEVEL_ERROR, message, params));
		cLogger.flush();
	}

	/**
	 * Close log file.
	 */
	public void close() {
		IOUtils.closeQuietly(cLogger);
	}

	/**
	 * Static file logger.
	 *
	 * @throws IOException If there is an exception when writing.
	 */
	public static void info(String baseName, String message, Object... params) throws IOException {
		logWrite(baseName, LEVEL_INFO, message, params);
	}

	/**
	 * Static file logger.
	 *
	 * @throws IOException If there is an exception when writing.
	 */
	public static void warn(String baseName, String message, Object... params) throws IOException {
		logWrite(baseName, LEVEL_WARN, message, params);
	}

	/**
	 * Static file logger.
	 *
	 * @throws IOException If there is an exception when writing.
	 */
	public static void error(String baseName, String message, Object... params) throws IOException {
		logWrite(baseName, LEVEL_ERROR, message, params);
	}

	/**
	 * Write to log file
	 *
	 * @throws IOException If there is an exception when writing.
	 */
	private static void logWrite(String baseName, String level, String message, Object... params) throws IOException {
		Writer sLogger = new FileWriter(getLogFile(baseName), true);
		sLogger.write(getLogEntry(level, message, params));
		sLogger.flush();
		sLogger.close();
	}

	/**
	 * Create a log file name from a base name.
	 *
	 * @param baseName The base name to construct the file name.
	 * @return The result file name.
	 */
	private static String getLogFile(String baseName) {
		String fileDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		return Config.LOG_DIR + File.separator + baseName + "_" + fileDate + ".log";
	}

	/**
	 * Build a long entry.
	 *
	 * @param message Message to log.
	 * @param params Optional menssage params.
	 * @return An String with the long entry.
	 */
	private static String getLogEntry(String level, String message, Object... params) {
		StringBuilder sb = new StringBuilder();
		sb.append(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
		sb.append(" ");
		sb.append(level);
		sb.append(" ");
		sb.append(MessageFormat.format(message, params));
		sb.append("\n");
		return sb.toString();
	}
}
