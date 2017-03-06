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

package com.openkm.frontend.client.widget.dashboard;

import com.google.gwt.user.client.ui.HTML;


/**
 * Score
 *
 * @author jllort
 *
 */
public class Score extends HTML {
	HTML html;
	String img = "";

	/**
	 * The score
	 *
	 * @param score
	 */
	public Score(long score) {
		super();

		if (score < 100) {
			img = "<img src='img/icon/search/semi_star.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			setHTML(img);
		} else if (score >= 100 && score < 200) {
			img = "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			setHTML(img);
		} else if (score >= 200 && score < 300) {
			img = "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/semi_star.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			setHTML(img);
		} else if (score >= 300 && score < 400) {
			img = "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			setHTML(img);
		} else if (score >= 400 && score < 500) {
			img = "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/semi_star.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			setHTML(img);
		} else if (score >= 500 && score < 600) {
			img = "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			setHTML(img);
		} else if (score >= 600 && score < 700) {
			img = "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/semi_star.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			setHTML(img);
		} else if (score >= 700 && score < 800) {
			img = "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star_gray.gif'>";
			setHTML(img);
		} else if (score >= 800 && score < 900) {
			img = "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/semi_star.gif'>";
			setHTML(img);
		} else {
			img = "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star.gif'>";
			img += "<img src='img/icon/search/star.gif'>";
			setHTML(img);
		}

		setWordWrap(false);
	}
}