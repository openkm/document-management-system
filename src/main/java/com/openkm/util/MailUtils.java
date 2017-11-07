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

package com.openkm.util;

import java.io.*;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.FlagTerm;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auxilii.msgparser.RecipientEntry;
import com.auxilii.msgparser.attachment.Attachment;
import com.auxilii.msgparser.attachment.FileAttachment;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.openkm.api.OKMDocument;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMMail;
import com.openkm.api.OKMRepository;
import com.openkm.automation.AutomationException;
import com.openkm.bean.Document;
import com.openkm.bean.FileUploadResponse;
import com.openkm.bean.Mail;
import com.openkm.bean.Repository;
import com.openkm.core.*;
import com.openkm.dao.MailAccountDAO;
import com.openkm.dao.bean.MailAccount;
import com.openkm.dao.bean.MailFilter;
import com.openkm.dao.bean.MailFilterRule;
import com.openkm.dao.bean.MailImportError;
import com.openkm.extension.core.ExtensionException;
import com.openkm.module.db.DbDocumentModule;
import com.openkm.module.db.DbMailModule;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.FlagTerm;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import java.io.*;
import java.util.*;

/**
 * Java Mail configuration properties
 *
 * http://docs.oracle.com/javaee/7/api/javax/mail/internet/package-summary.html
 */
public class MailUtils {
	private static Logger log = LoggerFactory.getLogger(MailUtils.class);
	public static final String NO_SUBJECT = "(Message without subject)";
	public static final String NO_BODY = "(Message without body)";
	public static final String MAIL_REGEX = "([_A-Za-z0-9-]+)(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})";
	public static String[] MAIL_STORE_SEPARATOR = {"/", "."};

	/**
	 * Common properties for all mail sessions.
	 */
	public static Properties getProperties() {
		Properties props = System.getProperties();
		props.put("mail.imaps.ssl.trust", "*");
		return props;
	}

	/**
	 * Send mail without FROM addresses.
	 *
	 * @param toAddress Destination addresses.
	 * @param subject The mail subject.
	 * @param content The mail body.
	 * @throws MessagingException If there is any error.
	 */
	public static void sendMessage(Collection<String> toAddress, String subject, String content) throws MessagingException {
		try {
			send(null, toAddress, subject, content, new ArrayList<String>());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		} catch (DatabaseException e) {
			log.warn(e.getMessage(), e);
		}
	}

	/**
	 * Send mail without FROM addresses.
	 *
	 * @param toAddress Destination addresses.
	 * @param subject The mail subject.
	 * @param content The mail body.
	 * @throws MessagingException If there is any error.
	 */
	public static void sendMessage(String toAddress, String subject, String content) throws MessagingException {
		try {
			ArrayList<String> toList = new ArrayList<String>();
			toList.add(toAddress);
			send(null, toList, subject, content, new ArrayList<String>());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		} catch (DatabaseException e) {
			log.warn(e.getMessage(), e);
		}
	}

	/**
	 * Send mail without FROM addresses.
	 *
	 * @param toAddress Destination addresses.
	 * @param subject The mail subject.
	 * @param content The mail body.
	 * @throws MessagingException If there is any error.
	 */
	public static void sendMessage(String fromAddress, List<String> toAddress, String subject, String content) throws MessagingException {
		try {
			send(fromAddress, toAddress, subject, content, new ArrayList<String>());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		} catch (DatabaseException e) {
			log.warn(e.getMessage(), e);
		}
	}

	/**
	 * Send mail without FROM addresses.
	 *
	 * @param toAddress Destination addresses.
	 * @param subject The mail subject.
	 * @param content The mail body.
	 * @throws MessagingException If there is any error.
	 */
	public static void sendMessage(String fromAddress, String toAddress, String subject, String content) throws MessagingException {
		try {
			ArrayList<String> toList = new ArrayList<String>();
			toList.add(toAddress);
			send(fromAddress, toList, subject, content, new ArrayList<String>());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		} catch (DatabaseException e) {
			log.warn(e.getMessage(), e);
		}
	}

	/**
	 * Send document to non-registered OpenKM users
	 *
	 * @param toAddress Destination addresses.
	 * @param subject The mail subject.
	 * @param text The mail body.
	 * @throws MessagingException If there is any error.
	 */
	public static void sendDocument(String fromAddress, List<String> toAddress, String subject, String text, String docPath)
			throws MessagingException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException, DatabaseException {
		send(fromAddress, toAddress, subject, text, Arrays.asList(new String[]{docPath}));
	}

	/**
	 * Send document to non-registered OpenKM users
	 *
	 * @param toAddress Destination addresses.
	 * @param subject The mail subject.
	 * @param text The mail body.
	 * @throws MessagingException If there is any error.
	 */
	public static void sendDocuments(String fromAddress, List<String> toAddress, String subject, String text, List<String> docsPath)
			throws MessagingException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException, DatabaseException {
		send(fromAddress, toAddress, subject, text, docsPath);
	}

	/**
	 * Send mail with FROM addresses.
	 *
	 * @param fromAddress Origin address.
	 * @param toAddress Destination addresses.
	 * @param subject The mail subject.
	 * @param text The mail body.
	 * @throws MessagingException If there is any error.
	 */
	private static void send(String fromAddress, Collection<String> toAddress, String subject, String text, Collection<String> docsPath)
			throws MessagingException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException, DatabaseException {
		log.debug("send({}, {}, {}, {}, {})", new Object[]{fromAddress, toAddress, subject, text, docsPath});
		List<File> tmpAttachments = new ArrayList<File>();

		try {
			// Need a temporal file for every attachment.
			for (int i = 0; i < docsPath.size(); i++) {
				tmpAttachments.add(FileUtils.createTempFile());
			}

			MimeMessage m = create(fromAddress, toAddress, subject, text, docsPath, tmpAttachments);
			Transport.send(m);
		} finally {
			for (File tmpAttach : tmpAttachments) {
				FileUtils.deleteQuietly(tmpAttach);
			}
		}

		log.debug("send: void");
	}

	/**
	 * Forward a mail in the repository.
	 *
	 * @param token Authentication token.
	 * @param fromAddress Origin address.
	 * @param toAddress Destination addresses.
	 * @param mailPath Path of the mail to be forwarded.
	 * @throws MessagingException If there is any error.
	 */
	public static void forwardMail(String token, String fromAddress, String toAddress, String message, String mailPath)
			throws MessagingException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException, DatabaseException {
		ArrayList<String> toList = new ArrayList<String>();
		toList.add(toAddress);
		forwardMail(token, fromAddress, toList, message, mailPath);
	}

