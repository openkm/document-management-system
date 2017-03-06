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

import com.openkm.core.Config;
import com.openkm.util.EnvironmentDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class ContextSubjectFilter implements Filter {
	private static Logger log = LoggerFactory.getLogger(ContextSubjectFilter.class);
	private static final String SESSION_AUTH_SUBJECT = "session.auth.subject";

	@Override
	public void init(FilterConfig cfg) throws ServletException {
		log.info("Init filter");
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) {
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		try {
			if (EnvironmentDetector.isServerTomcat() && httpRequest.getRemoteUser() != null) {
				HttpSession hs = (HttpSession) (httpRequest).getSession(false);
				Subject sub = (Subject) hs.getAttribute(SESSION_AUTH_SUBJECT);

				if (sub == null) {
					log.info("Login and put Subject in session");
					HttpAuthCallbackHandler hach = new HttpAuthCallbackHandler(httpRequest);
					LoginContext lc = new LoginContext(Config.CONTEXT, new Subject(), hach);
					lc.login();
					sub = lc.getSubject();
					hs.setAttribute(SESSION_AUTH_SUBJECT, sub);
					//LoginContextHolder.set(lc);
				}

				Subject.doAs(sub, new PrivilegedAction<Object>() {
					public Object run() {
						try {
							log.debug("AccessController: {}", AccessController.getContext());
							log.debug("Subject: {}", Subject.getSubject(AccessController.getContext()));
							chain.doFilter(request, response);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ServletException e) {
							e.printStackTrace();
						}

						return null;
					}
				});
			} else {
				chain.doFilter(request, response);
			}
		} catch (LoginException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} catch (ServletException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			//LoginContextHolder.set(null);
		}
	}

	@Override
	public void destroy() {
		log.info("Destroy filter");
	}
}
