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

package com.openkm.frontend.client.widget.eastereggs;

import com.openkm.frontend.client.Main;

/**
 * @author jllort
 *
 */
public class Futurama {

	private String phrase = "Be Open, my friend!";
	private char[] pass = phrase.toUpperCase().toCharArray();
	private int count = 0;

	/**
	 * Futurama
	 */
	public Futurama() {
	}

	/**
	 * Reset all values
	 */
	public void reset() {
		count = 0;
	}

	/**
	 * Evaluates the key
	 *
	 * @param key
	 */
	public void evaluateKey(char key) {

		if (Character.toUpperCase(key) == pass[count] && count < phrase.length()) {
			count++;
			String msg = "<b><font color=\"red\">";
			msg += phrase.substring(0, count);
			msg += "</font></b>";
			msg += phrase.substring(count);
			msg += "<br><br>";
			Main.get().aboutPopup.setText(msg);
		} else {
			String msg = "<b>" + phrase + "</b><br><br>";
			Main.get().aboutPopup.setText(msg);
			count = 0;
		}

		if (count == phrase.length()) {
			Main.get().aboutPopup.changeImg("img/futurama.gif");
		}
	}

}