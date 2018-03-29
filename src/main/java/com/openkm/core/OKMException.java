/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * <p>
 * Copyright (c) 2006-2017  Paco Avila & Josep Llort
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
package com.openkm.core;

import com.openkm.frontend.client.constants.service.ErrorCode;

/**
 * This class represents the root of the hierarchy of the exceptions defined in OpenKM.
 * <p>
 * All subclasses MUST set their error code on instantiation.
 */
public class OKMException extends Exception {
	private static final long serialVersionUID = 1L;
	private String errorCode = ErrorCode.CAUSE_OKMGeneral;

	public OKMException() {
		super();
	}

	public OKMException(String message) {
		super(message);
	}

	public OKMException(String message, Throwable cause) {
		super(message, cause);
	}

	public OKMException(Throwable cause) {
		super(cause);
	}

	public OKMException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public String getErrorCode() {
		return errorCode;
	}

	protected void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
