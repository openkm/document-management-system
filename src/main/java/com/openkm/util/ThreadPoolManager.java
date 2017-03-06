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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {
	private static Logger log = LoggerFactory.getLogger(ThreadPoolManager.class);
	private static final int POOL_TIMEOUT = 1;
	private static final int MAX_TIMEOUTS = 10;
	private static List<ThreadPoolManager> createdManagers = new ArrayList<ThreadPoolManager>();
	private ExecutorService executor;
	private String name;

	/**
	 * Create new thread pool manage.
	 */
	public ThreadPoolManager() {
		this.executor = Executors.newFixedThreadPool(Config.AVAILABLE_PROCESSORS);
		this.name = Integer.toHexString(hashCode());
		createdManagers.add(this);
	}

	/**
	 * Create new thread pool manage.
	 *
	 * @param nThreads Number of concurrent threads.
	 */
	public ThreadPoolManager(int nThreads) {
		this.executor = Executors.newFixedThreadPool(nThreads);
		this.name = Integer.toHexString(hashCode());
		createdManagers.add(this);
	}

	/**
	 * Create new thread pool manage.
	 *
	 * @param nThreads Number of concurrent threads.
	 * @param name     Name of the thread pool.
	 */
	public ThreadPoolManager(int nThreads, String name) {
		this.executor = Executors.newFixedThreadPool(nThreads);
		this.name = name;
		createdManagers.add(this);
	}

	/**
	 * Add a new thread to the manager.
	 *
	 * @param runnable The Runnable task to add.
	 */
	public synchronized void add(Runnable runnable) {
		executor.execute(runnable);
	}

	/**
	 * Shutdown pool manager.
	 */
	public void shutdown() {
		shutdown(false);
	}

	/**
	 * Shutdown pool manager.
	 *
	 * @param now If the shutdown should be right now or can wait until all thread have finished.
	 */
	public synchronized void shutdown(boolean now) {
		if (now) {
			executor.shutdownNow();
		} else {
			executor.shutdown();
		}

		log.info("### {}: All threads shutdown requested ###", name);

		try {
			for (int i = 0; !executor.awaitTermination(POOL_TIMEOUT, TimeUnit.MINUTES); i++) {
				if (i > MAX_TIMEOUTS) {
					log.info("### {}: Killing never ending tasks... ({}) ###", name, i);
					executor.shutdownNow();
				} else {
					log.info("### {}: Awaiting for pool tasks termination... ({}) ###", name, i);
				}
			}
		} catch (InterruptedException e) {
			log.warn("### {}: Exception awaiting for pool tasks termination: {} ###", name, e.getMessage());
		}

		createdManagers.remove(this);
		log.info("### {}: All threads have finished ###", name);
	}

	/**
	 * Shutdown all created poll managers.
	 */
	public static void shutdownAll() {
		shutdownAll(false);
	}

	/**
	 * Shutdown all created poll managers.
	 *
	 * @param now If the shutdown should be right now or can wait until all thread have finished.
	 */
	public static synchronized void shutdownAll(boolean now) {
		for (Iterator<ThreadPoolManager> it = createdManagers.iterator(); it.hasNext(); ) {
			ThreadPoolManager tpm = it.next();
			tpm.shutdown(now);
			it.remove();
			log.info("{}: Removed from list", tpm.name);
		}
	}
}
