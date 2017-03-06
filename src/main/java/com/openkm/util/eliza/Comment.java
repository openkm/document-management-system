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

package com.openkm.util.eliza;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

/**
 * @author pavila
 * @author jwedekind
 */
public class Comment {
	protected Vector<String> phrases;
	protected Vector<String> answers;
	protected int answerIndex;

	public Comment(BufferedReader reader) throws IOException {
		phrases = new Vector<String>();
		answers = new Vector<String>();
		answerIndex = 0;

		while (true) {
			String line = reader.readLine();
			if (line == null)
				break;
			if (line.equals("ANSWER"))
				break;
			phrases.addElement(line.toUpperCase());
		}

		while (true) {
			String line = reader.readLine();
			if (line == null)
				break;
			if (line.equals(""))
				break;
			answers.addElement(line);
		}
	}

	public String getAnswer() {
		String retVal;

		if (answers.size() > 0) {
			retVal = (String) answers.elementAt(answerIndex);
			answerIndex++;
			if (answerIndex >= answers.size())
				answerIndex = 0;
		} else {
			retVal = "";
		}

		return retVal;
	}

	public int getNumberOfPhrases() {
		return phrases.size();
	}

	public String getPhrase(int i) {
		return (String) phrases.elementAt(i);
	}
}
