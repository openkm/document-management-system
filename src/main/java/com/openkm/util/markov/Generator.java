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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.InputMismatchException;
import java.util.Random;

/**
 * Creates a Markov graph from an input file and generates text
 * based on it. Given two input files, generates two graphs
 * and interpolates between them.
 *
 * @author Lawrence Kesteloot
 * @author Paco Avila
 */
public class Generator {
	private static final int DEFAULT_PREFIX_LENGTH = 4;
	private static final int LINE_WIDTH = 80;
	private static final int TOTAL_CHARACTERS = 300;
	private int prefixLength;
	private Markov markov;

	public Generator(InputStream in, int prefixLength) throws IOException {
		markov = new Markov(new InputStreamReader(in), prefixLength);
		this.prefixLength = prefixLength;
	}

	public Generator(InputStream in) throws IOException {
		markov = new Markov(new InputStreamReader(in), DEFAULT_PREFIX_LENGTH);
		this.prefixLength = DEFAULT_PREFIX_LENGTH;
	}

	/**
	 * Generate a text using defaults to a writer
	 */
	public void generateText(int paragraphs, OutputStream out) throws Exception {
		generateText(paragraphs, LINE_WIDTH, TOTAL_CHARACTERS, out);
	}

	/**
	 * Generate a text to a writer
	 * @throws IOException
	 * @throws InputMismatchException
	 */
	public void generateText(int paragraphs, int lineWidth, int totalCharacters, OutputStream out)
			throws InputMismatchException, IOException {
		for (int i = 0; i < paragraphs; i++) {
			generateParagraph(lineWidth, totalCharacters, out);
			out.write("\n\n".getBytes());
		}
	}

	/**
	 * Generate a paragraph using defaults to a writer
	 * @throws IOException
	 * @throws InputMismatchException
	 */
	public void generateParagraph(OutputStream out) throws InputMismatchException, IOException {
		generateParagraph(LINE_WIDTH, TOTAL_CHARACTERS, out);
	}

	/**
	 * Generate a paragraph to a writer
	 * @throws IOException
	 * @throws InputMismatchException
	 */
	public void generateParagraph(int lineWidth, int totalCharacters, OutputStream out) throws
			InputMismatchException, IOException {
		Random random = new Random();
		CharQueue queue = new CharQueue(prefixLength);
		float weight = 0;
		int width = prefixLength;
		int c;

		queue.set(markov.getBootstrapPrefix());
		out.write(queue.toString().getBytes());

		do {
			String prefix = queue.toString();
			c = markov.get(prefix, random);

			if (c == -1) {
				break;
			}

			out.write((char) c);
			queue.put((char) c);
			width++;

			// line wrap
			if (c == ' ' && width > lineWidth) {
				out.write("\n".getBytes());
				width = 0;
			}

			// go towards second Markov chain
			weight += 1.0 / totalCharacters;
		} while (weight < 1 || c != '.');
	}
}
