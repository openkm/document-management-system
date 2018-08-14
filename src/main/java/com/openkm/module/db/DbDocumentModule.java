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
import com.openkm.bean.*;
import com.openkm.cache.UserItemsManager;
import com.openkm.core.*;
import com.openkm.core.Config;
import com.openkm.dao.*;
import com.openkm.dao.bean.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.module.DocumentModule;
import com.openkm.module.common.CommonGeneralModule;
import com.openkm.module.db.base.BaseDocumentModule;
import com.openkm.module.db.base.BaseModule;
import com.openkm.module.db.base.BaseNoteModule;
import com.openkm.module.db.base.BaseNotificationModule;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.*;
import com.openkm.util.impexp.RepositoryExporter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class DbDocumentModule implements DocumentModule {
	private static Logger log = LoggerFactory.getLogger(DbDocumentModule.class);

	@Override
	public Document create(String token, Document doc, InputStream is) throws UnsupportedMimeTypeException, FileSizeExceededException,
			UserQuotaExceededException, VirusDetectedException, ItemExistsException, PathNotFoundException, AccessDeniedException,
			RepositoryException, IOException, DatabaseException, ExtensionException, AutomationException {
		log.debug("create({}, {}, {})", new Object[]{token, doc, is});
		return create(token, doc, is, is.available(), null);
	}

	/**
	 * Used when big files and WebDAV and GoogleDocs
	 */
	public Document create(String token, Document doc, InputStream is, long size, String userId) throws UnsupportedMimeTypeException,
			FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, ItemExistsException, PathNotFoundException,
			AccessDeniedException, RepositoryException, IOException, DatabaseException, ExtensionException, AutomationException {
		log.debug("create({}, {}, {}, {}, {})", new Object[]{token, doc, is, size, userId});
		return create(token, doc, is, size, userId, new Ref<FileUploadResponse>(null));
	}

	/**
	 * Used when big files and FileUpload
	 */
	public Document create(String token, Document doc, InputStream is, long size, String userId, Ref<FileUploadResponse> fuResponse)
			throws UnsupportedMimeTypeException, FileSizeExceededException, UserQuotaExceededException, VirusDetectedException,
			ItemExistsException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException, DatabaseException,
			ExtensionException, AutomationException {
		log.debug("create({}, {}, {}, {}, {}, {})", new Object[]{token, doc, is, size, userId, fuResponse});
		long begin = System.currentTimeMillis();
		Document newDocument = null;
		Authentication auth = null, oldAuth = null;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		if (!PathUtils.checkPath(doc.getPath())) {
			throw new RepositoryException("Invalid path: " + doc.getPath());
		}

		String parentPath = PathUtils.getParent(doc.getPath());
		String name = PathUtils.getName(doc.getPath());

		// Add to KEA - must have the same extension
		int idx = name.lastIndexOf('.');
		String fileExtension = idx > 0 ? name.substring(idx) : ".tmp";
		File tmp = File.createTempFile("okm", fileExtension);

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (Config.CLOUD_MAX_REPOSITORY_SIZE > 0) {
				StatsInfo si = RepositoryInfo.getDocumentsSizeByContext();

				if (si.getTotal() > Config.CLOUD_MAX_REPOSITORY_SIZE) {
					String usr = userId == null ? auth.getName() : userId;
					UserActivity.log(usr, "ERROR_REPOSITORY_SIZE_EXCEEDED", null, null, FormatUtil.formatSize(si.getTotal()));
					throw new FileSizeExceededException(FormatUtil.formatSize(si.getTotal()));
				}
			}

			if (Config.MAX_FILE_SIZE > 0 && size > Config.MAX_FILE_SIZE) {
				log.error("Uploaded file size: {} ({}), Max file size: {} ({})", new Object[]{FormatUtil.formatSize(size), size,
						FormatUtil.formatSize(Config.MAX_FILE_SIZE), Config.MAX_FILE_SIZE});
				String usr = userId == null ? auth.getName() : userId;
				UserActivity.log(usr, "ERROR_FILE_SIZE_EXCEEDED", null, doc.getPath(), FormatUtil.formatSize(size));
				throw new FileSizeExceededException(FormatUtil.formatSize(size));
			}

			// Escape dangerous chars in name
			name = PathUtils.escape(name);

			if (!name.isEmpty()) {
				doc.setPath(parentPath + "/" + name);

				// Check file restrictions
				String mimeType = MimeTypeConfig.mimeTypes.getContentType(name.toLowerCase());
				doc.setMimeType(mimeType);

				if (Config.RESTRICT_FILE_MIME && MimeTypeDAO.findByName(mimeType) == null) {
					String usr = userId == null ? auth.getName() : userId;
					UserActivity.log(usr, "ERROR_UNSUPPORTED_MIME_TYPE", null, doc.getPath(), mimeType);
					throw new UnsupportedMimeTypeException(mimeType);
				}

				// Restrict for extension
				if (!Config.RESTRICT_FILE_NAME.isEmpty()) {
					StringTokenizer st = new StringTokenizer(Config.RESTRICT_FILE_NAME, Config.LIST_SEPARATOR);

					while (st.hasMoreTokens()) {
						String wc = st.nextToken().trim();
						String re = ConfigUtils.wildcard2regexp(wc);

						if (Pattern.matches(re, name)) {
							String usr = userId == null ? auth.getName() : userId;
							UserActivity.log(usr, "ERROR_UNSUPPORTED_MIME_TYPE", null, doc.getPath(), mimeType);
							throw new UnsupportedMimeTypeException(mimeType);
						}
					}
				}

				// Manage temporary files
				byte[] buff = new byte[4 * 1024];
				FileOutputStream fos = new FileOutputStream(tmp);
				int read;

				while ((read = is.read(buff)) != -1) {
					fos.write(buff, 0, read);
				}

				fos.flush();
				fos.close();
				is.close();
				is = new FileInputStream(tmp);

				if (!Config.SYSTEM_ANTIVIR.equals("")) {
					String info = VirusDetection.detect(tmp);

					if (info != null) {
						String usr = userId == null ? auth.getName() : userId;
						UserActivity.log(usr, "ERROR_VIRUS_DETECTED", null, doc.getPath(), info);
						throw new VirusDetectedException(info);
					}
				}

				String parentUuid = NodeBaseDAO.getInstance().getUuidFromPath(parentPath);
				NodeBase parentNode = NodeBaseDAO.getInstance().findByPk(parentUuid);

				// AUTOMATION - PRE
				// INSIDE BaseDocumentModule.create

				// Create node
				Set<String> keywords = doc.getKeywords() != null ? doc.getKeywords() : new HashSet<String>();
				NodeDocument docNode = BaseDocumentModule.create(auth.getName(), parentPath, parentNode, name, doc.getTitle(),
						doc.getCreated(), mimeType, is, size, keywords, new HashSet<String>(), new HashSet<NodeProperty>(),
						new ArrayList<NodeNote>(), null, fuResponse);

				// AUTOMATION - POST
				// INSIDE BaseDocumentModule.create

				// Set returned folder properties
				newDocument = BaseDocumentModule.getProperties(auth.getName(), docNode);

				// Setting wizard properties
				// INSIDE BaseDocumentModule.create

				if (fuResponse.get() == null) {
					fuResponse.set(new FileUploadResponse());
				}

				fuResponse.get().setHasAutomation(AutomationManager.getInstance().hasAutomation());

				if (userId == null) {
					// Check subscriptions
					BaseNotificationModule.checkSubscriptions(docNode, auth.getName(), "CREATE_DOCUMENT", null);

					// Activity log
					UserActivity.log(auth.getName(), "CREATE_DOCUMENT", docNode.getUuid(), doc.getPath(), mimeType + ", " + size);
				} else {
					// Check subscriptions
					BaseNotificationModule.checkSubscriptions(docNode, userId, "CREATE_MAIL_ATTACHMENT", null);

					// Activity log
					UserActivity.log(userId, "CREATE_MAIL_ATTACHMENT", docNode.getUuid(), doc.getPath(), mimeType + ", " + size);
				}
			} else {
				throw new RepositoryException("Invalid document name");
			}
		} finally {
			IOUtils.closeQuietly(is);
			org.apache.commons.io.FileUtils.deleteQuietly(tmp);

			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(doc.getPath(), System.currentTimeMillis() - begin);
		log.trace("create.Time: {}", System.currentTimeMillis() - begin);
		log.debug("create: {}", newDocument);
		return newDocument;
	}

	@Override
	public void delete(String token, String docId) throws LockException, PathNotFoundException, AccessDeniedException, AutomationException,
			RepositoryException, DatabaseException {
		log.debug("delete({}, {})", new Object[]{token, docId});
		long begin = System.currentTimeMillis();
		Authentication auth = null, oldAuth = null;
		NodeDocument docNode;

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

			docNode = (NodeDocument) BaseModule.resolveNodeById(docId);

			if (BaseDocumentModule.hasWorkflowNodes(docNode.getUuid())) {
				throw new LockException("Can't delete a document used in a workflow");
			}

			String userTrashPath = "/" + Repository.TRASH + "/" + auth.getName();
			String userTrashUuid = NodeBaseDAO.getInstance().getUuidFromPath(userTrashPath);
			String name = PathUtils.getName(docNode.getPath());

			// AUTOMATION - PRE
			Map<String, Object> env = new HashMap<>();
			env.put(AutomationUtils.DOCUMENT_NODE, docNode);
			env.put(AutomationUtils.DOCUMENT_UUID, docNode.getUuid());
			env.put(AutomationUtils.PARENT_UUID, docNode.getParent());
			AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_DOCUMENT_DELETE, AutomationRule.AT_PRE, env);

			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(docNode, PrincipalUtils.getUser(), "DELETE_DOCUMENT", null);

			// After notification move to trash folder
			NodeDocumentDAO.getInstance().delete(name, docNode.getUuid(), userTrashUuid);

			// AUTOMATION - POST
			AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_DOCUMENT_DELETE, AutomationRule.AT_POST, env);

			// Activity log
			UserActivity.log(auth.getName(), "DELETE_DOCUMENT", docNode.getUuid(), docNode.getPath(), null);
		} catch (WorkflowException e) {
			throw new RepositoryException(e.getMessage());
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(docNode.getPath(), System.currentTimeMillis() - begin);
		log.trace("delete.Time: {}", System.currentTimeMillis() - begin);
		log.debug("delete: void");
	}

	@Override
	public Document rename(String token, String docId, String newName) throws PathNotFoundException, ItemExistsException,
			AccessDeniedException, AutomationException, LockException, RepositoryException, DatabaseException {
		log.debug("rename({}, {}, {})", new Object[]{token, docId, newName});
		Document renamedDocument = null;
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

			NodeDocument docNode = (NodeDocument) BaseModule.resolveNodeById(docId);

			// AUTOMATION - PRE
			Map<String, Object> env = new HashMap<>();
			env.put(AutomationUtils.DOCUMENT_NODE, docNode);
			env.put(AutomationUtils.DOCUMENT_UUID, docNode.getUuid());
			env.put(AutomationUtils.PARENT_UUID, docNode.getParent());
			AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_DOCUMENT_RENAME, AutomationRule.AT_PRE, env);

			String name = PathUtils.getName(docNode.getPath());

			// Escape dangerous chars in name
			newName = PathUtils.escape(newName);

			if (newName != null && !newName.isEmpty() && !newName.equals(name)) {
				NodeDocument documentNode = NodeDocumentDAO.getInstance().rename(docNode.getUuid(), newName);
				renamedDocument = BaseDocumentModule.getProperties(auth.getName(), documentNode);

				// Check subscriptions
				BaseNotificationModule.checkSubscriptions(documentNode, PrincipalUtils.getUser(), "RENAME_DOCUMENT", null);
			} else {
				// Don't change anything
				NodeDocument documentNode = NodeDocumentDAO.getInstance().findByPk(docNode.getUuid());
				renamedDocument = BaseDocumentModule.getProperties(auth.getName(), documentNode);
			}

			// AUTOMATION - POST
			AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_DOCUMENT_RENAME, AutomationRule.AT_POST, env);

			// Activity log
			UserActivity.log(auth.getName(), "RENAME_DOCUMENT", docNode.getUuid(), docNode.getPath(), newName);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("rename: {}", renamedDocument);
		return renamedDocument;
	}

	@Override
	public Document getProperties(String token, String docId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getProperties({}, {})", token, docId);
		Authentication auth, oldAuth = null;
		String docPath;
		String docUuid;
		Document doc;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			NodeDocument docNode = NodeDocumentDAO.getInstance().findByPk(docUuid);
			doc = BaseDocumentModule.getProperties(auth.getName(), docNode);

			// Activity log
			UserActivity.log(auth.getName(), "GET_DOCUMENT_PROPERTIES", docUuid, docPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getProperties: {}", doc);
		return doc;
	}

	@Override
	public void setProperties(String token, Document doc) throws VersionException, LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("setProperties({}, {})", token, doc);
		Authentication auth, oldAuth = null;
		@SuppressWarnings("unused")
		String docPath = null;
		String docUuid;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (doc.getPath() != null && !doc.getPath().isEmpty()) {
				docPath = doc.getPath();
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(doc.getPath());
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(doc.getUuid());
				docUuid = doc.getUuid();
			}

			NodeDocument docNode = NodeDocumentDAO.getInstance().findByPk(docUuid);
			NodeDocumentDAO.getInstance().setProperties(docUuid, doc);
			
			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(docNode, auth.getName(), "SET_DOCUMENT_PROPERTIES", null);

			// Activity log
			UserActivity.log(auth.getName(), "SET_DOCUMENT_PROPERTIES", docUuid, doc.getPath(), null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("setProperties: void");
	}

	@Override
	public InputStream getContent(String token, String docId, boolean checkout) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, IOException, DatabaseException {
		log.debug("getContent({}, {}, {})", new Object[]{token, docId, checkout});
		return getContent(token, docId, checkout, true);
	}

	/**
	 * Retrieve the content input stream from a document
	 *
	 * @param token Authorization token.
	 * @param docId Path of the document to get the content or its UUID.
	 * @param checkout If the content is retrieved due to a checkout or not.
	 * @param extendedSecurity If the extended security DOWNLOAD permission should be evaluated.
	 *        This is used to enable the document preview.
	 */
	public InputStream getContent(String token, String docId, boolean checkout, boolean extendedSecurity) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, IOException, DatabaseException {
		log.debug("getContent({}, {}, {}, {})", new Object[]{token, docId, checkout, extendedSecurity});
		long begin = System.currentTimeMillis();
		InputStream is;
		Authentication auth = null, oldAuth = null;
		String docPath;
		String docUuid;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			is = BaseDocumentModule.getContent(auth.getName(), docUuid, docPath, checkout, extendedSecurity);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(docPath, System.currentTimeMillis() - begin);
		log.trace("getContent.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getContent: {}", is);
		return is;
	}

	@Override
	public InputStream getContentByVersion(String token, String docId, String verName) throws RepositoryException, AccessDeniedException,
			PathNotFoundException, IOException, DatabaseException {
		log.debug("getContentByVersion({}, {}, {})", new Object[]{token, docId, verName});
		Authentication auth, oldAuth = null;
		InputStream is;
		String docPath;
		String docUuid;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			is = NodeDocumentVersionDAO.getInstance().getVersionContentByParent(docUuid, verName);

			// Activity log
			UserActivity.log(auth.getName(), "GET_DOCUMENT_CONTENT_BY_VERSION", docUuid, docPath, verName + ", " + is.available());
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getContentByVersion: {}", is);
		return is;
	}

	@Override
	@Deprecated
	public List<Document> getChilds(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		return getChildren(token, fldId);
	}

	@Override
	public List<Document> getChildren(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getChildren({}, {})", token, fldId);
		long begin = System.currentTimeMillis();
		List<Document> children = new ArrayList<>();
		Authentication auth, oldAuth = null;
		String fldPath;
		String fldUuid;

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

			for (NodeDocument nDocument : NodeDocumentDAO.getInstance().findByParent(fldUuid)) {
				children.add(BaseDocumentModule.getProperties(auth.getName(), nDocument));
			}

			// Activity log
			UserActivity.log(auth.getName(), "GET_CHILDREN_DOCUMENTS", fldUuid, fldPath, null);
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
	public void checkout(String token, String docId) throws LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		checkout(token, docId, null);
	}

	/**
	 * Used in Zoho extension
	 */
	public void checkout(String token, String docId, String userId) throws LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("checkout({}, {}, {})", new Object[]{token, docId, userId});
		long begin = System.currentTimeMillis();
		Authentication auth, oldAuth = null;
		String docPath;
		String docUuid;

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

			if (userId == null) {
				userId = auth.getName();
			}

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			NodeDocumentDAO.getInstance().checkout(userId, docUuid);

			// Activity log
			UserActivity.log(auth.getName(), "CHECKOUT_DOCUMENT", docUuid, docPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(docPath, System.currentTimeMillis() - begin);
		log.trace("checkout.Time: {}", System.currentTimeMillis() - begin);
		log.debug("checkout: void");
	}

	@Override
	public void cancelCheckout(String token, String docId) throws LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("cancelCheckout({}, {})", token, docId);
		cancelCheckoutHelper(token, docId, false);
		log.debug("cancelCheckout: void");
	}

	@Override
	public void forceCancelCheckout(String token, String docId) throws LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException, PrincipalAdapterException {
		log.debug("forceCancelCheckout({}, {})", token, docId);

		if (PrincipalUtils.getRoles().contains(Config.DEFAULT_ADMIN_ROLE)) {
			cancelCheckoutHelper(token, docId, true);
		} else {
			throw new AccessDeniedException("Only administrator use allowed");
		}

		log.debug("forceCancelCheckout: void");
	}

	/**
	 * Implement cancelCheckout and forceCancelCheckout features
	 */
	private void cancelCheckoutHelper(String token, String docId, boolean force) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("cancelCheckoutHelper({}, {}, {})", new Object[]{token, docId, force});
		long begin = System.currentTimeMillis();
		Authentication auth, oldAuth = null;
		String action = force ? "FORCE_CANCEL_DOCUMENT_CHECKOUT" : "CANCEL_DOCUMENT_CHECKOUT";
		String docPath;
		String docUuid;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			NodeDocument docNode = NodeDocumentDAO.getInstance().findByPk(docUuid);
			NodeDocumentDAO.getInstance().cancelCheckout(auth.getName(), docUuid, force);

			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(docNode, auth.getName(), action, null);

			// Activity log
			UserActivity.log(auth.getName(), action, docUuid, docPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(docPath, System.currentTimeMillis() - begin);
		log.trace("cancelCheckoutHelper.Time: {}", System.currentTimeMillis() - begin);
		log.debug("cancelCheckoutHelper: void");
	}

	@Override
	public boolean isCheckedOut(String token, String docId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("isCheckedOut({}, {})", token, docId);
		boolean checkedOut = false;
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;
		@SuppressWarnings("unused")
		String docPath = null;
		String docUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			checkedOut = NodeDocumentDAO.getInstance().isCheckedOut(docUuid);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("isCheckedOut: {}", checkedOut);
		return checkedOut;
	}

	@Override
	public Version checkin(String token, String docId, InputStream is, String comment) throws FileSizeExceededException,
			UserQuotaExceededException, VirusDetectedException, AccessDeniedException, RepositoryException, PathNotFoundException,
			LockException, VersionException, IOException, DatabaseException, AutomationException {
		return checkin(token, docId, is, is.available(), comment, null, 0);
	}

	@Override
	public Version checkin(String token, String docId, InputStream is, String comment, int increment) throws FileSizeExceededException,
			UserQuotaExceededException, VirusDetectedException, AccessDeniedException, RepositoryException, PathNotFoundException,
			LockException, VersionException, IOException, DatabaseException, AutomationException {
		return checkin(token, docId, is, is.available(), comment, null, increment);
	}

	/**
	 * Used in Zoho extension
	 */
	public Version checkin(String token, String docId, InputStream is, String comment, String userId) throws FileSizeExceededException,
			UserQuotaExceededException, VirusDetectedException, AccessDeniedException, RepositoryException, PathNotFoundException,
			LockException, VersionException, IOException, DatabaseException, AutomationException {
		return checkin(token, docId, is, is.available(), comment, userId, 0);
	}

	/**
	 * Used when big files and WebDAV
	 */
	public Version checkin(String token, String docId, InputStream is, long size, String comment, String userId)
			throws FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, AccessDeniedException,
			RepositoryException, PathNotFoundException, LockException, VersionException, IOException, DatabaseException,
			AutomationException {
		return checkin(token, docId, is, size, comment, userId, 0);
	}

	/**
	 * Used when increase document major version
	 */
	public Version checkin(String token, String docId, InputStream is, long size, String comment, String userId, int increment)
			throws FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, AccessDeniedException,
			RepositoryException, PathNotFoundException, LockException, VersionException, IOException, DatabaseException,
			AutomationException {
		log.debug("checkin({}, {}, {}, {}, {}, {})", new Object[]{token, docId, is, size, comment, userId});
		long begin = System.currentTimeMillis();
		Authentication auth, oldAuth = null;
		Version version;
		String docPath;
		String docUuid;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		if (PathUtils.isPath(docId)) {
			docPath = docId;
			docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
		} else {
			docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
			docUuid = docId;
		}

		String name = PathUtils.getName(docPath);
		int idx = name.lastIndexOf('.');
		String fileExtension = idx > 0 ? name.substring(idx) : ".tmp";
		File tmp = File.createTempFile("okm", fileExtension);

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

			if (Config.CLOUD_MAX_REPOSITORY_SIZE > 0) {
				StatsInfo si = RepositoryInfo.getDocumentsSizeByContext();

				if (si.getTotal() > Config.CLOUD_MAX_REPOSITORY_SIZE) {
					UserActivity.log(userId, "ERROR_REPOSITORY_SIZE_EXCEEDED", null, null, FormatUtil.formatSize(si.getTotal()));
					throw new FileSizeExceededException(FormatUtil.formatSize(si.getTotal()));
				}
			}

			if (Config.MAX_FILE_SIZE > 0 && size > Config.MAX_FILE_SIZE) {
				log.error("Uploaded file size: {} ({}), Max file size: {} ({})", new Object[]{FormatUtil.formatSize(size), size,
						FormatUtil.formatSize(Config.MAX_FILE_SIZE), Config.MAX_FILE_SIZE});
				UserActivity.log(userId, "ERROR_FILE_SIZE_EXCEEDED", null, docPath, FormatUtil.formatSize(size));
				throw new FileSizeExceededException(FormatUtil.formatSize(size));
			}

			// Manage temporary files
			byte[] buff = new byte[4 * 1024];
			FileOutputStream fos = new FileOutputStream(tmp);
			int read;

			while ((read = is.read(buff)) != -1) {
				fos.write(buff, 0, read);
			}

			fos.flush();
			fos.close();
			is.close();
			is = new FileInputStream(tmp);

			if (!Config.SYSTEM_ANTIVIR.equals("")) {
				String info = VirusDetection.detect(tmp);

				if (info != null) {
					UserActivity.log(userId, "ERROR_VIRUS_DETECTED", null, docPath, info);
					throw new VirusDetectedException(info);
				}
			}

			// AUTOMATION - PRE
			NodeDocument docNode = NodeDocumentDAO.getInstance().findByPk(docUuid);
			Map<String, Object> env = new HashMap<String, Object>();
			env.put(AutomationUtils.DOCUMENT_NODE, docNode);
			env.put(AutomationUtils.PARENT_UUID, docNode.getParent());
			AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_DOCUMENT_UPDATE, AutomationRule.AT_PRE, env);
			docNode = (NodeDocument) env.get(AutomationUtils.DOCUMENT_NODE);

			NodeDocumentVersion newDocVersion = NodeDocumentVersionDAO.getInstance().checkin(userId, comment, docUuid, is, size, increment);
			version = BaseModule.getProperties(newDocVersion);

			// AUTOMATION - POST
			AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_DOCUMENT_UPDATE, AutomationRule.AT_POST, env);

			// Add comment (as system user)
			String text = "New version " + version.getName() + " by " + userId + ": " + comment;
			BaseNoteModule.create(docUuid, Config.SYSTEM_USER, text);

			// Update user items size
			if (Config.USER_ITEM_CACHE) {
				UserItemsManager.incSize(auth.getName(), size);
			}

			// Remove pdf & preview from cache
			CommonGeneralModule.cleanPreviewCache(docUuid);

			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(docNode, userId, "CHECKIN_DOCUMENT", comment);

			// Activity log
			UserActivity.log(auth.getName(), "CHECKIN_DOCUMENT", docUuid, docPath, size + ", " + comment);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(is);
			org.apache.commons.io.FileUtils.deleteQuietly(tmp);

			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(docPath, System.currentTimeMillis() - begin);
		log.trace("checkin.Time: {}", System.currentTimeMillis() - begin);
		log.debug("checkin: {}", version);
		return version;
	}

	@Override
	public LockInfo lock(String token, String docId) throws LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("lock({}, {})", token, docId);
		Authentication auth, oldAuth = null;
		String docPath;
		String docUuid;
		LockInfo lck;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			NodeDocument docNode = NodeDocumentDAO.getInstance().findByPk(docUuid);
			NodeLock nLock = NodeDocumentDAO.getInstance().lock(auth.getName(), docUuid);
			lck = BaseModule.getProperties(nLock, docPath);

			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(docNode, auth.getName(), "LOCK_DOCUMENT", null);

			// Activity log
			UserActivity.log(auth.getName(), "LOCK_DOCUMENT", docUuid, docPath, lck.getToken());
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("lock: {}", lck);
		return lck;
	}

	@Override
	public void unlock(String token, String docId) throws LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("unlock({}, {})", token, docId);
		unlockHelper(token, docId, false);
		log.debug("unlock: void");
	}

	@Override
	public void forceUnlock(String token, String docId) throws LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException, PrincipalAdapterException {
		log.debug("forceUnlock({}, {})", token, docId);

		if (PrincipalUtils.getRoles().contains(Config.DEFAULT_ADMIN_ROLE)) {
			unlockHelper(token, docId, true);
		} else {
			throw new AccessDeniedException("Only administrator use allowed");
		}

		log.debug("forceUnlock: void");
	}

	/**
	 * Implement unlock and forceUnlock features
	 */
	private void unlockHelper(String token, String docId, boolean force) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("unlock({}, {}, {})", new Object[]{token, docId, force});
		Authentication auth, oldAuth = null;
		String action = force ? "FORCE_UNLOCK_DOCUMENT" : "UNLOCK_DOCUMENT";
		String docPath;
		String docUuid;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			NodeDocument docNode = NodeDocumentDAO.getInstance().findByPk(docUuid);
			NodeDocumentDAO.getInstance().unlock(auth.getName(), docUuid, force);

			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(docNode, auth.getName(), action, null);

			// Activity log
			UserActivity.log(auth.getName(), action, docUuid, docPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("unlock: void");
	}

	@Override
	public boolean isLocked(String token, String docId) throws RepositoryException, AccessDeniedException, PathNotFoundException,
			DatabaseException {
		log.debug("isLocked({}, {})", token, docId);
		boolean locked;
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;
		@SuppressWarnings("unused")
		String docPath = null;
		String docUuid;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			locked = NodeDocumentDAO.getInstance().isLocked(docUuid);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("isLocked: {}", locked);
		return locked;
	}

	@Override
	public LockInfo getLockInfo(String token, String docId) throws RepositoryException, AccessDeniedException, PathNotFoundException,
			LockException, DatabaseException {
		log.debug("getLock({}, {})", token, docId);
		Authentication auth, oldAuth = null;
		String docPath;
		String docUuid;
		LockInfo lock;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			NodeLock nLock = NodeDocumentDAO.getInstance().getLock(docUuid);
			lock = BaseModule.getProperties(nLock, docPath);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getLock: {}", lock);
		return lock;
	}

	@Override
	public void purge(String token, String docId) throws LockException, AccessDeniedException, RepositoryException, PathNotFoundException,
			DatabaseException {
		log.debug("purge({}, {})", token, docId);
		Authentication auth = null, oldAuth = null;
		String docPath;
		String docUuid;

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

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			if (Config.REPOSITORY_PURGATORY_HOME != null && !Config.REPOSITORY_PURGATORY_HOME.isEmpty()) {
				File dateDir = FileUtils.createDateDir(Config.REPOSITORY_PURGATORY_HOME);
				File dstPath = new File(dateDir, PathUtils.getName(docPath));
				RepositoryExporter.exportDocument(null, docPath, dstPath.getPath(), true, false, null, null);
			}

			NodeDocumentDAO.getInstance().purge(docUuid);

			// Activity log - Already inside DAO
			// UserActivity.log(auth.getName(), "PURGE_DOCUMENT", docUuid, docPath, null);
		} catch (IOException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (ParseException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (NoSuchGroupException e) {
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
	public void move(String token, String docId, String dstId) throws PathNotFoundException, ItemExistsException, AccessDeniedException,
			LockException, RepositoryException, DatabaseException, ExtensionException, AutomationException {
		log.debug("move({}, {}, {})", new Object[]{token, docId, dstId});
		Authentication auth, oldAuth = null;
		String docPath;
		String docUuid;
		String dstPath;
		String dstUuid;

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

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
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

			NodeDocument docNode = (NodeDocument) BaseModule.resolveNodeById(docUuid);			
			
			// AUTOMATION - PRE
			Map<String, Object> env = new HashMap<>();
			env.put(AutomationUtils.DOCUMENT_NODE, docNode);
			env.put(AutomationUtils.DOCUMENT_UUID, docNode.getUuid());
			env.put(AutomationUtils.PARENT_UUID, docNode.getParent());
			env.put(AutomationUtils.FOLDER_UUID, dstUuid);
			AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_DOCUMENT_MOVE, AutomationRule.AT_PRE, env);

			NodeDocumentDAO.getInstance().move(docUuid, dstUuid);

			// AUTOMATION - POST
			AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_DOCUMENT_MOVE, AutomationRule.AT_POST, env);

			// Activity log
			UserActivity.log(auth.getName(), "MOVE_DOCUMENT", docUuid, docPath, dstPath);
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
	public void copy(String token, String docPath, String dstPath) throws ItemExistsException, PathNotFoundException,
			AccessDeniedException, RepositoryException, IOException, AutomationException, DatabaseException, UserQuotaExceededException {
		log.debug("copy({}, {}, {})", new Object[]{token, docPath, dstPath});
		extendedCopy(token, docPath, dstPath, PathUtils.getName(docPath), new ExtendedAttributes());
	}

	@Override
	public void extendedCopy(String token, String docId, String dstId, String docName, ExtendedAttributes extAttr)
			throws ItemExistsException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException,
			AutomationException, DatabaseException, UserQuotaExceededException {
		log.debug("extendedCopy({}, {}, {}, {}, {})", new Object[]{token, docId, dstId, docName, extAttr});
		Authentication auth, oldAuth = null;
		String docPath;
		String docUuid;
		String dstPath;
		String dstUuid;

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

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			if (PathUtils.isPath(dstId)) {
				dstPath = dstId;
				dstUuid = NodeBaseDAO.getInstance().getUuidFromPath(dstId);
			} else {
				dstPath = NodeBaseDAO.getInstance().getPathFromUuid(dstId);
				dstUuid = dstId;
			}

			if (docName == null) {
				docName = PathUtils.getName(docPath);
			} else {
				// Escape dangerous chars in name
				docName = PathUtils.escape(docName);
			}

			NodeDocument srcDocNode = NodeDocumentDAO.getInstance().findByPk(docUuid, extAttr.isPropertyGroups());
			NodeFolder dstFldNode = NodeFolderDAO.getInstance().findByPk(dstUuid);
			NodeDocument newDocNode = BaseDocumentModule.copy(auth.getName(), srcDocNode, dstPath, dstFldNode, docName, extAttr);

			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(dstFldNode, auth.getName(), "COPY_DOCUMENT", null);

			// Activity log
			UserActivity.log(auth.getName(), "COPY_DOCUMENT", newDocNode.getUuid(), docPath, dstPath);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	@Override
	public void restoreVersion(String token, String docId, String versionId) throws PathNotFoundException, AccessDeniedException,
			LockException, RepositoryException, DatabaseException {
		log.debug("restoreVersion({}, {}, {})", new Object[]{token, docId, versionId});
		Authentication auth, oldAuth = null;
		String docPath;
		String docUuid;

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

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			NodeDocumentVersionDAO.getInstance().restoreVersion(docUuid, versionId);

			// Remove pdf & preview from cache
			CommonGeneralModule.cleanPreviewCache(docUuid);

			// Activity log
			UserActivity.log(auth.getName(), "RESTORE_DOCUMENT_VERSION", docUuid, docPath, versionId);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("restoreVersion: void");
	}

	@Override
	public void purgeVersionHistory(String token, String docId) throws AccessDeniedException, PathNotFoundException, LockException,
			RepositoryException, DatabaseException {
		log.debug("purgeVersionHistory({}, {})", token, docId);
		Authentication auth = null, oldAuth = null;
		String docPath = null;
		String docUuid = null;

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

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			NodeDocumentVersionDAO.getInstance().purgeVersionHistory(docUuid);

			// Activity log
			UserActivity.log(auth.getName(), "PURGE_DOCUMENT_VERSION_HISTORY", docUuid, docPath, null);
		} catch (IOException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("purgeVersionHistory: void");
	}

	@Override
	public List<Version> getVersionHistory(String token, String docId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getVersionHistory({}, {})", token, docId);
		List<Version> history = new ArrayList<Version>();
		Authentication auth = null, oldAuth = null;
		String docPath = null;
		String docUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			List<NodeDocumentVersion> docVersions = NodeDocumentVersionDAO.getInstance().findByParent(docUuid);

			for (NodeDocumentVersion nDocVersion : docVersions) {
				history.add(BaseModule.getProperties(nDocVersion));
			}

			// Activity log
			UserActivity.log(auth.getName(), "GET_DOCUMENT_VERSION_HISTORY", docUuid, docPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getVersionHistory: {}", history);
		return history;
	}

	@Override
	@SuppressWarnings("unused")
	public long getVersionHistorySize(String token, String docId) throws RepositoryException, AccessDeniedException, PathNotFoundException,
			DatabaseException {
		log.debug("getVersionHistorySize({}, {})", token, docId);
		long versionHistorySize = 0;
		Authentication auth = null, oldAuth = null;
		String docPath = null;
		String docUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			List<NodeDocumentVersion> docVersions = NodeDocumentVersionDAO.getInstance().findByParent(docUuid);

			for (NodeDocumentVersion nDocVersion : docVersions) {
				versionHistorySize += nDocVersion.getSize();
			}
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getVersionHistorySize: {}", versionHistorySize);
		return versionHistorySize;
	}

	@Override
	public boolean isValid(String token, String docId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("isValid({}, {})", token, docId);
		boolean valid = true;
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;
		String docUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(docId)) {
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docUuid = docId;
			}

			try {
				NodeDocumentDAO.getInstance().findByPk(docUuid);
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
	
	/*
	 * ========================
	 * LiveEdit methods
	 * =========================
	 */

	/**
	 * Create temporal file and set content.
	 */
	public void liveEditSetContent(String token, String docId, InputStream is) throws FileSizeExceededException,
			UserQuotaExceededException, VirusDetectedException, AccessDeniedException, RepositoryException, PathNotFoundException,
			LockException, VersionException, IOException, DatabaseException {
		log.debug("liveEditSetContent({}, {})", new Object[]{docId, is});
		Authentication auth = null, oldAuth = null;
		int size = is.available();
		String docPath = null;
		String docUuid = null;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		if (PathUtils.isPath(docId)) {
			docPath = docId;
			docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
		} else {
			docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
			docUuid = docId;
		}

		String name = PathUtils.getName(docPath);
		int idx = name.lastIndexOf('.');
		String fileExtension = idx > 0 ? name.substring(idx) : ".tmp";
		File tmp = File.createTempFile("okm", fileExtension);

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (Config.CLOUD_MAX_REPOSITORY_SIZE > 0) {
				StatsInfo si = RepositoryInfo.getDocumentsSizeByContext();

				if (si.getTotal() > Config.CLOUD_MAX_REPOSITORY_SIZE) {
					UserActivity.log(auth.getName(), "ERROR_REPOSITORY_SIZE_EXCEEDED", null, null, FormatUtil.formatSize(si.getTotal()));
					throw new FileSizeExceededException(FormatUtil.formatSize(si.getTotal()));
				}
			}

			if (Config.MAX_FILE_SIZE > 0 && size > Config.MAX_FILE_SIZE) {
				log.error("Uploaded file size: {} ({}), Max file size: {} ({})", new Object[]{FormatUtil.formatSize(size), size,
						FormatUtil.formatSize(Config.MAX_FILE_SIZE), Config.MAX_FILE_SIZE});
				UserActivity.log(auth.getName(), "ERROR_FILE_SIZE_EXCEEDED", null, docPath, FormatUtil.formatSize(size));
				throw new FileSizeExceededException(FormatUtil.formatSize(size));
			}

			// Manage temporary files
			byte[] buff = new byte[4 * 1024];
			FileOutputStream fos = new FileOutputStream(tmp);
			int read;

			while ((read = is.read(buff)) != -1) {
				fos.write(buff, 0, read);
			}

			fos.flush();
			fos.close();
			is.close();
			is = new FileInputStream(tmp);

			if (!Config.SYSTEM_ANTIVIR.equals("")) {
				String info = VirusDetection.detect(tmp);

				if (info != null) {
					UserActivity.log(auth.getName(), "ERROR_VIRUS_DETECTED", null, docPath, info);
					throw new VirusDetectedException(info);
				}
			}

			NodeDocumentVersionDAO.getInstance().liveEditSetContent(docUuid, is, size);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(is);
			org.apache.commons.io.FileUtils.deleteQuietly(tmp);

			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("liveEditSetContent: void");
	}

	/**
	 * New version and delete temporal file.
	 */
	public Version liveEditCheckin(String token, String docId, String comment, int increment) throws FileSizeExceededException,
			UserQuotaExceededException, VirusDetectedException, AccessDeniedException, RepositoryException, PathNotFoundException,
			LockException, VersionException, IOException, DatabaseException, AutomationException {
		log.debug("liveEditCheckin({}, {}, {})", new Object[]{token, docId, comment});
		Version version = new Version();
		Authentication auth = null, oldAuth = null;
		String docPath = null;
		String docUuid = null;

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

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			NodeDocument docNode = NodeDocumentDAO.getInstance().findByPk(docUuid);

			// AUTOMATION - PRE
			Map<String, Object> env = new HashMap<>();
			env.put(AutomationUtils.DOCUMENT_NODE, docNode);
			env.put(AutomationUtils.PARENT_UUID, docNode.getParent());
			AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_DOCUMENT_UPDATE, AutomationRule.AT_PRE, env);
			docNode = (NodeDocument) env.get(AutomationUtils.DOCUMENT_NODE);
			
			NodeDocumentVersion newDocVersion = NodeDocumentVersionDAO.getInstance().liveEditCheckin(auth.getName(), comment, increment,
					docUuid);
			version = BaseModule.getProperties(newDocVersion);

			// AUTOMATION - POST
			AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_DOCUMENT_UPDATE, AutomationRule.AT_POST, env);
			
			// Add comment (as system user)
			String text = "New version " + version.getName() + " by " + auth.getName() + ": " + comment;
			BaseNoteModule.create(docUuid, Config.SYSTEM_USER, text);

			// Update user items size
			if (Config.USER_ITEM_CACHE) {
				UserItemsManager.incSize(auth.getName(), newDocVersion.getSize());
			}

			// Remove pdf & preview from cache
			CommonGeneralModule.cleanPreviewCache(docUuid);

			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(docNode, auth.getName(), "CHECKIN_DOCUMENT", comment);

			// Activity log
			UserActivity.log(auth.getName(), "CHECKIN_DOCUMENT", docUuid, docPath, newDocVersion.getSize() + ", " + comment);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("tempCheckin: {}", version);
		return version;
	}

	/**
	 * Cancel checkout and delete temporal file.
	 */
	public void liveEditCancelCheckout(String token, String docId) throws LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("liveEditCancelCheckout({}, {})", token, docId);
		liveEditCancelCheckoutHelper(token, docId, false);
		log.debug("liveEditCancelCheckout: void");
	}

	/**
	 * Cancel checkout and delete temporal file.
	 */
	public void liveEditForceCancelCheckout(String token, String docId) throws LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("liveEditForceCancelCheckout({}, {})", token, docId);

		if (PrincipalUtils.getRoles().contains(Config.DEFAULT_ADMIN_ROLE)) {
			liveEditCancelCheckoutHelper(token, docId, true);
		} else {
			throw new AccessDeniedException("Only administrator use allowed");
		}

		log.debug("liveEditForceCancelCheckout: void");
	}

	private void liveEditCancelCheckoutHelper(String token, String docId, boolean force) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("liveEditCancelCheckoutHelper({}, {}, {})", new Object[]{token, docId, force});
		long begin = System.currentTimeMillis();
		Authentication auth = null, oldAuth = null;
		String action = force ? "FORCE_CANCEL_DOCUMENT_CHECKOUT" : "CANCEL_DOCUMENT_CHECKOUT";
		String docPath = null;
		String docUuid = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (PathUtils.isPath(docId)) {
				docPath = docId;
				docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docId);
			} else {
				docPath = NodeBaseDAO.getInstance().getPathFromUuid(docId);
				docUuid = docId;
			}

			NodeDocument docNode = NodeDocumentDAO.getInstance().findByPk(docUuid);
			NodeDocumentDAO.getInstance().liveEditCancelCheckout(auth.getName(), docUuid, force);

			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(docNode, auth.getName(), action, null);

			// Activity log
			UserActivity.log(auth.getName(), action, docUuid, docPath, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(docPath, System.currentTimeMillis() - begin);
		log.trace("liveEditCancelCheckoutHelper.Time: {}", System.currentTimeMillis() - begin);
		log.debug("liveEditCancelCheckoutHelper: void");
	}
}
