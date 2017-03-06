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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.*;

/**
 * A Markov chain for characters. For each set of prefix strings, keeps track of possible next characters and the
 * probability of going to each.
 *
 * @author Lawrence Kesteloot
 * @author Paco Avila
 */
public class Markov {
	private static Logger log = LoggerFactory.getLogger(Markov.class);

	/**
	 * Map from the prefix string (String) to list of characters (Chain).
	 */
	private Map<String, Chain> map;
	private String bootstrapPrefix;

	/**
	 * Creates a chain based on the Reader with a prefix of length "length". Reads the entire input stream and creates
	 * the Markov chain.
	 */
	public Markov(Reader in, int length) throws java.io.IOException {
		map = new HashMap<String, Chain>();
		CharQueue queue = new CharQueue(length);
		int c;

		for (int i = 0; i < length; i++) {
			c = in.read();
			if (c == -1) {
				log.warn("Input is too short");
				return;
			}

			queue.put((char) c);
		}

		bootstrapPrefix = queue.toString();

		// for collapsing whitespace
		boolean wasWhitespace = false;

		while ((c = in.read()) != -1) {
			if (Character.isWhitespace((char) c)) {
				if (wasWhitespace) {
					// collapse continuous whitespace
					continue;
				}

				c = ' ';
				wasWhitespace = true;
			} else {
				wasWhitespace = false;
			}

			String prefix = queue.toString();
			Chain chain = map.get(prefix);

			if (chain == null) {
				chain = new Chain(prefix);
				map.put(prefix, chain);
			}

			chain.add((char) c);
			queue.put((char) c);
		}
	}

	/**
	 * Returns the first "length" characters that were read.
	 */
	public String getBootstrapPrefix() {
		return bootstrapPrefix;
	}

	/**
	 * Returns the next character to print given the prefix. Returns -1 when there are no possible next characters.
	 */
	public int get(String prefix, Random random) {
		Chain chain = map.get(prefix);

		if (chain == null) {
			return -1;
		}

		int index = random.nextInt(chain.getTotal());
		return chain.get(index);
	}

	/**
	 * Prints the contents of the Markov graph.
	 */
	public void dump() {
		for (Chain chain : map.values()) {
			chain.dump();
		}
	}

	/**
	 * List of possible next characters and their probabilities.
	 */
	private static class Chain {
		private String prefix;
		private int total;
		private List<Link> list;

		public Chain(String prefix) {
			this.prefix = prefix;
			total = 0;
			list = new LinkedList<Link>();
		}

		public int getTotal() {
			return total;
		}

		public char get(int index) {
			for (Link link : list) {
				int count = link.getCount();

				if (index < count) {
					return link.getChar();
				}

				index -= count;
			}

			// weird
			return '@';
		}

		public void add(char c) {
			boolean found = false;

			for (Link link : list) {
				if (c == link.getChar()) {
					link.increment();
					found = true;
					break;
				}
			}

			if (!found) {
				Link link = new Link(c);
				list.add(link);
			}

			total++;
		}

		public void dump() {
			log.info(prefix + ": (" + total + ")");

			for (Link link : list) {
				log.info("    " + link.getChar() + " (" + link.getCount() + ")");
			}
		}

		/**
		 * Possible next character and the number of times we've seen it.
		 */
		private static class Link {
			private char c;
			private int count;

			public Link(char c) {
				this.c = c;
				count = 1;
			}

			public void increment() {
				count++;
			}

			public int getCount() {
				return count;
			}

			public char getChar() {
				return c;
			}
		}
	}
}
