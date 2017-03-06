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

package com.openkm.servlet.frontend;

import com.openkm.api.*;
import com.openkm.automation.AutomationException;
import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Mail;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.form.FormElement;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMMassiveService;
import com.openkm.module.db.DbAuthModule;
import com.openkm.module.db.DbNotificationModule;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.util.FormUtils;
import com.openkm.util.GWTUtil;
import com.openkm.util.MailUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Massive service
 */
public class MassiveServlet extends OKMRemoteServiceServlet implements OKMMassiveService {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(MassiveServlet.class);

	@Override
	public void copy(List<String> paths, String fldPath) throws OKMException {
		log.debug("copy({}, {})", paths, fldPath);
		updateSessionManager();
		String error = "";
		String pathErrors = "";

		for (String path : paths) {
			try {
				if (OKMDocument.getInstance().isValid(null, path)) {
					OKMDocument.getInstance().copy(null, path, fldPath);
				} else if (OKMFolder.getInstance().isValid(null, path)) {
					OKMFolder.getInstance().copy(null, path, fldPath);
				} else if (OKMMail.getInstance().isValid(null, path)) {
					OKMMail.getInstance().copy(null, path, fldPath);
				}
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (ItemExistsException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (UserQuotaExceededException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (ExtensionException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AutomationException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}

		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General), pathErrors
					+ "\n\n" + error);
		}
	}

	@Override
	public void move(List<String> paths, String fldPath) throws OKMException {
		log.debug("move({}, {})", paths, fldPath);
		updateSessionManager();
		String error = "";
		String pathErrors = "";

		for (String path : paths) {
			try {
				if (OKMDocument.getInstance().isValid(null, path)) {
					OKMDocument.getInstance().move(null, path, fldPath);
				} else if (OKMFolder.getInstance().isValid(null, path)) {
					OKMFolder.getInstance().move(null, path, fldPath);
				} else if (OKMMail.getInstance().isValid(null, path)) {
					OKMMail.getInstance().move(null, path, fldPath);
				}
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (ItemExistsException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (LockException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (ExtensionException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AutomationException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}

		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General), pathErrors
					+ "\n\n" + error);
		}
	}

	@Override
	public void delete(List<String> paths) throws OKMException {
		log.debug("delete({})", paths);
		updateSessionManager();
		String error = "";
		String pathErrors = "";

		for (String path : paths) {
			try {
				if (OKMDocument.getInstance().isValid(null, path)) {
					OKMDocument.getInstance().delete(null, path);
				} else if (OKMFolder.getInstance().isValid(null, path)) {
					OKMFolder.getInstance().delete(null, path);
				} else if (OKMMail.getInstance().isValid(null, path)) {
					OKMMail.getInstance().delete(null, path);
				}
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (LockException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (ExtensionException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AutomationException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}

		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General), pathErrors
					+ "\n\n" + error);
		}
	}

	@Override
	public void addNote(List<String> paths, String text) throws OKMException {
		log.debug("addNote({},{})", paths, text);
		updateSessionManager();
		String error = "";
		String pathErrors = "";

		for (String path : paths) {
			try {
				OKMNote.getInstance().add(null, path, text);
			} catch (LockException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}

		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General), pathErrors
					+ "\n\n" + error);
		}
	}

