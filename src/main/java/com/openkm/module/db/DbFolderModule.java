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

import com.openkm.automation.AutomationException;
import com.openkm.automation.AutomationManager;
import com.openkm.automation.AutomationUtils;
import com.openkm.bean.ContentInfo;
import com.openkm.bean.ExtendedAttributes;
import com.openkm.bean.Folder;
import com.openkm.bean.Repository;
import com.openkm.core.*;
import com.openkm.core.Config;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.NodeFolderDAO;
import com.openkm.dao.PendingTaskDAO;
import com.openkm.dao.bean.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.module.FolderModule;
import com.openkm.module.db.base.BaseFolderModule;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.FileUtils;
import com.openkm.util.PathUtils;
import com.openkm.util.SystemProfiling;
import com.openkm.util.UserActivity;
import com.openkm.util.impexp.RepositoryExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class DbFolderModule implements FolderModule {
	private static Logger log = LoggerFactory.getLogger(DbFolderModule.class);

	@Override
	public Folder create(String token, Folder fld) throws PathNotFoundException, ItemExistsException, AccessDeniedException,
			RepositoryException, DatabaseException, ExtensionException, AutomationException {
		log.debug("create({}, {})", token, fld);
		Folder newFolder = null;
		Authentication auth = null, oldAuth = null;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		if (!PathUtils.checkPath(fld.getPath())) {
			throw new RepositoryException("Invalid path: " + fld.getPath());
		}

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			String name = PathUtils.getName(fld.getPath());
			String parentPath = PathUtils.getParent(fld.getPath());
			String parentUuid = NodeBaseDAO.getInstance().getUuidFromPath(parentPath);
			NodeFolder parentFolder = NodeFolderDAO.getInstance().findByPk(parentUuid);

			// Escape dangerous chars in name
			name = PathUtils.escape(name);

			if (!name.isEmpty()) {
				fld.setPath(parentPath + "/" + name);

				// AUTOMATION - PRE
				Map<String, Object> env = new HashMap<String, Object>();
				env.put(AutomationUtils.PARENT_UUID, parentUuid);
				env.put(AutomationUtils.PARENT_PATH, parentPath);
				env.put(AutomationUtils.PARENT_NODE, parentFolder);
				AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_FOLDER_CREATE, AutomationRule.AT_PRE, env);
				parentFolder = (NodeFolder) env.get(AutomationUtils.PARENT_NODE);

				// Create node
				NodeFolder fldNode = BaseFolderModule.create(auth.getName(), parentFolder, name, fld.getCreated(), new HashSet<String>(),
						new HashSet<String>(), new HashSet<NodeProperty>(), new ArrayList<NodeNote>(), null);

				// AUTOMATION - POST
				env.put(AutomationUtils.FOLDER_NODE, fldNode);
				AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_FOLDER_CREATE, AutomationRule.AT_POST, env);

				// Set returned folder properties
				newFolder = BaseFolderModule.getProperties(auth.getName(), fldNode);

				// Activity log
				UserActivity.log(auth.getName(), "CREATE_FOLDER", fldNode.getUuid(), fld.getPath(), null);
			} else {
				throw new RepositoryException("Invalid folder name");
			}
		} catch (DatabaseException e) {
			throw e;
			// } catch (ExtensionException e) {
			// throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("create: {}", newFolder);
		return newFolder;
	}

	@Override
	public Folder getProperties(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getProperties({}, {})", token, fldId);
		Folder fld = null;
		Authentication auth = null, oldAuth = null;
		String fldPath = null;
		String fldUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(fldId)) {
				fldPath = fldId;
				fldUuid = NodeBaseDAO.getInstance().getUuidFromPath(fldId);
			} else {
				fldPath = NodeBaseDAO.getInstance().getPathFromUuid(fldId);
				fldUuid = fldId;
			}

			NodeFolder fldNode = NodeFolderDAO.getInstance().findByPk(fldUuid);
			fld = BaseFolderModule.getProperties(auth.getName(), fldNode);

			// Activity log
			UserActivity.log(auth.getName(), "GET_FOLDER_PROPERTIES", fldUuid, fldPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getProperties: {}", fld);
		return fld;
	}

	@Override
	public void delete(String token, String fldId) throws LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("delete({}, {})", token, fldId);
		Authentication auth = null, oldAuth = null;
		String fldPath = null;
		String fldUuid = null;

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

			if (PathUtils.isPath(fldId)) {
				fldPath = fldId;
				fldUuid = NodeBaseDAO.getInstance().getUuidFromPath(fldId);
			} else {
				fldPath = NodeBaseDAO.getInstance().getPathFromUuid(fldId);
				fldUuid = fldId;
			}

			String name = PathUtils.getName(fldPath);

			if (Repository.ROOT.equals(name) || Repository.CATEGORIES.equals(name) || Repository.THESAURUS.equals(name)
					|| Repository.TEMPLATES.equals(name) || Repository.PERSONAL.equals(name) || Repository.MAIL.equals(name)
					|| Repository.TRASH.equals(name)) {
				throw new AccessDeniedException("Can't delete a required node");
			}

			if (BaseFolderModule.hasLockedNodes(fldUuid)) {
				throw new LockException("Can't delete a folder with child locked nodes");
			}

			if (!BaseFolderModule.hasWriteAccess(fldUuid)) {
				throw new AccessDeniedException("Can't delete a folder with readonly nodes");
			}

			if (BaseFolderModule.hasWorkflowNodes(fldUuid)) {
				throw new LockException("Can't delete a folder with nodes used in a workflow");
			}

			if (fldPath.startsWith("/" + Repository.CATEGORIES) && BaseFolderModule.isCategoryInUse(fldUuid)) {
				throw new AccessDeniedException("Can't delete a category in use");
			}

			String userTrashPath = "/" + Repository.TRASH + "/" + auth.getName();
			String userTrashUuid = NodeBaseDAO.getInstance().getUuidFromPath(userTrashPath);

			NodeFolderDAO.getInstance().delete(name, fldUuid, userTrashUuid);

			// Add pending task
			if (Config.STORE_NODE_PATH) {
				PendingTask pt = new PendingTask();
				pt.setNode(fldUuid);
				pt.setTask(PendingTask.TASK_UPDATE_PATH);
				pt.setParams("DELETE_FOLDER");
				pt.setCreated(Calendar.getInstance());
				PendingTaskDAO.getInstance().create(pt);
			}

			// Activity log
			UserActivity.log(auth.getName(), "DELETE_FOLDER", fldUuid, fldPath, null);
		} catch (WorkflowException e) {
			throw new RepositoryException(e.getMessage());
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("delete: void");
	}

	@Override
	public void purge(String token, String fldId) throws LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("purge({}, {})", token, fldId);
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;
		String fldPath = null;
		String fldUuid = null;

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

			if (PathUtils.isPath(fldId)) {
				fldPath = fldId;
				fldUuid = NodeBaseDAO.getInstance().getUuidFromPath(fldId);
			} else {
				fldPath = NodeBaseDAO.getInstance().getPathFromUuid(fldId);
				fldUuid = fldId;
			}

			String name = PathUtils.getName(fldPath);

			if (Repository.ROOT.equals(name) || Repository.CATEGORIES.equals(name) || Repository.THESAURUS.equals(name)
					|| Repository.TEMPLATES.equals(name) || Repository.PERSONAL.equals(name) || Repository.MAIL.equals(name)
					|| Repository.TRASH.equals(name)) {
				throw new AccessDeniedException("Can't delete a required node");
			}

			if (BaseFolderModule.hasLockedNodes(fldUuid)) {
				throw new LockException("Can't purge a folder with child locked nodes");
			}

			if (!BaseFolderModule.hasWriteAccess(fldUuid)) {
				throw new AccessDeniedException("Can't purge a folder with readonly nodes");
			}

			if (fldPath.startsWith("/" + Repository.CATEGORIES) && NodeBaseDAO.getInstance().isCategoryInUse(fldUuid)) {
				throw new AccessDeniedException("Can't purge a category in use");
			}

			if (Config.REPOSITORY_PURGATORY_HOME != null && !Config.REPOSITORY_PURGATORY_HOME.isEmpty()) {
				File dateDir = FileUtils.createDateDir(Config.REPOSITORY_PURGATORY_HOME);
				File dstPath = new File(dateDir, PathUtils.getName(fldPath));
				dstPath.mkdir();
				RepositoryExporter.exportDocuments(null, fldPath, dstPath, true, false, null, null);
			}

			NodeFolderDAO.getInstance().purge(fldUuid, true);

			// Activity log - Already inside DAO
			// UserActivity.log(auth.getName(), "PURGE_FOLDER", fldUuid, fldPath, null);
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
	public Folder rename(String token, String fldId, String newName) throws PathNotFoundException, ItemExistsException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("rename({}, {}, {})", new Object[]{token, fldId, newName});
		Folder renamedFolder = null;
		Authentication auth = null, oldAuth = null;
		String fldPath = null;
		String fldUuid = null;

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

			if (PathUtils.isPath(fldId)) {
				fldPath = fldId;
				fldUuid = NodeBaseDAO.getInstance().getUuidFromPath(fldId);
			} else {
				fldPath = NodeBaseDAO.getInstance().getPathFromUuid(fldId);
				fldUuid = fldId;
			}

			String name = PathUtils.getName(fldPath);

			// Escape dangerous chars in name
			newName = PathUtils.escape(newName);

			if (newName != null && !newName.isEmpty() && !newName.equals(name)) {
				NodeFolder folderNode = NodeFolderDAO.getInstance().rename(fldUuid, newName);
				renamedFolder = BaseFolderModule.getProperties(auth.getName(), folderNode);

				// Add pending task
				if (Config.STORE_NODE_PATH) {
					PendingTask pt = new PendingTask();
					pt.setNode(fldUuid);
					pt.setTask(PendingTask.TASK_UPDATE_PATH);
					pt.setParams("RENAME_FOLDER");
					pt.setCreated(Calendar.getInstance());
					PendingTaskDAO.getInstance().create(pt);
				}
			} else {
				// Don't change anything
				NodeFolder folderNode = NodeFolderDAO.getInstance().findByPk(fldUuid);
				renamedFolder = BaseFolderModule.getProperties(auth.getName(), folderNode);
			}

			// Activity log
			UserActivity.log(auth.getName(), "RENAME_FOLDER", fldUuid, fldPath, newName);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("rename: {}", renamedFolder);
		return renamedFolder;
	}

	@Override
	public void move(String token, String fldId, String dstId) throws PathNotFoundException, ItemExistsException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("move({}, {}, {})", new Object[]{token, fldId, dstId});
		Authentication auth = null, oldAuth = null;
		String fldPath = null;
		String fldUuid = null;
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

			if (PathUtils.isPath(fldId)) {
				fldPath = fldId;
				fldUuid = NodeBaseDAO.getInstance().getUuidFromPath(fldId);
			} else {
				fldPath = NodeBaseDAO.getInstance().getPathFromUuid(fldId);
				fldUuid = fldId;
			}

			if (PathUtils.isPath(dstId)) {
				if (!PathUtils.checkPath(dstId)) {
					throw new RepositoryException("Invalid destination path: " + dstId);
				}

				dstPath = dstId;
				dstUuid = NodeBaseDAO.getInstance().getUuidFromPath(dstId);
			} else {
				dstPath = NodeBaseDAO.getInstance().getPathFromUuid(dstId);
				dstUuid = dstId;

				if (!PathUtils.checkPath(dstPath)) {
					throw new RepositoryException("Invalid destination path: " + dstPath);
				}
			}

			// Check for recursive move
			if (dstPath.startsWith(fldPath)) {
				throw new RepositoryException("Recursive move detected");
			}

			NodeFolderDAO.getInstance().move(fldUuid, dstUuid);

			// Add pending task
			if (Config.STORE_NODE_PATH) {
				PendingTask pt = new PendingTask();
				pt.setNode(fldUuid);
				pt.setTask(PendingTask.TASK_UPDATE_PATH);
				pt.setParams("MOVE_FOLDER");
				pt.setCreated(Calendar.getInstance());
				PendingTaskDAO.getInstance().create(pt);
			}

			// Activity log
			UserActivity.log(auth.getName(), "MOVE_FOLDER", fldUuid, fldPath, dstPath);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("move: void");
	}

	@Override
	public void copy(String token, String fldId, String dstId) throws PathNotFoundException, ItemExistsException, AccessDeniedException,
			RepositoryException, IOException, AutomationException, DatabaseException, UserQuotaExceededException {
		log.debug("copy({}, {}, {})", new Object[]{token, fldId, dstId});
		extendedCopy(token, fldId, dstId, new ExtendedAttributes());
	}

	@Override
	public void extendedCopy(String token, String fldId, String dstId, ExtendedAttributes extAttr) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, IOException, AutomationException, DatabaseException,
			UserQuotaExceededException {
		log.debug("extendedCopy({}, {}, {})", new Object[]{token, fldId, dstId});
		Authentication auth = null, oldAuth = null;
		String fldPath = null;
		String fldUuid = null;
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

			if (PathUtils.isPath(fldId)) {
				fldPath = fldId;
				fldUuid = NodeBaseDAO.getInstance().getUuidFromPath(fldId);
			} else {
				fldPath = NodeBaseDAO.getInstance().getPathFromUuid(fldId);
				fldUuid = fldId;
			}

			if (PathUtils.isPath(dstId)) {
				dstPath = dstId;
				dstUuid = NodeBaseDAO.getInstance().getUuidFromPath(dstId);
			} else {
				dstPath = NodeBaseDAO.getInstance().getPathFromUuid(dstId);
				dstUuid = dstId;
			}

			// Check for recursive copy
			if (dstPath.startsWith(fldPath)) {
				throw new RepositoryException("Recursive copy detected");
			}

			NodeFolder srcFolderNode = NodeFolderDAO.getInstance().findByPk(fldUuid);
			NodeFolder dstFolderNode = NodeFolderDAO.getInstance().findByPk(dstUuid);
			NodeFolder newFldNode = BaseFolderModule.copy(auth.getName(), srcFolderNode, dstFolderNode, extAttr);

			// Activity log
			UserActivity.log(auth.getName(), "COPY_FOLDER", newFldNode.getUuid(), fldPath, dstPath);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	@Override
	@Deprecated
	public List<Folder> getChilds(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		return getChildren(token, fldId);
	}

	@Override
	public List<Folder> getChildren(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getChildren({}, {})", token, fldId);
		long begin = System.currentTimeMillis();
		List<Folder> children = new ArrayList<Folder>();
		Authentication auth = null, oldAuth = null;
		String fldPath = null;
		String fldUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(fldId)) {
				fldPath = fldId;
				fldUuid = NodeBaseDAO.getInstance().getUuidFromPath(fldId);
			} else {
				fldPath = NodeBaseDAO.getInstance().getPathFromUuid(fldId);
				fldUuid = fldId;
			}

			for (NodeFolder nFolder : NodeFolderDAO.getInstance().findByParent(fldUuid)) {
				children.add(BaseFolderModule.getProperties(auth.getName(), nFolder));
			}

			// Activity log
			UserActivity.log(auth.getName(), "GET_CHILDREN_FOLDERS", fldUuid, fldPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(fldPath, System.currentTimeMillis() - begin);
		log.trace("getChildren.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getChildren: {}", children);
		return children;
	}

	@Override
	public ContentInfo getContentInfo(String token, String fldId) throws AccessDeniedException, RepositoryException, PathNotFoundException,
			DatabaseException {
		log.debug("getContentInfo({}, {})", token, fldId);
		ContentInfo contentInfo = new ContentInfo();
		Authentication auth = null, oldAuth = null;
		String fldPath = null;
		String fldUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(fldId)) {
				fldPath = fldId;
				fldUuid = NodeBaseDAO.getInstance().getUuidFromPath(fldId);
			} else {
				fldPath = NodeBaseDAO.getInstance().getPathFromUuid(fldId);
				fldUuid = fldId;
			}

			if (fldPath.equals("/" + Repository.ROOT) || fldPath.equals("/" + Repository.PERSONAL) || fldPath.equals("/" + Repository.TEMPLATES)
					|| fldPath.equals("/" + Repository.TRASH) || fldPath.equals("/" + Repository.MAIL)) {
				long docCount = NodeBaseDAO.getInstance().getCount(NodeDocument.class.getSimpleName(), fldPath);
				long fldCount = NodeBaseDAO.getInstance().getCount(NodeFolder.class.getSimpleName(), fldPath);
				long mailCount = NodeBaseDAO.getInstance().getCount(NodeMail.class.getSimpleName(), fldPath);

				contentInfo.setDocuments(docCount);
				contentInfo.setFolders(fldCount);
				contentInfo.setMails(mailCount);
			} else {
				contentInfo = BaseFolderModule.getContentInfo(fldUuid);
			}

			// Activity log
			UserActivity.log(auth.getName(), "GET_FOLDER_CONTENT_INFO", fldUuid, fldPath, contentInfo.toString());
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getContentInfo: {}", contentInfo);
		return contentInfo;
	}

	@Override
	public boolean isValid(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("isValid({}, {})", token, fldId);
		boolean valid = true;
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;
		String fldUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(fldId)) {
				fldUuid = NodeBaseDAO.getInstance().getUuidFromPath(fldId);
			} else {
				fldUuid = fldId;
			}

			try {
				NodeFolderDAO.getInstance().findByPk(fldUuid);
			} catch (PathNotFoundException e) {
				valid = false;
			}
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("isValid: {}", valid);
		return valid;
	}

	@Override
	public String getPath(String token, String uuid) throws AccessDeniedException, RepositoryException, DatabaseException {
		try {
			return NodeBaseDAO.getInstance().getPathFromUuid(uuid);
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}
}
