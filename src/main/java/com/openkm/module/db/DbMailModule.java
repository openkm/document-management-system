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
import com.openkm.bean.*;
import com.openkm.core.Config;
import com.openkm.core.*;
import com.openkm.dao.*;
import com.openkm.dao.bean.*;
import com.openkm.module.MailModule;
import com.openkm.module.db.base.BaseDocumentModule;
import com.openkm.module.db.base.BaseMailModule;
import com.openkm.module.db.base.BaseModule;
import com.openkm.module.db.base.BaseNotificationModule;
import com.openkm.spring.PrincipalUtils;
import com.openkm.spring.SecurityHolder;
import com.openkm.util.*;
import com.openkm.util.impexp.RepositoryExporter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import javax.mail.MessagingException;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class DbMailModule implements MailModule {
	private static final Logger log = LoggerFactory.getLogger(DbMailModule.class);

	@Override
	public Mail create(String token, Mail mail) throws PathNotFoundException, ItemExistsException, VirusDetectedException,
			AccessDeniedException, RepositoryException, DatabaseException, UserQuotaExceededException, AutomationException {
		log.debug("create({}, {})", token, mail);
		return create(token, mail, null, new Ref<>(null));
	}

	/**
	 * Used when importing mail from scheduler
	 */
	public Mail create(String token, Mail mail, String userId, Ref<FileUploadResponse> fuResponse) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, ItemExistsException, VirusDetectedException, DatabaseException,
			UserQuotaExceededException, AutomationException {
		log.debug("create({}, {}, {})", token, mail, userId);
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
						mail.getSubject(), mail.getContent(), mail.getMimeType(), new HashSet<>(), new HashSet<>(), new HashSet<>(),
						new ArrayList<>(), null, fuResponse);

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
	public Document createAttachment(String token, String mailId, String docName, InputStream is) throws UnsupportedMimeTypeException,
			FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, ItemExistsException, PathNotFoundException,
			AccessDeniedException, RepositoryException, IOException, DatabaseException, AutomationException {
		return createAttachment(token, mailId, docName, is, is.available(), null);
	}

	/**
	 * Used when big files and FileUpload
	 */
	public Document createAttachment(String token, String mailId, String docName, InputStream is, long size, String userId)
			throws UnsupportedMimeTypeException, FileSizeExceededException, UserQuotaExceededException, VirusDetectedException,
			ItemExistsException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException, DatabaseException,
			AutomationException {
		log.debug("createAttachment({}, {}, {}, {}, {}, {})", token, mailId, docName, is, size, userId);
		long begin = System.currentTimeMillis();
		Document newDocument = null;
		String mailUuid = mailId;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		if (PathUtils.isPath(mailId)) {
			mailUuid = NodeBaseDAO.getInstance().getUuidFromPath(mailId);
		}

		// Escape dangerous chars in name
		docName = PathUtils.escape(docName);

		NodeMail parentNode = NodeMailDAO.getInstance().findByPk(mailUuid);
		String parentPath = parentNode.getPath();

		if (parentPath == null) {
			parentPath = getPath(token, parentNode.getUuid());
		}

		String docPath = parentPath + "/" + docName;
		String fileExt = FileUtils.getFileExtension(docName);
		File tmp = File.createTempFile("okm", fileExt.isEmpty() ? ".tmp" : "." + fileExt);

		try {
			if (token != null) {
				SecurityHolder.set(PrincipalUtils.getAuthenticationByToken(token));
			}

			if (Config.MAX_FILE_SIZE > 0 && size > Config.MAX_FILE_SIZE) {
				log.error("Uploaded file size: {} ({}), Max file size: {} ({})", FormatUtil.formatSize(size), size,
						FormatUtil.formatSize(Config.MAX_FILE_SIZE), Config.MAX_FILE_SIZE);
				String usr = userId == null ? PrincipalUtils.getUser() : userId;
				UserActivity.log(usr, "ERROR_FILE_SIZE_EXCEEDED", null, docPath, FormatUtil.formatSize(size));
				throw new FileSizeExceededException(FormatUtil.formatSize(size));
			}

			if (!docName.isEmpty()) {
				// Check file restrictions
				String mimeType = MimeTypeConfig.mimeTypes.getContentType(docName.toLowerCase());

				if (Config.RESTRICT_FILE_MIME && MimeTypeDAO.findByName(mimeType) == null) {
					String usr = userId == null ? PrincipalUtils.getUser() : userId;
					UserActivity.log(usr, "ERROR_UNSUPPORTED_MIME_TYPE", null, docPath, mimeType);
					throw new UnsupportedMimeTypeException(mimeType);
				}

				// Restrict for extension
				if (!Config.RESTRICT_FILE_NAME.isEmpty()) {
					StringTokenizer st = new StringTokenizer(Config.RESTRICT_FILE_NAME, Config.LIST_SEPARATOR);

					while (st.hasMoreTokens()) {
						String wc = st.nextToken().trim();
						String re = ConfigUtils.wildcard2regexp(wc);

						if (Pattern.matches(re, docName)) {
							String usr = userId == null ? PrincipalUtils.getUser() : userId;
							UserActivity.log(usr, "ERROR_UNSUPPORTED_MIME_TYPE", null, docPath, mimeType);
							throw new UnsupportedMimeTypeException(mimeType);
						}
					}
				}

				if (!Config.SYSTEM_ANTIVIR.isEmpty()) {
					FileOutputStream fos = new FileOutputStream(tmp);
					IOUtils.copy(is, fos);
					IOUtils.closeQuietly(fos);
					IOUtils.closeQuietly(is);
					is = new FileInputStream(tmp);
					String info = VirusDetection.detect(tmp);

					if (info != null) {
						String usr = userId == null ? PrincipalUtils.getUser() : userId;
						UserActivity.log(usr, "ERROR_VIRUS_DETECTED", null, docPath, info);
						throw new VirusDetectedException(info);
					}
				}

				// AUTOMATION - PRE
				// INSIDE BaseDocumentModule.create

				// Create node
				NodeDocument docNode = BaseDocumentModule.create(PrincipalUtils.getUser(), parentPath, parentNode, docName, null, Calendar.getInstance(),
						mimeType, is, size, new HashSet<String>(), new HashSet<String>(), new HashSet<NodeProperty>(), new ArrayList<NodeNote>(),
						null, new Ref<FileUploadResponse>(null));

				// AUTOMATION - POST
				// INSIDE BaseDocumentModule.create

				// Keep on sync
				// See also com.openkm.module.db.DbDocumentModule.create(String, Document, InputStream, long, String)
				NodeMail nodeMail = NodeMailDAO.getInstance().findByPk(mailId);
				if (!nodeMail.getHasAttachments()) {
					nodeMail.setHasAttachments(true);
					NodeMailDAO.getInstance().update(nodeMail);
				}

				// Set returned folder properties
				newDocument = BaseDocumentModule.getProperties(PrincipalUtils.getUser(), docNode);

				// Setting wizard properties
				// INSIDE BaseDocumentModule.create

				if (userId == null) {
					userId = PrincipalUtils.getUser();
				}

				// Check subscriptions
				BaseNotificationModule.checkSubscriptions(docNode, userId, "CREATE_MAIL_ATTACHMENT", null);

				// Activity log
				UserActivity.log(userId, "CREATE_MAIL_ATTACHMENT", docNode.getUuid(), docPath, mimeType + ", " + size);
			} else {
				throw new RepositoryException("Invalid document name");
			}
		} finally {
			IOUtils.closeQuietly(is);
			org.apache.commons.io.FileUtils.deleteQuietly(tmp);

			if (token != null) {
				SecurityHolder.unset();
			}
		}

		SystemProfiling.log(docPath, System.currentTimeMillis() - begin);
		log.trace("createAttachment.Time: {}", System.currentTimeMillis() - begin);
		log.debug("createAttachment: {}", newDocument);
		return newDocument;
	}

	@Override
	public void deleteAttachment(String token, String mailId, String docId) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("deleteAttachment({}, {})", token, docId);
		long begin = System.currentTimeMillis();
		String mailUuid = mailId;
		String docPath = null;
		String docUuid = null;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token != null) {
				SecurityHolder.set(PrincipalUtils.getAuthenticationByToken(token));
			}

			if (PathUtils.isPath(mailId)) {
				mailUuid = NodeBaseDAO.getInstance().getUuidFromPath(mailId);
			}

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			if (BaseDocumentModule.hasWorkflowNodes(docUuid)) {
				throw new RepositoryException("Can't delete a document used in a workflow");
			}

			String parentUuid = NodeBaseDAO.getInstance().getParentUuid(docUuid);

			if (!mailUuid.equals(parentUuid)) {
				throw new RepositoryException("This mail does not include this attachment");
			}

			String userTrashPath = "/" + Repository.TRASH + "/" + PrincipalUtils.getUser();
			String userTrashUuid = NodeBaseDAO.getInstance().getUuidFromPath(userTrashPath);
			String name = PathUtils.getName(docPath);

			// Check subscriptions
			NodeDocument documentNode = NodeDocumentDAO.getInstance().findByPk(docUuid);
			BaseNotificationModule.checkSubscriptions(documentNode, PrincipalUtils.getUser(), "DELETE_MAIL_ATTACHMENT", null);

			// After notification move to trash folder
			NodeDocumentDAO.getInstance().delete(name, docUuid, userTrashUuid);

			// Keep on sync
			// See also com.openkm.module.db.DbDocumentModule.delete(String, String)
			NodeMail nodeMail = NodeMailDAO.getInstance().findByPk(mailId);
			if (nodeMail.getHasAttachments()) {
				nodeMail.setHasAttachments(NodeDocumentDAO.getInstance().hasChildren(nodeMail.getUuid()));
				NodeMailDAO.getInstance().update(nodeMail);
			}

			// Activity log
			UserActivity.log(PrincipalUtils.getUser(), "DELETE_MAIL_ATTACHMENT", docUuid, docPath, null);
		} catch (WorkflowException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				SecurityHolder.unset();
			}
		}

		SystemProfiling.log(docPath, System.currentTimeMillis() - begin);
		log.trace("deleteAttachment.Time: {}", System.currentTimeMillis() - begin);
		log.debug("deleteAttachment: void");
	}

	@Override
	public List<Document> getAttachments(String token, String mailId) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("getAttachments({}, {})", token, mailId);
		long begin = System.currentTimeMillis();
		List<Document> children = new ArrayList<Document>();

		try {
			if (token != null) {
				SecurityHolder.set(PrincipalUtils.getAuthenticationByToken(token));
			}

			NodeBase node = BaseModule.resolveNodeById(mailId);

			for (NodeDocument nDocument : NodeDocumentDAO.getInstance().findByParent(node.getUuid())) {
				children.add(BaseDocumentModule.getProperties(PrincipalUtils.getUser(), nDocument));
			}

			// Activity log
			UserActivity.log(PrincipalUtils.getUser(), "GET_MAIL_ATTACHMENTS", node.getUuid(), node.getPath(), null);

			SystemProfiling.log(node.getPath(), System.currentTimeMillis() - begin);
			log.trace("getAttachments.Time: {}", System.currentTimeMillis() - begin);
			log.debug("getAttachments: {}", children);
			return children;
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				SecurityHolder.unset();
			}
		}
	}

	@Override
	public void delete(String token, String mailId) throws LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("delete({}, {})", token, mailId);
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
	public void purge(String token, String mailId) throws LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
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
		} catch (IOException | ParseException | NoSuchGroupException | MessagingException e) {
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
		log.debug("rename({}, {}, {})", token, mailId, newName);
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
		log.debug("move({}, {}, {})", token, mailId, dstId);
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
		log.debug("copy({}, {}, {})", token, mailId, dstId);
		extendedCopy(token, mailId, dstId, new ExtendedAttributes());
	}

	@Override
	public void extendedCopy(String token, String mailId, String dstId, ExtendedAttributes extAttr) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, IOException, AutomationException, DatabaseException,
			UserQuotaExceededException {
		log.debug("extendedCopy({}, {}, {}, {})", token, mailId, dstId, extAttr);
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

			NodeMail srcMailNode = NodeMailDAO.getInstance().findByPk(mailUuid, true);
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
	public List<Mail> getChilds(String token, String fldId) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		return getChildren(token, fldId);
	}

	@Override
	public List<Mail> getChildren(String token, String fldId) throws AccessDeniedException, PathNotFoundException,
			DatabaseException {
		log.debug("getChildren({}, {})", token, fldId);
		long begin = System.currentTimeMillis();
		List<Mail> children = new ArrayList<>();
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

	@Override
	public void sendMail(String token, List<String> recipients, String subject, String body) throws AccessDeniedException, IOException {
		try {
			if (token != null) {
				SecurityHolder.set(PrincipalUtils.getAuthenticationByToken(token));
			}

			MailUtils.sendMessage(recipients, subject, body);
		} catch (MessagingException e) {
			throw new IOException(e.getMessage(), e);
		} finally {
			if (token != null) {
				SecurityHolder.unset();
			}
		}
	}
}
