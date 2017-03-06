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

package com.openkm.cmis;

import org.apache.chemistry.opencmis.commons.impl.server.AbstractServiceFactory;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.chemistry.opencmis.server.support.CmisServiceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Map;

/**
 * CMIS Service Factory.
 */
public class CmisServiceFactory extends AbstractServiceFactory {
	private static Logger log = LoggerFactory.getLogger(CmisServiceFactory.class);
	private CmisTypeManager typeManager;
	private CmisRepository repository;

	/** Default maxItems value for getTypeChildren()}. */
	private static final BigInteger DEFAULT_MAX_ITEMS_TYPES = BigInteger.valueOf(50);

	/** Default depth value for getTypeDescendants(). */
	private static final BigInteger DEFAULT_DEPTH_TYPES = BigInteger.valueOf(-1);

	/** Default maxItems value for getChildren() and other methods returning lists of objects. */
	private static final BigInteger DEFAULT_MAX_ITEMS_OBJECTS = BigInteger.valueOf(200);

	/** Default depth value for getDescendants(). */
	private static final BigInteger DEFAULT_DEPTH_OBJECTS = BigInteger.valueOf(10);

	@Override
	public void init(Map<String, String> parameters) {
		typeManager = new CmisTypeManager();
		repository = new CmisRepository("test", typeManager);
	}

	@Override
	public void destroy() {
	}

	@Override
	public CmisService getService(CallContext context) {
		// authentication can go here
		String user = context.getUsername();
		String password = context.getPassword();
		log.debug("User: {}", user);
		log.debug("Password: {}", password);

		// if the authentication fails, throw a CmisPermissionDeniedException

		// create a new service object (can also be pooled or stored in a ThreadLocal)
		CmisServiceImpl service = new CmisServiceImpl(repository);

		// add the CMIS service wrapper
		// (The wrapper catches invalid CMIS requests and sets default values
		// for parameters that have not been provided by the client.)
		CmisServiceWrapper<CmisService> wrapperService = new CmisServiceWrapper<CmisService>(service, DEFAULT_MAX_ITEMS_TYPES,
				DEFAULT_DEPTH_TYPES, DEFAULT_MAX_ITEMS_OBJECTS, DEFAULT_DEPTH_OBJECTS);

		// hand over the call context to the service object
		service.setCallContext(context);

		return wrapperService;
	}
}
