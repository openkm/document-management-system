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

package com.openkm.core;

import com.openkm.bean.ConfigStoredFile;
import com.openkm.dao.ConfigDAO;
import com.openkm.dao.SearchDAO;
import com.openkm.module.db.stuff.DbSimpleAccessManager;
import com.openkm.module.db.stuff.FsDataStore;
import com.openkm.principal.DatabasePrincipalAdapter;
import com.openkm.util.EnvironmentDetector;
import com.openkm.util.FormatUtil;
import com.openkm.validator.password.NoPasswordValidator;
import com.openkm.vernum.MajorMinorVersionNumerationAdapter;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

public class Config {
	private static final Logger log = LoggerFactory.getLogger(Config.class);
	public static TreeMap<String, String> values = new TreeMap<>();
	public static final String DEFAULT_CONTEXT = "OpenKM";

	// Server specific configuration
	public static final String HOME_DIR = EnvironmentDetector.getServerHomeDir();
	public static final String LOG_DIR = EnvironmentDetector.getServerLogDir();
	public static final String JNDI_BASE = EnvironmentDetector.getServerJndiBase();
	public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
	public static final URI PLUGIN_DIR = new File(Config.HOME_DIR + File.separator + "plugins").toURI();
	public static final String WEBAPPS_DIR = Config.HOME_DIR + File.separator + "webapps";

	// Scripting
	public static final String START_SCRIPT = "start.bsh";
	public static final String STOP_SCRIPT = "stop.bsh";
	public static final String START_JAR = "start.jar";
	public static final String STOP_JAR = "stop.jar";
	public static final String START_SQL = "start.sql";
	public static final String STOP_SQL = "stop.sql";

	// Configuration files
	public static final String OPENKM_CONFIG = "OpenKM.cfg";
	public static String CONTEXT;
	public static String INSTANCE_HOME;
	public static String INSTANCE_DIRNAME = "instances";
	public static String INSTANCE_CHROOT_PATH;
	public static String JBPM_CONFIG;
	public static String PROPERTY_GROUPS_XML;
	public static String PROPERTY_GROUPS_CND;
	public static String DTD_BASE;
	public static String LANG_PROFILES_BASE;

	// Default users
	public static String PROPERTY_SYSTEM_USER = "user.system";
	public static String PROPERTY_ADMIN_USER = "user.admin";

	// General configuration
	public static String EXPORT_METADATA_EXT = ".okm";
	public static String ROOT_NODE_UUID = "cafebabe-cafe-babe-cafe-babecafebabe";
	public static Version LUCENE_VERSION = Version.LUCENE_31;
	public static String DEFAULT_CRONTAB_MAIL = "none@nomail.com";

	// Preview cache
	public static String REPOSITORY_CACHE_HOME;
	public static String REPOSITORY_CACHE_DIRNAME = "cache";
	public static String REPOSITORY_CACHE_DXF;
	public static String REPOSITORY_CACHE_PDF;
	public static String REPOSITORY_CACHE_SWF;

	// Experimental features
	public static final String PROPERTY_PLUGIN_DEBUG = "plugin.debug";
	public static final String PROPERTY_MANAGED_TEXT_EXTRACTION = "managed.text.extraction";
	public static final String PROPERTY_MANAGED_TEXT_EXTRACTION_BATCH = "managed.text.extraction.batch";
	public static final String PROPERTY_MANAGED_TEXT_EXTRACTION_POOL_SIZE = "managed.text.extraction.pool.size";
	public static final String PROPERTY_MANAGED_TEXT_EXTRACTION_POOL_THREADS = "managed.text.extraction.pool.threads";
	public static final String PROPERTY_MANAGED_TEXT_EXTRACTION_POOL_TIMEOUT = "managed.text.extraction.pool.timeout";
	public static final String PROPERTY_MANAGED_TEXT_EXTRACTION_CONCURRENT = "managed.text.extraction.concurrent";
	public static final String PROPERTY_MOBILE_THEME = "mobile.theme";
	public static final String PROPERTY_REPOSITORY_CONTENT_CHECKSUM = "repository.content.checksum";
	public static final String PROPERTY_REPOSITORY_PURGATORY_HOME = "repository.purgatory.home";
	public static final String PROPERTY_REPOSITORY_STATS_OPTIMIZATION = "repository.stats.optimization";
	public static final String PROPERTY_AMAZON_ACCESS_KEY = "amazon.access.key";
	public static final String PROPERTY_AMAZON_SECRET_KEY = "amazon.secret.key";
	public static final String PROPERTY_NATIVE_SQL_OPTIMIZATIONS = "native.sql.optimizations";
	public static final String PROPERTY_USER_PASSWORD_RESET = "user.password.reset";
	public static final String PROPERTY_KEEP_SESSION_ALIVE_INTERVAL = "keep.session.alive.interval";
	public static final String PROPERTY_ACTIVITY_LOG_ACTIONS = "activity.log.actions";
	public static final String PROPERTY_STORE_NODE_PATH = "store.node.path";
	public static final String PROPERTY_TOMCAT_CONNECTOR_URI_ENCODING = "tomcat.connector.uri.encoding";

	// Security properties
	public static final String PROPERTY_SECURITY_ACCESS_MANAGER = "security.access.manager";
	public static final String PROPERTY_SECURITY_SEARCH_EVALUATION = "security.search.evaluation";
	public static final String PROPERTY_SECURITY_EXTENDED_MASK = "security.extended.mask";
	public static final String PROPERTY_SECURITY_MODE_MULTIPLE = "security.mode.multiple";
	public static final String PROPERTY_SECURITY_LIVE_CHANGE_NODE_LIMIT = "security.live.change.node.limit";

	// Configuration properties
	public static final String PROPERTY_REPOSITORY_UUID = "repository.uuid";
	public static final String PROPERTY_REPOSITORY_VERSION = "repository.version";
	public static final String PROPERTY_REPOSITORY_HOME = "repository.home";
	public static final String PROPERTY_REPOSITORY_DATASTORE_BACKEND = "repository.datastore.backend";
	public static final String PROPERTY_REPOSITORY_DATASTORE_HOME = "repository.datastore.home";
	public static final String PROPERTY_REPOSITORY_CACHE_HOME = "repository.cache.home";
	public static final String PROPERTY_VERSION_NUMERATION_ADAPTER = "version.numeration.adapter";
	public static final String PROPERTY_VERSION_NUMERATION_FORMAT = "version.numeration.format";
	public static final String PROPERTY_VERSION_APPEND_DOWNLOAD = "version.append.download";
	public static final String PROPERTY_MAX_FILE_SIZE = "max.file.size";
	public static final String PROPERTY_MAX_SEARCH_RESULTS = "max.search.results";
	public static final String PROPERTY_MAX_SEARCH_CLAUSES = "max.search.clauses";
	public static final String PROPERTY_MIN_SEARCH_CHARACTERS = "min.search.characters";
	public static final String PROPERTY_SEND_MAIL_FROM_USER = "send.mail.from.user";
	public static final String PROPERTY_DEFAULT_USER_ROLE = "default.user.role";
	public static final String PROPERTY_DEFAULT_ADMIN_ROLE = "default.admin.role";
	public static final String PROPERTY_WEBSERVICES_VISIBLE_PROPERTIES = "webservices.visible.properties";

	// Workflow
	public static final String PROPERTY_WORKFLOW_START_TASK_AUTO_RUN = "workflow.start.task.auto.run";
	public static final String PROPERTY_WORKFLOW_RUN_CONFIG_FORM = "workflow.run.config.form";

	// Principal
	public static final String PROPERTY_PRINCIPAL_ADAPTER = "principal.adapter";
	public static final String PROPERTY_PRINCIPAL_DATABASE_FILTER_INACTIVE_USERS = "principal.database.filter.inactive.users";
	public static final String PROPERTY_PRINCIPAL_HIDE_CONNECTION_ROLES = "principal.hide.connection.roles";
	public static final String PROPERTY_PRINCIPAL_IDENTIFIER_VALIDATION = "principal.identifier.validation";

	// LDAP
	public static final String PROPERTY_PRINCIPAL_LDAP_SERVER = "principal.ldap.server";
	public static final String PROPERTY_PRINCIPAL_LDAP_SECURITY_PRINCIPAL = "principal.ldap.security.principal";
	public static final String PROPERTY_PRINCIPAL_LDAP_SECURITY_CREDENTIALS = "principal.ldap.security.credentials";
	public static final String PROPERTY_PRINCIPAL_LDAP_REFERRAL = "principal.ldap.referral";
	public static final String PROPERTY_PRINCIPAL_LDAP_USERS_FROM_ROLES = "principal.ldap.users.from.roles";

	public static final String PROPERTY_PRINCIPAL_LDAP_USER_SEARCH_BASE = "principal.ldap.user.search.base";
	public static final String PROPERTY_PRINCIPAL_LDAP_USER_SEARCH_FILTER = "principal.ldap.user.search.filter";
	public static final String PROPERTY_PRINCIPAL_LDAP_USER_ATTRIBUTE = "principal.ldap.user.attribute";

	public static final String PROPERTY_PRINCIPAL_LDAP_ROLE_SEARCH_BASE = "principal.ldap.role.search.base";
	public static final String PROPERTY_PRINCIPAL_LDAP_ROLE_SEARCH_FILTER = "principal.ldap.role.search.filter";
	public static final String PROPERTY_PRINCIPAL_LDAP_ROLE_ATTRIBUTE = "principal.ldap.role.attribute";

	public static final String PROPERTY_PRINCIPAL_LDAP_USERNAME_SEARCH_BASE = "principal.ldap.username.search.base";
	public static final String PROPERTY_PRINCIPAL_LDAP_USERNAME_SEARCH_FILTER = "principal.ldap.username.search.filter";
	public static final String PROPERTY_PRINCIPAL_LDAP_USERNAME_ATTRIBUTE = "principal.ldap.username.attribute";

	public static final String PROPERTY_PRINCIPAL_LDAP_MAIL_SEARCH_BASE = "principal.ldap.mail.search.base";
	public static final String PROPERTY_PRINCIPAL_LDAP_MAIL_SEARCH_FILTER = "principal.ldap.mail.search.filter";
	public static final String PROPERTY_PRINCIPAL_LDAP_MAIL_ATTRIBUTE = "principal.ldap.mail.attribute";

	public static final String PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_BASE = "principal.ldap.users.by.role.search.base";
	public static final String PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_FILTER = "principal.ldap.users.by.role.search.filter";
	public static final String PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_ATTRIBUTE = "principal.ldap.users.by.role.attribute";

	public static final String PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_BASE = "principal.ldap.roles.by.user.search.base";
	public static final String PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_FILTER = "principal.ldap.roles.by.user.search.filter";
	public static final String PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_ATTRIBUTE = "principal.ldap.roles.by.user.attribute";

	public static final String PROPERTY_RESTRICT_FILE_MIME = "restrict.file.mime";
	public static final String PROPERTY_RESTRICT_FILE_NAME = "restrict.file.name";

	public static final String PROPERTY_NOTIFICATION_MESSAGE_SUBJECT = "notification.message.subject";
	public static final String PROPERTY_NOTIFICATION_MESSAGE_BODY = "notification.message.body";

	public static final String PROPERTY_SUBSCRIPTION_MESSAGE_SUBJECT = "subscription.message.subject";
	public static final String PROPERTY_SUBSCRIPTION_MESSAGE_BODY = "subscription.message.body";

	public static final String PROPERTY_PROPOSED_SUBSCRIPTION_MESSAGE_SUBJECT = "proposed.subscription.message.subject";
	public static final String PROPERTY_PROPOSED_SUBSCRIPTION_MESSAGE_BODY = "proposed.subscription.message.body";

	public static final String PROPERTY_SUBSCRIPTION_TWITTER_USER = "notify.twitter.user";
	public static final String PROPERTY_SUBSCRIPTION_TWITTER_PASSWORD = "notify.twitter.password";
	public static final String PROPERTY_SUBSCRIPTION_TWITTER_STATUS = "notify.twitter.status";

