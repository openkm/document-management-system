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

package com.openkm.vernum;

import com.google.gson.Gson;
import com.openkm.bean.AppVersion;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.bean.NodeDocument;
import com.openkm.dao.bean.NodeDocumentVersion;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pavila
 */
public class JbcmVersionNumerationAdapter implements VersionNumerationAdapter {
	private static Logger log = LoggerFactory.getLogger(JbcmVersionNumerationAdapter.class);
	private static final String PROPERTY_GROUP = "okg:jbcapital";
	private static final String PROPERTY_ESTADO = "okp:jbcapital.estado";
	private static final String PROPERTY_ESTADO_NECESIDAD = "1";
	private static final String PROPERTY_ESTADO_BORRADOR = "2";
	private static final String PROPERTY_ESTADO_PROPUESTO = "3";
	private static final String PROPERTY_ESTADO_REVISADO = "4";
	private static final String PROPERTY_ESTADO_APROBADO = "5";

	@Override
	public String getInitialVersionNumber() {
		return "0.0.0";
	}

	@Override
	public String getNextVersionNumber(Session session, NodeDocument nDoc, NodeDocumentVersion nDocVer, int increment) {
		String nextVersionName = nDocVer.getName();
		Gson gson = new Gson();

		try {
			String value = NodeBaseDAO.getInstance().getProperty(nDoc.getUuid(), PROPERTY_GROUP, PROPERTY_ESTADO);
			String[] values = gson.fromJson(value, String[].class);
			String estado = values[0];
			AppVersion jbVersion = parseVersion(nDocVer.getName());
			boolean inc = false;

			if (PROPERTY_ESTADO_NECESIDAD.equals(estado)) {
				// No requiere aumento de versión
			} else if (PROPERTY_ESTADO_BORRADOR.equals(estado)) {
				int x = Integer.parseInt(jbVersion.getMaintenance());
				jbVersion.setMaintenance(String.valueOf(x + 1));
				inc = true;
			} else if (PROPERTY_ESTADO_PROPUESTO.equals(estado)) {
				int x = Integer.parseInt(jbVersion.getMinor());
				jbVersion.setMinor(String.valueOf(x + 1));
				inc = true;
			} else if (PROPERTY_ESTADO_REVISADO.equals(estado)) {
				// No requiere aumento de versión: pero algi hay que poner de forma temporal
				jbVersion.setMajor("9");
				jbVersion.setMinor("9");
				jbVersion.setMaintenance("9");
				inc = true;
			} else if (PROPERTY_ESTADO_APROBADO.equals(estado)) {
				int x = Integer.parseInt(jbVersion.getMajor());
				jbVersion.setMajor(String.valueOf(x + 1));
				inc = true;
			}

			if (inc) {
				nextVersionName = jbVersion.getVersion();
			}
		} catch (PathNotFoundException e) {
			log.error("PathNotFound: " + e.getMessage(), e);
		} catch (DatabaseException e) {
			log.error("DatabaseError: " + e.getMessage(), e);
		}

		return nextVersionName;
	}

	/**
	 * Parse JB Capital version schema.
	 */
	public static AppVersion parseVersion(String impVersion) {
		AppVersion jbVersion = new AppVersion();
		String[] version = impVersion.split("\\.");

		if (version.length > 0 && version[0] != null) {
			jbVersion.setMajor(version[0]);
		}

		if (version.length > 1 && version[1] != null) {
			jbVersion.setMinor(version[1]);
		}

		if (version.length > 2 && version[2] != null && !version[2].equals("")) {
			jbVersion.setMaintenance(version[2]);
		}

		return jbVersion;
	}
}
