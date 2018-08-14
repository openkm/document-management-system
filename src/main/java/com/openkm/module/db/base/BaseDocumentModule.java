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

package com.openkm.module.db.base;

import com.openkm.automation.AutomationException;
import com.openkm.automation.AutomationManager;
import com.openkm.automation.AutomationUtils;
import com.openkm.bean.*;
import com.openkm.bean.workflow.ProcessDefinition;
import com.openkm.bean.workflow.ProcessInstance;
import com.openkm.cache.UserItemsManager;
import com.openkm.core.*;
import com.openkm.core.Config;
import com.openkm.dao.*;
import com.openkm.dao.bean.*;
import com.openkm.dao.bean.cache.UserItems;
import com.openkm.extension.dao.WikiPageDAO;
import com.openkm.extension.dao.bean.WikiPage;
import com.openkm.module.common.CommonWorkflowModule;
import com.openkm.util.CloneUtils;
import com.openkm.util.DocConverter;
import com.openkm.util.SystemProfiling;
import com.openkm.util.UserActivity;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class BaseDocumentModule {
	private static Logger log = LoggerFactory.getLogger(BaseDocumentModule.class);

	/**
	 * Create a new document
	 */
	@SuppressWarnings("unchecked")
	public static NodeDocument create(String user, String parentPath, NodeBase parentNode, String name, String title, Calendar created,
			String mimeType, InputStream is, long size, Set<String> keywords, Set<String> categories, Set<NodeProperty> propertyGroups,
			List<NodeNote> notes, WikiPage wiki, Ref<FileUploadResponse> fuResponse) throws PathNotFoundException, AccessDeniedException,
			ItemExistsException, UserQuotaExceededException, AutomationException, DatabaseException, IOException {

		// Check user quota
		UserConfig uc = UserConfigDAO.findByPk(user);
		ProfileMisc pm = uc.getProfile().getPrfMisc();

		// System user don't care quotas
		if (!Config.SYSTEM_USER.equals(user) && pm.getUserQuota() > 0) {
			long currentQuota = 0;

			if (Config.USER_ITEM_CACHE) {
				UserItems ui = UserItemsManager.get(user);
				currentQuota = ui.getSize();
			} else {
				// Other implementation
			}

			if (currentQuota + size > pm.getUserQuota() * 1024 * 1024) {
				throw new UserQuotaExceededException(Long.toString(currentQuota + size));
			}
		}

		// AUTOMATION - PRE
		Map<String, Object> env = new HashMap<>();
		env.put(AutomationUtils.PARENT_UUID, parentNode.getUuid());
		env.put(AutomationUtils.PARENT_PATH, parentPath);
		env.put(AutomationUtils.PARENT_NODE, parentNode);
		env.put(AutomationUtils.DOCUMENT_NAME, name);
		env.put(AutomationUtils.DOCUMENT_MIME_TYPE, mimeType);
		env.put(AutomationUtils.DOCUMENT_KEYWORDS, keywords);

		AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_DOCUMENT_CREATE, AutomationRule.AT_PRE, env);
		parentNode = (NodeBase) env.get(AutomationUtils.PARENT_NODE);
		name = (String) env.get(AutomationUtils.DOCUMENT_NAME);
		mimeType = (String) env.get(AutomationUtils.DOCUMENT_MIME_TYPE);
		keywords = (Set<String>) env.get(AutomationUtils.DOCUMENT_KEYWORDS);

		// Create and add a new document node
		NodeDocument documentNode = new NodeDocument();
		documentNode.setUuid(UUID.randomUUID().toString());
		documentNode.setContext(parentNode.getContext());
		documentNode.setParent(parentNode.getUuid());
		documentNode.setAuthor(user);
		documentNode.setName(name);
		documentNode.setTitle(title);
		documentNode.setMimeType(mimeType);
		documentNode.setCreated(created != null ? created : Calendar.getInstance());
		documentNode.setLastModified(documentNode.getCreated());

		if (Config.STORE_NODE_PATH) {
			documentNode.setPath(parentNode.getPath() + "/" + name);
		}

		// Extended Copy Attributes
		documentNode.setKeywords(CloneUtils.clone(keywords));
		documentNode.setCategories(CloneUtils.clone(categories));

		for (NodeProperty nProp : CloneUtils.clone(propertyGroups)) {
			nProp.setNode(documentNode);
			documentNode.getProperties().add(nProp);
		}

		// Get parent node auth info
		Map<String, Integer> userPerms = parentNode.getUserPermissions();
		Map<String, Integer> rolePerms = parentNode.getRolePermissions();

		// Always assign all grants to creator
		if (Config.USER_ASSIGN_DOCUMENT_CREATION) {
			int allGrants = Permission.ALL_GRANTS;

			if ((Config.SECURITY_EXTENDED_MASK & Permission.PROPERTY_GROUP) == Permission.PROPERTY_GROUP) {
				allGrants = allGrants | Permission.PROPERTY_GROUP;
			}

			if ((Config.SECURITY_EXTENDED_MASK & Permission.COMPACT_HISTORY) == Permission.COMPACT_HISTORY) {
				allGrants = allGrants | Permission.COMPACT_HISTORY;
			}

			if ((Config.SECURITY_EXTENDED_MASK & Permission.START_WORKFLOW) == Permission.START_WORKFLOW) {
				allGrants = allGrants | Permission.START_WORKFLOW;
			}

			if ((Config.SECURITY_EXTENDED_MASK & Permission.DOWNLOAD) == Permission.DOWNLOAD) {
				allGrants = allGrants | Permission.DOWNLOAD;
			}

			userPerms.put(user, allGrants);
		}

		// Set auth info
		// NOTICE: Pay attention to the need of cloning
		documentNode.setUserPermissions(CloneUtils.clone(userPerms));
		documentNode.setRolePermissions(CloneUtils.clone(rolePerms));

		NodeDocumentDAO.getInstance().create(documentNode, is, size);

		// Extended Copy Attributes
		for (NodeNote nNote : CloneUtils.clone(notes)) {
			BaseNoteModule.create(documentNode.getUuid(), nNote.getAuthor(), nNote.getText());
		}

		if (wiki != null) {
			wiki.setNode(documentNode.getUuid());
			wiki.setTitle(documentNode.getUuid());
			wiki.setDate(Calendar.getInstance());
			wiki.setLockUser(null);
			wiki.setDeleted(false);
			WikiPageDAO.create(wiki);
		}

		// AUTOMATION - POST
		env.put(AutomationUtils.DOCUMENT_NODE, documentNode);
		AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_DOCUMENT_CREATE, AutomationRule.AT_POST, env);

		// Update user items size
		if (Config.USER_ITEM_CACHE) {
			UserItemsManager.incSize(user, size);
			UserItemsManager.incDocuments(user, 1);
		}

		// Setting wizard properties
		fuResponse.set((FileUploadResponse) env.get(AutomationUtils.UPLOAD_RESPONSE));

		return documentNode;
	}

	/**
	 * Get folder properties
	 */
	public static Document getProperties(String user, NodeDocument nDocument) throws PathNotFoundException, DatabaseException {
		log.debug("getProperties({}, {})", user, nDocument);
		long begin = System.currentTimeMillis();
		Document doc = new Document();

		// Properties
		String docPath = NodeBaseDAO.getInstance().getPathFromUuid(nDocument.getUuid());
		doc.setPath(docPath);
		doc.setCreated(nDocument.getCreated());
		doc.setLastModified(nDocument.getLastModified());
		doc.setAuthor(nDocument.getAuthor());
		doc.setUuid(nDocument.getUuid());
		doc.setMimeType(nDocument.getMimeType());
		doc.setCheckedOut(nDocument.isCheckedOut());
		doc.setLocked(nDocument.isLocked());
		doc.setDescription(nDocument.getDescription());
		doc.setTitle(nDocument.getTitle());
		doc.setLanguage(nDocument.getLanguage());

		if (doc.isLocked()) {
			NodeLock nLock = nDocument.getLock();
			LockInfo lock = BaseModule.getProperties(nLock, docPath);
			doc.setLockInfo(lock);
		} else {
			doc.setLockInfo(null);
		}

		// Get current version
		NodeDocumentVersionDAO nodeDocVerDao = NodeDocumentVersionDAO.getInstance();
		NodeDocumentVersion currentVersion = nodeDocVerDao.findCurrentVersion(doc.getUuid());
		doc.setActualVersion(BaseModule.getProperties(currentVersion));

		// Get permissions
		BaseModule.setPermissions(nDocument, doc);

		// Document conversion capabilities
		DocConverter convert = DocConverter.getInstance();
		doc.setConvertibleToPdf(convert.convertibleToPdf(doc.getMimeType()));
		doc.setConvertibleToSwf(convert.convertibleToSwf(doc.getMimeType()));

		// Get user subscription & keywords
		doc.setSubscriptors(nDocument.getSubscriptors());
		doc.setSubscribed(nDocument.getSubscriptors().contains(user));
		doc.setKeywords(nDocument.getKeywords());

		// Get categories
		Set<Folder> categories = new HashSet<>();
		NodeFolderDAO nFldDao = NodeFolderDAO.getInstance();
		Set<NodeFolder> resolvedCategories = nFldDao.resolveCategories(nDocument.getCategories());

		for (NodeFolder nfldCat : resolvedCategories) {
			categories.add(BaseFolderModule.getProperties(user, nfldCat));
		}

		doc.setCategories(categories);

		// Get notes
		List<Note> notes = new ArrayList<>();
		List<NodeNote> nNoteList = NodeNoteDAO.getInstance().findByParent(nDocument.getUuid());

		for (NodeNote nNote : nNoteList) {
			notes.add(BaseNoteModule.getProperties(nNote, nNote.getUuid()));
		}

		doc.setNotes(notes);

		// Get crypto
		if (nDocument.isEncryption()) {
			String cipherName = nDocument.getCipherName();
			doc.setCipherName(cipherName);
		}

		// Signature
		doc.setSigned(nDocument.isSigned());

		SystemProfiling.log(user + ", " + nDocument, System.currentTimeMillis() - begin);
		log.trace("getProperties.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getProperties: {}", doc);
		return doc;
	}

	/**
	 * Retrieve the content input stream from a document
	 *
	 * @param user The user who make the content petition.
	 * @param docUuid UUID of the document to get the content.
	 * @param docPath Path of the document to get the content.
	 * @param checkout If the content is retrieved due to a checkout or not.
	 * @param extendedSecurity If the extended security DOWNLOAD permission should be evaluated.
	 *        This is used to enable the document preview.
	 */
	public static InputStream getContent(String user, String docUuid, String docPath, boolean checkout, boolean extendedSecurity)
			throws IOException, PathNotFoundException, AccessDeniedException, DatabaseException {
		InputStream is = NodeDocumentVersionDAO.getInstance().getCurrentContentByParent(docUuid, extendedSecurity);

		// Activity log
		UserActivity.log(user, (checkout ? "GET_DOCUMENT_CONTENT_CHECKOUT" : "GET_DOCUMENT_CONTENT"), docUuid, docPath,
				Integer.toString(is.available()));

		return is;
	}

	/**
	 * Check if a node is being used in a running workflow
	 */
	public static boolean hasWorkflowNodes(String docUuid) throws WorkflowException, PathNotFoundException, DatabaseException {
		Set<String> workflowNodes = new HashSet<>();

		for (ProcessDefinition procDef : CommonWorkflowModule.findAllProcessDefinitions()) {
			for (ProcessInstance procIns : CommonWorkflowModule.findProcessInstances(procDef.getId())) {
				if (procIns.getEnd() == null) {
					String uuid = (String) procIns.getVariables().get(Config.WORKFLOW_PROCESS_INSTANCE_VARIABLE_UUID);
					workflowNodes.add(uuid);
				}
			}
		}

		if (workflowNodes.contains(docUuid)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Is invoked from DbDocumentNode and DbFolderNode.
	 */
	public static NodeDocument copy(String user, NodeDocument srcDocNode, String dstPath, NodeBase dstNode, String docName,
			ExtendedAttributes extAttr) throws PathNotFoundException, AccessDeniedException, ItemExistsException,
			UserQuotaExceededException, AutomationException, DatabaseException, IOException {
		log.debug("copy({}, {}, {}, {}, {})", new Object[]{user, srcDocNode, dstNode, docName, extAttr});
		InputStream is = null;
		NodeDocument newDocument;

		try {
			Set<String> keywords = new HashSet<>();
			Set<String> categories = new HashSet<>();
			Set<NodeProperty> propertyGroups = new HashSet<>();
			List<NodeNote> notes = new ArrayList<>();
			WikiPage wiki = null;

			if (extAttr != null) {
				if (extAttr.isKeywords()) {
					keywords = srcDocNode.getKeywords();
				}

				if (extAttr.isCategories()) {
					categories = srcDocNode.getCategories();
				}

				if (extAttr.isPropertyGroups()) {
					propertyGroups = srcDocNode.getProperties();
				}

				if (extAttr.isNotes()) {
					notes = NodeNoteDAO.getInstance().findByParent(srcDocNode.getUuid());
				}

				if (extAttr.isWiki()) {
					wiki = WikiPageDAO.findLatestByNode(srcDocNode.getUuid());
				}
			}

			Ref<FileUploadResponse> fuResponse = new Ref<>(new FileUploadResponse());
			is = NodeDocumentVersionDAO.getInstance().getCurrentContentByParent(srcDocNode.getUuid(), true);
			NodeDocumentVersion nDocVer = NodeDocumentVersionDAO.getInstance().findCurrentVersion(srcDocNode.getUuid());
			newDocument = create(user, dstPath, dstNode, docName, srcDocNode.getTitle(), Calendar.getInstance(), srcDocNode.getMimeType(),
					is, nDocVer.getSize(), keywords, categories, propertyGroups, notes, wiki, fuResponse);
		} finally {
			IOUtils.closeQuietly(is);
		}

		log.debug("copy: {}", newDocument);
		return newDocument;
	}
}
