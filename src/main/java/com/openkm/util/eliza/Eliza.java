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

import java.io.IOException;
import java.io.InputStream;

/**
 * @author pavila
 * @author jwedekind
 */
public class Eliza {
	protected String previousInput;
	protected ElizaComments elizaComments;

	public Eliza() throws IOException {
		previousInput = "";
		InputStream is = Eliza.class.getResourceAsStream("eliza.dat");
		elizaComments = new ElizaComments(is);
		is.close();
	}

	public Eliza(InputStream stream) throws IOException {
		previousInput = "";
		elizaComments = new ElizaComments(stream);
	}

	public String getGreetString() {
		return "Hi. I'm Eliza. Tell me your problems!";
	}

	public String getSayGoodByeString() {
		return "Bye Bye. It was interesting to talk with a lower intelligence.";
	}

	protected String getRepeatString() {
		return "Please don't say it again!";
	}

	public String getResponse(String userInput) {
		String cleanedInput = cleanInput(userInput);
		String upperInput = userInput.toUpperCase();
		String retVal;

		if (upperInput.equals(previousInput)) {
			retVal = getRepeatString();
		} else {
			previousInput = upperInput;
			retVal = cleanOutput(elizaComments.getAnswer(cleanedInput));
		}

		return retVal;
	}

	protected static String cleanInput(String input) {
		return Replace.all(Replace.all(Replace.all(Replace.all(Replace.all(
				Replace.all(Replace.all(
						Replace.all(" " + input + " ", "*", ""), "#", ""), ".",
						""), "!", ""), "?", ""), ",", " # "), "#", " , "),
				"  ", " ");
	}

	protected static String cleanOutput(String output) {
		return Replace.all(Replace.all(Replace.all(Replace.all(Replace.all(
				output, " .", "."), " !", "!"), " ?", "?"), " ,", ","), "  ",
				" ");
	}
}
