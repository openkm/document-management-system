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

package com.openkm.dao;

import com.google.gson.Gson;
import com.openkm.bean.ConfigStoredFile;
import com.openkm.bean.ConfigStoredOption;
import com.openkm.bean.ConfigStoredSelect;
import com.openkm.core.DatabaseException;
import com.openkm.core.MimeTypeConfig;
import com.openkm.dao.bean.Config;
import com.openkm.util.PathUtils;
import com.openkm.util.SecureStore;
import org.apache.commons.io.IOUtils;
import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ConfigDAO {
	private static Logger log = LoggerFactory.getLogger(ConfigDAO.class);

	private ConfigDAO() {
	}

	/**
	 * Create activity
	 */
	public static void create(Config cfg) throws DatabaseException {
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.save(cfg);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Update
	 */
	public static void update(Config cfg) throws DatabaseException {
		log.debug("update({})", cfg);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(cfg);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("update: void");
	}

	/**
	 * Delete
	 */
	public static void delete(String key) throws DatabaseException {
		log.debug("delete({})", key);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Config mt = (Config) session.load(Config.class, key);
			session.delete(mt);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("delete: void");
	}

	/**
	 * Find by pk
	 */
	public static Config findByPk(String key) throws DatabaseException {
		log.debug("findByPk({})", key);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Config ret = (Config) session.load(Config.class, key);
			Hibernate.initialize(ret);
			HibernateUtil.commit(tx);
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find by pk with a default value
	 */
	private static String getProperty(String key, String defaultValue, String type) throws DatabaseException {
		log.debug("getProperty({}, {}, {})", new Object[]{key, defaultValue, type});
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Config ret = (Config) session.get(Config.class, key);

			if (ret == null) {
				ret = new Config();
				ret.setKey(key);
				ret.setType(type);
				ret.setValue(defaultValue);
				session.save(ret);
			} else if (ret.getValue() == null) {
				// For Oracle '' are like NULL
				ret.setValue("");
			}

			HibernateUtil.commit(tx);
			log.debug("getProperty: {}", ret.getValue());
			return ret.getValue();
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find by pk with a default value
	 */
	public static String getHidden(String key, String defaultValue) throws DatabaseException {
		return getProperty(key, defaultValue, Config.HIDDEN);
	}

	/**
	 * Find by pk with a default value
	 */
	public static String getString(String key, String defaultValue) throws DatabaseException {
		return getProperty(key, defaultValue, Config.STRING);
	}

	/**
	 * Find by pk with a default value
	 */
	public static String getText(String key, String defaultValue) throws DatabaseException {
		return getProperty(key, defaultValue, Config.TEXT);
	}

	/**
	 * Find by pk with a default value
	 */
	public static boolean getBoolean(String key, boolean defaultValue) throws DatabaseException {
		return "true".equalsIgnoreCase(getProperty(key, Boolean.toString(defaultValue), Config.BOOLEAN));
	}

	/**
	 * Find by pk with a default value
	 */
	public static int getInteger(String key, int defaultValue) throws DatabaseException {
		return Integer.parseInt(getProperty(key, Integer.toString(defaultValue), Config.INTEGER));
	}

	/**
	 * Find by pk with a default value
	 */
	public static long getLong(String key, long defaultValue) throws DatabaseException {
		return Long.parseLong(getProperty(key, Long.toString(defaultValue), Config.LONG));
	}
	
	/**
	 * Find by pk with a default value
	 */
	public static String getHtml(String key, String defaultValue) throws DatabaseException {
		return getProperty(key, defaultValue, Config.HTML);
	}

	/**
	 * Find by pk with a default value
	 */
	public static ConfigStoredFile getFile(String key, String path, ServletContext sc) throws DatabaseException,
			IOException {
		InputStream is = null;

		try {
			if (sc == null) {
				is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
			} else {
				is = sc.getResourceAsStream(path);
			}

			ConfigStoredFile stFile = new ConfigStoredFile();

			if (is == null) {
				stFile.setContent("");
			} else {
				stFile.setContent(SecureStore.b64Encode(IOUtils.toByteArray(is)));
			}

			stFile.setName(PathUtils.getName(path));
			stFile.setMime(MimeTypeConfig.mimeTypes.getContentType(stFile.getName()));

			// MIME still are not initialized from database
			if (MimeTypeConfig.MIME_UNDEFINED.equals(stFile.getMime())) {
				if (stFile.getName().toLowerCase().endsWith(".ico")) {
					stFile.setMime(MimeTypeConfig.MIME_ICO);
				}
			}

			String value = getProperty(key, new Gson().toJson(stFile), Config.FILE);
			return new Gson().fromJson(value, ConfigStoredFile.class);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * Find by pk with a default value
	 */
	public static void initSelect(String key, ConfigStoredSelect value) throws DatabaseException {
		Config cfg = new Config();
		cfg.setKey(key);
		cfg.setValue(new Gson().toJson(value));
		cfg.setType(Config.SELECT);
		delete(key);
		create(cfg);
	}

	/**
	 * Find by pk with a default value
	 */
	public static ConfigStoredSelect getSelect(String key) throws DatabaseException {
		String dbValue = getProperty(key, null, Config.SELECT);

		if (dbValue == null || dbValue.equals("")) {
			return null;
		} else {
			return new Gson().fromJson(dbValue, ConfigStoredSelect.class);
		}
	}

	/**
	 * Find by pk with a default value
	 */
	public static String getSelectedOption(String key, String value) throws DatabaseException {
		StringTokenizer st = new StringTokenizer(value, "|");
		ConfigStoredSelect stSelect = new ConfigStoredSelect();
		boolean selected = false;

		while (st.hasMoreTokens()) {
			String tk = st.nextToken().trim();
			ConfigStoredOption stOption = new ConfigStoredOption();

			if (tk.startsWith(ConfigStoredOption.SELECTED)) {
				stOption.setName(tk.substring(1));
				stOption.setValue(tk.substring(1));
				stOption.setSelected(true);
				selected = true;
			} else {
				stOption.setName(tk);
				stOption.setValue(tk);
				stOption.setSelected(false);
			}

			stSelect.getOptions().add(stOption);
		}

		// Set first option as default
		if (!selected && stSelect.getOptions().size() > 0) {
			stSelect.getOptions().get(0).setSelected(true);
		}

		String dbValue = getProperty(key, new Gson().toJson(stSelect), Config.SELECT);
		ConfigStoredSelect dbSelect = new Gson().fromJson(dbValue, ConfigStoredSelect.class);

		for (ConfigStoredOption option : dbSelect.getOptions()) {
			if (option.isSelected()) {
				return option.getValue();
			}
		}

		return "";
	}

	/**
	 * Find by pk with a default value
	 */
	public static List<String> getList(String key, String defaultValue) throws DatabaseException {
		List<String> list = new ArrayList<String>();
		String dbValue = getProperty(key, defaultValue, Config.LIST);
		StringTokenizer st = new StringTokenizer(dbValue, "\t\n\r\f");

		while (st.hasMoreTokens()) {
			String tk = st.nextToken().trim();

			if (tk != null && !tk.equals("")) {
				list.add(tk);
			}
		}

		return list;
	}

	/**
	 * Find by pk
	 */
	@SuppressWarnings("unchecked")
	public static List<Config> findAll() throws DatabaseException {
		log.debug("findAll()");
		String qs = "from Config cfg order by cfg.key";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			List<Config> ret = q.list();
			HibernateUtil.commit(tx);
			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}
