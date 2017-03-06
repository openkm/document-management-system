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
package com.openkm.select.values;

import com.openkm.api.OKMAuth;
import com.openkm.bean.form.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazarovski
 */
public class ListClass implements OptionSelectValues {
	@Override
	public List<Option> getOptions() {
		List<Option> options = new ArrayList<Option>();
		try {
			List<String> userList = OKMAuth.getInstance().getUsers(null);
			for (String user : userList) {
				Option option = new Option();
				option.setLabel(OKMAuth.getInstance().getName(null, user));
				option.setValue(user);
				options.add(option);
			}

		} catch (Exception e) {
			System.out.println("Exception occured: " + e.getMessage());
		}
		return options;
	}
}