	/**
	 * Forward a mail in the repository.
	 *
	 * @param token Authentication token.
	 * @param fromAddress Origin address.
	 * @param toAddress Destination addresses.
	 * @param mailId Path of the mail to be forwarded or its UUID.
	 * @throws MessagingException If there is any error.
	 */
	public static void forwardMail(String token, String fromAddress, Collection<String> toAddress, String message, String mailId)
			throws MessagingException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException, DatabaseException {
		log.debug("forwardMail({}, {}, {}, {}, {})", new Object[]{token, fromAddress, toAddress, mailId});
		Mail mail = OKMMail.getInstance().getProperties(token, mailId);
		mail.setSubject("Fwd: " + mail.getSubject());

		if (Mail.MIME_TEXT.equals(mail.getMimeType())) {
			mail.setContent(message + "\n\n---------- Forwarded message ----------\n\n" + mail.getContent());
		} else if (Mail.MIME_HTML.equals(mail.getMimeType())) {
			mail.setContent(message + "<br/><br/>---------- Forwarded message ----------<br/><br/>" + mail.getContent());
		} else {
			log.warn("Email does not specify content MIME type");
		}

		if (fromAddress != null) {
			mail.setFrom(fromAddress);
		}

		if (toAddress != null && !toAddress.isEmpty()) {
			String[] to = toAddress.toArray(new String[toAddress.size()]);
			mail.setTo(to);
		}

		MimeMessage m = create(token, mail);
		Transport.send(m);
		log.debug("forwardMail: void");
	}

	/**
	 * Create a mail.
	 *
	 * @param fromAddress Origin address.
	 * @param toAddress Destination addresses.
	 * @param subject The mail subject.
	 * @param text The mail body.
	 * @throws MessagingException If there is any error.
	 */
	private static MimeMessage create(String fromAddress, Collection<String> toAddress, String subject, String text,
	                                  Collection<String> docsPath, List<File> tmpAttachments) throws MessagingException, PathNotFoundException,
			AccessDeniedException, RepositoryException, IOException, DatabaseException {
		log.debug("create({}, {}, {}, {}, {})", new Object[]{fromAddress, toAddress, subject, text, docsPath});
		Session mailSession = getMailSession();
		MimeMessage msg = new MimeMessage(mailSession);

		if (fromAddress != null && Config.SEND_MAIL_FROM_USER) {
			InternetAddress from = new InternetAddress(fromAddress);
			msg.setFrom(from);
		} else {
			msg.setFrom();
		}

		InternetAddress[] to = new InternetAddress[toAddress.size()];
		int idx = 0;

		for (Iterator<String> it = toAddress.iterator(); it.hasNext(); ) {
			to[idx++] = new InternetAddress(it.next());
		}

		// Build a multiparted mail with HTML and text content for better SPAM behaviour
		Multipart content = new MimeMultipart();

		// HTML Part
		MimeBodyPart htmlPart = new MimeBodyPart();
		StringBuilder htmlContent = new StringBuilder();
		htmlContent.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
		htmlContent.append("<html>\n<head>\n");
		htmlContent.append("<meta content=\"text/html;charset=UTF-8\" http-equiv=\"Content-Type\"/>\n");
		htmlContent.append("</head>\n<body>\n");
		htmlContent.append(text);
		htmlContent.append("\n</body>\n</html>");
		htmlPart.setContent(htmlContent.toString(), "text/html;charset=UTF-8");
		htmlPart.setHeader("Content-Type", "text/html;charset=UTF-8");
		htmlPart.setDisposition(Part.INLINE);
		content.addBodyPart(htmlPart);
		idx = 0;

		if (docsPath != null) {
			for (String docPath : docsPath) {
				InputStream is = null;
				FileOutputStream fos = null;
				String docName = PathUtils.getName(docPath);

				try {
					final Document doc = OKMDocument.getInstance().getProperties(null, docPath);
					is = OKMDocument.getInstance().getContent(null, docPath, false);
					final File tmpAttch = tmpAttachments.get(idx++);
					fos = new FileOutputStream(tmpAttch);
					IOUtils.copy(is, fos);
					fos.flush();

					// Document attachment part
					MimeBodyPart docPart = new MimeBodyPart();
					DataSource source = new FileDataSource(tmpAttch.getPath()) {
						@Override
						public String getContentType() {
							return doc.getMimeType();
						}
					};

					docPart.setDataHandler(new DataHandler(source));
					docPart.setFileName(MimeUtility.encodeText(docName));
					docPart.setDisposition(Part.ATTACHMENT);
					content.addBodyPart(docPart);
				} finally {
					IOUtils.closeQuietly(is);
					IOUtils.closeQuietly(fos);
				}
			}
		}

		msg.setHeader("MIME-Version", "1.0");
		msg.setHeader("Content-Type", content.getContentType());
		msg.addHeader("Charset", "UTF-8");
		msg.setRecipients(Message.RecipientType.TO, to);
		msg.setSubject(subject, "UTF-8");
		msg.setSentDate(new Date());
		msg.setContent(content);
		msg.saveChanges();

		log.debug("create: {}", msg);
		return msg;
	}

