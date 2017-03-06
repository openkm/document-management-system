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

package com.openkm.module.db.stuff;

import com.google.gson.Gson;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.form.FormElement;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Select;
import com.openkm.core.Config;
import com.openkm.core.ParseException;
import com.openkm.dao.bean.NodeProperty;
import com.openkm.util.FormUtils;
import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pavila
 */
public class SetPropertiesFieldBridge implements FieldBridge {
	private static Logger log = LoggerFactory.getLogger(SetPropertiesFieldBridge.class);

	@Override
	public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
		if (value instanceof Set<?>) {
			@SuppressWarnings("unchecked")
			Set<NodeProperty> properties = (Set<NodeProperty>) value;

			try {
				Map<PropertyGroup, List<FormElement>> formsElements = FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML);
				Gson gson = new Gson();

				for (NodeProperty nodProp : properties) {
					String propValue = nodProp.getValue();

					if (propValue != null && !propValue.equals("")) {
						FormElement fe = FormUtils.getFormElement(formsElements, nodProp.getName());

						if (fe instanceof Input && ((Input) fe).getType().equals(Input.TYPE_DATE)) {
							propValue = propValue.substring(0, 8);
							log.debug("Added date field '{}' with value '{}'", nodProp.getName(), propValue);
							luceneOptions.addFieldToDocument(nodProp.getName(), propValue, document);
						} else if (fe instanceof Select) {
							String[] propValues = gson.fromJson(propValue, String[].class);

							for (String optValue : propValues) {
								log.debug("Added list field '{}' with value '{}'", nodProp.getName(), optValue);
								luceneOptions.addFieldToDocument(nodProp.getName(), optValue, document);
							}
						} else {
							log.debug("Added field '{}' with value '{}'", nodProp.getName(), propValue);
							luceneOptions.addFieldToDocument(nodProp.getName(), propValue, document);
						}
					}
				}
			} catch (ParseException e) {
				log.error("Property Groups parse error: {}", e.getMessage(), e);
			} catch (IOException e) {
				log.error("Property Groups IO error: {}", e.getMessage(), e);
			}
		} else {
			log.warn("IllegalArgumentException: Support only Set<NodeProperty>");
			throw new IllegalArgumentException("Support only Set<NodeProperty>");
		}
	}
}
