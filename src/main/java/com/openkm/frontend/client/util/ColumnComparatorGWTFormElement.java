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

package com.openkm.frontend.client.util;

import com.openkm.frontend.client.bean.GWTObjectToOrder;
import com.openkm.frontend.client.bean.form.*;

import java.util.Comparator;

/**
 * ColumnComparatorGWTFormElement
 *
 * @author jllort
 *
 */
public class ColumnComparatorGWTFormElement implements Comparator<GWTObjectToOrder> {
	private static final Comparator<GWTObjectToOrder> INSTANCE = new ColumnComparatorGWTFormElement();

	public static Comparator<GWTObjectToOrder> getInstance() {
		return INSTANCE;
	}

	public int compare(GWTObjectToOrder arg0, GWTObjectToOrder arg1) {
		if (arg0.getObject() == null && arg1.getObject() == null) {
			return 0;
		} else if (arg0.getObject() == null) {
			return -1;
		} else if (arg1.getObject() == null) {
			return 1;
		} else {
			if (arg0.getObject() instanceof GWTTextArea) {
				return ((GWTTextArea) arg0.getObject()).getValue().compareTo(((GWTTextArea) arg1.getObject()).getValue());
			} else if (arg0.getObject() instanceof GWTInput) {
				// Any type can be compared directly with value ( date is ISO8601, others are normal text )
				return ((GWTInput) arg0.getObject()).getValue().compareTo(((GWTInput) arg1.getObject()).getValue());
			} else if (arg0.getObject() instanceof GWTSuggestBox) {
				return String.valueOf(((GWTSuggestBox) arg0.getObject()).getText()).compareTo(String.valueOf(((GWTSuggestBox) arg1.getObject()).getText()));
			} else if (arg0.getObject() instanceof GWTCheckBox) {
				return String.valueOf(((GWTCheckBox) arg0.getObject()).getValue()).compareTo(String.valueOf(((GWTCheckBox) arg1.getObject()).getValue()));
			} else if (arg0.getObject() instanceof GWTSelect) {
				String value0 = "";
				String value1 = "";
				boolean breakEnabled = false;
				breakEnabled = ((GWTSelect) arg0.getObject()).getType().equals(GWTSelect.TYPE_SIMPLE);
				for (GWTOption opt : ((GWTSelect) arg0.getObject()).getOptions()) {
					if (opt.isSelected()) {
						value0 += opt.getValue();
						if (breakEnabled) {
							break;
						}
					}
				}
				for (GWTOption opt : ((GWTSelect) arg1.getObject()).getOptions()) {
					if (opt.isSelected()) {
						value1 += opt.getValue();
						if (breakEnabled) {
							break;
						}
					}
				}
				return value0.compareTo(value1);
			}
			return 0;
		}
	}
}