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

package com.openkm.automation.action;

import com.openkm.automation.Action;
import com.openkm.automation.AutomationUtils;
import com.openkm.dao.bean.Automation;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * SetTextExtracted
 *
 * @author jllort
 */
@PluginImplementation
public class SetTextExtracted implements Action {
	private static Logger log = LoggerFactory.getLogger(SetTextExtracted.class);

	@Override
	public void executePre(Map<String, Object> env, Object... params) throws Exception {
	}

	@Override
	public void executePost(Map<String, Object> env, Object... params) throws Exception {
		String text = AutomationUtils.getString(0, params);
		String textExtracted = AutomationUtils.getTextExtracted(env);

		log.info("Original Extracted Text: {}", textExtracted);
		env.put(AutomationUtils.TEXT_EXTRACTED, text);
	}

	@Override
	public boolean hasPost() {
		return true;
	}

	@Override
	public boolean hasPre() {
		return false;
	}

	@Override
	public String getName() {
		return "SetTextExtracted";
	}

	@Override
	public String getParamType00() {
		return Automation.PARAM_TYPE_TEXT;
	}

	@Override
	public String getParamSrc00() {
		return Automation.PARAM_SOURCE_EMPTY;
	}

	@Override
	public String getParamDesc00() {
		return "Text";
	}

	@Override
	public String getParamType01() {
		return Automation.PARAM_TYPE_EMPTY;
	}

	@Override
	public String getParamSrc01() {
		return Automation.PARAM_SOURCE_EMPTY;
	}

	@Override
	public String getParamDesc01() {
		return "";
	}

	@Override
	public String getParamType02() {
		return Automation.PARAM_TYPE_EMPTY;
	}

	@Override
	public String getParamSrc02() {
		return Automation.PARAM_SOURCE_EMPTY;
	}

	@Override
	public String getParamDesc02() {
		return "";
	}
}