	public static final String PROPERTY_SYSTEM_DEMO = "system.demo";
	public static final String PROPERTY_SYSTEM_MULTIPLE_INSTANCES = "system.multiple.instances";
	public static final String PROPERTY_SYSTEM_APACHE_REQUEST_HEADER_FIX = "system.apache.request.header.fix";
	public static final String PROPERTY_SYSTEM_WEBDAV_SERVER = "system.webdav.server";
	public static final String PROPERTY_SYSTEM_WEBDAV_FIX = "system.webdav.fix";
	public static final String PROPERTY_SYSTEM_READONLY = "system.readonly";
	public static final String PROPERTY_SYSTEM_MAINTENANCE = "system.maintenance";
	public static final String PROPERTY_SYSTEM_OCR = "system.ocr";
	public static final String PROPERTY_SYSTEM_OCR_ROTATE = "system.ocr.rotate";
	public static final String PROPERTY_SYSTEM_PDF_FORCE_OCR = "system.pdf.force.ocr";
	public static final String PROPERTY_SYSTEM_OPENOFFICE_PROGRAM = "system.openoffice.program";
	public static final String PROPERTY_SYSTEM_OPENOFFICE_DICTIONARY = "system.openoffice.dictionary";
	public static final String PROPERTY_SYSTEM_IMAGEMAGICK_CONVERT = "system.imagemagick.convert";
	public static final String PROPERTY_SYSTEM_SWFTOOLS_PDF2SWF = "system.swftools.pdf2swf";
	public static final String PROPERTY_SYSTEM_GHOSTSCRIPT = "system.ghostscript";
	public static final String PROPERTY_SYSTEM_DWG2DXF = "system.dwg2dxf";
	public static final String PROPERTY_SYSTEM_ANTIVIR = "system.antivir";
	public static final String PROPERTY_SYSTEM_PDFIMAGES = "system.pdfimages";
	public static final String PROPERTY_SYSTEM_CATDOC_XLS2CSV = "system.catdoc.xls2csv";
	public static final String PROPERTY_SYSTEM_LOGIN_LOWERCASE = "system.login.lowercase";
	public static final String PROPERTY_SYSTEM_PREVIEWER = "system.previewer";
	public static final String PROPERTY_SYSTEM_DOCUMENT_NAME_MISMATCH_CHECK = "system.document.name.mismatch.check";
	public static final String PROPERTY_SYSTEM_KEYWORD_LOWERCASE = "system.keyword.lowercase";
	public static final String PROPERTY_SYSTEM_EXECUTION_TIMEOUT = "system.execution.timeout";
	public static final String PROPERTY_SYSTEM_PROFILING = "system.profiling";

	public static final String PROPERTY_UPDATE_INFO = "update.info";
	public static final String PROPERTY_APPLICATION_URL = "application.url";
	public static final String PROPERTY_DEFAULT_LANG = "default.lang";
	public static final String PROPERTY_USER_ASSIGN_DOCUMENT_CREATION = "user.assign.document.creation";
	public static final String PROPERTY_USER_KEYWORDS_CACHE = "user.keywords.cache";
	public static final String PROPERTY_USER_ITEM_CACHE = "user.item.cache";
	public static final String PROPERTY_UPLOAD_THROTTLE_FILTER = "upload.throttle.filter";
	public static final String PROPERTY_REMOTE_CONVERSION_SERVER = "remote.conversion.server";

	// Schedule
	public static final String PROPERTY_SCHEDULE_SESSION_KEEPALIVE = "schedule.session.keepalive";
	public static final String PROPERTY_SCHEDULE_DASHBOARD_REFRESH = "schedule.dashboard.refresh";
	public static final String PROPERTY_SCHEDULE_UI_NOTIFICATION = "schedule.ui.notification";

	// KEA
	// Used in generate_thesaurus.jsp
	public static final String PROPERTY_KEA_THESAURUS_SKOS_FILE = "kea.thesaurus.skos.file";
	public static final String PROPERTY_KEA_THESAURUS_OWL_FILE = "kea.thesaurus.owl.file";
	public static final String PROPERTY_KEA_THESAURUS_VOCABULARY_SERQL = "kea.thesaurus.vocabulary.serql";
	public static final String PROPERTY_KEA_THESAURUS_BASE_URL = "kea.thesaurus.base.url";
	public static final String PROPERTY_KEA_THESAURUS_TREE_ROOT = "kea.thesaurus.tree.root";
	public static final String PROPERTY_KEA_THESAURUS_TREE_CHILDS = "kea.thesaurus.tree.childs";

	// Validator
	public static final String PROPERTY_VALIDATOR_PASSWORD = "validator.password";

	public static final String PROPERTY_VALIDATOR_PASSWORD_MIN_LENGTH = "validator.password.min.length";
	public static final String PROPERTY_VALIDATOR_PASSWORD_MAX_LENGTH = "validator.password.max.length";
	public static final String PROPERTY_VALIDATOR_PASSWORD_MIN_LOWERCASE = "validator.password.min.lowercase";
	public static final String PROPERTY_VALIDATOR_PASSWORD_MIN_UPPERCASE = "validator.password.min.uppercase";
	public static final String PROPERTY_VALIDATOR_PASSWORD_MIN_DIGITS = "validator.password.min.digits";
	public static final String PROPERTY_VALIDATOR_PASSWORD_MIN_SPECIAL = "validator.password.mini.special";

	// Hibernate
	public static final String PROPERTY_HIBERNATE_DIALECT = "hibernate.dialect";
	public static final String PROPERTY_HIBERNATE_DATASOURCE = "hibernate.datasource";
	public static final String PROPERTY_HIBERNATE_HBM2DDL = "hibernate.hbm2ddl"; // Used in login.jsp
	public static final String PROPERTY_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
	public static final String PROPERTY_HIBERNATE_STATISTICS = "hibernate.statistics";
	public static final String PROPERTY_HIBERNATE_SEARCH_ANALYZER = "hibernate.search.analyzer";
	public static final String PROPERTY_HIBERNATE_CREATE_AUTOFIX = "hibernate.create.autofix";
	public static final String PROPERTY_HIBERNATE_INDEXER_MASS_INDEXER = "hibernate.indexer.mass.indexer";
	public static final String PROPERTY_HIBERNATE_INDEXER_BATCH_SIZE_LOAD_OBJECTS = "hibernate.indexer.batch.size.load.objects";
	public static final String PROPERTY_HIBERNATE_INDEXER_THREADS_SUBSEQUENT_FETCHING = "hibernate.indexer.threads.subsequent.fetching";
	public static final String PROPERTY_HIBERNATE_INDEXER_THREADS_LOAD_OBJECTS = "hibernate.indexer.threads.load.objects";
	public static final String PROPERTY_HIBERNATE_INDEXER_THREADS_INDEX_WRITER = "hibernate.indexer.threads.index.writer";

	// Hibernate Search indexes
	public static String PROPERTY_HIBERNATE_SEARCH_INDEX_HOME = "hibernate.search.index.home";
	public static String PROPERTY_HIBERNATE_SEARCH_INDEX_EXCLUSIVE = "hibernate.search.index.exclusive";
	public static String PROPERTY_HIBERNATE_SEARCH_WORKER_EXECUTION = "hibernate.search.worker.execution";
	public static String PROPERTY_HIBERNATE_SEARCH_WORKER_THREAD_POOL_SIZE = "hibernate.search.worker.thread.pool.size";
	public static String PROPERTY_HIBERNATE_SEARCH_WORKER_BUFFER_QUEUE_MAX = "hibernate.search.worker.buffer.queue.max";

	// Logo icons & login texts
	public static final String PROPERTY_TEXT_BANNER = "text.banner";
	public static final String PROPERTY_TEXT_WELCOME = "text.welcome";
	public static final String PROPERTY_TEXT_TITLE = "text.title";
	public static final String PROPERTY_LOGO_TINY = "logo.tiny";
	public static final String PROPERTY_LOGO_LOGIN = "logo.login";
	public static final String PROPERTY_LOGO_MOBILE = "logo.mobile";
	public static final String PROPERTY_LOGO_REPORT = "logo.report";
	public static final String PROPERTY_LOGO_FAVICON = "logo.favicon";

	// Zoho
	public static final String PROPERTY_ZOHO_USER = "zoho.user";
	public static final String PROPERTY_ZOHO_PASSWORD = "zoho.password";
	public static final String PROPERTY_ZOHO_API_KEY = "zoho.api.key";
	public static final String PROPERTY_ZOHO_SECRET_KEY = "zoho.secret.key";

	// OpenMeetings
	public static final String PROPERTY_OPENMEETINGS_URL = "openmeetings.url";
	public static final String PROPERTY_OPENMEETINGS_PORT = "openmeetings.port";
	public static final String PROPERTY_OPENMEETINGS_USER = "openmeetings.user";
	public static final String PROPERTY_OPENMEETINGS_CREDENTIALS = "openmeetings.credentials";

	// Cloud
	public static final String PROPERTY_CLOUD_MODE = "cloud.mode";
	public static final String PROPERTY_CLOUD_MAX_REPOSITORY_SIZE = "cloud.max.repository.size";
	public static final String PROPERTY_CLOUD_MAX_USERS = "cloud.max.users";

	// TinyMCE 4
	public static final String PROPERTY_TINYMCE4_THEME = "extension.tinymce4.theme";
	public static final String PROPERTY_TINYMCE4_PLUGINS = "extension.tinymce4.plugins";
	public static final String PROPERTY_TINYMCE4_TOOLBAR1 = "extension.tinymce4.toolbar1";
	public static final String PROPERTY_TINYMCE4_TOOLBAR2 = "extension.tinymce4.toolbar2";

	// HTML syntax highlighter
	public static String PROPERTY_HTML_SINTAXHIGHLIGHTER_CORE = "html.syntaxhighlighter.core";
	public static String PROPERTY_HTML_SINTAXHIGHLIGHTER_THEME = "html.syntaxhighlighter.theme";

	// CSV
	public static String PROPERTY_CSV_FORMAT_DELIMITER = "csv.format.delimiter";
	public static String PROPERTY_CSV_FORMAT_QUOTE_CHARACTER = "csv.format.quote.character";
	public static String PROPERTY_CSV_FORMAT_COMMENT_INDICATOR = "csv.format.comment.indicator";
	public static String PROPERTY_CSV_FORMAT_SKIP_HEADER = "csv.format.skip_header";
	public static String PROPERTY_CSV_FORMAT_IGNORE_EMPTY_LINES = "csv.format.ignore.empty.lines";

	// Extra Tab Workspace
	public static final String PROPERTY_EXTRA_TAB_WORKSPACE_LABEL = "extra.tab.workspace.label";
	public static final String PROPERTY_EXTRA_TAB_WORKSPACE_URL = "extra.tab.workspace.url";

	// Unit Testing
	public static final String PROPERTY_UNIT_TESTING_USER = "unit.testing.user";
	public static final String PROPERTY_UNIT_TESTING_PASSWORD = "unit.testing.password";
	public static final String PROPERTY_UNIT_TESTING_FOLDER = "unit.testing.folder";

	// Rss news
	public static String PROPERTY_RSS_NEWS = "rss.news";
	public static String PROPERTY_RSS_NEWS_BOX_WIDTH = "rss.news.box.width";
	public static String PROPERTY_RSS_NEWS_MAX_SIZE = "rss.news.max.size";
	public static String PROPERTY_RSS_NEWS_VISIBLE = "rss.news.visible";

	// Ask for drag and drop updates
	public static String PROPERTY_ASK_DRAG_AND_DROP_UPDATES = "ask.drag.and.drop.updates";

	/**
	 * Default values
	 */
	// Experimental features
	public static boolean PLUGIN_DEBUG = false;
	public static String MOBILE_CONTEXT = "mobile";
	public static String MOBILE_THEME = "";
	public static boolean MANAGED_TEXT_EXTRACTION = true;
	public static int MANAGED_TEXT_EXTRACTION_BATCH = 10;
	public static int MANAGED_TEXT_EXTRACTION_POOL_SIZE = 5;
	public static int MANAGED_TEXT_EXTRACTION_POOL_THREADS = 5;
	public static int MANAGED_TEXT_EXTRACTION_POOL_TIMEOUT = 1; // 1 minute
	public static boolean MANAGED_TEXT_EXTRACTION_CONCURRENT = false;
	public static boolean REPOSITORY_CONTENT_CHECKSUM = true;
	public static String REPOSITORY_PURGATORY_HOME = "";
	public static boolean REPOSITORY_STATS_OPTIMIZATION = true;
	public static String AMAZON_ACCESS_KEY = "";
	public static String AMAZON_SECRET_KEY = "";
	public static boolean NATIVE_SQL_OPTIMIZATIONS = false;
	public static boolean USER_PASSWORD_RESET = true;
	public static int KEEP_SESSION_ALIVE_INTERVAL = 5; // 5 minutes
	public static List<String> ACTIVITY_LOG_ACTIONS = new ArrayList<>();
	private static final String DEFAULT_ACTIVITY_LOG_ACTIONS =
			"LOGIN\n" +
					"LOGOUT\n" +
					"CREATE_.*\n" +
					"DELETE_.*\n" +
					"PURGE_.*\n" +
					"MOVE_.*\n" +
					"COPY_.*\n" +
					"CHECKOUT_DOCUMENT\n" +
					"CHECKIN_DOCUMENT\n" +
					"GET_DOCUMENT_CONTENT.*";
	public static boolean STORE_NODE_PATH = true;
	public static String TOMCAT_CONNECTOR_URI_ENCODING = "ISO-8859-1";

	// Security properties
	public static String SECURITY_ACCESS_MANAGER = "";
	public static String SECURITY_SEARCH_EVALUATION = "";
	public static int SECURITY_EXTENDED_MASK = 0;
	public static boolean SECURITY_MODE_MULTIPLE = false;
	public static int SECURITY_LIVE_CHANGE_NODE_LIMIT = 100;

