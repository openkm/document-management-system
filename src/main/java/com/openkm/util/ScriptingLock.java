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

/**
 * Lock
 *
 * @author jllort
 */
public class ScriptingLock {
	private static ScriptingLock instance = new ScriptingLock();

	boolean isLocked = false;
	Thread lockedBy = null;
	int lockedCount = 0;

	/**
	 * getInstance
	 */
	public static ScriptingLock getInstance() {
		return instance;
	}

	/**
	 * lock
	 */
	public synchronized void lock() throws InterruptedException {
		Thread callingThread = Thread.currentThread();

		while (isLocked) {
			wait();
		}

		isLocked = true;
		lockedCount++;
		lockedBy = callingThread;
	}

	/**
	 * unlock
	 */
	public synchronized void unlock() {
		if (Thread.currentThread() == this.lockedBy) {
			lockedCount--;

			if (lockedCount == 0) {
				isLocked = false;
				notify();
			}
		}
	}
}