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

package com.openkm.bean.form;

import com.openkm.dao.KeyValueDAO;
import com.openkm.dao.bean.KeyValue;
import com.openkm.util.cl.ClassLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Select extends FormElement {
	private static Logger log = LoggerFactory.getLogger(Select.class);
	private static final long serialVersionUID = 1L;
	public static final String TYPE_SIMPLE = "simple";
	public static final String TYPE_MULTIPLE = "multiple";
	private List<Validator> validators = new ArrayList<Validator>();
	private List<Option> options = new ArrayList<Option>();
	private String type = TYPE_SIMPLE;
	private String value = "";
	private String data = "";
	private String optionsData = "";
	private String table = "";
	private String optionsQuery = "";
	private String suggestion = "";
	private String className = "";
	private boolean readonly = false;

	public Select() {
		super.width = "150px";
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		handleClassOptions(handleDbOptions(options));
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Validator> getValidators() {
		return validators;
	}

	public void setValidators(List<Validator> validators) {
		this.validators = validators;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getOptionsData() {
		return optionsData;
	}

	public void setOptionsData(String optionsData) {
		this.optionsData = optionsData;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getOptionsQuery() {
		return optionsQuery;
	}

	public void setOptionsQuery(String optionsQuery) {
		this.optionsQuery = optionsQuery;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	/**
	 * If Select reads options from DB, it gets options from DB and set into internal list {@see Select#options}.
	 * It is assumed that each option in list has different Value. New options are matched by Value with old
	 * options (by temporal hash) and selected if old option is also selected.
	 *
	 * If {@see Select#filterQuery} is not specified, it replace internal list of options with parameter options.
	 *
	 * @param options list of options
	 */
	private List<Option> handleDbOptions(List<Option> options) {
		// read options from DB?
		if (optionsQuery == null || optionsQuery.isEmpty()) {
			// no -> set options from parameter
			this.options = options;
		} else {
			// read options from DB:
			List<Option> dbOptions = getOptionsFromDb();

			// creates hashed options (key is value) from internal option list
			HashMap<String, Option> hashedOptions = new HashMap<String, Option>();

			if (this.options != null) {
				for (Option option : this.options) {
					hashedOptions.put(option.getValue(), option);
				}
			}

			// iterates DB options and set option if value is matched
			for (Option dbOption : dbOptions) {
				if (dbOption == null) {
					continue;
				}

				Option option = hashedOptions.get(dbOption.getValue());
				dbOption.setSelected(option != null ? option.isSelected() : false);
			}

			this.options = dbOptions;
		}

		return this.options;
	}

	/**
	 * Return list of Select's Options from meta table according to query.
	 *
	 * @return list of options from meta table, empty list on error.
	 */
	private List<Option> getOptionsFromDb() {
		List<Option> dbOptions = new ArrayList<Option>();

		try {
			log.debug("Getting options from DB (table={}, query={})", new Object[]{table, optionsQuery});
			List<KeyValue> keyValues = KeyValueDAO.getKeyValues(table, optionsQuery);

			for (KeyValue keyValue : keyValues) {
				Option option = new Option();
				option.setValue(keyValue.getKey());
				option.setLabel(keyValue.getValue());
				dbOptions.add(option);
			}

			log.debug("Got {} options from DB", dbOptions.size());
		} catch (Throwable t) {
			log.error("Unable to get key values for Select", t);
		}

		return dbOptions;
	}

	/**
	 * If Select reads options from Class, it gets options from DB and set into internal list {@see Select#options}.
	 * It is assumed that each option in list has different Value. New options are matched by Value with old
	 * options (by temporal hash) and selected if old option is also selected.
	 *
	 * If {@see Select#className} is not specified, it replace internal list of options with parameter options.
	 *
	 * @param options list of options
	 */
	private List<Option> handleClassOptions(List<Option> options) {
		// read options from DB?
		if (className == null || className.isEmpty()) {
			// no -> set options from parameter
			this.options = options;
		} else {
			// read options from DB:
			List<Option> classOptions = getOptionsFromClass();

			// creates hashed options (key is value) from internal option list
			HashMap<String, Option> hashedOptions = new HashMap<String, Option>();

			if (this.options != null) {
				for (Option option : this.options) {
					hashedOptions.put(option.getValue(), option);
				}
			}

			// iterates DB options and set option if value is matched
			for (Option classOption : classOptions) {
				if (classOption == null) {
					continue;
				}

				Option option = hashedOptions.get(classOption.getValue());
				classOption.setSelected(option != null ? option.isSelected() : false);
			}

			this.options = classOptions;
		}

		return this.options;
	}

	/**
	 * Return list of Select's Options from class 
	 *
	 * @return list of options from class, empty list on error.
	 */
	@SuppressWarnings("unchecked")
	private List<Option> getOptionsFromClass() {
		List<Option> classOptions = new ArrayList<Option>();

		try {
			log.debug("Getting options from Class (className={})", className);
			ClassLoader cl = getClass().getClassLoader();
			classOptions = (List<Option>) ClassLoaderUtils.invokeMethodFromClass(className, "getOptions", cl);
			log.debug("Got {} options from DB", classOptions.size());
		} catch (Throwable t) {
			log.error("Unable to get key values for Select", t);
		}

		return classOptions;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("label=").append(label);
		sb.append(", name=").append(name);
		sb.append(", width=").append(width);
		sb.append(", height=").append(height);
		sb.append(", readonly=").append(readonly);
		sb.append(", type=").append(type);
		sb.append(", value=").append(value);
		sb.append(", data=").append(data);
		sb.append(", optionsData=").append(optionsData);
		sb.append(", options=").append(options);
		sb.append(", validators=").append(validators);
		sb.append(", table=").append(table);
		sb.append(", optionsQuery=").append(optionsQuery);
		sb.append(", suggestion=").append(suggestion);
		sb.append(", class=").append(className);
		sb.append("}");
		return sb.toString();
	}
}