	/**
	 * Create a mail from a Mail object
	 */
	public static MimeMessage create(String token, Mail mail) throws MessagingException, PathNotFoundException, AccessDeniedException,
			RepositoryException, IOException, DatabaseException {
		log.debug("create({})", mail);
		Session mailSession = getMailSession();
		MimeMessage msg = new MimeMessage(mailSession);

		if (mail.getFrom() != null) {
			InternetAddress from = new InternetAddress(mail.getFrom());
			msg.setFrom(from);
		} else {
			msg.setFrom();
		}

		InternetAddress[] to = new InternetAddress[mail.getTo().length];
		int i = 0;

		for (String strTo : mail.getTo()) {
			to[i++] = new InternetAddress(strTo);
		}

		// Build a multiparted mail with HTML and text content for better SPAM behaviour
		MimeMultipart content = new MimeMultipart();

		if (Mail.MIME_TEXT.equals(mail.getMimeType())) {
			// Text part
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(mail.getContent());
			textPart.setHeader("Content-Type", "text/plain");
			textPart.setDisposition(Part.INLINE);
			content.addBodyPart(textPart);
		} else if (Mail.MIME_HTML.equals(mail.getMimeType())) {
			// HTML Part
			MimeBodyPart htmlPart = new MimeBodyPart();
			StringBuilder htmlContent = new StringBuilder();
			htmlContent.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
			htmlContent.append("<html>\n<head>\n");
			htmlContent.append("<meta content=\"text/html;charset=UTF-8\" http-equiv=\"Content-Type\"/>\n");
			htmlContent.append("</head>\n<body>\n");
			htmlContent.append(mail.getContent());
			htmlContent.append("\n</body>\n</html>");
			htmlPart.setContent(htmlContent.toString(), "text/html");
			htmlPart.setHeader("Content-Type", "text/html");
			htmlPart.setDisposition(Part.INLINE);
			content.addBodyPart(htmlPart);
		} else {
			log.warn("Email does not specify content MIME type");

			// Text part
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(mail.getContent());
			textPart.setHeader("Content-Type", "text/plain");
			textPart.setDisposition(Part.INLINE);
			content.addBodyPart(textPart);
		}
		
		for (Document doc : mail.getAttachments()) {
			String docName = PathUtils.getName(doc.getPath());
			InputStream is = null;

			try {
				is = OKMDocument.getInstance().getContent(token, doc.getPath(), false);
				String mimeType = MimeTypeConfig.mimeTypes.getContentType(docName.toLowerCase());

				// Document attachment part
				MimeBodyPart docPart = new MimeBodyPart();
				DataSource source = new ByteArrayDataSource(is, mimeType);
				docPart.setDataHandler(new DataHandler(source));
				docPart.setFileName(docName);
				docPart.setDisposition(Part.ATTACHMENT);
				content.addBodyPart(docPart);
			} finally {
				IOUtils.closeQuietly(is);
			}
		}

		msg.setHeader("MIME-Version", "1.0");
		msg.setHeader("Content-Type", content.getContentType());
		msg.addHeader("Charset", "UTF-8");
		msg.setRecipients(Message.RecipientType.TO, to);
		msg.setSubject(mail.getSubject(), "UTF-8");
		msg.setSentDate(new Date());
		msg.setContent(content);
		msg.saveChanges();

		log.debug("create: {}", msg);
		return msg;
	}