	@Override
	public void addCategory(List<String> paths, String category) throws OKMException {
		log.debug("addCategory({},{})", paths, category);
		updateSessionManager();
		String error = "";
		String pathErrors = "";

		for (String path : paths) {
			try {
				OKMProperty.getInstance().addCategory(null, path, category);
			} catch (VersionException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (LockException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}

		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General), pathErrors
					+ "\n\n" + error);
		}
	}

	@Override
	public void removeCategory(List<String> paths, String category) throws OKMException {
		log.debug("addCategory({},{})", paths, category);
		updateSessionManager();
		String error = "";
		String pathErrors = "";

		for (String path : paths) {
			try {
				OKMProperty.getInstance().removeCategory(null, path, category);
			} catch (VersionException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (LockException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}

		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General), pathErrors
					+ "\n\n" + error);
		}
	}

	@Override
	public void addKeyword(List<String> paths, String keyword) throws OKMException {
		log.debug("addKeyword({},{})", paths, keyword);
		updateSessionManager();
		String error = "";
		String pathErrors = "";

		for (String path : paths) {
			try {
				OKMProperty.getInstance().addKeyword(null, path, keyword);
			} catch (VersionException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (LockException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}

		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General), pathErrors
					+ "\n\n" + error);
		}
	}

	@Override
	public void removeKeyword(List<String> paths, String keyword) throws OKMException {
		log.debug("addKeyword({},{})", paths, keyword);
		updateSessionManager();
		String error = "";
		String pathErrors = "";

		for (String path : paths) {
			try {
				OKMProperty.getInstance().removeKeyword(null, path, keyword);
			} catch (VersionException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (LockException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}

		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General), pathErrors
					+ "\n\n" + error);
		}
	}

	@Override
	public void addPropertyGroup(List<String> paths, String grpName) throws OKMException {
		log.debug("addPropertyGroup({}, {})", paths, grpName);
		updateSessionManager();
		String error = "";
		String pathErrors = "";

		for (String path : paths) {
			try {
				boolean hasPropertyGroup = false;
				for (PropertyGroup pg : OKMPropertyGroup.getInstance().getGroups(null, path)) {
					if (pg.getName().equals(grpName)) {
						hasPropertyGroup = true;
						break;
					}
				}

				if (!hasPropertyGroup) {
					OKMPropertyGroup.getInstance().addGroup(null, path, grpName);
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (ParseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (NoSuchGroupException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (LockException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (ExtensionException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AutomationException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}

		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General), pathErrors
					+ "\n\n" + error);
		}
	}

	@Override
	public void setProperties(List<String> paths, String grpName, List<GWTFormElement> formProperties) throws OKMException {
		log.debug("setProperties({}, {}, {})", new Object[]{paths, grpName, formProperties});
		updateSessionManager();
		String error = "";
		String pathErrors = "";

		// Properties conversion to be added
		List<FormElement> properties = new ArrayList<FormElement>();
		for (GWTFormElement gWTformElement : formProperties) {
			properties.add(GWTUtil.copy(gWTformElement));
		}

		for (String path : paths) {
			try {
				OKMPropertyGroup.getInstance().setProperties(null, path, grpName, properties);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (ParseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (NoSuchPropertyException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (NoSuchGroupException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (LockException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (ExtensionException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AutomationException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}

		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General), pathErrors
					+ "\n\n" + error);
		}
	}

	@Override
	public void lock(List<String> paths) throws OKMException {
		log.debug("lock({})", paths);
		updateSessionManager();
		String error = "";
		String pathErrors = "";

		for (String path : paths) {
			try {
				OKMDocument.getInstance().lock(null, path);
			} catch (LockException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}

		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General), pathErrors
					+ "\n\n" + error);
		}
	}

	@Override
	public void unlock(List<String> paths) throws OKMException {
		log.debug("unlock({})", paths);
		updateSessionManager();
		String error = "";
		String pathErrors = "";

		for (String path : paths) {
			try {
				if (getThreadLocalRequest().isUserInRole(Config.DEFAULT_ADMIN_ROLE)) {
					OKMDocument.getInstance().forceUnlock(null, path);
				} else {
					OKMDocument.getInstance().unlock(null, path);
				}
			} catch (LockException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}

		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General), pathErrors
					+ "\n\n" + error);
		}
	}

	@Override
	public void notify(List<String> uuids, String mails, String users, String roles, String message, boolean attachment) throws OKMException {
		log.debug("notify({}, {}, {}, {}, {})", new Object[]{uuids, mails, users, roles, message, attachment});
		updateSessionManager();

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

			List<String> mailList = MailUtils.parseMailList(mails);
			new DbNotificationModule().notify(null, uuids, userNames, mailList, message, attachment);
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMNotifyService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMNotifyService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMNotifyService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMNotifyService, ErrorCode.CAUSE_PrincipalAdapter),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMNotifyService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMNotifyService, ErrorCode.CAUSE_IO), e.getMessage());
		}

		log.debug("notify: void");
	}

	@Override
	public void forwardMail(List<String> uuids, String mails, String users, String roles, String message) throws OKMException {
		log.debug("forwardMail({}, {}, {}, {}, {})", new Object[]{uuids, mails, users, roles, message});
		updateSessionManager();

		try {
			List<String> userNames = new ArrayList<String>(Arrays.asList(users.isEmpty() ? new String[0] : users.split(",")));
			List<String> roleNames = new ArrayList<String>(Arrays.asList(roles.isEmpty() ? new String[0] : roles.split(",")));
			List<String> to = new ArrayList<String>(MailUtils.parseMailList(mails));

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

			for (String uuid : uuids) {
				MailUtils.forwardMail(null, from, to, message, uuid);
			}
		} catch (PrincipalAdapterException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PrincipalAdapter),
					e.getMessage());
		} catch (MessagingException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Messaging), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (DatabaseException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}

	@Override
	public void setMixedProperties(List<String> uuidList, List<GWTFormElement> formProperties, boolean recursive) throws OKMException {
		log.debug("setMixedProperties({}, {})", new Object[]{uuidList, formProperties, recursive});
		Map<String, List<FormElement>> groupElements = new HashMap<String, List<FormElement>>();
		updateSessionManager();
		String error = "";
		String pathErrors = "";

		// Prepare data
		try {
			for (Entry<PropertyGroup, List<FormElement>> entry : FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML).entrySet()) {
				List<FormElement> grpEltos = new ArrayList<FormElement>();

				for (FormElement fe : entry.getValue()) {
					for (GWTFormElement gwtFe : formProperties) {
						if (fe.getName().equals(gwtFe.getName())) {
							grpEltos.add(GWTUtil.copy(gwtFe));
						}
					}
				}

				if (!grpEltos.isEmpty()) {
					groupElements.put(entry.getKey().getName(), grpEltos);
				}
			}
		} catch (IOException e) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_Parse), e.getMessage());
		}

		for (String uuid : uuidList) {
			try {
				for (Entry<String, List<FormElement>> entry : groupElements.entrySet()) {
					if (OKMPropertyGroup.getInstance().hasGroup(null, uuid, entry.getKey())) {
						OKMPropertyGroup.getInstance().setProperties(null, uuid, entry.getKey(), entry.getValue());
					}
				}

				if (recursive && OKMFolder.getInstance().isValid(null, uuid)) {
					setMixedPropertiesHelper(uuid, groupElements);
				}
			} catch (LockException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + uuid;
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + uuid;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + uuid;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + uuid;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + uuid;
			} catch (ExtensionException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + uuid;
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + uuid;
			} catch (ParseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + uuid;
			} catch (NoSuchPropertyException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + uuid;
			} catch (NoSuchGroupException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + uuid;
			} catch (AutomationException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + uuid;
			}
		}

		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General), pathErrors
					+ "\n\n" + error);
		}

		log.debug("setMixedProperties: void");
	}

	/**
	 * Process setMixedProperties recursion.
	 */
	private void setMixedPropertiesHelper(String uuid, Map<String, List<FormElement>> groupElements) throws PathNotFoundException,
			RepositoryException, DatabaseException, IOException, ParseException, NoSuchPropertyException, NoSuchGroupException,
			LockException, AccessDeniedException, ExtensionException, AutomationException {
		log.debug("setMixedPropertiesHelper({}, {})", uuid, groupElements);

		// Process documents
		for (Document doc : OKMDocument.getInstance().getChildren(null, uuid)) {
			for (Entry<String, List<FormElement>> entry : groupElements.entrySet()) {
				if (OKMPropertyGroup.getInstance().hasGroup(null, doc.getUuid(), entry.getKey())) {
					OKMPropertyGroup.getInstance().setProperties(null, doc.getUuid(), entry.getKey(), entry.getValue());
				}
			}
		}

		// Process mails
		for (Mail mail : OKMMail.getInstance().getChildren(null, uuid)) {
			for (Entry<String, List<FormElement>> entry : groupElements.entrySet()) {
				if (OKMPropertyGroup.getInstance().hasGroup(null, mail.getUuid(), entry.getKey())) {
					OKMPropertyGroup.getInstance().setProperties(null, mail.getUuid(), entry.getKey(), entry.getValue());
				}
			}
		}

		// Process folders
		for (Folder fld : OKMFolder.getInstance().getChildren(null, uuid)) {
			for (Entry<String, List<FormElement>> entry : groupElements.entrySet()) {
				if (OKMPropertyGroup.getInstance().hasGroup(null, fld.getUuid(), entry.getKey())) {
					OKMPropertyGroup.getInstance().setProperties(null, fld.getUuid(), entry.getKey(), entry.getValue());
				}
			}

			// Go deeper
			setMixedPropertiesHelper(fld.getUuid(), groupElements);
		}
	}
}