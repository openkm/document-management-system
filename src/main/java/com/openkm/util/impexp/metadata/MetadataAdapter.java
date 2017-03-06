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

import com.openkm.api.OKMAuth;
import com.openkm.api.OKMPropertyGroup;
import com.openkm.bean.*;
import com.openkm.bean.form.*;
import com.openkm.core.*;
import com.openkm.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class MetadataAdapter {
	private static Logger log = LoggerFactory.getLogger(MetadataAdapter.class);
	protected String token = null;
	protected boolean uuid = false;

	protected MetadataAdapter() {
	}

	public static MetadataAdapter getInstance(String token) {
		if (Config.REPOSITORY_NATIVE) {
			return new DbMetadataAdapter(token);
		} else {
			// Other implementation
			return null;
		}
	}

	/**
	 * Set if the documents and folder UUID should be restored on import.
	 */
	public void setRestoreUuid(boolean uuid) {
		this.uuid = uuid;
	}

	/**
	 * Performs metadata conversion.
	 */
	public DocumentMetadata getMetadata(Document doc) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException, IOException, ParseException, NoSuchGroupException {
		log.debug("getMetadata({})", new Object[]{doc});
		DocumentMetadata dmd = new DocumentMetadata();
		dmd.setUuid(doc.getUuid());
		dmd.setAuthor(doc.getAuthor());
		dmd.setName(PathUtils.getName(doc.getPath()));
		dmd.setPath(doc.getPath());
		dmd.setLanguage(doc.getLanguage());
		dmd.setCreated(doc.getCreated());
		dmd.setLastModified(doc.getLastModified());
		dmd.setCipherName(doc.getCipherName());
		dmd.setKeywords(doc.getKeywords());
		dmd.setSubscriptors(doc.getSubscriptors());
		dmd.setVersion(getMetadata(doc.getActualVersion(), doc.getMimeType()));

		// Categories
		for (Folder cat : doc.getCategories()) {
			CategoryMetadata cmd = new CategoryMetadata();
			cmd.setUuid(cat.getUuid());
			cmd.setPath(cat.getPath());
			dmd.getCategories().add(cmd);
		}

		// Notes
		for (Note nt : doc.getNotes()) {
			dmd.getNotes().add(getMetadata(nt));
		}

		// Security
		OKMAuth okmAuth = OKMAuth.getInstance();
		dmd.setGrantedUsers(okmAuth.getGrantedUsers(token, doc.getPath()));
		dmd.setGrantedRoles(okmAuth.getGrantedRoles(token, doc.getPath()));

		// Property Groups
		dmd.setPropertyGroups(getPropertyGroupsMetada(doc.getPath()));

		log.debug("getMetadata: {}", dmd);
		return dmd;
	}

	/**
	 * Performs metadata conversion.
	 */
	public FolderMetadata getMetadata(Folder fld) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException, IOException, ParseException, NoSuchGroupException {
		log.debug("getMetadata({})", new Object[]{fld});
		FolderMetadata fmd = new FolderMetadata();
		fmd.setUuid(fld.getUuid());
		fmd.setAuthor(fld.getAuthor());
		fmd.setName(PathUtils.getName(fld.getPath()));
		fmd.setPath(fld.getPath());
		fmd.setCreated(fld.getCreated());
		fmd.setKeywords(fld.getKeywords());
		fmd.setSubscriptors(fld.getSubscriptors());

		// Categories
		for (Folder cat : fld.getCategories()) {
			CategoryMetadata cmd = new CategoryMetadata();
			cmd.setUuid(cat.getUuid());
			cmd.setPath(cat.getPath());
			fmd.getCategories().add(cmd);
		}

		// Notes
		for (Note nt : fld.getNotes()) {
			fmd.getNotes().add(getMetadata(nt));
		}

		// Security
		OKMAuth okmAuth = OKMAuth.getInstance();
		fmd.setGrantedUsers(okmAuth.getGrantedUsers(token, fld.getPath()));
		fmd.setGrantedRoles(okmAuth.getGrantedRoles(token, fld.getPath()));

		// Property Groups
		fmd.setPropertyGroups(getPropertyGroupsMetada(fld.getPath()));

		log.debug("getMetadata: {}", fmd);
		return fmd;
	}

	/**
	 * Performs metadata conversion.
	 */
	public MailMetadata getMetadata(Mail mail) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException, IOException, ParseException, NoSuchGroupException {
		log.debug("getMetadata({})", new Object[]{mail});
		MailMetadata mmd = new MailMetadata();
		mmd.setUuid(mail.getUuid());
		mmd.setPath(mail.getPath());
		mmd.setName(PathUtils.getName(mail.getPath()));
		mmd.setSize(mail.getSize());
		mmd.setFrom(mail.getFrom());
		mmd.setReply(getValues(mail.getReply()));
		mmd.setTo(getValues(mail.getTo()));
		mmd.setCc(getValues(mail.getCc()));
		mmd.setBcc(getValues(mail.getBcc()));
		mmd.setSentDate(mail.getSentDate());
		mmd.setReceivedDate(mail.getReceivedDate());
		mmd.setSubject(mail.getSubject());
		mmd.setContent(mail.getContent());
		mmd.setMimeType(mail.getMimeType());
		mmd.setKeywords(mail.getKeywords());

		// Categories
		for (Folder cat : mail.getCategories()) {
			CategoryMetadata cmd = new CategoryMetadata();
			cmd.setUuid(cat.getUuid());
			cmd.setPath(cat.getPath());
			mmd.getCategories().add(cmd);
		}

		// Notes
		for (Note nt : mail.getNotes()) {
			mmd.getNotes().add(getMetadata(nt));
		}

		// Security
		OKMAuth okmAuth = OKMAuth.getInstance();
		mmd.setGrantedUsers(okmAuth.getGrantedUsers(token, mail.getPath()));
		mmd.setGrantedRoles(okmAuth.getGrantedRoles(token, mail.getPath()));

		// Property Groups
		mmd.setPropertyGroups(getPropertyGroupsMetada(mail.getPath()));

		log.debug("getMetadata: {}", mmd);
		return mmd;
	}

	/**
	 * Performs metadata conversion.
	 */
	public VersionMetadata getMetadata(Version ver, String mimeType) {
		log.debug("getMetadata({})", new Object[]{ver});
		VersionMetadata vmd = new VersionMetadata();
		vmd.setAuthor(ver.getAuthor());
		vmd.setName(ver.getName());
		vmd.setCreated(ver.getCreated());
		vmd.setSize(ver.getSize());
		vmd.setComment(ver.getComment());
		vmd.setMimeType(mimeType);
		log.debug("getMetadata: {}", vmd);
		return vmd;
	}

	/**
	 * Performs metadata conversion.
	 */
	private NoteMetadata getMetadata(Note nt) {
		log.debug("getMetadata({})", new Object[]{nt});
		NoteMetadata nmd = new NoteMetadata();
		nmd.setUser(nt.getAuthor());
		nmd.setDate(nt.getDate());
		nmd.setText(nt.getText());
		nmd.setPath(nt.getPath());
		log.debug("getMetadata: {}", nmd);
		return nmd;
	}

	/**
	 * Convert between value formats.
	 */
	private List<String> getValues(String[] values) {
		List<String> ret = new ArrayList<String>();

		for (String val : values) {
			ret.add(val);
		}

		return ret;
	}

	/**
	 * Perform specific PropertyGroup extraction.
	 */
	private List<PropertyGroupMetadata> getPropertyGroupsMetada(String path) throws IOException, ParseException,
			AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException, NoSuchGroupException {
		List<PropertyGroupMetadata> propGrpMeta = new ArrayList<PropertyGroupMetadata>();
		OKMPropertyGroup okmPropGrp = OKMPropertyGroup.getInstance();

		for (PropertyGroup propGrp : okmPropGrp.getGroups(token, path)) {
			PropertyGroupMetadata pgmd = new PropertyGroupMetadata();
			List<PropertyMetadata> pmds = new ArrayList<PropertyMetadata>();

			for (FormElement fe : okmPropGrp.getProperties(token, path, propGrp.getName())) {
				PropertyMetadata pmd = new PropertyMetadata();
				pmd.setName(fe.getName());

				if (fe instanceof Input) {
					Input i = (Input) fe;
					pmd.setValue(i.getValue());
				} else if (fe instanceof SuggestBox) {
					SuggestBox sb = (SuggestBox) fe;
					pmd.setValue(sb.getValue());
				} else if (fe instanceof TextArea) {
					TextArea ta = (TextArea) fe;
					pmd.setValue(ta.getValue());
				} else if (fe instanceof CheckBox) {
					CheckBox cb = (CheckBox) fe;
					pmd.setValue(Boolean.toString(cb.getValue()));
				} else if (fe instanceof Select) {
					List<String> values = new ArrayList<String>();
					Select s = (Select) fe;

					for (Option opt : s.getOptions()) {
						if (opt.isSelected()) {
							values.add(opt.getValue());
						}
					}

					pmd.setValues(values);
					pmd.setMultiValue(Select.TYPE_MULTIPLE.equals(s.getType()));
				}

				pmd.setType(fe.getClass().getSimpleName());
				pmds.add(pmd);
			}

			pgmd.setName(propGrp.getName());
			pgmd.setProperties(pmds);
			propGrpMeta.add(pgmd);
		}

		return propGrpMeta;
	}

	/**
	 * Perform specific document metadata import.
	 */
	public abstract void importWithMetadata(DocumentMetadata dmd, InputStream is) throws ItemExistsException,
			RepositoryException, DatabaseException, IOException;

	/**
	 * Perform specific version metadata import.
	 */
	public abstract void importWithMetadata(String parentPath, VersionMetadata vmd, InputStream is) throws
			ItemExistsException, RepositoryException, DatabaseException, IOException;

	/**
	 * Perform specific folder metadata import.
	 */
	public abstract void importWithMetadata(FolderMetadata fmd) throws ItemExistsException, RepositoryException,
			DatabaseException;

	/**
	 * Perform specific mail metadata import.
	 */
	public abstract void importWithMetadata(MailMetadata mmd) throws ItemExistsException, RepositoryException,
			DatabaseException;
}
