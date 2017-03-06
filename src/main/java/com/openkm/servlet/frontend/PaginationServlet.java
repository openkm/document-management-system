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

import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Mail;
import com.openkm.bean.Repository;
import com.openkm.bean.pagination.FilterResult;
import com.openkm.bean.pagination.ObjectToOrder;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.*;
import com.openkm.dao.bean.NodeDocument;
import com.openkm.dao.bean.NodeDocumentVersion;
import com.openkm.dao.bean.NodeFolder;
import com.openkm.dao.bean.NodeMail;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.*;
import com.openkm.frontend.client.bean.form.*;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMPaginationService;
import com.openkm.frontend.client.widget.filebrowser.GWTFilter;
import com.openkm.module.db.base.BaseDocumentModule;
import com.openkm.module.db.base.BaseFolderModule;
import com.openkm.module.db.base.BaseMailModule;
import com.openkm.util.GWTUtil;
import com.openkm.util.SystemProfiling;
import com.openkm.util.pagination.FilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Servlet Class
 */
public class PaginationServlet extends OKMRemoteServiceServlet implements OKMPaginationService {
	private static Logger log = LoggerFactory.getLogger(PaginationServlet.class);
	private static final long serialVersionUID = 1L;

	@Override
	public GWTPaginated getChildrenPaginated(String fldPath, boolean extraColumns, int offset, int limit, int order,
	                                         boolean reverse, boolean folders, boolean documents, boolean mails, String selectedRowId,
	                                         Map<String, GWTFilter> mapFilter) throws OKMException {
		log.debug("getChildrenPaginated({})", fldPath);
		long begin = System.currentTimeMillis();
		GWTPaginated paginated = new GWTPaginated();
		List<Object> col = new ArrayList<Object>();
		List<Object> gwtCol = new ArrayList<Object>();
		String user = getThreadLocalRequest().getRemoteUser();
		paginated.setObjects(gwtCol);
		updateSessionManager();

		try {
			String fldUuid = "";
			// Calculating folder uuid only when is not metadata or thesaurus case
			if (!fldPath.startsWith("/" + Repository.THESAURUS) && !fldPath.startsWith("/" + Repository.METADATA)) {
				fldUuid = NodeBaseDAO.getInstance().getUuidFromPath(fldPath);
			}

			// Folders
			List<NodeFolder> colFolders = new ArrayList<NodeFolder>();

			if (fldPath.startsWith("/" + Repository.CATEGORIES)) {
				colFolders = NodeFolderDAO.getInstance().findByCategory(fldUuid);
			} else if (fldPath.startsWith("/" + Repository.THESAURUS)) {
				String keyword = fldPath.substring(fldPath.lastIndexOf("/") + 1).replace(" ", "_");
				colFolders = NodeFolderDAO.getInstance().findByKeyword(keyword);
			} else if (fldPath.startsWith("/" + Repository.METADATA)) {
				if ((fldPath.split("/").length - 1) == 4) {
					String subFolder[] = fldPath.split("/");
					String group = subFolder[2];
					String property = subFolder[3];
					String value = subFolder[4];
					colFolders = NodeFolderDAO.getInstance().findByPropertyValue(group, property, value);
				}
			} else {
				colFolders = NodeFolderDAO.getInstance().findByParent(fldUuid);
			}

			paginated.setTotalFolder(colFolders.size());

			if (folders) {
				col.addAll(colFolders); // Add in one collection to make full ordering
			}

			// Documents
			List<NodeDocument> colDocuments = new ArrayList<NodeDocument>();

			if (fldPath.startsWith("/" + Repository.CATEGORIES)) {
				colDocuments = NodeDocumentDAO.getInstance().findByCategory(fldUuid);
			} else if (fldPath.startsWith("/" + Repository.THESAURUS)) {
				String keyword = fldPath.substring(fldPath.lastIndexOf("/") + 1).replace(" ", "_");
				colDocuments = NodeDocumentDAO.getInstance().findByKeyword(keyword);
			} else if (fldPath.startsWith("/" + Repository.METADATA)) {
				// Case metadata at value level
				if (fldPath.split("/").length - 1 == 4) {
					String subFolder[] = fldPath.split("/");
					String group = subFolder[2];
					String property = subFolder[3];
					String value = subFolder[4];
					colDocuments = NodeDocumentDAO.getInstance().findByPropertyValue(group, property, value);
				}
			} else {
				colDocuments = NodeDocumentDAO.getInstance().findByParent(fldUuid);
			}

			paginated.setTotalDocuments(colDocuments.size());

			if (documents) {
				col.addAll(colDocuments); // Add in one collection to make full ordering
			}

			// Mails
			List<NodeMail> colMails = new ArrayList<NodeMail>();

			if (fldPath.startsWith("/" + Repository.CATEGORIES)) {
				colMails = NodeMailDAO.getInstance().findByCategory(fldUuid);
			} else if (fldPath.startsWith("/" + Repository.THESAURUS)) {
				String keyword = fldPath.substring(fldPath.lastIndexOf("/") + 1).replace(" ", "_");
				colMails = NodeMailDAO.getInstance().findByKeyword(keyword);
			} else if (fldPath.startsWith("/" + Repository.METADATA)) {
				// Case metadata value level
				if (fldPath.split("/").length - 1 == 4) {
					String subFolder[] = fldPath.split("/");
					String group = subFolder[2];
					String property = subFolder[3];
					String value = subFolder[4];
					colMails = NodeMailDAO.getInstance().findByPropertyValue(group, property, value);
				}
			} else {
				colMails = NodeMailDAO.getInstance().findByParent(fldUuid);
			}

			paginated.setTotalMails(colMails.size());

			if (mails) {
				col.addAll(colMails); // Add in one collection to make full ordering
			}

			// Filtering
			GWTWorkspace workspace = getUserWorkspaceSession();
			FilterResult fr = FilterUtils.filter(workspace, col, order, selectedRowId, mapFilter);
			List<ObjectToOrder> convertedCol = fr.getConvertedCol();
			int selectedRow = fr.getSelectedRow();

			// Setting total
			int total = convertedCol.size();
			paginated.setTotal(total);

			// Testing offset error ( trying to find correct offset ).
			if (offset >= total) {
				if (total == 0 || total <= limit) {
					offset = 0;
				} else if (total % limit != 0) { // case there's some remainder
					offset = (total / limit) * limit;
				} else { // case exact division
					offset = (total / limit) * limit;
					offset = offset - limit; // back limit to see latest values
				}

				paginated.setOutOfRange(true);
				paginated.setNewOffset(offset);
			}

			// Test for selectedRowId between offset - offset+limit otherwise changes offset
			if (selectedRow != -1) {
				if ((offset > selectedRow) || (selectedRow > (offset + limit))) {
					if (selectedRow == 0) {
						offset = 0;
					} else {
						offset = (selectedRow / limit) * limit;
					}

					paginated.setOutOfRange(true);
					paginated.setNewOffset(offset);
				}
			}

			// When selectedRowId any filtering, ordering, reverse etc.. value is empty
			// Ordering
			switch (order) {
				case GWTPaginated.COL_NONE:
					// No ordering
					break;

				case GWTPaginated.COL_TYPE:
					// Ordering as has been added
					break;

				case GWTPaginated.COL_NAME:
					Collections.sort(convertedCol, OrderByName.getInstance());
					break;

				case GWTPaginated.COL_SIZE:
					Collections.sort(convertedCol, OrderBySize.getInstance());
					break;

				case GWTPaginated.COL_DATE:
					Collections.sort(convertedCol, OrderByDate.getInstance());
					break;

				case GWTPaginated.COL_AUTHOR:
					Collections.sort(convertedCol, OrderByAuthor.getInstance());
					break;

				case GWTPaginated.COL_VERSION:
					Collections.sort(convertedCol, OrderByVersion.getInstance());
					break;

				case GWTPaginated.COL_COLUMN0:
					Collections.sort(convertedCol, OrderByColumn.getInstance());
					break;

				case GWTPaginated.COL_COLUMN1:
					Collections.sort(convertedCol, OrderByColumn.getInstance());
					break;

				case GWTPaginated.COL_COLUMN2:
					Collections.sort(convertedCol, OrderByColumn.getInstance());
					break;

				case GWTPaginated.COL_COLUMN3:
					Collections.sort(convertedCol, OrderByColumn.getInstance());
					break;

				case GWTPaginated.COL_COLUMN4:
					Collections.sort(convertedCol, OrderByColumn.getInstance());
					break;

				case GWTPaginated.COL_COLUMN5:
					Collections.sort(convertedCol, OrderByColumn.getInstance());
					break;

				case GWTPaginated.COL_COLUMN6:
					Collections.sort(convertedCol, OrderByColumn.getInstance());
					break;

				case GWTPaginated.COL_COLUMN7:
					Collections.sort(convertedCol, OrderByColumn.getInstance());
					break;

				case GWTPaginated.COL_COLUMN8:
					Collections.sort(convertedCol, OrderByColumn.getInstance());
					break;

				case GWTPaginated.COL_COLUMN9:
					Collections.sort(convertedCol, OrderByColumn.getInstance());
					break;
			}

			// Reverse ordering
			if (reverse) {
				Collections.reverse(convertedCol);
			}

			// Copy based in offset - limit
			int actual = 0;

			if (actual <= offset && offset <= (actual + convertedCol.size())) {
				boolean found = false;
				found = (actual == offset);

				for (ObjectToOrder obj : convertedCol) {
					// Jump to actual
					if (found) {
						if (actual < offset + limit) {
							if (obj.getObj() instanceof NodeFolder) {
								Folder fld = BaseFolderModule.getProperties(user, (NodeFolder) obj.getObj());
								GWTFolder gWTFolder = (extraColumns) ? GWTUtil.copy(fld, workspace)
										: GWTUtil.copy(fld, null);
								gwtCol.add(gWTFolder);
							} else if (obj.getObj() instanceof NodeDocument) {
								Document doc = BaseDocumentModule.getProperties(user, (NodeDocument) obj.getObj());
								GWTDocument gWTDoc = (extraColumns) ? GWTUtil.copy(doc, workspace)
										: GWTUtil.copy(doc, null);
								gwtCol.add(gWTDoc);
							} else if (obj.getObj() instanceof NodeMail) {
								Mail mail = BaseMailModule.getProperties(user, (NodeMail) obj.getObj());
								GWTMail gWTMail = (extraColumns) ? GWTUtil.copy(mail, workspace)
										: GWTUtil.copy(mail, null);
								gwtCol.add(gWTMail);
							}
						} else {
							break;
						}
					}

					actual++;

					if (!found) {
						found = (actual == offset);
					}
				}
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General),
					e.getMessage());
		}

