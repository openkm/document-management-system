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

package com.openkm.automation;

import net.xeoh.plugins.base.Plugin;

import java.util.Map;

public interface Validation extends Plugin {
	String METHOD = "isValid";

	boolean isValid(Map<String, Object> env, Object... params) throws Exception;

	boolean hasPost();

	boolean hasPre();

	String getName();

	String getParamType00();

	String getParamSrc00();

	String getParamDesc00();

	String getParamType01();

	String getParamSrc01();

	String getParamDesc01();

	String getParamType02();

	String getParamSrc02();

	String getParamDesc02();
}
