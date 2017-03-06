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

package com.openkm.util.impexp.metadata;

import com.google.gson.Gson;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Select;
import com.openkm.core.Config;
import com.openkm.core.*;
import com.openkm.dao.HibernateUtil;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.NodeDocumentVersionDAO;
import com.openkm.dao.bean.*;
import com.openkm.module.db.stuff.FsDataStore;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.ISO8601;
import com.openkm.util.PathUtils;
import com.openkm.util.SystemProfiling;
import com.openkm.util.UserActivity;
import com.openkm.vernum.VersionNumerationAdapter;
import com.openkm.vernum.VersionNumerationFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class DbMetadataAdapter extends MetadataAdapter {
	private static Logger log = LoggerFactory.getLogger(DbMetadataAdapter.class);

	public DbMetadataAdapter(String token) {
		super.token = token;
	}

	@Override
	public void importWithMetadata(DocumentMetadata dmd, InputStream is) throws ItemExistsException,
			RepositoryException, DatabaseException, IOException {
		log.debug("importWithMetadata({}, {})", new Object[]{dmd, is});
		long begin = System.currentTimeMillis();
		NodeDocumentVersion nDocVer = new NodeDocumentVersion();
		NodeDocument nDoc = new NodeDocument();
		Session session = null;
		Transaction tx = null;

		if (NodeBaseDAO.getInstance().itemPathExists(dmd.getPath())) {
			throw new ItemExistsException(dmd.getPath());
		}

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			String name = PathUtils.getName(dmd.getPath());
			String user = PrincipalUtils.getUser();
			String parentPath = PathUtils.getParent(dmd.getPath());
			String parentUuid = NodeBaseDAO.getInstance().getUuidFromPath(parentPath);
			NodeBase parentNode = NodeBaseDAO.getInstance().findByPk(parentUuid);

			nDoc.setParent(parentUuid);
			nDoc.setContext(parentNode.getContext());

			if (uuid && dmd.getUuid() != null && !dmd.getUuid().isEmpty()) {
				nDoc.setUuid(dmd.getUuid());
			} else {
				nDoc.setUuid(UUID.randomUUID().toString());
			}

			// Basic
			if (dmd.getAuthor() != null && !dmd.getAuthor().isEmpty()) {
				nDoc.setAuthor(dmd.getAuthor());
			} else {
				nDoc.setAuthor(user);
			}

			if (dmd.getName() != null && !dmd.getName().isEmpty()) {
				nDoc.setName(dmd.getName());
			} else {
				nDoc.setName(name);
			}

			if (dmd.getCreated() != null) {
				nDoc.setCreated(dmd.getCreated());
			} else {
				nDoc.setCreated(Calendar.getInstance());
			}

			if (dmd.getLastModified() != null) {
				nDoc.setLastModified(dmd.getLastModified());
			} else {
				nDoc.setLastModified(nDoc.getCreated());
			}

			if (dmd.getLanguage() != null) {
				nDoc.setLanguage(dmd.getLanguage());
			}

			if (dmd.getTitle() != null) {
				nDoc.setTitle(dmd.getTitle());
			}

			if (dmd.getDescription() != null) {
				nDoc.setDescription(dmd.getDescription());
			}

			// Document path
			if (Config.STORE_NODE_PATH) {
				nDoc.setPath(parentPath + "/" + nDoc.getName());
			}

			// Keywords & categories
			nDoc.setKeywords(dmd.getKeywords());
			nDoc.setCategories(getValues(dmd.getCategories()));

			// Notification
			if (!dmd.getSubscriptors().isEmpty()) {
				nDoc.setSubscriptors(dmd.getSubscriptors());
			}

			// Encryption
			if (dmd.getCipherName() != null && !dmd.getCipherName().equals("")) {
				nDoc.setCipherName(dmd.getCipherName());
				nDoc.setEncryption(true);
			}

			// Property Groups
			importPropertyGroups(nDoc, dmd.getPropertyGroups());

			// Security
			if (dmd.getGrantedUsers() != null && !dmd.getGrantedUsers().isEmpty()) {
				nDoc.setUserPermissions(dmd.getGrantedUsers());
			}

			if (dmd.getGrantedRoles() != null && !dmd.getGrantedRoles().isEmpty()) {
				nDoc.setRolePermissions(dmd.getGrantedRoles());
			}

			// Content / Version
			VersionMetadata vmd = dmd.getVersion();

			if (vmd == null) {
				vmd = new VersionMetadata();
			}

			VersionNumerationAdapter verNumAdapter = VersionNumerationFactory.getVersionNumerationAdapter();
			nDocVer.setParent(nDoc.getUuid());
			nDocVer.setUuid(UUID.randomUUID().toString());
			nDocVer.setName(verNumAdapter.getInitialVersionNumber());
			nDocVer.setCurrent(true);

			if (vmd.getSize() > 0) {
				nDocVer.setSize(vmd.getSize());
			} else {
				nDocVer.setSize(is.available());
			}

			if (vmd.getAuthor() != null && !vmd.getAuthor().equals("")) {
				nDocVer.setAuthor(vmd.getAuthor());
			} else {
				nDocVer.setAuthor(user);
			}

			if (vmd.getComment() != null && !vmd.getComment().equals("")) {
				nDocVer.setComment(vmd.getComment());
			} else {
				nDocVer.setComment("Imported by " + user);
			}

			if (vmd.getCreated() != null) {
				nDocVer.setCreated(vmd.getCreated());
			} else {
				nDocVer.setCreated(Calendar.getInstance());
			}

			if (vmd.getMimeType() != null && !vmd.getMimeType().equals("")) {
				nDocVer.setMimeType(vmd.getMimeType());
				nDoc.setMimeType(vmd.getMimeType());
			} else {
				String mimeType = MimeTypeConfig.mimeTypes.getContentType(name.toLowerCase());
				nDocVer.setMimeType(mimeType);
				nDoc.setMimeType(mimeType);
			}

			// Persist file in datastore
			FsDataStore.persist(nDocVer, is);

			// Persist
			session.save(nDoc);
			session.save(nDocVer);

			// Notes
			if (!dmd.getNotes().isEmpty()) {
				for (NoteMetadata nmd : dmd.getNotes()) {
					NodeNote nNote = new NodeNote();
					nNote.setUuid(UUID.randomUUID().toString());
					nNote.setParent(nDoc.getUuid());
					nNote.setAuthor(nmd.getUser());
					nNote.setCreated(nmd.getDate());
					nNote.setText(nmd.getText());
					session.save(nNote);
				}
			}

			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);

			// What happen when create fails? This datastore file should be deleted!
			FsDataStore.delete(nDocVer.getUuid());

			throw new DatabaseException(e.getMessage(), e);
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw new RepositoryException("PathNotFound: " + e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		// Activity log
		UserActivity.log(PrincipalUtils.getUser(), "CREATE_DOCUMENT", nDoc.getUuid(), dmd.getPath(), "Imported with metadata");

		SystemProfiling.log(String.valueOf(dmd), System.currentTimeMillis() - begin);
		log.trace("importWithMetadata.Time: {}", System.currentTimeMillis() - begin);
	}

	@Override
	public void importWithMetadata(String parentPath, VersionMetadata vmd, InputStream is) throws ItemExistsException,
			RepositoryException, DatabaseException, IOException {
		log.debug("importWithMetadata({}, {})", vmd, is);
		long begin = System.currentTimeMillis();
		VersionNumerationAdapter verNumAdapter = VersionNumerationFactory.getVersionNumerationAdapter();
		NodeDocumentVersion nDocVer = new NodeDocumentVersion();
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			String user = PrincipalUtils.getUser();
			String parentUuid = NodeBaseDAO.getInstance().getUuidFromPath(session, parentPath);
			NodeDocument parentNode = (NodeDocument) session.load(NodeDocument.class, parentUuid);

			nDocVer.setParent(parentUuid);
			nDocVer.setUuid(UUID.randomUUID().toString());
			nDocVer.setCurrent(true);

			NodeDocumentVersion prevDocVer = NodeDocumentVersionDAO.getInstance().findCurrentVersion(session, parentUuid);
			prevDocVer.setCurrent(false);

			// Basic
			if (vmd.getAuthor() != null && !vmd.getAuthor().equals("")) {
				nDocVer.setAuthor(vmd.getAuthor());
			} else {
				nDocVer.setAuthor(user);
			}

			if (vmd.getComment() != null && !vmd.getComment().equals("")) {
				nDocVer.setComment(vmd.getComment());
			} else {
				nDocVer.setComment("Imported by " + user);
			}

			if (vmd.getCreated() != null) {
				nDocVer.setCreated(vmd.getCreated());
			} else {
				nDocVer.setCreated(Calendar.getInstance());
			}

			if (vmd.getSize() > 0) {
				nDocVer.setSize(vmd.getSize());
			} else {
				nDocVer.setSize(is.available());
			}

			if (vmd.getMimeType() != null && !vmd.getMimeType().equals("")) {
				nDocVer.setMimeType(vmd.getMimeType());
			} else {
				nDocVer.setMimeType(parentNode.getMimeType());
			}

			if (vmd.getName() != null && !vmd.getName().equals("")) {
				nDocVer.setName(vmd.getName());
			} else {
				nDocVer.setName(verNumAdapter.getNextVersionNumber(session, parentNode, nDocVer, 0));
			}

			// Persist file in datastore
			FsDataStore.persist(nDocVer, is);

			// Persist
			session.save(nDocVer);
			session.save(prevDocVer);

			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);

			// What happen when create fails? This datastore file should be deleted!
			FsDataStore.delete(nDocVer.getUuid());

			throw new DatabaseException(e.getMessage(), e);
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw new RepositoryException("PathNotFound: " + e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		SystemProfiling.log(String.valueOf(vmd), System.currentTimeMillis() - begin);
		log.trace("importWithMetadata.Time: {}", System.currentTimeMillis() - begin);
	}

	@Override
	public void importWithMetadata(FolderMetadata fmd) throws ItemExistsException, RepositoryException,
			DatabaseException {
		log.debug("importWithMetadata({})", fmd);
		long begin = System.currentTimeMillis();
		NodeFolder nFld = new NodeFolder();
		Session session = null;
		Transaction tx = null;

		if (NodeBaseDAO.getInstance().itemPathExists(fmd.getPath())) {
			throw new ItemExistsException(fmd.getPath());
		}

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			String name = PathUtils.getName(fmd.getPath());
			String user = PrincipalUtils.getUser();
			String parentPath = PathUtils.getParent(fmd.getPath());
			String parentUuid = NodeBaseDAO.getInstance().getUuidFromPath(parentPath);
			NodeBase parentNode = NodeBaseDAO.getInstance().findByPk(parentUuid);

			nFld.setParent(parentUuid);
			nFld.setContext(parentNode.getContext());

			if (uuid && fmd.getUuid() != null && !fmd.getUuid().equals("")) {
				nFld.setUuid(fmd.getUuid());
			} else {
				nFld.setUuid(UUID.randomUUID().toString());
			}

			// Basic
			if (fmd.getAuthor() != null && !fmd.getAuthor().equals("")) {
				nFld.setAuthor(fmd.getAuthor());
			} else {
				nFld.setAuthor(user);
			}

			if (fmd.getName() != null && !fmd.getName().equals("")) {
				nFld.setName(fmd.getName());
			} else {
				nFld.setName(name);
			}

			if (fmd.getCreated() != null) {
				nFld.setCreated(fmd.getCreated());
			} else {
				nFld.setCreated(Calendar.getInstance());
			}

			if (fmd.getDescription() != null) {
				nFld.setDescription(fmd.getDescription());
			}

			// Folder path
			if (Config.STORE_NODE_PATH) {
				nFld.setPath(parentPath + "/" + nFld.getName());
			}

			// Keywords & categories
			nFld.setKeywords(fmd.getKeywords());
			nFld.setCategories(getValues(fmd.getCategories()));

			// Notification
			if (!fmd.getSubscriptors().isEmpty()) {
				nFld.setSubscriptors(fmd.getSubscriptors());
			}

			// Property Groups
			importPropertyGroups(nFld, fmd.getPropertyGroups());

			// Security
			if (fmd.getGrantedUsers() != null && !fmd.getGrantedUsers().isEmpty()) {
				nFld.setUserPermissions(fmd.getGrantedUsers());
			}

			if (fmd.getGrantedRoles() != null && !fmd.getGrantedRoles().isEmpty()) {
				nFld.setRolePermissions(fmd.getGrantedRoles());
			}

			// Persist
			session.save(nFld);

			// Notes
			if (!fmd.getNotes().isEmpty()) {
				for (NoteMetadata nmd : fmd.getNotes()) {
					NodeNote nNote = new NodeNote();
					nNote.setUuid(UUID.randomUUID().toString());
					nNote.setParent(nFld.getUuid());
					nNote.setAuthor(nmd.getUser());
					nNote.setCreated(nmd.getDate());
					nNote.setText(nmd.getText());
					session.save(nNote);
				}
			}

			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw new RepositoryException("PathNotFound: " + e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		// Activity log
		UserActivity.log(PrincipalUtils.getUser(), "CREATE_FOLDER", nFld.getUuid(), fmd.getPath(), "Imported with metadata");

		SystemProfiling.log(String.valueOf(fmd), System.currentTimeMillis() - begin);
		log.trace("importWithMetadata.Time: {}", System.currentTimeMillis() - begin);
	}

	@Override
	public void importWithMetadata(MailMetadata mmd) throws ItemExistsException, RepositoryException, DatabaseException {
		log.debug("importWithMetadata({})", new Object[]{mmd});
		long begin = System.currentTimeMillis();
		NodeMail nMail = new NodeMail();
		Session session = null;
		Transaction tx = null;

		if (NodeBaseDAO.getInstance().itemPathExists(mmd.getPath())) {
			throw new ItemExistsException(mmd.getPath());
		}

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			String name = PathUtils.getName(mmd.getPath());
			String user = PrincipalUtils.getUser();
			String parentPath = PathUtils.getParent(mmd.getPath());
			String parentUuid = NodeBaseDAO.getInstance().getUuidFromPath(parentPath);
			NodeBase parentNode = NodeBaseDAO.getInstance().findByPk(parentUuid);

			nMail.setParent(parentUuid);
			nMail.setContext(parentNode.getContext());

			if (uuid && mmd.getUuid() != null && !mmd.getUuid().equals("")) {
				nMail.setUuid(mmd.getUuid());
			} else {
				nMail.setUuid(UUID.randomUUID().toString());
			}

			// Basic
			if (mmd.getAuthor() != null && !mmd.getAuthor().equals("")) {
				nMail.setAuthor(mmd.getAuthor());
			} else {
				nMail.setAuthor(user);
			}

			if (mmd.getName() != null && !mmd.getName().equals("")) {
				nMail.setName(mmd.getName());
			} else {
				nMail.setName(name);
			}

			if (mmd.getCreated() != null) {
				nMail.setCreated(mmd.getCreated());
			} else {
				nMail.setCreated(Calendar.getInstance());
			}

			if (mmd.getSentDate() != null) {
				nMail.setSentDate(mmd.getSentDate());
			}

			if (mmd.getReceivedDate() != null) {
				nMail.setReceivedDate(mmd.getReceivedDate());
			}

			if (mmd.getSize() > 0) {
				nMail.setSize(mmd.getSize());
			}

			if (mmd.getSubject() != null) {
				nMail.setSubject(mmd.getSubject());
			}

			if (mmd.getContent() != null) {
				nMail.setContent(mmd.getContent());
			}

			// Document path
			if (Config.STORE_NODE_PATH) {
				nMail.setPath(parentPath + "/" + nMail.getName());
			}

			// Keywords & categories
			nMail.setKeywords(mmd.getKeywords());
			nMail.setCategories(getValues(mmd.getCategories()));

			// Property Groups
			importPropertyGroups(nMail, mmd.getPropertyGroups());

			// Security
			if (mmd.getGrantedUsers() != null && !mmd.getGrantedUsers().isEmpty()) {
				nMail.setUserPermissions(mmd.getGrantedUsers());
			}

			if (mmd.getGrantedRoles() != null && !mmd.getGrantedRoles().isEmpty()) {
				nMail.setRolePermissions(mmd.getGrantedRoles());
			}

			// Persist
			session.save(nMail);

			// Notes
			if (!mmd.getNotes().isEmpty()) {
				for (NoteMetadata nmd : mmd.getNotes()) {
					NodeNote nNote = new NodeNote();
					nNote.setUuid(UUID.randomUUID().toString());
					nNote.setParent(nMail.getUuid());
					nNote.setAuthor(nmd.getUser());
					nNote.setCreated(nmd.getDate());
					nNote.setText(nmd.getText());
					session.save(nNote);
				}
			}

			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw new RepositoryException("PathNotFound: " + e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		// Activity log
		UserActivity.log(PrincipalUtils.getUser(), "CREATE_MAIL", nMail.getUuid(), mmd.getPath(), "Imported with metadata");

		SystemProfiling.log(String.valueOf(mmd), System.currentTimeMillis() - begin);
		log.trace("importWithMetadata.Time: {}", System.currentTimeMillis() - begin);
	}

	/**
	 *
	 */
	private void importPropertyGroups(NodeBase nBase, List<PropertyGroupMetadata> pGroups) {
		Gson gson = new Gson();

		for (PropertyGroupMetadata pgmd : pGroups) {
			for (PropertyMetadata pmd : pgmd.getProperties()) {
				NodeProperty nProp = new NodeProperty();
				nProp.setGroup(pgmd.getName());
				nProp.setName(pmd.getName());
				nProp.setNode(nBase);

				if (pmd.isMultiValue() || Select.class.getSimpleName().equals(pmd.getType())) {
					nProp.setValue(gson.toJson(pmd.getValues()));
				} else {
					// Check if input of type date is in extended ISO8601 format, and convert if needed
					if (Input.class.getSimpleName().equals(pmd.getType())) {
						String value = pmd.getValue();

						if (ISO8601.isExtended(value)) {
							Calendar calValue = ISO8601.parseExtended(value);
							nProp.setValue(ISO8601.formatBasic(calValue));
						} else {
							nProp.setValue(value);
						}
					} else {
						nProp.setValue(pmd.getValue());
					}
				}

				log.info("PROPERTY: {}", nProp);
				nBase.getProperties().add(nProp);
			}
		}
	}

	/**
	 * Convert between formats.
	 */
	private Set<String> getValues(Set<CategoryMetadata> categories) {
		Set<String> ret = new HashSet<String>();

		for (CategoryMetadata cmd : categories) {
			try {
				if (cmd.getUuid() != null && !cmd.getUuid().equals("")) {
					if (NodeBaseDAO.getInstance().itemUuidExists(cmd.getUuid())) {
						ret.add(cmd.getUuid());
					} else {
						log.warn("Category UUID not found: {}", cmd.getUuid());
					}
				} else if (cmd.getPath() != null && !cmd.getPath().equals("")) {
					String uuid = NodeBaseDAO.getInstance().getUuidFromPath(cmd.getPath());
					ret.add(uuid);
				}
			} catch (PathNotFoundException e) {
				log.warn("Category node not found: {}", cmd.getPath());
			} catch (DatabaseException e) {
				log.warn("Error resolving category: {}", cmd);
			}
		}

		return ret;
	}
}
