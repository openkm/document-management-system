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

package com.openkm.api;

import com.auxilii.msgparser.Message;
import com.auxilii.msgparser.MsgParser;
import com.openkm.automation.AutomationException;
import com.openkm.bean.ExtendedAttributes;
import com.openkm.bean.Mail;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.module.MailModule;
import com.openkm.module.ModuleManager;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.MailUtils;
import com.openkm.util.PathUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * @author pavila
 *
 */
public class OKMMail implements MailModule {
	private static Logger log = LoggerFactory.getLogger(OKMMail.class);
	private static OKMMail instance = new OKMMail();

	private OKMMail() {
	}

	public static OKMMail getInstance() {
		return instance;
	}

	@Override
	public Mail create(String token, Mail mail) throws PathNotFoundException, ItemExistsException,
			VirusDetectedException, AccessDeniedException, RepositoryException, DatabaseException,
			UserQuotaExceededException, AutomationException {
		log.debug("create({}, {})", token, mail);
		MailModule mm = ModuleManager.getMailModule();
		Mail newMail = mm.create(token, mail);
		log.debug("create: {}", newMail);
		return newMail;
	}

	@Override
	public Mail getProperties(String token, String mailId) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("getProperties({}, {})", token, mailId);
		MailModule mm = ModuleManager.getMailModule();
		Mail mail = mm.getProperties(token, mailId);
		log.debug("getProperties: {}", mail);
		return mail;
	}

	@Override
	public void delete(String token, String mailId) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("delete({}, {})", token, mailId);
		MailModule mm = ModuleManager.getMailModule();
		mm.delete(token, mailId);
		log.debug("delete: void");
	}

	@Override
	public void purge(String token, String mailId) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("purge({}, {})", token, mailId);
		MailModule mm = ModuleManager.getMailModule();
		mm.purge(token, mailId);
		log.debug("purge: void");
	}

	@Override
	public Mail rename(String token, String mailId, String newName) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("rename({}, {}, {})", new Object[]{token, mailId, newName});
		MailModule mm = ModuleManager.getMailModule();
		Mail renamedMail = mm.rename(token, mailId, newName);
		log.debug("rename: {}", renamedMail);
		return renamedMail;
	}

	@Override
	public void move(String token, String mailPath, String dstId) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("move({}, {}, {})", new Object[]{token, mailPath, dstId});
		MailModule mm = ModuleManager.getMailModule();
		mm.move(token, mailPath, dstId);
		log.debug("move: void");
	}

	@Override
	public void copy(String token, String mailPath, String dstId) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, IOException, AutomationException,
			DatabaseException, UserQuotaExceededException {
		log.debug("copy({}, {}, {})", new Object[]{token, mailPath, dstId});
		MailModule mm = ModuleManager.getMailModule();
		mm.copy(token, mailPath, dstId);
		log.debug("copy: void");
	}

	@Override
	public void extendedCopy(String token, String mailPath, String dstId, ExtendedAttributes extAttr)
			throws PathNotFoundException, ItemExistsException, AccessDeniedException, RepositoryException, IOException,
			AutomationException, DatabaseException, UserQuotaExceededException {
		log.debug("extendedCopy({}, {}, {}, {})", new Object[]{token, mailPath, dstId, extAttr});
		MailModule mm = ModuleManager.getMailModule();
		mm.extendedCopy(token, mailPath, dstId, extAttr);
		log.debug("extendedCopy: void");
	}

	@Override
	@Deprecated
	public List<Mail> getChilds(String token, String fldId) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("getChilds({}, {})", token, fldId);
		MailModule mm = ModuleManager.getMailModule();
		List<Mail> col = mm.getChilds(token, fldId);
		log.debug("getChilds: {}", col);
		return col;
	}

	@Override
	public List<Mail> getChildren(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getChildren({}, {})", token, fldId);
		MailModule mm = ModuleManager.getMailModule();
		List<Mail> col = mm.getChildren(token, fldId);
		log.debug("getChildren: {}", col);
		return col;
	}

	@Override
	public boolean isValid(String token, String mailId) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("isValid({}, {})", token, mailId);
		MailModule mm = ModuleManager.getMailModule();
		boolean valid = mm.isValid(token, mailId);
		log.debug("isValid: {}", valid);
		return valid;
	}

	@Override
	public String getPath(String token, String uuid) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getPath({})", uuid);
		MailModule mm = ModuleManager.getMailModule();
		String path = mm.getPath(token, uuid);
		log.debug("getPath: {}", path);
		return path;
	}

	/**
	 * Import EML file as MailNode.
	 */
	public Mail importEml(String path, InputStream is) throws MessagingException, PathNotFoundException, ItemExistsException,
			VirusDetectedException, AccessDeniedException, RepositoryException, DatabaseException, UserQuotaExceededException,
			UnsupportedMimeTypeException, FileSizeExceededException, ExtensionException, AutomationException, IOException {
		log.debug("importEml({}, {})", path, is);
		Properties props = System.getProperties();
		props.put("mail.host", "smtp.dummydomain.com");
		props.put("mail.transport.protocol", "smtp");
		Mail newMail = null;

		try {
			// Convert file
			Session mailSession = Session.getDefaultInstance(props, null);
			MimeMessage msg = new MimeMessage(mailSession, is);
			Mail mail = MailUtils.messageToMail(msg);

			// Create phantom path. In this case we don't have the IMAP message
			// ID, son create a random one.
			mail.setPath(path + "/" + UUID.randomUUID().toString() + "-" + PathUtils.escape(mail.getSubject()));

			// Import files
			newMail = OKMMail.getInstance().create(null, mail);
			MailUtils.addAttachments(null, mail, msg, PrincipalUtils.getUser());
		} catch (IOException e) {
			log.error("Error importing eml", e);
			throw e;
		} finally {
			IOUtils.closeQuietly(is);
		}

		log.debug("importEml: {}", newMail);
		return newMail;
	}

	/**
	 * Import MSG file as MailNode.
	 */
	public Mail importMsg(String path, InputStream is) throws MessagingException, PathNotFoundException, ItemExistsException,
			VirusDetectedException, AccessDeniedException, RepositoryException, DatabaseException, UserQuotaExceededException,
			UnsupportedMimeTypeException, FileSizeExceededException, ExtensionException, AutomationException, IOException {
		log.debug("importMsg({}, {})", path, is);
		Mail newMail = null;

		try {
			// Convert file
			MsgParser msgp = new MsgParser();
			Message msg = msgp.parseMsg(is);
			Mail mail = MailUtils.messageToMail(msg);

			// Create phantom path. In this case we don't have the IMAP message ID, son create a random one.
			mail.setPath(path + "/" + UUID.randomUUID().toString() + "-" + PathUtils.escape(mail.getSubject()));

			// Import files
			newMail = OKMMail.getInstance().create(null, mail);
			MailUtils.addAttachments(null, mail, msg, PrincipalUtils.getUser());
		} catch (IOException e) {
			log.error("Error importing msg", e);
			throw e;
		} finally {
			IOUtils.closeQuietly(is);
		}

		log.debug("importMsg: {}", newMail);
		return newMail;
	}
}
