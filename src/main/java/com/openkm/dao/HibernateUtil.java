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

package com.openkm.dao;

import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.dao.bean.*;
import com.openkm.dao.bean.cache.UserItems;
import com.openkm.dao.bean.cache.UserNodeKeywords;
import com.openkm.extension.dao.bean.WikiPage;
import com.openkm.extension.dao.bean.ZohoToken;
import com.openkm.util.ConfigUtils;
import com.openkm.util.DatabaseDialectAdapter;
import com.openkm.util.EnvironmentDetector;
import com.openkm.util.FileUtils;
import org.apache.commons.io.IOUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.hql.QueryTranslator;
import org.hibernate.hql.QueryTranslatorFactory;
import org.hibernate.hql.ast.ASTQueryTranslatorFactory;
import org.hibernate.jdbc.Work;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Show SQL => Logger.getLogger("org.hibernate.SQL").setThreshold(Level.INFO);
 * JBPM Integration => org.jbpm.db.JbpmSessionFactory
 *
 * @author pavila
 */
public class HibernateUtil {
	private static Logger log = LoggerFactory.getLogger(HibernateUtil.class);
	private static SessionFactory sessionFactory;
	public static String HBM2DDL_CREATE = "create";
	public static String HBM2DDL_UPDATE = "update";
	public static String HBM2DDL_NONE = "none";

	/**
	 * Disable constructor to guaranty a single instance
	 */
	private HibernateUtil() {
	}

	/**
	 * Get instance
	 */
	public static SessionFactory getSessionFactory() {
		return getSessionFactory(Config.HIBERNATE_HBM2DDL);
	}

	/**
	 * Construct annotation configuration
	 */
	private static Configuration getConfiguration() {
		Configuration cfg = new Configuration();

		// Add annotated beans
		cfg.addAnnotatedClass(Activity.class);
		cfg.addAnnotatedClass(Bookmark.class);
		cfg.addAnnotatedClass(MimeType.class);
		cfg.addAnnotatedClass(DatabaseMetadataType.class);
		cfg.addAnnotatedClass(DatabaseMetadataValue.class);
		cfg.addAnnotatedClass(DatabaseMetadataSequence.class);
		cfg.addAnnotatedClass(WikiPage.class);
		cfg.addAnnotatedClass(ZohoToken.class);
		cfg.addAnnotatedClass(com.openkm.dao.bean.Config.class);
		cfg.addAnnotatedClass(Profiling.class);
		cfg.addAnnotatedClass(DashboardActivity.class);
		cfg.addAnnotatedClass(Css.class);
		cfg.addAnnotatedClass(Omr.class);
		cfg.addAnnotatedClass(PendingTask.class);

		// Cache
		cfg.addAnnotatedClass(UserItems.class);
		cfg.addAnnotatedClass(UserNodeKeywords.class);

		// Automation
		cfg.addAnnotatedClass(AutomationRule.class);
		cfg.addAnnotatedClass(AutomationValidation.class);
		cfg.addAnnotatedClass(AutomationAction.class);

		// New Persistence Model
		cfg.addAnnotatedClass(NodeBase.class);
		cfg.addAnnotatedClass(NodeDocument.class);
		cfg.addAnnotatedClass(NodeDocumentVersion.class);
		cfg.addAnnotatedClass(NodeFolder.class);
		cfg.addAnnotatedClass(NodeMail.class);
		cfg.addAnnotatedClass(NodeNote.class);
		cfg.addAnnotatedClass(NodeLock.class);
		cfg.addAnnotatedClass(NodeProperty.class);
		cfg.addAnnotatedClass(RegisteredPropertyGroup.class);

		//..
		cfg.addAnnotatedClass(MailImportError.class);
		cfg.addAnnotatedClass(Plugin.class);
		
		return cfg;
	}

