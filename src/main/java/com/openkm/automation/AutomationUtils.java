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

package com.openkm.automation;

import java.io.File;
import java.util.Map;

import com.openkm.api.OKMRepository;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.bean.NodeBase;
import com.openkm.dao.bean.NodeDocument;
import com.openkm.dao.bean.NodeFolder;
import com.openkm.dao.bean.NodeMail;
import com.openkm.util.PathUtils;

/**
 * AutomationUtils
 *
 * @author jllort
 *
 */
public class AutomationUtils {
	public static final String UPLOAD_RESPONSE = "response";
	public static final String PARENT_UUID = "parentUuid";
	public static final String PARENT_PATH = "parentPath";
	public static final String PARENT_NODE = "parentNode";
	public static final String TEXT_EXTRACTED = "textExtracted";
	public static final String DOCUMENT_NODE = "documentNode";
	public static final String DOCUMENT_UUID = "documentUuid";
	public static final String DOCUMENT_FILE = "documentFile";
	public static final String DOCUMENT_NAME = "documentName";
	public static final String DOCUMENT_KEYWORDS = "documentKeywords";
	public static final String DOCUMENT_MIME_TYPE = "documentMimeType";
	public static final String FOLDER_NODE = "folderNode";
	public static final String FOLDER_UUID = "folderUuid";
	public static final String FOLDER_NAME = "folderName";
	public static final String MAIL_NODE = "mailNode";
	public static final String MAIL_UUID = "mailUuid";
	public static final String MAIL_NAME = "mailName";
	public static final String MAIL_KEYWORDS = "mailKeywords";
	public static final String MAIL_MIME_TYPE = "mailMimeType";
	public static final String NODE_UUID = "nodeUuid";
	public static final String NODE_PATH = "nodePath";
	public static final String PROPERTY_GROUP_NAME = "propGroupName";
	public static final String PROPERTY_GROUP_PROPERTIES = "propGroupProperties";
	public static final String EVENT = "event";
	public static final String USER = "user";
	
	/**
	 * getPath
	 */
	public static String getPath(Map<String, Object> env) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		NodeDocument docNode = (NodeDocument) env.get(DOCUMENT_NODE);
		NodeFolder fldNode = (NodeFolder) env.get(FOLDER_NODE);
		NodeMail mailNode = (NodeMail) env.get(MAIL_NODE);		
		String uuid = null;
		String path = null;

		if (docNode != null) {
			uuid = docNode.getUuid();
		} else if (fldNode != null) {
			uuid = fldNode.getUuid();
		} else if (mailNode != null) {
			uuid = mailNode.getUuid();
		} 

		if (uuid != null) {
			path = OKMRepository.getInstance().getNodePath(null, uuid);
		}

		return path;
	}
	
	/**
	 * getUuid
	 */
	public static String getUuid(Map<String, Object> env) {
		NodeDocument docNode = (NodeDocument) env.get(DOCUMENT_NODE);
		NodeFolder fldNode = (NodeFolder) env.get(FOLDER_NODE);
		NodeMail mailNode = (NodeMail) env.get(MAIL_NODE);
		String docUuid = (String) env.get(DOCUMENT_UUID);
		String folderUuid = (String) env.get(FOLDER_UUID);
		String mailUuid = (String) env.get(MAIL_UUID);
		String nodeUuid = (String) env.get(NODE_UUID);
		String uuid = null;

		if (docNode != null) {
			uuid = docNode.getUuid();
		} else if (fldNode != null) {
			uuid = fldNode.getUuid();
		} else if (mailNode != null) {
			uuid = mailNode.getUuid();
		} else if (docUuid != null) {
			uuid = docUuid;
		} else if (folderUuid != null) {
			uuid = folderUuid;
		} else if (mailUuid != null) {
			uuid = mailUuid;
		} else if (nodeUuid != null) {
			uuid = nodeUuid;
		}

		return uuid;
	}

	/**
	 * getNode
	 */
	public static NodeBase getNode(Map<String, Object> env) {
		NodeDocument docNode = (NodeDocument) env.get(DOCUMENT_NODE);
		NodeFolder fldNode = (NodeFolder) env.get(FOLDER_NODE);
		NodeMail mailNode = (NodeMail) env.get(MAIL_NODE);

		if (docNode != null) {
			return docNode;
		} else if (fldNode != null) {
			return fldNode;
		} else if (mailNode != null) {
			return mailNode;
		}

		return null;
	}

	/**
	 * getFile
	 */
	public static File getFile(Map<String, Object> env) {
		return (File) env.get(DOCUMENT_FILE);
	}

	/**
	 * getParentUuid
	 */
	public static String getParentUuid(Map<String, Object> env) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		if (env.containsKey(PARENT_UUID)) {
			return (String) env.get(PARENT_UUID);
		} else {
			// Trying to search parent uuid from parent path variables
			String parentPath = getParentPath(env);
			if (parentPath != null) {
				return OKMRepository.getInstance().getNodeUuid(null, parentPath);
			} else {
				return null;
			}
		}
	}
	
	/**
	 * getParentPath
	 */
	public static String getParentPath(Map<String, Object> env) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		if (env.containsKey(PARENT_PATH)) {
			return (String) env.get(PARENT_PATH);
		} else if (env.containsKey(DOCUMENT_NODE)) {
			NodeDocument docNode = (NodeDocument) env.get(DOCUMENT_NODE);
			return OKMRepository.getInstance().getNodePath(null, docNode.getParent());
		} else if (env.containsKey(FOLDER_NODE)) {
			NodeFolder fldNode = (NodeFolder) env.get(FOLDER_NODE);
			return OKMRepository.getInstance().getNodePath(null, fldNode.getParent());
		} else if (env.containsKey(MAIL_NODE)) {
			NodeMail mailNode = (NodeMail) env.get(MAIL_NODE);
			return OKMRepository.getInstance().getNodePath(null, mailNode.getParent());
		} else if (env.containsKey(NODE_PATH)) {
			return PathUtils.getParent((String) env.get(NODE_PATH));
		}
		return null;
	}

	/**
	 * getUser
	 */
	public static String getUser(Map<String, Object> env) {
		if (env.containsKey(USER)) {
			return (String) env.get(USER);
		}
		return "";
	}

	/**
	 * getTextExtracted
	 */
	public static String getTextExtracted(Map<String, Object> env) {
		return (String) env.get(TEXT_EXTRACTED);
	}

	/**
	 * getString
	 */
	public static String getString(int index, Object... params) {
		return (String) params[index];
	}

	/**
	 * getInterger
	 */
	public static Integer getInteger(int index, Object... params) {
		return (Integer) params[index];
	}
	
	/**
	 * getLong
	 */
	public static Long getLong(int index, Object... params) {
		return (Long) params[index];
	}

	/**
	 * getBoolean
	 */
	public static Boolean getBoolean(int index, Object... params) {
		return (Boolean) params[index];
	}
}