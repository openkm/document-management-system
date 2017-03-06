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

package com.openkm.extension.servlet;

import com.openkm.api.*;
import com.openkm.automation.AutomationException;
import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Mail;
import com.openkm.bean.Permission;
import com.openkm.core.*;
import com.openkm.dao.ConfigDAO;
import com.openkm.extension.core.ExtensionException;
import com.openkm.extension.frontend.client.bean.GWTMacros;
import com.openkm.extension.frontend.client.service.OKMMacrosService;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.servlet.frontend.OKMRemoteServiceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MacrosServlet
 *
 * @author jllort
 *
 */
public class MacrosServlet extends OKMRemoteServiceServlet implements OKMMacrosService {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(MacrosServlet.class);

	@Override
	public List<GWTMacros> getActions() throws OKMException {
		updateSessionManager();
		List<GWTMacros> actionList = new ArrayList<GWTMacros>();

		try {
			String actions[] = ConfigDAO.getText("macros.actions", "").split("\r\n");

			for (String action : actions) {
				String act[] = action.split(",");

				if (act.length > 1) {
					// Path fix: all paths must finish with "/" for comparison
					if (!act[0].endsWith("/")) {
						act[0] += "/";
					}

					if (!act[1].endsWith("/")) {
						act[1] += "/";
					}

					GWTMacros fastAction = new GWTMacros();
					fastAction.setPathOrigin(act[0]);
					fastAction.setPathDestination(act[1]);
					actionList.add(fastAction);
				}
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFastActionService, ErrorCode.CAUSE_Database),
					e.getMessage());
		}

