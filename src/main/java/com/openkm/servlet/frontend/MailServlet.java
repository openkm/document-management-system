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

package com.openkm.servlet.frontend;

import com.openkm.api.*;
import com.openkm.bean.Mail;
import com.openkm.bean.Repository;
import com.openkm.core.*;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMMailService;
import com.openkm.frontend.client.widget.filebrowser.GWTFilter;
import com.openkm.module.db.DbAuthModule;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.servlet.frontend.util.MailComparator;
import com.openkm.util.GWTUtil;
import com.openkm.util.MailUtils;
import com.openkm.util.pagination.FilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

/**
 * Servlet Class
 */
public class MailServlet extends OKMRemoteServiceServlet implements OKMMailService {
	private static Logger log = LoggerFactory.getLogger(MailServlet.class);
	private static final long serialVersionUID = 6444705787188086209L;

	@Override
	public List<GWTMail> getChilds(String fldPath, Map<String, GWTFilter> mapFilter) throws OKMException {
		log.debug("getChilds({})", fldPath);
		List<GWTMail> mailList = new ArrayList<GWTMail>();
		updateSessionManager();

		try {
			if (fldPath == null) {
				fldPath = OKMRepository.getInstance().getMailFolder(null).getPath();
			}

			// Case thesaurus view must search documents in keywords
			if (fldPath.startsWith("/" + Repository.THESAURUS)) {
				String keyword = fldPath.substring(fldPath.lastIndexOf("/") + 1).replace(" ", "_");
				List<Mail> results = OKMSearch.getInstance().getMailsByKeyword(null, keyword);

				for (Mail mail : results) {
					mailList.add(GWTUtil.copy(mail, getUserWorkspaceSession()));
				}
			} else if (fldPath.startsWith("/" + Repository.CATEGORIES)) {
				// Case categories view
				String uuid = OKMFolder.getInstance().getProperties(null, fldPath).getUuid();
				List<Mail> results = OKMSearch.getInstance().getCategorizedMails(null, uuid);

				for (Mail mail : results) {
					mailList.add(GWTUtil.copy(mail, getUserWorkspaceSession()));
				}
			} else if (fldPath.startsWith("/" + Repository.METADATA)) {
				// Case metadata value level
				if (fldPath.split("/").length - 1 == 4) {
					String subFolder[] = fldPath.split("/");
					String group = subFolder[2];
					String property = subFolder[3];
					String value = subFolder[4];
					List<Mail> results = OKMSearch.getInstance().getMailsByPropertyValue(null, group, property, value);

					for (Mail mail : results) {
						mailList.add(GWTUtil.copy(mail, getUserWorkspaceSession()));
					}
				}
			} else {
				log.debug("ParentFolder: {}", fldPath);
				for (Mail mail : OKMMail.getInstance().getChildren(null, fldPath)) {
					log.debug("Mail: {}", mail);
					mailList.add(GWTUtil.copy(mail, getUserWorkspaceSession()));
				}
			}
			if (mapFilter != null) {
				FilterUtils.filter(getUserWorkspaceSession(), mailList, mapFilter);
			}
			Collections.sort(mailList, MailComparator.getInstance(getLanguage()));
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_General),
					e.getMessage());
		}

		log.debug("getChilds: {}", mailList);
		return mailList;
	}

	@Override
	public void delete(String mailPath) throws OKMException {
		log.debug("delete({})", mailPath);
		updateSessionManager();

		try {
			OKMMail.getInstance().delete(null, mailPath);
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_General),
					e.getMessage());
		}

		log.debug("delete: void");
	}

	@Override
	public void move(String mailPath, String destPath) throws OKMException {
		log.debug("move({}, {})", mailPath, destPath);
		updateSessionManager();

		try {
			OKMMail.getInstance().move(null, mailPath, destPath);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_ItemExists),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_General),
					e.getMessage());
		}

		log.debug("move: void");
	}

	@Override
	public void purge(String mailPath) throws OKMException {
		log.debug("purge({})", mailPath);
		updateSessionManager();

		try {
			OKMMail.getInstance().purge(null, mailPath);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_General),
					e.getMessage());
		}

		log.debug("purge: void");
	}

	@Override
	public void copy(String mailPath, String fldPath) throws OKMException {
		log.debug("copy({}, {})", mailPath, fldPath);
		updateSessionManager();

		try {
			OKMMail.getInstance().copy(null, mailPath, fldPath);
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_ItemExists),
					e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_General),
					e.getMessage());
		}

		log.debug("copy: void");
	}

	@Override
	public GWTMail getProperties(String mailPath) throws OKMException {
		log.debug("getProperties({})", mailPath);
		GWTMail mailClient = new GWTMail();
		updateSessionManager();

		try {
			mailClient = GWTUtil.copy(OKMMail.getInstance().getProperties(null, mailPath), getUserWorkspaceSession());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_General),
					e.getMessage());
		}

		log.debug("copy: getProperties");
		return mailClient;
	}

	@Override
	public GWTMail rename(String mailId, String newName) throws OKMException {
		log.debug("rename({}, {})", mailId, newName);
		GWTMail gWTMail = new GWTMail();
		updateSessionManager();

		try {
			gWTMail = GWTUtil.copy(OKMMail.getInstance().rename(null, mailId, newName), getUserWorkspaceSession());
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_ItemExists),
					e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_General),
					e.getMessage());
		}

		log.debug("rename: {}", gWTMail);
		return gWTMail;
	}

	@Override
	public Boolean isValid(String mailPath) throws OKMException {
		log.debug("isValid({})", mailPath);
		updateSessionManager();

		try {
			return new Boolean(OKMMail.getInstance().isValid(null, mailPath));
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
	}

	@Override
	public void forwardMail(String mailPath, String mails, String users, String roles, String message) throws OKMException {
		log.debug("forwardMail({}, {}, {}, {}, {})", new Object[]{mailPath, mails, users, roles, message});
		updateSessionManager();
		List<String> to = new ArrayList<String>(MailUtils.parseMailList(mails));

		try {
			List<String> userNames = new ArrayList<String>(Arrays.asList(users.isEmpty() ? new String[0] : users.split(",")));
			List<String> roleNames = new ArrayList<String>(Arrays.asList(roles.isEmpty() ? new String[0] : roles.split(",")));

			for (String role : roleNames) {
				List<String> usersInRole = OKMAuth.getInstance().getUsersByRole(null, role);

				for (String user : usersInRole) {
					if (!userNames.contains(user)) {
						userNames.add(user);
					}
				}
			}

			for (String usr : userNames) {
				String mail = new DbAuthModule().getMail(null, usr);

				if (mail != null) {
					to.add(mail);
				}
			}

			// Get session user email address && mail forward
			String from = new DbAuthModule().getMail(null, getThreadLocalRequest().getRemoteUser());
			MailUtils.forwardMail(null, from, to, message, mailPath);
		} catch (PrincipalAdapterException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PrincipalAdapter),
					e.getMessage());
		} catch (MessagingException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Messaging),
					e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_IO),
					e.getMessage());
		} catch (DatabaseException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Database),
					e.getMessage());
		}
	}
}
