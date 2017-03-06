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
import com.openkm.bean.ExtendedAttributes;
import com.openkm.bean.FileUploadResponse;
import com.openkm.bean.Mail;
import com.openkm.bean.Repository;
import com.openkm.core.*;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.NodeFolderDAO;
import com.openkm.dao.NodeMailDAO;
import com.openkm.dao.bean.NodeFolder;
import com.openkm.dao.bean.NodeMail;
import com.openkm.module.MailModule;
import com.openkm.module.db.base.BaseMailModule;
import com.openkm.module.db.base.BaseNotificationModule;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DbMailModule implements MailModule {
	private static Logger log = LoggerFactory.getLogger(DbMailModule.class);

	@Override
	public Mail create(String token, Mail mail) throws PathNotFoundException, ItemExistsException, VirusDetectedException,
			AccessDeniedException, RepositoryException, DatabaseException, UserQuotaExceededException, AutomationException {
		log.debug("create({}, {})", token, mail);
		return create(token, mail, null, new Ref<FileUploadResponse>(null));
	}

	/**
	 * Used when importing mail from scheduler
	 */
	public Mail create(String token, Mail mail, String userId, Ref<FileUploadResponse> fuResponse) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, ItemExistsException, VirusDetectedException, DatabaseException,
			UserQuotaExceededException, AutomationException {
		log.debug("create({}, {}, {})", new Object[]{token, mail, userId});
		Mail newMail = null;
		Authentication auth = null, oldAuth = null;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		if (!PathUtils.checkPath(mail.getPath())) {
			throw new RepositoryException("Invalid path: " + mail.getPath());
		}

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (userId == null) {
				userId = auth.getName();
			}

			String parentPath = PathUtils.getParent(mail.getPath());
			String name = PathUtils.getName(mail.getPath());

			// Escape dangerous chars in name
			name = PathUtils.escape(name);

			if (!name.isEmpty()) {
				mail.setPath(parentPath + "/" + name);

				String parentUuid = NodeBaseDAO.getInstance().getUuidFromPath(parentPath);
				NodeFolder parentFolder = NodeFolderDAO.getInstance().findByPk(parentUuid);

				// AUTOMATION - PRE
				// INSIDE BaseMailModule.create

				// Create node
				NodeMail mailNode = BaseMailModule.create(userId, parentPath, parentFolder, name, mail.getSize(), mail.getFrom(),
						mail.getReply(), mail.getTo(), mail.getCc(), mail.getBcc(), mail.getSentDate(), mail.getReceivedDate(),
						mail.getSubject(), mail.getContent(), mail.getMimeType(), new HashSet<String>(), new HashSet<String>(), fuResponse);

				// AUTOMATION - POST
				// INSIDE BaseMailModule.create

				// Set returned mail properties
				newMail = BaseMailModule.getProperties(auth.getName(), mailNode);

				// Check subscriptions
				BaseNotificationModule.checkSubscriptions(mailNode, userId, "CREATE_MAIL", null);

				// Activity log
				UserActivity.log(userId, "CREATE_MAIL", mailNode.getUuid(), mail.getPath(), null);
			} else {
				throw new RepositoryException("Invalid mail name");
			}
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("create: {}", newMail);
		return newMail;
	}

	@Override
	public Mail getProperties(String token, String mailId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getProperties({}, {})", token, mailId);
		Mail mail = null;
		Authentication auth = null, oldAuth = null;
		String mailPath = null;
		String mailUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(mailId)) {
				mailPath = mailId;
				mailUuid = NodeBaseDAO.getInstance().getUuidFromPath(mailId);
			} else {
				mailPath = NodeBaseDAO.getInstance().getPathFromUuid(mailId);
				mailUuid = mailId;
			}

			NodeMail mailNode = NodeMailDAO.getInstance().findByPk(mailUuid);
			mail = BaseMailModule.getProperties(auth.getName(), mailNode);

			// Activity log
			UserActivity.log(auth.getName(), "GET_MAIL_PROPERTIES", mailUuid, mailPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getProperties: {}", mail);
		return mail;
	}

	@Override
	public void delete(String token, String mailId) throws LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("delete({}, {})", new Object[]{token, mailId});
		Authentication auth = null, oldAuth = null;
		String mailPath = null;
		String mailUuid = null;

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

			if (PathUtils.isPath(mailId)) {
				mailPath = mailId;
				mailUuid = NodeBaseDAO.getInstance().getUuidFromPath(mailId);
			} else {
				mailPath = NodeBaseDAO.getInstance().getPathFromUuid(mailId);
				mailUuid = mailId;
			}

			if (BaseMailModule.hasLockedNodes(mailUuid)) {
				throw new LockException("Can't delete a mail with child locked attachments");
			}

			if (!BaseMailModule.hasWriteAccess(mailUuid)) {
				throw new AccessDeniedException("Can't delete a mail with readonly attachments");
			}

			String userTrashPath = "/" + Repository.TRASH + "/" + auth.getName();
			String userTrashUuid = NodeBaseDAO.getInstance().getUuidFromPath(userTrashPath);
			String name = PathUtils.getName(mailPath);

			NodeMailDAO.getInstance().delete(name, mailUuid, userTrashUuid);

			// Activity log
			UserActivity.log(auth.getName(), "DELETE_MAIL", mailUuid, mailPath, null);
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
	public void purge(String token, String mailId) throws LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("purge({}, {})", token, mailId);
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;
		String mailPath = null;
		String mailUuid = null;

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

			if (PathUtils.isPath(mailId)) {
				mailPath = mailId;
				mailUuid = NodeBaseDAO.getInstance().getUuidFromPath(mailId);
			} else {
				mailPath = NodeBaseDAO.getInstance().getPathFromUuid(mailId);
				mailUuid = mailId;
			}

			if (Config.REPOSITORY_PURGATORY_HOME != null && !Config.REPOSITORY_PURGATORY_HOME.isEmpty()) {
				File dateDir = FileUtils.createDateDir(Config.REPOSITORY_PURGATORY_HOME);
				File dstPath = new File(dateDir, PathUtils.getName(mailPath));
				RepositoryExporter.exportMail(null, mailPath, dstPath.getPath() + ".eml", true, null, null);
			}

			if (BaseMailModule.hasLockedNodes(mailUuid)) {
				throw new LockException("Can't delete a mail with child locked attachments");
			}

			if (!BaseMailModule.hasWriteAccess(mailUuid)) {
				throw new AccessDeniedException("Can't delete a mail with readonly attachments");
			}

			NodeMailDAO.getInstance().purge(mailUuid);

			// Activity log - Already inside DAO
			// UserActivity.log(auth.getName(), "PURGE_MAIL", mailUuid, mailPath, null);
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
	public Mail rename(String token, String mailId, String newName) throws PathNotFoundException, ItemExistsException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("rename({}, {}, {})", new Object[]{token, mailId, newName});
		Mail renamedMail = null;
		Authentication auth = null, oldAuth = null;
		String mailPath = null;
		String mailUuid = null;

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

			if (PathUtils.isPath(mailId)) {
				mailPath = mailId;
				mailUuid = NodeBaseDAO.getInstance().getUuidFromPath(mailId);
			} else {
				mailPath = NodeBaseDAO.getInstance().getPathFromUuid(mailId);
				mailUuid = mailId;
			}

			String name = PathUtils.getName(mailPath);

			// Escape dangerous chars in name
			newName = PathUtils.escape(newName);

			if (newName != null && !newName.isEmpty() && !newName.equals(name)) {
				NodeMail mailNode = NodeMailDAO.getInstance().rename(mailUuid, newName);
				renamedMail = BaseMailModule.getProperties(auth.getName(), mailNode);
			} else {
				// Don't change anything
				NodeMail mailNode = NodeMailDAO.getInstance().findByPk(mailUuid);
				renamedMail = BaseMailModule.getProperties(auth.getName(), mailNode);
			}

			// Activity log
			UserActivity.log(auth.getName(), "RENAME_MAIL", mailUuid, mailPath, newName);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("rename: {}", renamedMail);
		return renamedMail;
	}

	@Override
	public void move(String token, String mailId, String dstId) throws PathNotFoundException, ItemExistsException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("move({}, {}, {})", new Object[]{token, mailId, dstId});
		Authentication auth = null, oldAuth = null;
		String mailPath = null;
		String mailUuid = null;
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

			if (PathUtils.isPath(mailId)) {
				mailPath = mailId;
				mailUuid = NodeBaseDAO.getInstance().getUuidFromPath(mailId);
			} else {
				mailPath = NodeBaseDAO.getInstance().getPathFromUuid(mailId);
				mailUuid = mailId;
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

			NodeMailDAO.getInstance().move(mailUuid, dstUuid);

			// Activity log
			UserActivity.log(auth.getName(), "MOVE_MAIL", mailUuid, mailPath, dstPath);
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
	public void copy(String token, String mailId, String dstId) throws PathNotFoundException, ItemExistsException, AccessDeniedException,
			RepositoryException, IOException, AutomationException, DatabaseException, UserQuotaExceededException {
		log.debug("copy({}, {}, {})", new Object[]{token, mailId, dstId});
		extendedCopy(token, mailId, dstId, new ExtendedAttributes());
	}

	@Override
	public void extendedCopy(String token, String mailId, String dstId, ExtendedAttributes extAttr) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, IOException, AutomationException, DatabaseException,
			UserQuotaExceededException {
		log.debug("extendedCopy({}, {}, {}, {})", new Object[]{token, mailId, dstId, extAttr});
		Authentication auth = null, oldAuth = null;
		String mailPath = null;
		String mailUuid = null;
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

			if (PathUtils.isPath(mailId)) {
				mailPath = mailId;
				mailUuid = NodeBaseDAO.getInstance().getUuidFromPath(mailId);
			} else {
				mailPath = NodeBaseDAO.getInstance().getPathFromUuid(mailId);
				mailUuid = mailId;
			}

			if (PathUtils.isPath(dstId)) {
				dstPath = dstId;
				dstUuid = NodeBaseDAO.getInstance().getUuidFromPath(dstId);
			} else {
				dstPath = NodeBaseDAO.getInstance().getPathFromUuid(dstId);
				dstUuid = dstId;
			}

			NodeMail srcMailNode = NodeMailDAO.getInstance().findByPk(mailUuid);
			NodeFolder dstFldNode = NodeFolderDAO.getInstance().findByPk(dstUuid);
			NodeMail newMailNode = BaseMailModule.copy(auth.getName(), srcMailNode, dstPath, dstFldNode, extAttr);

			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(dstFldNode, auth.getName(), "COPY_MAIL", null);

			// Activity log
			UserActivity.log(auth.getName(), "COPY_MAIL", newMailNode.getUuid(), mailPath, dstPath);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("extendedCopy: void");
	}

	@Override
	@Deprecated
	public List<Mail> getChilds(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		return getChildren(token, fldId);
	}

	@Override
	public List<Mail> getChildren(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getChildren({}, {})", token, fldId);
		long begin = System.currentTimeMillis();
		List<Mail> children = new ArrayList<Mail>();
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

			for (NodeMail nMail : NodeMailDAO.getInstance().findByParent(fldUuid)) {
				children.add(BaseMailModule.getProperties(auth.getName(), nMail));
			}

			// Activity log
			UserActivity.log(auth.getName(), "GET_CHILDREN_MAILS", fldUuid, fldPath, null);
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
	public boolean isValid(String token, String mailId) throws PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("isValid({}, {})", token, mailId);
		boolean valid = true;
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;
		String mailUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(mailId)) {
				mailUuid = NodeBaseDAO.getInstance().getUuidFromPath(mailId);
			} else {
				mailUuid = mailId;
			}

			try {
				NodeMailDAO.getInstance().findByPk(mailUuid);
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
