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

import java.util.Map;

import com.openkm.automation.Action;
import com.openkm.automation.AutomationUtils;
import com.openkm.dao.bean.Automation;
import com.openkm.dao.bean.NodeBase;
import com.openkm.module.db.stuff.DbSessionManager;
import com.openkm.spring.PrincipalUtils;

import bsh.Interpreter;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 * ExecuteScripting
 *
 * @author jllort
 */
@PluginImplementation
public class ExecuteScripting implements Action {

	@Override
	public void executePre(Map<String, Object> env, Object... params) throws Exception {
		execute(env, params);
	}

	@Override
	public void executePost(Map<String, Object> env, Object... params) throws Exception {
		execute(env, params);
	}

	/**
	 * execute
	 *
	 * @param env    OpenKM API internal environment data.
	 * @param params Action configured parameters.
	 */
	private void execute(Map<String, Object> env, Object... params) throws Exception {
		String script = AutomationUtils.getString(0, params);
		NodeBase node = AutomationUtils.getNode(env);
		String uuid = AutomationUtils.getUuid(env);
		Object file = AutomationUtils.getFile(env);
		String systemToken = DbSessionManager.getInstance().getSystemToken();
		String userId = PrincipalUtils.getUser();

		Interpreter i = new Interpreter();
		i.set("systemToken", systemToken);
		i.set("node", node);
		i.set("uuid", uuid);
		i.set("file", file);
		i.set("userId", userId);

		for (Map.Entry<String, Object> entry : env.entrySet()) {
			i.set(entry.getKey(), entry.getValue());
		}

		// Environment vars
		i.set("env", env);
		i.eval(script);
	}

	@Override
	public boolean hasPost() {
		return true;
	}

	@Override
	public boolean hasPre() {
		return true;
	}

	@Override
	public String getName() {
		return "ExecuteScripting";
	}

	@Override
	public String getParamType00() {
		return Automation.PARAM_TYPE_CODE;
	}

	@Override
	public String getParamSrc00() {
		return Automation.PARAM_SOURCE_EMPTY;
	}

	@Override
	public String getParamDesc00() {
		return "Script";
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