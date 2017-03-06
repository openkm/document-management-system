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
import org.slf4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtils {

	/**
	 * Return the method who make the call.
	 */
	public static StackTraceElement whoCalledMe() {
		// The constructor for Throwable has a native function that fills the stack trace.
		StackTraceElement[] trace = (new Throwable()).getStackTrace();

		// Once you have the trace you can pick out information you need.
		if (trace.length >= 2) {
			return trace[2];
		}

		return null;
	}

	/**
	 * Return the method who make the call.
	 */
	public static void logTrace(Logger log) {
		// The constructor for Throwable has a native function that fills the stack trace.
		StackTraceElement[] trace = (new Throwable()).getStackTrace();

		// Once you have the trace you can pick out information you need.
		if (trace.length >= 2) {
			for (int i = 2; i < trace.length; i++) {
				if (trace[i].getClassName().startsWith("com.openkm")) {
					StackTraceElement sse = trace[i];
					log.warn("{} -> {} ({}:{})", new Object[]{sse.getClassName(), sse.getMethodName(), sse.getFileName(), sse.getLineNumber()});
				}
			}
		}
	}

	/**
	 * Return the whole call trace.
	 */
	public static String getTrace() {
		// The constructor for Throwable has a native function that fills the stack trace.
		StackTraceElement[] trace = (new Throwable()).getStackTrace();
		StringBuilder sb = new StringBuilder();

		// Once you have the trace you can pick out information you need.
		if (trace.length >= 2) {
			for (int i = 2; i < trace.length; i++) {
				if (trace[i].getClassName().startsWith("com.openkm")) {
					sb.append(trace[i]);
					sb.append("\n");
				}
			}
		}

		return sb.toString();
	}

	/**
	 * Convert stack trace to String
	 */
	public static String toString(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		IOUtils.closeQuietly(pw);
		IOUtils.closeQuietly(sw);
		return sw.toString();
	}
}