	// Configuration properties
	public static String REPOSITORY_HOME;
	public static String REPOSITORY_DIRNAME = "repository";
	public static String REPOSITORY_DATASTORE_BACKEND;
	public static String REPOSITORY_DATASTORE_HOME;
	public static String VERSION_NUMERATION_ADAPTER = MajorMinorVersionNumerationAdapter.class.getCanonicalName();
	public static String VERSION_NUMERATION_FORMAT = "%d";
	public static boolean VERSION_APPEND_DOWNLOAD = false;
	public static long MAX_FILE_SIZE;
	public static int MAX_SEARCH_RESULTS;
	public static int MAX_SEARCH_CLAUSES;
	public static int MIN_SEARCH_CHARACTERS;
	public static boolean SEND_MAIL_FROM_USER;
	public static String SYSTEM_USER = "system";
	public static String ADMIN_USER = "okmAdmin";

	public static String DEFAULT_USER_ROLE = "ROLE_USER";
	public static String DEFAULT_ADMIN_ROLE = "ROLE_ADMIN";

	public static List<String> WEBSERVICES_VISIBLE_PROPERTIES = new ArrayList<>();

	// Workflow
	public static String WORKFLOW_RUN_CONFIG_FORM = "run_config";
	public static boolean WORKFLOW_START_TASK_AUTO_RUN = true;
	public static String WORKFLOW_PROCESS_INSTANCE_VARIABLE_UUID = "uuid";
	public static String WORKFLOW_PROCESS_INSTANCE_VARIABLE_PATH = "path";

	// Principal
	public static String PRINCIPAL_ADAPTER = DatabasePrincipalAdapter.class.getCanonicalName();
	public static boolean PRINCIPAL_DATABASE_FILTER_INACTIVE_USERS = true;
	public static boolean PRINCIPAL_HIDE_CONNECTION_ROLES = false;
	public static String PRINCIPAL_IDENTIFIER_VALIDATION = "^[a-zA-Z0-9_]+$";

	// LDAP
	public static String PRINCIPAL_LDAP_SERVER; // ldap://phoenix.server:389
	public static String PRINCIPAL_LDAP_SECURITY_PRINCIPAL; // "cn=Administrator,cn=Users,dc=openkm,dc=com"
	public static String PRINCIPAL_LDAP_SECURITY_CREDENTIALS; // "xxxxxx"
	public static String PRINCIPAL_LDAP_REFERRAL;
	public static boolean PRINCIPAL_LDAP_USERS_FROM_ROLES;

	public static List<String> PRINCIPAL_LDAP_USER_SEARCH_BASE = new ArrayList<>(); // ou=people,dc=openkm,dc=com
	public static String PRINCIPAL_LDAP_USER_SEARCH_FILTER; // (&(objectClass=posixAccount)(!(objectClass=gosaUserTemplate)))
	public static String PRINCIPAL_LDAP_USER_ATTRIBUTE; // uid

	public static List<String> PRINCIPAL_LDAP_ROLE_SEARCH_BASE = new ArrayList<>(); // ou=groups,dc=openkm,dc=com
	public static String PRINCIPAL_LDAP_ROLE_SEARCH_FILTER; // (&(objectClass=posixGroup)(cn=*)(|(description=*OpenKM*)(cn=users)))
	public static String PRINCIPAL_LDAP_ROLE_ATTRIBUTE; // cn

	public static String PRINCIPAL_LDAP_USERNAME_SEARCH_BASE; // ou=people,dc=openkm,dc=com
	public static String PRINCIPAL_LDAP_USERNAME_SEARCH_FILTER; // (&(objectClass=posixAccount)(!(objectClass=gosaUserTemplate)))
	public static String PRINCIPAL_LDAP_USERNAME_ATTRIBUTE; // displayName

	public static String PRINCIPAL_LDAP_MAIL_SEARCH_BASE; // uid={0},ou=people,dc=openkm,dc=com
	public static String PRINCIPAL_LDAP_MAIL_SEARCH_FILTER; // (&(objectClass=inetOrgPerson)(mail=*))
	public static String PRINCIPAL_LDAP_MAIL_ATTRIBUTE; // mail

	public static String PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_BASE;
	public static String PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_FILTER; // (&(objectClass=group)(cn={0}))
	public static String PRINCIPAL_LDAP_USERS_BY_ROLE_ATTRIBUTE;

	public static String PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_BASE;
	public static String PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_FILTER; // (&(objectClass=group)(cn={0}))
	public static String PRINCIPAL_LDAP_ROLES_BY_USER_ATTRIBUTE;

	public static boolean RESTRICT_FILE_MIME;
	public static String RESTRICT_FILE_NAME;

	public static String NOTIFICATION_MESSAGE_SUBJECT;
	public static String NOTIFICATION_MESSAGE_BODY;

	public static String SUBSCRIPTION_MESSAGE_SUBJECT;
	public static String SUBSCRIPTION_MESSAGE_BODY;

	public static String PROPOSED_SUBSCRIPTION_MESSAGE_SUBJECT;
	public static String PROPOSED_SUBSCRIPTION_MESSAGE_BODY;

	public static String SUBSCRIPTION_TWITTER_USER;
	public static String SUBSCRIPTION_TWITTER_PASSWORD;
	public static String SUBSCRIPTION_TWITTER_STATUS;

	public static boolean SYSTEM_DEMO;
	public static boolean SYSTEM_MULTIPLE_INSTANCES;
	public static boolean SYSTEM_APACHE_REQUEST_HEADER_FIX;
	public static boolean SYSTEM_WEBDAV_SERVER;
	public static boolean SYSTEM_WEBDAV_FIX;
	public static boolean SYSTEM_MAINTENANCE;
	public static boolean SYSTEM_READONLY;
	public static String SYSTEM_OCR = "";
	public static String SYSTEM_OCR_ROTATE = "";
	public static boolean SYSTEM_PDF_FORCE_OCR;
	public static String SYSTEM_OPENOFFICE_PROGRAM = "";
	public static String SYSTEM_OPENOFFICE_DICTIONARY = "";
	public static String SYSTEM_IMAGEMAGICK_CONVERT = "";
	public static String SYSTEM_SWFTOOLS_PDF2SWF = "";
	public static String SYSTEM_GHOSTSCRIPT = "";
	public static String SYSTEM_DWG2DXF = "";
	public static String SYSTEM_ANTIVIR = "";
	public static String SYSTEM_PDFIMAGES = "";
	public static String SYSTEM_CATDOC_XLS2CSV = "";
	public static boolean SYSTEM_LOGIN_LOWERCASE = false;
	public static String SYSTEM_PREVIEWER = "";
	public static boolean SYSTEM_DOCUMENT_NAME_MISMATCH_CHECK = true;
	public static boolean SYSTEM_KEYWORD_LOWERCASE = false;
	public static int SYSTEM_EXECUTION_TIMEOUT = 5; // 5 min
	public static boolean SYSTEM_PROFILING = false;

	public static boolean UPDATE_INFO = true;
	public static String APPLICATION_URL;
	public static String APPLICATION_BASE;
	public static String DEFAULT_LANG = "";
	public static boolean USER_ASSIGN_DOCUMENT_CREATION = true;
	public static boolean USER_KEYWORDS_CACHE = false;
	public static boolean USER_ITEM_CACHE = true;
	public static boolean UPLOAD_THROTTLE_FILTER;
	public static String REMOTE_CONVERSION_SERVER = "";

	// Schedule
	public static int SCHEDULE_SESSION_KEEPALIVE = 15; // 15 min
	public static int SCHEDULE_DASHBOARD_REFRESH = 30; // 30 min
	public static int SCHEDULE_UI_NOTIFICATION = 1; // 1 min

	// KEA
	public static String KEA_THESAURUS_SKOS_FILE;
	public static String KEA_THESAURUS_OWL_FILE;
	public static String KEA_THESAURUS_VOCABULARY_SERQL;
	public static String KEA_THESAURUS_BASE_URL;
	public static String KEA_THESAURUS_TREE_ROOT;
	public static String KEA_THESAURUS_TREE_CHILDS;

	// Validator
	public static String VALIDATOR_PASSWORD = NoPasswordValidator.class.getCanonicalName();

	public static int VALIDATOR_PASSWORD_MIN_LENGTH;
	public static int VALIDATOR_PASSWORD_MAX_LENGTH;
	public static int VALIDATOR_PASSWORD_MIN_LOWERCASE;
	public static int VALIDATOR_PASSWORD_MIN_UPPERCASE;
	public static int VALIDATOR_PASSWORD_MIN_DIGITS;
	public static int VALIDATOR_PASSWORD_MIN_SPECIAL;

	public static String VALIDATOR_PASSWORD_ERROR_MIN_LENGTH = "Password error: too short";
	public static String VALIDATOR_PASSWORD_ERROR_MAX_LENGTH = "Password error: too long";
	public static String VALIDATOR_PASSWORD_ERROR_MIN_LOWERCASE = "Password error: too few lowercase characters";
	public static String VALIDATOR_PASSWORD_ERROR_MIN_UPPERCASE = "Password error: too few uppercase characters";
	public static String VALIDATOR_PASSWORD_ERROR_MIN_DIGITS = "Password error: too few digits";
	public static String VALIDATOR_PASSWORD_ERROR_MIN_SPECIAL = "Password error: too few special characters";

	// Hibernate
	public static String HIBERNATE_DIALECT = "";
	public static String HIBERNATE_HBM2DDL = "";
	public static String HIBERNATE_SHOW_SQL = "false";
	public static String HIBERNATE_STATISTICS = "false";
	public static String HIBERNATE_DATASOURCE = JNDI_BASE + "jdbc/" + DEFAULT_CONTEXT + "DS";
	public static String HIBERNATE_SEARCH_ANALYZER = "org.apache.lucene.analysis.standard.StandardAnalyzer";
	public static String HIBERNATE_CREATE_AUTOFIX = "true";
	public static boolean HIBERNATE_INDEXER_MASS_INDEXER = false;
	public static int HIBERNATE_INDEXER_BATCH_SIZE_LOAD_OBJECTS = 30;
	public static int HIBERNATE_INDEXER_THREADS_SUBSEQUENT_FETCHING = 8;
	public static int HIBERNATE_INDEXER_THREADS_LOAD_OBJECTS = 4;
	public static int HIBERNATE_INDEXER_THREADS_INDEX_WRITER = 3;

	// Hibernate Search indexes
	public static String HIBERNATE_SEARCH_INDEX_HOME;
	public static String HIBERNATE_SEARCH_INDEX_DIRNAME = "index";
	public static String HIBERNATE_SEARCH_INDEX_EXCLUSIVE = "true";
	public static String HIBERNATE_SEARCH_WORKER_EXECUTION = "sync";
	public static String HIBERNATE_SEARCH_WORKER_THREAD_POOL_SIZE = "1";
	public static String HIBERNATE_SEARCH_WORKER_BUFFER_QUEUE_MAX = "256";

	// Logo icons and login texts
	public static String TEXT_BANNER;
	public static String TEXT_WELCOME;
	public static String TEXT_TITLE;
	public static ConfigStoredFile LOGO_TINY;
	public static ConfigStoredFile LOGO_LOGIN;
	public static ConfigStoredFile LOGO_MOBILE;
	public static ConfigStoredFile LOGO_REPORT;
	public static ConfigStoredFile LOGO_FAVICON;

	// Zoho
	public static String ZOHO_USER;
	public static String ZOHO_PASSWORD;
	public static String ZOHO_API_KEY;
	public static String ZOHO_SECRET_KEY;

	// OpenMeetings
	public static String OPENMEETINGS_URL;
	public static String OPENMEETINGS_PORT;
	public static String OPENMEETINGS_USER;
	public static String OPENMEETINGS_CREDENTIALS;

	// Cloud
	public static boolean CLOUD_MODE;
	public static long CLOUD_MAX_REPOSITORY_SIZE;
	public static int CLOUD_MAX_USERS;

	// TinyMCE 4
	public static String TINYMCE4_THEME = "modern";
	public static String TINYMCE4_PLUGINS = "advlist autolink lists link image charmap print preview hr anchor pagebreak searchreplace wordcount visualblocks visualchars fullscreen insertdatetime media nonbreaking save table contextmenu directionality emoticons template paste textcolor code";
	public static String TINYMCE4_TOOLBAR1 = "insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image";
	public static String TINYMCE4_TOOLBAR2 = "okm_checkin okm_cancelcheckout okm_searchDocument okm_searchFolder okm_searchImage okm_searchDocumentToDownload okm_codeHighlight | print preview media | forecolor backcolor emoticons";

	// HTML syntax highlighter
	public static String HTML_SINTAXHIGHLIGHTER_CORE = "shCoreEclipse.css";
	public static String HTML_SINTAXHIGHLIGHTER_THEME = "shThemeEclipse.css";

	// CSV
	public static String CSV_FORMAT_DELIMITER = ";";
	public static String CSV_FORMAT_QUOTE_CHARACTER = "\"";
	public static String CSV_FORMAT_COMMENT_INDICATOR = "#";
	public static boolean CSV_FORMAT_SKIP_HEADER = false;
	public static boolean CSV_FORMAT_IGNORE_EMPTY_LINES = true;

	// Extra Tab Workspace
	public static String EXTRA_TAB_WORKSPACE_LABEL;
	public static String EXTRA_TAB_WORKSPACE_URL;

