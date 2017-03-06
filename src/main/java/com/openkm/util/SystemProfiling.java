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

package com.openkm.util;

import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.dao.ProfilingDAO;
import com.openkm.dao.bean.Profiling;
import com.openkm.spring.PrincipalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 *
 * @author pavila
 */
public class SystemProfiling {
	private static Logger log = LoggerFactory.getLogger(SystemProfiling.class);

	/**
	 * Log system profiling
	 *
	 * @param clazz The class which generate the action.
	 * @param method The method of the class which generate the action.
	 * @param time Time in milliseconds to register.
	 */
	public static void log(String params, long time) {
		if (Config.SYSTEM_PROFILING) {
			try {
				StackTraceElement[] trace = (new Throwable()).getStackTrace();

				// Once you have the trace you can pick out information you need.
				if (trace.length >= 1) {
					StringBuilder sb = new StringBuilder();
					StackTraceElement caller = trace[1];

					if (trace.length >= 2) {
						for (int i = 2; i < trace.length; i++) {
							if (trace[i].getClassName().startsWith("com.openkm")) {
								sb.append(trace[i]);
								sb.append("\n");
							}
						}
					}

					Profiling vo = new Profiling();
					vo.setDate(Calendar.getInstance());
					vo.setUser(PrincipalUtils.getUser());
					vo.setClazz(caller.getClassName());
					vo.setMethod(caller.getMethodName());
					vo.setParams(params);
					vo.setTime(time);
					vo.setTrace(sb.toString());
					ProfilingDAO.create(vo);
				}
			} catch (DatabaseException e) {
				log.error(e.getMessage());
			}
		}
	}
}