	/**
	 *
	 */
	private static Session getMailSession() {
		Session mailSession = null;

		try {
			InitialContext initialContext = new InitialContext();
			Object obj = initialContext.lookup(Config.JNDI_BASE + "mail/OpenKM");
			mailSession = (Session) PortableRemoteObject.narrow(obj, Session.class);
		} catch (javax.naming.NamingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mailSession;
	}

	/**
	 * Import messages
	 * http://www.jguru.com/faq/view.jsp?EID=26898
	 *
	 * == Using Unique Identifier (UIDL) ==
	 * Mail server assigns an unique identifier for every email in the same account. You can get as UIDL
	 * for every email by MailInfo.UIDL property. To avoid receiving the same email twice, the best way is
	 * storing the UIDL of email retrieved to a text file or database. Next time before you retrieve email,
	 * compare your local uidl list with remote uidl. If this uidl exists in your local uidl list, don't
	 * receive it; otherwise receive it.
	 *
	 * == Different property of UIDL in POP3 and IMAP4 ==
	 * UIDL is always unique in IMAP4 and it is always an incremental integer. UIDL in POP3 can be any valid
	 * asc-ii characters, and an UIDL may be reused by POP3 server if email with this UIDL has been deleted
	 * from the server. Hence you are advised to remove the uidl from your local uidl list if that uidl is
	 * no longer exist on the POP3 server.
	 *
	 * == Remarks ==
	 * You should create different local uidl list for different email account, because the uidl is only
	 * unique for the same account.
	 */
	public static String importMessages(String token, MailAccount ma) throws PathNotFoundException, ItemExistsException,
			VirusDetectedException, AccessDeniedException, RepositoryException, DatabaseException, UserQuotaExceededException,
			ExtensionException, AutomationException {
		log.debug("importMessages({}, {})", new Object[]{token, ma});
		Session session = Session.getDefaultInstance(getProperties());
		String exceptionMessage = null;

		try {
			// Open connection
			Store store = session.getStore(ma.getMailProtocol());
			store.connect(ma.getMailHost(), ma.getMailUser(), ma.getMailPassword());

			String currentFolder = fixFolderSeparator(store, ma.getMailFolder());
			Folder folder = store.getFolder(currentFolder);			
			folder.open(Folder.READ_WRITE);
			Message messages[];

			if (folder instanceof IMAPFolder) {
				// IMAP folder UIDs begins at 1 and are supposed to be sequential.
				// Each folder has its own UIDs sequence, not is a global one.
				long startUid = ma.getMailLastUid() + 1;
				IMAPFolder imapFolder = (IMAPFolder) folder;
				Message[] tmp = imapFolder.getMessagesByUID(startUid, UIDFolder.LASTUID);
				messages = removeAlreadyImported(imapFolder, tmp, startUid);				
			} else {
				messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
			}

			exceptionMessage = importMessages(token, messages, folder, ma);

			// Close connection
			log.debug("Expunge: {}", ma.isMailMarkDeleted());
			folder.close(ma.isMailMarkDeleted());
			store.close();
		} catch (NoSuchProviderException e) {
			log.error(e.getMessage() + " - MailAccount [Id: " + ma.getId() + ", User: " + ma.getUser() + "]", e);
			exceptionMessage = e.getMessage();
		} catch (MessagingException e) {
			log.error(e.getMessage() + " - MailAccount [Id: " + ma.getId() + ", User: " + ma.getUser() + "]", e);
			exceptionMessage = e.getMessage();
		}

		log.debug("importMessages: {}", exceptionMessage);
		return exceptionMessage;
	}

	/**
	 * Remove not needed elements
	 */
	private static Message[] removeAlreadyImported(IMAPFolder folder, Message[] messages, long startUid) throws MessagingException {
		List<Message> result = new LinkedList<>();

		for (Message msg : messages) {
			long msgUid = folder.getUID(msg);

			if (msgUid >= startUid) {
				result.add(msg);
			}
		}

		return result.toArray(new Message[0]);
	}

	/**
	 * Returns the full name of the folder with the correct separator
	 *
	 * @param store   the mail store
	 * @param fldName the full name of the folder
	 * @return The folder path with the correct folder separator
	 */
	public static String fixFolderSeparator(Store store, String fldName) throws MessagingException {
		if (fldName.contains(MailUtils.MAIL_STORE_SEPARATOR[0]) || fldName.contains(MailUtils.MAIL_STORE_SEPARATOR[1])) {
			String separator = fldName.contains(MAIL_STORE_SEPARATOR[0]) ? MAIL_STORE_SEPARATOR[0] : MAIL_STORE_SEPARATOR[1];
			String[] fldPath = fldName.split(separator);

			if (fldPath.length > 1) {
				Folder parentFolder = store.getFolder(fldPath[0]);
				fldName = fldName.replace(separator.charAt(0), parentFolder.getSeparator());
			}
		}

		return fldName;
	}

	/**
	 * Import helper.
	 */
	private static String importMessages(String token, Message messages[], Folder folder, MailAccount ma)
			throws MessagingException, DatabaseException {
		String exceptionMessage = null;

		for (int i = 0; i < messages.length; i++) {
			Message msg = messages[i];
			log.info("======= ======= {} ======= =======", i);
			log.info("Folder: {}", folder);
			log.info("Subject: {}", msg.getSubject());
			log.info("From: {}", msg.getFrom());
			log.info("Received: {}", msg.getReceivedDate());
			log.info("Sent: {}", msg.getSentDate());

			try {
				com.openkm.bean.Mail mail = messageToMail(msg);

				if (ma.getMailFilters().isEmpty()) {
					log.debug("Import in compatibility mode");
					String mailPath = getUserMailPath(ma.getUser());

					mailPath = mailPath + "/" + Mail.INBOX;

					// Check that the folder exists
					OKMFolder.getInstance().createMissingFolders(null, mailPath);
					importMail(token, mailPath, true, folder, msg, ma, mail);
				} else {
					for (MailFilter mf : ma.getMailFilters()) {
						log.debug("MailFilter: {}", mf);

						if (checkRules(mail, mf.getFilterRules())) {
							String mailPath = mf.getPath();
							importMail(token, mailPath, mf.isGrouping(), folder, msg, ma, mail);
						}
					}
				}

				// Set message as seen
				if (ma.isMailMarkSeen()) {
					msg.setFlag(Flags.Flag.SEEN, true);
				} else {
					msg.setFlag(Flags.Flag.SEEN, false);
				}

				// Delete read mail if requested
				if (ma.isMailMarkDeleted()) {
					msg.setFlag(Flags.Flag.DELETED, true);
				}

				// Set lastUid
				if (folder instanceof IMAPFolder) {
					long msgUid = ((IMAPFolder) folder).getUID(msg);
					log.info("Message UID: {}", msgUid);
					ma.setMailLastUid(msgUid);
					MailAccountDAO.update(ma);
				}
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
				exceptionMessage = e.getMessage();

				boolean alreadyLogged = false;
				String msgId;

				if (folder instanceof IMAPFolder) {
					msgId = String.valueOf(((IMAPFolder) folder).getUID(msg));
				} else {
					msgId = ((POP3Folder) folder).getUID(msg);
				}

				for (MailImportError mie : ma.getMailImportErrors()) {
					if (msgId.equals(mie.getMailUid())) {
						alreadyLogged = true;
						break;
					}
				}

				if (!alreadyLogged) {
					String subject = msg.getSubject();

					// Need to replace 0x00 because PostgreSQL does not accept string containing 0x00
					subject = FormatUtil.fixUTF8(subject);

					// Need to remove Unicode surrogate because of MySQL => SQL Error: 1366, SQLState: HY000
					subject = FormatUtil.trimUnicodeSurrogates(subject);

					MailImportError mie = new MailImportError();
					mie.setImportDate(Calendar.getInstance());
					mie.setErrorMessage(String.valueOf(e));
					mie.setMailSubject(subject);
					mie.setMailUid(msgId);
					ma.getMailImportErrors().add(mie);
					MailAccountDAO.update(ma);
				}
			}
		}

		return exceptionMessage;
	}

	/**
	 * Convert Mime Message to Mail
	 */
	public static Mail messageToMail(Message msg) throws MessagingException, IOException {
		com.openkm.bean.Mail mail = new com.openkm.bean.Mail();
		Calendar receivedDate = Calendar.getInstance();
		Calendar sentDate = Calendar.getInstance();

		// Can be void
		if (msg.getReceivedDate() != null) {
			receivedDate.setTime(msg.getReceivedDate());
		}

		// Can be void
		if (msg.getSentDate() != null) {
			sentDate.setTime(msg.getSentDate());
		}

		String body = getText(msg);

		// log.info("getText: "+body);
		if (body.charAt(0) == 'H') {
			mail.setMimeType(MimeTypeConfig.MIME_HTML);
		} else if (body.charAt(0) == 'T') {
			mail.setMimeType(MimeTypeConfig.MIME_TEXT);
		} else {
			mail.setMimeType(MimeTypeConfig.MIME_UNDEFINED);
		}

		if (msg.getFrom() != null && msg.getFrom().length > 0) {
			mail.setFrom(addressToString(msg.getFrom()[0]));
		}

		// Fix mail size
		if (msg.getSize() < 0) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			msg.writeTo(baos);
			mail.setSize(baos.size());
			IOUtils.closeQuietly(baos);
		} else {
			mail.setSize(msg.getSize());
		}

		// Need to replace 0x00 because PostgreSQL does not accept string containing 0x00
		// Need to remove Unicode surrogate because of MySQL => SQL Error: 1366, SQLState: HY000
		String subject = FormatUtil.trimUnicodeSurrogates(FormatUtil.fixUTF8(msg.getSubject()));

		mail.setContent(body.substring(1));
		mail.setSubject((subject == null || subject.isEmpty()) ? NO_SUBJECT : subject);
		mail.setTo(addressToString(msg.getRecipients(Message.RecipientType.TO)));
		mail.setCc(addressToString(msg.getRecipients(Message.RecipientType.CC)));
		mail.setBcc(addressToString(msg.getRecipients(Message.RecipientType.BCC)));
		mail.setReceivedDate(receivedDate);
		mail.setSentDate(sentDate);

		return mail;
	}

