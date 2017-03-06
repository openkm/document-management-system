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

package com.openkm.webdav;

import com.bradmcevoy.http.*;
import com.bradmcevoy.http.webdav.WebDavResponseHandler;
import com.openkm.webdav.resource.ResourceFactoryFactoryImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class WebDavService {
	private static final WebDavService INSTANCE = new WebDavService();
	protected ServletHttpManager httpManager;

	public static WebDavService get() {
		return INSTANCE;
	}

	private WebDavService() {
		init();
	}

	public void init() {
		try {
			initFromFactoryFactory();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	public ResourceFactory getResourceFactory() {
		return httpManager.getResourceFactory();
	}

	/**
	 *
	 */
	public void handleRequest(HttpServletRequest request, HttpServletResponse response, ServletContext ctx) throws IOException {
		Request miltonRequest = new com.bradmcevoy.http.ServletRequest(request, ctx);
		Response miltonResponse = new com.bradmcevoy.http.ServletResponse(response);
		httpManager.process(miltonRequest, miltonResponse);
	}

	/**
	 *
	 */
	protected void initFromFactoryFactory() throws ServletException {
		ResourceFactoryFactory rff = new ResourceFactoryFactoryImpl();
		rff.init();
		ResourceFactory rf = rff.createResourceFactory();
		WebDavResponseHandler responseHandler = rff.createResponseHandler();
		httpManager = new ServletHttpManager(rf, responseHandler, new AuthenticationService());
		httpManager.setEnableExpectContinue(false);
	}
}