		return actionList;
	}

	@Override
	public void executeAction(GWTMacros action, String path) throws OKMException {
		updateSessionManager();
		String orgPath = action.getPathOrigin(); // Origin path
		String dstPath = action.getPathDestination(); // Destination path

		// All paths should not finish with / althought must use for comparasion
		if (orgPath.endsWith("/")) {
			orgPath = orgPath.substring(0, orgPath.length() - 1);
		}

		if (dstPath.endsWith("/")) {
			dstPath = dstPath.substring(0, dstPath.length() - 1);
		}

		try {
			// Case when on destination should create folders to store documents
			String rightPath = path.substring(orgPath.length() + 1); // Right path

			if (rightPath.indexOf("/") >= 0) {
				String extraFldPath = rightPath.substring(0, rightPath.lastIndexOf("/"));
				String destFoldersToCreate[] = extraFldPath.split("/");
				for (int i = 0; i < destFoldersToCreate.length; i++) { // Put all destination files path
					if (i == 0) {
						destFoldersToCreate[i] = dstPath + "/" + destFoldersToCreate[i];
					} else {
						destFoldersToCreate[i] = destFoldersToCreate[i - 1] + "/" + destFoldersToCreate[i];
					}
				}
				createFolders(orgPath, dstPath, destFoldersToCreate);
			}

			// Move to destination
			String dstPathToMove = dstPath + "/" + rightPath;
			String dstParentFolder = dstPathToMove.substring(0, dstPathToMove.lastIndexOf("/"));

			if (OKMDocument.getInstance().isValid(null, path)) {
				OKMDocument.getInstance().move(null, path, dstParentFolder);
			} else if (OKMMail.getInstance().isValid(null, path)) {
				OKMMail.getInstance().move(null, path, dstParentFolder);
			} else if (OKMFolder.getInstance().isValid(null, path)) {
				// Control case if folder exists
				if (!OKMRepository.getInstance().hasNode(null, dstPathToMove)) {
					OKMFolder.getInstance().move(null, path, dstParentFolder);
				} else {
					moveFolderContents(path, dstPathToMove);
					OKMFolder.getInstance().delete(null, path); // delete folder ( yet exists and contents has been yet
					// moved )
				}
			}
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFastActionService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFastActionService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFastActionService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFastActionService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (ItemExistsException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFastActionService, ErrorCode.CAUSE_ItemExists),
					e.getMessage());
		} catch (ExtensionException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFastActionService, ErrorCode.CAUSE_Extension),
					e.getMessage());
		} catch (AutomationException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFastActionService, ErrorCode.CAUSE_Automation),
					e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFastActionService, ErrorCode.CAUSE_Lock), e.getMessage());
		}
	}

	/**
	 * moveFolderContents
	 */
	private void moveFolderContents(String fldPath, String dstPathToMove) throws PathNotFoundException, RepositoryException,
			DatabaseException, ItemExistsException, AccessDeniedException, LockException, ExtensionException,
			AutomationException {
		// Move documents
		for (Document doc : OKMDocument.getInstance().getChildren(null, fldPath)) {
			OKMDocument.getInstance().move(null, doc.getPath(), dstPathToMove);
		}

		// Move Mails
		for (Mail mail : OKMMail.getInstance().getChildren(null, fldPath)) {
			OKMMail.getInstance().move(null, mail.getPath(), dstPathToMove);
		}

		// Move subfolders
		for (Folder fld : OKMFolder.getInstance().getChildren(null, fldPath)) {
			String fldToMove = dstPathToMove + "/" + fld.getPath().substring(fld.getPath().lastIndexOf("/") + 1);
			// Control case if folder exists
			if (!OKMRepository.getInstance().hasNode(null, fldToMove)) {
				OKMFolder.getInstance().move(null, fld.getPath(), dstPathToMove);
			} else {
				moveFolderContents(fld.getPath(), fldToMove);
				OKMFolder.getInstance().delete(null, fld.getPath()); // delete folder ( yet exists and contents has been
				// yet moved )
			}
		}
	}

	/**
	 * createFolders
	 */
	private void createFolders(String orgPath, String dstPath, String folders[]) throws OKMException, RepositoryException,
			DatabaseException, PathNotFoundException, ItemExistsException, AccessDeniedException, ExtensionException,
			AutomationException {
		for (String folder : folders) {
			if (!OKMRepository.getInstance().hasNode(null, folder)) {
				Folder fld = new Folder();
				fld.setPath(folder);
				OKMFolder.getInstance().create(null, fld);

				// Change security
				String originPath = folder.replaceFirst(dstPath, orgPath);
				Map<String, Integer> dstRolesMap = OKMAuth.getInstance().getGrantedRoles(null, folder);
				Map<String, Integer> dstUsersMap = OKMAuth.getInstance().getGrantedUsers(null, folder);
				Map<String, Integer> orgRolesMap = OKMAuth.getInstance().getGrantedRoles(null, originPath);
				Map<String, Integer> orgUsersMap = OKMAuth.getInstance().getGrantedUsers(null, originPath);

				// Add full grants to actual remote user
				String remoteUser = getThreadLocalRequest().getRemoteUser();
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

				OKMAuth.getInstance().grantUser(null, folder, remoteUser, allGrants, false);

				// Remove all grants except remote user
				for (String role : dstRolesMap.keySet()) {
					OKMAuth.getInstance().revokeRole(null, folder, role, allGrants, false);
				}

				for (String user : dstUsersMap.keySet()) {
					if (!remoteUser.equals(user)) {
						OKMAuth.getInstance().revokeUser(null, folder, user, allGrants, false);
					}
				}

				// Setting privileges except remote user
				for (String role : orgRolesMap.keySet()) {
					OKMAuth.getInstance().grantRole(null, folder, role, orgRolesMap.get(role).intValue(), false);
				}

				for (String user : orgUsersMap.keySet()) {
					if (!remoteUser.equals(user)) {
						OKMAuth.getInstance().grantUser(null, folder, user, orgUsersMap.get(user).intValue(), false);
					}
				}

				// Setting user privileges if exists, otherside revokes all
				if (orgUsersMap.containsKey(remoteUser)) {
					int permission = orgUsersMap.get(remoteUser).intValue();

					if ((permission & Permission.READ) != Permission.READ) {
						OKMAuth.getInstance().revokeUser(null, folder, remoteUser, Permission.READ, false);
					}

					if ((permission & Permission.DELETE) != Permission.DELETE) {
						OKMAuth.getInstance().revokeUser(null, folder, remoteUser, Permission.DELETE, false);
					}

					if ((permission & Permission.WRITE) != Permission.WRITE) {
						OKMAuth.getInstance().revokeUser(null, folder, remoteUser, Permission.WRITE, false);
					}

					if ((permission & Permission.SECURITY) != Permission.SECURITY) {
						OKMAuth.getInstance().revokeUser(null, folder, remoteUser, Permission.SECURITY, false);
					}

					if ((Config.SECURITY_EXTENDED_MASK & Permission.PROPERTY_GROUP) == Permission.PROPERTY_GROUP) {
						if ((permission & Permission.PROPERTY_GROUP) != Permission.PROPERTY_GROUP) {
							OKMAuth.getInstance().revokeUser(null, folder, remoteUser, Permission.PROPERTY_GROUP, false);
						}
					}

					if ((Config.SECURITY_EXTENDED_MASK & Permission.COMPACT_HISTORY) == Permission.COMPACT_HISTORY) {
						if ((permission & Permission.COMPACT_HISTORY) != Permission.COMPACT_HISTORY) {
							OKMAuth.getInstance().revokeUser(null, folder, remoteUser, Permission.COMPACT_HISTORY, false);
						}
					}

					if ((Config.SECURITY_EXTENDED_MASK & Permission.DOWNLOAD) == Permission.DOWNLOAD) {
						if ((permission & Permission.DOWNLOAD) != Permission.DOWNLOAD) {
							OKMAuth.getInstance().revokeUser(null, folder, remoteUser, Permission.DOWNLOAD, false);
						}
					}
				} else {
					OKMAuth.getInstance().revokeUser(null, folder, remoteUser, allGrants, false);
				}
			}
		}
	}
}