		SystemProfiling.log(fldPath, System.currentTimeMillis() - begin);
		log.trace("getChildrenPaginated.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getChildrenPaginated: {}", paginated);
		return paginated;
	}

	/**
	 * OrderByName
	 *
	 * @author jllort
	 *
	 */
	private static class OrderByName implements Comparator<ObjectToOrder> {
		private static final Comparator<ObjectToOrder> INSTANCE = new OrderByName();

		public static Comparator<ObjectToOrder> getInstance() {
			return INSTANCE;
		}

		public int compare(ObjectToOrder arg0, ObjectToOrder arg1) {
			String value0 = "";
			String value1 = "";

			if (arg0.getObj() instanceof NodeFolder) {
				value0 = ((NodeFolder) arg0.getObj()).getName();
			} else if (arg0.getObj() instanceof NodeDocument) {
				value0 = ((NodeDocument) arg0.getObj()).getName();
			} else if (arg0.getObj() instanceof NodeMail) {
				value0 = ((NodeMail) arg0.getObj()).getSubject();
			}

			if (arg1.getObj() instanceof NodeFolder) {
				value1 = ((NodeFolder) arg1.getObj()).getName();
			} else if (arg1.getObj() instanceof NodeDocument) {
				value1 = ((NodeDocument) arg1.getObj()).getName();
			} else if (arg0.getObj() instanceof NodeMail) {
				value1 = ((NodeMail) arg1.getObj()).getSubject();
			}

			return value0.compareTo(value1);
		}
	}

	/**
	 * OrderBySize
	 *
	 * @author jllort
	 *
	 */
	private static class OrderBySize implements Comparator<ObjectToOrder> {
		private static final Comparator<ObjectToOrder> INSTANCE = new OrderBySize();

		public static Comparator<ObjectToOrder> getInstance() {
			return INSTANCE;
		}

		public int compare(ObjectToOrder arg0, ObjectToOrder arg1) {
			long value0 = 0;
			long value1 = 0;

			if (arg0.getObj() instanceof NodeDocument) {
				try {
					String docUuid = ((NodeDocument) arg0.getObj()).getUuid();
					NodeDocumentVersion nDocVer = NodeDocumentVersionDAO.getInstance().findCurrentVersion(docUuid);
					value0 = nDocVer.getSize();
				} catch (Exception e) {
					// Ignore
				}
			} else if (arg0.getObj() instanceof NodeMail) {
				value0 = ((NodeMail) arg0.getObj()).getSize();
			}

			if (arg1.getObj() instanceof NodeDocument) {
				try {
					String docUuid = ((NodeDocument) arg1.getObj()).getUuid();
					NodeDocumentVersion nDocVer = NodeDocumentVersionDAO.getInstance().findCurrentVersion(docUuid);
					value1 = nDocVer.getSize();
				} catch (Exception e) {
					// Ignore
				}
			} else if (arg1.getObj() instanceof NodeMail) {
				value1 = ((NodeMail) arg1.getObj()).getSize();
			}

			return new Long(value0 - value1).intValue();
		}
	}

	/**
	 * OrderByDate
	 *
	 * @author jllort
	 *
	 */
	private static class OrderByDate implements Comparator<ObjectToOrder> {
		private static final Comparator<ObjectToOrder> INSTANCE = new OrderByDate();

		public static Comparator<ObjectToOrder> getInstance() {
			return INSTANCE;
		}

		public int compare(ObjectToOrder arg0, ObjectToOrder arg1) {
			Calendar value0 = Calendar.getInstance();
			Calendar value1 = Calendar.getInstance();

			if (arg0.getObj() instanceof NodeFolder) {
				value0 = ((NodeFolder) arg0.getObj()).getCreated();
			} else if (arg0.getObj() instanceof NodeDocument) {
				value0 = ((NodeDocument) arg0.getObj()).getLastModified();
			} else if (arg0.getObj() instanceof NodeMail) {
				value0 = ((NodeMail) arg0.getObj()).getReceivedDate();
			}

			if (arg1.getObj() instanceof NodeFolder) {
				value1 = ((NodeFolder) arg1.getObj()).getCreated();
			} else if (arg1.getObj() instanceof NodeDocument) {
				value1 = ((NodeDocument) arg1.getObj()).getLastModified();
			} else if (arg1.getObj() instanceof NodeMail) {
				value1 = ((NodeMail) arg1.getObj()).getReceivedDate();
			}

			return value0.compareTo(value1);
		}
	}

	/**
	 * OrderByAuthor
	 *
	 * @author jllort
	 *
	 */
	private static class OrderByAuthor implements Comparator<ObjectToOrder> {
		private static final Comparator<ObjectToOrder> INSTANCE = new OrderByAuthor();

		public static Comparator<ObjectToOrder> getInstance() {
			return INSTANCE;
		}

		public int compare(ObjectToOrder arg0, ObjectToOrder arg1) {
			String value0 = "";
			String value1 = "";

			if (arg0.getObj() instanceof NodeFolder) {
				value0 = ((NodeFolder) arg0.getObj()).getAuthor();
			} else if (arg0.getObj() instanceof NodeDocument) {
				try {
					String docUuid = ((NodeDocument) arg0.getObj()).getUuid();
					NodeDocumentVersion nDocVer = NodeDocumentVersionDAO.getInstance().findCurrentVersion(docUuid);
					value0 = nDocVer.getAuthor();
				} catch (Exception e) {
					// Ignore
				}
			} else if (arg0.getObj() instanceof NodeMail) {
				value0 = ((NodeMail) arg0.getObj()).getAuthor();
			}

			if (arg1.getObj() instanceof NodeFolder) {
				value1 = ((NodeFolder) arg1.getObj()).getAuthor();
			} else if (arg1.getObj() instanceof NodeDocument) {
				try {
					String docUuid = ((NodeDocument) arg1.getObj()).getUuid();
					NodeDocumentVersion nDocVer = NodeDocumentVersionDAO.getInstance().findCurrentVersion(docUuid);
					value1 = nDocVer.getAuthor();
				} catch (Exception e) {
					// Ignore
				}
			} else if (arg1.getObj() instanceof NodeMail) {
				value1 = ((NodeMail) arg1.getObj()).getAuthor();
			}

			return value0.compareTo(value1);
		}
	}

	/**
	 * OrderByVersion
	 *
	 * @author jllort
	 *
	 */
	private static class OrderByVersion implements Comparator<ObjectToOrder> {
		private static final Comparator<ObjectToOrder> INSTANCE = new OrderByVersion();

		public static Comparator<ObjectToOrder> getInstance() {
			return INSTANCE;
		}

		public int compare(ObjectToOrder arg0, ObjectToOrder arg1) {
			if (arg0.getObj() instanceof NodeDocument && arg1.getObj() instanceof NodeDocument) {
				try {
					String docUuid0 = ((NodeDocument) arg0.getObj()).getUuid();
					NodeDocumentVersion nDocVer0 = NodeDocumentVersionDAO.getInstance().findCurrentVersion(docUuid0);
					String value0 = getComparableVersion(nDocVer0.getName());

					String docUuid1 = ((NodeDocument) arg1.getObj()).getUuid();
					NodeDocumentVersion nDocVer1 = NodeDocumentVersionDAO.getInstance().findCurrentVersion(docUuid1);
					String value1 = getComparableVersion(nDocVer1.getName());

					return value0.compareTo(value1);
				} catch (Exception e) {
					// Ignore
				}

				return 0;
			} else if (arg0.getObj() instanceof NodeDocument) {
				return 1;
			} else if (arg1.getObj() instanceof NodeDocument) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * getComparableVersion
	 *
	 * @return
	 */
	private static String getComparableVersion(String version) {
		// Based on ExtendedScrollTableSorter considering version number is 1.0 ( number dot number pattern )
		String numberParts[] = version.split("\\.");
		version = "";

		for (int x = 0; x < numberParts.length; x++) {
			switch (numberParts[x].length()) {
				case 1:
					version = version + "00" + numberParts[x];
					break;
				case 2:
					version = version + "0" + numberParts[x];
					break;
			}
		}

		if (numberParts.length == 2) {
			version = version + "000000";
		}

		if (numberParts.length == 3) {
			version = version + "000";
		}

		return version;
	}

	/**
	 * OrderByColumn
	 *
	 * @author jllort
	 *
	 */
	private static class OrderByColumn implements Comparator<ObjectToOrder> {
		private static final Comparator<ObjectToOrder> INSTANCE = new OrderByColumn();

		public static Comparator<ObjectToOrder> getInstance() {
			return INSTANCE;
		}

		public int compare(ObjectToOrder arg0, ObjectToOrder arg1) {
			GWTFormElement formE0 = arg0.getFormElement();
			GWTFormElement formE1 = arg1.getFormElement();

			if (formE0 == null && formE1 == null) {
				return 0;
			} else if (formE0 == null) {
				return -1;
			} else if (formE1 == null) {
				return 1;
			} else {
				if (formE0 instanceof GWTTextArea) {
					return ((GWTTextArea) formE0).getValue().compareTo(((GWTTextArea) formE1).getValue());
				} else if (formE0 instanceof GWTInput) {
					// Any type can be compared directly with value ( date is ISO8601, others are normal text )
					return ((GWTInput) formE0).getValue().compareTo(((GWTInput) formE1).getValue());
				} else if (formE0 instanceof GWTSuggestBox) {
					return String.valueOf(((GWTSuggestBox) formE0).getText()).compareTo(
							String.valueOf(((GWTSuggestBox) formE1).getText()));
				} else if (formE0 instanceof GWTCheckBox) {
					return String.valueOf(((GWTCheckBox) formE0).getValue()).compareTo(
							String.valueOf(((GWTCheckBox) formE1).getValue()));
				} else if (formE0 instanceof GWTSelect) {
					String value0 = "";
					String value1 = "";
					boolean breakEnabled = false;
					breakEnabled = ((GWTSelect) formE0).getType().equals(GWTSelect.TYPE_SIMPLE);

					for (GWTOption opt : ((GWTSelect) formE0).getOptions()) {
						if (opt.isSelected()) {
							value0 += opt.getValue();
							if (breakEnabled) {
								break;
							}
						}
					}

					for (GWTOption opt : ((GWTSelect) formE1).getOptions()) {
						if (opt.isSelected()) {
							value1 += opt.getValue();
							if (breakEnabled) {
								break;
							}
						}
					}

					return value0.compareTo(value1);
				}

				return 0;
			}
		}
	}
}