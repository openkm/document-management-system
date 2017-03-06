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

package com.openkm.module.db;

import com.openkm.bean.*;
import com.openkm.bean.form.FormElement;
import com.openkm.core.*;
import com.openkm.dao.ConfigDAO;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.NodeFolderDAO;
import com.openkm.dao.RegisteredPropertyGroupDAO;
import com.openkm.dao.bean.NodeFolder;
import com.openkm.dao.bean.RegisteredPropertyGroup;
import com.openkm.module.RepositoryModule;
import com.openkm.module.db.base.BaseFolderModule;
import com.openkm.module.db.stuff.DbSessionManager;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.*;
import com.openkm.util.impexp.RepositoryExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class DbRepositoryModule implements RepositoryModule {
	private static Logger log = LoggerFactory.getLogger(DbRepositoryModule.class);

	/**
	 * Initialize the repository.
	 *
	 * @return The root path of the initialized repository.
	 * @throws DatabaseException If there is any general repository problem.
	 */
	public synchronized static String initialize() throws RepositoryException, DatabaseException {
		log.debug("initialize()");

		// Initializes Repository
		String okmRootPath = create();

		try {
			// Store system session token
			DbSessionManager.getInstance().putSystemSession();
			DbAuthModule.loadUserData(Config.SYSTEM_USER);
		} catch (ItemExistsException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (AccessDeniedException e) {
			throw new RepositoryException(e.getMessage(), e);
		}

		log.debug("initialize: {}", okmRootPath);
		return okmRootPath;
	}

	/**
	 * Create OpenKM repository structure
	 */
	public synchronized static String create() throws RepositoryException, DatabaseException {
		String okmRootUuid = null;
		NodeFolder okmRootNode = null;
		String okmRootPath = null;

		try {
			okmRootUuid = NodeBaseDAO.getInstance().getUuidFromPath("/" + Repository.ROOT);
			okmRootNode = NodeFolderDAO.getInstance().findByPk(okmRootUuid);
		} catch (PathNotFoundException e) {
			log.info("No {} node found", Repository.ROOT);
		}

		try {
			if (okmRootNode == null) {
				log.info("Repository creation");

				// Create okm:root
				log.info("Create {}", Repository.ROOT);
				NodeFolder okmRoot = createBase(Repository.ROOT);
				okmRootUuid = okmRoot.getUuid();

				// Create okm:thesaurus
				log.info("Create {}", Repository.THESAURUS);
				createBase(Repository.THESAURUS);

				// Create okm:categories
				log.info("Create {}", Repository.CATEGORIES);
				createBase(Repository.CATEGORIES);

				// Create okm:templates
				log.info("Create {}", Repository.TEMPLATES);
				createBase(Repository.TEMPLATES);

				// Create okm:personal
				log.info("Create {}", Repository.PERSONAL);
				createBase(Repository.PERSONAL);

				// Create okm:mail
				log.info("Create {}", Repository.MAIL);
				createBase(Repository.MAIL);

				// Create okm:trash
				log.info("Create {}", Repository.TRASH);
				createBase(Repository.TRASH);

				// Create okm:config
				log.info("Create config");
				com.openkm.dao.bean.Config cfg = new com.openkm.dao.bean.Config();

				// Generate installation UUID
				String uuid = UUID.randomUUID().toString();
				cfg.setType(com.openkm.dao.bean.Config.HIDDEN);
				cfg.setKey(Config.PROPERTY_REPOSITORY_UUID);
				cfg.setValue(uuid);
				Repository.setUuid(uuid);
				ConfigDAO.create(cfg);

				// Set repository version
				cfg.setType(com.openkm.dao.bean.Config.HIDDEN);
				cfg.setKey(Config.PROPERTY_REPOSITORY_VERSION);
				cfg.setValue(WarUtils.getAppVersion().getMajor());
				ConfigDAO.create(cfg);
			} else {
				log.info("Repository already created");

				// Get installation UUID
				com.openkm.dao.bean.Config cfg = ConfigDAO.findByPk(Config.PROPERTY_REPOSITORY_UUID);
				String uuid = cfg.getValue();
				Repository.setUuid(uuid);

				// Test repository version
				cfg = ConfigDAO.findByPk(Config.PROPERTY_REPOSITORY_VERSION);
				String repoVer = cfg.getValue();

				if (!WarUtils.getAppVersion().getMajor().equals(repoVer)) {
					log.warn("### Actual repository version (" + repoVer + ") differs from application repository version ("
							+ WarUtils.getAppVersion().getMajor() + ") ###");
					log.warn("### You should upgrade the repository ###");
				}
			}

			okmRootPath = NodeBaseDAO.getInstance().getPathFromUuid(okmRootUuid);
		} catch (PathNotFoundException e) {
			// Should not happen
			throw new RepositoryException("PathNotFound: " + e.getMessage());
		} catch (AccessDeniedException e) {
			// Should not happen
			throw new RepositoryException("AccessDenied: " + e.getMessage());
		} catch (ItemExistsException e) {
			// Should not happen
			throw new RepositoryException("ItemExists: " + e.getMessage());
		} catch (DatabaseException e) {
			throw e;
		}

		return okmRootPath;
	}

	/**
	 * Create base node
	 */
	private static NodeFolder createBase(String name) throws PathNotFoundException, AccessDeniedException, ItemExistsException,
			DatabaseException {
		NodeFolder base = new NodeFolder();

		// Add basic properties
		base.setParent(Config.ROOT_NODE_UUID);
		base.setContext(PathUtils.fixContext("/" + name));
		base.setAuthor(Config.ADMIN_USER);
		base.setName(name);
		base.setUuid(UUID.randomUUID().toString());
		base.setCreated(Calendar.getInstance());

		if (Config.STORE_NODE_PATH) {
			base.setPath("/" + name);
		}

		// Auth info
		int perms = Permission.READ | Permission.WRITE | Permission.DELETE | Permission.SECURITY;
		base.getUserPermissions().put(Config.ADMIN_USER, perms);
		base.getRolePermissions().put(Config.DEFAULT_USER_ROLE, perms);

		NodeFolderDAO.getInstance().createBase(base);
		return base;
	}

	@Override
	public Folder getRootFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getRootFolder({})", token);
		Folder rootFolder = new Folder();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			String rootPath = "/" + Repository.ROOT;
			String rootUuid = NodeBaseDAO.getInstance().getUuidFromPath(rootPath);
			NodeFolder rootNode = NodeFolderDAO.getInstance().findByPk(rootUuid);
			rootFolder = BaseFolderModule.getProperties(auth.getName(), rootNode);

			// Activity log
			UserActivity.log(auth.getName(), "GET_ROOT_FOLDER", rootNode.getUuid(), rootPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getRootFolder: {}", rootFolder);
		return rootFolder;
	}

	@Override
	public Folder getTrashFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getTrashFolder({})", token);
		Folder trashFolder = new Folder();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			String trashPath = "/" + Repository.TRASH + "/" + auth.getName();
			String trashUuid = NodeBaseDAO.getInstance().getUuidFromPath(trashPath);
			NodeFolder trashNode = NodeFolderDAO.getInstance().findByPk(trashUuid);
			trashFolder = BaseFolderModule.getProperties(auth.getName(), trashNode);

			// Activity log
			UserActivity.log(auth.getName(), "GET_TRASH_FOLDER", trashNode.getUuid(), trashPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getTrashFolder: {}", trashFolder);
		return trashFolder;
	}

	@Override
	public Folder getTrashFolderBase(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getTrashFolderBase({})", token);
		Folder trashFolder = new Folder();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			String trashPath = "/" + Repository.TRASH;
			String trashUuid = NodeBaseDAO.getInstance().getUuidFromPath(trashPath);
			NodeFolder trashNode = NodeFolderDAO.getInstance().findByPk(trashUuid);
			trashFolder = BaseFolderModule.getProperties(auth.getName(), trashNode);

			// Activity log
			UserActivity.log(auth.getName(), "GET_TRASH_FOLDER_BASE", trashNode.getUuid(), trashPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getTrashFolderBase: {}", trashFolder);
		return trashFolder;
	}

	@Override
	public Folder getTemplatesFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getTemplatesFolder({})", token);
		Folder templatesFolder = new Folder();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			String templatesPath = "/" + Repository.TEMPLATES;
			String templatesUuid = NodeBaseDAO.getInstance().getUuidFromPath(templatesPath);
			NodeFolder templatesNode = NodeFolderDAO.getInstance().findByPk(templatesUuid);
			templatesFolder = BaseFolderModule.getProperties(auth.getName(), templatesNode);

			// Activity log
			UserActivity.log(auth.getName(), "GET_TEMPLATES_FOLDER", templatesNode.getUuid(), templatesPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getTemplatesFolder: {}", templatesFolder);
		return templatesFolder;
	}

	@Override
	public Folder getPersonalFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getPersonalFolder({})", token);
		Folder personalFolder = new Folder();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			String personalPath = "/" + Repository.PERSONAL + "/" + auth.getName();
			String personalUuid = NodeBaseDAO.getInstance().getUuidFromPath(personalPath);
			NodeFolder personalNode = NodeFolderDAO.getInstance().findByPk(personalUuid);
			personalFolder = BaseFolderModule.getProperties(auth.getName(), personalNode);

			// Activity log
			UserActivity.log(auth.getName(), "GET_PERSONAL_FOLDER", personalNode.getUuid(), personalPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getPersonalFolder: {}", personalFolder);
		return personalFolder;
	}

	@Override
	public Folder getPersonalFolderBase(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getPersonalFolderBase({})", token);
		Folder personalFolder = new Folder();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			String personalPath = "/" + Repository.PERSONAL;
			String personalUuid = NodeBaseDAO.getInstance().getUuidFromPath(personalPath);
			NodeFolder personalNode = NodeFolderDAO.getInstance().findByPk(personalUuid);
			personalFolder = BaseFolderModule.getProperties(auth.getName(), personalNode);

			// Activity log
			UserActivity.log(auth.getName(), "GET_PERSONAL_FOLDER_BASE", personalNode.getUuid(), personalPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getPersonalFolderBase: {}", personalFolder);
		return personalFolder;
	}

	@Override
	public Folder getMailFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getMailFolder({})", token);
		Folder mailFolder = new Folder();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			String mailPath = MailUtils.getUserMailPath(auth.getName());
			String mailUuid = NodeBaseDAO.getInstance().getUuidFromPath(mailPath);
			NodeFolder mailNode = NodeFolderDAO.getInstance().findByPk(mailUuid);
			mailFolder = BaseFolderModule.getProperties(auth.getName(), mailNode);

			// Activity log
			UserActivity.log(auth.getName(), "GET_MAIL_FOLDER", mailNode.getUuid(), mailPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getMailFolder: {}", mailFolder);
		return mailFolder;
	}

	@Override
	public Folder getMailFolderBase(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getMailFolderBase({})", token);
		Folder mailFolder = new Folder();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			String mailPath = "/" + Repository.MAIL;
			String mailUuid = NodeBaseDAO.getInstance().getUuidFromPath(mailPath);
			NodeFolder mailNode = NodeFolderDAO.getInstance().findByPk(mailUuid);
			mailFolder = BaseFolderModule.getProperties(auth.getName(), mailNode);

			// Activity log
			UserActivity.log(auth.getName(), "GET_MAIL_FOLDER_BASE", mailNode.getUuid(), mailPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getMailFolderBase: {}", mailFolder);
		return mailFolder;
	}

	@Override
	public Folder getThesaurusFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getThesaurusFolder({})", token);
		Folder thesaurusFolder = new Folder();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			String thesaurusPath = "/" + Repository.THESAURUS;
			String thesaurusUuid = NodeBaseDAO.getInstance().getUuidFromPath(thesaurusPath);
			NodeFolder thesaurusNode = NodeFolderDAO.getInstance().findByPk(thesaurusUuid);
			thesaurusFolder = BaseFolderModule.getProperties(auth.getName(), thesaurusNode);

			// Activity log
			UserActivity.log(auth.getName(), "GET_THESAURUS_FOLDER", thesaurusNode.getUuid(), thesaurusPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getThesaurusFolder: {}", thesaurusFolder);
		return thesaurusFolder;
	}

	@Override
	public Folder getCategoriesFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getCategoriesFolder({})", token);
		Folder categoriesFolder = new Folder();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			String categoriesUuid = NodeBaseDAO.getInstance().getUuidFromPath("/" + Repository.CATEGORIES);
			NodeFolder categoriesNode = NodeFolderDAO.getInstance().findByPk(categoriesUuid);
			categoriesFolder = BaseFolderModule.getProperties(auth.getName(), categoriesNode);

			// Activity log
			UserActivity.log(auth.getName(), "GET_CATEGORIES_FOLDER", categoriesNode.getUuid(), categoriesFolder.getPath(), null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getCategoriesFolder: {}", categoriesFolder);
		return categoriesFolder;
	}

	@Override
	public void purgeTrash(String token) throws PathNotFoundException, AccessDeniedException, LockException, RepositoryException,
			DatabaseException {
		log.debug("purgeTrash({})", token);
		Authentication auth = null, oldAuth = null;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			String userTrashPath = "/" + Repository.TRASH + "/" + auth.getName();
			String userTrashUuid = NodeBaseDAO.getInstance().getUuidFromPath(userTrashPath);

			if (BaseFolderModule.hasLockedNodes(userTrashUuid)) {
				throw new LockException("Can't delete a folder with child locked nodes");
			}

			if (!BaseFolderModule.hasWriteAccess(userTrashUuid)) {
				throw new AccessDeniedException("Can't delete a folder with readonly nodes");
			}

			if (Config.REPOSITORY_PURGATORY_HOME != null && !Config.REPOSITORY_PURGATORY_HOME.isEmpty()) {
				File dateDir = FileUtils.createDateDir(Config.REPOSITORY_PURGATORY_HOME);
				File dstPath = new File(dateDir, PathUtils.getName(userTrashPath));
				dstPath.mkdir();
				RepositoryExporter.exportDocuments(null, userTrashPath, dstPath, true, false, null, null);
			}

			NodeFolderDAO.getInstance().purge(userTrashUuid, false);

			// Activity log
			UserActivity.log(auth.getName(), "PURGE_TRASH", userTrashUuid, userTrashPath, null);
		} catch (IOException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (ParseException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (NoSuchGroupException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (MessagingException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("purge: void");
	}

	@Override
	public String getUpdateMessage(String token) throws RepositoryException {
		return Repository.getUpdateMsg();
	}

	@Override
	public String getRepositoryUuid(String token) throws RepositoryException {
		return Repository.getUuid();
	}

	@Override
	public boolean hasNode(String token, String nodeId) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("hasNode({}, {})", token, nodeId);
		boolean ret = false;
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(nodeId)) {
				ret = NodeBaseDAO.getInstance().itemPathExists(nodeId);
			} else {
				ret = NodeBaseDAO.getInstance().itemUuidExists(nodeId);
			}
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("hasNode: {}", ret);
		return ret;
	}

	@Override
	public String getNodePath(String token, String uuid) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getNodePath({}, {})", token, uuid);
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;
		String ret = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			ret = NodeBaseDAO.getInstance().getPathFromUuid(uuid);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getNodePath: {}", ret);
		return ret;
	}

	@Override
	public String getNodeUuid(String token, String path) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getNodeUuid({}, {})", token, path);
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;
		String ret = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			ret = NodeBaseDAO.getInstance().getUuidFromPath(path);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getNodeUuid: {}", ret);
		return ret;
	}

	@Override
	public AppVersion getAppVersion(String token) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getAppVersion({})", token);
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;
		AppVersion ret = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			ret = WarUtils.getAppVersion();
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getAppVersion: {}", ret);
		return ret;
	}

	@Override
	public void copyAttributes(String token, String srcId, String dstId, ExtendedAttributes extAttr) throws AccessDeniedException,
			PathNotFoundException, DatabaseException {
		log.debug("copyAttributes({}, {}, {}, {})", new Object[]{token, srcId, dstId, extAttr});
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;
		String srcPath = null;
		String srcUuid = null;
		String dstPath = null;
		String dstUuid = null;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(srcId)) {
				srcPath = srcId;
				srcUuid = NodeBaseDAO.getInstance().getUuidFromPath(srcId);
			} else {
				srcPath = NodeBaseDAO.getInstance().getPathFromUuid(srcId);
				srcUuid = srcId;
			}

			if (PathUtils.isPath(dstId)) {
				dstPath = dstId;
				dstUuid = NodeBaseDAO.getInstance().getUuidFromPath(dstId);
			} else {
				dstPath = NodeBaseDAO.getInstance().getPathFromUuid(dstId);
				dstUuid = dstId;
			}

			NodeBaseDAO.getInstance().copyAttributes(srcUuid, dstUuid, extAttr);

			// Check subscriptions
			// BaseNotificationModule.checkSubscriptions(dstNode, PrincipalUtils.getUser(), "COPY_ATTRIBUTES", null);

			// Activity log
			UserActivity.log(PrincipalUtils.getUser(), "COPY_ATTRIBUTES", dstUuid, srcPath, dstPath);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	/**
	 * Register custom node definition from file.
	 *
	 * @param pgDefFile Path to file where is the Property Groups definition.
	 */
	public synchronized static void registerPropertyGroups(String pgDefFile) throws IOException, ParseException, DatabaseException {
		// Check xml property groups definition
		FormUtils.resetPropertyGroupsForms();
		Map<PropertyGroup, List<FormElement>> pgForms = FormUtils.parsePropertyGroupsForms(pgDefFile);

		for (Entry<PropertyGroup, List<FormElement>> pgForm : pgForms.entrySet()) {
			PropertyGroup pg = pgForm.getKey();
			RegisteredPropertyGroup rpg = new RegisteredPropertyGroup();
			rpg.setName(pg.getName());

			for (FormElement fe : pgForm.getValue()) {
				String name = fe.getName();
				String type = fe.getClass().getName();
				rpg.getProperties().put(name, type);
			}

			RegisteredPropertyGroupDAO.getInstance().createOrUpdate(rpg);
		}
	}
}
