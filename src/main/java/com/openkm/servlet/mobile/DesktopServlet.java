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

package com.openkm.servlet.mobile;

import com.openkm.api.*;
import com.openkm.automation.AutomationException;
import com.openkm.bean.*;
import com.openkm.bean.form.*;
import com.openkm.core.*;
import com.openkm.dao.KeyValueDAO;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.bean.KeyValue;
import com.openkm.extension.core.ExtensionException;
import com.openkm.frontend.client.util.MessageFormat;
import com.openkm.util.ISO8601;
import com.openkm.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Servlet implementation class DesktopServlet
 */
public class DesktopServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(DesktopServlet.class);
	private static final long serialVersionUID = 1L;

	public DesktopServlet() {
		super();
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		String path = WebUtils.getString(request, "path");
		String uuid = WebUtils.getString(request, "uuid");
		log.debug("action: {}", action);

		try {
			if (uuid != null && !uuid.isEmpty()) {
				path = NodeBaseDAO.getInstance().getPathFromUuid(uuid);
			} else if (path != null && !path.isEmpty()) {
				uuid = NodeBaseDAO.getInstance().getUuidFromPath(path);
			}

			if (action.equals("browse")) {
				browse(uuid, path, request, response);
			} else if (action.equals("docMenu") || action.equals("fldMenu") || action.equals("mailMenu")) {
				menu(uuid, path, request, response);
			} else if (action.equals("delete")) {
				delete(uuid, path, request, response);
			} else if (action.equals("confirmDelete")) {
				dialog(uuid, path, request, response);
			} else if (action.equals("properties") || action.equals("directOpen") || action.equals("propertiesFromSearch")
					|| action.equals("propertiesFromDashBoard")) {
				properties(uuid, path, request, response);
			} else if (action.equals("lock")) {
				lock(uuid, path, request, response);
			} else if (action.equals("unlock")) {
				unlock(uuid, path, request, response);
			} else {
				browse(uuid, path, request, response);
			}
		} catch (PathNotFoundException e) {
			sendErrorRedirect(request, response, e);
		} catch (LockException e) {
			sendErrorRedirect(request, response, e);
		} catch (AccessDeniedException e) {
			sendErrorRedirect(request, response, e);
		} catch (ParseException e) {
			sendErrorRedirect(request, response, e);
		} catch (NoSuchGroupException e) {
			sendErrorRedirect(request, response, e);
		} catch (ExtensionException e) {
			sendErrorRedirect(request, response, e);
		} catch (RepositoryException e) {
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			sendErrorRedirect(request, response, e);
		} catch (Exception e) {
			sendErrorRedirect(request, response, e);
		}
	}

	/**
	 * Dispatch errors
	 */
	private void sendErrorRedirect(HttpServletRequest request, HttpServletResponse response, Throwable e)
			throws ServletException, IOException {
		ServletContext sc = getServletContext();
		sc.setAttribute("exception", e);
		sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/error.jsp").forward(request, response);
	}

	/**
	 * Browser list of contents
	 */
	private void browse(String uuid, String path, HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException, ServletException, IOException {
		log.debug("browse({}, {})", request, response);
		String context = "context.taxonomy";
		List<Folder> catList = new ArrayList<Folder>();
		List<Folder> fldList = new ArrayList<Folder>();
		List<Document> docList = new ArrayList<Document>();

		if (uuid == null || uuid.isEmpty()) {
			path = "/" + Repository.ROOT;
			uuid = NodeBaseDAO.getInstance().getUuidFromPath(path);
		}

		if (path.startsWith("/" + Repository.ROOT)) {
			context = "context.title.taxonomy";
			fldList = OKMFolder.getInstance().getChildren(null, uuid);
			docList = OKMDocument.getInstance().getChildren(null, uuid);
		} else if (path.startsWith("/" + Repository.CATEGORIES)) {
			context = "context.title.categories";
			catList = OKMFolder.getInstance().getChildren(null, uuid);
			fldList = OKMSearch.getInstance().getCategorizedFolders(null, uuid);
			docList = OKMSearch.getInstance().getCategorizedDocuments(null, uuid);
		} else if (path.startsWith("/" + Repository.TEMPLATES)) {
			context = "context.title.templates";
			fldList = OKMFolder.getInstance().getChildren(null, uuid);
			docList = OKMDocument.getInstance().getChildren(null, uuid);
		} else if (path.startsWith("/" + Repository.PERSONAL)) {
			context = "context.title.personal";
			fldList = OKMFolder.getInstance().getChildren(null, uuid);
			docList = OKMDocument.getInstance().getChildren(null, uuid);
		}

		ServletContext sc = getServletContext();
		sc.setAttribute("catChildren", catList);
		sc.setAttribute("fldChildren", fldList);
		sc.setAttribute("docChildren", docList);
		sc.setAttribute("parentUuid", uuid);
		sc.setAttribute("context", context);
		sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/desktop_browser.jsp").forward(request, response);
	}

	/**
	 * menu
	 */
	private void menu(String uuid, String path, HttpServletRequest request, HttpServletResponse response)
			throws RepositoryException, AccessDeniedException, PathNotFoundException, DatabaseException, ServletException, IOException {
		log.debug("menu({}, {})", request, response);
		String action = WebUtils.getString(request, "action");

		if ("docMenu".equals(action)) {
			ServletContext sc = getServletContext();
			sc.setAttribute("doc", OKMDocument.getInstance().getProperties(null, path));
			sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/document_menu.jsp").forward(request, response);
		} else if ("fldMenu".equals(action)) {
			ServletContext sc = getServletContext();
			sc.setAttribute("fld", OKMFolder.getInstance().getProperties(null, path));
			sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/folder_menu.jsp").forward(request, response);
		} else if ("mailMenu".equals(action)) {
			ServletContext sc = getServletContext();
			sc.setAttribute("mail", OKMMail.getInstance().getProperties(null, path));
			sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/mail_menu.jsp").forward(request, response);
		}
	}

	/**
	 * delete
	 */
	private void delete(String uuid, String path, HttpServletRequest request, HttpServletResponse response)
			throws LockException, PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException,
			ExtensionException, IOException, ServletException, AutomationException {
		log.debug("delete({}, {})", request, response);
		String nodeType = WebUtils.getString(request, "nodeType");
		String parentUuid = WebUtils.getString(request, "parentUuid");

		if (Document.TYPE.equals(nodeType)) {
			OKMDocument.getInstance().delete(null, path);
		} else if (Folder.TYPE.equals(nodeType)) {
			OKMFolder.getInstance().delete(null, path);
		} else if (Mail.TYPE.equals(nodeType)) {
			OKMMail.getInstance().delete(null, path);
		}

		browse(parentUuid, path, request, response);
	}

	/**
	 * dialog
	 */
	private void dialog(String uuid, String path, HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, PathNotFoundException, RepositoryException, IOException, ServletException, DatabaseException {
		log.debug("dialog({}, {})", request, response);
		String nodeType = WebUtils.getString(request, "nodeType");
		ServletContext sc = getServletContext();
		sc.setAttribute("action", WebUtils.getString(request, "action"));

		if (Document.TYPE.equals(nodeType)) {
			sc.setAttribute("doc", OKMDocument.getInstance().getProperties(null, path));
			sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/document_dialog.jsp").forward(request, response);
		} else if (Folder.TYPE.equals(nodeType)) {
			sc.setAttribute("fld", OKMFolder.getInstance().getProperties(null, path));
			sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/folder_dialog.jsp").forward(request, response);
		} else if (Mail.TYPE.equals(nodeType)) {
			sc.setAttribute("mail", OKMMail.getInstance().getProperties(null, path));
			sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/mail_dialog.jsp").forward(request, response);
		}
	}

	/**
	 * properties
	 */
	private void properties(String uuid, String path, HttpServletRequest request, HttpServletResponse response)
			throws PathNotFoundException, RepositoryException, IOException, ServletException, DatabaseException,
			ParseException, NoSuchGroupException, AccessDeniedException {
		log.debug("properties({}, {})", request, response);
		String nodeType = WebUtils.getString(request, "nodeType");
		ServletContext sc = getServletContext();
		sc.setAttribute("action", WebUtils.getString(request, "action"));

		// Case browse from search
		sc.setAttribute("query", WebUtils.getString(request, "query"));
		sc.setAttribute("offset", WebUtils.getString(request, "offset"));
		sc.setAttribute("limit", WebUtils.getString(request, "limit"));

		if (nodeType == null || nodeType.isEmpty()) {
			if (OKMDocument.getInstance().isValid(null, uuid)) {
				nodeType = Document.TYPE;
			} else if (OKMFolder.getInstance().isValid(null, uuid)) {
				nodeType = Folder.TYPE;
			} else if (OKMMail.getInstance().isValid(null, uuid)) {
				nodeType = Mail.TYPE;
			}
		}

		if (Document.TYPE.equals(nodeType)) {
			Document doc = OKMDocument.getInstance().getProperties(null, path);
			List<String> categories = new ArrayList<String>();

			for (Folder cat : doc.getCategories()) {
				categories.add(cat.getPath().substring(Repository.CATEGORIES.length() + 2));
			}

			sc.setAttribute("doc", doc);
			sc.setAttribute("categories", categories);
			sc.setAttribute("groups", getPropertyGroups(path));
			sc.setAttribute("notes", OKMNote.getInstance().list(null, path));
			sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/document_properties.jsp").forward(request, response);
		} else if (Folder.TYPE.equals(nodeType)) {
			Folder fld = OKMFolder.getInstance().getProperties(null, path);
			List<String> categories = new ArrayList<String>();

			for (Folder cat : fld.getCategories()) {
				categories.add(cat.getPath().substring(Repository.CATEGORIES.length() + 2));
			}

			sc.setAttribute("fld", fld);
			sc.setAttribute("categories", categories);
			sc.setAttribute("groups", getPropertyGroups(path));
			sc.setAttribute("notes", OKMNote.getInstance().list(null, path));
			sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/folder_properties.jsp").forward(request, response);
		} else if (Mail.TYPE.equals(nodeType)) {
			Mail mail = OKMMail.getInstance().getProperties(null, path);
			List<String> categories = new ArrayList<String>();

			for (Folder cat : mail.getCategories()) {
				categories.add(cat.getPath().substring(Repository.CATEGORIES.length() + 2));
			}

			sc.setAttribute("mail", mail);
			sc.setAttribute("categories", categories);
			sc.setAttribute("groups", getPropertyGroups(path));
			sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/mail_properties.jsp").forward(request, response);
		}
	}

	/**
	 * lock
	 */
	private void lock(String uuid, String path, HttpServletRequest request, HttpServletResponse response)
			throws PathNotFoundException, RepositoryException, IOException, ServletException, DatabaseException, LockException,
			AccessDeniedException {
		log.debug("lock({}, {})", request, response);
		String parentUuid = WebUtils.getString(request, "parentUuid");
		OKMDocument.getInstance().lock(null, path);
		browse(parentUuid, path, request, response);
	}

	/**
	 * unlock
	 */
	private void unlock(String uuid, String path, HttpServletRequest request, HttpServletResponse response)
			throws PathNotFoundException, RepositoryException, IOException, ServletException, DatabaseException, LockException,
			AccessDeniedException {
		log.debug("lock({}, {})", request, response);
		String parentUuid = WebUtils.getString(request, "parentUuid");
		OKMDocument.getInstance().unlock(null, path);
		browse(parentUuid, path, request, response);
	}

	/**
	 * getPropertyGroups
	 */
	private List<GroupMobileData> getPropertyGroups(String path) throws IOException, ParseException, AccessDeniedException,
			PathNotFoundException, RepositoryException, DatabaseException, NoSuchGroupException {
		List<GroupMobileData> groupMobileDataList = new ArrayList<GroupMobileData>();

		for (PropertyGroup pGroup : OKMPropertyGroup.getInstance().getGroups(null, path)) {
			Map<String, String> map = new HashMap<String, String>();

			for (FormElement fe : OKMPropertyGroup.getInstance().getProperties(null, path, pGroup.getName())) {
				if (fe instanceof Input) {
					Input input = (Input) fe;

					if (input.getType().equals(Input.TYPE_DATE)) {
						if (input.getValue() != null && !input.getValue().isEmpty()) {
							Calendar date = ISO8601.parseBasic(input.getValue());
							SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
							map.put(fe.getLabel(), sdf.format(date.getTime()));
						} else {
							map.put(fe.getLabel(), input.getValue());
						}
					} else {
						map.put(fe.getLabel(), input.getValue());
					}
				} else if (fe instanceof SuggestBox) {
					SuggestBox sb = (SuggestBox) fe;

					if (sb.getValue() != null && sb.getTable() != null) {
						String formatedQuery = MessageFormat.format(sb.getValueQuery(), sb.getValue());
						List<String> tables = new ArrayList<String>();
						tables.add(sb.getTable());

						for (KeyValue keyValue : KeyValueDAO.getKeyValues(tables, formatedQuery)) {
							map.put(fe.getLabel(), keyValue.getValue());
						}
					} else {
						map.put(fe.getLabel(), "");
					}
				} else if (fe instanceof Select) {
					Select sel = (Select) fe;

					if (sel.getType().equals(Select.TYPE_SIMPLE)) {
						for (Option option : sel.getOptions()) {
							if (option.isSelected()) {
								map.put(fe.getLabel(), option.getLabel());
								break;
							}
						}
					} else if (sel.getType().equals(Select.TYPE_MULTIPLE)) {
						String values = "";

						for (Option option : sel.getOptions()) {
							if (option.isSelected()) {
								if (values.length() > 0) {
									values += ", ";
								}

								values += option.getLabel();
							}
						}

						map.put(fe.getLabel(), values);
					}
				} else if (fe instanceof CheckBox) {
					CheckBox cb = (CheckBox) fe;
					map.put(fe.getLabel(), String.valueOf(cb.getValue()));
				} else if (fe instanceof TextArea) {
					TextArea ta = (TextArea) fe;
					map.put(fe.getLabel(), ta.getValue());
				}
			}

			groupMobileDataList.add(new GroupMobileData(pGroup.getLabel(), map));
		}

		return groupMobileDataList;
	}

	/**
	 * GroupMobileData
	 */
	public class GroupMobileData {
		private String label;
		private Map<String, String> map;

		public GroupMobileData(String label, Map<String, String> map) {
			this.label = label;
			this.map = map;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public Map<String, String> getMap() {
			return map;
		}

		public void setMap(Map<String, String> map) {
			this.map = map;
		}
	}
}