	/**
	 * Convert Outlook Message to Mail
	 */
	public static Mail messageToMail(com.auxilii.msgparser.Message msg) throws MessagingException, IOException {
		com.openkm.bean.Mail mail = new com.openkm.bean.Mail();
		Calendar receivedDate = Calendar.getInstance();
		Calendar sentDate = Calendar.getInstance();

		// Can be void
		if (msg.getDate() != null) {
			receivedDate.setTime(msg.getDate());
		}

		// Can be void
		if (msg.getCreationDate() != null) {
			sentDate.setTime(msg.getCreationDate());
		}

		if (msg.getBodyRTF() != null) {
			try {
				// JEditorPaneRTF2HTMLConverter converter = new JEditorPaneRTF2HTMLConverter();
				// mail.setContent(converter.rtf2html(msg.getBodyRTF()));
				ByteArrayInputStream bais = new ByteArrayInputStream(msg.getBodyRTF().getBytes());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DocConverter.getInstance().rtf2html(bais, baos);
				mail.setMimeType(MimeTypeConfig.MIME_HTML);
				mail.setContent(baos.toString().replace("<br>", "").replace("<BR>", ""));
				mail.setContent(mail.getContent().replace("<font size=\"3\" style=\"font-size: 12pt\">", "<font size=\"2\" style=\"font-size: 10pt\">"));
				mail.setContent(mail.getContent().replace("<FONT SIZE=\"3\" STYLE=\"font-size: 12pt\">", "<FONT SIZE=\"2\" STYLE=\"font-size: 10pt\">"));
				mail.setContent(mail.getContent().replace("<FONT SIZE=3 STYLE=\"font-size: 12pt\">", "<FONT SIZE=2 STYLE=\"font-size: 10pt\">"));
				IOUtils.closeQuietly(bais);
				IOUtils.closeQuietly(baos);
			} catch (Exception e) {
				log.warn(e.getMessage(), e);

				// Try to recover form error with other formats
				if (msg.getBodyHTML() != null) {
					mail.setMimeType(MimeTypeConfig.MIME_HTML);
					mail.setContent(msg.getBodyHTML());
				} else if (msg.getBodyText() != null) {
					mail.setMimeType(MimeTypeConfig.MIME_TEXT);
					mail.setContent(msg.getBodyText());
				} else {
					mail.setMimeType(MimeTypeConfig.MIME_UNDEFINED);
				}
			}
		} else if (msg.getBodyHTML() != null) {
			mail.setMimeType(MimeTypeConfig.MIME_HTML);
			mail.setContent(msg.getBodyHTML());
		} else if (msg.getBodyText() != null) {
			mail.setMimeType(MimeTypeConfig.MIME_TEXT);
			mail.setContent(msg.getBodyText());
		} else {
			mail.setMimeType(MimeTypeConfig.MIME_UNDEFINED);
			mail.setContent("");
		}

		if (!msg.getDisplayTo().isEmpty() && !msg.getRecipients().isEmpty()) {
			mail.setTo(recipientToString(msg.getDisplayTo(), msg.getRecipients()));
		} else if (msg.getToRecipient() != null) {
			mail.setTo(new String[]{addressToString(msg.getToRecipient().getToName(), msg.getToRecipient().getToEmail())});
		} else {
			mail.setTo(new String[]{});
		}

		if (!msg.getDisplayCc().isEmpty() && !msg.getRecipients().isEmpty()) {
			mail.setCc(recipientToString(msg.getDisplayCc(), msg.getRecipients()));
		} else if (msg.getCcRecipients() != null && !msg.getCcRecipients().isEmpty()) {
			mail.setCc(recipientToString(msg.getCcRecipients()));
		} else {
			mail.setCc(new String[]{});
		}

		if (!msg.getDisplayBcc().isEmpty() && !msg.getRecipients().isEmpty()) {
			mail.setBcc(recipientToString(msg.getDisplayBcc(), msg.getRecipients()));
		} else if (msg.getBccRecipients() != null && !msg.getBccRecipients().isEmpty()) {
			mail.setBcc(recipientToString(msg.getBccRecipients()));
		} else {
			mail.setBcc(new String[]{});
		}

		// Need to replace 0x00 because PostgreSQL does not accept string containing 0x00
		// Need to remove Unicode surrogate because of MySQL => SQL Error: 1366, SQLState: HY000
		String subject = FormatUtil.trimUnicodeSurrogates(FormatUtil.fixUTF8(msg.getSubject()));

		mail.setSize(mail.getContent().length());
		mail.setSubject((subject == null || subject.isEmpty()) ? NO_SUBJECT : subject);
		mail.setFrom(fixAddressName(msg.getFromName()) + " <" + msg.getFromEmail() + ">");
		mail.setReceivedDate(receivedDate);
		mail.setSentDate(sentDate);

		return mail;
	}

	/**
	 * Convert Outlook Message to Mail
	 */
	public static Mail messageToMail(MAPIMessage msg) throws MessagingException, IOException {
		com.openkm.bean.Mail mail = new com.openkm.bean.Mail();
		Calendar receivedDate = Calendar.getInstance();
		Calendar sentDate = Calendar.getInstance();

		try {
			// Can be void
			if (msg.getMessageDate() != null) {
				receivedDate.setTime(msg.getMessageDate().getTime());
			}

			// Can be void
			if (msg.getMessageDate() != null) {
				sentDate.setTime(msg.getMessageDate().getTime());
			}

			if (msg.getRtfBody() != null) {
				try {
					// JEditorPaneRTF2HTMLConverter converter = new JEditorPaneRTF2HTMLConverter();
					// mail.setContent(converter.rtf2html(msg.getBodyRTF()));
					ByteArrayInputStream bais = new ByteArrayInputStream(msg.getRtfBody().getBytes());
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DocConverter.getInstance().rtf2html(bais, baos);
					mail.setMimeType(MimeTypeConfig.MIME_HTML);
					mail.setContent(baos.toString().replace("<BR>", ""));
					IOUtils.closeQuietly(bais);
					IOUtils.closeQuietly(baos);
				} catch (Exception e) {
					throw new MessagingException(e.getMessage(), e);
				}
			} else if (msg.getHtmlBody() != null) {
				mail.setMimeType(MimeTypeConfig.MIME_HTML);
				mail.setContent(msg.getHtmlBody());
			} else if (msg.getTextBody() != null) {
				mail.setMimeType(MimeTypeConfig.MIME_TEXT);
				mail.setContent(msg.getTextBody());
			} else {
				mail.setMimeType(MimeTypeConfig.MIME_UNDEFINED);
			}

			if (msg.getDisplayTo() != null) {
				mail.setTo(recipientToString(msg.getDisplayTo()));
			} else {
				mail.setTo(new String[]{});
			}

			StringBuilder sb = new StringBuilder();
			for (String header : msg.getHeaders()) {
				sb.append(header).append("\n");
			}

			// Need to replace 0x00 because PostgreSQL does not accept string containing 0x00
			// Need to remove Unicode surrogate because of MySQL => SQL Error: 1366, SQLState: HY000
			String subject = FormatUtil.trimUnicodeSurrogates(FormatUtil.fixUTF8(msg.getSubject()));

			mail.setSize(mail.getContent().length());
			mail.setSubject((subject == null || subject.isEmpty()) ? NO_SUBJECT : subject);
			mail.setFrom(msg.getDisplayFrom());
			mail.setCc(recipientToString(msg.getDisplayCC()));
			mail.setBcc(recipientToString(msg.getDisplayBCC()));
			mail.setReceivedDate(receivedDate);
			mail.setSentDate(sentDate);
		} catch (ChunkNotFoundException e) {
			throw new MessagingException(e.getMessage(), e);
		}

		return mail;
	}
	/**
	 * Import mail into OpenKM repository
	 */
	public static void importMail(String token, String mailPath, boolean grouping, Folder folder, Message msg, MailAccount ma,
	                              com.openkm.bean.Mail mail) throws DatabaseException, RepositoryException, AccessDeniedException, ItemExistsException,
			PathNotFoundException, MessagingException, VirusDetectedException, UserQuotaExceededException, IOException, ExtensionException,
			AutomationException {
		OKMRepository okmRepository = OKMRepository.getInstance();
		String path = grouping ? createGroupPath(token, mailPath, mail.getReceivedDate()) : mailPath;

		if (ma.getMailProtocol().equals(MailAccount.PROTOCOL_POP3) || ma.getMailProtocol().equals(MailAccount.PROTOCOL_POP3S)) {
			mail.setPath(path + "/" + ((POP3Folder) folder).getUID(msg) + "-"
					+ PathUtils.escape((msg.getSubject() == null || msg.getSubject().isEmpty()) ? NO_SUBJECT : msg.getSubject()));
		} else {
			mail.setPath(path + "/" + ((IMAPFolder) folder).getUID(msg) + "-"
					+ PathUtils.escape((msg.getSubject() == null || msg.getSubject().isEmpty()) ? NO_SUBJECT : msg.getSubject()));
		}

		String newMailPath = PathUtils.getParent(mail.getPath()) + "/" + PathUtils.escape(PathUtils.getName(mail.getPath()));
		log.debug("newMailPath: {}", newMailPath);

		if (!okmRepository.hasNode(token, newMailPath)) {
			if (Config.REPOSITORY_NATIVE) {
				new DbMailModule().create(token, mail, ma.getUser(), new Ref<FileUploadResponse>(null));
			} else {
				// Other implementation
			}

			try {
				addAttachments(token, mail, msg, ma.getUser());
			} catch (UnsupportedMimeTypeException e) {
				log.warn(e.getMessage(), e);
			} catch (FileSizeExceededException e) {
				log.warn(e.getMessage(), e);
			} catch (UserQuotaExceededException e) {
				log.warn(e.getMessage(), e);
			}
		}
	}

