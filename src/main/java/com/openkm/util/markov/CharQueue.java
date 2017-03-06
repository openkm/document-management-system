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

package com.openkm.util.markov;

import java.util.InputMismatchException;

/**
 * Keeps a fixed-length queue of characters.  There are only three
 * operations on the queue: set the whole thing; append a character
 * (dropping the first); and retreive the whole thing.  This is useful
 * as a moving window on a text stream.
 *
 * @author Lawrence Kesteloot
 * @author Paco Avila
 */
class CharQueue {
	private int length;
	private char[] queue;
	private int count;

	/**
	 * Create the queue with a fixed length.  The queue will be
	 * filled with the value 0, so don't use the toString()
	 * method until the queue has been filled with either
	 * set() or put().
	 */
	public CharQueue(int length) {
		this.length = length;
		queue = new char[length];
		count = 0;
	}

	/**
	 * Sets the contents of the queue. The length of the string
	 * must be the same as the length passed to the constructor.
	 */
	public void set(String s) throws InputMismatchException {
		if (s.length() != length) {
			throw new InputMismatchException("Lengths don't match");
		}

		queue = s.toCharArray();
		count = length;
	}

	/**
	 * Appends the character to the queue.  If the resulting queue
	 * would be longer than the length set in the constructor, then
	 * the first character is dropped.
	 */
	public void put(char c) {
		if (count == length) {
			System.arraycopy(queue, 1, queue, 0, length - 1);
			count--;
		}

		queue[count++] = c;
	}

	/**
	 * Returns the contents of the queue as a string.  This does
	 * not take into account the number of characters that have
	 * been put into the queue.  The returned string's length
	 * is always the length passed to the constructor.
	 */
	public String toString() {
		return new String(queue);
	}
}
