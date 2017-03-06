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

package com.openkm.workflow;

import com.openkm.module.ModuleManager;
import com.openkm.principal.PrincipalAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddressResolver implements org.jbpm.mail.AddressResolver {
	private static Logger log = LoggerFactory.getLogger(AddressResolver.class);
	private static final long serialVersionUID = 1L;

	public Object resolveAddress(String actorId) {
		log.debug("resolveAddress({})", actorId);
		String mail = null;

		try {
			mail = ModuleManager.getAuthModule().getMail(null, actorId);
		} catch (PrincipalAdapterException e) {
			log.warn(e.getMessage());
		}

		log.debug("resolveAddress: {}", mail);
		return mail;
	}
}
