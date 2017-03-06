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

package com.openkm.servlet.frontend;

import com.openkm.core.Config;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

/**
 * A little filter that makes a pause after every byte transfered, simulating a slow Internet connection.
 */
public class UploadThrottleFilter implements Filter {

	public void init(final FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
	                     final FilterChain filterChain) throws IOException, ServletException {
		if (Config.UPLOAD_THROTTLE_FILTER) {
			final DelayRequestWrapper requestWrapper = new DelayRequestWrapper((HttpServletRequest) servletRequest);
			filterChain.doFilter(requestWrapper, servletResponse);
		} else {
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	public void destroy() {
	}

	public class DelayRequestWrapper extends HttpServletRequestWrapper {
		final private DelayServerInputStream in;

		public DelayRequestWrapper(HttpServletRequest request) throws IOException {
			super(request);
			in = new DelayServerInputStream(request.getInputStream());
		}

		public ServletInputStream getInputStream() {
			return in;
		}
	}

	public static class DelayServerInputStream extends ServletInputStream {
		final private ServletInputStream in;
		int numOfBytesRead = 0;

		public DelayServerInputStream(ServletInputStream inputStream) {
			super();
			in = inputStream;
		}

		public int read() throws IOException {
			final int chr = in.read();
			numOfBytesRead++;

			// 10 kb por segundo
			if (numOfBytesRead % (1024 * 10) == 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			return chr;
		}
	}
}