	/**
	 * Check mail import rules
	 */
	public static boolean checkRules(com.openkm.bean.Mail mail, Set<MailFilterRule> filterRules) {
		log.info("checkRules({}, {})", mail, filterRules);
		boolean ret = true;

		for (MailFilterRule fr : filterRules) {
			log.info("FilterRule: {}", fr);

			if (fr.isActive()) {
				if (MailFilterRule.FIELD_FROM.equals(fr.getField())) {
					if (MailFilterRule.OPERATION_CONTAINS.equals(fr.getOperation())) {
						ret &= mail.getFrom().toLowerCase().contains(fr.getValue().toLowerCase());
					} else if (MailFilterRule.OPERATION_EQUALS.equals(fr.getOperation())) {
						ret &= mail.getFrom().equalsIgnoreCase(fr.getValue());
					}
				} else if (MailFilterRule.FIELD_TO.equals(fr.getField())) {
					if (MailFilterRule.OPERATION_CONTAINS.equals(fr.getOperation())) {
						for (int j = 0; j < mail.getTo().length; j++) {
							ret &= mail.getTo()[j].toLowerCase().contains(fr.getValue().toLowerCase());
						}
					} else if (MailFilterRule.OPERATION_EQUALS.equals(fr.getOperation())) {
						for (int j = 0; j < mail.getTo().length; j++) {
							ret &= mail.getTo()[j].equalsIgnoreCase(fr.getValue());
						}
					}
				} else if (MailFilterRule.FIELD_SUBJECT.equals(fr.getField())) {
					if (MailFilterRule.OPERATION_CONTAINS.equals(fr.getOperation())) {
						ret &= mail.getSubject().toLowerCase().contains(fr.getValue().toLowerCase());
					} else if (MailFilterRule.OPERATION_EQUALS.equals(fr.getOperation())) {
						ret &= mail.getSubject().equalsIgnoreCase(fr.getValue());
					}
				} else if (MailFilterRule.FIELD_CONTENT.equals(fr.getField())) {
					if (MailFilterRule.OPERATION_CONTAINS.equals(fr.getOperation())) {
						ret &= mail.getContent().toLowerCase().contains(fr.getValue().toLowerCase());
					} else if (MailFilterRule.OPERATION_EQUALS.equals(fr.getOperation())) {
						ret &= mail.getContent().equalsIgnoreCase(fr.getValue());
					}
				}
			}

			log.info("FilterRule: {}", ret);
		}

		log.info("checkRules: {}", ret);
		return ret;
	}

	/**
	 * Create mail path
	 */
	private static String createGroupPath(String token, String mailPath, Calendar receivedDate) throws DatabaseException,
			RepositoryException, AccessDeniedException, ItemExistsException, PathNotFoundException, ExtensionException, AutomationException {
		log.debug("createGroupPath({}, {})", new Object[]{mailPath, receivedDate});
		OKMRepository okmRepository = OKMRepository.getInstance();
		String path = mailPath + "/" + receivedDate.get(Calendar.YEAR);
		OKMFolder okmFolder = OKMFolder.getInstance();

		if (!okmRepository.hasNode(token, path)) {
			com.openkm.bean.Folder fld = new com.openkm.bean.Folder();
			fld.setPath(path);
			okmFolder.create(token, fld);
		}

		path += "/" + (receivedDate.get(Calendar.MONTH) + 1);

		if (!okmRepository.hasNode(token, path)) {
			com.openkm.bean.Folder fld = new com.openkm.bean.Folder();
			fld.setPath(path);
			okmFolder.create(token, fld);
		}

		path += "/" + receivedDate.get(Calendar.DAY_OF_MONTH);

		if (!okmRepository.hasNode(token, path)) {
			com.openkm.bean.Folder fld = new com.openkm.bean.Folder();
			fld.setPath(path);
			okmFolder.create(token, fld);
		}

		log.debug("createGroupPath: {}", path);
		return path;
	}

