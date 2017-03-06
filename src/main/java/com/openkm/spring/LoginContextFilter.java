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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginContext;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginContextFilter implements Filter {
	private static Logger log = LoggerFactory.getLogger(LoginContextFilter.class);

	@Override
	public void init(FilterConfig cfg) throws ServletException {
		log.info("Init filter");
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) {
		LoginContext ctx = null;
		HttpSession sess = (HttpSession) ((HttpServletRequest) request).getSession(false);

		if (sess != null) {
			ctx = (LoginContext) sess.getAttribute("ctx");
			log.info("Context: {}", ctx);
		}

		try {
			LoginContextHolder.set(ctx);
			chain.doFilter(request, response);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		} finally {
			LoginContextHolder.set(null);
		}
	}

	@Override
	public void destroy() {
		log.info("Destroy filter");
	}
}