	// Unit Testing
	public static String UNIT_TESTING_USER = "okmAdmin";
	public static String UNIT_TESTING_PASSWORD = "admin";
	public static String UNIT_TESTING_FOLDER = "/okm:root/okmTesting";

	// OpenKM RSS news
	public static boolean RSS_NEWS = true;
	public static int RSS_NEWS_BOX_WIDTH = 300;
	public static int RSS_NEWS_MAX_SIZE = 10;
	public static int RSS_NEWS_VISIBLE = 1;

	// Misc
	public static int SESSION_EXPIRATION = 1800; // 30 mins (session.getMaxInactiveInterval())
	public static String LIST_SEPARATOR = ";";

	// Ask for drag and drop updates
	public static boolean ASK_DRAG_AND_DROP_UPDATES = false;

	/**
	 * Get url base
	 */
	private static String getBase(String url) {
		int idx = url.lastIndexOf('/');
		String ret = "";

		if (idx > 0) {
			ret = url.substring(0, idx);
		}

		return ret;
	}

	/**
	 * Load OpenKM configuration from OpenKM.cfg
	 */
	public static Properties load(ServletContext sc) {
		Properties config = new Properties();
		String configFile = HOME_DIR + File.separator + OPENKM_CONFIG;
		CONTEXT = sc.getContextPath().isEmpty() ? "" : sc.getContextPath().substring(1);

		// Initialize DTD location
		// TODO Add trailing "/" when upgrade to Tomcat 8 => "WEB-INF/classes/dtd/"
		DTD_BASE = sc.getRealPath("WEB-INF/classes/dtd");
		log.info("** Application {} has DTDs at {} **", sc.getServletContextName(), DTD_BASE);

		// Initialize language profiles location
		// TODO Add trailing "/" when upgrade to Tomcat 8 => "WEB-INF/classes/lang-profiles/"
		LANG_PROFILES_BASE = sc.getRealPath("WEB-INF/classes/lang-profiles");
		log.info("** Language profiles at {} **", LANG_PROFILES_BASE);

		// Read config
		try {
			log.info("** Reading config file " + configFile + " **");
			FileInputStream fis = new FileInputStream(configFile);
			config.load(fis);
			fis.close();

			// Hibernate
			HIBERNATE_DIALECT = config.getProperty(PROPERTY_HIBERNATE_DIALECT, HIBERNATE_DIALECT);
			values.put(PROPERTY_HIBERNATE_DIALECT, HIBERNATE_DIALECT);
			HIBERNATE_DATASOURCE = config.getProperty(PROPERTY_HIBERNATE_DATASOURCE, JNDI_BASE + "jdbc/" + (CONTEXT.isEmpty() ? DEFAULT_CONTEXT : CONTEXT) + "DS");
			values.put(PROPERTY_HIBERNATE_DATASOURCE, HIBERNATE_DATASOURCE);
			HIBERNATE_HBM2DDL = config.getProperty(PROPERTY_HIBERNATE_HBM2DDL, HIBERNATE_HBM2DDL);
			values.put(PROPERTY_HIBERNATE_HBM2DDL, HIBERNATE_HBM2DDL);
			HIBERNATE_SHOW_SQL = config.getProperty(PROPERTY_HIBERNATE_SHOW_SQL, HIBERNATE_SHOW_SQL);
			values.put(PROPERTY_HIBERNATE_SHOW_SQL, HIBERNATE_SHOW_SQL);
			HIBERNATE_STATISTICS = config.getProperty(PROPERTY_HIBERNATE_STATISTICS, HIBERNATE_STATISTICS);
			values.put(PROPERTY_HIBERNATE_STATISTICS, HIBERNATE_STATISTICS);
			HIBERNATE_SEARCH_ANALYZER = config.getProperty(PROPERTY_HIBERNATE_SEARCH_ANALYZER, HIBERNATE_SEARCH_ANALYZER);
			values.put(PROPERTY_HIBERNATE_SEARCH_ANALYZER, HIBERNATE_SEARCH_ANALYZER);
			HIBERNATE_CREATE_AUTOFIX = config.getProperty(PROPERTY_HIBERNATE_CREATE_AUTOFIX, HIBERNATE_CREATE_AUTOFIX);
			values.put(PROPERTY_HIBERNATE_CREATE_AUTOFIX, HIBERNATE_CREATE_AUTOFIX);
			HIBERNATE_SEARCH_INDEX_EXCLUSIVE = config.getProperty(PROPERTY_HIBERNATE_SEARCH_INDEX_EXCLUSIVE, HIBERNATE_SEARCH_INDEX_EXCLUSIVE);
			values.put(PROPERTY_HIBERNATE_SEARCH_INDEX_EXCLUSIVE, HIBERNATE_SEARCH_INDEX_EXCLUSIVE);
			HIBERNATE_SEARCH_WORKER_EXECUTION = config.getProperty(PROPERTY_HIBERNATE_SEARCH_WORKER_EXECUTION, HIBERNATE_SEARCH_WORKER_EXECUTION);
			values.put(PROPERTY_HIBERNATE_SEARCH_WORKER_EXECUTION, HIBERNATE_SEARCH_WORKER_EXECUTION);
			HIBERNATE_SEARCH_WORKER_THREAD_POOL_SIZE = config.getProperty(PROPERTY_HIBERNATE_SEARCH_WORKER_THREAD_POOL_SIZE, HIBERNATE_SEARCH_WORKER_THREAD_POOL_SIZE);
			values.put(PROPERTY_HIBERNATE_SEARCH_WORKER_THREAD_POOL_SIZE, HIBERNATE_SEARCH_WORKER_THREAD_POOL_SIZE);
			HIBERNATE_SEARCH_WORKER_BUFFER_QUEUE_MAX = config.getProperty(PROPERTY_HIBERNATE_SEARCH_WORKER_BUFFER_QUEUE_MAX, HIBERNATE_SEARCH_WORKER_BUFFER_QUEUE_MAX);
			values.put(PROPERTY_HIBERNATE_SEARCH_WORKER_BUFFER_QUEUE_MAX, HIBERNATE_SEARCH_WORKER_BUFFER_QUEUE_MAX);

			// Cloud
			CLOUD_MODE = "on".equalsIgnoreCase(config.getProperty(PROPERTY_CLOUD_MODE, "off"));
			values.put(PROPERTY_CLOUD_MODE, Boolean.toString(CLOUD_MODE));
			CLOUD_MAX_REPOSITORY_SIZE = FormatUtil.parseSize(config.getProperty(PROPERTY_CLOUD_MAX_REPOSITORY_SIZE, "0"));
			values.put(PROPERTY_CLOUD_MAX_REPOSITORY_SIZE, Long.toString(CLOUD_MAX_REPOSITORY_SIZE));
			CLOUD_MAX_USERS = Integer.parseInt(config.getProperty(PROPERTY_CLOUD_MAX_USERS, "0"));
			values.put(PROPERTY_CLOUD_MAX_USERS, Integer.toString(CLOUD_MAX_USERS));

			SYSTEM_MULTIPLE_INSTANCES = "on".equalsIgnoreCase(config.getProperty(PROPERTY_SYSTEM_MULTIPLE_INSTANCES, "off"));
			values.put(PROPERTY_SYSTEM_MULTIPLE_INSTANCES, Boolean.toString(SYSTEM_MULTIPLE_INSTANCES));

			if (CLOUD_MODE) {
				log.info("*** CLOUD MODE ***");
				INSTANCE_HOME = HOME_DIR;
				values.put("instance.home", INSTANCE_HOME);
				INSTANCE_CHROOT_PATH = INSTANCE_HOME + File.separator + "root" + File.separator;
				values.put("instance.chroot.path", INSTANCE_CHROOT_PATH);
			} else if (SYSTEM_MULTIPLE_INSTANCES) {
				log.info("*** MULTIPLE INSTANCES MODE ***");
				INSTANCE_HOME = HOME_DIR + File.separator + INSTANCE_DIRNAME + File.separator + (CONTEXT.isEmpty() ? DEFAULT_CONTEXT : CONTEXT);
				values.put("instance.home", INSTANCE_HOME);
				INSTANCE_CHROOT_PATH = INSTANCE_HOME + File.separator + "root" + File.separator;
				values.put("instance.chroot.path", INSTANCE_CHROOT_PATH);
			} else {
				INSTANCE_HOME = HOME_DIR;
				values.put("instance.home", INSTANCE_HOME);
				INSTANCE_CHROOT_PATH = "";
				values.put("instance.chroot.path", INSTANCE_CHROOT_PATH);
			}

			// Preview cache & Repository datastore backend & Hibernate Search indexes
			REPOSITORY_HOME = config.getProperty(PROPERTY_REPOSITORY_HOME, INSTANCE_HOME + File.separator + REPOSITORY_DIRNAME);
			REPOSITORY_CACHE_HOME = config.getProperty(PROPERTY_REPOSITORY_CACHE_HOME, Config.REPOSITORY_HOME + File.separator + REPOSITORY_CACHE_DIRNAME);
			REPOSITORY_DATASTORE_BACKEND = config.getProperty(PROPERTY_REPOSITORY_DATASTORE_BACKEND, FsDataStore.DATASTORE_BACKEND_FS);
			REPOSITORY_DATASTORE_HOME = config.getProperty(PROPERTY_REPOSITORY_DATASTORE_HOME, Config.REPOSITORY_HOME + File.separator + FsDataStore.DATASTORE_DIRNAME);
			HIBERNATE_SEARCH_INDEX_HOME = config.getProperty(PROPERTY_HIBERNATE_SEARCH_INDEX_HOME, Config.REPOSITORY_HOME + File.separator + HIBERNATE_SEARCH_INDEX_DIRNAME);

			values.put(PROPERTY_REPOSITORY_CACHE_HOME, REPOSITORY_CACHE_HOME);
			REPOSITORY_CACHE_DXF = REPOSITORY_CACHE_HOME + File.separator + "dxf";
			values.put("repository.cache.dxf", REPOSITORY_CACHE_DXF);
			REPOSITORY_CACHE_PDF = REPOSITORY_CACHE_HOME + File.separator + "pdf";
			values.put("repository.cache.pdf", REPOSITORY_CACHE_PDF);
			REPOSITORY_CACHE_SWF = REPOSITORY_CACHE_HOME + File.separator + "swf";
			values.put("repository.cache.swf", REPOSITORY_CACHE_SWF);
			values.put(PROPERTY_HIBERNATE_SEARCH_INDEX_HOME, HIBERNATE_SEARCH_INDEX_HOME);
			values.put(PROPERTY_REPOSITORY_DATASTORE_BACKEND, REPOSITORY_DATASTORE_BACKEND);
			values.put(PROPERTY_REPOSITORY_DATASTORE_HOME, REPOSITORY_DATASTORE_HOME);
			values.put(PROPERTY_REPOSITORY_HOME, REPOSITORY_HOME);

			JBPM_CONFIG = INSTANCE_HOME + File.separator + "jbpm.xml";
			values.put("jbpm.config", JBPM_CONFIG);

			PROPERTY_GROUPS_XML = INSTANCE_HOME + File.separator + "PropertyGroups.xml";
			values.put("property.groups.xml", PROPERTY_GROUPS_XML);
			PROPERTY_GROUPS_CND = INSTANCE_HOME + File.separator + "PropertyGroups.cnd";
			values.put("property.groups.cnd", PROPERTY_GROUPS_CND);

			for (Entry<String, String> entry : values.entrySet()) {
				log.info("LOAD - {}={}", entry.getKey(), entry.getValue());
			}
		} catch (FileNotFoundException e) {
			log.warn("** No {} file found, set default config **", OPENKM_CONFIG);
		} catch (IOException e) {
			log.warn("** IOError reading {}, set default config **", OPENKM_CONFIG);
		}

		return config;
	}

