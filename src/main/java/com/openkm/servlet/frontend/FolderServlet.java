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

import com.openkm.api.OKMFolder;
import com.openkm.api.OKMPropertyGroup;
import com.openkm.api.OKMSearch;
import com.openkm.bean.Folder;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.Repository;
import com.openkm.bean.form.*;
import com.openkm.core.*;
import com.openkm.dao.KeyValueDAO;
import com.openkm.dao.bean.KeyValue;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMFolderService;
import com.openkm.frontend.client.widget.filebrowser.GWTFilter;
import com.openkm.servlet.frontend.util.FolderComparator;
import com.openkm.util.GWTUtil;
import com.openkm.util.pagination.FilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Servlet Class
 */
public class FolderServlet extends OKMRemoteServiceServlet implements OKMFolderService {
	private static Logger log = LoggerFactory.getLogger(FolderServlet.class);
	private static final long serialVersionUID = -4436438730167948558L;

	@Override
	public GWTFolder create(String fldPath, String fldPathParent) throws OKMException {
		log.debug("create({}, {})", fldPath, fldPathParent);
		GWTFolder gWTFolder = new GWTFolder();
		Folder folder = new Folder();
		folder.setPath(fldPathParent + "/" + fldPath);
		updateSessionManager();

		try {
			gWTFolder = GWTUtil.copy(OKMFolder.getInstance().create(null, folder), getUserWorkspaceSession());
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_ItemExists), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("create: {}", gWTFolder);
		return gWTFolder;
	}

