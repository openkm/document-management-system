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

package com.openkm.spring;

import org.apache.ws.security.WSPasswordCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

public class ClientPasswordCallback implements CallbackHandler {
	private static Logger log = LoggerFactory.getLogger(ClientPasswordCallback.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		log.info("handle({})", callbacks);
		WSPasswordCallback pwdCallback = (WSPasswordCallback) callbacks[0];

		log.debug("identifier: " + pwdCallback.getIdentifier());
		log.debug("usage: " + pwdCallback.getUsage());
		int usage = pwdCallback.getUsage();

		if (usage == WSPasswordCallback.USERNAME_TOKEN) {
			String password = pwdCallback.getPassword();
			Authentication authentication = new UsernamePasswordAuthenticationToken(pwdCallback.getIdentifier(), password);
			authentication = authenticationManager.authenticate(authentication);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Return the password to the caller
			pwdCallback.setPassword(password);
		}
	}
}