	/**
	 * Get instance
	 */
	public static SessionFactory getSessionFactory(String hbm2ddl) {
		if (sessionFactory == null) {
			try {
				// Configure Hibernate
				Configuration cfg = getConfiguration().configure();
				cfg.setProperty("hibernate.dialect", Config.HIBERNATE_DIALECT);
				cfg.setProperty("hibernate.connection.datasource", Config.HIBERNATE_DATASOURCE);
				cfg.setProperty("hibernate.hbm2ddl.auto", hbm2ddl);
				cfg.setProperty("hibernate.show_sql", Config.HIBERNATE_SHOW_SQL);
				cfg.setProperty("hibernate.generate_statistics", Config.HIBERNATE_STATISTICS);
				cfg.setProperty("hibernate.search.analyzer", Config.HIBERNATE_SEARCH_ANALYZER);
				cfg.setProperty("hibernate.search.default.directory_provider", "org.hibernate.search.store.FSDirectoryProvider");
				cfg.setProperty("hibernate.search.default.indexBase", Config.HIBERNATE_SEARCH_INDEX_HOME);
				cfg.setProperty("hibernate.search.default.exclusive_index_use", Config.HIBERNATE_SEARCH_INDEX_EXCLUSIVE);
				cfg.setProperty("hibernate.search.default.optimizer.operation_limit.max", "500");
				cfg.setProperty("hibernate.search.default.optimizer.transaction_limit.max", "75");
				cfg.setProperty("hibernate.worker.execution", "async");

				// http://relation.to/Bloggers/PostgreSQLAndBLOBs
				// cfg.setProperty("hibernate.jdbc.use_streams_for_binary", "false");

				// Show configuration
				log.info("Hibernate 'hibernate.dialect' = {}", cfg.getProperty("hibernate.dialect"));
				log.info("Hibernate 'hibernate.connection.datasource' = {}", cfg.getProperty("hibernate.connection.datasource"));
				log.info("Hibernate 'hibernate.hbm2ddl.auto' = {}", cfg.getProperty("hibernate.hbm2ddl.auto"));
				log.info("Hibernate 'hibernate.show_sql' = {}", cfg.getProperty("hibernate.show_sql"));
				log.info("Hibernate 'hibernate.generate_statistics' = {}", cfg.getProperty("hibernate.generate_statistics"));
				log.info("Hibernate 'hibernate.search.default.directory_provider' = {}",
						cfg.getProperty("hibernate.search.default.directory_provider"));
				log.info("Hibernate 'hibernate.search.default.indexBase' = {}", cfg.getProperty("hibernate.search.default.indexBase"));

				if (HBM2DDL_CREATE.equals(hbm2ddl)) {
					// In case of database schema creation, also clean filesystem data.
					// This means, conversion cache, file datastore and Lucene indexes.
					log.info("Cleaning filesystem data from: {}", Config.REPOSITORY_HOME);
					FileUtils.deleteQuietly(new File(Config.REPOSITORY_HOME));
				}

				// Create database schema, if needed
				sessionFactory = cfg.buildSessionFactory();

				if (HBM2DDL_CREATE.equals(hbm2ddl)) {
					log.info("Executing specific import for: {}", Config.HIBERNATE_DIALECT);
					InputStream is = ConfigUtils.getResourceAsStream("default.sql");
					String adapted = DatabaseDialectAdapter.dialectAdapter(is, Config.HIBERNATE_DIALECT);
					executeSentences(new StringReader(adapted));
					IOUtils.closeQuietly(is);
				}

				if (HBM2DDL_CREATE.equals(hbm2ddl) || HBM2DDL_UPDATE.equals(hbm2ddl)) {
					// Create or update translations
					for (String res : ConfigUtils.getResources("i18n")) {
						String oldTrans = null;
						String langId = null;

						// Preserve translation changes
						if (HBM2DDL_UPDATE.equals(hbm2ddl)) {
							langId = FileUtils.getFileName(res);
							log.info("Preserving translations for: {}", langId);
							oldTrans = preserveTranslations(langId);
						}

						InputStream isLang = ConfigUtils.getResourceAsStream("i18n/" + res);
						log.info("Importing translation: {}", res);
						executeSentences(new InputStreamReader(isLang));
						IOUtils.closeQuietly(isLang);

						// Apply previous translation changes
						if (HBM2DDL_UPDATE.equals(hbm2ddl)) {
							if (oldTrans != null) {
								log.info("Restoring translations for: {}", langId);
								executeSentences(new StringReader(oldTrans));
							}
						}
					}

					// Replace "create" or "update" by "none" to prevent repository reset on restart
					if (Boolean.parseBoolean(Config.HIBERNATE_CREATE_AUTOFIX)) {
						log.info("Executing Hibernate create autofix");
						hibernateCreateAutofix(Config.HOME_DIR + "/" + Config.OPENKM_CONFIG);
					} else {
						log.info("Hibernate create autofix not executed because of {}={}", Config.PROPERTY_HIBERNATE_CREATE_AUTOFIX,
								Config.HIBERNATE_CREATE_AUTOFIX);
					}
				}
			} catch (HibernateException e) {
				log.error(e.getMessage(), e);
				throw new ExceptionInInitializerError(e);
			} catch (URISyntaxException e) {
				log.error(e.getMessage(), e);
				throw new ExceptionInInitializerError(e);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				throw new ExceptionInInitializerError(e);
			}
		}

		return sessionFactory;
	}