	@Override
	public void delete(String fldPath) throws OKMException {
		log.debug("delete({})", fldPath);
		updateSessionManager();

		try {
			OKMFolder.getInstance().delete(null, fldPath);
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("delete: void");
	}

	@Override
	public List<GWTFolder> getCategorizedChilds(String fldPath, Map<String, GWTFilter> mapFilter) throws OKMException {
		log.debug("getCategorizedChilds({})", fldPath);
		List<GWTFolder> folderList = new ArrayList<GWTFolder>();
		updateSessionManager();

		try {
			if (fldPath.startsWith("/" + Repository.CATEGORIES)) {
				// TODO: Possible optimization getting folder really could not be needed we've got UUID in GWT UI
				String uuid = OKMFolder.getInstance().getProperties(null, fldPath).getUuid();
				List<Folder> results = OKMSearch.getInstance().getCategorizedFolders(null, uuid);

				for (Folder folder : results) {
					folderList.add(GWTUtil.copy(folder, getUserWorkspaceSession()));
				}
			}
			if (mapFilter != null) {
				FilterUtils.filter(getUserWorkspaceSession(), folderList, mapFilter);
			}
			Collections.sort(folderList, FolderComparator.getInstance(getLanguage()));
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getCategorizedChilds: {}", folderList);
		return folderList;
	}

	@Override
	public List<GWTFolder> getThesaurusChilds(String fldPath, Map<String, GWTFilter> mapFilter) throws OKMException {
		log.debug("getThesaurusChilds({})", fldPath);
		List<GWTFolder> folderList = new ArrayList<GWTFolder>();
		updateSessionManager();

		try {
			// Thesaurus childs
			if (fldPath.startsWith("/" + Repository.THESAURUS)) {
				String keyword = fldPath.substring(fldPath.lastIndexOf("/") + 1).replace(" ", "_");
				List<Folder> results = OKMSearch.getInstance().getFoldersByKeyword(null, keyword);

				for (Folder fld : results) {
					folderList.add(GWTUtil.copy(fld, getUserWorkspaceSession()));
				}
			}
			if (mapFilter != null) {
				FilterUtils.filter(getUserWorkspaceSession(), folderList, mapFilter);
			}
			Collections.sort(folderList, FolderComparator.getInstance(getLanguage()));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getThesaurusChilds: {}", folderList);
		return folderList;
	}

	@Override
	public List<GWTFolder> getMetadataChilds(String fldPath, Map<String, GWTFilter> mapFilter) throws OKMException {
		log.debug("getMetadataChilds({})", fldPath);
		List<GWTFolder> folderList = new ArrayList<GWTFolder>();
		updateSessionManager();

		try {
			// Metadata value level
			if (fldPath.startsWith("/" + Repository.METADATA) && (fldPath.split("/").length - 1) == 4) {
				String subFolder[] = fldPath.split("/");
				String group = subFolder[2];
				String property = subFolder[3];
				String value = subFolder[4];
				List<Folder> results = OKMSearch.getInstance().getFoldersByPropertyValue(null, group, property, value);

				for (Folder fld : results) {
					folderList.add(GWTUtil.copy(fld, getUserWorkspaceSession()));
				}
			}
			if (mapFilter != null) {
				FilterUtils.filter(getUserWorkspaceSession(), folderList, mapFilter);
			}
			Collections.sort(folderList, FolderComparator.getInstance(getLanguage()));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getMetadataChilds: {}", folderList);
		return folderList;
	}

	@Override
	public List<GWTFolder> getChilds(String fldPath, boolean extraColumns, Map<String, GWTFilter> mapFilter) throws OKMException {
		log.debug("getChilds({})", fldPath);
		List<GWTFolder> folderList = new ArrayList<GWTFolder>();
		updateSessionManager();

		try {
			if (fldPath.startsWith("/" + Repository.METADATA)) {
				switch (fldPath.split("/").length - 1) {
					case 1: // getting group level
						for (PropertyGroup pg : OKMPropertyGroup.getInstance().getAllGroups(null)) {
							boolean hasValidChilds = false; // Only represent metadata groups which have some
							// representable object ( select or suggestbox )

							for (FormElement formElement : OKMPropertyGroup.getInstance().getPropertyGroupForm(null, pg.getName())) {
								if (formElement instanceof Select || formElement instanceof SuggestBox || formElement instanceof CheckBox) {
									hasValidChilds = true;
									break;
								}
							}

							if (hasValidChilds) {
								GWTFolder gWTFolder = new GWTFolder();
								String path = fldPath + "/" + pg.getName();
								gWTFolder.initMetadata(path, pg.getLabel(),
										OKMPropertyGroup.getInstance().getPropertyGroupForm(null, pg.getName()).size() > 0);
								folderList.add(gWTFolder);
							}
						}
						break;

					case 2: // getting property level
						String grpName = fldPath.substring(fldPath.lastIndexOf("/") + 1);
						for (FormElement formElement : OKMPropertyGroup.getInstance().getPropertyGroupForm(null, grpName)) {
							if (formElement instanceof Select || formElement instanceof SuggestBox || formElement instanceof CheckBox) {
								GWTFolder gWTFolder = new GWTFolder();
								String path = fldPath + "/" + formElement.getName();
								gWTFolder.initMetadata(path, formElement.getLabel(), false);
								folderList.add(gWTFolder);
							}
						}
						break;

					case 3: // getting value level
						String subFolder[] = fldPath.split("/");
						grpName = subFolder[2];
						String propertyName = subFolder[3];

						for (FormElement formElement : OKMPropertyGroup.getInstance().getPropertyGroupForm(null, grpName)) {
							if (formElement.getName().equals(propertyName)) {
								if (formElement instanceof Select) {
									for (Option option : ((Select) formElement).getOptions()) {
										GWTFolder gWTFolder = new GWTFolder();
										String path = fldPath + "/" + option.getValue();
										gWTFolder.initMetadata(path, option.getLabel(), false);
										folderList.add(gWTFolder);
									}
								} else if (formElement instanceof SuggestBox) {
									SuggestBox sb = ((SuggestBox) formElement);
									String sqlFilter = sb.getFilterQuery();

									// replacing filter parameter, if exist
									sqlFilter = sqlFilter.replaceAll("\\{0\\}", "");

									for (KeyValue keyValue : KeyValueDAO.getKeyValues(sb.getTable(), sqlFilter)) {
										GWTFolder gWTFolder = new GWTFolder();
										String path = fldPath + "/" + keyValue.getKey();
										gWTFolder.initMetadata(path, keyValue.getValue(), false);
										folderList.add(gWTFolder);
									}
								} else if (formElement instanceof CheckBox) {
									GWTFolder gWTFolder = new GWTFolder();
									String path = fldPath + "/true";
									gWTFolder.initMetadata(path, "true", false);
									folderList.add(gWTFolder);
									gWTFolder = new GWTFolder();
									path = fldPath + "/false";
									gWTFolder.initMetadata(path, "false", false);
									folderList.add(gWTFolder);
								}
								break;
							}
						}
						break;
				}
			} else {
				for (Folder folder : OKMFolder.getInstance().getChildren(null, fldPath)) {
					GWTFolder gWTFolder = (extraColumns) ? GWTUtil.copy(folder, getUserWorkspaceSession()) : GWTUtil.copy(folder, null);
					folderList.add(gWTFolder);
				}
			}

			if (mapFilter != null) {
				FilterUtils.filter(getUserWorkspaceSession(), folderList, mapFilter);
			}

			Collections.sort(folderList, FolderComparator.getInstance(getLanguage()));
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getChilds: {}", folderList);
		return folderList;
	}

	@Override
	public GWTFolder rename(String fldId, String newName) throws OKMException {
		log.debug("rename({}, {})", fldId, newName);
		GWTFolder gWTFolder = new GWTFolder();
		updateSessionManager();

		try {
			gWTFolder = GWTUtil.copy(OKMFolder.getInstance().rename(null, fldId, newName), getUserWorkspaceSession());
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_ItemExists), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("rename: {}", gWTFolder);
		return gWTFolder;
	}

	@Override
	public void move(String fldPath, String dstPath) throws OKMException {
		log.debug("move({}, {})", fldPath, dstPath);
		updateSessionManager();

		try {
			OKMFolder.getInstance().move(null, fldPath, dstPath);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_ItemExists), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("move: void");
	}

	@Override
	public void purge(String fldPath) throws OKMException {
		log.debug("purge({})", fldPath);
		updateSessionManager();

		try {
			OKMFolder.getInstance().purge(null, fldPath);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("purge: void");
	}

	@Override
	public GWTFolder getProperties(String fldPath) throws OKMException {
		log.debug("getProperties({})", fldPath);
		GWTFolder gWTFolder = new GWTFolder();
		updateSessionManager();

		try {
			if (fldPath.startsWith("/" + Repository.METADATA)) {
				String grpName = "";
				String propertyName = "";
				String subFolder[] = fldPath.split("/");

				switch (fldPath.split("/").length - 1) {
					case 1:
						gWTFolder.initMetadata(fldPath, OKMPropertyGroup.getInstance().getAllGroups(null).size() > 0);
						break;

					case 2: // group level
						grpName = fldPath.substring(fldPath.lastIndexOf("/") + 1);
						String label = "";

						for (PropertyGroup pg : OKMPropertyGroup.getInstance().getAllGroups(null)) {
							if (pg.getName().equals(grpName)) {
								label = pg.getLabel();
								break;
							}
						}

						gWTFolder.initMetadata(fldPath, label,
								OKMPropertyGroup.getInstance().getPropertyGroupForm(null, grpName).size() > 0);
						break;

					case 3: // property level
						grpName = subFolder[2];
						propertyName = subFolder[3];

						for (FormElement formElement : OKMPropertyGroup.getInstance().getPropertyGroupForm(null, grpName)) {
							if (formElement.getName().equals(propertyName)) {
								gWTFolder.initMetadata(fldPath, formElement.getLabel(), false);
								break;
							}
						}
						break;

					case 4: // value level
						grpName = subFolder[2];
						propertyName = subFolder[3];
						String value = subFolder[4];

						for (FormElement formElement : OKMPropertyGroup.getInstance().getPropertyGroupForm(null, grpName)) {
							if (formElement.getName().equals(propertyName)) {
								if (formElement instanceof Select) {
									for (Option option : ((Select) formElement).getOptions()) {
										if (option.getValue().equals(value)) {
											gWTFolder.initMetadata(fldPath, option.getLabel(), false);
											break;
										}
									}
								} else if (formElement instanceof SuggestBox) {
									SuggestBox sb = ((SuggestBox) formElement);
									String sqlFilter = sb.getFilterQuery();
									sqlFilter = sqlFilter.replaceAll("\\{0\\}", ""); // replacing filter parameter, if
									// exist
									for (KeyValue keyValue : KeyValueDAO.getKeyValues(sb.getTable(), sqlFilter)) {
										if (keyValue.getKey().equals(value)) {
											gWTFolder.initMetadata(fldPath, keyValue.getValue(), false);
											break;
										}
									}
								} else if (formElement instanceof CheckBox) {
									gWTFolder.initMetadata(fldPath, value, false);
								}
								break;
							}
						}
						break;
				}
			} else {
				Folder fld = OKMFolder.getInstance().getProperties(null, fldPath);
				gWTFolder = GWTUtil.copy(fld, getUserWorkspaceSession());
			}
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getProperties: {}", gWTFolder);
		return gWTFolder;
	}

	@Override
	public void copy(String fldPath, String dstPath) throws OKMException {
		log.debug("copy({}, {})", fldPath, dstPath);
		updateSessionManager();

		try {
			OKMFolder.getInstance().copy(null, fldPath, dstPath);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_ItemExists), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("copy: void");
	}

	@Override
	public Boolean isValid(String fldPath) throws OKMException {
		log.debug("isValid({})", fldPath);
		updateSessionManager();

		try {
			// Evaluate special case metadata that really do not exist this path in repository
			if (fldPath.startsWith(("/" + Repository.METADATA))) {
				return true;
			} else {
				return Boolean.valueOf(OKMFolder.getInstance().isValid(null, fldPath));
			}
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}
	}
}
