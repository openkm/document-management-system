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

package com.openkm.spring;

import com.openkm.automation.AutomationManager;
import com.openkm.automation.AutomationUtils;
import com.openkm.dao.bean.AutomationRule;
import com.openkm.util.GenericHolder;
import com.openkm.util.UserActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.HashMap;
import java.util.Map;

public class LoggerListener implements ApplicationListener<AbstractAuthenticationEvent> {
	private static Logger log = LoggerFactory.getLogger(LoggerListener.class);

	@Override
	public void onApplicationEvent(AbstractAuthenticationEvent event) {
		if (event instanceof AuthenticationSuccessEvent) {
			log.debug("Authentication OK: {}", event.getAuthentication().getName());

			// Activity log
			Object details = event.getAuthentication().getDetails();
			String params = null;

			if (details instanceof WebAuthenticationDetails) {
				WebAuthenticationDetails wad = (WebAuthenticationDetails) details;
				params = wad.getRemoteAddress();
			} else if (GenericHolder.get() != null) {
				params = (String) GenericHolder.get();
			}

			// AUTOMATION - POST
			Map<String, Object> env = new HashMap<>();
			env.put(AutomationUtils.USER, event.getAuthentication().getName());
			try {
				AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_USER_LOGIN, AutomationRule.AT_POST, env);
			} catch (Exception e) {
				log.info("Automation ERROR: {}", e.getCause());
			}

			UserActivity.log(event.getAuthentication().getName(), "LOGIN", null, null, params);
		} else if (event instanceof AuthenticationFailureBadCredentialsEvent) {
			log.info("Authentication ERROR: {}", event.getAuthentication().getName());
		}
	}
}
