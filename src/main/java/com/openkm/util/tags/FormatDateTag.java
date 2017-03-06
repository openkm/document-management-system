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

package com.openkm.util.tags;

import com.openkm.core.DatabaseException;
import com.openkm.dao.LanguageDAO;
import com.openkm.dao.bean.Language;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("serial")
public class FormatDateTag extends TagSupport {
	private static final String MODULE = "frontend";
	private static final String KEY_PATTERN = "general.date.pattern";
	private Date date;
	private Calendar calendar;

	@Override
	public int doStartTag() {
		try {
			Locale locale = pageContext.getRequest().getLocale();
			String lang = locale.getLanguage() + "-" + locale.getCountry();
			String pattern = LanguageDAO.getTranslation(lang, MODULE, KEY_PATTERN);
			String str = "";

			if (pattern == null || pattern.equals("")) {
				pattern = LanguageDAO.getTranslation(Language.DEFAULT, MODULE, KEY_PATTERN);
			}

			if (date != null) {
				str = new SimpleDateFormat(pattern).format(date);
			} else if (calendar != null) {
				str = new SimpleDateFormat(pattern).format(calendar.getTime());
			}

			pageContext.getOut().write(str);
		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return Tag.SKIP_BODY;
	}

	@Override
	public void release() {
		super.release();
		date = null;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
}
