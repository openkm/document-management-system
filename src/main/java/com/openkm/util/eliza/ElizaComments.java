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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * @author pavila
 * @author jwedekind
 */
public class ElizaComments {
	protected Vector<Comment> comments;
	protected Vector<AuxVerb> auxVerbs;
	protected Comment lameExcuse;

	public ElizaComments(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		comments = new Vector<Comment>();
		auxVerbs = new Vector<AuxVerb>();
		lameExcuse = null;

		while (true) {
			String line = reader.readLine();
			if (line == null)
				break;
			if (line.equals("PHRASE")) {
				Comment comment = new Comment(reader);
				comments.addElement(comment);
				if (comment.getNumberOfPhrases() == 0)
					lameExcuse = comment;
			} else if (line.equals("AUXVERB")) {
				AuxVerb auxVerb = new AuxVerb(reader);
				auxVerbs.addElement(auxVerb);
			}
		}
		reader.close();
	}

	public String getAnswer(String input) {
		String upperInput = input.toUpperCase();
		for (int i = 0; i < comments.size(); i++) {
			Comment comment = (Comment) comments.elementAt(i);
			for (int j = 0; j < comment.getNumberOfPhrases(); j++) {
				String phrase = comment.getPhrase(j);
				int index = upperInput.indexOf(" " + phrase + " ");
				if (index != -1) {
					String inputPhrase = adaptAuxVerbs(input.substring(index
							+ 2 + phrase.length()));
					return Replace.all(comment.getAnswer(), "*", inputPhrase);
				}
			}
		}

		return Replace.all(lameExcuse.getAnswer(), "*", adaptAuxVerbs(input));
	}

	public String adaptAuxVerbs(String input) {
		String retVal = input;
		for (int i = 0; i < auxVerbs.size(); i++) {
			AuxVerb auxVerb = (AuxVerb) auxVerbs.elementAt(i);
			retVal = Replace.all(retVal, " " + auxVerb.getOriginal() + " ", " "
					+ auxVerb.getSwitched() + "# ");
			retVal = Replace.all(retVal, " " + auxVerb.getSwitched() + " ", " "
					+ auxVerb.getOriginal() + "# ");
		}

		return Replace.all(retVal, "#", "");
	}
}
