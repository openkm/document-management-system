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

package com.openkm.servlet;

import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.openkm.cache.CacheProvider;
import com.openkm.cache.UserItemsManager;
import com.openkm.cache.UserNodeKeywordsManager;
import com.openkm.core.*;
import com.openkm.dao.HibernateUtil;
import com.openkm.extension.core.ExtensionManager;
import com.openkm.kea.RDFREpository;
import com.openkm.module.db.DbRepositoryModule;
import com.openkm.module.db.stuff.FsDataStore;
import com.openkm.spring.SystemAuthentication;
import com.openkm.util.*;
import com.openkm.util.pendtask.PendingTaskExecutor;
import org.apache.commons.io.IOUtils;
import org.jbpm.JbpmContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * Servlet Startup Class
 */
public class RepositoryStartupServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(RepositoryStartupServlet.class);
	private static final long serialVersionUID = 1L;
	private static Timer uiTimer; // Update Info (OpenKM Update Information)
	private static Timer cronTimer; // CRON Manager
	private static Timer uinTimer; // User Interface Notification (Create From Administration)
	private static Cron cron;
	private static UINotification uin;
	private static UpdateInfo ui;
	private static boolean hasConfiguredDataStore = false;
	private static boolean running = false;

	@Override
	public void init() throws ServletException {
		super.init();
		ServletContext sc = getServletContext();

		// Read configuration file
		Properties config = Config.load(sc);

		// Get OpenKM version
		WarUtils.readAppVersion(sc);
		log.info("*** Application version: {} ***", WarUtils.getAppVersion());

		// Initialize cache manager
		// NOTE: Should be executed BEFORE Hibernate initialization because it creates Ehcache
		// singleton instance from correct ehcache.xml configuration file
		log.info("*** Initialize cache manager... ***");
		CacheProvider.getInstance().getManager();

		// Database initialize
		log.info("*** Hibernate initialize ***");
		HibernateUtil.getSessionFactory();

		// Create missing directories
		// NOTE: Should be executed AFTER Hibernate initialization because if in created mode
		// initialization will drop these directories
		createMissingDirs();

		try {
			// Initialize property groups
			log.info("*** Initialize property groups... ***");
			FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		// Initialize language detection engine
		try {
			log.info("*** Initialize language detection engine... ***");
			DetectorFactory.loadProfile(Config.LANG_PROFILES_BASE);
		} catch (LangDetectException e) {
			log.error(e.getMessage(), e);
		}

		// Load database configuration
		Config.reload(sc, config);

		// Invoke start
		start();

		// Activity log
		UserActivity.log(Config.SYSTEM_USER, "MISC_OPENKM_START", null, null, null);
	}

	@Override
	public void destroy() {
		super.destroy();

		// Activity log
		UserActivity.log(Config.SYSTEM_USER, "MISC_OPENKM_STOP", null, null, null);

		// Invoke stop
		stop(this);

		try {
			log.info("*** Shutting down cache manager... ***");
			CacheProvider.getInstance().getManager().shutdown();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		try {
			// Database shutdown
			log.info("*** Hibernate shutdown ***");
			HibernateUtil.closeSessionFactory();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Start OpenKM and possible repository and database initialization
	 */
	public static synchronized void start() throws ServletException {
		SystemAuthentication systemAuth = new SystemAuthentication();

		if (running) {
			throw new IllegalStateException("OpenKM already started");
		}

		try {
			log.info("*** Repository initializing... ***");

			if (Config.REPOSITORY_NATIVE) {
				systemAuth.enable();
				DbRepositoryModule.initialize();
				systemAuth.disable();
			} else {
				// Other implementation
			}

			log.info("*** Repository initialized ***");
		} catch (Exception e) {
			throw new ServletException(e.getMessage(), e);
		}

		if (Config.USER_ITEM_CACHE) {
			// Deserialize
			try {
				log.info("*** Cache deserialization ***");
				UserItemsManager.deserialize();
				UserNodeKeywordsManager.deserialize();
			} catch (DatabaseException e) {
				log.warn(e.getMessage(), e);
			}
		}

		log.info("*** User database initialized ***");

		// Create timers
		uiTimer = new Timer("Update Info", true);
		cronTimer = new Timer("Crontab Manager", true);
		uinTimer = new Timer("User Interface Notification", true);

		// Workflow
		log.info("*** Initializing workflow engine... ***");
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		jbpmContext.setSessionFactory(HibernateUtil.getSessionFactory());
		jbpmContext.getGraphSession();
		jbpmContext.getJbpmConfiguration().getJobExecutor().start(); // startJobExecutor();
		jbpmContext.close();

		// Mime types
		log.info("*** Initializing MIME types... ***");
		MimeTypeConfig.loadMimeTypes();

		log.info("*** Activating update info ***");
		ui = new UpdateInfo();
		uiTimer.schedule(ui, TimeUnit.MINUTES.toMillis(5), TimeUnit.HOURS.toMillis(24)); // First in 5 min, next each 24 hours

		log.info("*** Activating cron ***");
		cron = new Cron();
		Calendar calCron = Calendar.getInstance();
		calCron.add(Calendar.MINUTE, 1);
		calCron.set(Calendar.SECOND, 0);
		calCron.set(Calendar.MILLISECOND, 0);

		// Round begin to next minute, 0 seconds, 0 miliseconds
		cronTimer.scheduleAtFixedRate(cron, calCron.getTime(), 60 * 1000); // First in 1 min, next each 1 min

		log.info("*** Activating UI Notification ***");
		uin = new UINotification();

		// First in 1 second next in x minutes
		uinTimer.scheduleAtFixedRate(uin, 1000, TimeUnit.MINUTES.toMillis(Config.SCHEDULE_UI_NOTIFICATION));

		try {
			// General maintenance works
			String dapContent = "com.openkm.dao.DashboardActivityDAO.purge();";
			CronTabUtils.createOrUpdate("Dashboard Activity Purge", "@daily", dapContent);

			String uisContent = "com.openkm.cache.UserItemsManager.serialize();";
			CronTabUtils.createOrUpdate("User Items Serialize", "@hourly", uisContent);

			String ruiContent = "com.openkm.cache.UserItemsManager.refreshDbUserItems();";
			CronTabUtils.createOrUpdate("Refresh User Items", "@weekly", ruiContent);

			String umiContent = "new com.openkm.core.UserMailImporter().run();";
			CronTabUtils.createOrUpdate("User Mail Importer", "*/30 * * * *", umiContent);

			String tewContent = "new com.openkm.extractor.TextExtractorWorker().run();";
			CronTabUtils.createOrUpdate("Text Extractor Worker", "*/5 * * * *", tewContent);

			// String riContent = "new com.openkm.core.RepositoryInfo().run();";
			// CronTabUtils.createOrUpdate("Repository Info", "@daily", riContent);

			String swdContent = "new com.openkm.core.Watchdog().run();";
			CronTabUtils.createOrUpdate("Session Watchdog", "*/5 * * * *", swdContent);

			String pptContent = "new com.openkm.util.pendtask.PendingTaskExecutor().run();";
			CronTabUtils.createOrUpdate("Process Pending Tasks", "*/5 * * * *", pptContent);
			
			// Datastore garbage collection
			if (!Config.REPOSITORY_NATIVE && hasConfiguredDataStore) {
				String dgcContent = "new com.openkm.module.jcr.stuff.DataStoreGarbageCollector().run();";
				CronTabUtils.createOrUpdate("Datastore Garbage Collector", "@daily", dgcContent);
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}

		try {
			log.info("*** Activating thesaurus repository ***");
			RDFREpository.getInstance();
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}

		try {
			if (Config.REMOTE_CONVERSION_SERVER.equals("")) {
				if (!Config.SYSTEM_OPENOFFICE_PATH.equals("")) {
					log.info("*** Start OpenOffice manager ***");
					DocConverter.getInstance().start();
				} else if (!Config.SYSTEM_OPENOFFICE_SERVER.equals("")) {
					log.info("*** Using OpenOffice conversion server ***");
				} else {
					log.warn("*** No OpenOffice manager nor server configured ***");
				}
			} else {
				log.info("*** Remote conversion configured ***");
			}
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
		}

		// Initialize plugin framework
		ExtensionManager.getInstance();

		try {
			log.info("*** Execute start script ***");
			File script = new File(Config.HOME_DIR + File.separatorChar + Config.START_SCRIPT);
			ExecutionUtils.runScript(script);
			File jar = new File(Config.HOME_DIR + File.separatorChar + Config.START_JAR);
			ExecutionUtils.getInstance().runJar(jar);
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
		}

		try {
			log.info("*** Execute start SQL ***");
			File sql = new File(Config.HOME_DIR + File.separatorChar + Config.START_SQL);

			if (sql.exists() && sql.canRead()) {
				FileReader fr = new FileReader(sql);
				HibernateUtil.executeSentences(fr);
				IOUtils.closeQuietly(fr);
			} else {
				log.warn("Unable to read sql: {}", sql.getPath());
			}
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
		}

		// OpenKM is started
		running = true;
	}

	/**
	 * Close OpenKM and free resources
	 */
	public static synchronized void stop(GenericServlet gs) {
		if (!running) {
			throw new IllegalStateException("OpenKM not started");
		}

		// Shutdown plugin framework
		ExtensionManager.getInstance().shutdown();

		try {
			if (Config.REMOTE_CONVERSION_SERVER.equals("")) {
				if (!Config.SYSTEM_OPENOFFICE_PATH.equals("")) {
					log.info("*** Shutting down OpenOffice manager ***");
					DocConverter.getInstance().stop();
				}
			}
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
		}

		log.info("*** Shutting down UI Notification... ***");
		uin.cancel();

		log.info("*** Shutting down cron... ***");
		cron.cancel();

		if (Config.UPDATE_INFO) {
			log.info("*** Shutting down update info... ***");
			ui.cancel();
		}

		// Cancel timers
		cronTimer.cancel();
		uinTimer.cancel();
		uiTimer.cancel();

		// Shutdown pending task executor
		log.info("*** Shutting pending task executor... ***");
		PendingTaskExecutor.shutdown();

		log.info("*** Shutting down repository... ***");

		if (Config.USER_ITEM_CACHE) {
			// Serialize
			try {
				log.info("*** Cache serialization ***");
				UserItemsManager.serialize();
				UserNodeKeywordsManager.serialize();
			} catch (DatabaseException e) {
				log.warn(e.getMessage(), e);
			}
		}

		log.info("*** Repository shutted down ***");

		try {
			log.info("*** Execute stop script ***");
			File script = new File(Config.HOME_DIR + File.separatorChar + Config.STOP_SCRIPT);
			ExecutionUtils.runScript(script);
			File jar = new File(Config.HOME_DIR + File.separatorChar + Config.STOP_JAR);
			ExecutionUtils.getInstance().runJar(jar);
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
		}

		try {
			log.info("*** Execute stop SQL ***");
			File sql = new File(Config.HOME_DIR + File.separatorChar + Config.STOP_SQL);

			if (sql.exists() && sql.canRead()) {
				FileReader fr = new FileReader(sql);
				HibernateUtil.executeSentences(fr);
				IOUtils.closeQuietly(fr);
			} else {
				log.warn("Unable to read sql: {}", sql.getPath());
			}
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
		}

		log.info("*** Shutting down workflow engine... ***");
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		jbpmContext.getJbpmConfiguration().getJobExecutor().stop();
		jbpmContext.getJbpmConfiguration().close();
		jbpmContext.close();

		// OpenKM is stopped
		running = false;
	}

	/**
	 * Create missing needed directories.
	 */
	private static void createMissingDirs() {
		// Initialize DXF cache folder
		File dxfCacheFolder = new File(Config.REPOSITORY_CACHE_DXF);
		if (!dxfCacheFolder.exists()) {
			log.info("Create missing directory {}", dxfCacheFolder.getPath());
			dxfCacheFolder.mkdirs();
		}

		// Initialize PDF cache folder
		File pdfCacheFolder = new File(Config.REPOSITORY_CACHE_PDF);
		if (!pdfCacheFolder.exists()) {
			log.info("Create missing directory {}", pdfCacheFolder.getPath());
			pdfCacheFolder.mkdirs();
		}

		// Initialize SWF cache folder
		File swfCacheFolder = new File(Config.REPOSITORY_CACHE_SWF);
		if (!swfCacheFolder.exists()) {
			log.info("Create missing directory {}", swfCacheFolder.getPath());
			swfCacheFolder.mkdirs();
		}

		// Initialize chroot folder
		if (Config.SYSTEM_MULTIPLE_INSTANCES || Config.CLOUD_MODE) {
			File chrootFolder = new File(Config.INSTANCE_CHROOT_PATH);
			if (!chrootFolder.exists()) {
				log.info("Create missing directory {}", chrootFolder.getPath());
				chrootFolder.mkdirs();
			}
		}

		// Initialize purgatory home
		if (Config.REPOSITORY_PURGATORY_HOME != null && !Config.REPOSITORY_PURGATORY_HOME.isEmpty()) {
			File purgatoryFolder = new File(Config.REPOSITORY_PURGATORY_HOME);
			if (!purgatoryFolder.exists()) {
				log.info("Create missing directory {}", purgatoryFolder.getPath());
				purgatoryFolder.mkdirs();
			}
		}

		if (FsDataStore.DATASTORE_BACKEND_FS.equals(Config.REPOSITORY_DATASTORE_BACKEND)) {
			// Initialize datastore
			File repoDatastoreFolder = new File(Config.REPOSITORY_DATASTORE_HOME);
			if (!repoDatastoreFolder.exists()) {
				log.info("Create missing directory {}", repoDatastoreFolder.getPath());
				repoDatastoreFolder.mkdirs();
			}
		}

		// Initialize Hibernate Search indexes
		// NOTE: This is already created on Hibernate initialization
		File hSearchIndexesFolder = new File(Config.HIBERNATE_SEARCH_INDEX_HOME);
		if (!hSearchIndexesFolder.exists()) {
			log.info("Create missing directory {}", hSearchIndexesFolder.getPath());
			hSearchIndexesFolder.mkdirs();
		}
	}
}