	/**
	 * Get text from message
	 */
	private static String getText(Part p) throws MessagingException, IOException {
		if (p.isMimeType("multipart/alternative")) {
			// prefer html over plain text
			Multipart mp = (Multipart) p.getContent();
			String text = "T" + NO_BODY;

			for (int i = 0; i < mp.getCount(); i++) {
				Part bp = mp.getBodyPart(i);

				if (bp.isMimeType("text/plain")) {
					text = getText(bp);
				} else if (bp.isMimeType("text/html")) {
					text = getText(bp);
					break;
				} else {
					text = getText(bp);
				}
			}

			return text;
		} else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();

			for (int i = 0; i < mp.getCount(); i++) {
				String s = getText(mp.getBodyPart(i));

				if (s != null) {
					return s;
				}
			}
		} else if (p.isMimeType("message/rfc822")) {
			Part np = (Part) p.getContent();
			String s = getText(np);

			if (s != null) {
				return s;
			}
		} else {
			String str;

			try {
				Object obj = p.getContent();

				if (obj instanceof InputStream) {
					InputStream is = (InputStream) obj;
					CharsetDetector detector = new CharsetDetector();
					detector.setText(new BufferedInputStream(is));
					CharsetMatch cm = detector.detect();
					Reader rd = cm.getReader();
					str = IOUtils.toString(rd);
					IOUtils.closeQuietly(rd);
					IOUtils.closeQuietly(is);
				} else if (obj instanceof String) {
					str = (String) obj;
				} else {
					str = obj.toString();
				}
			} catch (UnsupportedEncodingException e) {
				InputStream is = p.getInputStream();
				CharsetDetector detector = new CharsetDetector();
				detector.setText(new BufferedInputStream(is));
				CharsetMatch cm = detector.detect();
				Reader rd = cm.getReader();
				str = IOUtils.toString(rd);
				IOUtils.closeQuietly(rd);
				IOUtils.closeQuietly(is);
			}

			if (p.isMimeType("text/html")) {
				return "H" + str;
			} else if (p.isMimeType("text/plain")) {
				return "T" + str;
			} else if (StringUtils.containsIgnoreCase(str, "<html>")) {
				return "H" + str;
			} else {
				// Otherwise let's set as text/plain
				return "T" + str;
			}
		}

