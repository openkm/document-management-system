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

import com.google.gson.Gson;
import com.openkm.bean.ChangeSecurityParams;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.dao.HibernateUtil;
import com.openkm.dao.bean.PendingTask;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Execute pending tasks.
 */
public class PendingTaskExecutor {
	private static Logger log = LoggerFactory.getLogger(PendingTaskExecutor.class);
	private static Set<Long> runningTasks = new HashSet<Long>();
	public static volatile boolean running = true;
	private static ExecutorService executor = Executors.newFixedThreadPool(Config.AVAILABLE_PROCESSORS,
			new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r);
					t.setName("PendingTaskThread-" + t.getId());
					t.setDaemon(true);
					return t;
				}
			});

	/**
	 * Register running task.
	 */
	public static synchronized void addRunningTask(Long tkId) {
		runningTasks.add(tkId);
	}

	/**
	 * Deregister running task.
	 */
	public static synchronized void removeRunningTask(Long tkId) {
		runningTasks.remove(tkId);
	}

	/**
	 * Check if task is running.
	 */
	public static boolean isRunningTask(Long tkId) {
		return runningTasks.contains(tkId);
	}

	/**
	 * Return running tasks.
	 */
	public static Set<Long> getRunningTasks() {
		return runningTasks;
	}

	/**
	 * Process pending tasks
	 */
	public void run() throws DatabaseException {
		log.debug("*** Begin process pending tasks ***");

		if (!Config.SYSTEM_READONLY) {
			processQueue();
		} else {
			log.warn("*** Pending tasks disabled because system is readonly ***");
		}

		log.debug("*** End process pending tasks ***");
	}

	/**
	 * Shutdown executor right now.
	 */
	public static void shutdown() {
		running = false;
		executor.shutdown();
		log.info("### All threads shutdown requested ###");

		try {
			for (int i = 0; !executor.awaitTermination(10, TimeUnit.SECONDS); i++) {
				log.info("### Awaiting for pending task pool termination... ({}) ###", i);
			}
		} catch (InterruptedException e) {
			log.warn("### Exception awaiting for pending task pool termination: {} ###", e.getMessage());
		}

		log.info("### All threads have finished ###");
	}

	/**
	 * Process pending task queue
	 */
	@SuppressWarnings("unchecked")
	private void processQueue() throws DatabaseException {
		String qs = "from PendingTask pt order by pt.created";
		Gson gson = new Gson();
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);

			for (PendingTask pt : (List<PendingTask>) q.list()) {
				if (!runningTasks.contains(pt.getId())) {
					log.info("Processing {}", pt);

					if (PendingTask.TASK_UPDATE_PATH.equals(pt.getTask())) {
						if (Config.STORE_NODE_PATH) {
							executor.execute(new PendingTaskThread(pt, new UpdatePathTask()));
						}
					} else if (PendingTask.TASK_CHANGE_SECURITY.equals(pt.getTask())) {
						ChangeSecurityParams params = gson.fromJson(pt.getParams(), ChangeSecurityParams.class);
						executor.execute(new PendingTaskThread(pt, new ChangeSecurityTask(params)));
					} else {
						log.warn("Unknown pending task: {}", pt.getTask());
					}
				} else {
					log.info("Task already running: {}", pt);
				}
			}
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}
