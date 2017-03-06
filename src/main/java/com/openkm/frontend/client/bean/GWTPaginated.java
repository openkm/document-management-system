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

package com.openkm.frontend.client.bean;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.List;

/**
 * GWTPaginated
 *
 * @author jllort
 *
 */
@SuppressWarnings("unused")
public class GWTPaginated implements IsSerializable {
	public static final int COL_NONE = 0;
	public static final int COL_TYPE = 1;
	public static final int COL_NAME = 2;
	public static final int COL_SIZE = 3;
	public static final int COL_DATE = 4;
	public static final int COL_AUTHOR = 5;
	public static final int COL_VERSION = 6;
	public static final int COL_COLUMN0 = 7;
	public static final int COL_COLUMN1 = 8;
	public static final int COL_COLUMN2 = 9;
	public static final int COL_COLUMN3 = 10;
	public static final int COL_COLUMN4 = 12;
	public static final int COL_COLUMN5 = 13;
	public static final int COL_COLUMN6 = 14;
	public static final int COL_COLUMN7 = 15;
	public static final int COL_COLUMN8 = 16;
	public static final int COL_COLUMN9 = 17;

	private int totalFolder = 0;
	private int totalDocuments = 0;
	private int totalMails = 0;
	private int total = 0;
	private boolean outOfRange = false;
	private int newOffset = 0;
	private GWTFolder fld = new GWTFolder(); // Used to do not get serialization error
	private GWTDocument doc = new GWTDocument(); // Used to do not get serialization error
	private GWTMail mail = new GWTMail(); // Used to do not get serialization error
	private List<Object> objects;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTotalFolder() {
		return totalFolder;
	}

	public void setTotalFolder(int totalFolder) {
		this.totalFolder = totalFolder;
	}

	public int getTotalDocuments() {
		return totalDocuments;
	}

	public void setTotalDocuments(int totalDocuments) {
		this.totalDocuments = totalDocuments;
	}

	public int getTotalMails() {
		return totalMails;
	}

	public void setTotalMails(int totalMails) {
		this.totalMails = totalMails;
	}

	public boolean isOutOfRange() {
		return outOfRange;
	}

	public void setOutOfRange(boolean outOfRange) {
		this.outOfRange = outOfRange;
	}

	public int getNewOffset() {
		return newOffset;
	}

	public void setNewOffset(int newOffset) {
		this.newOffset = newOffset;
	}

	public List<Object> getObjects() {
		return objects;
	}

	public void setObjects(List<Object> objects) {
		this.objects = objects;
	}
}