	/**
	 * Preserve current translations
	 */
	private static String preserveTranslations(String lgId) {
		log.debug("preserveTranslations({})", lgId);

		try {
			Language language = LanguageDAO.findByPk(lgId);
			StringBuffer sb = new StringBuffer();

			for (Translation translation : language.getTranslations()) {
				sb.append("UPDATE OKM_TRANSLATION SET ");
				sb.append("TR_TEXT='").append(translation.getText().replaceAll("'", "''")).append("' ");
				sb.append("WHERE ");
				sb.append("TR_MODULE='").append(translation.getTranslationId().getModule()).append("' AND ");
				sb.append("TR_KEY='").append(translation.getTranslationId().getKey()).append("' AND ");
				sb.append("TR_LANGUAGE='").append(language.getId()).append("';");
				sb.append("\n");
			}

			return sb.toString();
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Get instance
	 */
	public static SessionFactory getSessionFactory(Configuration cfg) {
		if (sessionFactory == null) {
			sessionFactory = cfg.buildSessionFactory();
		}

		return sessionFactory;
	}

	/**
	 * Close factory
	 */
	public static void closeSessionFactory() {
		if (sessionFactory != null) {
			sessionFactory.close();
			sessionFactory = null;
		}
	}

	/**
	 * Close session
	 */
	public static void close(Session session) {
		if (session != null && session.isOpen()) {
			session.close();
		}
	}

	/**
	 * Commit transaction
	 */
	public static void commit(Transaction tx) {
		if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
			tx.commit();
		}
	}

	/**
	 * Rollback transaction
	 */
	public static void rollback(Transaction tx) {
		if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
			tx.rollback();
		}
	}

	/**
	 * Convert from Blob to byte array
	 */
	public static byte[] toByteArray(Blob fromImageBlob) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			byte buf[] = new byte[4000];
			int dataSize;
			InputStream is = fromImageBlob.getBinaryStream();

			try {
				while ((dataSize = is.read(buf)) != -1) {
					baos.write(buf, 0, dataSize);
				}
			} finally {
				if (is != null) {
					is.close();
				}
			}

			return baos.toByteArray();
		} catch (Exception e) {
		}

		return null;
	}

	/**
	 * HQL to SQL translator
	 */
	public static String toSql(String hql) {
		if (hql != null && hql.trim().length() > 0) {
			final QueryTranslatorFactory qtf = new ASTQueryTranslatorFactory();
			final SessionFactoryImplementor sfi = (SessionFactoryImplementor) sessionFactory;
			final QueryTranslator translator = qtf.createQueryTranslator(hql, hql, Collections.EMPTY_MAP, sfi);
			translator.compile(Collections.EMPTY_MAP, false);
			return translator.getSQLString();
		}

		return null;
	}

	/**
	 * Load specific database import
	 */
	public static void executeSentences(final Reader rd) {
		Session session = null;
		Transaction tx = null;

		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			session.doWork(new Work() {
				@Override
				public void execute(Connection con) throws SQLException {
					try {
						for (HashMap<String, String> error : LegacyDAO.executeScript(con, rd)) {
							log.error("Error during script execution at line {}: {} [ {} ]",
									new Object[]{error.get("ln"), error.get("msg"), error.get("sql")});
						}
					} catch (IOException e) {
						log.error(e.getMessage(), e);
					} finally {
						IOUtils.closeQuietly(rd);
					}
				}
			});

			commit(tx);
		} catch (Exception e) {
			rollback(tx);
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Generate database schema and initial data for a defined dialect
	 */
	public static void generateDatabase(String dialect) throws IOException {
		// Configure Hibernate
		log.info("Exporting Database Schema...");
		String dbSchema = EnvironmentDetector.getUserHome() + "/schema.sql";
		Configuration cfg = getConfiguration().configure();
		cfg.setProperty("hibernate.dialect", dialect);
		SchemaExport se = new SchemaExport(cfg);
		se.setOutputFile(dbSchema);
		se.setDelimiter(";");
		se.setFormat(false);
		se.create(false, false);
		log.info("Database Schema exported to {}", dbSchema);

		String initialData = new File("").getAbsolutePath() + "/src/main/resources/default.sql";
		log.info("Exporting Initial Data from '{}'...", initialData);
		String initData = EnvironmentDetector.getUserHome() + "/data.sql";
		FileInputStream fis = new FileInputStream(initialData);
		String ret = DatabaseDialectAdapter.dialectAdapter(fis, dialect);
		FileWriter fw = new FileWriter(initData);
		IOUtils.write(ret, fw);
		fw.flush();
		fw.close();
		log.info("Initial Data exported to {}", initData);
	}

	/**
	 * Replace "create" or "update" by "none" to prevent repository reset on restart
	 */
	@SuppressWarnings("unchecked")
	public static void hibernateCreateAutofix(String configFile) throws IOException {
		FileReader fr = null;
		FileWriter fw = null;

		try {
			// Open and close reader
			fr = new FileReader(configFile);
			List<String> lines = IOUtils.readLines(fr);
			IOUtils.closeQuietly(fr);

			// Modify configuration file
			fw = new FileWriter(configFile);

			for (String line : lines) {
				line = line.trim();
				int idx = line.indexOf("=");

				if (idx > -1) {
					String key = line.substring(0, idx).trim();
					String value = line.substring(idx + 1, line.length()).trim();

					if (Config.PROPERTY_HIBERNATE_HBM2DDL.equals(key)) {
						value = HBM2DDL_NONE;
					}

					fw.write(key + "=" + value + "\n");
				} else {
					fw.write(line + "\n");
				}
			}

			fw.flush();
		} finally {
			IOUtils.closeQuietly(fr);
			IOUtils.closeQuietly(fw);
		}
	}
}