		return "T" + NO_BODY;
	}

	/**
	 * Add attachments to an imported mail.
	 */
	public static void addAttachments(String token, com.openkm.bean.Mail mail, Part p, String userId) throws MessagingException,
			IOException, UnsupportedMimeTypeException, FileSizeExceededException, UserQuotaExceededException, VirusDetectedException,
			ItemExistsException, PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException, ExtensionException,
			AutomationException {
		if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			int count = mp.getCount();

			for (int i = 1; i < count; i++) {
				BodyPart bp = mp.getBodyPart(i);

				if (bp.getFileName() != null) {
					String name = MimeUtility.decodeText(bp.getFileName());
					String fileName = FileUtils.getFileName(name);
					String fileExtension = FileUtils.getFileExtension(name);
					String testName = name;

					// Test if already exists a document with the same name in the mail
					for (int j = 1; OKMRepository.getInstance().hasNode(token, mail.getPath() + "/" + testName); j++) {
						// log.info("Trying with: {}", testName);
						testName = fileName + " (" + j + ")." + fileExtension;
					}

					Document attachment = new Document();
					String mimeType = MimeTypeConfig.mimeTypes.getContentType(bp.getFileName().toLowerCase());
					attachment.setMimeType(mimeType);
					attachment.setPath(mail.getPath() + "/" + testName);
					InputStream is = bp.getInputStream();

					if (Config.REPOSITORY_NATIVE) {
						new DbDocumentModule().create(token, attachment, is, bp.getSize(), userId);
					} else {
						// Other implementation
					}

					is.close();
				}
			}
		}
	}

	/**
	 * Add attachments to an imported mail.
	 */
	public static void addAttachments(String token, com.openkm.bean.Mail mail, MAPIMessage msg, String userId) throws DatabaseException,
			RepositoryException, PathNotFoundException, ItemExistsException, VirusDetectedException, UserQuotaExceededException,
			UnsupportedMimeTypeException, ExtensionException, AccessDeniedException, IOException, AutomationException, FileSizeExceededException {
		for (AttachmentChunks att : msg.getAttachmentFiles()) {
			if (!att.isEmbeddedMessage()) {
				String attFileName = att.attachFileName.toString();
				if (att.attachLongFileName != null) {
					attFileName = att.attachLongFileName.toString();
				}

				log.debug("Importing attachment: {}", attFileName);
				String fileName = FileUtils.getFileName(attFileName);
				String fileExtension = FileUtils.getFileExtension(attFileName);
				String testName = fileName + "." + fileExtension;

				// Test if already exists a document with the same name in the mail
				for (int j = 1; OKMRepository.getInstance().hasNode(token, mail.getPath() + "/" + testName); j++) {
					// log.debug("Trying with: {}", testName);
					testName = fileName + " (" + j + ")." + fileExtension;
				}

				Document attachment = new Document();
				String mimeType = MimeTypeConfig.mimeTypes.getContentType(testName.toLowerCase());
				attachment.setMimeType(mimeType);
				attachment.setPath(mail.getPath() + "/" + testName);
				ByteArrayInputStream bais = new ByteArrayInputStream(att.attachData.getValue());

				if (Config.REPOSITORY_NATIVE) {
					new DbDocumentModule().create(token, attachment, bais, att.attachData.getValue().length, userId);
				} else {
					// Other implementation
				}

				IOUtils.closeQuietly(bais);
			}
		}
	}

	/**
	 * Add attachments to an imported mail.
	 */
	public static void addAttachments(String token, com.openkm.bean.Mail mail, com.auxilii.msgparser.Message msg, String userId)
			throws DatabaseException, RepositoryException, PathNotFoundException, ItemExistsException, VirusDetectedException,
			UserQuotaExceededException, UnsupportedMimeTypeException, ExtensionException, AccessDeniedException, IOException,
			AutomationException, FileSizeExceededException {
		for (Attachment att : msg.getAttachments()) {
			if (att instanceof FileAttachment) {
				FileAttachment fileAtt = (FileAttachment) att;
				String attachedFile = fileAtt.getLongFilename() != null ? fileAtt.getLongFilename() : fileAtt.getFilename();
				log.debug("Importing attachment: {}", attachedFile);

				String fileName = FileUtils.getFileName(attachedFile);
				String fileExtension = FileUtils.getFileExtension(attachedFile);
				String testName = fileName + "." + fileExtension;

				// Test if already exists a document with the same name in the mail
				for (int j = 1; OKMRepository.getInstance().hasNode(token, mail.getPath() + "/" + testName); j++) {
					// log.debug("Trying with: {}", testName);
					testName = fileName + " (" + j + ")." + fileExtension;
				}

				Document attachment = new Document();
				String mimeType = MimeTypeConfig.mimeTypes.getContentType(testName.toLowerCase());
				attachment.setMimeType(mimeType);
				attachment.setPath(mail.getPath() + "/" + testName);
				ByteArrayInputStream bais = new ByteArrayInputStream(fileAtt.getData());

				if (Config.REPOSITORY_NATIVE) {
					new DbDocumentModule().create(token, attachment, bais, fileAtt.getSize(), userId);
				} else {
					// Other implementation
				}

				IOUtils.closeQuietly(bais);
			}
		}
	}

	/**
	 * Conversion from array of Addresses to array of Strings.
	 */
	private static String[] addressToString(Address[] addresses) {
		ArrayList<String> list = new ArrayList<String>();

		if (addresses != null) {
			for (int i = 0; i < addresses.length; i++) {
				list.add(addressToString(addresses[i]));
			}
		}

		return list.toArray(new String[list.size()]);
	}

	/**
	 * Conversion from Address to String.
	 */
	public static String addressToString(Address a) {
		if (a != null) {
			InternetAddress ia = (InternetAddress) a;

			if (ia.getPersonal() != null) {
				return addressToString(ia.getPersonal(), ia.getAddress());
			} else {
				return ia.getAddress();
			}
		} else {
			return "";
		}
	}

	/**
	 * Conversion from Address to String.
	 */
	public static String addressToString(String name, String email) {
		if (name != null && !name.isEmpty()) {
			if (!name.equals(email)) {
				return fixAddressName(name) + " <" + email + ">";
			} else {
				return email;
			}
		} else {
			return email;
		}
	}

	/**
	 * Fix address name
	 */
	private static String fixAddressName(String name) {
		if (name != null) {
			name = name.trim();

			if (name.isEmpty()) {
				return "";
			} else if (name.startsWith("'") && name.endsWith("'")) {
				return "\"" + name.substring(1, name.length() - 1) + "\"";
			} else if (name.startsWith("\"") && name.endsWith("\"")) {
				return name;
			} else {
				return "\"" + name + "\"";
			}
		} else {
			return "";
		}
	}

	/**
	 * Conversion from array of Recipient to array of Strings.
	 */
	private static String[] recipientToString(List<RecipientEntry> recipEntries) {
		ArrayList<String> list = new ArrayList<String>();

		if (recipEntries != null) {
			for (RecipientEntry re : recipEntries) {
				list.add(addressToString(re.getToName(), re.getToEmail()));
			}
		}

		return list.toArray(new String[list.size()]);
	}

	/**
	 * Conversion from array of Recipient to array of Strings.
	 */
	private static String[] recipientToString(String recipEntries) {
		ArrayList<String> list = new ArrayList<>();

		if (recipEntries != null) {
			for (String re : recipEntries.split(";")) {
				list.add(re);
			}
		}

		return list.toArray(new String[list.size()]);
	}


	/**
	 * Conversion from array of Recipient to array of Strings (Quite weird!)
	 */
	private static String[] recipientToString(String displayText, List<RecipientEntry> recipients) {
		ArrayList<String> list = new ArrayList<String>();

		for (String name : displayText.split(";\\s*")) {
			if (name.endsWith("\u0000")) name = name.substring(0, name.length() - 1);
			for (RecipientEntry re : recipients) {
				if (re.getToName().equals(name)) {
					list.add(addressToString(re.getToName(), re.getToEmail()));
					break;
				}
			}
		}

		return list.toArray(new String[list.size()]);
	}

	/**
	 *
	 */
	public static String getUserMailPath(String user) {
		return "/" + Repository.MAIL + "/" + user;
	}

	/**
	 * User tinyurl service as url shorter Depends on commons-httpclient:commons-httpclient:jar:3.0 because of
	 * org.apache.jackrabbit:jackrabbit-webdav:jar:1.6.4
	 */
	public static String getTinyUrl(String fullUrl) throws HttpException, IOException {
		HttpClient httpclient = new HttpClient();

		// Prepare a request object
		HttpMethod method = new GetMethod("http://tinyurl.com/api-create.php");
		method.setQueryString(new NameValuePair[]{new NameValuePair("url", fullUrl)});
		httpclient.executeMethod(method);
		InputStreamReader isr = new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8");
		StringWriter sw = new StringWriter();
		int c;
		while ((c = isr.read()) != -1)
			sw.write(c);
		isr.close();
		method.releaseConnection();

		return sw.toString();
	}

	/**
	 * Test IMAP connection
	 */
	public static void testConnection(MailAccount ma) throws IOException {
		log.debug("testConnection({})", ma);
		Session session = Session.getDefaultInstance(getProperties());
		Store store = null;
		Folder folder = null;

		try {
			store = session.getStore(ma.getMailProtocol());
			store.connect(ma.getMailHost(), ma.getMailUser(), ma.getMailPassword());
			folder = store.getFolder(ma.getMailFolder());
			folder.open(Folder.READ_WRITE);
			folder.close(false);
		} catch (NoSuchProviderException e) {
			throw new IOException(e.getMessage());
		} catch (MessagingException e) {
			throw new IOException(e.getMessage());
		} finally {
			// Try to close folder
			if (folder != null && folder.isOpen()) {
				try {
					folder.close(false);
				} catch (MessagingException e) {
					throw new IOException(e.getMessage());
				}
			}

			// Try to close store
			if (store != null) {
				try {
					store.close();
				} catch (MessagingException e) {
					throw new IOException(e.getMessage());
				}
			}
		}

		log.debug("testConnection: void");
	}

	/**
	 * Generate HTML with mail object data and contents
	 */
	public static String mail2html(Mail mail) throws ConversionException {
		HashMap<String, String> hm = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < mail.getTo().length - 1; i++) {
			sb.append(mail.getTo()[i]).append(", ");
		}

		sb.append(mail.getTo()[mail.getTo().length - 1]);
		hm.put("mailTo", sb.toString());
		hm.put("mailFrom", mail.getFrom());
		hm.put("mailSubject", mail.getSubject());
		hm.put("mailContent", mail.getContent());
		StringWriter sw = new StringWriter();
		InputStreamReader isr = null;

		try {
			isr = new InputStreamReader(MailUtils.class.getResourceAsStream("mail.ftl"));
			Template tpl = new Template("mail", isr, TemplateUtils.getConfig());
			tpl.process(hm, sw);
		} catch (IOException e) {
			throw new ConversionException("IOException: " + e.getMessage(), e);
		} catch (TemplateException e) {
			throw new ConversionException("TemplateException: " + e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(sw);
			IOUtils.closeQuietly(isr);
		}

		return sw.toString();
	}

	/**
	 * Convert string with mails to list.
	 */
	public static List<String> parseMailList(String mails) {
		List<String> mailList = new ArrayList<String>();

		if (mails != null && !mails.isEmpty()) {
			for (StringTokenizer st = new StringTokenizer(mails, ","); st.hasMoreTokens(); ) {
				String mail = st.nextToken().trim();

				if (mail.matches(MAIL_REGEX)) {
					mailList.add(mail);
				}
			}
		}

		return mailList;
	}
}
