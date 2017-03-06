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

package com.openkm.jaas;

import com.openkm.module.common.CommonAuthModule;
import com.openkm.principal.PrincipalAdapter;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.util.SecureStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.CredentialNotFoundException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Used when running on Tomcat. Need to create a file conf/jaas.config with:
 *
 * OpenKM {
 *   com.openkm.jaas.CustomLoginModule REQUIRED debug=true;
 * };
 *
 * @author pavila
 */
public class CustomLoginModule implements LoginModule {
	private static Logger log = LoggerFactory.getLogger(CustomLoginModule.class);
	private Subject subject;
	private CallbackHandler callbackHandler;
	private String password;
	private String name;
	private boolean customCallbackHandler = false;

	/**
	 * For testing purposes
	 */
	public static void main(String args[]) throws LoginException {
		if (args.length != 2) {
			System.out.println("Usage: java CustomLoginModule -Djava.security.auth.login.config=jaas.config <user> <password>");
		} else {
			MyCallbackHandler mch = new CustomLoginModule.MyCallbackHandler(args[0], args[1]);
			LoginContext lc = new LoginContext("OpenKM", mch);
			lc.login();
			log.info("Authentication successful for {}", lc.getSubject());
			lc.logout();
		}
	}

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
	                       Map<String, ?> options) {
		this.subject = subject;
		this.callbackHandler = callbackHandler;
	}

	@Override
	public boolean commit() throws LoginException {
		try {
			authenticate();
			populateRoles();
			return true;
		} catch (PrincipalAdapterException pae) {
			throw new LoginException(pae.getMessage());
		} catch (NoSuchAlgorithmException nsae) {
			throw new LoginException(nsae.getMessage());
		}
	}

	@Override
	public boolean login() throws LoginException {
		NameCallback ncb = new NameCallback("User: ");
		PasswordCallback pcb = new PasswordCallback("Password: ", true);

		try {
			callbackHandler.handle(new Callback[]{ncb, pcb});
			name = ncb.getName();
			password = new String(pcb.getPassword());
		} catch (UnsupportedCallbackException e) {
			try {
				callbackHandler.handle(new Callback[]{ncb});
				name = ncb.getName();
				customCallbackHandler = true;
			} catch (Exception e1) {
				throw new LoginException(e.getMessage());
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		if (name == null || name.equals("")) {
			throw new CredentialNotFoundException("User name is required");
		}

		if (!customCallbackHandler) {
			if (password == null || password.equals("")) {
				throw new CredentialNotFoundException("Password is required");
			}
		}

		return true;
	}

	@Override
	public boolean abort() throws LoginException {
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
		return true;
	}

	/**
	 * Add user's roles to subject being authenticated
	 */
	private void populateRoles() throws PrincipalAdapterException {
		PrincipalAdapter pa = CommonAuthModule.getPrincipalAdapter();

		for (String role : pa.getRolesByUser(name)) {
			subject.getPrincipals().add(new RoleImpl(role));
		}

		log.debug("Roles: {}", subject.getPrincipals());
	}

	/**
	 * Check if user and password are valid
	 */
	private void authenticate() throws PrincipalAdapterException, NoSuchAlgorithmException, LoginException {
		PrincipalAdapter pa = CommonAuthModule.getPrincipalAdapter();
		String ppass = pa.getPassword(name);
		log.debug("User: {}, Password: {}, DBPassword: {}", new Object[]{name, password, ppass});

		if (customCallbackHandler || ppass.equals(SecureStore.md5Encode(password.getBytes()))) {
			subject.getPrincipals().add(new UserImpl(name));
		} else {
			throw new LoginException("Password does not match");
		}

		log.debug("Users: {}", subject.getPrincipals());
	}

	/**
	 * For testing purposes
	 */
	static class MyCallbackHandler implements CallbackHandler {
		private String user;
		private String password;

		public MyCallbackHandler(String user, String password) {
			this.user = user;
			this.password = password;
		}

		@Override
		public void handle(Callback[] cb) throws IOException, UnsupportedCallbackException {
			for (int i = 0; i < cb.length; i++) {
				if (cb[i] instanceof NameCallback) {
					NameCallback nc = (NameCallback) cb[i];
					nc.setName(user);
					log.info("User: {}", user);
				} else if (cb[i] instanceof PasswordCallback) {
					PasswordCallback pc = (PasswordCallback) cb[i];
					pc.setPassword(password.toCharArray());
					log.info("Password: {}", password);
				} else {
					throw new UnsupportedCallbackException(cb[i], "MyCallbackHandler");
				}
			}
		}
	}
}