	/**
	 * Reload OpenKM configuration from database
	 */
	public static void reload(ServletContext sc, Properties cfg) {
		try {
			// Experimental features
			MOBILE_THEME = ConfigDAO.getSelectedOption(PROPERTY_MOBILE_THEME, "a|*b|c|d");
			values.put(PROPERTY_MOBILE_THEME, MOBILE_THEME);
			PLUGIN_DEBUG = ConfigDAO.getBoolean(PROPERTY_PLUGIN_DEBUG, PLUGIN_DEBUG);
			values.put(PROPERTY_PLUGIN_DEBUG, Boolean.toString(PLUGIN_DEBUG));
			MANAGED_TEXT_EXTRACTION = ConfigDAO.getBoolean(PROPERTY_MANAGED_TEXT_EXTRACTION, MANAGED_TEXT_EXTRACTION);
			values.put(PROPERTY_MANAGED_TEXT_EXTRACTION, Boolean.toString(MANAGED_TEXT_EXTRACTION));
			MANAGED_TEXT_EXTRACTION_BATCH = ConfigDAO.getInteger(PROPERTY_MANAGED_TEXT_EXTRACTION_BATCH, MANAGED_TEXT_EXTRACTION_BATCH);
			values.put(PROPERTY_MANAGED_TEXT_EXTRACTION_BATCH, Integer.toString(MANAGED_TEXT_EXTRACTION_BATCH));
			MANAGED_TEXT_EXTRACTION_POOL_SIZE = ConfigDAO.getInteger(PROPERTY_MANAGED_TEXT_EXTRACTION_POOL_SIZE, MANAGED_TEXT_EXTRACTION_POOL_SIZE);
			values.put(PROPERTY_MANAGED_TEXT_EXTRACTION_POOL_SIZE, Integer.toString(MANAGED_TEXT_EXTRACTION_POOL_SIZE));
			MANAGED_TEXT_EXTRACTION_POOL_THREADS = ConfigDAO.getInteger(PROPERTY_MANAGED_TEXT_EXTRACTION_POOL_THREADS, MANAGED_TEXT_EXTRACTION_POOL_THREADS);
			values.put(PROPERTY_MANAGED_TEXT_EXTRACTION_POOL_THREADS, Integer.toString(MANAGED_TEXT_EXTRACTION_POOL_THREADS));
			MANAGED_TEXT_EXTRACTION_POOL_TIMEOUT = ConfigDAO.getInteger(PROPERTY_MANAGED_TEXT_EXTRACTION_POOL_TIMEOUT, MANAGED_TEXT_EXTRACTION_POOL_TIMEOUT);
			values.put(PROPERTY_MANAGED_TEXT_EXTRACTION_POOL_TIMEOUT, Integer.toString(MANAGED_TEXT_EXTRACTION_POOL_TIMEOUT));
			MANAGED_TEXT_EXTRACTION_CONCURRENT = ConfigDAO.getBoolean(PROPERTY_MANAGED_TEXT_EXTRACTION_CONCURRENT, MANAGED_TEXT_EXTRACTION_CONCURRENT);
			values.put(PROPERTY_MANAGED_TEXT_EXTRACTION_CONCURRENT, Boolean.toString(MANAGED_TEXT_EXTRACTION_CONCURRENT));

			REPOSITORY_CONTENT_CHECKSUM = ConfigDAO.getBoolean(PROPERTY_REPOSITORY_CONTENT_CHECKSUM, REPOSITORY_CONTENT_CHECKSUM);
			values.put(PROPERTY_REPOSITORY_CONTENT_CHECKSUM, Boolean.toString(REPOSITORY_CONTENT_CHECKSUM));
			REPOSITORY_PURGATORY_HOME = ConfigDAO.getString(PROPERTY_REPOSITORY_PURGATORY_HOME, REPOSITORY_PURGATORY_HOME);
			values.put(PROPERTY_REPOSITORY_PURGATORY_HOME, REPOSITORY_PURGATORY_HOME);
			REPOSITORY_STATS_OPTIMIZATION = ConfigDAO.getBoolean(PROPERTY_REPOSITORY_STATS_OPTIMIZATION, REPOSITORY_STATS_OPTIMIZATION);
			values.put(PROPERTY_REPOSITORY_STATS_OPTIMIZATION, Boolean.toString(REPOSITORY_STATS_OPTIMIZATION));
			AMAZON_ACCESS_KEY = ConfigDAO.getString(PROPERTY_AMAZON_ACCESS_KEY, cfg.getProperty(PROPERTY_AMAZON_ACCESS_KEY, AMAZON_ACCESS_KEY));
			values.put(PROPERTY_AMAZON_ACCESS_KEY, AMAZON_ACCESS_KEY);
			AMAZON_SECRET_KEY = ConfigDAO.getString(PROPERTY_AMAZON_SECRET_KEY, cfg.getProperty(PROPERTY_AMAZON_SECRET_KEY, AMAZON_SECRET_KEY));
			values.put(PROPERTY_AMAZON_SECRET_KEY, AMAZON_SECRET_KEY);
			NATIVE_SQL_OPTIMIZATIONS = ConfigDAO.getBoolean(PROPERTY_NATIVE_SQL_OPTIMIZATIONS, NATIVE_SQL_OPTIMIZATIONS);
			values.put(PROPERTY_NATIVE_SQL_OPTIMIZATIONS, Boolean.toString(NATIVE_SQL_OPTIMIZATIONS));
			USER_PASSWORD_RESET = ConfigDAO.getBoolean(PROPERTY_USER_PASSWORD_RESET, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_USER_PASSWORD_RESET, "off")));
			values.put(PROPERTY_USER_PASSWORD_RESET, Boolean.toString(USER_PASSWORD_RESET));
			KEEP_SESSION_ALIVE_INTERVAL = ConfigDAO.getInteger(PROPERTY_KEEP_SESSION_ALIVE_INTERVAL, KEEP_SESSION_ALIVE_INTERVAL);
			values.put(PROPERTY_KEEP_SESSION_ALIVE_INTERVAL, Integer.toString(KEEP_SESSION_ALIVE_INTERVAL));
			ACTIVITY_LOG_ACTIONS = ConfigDAO.getList(PROPERTY_ACTIVITY_LOG_ACTIONS, DEFAULT_ACTIVITY_LOG_ACTIONS);
			values.put(PROPERTY_ACTIVITY_LOG_ACTIONS, String.valueOf(ACTIVITY_LOG_ACTIONS));
			STORE_NODE_PATH = ConfigDAO.getBoolean(PROPERTY_STORE_NODE_PATH, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_STORE_NODE_PATH, "off")));
			values.put(PROPERTY_STORE_NODE_PATH, Boolean.toString(STORE_NODE_PATH));
			TOMCAT_CONNECTOR_URI_ENCODING = ConfigDAO.getString(PROPERTY_TOMCAT_CONNECTOR_URI_ENCODING, TOMCAT_CONNECTOR_URI_ENCODING);
			values.put(PROPERTY_TOMCAT_CONNECTOR_URI_ENCODING, TOMCAT_CONNECTOR_URI_ENCODING);

			// Security properties
			SECURITY_ACCESS_MANAGER = ConfigDAO.getString(PROPERTY_SECURITY_ACCESS_MANAGER, DbSimpleAccessManager.NAME);
			values.put(PROPERTY_SECURITY_ACCESS_MANAGER, SECURITY_ACCESS_MANAGER);
			SECURITY_SEARCH_EVALUATION = ConfigDAO.getString(PROPERTY_SECURITY_SEARCH_EVALUATION, SearchDAO.SEARCH_LUCENE);
			values.put(PROPERTY_SECURITY_SEARCH_EVALUATION, SECURITY_SEARCH_EVALUATION);
			SECURITY_MODE_MULTIPLE = ConfigDAO.getBoolean(PROPERTY_SECURITY_MODE_MULTIPLE, SECURITY_MODE_MULTIPLE);
			values.put(PROPERTY_SECURITY_MODE_MULTIPLE, Boolean.toString(SECURITY_MODE_MULTIPLE));
			SECURITY_LIVE_CHANGE_NODE_LIMIT = ConfigDAO.getInteger(PROPERTY_SECURITY_LIVE_CHANGE_NODE_LIMIT, SECURITY_LIVE_CHANGE_NODE_LIMIT);
			values.put(PROPERTY_SECURITY_LIVE_CHANGE_NODE_LIMIT, Integer.toString(SECURITY_LIVE_CHANGE_NODE_LIMIT));

			VERSION_NUMERATION_ADAPTER = ConfigDAO.getString(PROPERTY_VERSION_NUMERATION_ADAPTER, cfg.getProperty(PROPERTY_VERSION_NUMERATION_ADAPTER, VERSION_NUMERATION_ADAPTER));
			values.put(PROPERTY_VERSION_NUMERATION_ADAPTER, VERSION_NUMERATION_ADAPTER);
			VERSION_NUMERATION_FORMAT = ConfigDAO.getString(PROPERTY_VERSION_NUMERATION_FORMAT, cfg.getProperty(PROPERTY_VERSION_NUMERATION_FORMAT, VERSION_NUMERATION_FORMAT));
			values.put(PROPERTY_VERSION_NUMERATION_FORMAT, VERSION_NUMERATION_FORMAT);
			VERSION_APPEND_DOWNLOAD = ConfigDAO.getBoolean(PROPERTY_VERSION_APPEND_DOWNLOAD, VERSION_APPEND_DOWNLOAD);
			values.put(PROPERTY_VERSION_APPEND_DOWNLOAD, Boolean.toString(VERSION_APPEND_DOWNLOAD));

			MAX_FILE_SIZE = FormatUtil.parseSize(ConfigDAO.getString(PROPERTY_MAX_FILE_SIZE, "0"));
			values.put(PROPERTY_MAX_FILE_SIZE, Long.toString(MAX_FILE_SIZE));
			MAX_SEARCH_RESULTS = ConfigDAO.getInteger(PROPERTY_MAX_SEARCH_RESULTS, 500);
			values.put(PROPERTY_MAX_SEARCH_RESULTS, Integer.toString(MAX_SEARCH_RESULTS));
			MAX_SEARCH_CLAUSES = ConfigDAO.getInteger(PROPERTY_MAX_SEARCH_CLAUSES, 1024);
			values.put(PROPERTY_MAX_SEARCH_CLAUSES, Integer.toString(MAX_SEARCH_CLAUSES));
			MIN_SEARCH_CHARACTERS = ConfigDAO.getInteger(PROPERTY_MIN_SEARCH_CHARACTERS, 3);
			values.put(PROPERTY_MIN_SEARCH_CHARACTERS, Integer.toString(MIN_SEARCH_CHARACTERS));
			SEND_MAIL_FROM_USER = ConfigDAO.getBoolean(PROPERTY_SEND_MAIL_FROM_USER, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_SEND_MAIL_FROM_USER, "on")));
			values.put(PROPERTY_SEND_MAIL_FROM_USER, Boolean.toString(SEND_MAIL_FROM_USER));
			DEFAULT_USER_ROLE = ConfigDAO.getString(PROPERTY_DEFAULT_USER_ROLE, cfg.getProperty(PROPERTY_DEFAULT_USER_ROLE, DEFAULT_USER_ROLE));
			values.put(PROPERTY_DEFAULT_USER_ROLE, DEFAULT_USER_ROLE);
			DEFAULT_ADMIN_ROLE = ConfigDAO.getString(PROPERTY_DEFAULT_ADMIN_ROLE, cfg.getProperty(PROPERTY_DEFAULT_ADMIN_ROLE, DEFAULT_ADMIN_ROLE));
			values.put(PROPERTY_DEFAULT_ADMIN_ROLE, DEFAULT_ADMIN_ROLE);

			WEBSERVICES_VISIBLE_PROPERTIES = ConfigDAO.getList(PROPERTY_WEBSERVICES_VISIBLE_PROPERTIES, PROPERTY_RESTRICT_FILE_NAME);
			values.put(PROPERTY_WEBSERVICES_VISIBLE_PROPERTIES, String.valueOf(WEBSERVICES_VISIBLE_PROPERTIES));

			// Set max search clauses
			BooleanQuery.setMaxClauseCount(MAX_SEARCH_CLAUSES);

			// Workflow
			WORKFLOW_RUN_CONFIG_FORM = ConfigDAO.getString(PROPERTY_WORKFLOW_RUN_CONFIG_FORM, WORKFLOW_RUN_CONFIG_FORM);
			values.put(PROPERTY_WORKFLOW_RUN_CONFIG_FORM, WORKFLOW_RUN_CONFIG_FORM);
			WORKFLOW_START_TASK_AUTO_RUN = ConfigDAO.getBoolean(PROPERTY_WORKFLOW_START_TASK_AUTO_RUN, WORKFLOW_START_TASK_AUTO_RUN);
			values.put(PROPERTY_WORKFLOW_START_TASK_AUTO_RUN, Boolean.toString(WORKFLOW_START_TASK_AUTO_RUN));

			// Principal
			PRINCIPAL_ADAPTER = ConfigDAO.getString(PROPERTY_PRINCIPAL_ADAPTER, PRINCIPAL_ADAPTER);
			values.put(PROPERTY_PRINCIPAL_ADAPTER, PRINCIPAL_ADAPTER);
			PRINCIPAL_DATABASE_FILTER_INACTIVE_USERS = ConfigDAO.getBoolean(PROPERTY_PRINCIPAL_DATABASE_FILTER_INACTIVE_USERS, PRINCIPAL_DATABASE_FILTER_INACTIVE_USERS);
			values.put(PROPERTY_PRINCIPAL_DATABASE_FILTER_INACTIVE_USERS, Boolean.toString(PRINCIPAL_DATABASE_FILTER_INACTIVE_USERS));
			PRINCIPAL_HIDE_CONNECTION_ROLES = ConfigDAO.getBoolean(PROPERTY_PRINCIPAL_HIDE_CONNECTION_ROLES, PRINCIPAL_HIDE_CONNECTION_ROLES);
			values.put(PROPERTY_PRINCIPAL_HIDE_CONNECTION_ROLES, Boolean.toString(PRINCIPAL_HIDE_CONNECTION_ROLES));
			PRINCIPAL_IDENTIFIER_VALIDATION = ConfigDAO.getString(PROPERTY_PRINCIPAL_IDENTIFIER_VALIDATION, PRINCIPAL_IDENTIFIER_VALIDATION);
			values.put(PROPERTY_PRINCIPAL_IDENTIFIER_VALIDATION, PRINCIPAL_IDENTIFIER_VALIDATION);

			// LDAP
			PRINCIPAL_LDAP_SERVER = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_SERVER, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_SERVER, PRINCIPAL_LDAP_SERVER);
			PRINCIPAL_LDAP_SECURITY_PRINCIPAL = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_SECURITY_PRINCIPAL, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_SECURITY_PRINCIPAL, PRINCIPAL_LDAP_SECURITY_PRINCIPAL);
			PRINCIPAL_LDAP_SECURITY_CREDENTIALS = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_SECURITY_CREDENTIALS, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_SECURITY_CREDENTIALS, PRINCIPAL_LDAP_SECURITY_CREDENTIALS);
			PRINCIPAL_LDAP_REFERRAL = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_REFERRAL, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_REFERRAL, PRINCIPAL_LDAP_REFERRAL);
			PRINCIPAL_LDAP_USERS_FROM_ROLES = ConfigDAO.getBoolean(PROPERTY_PRINCIPAL_LDAP_USERS_FROM_ROLES, false);
			values.put(PROPERTY_PRINCIPAL_LDAP_USERS_FROM_ROLES, Boolean.toString(PRINCIPAL_LDAP_USERS_FROM_ROLES));

			PRINCIPAL_LDAP_USER_SEARCH_BASE = ConfigDAO.getList(PROPERTY_PRINCIPAL_LDAP_USER_SEARCH_BASE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_USER_SEARCH_BASE, String.valueOf(PRINCIPAL_LDAP_USER_SEARCH_BASE));
			PRINCIPAL_LDAP_USER_SEARCH_FILTER = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_USER_SEARCH_FILTER, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_USER_SEARCH_FILTER, PRINCIPAL_LDAP_USER_SEARCH_FILTER);
			PRINCIPAL_LDAP_USER_ATTRIBUTE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_USER_ATTRIBUTE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_USER_ATTRIBUTE, PRINCIPAL_LDAP_USER_ATTRIBUTE);

			PRINCIPAL_LDAP_ROLE_SEARCH_BASE = ConfigDAO.getList(PROPERTY_PRINCIPAL_LDAP_ROLE_SEARCH_BASE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_ROLE_SEARCH_BASE, String.valueOf(PRINCIPAL_LDAP_ROLE_SEARCH_BASE));
			PRINCIPAL_LDAP_ROLE_SEARCH_FILTER = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_ROLE_SEARCH_FILTER, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_ROLE_SEARCH_FILTER, PRINCIPAL_LDAP_ROLE_SEARCH_FILTER);
			PRINCIPAL_LDAP_ROLE_ATTRIBUTE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_ROLE_ATTRIBUTE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_ROLE_ATTRIBUTE, PRINCIPAL_LDAP_ROLE_ATTRIBUTE);

			PRINCIPAL_LDAP_USERNAME_SEARCH_BASE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_USERNAME_SEARCH_BASE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_USERNAME_SEARCH_BASE, PRINCIPAL_LDAP_USERNAME_SEARCH_BASE);
			PRINCIPAL_LDAP_USERNAME_SEARCH_FILTER = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_USERNAME_SEARCH_FILTER, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_USERNAME_SEARCH_FILTER, PRINCIPAL_LDAP_USERNAME_SEARCH_FILTER);
			PRINCIPAL_LDAP_USERNAME_ATTRIBUTE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_USERNAME_ATTRIBUTE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_USERNAME_ATTRIBUTE, PRINCIPAL_LDAP_USERNAME_ATTRIBUTE);

			PRINCIPAL_LDAP_MAIL_SEARCH_BASE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_MAIL_SEARCH_BASE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_MAIL_SEARCH_BASE, PRINCIPAL_LDAP_MAIL_SEARCH_BASE);
			PRINCIPAL_LDAP_MAIL_SEARCH_FILTER = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_MAIL_SEARCH_FILTER, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_MAIL_SEARCH_FILTER, PRINCIPAL_LDAP_MAIL_SEARCH_FILTER);
			PRINCIPAL_LDAP_MAIL_ATTRIBUTE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_MAIL_ATTRIBUTE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_MAIL_ATTRIBUTE, PRINCIPAL_LDAP_MAIL_ATTRIBUTE);

			PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_BASE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_BASE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_BASE, PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_BASE);
			PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_FILTER = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_FILTER, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_FILTER, PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_FILTER);
			PRINCIPAL_LDAP_USERS_BY_ROLE_ATTRIBUTE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_ATTRIBUTE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_ATTRIBUTE, PRINCIPAL_LDAP_USERS_BY_ROLE_ATTRIBUTE);

			PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_BASE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_BASE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_BASE, PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_BASE);
			PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_FILTER = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_FILTER, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_FILTER, PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_FILTER);
			PRINCIPAL_LDAP_ROLES_BY_USER_ATTRIBUTE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_ATTRIBUTE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_ATTRIBUTE, PRINCIPAL_LDAP_ROLES_BY_USER_ATTRIBUTE);

			RESTRICT_FILE_MIME = ConfigDAO.getBoolean(PROPERTY_RESTRICT_FILE_MIME, false);
			values.put(PROPERTY_RESTRICT_FILE_MIME, Boolean.toString(RESTRICT_FILE_MIME));
			RESTRICT_FILE_NAME = ConfigDAO.getString(PROPERTY_RESTRICT_FILE_NAME, "*~;*.bak");
			values.put(PROPERTY_RESTRICT_FILE_NAME, RESTRICT_FILE_NAME);

			NOTIFICATION_MESSAGE_SUBJECT = ConfigDAO.getText(PROPERTY_NOTIFICATION_MESSAGE_SUBJECT, "OpenKM - NOTIFICATION");
			values.put(PROPERTY_NOTIFICATION_MESSAGE_SUBJECT, NOTIFICATION_MESSAGE_SUBJECT);
			NOTIFICATION_MESSAGE_BODY = ConfigDAO.getHtml(PROPERTY_NOTIFICATION_MESSAGE_BODY, "<b>Message: </b>${notificationMessage}<br/><b>User: </b>${userId}<br/><#list documentList as doc><b>Document: </b><a href=\"${doc.url}\">${doc.path}</a><br/></#list>");
			values.put(PROPERTY_NOTIFICATION_MESSAGE_BODY, NOTIFICATION_MESSAGE_BODY);

			SUBSCRIPTION_MESSAGE_SUBJECT = ConfigDAO.getText(PROPERTY_SUBSCRIPTION_MESSAGE_SUBJECT, "OpenKM - ${eventType} - ${documentPath}");
			values.put(PROPERTY_SUBSCRIPTION_MESSAGE_SUBJECT, SUBSCRIPTION_MESSAGE_SUBJECT);
			SUBSCRIPTION_MESSAGE_BODY = ConfigDAO.getHtml(PROPERTY_SUBSCRIPTION_MESSAGE_BODY, "<b>Document: </b><a href=\"${documentUrl}\">${documentPath}</a><br/><b>User: </b>${userId}<br/><b>Event: </b>${eventType}<br/><b>Comment: </b>${subscriptionComment}<br/>");
			values.put(PROPERTY_SUBSCRIPTION_MESSAGE_BODY, SUBSCRIPTION_MESSAGE_BODY);

			PROPOSED_SUBSCRIPTION_MESSAGE_SUBJECT = ConfigDAO.getText(PROPERTY_PROPOSED_SUBSCRIPTION_MESSAGE_SUBJECT, "OpenKM - PROPOSED SUBSCRIPTION");
			values.put(PROPERTY_PROPOSED_SUBSCRIPTION_MESSAGE_SUBJECT, PROPOSED_SUBSCRIPTION_MESSAGE_SUBJECT);
			PROPOSED_SUBSCRIPTION_MESSAGE_BODY = ConfigDAO.getHtml(PROPERTY_PROPOSED_SUBSCRIPTION_MESSAGE_BODY, "<b>Comment: </b>${proposedSubscriptionComment}<br/><b>User: </b>${userId}<br/><#list documentList as doc><b>Document: </b><a href=\"${doc.url}\">${doc.path}</a><br/></#list>");
			values.put(PROPERTY_PROPOSED_SUBSCRIPTION_MESSAGE_BODY, PROPOSED_SUBSCRIPTION_MESSAGE_BODY);

			SUBSCRIPTION_TWITTER_USER = ConfigDAO.getString(PROPERTY_SUBSCRIPTION_TWITTER_USER, "");
			values.put(PROPERTY_SUBSCRIPTION_TWITTER_USER, SUBSCRIPTION_TWITTER_USER);
			SUBSCRIPTION_TWITTER_PASSWORD = ConfigDAO.getString(PROPERTY_SUBSCRIPTION_TWITTER_PASSWORD, "");
			values.put(PROPERTY_SUBSCRIPTION_TWITTER_PASSWORD, SUBSCRIPTION_TWITTER_PASSWORD);
			SUBSCRIPTION_TWITTER_STATUS = ConfigDAO.getText(PROPERTY_SUBSCRIPTION_TWITTER_STATUS, "OpenKM - ${documentUrl} - ${documentPath} - ${userId} - ${eventType}");
			values.put(PROPERTY_SUBSCRIPTION_TWITTER_STATUS, SUBSCRIPTION_TWITTER_STATUS);

			SYSTEM_DEMO = ConfigDAO.getBoolean(PROPERTY_SYSTEM_DEMO, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_SYSTEM_DEMO, "off")));
			values.put(PROPERTY_SYSTEM_DEMO, Boolean.toString(SYSTEM_DEMO));
			SYSTEM_APACHE_REQUEST_HEADER_FIX = ConfigDAO.getBoolean(PROPERTY_SYSTEM_APACHE_REQUEST_HEADER_FIX, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_SYSTEM_APACHE_REQUEST_HEADER_FIX, "off")));
			values.put(PROPERTY_SYSTEM_APACHE_REQUEST_HEADER_FIX, Boolean.toString(SYSTEM_APACHE_REQUEST_HEADER_FIX));
			SYSTEM_WEBDAV_SERVER = ConfigDAO.getBoolean(PROPERTY_SYSTEM_WEBDAV_SERVER, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_SYSTEM_WEBDAV_SERVER, "off")));
			values.put(PROPERTY_SYSTEM_WEBDAV_SERVER, Boolean.toString(SYSTEM_WEBDAV_SERVER));
			SYSTEM_WEBDAV_FIX = ConfigDAO.getBoolean(PROPERTY_SYSTEM_WEBDAV_FIX, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_SYSTEM_WEBDAV_FIX, "off")));
			values.put(PROPERTY_SYSTEM_WEBDAV_FIX, Boolean.toString(SYSTEM_WEBDAV_FIX));

			SYSTEM_MAINTENANCE = ConfigDAO.getBoolean(PROPERTY_SYSTEM_MAINTENANCE, false);
			values.put(PROPERTY_SYSTEM_MAINTENANCE, Boolean.toString(SYSTEM_MAINTENANCE));
			SYSTEM_READONLY = ConfigDAO.getBoolean(PROPERTY_SYSTEM_READONLY, false);
			values.put(PROPERTY_SYSTEM_READONLY, Boolean.toString(SYSTEM_READONLY));

			SYSTEM_OPENOFFICE_PROGRAM = ConfigDAO.getString(PROPERTY_SYSTEM_OPENOFFICE_PROGRAM, cfg.getProperty(PROPERTY_SYSTEM_OPENOFFICE_PROGRAM, EnvironmentDetector.detectOpenOfficeProgram()));
			values.put(PROPERTY_SYSTEM_OPENOFFICE_PROGRAM, SYSTEM_OPENOFFICE_PROGRAM);
			SYSTEM_OPENOFFICE_DICTIONARY = ConfigDAO.getString(PROPERTY_SYSTEM_OPENOFFICE_DICTIONARY, "");
			values.put(PROPERTY_SYSTEM_OPENOFFICE_DICTIONARY, SYSTEM_OPENOFFICE_DICTIONARY);

			SYSTEM_OCR = ConfigDAO.getString(PROPERTY_SYSTEM_OCR, cfg.getProperty(PROPERTY_SYSTEM_OCR, ""));
			values.put(PROPERTY_SYSTEM_OCR, SYSTEM_OCR);
			SYSTEM_OCR_ROTATE = ConfigDAO.getString(PROPERTY_SYSTEM_OCR_ROTATE, cfg.getProperty(PROPERTY_SYSTEM_OCR_ROTATE, ""));
			values.put(PROPERTY_SYSTEM_OCR_ROTATE, SYSTEM_OCR_ROTATE);
			SYSTEM_PDF_FORCE_OCR = ConfigDAO.getBoolean(PROPERTY_SYSTEM_PDF_FORCE_OCR, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_SYSTEM_PDF_FORCE_OCR, "off")));
			values.put(PROPERTY_SYSTEM_PDF_FORCE_OCR, Boolean.toString(SYSTEM_PDF_FORCE_OCR));
			SYSTEM_IMAGEMAGICK_CONVERT = ConfigDAO.getString(PROPERTY_SYSTEM_IMAGEMAGICK_CONVERT, cfg.getProperty(PROPERTY_SYSTEM_IMAGEMAGICK_CONVERT, EnvironmentDetector.detectImagemagickConvert()));
			values.put(PROPERTY_SYSTEM_IMAGEMAGICK_CONVERT, SYSTEM_IMAGEMAGICK_CONVERT);
			SYSTEM_SWFTOOLS_PDF2SWF = ConfigDAO.getString(PROPERTY_SYSTEM_SWFTOOLS_PDF2SWF, cfg.getProperty(PROPERTY_SYSTEM_SWFTOOLS_PDF2SWF, EnvironmentDetector.detectSwftoolsPdf2Swf()));
			values.put(PROPERTY_SYSTEM_SWFTOOLS_PDF2SWF, SYSTEM_SWFTOOLS_PDF2SWF);
			SYSTEM_GHOSTSCRIPT = ConfigDAO.getString(PROPERTY_SYSTEM_GHOSTSCRIPT, cfg.getProperty(PROPERTY_SYSTEM_GHOSTSCRIPT, EnvironmentDetector.detectGhostscript()));
			values.put(PROPERTY_SYSTEM_GHOSTSCRIPT, SYSTEM_GHOSTSCRIPT);
			SYSTEM_DWG2DXF = ConfigDAO.getString(PROPERTY_SYSTEM_DWG2DXF, cfg.getProperty(PROPERTY_SYSTEM_DWG2DXF, ""));
			values.put(PROPERTY_SYSTEM_DWG2DXF, SYSTEM_DWG2DXF);
			SYSTEM_ANTIVIR = ConfigDAO.getString(PROPERTY_SYSTEM_ANTIVIR, cfg.getProperty(PROPERTY_SYSTEM_ANTIVIR, ""));
			values.put(PROPERTY_SYSTEM_ANTIVIR, SYSTEM_ANTIVIR);
			SYSTEM_PDFIMAGES = ConfigDAO.getString(PROPERTY_SYSTEM_PDFIMAGES, cfg.getProperty(PROPERTY_SYSTEM_PDFIMAGES, EnvironmentDetector.detectPdfImages()));
			values.put(PROPERTY_SYSTEM_PDFIMAGES, SYSTEM_PDFIMAGES);
			SYSTEM_CATDOC_XLS2CSV = ConfigDAO.getString(PROPERTY_SYSTEM_CATDOC_XLS2CSV, cfg.getProperty(PROPERTY_SYSTEM_CATDOC_XLS2CSV, ""));
			values.put(PROPERTY_SYSTEM_CATDOC_XLS2CSV, SYSTEM_CATDOC_XLS2CSV);
			SYSTEM_PREVIEWER = ConfigDAO.getSelectedOption(PROPERTY_SYSTEM_PREVIEWER, "flexpaper");
			values.put(PROPERTY_SYSTEM_PREVIEWER, SYSTEM_PREVIEWER);
			SYSTEM_LOGIN_LOWERCASE = ConfigDAO.getBoolean(PROPERTY_SYSTEM_LOGIN_LOWERCASE, SYSTEM_LOGIN_LOWERCASE);
			values.put(PROPERTY_SYSTEM_LOGIN_LOWERCASE, Boolean.toString(SYSTEM_LOGIN_LOWERCASE));
			SYSTEM_DOCUMENT_NAME_MISMATCH_CHECK = ConfigDAO.getBoolean(PROPERTY_SYSTEM_DOCUMENT_NAME_MISMATCH_CHECK, SYSTEM_DOCUMENT_NAME_MISMATCH_CHECK);
			values.put(PROPERTY_SYSTEM_DOCUMENT_NAME_MISMATCH_CHECK, Boolean.toString(SYSTEM_DOCUMENT_NAME_MISMATCH_CHECK));
			SYSTEM_KEYWORD_LOWERCASE = ConfigDAO.getBoolean(PROPERTY_SYSTEM_KEYWORD_LOWERCASE, SYSTEM_KEYWORD_LOWERCASE);
			values.put(PROPERTY_SYSTEM_KEYWORD_LOWERCASE, Boolean.toString(SYSTEM_KEYWORD_LOWERCASE));

			// Modify default admin user if login lowercase is active
			if (SYSTEM_LOGIN_LOWERCASE) {
				ADMIN_USER = ADMIN_USER.toLowerCase();
			}

			values.put(PROPERTY_ADMIN_USER, ADMIN_USER);
			values.put(PROPERTY_SYSTEM_USER, SYSTEM_USER);
			SYSTEM_EXECUTION_TIMEOUT = ConfigDAO.getInteger(PROPERTY_SYSTEM_EXECUTION_TIMEOUT, SYSTEM_EXECUTION_TIMEOUT);
			values.put(PROPERTY_SYSTEM_EXECUTION_TIMEOUT, Integer.toString(SYSTEM_EXECUTION_TIMEOUT));
			SYSTEM_PROFILING = ConfigDAO.getBoolean(PROPERTY_SYSTEM_PROFILING, false);
			values.put(PROPERTY_SYSTEM_PROFILING, Boolean.toString(SYSTEM_PROFILING));

			// Guess default application URL
			String defaultApplicationUrl = cfg.getProperty(PROPERTY_APPLICATION_URL);

			if (defaultApplicationUrl == null || defaultApplicationUrl.isEmpty()) {
				String hostName = InetAddress.getLocalHost().getCanonicalHostName();
				defaultApplicationUrl = "http://" + hostName + "/" + Config.CONTEXT + "/index.jsp";
			}

			APPLICATION_URL = ConfigDAO.getString(PROPERTY_APPLICATION_URL, defaultApplicationUrl);
			APPLICATION_BASE = getBase(APPLICATION_URL);
			values.put(PROPERTY_APPLICATION_URL, APPLICATION_URL);
			DEFAULT_LANG = ConfigDAO.getString(PROPERTY_DEFAULT_LANG, DEFAULT_LANG);
			values.put(PROPERTY_DEFAULT_LANG, DEFAULT_LANG);
			// UPDATE_INFO = ConfigDAO.getBoolean(PROPERTY_UPDATE_INFO, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_UPDATE_INFO, "on")));
			// values.put(PROPERTY_UPDATE_INFO, Boolean.toString(UPDATE_INFO));

			USER_ASSIGN_DOCUMENT_CREATION = ConfigDAO.getBoolean(PROPERTY_USER_ASSIGN_DOCUMENT_CREATION, USER_ASSIGN_DOCUMENT_CREATION);
			values.put(PROPERTY_USER_ASSIGN_DOCUMENT_CREATION, Boolean.toString(USER_ASSIGN_DOCUMENT_CREATION));
			USER_KEYWORDS_CACHE = ConfigDAO.getBoolean(PROPERTY_USER_KEYWORDS_CACHE, USER_KEYWORDS_CACHE);
			values.put(PROPERTY_USER_KEYWORDS_CACHE, Boolean.toString(USER_KEYWORDS_CACHE));
			USER_ITEM_CACHE = ConfigDAO.getBoolean(PROPERTY_USER_ITEM_CACHE, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_USER_ITEM_CACHE, "on")));
			values.put(PROPERTY_USER_ITEM_CACHE, Boolean.toString(USER_ITEM_CACHE));
			UPLOAD_THROTTLE_FILTER = ConfigDAO.getBoolean(PROPERTY_UPLOAD_THROTTLE_FILTER, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_UPLOAD_THROTTLE_FILTER, "off")));
			values.put(PROPERTY_UPLOAD_THROTTLE_FILTER, Boolean.toString(UPLOAD_THROTTLE_FILTER));
			REMOTE_CONVERSION_SERVER = ConfigDAO.getString(PROPERTY_REMOTE_CONVERSION_SERVER, cfg.getProperty(PROPERTY_REMOTE_CONVERSION_SERVER, ""));
			values.put(PROPERTY_REMOTE_CONVERSION_SERVER, REMOTE_CONVERSION_SERVER);

			// Schedule
			SCHEDULE_SESSION_KEEPALIVE = ConfigDAO.getInteger(PROPERTY_SCHEDULE_SESSION_KEEPALIVE, SCHEDULE_SESSION_KEEPALIVE);
			values.put(PROPERTY_SCHEDULE_SESSION_KEEPALIVE, Integer.toString(SCHEDULE_SESSION_KEEPALIVE));
			SCHEDULE_DASHBOARD_REFRESH = ConfigDAO.getInteger(PROPERTY_SCHEDULE_DASHBOARD_REFRESH, SCHEDULE_DASHBOARD_REFRESH);
			values.put(PROPERTY_SCHEDULE_DASHBOARD_REFRESH, Integer.toString(SCHEDULE_DASHBOARD_REFRESH));
			SCHEDULE_UI_NOTIFICATION = ConfigDAO.getInteger(PROPERTY_SCHEDULE_UI_NOTIFICATION, SCHEDULE_UI_NOTIFICATION);
			values.put(PROPERTY_SCHEDULE_UI_NOTIFICATION, Integer.toString(SCHEDULE_UI_NOTIFICATION));

			// KEA
			KEA_THESAURUS_SKOS_FILE = ConfigDAO.getString(PROPERTY_KEA_THESAURUS_SKOS_FILE, cfg.getProperty(PROPERTY_KEA_THESAURUS_SKOS_FILE, ""));
			values.put(PROPERTY_KEA_THESAURUS_SKOS_FILE, KEA_THESAURUS_SKOS_FILE);
			KEA_THESAURUS_OWL_FILE = ConfigDAO.getString(PROPERTY_KEA_THESAURUS_OWL_FILE, cfg.getProperty(PROPERTY_KEA_THESAURUS_OWL_FILE, ""));
			values.put(PROPERTY_KEA_THESAURUS_OWL_FILE, KEA_THESAURUS_OWL_FILE);
			KEA_THESAURUS_VOCABULARY_SERQL = ConfigDAO.getText(PROPERTY_KEA_THESAURUS_VOCABULARY_SERQL, cfg.getProperty(PROPERTY_KEA_THESAURUS_VOCABULARY_SERQL, ""));
			values.put(PROPERTY_KEA_THESAURUS_VOCABULARY_SERQL, KEA_THESAURUS_VOCABULARY_SERQL);
			KEA_THESAURUS_BASE_URL = ConfigDAO.getString(PROPERTY_KEA_THESAURUS_BASE_URL, cfg.getProperty(PROPERTY_KEA_THESAURUS_BASE_URL, ""));
			values.put(PROPERTY_KEA_THESAURUS_BASE_URL, KEA_THESAURUS_BASE_URL);
			KEA_THESAURUS_TREE_ROOT = ConfigDAO.getText(PROPERTY_KEA_THESAURUS_TREE_ROOT, cfg.getProperty(PROPERTY_KEA_THESAURUS_TREE_ROOT, ""));
			values.put(PROPERTY_KEA_THESAURUS_TREE_ROOT, KEA_THESAURUS_TREE_ROOT);
			KEA_THESAURUS_TREE_CHILDS = ConfigDAO.getText(PROPERTY_KEA_THESAURUS_TREE_CHILDS, cfg.getProperty(PROPERTY_KEA_THESAURUS_TREE_CHILDS, ""));
			values.put(PROPERTY_KEA_THESAURUS_TREE_CHILDS, KEA_THESAURUS_TREE_CHILDS);

			// Validator
			VALIDATOR_PASSWORD = ConfigDAO.getString(PROPERTY_VALIDATOR_PASSWORD, VALIDATOR_PASSWORD);
			values.put(PROPERTY_VALIDATOR_PASSWORD, VALIDATOR_PASSWORD);

			VALIDATOR_PASSWORD_MIN_LENGTH = ConfigDAO.getInteger(PROPERTY_VALIDATOR_PASSWORD_MIN_LENGTH, 0);
			values.put(PROPERTY_VALIDATOR_PASSWORD_MIN_LENGTH, Integer.toString(VALIDATOR_PASSWORD_MIN_LENGTH));
			VALIDATOR_PASSWORD_MAX_LENGTH = ConfigDAO.getInteger(PROPERTY_VALIDATOR_PASSWORD_MAX_LENGTH, 0);
			values.put(PROPERTY_VALIDATOR_PASSWORD_MAX_LENGTH, Integer.toString(VALIDATOR_PASSWORD_MAX_LENGTH));
			VALIDATOR_PASSWORD_MIN_LOWERCASE = ConfigDAO.getInteger(PROPERTY_VALIDATOR_PASSWORD_MIN_LOWERCASE, 0);
			values.put(PROPERTY_VALIDATOR_PASSWORD_MIN_LOWERCASE, Integer.toString(VALIDATOR_PASSWORD_MIN_LOWERCASE));
			VALIDATOR_PASSWORD_MIN_UPPERCASE = ConfigDAO.getInteger(PROPERTY_VALIDATOR_PASSWORD_MIN_UPPERCASE, 0);
			values.put(PROPERTY_VALIDATOR_PASSWORD_MIN_UPPERCASE, Integer.toString(VALIDATOR_PASSWORD_MIN_UPPERCASE));
			VALIDATOR_PASSWORD_MIN_DIGITS = ConfigDAO.getInteger(PROPERTY_VALIDATOR_PASSWORD_MIN_DIGITS, 0);
			values.put(PROPERTY_VALIDATOR_PASSWORD_MIN_DIGITS, Integer.toString(VALIDATOR_PASSWORD_MIN_DIGITS));
			VALIDATOR_PASSWORD_MIN_SPECIAL = ConfigDAO.getInteger(PROPERTY_VALIDATOR_PASSWORD_MIN_SPECIAL, 0);
			values.put(PROPERTY_VALIDATOR_PASSWORD_MIN_SPECIAL, Integer.toString(VALIDATOR_PASSWORD_MIN_SPECIAL));

			// Hibernate
			HIBERNATE_INDEXER_MASS_INDEXER = ConfigDAO.getBoolean(PROPERTY_HIBERNATE_INDEXER_MASS_INDEXER, false);
			values.put(PROPERTY_HIBERNATE_INDEXER_MASS_INDEXER, Boolean.toString(HIBERNATE_INDEXER_MASS_INDEXER));
			HIBERNATE_INDEXER_BATCH_SIZE_LOAD_OBJECTS = ConfigDAO.getInteger(PROPERTY_HIBERNATE_INDEXER_BATCH_SIZE_LOAD_OBJECTS, 30);
			values.put(PROPERTY_HIBERNATE_INDEXER_BATCH_SIZE_LOAD_OBJECTS, Integer.toString(HIBERNATE_INDEXER_BATCH_SIZE_LOAD_OBJECTS));
			HIBERNATE_INDEXER_THREADS_SUBSEQUENT_FETCHING = ConfigDAO.getInteger(PROPERTY_HIBERNATE_INDEXER_THREADS_SUBSEQUENT_FETCHING, 8);
			values.put(PROPERTY_HIBERNATE_INDEXER_THREADS_SUBSEQUENT_FETCHING, Integer.toString(HIBERNATE_INDEXER_THREADS_SUBSEQUENT_FETCHING));
			HIBERNATE_INDEXER_THREADS_LOAD_OBJECTS = ConfigDAO.getInteger(PROPERTY_HIBERNATE_INDEXER_THREADS_LOAD_OBJECTS, 4);
			values.put(PROPERTY_HIBERNATE_INDEXER_THREADS_LOAD_OBJECTS, Integer.toString(HIBERNATE_INDEXER_THREADS_LOAD_OBJECTS));
			HIBERNATE_INDEXER_THREADS_INDEX_WRITER = ConfigDAO.getInteger(PROPERTY_HIBERNATE_INDEXER_THREADS_INDEX_WRITER, 3);
			values.put(PROPERTY_HIBERNATE_INDEXER_THREADS_INDEX_WRITER, Integer.toString(HIBERNATE_INDEXER_THREADS_INDEX_WRITER));

			// Logo icons & login texts
			TEXT_BANNER = ConfigDAO.getString(PROPERTY_TEXT_BANNER, "&nbsp;");
			values.put(PROPERTY_TEXT_BANNER, TEXT_BANNER);
			TEXT_WELCOME = ConfigDAO.getString(PROPERTY_TEXT_WELCOME, "<p>Welcome to OpenKM !</p><p>Use a valid username and password to access to OpenKM user Desktop.</p>");
			values.put(PROPERTY_TEXT_WELCOME, TEXT_WELCOME);
			TEXT_TITLE = ConfigDAO.getString(PROPERTY_TEXT_TITLE, "OpenKM");
			values.put(PROPERTY_TEXT_TITLE, TEXT_TITLE);
			LOGO_TINY = ConfigDAO.getFile(PROPERTY_LOGO_TINY, "/img/logo_tiny.gif", sc);
			values.put(PROPERTY_LOGO_TINY, LOGO_TINY.getName());
			LOGO_LOGIN = ConfigDAO.getFile(PROPERTY_LOGO_LOGIN, "/img/logo_login.gif", sc);
			values.put(PROPERTY_LOGO_LOGIN, LOGO_LOGIN.getName());
			LOGO_MOBILE = ConfigDAO.getFile(PROPERTY_LOGO_MOBILE, "/img/logo_mobile.gif", sc);
			values.put(PROPERTY_LOGO_MOBILE, LOGO_MOBILE.getName());
			LOGO_REPORT = ConfigDAO.getFile(PROPERTY_LOGO_REPORT, "/img/logo_report.gif", sc);
			values.put(PROPERTY_LOGO_REPORT, LOGO_REPORT.getName());
			LOGO_FAVICON = ConfigDAO.getFile(PROPERTY_LOGO_FAVICON, "/img/logo_favicon.ico", sc);
			values.put(PROPERTY_LOGO_FAVICON, LOGO_FAVICON.getName());

			// Zoho
			ZOHO_USER = ConfigDAO.getString(PROPERTY_ZOHO_USER, cfg.getProperty(PROPERTY_ZOHO_USER, ""));
			values.put(PROPERTY_ZOHO_USER, ZOHO_USER);
			ZOHO_PASSWORD = ConfigDAO.getString(PROPERTY_ZOHO_PASSWORD, cfg.getProperty(PROPERTY_ZOHO_PASSWORD, ""));
			values.put(PROPERTY_ZOHO_PASSWORD, ZOHO_PASSWORD);
			ZOHO_API_KEY = ConfigDAO.getString(PROPERTY_ZOHO_API_KEY, cfg.getProperty(PROPERTY_ZOHO_API_KEY, ""));
			values.put(PROPERTY_ZOHO_API_KEY, ZOHO_API_KEY);
			ZOHO_SECRET_KEY = ConfigDAO.getString(PROPERTY_ZOHO_SECRET_KEY, cfg.getProperty(PROPERTY_ZOHO_SECRET_KEY, ""));
			values.put(PROPERTY_ZOHO_SECRET_KEY, ZOHO_SECRET_KEY);

			// OpenMeetings
			OPENMEETINGS_URL = ConfigDAO.getString(PROPERTY_OPENMEETINGS_URL, cfg.getProperty(PROPERTY_OPENMEETINGS_URL, ""));
			values.put(PROPERTY_OPENMEETINGS_URL, OPENMEETINGS_URL);
			OPENMEETINGS_PORT = ConfigDAO.getString(PROPERTY_OPENMEETINGS_PORT, cfg.getProperty(PROPERTY_OPENMEETINGS_PORT, ""));
			values.put(PROPERTY_OPENMEETINGS_PORT, OPENMEETINGS_PORT);
			OPENMEETINGS_USER = ConfigDAO.getString(PROPERTY_OPENMEETINGS_USER, cfg.getProperty(PROPERTY_OPENMEETINGS_USER, ""));
			values.put(PROPERTY_OPENMEETINGS_USER, OPENMEETINGS_USER);
			OPENMEETINGS_CREDENTIALS = ConfigDAO.getString(PROPERTY_OPENMEETINGS_CREDENTIALS, cfg.getProperty(PROPERTY_OPENMEETINGS_CREDENTIALS, ""));
			values.put(PROPERTY_OPENMEETINGS_CREDENTIALS, OPENMEETINGS_CREDENTIALS);

			// TinyMCE 4
			TINYMCE4_THEME = ConfigDAO.getString(PROPERTY_TINYMCE4_THEME, cfg.getProperty(PROPERTY_TINYMCE4_THEME, TINYMCE4_THEME));
			TINYMCE4_PLUGINS = ConfigDAO.getString(PROPERTY_TINYMCE4_PLUGINS, cfg.getProperty(PROPERTY_TINYMCE4_PLUGINS, TINYMCE4_PLUGINS));
			TINYMCE4_TOOLBAR1 = ConfigDAO.getString(PROPERTY_TINYMCE4_TOOLBAR1, cfg.getProperty(PROPERTY_TINYMCE4_TOOLBAR1, TINYMCE4_TOOLBAR1));
			TINYMCE4_TOOLBAR2 = ConfigDAO.getString(PROPERTY_TINYMCE4_TOOLBAR2, cfg.getProperty(PROPERTY_TINYMCE4_TOOLBAR2, TINYMCE4_TOOLBAR2));

			// HTML syntax highlighter
			PROPERTY_HTML_SINTAXHIGHLIGHTER_CORE = ConfigDAO.getString(PROPERTY_HTML_SINTAXHIGHLIGHTER_CORE, cfg.getProperty(PROPERTY_HTML_SINTAXHIGHLIGHTER_CORE, HTML_SINTAXHIGHLIGHTER_CORE));
			PROPERTY_HTML_SINTAXHIGHLIGHTER_THEME = ConfigDAO.getString(PROPERTY_HTML_SINTAXHIGHLIGHTER_THEME, cfg.getProperty(PROPERTY_HTML_SINTAXHIGHLIGHTER_THEME, HTML_SINTAXHIGHLIGHTER_THEME));
			;

			// CSV
			CSV_FORMAT_DELIMITER = ConfigDAO.getString(PROPERTY_CSV_FORMAT_DELIMITER, cfg.getProperty(PROPERTY_CSV_FORMAT_DELIMITER, CSV_FORMAT_DELIMITER));
			CSV_FORMAT_QUOTE_CHARACTER = ConfigDAO.getString(PROPERTY_CSV_FORMAT_QUOTE_CHARACTER, cfg.getProperty(PROPERTY_CSV_FORMAT_QUOTE_CHARACTER, CSV_FORMAT_QUOTE_CHARACTER));
			CSV_FORMAT_COMMENT_INDICATOR = ConfigDAO.getString(PROPERTY_CSV_FORMAT_COMMENT_INDICATOR, cfg.getProperty(PROPERTY_CSV_FORMAT_COMMENT_INDICATOR, CSV_FORMAT_COMMENT_INDICATOR));
			CSV_FORMAT_SKIP_HEADER = ConfigDAO.getBoolean(PROPERTY_CSV_FORMAT_SKIP_HEADER, CSV_FORMAT_SKIP_HEADER);
			CSV_FORMAT_IGNORE_EMPTY_LINES = ConfigDAO.getBoolean(PROPERTY_CSV_FORMAT_IGNORE_EMPTY_LINES, CSV_FORMAT_IGNORE_EMPTY_LINES);

			// Extra Tab Workspace
			EXTRA_TAB_WORKSPACE_LABEL = ConfigDAO.getString(PROPERTY_EXTRA_TAB_WORKSPACE_LABEL, cfg.getProperty(PROPERTY_EXTRA_TAB_WORKSPACE_LABEL, EXTRA_TAB_WORKSPACE_LABEL));
			EXTRA_TAB_WORKSPACE_URL = ConfigDAO.getString(PROPERTY_EXTRA_TAB_WORKSPACE_URL, cfg.getProperty(PROPERTY_EXTRA_TAB_WORKSPACE_URL, EXTRA_TAB_WORKSPACE_URL));

			// Unit Testing
			UNIT_TESTING_USER = ConfigDAO.getString(PROPERTY_UNIT_TESTING_USER, UNIT_TESTING_USER);
			UNIT_TESTING_PASSWORD = ConfigDAO.getString(PROPERTY_UNIT_TESTING_PASSWORD, UNIT_TESTING_PASSWORD);
			UNIT_TESTING_FOLDER = ConfigDAO.getString(PROPERTY_UNIT_TESTING_FOLDER, UNIT_TESTING_FOLDER);

			// Extended security
			SECURITY_EXTENDED_MASK = ConfigDAO.getInteger(PROPERTY_SECURITY_EXTENDED_MASK, Integer.valueOf(cfg.getProperty(PROPERTY_SECURITY_EXTENDED_MASK, String.valueOf(SECURITY_EXTENDED_MASK))));
			values.put(PROPERTY_SECURITY_EXTENDED_MASK, Integer.toString(SECURITY_EXTENDED_MASK));

			// RSS news
			RSS_NEWS = ConfigDAO.getBoolean(PROPERTY_RSS_NEWS, RSS_NEWS);
			RSS_NEWS_BOX_WIDTH = ConfigDAO.getInteger(PROPERTY_RSS_NEWS_BOX_WIDTH, RSS_NEWS_BOX_WIDTH);
			RSS_NEWS_MAX_SIZE = ConfigDAO.getInteger(PROPERTY_RSS_NEWS_MAX_SIZE, RSS_NEWS_MAX_SIZE);
			RSS_NEWS_VISIBLE = ConfigDAO.getInteger(PROPERTY_RSS_NEWS_VISIBLE, RSS_NEWS_VISIBLE);

			// Ask for drag and drop updates
			ASK_DRAG_AND_DROP_UPDATES = ConfigDAO.getBoolean(PROPERTY_ASK_DRAG_AND_DROP_UPDATES, ASK_DRAG_AND_DROP_UPDATES);
			values.put(PROPERTY_ASK_DRAG_AND_DROP_UPDATES, Boolean.toString(ASK_DRAG_AND_DROP_UPDATES));

			for (Entry<String, String> entry : values.entrySet()) {
				log.info("RELOAD - {}={}", entry.getKey(), entry.getValue());
			}
		} catch (DatabaseException | IOException e) {
			log.error("** Error reading configuration table **");
		} catch (Exception e) {
			log.error("** Unknown error: {} **", e.getMessage());
		}
	}
}
