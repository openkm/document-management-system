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

package com.openkm.servlet.frontend;

import java.io.Serializable;

/**
 * Converter listener to show conversion status
 *
 * @author jllort
 */
public class ConverterListener implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int STATUS_LOADING = 1;
	public static final int STATUS_CONVERTING_TO_DXF = 2;
	public static final int STATUS_CONVERTING_TO_DXF_FINISHED = 3;
	public static final int STATUS_CONVERTING_TO_PDF = 4;
	public static final int STATUS_CONVERTING_TO_PDF_FINISHED = 5;
	public static final int STATUS_CONVERTING_TO_SWF = 6;
	public static final int STATUS_CONVERTING_TO_SWF_FINISHED = 7;
	public static final int STATUS_SENDING_FILE = 8;

	private volatile int status = 0;
	private volatile String error;
	private volatile boolean conversionFinish = false;

	/**
	 * ConverterListener
	 */
	public ConverterListener(int status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isConversionFinish() {
		return conversionFinish;
	}

	public void setConversionFinish(boolean conversionFinish) {
		this.conversionFinish = conversionFinish;
	}
}
