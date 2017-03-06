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

package com.openkm.frontend.client.widget.filebrowser;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;

/**
 * @author jllort
 *
 */
public class GWTFilter implements IsSerializable {
	public static final int SIZE_BYTES = 0;
	public static final int SIZE_KB = 1;
	public static final int SIZE_MB = 2;
	public static final int SIZE_GB = 3;

	private String item;
	private String value;
	private String filterValue1;
	private String filterValue2;
	private int sizeValue1;
	private int sizeValue2;
	private int sizeType1;
	private int sizeType2;
	private Date from;
	private Date to;

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFilterValue1() {
		return filterValue1;
	}

	public void setFilterValue1(String filterValue1) {
		this.filterValue1 = filterValue1;
	}

	public String getFilterValue2() {
		return filterValue2;
	}

	public void setFilterValue2(String filterValue2) {
		this.filterValue2 = filterValue2;
	}

	public int getSizeType1() {
		return sizeType1;
	}

	public void setSizeType1(int sizeType1) {
		this.sizeType1 = sizeType1;
	}

	public int getSizeType2() {
		return sizeType2;
	}

	public void setSizeType2(int sizeType2) {
		this.sizeType2 = sizeType2;
	}

	public int getSizeValue1() {
		return sizeValue1;
	}

	public void setSizeValue1(int sizeValue1) {
		this.sizeValue1 = sizeValue1;
	}

	public int getSizeValue2() {
		return sizeValue2;
	}

	public void setSizeValue2(int sizeValue2) {
		this.sizeValue2 = sizeValue2;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}
}