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

package com.openkm.util.pendtask;

import com.openkm.bean.ChangeSecurityParams;
import com.openkm.core.DatabaseException;
import com.openkm.dao.bean.PendingTask;
import com.openkm.spring.PrincipalUtils;
import com.openkm.spring.SecurityHolder;
import com.openkm.util.SystemProfiling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

/**
 * @author pavila
 */
public class PendingTaskThread implements Runnable {
	private static Logger log = LoggerFactory.getLogger(PendingTaskThread.class);
	private static volatile long global = 1;
	private long id = 0;
	private PendingTask task = null;
	private ProcessInDepthTask processor = null;

	public PendingTaskThread(PendingTask task, ProcessInDepthTask processor) {
		this.task = task;
		this.processor = processor;
		this.id = global++;
	}

	@Override
	public void run() {
		try {
			long begin = System.currentTimeMillis();
			log.debug("processPendingTask.Working {} on {}", id, task);

			if (processor instanceof ChangeSecurityTask) {
				try {
					ChangeSecurityParams params = ((ChangeSecurityTask) processor).getParams();
					Authentication auth = PrincipalUtils.createAuthentication(params.getUser(), params.getRoles());
					SecurityHolder.set(auth);
					new PendingTaskProcessor(task).processInDepth(processor);
				} finally {
					SecurityHolder.unset();
				}
			} else {
				new PendingTaskProcessor(task).processInDepth(processor);
			}

			log.debug("processPendingTask.Finish {} on {}", id, task);
			SystemProfiling.log(task.getTask() + ", " + task.getParams(), System.currentTimeMillis() - begin);
			log.trace("processPendingTasks.Time: {}", System.currentTimeMillis() - begin);
		} catch (DatabaseException e) {
			log.warn(e.getMessage(), e);
		}
	}
}
